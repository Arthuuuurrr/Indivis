package net.hautecapitale.metiers.command;

import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.suggestion.SuggestionProvider;
import net.hautecapitale.metiers.data.HcmData;
import net.hautecapitale.metiers.node.NodeAttachment;
import net.hautecapitale.metiers.node.NodeCommandsSupport;
import net.hautecapitale.metiers.node.NodeEngine;
import net.hautecapitale.metiers.node.NodeFeedback;
import net.hautecapitale.metiers.node.NodeStore;
import net.hautecapitale.metiers.node.NodeTool;
import net.hautecapitale.metiers.node.NodeType;
import net.minecraft.command.CommandSource;
import net.minecraft.command.argument.BlockPosArgumentType;
import net.minecraft.command.argument.EntityArgumentType;
import net.minecraft.command.argument.IdentifierArgumentType;
import net.minecraft.item.ItemStack;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;

import java.util.Locale;
import java.util.Map;
import java.util.TreeMap;

/**
 * {@code /metiers node …} — poser, retirer, inspecter, forcer la repousse, et
 * frapper un node par commande pour le tester sans client.
 */
public final class NodeCommands {

    private static final SuggestionProvider<ServerCommandSource> TYPES =
            (context, builder) -> CommandSource.suggestIdentifiers(HcmData.NODES.ids(), builder);

    private NodeCommands() {
    }

    public static LiteralArgumentBuilder<ServerCommandSource> node() {
        return CommandManager.literal("node")
                .requires(CommandManager.requirePermissionLevel(CommandManager.GAMEMASTERS_CHECK))

                .then(CommandManager.literal("outil")
                        .executes(ctx -> giveTool(ctx, ctx.getSource().getPlayerOrThrow(), null))
                        .then(CommandManager.argument("type", IdentifierArgumentType.identifier())
                                .suggests(TYPES)
                                .executes(ctx -> giveTool(ctx, ctx.getSource().getPlayerOrThrow(),
                                        IdentifierArgumentType.getIdentifier(ctx, "type")))))

                .then(CommandManager.literal("lier")
                        .then(CommandManager.argument("type", IdentifierArgumentType.identifier())
                                .suggests(TYPES)
                                .executes(NodeCommands::bind)))

                .then(CommandManager.literal("poser")
                        .then(CommandManager.argument("type", IdentifierArgumentType.identifier())
                                .suggests(TYPES)
                                .then(CommandManager.argument("position", BlockPosArgumentType.blockPos())
                                        .executes(NodeCommands::place))))

                .then(CommandManager.literal("retirer")
                        .then(CommandManager.argument("position", BlockPosArgumentType.blockPos())
                                .executes(NodeCommands::remove)))

                .then(CommandManager.literal("info")
                        .then(CommandManager.argument("position", BlockPosArgumentType.blockPos())
                                .executes(NodeCommands::info)))

                .then(CommandManager.literal("liste")
                        .executes(NodeCommands::list))

                .then(CommandManager.literal("remplir")
                        .executes(NodeCommands::refillAll)
                        .then(CommandManager.argument("position", BlockPosArgumentType.blockPos())
                                .executes(NodeCommands::refillOne)))

                .then(CommandManager.literal("vider")
                        .then(CommandManager.argument("position", BlockPosArgumentType.blockPos())
                                .executes(NodeCommands::empty)))

                .then(CommandManager.literal("frapper")
                        .then(CommandManager.argument("joueur", EntityArgumentType.player())
                                .then(CommandManager.argument("position", BlockPosArgumentType.blockPos())
                                        .executes(NodeCommands::strike))));
    }

    /** {@code /metiers inspecter nodes} — les types chargés. */
    public static LiteralArgumentBuilder<ServerCommandSource> inspectTypes() {
        return CommandManager.literal("nodes")
                .executes(ctx -> listTypes(ctx.getSource()));
    }

    // ------------------------------------------------------------------

