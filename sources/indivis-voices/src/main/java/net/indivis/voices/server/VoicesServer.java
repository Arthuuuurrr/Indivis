package net.indivis.voices.server;

import java.util.*;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.indivis.voices.catalog.*;
import net.indivis.voices.compat.DialogueBridge;
import net.indivis.voices.network.VoicePackets;
import net.indivis.voices.session.PlaybackLease;
import net.indivis.voices.IndivisVoices;
import net.minecraft.scoreboard.ScoreboardObjective;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerPlayerEntity;

public final class VoicesServer {
    private static final String SEQUENCE = "CAP_QSEQ", TIMER = "CAP_QSEQ_TIMER";
    private static final class PlayerState {
        final UUID epoch = UUID.randomUUID();
        Set<String> available = Set.of();
        long generation, lastHelloMs = Long.MIN_VALUE / 2;
        PlaybackLease playing;
        Followup followup;
        Pending pending;
        boolean helloLogged, earlyEndLogged;
    }
    private record Pending(VoiceLine line, DialogueBridge.Session session) {}
    private record Followup(PlaybackLease lease, long deadlineMs) {}
    private final VoiceCatalog catalog;
    private final DialogueBridge bridge = new DialogueBridge();
    private final Map<UUID, PlayerState> players = new HashMap<>();

    public VoicesServer(VoiceCatalog catalog) { this.catalog = catalog; }
    static long now() { return System.nanoTime() / 1_000_000L; }

