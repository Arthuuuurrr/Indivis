package net.hautecapitale.party.request;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

/**
 * Une consultation du groupe, a duree limitee.
 *
 * <p>Sert aux deux demandes qui attendent une reponse de chacun : la verification
 * de preparation (regle §5) et le vote d'exclusion (regle §15). Le compte a rebours,
 * qui n'attend rien de personne, a sa propre classe.
 *
 * <p>Classe volontairement pure — aucune dependance a Minecraft — pour que les
 * regles de decision soient verifiables hors du jeu, la ou elles sont le plus
 * faciles a mettre en defaut.
 */
public final class Poll {

    public enum Kind {
        READY_CHECK,
        KICK_VOTE
    }

    /** Etat d'une consultation. Une consultation close ne change plus jamais d'avis. */
    public enum Outcome {
        PENDING,
        PASSED,
        FAILED
    }

    private final UUID id;
    private final UUID partyId;
    private final int partyEpoch;
    private final Kind kind;
    private final UUID target;
    private final long deadline;
    private final List<UUID> voters;
    private final Map<UUID, Boolean> answers = new LinkedHashMap<>();
    private final int threshold;

    private Poll(UUID id, UUID partyId, int partyEpoch, Kind kind, UUID target,
                 long deadline, List<UUID> voters, int threshold) {
        this.id = id;
        this.partyId = partyId;
        this.partyEpoch = partyEpoch;
        this.kind = kind;
        this.target = target;
        this.deadline = deadline;
        this.voters = List.copyOf(voters);
        this.threshold = threshold;
    }

    /**
     * Verification de preparation : tout le monde repond, personne ne decide.
     *
     * <p>Le resultat est une information rendue au groupe, pas un declencheur : il
     * ne lance ni combat ni donjon (regle §5).
     */
    public static Poll readyCheck(UUID partyId, int partyEpoch, List<UUID> members, long deadline) {
        return new Poll(UUID.randomUUID(), partyId, partyEpoch, Kind.READY_CHECK, null,
                deadline, members, members.size());
    }

    /**
     * Vote d'exclusion.
     *
     * <p>Votants : tous les membres sauf la personne visee — elle ne vote pas sur
     * son propre sort. Le chef, qui lance le vote, compte pour un OUI : le lui
     * redemander n'apporterait rien.
     *
     * <p>Seuil : <b>majorite stricte des votants</b>. Avec quatre votants il en faut
     * trois, ce qui rend l'egalite arithmetiquement impossible ; et comme une
     * abstention vaut un refus, le statu quo l'emporte par defaut.
     */
    public static Poll kickVote(UUID partyId, int partyEpoch, List<UUID> members,
                                UUID target, UUID leader, long deadline) {
        List<UUID> voters = new ArrayList<>();
        for (UUID member : members) {
            if (!member.equals(target)) {
                voters.add(member);
            }
        }
        Poll poll = new Poll(UUID.randomUUID(), partyId, partyEpoch, Kind.KICK_VOTE, target,
                deadline, voters, majorityOf(voters.size()));
        if (voters.contains(leader)) {
            poll.answers.put(leader, Boolean.TRUE);
        }
        return poll;
    }

    /** Majorite stricte : plus de la moitie, jamais la moitie tout juste. */
    public static int majorityOf(int voterCount) {
        return voterCount / 2 + 1;
    }

    // --- identite ------------------------------------------------------------

    public UUID id() {
        return this.id;
    }

    public UUID partyId() {
        return this.partyId;
    }

    /**
     * Composition du groupe au lancement.
     *
     * <p>Si elle a change depuis, la consultation ne vaut plus : les votants ne sont
     * plus les memes, et un resultat calcule sur l'ancienne liste serait faux.
     */
    public int partyEpoch() {
        return this.partyEpoch;
    }

    public Kind kind() {
        return this.kind;
    }

    public Optional<UUID> target() {
        return Optional.ofNullable(this.target);
    }

    public long deadline() {
        return this.deadline;
    }

    public int threshold() {
        return this.threshold;
    }

    public List<UUID> voters() {
        return this.voters;
    }

    public boolean isVoter(UUID player) {
        return this.voters.contains(player);
    }

    // --- reponses ------------------------------------------------------------

    /**
     * Enregistre une reponse.
     *
     * @return faux si la personne n'a pas le droit de voter ou a deja repondu —
     *         on ne change pas d'avis, sinon un vote pourrait etre retourne a la
     *         derniere seconde
     */
    public boolean answer(UUID player, boolean yes) {
        if (!isVoter(player) || this.answers.containsKey(player)) {
            return false;
        }
        this.answers.put(player, yes);
        return true;
    }

    public boolean hasAnswered(UUID player) {
        return this.answers.containsKey(player);
    }

    public Optional<Boolean> answerOf(UUID player) {
        return Optional.ofNullable(this.answers.get(player));
    }

    /** Ceux dont on attend encore la reponse, dans l'ordre du groupe. */
    public List<UUID> pending() {
        List<UUID> waiting = new ArrayList<>();
        for (UUID voter : this.voters) {
            if (!this.answers.containsKey(voter)) {
                waiting.add(voter);
            }
        }
        return waiting;
    }

    public int yesCount() {
        return (int) this.answers.values().stream().filter(Boolean::booleanValue).count();
    }

    public int noCount() {
        return (int) this.answers.values().stream().filter(value -> !value).count();
    }

    public boolean allAnswered() {
        return this.answers.size() >= this.voters.size();
    }

    public boolean isExpired(long now) {
        return now >= this.deadline;
    }

    public long secondsLeft(long now) {
        return Math.max(0L, (this.deadline - now + 999L) / 1000L);
    }

    // --- decision ------------------------------------------------------------

    /**
     * Ou en est la consultation.
     *
     * <p>Deux raccourcis, pour ne pas faire attendre le groupe inutilement : le
     * seuil atteint conclut aussitot, et un seuil devenu inatteignable aussi. A
     * l'expiration, les silencieux sont comptes comme des refus.
     */
    public Outcome outcome(long now) {
        if (this.kind == Kind.READY_CHECK) {
            if (allAnswered()) {
                return yesCount() == this.voters.size() ? Outcome.PASSED : Outcome.FAILED;
            }
            return isExpired(now) ? Outcome.FAILED : Outcome.PENDING;
        }

        if (yesCount() >= this.threshold) {
            return Outcome.PASSED;
        }
        int stillPossible = yesCount() + pending().size();
        if (stillPossible < this.threshold) {
            return Outcome.FAILED;
        }
        return isExpired(now) ? Outcome.FAILED : Outcome.PENDING;
    }

    /**
     * Vrai si la consultation vise cette personne.
     *
     * <p>Sert au seul cas de depart qui demande un traitement propre : la cible d'un
     * vote d'exclusion qui se deconnecte. Une deconnexion ne change pas la
     * composition du groupe — la place reste reservee — donc l'{@code epoch} ne
     * bouge pas et rien n'annulerait le vote autrement. Exclure quelqu'un pendant
     * une coupure reseau serait injuste (regle §15).
     *
     * <p>Tous les autres departs, eux, changent la composition : la consultation
     * devient caduque par son {@code epoch}, et le gestionnaire l'abandonne sans
     * qu'il y ait rien de particulier a faire ici.
     */
    public boolean targets(UUID player) {
        return player.equals(this.target);
    }
}
