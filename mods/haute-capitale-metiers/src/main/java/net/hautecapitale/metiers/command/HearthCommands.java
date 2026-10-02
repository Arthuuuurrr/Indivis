package net.hautecapitale.metiers.command;

import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import net.hautecapitale.metiers.hearth.HearthLink;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.ServerPlayerEntity;

/**
 * {@code /metiers auberge …} — ouvert à tout joueur : ses auberges, et le
 * choix de celle où la Pierre de foyer le ramène. C'est aussi la commande que
 * la liste cliquable du chat envoie.
 */
public final class HearthCommands {

    private HearthCommands() {
    }

    public static LiteralArgumentBuilder<ServerCommandSource> auberge() {
        return CommandManager.literal("auberge")
                .executes(HearthCommands::list)
                .then(CommandManager.literal("choisir")
                        .then(CommandManager.argument("numero", IntegerArgumentType.integer(1))
                                .executes(ctx -> choose(ctx, IntegerArgumentType.getInteger(ctx, "numero")))));
    }

    private static int list(CommandContext<ServerCommandSource> ctx) throws CommandSyntaxException {
        ServerPlayerEntity player = ctx.getSource().getPlayerOrThrow();
        HearthLink.list(player);
        return 1;
    }

    private static int choose(CommandContext<ServerCommandSource> ctx, int number) throws CommandSyntaxException {
        ServerPlayerEntity player = ctx.getSource().getPlayerOrThrow();
        return HearthLink.choose(player, number) ? 1 : 0;
    }
}
