package net.hautecapitale.metiers.api;

import net.hautecapitale.metiers.config.MetiersConfig;
import net.hautecapitale.metiers.profession.Profession;
import net.hautecapitale.metiers.profession.ProfessionAttachments;
import net.hautecapitale.metiers.profession.ProfessionProgress;
import net.hautecapitale.metiers.profession.ProfessionsState;
import net.hautecapitale.metiers.profession.Rank;
import net.hautecapitale.metiers.profession.XpCurve;
import net.minecraft.server.network.ServerPlayerEntity;

/**
 * Point d'entrée unique pour tout ce qui touche à la progression de métier.
 *
 * <p>Tous les systèmes des étapes suivantes — nodes, chasse, dépeçage,
 * recettes, réparation — passent par ici. Aucun d'entre eux ne doit lire ou
 * écrire l'attachement directement : cela dupliquerait les règles de plafond,
 * de passage de niveau et de synchronisation.
 *
 * <p>Toutes les méthodes prennent un {@link ServerPlayerEntity} : la
 * progression est décidée côté serveur, sans exception.
 */
public final class Metiers {

    private Metiers() {
    }

    // ------------------------------------------------------------------
    // Lecture
    // ------------------------------------------------------------------

    private static ProfessionsState state(ServerPlayerEntity player) {
        return player.getAttachedOrCreate(ProfessionAttachments.PROFESSIONS);
    }

    /** Le joueur a-t-il appris ce métier ? */
    public static boolean hasProfession(ServerPlayerEntity player, Profession profession) {
        return state(player).has(profession);
    }

    /** Niveau du joueur dans ce métier, ou 0 s'il ne l'a pas appris. */
    public static int getLevel(ServerPlayerEntity player, Profession profession) {
        ProfessionProgress progress = state(player).get(profession);
        return progress == null ? 0 : progress.level();
    }

    /** XP accumulée depuis le niveau courant, ou 0 si le métier n'est pas appris. */
    public static double getXp(ServerPlayerEntity player, Profession profession) {
        ProfessionProgress progress = state(player).get(profession);
        return progress == null ? 0.0D : progress.xp();
    }

    /** XP restante avant le niveau suivant. Infinie au niveau maximum. */
    public static double getXpToNextLevel(ServerPlayerEntity player, Profession profession) {
        ProfessionProgress progress = state(player).get(profession);
        if (progress == null) {
            return XpCurve.xpToNextLevel(1);
        }
        double required = XpCurve.xpToNextLevel(progress.level());
        return Double.isInfinite(required) ? required : Math.max(0.0D, required - progress.xp());
    }

    /** Rang du joueur dans ce métier, ou {@code null} s'il ne l'a pas appris. */
    public static Rank getRank(ServerPlayerEntity player, Profession profession) {
        ProfessionProgress progress = state(player).get(profession);
        return progress == null ? null : progress.rank();
    }

    /** Le joueur atteint-il au moins ce niveau ? Faux s'il n'a pas le métier. */
    public static boolean hasLevel(ServerPlayerEntity player, Profession profession, int minLevel) {
        return getLevel(player, profession) >= minLevel;
    }

    public static ProfessionsState getState(ServerPlayerEntity player) {
        return state(player);
    }

    // ------------------------------------------------------------------
    // Écriture
    // ------------------------------------------------------------------

    /**
     * Fait apprendre un métier au joueur.
     *
     * @return {@code false} si le métier était déjà appris, ou si le plafond de
     *         métiers simultanés est atteint.
     */
    public static boolean learn(ServerPlayerEntity player, Profession profession) {
        ProfessionsState current = state(player);
        if (current.has(profession)) {
            return false;
        }
        int cap = MetiersConfig.get().metiers_max_par_joueur;
        if (cap > 0 && current.count() >= cap) {
            return false;
        }
        player.setAttached(ProfessionAttachments.PROFESSIONS,
                current.with(profession, ProfessionProgress.INITIAL));
        return true;
    }

    /**
     * Ajoute de l'XP et applique les passages de niveau qui en découlent.
     *
     * <p>Un gain massif peut faire franchir plusieurs niveaux d'un coup : la
     * boucle les traite tous. Au niveau maximum, l'XP excédentaire est écartée
     * plutôt que de s'accumuler sans fin.
     *
     * @return le nombre de niveaux gagnés, 0 si aucun.
     */
    public static int addXp(ServerPlayerEntity player, Profession profession, double amount) {
        if (amount <= 0.0D || !Double.isFinite(amount)) {
            return 0;
        }
        ProfessionsState current = state(player);
        ProfessionProgress progress = current.get(profession);
        if (progress == null) {
            return 0;
        }

        int maxLevel = MetiersConfig.get().niveau_max;
        XpCurve.Gain gain = XpCurve.applyXp(progress.level(), progress.xp(), amount, maxLevel);
        if (gain.level() == progress.level() && gain.xp() == progress.xp()) {
            return 0;
        }

        player.setAttached(ProfessionAttachments.PROFESSIONS,
                current.with(profession, new ProfessionProgress(gain.level(), gain.xp())));
        return gain.levelsGained();
    }

    /**
     * Fixe directement le niveau, en remettant l'XP du palier à zéro.
     * Apprend le métier au passage s'il ne l'était pas.
     *
     * @return {@code false} si le niveau demandé est hors bornes.
     */
    public static boolean setLevel(ServerPlayerEntity player, Profession profession, int level) {
        int maxLevel = MetiersConfig.get().niveau_max;
        if (level < 1 || level > maxLevel) {
            return false;
        }
        ProfessionsState current = state(player);
        player.setAttached(ProfessionAttachments.PROFESSIONS,
                current.with(profession, new ProfessionProgress(level, 0.0D)));
        return true;
    }

    /** Remet un métier au niveau 1 sans XP. Ne le retire pas au joueur. */
    public static boolean reset(ServerPlayerEntity player, Profession profession) {
        ProfessionsState current = state(player);
        if (!current.has(profession)) {
            return false;
        }
        player.setAttached(ProfessionAttachments.PROFESSIONS,
                current.with(profession, ProfessionProgress.INITIAL));
        return true;
    }

    /** Retire complètement un métier au joueur. */
    public static boolean forget(ServerPlayerEntity player, Profession profession) {
        ProfessionsState current = state(player);
        if (!current.has(profession)) {
            return false;
        }
        player.setAttached(ProfessionAttachments.PROFESSIONS, current.without(profession));
        return true;
    }

    /** Efface tous les métiers du joueur. */
    public static void resetAll(ServerPlayerEntity player) {
        player.setAttached(ProfessionAttachments.PROFESSIONS, ProfessionsState.empty());
    }
}
