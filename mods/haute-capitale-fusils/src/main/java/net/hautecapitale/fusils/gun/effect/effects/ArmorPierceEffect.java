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

/** Ignore une fraction de l'armure et traverse {@code piercing} créatures supplémentaires. */
public record ArmorPierceEffect(double fraction, int piercing) implements ShotEffect {
	public static final MapCodec<ArmorPierceEffect> CODEC = RecordCodecBuilder.mapCodec(b -> b.group(
			Codec.DOUBLE.optionalFieldOf("fraction", 0.4).forGetter(ArmorPierceEffect::fraction),
			Codec.INT.optionalFieldOf("piercing", 1).forGetter(ArmorPierceEffect::piercing)
	).apply(b, ArmorPierceEffect::new));

	@Override
	public ShotEffectType<?> type() {
		return ShotEffectTypes.ARMOR_PIERCE;
	}

	@Override
	public void applyStats(ShotStats stats) {
		stats.value(Stat.ARMOR_PIERCE).add(StatModifier.add(this.fraction));
		stats.value(Stat.PIERCING).add(StatModifier.add(this.piercing));
	}
}
