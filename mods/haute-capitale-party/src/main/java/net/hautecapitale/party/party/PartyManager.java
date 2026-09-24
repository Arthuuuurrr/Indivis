package net.hautecapitale.party.party;

import net.hautecapitale.party.HauteCapitaleParty;
import net.hautecapitale.party.config.PartyConfig;
import net.hautecapitale.party.invite.Invites;
import net.hautecapitale.party.team.PartyTeams;
import net.hautecapitale.party.text.Msg;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Toutes les operations de groupe, et le seul endroit qui a le droit de les faire.
 *
 * <p>Serveur autoritaire (regle §62) : les commandes ne font que transmettre une
 * intention, chaque verification a lieu ici, et rien n'est renvoye au client qu'un
 * texte deja decide.
 *
 * <p>Chaque mutation suit le meme ordre — verifier, muter le modele, ajuster
 * l'equipe de scoreboard, annoncer. L'equipe est ajustee <em>apres</em> le modele
 * pour qu'une exception de scoreboard ne laisse jamais un groupe a moitie constitue.
 */
public final class PartyManager {

    private static Invites invites = new Invites();

    private PartyManager() {
    }

    /** Repart d'invitations vierges a chaque demarrage : elles ne sont pas persistees. */
    public static void resetTransient() {
        invites = new Invites();
    }

    public static Invites invites() {
        return invites;
    }

    private static long now() {
        return System.currentTimeMillis();
    }

    // --- lecture -------------------------------------------------------------

    public static Optional<Party> partyOf(MinecraftServer server, UUID player) {
        return PartyStore.of(server).partyOf(player);
    }

    /** Un groupe par son identifiant. Sert aux consultations, qui retiennent l'identifiant et non l'objet. */
    public static Optional<Party> byId(MinecraftServer server, UUID partyId) {
        return PartyStore.of(server).byId(partyId);
    }

    /** Vrai si les deux joueurs sont dans le meme groupe. Deux acces indexes, rien de plus. */
    public static boolean sameParty(MinecraftServer server, UUID a, UUID b) {
        if (a.equals(b)) {
            return false;
        }
        PartyStore store = PartyStore.of(server);
        Optional<Party> party = store.partyOf(a);
        return party.isPresent() && party.get().contains(b);
    }

    // --- creation ------------------------------------------------------------

    public static Outcome create(MinecraftServer server, ServerPlayerEntity player, PartyType type) {
        PartyStore store = PartyStore.of(server);
        if (store.hasParty(player.getUuid())) {
            return Outcome.fail(Msg.bad("Vous etes deja dans un groupe. Quittez-le d'abord avec /party leave."));
        }
        Party party = Party.found(player.getUuid(), player.getName().getString(), type, now());
        store.register(party);
        PartyTeams.join(server, party, player);
        HauteCapitaleParty.LOGGER.info("Groupe {} cree par {}.", party.id(), player.getName().getString());
        return Outcome.ok(Msg.good("Groupe cree (" + type.label() + "). Invitez avec /party invite <joueur>."));
    }

    // --- invitations ---------------------------------------------------------

    public static Outcome invite(MinecraftServer server, ServerPlayerEntity leader, ServerPlayerEntity target) {
        PartyConfig config = PartyConfig.get();
        PartyStore store = PartyStore.of(server);

        if (leader.getUuid().equals(target.getUuid())) {
            return Outcome.fail(Msg.bad("Vous ne pouvez pas vous inviter vous-meme."));
        }
        Optional<Party> maybeParty = store.partyOf(leader.getUuid());
        if (maybeParty.isEmpty()) {
            return Outcome.fail(Msg.bad("Vous n'avez pas de groupe. Creez-en un avec /party create."));
        }
        Party party = maybeParty.get();
        if (!party.isLeader(leader.getUuid())) {
            return Outcome.fail(Msg.bad("Seul le chef du groupe peut inviter."));
        }
        if (store.hasParty(target.getUuid())) {
            return Outcome.fail(Msg.bad(target.getName().getString() + " est deja dans un groupe."));
        }
        int max = maxSizeOf(party.type());
        if (party.size() >= max) {
            return Outcome.fail(Msg.bad("Le groupe est complet (" + max + " joueurs)."));
        }
        long cooldown = invites.cooldownRemaining(leader.getUuid(), now());
        if (cooldown > 0) {
            return Outcome.fail(Msg.bad("Patientez " + cooldown + " s avant d'inviter a nouveau."));
        }

        long expiresAt = now() + config.inviteTimeoutSeconds * 1000L;
        invites.put(target.getUuid(), new Invites.Invite(party.id(), leader.getUuid(), party.epoch(), expiresAt));
        invites.noteInvite(leader.getUuid(), now(), config.inviteCooldownSeconds);

        tell(target, Msg.invitation(leader.getName().getString(), party.type().label()));
        // Fenetre Accepter / Refuser (dialogue vanilla) : la cible repond au clic, sans taper de commande.
        net.hautecapitale.party.dialog.PartyDialogs.askYesNo(target,
                net.minecraft.text.Text.literal("Invitation de groupe"),
                net.minecraft.text.Text.literal(leader.getName().getString() + " vous invite dans son groupe "
                        + party.type().label() + "."),
                net.minecraft.text.Text.literal("Accepter"), "party accept",
                net.minecraft.text.Text.literal("Refuser"), "party decline");
        return Outcome.ok(Msg.good("Invitation envoyee a " + target.getName().getString()
                + " (valable " + config.inviteTimeoutSeconds + " s)."));
    }

