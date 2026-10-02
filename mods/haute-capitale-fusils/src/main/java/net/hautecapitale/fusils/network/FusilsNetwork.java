package net.hautecapitale.fusils.network;

import net.fabricmc.fabric.api.networking.v1.PayloadTypeRegistry;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.hautecapitale.fusils.gun.FusilsData;
import net.hautecapitale.fusils.gun.GunplayManager;
import net.hautecapitale.fusils.item.FusilItem;
import net.hautecapitale.fusils.menu.AtelierMenu;
import net.hautecapitale.fusils.network.payload.CancelAnimPayload;
import net.hautecapitale.fusils.network.payload.FirePayload;
import net.hautecapitale.fusils.network.payload.GunAnimPayload;
import net.hautecapitale.fusils.network.payload.GunshotPayload;
import net.hautecapitale.fusils.network.payload.HitmarkerPayload;
import net.hautecapitale.fusils.network.payload.ImpactPayload;
import net.hautecapitale.fusils.network.payload.LocalSoundPayload;
import net.hautecapitale.fusils.network.payload.MuzzleFlashPayload;
import net.hautecapitale.fusils.network.payload.OpenAtelierPayload;
import net.hautecapitale.fusils.network.payload.RecoilPayload;
import net.hautecapitale.fusils.network.payload.ReloadPayload;
import net.hautecapitale.fusils.network.payload.SyncDataPayload;
import net.hautecapitale.fusils.network.payload.TrailPayload;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.ItemStack;

/** Enregistrement des paquets et réception côté serveur. */
public final class FusilsNetwork {
	private FusilsNetwork() {}

	public static void register() {
		PayloadTypeRegistry.playC2S().register(FirePayload.TYPE, FirePayload.CODEC);
		PayloadTypeRegistry.playC2S().register(ReloadPayload.TYPE, ReloadPayload.CODEC);
		PayloadTypeRegistry.playC2S().register(OpenAtelierPayload.TYPE, OpenAtelierPayload.CODEC);

		PayloadTypeRegistry.playS2C().register(GunAnimPayload.TYPE, GunAnimPayload.CODEC);
		PayloadTypeRegistry.playS2C().register(CancelAnimPayload.TYPE, CancelAnimPayload.CODEC);
		PayloadTypeRegistry.playS2C().register(MuzzleFlashPayload.TYPE, MuzzleFlashPayload.CODEC);
		PayloadTypeRegistry.playS2C().register(TrailPayload.TYPE, TrailPayload.CODEC);
		PayloadTypeRegistry.playS2C().register(ImpactPayload.TYPE, ImpactPayload.CODEC);
		PayloadTypeRegistry.playS2C().register(GunshotPayload.TYPE, GunshotPayload.CODEC);
		PayloadTypeRegistry.playS2C().register(LocalSoundPayload.TYPE, LocalSoundPayload.CODEC);
		PayloadTypeRegistry.playS2C().register(RecoilPayload.TYPE, RecoilPayload.CODEC);
		PayloadTypeRegistry.playS2C().register(HitmarkerPayload.TYPE, HitmarkerPayload.CODEC);
		PayloadTypeRegistry.playS2C().register(SyncDataPayload.TYPE, SyncDataPayload.CODEC);

		ServerPlayNetworking.registerGlobalReceiver(FirePayload.TYPE, (payload, context) -> {
			GunplayManager.tryFire(context.player(), payload.direction());
		});
		ServerPlayNetworking.registerGlobalReceiver(ReloadPayload.TYPE, (payload, context) -> {
			ServerPlayer player = context.player();
			ItemStack stack = player.getMainHandItem();
			if (FusilItem.isGun(stack)) {
				GunplayManager.reloadFeedback(player, GunplayManager.startReload(player, stack));
			}
		});
		ServerPlayNetworking.registerGlobalReceiver(OpenAtelierPayload.TYPE, (payload, context) -> {
			ServerPlayer player = context.player();
			if (FusilItem.isGun(player.getMainHandItem()) && !FusilItem.isReloading(player.getMainHandItem())) {
				AtelierMenu.open(player);
			}
		});
	}

	public static void sendDefinitions(ServerPlayer player) {
		ServerPlayNetworking.send(player, new SyncDataPayload(FusilsData.raw()));
	}
}
