package net.hautecapitale.fusils.client.gui;

import net.hautecapitale.fusils.FusilsIds;
import net.hautecapitale.fusils.config.FusilsConfig;
import net.hautecapitale.fusils.data.component.ChargedAmmo;
import net.hautecapitale.fusils.gun.AmmoType;
import net.hautecapitale.fusils.gun.ComposedShot;
import net.hautecapitale.fusils.gun.FusilsData;
import net.hautecapitale.fusils.gun.GunplayManager;
import net.hautecapitale.fusils.gun.ShotComposer;
import net.hautecapitale.fusils.item.FusilItem;
import net.hautecapitale.fusils.registry.FusilsComponents;
import net.minecraft.client.DeltaTracker;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.resources.Identifier;
import net.minecraft.util.ARGB;
import net.minecraft.util.Mth;
import net.minecraft.world.item.ItemStack;
import org.joml.Matrix3x2fStack;

/** Compteur de munitions : coups chargés / capacité, réserve, munition spéciale, flash « 0 » quand l'arme se vide. */
public final class AmmoHud {
	private static final Identifier ICON = FusilsIds.id("textures/gui/balle_icone.png");
	private static int previousLoaded = -1;
	private static int flashTicks;
	private static int flashDuration;

	private AmmoHud() {}

	public static void render(GuiGraphics graphics, DeltaTracker delta) {
		FusilsConfig cfg = FusilsConfig.INSTANCE;
		if (!cfg.hud_enabled) return;
		Minecraft mc = Minecraft.getInstance();
		if (mc.options.hideGui) return;
		LocalPlayer player = mc.player;
		if (player == null || player.isSpectator()) return;
		ItemStack held = player.getMainHandItem();
		if (!FusilItem.isGun(held)) return;

		Font font = mc.font;
		ComposedShot composed = ShotComposer.compose(player, held);
		int loaded = FusilItem.magazine(held).count();
		int capacity = FusilItem.magazineCapacity(held, composed);
		int reserve = GunplayManager.countBullets(player);
		float loadedScale = (float) cfg.hud_scale;
		float capacityScale = loadedScale * 0.75F;
		boolean showReserve = cfg.hud_show_reserve && !FusilsConfig.INSTANCE.freeAmmo();
		int iconSize = 16;

		String loadedText = Integer.toString(loaded);
		String capacityText = "/" + capacity;
		String reserveText = reserve >= 999 ? "∞" : Integer.toString(reserve);
		float loadedWidth = font.width(loadedText) * loadedScale;
		float capacityWidth = font.width(capacityText) * capacityScale;
		float magazineWidth = loadedWidth + capacityWidth;
		float reserveWidth = showReserve ? font.width(reserveText) : 0.0F;
		float column = Math.max(magazineWidth, reserveWidth);
		float iconBlock = iconSize + 4;
		float totalWidth = iconBlock + column;
		float magazineHeight = 9.0F * loadedScale;
		float reserveHeight = showReserve ? 9.0F : 0.0F;
		float gapRows = showReserve ? 2.0F : 0.0F;
		float totalHeight = Math.max(iconSize, magazineHeight + gapRows + reserveHeight);

		float left, top;
		int ox = cfg.hud_offset_x, oy = cfg.hud_offset_y;
		switch (cfg.hud_anchor) {
			case "bottom_left" -> { left = ox; top = graphics.guiHeight() - oy - totalHeight; }
			case "top_right" -> { left = graphics.guiWidth() - ox - totalWidth; top = oy; }
			case "top_left" -> { left = ox; top = oy; }
			case "hotbar" -> { left = ox + graphics.guiWidth() / 2.0F + 96; top = graphics.guiHeight() - oy - totalHeight; }
			default -> { left = graphics.guiWidth() - ox - totalWidth; top = graphics.guiHeight() - oy - totalHeight; }
		}
		int magColor = magazineColor(loaded, capacity);
		graphics.nextStratum();
		float iconY = top + (totalHeight - iconSize) * 0.5F;
		graphics.blit(RenderPipelines.GUI_TEXTURED, ICON, Math.round(left), Math.round(iconY), 0.0F, 0.0F, iconSize, iconSize, iconSize, iconSize);
		float textLeft = left + iconBlock;
		float magazineTop = top + Math.max(0.0F, (totalHeight - (magazineHeight + gapRows + reserveHeight)) * 0.5F);
		float magazineLeft = textLeft + (column - magazineWidth);
		drawScaled(graphics, font, loadedText, magazineLeft, magazineTop, loadedScale, magColor);
		drawScaled(graphics, font, capacityText, magazineLeft + loadedWidth, magazineTop + (9.0F * loadedScale - 9.0F * capacityScale), capacityScale, magColor);
		if (showReserve) {
			drawScaled(graphics, font, reserveText, textLeft + column - reserveWidth, magazineTop + magazineHeight + gapRows, 1.0F, 0xFFCCCCCC);
		}
		ChargedAmmo charged = held.get(FusilsComponents.CHARGED_AMMO);
		if (charged != null && charged.shots() > 0) {
			AmmoType ammo = FusilsData.ammo(charged.ammo());
			if (ammo != null) {
				String label = ammo.name().getString() + " ×" + charged.shots();
				float w = font.width(label);
				drawScaled(graphics, font, label, left + totalWidth - w, top - 11, 1.0F, 0xFF000000 | ammo.color());
			}
		}
		if (flashTicks > 0 && cfg.hud_flash_on_empty) {
			float partial = delta.getGameTimeDeltaPartialTick(false);
			float ox2 = left + totalWidth / 2.0F;
			renderFlash(graphics, font, partial, ox2, magazineTop, loadedScale, 0.2F, 2.0F);
			renderFlash(graphics, font, partial, ox2, magazineTop, loadedScale, 0.3F, 4.0F);
			renderFlash(graphics, font, partial, ox2, magazineTop, loadedScale, 0.4F, 6.0F);
		}
	}