    public static Outcome accept(MinecraftServer server, ServerPlayerEntity player) {
        PartyStore store = PartyStore.of(server);
        if (store.hasParty(player.getUuid())) {
            return Outcome.fail(Msg.bad("Vous etes deja dans un groupe."));
        }
        Optional<Invites.Invite> maybeInvite = invites.take(player.getUuid(), now());
        if (maybeInvite.isEmpty()) {
            return Outcome.fail(Msg.bad("Aucune invitation en attente."));
        }
        Invites.Invite invite = maybeInvite.get();

        Optional<Party> maybeParty = store.byId(invite.partyId());
        if (maybeParty.isEmpty()) {
            return Outcome.fail(Msg.bad("Ce groupe n'existe plus."));
        }
        Party party = maybeParty.get();
        // Le groupe a change entre l'envoi et la reponse : l'invitation ne vaut plus.
        // Meme verrou que pour la teleportation collective (regle §20).
        if (party.epoch() != invite.partyEpoch()) {
            return Outcome.fail(Msg.bad("La composition du groupe a change : demandez une nouvelle invitation."));
        }
        int max = maxSizeOf(party.type());
        if (party.size() >= max) {
            return Outcome.fail(Msg.bad("Le groupe est complet (" + max + " joueurs)."));
        }

        party.add(player.getUuid(), player.getName().getString());
        store.indexMember(player.getUuid(), party);
        PartyTeams.join(server, party, player);

        broadcast(server, party, Msg.info(player.getName().getString() + " a rejoint le groupe. ("
                + party.size() + "/" + max + ")"));
        return Outcome.ok(Msg.good("Vous avez rejoint le groupe."));
    }

    public static Outcome decline(MinecraftServer server, ServerPlayerEntity player) {
        Optional<Invites.Invite> maybeInvite = invites.take(player.getUuid(), now());
        if (maybeInvite.isEmpty()) {
            return Outcome.fail(Msg.bad("Aucune invitation en attente."));
        }
        ServerPlayerEntity inviter = server.getPlayerManager().getPlayer(maybeInvite.get().inviter());
        if (inviter != null) {
            tell(inviter, Msg.info(player.getName().getString() + " a refuse votre invitation."));
        }
        return Outcome.ok(Msg.info("Invitation refusee."));
    }

    // --- depart --------------------------------------------------------------

    public static Outcome leave(MinecraftServer server, ServerPlayerEntity player) {
        PartyStore store = PartyStore.of(server);
        Optional<Party> maybeParty = store.partyOf(player.getUuid());
        if (maybeParty.isEmpty()) {
            return Outcome.fail(Msg.bad("Vous n'etes dans aucun groupe."));
        }
        Party party = maybeParty.get();
        removeMember(server, store, party, player.getUuid(),
                player.getName().getString() + " a quitte le groupe.");
        return Outcome.ok(Msg.info("Vous avez quitte le groupe."));
    }

    /**
     * Retire un membre et remet le groupe d'aplomb.
     *
     * <p>Chemin unique pour un depart volontaire comme pour une exclusion : c'est ce
     * qui garantit qu'un chef exclu et un chef parti produisent exactement le meme
     * transfert de leadership.
     */
    private static void removeMember(MinecraftServer server, PartyStore store, Party party,
                                     UUID member, String announcement) {
        ServerPlayerEntity player = server.getPlayerManager().getPlayer(member);
        boolean wasLeader = party.isLeader(member);

        if (player != null) {
            PartyTeams.leave(server, party, player);
        }
        party.remove(member);
        store.unindexMember(member);
        invites.clear(member);

        if (party.isEmpty()) {
            disbandInternal(server, store, party, null);
            return;
        }
        if (wasLeader) {
            // Successeur : le membre restant le plus ancien. Regle deterministe,
            // donc identique apres un redemarrage (regle §27).
            party.nextLeaderAfter(member).ifPresent(next -> {
                party.transferTo(next);
                broadcast(server, party, Msg.info(party.nameOf(next) + " devient chef du groupe."));
            });
        }
        store.markDirty();
        broadcast(server, party, Msg.info(announcement));
    }

