package net.hautecapitale.fusils.gun.effect.effects;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

import net.hautecapitale.fusils.data.ShotStats;
import net.hautecapitale.fusils.data.Stat;
import net.hautecapitale.fusils.data.StatModifier;
import net.hautecapitale.fusils.gun.effect.ShotEffect;
import net.hautecapitale.fusils.gun.effect.ShotEffectType;
import net.hautecapitale.fusils.gun.effect.ShotEffectTypes;

/** Retouche générique d'une statistique : {@code {"type":"stat","stat":"damage","op":"mul","value":0.3}}. */
public record StatEffect(Stat stat, StatModifier modifier) implements ShotEffect {
	public static final MapCodec<StatEffect> CODEC = RecordCodecBuilder.mapCodec(b -> b.group(
			Stat.CODEC.fieldOf("stat").forGetter(StatEffect::stat),
			StatModifier.Op.CODEC.optionalFieldOf("op", StatModifier.Op.ADD).forGetter(e -> e.modifier.op()),
			com.mojang.serialization.Codec.DOUBLE.fieldOf("value").forGetter(e -> e.modifier.value())
	).apply(b, (stat, op, value) -> new StatEffect(stat, new StatModifier(op, value))));

	@Override
	public ShotEffectType<?> type() {
		return ShotEffectTypes.STAT;
	}

	@Override
	public void applyStats(ShotStats stats) {
		stats.value(this.stat).add(this.modifier);
	}
}
