package net.hautecapitale.metiers.craft;

import net.minecraft.registry.Registries;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;

import java.util.Locale;

/**
 * Ce qu'on dit au joueur après une fabrication — réussie ou refusée.
 *
 * <p>Un refus doit dire <em>pourquoi</em>. « Impossible » ne sert à personne ;
 * « il manque de la monnaie » se corrige.
 */
public final class CraftFeedback {

    private CraftFeedback() {
    }

    public static Text describe(CraftRecipe recipe, CraftEngine.Outcome outcome) {
        if (!outcome.succeeded()) {
            return refusal(outcome.refusal());
        }

        Text result = Registries.ITEM.containsId(recipe.result().item())
                ? Registries.ITEM.get(recipe.result().item()).getName()
                : Text.literal(recipe.result().item().toString());
        int made = recipe.result().count() * outcome.crafted();

        MutableText message = Text.translatableWithFallback(
                "hcm.fabrication.reussie", "Fabriqué : %s ×%s",
                result, Text.literal(String.valueOf(made))).formatted(Formatting.GREEN);

        if (outcome.xpGained() > 0.0D) {
            message.append(Text.literal(String.format(Locale.ROOT, "  +%.0f XP", outcome.xpGained()))
                    .formatted(Formatting.AQUA));
        } else {
            message.append(Text.translatableWithFallback(
                    "hcm.fabrication.sans_xp", "  (aucune XP : recette trop facile pour votre niveau)")
                    .formatted(Formatting.GRAY));
        }
        if (outcome.levelsGained() > 0) {
            message.append(Text.translatableWithFallback(
                    "hcm.fabrication.niveau", "  Niveau supérieur !").formatted(Formatting.GOLD));
        }
        if (outcome.excellent() > 0) {
            message.append(Text.translatableWithFallback(
                    "hcm.fabrication.excellent", "  ✦ %s Excellent !", Text.literal(String.valueOf(outcome.excellent())))
                    .formatted(Formatting.GOLD));
        }
        if (outcome.masteryAfter() == Mastery.MASTERED && outcome.masteryBefore() != Mastery.MASTERED) {
            message.append(Text.translatableWithFallback(
                    "hcm.fabrication.maitrisee", "  ✦ Recette maîtrisée !").formatted(Formatting.LIGHT_PURPLE));
        }
        return message;
    }

    public static Text refusal(CraftEngine.Refusal refusal) {
        String key = "hcm.fabrication.refus." + refusal.name().toLowerCase(Locale.ROOT);
        String fallback = switch (refusal) {
            case RECETTE_INCONNUE -> "Cette recette n'existe plus.";
            case METIER_NON_APPRIS -> "Vous n'exercez pas ce métier.";
            case NIVEAU_INSUFFISANT -> "Votre niveau est insuffisant pour cette recette.";
            case INGREDIENTS_MANQUANTS -> "Il vous manque des ingrédients.";
            case MONNAIE_INSUFFISANTE -> "Il vous manque de la monnaie.";
            case MONNAIE_ABSENTE -> "La monnaie du serveur n'est pas disponible — prévenez un administrateur.";
            case RESULTAT_INCONNU -> "Le résultat de cette recette n'existe pas dans cette installation — prévenez un administrateur.";
        };
        return Text.translatableWithFallback(key, fallback).formatted(Formatting.RED);
    }
}
