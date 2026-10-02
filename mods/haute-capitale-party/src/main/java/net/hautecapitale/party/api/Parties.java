package net.hautecapitale.party.api;

import net.hautecapitale.party.party.Party;
import net.hautecapitale.party.party.PartyManager;
import net.hautecapitale.party.party.Role;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerPlayerEntity;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Facade publique du systeme de groupe.
 *
 * <p>Point d'entree unique et stable. Les modules a venir — instances, donjons,
 * HUD, recompenses — passent par ici, jamais par le magasin ni par le gestionnaire
 * directement. Cela laisse la liberte de changer le stockage sans casser les
 * appelants, comme le noyau RPG le fait deja avec {@code Classes}.
 */
public final class Parties {

    private Parties() {
    }

    /** Le groupe d'un joueur, s'il en a un. */
    public static Optional<Party> of(ServerPlayerEntity player) {
        MinecraftServer server = player.getEntityWorld().getServer();
        return server == null ? Optional.empty() : PartyManager.partyOf(server, player.getUuid());
    }

    public static Optional<Party> of(MinecraftServer server, UUID player) {
        return PartyManager.partyOf(server, player);
    }

    /**
     * Vrai si les deux joueurs sont dans le meme groupe.
     *
     * <p>Un joueur n'est jamais « dans le meme groupe » que lui-meme : les appelants
     * cherchent presque toujours a savoir s'il faut proteger un tiers, et repondre
     * vrai bloquerait les degats qu'un joueur s'inflige.
     */
    public static boolean sameParty(MinecraftServer server, UUID a, UUID b) {
        return PartyManager.sameParty(server, a, b);
    }

    public static boolean isLeader(ServerPlayerEntity player) {
        return of(player).map(party -> party.isLeader(player.getUuid())).orElse(false);
    }

    public static Role roleOf(ServerPlayerEntity player) {
        return of(player).map(party -> party.roleOf(player.getUuid())).orElse(Role.UNSET);
    }

    /**
     * Le compteur de composition du groupe d'un joueur.
     *
     * <p>A memoriser au debut de toute operation collective et a revalider avant de
     * la conclure : c'est le verrou qui empeche qu'une reponse tardive engage un
     * groupe qui a change entre temps (regles §19 et §20).
     */
    public static Optional<Integer> epochOf(ServerPlayerEntity player) {
        return of(player).map(Party::epoch);
    }

    public static List<ServerPlayerEntity> onlineMembers(MinecraftServer server, Party party) {
        return PartyManager.onlineMembers(server, party);
    }

    /** Plafond effectif du type de groupe, configuration comprise. */
    public static int maxSizeOf(Party party) {
        return PartyManager.maxSizeOf(party.type());
    }
}
