package net.hautecapitale.metiers.profession;

import net.hautecapitale.metiers.config.MetiersConfig;

/**
 * Courbe d'expérience des métiers.
 *
 * <p>Les valeurs viennent entièrement de la configuration. Le résultat est mis
 * en cache parce qu'il est lu à chaque gain d'XP, mais le cache est invalidé
 * dès que la configuration change.
 */
public final class XpCurve {

    private static double[] cache = null;
    private static double cachedBase;
    private static double cachedExponent;
    private static int cachedMaxLevel;

    private XpCurve() {
    }

    /** XP nécessaire pour passer du niveau donné au suivant. */
    public static double xpToNextLevel(int level) {
        MetiersConfig config = MetiersConfig.get();
        if (level >= config.niveau_max) {
            return Double.POSITIVE_INFINITY;
        }
        if (level < 1) {
            level = 1;
        }
        double[] table = table(config);
        return table[level];
    }

    /** Invalide le cache — à appeler après tout rechargement de configuration. */
    public static void invalidate() {
        cache = null;
    }

    /** Résultat d'un gain d'XP : l'état atteint, et le nombre de niveaux franchis. */
    public record Gain(int level, double xp, int levelsGained) {
    }

    /**
     * Applique un gain d'XP à un couple niveau/XP.
     *
     * <p>Fonction pure — aucun accès au joueur ni au monde. C'est ce qui permet
     * de la tester hors du jeu, et c'est le seul endroit où vivent les règles de
     * passage de niveau : gains multiples enchaînés, et plafond au niveau
     * maximum où l'XP excédentaire est écartée plutôt qu'accumulée.
     */
    public static Gain applyXp(int level, double xp, double amount, int maxLevel) {
        if (amount <= 0.0D || !Double.isFinite(amount) || level >= maxLevel) {
            return new Gain(level, level >= maxLevel ? 0.0D : xp, 0);
        }
        double total = xp + amount;
        int gained = 0;
        while (level < maxLevel) {
            double required = xpToNextLevel(level);
            if (!Double.isFinite(required) || total < required) {
                break;
            }
            total -= required;
            level++;
            gained++;
        }
        if (level >= maxLevel) {
            total = 0.0D;
        }
        return new Gain(level, total, gained);
    }

    private static double[] table(MetiersConfig config) {
        if (cache != null
                && cachedBase == config.xp_base
                && cachedExponent == config.xp_exposant
                && cachedMaxLevel == config.niveau_max) {
            return cache;
        }
        double[] table = new double[config.niveau_max + 1];
        for (int level = 1; level < config.niveau_max; level++) {
            table[level] = Math.floor(config.xp_base * Math.pow(level, config.xp_exposant));
        }
        cache = table;
        cachedBase = config.xp_base;
        cachedExponent = config.xp_exposant;
        cachedMaxLevel = config.niveau_max;
        return table;
    }
}
