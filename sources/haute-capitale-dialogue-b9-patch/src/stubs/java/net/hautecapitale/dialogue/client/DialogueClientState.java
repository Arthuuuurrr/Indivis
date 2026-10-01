package net.hautecapitale.dialogue.client;

import net.hautecapitale.dialogue.client.capture.MessageCapture;

public final class DialogueClientState {
    private DialogueClientState() {}
    public static boolean captureActive() { return false; }
    public static String pnjNom() { return ""; }
    public static void onMessageCapture(MessageCapture.Classification classification) {}
}
