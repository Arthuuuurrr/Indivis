package net.hautecapitale.party.combat;

import net.fabricmc.fabric.api.entity.event.v1.ServerLivingEntityEvents;
import net.hautecapitale.party.config.PartyConfig;
import net.hautecapitale.party.party.PartyManager;
import net.minecraft.entity.Entity;
import net.minecraft.entity.Ownable;
import net.minecraft.entity.Tameable;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.projectile.ProjectileEntity;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerPlayerEntity;

import java.util.UUID;

/**
 * Le filet qui complete l'equipe de scoreboard.
 *
 * <p>L'essentiel de la protection entre membres ne se joue pas ici mais dans
 * {@code PartyTeams} : une equipe vanilla au tir ami desactive suffit a Minecraft,
 * a Spell Engine et a Better Combat, qui en deduisent tous les trois que les
 * membres sont allies. Melee, fleches, sorts nuisibles directs et de zone,
 * balayages : couverts, sans qu'aucun de ces mods soit modifie.
 *
 * <p>Restent les degats qui ne portent pas la signature d'un joueur, et que la
 * verification d'equipe de Minecraft ne voit donc pas :
 * <ul>
 *   <li>ceux d'une invocation ou d'un familier appartenant a un membre — l'entite
 *       causante est la creature, pas son maitre ;</li>
 *   <li>ceux d'un projectile dont seul le proprietaire relie au joueur ;</li>
 *   <li>plus generalement toute chaine de propriete de plusieurs niveaux.</li>
 * </ul>
 *
 * <p>C'est le seul code de tir ami de ce mod, et il tient en une remontee de
 * chaine de propriete.
 */
public final class FriendlyFire {

    /**
     * Nombre maximal de maillons remontes.
     *
     * <p>Une invocation qui lance un projectile fait deja deux niveaux ; huit laisse
     * de la marge sans jamais boucler indefiniment si un mod construit un cycle de
     * proprietaires.
     */
    private static final int MAX_OWNER_DEPTH = 8;

    private FriendlyFire() {
    }

    public static void register() {
        ServerLivingEntityEvents.ALLOW_DAMAGE.register((entity, source, amount) ->
                !(entity instanceof ServerPlayerEntity victim) || allows(victim, source));
    }

    /**
     * Le verdict de ce mod seul, sans les autres ecouteurs de l'evenement.
     *
     * <p>Expose pour que le diagnostic puisse l'interroger directement : compare a
     * ce que rend l'evenement complet, cela dit tout de suite si un refus vient de
     * nous ou d'un autre mod.
     *
     * @return vrai si le degat peut passer
     */
    public static boolean allows(ServerPlayerEntity victim, DamageSource source) {
        PartyConfig config = PartyConfig.get();
        if (!config.friendlyFireProtection) {
            return true;
        }
        MinecraftServer server = victim.getEntityWorld().getServer();
        if (server == null) {
            return true;
        }
        UUID origin = originPlayer(source, config.guardOwnedEntities);
        if (origin == null || origin.equals(victim.getUuid())) {
            // Degat sans auteur (chute, noyade, feu) ou auto-inflige : jamais bloque.
            return true;
        }
        return !PartyManager.sameParty(server, origin, victim.getUuid());
    }

    /**
     * Le joueur a la racine de la chaine de propriete d'une entite (le maitre d'une invocation ou
     * d'un familier), ou {@code null} si l'entite n'appartient a aucun joueur. Reutilise par la garde
     * de <b>ciblage</b> des invocations (empeche un golem de cibler les allies de son invocateur).
     */
    public static UUID rootPlayer(Entity entity) {
        return rootPlayerOf(entity, true);
    }

    /**
     * Le joueur a l'origine d'un degat, s'il y en a un.
     *
     * <p>On essaie l'entite causante d'abord — pour un projectile, Minecraft y met
     * deja le tireur — puis l'entite directe, qui rattrape les sources ou la
     * causante n'a pas ete renseignee.
     */
    private static UUID originPlayer(DamageSource source, boolean followOwners) {
        UUID fromAttacker = rootPlayerOf(source.getAttacker(), followOwners);
        if (fromAttacker != null) {
            return fromAttacker;
        }
        return rootPlayerOf(source.getSource(), followOwners);
    }

    /**
     * Remonte la chaine de propriete jusqu'a un joueur.
     *
     * @param followOwners si faux, seule l'entite elle-meme est examinee : la
     *                     protection se limite alors a ce que couvre deja l'equipe
     *                     de scoreboard
     */
    private static UUID rootPlayerOf(Entity entity, boolean followOwners) {
        Entity current = entity;
        for (int depth = 0; current != null && depth < MAX_OWNER_DEPTH; depth++) {
            if (current instanceof PlayerEntity player) {
                return player.getUuid();
            }
            if (!followOwners) {
                return null;
            }
            Entity owner = ownerOf(current);
            if (owner == null || owner == current) {
                return null;
            }
            current = owner;
        }
        return null;
    }

    /**
     * Le proprietaire declare d'une entite.
     *
     * <p>Les deux formes existent cote a cote dans le jeu : {@code ProjectileEntity}
     * porte son tireur sans implementer {@code Ownable}, tandis que les creatures
     * apprivoisees et la plupart des invocations de mods passent par {@code Ownable}.
     */
    private static Entity ownerOf(Entity entity) {
        if (entity instanceof ProjectileEntity projectile) {
            return projectile.getOwner();
        }
        if (entity instanceof Ownable ownable) {
            return ownable.getOwner();
        }
        // 1.21.11 : les creatures apprivoisees / la plupart des invocations de mods (golems
        // d'invocateur, familiers) implementent Tameable, PAS Ownable — d'ou ce second cas, sans
        // lequel ni les degats ni le ciblage d'un familier ne seraient reconnus.
        if (entity instanceof Tameable tameable) {
            return tameable.getOwner();
        }
        return null;
    }
}
