package net.hautecapitale.spawns;

import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.fabricmc.fabric.api.entity.event.v1.ServerLivingEntityEvents;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerEntityEvents;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.fabricmc.fabric.api.networking.v1.ServerPlayConnectionEvents;
import net.fabricmc.loader.api.FabricLoader;
import net.hautecapitale.spawns.bridge.EasyNpcActions;
import net.hautecapitale.spawns.command.DebugView;
import net.hautecapitale.spawns.command.Selection;
import net.hautecapitale.spawns.command.SpawnCommands;
import net.hautecapitale.spawns.config.SpawnsConfig;
import net.hautecapitale.spawns.diagnostic.SpawnDiagnostic;
import net.hautecapitale.spawns.engine.ControlledMarker;
import net.hautecapitale.spawns.engine.SpawnEngine;
import net.hautecapitale.spawns.state.SpawnStateStore;
import net.hautecapitale.spawns.store.LoadReport;
import net.hautecapitale.spawns.store.SpawnRegistry;
import net.minecraft.util.Identifier;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.nio.file.Path;

/**
 * Haute Capitale — Spawns MMO.
 *
 * <p>Gestionnaire universel des spawns fixes du monde ouvert. Trois categories
 * de spawns coexistent sur le serveur et ce mod n'en gere qu'une :
 * <ul>
 *   <li>naturels (biomes) : Minecraft et les mods de mobs, intouches ;</li>
 *   <li>donjons instancies : le mod de donjons, intouche ;</li>
 *   <li><b>fixes MMO</b> (camps, ruines, repaires, boss d'exterieur) : ici.</li>
 * </ul>
 * Il ne reconnait que les entites qu'il a lui-meme creees (marque persistante),
 * donc aucun mob des deux autres categories ne peut etre capture ni compte.
 */
public final class HauteCapitaleSpawns implements ModInitializer {

    public static final String MOD_ID = "haute_capitale_spawns";
    public static final Logger LOGGER = LoggerFactory.getLogger("HC-Spawns");

    public static Identifier id(String path) {
        return Identifier.of(MOD_ID, path);
    }

    public static Path dataRoot() {
        return FabricLoader.getInstance().getConfigDir().resolve(MOD_ID);
    }

    @Override
    public void onInitialize() {
        ControlledMarker.init();

        ServerLifecycleEvents.SERVER_STARTED.register(server -> {
            Path root = dataRoot();
            SpawnsConfig.load(root);
            SpawnRegistry registry = new SpawnRegistry(root);
            LoadReport report = registry.reload();
            for (String error : report.errors) {
                LOGGER.error("Donnees de spawns : {}", error);
            }
            for (String warning : report.warnings) {
                LOGGER.warn("Donnees de spawns : {}", warning);
            }
            LOGGER.info("Donnees de spawns chargees : {}", report.summary());
            SpawnStateStore store = SpawnStateStore.of(server);
            SpawnEngine.start(server, registry, store);
            EasyNpcActions.init();
            LOGGER.info("Gestionnaire de spawns demarre ({} etat(s) de points en memoire)", store.size());
        });

        ServerLifecycleEvents.SERVER_STOPPING.register(server -> {
            SpawnEngine.stop();
            DebugView.clearAll();
            Selection.clearAll();
        });

        ServerTickEvents.END_SERVER_TICK.register(server -> SpawnEngine.get().ifPresent(engine -> {
            engine.tick();
            DebugView.tick(engine);
        }));

        ServerEntityEvents.ENTITY_LOAD.register((entity, world) ->
                SpawnEngine.get().ifPresent(engine -> engine.onEntityLoad(entity, world)));
        ServerEntityEvents.ENTITY_UNLOAD.register((entity, world) ->
                SpawnEngine.get().ifPresent(engine -> engine.onEntityUnload(entity, world)));

        ServerLivingEntityEvents.ALLOW_DAMAGE.register((entity, source, amount) ->
                SpawnEngine.get().map(engine -> engine.allowDamage(entity, source)).orElse(true));
        ServerLivingEntityEvents.AFTER_DAMAGE.register((entity, source, baseAmount, taken, blocked) ->
                SpawnEngine.get().ifPresent(engine -> engine.onDamage(entity, source)));
        ServerLivingEntityEvents.AFTER_DEATH.register((entity, source) ->
                SpawnEngine.get().ifPresent(engine -> engine.onDeath(entity, source)));

        ServerPlayConnectionEvents.DISCONNECT.register((handler, server) -> {
            DebugView.clear(handler.getPlayer());
            Selection.clear(handler.getPlayer().getUuid());
        });

        CommandRegistrationCallback.EVENT.register((dispatcher, registryAccess, environment) -> {
            SpawnCommands.register(dispatcher);
            SpawnDiagnostic.register(dispatcher);
        });

        LOGGER.info("Haute Capitale — Spawns MMO initialise");
    }
}
