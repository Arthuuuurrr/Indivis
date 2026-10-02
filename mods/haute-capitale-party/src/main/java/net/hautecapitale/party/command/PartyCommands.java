package net.hautecapitale.party.command;

import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.hautecapitale.party.diagnostic.PartyDiagnostic;
import net.hautecapitale.party.dialog.PartyMenu;
import net.hautecapitale.party.party.Outcome;
import net.hautecapitale.party.party.Party;
import net.hautecapitale.party.party.PartyManager;
import net.hautecapitale.party.party.PartyType;
import net.hautecapitale.party.party.Role;
import net.hautecapitale.party.request.RequestManager;
import net.hautecapitale.party.text.Msg;
import net.minecraft.command.CommandSource;
import net.minecraft.command.argument.EntityArgumentType;
import net.minecraft.command.argument.UuidArgumentType;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;

import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import java.util.Optional;
import java.util.UUID;
import java.util.function.BiConsumer;
import java.util.function.Function;

/**
 * Les commandes de groupe.
 *
 * <p>Chacune se contente de traduire une intention et d'afficher le
 * {@link Outcome} rendu par {@link PartyManager}. Aucune verification de regle
 * ici : elles vivent toutes au meme endroit, ce qui evite qu'une commande et un
 * bouton de chat divergent sur ce qui est permis.
 */
public final class PartyCommands {

    private PartyCommands() {
    }

    public static void register() {
        CommandRegistrationCallback.EVENT.register((dispatcher, registryAccess, environment) -> {
            dispatcher.register(buildParty());
            dispatcher.register(buildChatShortcut());
        });
    }

