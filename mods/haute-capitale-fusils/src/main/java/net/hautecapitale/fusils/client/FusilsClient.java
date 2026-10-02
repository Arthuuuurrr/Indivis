package net.hautecapitale.fusils.client;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.client.particle.v1.ParticleFactoryRegistry;
import net.fabricmc.fabric.api.client.rendering.v1.EntityRendererRegistry;
import net.fabricmc.fabric.api.client.rendering.v1.hud.HudElementRegistry;
import net.fabricmc.fabric.api.client.rendering.v1.hud.VanillaHudElements;
import net.hautecapitale.fusils.FusilsIds;
import net.hautecapitale.fusils.client.gui.AmmoHud;
import net.hautecapitale.fusils.client.gui.AtelierScreen;
import net.hautecapitale.fusils.client.gui.CrosshairRenderer;
import net.hautecapitale.fusils.client.gui.ScopeOverlay;
import net.hautecapitale.fusils.client.particle.ImpactParticle;
import net.hautecapitale.fusils.client.particle.MuzzleFlashParticle;
import net.hautecapitale.fusils.client.particle.TrailParticle;
import net.hautecapitale.fusils.client.render.GunItemRenderer;
import net.hautecapitale.fusils.gun.FusilsData;
import net.hautecapitale.fusils.gun.GunplayManager;
import net.hautecapitale.fusils.item.FusilItem;
import net.hautecapitale.fusils.network.payload.CancelAnimPayload;
import net.hautecapitale.fusils.network.payload.GunAnimPayload;
import net.hautecapitale.fusils.network.payload.GunshotPayload;
import net.hautecapitale.fusils.network.payload.HitmarkerPayload;
import net.hautecapitale.fusils.network.payload.ImpactPayload;
import net.hautecapitale.fusils.network.payload.LocalSoundPayload;
import net.hautecapitale.fusils.network.payload.MuzzleFlashPayload;
import net.hautecapitale.fusils.network.payload.RecoilPayload;
import net.hautecapitale.fusils.network.payload.SyncDataPayload;
import net.hautecapitale.fusils.network.payload.TrailPayload;
import net.hautecapitale.fusils.registry.FusilsEntities;
import net.hautecapitale.fusils.registry.FusilsMenus;
import net.hautecapitale.fusils.registry.FusilsParticles;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.gui.screens.MenuScreens;
import net.minecraft.client.renderer.entity.NoopRenderer;
import org.lwjgl.glfw.GLFW;
import software.bernie.geckolib.animatable.client.GeoRenderProvider;
import software.bernie.geckolib.renderer.GeoItemRenderer;

/** Point d'entrée client : touches, rendu, particules, HUD, réception des paquets. */
public final class FusilsClient implements ClientModInitializer {
	public static KeyMapping RELOAD;
	public static KeyMapping ATELIER;

	@Override
	public void onInitializeClient() {
		RELOAD = KeyBindingHelper.registerKeyBinding(new KeyMapping("key.haute_capitale_fusils.recharger", GLFW.GLFW_KEY_J, KeyMapping.Category.GAMEPLAY));
		ATELIER = KeyBindingHelper.registerKeyBinding(new KeyMapping("key.haute_capitale_fusils.atelier", GLFW.GLFW_KEY_G, KeyMapping.Category.GAMEPLAY));

		FusilItem.rendererFactory = item -> new GeoRenderProvider() {
			private GeoItemRenderer<FusilItem> renderer;

			@Override
			public GeoItemRenderer<?> getGeoItemRenderer() {
				if (this.renderer == null) this.renderer = new GunItemRenderer(item);
				return this.renderer;
			}
		};
		GunplayManager.clientDryFire = ClientGunplay::dryFire;

		EntityRendererRegistry.register(FusilsEntities.BULLET, NoopRenderer::new);
		MenuScreens.register(FusilsMenus.ATELIER, AtelierScreen::new);

		ParticleFactoryRegistry registry = ParticleFactoryRegistry.getInstance();
		registry.register(FusilsParticles.FLASH_LARGE, MuzzleFlashParticle.Provider::new);
		registry.register(FusilsParticles.FLASH_TRIANGLE, MuzzleFlashParticle.Provider::new);
		registry.register(FusilsParticles.FLASH_ETOILE, MuzzleFlashParticle.Provider::new);
		registry.register(FusilsParticles.TRAINEE, TrailParticle.Provider::new);
		registry.register(FusilsParticles.IMPACT, ImpactParticle.Provider::new);

		HudElementRegistry.attachElementAfter(VanillaHudElements.HOTBAR, FusilsIds.id("munitions"), AmmoHud::render);
		HudElementRegistry.attachElementAfter(VanillaHudElements.MISC_OVERLAYS, FusilsIds.id("lunette"), ScopeOverlay::render);

		ClientTickEvents.END_CLIENT_TICK.register(client -> {
			if (client.player == null || client.level == null) return;
			if (!client.isPaused()) {
				ClientGunplay.tick(client);
				RecoilManager.tick();
				CrosshairRenderer.tick(client);
				AmmoHud.tick(client);
				ClientEffects.tick();
			}
		});

		ClientPlayNetworking.registerGlobalReceiver(SyncDataPayload.TYPE, (payload, ctx) -> FusilsData.applySync(payload.raw()));
		ClientPlayNetworking.registerGlobalReceiver(GunAnimPayload.TYPE, (payload, ctx) -> ClientAnimations.play(payload));
		ClientPlayNetworking.registerGlobalReceiver(CancelAnimPayload.TYPE, (payload, ctx) -> ClientAnimations.cancel(payload));
		ClientPlayNetworking.registerGlobalReceiver(MuzzleFlashPayload.TYPE, (payload, ctx) -> ClientEffects.muzzleFlash(payload));
		ClientPlayNetworking.registerGlobalReceiver(TrailPayload.TYPE, (payload, ctx) -> ClientEffects.trail(payload));
		ClientPlayNetworking.registerGlobalReceiver(ImpactPayload.TYPE, (payload, ctx) -> ClientEffects.impact(payload));
		ClientPlayNetworking.registerGlobalReceiver(GunshotPayload.TYPE, (payload, ctx) -> ClientEffects.gunshot(payload));
		ClientPlayNetworking.registerGlobalReceiver(LocalSoundPayload.TYPE, (payload, ctx) -> ClientEffects.localSound(payload));
		ClientPlayNetworking.registerGlobalReceiver(RecoilPayload.TYPE, (payload, ctx) -> RecoilManager.applyRaw(payload.pitch(), payload.yaw()));
		ClientPlayNetworking.registerGlobalReceiver(HitmarkerPayload.TYPE, (payload, ctx) -> ClientEffects.hitmarker(payload));
	}
}
