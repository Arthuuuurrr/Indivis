package net.indivis.voices;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import net.indivis.voices.session.DialogueContinuation;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class DialogueContinuationTest {
    private final UUID npc = UUID.randomUUID();
    private final String spoken = new String("Vous descendez du dirigeable de la Couronne ?");
    private DialogueContinuation<String> continuation() { return new DialogueContinuation<>(spoken, 3, npc, 1000); }

    @Test void leovicUnvoicedProposalBecomesTheCurrentTextAfterTheVoice() {
        var handover = continuation();
        var captured = new ArrayList<>(List.of(spoken));
        assertTrue(handover.poll(3, npc, captured, 1100, false).isEmpty());
        assertFalse(handover.finished());
        captured.add("Léovic vous adresse une proposition.");
        assertEquals(List.of("Léovic vous adresse une proposition."),
            handover.poll(3, npc, captured, 2000, false).orElseThrow());
        assertEquals(2, captured.size(), "Policy does not mutate the native capture or history");
        assertTrue(handover.finished());
    }
    @Test void keepsAllNewUnvoicedParagraphsAndOnlyConsumesOneHandover() {
        var handover = continuation();
        var captured = List.of(spoken, "Suite sans audio", "Dernière précision");
        assertEquals(captured.subList(1, 3), handover.poll(3, npc, captured, 2000, false).orElseThrow());
        assertTrue(handover.poll(3, npc, captured, 2001, false).isEmpty());
    }
    @Test void choicesWithoutNewTextKeepTheirQuestionVisible() {
        var handover = continuation();
        assertTrue(handover.poll(3, npc, List.of(spoken), 2000, false).isEmpty());
        assertFalse(handover.finished());
    }
    @Test void playerChoiceReplacingTheCaptureCancelsAutomaticHandover() {
        var handover = continuation();
        assertTrue(handover.poll(3, npc, List.of("Réponse à la question choisie"), 2000, false).isEmpty());
        assertTrue(handover.finished());
        var repeated = continuation();
        assertTrue(repeated.poll(3, npc, List.of(new String(spoken), "Suite"), 2000, false).isEmpty());
        assertTrue(repeated.finished(), "Equal text newly captured is not the old spoken object");
    }
    @Test void doesNotMovePagesWhileReadingNativeHistory() {
        var handover = continuation(); var captured = List.of(spoken, "Suite");
        assertTrue(handover.poll(3, npc, captured, 2000, true).isEmpty());
        assertFalse(handover.finished());
        assertEquals(List.of("Suite"), handover.poll(3, npc, captured, 2200, false).orElseThrow());
    }
    @Test void differentSessionNpcAndExpiredWindowNeverChangeText() {
        var captured = List.of(spoken, "Suite");
        var session = continuation();
        assertTrue(session.poll(4, npc, captured, 2000, false).isEmpty()); assertTrue(session.finished());
        var other = continuation();
        assertTrue(other.poll(3, UUID.randomUUID(), captured, 2000, false).isEmpty()); assertTrue(other.finished());
        var expired = continuation();
        assertTrue(expired.poll(3, npc, captured, 6001, false).isEmpty()); assertTrue(expired.finished());
    }
}
