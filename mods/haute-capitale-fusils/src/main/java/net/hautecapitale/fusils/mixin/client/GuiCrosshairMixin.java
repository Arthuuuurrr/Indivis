package net.hautecapitale.fusils.mixin.client;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import net.hautecapitale.fusils.client.gui.CrosshairRenderer;
import net.minecraft.client.DeltaTracker;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.GuiGraphics;

/** Remplace le réticule vanilla par le réticule d'arme quand un fusil est en main. */
@Mixin(Gui.class)
public abstract class GuiCrosshairMixin {
	@Inject(method = "renderCrosshair", at = @At("HEAD"), cancellable = true)
	private void hcFusils$gunCrosshair(GuiGraphics graphics, DeltaTracker delta, CallbackInfo ci) {
		if (CrosshairRenderer.render(graphics, delta)) {
			ci.cancel();
		}
	}
}
