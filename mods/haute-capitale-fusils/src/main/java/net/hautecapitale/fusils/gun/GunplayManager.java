package net.hautecapitale.fusils.gun;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import net.fabricmc.fabric.api.networking.v1.PlayerLookup;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.hautecapitale.fusils.config.FusilsConfig;
import net.hautecapitale.fusils.data.ShotStats;
import net.hautecapitale.fusils.data.Stat;
import net.hautecapitale.fusils.data.component.ChargedAmmo;
import net.hautecapitale.fusils.data.component.FireDelayState;
import net.hautecapitale.fusils.data.component.Magazine;
import net.hautecapitale.fusils.data.component.ReloadState;
import net.hautecapitale.fusils.entity.BulletEntity;
import net.hautecapitale.fusils.item.FusilItem;
import net.hautecapitale.fusils.network.payload.CancelAnimPayload;
import net.hautecapitale.fusils.network.payload.GunAnimPayload;
import net.hautecapitale.fusils.network.payload.GunshotPayload;
import net.hautecapitale.fusils.network.payload.LocalSoundPayload;
import net.hautecapitale.fusils.network.payload.MuzzleFlashPayload;
import net.hautecapitale.fusils.network.payload.RecoilPayload;
import net.hautecapitale.fusils.registry.FusilsComponents;
import net.hautecapitale.fusils.registry.FusilsEntities;
import net.hautecapitale.fusils.registry.FusilsItems;
import net.hautecapitale.fusils.registry.FusilsSounds;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.Mth;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.HumanoidArm;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.phys.Vec2;
import net.minecraft.world.phys.Vec3;
import software.bernie.geckolib.animatable.GeoItem;

/**
 * La boucle de jeu d'une arme : tir, délai, recharge, munitions, charges spéciales, rafales.
 * Le client prédit (pour la réactivité), le serveur décide (munitions, délai, direction bornée).
 */
public final class GunplayManager {
	public enum ReloadResult { STARTED, FINISHED, NO_AMMO, ALREADY_FULL, BUSY }

	/** Options d'un tir déclenché par une compétence. */
	public record FireOptions(boolean ignoreDelay, boolean consumeAmmo, boolean requireAmmo, String skill, double damageMultiplier) {
		public static final FireOptions NORMAL = new FireOptions(false, true, true, null, 1.0);
	}

	private record PendingBurst(LivingEntity shooter, int remaining, int interval, int countdown, FireOptions options) {}

	private static final List<PendingBurst> BURSTS = new ArrayList<>();

	/** Posé par le client : joue le « clic » à vide localement. */
	public static java.util.function.BiConsumer<LivingEntity, ComposedShot> clientDryFire;

	private GunplayManager() {}

	// --- Tir --------------------------------------------------------------------

	/** Point d'entrée du clic (client : prédiction ; serveur : autorité). */
	public static boolean tryFire(LivingEntity shooter, Vec3 direction) {
		return tryFire(shooter, direction, FireOptions.NORMAL);
	}

