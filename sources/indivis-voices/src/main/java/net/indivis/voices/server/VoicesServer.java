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
        Pending pending;
        boolean helloLogged;
    }
    private record Pending(VoiceLine line, DialogueBridge.Session session) {}
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
            if (playing.expired(now())) { release(player, state, PlaybackLease.Release.TEXT); continue; }
            // Short renewable lease: core/tick subtracts one, then does its normal dispatch.
            set(player, TIMER, 2);
            bridge.keepAlive(player, server.getTicks());
        }
    }
    public void endTick(MinecraftServer server) {
        for (var player : server.getPlayerManager().getPlayerList()) {
            var state = players.get(player.getUuid());
            if (state == null) continue;
            if (state.playing != null && !valid(player, state.playing)) cancel(player, state, true);
            var pending = state.pending; state.pending = null;
            if (pending == null || !state.available.contains(pending.line.id())) continue;
            var session = bridge.session(player);
            if (session == null || !session.equals(pending.session) ||
                score(player, SEQUENCE) != pending.line.nextSequence() || score(player, TIMER) <= 0) continue;
            cancel(player, state, false);
            var playing = new PlaybackLease(state.epoch, session.id(), ++state.generation,
                pending.line, session.character(), dimension(player), session.npc(), session.entityId(), score(player, TIMER), now());
            state.playing = playing;
            ServerPlayNetworking.send(player, new VoicePackets.Start(token(playing), playing.npc, playing.entityId));
        }
    }
    public void end(ServerPlayerEntity player, VoicePackets.End packet) {
        var state = players.get(player.getUuid());
        if (state == null || state.playing == null) return;
        var lease = state.playing; var t = packet.token();
        if (!lease.matches(t.epoch(), t.session(), t.generation(), t.line())) return;
        if (!valid(player, lease)) { cancel(player, state, true); return; }
        release(player, state, lease.finish(packet.reason(), now()));
    }
    private boolean valid(ServerPlayerEntity player, PlaybackLease lease) {
        var s = bridge.session(player);
        return s != null && player.isAlive() && lease.dimension.equals(dimension(player)) &&
            lease.context(s.id(), s.character(), score(player, SEQUENCE), s.npc());
    }
    private void release(ServerPlayerEntity player, PlayerState state, PlaybackLease.Release release) {
        if (release == PlaybackLease.Release.NONE || state.playing == null) return;
        var lease = state.playing;
        set(player, TIMER, release == PlaybackLease.Release.ADVANCE ? 0 : lease.textTimer);
        cancel(player, state, false);
    }
    private void cancel(ServerPlayerEntity player, PlayerState state, boolean restore) {
        var lease = state.playing;
        state.playing = null;
        if (lease == null) return;
        if (restore && lease.dimension.equals(dimension(player)) && lease.character.equals(bridge.character(player)) &&
            score(player, SEQUENCE) == lease.line.nextSequence()) {
            set(player, TIMER, lease.textTimer);
        }
        lease.invalidate();
        if (ServerPlayNetworking.canSend(player, VoicePackets.Stop.ID)) ServerPlayNetworking.send(player, new VoicePackets.Stop(token(lease)));
    }
    public void disconnect(ServerPlayerEntity player) {
        var state = players.remove(player.getUuid());
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
