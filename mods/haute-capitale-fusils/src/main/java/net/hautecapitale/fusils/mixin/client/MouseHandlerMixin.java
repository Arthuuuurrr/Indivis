package net.hautecapitale.fusils.mixin.client;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import net.hautecapitale.fusils.client.AimState;
import net.hautecapitale.fusils.item.FusilItem;
import net.minecraft.client.Minecraft;
import net.minecraft.client.MouseHandler;
import net.minecraft.client.player.LocalPlayer;

/** Sensibilité réduite en visée (×0,7), fortement réduite à la longue-vue (×0,125, comme la longue-vue vanilla). */
@Mixin(MouseHandler.class)
public abstract class MouseHandlerMixin {
	@Shadow private double accumulatedDX;
	@Shadow private double accumulatedDY;

	@Inject(method = "turnPlayer", at = @At("HEAD"))
	private void hcFusils$aimSensitivity(double time, CallbackInfo ci) {
		LocalPlayer player = Minecraft.getInstance().player;
		if (player == null || !FusilItem.isAiming(player)) return;
		double factor = FusilItem.isScoping(player) ? 0.125 : 1.0 - 0.3 * AimState.progress(1.0F);
		this.accumulatedDX *= factor;
		this.accumulatedDY *= factor;
	}
}