    public void hello(ServerPlayerEntity player, VoicePackets.Hello hello) {
        var state = players.computeIfAbsent(player.getUuid(), k -> new PlayerState());
        if (now() - state.lastHelloMs < 500) return;
        state.lastHelloMs = now();
        if (hello.protocol() != VoicePackets.PROTOCOL || !hello.enabled() ||
            !catalog.revision().equals(hello.revision()) || !ServerPlayNetworking.canSend(player, VoicePackets.Start.ID)) {
            state.available = Set.of(); state.pending = null; cancel(player, state, true); return;
        }
        var available = new HashSet<String>();
        for (var id : hello.available()) if (catalog.line(id) != null) available.add(id);
        boolean changed = !state.available.equals(available);
        state.available = Set.copyOf(available);
        if (!state.helloLogged || changed) {
            IndivisVoices.LOG.info("Voices serveur : client {} : {}/{} prises annoncées, catalogue {}", player.getName().getString(), state.available.size(), catalog.lines().size(), catalog.revision());
            state.helloLogged = true;
        }
        if (state.pending != null && !state.available.contains(state.pending.line.id())) state.pending = null;
        if (state.playing != null && !state.available.contains(state.playing.line.id())) cancel(player, state, true);
    }
    /** Invoked only for a private game message on this player's connection. */
    public void message(ServerPlayerEntity player, String text) {
        var state = players.get(player.getUuid());
        var line = catalog.message(text);
        if (state == null || line == null || !state.available.contains(line.id())) return;
        var session = bridge.session(player);
        if (session == null || player.isSpectator() || !SpeakerName.matches(session.name(), line.npcName())) return;
        // Native functions set CAP_QSEQ after tellraw. Inspect only at END_SERVER_TICK.
        if (state.playing != null && state.playing.session == session.id() &&
            state.playing.line.id().equals(line.id())) return;
        state.pending = new Pending(line, session);
    }
    public void beginTick(MinecraftServer server) {
        for (var player : server.getPlayerManager().getPlayerList()) {
            var state = players.get(player.getUuid());
            if (state == null || state.playing == null) continue;
            var playing = state.playing;
            if (!valid(player, playing)) { cancel(player, state, true); continue; }
            if (playing.expired(now())) {
                IndivisVoices.LOG.warn("Voices serveur : délai vocal dépassé, retour texte {} (session #{}, séquence {}, timer {})",
                    playing.line.id(), playing.session, score(player, SEQUENCE), score(player, TIMER));
                release(player, state, PlaybackLease.Release.TEXT); continue;
            }
            // Short renewable lease: core/tick subtracts one, then does its normal dispatch.
            if (playing.line.sequenced()) set(player, TIMER, 2);
            bridge.keepAlive(player, server.getTicks());
        }
    }
    public void endTick(MinecraftServer server) {
        for (var player : server.getPlayerManager().getPlayerList()) {
            var state = players.get(player.getUuid());
            if (state == null) continue;
            observeFollowup(player, state);
            if (state.playing != null && !valid(player, state.playing)) cancel(player, state, true);
            var pending = state.pending; state.pending = null;
            if (pending == null || !state.available.contains(pending.line.id())) continue;
            var session = bridge.session(player);
            if (session == null || !session.equals(pending.session) ||
                score(player, SEQUENCE) != pending.line.nextSequence() ||
                pending.line.sequenced() && score(player, TIMER) <= 0) continue;
            cancel(player, state, false);
            var playing = new PlaybackLease(state.epoch, session.id(), ++state.generation,
                pending.line, session.character(), dimension(player), session.npc(), session.entityId(), score(player, TIMER), now());
            state.playing = playing;
            state.earlyEndLogged = false;
            IndivisVoices.LOG.info("Voices serveur : début {} (session #{}, séquence {}, timer texte {})",
                playing.line.id(), playing.session, playing.line.nextSequence(), playing.textTimer);
            ServerPlayNetworking.send(player, new VoicePackets.Start(token(playing), playing.npc, playing.entityId));
        }
    }
    public void end(ServerPlayerEntity player, VoicePackets.End packet) {
        var state = players.get(player.getUuid());
        if (state == null || state.playing == null) return;
        var lease = state.playing; var t = packet.token();
        if (!lease.matches(t.epoch(), t.session(), t.generation(), t.line())) return;
        if (!valid(player, lease)) { cancel(player, state, true); return; }
        var outcome = lease.finish(packet.reason(), now());
        if (outcome == PlaybackLease.Release.NONE && !state.earlyEndLogged) {
            IndivisVoices.LOG.warn("Voices serveur : fin prématurée ignorée {} (session #{}, {} après {} ms)",
                lease.line.id(), lease.session, packet.reason(), now() - lease.startedMs);
            state.earlyEndLogged = true;
        } else if (outcome != PlaybackLease.Release.NONE) {
            IndivisVoices.LOG.info("Voices serveur : fin {} (session #{}, {} après {} ms, suite {})",
                lease.line.id(), lease.session, packet.reason(), now() - lease.startedMs, outcome);
        }
        release(player, state, outcome);
    }
    private boolean valid(ServerPlayerEntity player, PlaybackLease lease) {
        var s = bridge.session(player);
        return s != null && player.isAlive() && lease.dimension.equals(dimension(player)) &&
            lease.context(s.id(), s.character(), score(player, SEQUENCE), s.npc());
    }
    private void release(ServerPlayerEntity player, PlayerState state, PlaybackLease.Release release) {
        if (release == PlaybackLease.Release.NONE || state.playing == null) return;
        var lease = state.playing;
        lease.releasedTimer(release).ifPresent(value -> set(player, TIMER, value));
        if (lease.line.sequenced()) state.followup = new Followup(lease, now() + 5_000);
        cancel(player, state, false);
    }
    /** Observation only: never drive the quest or restore a previous character's state. */
    private void observeFollowup(ServerPlayerEntity player, PlayerState state) {
        var followup = state.followup;
        if (followup == null) return;
        var lease = followup.lease;
        if (!lease.character.equals(bridge.character(player)) || !lease.dimension.equals(dimension(player))) {
            state.followup = null; return;
        }
        int sequence = score(player, SEQUENCE), timer = score(player, TIMER);
        if (sequence != lease.line.nextSequence()) {
            IndivisVoices.LOG.info("Voices serveur : suite native {} (session #{}, séquence {} → {}, timer {})",
                lease.line.id(), lease.session, lease.line.nextSequence(), sequence, timer);
            state.followup = null;
        } else if (now() > followup.deadlineMs) {
            IndivisVoices.LOG.warn("Voices serveur : séquence native inchangée après libération {} (session #{}, séquence {}, timer {})",
                lease.line.id(), lease.session, sequence, timer);
            state.followup = null;
        }
    }
    private void cancel(ServerPlayerEntity player, PlayerState state, boolean restore) {
        var lease = state.playing;
        state.playing = null;
        if (lease == null) return;
        if (restore) IndivisVoices.LOG.info("Voices serveur : lecture annulée {} (session #{}, séquence actuelle {}, timer {})",
            lease.line.id(), lease.session, score(player, SEQUENCE), score(player, TIMER));
        if (restore && lease.line.sequenced() && lease.dimension.equals(dimension(player)) && lease.character.equals(bridge.character(player)) &&
            score(player, SEQUENCE) == lease.line.nextSequence()) {
            set(player, TIMER, lease.textTimer);
        }
        lease.invalidate();
        if (ServerPlayNetworking.canSend(player, VoicePackets.Stop.ID)) ServerPlayNetworking.send(player, new VoicePackets.Stop(token(lease)));
    }
    public void disconnect(ServerPlayerEntity player) {
        var state = players.remove(player.getUuid());
        if (state != null && state.playing != null) IndivisVoices.LOG.info(
            "Voices serveur : déconnexion pendant {} (session #{}, après {} ms)",
            state.playing.line.id(), state.playing.session, now() - state.playing.startedMs);
        if (state != null) cancel(player, state, true);
    }
    public void stop(MinecraftServer server) {
        server.getPlayerManager().getPlayerList().forEach(this::disconnect);
        players.clear();
    }
    private static VoicePackets.Token token(PlaybackLease p) { return new VoicePackets.Token(p.epoch, p.session, p.generation, p.line.id()); }
    private static String dimension(ServerPlayerEntity p) { return p.getEntityWorld().getRegistryKey().getValue().toString(); }
    private static int score(ServerPlayerEntity player, String name) {
        var board = player.getEntityWorld().getScoreboard(); var objective = board.getNullableObjective(name);
        if (objective == null) return -1;
        var score = board.getScore(player, objective);
        return score == null ? -1 : score.getScore();
    }
    private static void set(ServerPlayerEntity player, String name, int value) {
        var board = player.getEntityWorld().getScoreboard(); ScoreboardObjective objective = board.getNullableObjective(name);
        if (objective != null) board.getOrCreateScore(player, objective).setScore(value);
    }
}
