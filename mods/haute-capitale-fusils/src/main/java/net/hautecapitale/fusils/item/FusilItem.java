package net.hautecapitale.fusils.item;

import java.util.List;
import java.util.function.Consumer;
import java.util.function.Function;

import net.hautecapitale.fusils.FusilsIds;
import net.hautecapitale.fusils.data.ShotStats;
import net.hautecapitale.fusils.data.Stat;
import net.hautecapitale.fusils.data.component.ChargedAmmo;
import net.hautecapitale.fusils.data.component.Magazine;
import net.hautecapitale.fusils.data.component.ReloadState;
import net.hautecapitale.fusils.gun.AmmoType;
import net.hautecapitale.fusils.gun.ComposedShot;
import net.hautecapitale.fusils.gun.FusilsData;
import net.hautecapitale.fusils.gun.GunModification;
import net.hautecapitale.fusils.gun.GunProfile;
import net.hautecapitale.fusils.gun.GunVariant;
import net.hautecapitale.fusils.gun.GunplayManager;
import net.hautecapitale.fusils.gun.ShotComposer;
import net.hautecapitale.fusils.registry.FusilsComponents;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.Mth;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.ItemUseAnimation;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.component.TooltipDisplay;
import net.minecraft.world.level.Level;
import software.bernie.geckolib.animatable.GeoItem;
import software.bernie.geckolib.animatable.client.GeoRenderProvider;
import software.bernie.geckolib.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.animatable.manager.AnimatableManager;
import software.bernie.geckolib.animation.AnimationController;
import software.bernie.geckolib.animation.RawAnimation;
import software.bernie.geckolib.animation.object.PlayState;
import software.bernie.geckolib.animation.state.AnimationTest;
import software.bernie.geckolib.model.GeoModel;
import software.bernie.geckolib.renderer.base.GeoRenderState;
import software.bernie.geckolib.util.GeckoLibUtil;

/**
 * Une arme à feu. Toute sa mécanique vient de son profil de données ({@link GunProfile}) et de
 * ses composants ; la classe ne porte que l'identifiant du profil et le rendu GeckoLib.
 */
public class FusilItem extends Item implements GeoItem {
	public static final String IDLE_CONTROLLER = "idle";
	public static final String ACTIONS_CONTROLLER = "actions";
	public static final int AIM_USE_DURATION = 72000;

	/** Posé par le client : fabrique le rendu GeckoLib (jamais chargé sur un serveur dédié). */
	public static Function<FusilItem, GeoRenderProvider> rendererFactory;

	private final Identifier profileId;
	private final AnimatableInstanceCache cache = GeckoLibUtil.createInstanceCache(this);

	public FusilItem(Properties properties, Identifier profileId) {
		super(properties.stacksTo(1).component(FusilsComponents.MAGAZINE, Magazine.EMPTY));
		this.profileId = profileId;
	}

	public Identifier profileId() {
		return this.profileId;
	}

	public GunProfile profile() {
		return FusilsData.profile(this.profileId);
	}

	// --- Accès aux données de l'objet ------------------------------------------

	public static boolean isGun(ItemStack stack) {
		return stack.getItem() instanceof FusilItem;
	}

	public static GunVariant variantOf(ItemStack stack) {
		Identifier v = stack.get(FusilsComponents.VARIANT);
		return v == null ? null : FusilsData.variant(v);
	}

	/** Profil effectif : celui de la variante si l'objet en porte une, sinon celui de l'item. */
	public static GunProfile profileOf(ItemStack stack) {
		GunVariant variant = variantOf(stack);
		if (variant != null && FusilsData.hasProfile(variant.profile())) {
			return FusilsData.profile(variant.profile());
		}
		return stack.getItem() instanceof FusilItem gun ? gun.profile() : GunProfile.fallback("mousquet");
	}

