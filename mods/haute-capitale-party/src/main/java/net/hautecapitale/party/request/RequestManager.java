package net.hautecapitale.party.request;

import net.hautecapitale.party.config.PartyConfig;
import net.hautecapitale.party.dialog.PartyDialogs;
import net.hautecapitale.party.party.Outcome;
import net.hautecapitale.party.party.Party;
import net.hautecapitale.party.party.PartyManager;
import net.hautecapitale.party.text.Msg;
import net.minecraft.network.packet.s2c.play.SubtitleS2CPacket;
import net.minecraft.network.packet.s2c.play.TitleFadeS2CPacket;
import net.minecraft.network.packet.s2c.play.TitleS2CPacket;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.sound.SoundEvents;
import net.minecraft.text.Text;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

/**
 * Les consultations du groupe : preparation, compte a rebours, vote d'exclusion.
 *
 * <p>Tout est volatil. Une consultation dure quelques dizaines de secondes et
 * s'adresse a des joueurs connectes ; la faire survivre a un redemarrage
 * produirait des questions auxquelles plus personne ne peut repondre.
 *
 * <p><b>Sur le tick.</b> Les expirations et le compte a rebours ont besoin d'un
 * battement, mais on ne parcourt jamais les groupes : seulement les consultations
 * <em>en cours</em>, presque toujours aucune. Le tick sort immediatement quand il
 * n'y a rien a faire (regle §65).
 */
public final class RequestManager {

    private static final Map<UUID, Poll> READY_CHECKS = new HashMap<>();
    private static final Map<UUID, Poll> KICK_VOTES = new HashMap<>();
    private static final Map<UUID, Countdown> COUNTDOWNS = new HashMap<>();
    private static final Map<String, Long> COOLDOWNS = new HashMap<>();

    private RequestManager() {
    }

    /** Repart de zero a chaque demarrage : rien de tout ceci n'est persiste. */
    public static void resetTransient() {
        READY_CHECKS.clear();
        KICK_VOTES.clear();
        COUNTDOWNS.clear();
        COOLDOWNS.clear();
    }

    private static long now() {
        return System.currentTimeMillis();
    }

    // --- verification de preparation (regle §5) ------------------------------

    public static Outcome startReadyCheck(MinecraftServer server, ServerPlayerEntity leader) {
        Optional<Party> maybeParty = leaderPartyOf(server, leader);
        if (maybeParty.isEmpty()) {
            return notLeader(server, leader);
        }
        Party party = maybeParty.get();

        if (READY_CHECKS.containsKey(party.id())) {
            return Outcome.fail(Msg.bad("Une verification est deja en cours."));
        }
        long wait = cooldownLeft("ready:" + party.id());
        if (wait > 0) {
            return Outcome.fail(Msg.bad("Patientez " + wait + " s avant une nouvelle verification."));
        }

        PartyConfig config = PartyConfig.get();
        long deadline = now() + config.readyCheckSeconds * 1000L;
        Poll poll = Poll.readyCheck(party.id(), party.epoch(), party.memberIds(), deadline);
        READY_CHECKS.put(party.id(), poll);
        setCooldown("ready:" + party.id(), config.readyCheckCooldownSeconds);

        String leaderName = leader.getName().getString();
        for (UUID member : poll.voters()) {
            ServerPlayerEntity player = server.getPlayerManager().getPlayer(member);
            if (player == null) {
                continue;
            }
            PartyDialogs.askYesNo(player,
                    Msg.readyTitle(),
                    Msg.readyBody(leaderName),
                    Text.literal("PRET"), "/party ready " + poll.id() + " oui",
                    Text.literal("PAS PRET"), "/party ready " + poll.id() + " non");
        }
        PartyManager.broadcast(server, party,
                Msg.info(leaderName + " verifie que le groupe est pret (" + config.readyCheckSeconds + " s)."));
        return Outcome.ok(Msg.good("Verification lancee."));
    }

