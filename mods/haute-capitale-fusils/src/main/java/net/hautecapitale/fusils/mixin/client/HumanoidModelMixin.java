package net.hautecapitale.fusils.mixin.client;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import net.hautecapitale.fusils.client.ArmPoses;
import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.renderer.entity.state.HumanoidRenderState;

/** Troisième personne : poses de bras des fusils (pistolet suiveur de tête, fusil épaulé, recharge). */
@Mixin(HumanoidModel.class)
public abstract class HumanoidModelMixin {
	@Inject(method = "setupAnim(Lnet/minecraft/client/renderer/entity/state/HumanoidRenderState;)V", at = @At("TAIL"))
	private void hcFusils$gunPoses(HumanoidRenderState state, CallbackInfo ci) {
		ArmPoses.apply((HumanoidModel<?>) (Object) this, state);
	}
}
