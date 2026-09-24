package net.hautecapitale.dialogue.client.compat.bettercombat;

import net.bettercombat.api.MinecraftClient_BetterCombat;
import net.hautecapitale.dialogue.HauteCapitaleDialogue;
import net.hautecapitale.dialogue.client.DialogueClientState;
import net.minecraft.client.MinecraftClient;

/**
 * Le seul trou que laisse un écran ouvert face à Better Combat : un élan
 * d'attaque déjà commencé se termine tout seul, écran ou pas, et le coup part.
 * Better Combat expose exactement de quoi le refermer.
 *
 * <p>Classe chargée uniquement si Better Combat est présent ; son interface est
 * greffée sur {@code MinecraftClient} par son propre mixin.
 */
public final class BetterCombatClientCompat {

    private BetterCombatClientCompat() {
    }

    public static void init() {
        DialogueClientState.aLOuverture(() -> {
            MinecraftClient client = MinecraftClient.getInstance();
            if (client instanceof MinecraftClient_BetterCombat bc) {
                bc.cancelUpswing();
            }
        });
        HauteCapitaleDialogue.LOGGER.info("Compat Better Combat : l'élan d'attaque est annulé à l'ouverture d'un dialogue.");
    }
}
