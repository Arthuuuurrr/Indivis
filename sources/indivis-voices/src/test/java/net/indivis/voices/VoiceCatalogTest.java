package net.indivis.voices;

import java.util.List;
import net.indivis.voices.catalog.*;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class VoiceCatalogTest {
    @Test void aLineCannotAttachToAnUnrelatedNpcSession() {
        assertTrue(SpeakerName.matches("§eQuartier-maître Géraud Rivet", "Géraud Rivet"));
        assertTrue(SpeakerName.matches("CAPITAINE ALTHEON BRUMEFORGE", "Althéon Brumeforge"));
        assertFalse(SpeakerName.matches("Jojo", "Géraud Rivet"));
        assertFalse(SpeakerName.matches("Léovic", ""));
    }
    @Test void aSimilarOrEditedTextCannotPlayTheWrongTake() {
        var c = VoiceCatalog.load();
        var first = c.line("prologue.geraud.reveil.01");
        assertNotNull(first);
        assertEquals(first, c.message(first.wireText()));
        assertNull(c.message(first.text())); // A speaker-less message is not the native prologue line.
        assertNull(c.message(first.wireText() + " Texte modifié."));
    }
    @Test void duplicateIdsOrTextsCannotSilentlyReplaceARecording() {
        var first = VoiceCatalog.load().lines().iterator().next();
        assertThrows(IllegalArgumentException.class, () -> new VoiceCatalog("invalid", List.of(first, first)));
    }
    @Test void allTakesHaveBoundedDurationsAndNativeSequenceLinks() {
        var c = VoiceCatalog.load();
        assertEquals(20, c.lines().size());
        for (var l : c.lines()) {
            assertTrue(l.durationMs() >= 100 && l.durationMs() <= 120_000);
            assertTrue(l.nextSequence() >= 0);
            assertTrue(l.sourceFunction().startsWith("capitale:"));
            assertEquals(64, l.sha256().length());
        }
        assertEquals(18, c.lines().stream().filter(l -> l.productionId() != null).count());
        assertEquals(4, c.lines().stream().filter(l -> !l.sequenced()).count());
    }
    @Test void productionCodesSelectTheCorrectNativeQuestionAndNeverFallBackToAnotherTake() {
        var c = VoiceCatalog.load();
        assertEquals("prologue.geraud.reveil.01", c.lookup("P01-GER-004").id());
        assertEquals("prologue.geraud.reveil.02", c.lookup("P01-GER-010").id());
        assertEquals("prologue.geraud.reveil.03", c.lookup("P01-GER-011").id());
        assertEquals(211, c.lookup("P01-ALT-010").nextSequence());
        assertEquals(212, c.lookup("P01-ALT-023").nextSequence());
        assertEquals(221, c.lookup("P01-ALT-011").nextSequence());
        assertEquals(222, c.lookup("P01-ALT-024").nextSequence());
        assertEquals(251, c.lookup("P01-ALT-008").nextSequence());
        assertEquals(231, c.lookup("P01-ALT-009").nextSequence());
        assertEquals(232, c.lookup("P01-ALT-022").nextSequence());
        assertEquals(241, c.lookup("P01-ALT-007").nextSequence());
        assertEquals(c.lookup("P01-ALT-010"), c.lookup("p01-alt-010"));
        assertNull(c.lookup("P01-ALT-999"));
    }
    @Test void duplicateProductionCodesCannotSelectAnArbitraryTake() {
        var a = VoiceCatalog.load().lookup("P01-ALT-010");
        var b = new VoiceLine("other.line", a.speaker(), a.npcName(), "other text", "other wire", "other", "other.ogg",
            a.sha256(), a.durationMs(), a.language(), a.takeVersion(), a.sourceFunction(), a.nextSequence(), a.productionId());
        assertThrows(IllegalArgumentException.class, () -> new VoiceCatalog("duplicate-code", List.of(a, b)));
    }
}
