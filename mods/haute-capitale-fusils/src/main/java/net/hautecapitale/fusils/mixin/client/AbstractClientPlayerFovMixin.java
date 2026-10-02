package net.hautecapitale.fusils.mixin.client;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import net.hautecapitale.fusils.client.AimState;
import net.hautecapitale.fusils.gun.ShotComposer;
import net.hautecapitale.fusils.item.FusilItem;
import net.minecraft.client.player.AbstractClientPlayer;
import net.minecraft.util.Mth;

/** Champ de vision : léger resserrement en visée, fort zoom avec une longue-vue. */
@Mixin(AbstractClientPlayer.class)
public abstract class AbstractClientPlayerFovMixin {
	@Inject(method = "getFieldOfViewModifier", at = @At("HEAD"), cancellable = true)
	private void hcFusils$aimFov(boolean firstPerson, float effectScale, CallbackInfoReturnable<Float> cir) {
		if (!firstPerson) return;
		AbstractClientPlayer self = (AbstractClientPlayer) (Object) this;
		if (!FusilItem.isAiming(self)) return;
		var composed = ShotComposer.compose(self, self.getUseItem());
		float target = composed.scope() ? 0.125F : composed.profile().aimFov();
		cir.setReturnValue(Mth.lerp(AimState.progress(1.0F), 1.0F, target));
	}
}
