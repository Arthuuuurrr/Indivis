package net.hautecapitale.fusils.client.render;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import com.mojang.blaze3d.vertex.PoseStack;

import net.hautecapitale.fusils.client.AimState;
import net.hautecapitale.fusils.data.component.ReloadState;
import net.hautecapitale.fusils.gun.ComposedShot;
import net.hautecapitale.fusils.gun.GunProfile;
import net.hautecapitale.fusils.gun.GunVariant;
import net.hautecapitale.fusils.gun.ShotComposer;
import net.hautecapitale.fusils.item.FusilItem;
import net.hautecapitale.fusils.registry.FusilsComponents;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.player.PlayerModel;
import net.minecraft.client.player.AbstractClientPlayer;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.entity.player.AvatarRenderer;
import net.minecraft.client.renderer.rendertype.RenderType;
import net.minecraft.client.renderer.rendertype.RenderTypes;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.world.entity.HumanoidArm;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import org.joml.Matrix3f;
import software.bernie.geckolib.animatable.GeoItem;
import software.bernie.geckolib.animation.AnimationController;
import software.bernie.geckolib.animation.state.BoneSnapshot;
import software.bernie.geckolib.cache.model.GeoBone;
import software.bernie.geckolib.constant.DataTickets;
import software.bernie.geckolib.renderer.GeoItemRenderer;
import software.bernie.geckolib.renderer.base.BoneSnapshots;
import software.bernie.geckolib.renderer.base.GeoRenderState;
import software.bernie.geckolib.renderer.base.RenderPassInfo;

/**
 * Rendu d'un fusil. À la première personne, les bras du joueur (avec sa skin) sont dessinés sur les
 * os {@code right_arm} / {@code left_arm} du modèle, que l'animation de l'arme déplace : le joueur
 * voit ses mains manipuler l'arme, recharger, épauler. Les états de l'arme ajustent les os
 * (chien abaissé quand vide, magasin qui glisse, accessoires visibles seulement s'ils sont installés).
 */
public class GunItemRenderer extends GeoItemRenderer<FusilItem> {
	public GunItemRenderer(FusilItem item) {
		super(new GunGeoModel(item));
	}

	@Override
	public void captureDefaultRenderState(FusilItem animatable, RenderData renderData, GeoRenderState renderState, float partialTick) {
		super.captureDefaultRenderState(animatable, renderData, renderState, partialTick);
		ItemStack stack = renderData.itemStack();
		ComposedShot composed = ShotComposer.compose(null, stack);
		renderState.addGeckolibData(GunRenderTickets.MAGAZINE, FusilItem.magazine(stack).count());
		renderState.addGeckolibData(GunRenderTickets.CAPACITY, FusilItem.magazineCapacity(stack, composed));
		renderState.addGeckolibData(GunRenderTickets.ADJUSTER, composed.profile().adjuster());
		renderState.addGeckolibData(GunRenderTickets.SCOPE, composed.scope());
		GunVariant variant = composed.variant();
		if (variant != null) {
			variant.texture().ifPresent(t -> renderState.addGeckolibData(GunRenderTickets.TEXTURE, t));
			variant.model().ifPresent(m -> renderState.addGeckolibData(GunRenderTickets.MODEL, m));
		}
		AnimationController<?> controller = animatable.getAnimatableInstanceCache().getManagerForId(GeoItem.getId(stack))
				.getAnimationControllers().get(FusilItem.ACTIONS_CONTROLLER);
		double reloadSeconds = 0.0;
		if (controller != null && controller.isTriggeredAnimation("reload")) reloadSeconds = controller.getCurrentAnimationTime();
		ReloadState rs = stack.get(FusilsComponents.RELOAD);
		if (rs != null && reloadSeconds <= 0.0) reloadSeconds = Math.max(0.001, rs.progress());
		renderState.addGeckolibData(GunRenderTickets.RELOAD_SECONDS, reloadSeconds);

		LivingEntity owner = renderData.itemOwner() instanceof LivingEntity l ? l : Minecraft.getInstance().player;
		GunProfile.HandOccupancy occupancy = owner != null ? FusilItem.currentOccupancy(owner, stack) : FusilItem.currentOccupancy(stack);
		renderState.addGeckolibData(GunRenderTickets.OCCUPANCY, occupancy);

		ItemDisplayContext perspective = renderData.renderPerspective();
		boolean localFirstPerson = perspective != null && perspective.firstPerson() && owner == Minecraft.getInstance().player;
		renderState.addGeckolibData(GunRenderTickets.LOCAL_FIRST_PERSON, localFirstPerson);
		renderState.addGeckolibData(GunRenderTickets.AIM, localFirstPerson ? AimState.progress(partialTick) : 0.0F);
		List<Float> off = composed.profile().aimOffset();
		renderState.addGeckolibData(GunRenderTickets.AIM_OFFSET, new float[] {
				off.size() > 0 ? off.get(0) : 0.0F, off.size() > 1 ? off.get(1) : 0.0F, off.size() > 2 ? off.get(2) : 0.0F });
	}

