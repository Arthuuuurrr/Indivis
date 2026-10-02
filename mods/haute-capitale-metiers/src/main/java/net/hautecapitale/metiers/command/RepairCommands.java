package net.hautecapitale.metiers.command;

import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import net.hautecapitale.metiers.npc.RoleGate;
import net.hautecapitale.metiers.repair.Breakage;
import net.hautecapitale.metiers.repair.RepairData;
import net.hautecapitale.metiers.repair.RepairEngine;
import net.hautecapitale.metiers.repair.RepairFeedback;
import net.minecraft.command.argument.EntityArgumentType;
import net.minecraft.item.ItemStack;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;

import java.util.Locale;

/**
 * {@code /metiers reparer …} et {@code /metiers briser …} — la réparation par
 * commande, pour la tester sans client, et briser l'objet en main d'un joueur
 * pour voir ce qu'un objet brisé devient.
 */
public final class RepairCommands {

    private RepairCommands() {
    }

    public static LiteralArgumentBuilder<ServerCommandSource> reparer() {
        return CommandManager.literal("reparer")
                .requires(CommandManager.requirePermissionLevel(CommandManager.GAMEMASTERS_CHECK))
                .then(CommandManager.argument("joueur", EntityArgumentType.player())
                        .executes(RepairCommands::list)
                        .then(CommandManager.literal("tout")
                                .executes(ctx -> repair(ctx, -1)))
                        .then(CommandManager.argument("emplacement", IntegerArgumentType.integer(0))
                                .executes(ctx -> repair(ctx, IntegerArgumentType.getInteger(ctx, "emplacement")))));
    }

    public static LiteralArgumentBuilder<ServerCommandSource> briser() {
        return CommandManager.literal("briser")
                .requires(CommandManager.requirePermissionLevel(CommandManager.GAMEMASTERS_CHECK))
                .then(CommandManager.argument("joueur", EntityArgumentType.player())
                        .executes(RepairCommands::breakHeld));
    }

    // ------------------------------------------------------------------

    /** Sans emplacement : ce que l'onglet Réparation montrerait à ce joueur. */
    private static int list(CommandContext<ServerCommandSource> ctx) throws CommandSyntaxException {
        ServerPlayerEntity player = EntityArgumentType.getPlayer(ctx, "joueur");
        RepairData data = RepairEngine.describe(player);
        ServerCommandSource source = ctx.getSource();
        source.sendFeedback(() -> Text.literal(player.getName().getString() + " — solde " + data.balance() + " ")
                .append(data.currencyName()).append(" ; " + data.items().size() + " objet(s) réparable(s), total "
                        + data.total() + " :").formatted(Formatting.GOLD), false);
        for (RepairData.RepairEntry item : data.items()) {
            source.sendFeedback(() -> Text.literal(String.format(Locale.ROOT, "  emplacement %2d  ", item.slot()))
                    .append(item.label()).append(Text.literal(String.format(Locale.ROOT, "  %s%d %%  %d",
                            item.broken() ? "BRISÉ, " : "", item.percent(), item.price()))).formatted(Formatting.WHITE), false);
        }
        return data.items().size();
    }

    private static int repair(CommandContext<ServerCommandSource> ctx, int slot) throws CommandSyntaxException {
        ServerPlayerEntity player = EntityArgumentType.getPlayer(ctx, "joueur");
        RepairEngine.Outcome outcome = slot < 0 ? RepairEngine.repairAll(player) : RepairEngine.repair(player, slot);
        Text text = RepairFeedback.describe(outcome);
        if (player.networkHandler != null) {
            player.sendMessage(text, false);
        }
        ctx.getSource().sendFeedback(() -> Text.literal(player.getName().getString() + " : ").append(text), false);
        RoleGate.refresh(player);
        return outcome.succeeded() ? outcome.repaired() : 0;
    }

    private static int breakHeld(CommandContext<ServerCommandSource> ctx) throws CommandSyntaxException {
        ServerPlayerEntity player = EntityArgumentType.getPlayer(ctx, "joueur");
        ItemStack held = player.getMainHandStack();
        if (held.isEmpty() || !held.isDamageable()) {
            ctx.getSource().sendError(Text.literal("L'objet en main n'a pas de durabilité."));
            return 0;
        }
        if (!Breakage.protects(held)) {
            ctx.getSource().sendError(Text.literal("Cet objet ne se brise pas : il n'est pas dans"
                    + " #haute_capitale_metiers:reparable_si_brise, ou il est en exception."));
            return 0;
        }
        Breakage.intercept(held, held.getMaxDamage(), player);
        RoleGate.refresh(player);
        ctx.getSource().sendFeedback(() -> Text.literal("Brisé : ").append(held.getName())
                .append(" — prix de réparation " + RepairEngine.price(held) + ".").formatted(Formatting.YELLOW), false);
        return 1;
    }
}
