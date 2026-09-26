package net.hautecapitale.metiers.craft;

import net.hautecapitale.metiers.config.MetiersConfig;

/**
 * La maîtrise d'une recette : un compteur invisible par recette, et trois états.
 *
 * <p>Strictement du confort. La maîtrise ne donne jamais d'XP, n'améliore pas
 * l'objet, ne réduit pas les composants et ne débloque rien au-dessus du niveau.
 * Elle marque la recette d'un ✦, la fait remonter dans la liste, et ouvre la
 * fabrication multiple. Une recette trop basse reste à 0 XP, maîtrisée ou non.
 *
 * <p>Les deux seuils viennent de la configuration.
 */
public enum Mastery {
    /** Moins de cinq fabrications : rien. */
    NONE,
    /** Une jauge discrète, et la recette remonte dans la liste. */
    LEARNING,
    /** Marque ✦, fabrication ×5 et ×10. */
    MASTERED;

    public static Mastery of(int crafts) {
        return of(crafts, MetiersConfig.get().maitrise_apprentissage, MetiersConfig.get().maitrise_acquise);
    }

    /** Même calcul, sur des seuils explicites — pour les tests. */
    public static Mastery of(int crafts, int learningAt, int masteredAt) {
        if (crafts >= Math.max(masteredAt, 1)) {
            return MASTERED;
        }
        if (crafts >= Math.max(learningAt, 1)) {
            return LEARNING;
        }
        return NONE;
    }

    /** Ce qu'il reste à fabriquer avant la maîtrise, 0 si acquise. */
    public static int remaining(int crafts) {
        return Math.max(0, MetiersConfig.get().maitrise_acquise - crafts);
    }

    /** Les multiplicateurs de fabrication ouverts à ce stade. */
    public int[] batches() {
        return this == MASTERED ? new int[]{1, 5, 10} : new int[]{1};
    }
}
