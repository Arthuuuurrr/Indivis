package net.hautecapitale.metiers.command;

import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import net.hautecapitale.metiers.HauteCapitaleMetiers;
import net.hautecapitale.metiers.data.HcmData;
import net.hautecapitale.metiers.hearth.HearthAttachment;
import net.hautecapitale.metiers.hearth.HearthLink;
import net.hautecapitale.metiers.npc.NpcRole;
import net.minecraft.command.argument.EntityArgumentType;
import net.minecraft.item.ItemStack;
import net.minecraft.network.packet.s2c.play.UpdateSelectedSlotS2CPacket;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.text.Text;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Formatting;
import net.minecraft.util.Hand;

/**
 * {@code /metiers foyer …} et {@code /metiers gadget …} — le foyer d'un joueur
 * par commande, et « utiliser » l'objet en main d'un joueur côté serveur,
 * exactement comme son clic droit le ferait : pour vérifier un gadget sur un
 * vrai client sans avoir à cliquer à sa place.
 */
public final class GadgetCommands {

    private GadgetCommands() {
    }

    public static LiteralArgumentBuilder<ServerCommandSource> foyer() {
        return CommandManager.literal("foyer")
                .requires(CommandManager.requirePermissionLevel(CommandManager.GAMEMASTERS_CHECK))
                .then(CommandManager.argument("joueur", EntityArgumentType.player())
                        .executes(GadgetCommands::info)
                        .then(CommandManager.literal("lier").executes(GadgetCommands::bind))
                        .then(CommandManager.literal("choisir")
                                .then(CommandManager.argument("numero", IntegerArgumentType.integer(1))
                                        .executes(GadgetCommands::choose)))
                        .then(CommandManager.literal("effacer").executes(GadgetCommands::clear)));
    }

    public static LiteralArgumentBuilder<ServerCommandSource> gadget() {
        return CommandManager.literal("gadget")
                .requires(CommandManager.requirePermissionLevel(CommandManager.GAMEMASTERS_CHECK))
                .then(CommandManager.literal("utiliser")
                        .then(CommandManager.argument("joueur", EntityArgumentType.player())
                                .executes(ctx -> use(ctx, -1))
                                .then(CommandManager.argument("case", IntegerArgumentType.integer(0, 8))
                                        .executes(ctx -> use(ctx, IntegerArgumentType.getInteger(ctx, "case"))))));
    }

    // ------------------------------------------------------------------

    private static int info(CommandContext<ServerCommandSource> ctx) throws CommandSyntaxException {
        ServerPlayerEntity player = EntityArgumentType.getPlayer(ctx, "joueur");
        HearthAttachment hearth = HearthAttachment.of(player);
        String name = player.getName().getString();
        if (hearth == null) {
            ctx.getSource().sendFeedback(() -> Text.literal(name + " n'a pas de foyer.").formatted(Formatting.GRAY), false);
            return 0;
        }
        ctx.getSource().sendFeedback(() -> Text.literal(name + " — foyer : " + HearthLink.describe(hearth.pos())
                + " dans " + hearth.pos().dimension().getValue()
                + (hearth.name().isEmpty() ? "" : " (" + hearth.name() + ")")
                + " — " + hearth.inns().size() + " auberge(s) connue(s)").formatted(Formatting.GOLD), false);
        for (int i = 0; i < hearth.inns().size(); i++) {
            HearthAttachment.Inn inn = hearth.inns().get(i);
            String line = "  " + (i + 1) + ". " + HearthLink.describe(inn) + " dans " + inn.pos().dimension().getValue()
                    + (i == hearth.selected() ? "  ✦ choisie" : "");
            ctx.getSource().sendFeedback(() -> Text.literal(line).formatted(Formatting.GRAY), false);
        }
        return 1;
    }

    /** Choisit, pour ce joueur, l'auberge de ce numéro dans sa liste. */
    private static int choose(CommandContext<ServerCommandSource> ctx) throws CommandSyntaxException {
        ServerPlayerEntity player = EntityArgumentType.getPlayer(ctx, "joueur");
        int number = IntegerArgumentType.getInteger(ctx, "numero");
        if (!HearthLink.choose(player, number)) {
            ctx.getSource().sendError(Text.literal(player.getName().getString() + " n'a pas d'auberge n° " + number + "."));
            return 0;
        }
        ctx.getSource().sendFeedback(() -> Text.literal(player.getName().getString() + " : foyer choisi, auberge n° " + number + ".")
                .formatted(Formatting.GREEN), false);
        return 1;
    }

    /** Lie le joueur là où il se tient, comme le ferait l'aubergiste. */
    private static int bind(CommandContext<ServerCommandSource> ctx) throws CommandSyntaxException {
        ServerPlayerEntity player = EntityArgumentType.getPlayer(ctx, "joueur");
        NpcRole innkeeper = HcmData.ROLES.get(HauteCapitaleMetiers.id("aubergiste"));
        if (innkeeper == null) {
            ctx.getSource().sendError(Text.literal("Le rôle aubergiste n'est pas chargé."));
            return 0;
        }
        HearthLink.Result result = HearthLink.bind(player, innkeeper, player.getEntityPos());
        ctx.getSource().sendFeedback(() -> Text.literal(player.getName().getString() + " : "
                + (result == HearthLink.Result.LIE ? "foyer lié en " : "déjà lié en ")
                + player.getBlockPos().toShortString()).formatted(Formatting.GREEN), false);
        return 1;
    }

    private static int clear(CommandContext<ServerCommandSource> ctx) throws CommandSyntaxException {
        ServerPlayerEntity player = EntityArgumentType.getPlayer(ctx, "joueur");
        HearthAttachment.clear(player);
        ctx.getSource().sendFeedback(() -> Text.literal(player.getName().getString() + " : foyer effacé.")
                .formatted(Formatting.GRAY), false);
        return 1;
    }

    /**
     * Le clic droit du joueur, joué par le serveur sur l'objet qu'il tient —
     * ou sur la case de barre demandée, sélectionnée d'abord (et le client
     * prévenu, pour qu'il montre la même chose).
     */
    private static int use(CommandContext<ServerCommandSource> ctx, int slot) throws CommandSyntaxException {
        ServerPlayerEntity player = EntityArgumentType.getPlayer(ctx, "joueur");
        if (slot >= 0 && slot != player.getInventory().getSelectedSlot()) {
            player.getInventory().setSelectedSlot(slot);
            if (player.networkHandler != null) {
                player.networkHandler.sendPacket(new UpdateSelectedSlotS2CPacket(slot));
            }
        }
        ItemStack stack = player.getMainHandStack();
        if (stack.isEmpty()) {
            ctx.getSource().sendError(Text.literal(player.getName().getString() + " n'a rien en main."));
            return 0;
        }
        if (player.getItemCooldownManager().isCoolingDown(stack)) {
            ctx.getSource().sendError(Text.literal("En recharge : rien ne se passe, comme pour un clic."));
            return 0;
        }
        ActionResult result = stack.getItem().use((ServerWorld) player.getEntityWorld(), player, Hand.MAIN_HAND);
        ctx.getSource().sendFeedback(() -> Text.literal(player.getName().getString() + " utilise ")
                .append(stack.getName()).append(" → " + result).formatted(Formatting.WHITE), false);
        return result.isAccepted() ? 1 : 0;
    }
}