    // --- chef ----------------------------------------------------------------

    public static Outcome transfer(MinecraftServer server, ServerPlayerEntity leader, ServerPlayerEntity target) {
        PartyStore store = PartyStore.of(server);
        Optional<Party> maybeParty = store.partyOf(leader.getUuid());
        if (maybeParty.isEmpty()) {
            return Outcome.fail(Msg.bad("Vous n'etes dans aucun groupe."));
        }
        Party party = maybeParty.get();
        if (!party.isLeader(leader.getUuid())) {
            return Outcome.fail(Msg.bad("Seul le chef peut transmettre le commandement."));
        }
        if (!party.contains(target.getUuid())) {
            return Outcome.fail(Msg.bad(target.getName().getString() + " n'est pas dans votre groupe."));
        }
        if (!party.transferTo(target.getUuid())) {
            return Outcome.fail(Msg.bad("Cette personne est deja chef."));
        }
        store.markDirty();
        broadcast(server, party, Msg.info(target.getName().getString() + " devient chef du groupe."));
        return Outcome.ok(Msg.good("Commandement transmis."));
    }

    /**
     * Exclusion directe par le chef.
     *
     * <p>Reservee aux petits groupes. Des que le groupe atteint la taille ou un vote
     * a du sens, l'exclusion passe par le vote (regle §15) : la commande renvoie alors
     * vers lui plutot que d'agir. Le vote lui-meme arrive en phase 2 — d'ici la, la
     * commande refuse et le dit, ce qui vaut mieux qu'un chef tout-puissant qu'il
     * faudrait ensuite lui retirer.
     */
    public static Outcome kick(MinecraftServer server, ServerPlayerEntity leader, ServerPlayerEntity target) {
        PartyStore store = PartyStore.of(server);
        Optional<Party> maybeParty = store.partyOf(leader.getUuid());
        if (maybeParty.isEmpty()) {
            return Outcome.fail(Msg.bad("Vous n'etes dans aucun groupe."));
        }
        Party party = maybeParty.get();
        if (!party.isLeader(leader.getUuid())) {
            return Outcome.fail(Msg.bad("Seul le chef peut exclure."));
        }
        if (leader.getUuid().equals(target.getUuid())) {
            return Outcome.fail(Msg.bad("Pour partir, utilisez /party leave."));
        }
        if (!party.contains(target.getUuid())) {
            return Outcome.fail(Msg.bad(target.getName().getString() + " n'est pas dans votre groupe."));
        }
        if (party.size() >= PartyConfig.get().kickVoteMinMembers) {
            return Outcome.fail(Msg.bad("A " + party.size()
                    + " membres, l'exclusion passe par un vote du groupe : /party kickvote <joueur>."));
        }

        removeMember(server, store, party, target.getUuid(),
                target.getName().getString() + " a ete exclu du groupe.");
        tell(target, Msg.bad("Vous avez ete exclu du groupe."));
        return Outcome.ok(Msg.good(target.getName().getString() + " a ete exclu."));
    }

    /**
     * Retire un membre sur decision du groupe.
     *
     * <p>Passe par le meme chemin que le depart volontaire et l'exclusion directe —
     * transfert de leadership et dissolution d'un groupe vide compris. Contrairement
     * a {@link #kick}, aucune verification de droit n'est refaite : le vote a deja
     * tranche, et c'est lui qui fait autorite (regle §15).
     */
    public static void removeByVote(MinecraftServer server, Party party, UUID member) {
        PartyStore store = PartyStore.of(server);
        String name = party.nameOf(member);
        removeMember(server, store, party, member, name + " a ete exclu par vote du groupe.");
        tell(server.getPlayerManager().getPlayer(member),
                Msg.bad("Le groupe a vote votre exclusion."));
    }

    public static Outcome disband(MinecraftServer server, ServerPlayerEntity leader) {
        PartyStore store = PartyStore.of(server);
        Optional<Party> maybeParty = store.partyOf(leader.getUuid());
        if (maybeParty.isEmpty()) {
            return Outcome.fail(Msg.bad("Vous n'etes dans aucun groupe."));
        }
        Party party = maybeParty.get();
        if (!party.isLeader(leader.getUuid())) {
            return Outcome.fail(Msg.bad("Seul le chef peut dissoudre le groupe."));
        }
        disbandInternal(server, store, party, Msg.info("Le groupe a ete dissous."));
        return Outcome.ok(Msg.info("Groupe dissous."));
    }