    private static int giveTool(CommandContext<ServerCommandSource> ctx, ServerPlayerEntity player,
                                Identifier type) {
        ItemStack wand = type == null ? new ItemStack(NodeTool.ITEM) : NodeTool.create(type);
        if (type != null && !HcmData.NODES.contains(type)) {
            ctx.getSource().sendError(Text.literal("Type de node inconnu : " + type));
            return 0;
        }
        if (!player.getInventory().insertStack(wand)) {
            player.dropItem(wand, false);
        }
        ctx.getSource().sendFeedback(() -> Text.literal(type == null
                ? "Baguette donnée — liez-la avec « /metiers node lier <type> »."
                : "Baguette liée à " + type + " donnée.").formatted(Formatting.GREEN), false);
        return 1;
    }

    private static int bind(CommandContext<ServerCommandSource> ctx) throws CommandSyntaxException {
        ServerPlayerEntity player = ctx.getSource().getPlayerOrThrow();
        Identifier type = IdentifierArgumentType.getIdentifier(ctx, "type");
        if (!HcmData.NODES.contains(type)) {
            ctx.getSource().sendError(Text.literal("Type de node inconnu : " + type
                    + " — voir « /metiers inspecter nodes »."));
            return 0;
        }
        ItemStack held = player.getMainHandStack();
        if (!held.isOf(NodeTool.ITEM)) {
            ctx.getSource().sendError(Text.literal("Tenez la baguette en main — « /metiers node outil »."));
            return 0;
        }
        NodeTool.bind(held, type);
        ctx.getSource().sendFeedback(() -> Text.literal("Baguette liée à " + type + ".").formatted(Formatting.GREEN), false);
        return 1;
    }

    private static int place(CommandContext<ServerCommandSource> ctx) throws CommandSyntaxException {
        Identifier type = IdentifierArgumentType.getIdentifier(ctx, "type");
        BlockPos pos = BlockPosArgumentType.getLoadedBlockPos(ctx, "position");
        ServerWorld world = ctx.getSource().getWorld();

        NodeEngine.PlaceRefusal refusal = NodeEngine.place(world, pos, type);
        if (refusal != null) {
            ctx.getSource().sendError(Text.literal("Impossible de poser : " + NodeCommandsSupport.explain(refusal)));
            return 0;
        }
        ctx.getSource().sendFeedback(() -> Text.literal("Node " + type + " posé en " + pos.toShortString() + ".")
                .formatted(Formatting.GREEN), true);
        return 1;
    }

    private static int remove(CommandContext<ServerCommandSource> ctx) throws CommandSyntaxException {
        BlockPos pos = BlockPosArgumentType.getLoadedBlockPos(ctx, "position");
        boolean removed = NodeEngine.remove(ctx.getSource().getWorld(), pos);
        if (!removed) {
            ctx.getSource().sendError(Text.literal("Aucun node en " + pos.toShortString() + "."));
            return 0;
        }
        ctx.getSource().sendFeedback(() -> Text.literal("Node retiré en " + pos.toShortString()
                + " — le bloc est laissé tel quel.").formatted(Formatting.YELLOW), true);
        return 1;
    }

    private static int info(CommandContext<ServerCommandSource> ctx) throws CommandSyntaxException {
        BlockPos pos = BlockPosArgumentType.getLoadedBlockPos(ctx, "position");
        NodeStore.Node node = NodeAttachment.of(ctx.getSource().getWorld()).get(pos);
        if (node == null) {
            ctx.getSource().sendError(Text.literal("Aucun node en " + pos.toShortString() + "."));
            return 0;
        }
        ctx.getSource().sendFeedback(() -> NodeCommandsSupport.describe(pos, node), false);
        return 1;
    }

