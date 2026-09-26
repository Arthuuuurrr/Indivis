package net.hautecapitale.fusils;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.fabricmc.fabric.api.event.player.UseItemCallback;
import net.fabricmc.fabric.api.networking.v1.ServerPlayConnectionEvents;
import net.fabricmc.fabric.api.resource.ResourceManagerHelper;
import net.fabricmc.fabric.api.resource.SimpleSynchronousResourceReloadListener;
import net.hautecapitale.fusils.command.FusilCommands;
import net.hautecapitale.fusils.config.FusilsConfig;
import net.hautecapitale.fusils.gun.FusilsData;
import net.hautecapitale.fusils.gun.GunplayManager;
import net.hautecapitale.fusils.item.FusilItem;
import net.hautecapitale.fusils.network.FusilsNetwork;
import net.hautecapitale.fusils.registry.FusilsAttachments;
import net.hautecapitale.fusils.registry.FusilsComponents;
import net.hautecapitale.fusils.registry.FusilsEntities;
import net.hautecapitale.fusils.registry.FusilsItems;
import net.hautecapitale.fusils.registry.FusilsMenus;
import net.hautecapitale.fusils.registry.FusilsParticles;
import net.hautecapitale.fusils.registry.FusilsSounds;
import net.minecraft.resources.Identifier;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.packs.PackType;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.item.ItemStack;
import software.bernie.geckolib.animatable.GeoItem;

/** Point d'entrée commun. */
public final class HauteCapitaleFusils implements ModInitializer {
	public static final Logger LOGGER = LoggerFactory.getLogger("haute_capitale_fusils");

	/** Dernier fusil vu en main par joueur (instance GeckoLib) pour jouer l'animation d'équipement. */
	private static final Map<UUID, Long> LAST_HELD = new HashMap<>();

	@Override
	public void onInitialize() {
		FusilsConfig.load();
		FusilsComponents.init();
		FusilsSounds.init();
		FusilsParticles.init();
		FusilsItems.init();
		FusilsEntities.init();
		FusilsMenus.init();
		FusilsAttachments.init();
		FusilsNetwork.register();

		ResourceManagerHelper.get(PackType.SERVER_DATA).registerReloadListener(new SimpleSynchronousResourceReloadListener() {
			@Override
			public Identifier getFabricId() {
				return FusilsIds.id("definitions");
			}

			@Override
			public void onResourceManagerReload(ResourceManager manager) {
				FusilsData.load(manager);
			}
		});

		CommandRegistrationCallback.EVENT.register(FusilCommands::register);

		ServerPlayConnectionEvents.JOIN.register((handler, sender, server) -> FusilsNetwork.sendDefinitions(handler.player));
		ServerLifecycleEvents.END_DATA_PACK_RELOAD.register((server, resources, success) -> {
			for (ServerPlayer p : server.getPlayerList().getPlayers()) FusilsNetwork.sendDefinitions(p);
		});
		ServerLifecycleEvents.SERVER_STOPPING.register(server -> {
			GunplayManager.clearServerState();
			LAST_HELD.clear();
		});
		ServerTickEvents.END_WORLD_TICK.register(net.hautecapitale.fusils.gun.PelletAccumulator::flush);
		ServerTickEvents.END_SERVER_TICK.register(server -> {
			GunplayManager.tickServer(server);
			trackEquipment(server);
		});

		// Main secondaire bloquée quand l'arme principale occupe les deux mains.
		UseItemCallback.EVENT.register((player, level, hand) -> {
			if (hand == InteractionHand.OFF_HAND && FusilItem.isOffhandBlocked(player)) {
				return InteractionResult.FAIL;
			}
			return InteractionResult.PASS;
		});

		LOGGER.info("[fusils] Haute Capitale — Fusils du Chasseur initialise ({} armes)", FusilsItems.GUNS.size());
	}

	/** Détecte un fusil qui arrive en main principale : animation et son d'équipement, ou reprise de recharge. */
	private static void trackEquipment(net.minecraft.server.MinecraftServer server) {
		for (ServerPlayer player : server.getPlayerList().getPlayers()) {
			ItemStack held = player.getMainHandItem();
			long id = FusilItem.isGun(held) ? GeoItem.getOrAssignId(held, (ServerLevel) player.level()) : -1L;
			Long last = LAST_HELD.put(player.getUUID(), id);
			if (id != -1L && (last == null || last != id)) {
				if (FusilItem.isReloading(held)) {
					GunplayManager.playReloadAnimation(player, held);
				} else {
					GunplayManager.playEquipAnimation((ServerLevel) player.level(), player, held);
				}
			}
		}
	}
}