    private static LiteralArgumentBuilder<ServerCommandSource> buildParty() {
        return CommandManager.literal("party")
                .executes(context -> info(context.getSource()))

                .then(CommandManager.literal("create")
                        .executes(context -> run(context, player ->
                                PartyManager.create(player.getEntityWorld().getServer(), player, PartyType.DUNGEON_5)))
                        .then(CommandManager.argument("type", StringArgumentType.word())
                                .suggests((context, builder) -> CommandSource.suggestMatching(
                                        Arrays.stream(PartyType.values()).map(PartyType::asString), builder))
                                .executes(context -> {
                                    String raw = StringArgumentType.getString(context, "type");
                                    Optional<PartyType> type = PartyType.parse(raw);
                                    if (type.isEmpty()) {
                                        context.getSource().sendFeedback(
                                                () -> Msg.bad("Type de groupe inconnu : " + raw), false);
                                        return 0;
                                    }
                                    return run(context, player ->
                                            PartyManager.create(player.getEntityWorld().getServer(), player, type.get()));
                                })))

                .then(CommandManager.literal("invite")
                        .then(CommandManager.argument("joueur", EntityArgumentType.player())
                                .executes(context -> {
                                    ServerPlayerEntity target = EntityArgumentType.getPlayer(context, "joueur");
                                    return run(context, player ->
                                            PartyManager.invite(player.getEntityWorld().getServer(), player, target));
                                })))

                .then(CommandManager.literal("accept")
                        .executes(context -> run(context, player ->
                                PartyManager.accept(player.getEntityWorld().getServer(), player))))

                .then(CommandManager.literal("decline")
                        .executes(context -> run(context, player ->
                                PartyManager.decline(player.getEntityWorld().getServer(), player))))

                .then(CommandManager.literal("leave")
                        .executes(context -> run(context, player ->
                                PartyManager.leave(player.getEntityWorld().getServer(), player))))

                .then(CommandManager.literal("transfer")
                        .then(CommandManager.argument("joueur", EntityArgumentType.player())
                                .executes(context -> {
                                    ServerPlayerEntity target = EntityArgumentType.getPlayer(context, "joueur");
                                    return run(context, player ->
                                            PartyManager.transfer(player.getEntityWorld().getServer(), player, target));
                                })))

                .then(CommandManager.literal("kick")
                        .then(CommandManager.argument("joueur", EntityArgumentType.player())
                                .executes(context -> {
                                    ServerPlayerEntity target = EntityArgumentType.getPlayer(context, "joueur");
                                    return run(context, player ->
                                            PartyManager.kick(player.getEntityWorld().getServer(), player, target));
                                })))

                .then(CommandManager.literal("role")
                        .then(CommandManager.argument("role", StringArgumentType.word())
                                .suggests((context, builder) -> CommandSource.suggestMatching(
                                        Arrays.stream(Role.values()).map(Role::asString), builder))
                                .executes(context -> {
                                    String raw = StringArgumentType.getString(context, "role");
                                    Optional<Role> role = Role.parse(raw);
                                    if (role.isEmpty()) {
                                        context.getSource().sendFeedback(
                                                () -> Msg.bad("Role inconnu : " + raw
                                                        + " (tank, heal, dps, aucun)"), false);
                                        return 0;
                                    }
                                    return run(context, player ->
                                            PartyManager.setRole(player.getEntityWorld().getServer(), player, role.get()));
                                })))

                // --- menu au clic (touche P cote client) : aucune commande a taper ---
                .then(CommandManager.literal("menu")
                        .executes(context -> openMenu(context, PartyMenu::open))
                        .then(CommandManager.literal("invite").executes(context -> openMenu(context, PartyMenu::openInvite)))
                        .then(CommandManager.literal("kick").executes(context -> openMenu(context, PartyMenu::openKick)))
                        .then(CommandManager.literal("transfer").executes(context -> openMenu(context, PartyMenu::openTransfer)))
                        .then(CommandManager.literal("role").executes(context -> openMenu(context, PartyMenu::openRole))))

                // --- consultations du groupe (phase 2) ---
                .then(CommandManager.literal("readycheck")
                        .executes(context -> run(context, player ->
                                RequestManager.startReadyCheck(player.getEntityWorld().getServer(), player))))

                .then(CommandManager.literal("countdown")
                        .then(CommandManager.literal("cancel")
                                .executes(context -> run(context, player ->
                                        RequestManager.cancelCountdown(player.getEntityWorld().getServer(), player))))
                        .then(CommandManager.argument("secondes", IntegerArgumentType.integer(1, 300))
                                .executes(context -> {
                                    int seconds = IntegerArgumentType.getInteger(context, "secondes");
                                    return run(context, player -> RequestManager.startCountdown(
                                            player.getEntityWorld().getServer(), player, seconds));
                                })))

                .then(CommandManager.literal("kickvote")
                        .then(CommandManager.argument("joueur", EntityArgumentType.player())
                                .executes(context -> {
                                    ServerPlayerEntity target = EntityArgumentType.getPlayer(context, "joueur");
                                    return run(context, player -> RequestManager.startKickVote(
                                            player.getEntityWorld().getServer(), player, target));
                                })))

                // Les deux commandes ci-dessous sont la cible des boutons de dialogue.
                // Elles portent l'identifiant de la demande : une reponse designe donc
                // explicitement ce a quoi elle repond, et le serveur refuse tout ce qui
                // n'est plus d'actualite (regle §20). Personne n'a a les taper.
                .then(CommandManager.literal("ready")
                        .then(CommandManager.argument("demande", UuidArgumentType.uuid())
                                .then(CommandManager.argument("reponse", StringArgumentType.word())
                                        .suggests((context, builder) ->
                                                CommandSource.suggestMatching(YES_NO, builder))
                                        .executes(context -> answer(context, true)))))

                .then(CommandManager.literal("vote")
                        .then(CommandManager.argument("demande", UuidArgumentType.uuid())
                                .then(CommandManager.argument("reponse", StringArgumentType.word())
                                        .suggests((context, builder) ->
                                                CommandSource.suggestMatching(YES_NO, builder))
                                        .executes(context -> answer(context, false)))))

                .then(CommandManager.literal("info")
                        .executes(context -> info(context.getSource())))

                .then(CommandManager.literal("disband")
                        .executes(context -> run(context, player ->
                                PartyManager.disband(player.getEntityWorld().getServer(), player))))

                .then(CommandManager.literal("chat")
                        .then(CommandManager.argument("message", StringArgumentType.greedyString())
                                .executes(context -> chat(context,
                                        StringArgumentType.getString(context, "message")))))

                // Reserve aux administrateurs : exerce les vrais chemins de code sur des
                // joueurs construits en memoire, donc pilotable depuis la console ou RCON,
                // la ou les commandes de groupe exigent un vrai joueur.
                .then(CommandManager.literal("diagnostic")
                        .requires(CommandManager.requirePermissionLevel(CommandManager.GAMEMASTERS_CHECK))
                        .executes(context -> PartyDiagnostic.run(context.getSource()))
                        // Les deux moities du test de persistance : poser un temoin,
                        // redemarrer le serveur, puis verifier qu'il a survecu (regle §39).
                        .then(CommandManager.literal("persist")
                                .executes(context -> PartyDiagnostic.plantWitness(context.getSource())))
                        .then(CommandManager.literal("hud")
                                .executes(context -> PartyDiagnostic.pushSampleHud(context.getSource(), true))
                                .then(CommandManager.literal("off")
                                        .executes(context -> PartyDiagnostic.pushSampleHud(context.getSource(), false))))
                        .then(CommandManager.literal("verify")
                                .executes(context -> PartyDiagnostic.verifyWitness(context.getSource())))
                        .then(CommandManager.literal("menu")
                                .executes(context -> PartyDiagnostic.menuSmoke(context.getSource())))
                        .then(CommandManager.literal("summon")
                                .executes(context -> PartyDiagnostic.summonTargeting(context.getSource()))));
    }