	@Override
	public void preRenderPass(RenderPassInfo<GeoRenderState> info, SubmitNodeCollector collector) {
		super.preRenderPass(info, collector);
		if (!isFirstPerson(info.renderState())) return;
		AbstractClientPlayer player = Minecraft.getInstance().player;
		if (player == null) return;
		if (!(Minecraft.getInstance().getEntityRenderDispatcher().getRenderer(player) instanceof AvatarRenderer<?> avatar)) return;
		PlayerModel model = (PlayerModel) avatar.getModel();
		RenderType renderType = RenderTypes.entityTranslucent(player.getSkin().body().texturePath());
		info.model().getBone("right_arm").ifPresent(bone -> info.addPerBoneRender(bone,
				(pass, b, tasks) -> renderArm(tasks, renderType, model.rightArm, model.rightSleeve, pass)));
		GunProfile.HandOccupancy occupancy = info.getOrDefaultGeckolibData(GunRenderTickets.OCCUPANCY, GunProfile.HandOccupancy.BOTH);
		if (occupancy == GunProfile.HandOccupancy.BOTH) {
			info.model().getBone("left_arm").ifPresent(bone -> info.addPerBoneRender(bone,
					(pass, b, tasks) -> renderArm(tasks, renderType, model.leftArm, model.leftSleeve, pass)));
		}
	}

	private static boolean armLogged;

	private void renderArm(SubmitNodeCollector tasks, RenderType renderType, ModelPart arm, ModelPart sleeve, RenderPassInfo<GeoRenderState> pass) {
		if (!armLogged) {
			armLogged = true;
			net.hautecapitale.fusils.HauteCapitaleFusils.LOGGER.info("[fusils] bras 1re personne rendu (pose = {})", pass.poseStack().last().pose());
		}
		arm.x = 0; arm.y = 0; arm.z = 0;
		arm.xRot = 0; arm.yRot = 0; arm.zRot = 0;
		sleeve.x = 0; sleeve.y = 0; sleeve.z = 0;
		sleeve.xRot = 0; sleeve.yRot = 0; sleeve.zRot = 0;
		PoseStack poseStack = new PoseStack();
		poseStack.last().set(pass.poseStack().last());
		poseStack.scale(-1.0F, -1.0F, 1.0F);
		poseStack.translate(0.0625F, -0.625F, 0.0F);
		tasks.submitModelPart(arm, poseStack, renderType, pass.packedLight(), OverlayTexture.NO_OVERLAY, null);
		tasks.submitModelPart(sleeve, poseStack, renderType, pass.packedLight(), OverlayTexture.NO_OVERLAY, null);
	}

	@Override
	public void adjustRenderPose(RenderPassInfo<GeoRenderState> info) {
		super.adjustRenderPose(info);
		ItemDisplayContext perspective = info.renderState().getGeckolibData(DataTickets.ITEM_RENDER_PERSPECTIVE);
		if (perspective != null && perspective.leftHand()) {
			PoseStack.Pose last = info.poseStack().last();
			last.pose().scale(-1.0F, 1.0F, 1.0F);
			Matrix3f normal = last.normal();
			normal.scale(-1.0F, -1.0F, 1.0F);
		}
	}