	public static int magazineCapacity(ItemStack stack, ComposedShot composed) {
		GunVariant variant = variantOf(stack);
		int base = variant != null && variant.magazine().isPresent() ? variant.magazine().get() : profileOf(stack).magazine();
		return Math.max(1, base + (int) Math.round(composed.stats().get(Stat.MAGAZINE_BONUS)));
	}

	public static int reloadTicks(ItemStack stack) {
		GunVariant variant = variantOf(stack);
		return variant != null && variant.reloadTicks().isPresent() ? variant.reloadTicks().get() : profileOf(stack).reloadTicks();
	}

	public static Magazine magazine(ItemStack stack) {
		return stack.getOrDefault(FusilsComponents.MAGAZINE, Magazine.EMPTY);
	}

	public static void setMagazine(ItemStack stack, Magazine magazine) {
		stack.set(FusilsComponents.MAGAZINE, magazine);
	}

	public static boolean isReloading(ItemStack stack) {
		return stack.has(FusilsComponents.RELOAD);
	}

	public static boolean isDelaying(ItemStack stack) {
		return stack.has(FusilsComponents.FIRE_DELAY);
	}

	public static boolean isAiming(LivingEntity entity) {
		return entity.isUsingItem() && isGun(entity.getUseItem());
	}

	/** Visée à la lunette : viser avec une modification « longue-vue » installée. */
	public static boolean isScoping(LivingEntity entity) {
		return isAiming(entity) && ShotComposer.compose(entity, entity.getUseItem()).scope();
	}

	public static GunProfile.HandOccupancy currentOccupancy(ItemStack stack) {
		GunProfile profile = profileOf(stack);
		if (isReloading(stack)) return profile.occupancyFor("reload");
		if (isDelaying(stack)) return profile.occupancyFor("fire");
		return profile.defaultOccupancy();
	}

	public static GunProfile.HandOccupancy currentOccupancy(LivingEntity entity, ItemStack stack) {
		GunProfile.HandOccupancy o = currentOccupancy(stack);
		return o == GunProfile.HandOccupancy.BOTH && stack == entity.getOffhandItem() && !entity.getMainHandItem().isEmpty()
				? GunProfile.HandOccupancy.MAINHAND : o;
	}

	public static boolean isOffhandBlocked(LivingEntity entity) {
		ItemStack main = entity.getMainHandItem();
		return isGun(main) && currentOccupancy(entity, main) == GunProfile.HandOccupancy.BOTH;
	}

	// --- Comportement vanilla ---------------------------------------------------

	@Override
	public InteractionResult use(Level level, Player player, InteractionHand hand) {
		ItemStack stack = player.getItemInHand(hand);
		if (hand != InteractionHand.MAIN_HAND || isReloading(stack)) {
			return InteractionResult.FAIL;
		}
		player.startUsingItem(hand);
		return InteractionResult.CONSUME;
	}

	@Override
	public ItemUseAnimation getUseAnimation(ItemStack stack) {
		return ItemUseAnimation.NONE;
	}

	@Override
	public int getUseDuration(ItemStack stack, LivingEntity user) {
		return AIM_USE_DURATION;
	}

	@Override
	public boolean releaseUsing(ItemStack stack, Level level, LivingEntity entity, int remaining) {
		return true;
	}

	@Override
	public ItemStack finishUsingItem(ItemStack stack, Level level, LivingEntity entity) {
		return stack;
	}

	@Override
	public void inventoryTick(ItemStack stack, ServerLevel level, Entity entity, EquipmentSlot slot) {
		if (slot == EquipmentSlot.MAINHAND && entity instanceof LivingEntity living) {
			GunplayManager.tickHeld(stack, living);
		}
	}

	@Override
	public boolean isBarVisible(ItemStack stack) {
		return true;
	}

	@Override
	public int getBarWidth(ItemStack stack) {
		if (isReloading(stack)) {
			ReloadState r = stack.get(FusilsComponents.RELOAD);
			return (int) (r.percent(0.0F) * 13.0F);
		}
		int capacity = magazineCapacity(stack, ShotComposer.compose(null, stack));
		return Mth.clamp(Math.round(magazine(stack).count() * 13.0F / capacity), 0, 13);
	}

