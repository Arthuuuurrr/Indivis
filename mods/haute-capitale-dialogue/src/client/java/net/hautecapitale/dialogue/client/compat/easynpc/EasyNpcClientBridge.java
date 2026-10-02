package net.hautecapitale.dialogue.client.compat.easynpc;

import de.markusbordihn.easynpc.config.ClientDialogConfig;
import net.hautecapitale.dialogue.HauteCapitaleDialogue;
import net.hautecapitale.dialogue.client.DialogueClientState;
import net.hautecapitale.dialogue.compat.easynpc.EasyNpcDialogueBridge;
import net.minecraft.client.gui.screen.ingame.HandledScreens;

/**
 * Côté client : notre écran pour notre type de menu, et le réglage « machine à
 * écrire » d'Easy NPC partagé avec les dialogues capturés.
 */
public final class EasyNpcClientBridge {

    private EasyNpcClientBridge() {
    }

    public static void init() {
        HandledScreens.register(EasyNpcDialogueBridge.MENU_TYPE, RpgDialogueScreen::new);
        DialogueClientState.typewriterParDefaut(
                () -> ClientDialogConfig.TYPEWRITER_ENABLED,
                () -> ClientDialogConfig.TYPEWRITER_CHARS_PER_SECOND);
        HauteCapitaleDialogue.LOGGER.info("Écran de dialogue RPG enregistré pour les dialogues Easy NPC.");
    }
}
