package net.hautecapitale.fusils.entity;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.function.Predicate;

import net.fabricmc.fabric.api.networking.v1.PlayerLookup;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.hautecapitale.fusils.config.FusilsConfig;
import net.hautecapitale.fusils.data.ShotStats;
import net.hautecapitale.fusils.data.Stat;
import net.hautecapitale.fusils.gun.HunterMark;
import net.hautecapitale.fusils.gun.PelletAccumulator;
import net.hautecapitale.fusils.gun.effect.HitContext;
import net.hautecapitale.fusils.gun.effect.ImpactContext;
import net.hautecapitale.fusils.gun.effect.ShotEffect;
import net.hautecapitale.fusils.network.payload.HitmarkerPayload;
import net.hautecapitale.fusils.network.payload.ImpactPayload;
import net.hautecapitale.fusils.network.payload.TrailPayload;
import net.hautecapitale.fusils.registry.FusilsDamage;
import net.hautecapitale.fusils.registry.FusilsSounds;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.Mth;
import net.minecraft.world.damagesource.CombatRules;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.OwnableEntity;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.CollisionContext;

/**
 * La balle. Rapide (plusieurs blocs par tick), donc son déplacement est un lancer de rayon par
 * tick : blocs, puis créatures sur le segment, perforation et ricochet dans la même boucle.
 * Jamais de dégâts aux blocs, jamais sauvegardée. Les dégâts sont crédités au tireur.
 */
public class BulletEntity extends Projectile {
	private ShotStats stats = new ShotStats();
	private final Set<Integer> pierced = new HashSet<>();
	private int piercingRemaining;
	private int ricochetRemaining;
	private double travelled;
	private double maxRange = 96.0;
	private float gravity = 0.05F;
	private float drag = 0.98F;
	private float underwaterDrag = 0.95F;
	private boolean waterSlowApplied;
	private boolean firstTick = true;
	private java.util.UUID shotId;
	private int pelletIndex;
	private HitState hitState = HitState.CONTINUE;

	private enum HitState { CONTINUE, STOP, DISCARD }

	public BulletEntity(EntityType<? extends BulletEntity> type, Level level) {
		super(type, level);
	}

	public void applyStats(ShotStats stats) {
		this.stats = stats;
		this.piercingRemaining = (int) stats.get(Stat.PIERCING);
		this.ricochetRemaining = (int) stats.get(Stat.RICOCHET);
		this.maxRange = stats.get(Stat.RANGE);
		this.gravity = (float) stats.get(Stat.GRAVITY);
		this.drag = (float) stats.get(Stat.DRAG);
		this.underwaterDrag = (float) stats.get(Stat.UNDERWATER_DRAG);
	}

	public ShotStats stats() {
		return this.stats;
	}

	public void setShot(java.util.UUID shotId, int pelletIndex) {
		this.shotId = shotId;
		this.pelletIndex = pelletIndex;
	}

	@Override
	protected void defineSynchedData(SynchedEntityData.Builder builder) {}

	@Override
	protected double getDefaultGravity() {
		return this.gravity;
	}

	@Override
	public boolean shouldBeSaved() {
		return false;
	}

	private LivingEntity shooter() {
		return getOwner() instanceof LivingEntity l ? l : null;
	}

	/** Une balle ne blesse ni son tireur, ni ses alliés d'équipe, ni les animaux apprivoisés d'un allié. */
	public static boolean canHarm(Entity attacker, Entity target) {
		if (attacker == null || target == null) return true;
		if (attacker == target || !target.isAlive()) return false;
		if (target.isAlliedTo(attacker) || attacker.isAlliedTo(target)) return false;
		if (target instanceof OwnableEntity ownable && ownable.getOwner() != null && !canHarm(attacker, ownable.getOwner())) return false;
		if (target instanceof Player p && attacker instanceof Player a) {
			return FusilsConfig.INSTANCE.player_vs_player && a.canHarmPlayer(p);
		}
		return true;
	}

	@Override
	protected boolean canHitEntity(Entity entity) {
		return super.canHitEntity(entity) && !this.pierced.contains(entity.getId()) && canHarm(getOwner(), entity);
	}

	/** Dégâts de cette balle : la part d'un plomb, réduite si elle a beaucoup ralenti. */
	public float resolveDamage() {
		double velocityFactor = Mth.clamp(getDeltaMovement().length(), 0.0, 2.0) / 2.0;
		return (float) (velocityFactor * this.stats.get(Stat.DAMAGE) / Math.max(1.0, this.stats.get(Stat.PROJECTILE_COUNT)));
	}

