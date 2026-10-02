package net.hautecapitale.rpg.ability;

import net.fabricmc.fabric.api.entity.event.v1.ServerEntityWorldChangeEvents;
import net.fabricmc.fabric.api.entity.event.v1.ServerPlayerEvents;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.fabricmc.fabric.api.networking.v1.ServerPlayConnectionEvents;
import net.fabricmc.fabric.api.resource.v1.ResourceLoader;
import net.fabricmc.loader.api.FabricLoader;
import net.hautecapitale.rpg.HauteCapitaleRpg;
import net.minecraft.item.Item;
import net.minecraft.resource.ResourceType;
import net.minecraft.resource.SynchronousResourceReloader;
import net.minecraft.server.network.ServerPlayerEntity;
import net.puffish.skillsmod.api.SkillsAPI;

import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Le câblage de la couche de capacités : ce qui la réveille, et quand.
 *
 * <p>Aucun balayage périodique. Chaque reconstruction est déclenchée par un événement
 * précis — arrivée, réapparition, changement de monde, changement d'arme, modification de
 * l'arbre, rechargement des données — et repose sur un drapeau par joueur pour qu'un
 * respec qui rejoue quarante récompenses ne produise qu'une seule reconstruction.
 *
 * <p>La seule chose faite à chaque tic est la comparaison de l'objet en main principale.
 * C'est une lecture de champ et une comparaison de référence, du même coût que celle que
 * Spell Engine effectue déjà pour son propre compte — et c'est ce qui rend le changement
 * d'arme immédiat plutôt que différé.
 */
public final class AbilityFeature {

    private static final Map<UUID, Item> LAST_MAIN_HAND = new ConcurrentHashMap<>();
    private static final Set<UUID> DIRTY = ConcurrentHashMap.newKeySet();

    private AbilityFeature() {
    }

    public static void markDirty(ServerPlayerEntity player) {
        DIRTY.add(player.getUuid());
    }

    public static void init() {
        ResourceLoader.get(ResourceType.SERVER_DATA).registerReloader(
                HauteCapitaleRpg.id("abilities"),
                (SynchronousResourceReloader) manager -> {
                    AbilityRegistry.load(manager);
                    DIRTY.addAll(LAST_MAIN_HAND.keySet()); // le catalogue a changé : tout le monde est à revoir
                });

        // Le registre des sorts est dynamique : il n'existe pas encore au rechargement des
        // ressources. La vérification « ce sort existe-t-il » attend donc le monde ouvert.
        ServerLifecycleEvents.SERVER_STARTED.register(AbilityRegistry::auditSpells);

        if (PuffishBridge.present()) {
            AbilityReward.register();
        } else {
            HauteCapitaleRpg.LOGGER.warn(
                    "[Capacités] Pufferfish absent : seules les capacités intrinsèques seront accordées.");
        }

        AbilityGuard.init();

        // Enregistré ici plutôt qu'avec les commandes du noyau : ces sous-commandes
        // touchent Spell Engine, elles ne doivent pas exister sans lui. Brigadier fusionne
        // les deux déclarations de « rpg » en un seul arbre.
        net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback.EVENT.register(
                (dispatcher, access, environment) -> dispatcher.register(
                        net.minecraft.server.command.CommandManager.literal("rpg")
                                .then(AbilityCommands.build())));

        ServerPlayConnectionEvents.JOIN.register((handler, sender, server) -> {
            ServerPlayerEntity player = handler.getPlayer();
            // On repart d'une ardoise vide : une capacité retirée de l'arbre entre deux
            // sessions ne doit pas survivre dans le cache.
            LearnedAbilities.forget(player);
            if (PuffishBridge.present()) {
                // Redemande à Pufferfish de rejouer nos récompenses pour ce joueur — c'est
                // ce rejeu qui repeuple le cache, et non une lecture de notre côté.
                SkillsAPI.updateRewards(player, AbilityReward.class);
            }
            markDirty(player);
        });

        ServerPlayConnectionEvents.DISCONNECT.register((handler, server) -> {
            ServerPlayerEntity player = handler.getPlayer();
            LearnedAbilities.forget(player);
            LAST_MAIN_HAND.remove(player.getUuid());
            DIRTY.remove(player.getUuid());
        });

        // La réapparition et le changement de monde produisent une nouvelle entité joueur,
        // dont la carte de conteneurs est vide : il faut la réécrire, pas seulement la
        // considérer à jour.
        ServerPlayerEvents.AFTER_RESPAWN.register((oldPlayer, newPlayer, alive) -> {
            LAST_MAIN_HAND.remove(newPlayer.getUuid());
            markDirty(newPlayer);
        });
        ServerEntityWorldChangeEvents.AFTER_PLAYER_CHANGE_WORLD.register(
                (player, origin, destination) -> markDirty(player));

        ServerTickEvents.END_SERVER_TICK.register(server -> {
            for (ServerPlayerEntity player : server.getPlayerManager().getPlayerList()) {
                UUID uuid = player.getUuid();
                Item mainHand = player.getMainHandStack().getItem();
                if (LAST_MAIN_HAND.put(uuid, mainHand) != mainHand) {
                    DIRTY.add(uuid);
                }
                if (DIRTY.remove(uuid)) {
                    AbilitySync.rebuild(player);
                }
            }
        });

        HauteCapitaleRpg.LOGGER.info("[Capacités] Couche branchée sur Spell Engine{}.",
                PuffishBridge.present() ? " et Pufferfish" : "");
    }

    /** Vrai si les deux mods dont la couche a besoin sont là. */
    public static boolean supported() {
        return FabricLoader.getInstance().isModLoaded("spell_engine");
    }
}
