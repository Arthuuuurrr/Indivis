package net.hautecapitale.metiers.profession;

/**
 * Rang dérivé du niveau. Rien n'est stocké : le rang se recalcule toujours
 * depuis le niveau, il ne peut donc jamais désynchroniser.
 */
public enum Rank {
    APPRENTI(1, 10),
    COMPAGNON(11, 20),
    EXPERT(21, 30),
    MAITRE(31, 40),
    GRAND_MAITRE(41, 50);

    private final int minLevel;
    private final int maxLevel;
    private final String translationKey;

    Rank(int minLevel, int maxLevel) {
        this.minLevel = minLevel;
        this.maxLevel = maxLevel;
        this.translationKey = "rank.haute_capitale_metiers." + name().toLowerCase(java.util.Locale.ROOT);
    }

    public int getMinLevel() {
        return minLevel;
    }

    public int getMaxLevel() {
        return maxLevel;
    }

    public String getTranslationKey() {
        return translationKey;
    }

    /**
     * Rang correspondant à un niveau. Les niveaux hors bornes sont ramenés aux
     * extrémités plutôt que de lever une exception : un niveau invalide ne doit
     * jamais faire échouer un affichage.
     */
    public static Rank fromLevel(int level) {
        if (level <= APPRENTI.maxLevel) {
            return APPRENTI;
        }
        for (Rank rank : values()) {
            if (level <= rank.maxLevel) {
                return rank;
            }
        }
        return GRAND_MAITRE;
    }
}