	@Override
	public Vec3 getMovementToShoot(double x, double y, double z, float speed, float spreadDegrees) {
		return directionWithinCone(new Vec3(x, y, z), spreadDegrees).scale(speed);
	}

	private Vec3 directionWithinCone(Vec3 axis, float degrees) {
		Vec3 n = axis.normalize();
		Vec3 helper = Math.abs(n.y) < 0.99 ? new Vec3(0, 1, 0) : new Vec3(1, 0, 0);
		Vec3 u = n.cross(helper).normalize();
		Vec3 v = n.cross(u).normalize();
		float cosHalf = Mth.cos(degrees * Mth.DEG_TO_RAD);
		float z = Mth.lerp(this.random.nextFloat(), cosHalf, 1.0F);
		float r = Mth.sqrt(1.0F - z * z);
		float phi = this.random.nextFloat() * Mth.TWO_PI;
		return n.scale(z).add(u.scale(r * Mth.cos(phi))).add(v.scale(r * Mth.sin(phi))).normalize();
	}

	@Override
	public void tick() {
		super.tick();
		if (!(level() instanceof ServerLevel level)) {
			discard();
			return;
		}
		if (isUnderWater() && this.underwaterDrag < 1.0F) {
			if (!this.waterSlowApplied) {
				this.waterSlowApplied = true;
				setDeltaMovement(getDeltaMovement().scale(Math.pow(this.underwaterDrag, 40.0)));
			}
			setDeltaMovement(getDeltaMovement().scale(this.underwaterDrag));
		}
		handleSeeking(level);

		Vec3 delta = getDeltaMovement();
		Vec3 start = position();
		Vec3 destination = start.add(delta);
		Vec3 segmentStart = start;
		this.hitState = HitState.CONTINUE;
		int guard = 48;
		while (this.hitState == HitState.CONTINUE && --guard > 0) {
			HitResult blockHit = level.clip(new ClipContext(segmentStart, destination, ClipContext.Block.COLLIDER, ClipContext.Fluid.NONE, CollisionContext.empty()));
			Vec3 blockClip = blockHit.getLocation();
			EntityHitResult entityHit = findEntityHit(level, segmentStart, blockClip, getBoundingBox().expandTowards(delta).inflate(1.0), this::canHitEntity, 0.25F, 3.0F);
			if (entityHit != null) {
				onHit(entityHit);
				destination = entityHit.getLocation();
				segmentStart = destination;
				if (this.hitState == HitState.CONTINUE) destination = segmentStart.add(getDeltaMovement().scale(1.0 - Math.min(1.0, segmentStart.subtract(start).length() / Math.max(1e-6, delta.length()))));
			} else if (blockHit.getType() != HitResult.Type.MISS) {
				onHit(blockHit);
				destination = blockHit.getLocation();
			} else {
				this.hitState = HitState.STOP;
			}
			if (this.hitState == HitState.DISCARD) {
				break;
			}
		}

		Vec3 trailStart = start;
		Vec3 trailEnd = destination;
		if (this.firstTick) {
			// La première portion part légèrement sous et à droite de l'œil : la balle « sort » de l'arme.
			Vec3 right = delta.cross(new Vec3(0, 1, 0)).normalize();
			LivingEntity shooter = shooter();
			double side = shooter != null && shooter.getMainArm() == net.minecraft.world.entity.HumanoidArm.LEFT ? -0.3 : 0.3;
			trailStart = start.add(right.scale(side)).add(0, -0.2, 0).add(delta.normalize().scale(0.6));
		}
		if (this.pelletIndex < 3) emitTrail(level, trailStart, trailEnd);

		this.travelled += start.distanceTo(destination);
		setDeltaMovement(getDeltaMovement().scale(this.drag));
		applyGravity();
		setPos(destination);
		this.firstTick = false;
		if (this.hitState == HitState.DISCARD || getDeltaMovement().lengthSqr() < 0.125 || this.travelled > this.maxRange || this.tickCount > 200) {
			discard();
		}
	}