	public static boolean tryFire(LivingEntity shooter, Vec3 direction, FireOptions options) {
		if (!shooter.isAlive() || shooter.isSpectator()) return false;
		ItemStack stack = shooter.getMainHandItem();
		if (!(stack.getItem() instanceof FusilItem gunItem)) return false;
		if (FusilItem.isReloading(stack)) return false;
		if (!options.ignoreDelay() && FusilItem.isDelaying(stack)) return false;

		if (options.skill() != null) stack.set(FusilsComponents.SKILL_SHOT, options.skill());
		ComposedShot composed = ShotComposer.compose(shooter, stack);
		ShotStats stats = composed.stats();
		Magazine magazine = FusilItem.magazine(stack);
		boolean creative = shooter instanceof Player p && p.hasInfiniteMaterials();
		boolean needsRound = options.requireAmmo() && !creative;
		if (needsRound && magazine.isEmpty()) {
			if (shooter.level().isClientSide() && clientDryFire != null) {
				clientDryFire.accept(shooter, composed);
			}
			stack.remove(FusilsComponents.SKILL_SHOT);
			return false;
		}

		float pitchMul = (float) ((stats.get(Stat.FIRE_RATE) + 2.0) / 3.0);
		int delay = (int) Math.round(stats.fireDelayTicks());
		if (delay > 0) stack.set(FusilsComponents.FIRE_DELAY, new FireDelayState(0, delay, 0, pitchMul));

		if (!(shooter.level() instanceof ServerLevel level)) {
			return true; // prédiction client : le serveur confirme par ses paquets
		}

		long now = level.getGameTime();
		Vec3 aim = clampDirection(shooter, direction);
		RecoilState recoil = RecoilState.current(shooter, now);
		Vec2 rot = aim.rotation();
		Vec3 finalDir = Vec3.directionFromRotation(rot.x - recoil.pitch(), rot.y + recoil.yaw());

		int capacity = FusilItem.magazineCapacity(stack, composed);
		int bulletIndex = capacity - magazine.count();
		if (options.consumeAmmo() && !creative && level.getRandom().nextDouble() < stats.get(Stat.AMMO_CONSUME_CHANCE)) {
			FusilItem.setMagazine(stack, magazine.deplete(1));
		}
		ChargedAmmo charged = stack.get(FusilsComponents.CHARGED_AMMO);
		if (charged != null) {
			ChargedAmmo next = charged.consume();
			if (next.shots() <= 0) stack.remove(FusilsComponents.CHARGED_AMMO);
			else stack.set(FusilsComponents.CHARGED_AMMO, next);
		}
		stack.remove(FusilsComponents.SKILL_SHOT);

		if (options.damageMultiplier() != 1.0) {
			stats.value(Stat.DAMAGE).add(net.hautecapitale.fusils.data.StatModifier.mul(options.damageMultiplier() - 1.0));
		}

		playGunshot(level, shooter, composed);
		RecoilState.addImpulse(shooter, now, stats, composed.profile().recoil(), bulletIndex);
		fireShot(level, shooter, shooter.getEyePosition(), finalDir, composed);
		applyBlowback(shooter, stats);
		playFireAnimation(level, shooter, stack, stats);
		ShotComposer.recordShot(shooter);

		if (shooter instanceof ServerPlayer player && options.skill() != null) {
			// Tir déclenché par le serveur : le client n'a pas prédit, on lui envoie le recul.
			Vec2 cam = RecoilMath.fullRecoil(stats, composed.profile().recoil(), bulletIndex);
			ServerPlayNetworking.send(player, new RecoilPayload(cam.x, cam.y));
		}
		if (shooter.isUsingItem() && shooter.getUseItem() != stack && FusilItem.isOffhandBlocked(shooter)) {
			shooter.stopUsingItem();
		}
		return true;
	}

	/** Le client donne sa direction ; le serveur la borne autour de la vue qu'il connaît. */
	private static Vec3 clampDirection(LivingEntity shooter, Vec3 direction) {
		Vec3 known = shooter.getLookAngle();
		double tolerance = Math.cos(Math.toRadians(FusilsConfig.INSTANCE.aim_tolerance_degrees));
		Vec3 d = direction.normalize();
		return d.dot(known) >= tolerance ? d : known;
	}

	public static void fireShot(ServerLevel level, LivingEntity shooter, Vec3 origin, Vec3 direction, ComposedShot composed) {
		ShotStats stats = composed.stats();
		int count = Math.max(1, (int) Math.round(stats.get(Stat.PROJECTILE_COUNT)));
		float speed = (float) stats.get(Stat.BULLET_SPEED);
		float spread = spreadFor(composed, shooter);
		java.util.UUID shotId = java.util.UUID.randomUUID();
		for (int i = 0; i < count; i++) {
			BulletEntity bullet = new BulletEntity(FusilsEntities.BULLET, level);
			bullet.setOwner(shooter);
			bullet.applyStats(stats.copy());
			bullet.setShot(shotId, i);
			bullet.setPos(origin);
			bullet.shoot(direction.x, direction.y, direction.z, speed, spread);
			level.addFreshEntity(bullet);
		}
		spawnMuzzleFlash(level, shooter, direction, composed);
	}

