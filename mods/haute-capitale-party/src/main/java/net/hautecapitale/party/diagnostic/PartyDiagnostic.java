package net.hautecapitale.party.diagnostic;

import com.mojang.authlib.GameProfile;
import io.netty.buffer.Unpooled;
import net.fabricmc.fabric.api.entity.event.v1.ServerLivingEntityEvents;
import net.hautecapitale.party.combat.FriendlyFire;
import net.hautecapitale.party.config.PartyConfig;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.hautecapitale.party.invite.Invites;
import net.hautecapitale.party.network.HudPayload;
import net.hautecapitale.party.network.HudSync;
import net.hautecapitale.party.party.Outcome;
import net.hautecapitale.party.party.Party;
import net.hautecapitale.party.party.PartyManager;
import net.hautecapitale.party.party.PartyStore;
import net.hautecapitale.party.party.PartyType;
import net.hautecapitale.party.party.Role;
import net.hautecapitale.party.request.Poll;
import net.hautecapitale.party.request.RequestManager;
import net.hautecapitale.party.team.PartyTeams;
import net.minecraft.dialog.AfterAction;
import net.minecraft.dialog.DialogActionButtonData;
import net.minecraft.dialog.DialogButtonData;
import net.minecraft.dialog.DialogCommonData;
import net.minecraft.dialog.action.SimpleDialogAction;
import net.minecraft.dialog.body.DialogBody;
import net.minecraft.dialog.body.PlainMessageDialogBody;
import net.minecraft.dialog.type.ConfirmationDialog;
import net.minecraft.dialog.type.Dialog;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.projectile.ArrowEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.packet.s2c.common.ShowDialogS2CPacket;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.SpawnReason;
import net.minecraft.entity.passive.WolfEntity;
import net.minecraft.network.ClientConnection;
import net.minecraft.network.NetworkSide;
import net.minecraft.network.packet.c2s.common.SyncedClientOptions;
import net.minecraft.server.network.ConnectedClientData;
import net.minecraft.scoreboard.Team;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.text.ClickEvent;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Diagnostic en jeu : {@code /party diagnostic}.
 *
 * <p>Les commandes de groupe exigent un vrai joueur, ce que la console d'un serveur
 * ne fournit pas. Ce harnais contourne l'obstacle en construisant des
 * {@code ServerPlayerEntity} en memoire — jamais ajoutes au monde, jamais connectes —
 * et en les faisant passer par les <em>vrais</em> chemins de code : le gestionnaire,
 * le magasin, les equipes de scoreboard. Il est donc pilotable par RCON.
 *
 * <p>Il nettoie systematiquement derriere lui : les groupes et les equipes qu'il cree
 * portent des noms reconnaissables et sont supprimes avant de rendre la main, meme
 * si une verification echoue.
 */
public final class PartyDiagnostic {

    private static final String PREFIX = "~diag_";

    private PartyDiagnostic() {
    }

    public static int run(ServerCommandSource source) {
        MinecraftServer server = source.getServer();
        Report report = new Report(source);
        List<UUID> created = new ArrayList<>();

        // Le delai anti-spam est neutralise pour tous les scenarios, puis remis en
        // service par le scenario qui l'examine. Sans cela, la premiere invitation
        // d'un scenario poserait un delai qui ferait echouer le scenario suivant —
        // ce qui mesurerait l'ordre des tests, pas le comportement du mod.
        PartyConfig.get().inviteCooldownSeconds = 0;

        // Chaque scenario est isole : un qui leve ne doit pas emporter les suivants,
        // sinon un seul incident masque tout le reste du rapport — ce qui est
        // exactement arrive lors de la mise au point de ce harnais.
        try {
            scenario(report, "creation", () -> creation(server, report, created));
            scenario(report, "invitations", () -> invitations(server, report, created));
            scenario(report, "anti-spam", () -> antiSpam(server, report, created));
            scenario(report, "verrou d'epoch", () -> epochGuard(server, report, created));
            scenario(report, "capacite", () -> capacity(server, report, created));
            scenario(report, "leadership", () -> leadership(server, report, created));
            scenario(report, "exclusion", () -> exclusion(server, report, created));
            scenario(report, "roles", () -> roles(server, report, created));
            scenario(report, "equipe de scoreboard", () -> scoreboardTeam(server, report, created));
            scenario(report, "relation de groupe", () -> friendlyFireRelation(server, report, created));
            scenario(report, "garde de degats", () -> damageGuard(server, report, created));
            scenario(report, "paquet de dialogue", () -> dialogPacket(server, report, created));
            scenario(report, "verification de preparation", () -> readyCheckFlow(server, report, created));
            scenario(report, "vote d exclusion", () -> kickVoteFlow(server, report, created));
            scenario(report, "vote d exclusion perdu", () -> kickVoteRejected(server, report, created));
            scenario(report, "cible deconnectee", () -> kickVoteTargetLeaves(server, report, created));
            scenario(report, "compte a rebours", () -> countdownFlow(server, report, created));
            scenario(report, "verrou d epoch des consultations", () -> pollEpochGuard(server, report, created));
            scenario(report, "paquet de HUD", () -> hudPacket(server, report, created));
            scenario(report, "instantane de HUD", () -> hudSnapshot(server, report, created));
            scenario(report, "ponts de dependance douce", () -> bridges(server, report, created));
            scenario(report, "dissolution", () -> dissolution(server, report, created));
            scenario(report, "coherence", () -> coherence(server, report, created));
        } finally {
            cleanup(server, created);
        }

        report.summary();
        return report.failed == 0 ? 1 : 0;
    }

    private static final String WITNESS = PREFIX + "temoin_";

    /**
     * Pose un groupe temoin destine a survivre a un redemarrage.
     *
     * <p>La persistance (regle §39) ne peut pas se verifier dans une seule execution :
     * il faut ecrire, arreter, relire. Cette commande fait la premiere moitie,
     * {@link #verifyWitness} la seconde. Le temoin porte deux membres, un chef
     * designe et un role, pour que la verification porte sur autre chose que
     * l'existence du groupe.
     */
    public static int plantWitness(ServerCommandSource source) {
        MinecraftServer server = source.getServer();
        PartyStore store = PartyStore.of(server);

        // Un temoin plus ancien trainerait et fausserait la lecture.
        clearWitnesses(server, store);

        ServerPlayerEntity a = ghost(server, "temoin_A");
        ServerPlayerEntity b = ghost(server, "temoin_B");

        PartyConfig.get().inviteCooldownSeconds = 0;
        try {
            PartyManager.create(server, a, PartyType.RAID);
            PartyManager.invite(server, a, b);
            PartyManager.accept(server, b);
            PartyManager.setRole(server, b, Role.HEAL);
            PartyManager.transfer(server, a, b);
        } finally {
            // Ne pas laisser la configuration du serveur alteree par un diagnostic.
            PartyConfig.load();
        }

        Party party = PartyManager.partyOf(server, a.getUuid()).orElseThrow();
        source.sendFeedback(() -> Text.literal("Temoin pose : groupe " + party.id()
                + ", " + party.size() + " membres, chef " + party.nameOf(party.leader())
                + ", epoch " + party.epoch()).formatted(Formatting.AQUA), false);
        return 1;
    }

