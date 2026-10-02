package net.hautecapitale.metiers.craft;

import net.hautecapitale.metiers.config.MetiersConfig;

import java.util.List;

/**
 * La décote : moins une recette est difficile pour le joueur, moins elle
 * rapporte.
 *
 * <p>C'est la règle qui empêche un Forgeron 50 de monter sur 5 000 objets de
 * niveau 1. Elle se lit sur l'écart {@code niveau du joueur − niveau de la
 * recette} : jusqu'à 5 niveaux d'écart, plein tarif ; puis 60 %, 25 %, 5 %, et
 * plus rien au-delà de 20. Les paliers sont ceux de la configuration, jamais
 * des constantes d'ici.
 *
 * <p>La même décote servira aux nodes et aux proies : une vache de niveau 5 ne
 * rapporte rien à un Dépeceur 30, même naturelle.
 */
public final class XpFalloff {

    private XpFalloff() {
    }

    /**
     * Part de l'XP accordée, entre 0 et 1.
     *
     * @param playerLevel niveau du joueur dans le métier
     * @param recipeLevel niveau requis par la recette
     */
    public static double factor(int playerLevel, int recipeLevel) {
        return factor(playerLevel - recipeLevel, MetiersConfig.get().decote);
    }

    /** Même calcul, sur des paliers explicites — pour les tests et l'inspection. */
    public static double factor(int gap, List<MetiersConfig.Palier> paliers) {
        if (gap < 0) {
            // Une recette au-dessus du niveau du joueur ne se fabrique pas ;
            // si on arrive tout de même ici, elle vaut plein tarif.
            return 1.0D;
        }
        for (MetiersConfig.Palier palier : paliers) {
            if (gap <= palier.ecart) {
                return Math.clamp(palier.pourcent, 0, 100) / 100.0D;
            }
        }
        return 0.0D;
    }

    /** L'XP effectivement accordée pour une recette. */
    public static double apply(double baseXp, int playerLevel, int recipeLevel) {
        return baseXp * factor(playerLevel, recipeLevel);
    }

    /** « 100 % », « 25 % »… pour l'affichage. */
    public static String describe(int playerLevel, int recipeLevel) {
        return Math.round(factor(playerLevel, recipeLevel) * 100.0D) + " %";
    }
}