	private static void spawnMuzzleFlash(ServerLevel level, LivingEntity shooter, Vec3 direction, ComposedShot composed) {
		GunProfile.MuzzleFlash mf = composed.profile().muzzleFlash();
		if (mf.types().isEmpty()) return;
		String type = mf.types().get(level.getRandom().nextInt(mf.types().size()));
		float side = shooter.getMainArm() == HumanoidArm.LEFT ? -1.0F : 1.0F;
		Vec3 offset = direction.normalize().scale(Math.max(1.25, 0.75 * mf.distance()))
				.add(shooter.getForward().cross(new Vec3(0, 1, 0)).scale(0.35 * side)).add(0, -0.15, 0);
		MuzzleFlashPayload payload = new MuzzleFlashPayload(type, composed.stats().muzzleTint(), shooter.getId(), shooter.getDeltaMovement(), shooter.getEyePosition(), offset);
		broadcastTracking(shooter, payload);
	}

	/** Dispersion effective : base, mouvement, accroupi, en l'air, visée. */
	public static float spreadFor(ComposedShot composed, Entity entity) {
		ShotStats stats = composed.stats();
		float spread = (float) stats.get(Stat.SPREAD);
		if (entity instanceof LivingEntity living && FusilItem.isAiming(living)) {
			spread *= (float) stats.get(Stat.AIM_SPREAD_MULTIPLIER);
		}
		if (entity.isCrouching()) spread *= 0.667F;
		if (!entity.onGround()) spread *= (float) stats.get(Stat.IN_AIR_PENALTY);
		Vec3 movement = entity.position().subtract(entity.xOld, entity.yOld, entity.zOld);
		float speed = (float) movement.length();
		if (speed > 0.1F) {
			spread += Mth.clamp(7.5F * speed - 0.05F, 0.0F, 20.0F);
		}
		return Math.max(0.0F, spread);
	}

	private static void applyBlowback(LivingEntity living, ShotStats stats) {
		float strength = (float) stats.get(Stat.BLOWBACK);
		if (strength <= 0.0F) return;
		Vec3 look = living.getForward();
		living.push(-look.x * strength, -look.y * strength * 0.5 + 0.05, -look.z * strength);
		if (living instanceof Player) {
			double f = Mth.clamp((living.getDeltaMovement().y + 0.5) / 0.4, 0.0, 1.0);
			living.fallDistance *= f;
		}
		living.hurtMarked = true;
	}

	private static void playGunshot(ServerLevel level, LivingEntity shooter, ComposedShot composed) {
		GunProfile.Sounds sounds = composed.profile().sounds();
		Vec3 pos = shooter.position();
		float pitch = sounds.shoot().samplePitch(level.getRandom());
		float echoPitch = sounds.echo().samplePitch(level.getRandom());
		GunshotPayload payload = new GunshotPayload(pos, sounds.shoot().id(), sounds.shoot().volume(), pitch, sounds.echo().id(), sounds.echo().volume(), echoPitch);
		for (ServerPlayer p : PlayerLookup.around(level, pos, 192.0)) {
			ServerPlayNetworking.send(p, payload);
		}
	}

	private static void playFireAnimation(ServerLevel level, LivingEntity living, ItemStack stack, ShotStats stats) {
		double speedMul = stats.value(Stat.FIRE_DELAY).base() / Math.max(1.0, stats.fireDelayTicks());
		GunAnimPayload payload = new GunAnimPayload(living.getId(), GeoItem.getOrAssignId(stack, level), stack == living.getMainHandItem(),
				"fire", (speedMul + 1.0) / 2.0, 0.0, 0.0, 0.0);
		broadcastTracking(living, payload);
	}

