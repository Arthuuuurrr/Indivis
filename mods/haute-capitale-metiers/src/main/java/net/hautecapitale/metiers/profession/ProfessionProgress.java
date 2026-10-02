package net.hautecapitale.metiers.profession;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

/**
 * Progression d'un joueur dans un métier : son niveau, et l'XP accumulée
 * <em>depuis</em> ce niveau (pas l'XP totale — cela simplifie le passage de
 * niveau et évite de recalculer une somme à chaque lecture).
 */
public record ProfessionProgress(int level, double xp) {

    public static final ProfessionProgress INITIAL = new ProfessionProgress(1, 0.0D);

    public static final Codec<ProfessionProgress> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            Codec.INT.optionalFieldOf("niveau", 1).forGetter(ProfessionProgress::level),
            Codec.DOUBLE.optionalFieldOf("xp", 0.0D).forGetter(ProfessionProgress::xp)
    ).apply(instance, ProfessionProgress::new));

    public ProfessionProgress {
        // Une donnée corrompue ne doit pas empêcher le joueur de se connecter.
        if (level < 1) {
            level = 1;
        }
        if (xp < 0.0D || !Double.isFinite(xp)) {
            xp = 0.0D;
        }
    }

    public Rank rank() {
        return Rank.fromLevel(level);
    }

    public ProfessionProgress withLevel(int newLevel) {
        return new ProfessionProgress(newLevel, xp);
    }

    public ProfessionProgress withXp(double newXp) {
        return new ProfessionProgress(level, newXp);
    }
}
