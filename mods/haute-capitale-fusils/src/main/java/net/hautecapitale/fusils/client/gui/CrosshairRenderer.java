package net.hautecapitale.fusils.client.gui;

import net.hautecapitale.fusils.client.ClientEffects;
import net.hautecapitale.fusils.client.RecoilManager;
import net.hautecapitale.fusils.config.FusilsConfig;
import net.hautecapitale.fusils.data.component.ReloadState;
import net.hautecapitale.fusils.gun.ComposedShot;
import net.hautecapitale.fusils.gun.GunplayManager;
import net.hautecapitale.fusils.gun.ShotComposer;
import net.hautecapitale.fusils.item.FusilItem;
import net.hautecapitale.fusils.registry.FusilsComponents;
import net.minecraft.client.DeltaTracker;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.util.Mth;
import net.minecraft.world.item.ItemStack;
import org.joml.Matrix3x2fStack;

/**
 * Réticule d'arme : quatre traits qui s'écartent avec la dispersion réelle et le recul, tournent
 * d'un demi-tour pendant la recharge, et se contractent en marqueur à chaque touche.
 */
public final class CrosshairRenderer {
	private static float gapCursor, gapCursorO;
	private static int reloadDuration, reloadTick;

	private CrosshairRenderer() {}

	public static boolean render(GuiGraphics graphics, DeltaTracker delta) {
		if (!FusilsConfig.INSTANCE.crosshair_enabled) return false;
		Minecraft mc = Minecraft.getInstance();
		if (!mc.options.getCameraType().isFirstPerson() || mc.options.hideGui) return false;
		LocalPlayer player = mc.player;
		if (player == null || player.isSpectator()) return false;
		ItemStack held = player.getMainHandItem();
		if (!FusilItem.isGun(held)) return false;
		float partial = delta.getGameTimeDeltaPartialTick(false);
		float degrees = Mth.lerp(partial, gapCursorO, gapCursor);
		float fov = mc.options.fov().get();
		float gap = Math.max(degreesToPixels(degrees, graphics.guiHeight(), fov), 0.0F);

		graphics.nextStratum();
		Matrix3x2fStack pose = graphics.pose();
		pose.pushMatrix();
		pose.translate(graphics.guiWidth() / 2.0F - 1.0F, graphics.guiHeight() / 2.0F);
		if (reloadTick > 0) {
			pose.translate(0.5F, 0.5F);
			float f = (reloadTick - partial) / Math.max(1, reloadDuration);
			float p = 1.0F - f;
			f = 1.0F - p * p * p * p * p;
			pose.rotate(f * (float) Math.PI);
			pose.translate(-0.5F, -0.5F);
		}
		drawCross(graphics, gap);
		int hm = ClientEffects.hitmarkerTicks();
		if (hm > 0) {
			int color = ClientEffects.hitmarkerKill() ? 0xFFFF4040 : ClientEffects.hitmarkerCrit() ? 0xFFFFD040 : 0xFFFFFFFF;
			drawHitmarker(graphics, gap + 2.0F + hm * 0.5F, color);
		}
		pose.popMatrix();
		return true;
	}

	private static float degreesToPixels(float degrees, int guiHeight, float fovDegrees) {
		if (degrees <= 0.0F || guiHeight <= 0 || fovDegrees <= 0.0F) return 0.0F;
		float halfFov = fovDegrees * Mth.DEG_TO_RAD * 0.5F;
		float denom = (float) Math.tan(halfFov);
		return denom <= 1.0E-6F ? 0.0F : guiHeight * 0.5F * (float) Math.tan(degrees * Mth.DEG_TO_RAD) / denom;
	}

	private static void drawCross(GuiGraphics graphics, float gap) {
		Matrix3x2fStack pose = graphics.pose();
		int length = 3 + (int) (gap / 40.0F);
		pose.pushMatrix(); pose.translate(-gap - length, 0.0F); graphics.fill(RenderPipelines.GUI_INVERT, 0, 0, length, 1, -1); pose.popMatrix();
		pose.pushMatrix(); pose.translate(1.0F + gap, 0.0F); graphics.fill(RenderPipelines.GUI_INVERT, 0, 0, length, 1, -1); pose.popMatrix();
		pose.pushMatrix(); pose.translate(0.0F, -gap - length); graphics.fill(RenderPipelines.GUI_INVERT, 0, 0, 1, length, -1); pose.popMatrix();
		pose.pushMatrix(); pose.translate(0.0F, 1.0F + gap); graphics.fill(RenderPipelines.GUI_INVERT, 0, 0, 1, length, -1); pose.popMatrix();
	}

	private static void drawHitmarker(GuiGraphics graphics, float gap, int color) {
		Matrix3x2fStack pose = graphics.pose();
		for (int sx = -1; sx <= 1; sx += 2) {
			for (int sy = -1; sy <= 1; sy += 2) {
				for (int i = 0; i < 3; i++) {
					pose.pushMatrix();
					pose.translate(sx * (gap + i) + (sx < 0 ? 0 : 1), sy * (gap + i) + (sy < 0 ? 0 : 1));
					graphics.fill(RenderPipelines.GUI, 0, 0, 1, 1, color);
					pose.popMatrix();
				}
			}
		}
	}

	public static void tick(Minecraft mc) {
		gapCursorO = gapCursor;
		LocalPlayer player = mc.player;
		if (player == null) return;
		ItemStack held = player.getMainHandItem();
		if (FusilItem.isGun(held)) {
			ComposedShot composed = ShotComposer.compose(player, held);
			float spread = GunplayManager.spreadFor(composed, player);
			float target = RecoilManager.magnitude() * 0.5F + spread;
			gapCursor = Mth.lerp(0.25F, gapCursor, target);
			if (Math.abs(gapCursor) < 0.01F) gapCursor = 0.0F;
			ReloadState rs = held.get(FusilsComponents.RELOAD);
			if (rs != null) {
				if (reloadDuration == 0) {
					reloadDuration = rs.durationTicks();
					reloadTick = reloadDuration;
				}
			} else if (reloadTick > 2 || reloadTick == 0) {
				reloadDuration = 0;
				reloadTick = 0;
			}
		}
		if (reloadTick > 0) reloadTick--;
	}
}