	public static void playReloadAnimation(LivingEntity living, ItemStack stack) {
		if (!(living.level() instanceof ServerLevel level)) return;
		ReloadState state = stack.get(FusilsComponents.RELOAD);
		if (state == null) return;
		GunAnimPayload payload = new GunAnimPayload(living.getId(), GeoItem.getOrAssignId(stack, level), stack == living.getMainHandItem(),
				"reload", state.speed(), state.progress(), state.skipAt(), state.skipTo());
		broadcastTracking(living, payload);
	}

	public static void playEquipAnimation(ServerLevel level, LivingEntity living, ItemStack stack) {
		GunAnimPayload payload = new GunAnimPayload(living.getId(), GeoItem.getOrAssignId(stack, level), stack == living.getMainHandItem(), "equip", 1.0, 0.0, 0.0, 0.0);
		broadcastTracking(living, payload);
		GunProfile profile = FusilItem.profileOf(stack);
		if (profile.sounds().equip().isPresent() && living instanceof ServerPlayer player) {
			GunProfile.SoundRef s = profile.sounds().equip().get();
			ServerPlayNetworking.send(player, new LocalSoundPayload(s.id(), s.volume(), s.samplePitch(level.getRandom())));
		}
	}

	public static void cancelAnimation(LivingEntity living, ItemStack stack) {
		if (!(living.level() instanceof ServerLevel level)) return;
		broadcastTracking(living, new CancelAnimPayload(living.getId(), GeoItem.getOrAssignId(stack, level), stack == living.getMainHandItem()));
	}

	public static void broadcastTracking(Entity entity, CustomPacketPayload payload) {
		for (ServerPlayer p : PlayerLookup.tracking(entity)) {
			ServerPlayNetworking.send(p, payload);
		}
		if (entity instanceof ServerPlayer self) {
			ServerPlayNetworking.send(self, payload);
		}
	}

	// --- Tick de l'arme tenue ---------------------------------------------------

	/** Serveur : avance le délai de tir et la recharge de l'arme en main. */
	public static void tickHeld(ItemStack stack, LivingEntity living) {
		if (!(stack.getItem() instanceof FusilItem)) return;
		GunProfile profile = FusilItem.profileOf(stack);
		FireDelayState delay = stack.get(FusilsComponents.FIRE_DELAY);
		if (delay != null) {
			delay = delay.increment();
			int cue = SoundUtil.playDueCues(profile.sounds().fireCycleCues(), living, delay.percent(), delay.cueIndex(), delay.pitchMultiplier());
			delay = delay.withCue(cue);
			if (delay.isFinished()) stack.remove(FusilsComponents.FIRE_DELAY);
			else stack.set(FusilsComponents.FIRE_DELAY, delay);
		}
		ReloadState reload = stack.get(FusilsComponents.RELOAD);
		if (reload != null) {
			double previous = reload.progress();
			reload = reload.increment(1);
			SoundUtil.playCuesBetween(profile.sounds().reloadCues(), living, previous, reload.progress(), reload.pitchMultiplier());
			reload = reload.applySkip();
			if (reload.isFinished()) {
				stack.remove(FusilsComponents.RELOAD);
				ReloadResult result = finishReload(living, stack, reload.roundsToLoad());
				if (living instanceof Player player) reloadFeedback(player, result);
			} else {
				stack.set(FusilsComponents.RELOAD, reload);
			}
		}
	}

	/** Client : même avancement, sans sons ni fin de recharge (le serveur les envoie). */
	public static void tickHeldClient(ItemStack stack) {
		FireDelayState delay = stack.get(FusilsComponents.FIRE_DELAY);
		if (delay != null) {
			delay = delay.increment();
			if (delay.isFinished()) stack.remove(FusilsComponents.FIRE_DELAY);
			else stack.set(FusilsComponents.FIRE_DELAY, delay);
		}
		ReloadState reload = stack.get(FusilsComponents.RELOAD);
		if (reload != null && !reload.isFinished()) {
			stack.set(FusilsComponents.RELOAD, reload.increment(1).applySkip());
		}
	}