	private static void renderFlash(GuiGraphics graphics, Font font, float partial, float startX, float startY, float baseScale, float travel, float endScale) {
		float remaining = flashTicks - partial;
		float progress = 1.0F - Mth.clamp(remaining / Math.max(1, flashDuration), 0.0F, 1.0F);
		float eased = 1.0F - (1.0F - progress) * (1.0F - progress) * (1.0F - progress) * (1.0F - progress);
		float scale = baseScale * Mth.lerp(eased, 1.0F, endScale);
		float alpha = 1.0F - eased;
		if (alpha <= 0.01F) return;
		int color = ARGB.color(alpha, 0xFF5555);
		float w = font.width("0") * scale;
		float h = 9.0F * scale;
		float x = Mth.lerp(eased * travel, startX, graphics.guiWidth() * 0.5F) - w * 0.5F;
		float y = Mth.lerp(eased * travel, startY, graphics.guiHeight() * 0.5F) - h * 0.5F;
		drawScaled(graphics, font, "0", x, y, scale, color);
	}

	private static int magazineColor(int loaded, int capacity) {
		if (loaded <= 0) return 0xFFFF5555;
		if (loaded >= capacity) return 0xFFFFFFFF;
		float t = Mth.clamp((float) (loaded - 1) / Math.max(1, capacity - 1), 0.0F, 1.0F);
		return ARGB.srgbLerp(t, 0xFFFFAA00, 0xFFFFFFFF);
	}

	private static void drawScaled(GuiGraphics graphics, Font font, String text, float x, float y, float scale, int color) {
		if (ARGB.alpha(color) == 0) return;
		Matrix3x2fStack pose = graphics.pose();
		pose.pushMatrix();
		pose.translate(x, y);
		pose.scale(scale, scale);
		graphics.drawString(font, text, 0, 0, color, true);
		pose.popMatrix();
	}

	public static void tick(Minecraft mc) {
		LocalPlayer player = mc.player;
		if (player == null) {
			previousLoaded = -1;
			flashTicks = 0;
			return;
		}
		ItemStack held = player.getMainHandItem();
		if (FusilItem.isGun(held)) {
			int loaded = FusilItem.magazine(held).count();
			if (previousLoaded > 0 && loaded == 0) {
				flashDuration = 30;
				flashTicks = flashDuration;
			}
			previousLoaded = loaded;
		} else {
			previousLoaded = -1;
		}
		if (flashTicks > 0) flashTicks--;
		net.hautecapitale.fusils.client.AimState.tick();
	}
}
