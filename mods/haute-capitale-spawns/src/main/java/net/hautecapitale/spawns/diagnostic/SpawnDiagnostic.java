package net.hautecapitale.spawns.diagnostic;

import com.mojang.brigadier.CommandDispatcher;
import net.hautecapitale.spawns.engine.SpawnEngine;
import net.hautecapitale.spawns.text.Msg;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;

import static net.minecraft.server.command.CommandManager.literal;

/**
 * {@code /mmospawn-diagnostic ...} — banc d'essai en jeu (niveau 4), pilotable par RCON.
 * Rempli par les scenarios de test ; voir {@link Scenarios}.
 */
public final class SpawnDiagnostic {

    private SpawnDiagnostic() {
    }

    public static void register(CommandDispatcher<ServerCommandSource> dispatcher) {
        var root = literal("mmospawn-diagnostic").requires(CommandManager.requirePermissionLevel(CommandManager.OWNERS_CHECK));
        root.then(literal("stats").executes(ctx -> {
            SpawnEngine engine = SpawnEngine.get().orElse(null);
            Msg.feedback(ctx.getSource(), Msg.info(engine == null ? "moteur arrete" : engine.stats()));
            return 1;
        }));
        Scenarios.attach(root);
        dispatcher.register(root);
    }
}
