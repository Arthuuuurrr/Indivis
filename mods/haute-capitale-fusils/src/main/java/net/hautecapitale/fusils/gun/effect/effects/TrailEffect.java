package net.hautecapitale.fusils.gun.effect.effects;

import java.util.Optional;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

import net.hautecapitale.fusils.data.ShotStats;
import net.hautecapitale.fusils.gun.effect.ShotEffect;
import net.hautecapitale.fusils.gun.effect.ShotEffectType;
import net.hautecapitale.fusils.gun.effect.ShotEffectTypes;

/** Effet purement visuel : couleur de traînée et teinte du flash de bouche. */
public record TrailEffect(int color, Optional<Integer> muzzleTint) implements ShotEffect {
	public static final MapCodec<TrailEffect> CODEC = RecordCodecBuilder.mapCodec(b -> b.group(
			Codec.INT.fieldOf("color").forGetter(TrailEffect::color),
			Codec.INT.optionalFieldOf("muzzle_tint").forGetter(TrailEffect::muzzleTint)
	).apply(b, TrailEffect::new));

	@Override
	public ShotEffectType<?> type() {
		return ShotEffectTypes.TRAIL;
	}

	@Override
	public void applyStats(ShotStats stats) {
		stats.addTrailColor(this.color);
		this.muzzleTint.ifPresent(stats::setMuzzleTint);
	}
}
