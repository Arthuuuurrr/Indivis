package net.hautecapitale.rpg.command;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.DynamicCommandExceptionType;
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType;
import com.mojang.brigadier.suggestion.SuggestionProvider;
import net.hautecapitale.rpg.api.Classes;
import net.hautecapitale.rpg.config.RpgConfig;
import net.hautecapitale.rpg.rpgclass.RpgClass;
import net.minecraft.command.CommandSource;
import net.minecraft.command.argument.EntityArgumentType;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;

/**
 * Commandes du noyau.
 *
 * <p>Deux niveaux : la consultation et le choix, ouverts au joueur ; l'attribution
 * et le niveau, réservés aux MJ. Rien ici ne touche au contenu des mods de classe.
 */
public final class RpgCommands {

    private static final DynamicCommandExceptionType CLASSE_INCONNUE =
            new DynamicCommandExceptionType(id -> Text.literal("Classe inconnue : " + id));

    private static final SimpleCommandExceptionType CHOIX_FERME =
            new SimpleCommandExceptionType(Text.literal(
                    "Le choix libre de classe est désactivé sur ce serveur."));

    private static final SimpleCommandExceptionType DEJA_UNE_CLASSE =
            new SimpleCommandExceptionType(Text.literal(
                    "Tu as déjà une classe, et le changement est désactivé."));

    private static final SuggestionProvider<ServerCommandSource> CLASSES =
            (context, builder) -> CommandSource.suggestMatching(RpgClass.ids(), builder);

    private RpgCommands() {
    }

    public static void register(CommandDispatcher<ServerCommandSource> dispatcher) {
        dispatcher.register(CommandManager.literal("rpg")

                .then(CommandManager.literal("classes")
                        .executes(RpgCommands::listClasses))

                .then(CommandManager.literal("voir")
                        .executes(ctx -> show(ctx, ctx.getSource().getPlayerOrThrow()))
                        .then(CommandManager.argument("joueur", EntityArgumentType.player())
                                .requires(CommandManager.requirePermissionLevel(
                                        CommandManager.GAMEMASTERS_CHECK))
                                .executes(ctx -> show(ctx,
                                        EntityArgumentType.getPlayer(ctx, "joueur")))))

                .then(CommandManager.literal("choisir")
                        .then(CommandManager.argument("classe", StringArgumentType.word())
                                .suggests(CLASSES)
                                .executes(RpgCommands::choose)))

                .then(CommandManager.literal("attribuer")
                        .requires(CommandManager.requirePermissionLevel(
                                CommandManager.GAMEMASTERS_CHECK))
                        .then(CommandManager.argument("joueur", EntityArgumentType.player())
                                .then(CommandManager.argument("classe", StringArgumentType.word())
                                        .suggests(CLASSES)
                                        .executes(ctx -> assign(ctx,
                                                EntityArgumentType.getPlayer(ctx, "joueur"),
                                                rpgClass(ctx))))))

                .then(CommandManager.literal("retirer")
                        .requires(CommandManager.requirePermissionLevel(
                                CommandManager.GAMEMASTERS_CHECK))
                        .then(CommandManager.argument("joueur", EntityArgumentType.player())
                                .executes(ctx -> clear(ctx,
                                        EntityArgumentType.getPlayer(ctx, "joueur")))))
);
    }

    // MARK: consultation

    private static int listClasses(CommandContext<ServerCommandSource> ctx) {
        ctx.getSource().sendFeedback(() -> Text.literal("Classes de Haute Capitale")
                .formatted(Formatting.GOLD), false);
        for (RpgClass rpgClass : RpgClass.values()) {
            ctx.getSource().sendFeedback(() -> Text.literal("  " + rpgClass.getId())
                    .formatted(Formatting.WHITE)
                    .append(Text.literal("  " + rpgClass.archetype().name().toLowerCase()
                                    + "  ·  " + String.join(", ", rpgClass.namespaces()))
                            .formatted(Formatting.DARK_GRAY)), false);
        }
        return RpgClass.values().length;
    }

    private static int show(CommandContext<ServerCommandSource> ctx, ServerPlayerEntity player) {
        var state = Classes.stateOf(player);
        if (!state.hasClass()) {
            ctx.getSource().sendFeedback(() -> Text.literal(player.getNameForScoreboard())
                    .append(Text.literal(" n'a pas de classe.").formatted(Formatting.GRAY)), false);
            return 0;
        }
        RpgClass rpgClass = state.rpgClass().orElseThrow();
        ctx.getSource().sendFeedback(() -> Text.literal(player.getNameForScoreboard() + " — ")
                .append(Text.literal(rpgClass.getId()).formatted(Formatting.GOLD)), false);
        ctx.getSource().sendFeedback(() -> Text.literal("  écoles : "
                + String.join(", ", rpgClass.schools())).formatted(Formatting.DARK_GRAY), false);
        return 1;
    }

    // MARK: choix du joueur

    private static int choose(CommandContext<ServerCommandSource> ctx) throws CommandSyntaxException {
        ServerPlayerEntity player = ctx.getSource().getPlayerOrThrow();
        RpgClass rpgClass = rpgClass(ctx);

        if (!RpgConfig.get().choix_libre) {
            throw CHOIX_FERME.create();
        }
        if (Classes.stateOf(player).hasClass() && !RpgConfig.get().changement_autorise) {
            throw DEJA_UNE_CLASSE.create();
        }

        Classes.set(player, rpgClass);
        ctx.getSource().sendFeedback(() -> Text.literal("Tu es désormais ")
                .append(Text.literal(rpgClass.getId()).formatted(Formatting.GOLD))
                .append(Text.literal(".")), false);
        return 1;
    }

    // MARK: administration

    private static int assign(CommandContext<ServerCommandSource> ctx,
                              ServerPlayerEntity player, RpgClass rpgClass) {
        Classes.set(player, rpgClass);
        ctx.getSource().sendFeedback(() -> Text.literal(player.getNameForScoreboard()
                + " devient " + rpgClass.getId() + "."), true);
        return 1;
    }

    private static int clear(CommandContext<ServerCommandSource> ctx, ServerPlayerEntity player) {
        Classes.clear(player);
        ctx.getSource().sendFeedback(() -> Text.literal("Classe retirée à "
                + player.getNameForScoreboard() + "."), true);
        return 1;
    }

    // MARK: outils

    private static RpgClass rpgClass(CommandContext<ServerCommandSource> ctx)
            throws CommandSyntaxException {
        String id = StringArgumentType.getString(ctx, "classe");
        RpgClass rpgClass = RpgClass.byId(id);
        if (rpgClass == null) {
            throw CLASSE_INCONNUE.create(id);
        }
        return rpgClass;
    }
}