	// --- Recharge ---------------------------------------------------------------

	public static ReloadResult startReload(LivingEntity living, ItemStack stack) {
		if (!(stack.getItem() instanceof FusilItem)) return ReloadResult.NO_AMMO;
		if (FusilItem.isReloading(stack)) return ReloadResult.BUSY;
		ComposedShot composed = ShotComposer.compose(living, stack);
		int capacity = FusilItem.magazineCapacity(stack, composed);
		Magazine magazine = FusilItem.magazine(stack);
		int missing = magazine.missing(capacity);
		if (missing <= 0) return ReloadResult.ALREADY_FULL;
		boolean needsAmmo = needsAmmo(living);
		if (needsAmmo) {
			int available = countBullets((Player) living);
			if (available <= 0) return ReloadResult.NO_AMMO;
			missing = Math.min(missing, available);
		}
		if (living.level() instanceof ServerLevel) {
			double speed = composed.stats().get(Stat.RELOAD_SPEED);
			GunProfile.TopLoad topLoad = composed.profile().topLoad().orElse(null);
			boolean partial = topLoad != null && missing < capacity;
			double skipAt = 0, skipTo = 0;
			if (partial) {
				skipAt = topLoad.loopStart();
				skipTo = topLoad.resumeFrom(missing);
			}
			stack.set(FusilsComponents.RELOAD, new ReloadState(0.0, FusilItem.reloadTicks(stack) / 20.0, speed, missing, skipAt, skipTo));
			playReloadAnimation(living, stack);
		}
		if (living.isUsingItem()) living.stopUsingItem();
		return ReloadResult.STARTED;
	}

	public static ReloadResult finishReload(LivingEntity living, ItemStack stack, int rounds) {
		if (!(stack.getItem() instanceof FusilItem)) return ReloadResult.NO_AMMO;
		ComposedShot composed = ShotComposer.compose(living, stack);
		int capacity = FusilItem.magazineCapacity(stack, composed);
		Magazine magazine = FusilItem.magazine(stack);
		int missing = magazine.missing(capacity);
		if (missing <= 0) return ReloadResult.ALREADY_FULL;
		boolean needsAmmo = needsAmmo(living);
		int available = needsAmmo ? countBullets((Player) living) : missing;
		if (needsAmmo && available <= 0) return ReloadResult.NO_AMMO;
		int toLoad = Math.min(missing, available);
		if (rounds > 0) toLoad = Math.min(toLoad, rounds);
		if (needsAmmo) consumeBullets((Player) living, toLoad);
		FusilItem.setMagazine(stack, magazine.with(magazine.count() + toLoad));
		return ReloadResult.FINISHED;
	}

	/** Recharge instantanée de {@code rounds} coups (Repli tactique, commandes). */
	public static int instantReload(LivingEntity living, ItemStack stack, int rounds) {
		Magazine before = FusilItem.magazine(stack);
		stack.remove(FusilsComponents.RELOAD);
		finishReload(living, stack, rounds);
		int loaded = FusilItem.magazine(stack).count() - before.count();
		if (loaded > 0 && living.level() instanceof ServerLevel level) {
			level.playSound(null, living.getX(), living.getY(), living.getZ(), FusilsSounds.CHIEN, SoundSource.PLAYERS, 1.0F, 1.2F);
			cancelAnimation(living, stack);
		}
		return loaded;
	}

	private static boolean needsAmmo(LivingEntity living) {
		return living instanceof Player p && !p.hasInfiniteMaterials() && !FusilsConfig.INSTANCE.freeAmmo();
	}