	@Override
	public int getBarColor(ItemStack stack) {
		return isReloading(stack) ? 0xAAAAAA : 0xFFAA00;
	}

	@Override
	public Component getName(ItemStack stack) {
		GunVariant variant = variantOf(stack);
		if (variant != null) {
			Component base = variant.name().orElseGet(() -> super.getName(stack));
			return base.copy().withStyle(variant.rarity().color);
		}
		return super.getName(stack);
	}

	@Override
	public void appendHoverText(ItemStack stack, TooltipContext context, TooltipDisplay display, Consumer<Component> lines, TooltipFlag flag) {
		ComposedShot composed = ShotComposer.compose(null, stack);
		ShotStats stats = composed.stats();
		GunVariant variant = variantOf(stack);
		if (variant != null) {
			lines.accept(Component.translatable(variant.rarity().translationKey()).withStyle(variant.rarity().color, ChatFormatting.ITALIC));
			variant.lore().forEach(l -> lines.accept(l.copy().withStyle(ChatFormatting.GRAY, ChatFormatting.ITALIC)));
		}
		lines.accept(Component.empty());
		int count = (int) Math.round(stats.get(Stat.PROJECTILE_COUNT));
		String dmg = fmt(stats.get(Stat.DAMAGE) / Math.max(1, count));
		if (count > 1) {
			lines.accept(stat("degats_plombs", dmg, String.valueOf(count)));
		} else {
			lines.accept(stat("degats", dmg));
		}
		double delay = stats.fireDelayTicks();
		int capacity = magazineCapacity(stack, composed);
		if (capacity > 1) {
			lines.accept(stat("cadence", fmt(20.0 / Math.max(1.0, delay))));
		}
		lines.accept(stat("chargeur", capacity + ""));
		lines.accept(stat("rechargement", fmt(reloadTicks(stack) / 20.0 / Math.max(0.01, stats.get(Stat.RELOAD_SPEED))) + " s"));
		lines.accept(stat("precision", fmt(stats.get(Stat.SPREAD)) + "°"));
		lines.accept(stat("portee", fmt(stats.get(Stat.RANGE))));
		double crit = stats.get(Stat.CRIT_CHANCE);
		if (crit > 0) lines.accept(stat("critique", Math.round(crit * 100) + " % ×" + fmt(stats.get(Stat.CRIT_MULTIPLIER))));
		double pierce = stats.get(Stat.ARMOR_PIERCE);
		if (pierce > 0) lines.accept(stat("perforation_armure", Math.round(pierce * 100) + " %"));
		double piercing = stats.get(Stat.PIERCING);
		if (piercing > 0) lines.accept(stat("perforation", fmt(piercing)));
		lines.accept(stat("recul", fmt(composed.profile().recoil().magnitude() * stats.get(Stat.RECOIL_MULTIPLIER)) + "°"));

		List<Identifier> mods = composed.installedIds();
		if (!mods.isEmpty()) {
			lines.accept(Component.empty());
			for (Identifier id : mods) {
				GunModification mod = FusilsData.modification(id);
				if (mod != null) {
					lines.accept(Component.literal(" ◆ ").withStyle(ChatFormatting.GOLD)
							.append(Component.translatable(mod.category().translationKey()).withStyle(ChatFormatting.GRAY))
							.append(" : ").append(mod.name().copy().withStyle(ChatFormatting.YELLOW)));
				}
			}
		}
		ChargedAmmo charged = stack.get(FusilsComponents.CHARGED_AMMO);
		if (charged != null && charged.shots() > 0) {
			AmmoType ammo = FusilsData.ammo(charged.ammo());
			if (ammo != null) {
				lines.accept(Component.empty());
				lines.accept(Component.translatable("tooltip.haute_capitale_fusils.munition_chargee", ammo.name().copy().withColor(ammo.color()), charged.shots())
						.withStyle(ChatFormatting.AQUA));
			}
		}
		if (flag.isAdvanced()) {
			lines.accept(Component.literal("profil " + composed.profileId()).withStyle(ChatFormatting.DARK_GRAY));
		}
	}

