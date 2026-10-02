package net.hautecapitale.fusils.mixin.client;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import net.hautecapitale.fusils.client.RecoilManager;
import net.minecraft.client.Camera;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.Level;

/** Applique le recul de caméra (part non permanente) à l'orientation de la caméra. */
@Mixin(Camera.class)
public abstract class CameraRecoilMixin {
	@Shadow private float xRot;
	@Shadow private float yRot;

	@Shadow protected abstract void setRotation(float yRot, float xRot);

	@Inject(method = "setup", at = @At("RETURN"))
	private void hcFusils$recoil(Level level, Entity entity, boolean detached, boolean mirrored, float partialTick, CallbackInfo ci) {
		float p = RecoilManager.cameraPitch(partialTick);
		float y = RecoilManager.cameraYaw(partialTick);
		if (p != 0.0F || y != 0.0F) {
			setRotation(this.yRot + y, this.xRot - p);
		}
	}
}