    private static int list(CommandContext<ServerCommandSource> ctx) {
        ServerWorld world = ctx.getSource().getWorld();
        NodeStore store = NodeAttachment.of(world);
        Map<Identifier, int[]> byType = new TreeMap<>();
        for (NodeStore.Node node : store.all().values()) {
            int[] counts = byType.computeIfAbsent(node.type, ignored -> new int[2]);
            counts[node.isFull() ? 0 : 1]++;
        }

        ServerCommandSource source = ctx.getSource();
        source.sendFeedback(() -> Text.literal(store.size() + " node(s) dans "
                + world.getRegistryKey().getValue() + " :").formatted(Formatting.GOLD), false);
        byType.forEach((type, counts) -> source.sendFeedback(() -> Text.literal(String.format(Locale.ROOT,
                "  %s — %d plein(s), %d vide(s)%s", type, counts[0], counts[1],
                HcmData.NODES.contains(type) ? "" : "  [type inconnu dans les données]"))
                .formatted(HcmData.NODES.contains(type) ? Formatting.WHITE : Formatting.RED), false));
        if (store.size() <= 12) {
            store.all().forEach((pos, node) -> source.sendFeedback(
                    () -> Text.literal("  ").append(NodeCommandsSupport.describe(pos, node)), false));
        }
        return store.size();
    }

    private static int refillAll(CommandContext<ServerCommandSource> ctx) {
        ServerWorld world = ctx.getSource().getWorld();
        NodeStore store = NodeAttachment.of(world);
        int restored = 0;
        for (BlockPos pos : store.pending()) {
            if (NodeEngine.forceRestore(world, pos)) {
                restored++;
            }
        }
        int count = restored;
        ctx.getSource().sendFeedback(() -> Text.literal(count + " node(s) rempli(s).").formatted(Formatting.GREEN), true);
        return restored;
    }

    private static int refillOne(CommandContext<ServerCommandSource> ctx) throws CommandSyntaxException {
        BlockPos pos = BlockPosArgumentType.getLoadedBlockPos(ctx, "position");
        boolean restored = NodeEngine.forceRestore(ctx.getSource().getWorld(), pos);
        ctx.getSource().sendFeedback(() -> Text.literal(restored
                ? "Node rempli en " + pos.toShortString() + "."
                : "Rien à remplir en " + pos.toShortString() + ".")
                .formatted(restored ? Formatting.GREEN : Formatting.GRAY), true);
        return restored ? 1 : 0;
    }

    private static int empty(CommandContext<ServerCommandSource> ctx) throws CommandSyntaxException {
        BlockPos pos = BlockPosArgumentType.getLoadedBlockPos(ctx, "position");
        boolean emptied = NodeEngine.forceEmpty(ctx.getSource().getWorld(), pos);
        if (!emptied) {
            ctx.getSource().sendError(Text.literal("Aucun node (ou type inconnu) en " + pos.toShortString() + "."));
            return 0;
        }
        ctx.getSource().sendFeedback(() -> Text.literal("Node vidé en " + pos.toShortString()
                + " — il repoussera à son heure.").formatted(Formatting.YELLOW), true);
        return 1;
    }

    /** Le même chemin qu'un clic gauche du joueur, sans le clic. */
    private static int strike(CommandContext<ServerCommandSource> ctx) throws CommandSyntaxException {
        ServerPlayerEntity player = EntityArgumentType.getPlayer(ctx, "joueur");
        BlockPos pos = BlockPosArgumentType.getLoadedBlockPos(ctx, "position");
        ServerWorld world = ctx.getSource().getWorld();

        NodeEngine.HitOutcome outcome = NodeEngine.hit(player, world, pos);
        NodeFeedback.overlay(player, outcome);
        Text text = NodeFeedback.describe(outcome);
        ctx.getSource().sendFeedback(() -> Text.literal(player.getName().getString() + " : " + outcome.hit().name())
                .append(text == null ? Text.empty() : Text.literal(" — ").append(text)), false);
        return outcome.harvested() ? 1 : 0;
    }

    private static int listTypes(ServerCommandSource source) {
        source.sendFeedback(() -> Text.literal(HcmData.NODES.size() + " type(s) de node :").formatted(Formatting.GOLD), false);
        HcmData.NODES.all().forEach((id, type) -> source.sendFeedback(() -> Text.literal(String.format(Locale.ROOT,
                "  %s — %s niv. %d, %.0f XP, %s → %s, repousse %d s, %d coup(s)",
                id, type.profession().getId(), type.level(), type.xp(),
                type.fullBlock(), type.emptyBlock(), type.respawnSeconds(), type.hits()))
                .formatted(Formatting.WHITE), false));
        return HcmData.NODES.size();
    }
}