	private static Component stat(String key, Object... args) {
		return Component.literal(" ").append(Component.translatable("tooltip.haute_capitale_fusils." + key, args).withStyle(ChatFormatting.DARK_GREEN));
	}

	private static String fmt(double v) {
		return v == Math.floor(v) ? String.valueOf((long) v) : String.format(java.util.Locale.ROOT, "%.1f", v);
	}

	// --- GeckoLib ---------------------------------------------------------------

	@Override
	public void registerControllers(AnimatableManager.ControllerRegistrar controllers) {
		controllers.add(new AnimationController<>(IDLE_CONTROLLER, this::idle));
		controllers.add(new OffsetableController<>(ACTIONS_CONTROLLER, test -> PlayState.STOP)
				.triggerableAnim("fire", RawAnimation.begin().thenPlay("fire"))
				.triggerableAnim("reload", RawAnimation.begin().thenPlay("reload"))
				.triggerableAnim("equip", RawAnimation.begin().thenPlay("equip")));
	}

	private PlayState idle(AnimationTest<FusilItem> test) {
		test.setAnimation(RawAnimation.begin().thenPlayAndHold("idle"));
		return PlayState.CONTINUE;
	}

	@Override
	public AnimatableInstanceCache getAnimatableInstanceCache() {
		return this.cache;
	}

	@Override
	public void createGeoRenderer(Consumer<GeoRenderProvider> consumer) {
		if (rendererFactory != null) {
			consumer.accept(rendererFactory.apply(this));
		}
	}

	/** Programme un saut de ligne de temps sur le contrôleur d'actions (rechargement partiel). */
	public void configureSkip(long instanceId, double skipAt, double skipTo) {
		AnimationController<?> c = this.cache.getManagerForId(instanceId).getAnimationControllers().get(ACTIONS_CONTROLLER);
		if (c instanceof OffsetableController<?> oc) {
			oc.setSkip(skipAt, skipTo);
		}
	}

	/**
	 * Contrôleur qui accepte de démarrer une animation à un temps donné (reprise d'une recharge
	 * en cours après un changement d'objet) et de sauter un segment (chambres déjà pleines).
	 */
	public static class OffsetableController<T extends software.bernie.geckolib.animatable.GeoAnimatable> extends AnimationController<T> {
		private double skipAt;
		private double skipTo;
		private boolean skipped;

		public OffsetableController(String name, AnimationStateHandler<T> handler) {
			super(name, handler);
		}

		public void setSkip(double skipAt, double skipTo) {
			this.skipAt = skipAt;
			this.skipTo = skipTo;
			this.skipped = false;
		}

		private boolean applySkip() {
			if (!this.skipped && this.skipTo > this.skipAt && this.timelineTime >= this.skipAt && this.timelineTime < this.skipTo) {
				this.timelineTime = this.skipTo;
				this.skipped = true;
				return true;
			}
			return false;
		}

		@Override
		protected void initializeNewAnimation(T animatable, GeoRenderState renderState, GeoModel<T> model, double prevSpeed, int prevTransitionTicks) {
			double offset = this.timelineTime;
			super.initializeNewAnimation(animatable, renderState, model, prevSpeed, prevTransitionTicks);
			if (offset > 0.0) {
				this.timelineTime = offset;
			}
			applySkip();
		}

		@Override
		protected void progressExistingAnimation(T animatable, GeoRenderState renderState, double prevTimelineTime, double timeAdvanced) {
			if (applySkip()) {
				prevTimelineTime = this.timelineTime;
			}
			super.progressExistingAnimation(animatable, renderState, prevTimelineTime, timeAdvanced);
		}
	}

	public static Identifier id(String path) {
		return FusilsIds.id(path);
	}
}