    public static Outcome answerReady(MinecraftServer server, ServerPlayerEntity player,
                                      UUID requestId, boolean ready) {
        return answer(server, player, requestId, ready, READY_CHECKS);
    }

    // --- vote d'exclusion (regle §15) ----------------------------------------

    public static Outcome startKickVote(MinecraftServer server, ServerPlayerEntity leader,
                                        ServerPlayerEntity target) {
        Optional<Party> maybeParty = leaderPartyOf(server, leader);
        if (maybeParty.isEmpty()) {
            return notLeader(server, leader);
        }
        Party party = maybeParty.get();
        UUID targetId = target.getUuid();

        if (targetId.equals(leader.getUuid())) {
            return Outcome.fail(Msg.bad("Pour partir, utilisez /party leave."));
        }
        if (!party.contains(targetId)) {
            return Outcome.fail(Msg.bad(target.getName().getString() + " n'est pas dans votre groupe."));
        }
        if (KICK_VOTES.containsKey(party.id())) {
            return Outcome.fail(Msg.bad("Un vote est deja en cours."));
        }
        if (party.size() < PartyConfig.get().kickVoteMinMembers) {
            return Outcome.fail(Msg.bad("A " + party.size()
                    + " membres, l'exclusion se fait directement : /party kick <joueur>."));
        }

        long globalWait = cooldownLeft("kick:" + party.id());
        if (globalWait > 0) {
            return Outcome.fail(Msg.bad("Patientez " + globalWait + " s avant un nouveau vote."));
        }
        long targetWait = cooldownLeft("target:" + party.id() + ":" + targetId);
        if (targetWait > 0) {
            return Outcome.fail(Msg.bad("Un vote contre " + target.getName().getString()
                    + " a deja eu lieu recemment. Patientez " + targetWait + " s."));
        }

        PartyConfig config = PartyConfig.get();
        long deadline = now() + config.kickVoteSeconds * 1000L;
        Poll poll = Poll.kickVote(party.id(), party.epoch(), party.memberIds(),
                targetId, leader.getUuid(), deadline);
        KICK_VOTES.put(party.id(), poll);
        setCooldown("kick:" + party.id(), config.kickVoteCooldownSeconds);

        String leaderName = leader.getName().getString();
        String targetName = target.getName().getString();
        for (UUID voter : poll.voters()) {
            if (poll.hasAnswered(voter)) {
                continue; // le chef, dont la voix est deja comptee
            }
            ServerPlayerEntity player = server.getPlayerManager().getPlayer(voter);
            if (player == null) {
                continue;
            }
            PartyDialogs.askYesNo(player,
                    Msg.kickVoteTitle(),
                    Msg.kickVoteBody(leaderName, targetName, poll.threshold(), poll.voters().size()),
                    Text.literal("OUI, EXCLURE"), "/party vote " + poll.id() + " oui",
                    Text.literal("NON"), "/party vote " + poll.id() + " non");
        }

        PartyManager.broadcast(server, party,
                Msg.info(leaderName + " propose d'exclure " + targetName + ". "
                        + poll.threshold() + " voix sur " + poll.voters().size() + " sont necessaires."));
        // La personne visee est prevenue : un vote secret sur son sort serait pire.
        PartyManager.tell(target, Msg.bad("Un vote d'exclusion vous concernant a ete lance."));

        // Le chef a peut-etre suffi a lui seul (groupe de 2 votants).
        settleIfDecided(server, party, poll, KICK_VOTES);
        return Outcome.ok(Msg.good("Vote lance."));
    }

    public static Outcome answerVote(MinecraftServer server, ServerPlayerEntity player,
                                     UUID requestId, boolean yes) {
        return answer(server, player, requestId, yes, KICK_VOTES);
    }

    // --- compte a rebours (regle §6) -----------------------------------------

