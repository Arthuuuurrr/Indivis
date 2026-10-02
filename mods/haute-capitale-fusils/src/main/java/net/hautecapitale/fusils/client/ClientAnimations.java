package net.hautecapitale.fusils.client;

import net.hautecapitale.fusils.item.FusilItem;
import net.hautecapitale.fusils.network.payload.CancelAnimPayload;
import net.hautecapitale.fusils.network.payload.GunAnimPayload;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import software.bernie.geckolib.animatable.manager.AnimatableManager;
import software.bernie.geckolib.animation.AnimationController;
import software.bernie.geckolib.constant.DataTickets;

/** Déclenche les animations d'arme reçues du serveur sur l'instance GeckoLib de l'objet. */
public final class ClientAnimations {
	private ClientAnimations() {}

	public static void play(GunAnimPayload p) {
		ClientLevel level = Minecraft.getInstance().level;
		if (level == null) return;
		if (level.getEntity(p.entityId()) instanceof LivingEntity living) {
			ItemStack stack = living.getItemInHand(p.mainHand() ? InteractionHand.MAIN_HAND : InteractionHand.OFF_HAND);
			if (stack.getItem() instanceof FusilItem gun) {
				play(gun, p.instanceId(), p.anim(), p.speed(), p.offset(), p.skipAt(), p.skipTo());
			}
		}
	}

	public static void play(FusilItem gun, long instanceId, String anim, double speed, double offset, double skipAt, double skipTo) {
		AnimatableManager<?> manager = gun.getAnimatableInstanceCache().getManagerForId(instanceId);
		manager.setAnimatableData(DataTickets.ITEM_RENDER_PERSPECTIVE, ItemDisplayContext.THIRD_PERSON_RIGHT_HAND);
		AnimationController<?> controller = manager.getAnimationControllers().get(FusilItem.ACTIONS_CONTROLLER);
		if (controller != null) {
			controller.triggerAnimation(anim);
			controller.setAnimationSpeed(speed);
			controller.setTimelineTime(offset);
			gun.configureSkip(instanceId, skipAt, skipTo);
		}
	}

	public static void cancel(CancelAnimPayload p) {
		ClientLevel level = Minecraft.getInstance().level;
		if (level == null) return;
		if (level.getEntity(p.entityId()) instanceof LivingEntity living) {
			ItemStack stack = living.getItemInHand(p.mainHand() ? InteractionHand.MAIN_HAND : InteractionHand.OFF_HAND);
			if (stack.getItem() instanceof FusilItem gun) {
				AnimationController<?> c = gun.getAnimatableInstanceCache().getManagerForId(p.instanceId()).getAnimationControllers().get(FusilItem.ACTIONS_CONTROLLER);
				if (c != null) {
					c.stopTriggeredAnimation();
					c.reset();
				}
			}
		}
	}
}