    /** Verifie que le temoin a survecu au redemarrage, puis l'efface. */
    /**
     * Fume-test du menu de groupe (touche P) : construit chaque section pour un groupe fabrique
     * (chef + 2 membres, et un joueur seul) et verifie qu'aucune ne leve d'exception. Le rendu
     * client des dialogues ne peut se voir que sur un vrai client ; ici on eprouve la construction.
     */
    public static int menuSmoke(ServerCommandSource source) {
        MinecraftServer server = source.getServer();
        List<UUID> created = new ArrayList<>();
        Report report = new Report(source);
        int savedCooldown = net.hautecapitale.party.config.PartyConfig.get().inviteCooldownSeconds;
        net.hautecapitale.party.config.PartyConfig.get().inviteCooldownSeconds = 0;
        try {
            ServerPlayerEntity leader = ghost(server, "menu_lead");
            ServerPlayerEntity m1 = ghost(server, "menu_m1");
            ServerPlayerEntity m2 = ghost(server, "menu_m2");
            Party party = buildParty(server, created, leader, m1, m2);
            report.check("groupe de test monte (3 membres)", party.size() == 3);
            report.check("menu principal (chef) construit",
                    net.hautecapitale.party.dialog.PartyMenu.buildMain(server, leader) != null);
            report.check("menu principal (membre) construit",
                    net.hautecapitale.party.dialog.PartyMenu.buildMain(server, m1) != null);
            report.check("sous-menu inviter construit",
                    net.hautecapitale.party.dialog.PartyMenu.buildInvite(server, leader) != null);
            report.check("sous-menu exclure construit",
                    net.hautecapitale.party.dialog.PartyMenu.buildKick(server, leader) != null);
            report.check("sous-menu nommer un chef construit",
                    net.hautecapitale.party.dialog.PartyMenu.buildTransfer(server, leader) != null);
            report.check("sous-menu role construit",
                    net.hautecapitale.party.dialog.PartyMenu.buildRole() != null);
            ServerPlayerEntity solo = ghost(server, "menu_solo");
            report.check("menu principal (sans groupe) construit",
                    net.hautecapitale.party.dialog.PartyMenu.buildMain(server, solo) != null);
        } catch (Throwable t) {
            report.check("aucune exception lors de la construction du menu (" + t + ")", false);
        } finally {
            net.hautecapitale.party.config.PartyConfig.get().inviteCooldownSeconds = savedCooldown;
            cleanup(server, created);
        }
        report.summary();
        return report.failed == 0 ? 1 : 0;
    }

    /**
     * Test du ciblage des invocations (mixin {@code MobEntityTargetMixin}) : une creature possedee
     * par un membre ne doit PAS pouvoir prendre pour cible un allie du meme groupe, mais doit cibler
     * normalement un joueur hors groupe ; une creature sans maitre cible normalement.
     */
    public static int summonTargeting(ServerCommandSource source) {
        MinecraftServer server = source.getServer();
        ServerWorld world = server.getOverworld();
        Report report = new Report(source);
        List<UUID> created = new ArrayList<>();
        List<ServerPlayerEntity> connected = new ArrayList<>();
        int savedCooldown = PartyConfig.get().inviteCooldownSeconds;
        PartyConfig.get().inviteCooldownSeconds = 0;
        WolfEntity ownedWolf = null;
        WolfEntity freeWolf = null;
        try {
            ServerPlayerEntity summoner = connect(server, "sum_lead");
            ServerPlayerEntity ally = connect(server, "sum_ally");
            ServerPlayerEntity outsider = connect(server, "sum_out");
            connected.add(summoner);
            connected.add(ally);
            connected.add(outsider);
            PartyManager.create(server, summoner, PartyType.DUNGEON_5);
            track(server, summoner, created);
            joinParty(server, summoner, ally); // outsider reste hors groupe

            ownedWolf = EntityType.WOLF.create(world, SpawnReason.EVENT);
            ownedWolf.refreshPositionAndAngles(0.5, world.getBottomY() + 80, 0.5, 0f, 0f);
            world.spawnEntity(ownedWolf); // dans le monde AVANT de fixer le maitre (resolution par UUID)
            ownedWolf.setTamed(true, false);
            ownedWolf.setOwner(summoner);
            UUID root0 = net.hautecapitale.party.combat.FriendlyFire.rootPlayer(ownedWolf);
            report.check("l'invocation appartient bien a l'invocateur",
                    root0 != null && root0.equals(summoner.getUuid()));

            ownedWolf.setTarget(ally);
            report.check("l'invocation NE cible PAS un allie du groupe", ownedWolf.getTarget() == null);

            ownedWolf.setTarget(outsider);
            report.check("l'invocation cible bien un joueur hors groupe", ownedWolf.getTarget() == outsider);

            freeWolf = EntityType.WOLF.create(world, SpawnReason.EVENT);
            freeWolf.refreshPositionAndAngles(0.5, world.getBottomY() + 80, 2.5, 0f, 0f);
            world.spawnEntity(freeWolf);
            freeWolf.setTarget(ally);
            report.check("un mob SANS maitre cible normalement un joueur", freeWolf.getTarget() == ally);
        } catch (Throwable t) {
            report.check("aucune exception (" + t + ")", false);
        } finally {
            if (ownedWolf != null) {
                ownedWolf.discard();
            }
            if (freeWolf != null) {
                freeWolf.discard();
            }
            PartyConfig.get().inviteCooldownSeconds = savedCooldown;
            cleanup(server, created);
            for (ServerPlayerEntity p : connected) {
                try {
                    server.getPlayerManager().remove(p);
                } catch (Throwable ignored) {
                }
            }
        }
        report.summary();
        return report.failed == 0 ? 1 : 0;
    }

    public static int verifyWitness(ServerCommandSource source) {
        MinecraftServer server = source.getServer();
        PartyStore store = PartyStore.of(server);
        Report report = new Report(source);

        Optional<Party> witness = store.all().stream()
                .filter(party -> party.entries().stream()
                        .anyMatch(entry -> entry.lastKnownName().startsWith(WITNESS)))
                .findFirst();

        report.check("le groupe temoin a survecu au redemarrage", witness.isPresent());
        witness.ifPresent(party -> {
            report.check("les deux membres sont la", party.size() == 2);
            report.check("le type est conserve", party.type() == PartyType.RAID);
            report.check("le chef transfere est conserve",
                    party.nameOf(party.leader()).equals(WITNESS + "B"));
            report.check("le role est conserve",
                    party.roleOf(party.leader()) == Role.HEAL);
            report.check("l'epoch est conserve", party.epoch() > 0);
            report.check("les noms sont conserves",
                    party.entries().stream().allMatch(e -> e.lastKnownName().startsWith(WITNESS)));
            report.check("l'index joueur pointe vers le groupe",
                    store.partyOf(party.leader()).map(found -> found.id().equals(party.id())).orElse(false));
            report.check("l'equipe de scoreboard a ete recreee au demarrage",
                    server.getScoreboard().getTeam(PartyTeams.teamName(party)) != null);
        });

        clearWitnesses(server, store);
        report.summary();
        return report.failed == 0 ? 1 : 0;
    }

    private static void clearWitnesses(MinecraftServer server, PartyStore store) {
        for (Party party : store.all()) {
            boolean isWitness = party.entries().stream()
                    .anyMatch(entry -> entry.lastKnownName().startsWith(WITNESS));
            if (isWitness) {
                PartyTeams.dissolve(server, party);
                store.unregister(party);
            }
        }
    }

