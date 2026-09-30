package fr.hautecapitale.nexusbridge;

import java.nio.file.Path;
import java.util.Iterator;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.fabricmc.fabric.api.networking.v1.ServerPlayConnectionEvents;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerPlayerEntity;

public final class NexusCapitaleFirstSpawnBridge implements ModInitializer {
    private static final Map<UUID, Integer> PENDING_NEW_CHARACTERS = new ConcurrentHashMap<>();
    private static final Map<UUID, Path> NEW_VAULTS = new ConcurrentHashMap<>();
    private static final int DELAY_TICKS = 3;
    private static final String PROLOGUE_FUNCTION = "function capitale:spawn/character_first_join_to_prologue_start_self";

    public static void markNewAuthoritativeVault(UUID owner, Path vault) {
        if (owner == null) {
            return;
        }
        NEW_VAULTS.put(owner, vault);
        System.out.println("[NexusCapitaleBridge] New empty Nexus vault detected for " + owner
                + (vault == null ? "" : " at " + vault));
    }

    @Override
    public void onInitialize() {
        ServerPlayConnectionEvents.JOIN.register((handler, sender, server) -> {
            ServerPlayerEntity player = handler.player;
            UUID owner = player.getUuid();
            Path vault = NEW_VAULTS.remove(owner);
            if (vault != null) {
                PENDING_NEW_CHARACTERS.put(owner, DELAY_TICKS);
                System.out.println("[NexusCapitaleBridge] Scheduling first-prologue hook for "
                        + player.getName().getString() + " (" + owner + ") in " + DELAY_TICKS + " ticks.");
            }
        });

        ServerPlayConnectionEvents.DISCONNECT.register((handler, server) -> {
            UUID owner = handler.player.getUuid();
            PENDING_NEW_CHARACTERS.remove(owner);
        });

        ServerTickEvents.END_SERVER_TICK.register(NexusCapitaleFirstSpawnBridge::tickPending);

        System.out.println("[NexusCapitaleBridge] 1.0.0 initialized: exact new-vault -> Capitale prologue hook.");
    }

    private static void tickPending(MinecraftServer server) {
        Iterator<Map.Entry<UUID, Integer>> it = PENDING_NEW_CHARACTERS.entrySet().iterator();
        while (it.hasNext()) {
            Map.Entry<UUID, Integer> entry = it.next();
            int remaining = entry.getValue() - 1;
            if (remaining > 0) {
                entry.setValue(remaining);
                continue;
            }

            it.remove();
            ServerPlayerEntity player = server.getPlayerManager().getPlayer(entry.getKey());
            if (player == null) {
                System.out.println("[NexusCapitaleBridge] New character player disconnected before prologue hook: "
                        + entry.getKey());
                continue;
            }

            try {
                int result = server.getCommandManager().executeWithPrefix(
                        player.getCommandSource().withLevel(4),
                        PROLOGUE_FUNCTION);
                System.out.println("[NexusCapitaleBridge] First-prologue hook executed for "
                        + player.getName().getString() + " result=" + result
                        + " pos=" + player.getX() + "," + player.getY() + "," + player.getZ());
            } catch (Throwable t) {
                System.err.println("[NexusCapitaleBridge] First-prologue hook failed for "
                        + player.getName().getString() + ": " + t);
                t.printStackTrace(System.err);
            }
        }
    }
}
