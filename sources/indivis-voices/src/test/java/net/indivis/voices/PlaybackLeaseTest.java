package net.indivis.voices;

import java.util.UUID;
import net.indivis.voices.catalog.*;
import net.indivis.voices.session.*;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class PlaybackLeaseTest {
    final UUID epoch = UUID.randomUUID(), character = UUID.randomUUID(), npc = UUID.randomUUID();
    final VoiceLine line = new VoiceLine("prologue.geraud.reveil.01", "Géraud", "Géraud Rivet", "texte", "[Géraud] : texte", "voice", "voice.ogg", "a".repeat(64), 3000, "fr_fr", "v1", "capitale:test", 101);
    PlaybackLease lease() { return new PlaybackLease(epoch, 7, 42, line, character, "minecraft:overworld", npc, 123, 20, 1000); }
    @Test void lateOrDuplicateCompletionCannotAdvanceAgain() {
        var p = lease();
        assertEquals(PlaybackLease.Release.ADVANCE, p.finish(PlaybackLease.End.SKIP, 1100));
        assertEquals(PlaybackLease.Release.NONE, p.finish(PlaybackLease.End.NATURAL, 4100));
        assertFalse(p.matches(epoch, 7, 42, line.id()));
    }
    @Test void rejectsOtherSessionsGenerationsLinesAndConnections() {
        var p = lease();
        assertTrue(p.matches(epoch, 7, 42, line.id()));
        assertFalse(p.matches(UUID.randomUUID(), 7, 42, line.id()));
        assertFalse(p.matches(epoch, 8, 42, line.id()));
        assertFalse(p.matches(epoch, 7, 41, line.id()));
        assertFalse(p.matches(epoch, 7, 42, "another.line"));
    }
    @Test void characterSwitchAndQuestChangeInvalidateTheOldContext() {
        var p = lease();
        assertTrue(p.context(7, character, 101, npc));
        assertFalse(p.context(7, UUID.randomUUID(), 101, npc));
        assertFalse(p.context(7, character, 102, npc));
        assertFalse(p.context(8, character, 101, npc));
        assertFalse(p.context(7, character, 101, UUID.randomUUID()));
    }
    @Test void boundsNaturalCompletionButAllowsDeliberateSkip() {
        var p = lease();
        assertEquals(PlaybackLease.Release.NONE, p.finish(PlaybackLease.End.NATURAL, 1300));
        assertEquals(PlaybackLease.Release.ADVANCE, p.finish(PlaybackLease.End.NATURAL, 4000));
        assertEquals(PlaybackLease.Release.ADVANCE, lease().finish(PlaybackLease.End.SKIP, 1001));
    }
    @Test void missingAudioAndCloseRestoreTheTextPath() {
        assertEquals(PlaybackLease.Release.TEXT, lease().finish(PlaybackLease.End.MISSING, 1200));
        assertEquals(PlaybackLease.Release.TEXT, lease().finish(PlaybackLease.End.CANCEL, 1200));
        assertEquals(20, lease().textTimer);
    }
    @Test void timeoutCannotHoldAQuestForever() {
        assertFalse(lease().expired(9000));
        assertTrue(lease().expired(9001));
    }
    @Test void invalidatedGenerationHasNoCallback() {
        var p = lease(); p.invalidate();
        assertEquals(PlaybackLease.Release.NONE, p.finish(PlaybackLease.End.SKIP, 9999));
    }
    @Test void independentPlayersDoNotShareTerminalState() {
        var a = lease(); var b = lease();
        a.finish(PlaybackLease.End.SKIP, 1001);
        assertTrue(b.matches(epoch, 7, 42, line.id()));
    }
    @Test void envelopeUsesFiftyMillisecondsAndDoesNotRestartOnSpam() {
        var fade = new FadeEnvelope();
        assertEquals(1, fade.gain(0));
        fade.begin(100_000_000); fade.begin(120_000_000);
        assertEquals(0.5f, fade.gain(125_000_000), .001);
        assertFalse(fade.finished(149_000_000));
        assertTrue(fade.finished(150_000_000));
        assertEquals(0, fade.gain(Long.MAX_VALUE / 2));
    }
    @Test void standaloneLinesCannotWriteOrReleaseAQuestTimerForAnyCompletionReason() {
        var idle = VoiceCatalog.load().lookup("P01-ALT-001");
        assertFalse(idle.sequenced());
        for (var reason : PlaybackLease.End.values()) {
            var p = new PlaybackLease(epoch, 7, 42, idle, character, "minecraft:overworld", npc, 123, 20, 1000);
            assertTrue(p.context(7, character, 0, npc));
            assertFalse(p.context(7, character, 101, npc));
            var release = p.finish(reason, 1000 + idle.durationMs());
            assertTrue(p.releasedTimer(release).isEmpty(), reason.toString());
            assertTrue(p.releasedTimer(PlaybackLease.Release.TEXT).isEmpty());
        }
    }
    @Test void standaloneRemindersStillRejectAnotherCharacterNpcOrSession() {
        var idle = VoiceCatalog.load().lookup("P01-GER-005");
        var p = new PlaybackLease(epoch, 7, 42, idle, character, "minecraft:overworld", npc, 123, 0, 1000);
        assertFalse(p.context(8, character, 0, npc));
        assertFalse(p.context(7, UUID.randomUUID(), 0, npc));
        assertFalse(p.context(7, character, 0, UUID.randomUUID()));
    }
    @Test void sequencedLinesReleaseOnlyTheirNativeTimer() {
        assertEquals(0, lease().releasedTimer(PlaybackLease.Release.ADVANCE).orElseThrow());
        assertEquals(20, lease().releasedTimer(PlaybackLease.Release.TEXT).orElseThrow());
        assertTrue(lease().releasedTimer(PlaybackLease.Release.NONE).isEmpty());
    }
}
