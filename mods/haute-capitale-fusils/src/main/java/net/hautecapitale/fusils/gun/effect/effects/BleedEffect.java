package net.hautecapitale.fusils.gun.effect.effects;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

import net.hautecapitale.fusils.data.ShotStats;
import net.hautecapitale.fusils.gun.BleedManager;
import net.hautecapitale.fusils.gun.effect.HitContext;
import net.hautecapitale.fusils.gun.effect.ShotEffect;
import net.hautecapitale.fusils.gun.effect.ShotEffectType;
import net.hautecapitale.fusils.gun.effect.ShotEffectTypes;
import net.minecraft.world.entity.LivingEntity;

/** Saignement : {@code damage_per_tick} toutes les {@code period} ticks pendant {@code ticks}. */
public record BleedEffect(int ticks, int period, double damagePerTick) implements ShotEffect {
	public static final MapCodec<BleedEffect> CODEC = RecordCodecBuilder.mapCodec(b -> b.group(
			Codec.INT.optionalFieldOf("ticks", 100).forGetter(BleedEffect::ticks),
			Codec.INT.optionalFieldOf("period", 20).forGetter(BleedEffect::period),
			Codec.DOUBLE.optionalFieldOf("damage_per_tick", 1.0).forGetter(BleedEffect::damagePerTick)
	).apply(b, BleedEffect::new));

	@Override
	public ShotEffectType<?> type() {
		return ShotEffectTypes.BLEED;
	}

	@Override
	public void applyStats(ShotStats stats) {
		stats.addTrailColor(0xB3242A);
	}

	@Override
	public void onHitEntity(HitContext ctx) {
		LivingEntity living = ctx.livingTarget();
		if (living != null) {
			BleedManager.apply(living, ctx.shooter(), this.ticks, this.period, (float) this.damagePerTick);
		}
	}
}