    /** Execute un scenario en retenant l'exception qu'il pourrait lever. */
    private static void scenario(Report report, String name, Runnable body) {
        try {
            body.run();
        } catch (Exception exception) {
            report.fail("scenario '" + name + "' interrompu : " + exception);
        }
    }

    // --- scenarios -----------------------------------------------------------

    private static void creation(MinecraftServer server, Report report, List<UUID> created) {
        ServerPlayerEntity a = ghost(server, "A");
        Outcome first = PartyManager.create(server, a, PartyType.DUNGEON_5);
        report.check("creation d'un groupe", first.ok());
        track(server, a, created);

        Outcome second = PartyManager.create(server, a, PartyType.DUNGEON_5);
        report.check("deuxieme creation refusee", !second.ok());
    }

    private static void invitations(MinecraftServer server, Report report, List<UUID> created) {
        ServerPlayerEntity leader = ghost(server, "L1");
        ServerPlayerEntity guest = ghost(server, "G1");
        PartyManager.create(server, leader, PartyType.DUNGEON_5);
        track(server, leader, created);

        report.check("invitation par le chef", PartyManager.invite(server, leader, guest).ok());
        report.check("acceptation", PartyManager.accept(server, guest).ok());

        Optional<Party> party = PartyManager.partyOf(server, leader.getUuid());
        report.check("groupe a 2 membres", party.isPresent() && party.get().size() == 2);
        report.check("l'invite n'est pas chef",
                party.isPresent() && !party.get().isLeader(guest.getUuid()));

        // Un non-chef ne peut pas inviter.
        ServerPlayerEntity outsider = ghost(server, "O1");
        report.check("invitation par un non-chef refusee",
                !PartyManager.invite(server, guest, outsider).ok());

        // Refus explicite.
        PartyManager.invite(server, leader, outsider);
        report.check("refus d'invitation", PartyManager.decline(server, outsider).ok());
        report.check("le refus laisse le groupe a 2",
                PartyManager.partyOf(server, leader.getUuid()).map(Party::size).orElse(0) == 2);
    }

    /**
     * L'anti-spam d'invitations (regle §49), remis en service le temps du scenario.
     *
     * <p>Ce delai a fait echouer une premiere version de ce diagnostic, en bloquant
     * l'invitation d'un scenario a cause de celle du scenario precedent. Le
     * comportement etait correct, le test ne l'etait pas — d'ou ce scenario dedie,
     * seul endroit ou le delai est actif.
     */
    private static void antiSpam(MinecraftServer server, Report report, List<UUID> created) {
        ServerPlayerEntity leader = ghost(server, "SP");
        ServerPlayerEntity first = ghost(server, "SP1");
        ServerPlayerEntity second = ghost(server, "SP2");
        PartyManager.create(server, leader, PartyType.DUNGEON_5);
        track(server, leader, created);

        PartyConfig.get().inviteCooldownSeconds = 30;
        try {
            report.check("premiere invitation acceptee",
                    PartyManager.invite(server, leader, first).ok());
            report.check("invitation immediate suivante refusee",
                    !PartyManager.invite(server, leader, second).ok());
            report.check("le delai n'empeche pas de repondre",
                    PartyManager.accept(server, first).ok());
        } finally {
            PartyConfig.get().inviteCooldownSeconds = 0;
            PartyManager.invites().forget(leader.getUuid());
        }
    }

    /**
     * Le verrou central : une reponse ne peut pas engager un groupe qui a change
     * depuis l'envoi de la demande (regles §19 et §20).
     */
    private static void epochGuard(MinecraftServer server, Report report, List<UUID> created) {
        ServerPlayerEntity leader = ghost(server, "L2");
        ServerPlayerEntity late = ghost(server, "Late");
        ServerPlayerEntity filler = ghost(server, "F2");
        PartyManager.create(server, leader, PartyType.DUNGEON_5);
        track(server, leader, created);

        PartyManager.invite(server, leader, late);
        int epochBefore = PartyManager.partyOf(server, leader.getUuid()).orElseThrow().epoch();

        // La composition change entre l'envoi et la reponse.
        PartyManager.invite(server, leader, filler);
        PartyManager.accept(server, filler);
        int epochAfter = PartyManager.partyOf(server, leader.getUuid()).orElseThrow().epoch();

        report.check("l'epoch change quand la composition change", epochAfter != epochBefore);
        report.check("acceptation perimee refusee", !PartyManager.accept(server, late).ok());
        report.check("le groupe reste a 2 apres le refus",
                PartyManager.partyOf(server, leader.getUuid()).map(Party::size).orElse(0) == 2);
    }

    private static void capacity(MinecraftServer server, Report report, List<UUID> created) {
        ServerPlayerEntity leader = ghost(server, "L3");
        PartyManager.create(server, leader, PartyType.DUNGEON_5);
        track(server, leader, created);

        for (int i = 0; i < 4; i++) {
            ServerPlayerEntity member = ghost(server, "M3" + i);
            PartyManager.invite(server, leader, member);
            PartyManager.accept(server, member);
        }
        Party party = PartyManager.partyOf(server, leader.getUuid()).orElseThrow();
        report.check("groupe complet a 5", party.size() == 5);

        ServerPlayerEntity sixth = ghost(server, "M35");
        report.check("sixieme invitation refusee", !PartyManager.invite(server, leader, sixth).ok());
    }

    private static void leadership(MinecraftServer server, Report report, List<UUID> created) {
        ServerPlayerEntity leader = ghost(server, "L4");
        ServerPlayerEntity second = ghost(server, "S4");
        ServerPlayerEntity third = ghost(server, "T4");
        PartyManager.create(server, leader, PartyType.DUNGEON_5);
        track(server, leader, created);

        PartyManager.invite(server, leader, second);
        PartyManager.accept(server, second);
        PartyManager.invite(server, leader, third);
        PartyManager.accept(server, third);

        report.check("transfert volontaire", PartyManager.transfer(server, leader, third).ok());
        report.check("le nouveau chef est le bon",
                PartyManager.partyOf(server, leader.getUuid()).orElseThrow().isLeader(third.getUuid()));
        report.check("l'ancien chef ne peut plus transferer",
                !PartyManager.transfer(server, leader, second).ok());

        // Le chef s'en va : successeur = le membre restant le plus ancien.
        PartyManager.leave(server, third);
        Party party = PartyManager.partyOf(server, leader.getUuid()).orElseThrow();
        report.check("succession deterministe au plus ancien", party.isLeader(leader.getUuid()));
        report.check("groupe reduit a 2", party.size() == 2);
    }

