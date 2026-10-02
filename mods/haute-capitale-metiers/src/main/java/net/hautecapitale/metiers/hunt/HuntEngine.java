package net.hautecapitale.metiers.hunt;

import net.fabricmc.fabric.api.entity.event.v1.ServerLivingEntityEvents;
import net.hautecapitale.metiers.api.Metiers;
import net.hautecapitale.metiers.craft.XpFalloff;
import net.hautecapitale.metiers.creature.CreatureEnums.SpawnOrigin;
import net.hautecapitale.metiers.creature.CreatureProfile;
import net.hautecapitale.metiers.creature.DropRoll;
import net.hautecapitale.metiers.entity.CarcassEntity;
import net.hautecapitale.metiers.data.HcmData;
import net.hautecapitale.metiers.profession.Profession;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.registry.Registries;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.random.Random;

import java.util.List;

/**
 * Le Chasseur : ce qui se passe quand une créature déclarée meurt.
 *
 * <p>Un seul écouteur, sur la mort de l'entité, et une règle par question :
 * <ul>
 *   <li><b>Qui est crédité ?</b> Le joueur qui a porté le coup fatal — flèche
 *       comprise — ou, s'il n'y en a pas, celui qui l'a frappée dans les cinq
 *       dernières secondes, comme Minecraft le fait pour ses propres butins
 *       rares. Une mort sans joueur ne donne rien à personne. Deux joueurs sur
 *       la même proie : un seul coup est fatal, un seul est crédité.</li>
 *   <li><b>D'où vient-elle ?</b> Son origine, marquée à l'apparition. La fiche
 *       dit quelles origines comptent. Un élevage, un générateur, un œuf ne
 *       donnent ni XP ni butin de combat — c'est l'anti-farm.</li>
 *   <li><b>Combien d'XP ?</b> Rien sans le métier ni sans le niveau requis ;
 *       sinon l'XP de la fiche, décotée comme en fabrication quand la proie
 *       devient trop facile.</li>
 *   <li><b>Quel butin ?</b> Le butin de combat de la fiche, tiré au sort et
 *       déposé au sol, pour tout joueur — pas seulement un Chasseur. La viande
 *       aussi, mais elle ne regarde pas l'origine : élever pour manger est un
 *       jeu légitime, et Minecraft le permet déjà.</li>
 * </ul>
 *
 * <p>Aucune table de butin n'est réécrite : 158 des 220 créatures du serveur
 * n'en ont pas, et celles qui en ont la gardent. Ce butin s'ajoute.
 */
public final class HuntEngine {

    /** Pourquoi une mort n'a pas rapporté d'XP — ou {@code XP} si elle en a rapporté. */
    public enum Verdict {
        XP,
        SANS_XP_TROP_FACILE,
        ORIGINE_NON_ELIGIBLE,
        NIVEAU_INSUFFISANT,
        METIER_NON_APPRIS,
        PAS_UNE_PROIE
    }

    public record Outcome(CreatureProfile profile, SpawnOrigin origin, boolean eligible, Verdict verdict,
                          double xpGained, int levelsGained, List<ItemStack> loot, List<ItemStack> meat,
                          CarcassEntity carcass) {
        public boolean rewarded() {
            return xpGained > 0.0D || !loot.isEmpty() || !meat.isEmpty();
        }
    }

    private HuntEngine() {
    }

    public static void init() {
        ServerLivingEntityEvents.AFTER_DEATH.register(HuntEngine::onDeath);
    }

    // ------------------------------------------------------------------

    static void onDeath(LivingEntity entity, DamageSource source) {
        if (entity instanceof PlayerEntity || !(entity.getEntityWorld() instanceof ServerWorld world)) {
            return;
        }
        CreatureProfile profile = profileOf(entity);
        if (profile == null) {
            return;
        }
        ServerPlayerEntity killer = killerOf(entity, source);
        if (killer == null) {
            return;
        }
        Outcome outcome = kill(world, entity, killer, profile, OriginMarker.resolve(entity));
        HuntFeedback.overlay(killer, entity, outcome);
    }

    /** La fiche de cette créature, ou {@code null} si elle n'en a pas. */
    public static CreatureProfile profileOf(LivingEntity entity) {
        Identifier id = Registries.ENTITY_TYPE.getId(entity.getType());
        return HcmData.CREATURES.get(id);
    }

    /** Le joueur à créditer, ou {@code null} : coup fatal d'abord, dernier assaillant ensuite. */
    public static ServerPlayerEntity killerOf(LivingEntity entity, DamageSource source) {
        if (source != null && source.getAttacker() instanceof ServerPlayerEntity player) {
            return player;
        }
        if (entity.getAttackingPlayer() instanceof ServerPlayerEntity player) {
            return player;
        }
        return null;
    }

    /**
     * Applique la fiche à une mort créditée. Séparé de l'écouteur pour que le
     * diagnostic puisse le nourrir d'une fiche et d'une origine choisies.
     */
    public static Outcome kill(ServerWorld world, LivingEntity entity, ServerPlayerEntity killer,
                               CreatureProfile profile, SpawnOrigin origin) {
        boolean eligible = profile.grantsXpFrom(origin);

        // --- XP du Chasseur.
        Verdict verdict;
        double xp = 0.0D;
        int levels = 0;
        if (profile.hunter().isEmpty()) {
            verdict = Verdict.PAS_UNE_PROIE;
        } else if (!eligible) {
            verdict = Verdict.ORIGINE_NON_ELIGIBLE;
        } else if (!Metiers.hasProfession(killer, Profession.CHASSEUR)) {
            verdict = Verdict.METIER_NON_APPRIS;
        } else {
            CreatureProfile.HunterEntry hunter = profile.hunter().get();
            int level = Metiers.getLevel(killer, Profession.CHASSEUR);
            if (level < hunter.level()) {
                verdict = Verdict.NIVEAU_INSUFFISANT;
            } else {
                xp = XpFalloff.apply(hunter.xp(), level, hunter.level());
                if (xp > 0.0D) {
                    levels = Metiers.addXp(killer, Profession.CHASSEUR, xp);
                    verdict = Verdict.XP;
                } else {
                    verdict = Verdict.SANS_XP_TROP_FACILE;
                }
            }
        }

        // --- Butin, au sol, là où la créature est tombée.
        Random random = world.getRandom();
        List<ItemStack> loot = eligible ? DropRoll.roll(profile.combatLoot(), random) : List.of();
        List<ItemStack> meat = profile.meat().map(entry -> DropRoll.roll(List.of(entry), random)).orElse(List.of());
        for (ItemStack stack : loot) {
            entity.dropStack(world, stack.copy());
        }
        for (ItemStack stack : meat) {
            entity.dropStack(world, stack.copy());
        }

        // --- La carcasse, pour le Dépeceur — même règle d'origine que le butin :
        // une bête d'élevage meurt à la vanilla, sans carcasse à travailler.
        CarcassEntity carcass = eligible && profile.isSkinnable() ? CarcassEntity.spawnFrom(world, entity, profile) : null;

        return new Outcome(profile, origin, eligible, verdict, xp, levels, loot, meat, carcass);
    }
}
