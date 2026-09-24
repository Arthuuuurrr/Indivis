package net.hautecapitale.fusils.client.gui;

import net.hautecapitale.fusils.item.FusilItem;
import net.minecraft.client.DeltaTracker;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.resources.Identifier;
import net.minecraft.util.Mth;

/** Vignette de longue-vue quand le joueur vise avec une lunette installée. */
public final class ScopeOverlay {
	private static final Identifier TEXTURE = Identifier.withDefaultNamespace("textures/misc/spyglass_scope.png");
	private static float scale = 0.5F;

	private ScopeOverlay() {}

	public static void render(GuiGraphics graphics, DeltaTracker delta) {
		Minecraft mc = Minecraft.getInstance();
		LocalPlayer player = mc.player;
		if (player == null || !mc.options.getCameraType().isFirstPerson() || !FusilItem.isScoping(player)) {
			scale = 0.5F;
			return;
		}
		scale = Mth.lerp(0.5F * delta.getGameTimeDeltaTicks(), scale, 1.125F);
		float src = Math.min(graphics.guiWidth(), graphics.guiHeight());
		float ratio = Math.min(graphics.guiWidth() / src, graphics.guiHeight() / src) * scale;
		int size = Mth.floor(src * ratio);
		int left = (graphics.guiWidth() - size) / 2;
		int top = (graphics.guiHeight() - size) / 2;
		graphics.nextStratum();
		graphics.blit(RenderPipelines.GUI_TEXTURED, TEXTURE, left, top, 0.0F, 0.0F, size, size, size, size);
		graphics.fill(RenderPipelines.GUI, 0, top + size, graphics.guiWidth(), graphics.guiHeight(), 0xFF000000);
		graphics.fill(RenderPipelines.GUI, 0, 0, graphics.guiWidth(), top, 0xFF000000);
		graphics.fill(RenderPipelines.GUI, 0, top, left, top + size, 0xFF000000);
		graphics.fill(RenderPipelines.GUI, left + size, top, graphics.guiWidth(), top + size, 0xFF000000);
	}
}