    /** {@code /p <message>} : le canal du groupe (regle §45). */
    private static LiteralArgumentBuilder<ServerCommandSource> buildChatShortcut() {
        return CommandManager.literal("p")
                .then(CommandManager.argument("message", StringArgumentType.greedyString())
                        .executes(context -> chat(context, StringArgumentType.getString(context, "message"))));
    }

    // --- rouages -------------------------------------------------------------

    /** Vocabulaire accepte comme reponse. Les boutons envoient toujours la premiere forme. */
    private static final List<String> YES_NO = List.of("oui", "non");

    /**
     * Traite la reponse a une consultation.
     *
     * @param readyCheck vrai pour une verification de preparation, faux pour un vote
     */
    private static int answer(CommandContext<ServerCommandSource> context, boolean readyCheck)
            throws CommandSyntaxException {
        ServerPlayerEntity player = context.getSource().getPlayerOrThrow();
        UUID requestId = UuidArgumentType.getUuid(context, "demande");
        String raw = StringArgumentType.getString(context, "reponse").toLowerCase(Locale.ROOT);
        boolean yes = raw.equals("oui") || raw.equals("yes") || raw.equals("true");

        Outcome outcome = readyCheck
                ? RequestManager.answerReady(player.getEntityWorld().getServer(), player, requestId, yes)
                : RequestManager.answerVote(player.getEntityWorld().getServer(), player, requestId, yes);
        context.getSource().sendFeedback(outcome::message, false);
        return outcome.ok() ? 1 : 0;
    }

    /** Ouvre une section du menu de groupe pour le joueur appelant (aucune commande visible pour lui). */
    private static int openMenu(CommandContext<ServerCommandSource> context,
                                BiConsumer<MinecraftServer, ServerPlayerEntity> opener) throws CommandSyntaxException {
        ServerPlayerEntity player = context.getSource().getPlayerOrThrow();
        opener.accept(player.getEntityWorld().getServer(), player);
        return 1;
    }

    private static int run(CommandContext<ServerCommandSource> context,
                           Function<ServerPlayerEntity, Outcome> action) throws CommandSyntaxException {
        ServerPlayerEntity player = context.getSource().getPlayerOrThrow();
        Outcome outcome = action.apply(player);
        context.getSource().sendFeedback(outcome::message, false);
        return outcome.ok() ? 1 : 0;
    }

    private static int chat(CommandContext<ServerCommandSource> context, String message)
            throws CommandSyntaxException {
        ServerPlayerEntity player = context.getSource().getPlayerOrThrow();
        Optional<Party> party = PartyManager.partyOf(player.getEntityWorld().getServer(), player.getUuid());
        if (party.isEmpty()) {
            context.getSource().sendFeedback(() -> Msg.bad("Vous n'etes dans aucun groupe."), false);
            return 0;
        }
        PartyManager.broadcast(player.getEntityWorld().getServer(), party.get(),
                Msg.chat(player.getName().getString(), message));
        return 1;
    }

    private static int info(ServerCommandSource source) throws CommandSyntaxException {
        ServerPlayerEntity player = source.getPlayerOrThrow();
        Optional<Party> maybeParty = PartyManager.partyOf(player.getEntityWorld().getServer(), player.getUuid());
        if (maybeParty.isEmpty()) {
            source.sendFeedback(() -> Msg.info("Vous n'etes dans aucun groupe. /party create pour en ouvrir un."),
                    false);
            return 0;
        }
        Party party = maybeParty.get();
        int max = PartyManager.maxSizeOf(party.type());

        source.sendFeedback(() -> Msg.heading("Groupe " + party.type().label()
                + " — " + party.size() + "/" + max), false);
        for (Party.MemberEntry entry : party.entries()) {
            UUID uuid = entry.uuid();
            ServerPlayerEntity member = player.getEntityWorld().getServer().getPlayerManager().getPlayer(uuid);
            String name = member != null ? member.getName().getString() : entry.lastKnownName();
            Text line = Msg.memberLine(entry.role().symbol(), entry.role().color(), name,
                    entry.role().label(), party.isLeader(uuid), member != null);
            source.sendFeedback(() -> line, false);
        }
        return 1;
    }
}
