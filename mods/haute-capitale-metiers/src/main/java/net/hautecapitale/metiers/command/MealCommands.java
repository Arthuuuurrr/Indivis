package net.hautecapitale.metiers.command;

import com.mojang.brigadier.arguments.DoubleArgumentType;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import net.hautecapitale.metiers.config.MetiersConfig;
import net.hautecapitale.metiers.data.HcmData;
import net.hautecapitale.metiers.meal.MealAttachment;
import net.hautecapitale.metiers.meal.MealBuff;
import net.hautecapitale.metiers.meal.MealConsumeEffect;
import net.hautecapitale.metiers.meal.MealEngine;
import net.minecraft.command.argument.EntityArgumentType;
import net.minecraft.command.argument.IdentifierArgumentType;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.Identifier;

/**
 * {@code /metiers repas …} — le buff de repas d'un joueur par commande : le
 * voir, le poser sans plat, le retirer. Pour tester sans cuisiner, et pour
 * l'administrateur qui veut offrir un festin.
 */
public final class MealCommands {

    private MealCommands() {
    }

    public static LiteralArgumentBuilder<ServerCommandSource> repas() {
        return CommandManager.literal("repas")
                .requires(CommandManager.requirePermissionLevel(CommandManager.GAMEMASTERS_CHECK))
                .then(CommandManager.argument("joueur", EntityArgumentType.player())
                        .executes(MealCommands::info)
                        .then(CommandManager.literal("effacer").executes(MealCommands::clear))
                        .then(CommandManager.argument("buff", IdentifierArgumentType.identifier())
                                .suggests((ctx, builder) -> {
                                    HcmData.BUFFS.ids().forEach(id -> builder.suggest(id.toString()));
                                    return builder.buildFuture();
                                })
                                .executes(ctx -> apply(ctx, -1.0D, -1))
                                .then(CommandManager.argument("valeur", DoubleArgumentType.doubleArg(-10.0D, 10.0D))
                                        .executes(ctx -> apply(ctx, DoubleArgumentType.getDouble(ctx, "valeur"), -1))
                                        .then(CommandManager.argument("secondes", IntegerArgumentType.integer(1))
                                                .executes(ctx -> apply(ctx, DoubleArgumentType.getDouble(ctx, "valeur"),
                                                        IntegerArgumentType.getInteger(ctx, "secondes")))))));
    }

    private static int info(CommandContext<ServerCommandSource> ctx) throws CommandSyntaxException {
        ServerPlayerEntity player = EntityArgumentType.getPlayer(ctx, "joueur");
        MealAttachment current = MealAttachment.of(player);
        String name = player.getName().getString();
        if (current == null) {
            ctx.getSource().sendFeedback(() -> Text.literal(name + " n'a pas de repas en cours.").formatted(Formatting.GRAY), false);
            return 0;
        }
        MealBuff buff = HcmData.BUFFS.get(current.buff());
        int remaining = current.remainingSeconds(MealEngine.now());
        Text detail = buff == null ? Text.literal(current.buff().toString())
                : MealEngine.describe(buff.title().orElse(current.buff().getPath()), buff.attribute(), buff.operation(),
                        current.value(), remaining);
        ctx.getSource().sendFeedback(() -> Text.literal(name + " — repas : ").append(detail)
                .append(Text.literal("  (" + remaining + " s restantes)")).formatted(Formatting.GOLD), false);
        return 1;
    }

    private static int apply(CommandContext<ServerCommandSource> ctx, double value, int seconds) throws CommandSyntaxException {
        ServerPlayerEntity player = EntityArgumentType.getPlayer(ctx, "joueur");
        Identifier buffId = IdentifierArgumentType.getIdentifier(ctx, "buff");
        MealBuff buff = HcmData.BUFFS.get(buffId);
        if (buff == null) {
            ctx.getSource().sendError(Text.literal("Buff inconnu : " + buffId + " — voir « /metiers inspecter resume »."));
            return 0;
        }
        double applied = value == -1.0D ? buff.value() : value;
        int duration = seconds > 0 ? seconds : buff.seconds().orElse(MetiersConfig.get().repas.duree_secondes);
        MealEngine.apply(player, buffId, applied, duration);
        MealConsumeEffect effect = MealConsumeEffect.of(buffId, buff, applied, duration);
        Text detail = MealEngine.describe(effect, applied);
        if (player.networkHandler != null) {
            player.sendMessage(detail.copy().formatted(Formatting.GOLD), true);
        }
        ctx.getSource().sendFeedback(() -> Text.literal(player.getName().getString() + " : ").append(detail).formatted(Formatting.GOLD), false);
        return 1;
    }

    private static int clear(CommandContext<ServerCommandSource> ctx) throws CommandSyntaxException {
        ServerPlayerEntity player = EntityArgumentType.getPlayer(ctx, "joueur");
        MealEngine.clear(player);
        ctx.getSource().sendFeedback(() -> Text.literal(player.getName().getString() + " : repas retiré.").formatted(Formatting.GRAY), false);
        return 1;
    }
}
