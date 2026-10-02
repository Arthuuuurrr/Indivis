package net.hautecapitale.metiers.command;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.DoubleArgumentType;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.DynamicCommandExceptionType;
import com.mojang.brigadier.suggestion.SuggestionProvider;
import net.hautecapitale.metiers.api.Metiers;
import net.hautecapitale.metiers.config.MetiersConfig;
import net.hautecapitale.metiers.profession.Profession;
import net.hautecapitale.metiers.profession.ProfessionProgress;
import net.hautecapitale.metiers.profession.ProfessionsState;
import net.hautecapitale.metiers.profession.XpCurve;
import net.minecraft.command.CommandSource;
import net.minecraft.command.argument.EntityArgumentType;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;

import java.util.Locale;

/**
 * Commandes d'administration du socle.
 *
 * <p>Volontairement minimales : elles servent à tester l'étape 1, pas à jouer.
 * Les outils d'administration complets — nodes, carcasses, recettes — arrivent
 * avec leurs systèmes respectifs.
 */
public final class MetiersCommands {

    private static final DynamicCommandExceptionType METIER_INCONNU =
            new DynamicCommandExceptionType(id -> Text.literal("Métier inconnu : " + id));

    private static final SuggestionProvider<ServerCommandSource> PROFESSIONS =
            (context, builder) -> CommandSource.suggestMatching(Profession.ids(), builder);

    private MetiersCommands() {
    }

    public static void register(CommandDispatcher<ServerCommandSource> dispatcher) {
        dispatcher.register(CommandManager.literal("metiers")
                // Consultation : accessible à tous, sur soi-même.
                .then(CommandManager.literal("voir")
                        .executes(ctx -> showAll(ctx, ctx.getSource().getPlayerOrThrow()))
                        .then(CommandManager.argument("joueur", EntityArgumentType.player())
                                .requires(CommandManager.requirePermissionLevel(CommandManager.GAMEMASTERS_CHECK))
                                .executes(ctx -> showAll(ctx, EntityArgumentType.getPlayer(ctx, "joueur")))
                                .then(CommandManager.argument("metier", StringArgumentType.word())
                                        .suggests(PROFESSIONS)
                                        .executes(ctx -> showOne(ctx,
                                                EntityArgumentType.getPlayer(ctx, "joueur"),
                                                profession(ctx))))))

                // Administration.
                .then(CommandManager.literal("apprendre")
                        .requires(CommandManager.requirePermissionLevel(CommandManager.GAMEMASTERS_CHECK))
                        .then(CommandManager.argument("joueur", EntityArgumentType.player())
                                .then(CommandManager.argument("metier", StringArgumentType.word())
                                        .suggests(PROFESSIONS)
                                        .executes(ctx -> learn(ctx,
                                                EntityArgumentType.getPlayer(ctx, "joueur"),
                                                profession(ctx))))))

                .then(CommandManager.literal("oublier")
                        .requires(CommandManager.requirePermissionLevel(CommandManager.GAMEMASTERS_CHECK))
                        .then(CommandManager.argument("joueur", EntityArgumentType.player())
                                .then(CommandManager.argument("metier", StringArgumentType.word())
                                        .suggests(PROFESSIONS)
                                        .executes(ctx -> forget(ctx,
                                                EntityArgumentType.getPlayer(ctx, "joueur"),
                                                profession(ctx))))))

                .then(CommandManager.literal("xp")
                        .requires(CommandManager.requirePermissionLevel(CommandManager.GAMEMASTERS_CHECK))
                        .then(CommandManager.argument("joueur", EntityArgumentType.player())
                                .then(CommandManager.argument("metier", StringArgumentType.word())
                                        .suggests(PROFESSIONS)
                                        .then(CommandManager.argument("quantite", DoubleArgumentType.doubleArg(0.0D))
                                                .executes(ctx -> addXp(ctx,
                                                        EntityArgumentType.getPlayer(ctx, "joueur"),
                                                        profession(ctx),
                                                        DoubleArgumentType.getDouble(ctx, "quantite")))))))

                .then(CommandManager.literal("niveau")
                        .requires(CommandManager.requirePermissionLevel(CommandManager.GAMEMASTERS_CHECK))
                        .then(CommandManager.argument("joueur", EntityArgumentType.player())
                                .then(CommandManager.argument("metier", StringArgumentType.word())
                                        .suggests(PROFESSIONS)
                                        .then(CommandManager.argument("valeur", IntegerArgumentType.integer(1))
                                                .executes(ctx -> setLevel(ctx,
                                                        EntityArgumentType.getPlayer(ctx, "joueur"),
                                                        profession(ctx),
                                                        IntegerArgumentType.getInteger(ctx, "valeur")))))))

                .then(CommandManager.literal("reinitialiser")
                        .requires(CommandManager.requirePermissionLevel(CommandManager.GAMEMASTERS_CHECK))
                        .then(CommandManager.argument("joueur", EntityArgumentType.player())
                                .executes(ctx -> resetAll(ctx, EntityArgumentType.getPlayer(ctx, "joueur")))
                                .then(CommandManager.argument("metier", StringArgumentType.word())
                                        .suggests(PROFESSIONS)
                                        .executes(ctx -> reset(ctx,
                                                EntityArgumentType.getPlayer(ctx, "joueur"),
                                                profession(ctx))))))

                .then(CommandManager.literal("recharger")
                        .requires(CommandManager.requirePermissionLevel(CommandManager.ADMINS_CHECK))
                        .executes(MetiersCommands::reload))

                .then(CommandManager.literal("diagnostic")
                        .requires(CommandManager.requirePermissionLevel(CommandManager.ADMINS_CHECK))
                        .executes(MetiersCommands::diagnostic))

                .then(DataCommands.inspecter())

                .then(RoleCommands.atelier())

                .then(RecipeCommands.fabriquer())

                .then(NodeCommands.node())

                .then(HuntCommands.origine())

                .then(HuntCommands.abattre())

                .then(SkinCommands.carcasse())

                .then(SkinCommands.depecer())

                .then(RepairCommands.reparer())

                .then(RepairCommands.briser())

                .then(GadgetCommands.foyer())
                .then(HearthCommands.auberge())

                .then(GadgetCommands.gadget())

                .then(MealCommands.repas()));
    }

