package net.hautecapitale.party.invite;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

/**
 * Les invitations en attente.
 *
 * <p>Volatiles par choix : une invitation vit une minute et s'adresse a un joueur
 * connecte. La faire survivre a un redemarrage produirait des invitations orphelines
 * vers des groupes qui n'existent plus.
 *
 * <p>Aucun tick : les invitations perimees sont ecartees au moment ou on les lit.
 * Un compteur qui tourne en permanence pour surveiller quelques entrees serait
 * exactement le genre de sondage que la regle §65 proscrit.
 */
public final class Invites {

    /**
     * Une invitation.
     *
     * @param partyId    le groupe vise
     * @param inviter    qui a invite, pour pouvoir le prevenir du resultat
     * @param partyEpoch la composition du groupe au moment de l'envoi : si elle a
     *                   change, l'invitation ne vaut plus (memes verrous que la
     *                   teleportation collective, regle §20)
     * @param expiresAt  horodatage d'expiration, en millisecondes
     */
    public record Invite(UUID partyId, UUID inviter, int partyEpoch, long expiresAt) {
        public boolean isExpired(long now) {
            return now >= this.expiresAt;
        }
    }

    private final Map<UUID, Invite> pending = new HashMap<>();
    private final Map<UUID, Long> inviterCooldowns = new HashMap<>();

    /** Enregistre une invitation. Une nouvelle remplace la precedente pour ce joueur. */
    public void put(UUID invitee, Invite invite) {
        this.pending.put(invitee, invite);
    }

    /** L'invitation valide d'un joueur, s'il en a une. Une invitation perimee est effacee. */
    public Optional<Invite> get(UUID invitee, long now) {
        Invite invite = this.pending.get(invitee);
        if (invite == null) {
            return Optional.empty();
        }
        if (invite.isExpired(now)) {
            this.pending.remove(invitee);
            return Optional.empty();
        }
        return Optional.of(invite);
    }

    /** Consomme l'invitation : elle ne peut plus servir deux fois. */
    public Optional<Invite> take(UUID invitee, long now) {
        Optional<Invite> invite = get(invitee, now);
        invite.ifPresent(ignored -> this.pending.remove(invitee));
        return invite;
    }

    public void clear(UUID invitee) {
        this.pending.remove(invitee);
    }

    /** Efface toutes les invitations vers un groupe donne : il vient d'etre dissous. */
    public void clearForParty(UUID partyId) {
        this.pending.entrySet().removeIf(entry -> entry.getValue().partyId().equals(partyId));
    }

    // --- anti-spam (regle §49) ----------------------------------------------

    /** Secondes restantes avant que ce joueur puisse inviter a nouveau. 0 s'il le peut. */
    public long cooldownRemaining(UUID inviter, long now) {
        Long until = this.inviterCooldowns.get(inviter);
        if (until == null || now >= until) {
            return 0L;
        }
        return (until - now + 999L) / 1000L;
    }

    public void noteInvite(UUID inviter, long now, int cooldownSeconds) {
        if (cooldownSeconds <= 0) {
            this.inviterCooldowns.remove(inviter);
            return;
        }
        this.inviterCooldowns.put(inviter, now + cooldownSeconds * 1000L);
    }

    /** Nombre d'invitations encore valides. Sert au diagnostic, pas au fonctionnement. */
    public int pendingCount(long now) {
        this.pending.entrySet().removeIf(entry -> entry.getValue().isExpired(now));
        return this.pending.size();
    }

    /** Oublie tout ce qui concerne un joueur qui se deconnecte. */
    public void forget(UUID player) {
        this.pending.remove(player);
        this.inviterCooldowns.remove(player);
    }
}