	public static void reloadFeedback(Player player, ReloadResult result) {
		switch (result) {
			case NO_AMMO -> player.level().playSound(null, player.getX(), player.getY(), player.getZ(), SoundEvents.DISPENSER_FAIL, SoundSource.PLAYERS, 0.6F, 1.0F);
			case ALREADY_FULL -> player.level().playSound(null, player.getX(), player.getY(), player.getZ(), SoundEvents.NOTE_BLOCK_DIDGERIDOO.value(), SoundSource.PLAYERS, 0.6F, 1.0F);
			default -> {}
		}
	}

	public static int countBullets(Player player) {
		if (FusilsConfig.INSTANCE.freeAmmo() || player.hasInfiniteMaterials()) return 999;
		Inventory inv = player.getInventory();
		int total = 0;
		for (int i = 0; i < inv.getContainerSize(); i++) {
			ItemStack s = inv.getItem(i);
			if (isBullet(s)) total += s.getCount();
		}
		return total;
	}

	private static boolean isBullet(ItemStack s) {
		return !s.isEmpty() && (s.is(FusilsItems.BALLE) || s.is(FusilsItems.BALLES_TAG));
	}

	private static void consumeBullets(Player player, int amount) {
		Inventory inv = player.getInventory();
		int remaining = amount;
		for (int i = 0; i < inv.getContainerSize() && remaining > 0; i++) {
			ItemStack s = inv.getItem(i);
			if (isBullet(s)) {
				int take = Math.min(remaining, s.getCount());
				s.shrink(take);
				remaining -= take;
			}
		}
	}

	// --- Charges de munition spéciale ------------------------------------------

	public static boolean charge(LivingEntity living, ItemStack gun, net.minecraft.resources.Identifier ammoId, int shots) {
		if (!(gun.getItem() instanceof FusilItem)) return false;
		AmmoType ammo = FusilsData.ammo(ammoId);
		if (ammo == null) return false;
		gun.set(FusilsComponents.CHARGED_AMMO, new ChargedAmmo(ammoId, Math.max(1, shots)));
		if (living.level() instanceof ServerLevel level) {
			level.playSound(null, living.getX(), living.getY(), living.getZ(), FusilsSounds.CHARGE_MUNITION, SoundSource.PLAYERS, 0.9F, 1.0F);
			Vec3 c = living.getEyePosition().add(living.getForward().scale(0.6));
			level.sendParticles(ParticleTypes.ENCHANT, c.x, c.y, c.z, 12, 0.2, 0.2, 0.2, 0.3);
		}
		return true;
	}

	// --- Rafales ----------------------------------------------------------------

	public static void scheduleBurst(LivingEntity shooter, int shots, int interval, FireOptions options) {
		BURSTS.add(new PendingBurst(shooter, shots, Math.max(1, interval), 0, options));
	}

	public static void tickServer(MinecraftServer server) {
		BleedManager.tick(server);
		if (BURSTS.isEmpty()) return;
		Iterator<PendingBurst> it = BURSTS.iterator();
		List<PendingBurst> next = new ArrayList<>();
		while (it.hasNext()) {
			PendingBurst b = it.next();
			it.remove();
			if (!b.shooter().isAlive() || b.remaining() <= 0) continue;
			if (b.countdown() > 0) {
				next.add(new PendingBurst(b.shooter(), b.remaining(), b.interval(), b.countdown() - 1, b.options()));
				continue;
			}
			tryFire(b.shooter(), b.shooter().getLookAngle(), b.options());
			if (b.remaining() - 1 > 0) {
				next.add(new PendingBurst(b.shooter(), b.remaining() - 1, b.interval(), b.interval(), b.options()));
			}
		}
		BURSTS.addAll(next);
	}

	public static void clearServerState() {
		BURSTS.clear();
		BleedManager.clear();
		PelletAccumulator.clear();
	}

	public static InteractionHand handOf(LivingEntity living, ItemStack stack) {
		return stack == living.getOffhandItem() ? InteractionHand.OFF_HAND : InteractionHand.MAIN_HAND;
	}
}