    // ------------------------------------------------------------------

    private static Profession profession(CommandContext<ServerCommandSource> ctx) throws CommandSyntaxException {
        String id = StringArgumentType.getString(ctx, "metier");
        Profession profession = Profession.byId(id);
        if (profession == null) {
            throw METIER_INCONNU.create(id);
        }
        return profession;
    }

    private static String display(Profession profession) {
        String id = profession.getId().replace('_', ' ');
        return Character.toUpperCase(id.charAt(0)) + id.substring(1);
    }

    private static String name(ServerPlayerEntity player) {
        return player.getName().getString();
    }

    private static int showAll(CommandContext<ServerCommandSource> ctx, ServerPlayerEntity player) {
        ProfessionsState state = Metiers.getState(player);
        ServerCommandSource source = ctx.getSource();

        if (state.count() == 0) {
            source.sendFeedback(() -> Text.literal(name(player) + " n'a aucun métier.")
                    .formatted(Formatting.GRAY), false);
            return 0;
        }

        source.sendFeedback(() -> Text.literal("Métiers de " + name(player) + " :")
                .formatted(Formatting.GOLD), false);
        for (Profession profession : Profession.values()) {
            ProfessionProgress progress = state.get(profession);
            if (progress == null) {
                continue;
            }
            double required = XpCurve.xpToNextLevel(progress.level());
            String xpPart = Double.isInfinite(required)
                    ? "niveau maximum"
                    : String.format(Locale.ROOT, "%.0f / %.0f XP", progress.xp(), required);
            source.sendFeedback(() -> Text.literal(String.format(Locale.ROOT,
                    "  %-22s niveau %2d  [%s]  %s",
                    display(profession), progress.level(), progress.rank().name(), xpPart))
                    .formatted(Formatting.WHITE), false);
        }
        return state.count();
    }

    private static int showOne(CommandContext<ServerCommandSource> ctx, ServerPlayerEntity player,
                               Profession profession) {
        ProfessionProgress progress = Metiers.getState(player).get(profession);
        ServerCommandSource source = ctx.getSource();
        if (progress == null) {
            source.sendFeedback(() -> Text.literal(
                    name(player) + " n'a pas le métier " + display(profession) + ".")
                    .formatted(Formatting.GRAY), false);
            return 0;
        }
        double required = XpCurve.xpToNextLevel(progress.level());
        String xpPart = Double.isInfinite(required)
                ? "niveau maximum atteint"
                : String.format(Locale.ROOT, "%.0f / %.0f XP (reste %.0f)",
                        progress.xp(), required, required - progress.xp());
        source.sendFeedback(() -> Text.literal(String.format(Locale.ROOT,
                "%s — %s : niveau %d [%s], %s",
                name(player), display(profession),
                progress.level(), progress.rank().name(), xpPart))
                .formatted(Formatting.WHITE), false);
        return progress.level();
    }

    private static int learn(CommandContext<ServerCommandSource> ctx, ServerPlayerEntity player,
                             Profession profession) {
        boolean done = Metiers.learn(player, profession);
        ctx.getSource().sendFeedback(() -> done
                ? Text.literal(name(player) + " apprend " + display(profession) + ".")
                        .formatted(Formatting.GREEN)
                : Text.literal("Impossible : métier déjà appris, ou plafond de métiers atteint.")
                        .formatted(Formatting.RED), true);
        return done ? 1 : 0;
    }

    private static int forget(CommandContext<ServerCommandSource> ctx, ServerPlayerEntity player,
                              Profession profession) {
        boolean done = Metiers.forget(player, profession);
        ctx.getSource().sendFeedback(() -> done
                ? Text.literal(name(player) + " oublie " + display(profession) + ".")
                        .formatted(Formatting.YELLOW)
                : Text.literal("Ce joueur n'a pas ce métier.").formatted(Formatting.RED), true);
        return done ? 1 : 0;
    }

