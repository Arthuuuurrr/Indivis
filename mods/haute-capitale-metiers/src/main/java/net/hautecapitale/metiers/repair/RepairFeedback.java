package net.hautecapitale.metiers.repair;

import net.minecraft.text.Text;
import net.minecraft.util.Formatting;

/** Ce que le joueur lit après avoir cliqué sur Réparer. */
public final class RepairFeedback {

    private RepairFeedback() {
    }

    public static Text describe(RepairEngine.Outcome outcome) {
        if (outcome.succeeded()) {
            return outcome.repaired() == 1
                    ? Text.translatableWithFallback("hcm.reparation.faite", "Objet réparé pour %s Martins.",
                            Text.literal(String.valueOf(outcome.paid()))).formatted(Formatting.GREEN)
                    : Text.translatableWithFallback("hcm.reparation.toutes", "%s objets réparés pour %s Martins.",
                            Text.literal(String.valueOf(outcome.repaired())),
                            Text.literal(String.valueOf(outcome.paid()))).formatted(Formatting.GREEN);
        }
        return switch (outcome.refusal()) {
            case RIEN_A_REPARER -> Text.translatableWithFallback("hcm.reparation.refus.rien",
                    "Rien à réparer.").formatted(Formatting.GRAY);
            case MONNAIE_ABSENTE -> Text.translatableWithFallback("hcm.reparation.refus.monnaie_absente",
                    "La monnaie du serveur n'est pas disponible — prévenez un administrateur.").formatted(Formatting.RED);
            case MONNAIE_INSUFFISANTE -> Text.translatableWithFallback("hcm.reparation.refus.monnaie",
                    "Il vous manque des Martins : rien n'a été réparé.").formatted(Formatting.RED);
        };
    }
}