    private static void exclusion(MinecraftServer server, Report report, List<UUID> created) {
        ServerPlayerEntity leader = ghost(server, "L5");
        ServerPlayerEntity victim = ghost(server, "V5");
        PartyManager.create(server, leader, PartyType.DUNGEON_5);
        track(server, leader, created);

        PartyManager.invite(server, leader, victim);
        PartyManager.accept(server, victim);

        report.check("un membre ne peut pas exclure",
                !PartyManager.kick(server, victim, leader).ok());
        report.check("exclusion directe a 2 membres", PartyManager.kick(server, leader, victim).ok());
        report.check("l'exclu n'a plus de groupe",
                PartyManager.partyOf(server, victim.getUuid()).isEmpty());

        // A partir du seuil, l'exclusion doit passer par un vote : la commande refuse.
        List<ServerPlayerEntity> crowd = new ArrayList<>();
        for (int i = 0; i < 3; i++) {
            ServerPlayerEntity member = ghost(server, "C5" + i);
            PartyManager.invite(server, leader, member);
            PartyManager.accept(server, member);
            crowd.add(member);
        }
        Party party = PartyManager.partyOf(server, leader.getUuid()).orElseThrow();
        report.check("groupe a " + PartyConfig.get().kickVoteMinMembers + " membres",
                party.size() >= PartyConfig.get().kickVoteMinMembers);
        report.check("exclusion directe refusee au-dela du seuil",
                !PartyManager.kick(server, leader, crowd.get(0)).ok());
    }

    private static void roles(MinecraftServer server, Report report, List<UUID> created) {
        ServerPlayerEntity player = ghost(server, "R6");
        PartyManager.create(server, player, PartyType.DUNGEON_5);
        track(server, player, created);

        report.check("role par defaut",
                PartyManager.partyOf(server, player.getUuid()).orElseThrow()
                        .roleOf(player.getUuid()) == Role.UNSET);
        report.check("definition du role", PartyManager.setRole(server, player, Role.HEAL).ok());
        report.check("role enregistre",
                PartyManager.partyOf(server, player.getUuid()).orElseThrow()
                        .roleOf(player.getUuid()) == Role.HEAL);
    }

    /**
     * La verification qui compte pour le tir ami : l'equipe existe et son drapeau
     * est bien a faux. C'est ce drapeau que lisent Minecraft, Spell Engine et
     * Better Combat.
     */
    private static void scoreboardTeam(MinecraftServer server, Report report, List<UUID> created) {
        ServerPlayerEntity leader = ghost(server, "L7");
        ServerPlayerEntity mate = ghost(server, "M7");
        PartyManager.create(server, leader, PartyType.DUNGEON_5);
        track(server, leader, created);
        PartyManager.invite(server, leader, mate);
        PartyManager.accept(server, mate);

        Party party = PartyManager.partyOf(server, leader.getUuid()).orElseThrow();
        Team team = server.getScoreboard().getTeam(PartyTeams.teamName(party));

        report.check("equipe de scoreboard creee", team != null);
        if (team == null) {
            return;
        }
        report.check("tir ami desactive sur l'equipe", !team.isFriendlyFireAllowed());
        report.check("les deux membres sont dans l'equipe",
                team.getPlayerList().contains(leader.getNameForScoreboard())
                        && team.getPlayerList().contains(mate.getNameForScoreboard()));
        report.check("equipiers visibles meme invisibles", team.shouldShowFriendlyInvisibles());
    }

    private static void friendlyFireRelation(MinecraftServer server, Report report, List<UUID> created) {
        ServerPlayerEntity a = ghost(server, "FA");
        ServerPlayerEntity b = ghost(server, "FB");
        ServerPlayerEntity stranger = ghost(server, "FS");

        PartyManager.create(server, a, PartyType.DUNGEON_5);
        track(server, a, created);
        PartyManager.invite(server, a, b);
        PartyManager.accept(server, b);

        PartyManager.create(server, stranger, PartyType.DUNGEON_5);
        track(server, stranger, created);

        report.check("coequipiers reconnus",
                PartyManager.sameParty(server, a.getUuid(), b.getUuid()));
        report.check("relation symetrique",
                PartyManager.sameParty(server, b.getUuid(), a.getUuid()));
        report.check("etranger non protege",
                !PartyManager.sameParty(server, a.getUuid(), stranger.getUuid()));
        report.check("un joueur n'est pas son propre coequipier",
                !PartyManager.sameParty(server, a.getUuid(), a.getUuid()));
    }

    /**
     * La garde de degats, exercee pour de vrai.
     *
     * <p>Les scenarios precedents verifient la <em>relation</em> entre joueurs ;
     * celui-ci verifie le <em>verdict</em>, en fabriquant de vraies sources de degats
     * et en les soumettant a la garde. C'est le seul code de tir ami que ce mod
     * ecrive lui-meme, donc celui qu'il faut eprouver.
     *
     * <p>Chaque cas est pose deux fois : au verdict du mod seul, puis a l'evenement
     * complet auquel tous les mods sont abonnes. Un ecart entre les deux designe un
     * autre mod, pas celui-ci.
     */
    private static void damageGuard(MinecraftServer server, Report report, List<UUID> created) {
        ServerWorld world = server.getOverworld();
        ServerPlayerEntity a = ghost(server, "DA");
        ServerPlayerEntity mate = ghost(server, "DB");
        ServerPlayerEntity stranger = ghost(server, "DS");

        PartyManager.create(server, a, PartyType.DUNGEON_5);
        track(server, a, created);
        PartyManager.invite(server, a, mate);
        PartyManager.accept(server, mate);

        DamageSource melee = world.getDamageSources().playerAttack(a);

        report.check("melee entre coequipiers refusee", !FriendlyFire.allows(mate, melee));
        report.check("melee sur un etranger autorisee", FriendlyFire.allows(stranger, melee));
        report.check("degat auto-inflige autorise", FriendlyFire.allows(a, melee));

        // Chute : aucune entite en cause, la garde ne doit jamais s'en meler.
        report.check("degat sans auteur autorise",
                FriendlyFire.allows(mate, world.getDamageSources().generic()));

        // Le cas que l'equipe de scoreboard ne couvre pas : l'entite causante n'est
        // pas le joueur, il faut remonter jusqu'a son proprietaire.
        // L'arme doit etre une vraie arme de jet : ArrowEntity refuse une pile vide.
        ArrowEntity arrow = new ArrowEntity(world, a, new ItemStack(Items.ARROW), new ItemStack(Items.BOW));
        DamageSource orphanArrow = world.getDamageSources().arrow(arrow, null);
        report.check("projectile d'un coequipier refuse par remontee de propriete",
                !FriendlyFire.allows(mate, orphanArrow));
        report.check("projectile d'un coequipier autorise sur un etranger",
                FriendlyFire.allows(stranger, orphanArrow));

        // Meme chaine, mais sans le suivi des proprietaires : le degat repasse.
        // C'est la preuve que c'est bien la remontee qui bloque, et pas autre chose.
        PartyConfig.get().guardOwnedEntities = false;
        try {
            report.check("sans remontee, le projectile repasse",
                    FriendlyFire.allows(mate, orphanArrow));
        } finally {
            PartyConfig.get().guardOwnedEntities = true;
        }

        // L'evenement complet, tel que Minecraft l'appelle reellement.
        report.check("evenement complet : melee entre coequipiers refusee",
                !ServerLivingEntityEvents.ALLOW_DAMAGE.invoker().allowDamage(mate, melee, 1.0f));
        report.check("evenement complet : etranger toujours frappable",
                ServerLivingEntityEvents.ALLOW_DAMAGE.invoker().allowDamage(stranger, melee, 1.0f));

        arrow.discard();
    }

    // --- consultations du groupe (phase 2) -----------------------------------

