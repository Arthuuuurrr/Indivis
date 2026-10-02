package net.hautecapitale.rpg.ability;

import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import net.minecraft.command.CommandSource;
import net.minecraft.command.argument.EntityArgumentType;
import net.minecraft.command.argument.IdentifierArgumentType;
import net.minecraft.item.ItemStack;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.Identifier;

import java.util.function.BiFunction;

/**
 * Les commandes de diagnostic.
 *
 * <p>Sur un arbre de plusieurs dizaines de capacités, « elle n'apparaît pas » est une
 * plainte qu'on ne peut pas instruire sans outil. Ces commandes existent d'abord pour
 * répondre à la question <i>pourquoi</i>, capacité par capacité, plutôt que d'obliger à
 * relire des fichiers pour le deviner.
 *
 * <p>Consulter son propre état est ouvert à tous ; consulter ou forcer celui d'un autre
 * est réservé aux MJ, au même niveau que le reste des commandes du noyau.
 */
public final class AbilityCommands {

    private AbilityCommands() {
    }

    public static LiteralArgumentBuilder<ServerCommandSource> build() {
        return CommandManager.literal("capacites")
                .then(sub("connues", AbilityCommands::listKnown))
                .then(sub("disponibles", AbilityCommands::listAvailable))
                .then(sub("diagnostic", AbilityCommands::diagnose))
                .then(sub("arme", AbilityCommands::inspectWeapon))
                .then(CommandManager.literal("sync")
                        .requires(CommandManager.requirePermissionLevel(CommandManager.GAMEMASTERS_CHECK))
                        .executes(ctx -> resync(ctx, ctx.getSource().getPlayerOrThrow()))
                        .then(CommandManager.argument("joueur", EntityArgumentType.player())
                                .executes(ctx -> resync(ctx,
                                        EntityArgumentType.getPlayer(ctx, "joueur")))))
                .then(CommandManager.literal("rapport")
                        .executes(AbilityCommands::report))
                .then(grantCommand("accorder", true))
                .then(grantCommand("reprendre", false));
    }

    /**
     * Accorde ou reprend une capacité <b>dans le cache seulement</b>, pour les essais.
     *
     * <p>Rien n'est écrit sur disque et rien ne remonte à Pufferfish : la moindre
     * reconnexion efface ce qui a été accordé ici, puisque l'arrivée d'un joueur repart
     * de l'arbre. C'est voulu — cela permet d'éprouver la chaîne complète sans toucher à
     * l'arbre de production, et cela démontre au passage que l'arbre reste bien la seule
     * source de vérité.
     */
    private static LiteralArgumentBuilder<ServerCommandSource> grantCommand(String name, boolean grant) {
        return CommandManager.literal(name)
                .requires(CommandManager.requirePermissionLevel(CommandManager.GAMEMASTERS_CHECK))
                .then(CommandManager.argument("joueur", EntityArgumentType.player())
                        .then(CommandManager.argument("capacite", IdentifierArgumentType.identifier())
                                .suggests((context, builder) -> CommandSource.suggestIdentifiers(
                                        AbilityRegistry.all().keySet(), builder))
                                .executes(ctx -> {
                                    ServerPlayerEntity target = EntityArgumentType.getPlayer(ctx, "joueur");
                                    Identifier id = IdentifierArgumentType.getIdentifier(ctx, "capacite");
                                    if (AbilityRegistry.get(id) == null) {
                                        ctx.getSource().sendError(
                                                Text.literal("Capacité inconnue : " + id));
                                        return 0;
                                    }
                                    if (grant) {
                                        LearnedAbilities.grant(target, java.util.List.of(id));
                                    } else {
                                        LearnedAbilities.revoke(target, java.util.List.of(id));
                                    }
                                    AbilitySync.rebuild(target);
                                    ctx.getSource().sendFeedback(() -> Text.literal(
                                            (grant ? "Accordé " : "Repris ") + id + " à "
                                                    + target.getNameForScoreboard())
                                            .append(Text.literal("  (volatile — Pufferfish reste la référence)")
                                                    .formatted(Formatting.DARK_GRAY)), true);
                                    return 1;
                                })));
    }

    /** Une sous-commande qui agit sur soi-même sans permission, sur autrui avec. */
    private static LiteralArgumentBuilder<ServerCommandSource> sub(
            String name,
            BiFunction<CommandContext<ServerCommandSource>, ServerPlayerEntity, Integer> action) {
        return CommandManager.literal(name)
                .executes(ctx -> action.apply(ctx, ctx.getSource().getPlayerOrThrow()))
                .then(CommandManager.argument("joueur", EntityArgumentType.player())
                        .requires(CommandManager.requirePermissionLevel(CommandManager.GAMEMASTERS_CHECK))
                        .executes(ctx -> action.apply(ctx, player(ctx))));
    }

    private static ServerPlayerEntity player(CommandContext<ServerCommandSource> ctx) {
        try {
            return EntityArgumentType.getPlayer(ctx, "joueur");
        } catch (CommandSyntaxException e) {
            throw new IllegalStateException(e);
        }
    }