    public static Outcome startCountdown(MinecraftServer server, ServerPlayerEntity leader, int seconds) {
        Optional<Party> maybeParty = leaderPartyOf(server, leader);
        if (maybeParty.isEmpty()) {
            return notLeader(server, leader);
        }
        Party party = maybeParty.get();
        PartyConfig config = PartyConfig.get();

        if (seconds < 1 || seconds > config.countdownMaxSeconds) {
            return Outcome.fail(Msg.bad("Duree hors bornes (1 a " + config.countdownMaxSeconds + " s)."));
        }
        if (COUNTDOWNS.containsKey(party.id())) {
            return Outcome.fail(Msg.bad("Un compte a rebours est deja en cours. /party countdown cancel"));
        }
        long wait = cooldownLeft("countdown:" + party.id());
        if (wait > 0) {
            return Outcome.fail(Msg.bad("Patientez " + wait + " s avant un nouveau compte a rebours."));
        }

        COUNTDOWNS.put(party.id(), new Countdown(party.id(), seconds));
        setCooldown("countdown:" + party.id(), config.countdownCooldownSeconds);

        // Titres courts : sans cela le chiffre precedent chevaucherait le suivant.
        for (ServerPlayerEntity member : PartyManager.onlineMembers(server, party)) {
            if (member.networkHandler != null) {
                member.networkHandler.sendPacket(new TitleFadeS2CPacket(0, 15, 5));
            }
        }
        PartyManager.broadcast(server, party,
                Msg.info(leader.getName().getString() + " lance un compte a rebours de " + seconds + " s."));
        return Outcome.ok(Msg.good("Compte a rebours lance."));
    }

    public static Outcome cancelCountdown(MinecraftServer server, ServerPlayerEntity leader) {
        Optional<Party> maybeParty = leaderPartyOf(server, leader);
        if (maybeParty.isEmpty()) {
            return notLeader(server, leader);
        }
        Party party = maybeParty.get();
        if (COUNTDOWNS.remove(party.id()) == null) {
            return Outcome.fail(Msg.bad("Aucun compte a rebours en cours."));
        }
        for (ServerPlayerEntity member : PartyManager.onlineMembers(server, party)) {
            if (member.networkHandler != null) {
                member.networkHandler.sendPacket(new TitleS2CPacket(Text.empty()));
            }
        }
        PartyManager.broadcast(server, party, Msg.info("Compte a rebours annule."));
        return Outcome.ok(Msg.info("Compte a rebours annule."));
    }

    // --- battement -----------------------------------------------------------

    /** Fait avancer ce qui est en cours. Sort aussitot s'il n'y a rien. */
    public static void tick(MinecraftServer server) {
        if (!COUNTDOWNS.isEmpty()) {
            tickCountdowns(server);
        }
        if (!READY_CHECKS.isEmpty()) {
            tickPolls(server, READY_CHECKS);
        }
        if (!KICK_VOTES.isEmpty()) {
            tickPolls(server, KICK_VOTES);
        }
    }

    private static void tickCountdowns(MinecraftServer server) {
        for (UUID partyId : new ArrayList<>(COUNTDOWNS.keySet())) {
            Countdown countdown = COUNTDOWNS.get(partyId);
            Optional<Party> party = PartyManager.byId(server, partyId);
            if (party.isEmpty()) {
                COUNTDOWNS.remove(partyId);
                continue;
            }
            int announce = countdown.tick();
            if (announce >= 0) {
                announceCountdown(server, party.get(), announce);
            }
            if (countdown.isFinished()) {
                COUNTDOWNS.remove(partyId);
            }
        }
    }

    private static void announceCountdown(MinecraftServer server, Party party, int seconds) {
        Text title = seconds == 0 ? Msg.countdownGo() : Msg.countdownNumber(seconds);
        for (ServerPlayerEntity member : PartyManager.onlineMembers(server, party)) {
            if (member.networkHandler == null) {
                continue;
            }
            member.networkHandler.sendPacket(new SubtitleS2CPacket(Text.empty()));
            member.networkHandler.sendPacket(new TitleS2CPacket(title));
            member.playSound(SoundEvents.BLOCK_NOTE_BLOCK_PLING.value(), 0.7f, seconds == 0 ? 1.5f : 1.0f);
        }
    }

