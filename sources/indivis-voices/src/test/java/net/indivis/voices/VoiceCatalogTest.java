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
        assertEquals(8, c.lines().size());
        for (var l : c.lines()) {
            assertTrue(l.durationMs() >= 100 && l.durationMs() <= 120_000);
            assertTrue(l.nextSequence() > 0);
            assertTrue(l.sourceFunction().startsWith("capitale:quest/"));
            assertEquals(64, l.sha256().length());
        }
    }
}
