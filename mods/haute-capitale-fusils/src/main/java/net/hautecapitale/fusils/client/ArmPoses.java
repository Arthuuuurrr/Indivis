package net.hautecapitale.fusils.client;

import net.hautecapitale.fusils.data.component.ReloadState;
import net.hautecapitale.fusils.gun.GunProfile;
import net.hautecapitale.fusils.item.FusilItem;
import net.hautecapitale.fusils.registry.FusilsComponents;
import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.renderer.entity.state.HumanoidRenderState;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.HumanoidArm;
import net.minecraft.world.item.ItemStack;

/**
 * Poses des bras à la troisième personne : le bras armé suit la tête (pistolet), ou les deux bras
 * épaulent l'arme (fusil) ; pendant la recharge, le bras libre va et vient sur l'arme.
 */
public final class ArmPoses {
	private static final float QUARTER_PI = (float) (Math.PI / 4);

	private ArmPoses() {}

	public static void apply(HumanoidModel<?> model, HumanoidRenderState state) {
		HumanoidArm mainArm = state.mainArm == null ? HumanoidArm.RIGHT : state.mainArm;
		ItemStack main = mainArm == HumanoidArm.RIGHT ? state.rightHandItemStack : state.leftHandItemStack;
		if (main == null || !FusilItem.isGun(main)) return;
		GunProfile profile = FusilItem.profileOf(main);
		if (profile.pose() == GunProfile.ArmPoseKind.PISTOL) {
			pistol(model, state, mainArm, main);
		} else {
			rifle(model, state, mainArm, main);
		}
	}

	private static void pistol(HumanoidModel<?> model, HumanoidRenderState state, HumanoidArm arm, ItemStack stack) {
		boolean right = arm == HumanoidArm.RIGHT;
		ReloadState reload = stack.get(FusilsComponents.RELOAD);
		if (reload != null && !reload.isFinished()) {
			reloadPose(model.rightArm, model.leftArm, reload.percent(net.minecraft.client.Minecraft.getInstance().getDeltaTracker().getGameTimeDeltaPartialTick(true)), right);
			return;
		}
		ModelPart head = model.head;
		ModelPart armPart = model.getArm(arm);
		armPart.yRot = head.yRot;
		armPart.xRot = -1.5F + head.xRot;
		if (state.isCrouching) armPart.xRot -= 0.4F;
	}

	private static void rifle(HumanoidModel<?> model, HumanoidRenderState state, HumanoidArm arm, ItemStack stack) {
		boolean right = arm == HumanoidArm.RIGHT;
		ModelPart shooting = right ? model.rightArm : model.leftArm;
		ModelPart support = right ? model.leftArm : model.rightArm;
		ReloadState reload = stack.get(FusilsComponents.RELOAD);
		if (reload != null && !reload.isFinished()) {
			reloadPose(model.rightArm, model.leftArm, reload.percent(net.minecraft.client.Minecraft.getInstance().getDeltaTracker().getGameTimeDeltaPartialTick(true)), right);
		} else {
			ModelPart head = model.head;
			shooting.z += 1.0F;
			shooting.yRot = head.yRot;
			shooting.xRot = (float) (-Math.PI / 2) + head.xRot + 0.1F;
			support.yRot = (right ? 0.8F : -0.8F) + head.yRot;
			support.xRot = -1.5F + head.xRot;
			support.z -= 3.0F;
			float f = -Math.min(head.yRot, QUARTER_PI) / QUARTER_PI;
			if (head.yRot > 0.0F) {
				support.x += f * 4.0F;
				support.z += f * 2.0F;
				shooting.z -= f * 2.0F;
			} else {
				support.z += f * 4.0F;
			}
			if (state.isCrouching) {
				support.xRot -= 0.4F;
				shooting.xRot -= 0.4F;
			}
		}
		support.y += 1.0F;
		shooting.y += 2.0F;
	}

	/** Pose de recharge : le bras qui tient l'arme reste levé, l'autre fait des allers-retours. */
	private static void reloadPose(ModelPart rightArm, ModelPart leftArm, float percent, boolean holdingRight) {
		ModelPart holding = holdingRight ? rightArm : leftArm;
		ModelPart working = holdingRight ? leftArm : rightArm;
		holding.yRot = holdingRight ? -0.8F : 0.8F;
		holding.xRot = -0.97F;
		working.xRot = holding.xRot;
		float alpha = Mth.sin(Mth.clamp(percent, 0.0F, 1.0F) * Mth.TWO_PI * 2.0F) * 0.5F + 0.5F;
		working.yRot = Mth.lerp(alpha, 0.4F, 0.85F) * (holdingRight ? 1 : -1);
		working.xRot = Mth.lerp(alpha, working.xRot, (float) (-Math.PI / 2));
	}
}