    private static void disbandInternal(MinecraftServer server, PartyStore store, Party party, Text announcement) {
        if (announcement != null) {
            broadcast(server, party, announcement);
        }
        PartyTeams.dissolve(server, party);
        invites.clearForParty(party.id());
        store.unregister(party);
        HauteCapitaleParty.LOGGER.info("Groupe {} dissous.", party.id());
    }

    // --- roles ---------------------------------------------------------------

    public static Outcome setRole(MinecraftServer server, ServerPlayerEntity player, Role role) {
        PartyStore store = PartyStore.of(server);
        Optional<Party> maybeParty = store.partyOf(player.getUuid());
        if (maybeParty.isEmpty()) {
            return Outcome.fail(Msg.bad("Vous n'etes dans aucun groupe."));
        }
        Party party = maybeParty.get();
        party.setRole(player.getUuid(), role);
        store.markDirty();
        broadcast(server, party, Msg.info(player.getName().getString() + " prend le role : " + role.label() + "."));
        return Outcome.ok(Msg.good("Role defini : " + role.label() + "."));
    }

    // --- cycle de vie des joueurs -------------------------------------------

    /**
     * Rattrape ce qui a pu changer pendant qu'un joueur etait absent.
     *
     * <p>Le scoreboard ne connait que des noms de joueurs, jamais des identifiants :
     * un membre exclu hors ligne n'a donc pas pu etre sorti de l'equipe a ce
     * moment-la. C'est ici que cela se regle.
     */
    public static void onPlayerJoin(MinecraftServer server, ServerPlayerEntity player) {
        PartyStore store = PartyStore.of(server);
        Optional<Party> party = store.partyOf(player.getUuid());
        party.ifPresent(p -> {
            p.setName(player.getUuid(), player.getName().getString());
            store.markDirty();
        });
        PartyTeams.syncOnJoin(server, party, player);
        party.ifPresent(p -> {
            tell(player, Msg.info("Vous etes dans un groupe de " + p.size()
                    + " (" + p.type().label() + "). /party info pour le detail."));
            broadcastExcept(server, p, player.getUuid(),
                    Msg.info(player.getName().getString() + " s'est reconnecte."));
        });
    }

    /**
     * Un joueur se deconnecte.
     *
     * <p>Sa place est conservee : le groupe survit aux deconnexions, et la reservation
     * temporaire prevue pour les donjons (regle §14) viendra se greffer ici en phase 5.
     * Seules ses invitations en attente sont oubliees.
     */
    public static void onPlayerLeave(MinecraftServer server, ServerPlayerEntity player) {
        invites.forget(player.getUuid());
        PartyStore.of(server).partyOf(player.getUuid()).ifPresent(party ->
                broadcastExcept(server, party, player.getUuid(),
                        Msg.info(player.getName().getString() + " s'est deconnecte.")));
    }

    // --- diffusion -----------------------------------------------------------

    /**
     * Envoie un message a un joueur, s'il est reellement connecte.
     *
     * <p>Un {@code ServerPlayerEntity} peut exister sans connexion — pendant une
     * deconnexion en cours, ou dans le harnais de diagnostic qui en fabrique en
     * memoire. {@code sendMessage} passerait alors par un gestionnaire de reseau nul.
     * Tout envoi direct a un joueur passe donc par ici.
     */
    public static void tell(ServerPlayerEntity player, Text message) {
        if (player != null && player.networkHandler != null) {
            player.sendMessage(message, false);
        }
    }

    public static void broadcast(MinecraftServer server, Party party, Text message) {
        broadcastExcept(server, party, null, message);
    }

    public static void broadcastExcept(MinecraftServer server, Party party, UUID excluded, Text message) {
        for (UUID member : party.memberIds()) {
            if (member.equals(excluded)) {
                continue;
            }
            tell(server.getPlayerManager().getPlayer(member), message);
        }
    }

    /** Les membres actuellement connectes. */
    public static List<ServerPlayerEntity> onlineMembers(MinecraftServer server, Party party) {
        List<ServerPlayerEntity> online = new ArrayList<>();
        for (UUID member : party.memberIds()) {
            ServerPlayerEntity player = server.getPlayerManager().getPlayer(member);
            if (player != null) {
                online.add(player);
            }
        }
        return online;
    }

    /** Plafond effectif : celui du type, eventuellement abaisse par la configuration. */
    public static int maxSizeOf(PartyType type) {
        if (type == PartyType.DUNGEON_5) {
            return Math.min(type.maxSize(), PartyConfig.get().dungeonPartyMaxSize);
        }
        return type.maxSize();
    }
}