	private static EntityHitResult findEntityHit(Level level, Entity source, Vec3 from, Vec3 to, AABB area, Predicate<Entity> filter, float margin, float motionMargin) {
		double nearest = Double.MAX_VALUE;
		Entity hitEntity = null;
		Vec3 hitPos = null;
		for (Entity entity : level.getEntities(source, area, filter)) {
			AABB bb = entity.getBoundingBox().inflate(margin).expandTowards(entity.getDeltaMovement().scale(motionMargin));
			Optional<Vec3> clip = bb.clip(from, to);
			if (clip.isPresent()) {
				double d = from.distanceToSqr(clip.get());
				if (d < nearest) {
					nearest = d;
					hitEntity = entity;
					hitPos = clip.get();
				}
			}
		}
		return hitEntity == null ? null : new EntityHitResult(hitEntity, hitPos);
	}

	private EntityHitResult findEntityHit(ServerLevel level, Vec3 from, Vec3 to, AABB area, Predicate<Entity> filter, float margin, float motionMargin) {
		return findEntityHit(level, this, from, to, area, filter, margin, motionMargin);
	}

	private void handleSeeking(ServerLevel level) {
		double seeking = this.stats.get(Stat.SEEKING);
		if (seeking <= 0) return;
		Vec3 motion = getDeltaMovement();
		double speed = motion.length();
		if (speed < 1e-4) return;
		float strength = Mth.clamp((float) seeking, 0.0F, 1.0F);
		double range = 7.0 * (1.0 + strength);
		Vec3 start = position();
		Vec3 dir = motion.scale(1.0 / speed);
		Vec3 end = level.clip(new ClipContext(start, start.add(dir.scale(40.0)), ClipContext.Block.COLLIDER, ClipContext.Fluid.NONE, CollisionContext.empty())).getLocation();
		Entity closest = null;
		double closestD = Double.MAX_VALUE;
		for (Entity e : level.getEntities(this, new AABB(start, end).inflate(range), this::canHitEntity)) {
			if (!(e instanceof LivingEntity)) continue;
			Vec3 to = e.getBoundingBox().getCenter().subtract(start);
			double d2 = to.lengthSqr();
			if (d2 < 1e-6 || to.normalize().dot(dir) < 0.2) continue;
			if (d2 < closestD) {
				closest = e;
				closestD = d2;
			}
		}
		if (closest != null) {
			Vec3 wanted = closest.getBoundingBox().getCenter().subtract(start).normalize();
			Vec3 blended = dir.scale(1.0 - strength).add(wanted.scale(strength)).normalize();
			setDeltaMovement(blended.scale(speed));
		}
	}

	@Override
	protected void onHit(HitResult hit) {
		this.hitState = HitState.DISCARD;
		super.onHit(hit);
		if (!(level() instanceof ServerLevel level)) return;
		Set<Integer> hitSet = new HashSet<>();
		if (hit instanceof EntityHitResult ehr) hitSet.add(ehr.getEntity().getId());
		ImpactContext ctx = new ImpactContext(level, this, this.stats, hit, hit.getLocation(), shooter(), hitSet);
		for (ShotEffect effect : this.stats.effects()) {
			effect.onImpact(ctx);
		}
		if (this.hitState != HitState.CONTINUE && hit instanceof BlockHitResult bhr && this.ricochetRemaining > 0) {
			this.ricochetRemaining--;
			reflect(bhr.getDirection());
			this.hitState = HitState.STOP;
			level.playSound(null, hit.getLocation().x, hit.getLocation().y, hit.getLocation().z, FusilsSounds.RICOCHET, SoundSource.NEUTRAL, 1.2F, 0.8F + this.random.nextFloat() * 0.4F);
		}
	}