    private static int addXp(CommandContext<ServerCommandSource> ctx, ServerPlayerEntity player,
                             Profession profession, double amount) {
        if (!Metiers.hasProfession(player, profession)) {
            ctx.getSource().sendFeedback(
                    () -> Text.literal("Ce joueur n'a pas ce métier — utilisez d'abord « apprendre ».")
                            .formatted(Formatting.RED), false);
            return 0;
        }
        int gained = Metiers.addXp(player, profession, amount);
        int level = Metiers.getLevel(player, profession);
        ctx.getSource().sendFeedback(() -> Text.literal(String.format(Locale.ROOT,
                "%s : +%.0f XP en %s — niveau %d%s",
                name(player), amount, display(profession), level,
                gained > 0 ? " (+" + gained + " niveau" + (gained > 1 ? "x)" : ")") : ""))
                .formatted(gained > 0 ? Formatting.GREEN : Formatting.WHITE), true);
        return gained;
    }

    private static int setLevel(CommandContext<ServerCommandSource> ctx, ServerPlayerEntity player,
                                Profession profession, int level) {
        boolean done = Metiers.setLevel(player, profession, level);
        ctx.getSource().sendFeedback(() -> done
                ? Text.literal(String.format(Locale.ROOT, "%s — %s : niveau %d [%s]",
                        name(player), display(profession), level,
                        Metiers.getRank(player, profession).name())).formatted(Formatting.GREEN)
                : Text.literal("Niveau hors bornes : 1 à " + MetiersConfig.get().niveau_max + ".")
                        .formatted(Formatting.RED), true);
        return done ? level : 0;
    }

    private static int reset(CommandContext<ServerCommandSource> ctx, ServerPlayerEntity player,
                             Profession profession) {
        boolean done = Metiers.reset(player, profession);
        ctx.getSource().sendFeedback(() -> done
                ? Text.literal(display(profession) + " remis au niveau 1 pour "
                        + name(player) + ".").formatted(Formatting.YELLOW)
                : Text.literal("Ce joueur n'a pas ce métier.").formatted(Formatting.RED), true);
        return done ? 1 : 0;
    }

    private static int resetAll(CommandContext<ServerCommandSource> ctx, ServerPlayerEntity player) {
        Metiers.resetAll(player);
        ctx.getSource().sendFeedback(() -> Text.literal(
                "Tous les métiers de " + name(player) + " ont été effacés.")
                .formatted(Formatting.YELLOW), true);
        return 1;
    }

    /**
     * Exerce les chemins de code que les tests hors du jeu ne peuvent pas
     * atteindre : persistance sur disque, copie à la mort, changement de
     * dimension. Utile pour vérifier une mise à jour du serveur sans avoir à
     * refaire le parcours à la main.
     */
    private static int diagnostic(CommandContext<ServerCommandSource> ctx) {
        ServerCommandSource source = ctx.getSource();
        java.util.List<String> report;
        try {
            report = SocleDiagnostic.run(source.getServer());
        } catch (RuntimeException | Error e) {
            // Une exception non prévue serait avalée par Brigadier en « unexpected
            // error » : on la journalise et on la montre, avec sa première trace.
            net.hautecapitale.metiers.HauteCapitaleMetiers.LOGGER.error("Diagnostic interrompu", e);
            StackTraceElement[] trace = e.getStackTrace();
            source.sendError(Text.literal("Diagnostic interrompu : " + e
                    + (trace.length > 0 ? " — " + trace[0] : "")));
            return 0;
        }
        SocleDiagnostic.summarize(report, line -> {
            Formatting color = line.startsWith("  FAIL") ? Formatting.RED
                    : line.startsWith("  PASS") ? Formatting.GREEN
                    : line.startsWith("  SAUT") || line.startsWith("  WAIT") ? Formatting.YELLOW
                    : line.startsWith("---") ? Formatting.GOLD
                    : Formatting.WHITE;
            source.sendFeedback(() -> Text.literal(line).formatted(color), false);
        });
        return (int) report.stream().filter(line -> line.startsWith("  FAIL")).count() == 0 ? 1 : 0;
    }

    private static int reload(CommandContext<ServerCommandSource> ctx) {
        MetiersConfig.load();
        XpCurve.invalidate();
        ctx.getSource().sendFeedback(() -> Text.literal(String.format(Locale.ROOT,
                "Configuration rechargée — niveau max %d, courbe %.0f × N^%.2f",
                MetiersConfig.get().niveau_max, MetiersConfig.get().xp_base, MetiersConfig.get().xp_exposant))
                .formatted(Formatting.GREEN), true);

        // Puis les fichiers de contenu, qui vivent dans les datapacks : une seule
        // commande remet le serveur d'aplomb après une retouche.
        DataCommands.reloadDataPacks(ctx.getSource());
        return 1;
    }
}