	@Override
	public void adjustModelBonesForRender(RenderPassInfo<GeoRenderState> info, BoneSnapshots snapshots) {
		super.adjustModelBonesForRender(info, snapshots);
		ItemDisplayContext perspective = info.renderState().getGeckolibData(DataTickets.ITEM_RENDER_PERSPECTIVE);
		boolean inHand = perspective == ItemDisplayContext.FIRST_PERSON_RIGHT_HAND || perspective == ItemDisplayContext.FIRST_PERSON_LEFT_HAND
				|| perspective == ItemDisplayContext.THIRD_PERSON_RIGHT_HAND || perspective == ItemDisplayContext.THIRD_PERSON_LEFT_HAND;
		if (!inHand) {
			silenceAll(info, snapshots);
		} else if (!isFirstPerson(info.renderState())) {
			snapshots.get("root").ifPresent(root -> {
				root.setTranslation(0, 0, 0);
				root.setRotation(0, 0, 0);
			});
		}
		applyAdjusters(info, snapshots);
		applyAttachments(info, snapshots);
		applyAim(info, snapshots);
	}

	private void silenceAll(RenderPassInfo<GeoRenderState> info, BoneSnapshots snapshots) {
		List<String> names = new ArrayList<>();
		collect(info.model().topLevelBones(), names);
		for (String name : names) {
			if (name.contains("hammer") || name.contains("chien")) continue;
			snapshots.get(name).ifPresent(s -> {
				s.setTranslation(0, 0, 0);
				s.setRotation(0, 0, 0);
			});
		}
	}

	private static void collect(GeoBone[] bones, List<String> out) {
		for (GeoBone bone : bones) {
			if (bone == null) continue;
			out.add(bone.name());
			collect(bone.children(), out);
		}
	}

	private void applyAdjusters(RenderPassInfo<GeoRenderState> info, BoneSnapshots snapshots) {
		GunProfile.Adjuster adjuster = info.getOrDefaultGeckolibData(GunRenderTickets.ADJUSTER, GunProfile.Adjuster.NONE);
		int magazine = info.getOrDefaultGeckolibData(GunRenderTickets.MAGAZINE, 0);
		int capacity = Math.max(1, info.getOrDefaultGeckolibData(GunRenderTickets.CAPACITY, 1));
		double reload = info.getOrDefaultGeckolibData(GunRenderTickets.RELOAD_SECONDS, 0.0);
		switch (adjuster) {
			case LOWER_HAMMER -> {
				if (magazine <= 0 && reload <= 0.0) {
					snapshots.get("hammer").ifPresent(h -> h.setRotation(0, 0, 0));
				}
			}
			case DOUBLE_HAMMER -> {
				if (reload <= 1.1) {
					if (magazine <= 1) snapshots.get("hammer_left").ifPresent(h -> h.setRotation(0, 0, 0));
					if (magazine <= 0) snapshots.get("hammer_right").ifPresent(h -> h.setRotation(0, 0, 0));
				}
			}
			case MAGAZINE_SLIDE -> {
				if (reload <= 0.4) {
					float percent = 1.0F - (float) magazine / capacity;
					snapshots.get("magazine").ifPresent(m -> m.setTranslation(4.0F * percent, 0, 0));
				}
			}
			default -> {}
		}
	}

	private void applyAttachments(RenderPassInfo<GeoRenderState> info, BoneSnapshots snapshots) {
		boolean scope = info.getOrDefaultGeckolibData(GunRenderTickets.SCOPE, false);
		if (!scope) {
			snapshots.get("attachment_optic").ifPresent(b -> b.setScale(0.0F, 0.0F, 0.0F));
		}
	}

	private void applyAim(RenderPassInfo<GeoRenderState> info, BoneSnapshots snapshots) {
		if (!isFirstPerson(info.renderState())) return;
		float aim = info.getOrDefaultGeckolibData(GunRenderTickets.AIM, 0.0F);
		if (aim <= 0.0F) return;
		float[] off = info.getOrDefaultGeckolibData(GunRenderTickets.AIM_OFFSET, new float[3]);
		Optional<BoneSnapshot> root = snapshots.get("root");
		root.ifPresent(r -> r.setTranslation(off[0] * aim, off[1] * aim, off[2] * aim));
	}

	private boolean isFirstPerson(GeoRenderState state) {
		ItemDisplayContext p = state.getGeckolibData(DataTickets.ITEM_RENDER_PERSPECTIVE);
		return p == ItemDisplayContext.FIRST_PERSON_RIGHT_HAND || p == ItemDisplayContext.FIRST_PERSON_LEFT_HAND;
	}

	@SuppressWarnings("unused")
	private static HumanoidArm armOf(ItemDisplayContext p) {
		return p != null && p.leftHand() ? HumanoidArm.LEFT : HumanoidArm.RIGHT;
	}
}