    /**
     * Verification de preparation, de bout en bout (regle §5).
     *
     * <p>Les joueurs fabriques n'ont pas de connexion, donc aucune fenetre ne part
     * reellement — {@code PartyDialogs} le detecte et s'abstient. Ce qui est
     * eprouve ici est tout le reste : les droits, l'unicite, l'identifiant de
     * demande, le comptage et la cloture.
     */
    private static void readyCheckFlow(MinecraftServer server, Report report, List<UUID> created) {
        ServerPlayerEntity leader = ghost(server, "RC_L");
        ServerPlayerEntity mate = ghost(server, "RC_M");
        Party party = buildParty(server, created, leader, mate);
        RequestManager.clearCooldowns(party.id());

        report.check("un membre ne peut pas lancer la verification",
                !RequestManager.startReadyCheck(server, mate).ok());
        report.check("le chef la lance", RequestManager.startReadyCheck(server, leader).ok());
        report.check("elle est active", RequestManager.readyCheckOf(party.id()).isPresent());
        report.check("une seule a la fois",
                !RequestManager.startReadyCheck(server, leader).ok());

        Poll poll = RequestManager.readyCheckOf(party.id()).orElseThrow();
        report.check("tous les membres sont consultes", poll.voters().size() == 2);

        report.check("un identifiant errone est refuse",
                !RequestManager.answerReady(server, leader, UUID.randomUUID(), true).ok());
        report.check("la bonne demande accepte la reponse",
                RequestManager.answerReady(server, leader, poll.id(), true).ok());
        report.check("on ne repond pas deux fois",
                !RequestManager.answerReady(server, leader, poll.id(), false).ok());

        report.check("la verification reste ouverte tant qu'il manque une reponse",
                RequestManager.readyCheckOf(party.id()).isPresent());
        RequestManager.answerReady(server, mate, poll.id(), false);
        report.check("elle se conclut quand tout le monde a repondu",
                RequestManager.readyCheckOf(party.id()).isEmpty());
        report.check("le compte des prets est juste", poll.yesCount() == 1);

        report.check("un delai empeche de relancer aussitot",
                !RequestManager.startReadyCheck(server, leader).ok());
        RequestManager.clearCooldowns(party.id());
        report.check("et la relance passe une fois le delai efface",
                RequestManager.startReadyCheck(server, leader).ok());
        RequestManager.clearCooldowns(party.id());
    }

    /**
     * Le paquet de dialogue passe-t-il vraiment sur le fil ?
     *
     * <p>C'est le seul risque que les scenarios precedents ne couvrent pas : les
     * joueurs fabriques n'ont pas de connexion, donc aucune fenetre n'est reellement
     * envoyee et une erreur de serialisation resterait invisible jusqu'a ce qu'un
     * vrai joueur clique.
     *
     * <p>On encode donc le paquet dans un tampon reseau puis on le relit, exactement
     * comme le ferait la connexion. Le dialogue etant transmis en <em>entree
     * directe</em> plutot que par une reference de registre, c'est sa definition
     * complete — titre, corps, boutons, commandes — qui doit survivre au trajet.
     */
    private static void dialogPacket(MinecraftServer server, Report report, List<UUID> created) {
        DialogCommonData common = new DialogCommonData(
                Text.literal("Etes-vous pret ?"),
                Optional.empty(), true, false, AfterAction.CLOSE,
                List.<DialogBody>of(new PlainMessageDialogBody(Text.literal("Le chef verifie le groupe."), 320)),
                List.of());
        UUID demande = UUID.randomUUID();
        ConfirmationDialog dialog = new ConfirmationDialog(common,
                actionButton("PRET", "/party ready " + demande + " oui"),
                actionButton("PAS PRET", "/party ready " + demande + " non"));

        ShowDialogS2CPacket packet = new ShowDialogS2CPacket(RegistryEntry.of(dialog));
        RegistryByteBuf buffer = new RegistryByteBuf(Unpooled.buffer(), server.getRegistryManager());

        ShowDialogS2CPacket.REGISTRY_CODEC.encode(buffer, packet);
        report.check("le paquet de dialogue s'encode", buffer.readableBytes() > 0);

        ShowDialogS2CPacket relu = ShowDialogS2CPacket.REGISTRY_CODEC.decode(buffer);
        report.check("il se relit entierement", buffer.readableBytes() == 0);

        Dialog dialogRelu = relu.dialog().value();
        report.check("le titre survit au trajet",
                dialogRelu.common().title().getString().equals("Etes-vous pret ?"));
        report.check("le corps survit au trajet", dialogRelu.common().body().size() == 1);
        report.check("la fenetre se ferme apres le clic",
                dialogRelu.common().afterAction() == AfterAction.CLOSE);

        report.check("c'est bien une confirmation a deux boutons",
                dialogRelu instanceof ConfirmationDialog);
        if (dialogRelu instanceof ConfirmationDialog confirmation) {
            report.check("le bouton d'acceptation garde son libelle",
                    confirmation.yesButton().data().label().getString().equals("PRET"));
            report.check("il porte bien une action",
                    confirmation.yesButton().action().isPresent());
            // Le point decisif : l'identifiant de la demande doit voyager dans la
            // commande, sinon le serveur ne saurait pas a quoi la reponse repond.
            report.check("l'identifiant de la demande voyage avec le bouton",
                    confirmation.yesButton().action()
                            .flatMap(action -> action.createClickEvent(java.util.Map.of()))
                            .filter(event -> event instanceof ClickEvent.RunCommand)
                            .map(event -> ((ClickEvent.RunCommand) event).command())
                            .filter(command -> command.contains(demande.toString()))
                            .isPresent());
            report.check("le bouton de refus porte la reponse inverse",
                    confirmation.noButton().action()
                            .flatMap(action -> action.createClickEvent(java.util.Map.of()))
                            .filter(event -> event instanceof ClickEvent.RunCommand)
                            .map(event -> ((ClickEvent.RunCommand) event).command())
                            .filter(command -> command.endsWith(" non"))
                            .isPresent());
        }

        buffer.release();
    }

    private static DialogActionButtonData actionButton(String label, String command) {
        return new DialogActionButtonData(
                new DialogButtonData(Text.literal(label), Optional.empty(), 150),
                Optional.of(new SimpleDialogAction(new ClickEvent.RunCommand(command))));
    }

    /** Vote d'exclusion, jusqu'a l'exclusion effective (regle §15). */
    private static void kickVoteFlow(MinecraftServer server, Report report, List<UUID> created) {
        ServerPlayerEntity leader = ghost(server, "KV_L");
        ServerPlayerEntity cible = ghost(server, "KV_T");
        ServerPlayerEntity x = ghost(server, "KV_X");
        Party party = buildParty(server, created, leader, cible, x);
        RequestManager.clearCooldowns(party.id());

        report.check("sous le seuil de membres, le vote est refuse",
                !RequestManager.startKickVote(server, leader, cible).ok());

        ServerPlayerEntity y = ghost(server, "KV_Y");
        joinParty(server, leader, y);
        report.check("groupe a 4", party.size() == 4);

        report.check("un membre ne peut pas lancer le vote",
                !RequestManager.startKickVote(server, x, cible).ok());
        report.check("le chef ne peut pas se viser lui-meme",
                !RequestManager.startKickVote(server, leader, leader).ok());
        report.check("le chef lance le vote",
                RequestManager.startKickVote(server, leader, cible).ok());

        Poll poll = RequestManager.kickVoteOf(party.id()).orElseThrow();
        report.check("la cible ne vote pas", !poll.isVoter(cible.getUuid()));
        report.check("trois votants", poll.voters().size() == 3);
        report.check("seuil a 2", poll.threshold() == 2);
        report.check("la voix du chef compte deja", poll.yesCount() == 1);
        report.check("la cible ne peut pas repondre",
                !RequestManager.answerVote(server, cible, poll.id(), false).ok());

        report.check("une voix de plus emporte la decision",
                RequestManager.answerVote(server, x, poll.id(), true).ok());
        report.check("le vote est clos", RequestManager.kickVoteOf(party.id()).isEmpty());
        report.check("la cible a bien ete exclue",
                PartyManager.partyOf(server, cible.getUuid()).isEmpty());
        report.check("le groupe est retombe a 3", party.size() == 3);
        RequestManager.clearCooldowns(party.id());
    }

