package net.hautecapitale.fusils.gun.effect.effects;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

import net.hautecapitale.fusils.data.ShotStats;
import net.hautecapitale.fusils.gun.effect.HitContext;
import net.hautecapitale.fusils.gun.effect.ImpactContext;
import net.hautecapitale.fusils.gun.effect.ShotEffect;
import net.hautecapitale.fusils.gun.effect.ShotEffectType;
import net.hautecapitale.fusils.gun.effect.ShotEffectTypes;
import net.minecraft.core.particles.ParticleTypes;

/** Brûlure : la cible s'enflamme pour {@code ticks}. */
public record IgniteEffect(int ticks) implements ShotEffect {
	public static final MapCodec<IgniteEffect> CODEC = RecordCodecBuilder.mapCodec(b -> b.group(
			Codec.INT.optionalFieldOf("ticks", 80).forGetter(IgniteEffect::ticks)
	).apply(b, IgniteEffect::new));

	@Override
	public ShotEffectType<?> type() {
		return ShotEffectTypes.IGNITE;
	}

	@Override
	public void applyStats(ShotStats stats) {
		stats.addTrailColor(0xFFC86A);
		stats.setMuzzleTint(0xFF8C2A);
	}

	@Override
	public void onHitEntity(HitContext ctx) {
		ctx.target().igniteForTicks(this.ticks);
	}

	@Override
	public void onImpact(ImpactContext ctx) {
		ctx.level().sendParticles(ParticleTypes.LAVA, ctx.position().x, ctx.position().y, ctx.position().z, 3, 0.05, 0.05, 0.05, 0.1);
	}
}