    private static void tickPolls(MinecraftServer server, Map<UUID, Poll> polls) {
        for (UUID partyId : new ArrayList<>(polls.keySet())) {
            Poll poll = polls.get(partyId);
            Optional<Party> party = PartyManager.byId(server, partyId);
            if (party.isEmpty()) {
                polls.remove(partyId);
                continue;
            }
            // Un seul appel suffit : l'echeance depassee fait deja sortir la
            // consultation de l'etat « en attente », donc elle se conclut ici.
            settleIfDecided(server, party.get(), poll, polls);
        }
    }

    // --- rouages communs -----------------------------------------------------

    private static Outcome answer(MinecraftServer server, ServerPlayerEntity player,
                                  UUID requestId, boolean yes, Map<UUID, Poll> polls) {
        Optional<Party> maybeParty = PartyManager.partyOf(server, player.getUuid());
        if (maybeParty.isEmpty()) {
            return Outcome.fail(Msg.bad("Vous n'etes dans aucun groupe."));
        }
        Party party = maybeParty.get();
        Poll poll = polls.get(party.id());

        // Le coeur de la protection contre les reponses perimees (regle §20) : une
        // reponse designe la demande a laquelle elle repond, et une demande close ou
        // remplacee n'accepte plus rien.
        if (poll == null || !poll.id().equals(requestId)) {
            return Outcome.fail(Msg.bad("Cette demande n'est plus d'actualite."));
        }
        if (!poll.answer(player.getUuid(), yes)) {
            return Outcome.fail(Msg.bad("Vous avez deja repondu."));
        }

        settleIfDecided(server, party, poll, polls);
        return Outcome.ok(Msg.info("Reponse enregistree : " + (yes ? "oui" : "non") + "."));
    }

    /**
     * Conclut si la decision est acquise.
     *
     * @return vrai si la consultation vient d'etre close
     */
    private static boolean settleIfDecided(MinecraftServer server, Party party,
                                           Poll poll, Map<UUID, Poll> polls) {
        // Une consultation lancee sur une autre composition ne vaut plus rien.
        if (poll.partyEpoch() != party.epoch()) {
            polls.remove(party.id());
            PartyDialogs.clearAll(server, poll.pending());
            PartyManager.broadcast(server, party,
                    Msg.info("La composition du groupe a change : la demande est annulee."));
            return true;
        }
        if (poll.outcome(now()) == Poll.Outcome.PENDING) {
            return false;
        }
        conclude(server, party, poll, polls);
        return true;
    }

    private static void conclude(MinecraftServer server, Party party, Poll poll, Map<UUID, Poll> polls) {
        polls.remove(party.id());
        // Fermer la fenetre de ceux qui n'ont pas repondu : la question n'a plus d'objet.
        PartyDialogs.clearAll(server, poll.pending());

        if (poll.kind() == Poll.Kind.READY_CHECK) {
            concludeReadyCheck(server, party, poll);
        } else {
            concludeKickVote(server, party, poll);
        }
    }

    private static void concludeReadyCheck(MinecraftServer server, Party party, Poll poll) {
        List<Text> lines = new ArrayList<>();
        int ready = 0;
        for (UUID voter : poll.voters()) {
            boolean isReady = poll.answerOf(voter).orElse(Boolean.FALSE);
            if (isReady) {
                ready++;
            }
            lines.add(Msg.readyLine(party.nameOf(voter), isReady));
        }
        PartyManager.broadcast(server, party, Msg.heading("Verification de preparation"));
        for (Text line : lines) {
            PartyManager.broadcast(server, party, line);
        }
        PartyManager.broadcast(server, party, Msg.readyTally(ready, poll.voters().size()));
    }

