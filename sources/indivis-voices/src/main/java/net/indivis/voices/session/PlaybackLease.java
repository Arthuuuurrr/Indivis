package net.indivis.voices.session;

import java.util.UUID;
import net.indivis.voices.catalog.VoiceLine;

/** Pure authority/state logic; no quest commands, no audio callbacks with mutable captures. */
public final class PlaybackLease {
    public enum End { NATURAL, SKIP, MISSING, CANCEL }
    public enum Release { NONE, ADVANCE, TEXT }
    public final UUID epoch;
    public final int session;
    public final long generation;
    public final VoiceLine line;
    public final UUID character;
    public final String dimension;
    public final UUID npc;
    public final int entityId;
    public final int textTimer;
    public final long startedMs;
    private boolean terminal;

    public PlaybackLease(UUID epoch, int session, long generation, VoiceLine line,
                         UUID character, String dimension, UUID npc, int entityId, int textTimer, long nowMs) {
        this.epoch = epoch; this.session = session; this.generation = generation;
        this.line = line; this.character = character; this.npc = npc;
        this.dimension = dimension;
        this.entityId = entityId; this.textTimer = textTimer; this.startedMs = nowMs;
    }
    public boolean matches(UUID epoch, int session, long generation, String line) {
        return !terminal && this.epoch.equals(epoch) && this.session == session &&
            this.generation == generation && this.line.id().equals(line);
    }
    public boolean context(int session, UUID character, int sequence, UUID npc) {
        return !terminal && this.session == session && this.character.equals(character) &&
            line.nextSequence() == sequence && this.npc.equals(npc);
    }
    public Release finish(End end, long nowMs) {
        if (terminal) return Release.NONE;
        // Natural completion cannot shorten the catalogued take via a premature packet.
        if (end == End.NATURAL && nowMs - startedMs < Math.max(0, line.durationMs() - 500)) {
            return Release.NONE;
        }
        terminal = true;
        return end == End.NATURAL || end == End.SKIP ? Release.ADVANCE : Release.TEXT;
    }
    public boolean expired(long nowMs) { return nowMs - startedMs > line.durationMs() + 5_000L; }
    public void invalidate() { terminal = true; }
}