    /** Vote perdu : la cible reste, et se trouve protegee contre une relance immediate. */
    private static void kickVoteRejected(MinecraftServer server, Report report, List<UUID> created) {
        ServerPlayerEntity leader = ghost(server, "KR_L");
        ServerPlayerEntity cible = ghost(server, "KR_T");
        ServerPlayerEntity x = ghost(server, "KR_X");
        ServerPlayerEntity y = ghost(server, "KR_Y");
        Party party = buildParty(server, created, leader, cible, x, y);
        RequestManager.clearCooldowns(party.id());

        RequestManager.startKickVote(server, leader, cible);
        Poll poll = RequestManager.kickVoteOf(party.id()).orElseThrow();

        // Deux refus sur trois votants : le seuil de 2 devient inatteignable.
        RequestManager.answerVote(server, x, poll.id(), false);
        RequestManager.answerVote(server, y, poll.id(), false);

        report.check("le vote perdu se conclut sans attendre l'echeance",
                RequestManager.kickVoteOf(party.id()).isEmpty());
        report.check("la cible reste dans le groupe",
                PartyManager.partyOf(server, cible.getUuid()).isPresent());
        report.check("elle est protegee contre une relance immediate",
                !RequestManager.startKickVote(server, leader, cible).ok());
        RequestManager.clearCooldowns(party.id());
    }

    /** Compte a rebours (regle §6). */
    private static void countdownFlow(MinecraftServer server, Report report, List<UUID> created) {
        ServerPlayerEntity leader = ghost(server, "CD_L");
        ServerPlayerEntity mate = ghost(server, "CD_M");
        Party party = buildParty(server, created, leader, mate);
        RequestManager.clearCooldowns(party.id());

        report.check("un membre ne peut pas le lancer",
                !RequestManager.startCountdown(server, mate, 5).ok());
        report.check("zero seconde est refuse",
                !RequestManager.startCountdown(server, leader, 0).ok());
        report.check("une duree demesuree est refusee",
                !RequestManager.startCountdown(server, leader, 9999).ok());
        report.check("le chef lance un compte a rebours",
                RequestManager.startCountdown(server, leader, 3).ok());
        report.check("il est actif", RequestManager.countdownOf(party.id()).isPresent());
        report.check("un seul a la fois",
                !RequestManager.startCountdown(server, leader, 3).ok());

        report.check("il decompte", RequestManager.countdownOf(party.id())
                .map(countdown -> countdown.secondsLeft() == 3).orElse(false));

        // On fait tourner le battement a la main : 3 s plus une marge.
        for (int i = 0; i < 3 * 20 + 5; i++) {
            RequestManager.tick(server);
        }
        report.check("il se termine tout seul", RequestManager.countdownOf(party.id()).isEmpty());

        RequestManager.clearCooldowns(party.id());
        report.check("un nouveau peut etre lance",
                RequestManager.startCountdown(server, leader, 10).ok());
        report.check("le chef peut l'annuler",
                RequestManager.cancelCountdown(server, leader).ok());
        report.check("annuler sans compte a rebours est refuse",
                !RequestManager.cancelCountdown(server, leader).ok());
        RequestManager.clearCooldowns(party.id());
    }

    /**
     * Une consultation ne survit pas a un changement de composition.
     *
     * <p>Meme verrou que pour les invitations, et demain pour la teleportation
     * collective : les votants ne sont plus les memes, donc le resultat serait
     * calcule sur une liste perimee (regles §19 et §20).
     */
    private static void pollEpochGuard(MinecraftServer server, Report report, List<UUID> created) {
        ServerPlayerEntity leader = ghost(server, "PE_L");
        ServerPlayerEntity a = ghost(server, "PE_A");
        ServerPlayerEntity b = ghost(server, "PE_B");
        Party party = buildParty(server, created, leader, a, b);
        RequestManager.clearCooldowns(party.id());

        RequestManager.startReadyCheck(server, leader);
        Poll poll = RequestManager.readyCheckOf(party.id()).orElseThrow();
        int epochAvant = poll.partyEpoch();

        // Un membre s'en va : la composition change, donc l'epoch aussi.
        PartyManager.leave(server, b);
        report.check("l'epoch du groupe a change", party.epoch() != epochAvant);

        RequestManager.tick(server);
        report.check("la verification a ete abandonnee",
                RequestManager.readyCheckOf(party.id()).isEmpty());
        report.check("une reponse tardive est refusee",
                !RequestManager.answerReady(server, a, poll.id(), true).ok());
        RequestManager.clearCooldowns(party.id());
    }

    /** La cible d'un vote qui se deconnecte fait tomber le vote (regle §15). */
    private static void kickVoteTargetLeaves(MinecraftServer server, Report report, List<UUID> created) {
        ServerPlayerEntity leader = ghost(server, "KD_L");
        ServerPlayerEntity cible = ghost(server, "KD_T");
        ServerPlayerEntity x = ghost(server, "KD_X");
        ServerPlayerEntity y = ghost(server, "KD_Y");
        Party party = buildParty(server, created, leader, cible, x, y);
        RequestManager.clearCooldowns(party.id());

        RequestManager.startKickVote(server, leader, cible);
        report.check("le vote est en cours", RequestManager.kickVoteOf(party.id()).isPresent());

        RequestManager.onPlayerLeave(server, cible.getUuid());
        report.check("la deconnexion de la cible annule le vote",
                RequestManager.kickVoteOf(party.id()).isEmpty());
        report.check("elle reste membre le temps de sa reservation",
                party.contains(cible.getUuid()));
        RequestManager.clearCooldowns(party.id());
    }

    // --- HUD (phase 3) -------------------------------------------------------

