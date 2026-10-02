package net.hautecapitale.fusils.client;

import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.hautecapitale.fusils.data.Stat;
import net.hautecapitale.fusils.gun.ComposedShot;
import net.hautecapitale.fusils.gun.GunProfile;
import net.hautecapitale.fusils.gun.GunplayManager;
import net.hautecapitale.fusils.gun.RecoilMath;
import net.hautecapitale.fusils.gun.ShotComposer;
import net.hautecapitale.fusils.gun.SoundUtil;
import net.hautecapitale.fusils.item.FusilItem;
import net.hautecapitale.fusils.network.payload.FirePayload;
import net.hautecapitale.fusils.network.payload.OpenAtelierPayload;
import net.hautecapitale.fusils.network.payload.ReloadPayload;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.phys.Vec2;

/** Entrées du joueur local : détente (semi / auto), rechargement, atelier ; prédiction locale. */
public final class ClientGunplay {
	private static boolean attackHeldLastTick;
	private static long lastDryFire;

	private ClientGunplay() {}

	public static void tick(Minecraft mc) {
		LocalPlayer player = mc.player;
		ItemStack held = player.getMainHandItem();
		boolean gun = FusilItem.isGun(held);
		if (gun) GunplayManager.tickHeldClient(held);

		boolean canInput = mc.screen == null && mc.mouseHandler.isMouseGrabbed();
		boolean attackHeld = canInput && mc.options.keyAttack.isDown();
		if (!gun) {
			attackHeldLastTick = attackHeld;
		} else {
			ComposedShot composed = ShotComposer.compose(player, held);
			boolean pulled = switch (composed.fireMode()) {
				case SEMI -> attackHeld && !attackHeldLastTick;
				case AUTO -> attackHeld;
			};
			attackHeldLastTick = attackHeld;
			if (pulled) tryFire(player, composed);
		}

		while (FusilsClient.RELOAD.consumeClick()) {
			if (gun && mc.screen == null && !player.isSpectator() && !FusilItem.isReloading(held)) {
				ClientPlayNetworking.send(ReloadPayload.INSTANCE);
			}
		}
		while (FusilsClient.ATELIER.consumeClick()) {
			if (gun && mc.screen == null && !player.isSpectator()) {
				ClientPlayNetworking.send(OpenAtelierPayload.INSTANCE);
			}
		}
	}

	private static void tryFire(LocalPlayer player, ComposedShot composed) {
		ItemStack held = player.getMainHandItem();
		int capacity = FusilItem.magazineCapacity(held, composed);
		int bulletIndex = capacity - FusilItem.magazine(held).count();
		if (GunplayManager.tryFire(player, player.getLookAngle())) {
			ClientPlayNetworking.send(new FirePayload(player.getLookAngle()));
			RecoilManager.apply(composed, bulletIndex);
			// Souffle arriere predit localement : le serveur enverra la meme vitesse, sans a-coup.
			float strength = (float) composed.stats().get(Stat.BLOWBACK);
			if (strength > 0.0F) {
				net.minecraft.world.phys.Vec3 look = player.getForward();
				player.push(-look.x * strength, -look.y * strength * 0.5 + 0.05, -look.z * strength);
			}
		}
	}

	/** Le « clic » à vide, joué localement et pas plus d'une fois par 8 ticks. */
	public static void dryFire(LivingEntity shooter, ComposedShot composed) {
		long now = shooter.level().getGameTime();
		if (now - lastDryFire < 8) return;
		lastDryFire = now;
		GunProfile.SoundRef dry = composed.profile().sounds().dry();
		shooter.level().playLocalSound(shooter.getX(), shooter.getY(), shooter.getZ(), SoundUtil.holder(dry.id()).value(),
				shooter.getSoundSource(), dry.volume(), dry.samplePitch(shooter.getRandom()), false);
	}

	public static Vec2 permanentRecoil(ComposedShot composed, int bulletIndex) {
		return RecoilMath.permanentRecoil(composed.stats(), composed.profile().recoil(), bulletIndex);
	}

	public static float aimFov(ComposedShot composed) {
		return composed.profile().aimFov();
	}

	@SuppressWarnings("unused")
	private static double unused(ComposedShot c) {
		return c.stats().get(Stat.SPREAD);
	}
}
