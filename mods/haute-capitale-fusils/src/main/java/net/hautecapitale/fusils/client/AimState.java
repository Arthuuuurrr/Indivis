package net.hautecapitale.fusils.client;

import net.hautecapitale.fusils.item.FusilItem;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.util.Mth;

/** Progression lissée de la visée du joueur local (0 = hanche, 1 = épaulé). */
public final class AimState {
	private static float progress;
	private static float progressO;

	private AimState() {}

	public static void tick() {
		progressO = progress;
		LocalPlayer player = Minecraft.getInstance().player;
		boolean aiming = player != null && FusilItem.isAiming(player);
		progress = Mth.clamp(progress + (aiming ? 0.2F : -0.25F), 0.0F, 1.0F);
	}

	public static float progress(float partialTick) {
		float p = Mth.lerp(partialTick, progressO, progress);
		return p * p * (3 - 2 * p);
	}

	public static boolean isAiming() {
		return progress > 0.5F;
	}
}
