package net.hautecapitale.fusils.gun.effect.effects;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

import net.hautecapitale.fusils.data.ShotStats;
import net.hautecapitale.fusils.data.Stat;
import net.hautecapitale.fusils.data.StatModifier;
import net.hautecapitale.fusils.gun.effect.ShotEffect;
import net.hautecapitale.fusils.gun.effect.ShotEffectType;
import net.hautecapitale.fusils.gun.effect.ShotEffectTypes;

/** Multiplie le recul infligé à la cible. */
public record KnockbackEffect(double multiplier) implements ShotEffect {
	public static final MapCodec<KnockbackEffect> CODEC = RecordCodecBuilder.mapCodec(b -> b.group(
			Codec.DOUBLE.optionalFieldOf("multiplier", 3.0).forGetter(KnockbackEffect::multiplier)
	).apply(b, KnockbackEffect::new));

	@Override
	public ShotEffectType<?> type() {
		return ShotEffectTypes.KNOCKBACK;
	}

	@Override
	public void applyStats(ShotStats stats) {
		stats.value(Stat.KNOCKBACK).add(StatModifier.mul(this.multiplier - 1.0));
	}
}
