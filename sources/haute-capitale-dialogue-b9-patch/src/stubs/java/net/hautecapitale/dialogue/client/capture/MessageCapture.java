package net.hautecapitale.dialogue.client.capture;

import java.util.List;
import net.minecraft.text.Text;

public final class MessageCapture {
    private MessageCapture() {}
    public static Classification classer(Text message, String pnjNom, boolean captureActive, List<String> ignored) { return null; }

    public static final class Classification {
        public boolean vide() { return true; }
    }
}