    /** Le paquet de HUD passe-t-il sur le fil, et revient-il identique ? */
    private static void hudPacket(MinecraftServer server, Report report, List<UUID> created) {
        HudPayload original = new HudPayload(List.of(
                new HudPayload.Member("Arthur", 17.5f, 20f, 0, 34, (byte) 1, "Paladin",
                        HudPayload.Member.flags(true, true, true, true)),
                new HudPayload.Member("Bertrand", 0f, 20f, 12, 30, (byte) 3, "Voleur",
                        HudPayload.Member.flags(false, false, true, false)),
                new HudPayload.Member("Clémence", 0f, 0f, -1, -1, (byte) 2, "",
                        HudPayload.Member.flags(false, true, false, false))));

        RegistryByteBuf buffer = new RegistryByteBuf(Unpooled.buffer(), server.getRegistryManager());
        HudPayload.CODEC.encode(buffer, original);
        report.check("le paquet de HUD s'encode", buffer.readableBytes() > 0);
        int taille = buffer.readableBytes();

        HudPayload relu = HudPayload.CODEC.decode(buffer);
        report.check("il se relit entierement", buffer.readableBytes() == 0);
        report.check("il revient identique", relu.equals(original));
        report.check("les accents survivent", relu.members().get(2).name().equals("Clémence"));
        report.check("les drapeaux survivent",
                relu.members().get(0).leader() && relu.members().get(0).self()
                        && !relu.members().get(1).alive() && !relu.members().get(2).online());
        report.check("un paquet de trois lignes reste petit (< 120 octets : " + taille + ")", taille < 120);
        buffer.release();

        RegistryByteBuf vide = new RegistryByteBuf(Unpooled.buffer(), server.getRegistryManager());
        HudPayload.CODEC.encode(vide, HudPayload.empty());
        report.check("l'instantane vide tient en un octet", vide.readableBytes() == 1);
        report.check("et se relit vide", HudPayload.CODEC.decode(vide).isEmpty());
        vide.release();
    }

    /**
     * La construction d'un instantane sur un vrai groupe.
     *
     * <p>Les joueurs fabriques ne sont pas dans le gestionnaire de joueurs, donc le
     * serveur les voit tous <em>hors ligne</em> — c'est le chemin qui est eprouve ici,
     * avec le chef et le drapeau « soi ». Le chemin « en ligne » (vie, distance,
     * classe, niveau) exige un vrai client connecte : c'est {@code /party diagnostic hud}.
     */
    private static void hudSnapshot(MinecraftServer server, Report report, List<UUID> created) {
        ServerPlayerEntity leader = ghost(server, "HS_L");
        ServerPlayerEntity a = ghost(server, "HS_A");
        ServerPlayerEntity b = ghost(server, "HS_B");
        Party party = buildParty(server, created, leader, a, b);
        PartyManager.setRole(server, a, Role.HEAL);

        HudPayload vuParA = HudSync.build(server, party, a);
        report.check("trois lignes", vuParA.members().size() == 3);
        report.check("l'ordre est celui du groupe",
                vuParA.members().get(0).name().equals(leader.getName().getString()));
        report.check("le chef est marque", vuParA.members().get(0).leader());
        report.check("un seul chef", vuParA.members().stream().filter(HudPayload.Member::leader).count() == 1);
        report.check("« soi » designe le destinataire", vuParA.members().get(1).self());
        report.check("un seul « soi »", vuParA.members().stream().filter(HudPayload.Member::self).count() == 1);
        report.check("le role voyage", vuParA.members().get(1).role() == (byte) Role.HEAL.ordinal());
        report.check("les fantomes sont vus hors ligne",
                vuParA.members().stream().noneMatch(HudPayload.Member::online));
        report.check("un hors ligne n'est jamais declare mort",
                vuParA.members().stream().allMatch(HudPayload.Member::alive));
        report.check("le nom d'un hors ligne vient du groupe",
                vuParA.members().get(2).name().equals(b.getName().getString()));

        HudPayload vuParB = HudSync.build(server, party, b);
        report.check("l'instantane depend du destinataire", !vuParA.equals(vuParB));
        report.check("chacun se voit lui-meme", vuParB.members().get(2).self());
    }

    /**
     * Les ponts vers le noyau RPG et Pufferfish se lient-ils sans casser ?
     *
     * <p>Le risque n'est pas logique mais de liaison : une signature qui aurait
     * change dans l'un de ces mods ne se verrait qu'au premier appel, par une
     * {@code NoSuchMethodError}. On force donc ce premier appel ici, sur un joueur
     * fabrique, et on verifie que le pont repond sans se couper. Quand le mod est
     * absent, le pont doit se taire sans rien lever non plus.
     */
    private static void bridges(MinecraftServer server, Report report, List<UUID> created) {
        ServerPlayerEntity ghost = ghost(server, "BR");
        boolean rpgLoaded = net.fabricmc.loader.api.FabricLoader.getInstance().isModLoaded("haute_capitale_rpg");
        boolean skillsLoaded = net.fabricmc.loader.api.FabricLoader.getInstance().isModLoaded("puffish_skills");

        String label = net.hautecapitale.party.bridge.RpgClassBridge.classLabel(ghost);
        report.check("pont RPG : repond sans lever (" + (rpgLoaded ? "noyau present" : "noyau absent") + ")",
                label != null);
        report.check("pont RPG : toujours actif apres l'appel",
                net.hautecapitale.party.bridge.RpgClassBridge.present() == rpgLoaded);
        report.check("pont RPG : un joueur sans classe donne une chaine vide", label.isEmpty());

        int level = net.hautecapitale.party.bridge.SkillsBridge.level(ghost);
        report.check("pont Pufferfish : repond sans lever (" + (skillsLoaded ? "present" : "absent") + ")",
                level >= -1);
        report.check("pont Pufferfish : toujours actif apres l'appel",
                net.hautecapitale.party.bridge.SkillsBridge.present() == skillsLoaded);
        if (!skillsLoaded) {
            report.check("pont Pufferfish : inconnu quand le mod manque", level == -1);
        }
    }

    /**
     * {@code /party diagnostic hud} : envoie au joueur qui l'execute un instantane
     * fabrique — cinq lignes couvrant tous les etats — pour juger le rendu a l'oeil.
     * {@code /party diagnostic hud off} l'efface.
     */
    public static int pushSampleHud(ServerCommandSource source, boolean on) {
        ServerPlayerEntity player;
        try {
            player = source.getPlayerOrThrow();
        } catch (Exception exception) {
            source.sendFeedback(() -> Text.literal("Cette commande demande un joueur connecte.")
                    .formatted(Formatting.RED), false);
            return 0;
        }
        if (!ServerPlayNetworking.canSend(player, HudPayload.ID)) {
            source.sendFeedback(() -> Text.literal("Votre client n'a pas le mod : aucun HUD ne peut lui etre envoye.")
                    .formatted(Formatting.YELLOW), false);
            return 0;
        }
        if (!on) {
            ServerPlayNetworking.send(player, HudPayload.empty());
            source.sendFeedback(() -> Text.literal("HUD d'essai efface.").formatted(Formatting.GRAY), false);
            return 1;
        }
        HudPayload sample = new HudPayload(List.of(
                new HudPayload.Member(player.getName().getString(), 20f, 20f, 0, 34, (byte) 1, "Paladin",
                        HudPayload.Member.flags(true, true, true, true)),
                new HudPayload.Member("Bertrand", 8f, 20f, 12, 30, (byte) 3, "Voleur",
                        HudPayload.Member.flags(false, true, true, false)),
                new HudPayload.Member("Clémence", 0f, 20f, 3, 33, (byte) 2, "Prêtre",
                        HudPayload.Member.flags(false, false, true, false)),
                new HudPayload.Member("Damien", 0f, 0f, -1, -1, (byte) 3, "",
                        HudPayload.Member.flags(false, true, false, false)),
                new HudPayload.Member("Éléonore", 4f, 30f, -1, 12, (byte) 0, "Barde",
                        HudPayload.Member.flags(false, true, true, false))));
        ServerPlayNetworking.send(player, sample);
        source.sendFeedback(() -> Text.literal(
                "HUD d'essai envoye : chef, blesse, morte, hors ligne, autre dimension. « /party diagnostic hud off » pour l'effacer.")
                .formatted(Formatting.AQUA), false);
        return 1;
    }

