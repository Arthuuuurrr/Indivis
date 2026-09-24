package net.hautecapitale.fusils.gun.effect.effects;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

import net.hautecapitale.fusils.data.ShotStats;
import net.hautecapitale.fusils.gun.effect.HitContext;
import net.hautecapitale.fusils.gun.effect.ShotEffect;
import net.hautecapitale.fusils.gun.effect.ShotEffectType;
import net.hautecapitale.fusils.gun.effect.ShotEffectTypes;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.phys.Vec3;

/** Givre : gel visuel + Lenteur. */
public record FreezeEffect(int ticks, int slownessAmplifier) implements ShotEffect {
	public static final MapCodec<FreezeEffect> CODEC = RecordCodecBuilder.mapCodec(b -> b.group(
			Codec.INT.optionalFieldOf("ticks", 80).forGetter(FreezeEffect::ticks),
			Codec.INT.optionalFieldOf("slowness_amplifier", 1).forGetter(FreezeEffect::slownessAmplifier)
	).apply(b, FreezeEffect::new));

	@Override
	public ShotEffectType<?> type() {
		return ShotEffectTypes.FREEZE;
	}

	@Override
	public void applyStats(ShotStats stats) {
		stats.addTrailColor(0x9FDFFF);
		stats.setMuzzleTint(0xA8E4FF);
	}

	@Override
	public void onHitEntity(HitContext ctx) {
		LivingEntity living = ctx.livingTarget();
		if (living == null) return;
		living.setTicksFrozen(Math.min(living.getTicksRequiredToFreeze() * 5, living.getTicksFrozen() + this.ticks));
		living.addEffect(new MobEffectInstance(MobEffects.SLOWNESS, this.ticks, this.slownessAmplifier), ctx.shooter());
		Vec3 c = living.getBoundingBox().getCenter();
		ctx.level().sendParticles(ParticleTypes.SNOWFLAKE, c.x, c.y, c.z, 8, 0.25, 0.35, 0.25, 0.02);
	}
}