	@Override
	protected void onHitEntity(EntityHitResult result) {
		super.onHitEntity(result);
		if (!(level() instanceof ServerLevel level)) return;
		Entity target = result.getEntity();
		if (!this.pierced.add(target.getId())) return;
		LivingEntity shooter = shooter();
		float damage = resolveDamage();
		boolean marked = shooter != null && HunterMark.isMarkedBy(target, shooter);
		if (marked) damage *= (float) (1.0 + this.stats.get(Stat.DAMAGE_VS_MARKED));
		boolean crit = this.random.nextDouble() < this.stats.get(Stat.CRIT_CHANCE);
		if (crit) damage *= (float) this.stats.get(Stat.CRIT_MULTIPLIER);

		if (this.shotId != null && this.stats.get(Stat.PROJECTILE_COUNT) > 1.5) {
			// Tir a plombs : une seule blessure par cible et par coup, appliquee en fin de tick.
			PelletAccumulator.add(this.shotId, this, target, result, damage, crit, marked);
			if (this.piercingRemaining > 0) {
				this.piercingRemaining--;
				this.hitState = HitState.CONTINUE;
			}
			return;
		}
		HitContext ctx = new HitContext(level, this, this.stats, result, target, shooter, marked, crit);
		for (ShotEffect effect : this.stats.effects()) {
			damage = effect.modifyDamage(ctx, damage);
		}
		DamageSource source = FusilsDamage.bullet(level, this, shooter);
		if (target instanceof LivingEntity living) {
			damage += armorPierceBonus(living, damage, source);
		}
		boolean hurt = target.hurtServer(level, source, damage);
		if (hurt) {
			if (target instanceof LivingEntity living) {
				float kb = (float) this.stats.get(Stat.KNOCKBACK);
				if (kb > 0) {
					Vec3 v = getDeltaMovement();
					living.knockback(kb, -v.x, -v.z);
				}
				living.invulnerableTime = 0;
				for (ShotEffect effect : this.stats.effects()) {
					effect.onHitEntity(ctx);
				}
			}
			if (shooter instanceof ServerPlayer player && FusilsConfig.INSTANCE.hitmarker_enabled) {
				boolean kill = target instanceof LivingEntity l && !l.isAlive();
				ServerPlayNetworking.send(player, new HitmarkerPayload(crit, kill));
			}
		}
		level.playSound(null, result.getLocation().x, result.getLocation().y, result.getLocation().z, FusilsSounds.IMPACT, SoundSource.NEUTRAL, 1.0F, 0.9F + this.random.nextFloat() * 0.2F);
		if (this.piercingRemaining > 0) {
			this.piercingRemaining--;
			this.hitState = HitState.CONTINUE;
		}
	}

	/** Part des dégâts que l'armure aurait absorbée et que la perforation restitue. */
	public float armorPierceBonus(LivingEntity target, float damage, DamageSource source) {
		double pierce = Mth.clamp(this.stats.get(Stat.ARMOR_PIERCE), 0.0, 1.0);
		if (pierce <= 0) return 0.0F;
		float armor = target.getArmorValue();
		float toughness = (float) target.getAttributeValue(Attributes.ARMOR_TOUGHNESS);
		float withFull = CombatRules.getDamageAfterAbsorb(target, damage, source, armor, toughness);
		float withReduced = CombatRules.getDamageAfterAbsorb(target, damage, source, armor * (float) (1.0 - pierce), toughness);
		return Math.max(0.0F, withReduced - withFull);
	}

	@Override
	protected void onHitBlock(BlockHitResult result) {
		super.onHitBlock(result);
		if (!(level() instanceof ServerLevel level)) return;
		if (this.pelletIndex >= 3) return;
		BlockPos pos = result.getBlockPos();
		BlockState state = level.getBlockState(pos);
		if (this.pelletIndex == 0) level.playSound(null, pos, state.getSoundType().getBreakSound(), SoundSource.BLOCKS, 0.6F, 1.0F);
		ImpactPayload payload = new ImpactPayload(result.getLocation(), getDeltaMovement(), result.getDirection().getUnitVec3(), resolveDamage(),
				net.minecraft.world.level.block.Block.getId(state));
		for (ServerPlayer p : PlayerLookup.tracking(level, chunkPosition())) {
			ServerPlayNetworking.send(p, payload);
		}
	}

	private void reflect(Direction face) {
		Vec3 n = face.getUnitVec3();
		Vec3 d = getDeltaMovement();
		setDeltaMovement(d.subtract(n.scale(2.0 * n.dot(d))).scale(0.8));
		this.pierced.clear();
	}

	private void emitTrail(ServerLevel level, Vec3 from, Vec3 to) {
		if (from.distanceToSqr(to) < 0.01) return;
		List<Integer> colors = new ArrayList<>(this.stats.trailColors());
		if (colors.isEmpty()) colors.add(0xD9D2C5);
		TrailPayload payload = new TrailPayload(from, to, colors);
		for (ServerPlayer p : isRemoved() ? PlayerLookup.tracking(level, chunkPosition()) : PlayerLookup.tracking(this)) {
			ServerPlayNetworking.send(p, payload);
		}
	}
}
