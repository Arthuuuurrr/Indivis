package net.hautecapitale.fusils.mixin.client;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import net.hautecapitale.fusils.item.FusilItem;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;

/**
 * Le clic gauche avec un fusil en main n'est ni une attaque ni un minage : c'est la détente,
 * lue par {@link net.hautecapitale.fusils.client.ClientGunplay} à chaque tick. Better Combat laisse
 * passer les objets sans attributs d'arme, donc le clic arrive bien ici.
 */
@Mixin(Minecraft.class)
public abstract class MinecraftAttackMixin {
	@Shadow
	public LocalPlayer player;

	@Inject(method = "startAttack", at = @At("HEAD"), cancellable = true)
	private void hcFusils$noAttackWithGun(CallbackInfoReturnable<Boolean> cir) {
		if (this.player != null && FusilItem.isGun(this.player.getMainHandItem())) {
			cir.setReturnValue(false);
		}
	}

	@Inject(method = "continueAttack", at = @At("HEAD"), cancellable = true)
	private void hcFusils$noMiningWithGun(boolean down, CallbackInfo ci) {
		if (this.player != null && FusilItem.isGun(this.player.getMainHandItem())) {
			ci.cancel();
		}
	}
}