    private static void concludeKickVote(MinecraftServer server, Party party, Poll poll) {
        boolean passed = poll.outcome(now()) == Poll.Outcome.PASSED;
        PartyManager.broadcast(server, party,
                Msg.kickVoteTally(poll.yesCount(), poll.voters().size() - poll.yesCount(), poll.threshold()));

        UUID target = poll.target().orElse(null);
        if (target == null) {
            return;
        }
        PartyConfig config = PartyConfig.get();

        if (passed) {
            setCooldown("target:" + party.id() + ":" + target, config.kickVoteTargetCooldownSeconds);
            PartyManager.removeByVote(server, party, target);
        } else {
            // Un vote perdu protege sa cible plus longtemps qu'un vote gagne : sans
            // cela, un chef relancerait jusqu'a ce que l'inattention des autres
            // finisse par le faire passer (regle §49).
            setCooldown("target:" + party.id() + ":" + target, config.kickVoteFailProtectionSeconds);
            PartyManager.broadcast(server, party,
                    Msg.info(party.nameOf(target) + " reste dans le groupe."));
        }
    }

    // --- reactions au cycle de vie -------------------------------------------

    /**
     * Un joueur se deconnecte.
     *
     * <p>Seul cas qui demande une action : il est la cible d'un vote. Une
     * deconnexion ne change pas la composition du groupe — sa place reste
     * reservee — donc rien ne l'annulerait autrement, et il serait exclu pendant
     * une coupure reseau sans avoir pu se defendre (regle §15).
     */
    public static void onPlayerLeave(MinecraftServer server, UUID player) {
        PartyManager.partyOf(server, player).ifPresent(party -> {
            Poll vote = KICK_VOTES.get(party.id());
            if (vote != null && vote.targets(player)) {
                KICK_VOTES.remove(party.id());
                PartyDialogs.clearAll(server, vote.pending());
                PartyManager.broadcast(server, party,
                        Msg.info(party.nameOf(player) + " s'est deconnecte : le vote est annule."));
            }
        });
    }

    // --- petits outils -------------------------------------------------------

    private static Optional<Party> leaderPartyOf(MinecraftServer server, ServerPlayerEntity player) {
        return PartyManager.partyOf(server, player.getUuid())
                .filter(party -> party.isLeader(player.getUuid()));
    }

    private static Outcome notLeader(MinecraftServer server, ServerPlayerEntity player) {
        if (PartyManager.partyOf(server, player.getUuid()).isEmpty()) {
            return Outcome.fail(Msg.bad("Vous n'etes dans aucun groupe."));
        }
        return Outcome.fail(Msg.bad("Seul le chef du groupe peut lancer cette demande."));
    }

    private static long cooldownLeft(String key) {
        Long until = COOLDOWNS.get(key);
        if (until == null || now() >= until) {
            return 0L;
        }
        return (until - now() + 999L) / 1000L;
    }

    private static void setCooldown(String key, int seconds) {
        if (seconds <= 0) {
            COOLDOWNS.remove(key);
            return;
        }
        COOLDOWNS.put(key, now() + seconds * 1000L);
    }

    // --- lecture, pour le diagnostic ----------------------------------------

    public static Optional<Poll> readyCheckOf(UUID partyId) {
        return Optional.ofNullable(READY_CHECKS.get(partyId));
    }

    public static Optional<Poll> kickVoteOf(UUID partyId) {
        return Optional.ofNullable(KICK_VOTES.get(partyId));
    }

    public static Optional<Countdown> countdownOf(UUID partyId) {
        return Optional.ofNullable(COUNTDOWNS.get(partyId));
    }

    /** Efface les delais d'attente d'un groupe. Reserve au diagnostic. */
    public static void clearCooldowns(UUID partyId) {
        COOLDOWNS.keySet().removeIf(key -> key.contains(partyId.toString()));
    }
}