    // MARK: consultation

    private static int listKnown(CommandContext<ServerCommandSource> ctx, ServerPlayerEntity player) {
        var known = AbilityResolver.known(player);
        header(ctx, player, "connaît " + known.size() + " capacité(s)");
        for (var status : known) {
            ctx.getSource().sendFeedback(() -> Text.literal("  " + status.id())
                    .formatted(status.available() ? Formatting.WHITE : Formatting.DARK_GRAY)
                    .append(Text.literal("  " + status.definition().spell())
                            .formatted(Formatting.DARK_GRAY)), false);
        }
        return known.size();
    }

    private static int listAvailable(CommandContext<ServerCommandSource> ctx, ServerPlayerEntity player) {
        var available = AbilityResolver.statuses(player).stream()
                .filter(AbilityStatus::available).toList();
        header(ctx, player, available.size() + " capacité(s) utilisable(s) maintenant");
        for (var status : available) {
            ctx.getSource().sendFeedback(() -> Text.literal("  " + status.id())
                    .formatted(Formatting.GOLD), false);
        }
        return available.size();
    }

    /** La sortie explicable : les trois réponses séparées, et la première qui bloque. */
    private static int diagnose(CommandContext<ServerCommandSource> ctx, ServerPlayerEntity player) {
        var statuses = AbilityResolver.statuses(player);
        header(ctx, player, statuses.size() + " capacité(s) définie(s)");
        for (var status : statuses) {
            Formatting colour = status.available() ? Formatting.GREEN
                    : (status.learned() ? Formatting.YELLOW : Formatting.DARK_GRAY);
            ctx.getSource().sendFeedback(() -> Text.literal("  " + status.id())
                    .formatted(colour)
                    .append(Text.literal("  définie:" + oui(status.defined())
                                    + " apprise:" + oui(status.learned())
                                    + " arme:" + oui(status.weaponCompatible()))
                            .formatted(Formatting.DARK_GRAY)), false);
            if (!status.available()) {
                ctx.getSource().sendFeedback(() -> Text.literal("      " + status.reason())
                        .formatted(Formatting.DARK_GRAY), false);
            }
        }
        return statuses.size();
    }

    private static int inspectWeapon(CommandContext<ServerCommandSource> ctx, ServerPlayerEntity player) {
        ItemStack stack = player.getMainHandStack();
        header(ctx, player, "arme en main principale");
        ctx.getSource().sendFeedback(() -> Text.literal("  objet : ")
                .append(Text.literal(stack.isEmpty() ? "aucun"
                                : stack.getRegistryEntry().getKey()
                                        .map(key -> key.getValue().toString()).orElse("?"))
                        .formatted(Formatting.GOLD)), false);
        var matching = AbilityResolver.statuses(player).stream()
                .filter(AbilityStatus::weaponCompatible).toList();
        ctx.getSource().sendFeedback(() -> Text.literal(
                "  capacités que cette arme accepte : " + matching.size())
                .formatted(Formatting.DARK_GRAY), false);
        for (var status : matching) {
            ctx.getSource().sendFeedback(() -> Text.literal("    " + status.id()
                            + (status.learned() ? "" : "  (non apprise)"))
                    .formatted(status.available() ? Formatting.WHITE : Formatting.DARK_GRAY), false);
        }
        return matching.size();
    }

    private static int report(CommandContext<ServerCommandSource> ctx) {
        var report = AbilityRegistry.report();
        ctx.getSource().sendFeedback(() -> Text.literal("Catalogue des capacités")
                .formatted(Formatting.GOLD), false);
        ctx.getSource().sendFeedback(() -> Text.literal(
                "  lues : " + report.loaded()
                        + "   valides : " + report.valid()
                        + "   avertissements : " + report.warnings().size()
                        + "   sorts manquants : " + report.missingSpells().size()), false);
        report.warnings().forEach(warning -> ctx.getSource().sendFeedback(
                () -> Text.literal("  ! " + warning).formatted(Formatting.YELLOW), false));
        report.missingSpells().forEach(id -> ctx.getSource().sendFeedback(
                () -> Text.literal("  ? sort absent pour " + id).formatted(Formatting.RED), false));
        return report.valid();
    }

    // MARK: action

    private static int resync(CommandContext<ServerCommandSource> ctx, ServerPlayerEntity player) {
        boolean changed = AbilitySync.rebuild(player);
        ctx.getSource().sendFeedback(() -> Text.literal(
                changed ? "Capacités reconstruites pour " + player.getNameForScoreboard() + "."
                        : "Rien à changer pour " + player.getNameForScoreboard() + "."), true);
        return changed ? 1 : 0;
    }

    // MARK: présentation

    private static void header(CommandContext<ServerCommandSource> ctx,
                               ServerPlayerEntity player, String what) {
        ctx.getSource().sendFeedback(() -> Text.literal(player.getNameForScoreboard() + " — ")
                .append(Text.literal(what).formatted(Formatting.GOLD)), false);
    }

    private static String oui(boolean value) {
        return value ? "oui" : "non";
    }
}
