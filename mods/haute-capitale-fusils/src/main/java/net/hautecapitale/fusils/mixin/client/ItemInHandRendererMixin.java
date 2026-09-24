package net.hautecapitale.fusils.mixin.client;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import com.mojang.blaze3d.vertex.PoseStack;

import net.hautecapitale.fusils.gun.GunProfile;
import net.hautecapitale.fusils.item.FusilItem;
import net.minecraft.client.player.AbstractClientPlayer;
import net.minecraft.client.renderer.ItemInHandRenderer;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.item.ItemStack;

/**
 * Première personne : la main libre disparaît quand l'arme occupe les deux mains (les bras sont
 * dessinés par le rendu de l'arme), l'arme ne « plonge » pas à l'équipement, et rien n'est dessiné
 * pendant la visée à la lunette.
 */
@Mixin(ItemInHandRenderer.class)
public abstract class ItemInHandRendererMixin {
	@Inject(method = "renderArmWithItem", at = @At("HEAD"), cancellable = true)
	private void hcFusils$hideHands(AbstractClientPlayer player, float partialTick, float pitch, InteractionHand hand, float swingProgress,
			ItemStack stack, float equipProgress, PoseStack poseStack, SubmitNodeCollector collector, int light, CallbackInfo ci) {
		if (FusilItem.isScoping(player)) {
			ci.cancel();
			return;
		}
		ItemStack main = player.getMainHandItem();
		if (hand == InteractionHand.OFF_HAND && FusilItem.isGun(main) && FusilItem.currentOccupancy(player, main) == GunProfile.HandOccupancy.BOTH) {
			ci.cancel();
		}
	}

	@ModifyVariable(method = "renderArmWithItem", at = @At("HEAD"), argsOnly = true, ordinal = 3)
	private float hcFusils$noEquipDip(float equipProgress, AbstractClientPlayer player, float partialTick, float pitch, InteractionHand hand, float swingProgress, ItemStack stack) {
		return FusilItem.isGun(stack) ? 0.0F : equipProgress;
	}
}