    private static void dissolution(MinecraftServer server, Report report, List<UUID> created) {
        ServerPlayerEntity leader = ghost(server, "L8");
        ServerPlayerEntity mate = ghost(server, "M8");
        PartyManager.create(server, leader, PartyType.DUNGEON_5);
        PartyManager.invite(server, leader, mate);
        PartyManager.accept(server, mate);

        Party party = PartyManager.partyOf(server, leader.getUuid()).orElseThrow();
        String teamName = PartyTeams.teamName(party);

        report.check("un membre ne peut pas dissoudre", !PartyManager.disband(server, mate).ok());
        report.check("dissolution par le chef", PartyManager.disband(server, leader).ok());
        report.check("plus aucun groupe pour le chef",
                PartyManager.partyOf(server, leader.getUuid()).isEmpty());
        report.check("plus aucun groupe pour le membre",
                PartyManager.partyOf(server, mate.getUuid()).isEmpty());
        report.check("equipe de scoreboard supprimee",
                server.getScoreboard().getTeam(teamName) == null);

        // Le dernier membre qui part emporte le groupe avec lui.
        ServerPlayerEntity solo = ghost(server, "S8");
        PartyManager.create(server, solo, PartyType.DUNGEON_5);
        String soloTeam = PartyTeams.teamName(
                PartyManager.partyOf(server, solo.getUuid()).orElseThrow());
        PartyManager.leave(server, solo);
        report.check("groupe d'une personne dissous au depart",
                PartyManager.partyOf(server, solo.getUuid()).isEmpty());
        report.check("son equipe aussi", server.getScoreboard().getTeam(soloTeam) == null);
    }

    /** Le magasin doit savoir se remettre d'aplomb : c'est ce qui protege un redemarrage. */
    private static void coherence(MinecraftServer server, Report report, List<UUID> created) {
        PartyStore store = PartyStore.of(server);
        ServerPlayerEntity leader = ghost(server, "L9");
        PartyManager.create(server, leader, PartyType.DUNGEON_5);
        track(server, leader, created);

        // Index pollue volontairement : un joueur qui pointe vers un groupe inexistant.
        UUID orphan = UUID.randomUUID();
        store.indexMember(orphan, Party.found(orphan, PREFIX + "orphan", PartyType.DUNGEON_5, 0L));

        List<String> problems = store.reconcile();
        report.check("l'incoherence est detectee", !problems.isEmpty());
        report.check("l'index orphelin est nettoye", !store.hasParty(orphan));
        report.check("le groupe sain survit au nettoyage",
                PartyManager.partyOf(server, leader.getUuid()).isPresent());

        Invites invites = PartyManager.invites();
        report.check("les invitations sont interrogeables",
                invites.pendingCount(System.currentTimeMillis()) >= 0);
    }

    // --- outillage -----------------------------------------------------------

    /**
     * Un joueur en memoire.
     *
     * <p>Jamais ajoute au monde ni au gestionnaire de joueurs : il n'a pas de
     * connexion reseau. Tout envoi de message le concernant passe donc par
     * {@code PartyManager.tell}, qui verifie ce point avant d'ecrire.
     */
    private static ServerPlayerEntity ghost(MinecraftServer server, String label) {
        ServerWorld world = server.getOverworld();
        GameProfile profile = new GameProfile(UUID.randomUUID(), PREFIX + label);
        return new ServerPlayerEntity(server, world, profile, SyncedClientOptions.createDefault());
    }

    /**
     * Un joueur reellement <b>en ligne</b> (via un canal embarque) : contrairement au {@code ghost},
     * il est dans le gestionnaire de joueurs, donc resoluble par UUID — indispensable pour que le
     * proprietaire d'une creature ({@code getOwner()}) soit retrouve dans le test de ciblage.
     */
    private static ServerPlayerEntity connect(MinecraftServer server, String label) {
        GameProfile profile = new GameProfile(UUID.randomUUID(), PREFIX + label);
        ServerPlayerEntity player = new ServerPlayerEntity(server, server.getOverworld(), profile, SyncedClientOptions.createDefault());
        ClientConnection conn = new ClientConnection(NetworkSide.SERVERBOUND);
        new io.netty.channel.embedded.EmbeddedChannel(conn);
        server.getPlayerManager().onPlayerConnect(conn, player, ConnectedClientData.createDefault(profile, false));
        return player;
    }

    /** Monte un groupe complet en une ligne : le premier est chef, les autres le rejoignent. */
    private static Party buildParty(MinecraftServer server, List<UUID> created,
                                    ServerPlayerEntity leader, ServerPlayerEntity... others) {
        PartyManager.create(server, leader, PartyType.DUNGEON_5);
        track(server, leader, created);
        for (ServerPlayerEntity other : others) {
            joinParty(server, leader, other);
        }
        return PartyManager.partyOf(server, leader.getUuid()).orElseThrow();
    }

    private static void joinParty(MinecraftServer server, ServerPlayerEntity leader, ServerPlayerEntity guest) {
        PartyManager.invite(server, leader, guest);
        PartyManager.accept(server, guest);
    }

    private static void track(MinecraftServer server, ServerPlayerEntity player, List<UUID> created) {
        PartyManager.partyOf(server, player.getUuid())
                .map(Party::id)
                .filter(id -> !created.contains(id))
                .ifPresent(created::add);
    }

    /** Efface tout ce que le diagnostic a pu laisser, groupes comme equipes. */
    private static void cleanup(MinecraftServer server, List<UUID> created) {
        PartyStore store = PartyStore.of(server);
        for (Party party : store.all()) {
            boolean mine = created.contains(party.id());
            if (!mine) {
                // Filet : un groupe dont tous les membres portent le prefixe est a nous,
                // meme si son identifiant n'a pas ete suivi (cas d'une exception en cours).
                mine = !party.entries().isEmpty() && party.entries().stream()
                        .allMatch(entry -> entry.lastKnownName().startsWith(PREFIX));
            }
            if (mine) {
                PartyTeams.dissolve(server, party);
                store.unregister(party);
            }
        }
        PartyConfig.load();
    }

    /** Compte les verifications et rend le resultat lisible dans la console comme par RCON. */
    private static final class Report {
        private final ServerCommandSource source;
        private int passed;
        private int failed;

        private Report(ServerCommandSource source) {
            this.source = source;
        }

        private void check(String label, boolean condition) {
            if (condition) {
                this.passed++;
            } else {
                this.failed++;
                this.source.sendFeedback(() -> Text.literal("  ECHEC - " + label)
                        .formatted(Formatting.RED), false);
            }
        }

        private void fail(String label) {
            this.failed++;
            this.source.sendFeedback(() -> Text.literal("  ECHEC - " + label)
                    .formatted(Formatting.RED), false);
        }

        private void summary() {
            int total = this.passed + this.failed;
            Formatting color = this.failed == 0 ? Formatting.GREEN : Formatting.RED;
            this.source.sendFeedback(() -> Text.literal(
                    "Diagnostic groupe : " + this.passed + "/" + total + " verifications reussies.")
                    .formatted(color), false);
        }
    }
}
