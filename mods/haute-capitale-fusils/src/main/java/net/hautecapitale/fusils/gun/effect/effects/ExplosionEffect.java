package net.hautecapitale.fusils.gun.effect.effects;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

import net.hautecapitale.fusils.data.ShotStats;
import net.hautecapitale.fusils.gun.effect.ImpactContext;
import net.hautecapitale.fusils.gun.effect.ShotEffect;
import net.hautecapitale.fusils.gun.effect.ShotEffectType;
import net.hautecapitale.fusils.gun.effect.ShotEffectTypes;
import net.hautecapitale.fusils.registry.FusilsDamage;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;

/**
 * Explosion contrôlée à l'impact : dégâts décroissants dans un rayon, jamais de dégâts aux blocs,
 * jamais sur le tireur ni ses alliés.
 */
public record ExplosionEffect(double radius, double damageFraction, int muzzleTint) implements ShotEffect {
	public static final MapCodec<ExplosionEffect> CODEC = RecordCodecBuilder.mapCodec(b -> b.group(
			Codec.DOUBLE.optionalFieldOf("radius", 2.5).forGetter(ExplosionEffect::radius),
			Codec.DOUBLE.optionalFieldOf("damage_fraction", 1.0).forGetter(ExplosionEffect::damageFraction),
			Codec.INT.optionalFieldOf("muzzle_tint", 0xFF9A3C).forGetter(ExplosionEffect::muzzleTint)
	).apply(b, ExplosionEffect::new));

	@Override
	public ShotEffectType<?> type() {
		return ShotEffectTypes.EXPLOSION;
	}

	@Override
	public void applyStats(ShotStats stats) {
		stats.setMuzzleTint(this.muzzleTint);
		stats.addTrailColor(0xFFB347);
	}

	@Override
	public void onImpact(ImpactContext ctx) {
		Vec3 center = ctx.position();
		double r2 = this.radius * this.radius;
		float base = (float) (ctx.bullet().resolveDamage() * this.damageFraction);
		AABB area = AABB.ofSize(center, this.radius * 2, this.radius * 2, this.radius * 2);
		for (Entity e : ctx.level().getEntities(ctx.bullet(), area, en -> en.isAlive() && ctx.canHarm(en))) {
			if (ctx.alreadyHit().contains(e.getId())) continue;
			double d2 = e.getBoundingBox().getCenter().distanceToSqr(center);
			if (d2 > r2) continue;
			float falloff = 1.0F - (float) (Math.sqrt(d2) / this.radius);
			float dmg = base * Math.max(0.25F, falloff);
			if (e.hurtServer(ctx.level(), FusilsDamage.explosion(ctx.level(), ctx.bullet(), ctx.shooter()), dmg)) {
				ctx.alreadyHit().add(e.getId());
			}
		}
		ctx.level().playSound(null, center.x, center.y, center.z, SoundEvents.GENERIC_EXPLODE.value(), SoundSource.NEUTRAL, 1.6F, 1.35F);
		ctx.level().sendParticles(ParticleTypes.EXPLOSION, center.x, center.y + 0.2, center.z, 1, 0, 0, 0, 0);
		ctx.level().sendParticles(ParticleTypes.SMOKE, center.x, center.y, center.z, 10, 0.4, 0.4, 0.4, 0.02);
		ctx.level().sendParticles(ParticleTypes.LAVA, center.x, center.y, center.z, 5, 0.3, 0.3, 0.3, 0.01);
	}
}
