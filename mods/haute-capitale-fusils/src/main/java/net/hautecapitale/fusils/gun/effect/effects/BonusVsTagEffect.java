package net.hautecapitale.fusils.gun.effect.effects;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

import net.hautecapitale.fusils.data.ShotStats;
import net.hautecapitale.fusils.gun.effect.HitContext;
import net.hautecapitale.fusils.gun.effect.ShotEffect;
import net.hautecapitale.fusils.gun.effect.ShotEffectType;
import net.hautecapitale.fusils.gun.effect.ShotEffectTypes;
import net.minecraft.core.registries.Registries;
import net.minecraft.tags.TagKey;
import net.minecraft.world.entity.EntityType;

/** Bonus de dégâts contre les créatures d'un tag : la balle d'argent contre {@code #haute_capitale_fusils:vulnerable_argent}. */
public record BonusVsTagEffect(TagKey<EntityType<?>> tag, double multiplier, int trailColor) implements ShotEffect {
	public static final MapCodec<BonusVsTagEffect> CODEC = RecordCodecBuilder.mapCodec(b -> b.group(
			TagKey.codec(Registries.ENTITY_TYPE).fieldOf("tag").forGetter(BonusVsTagEffect::tag),
			Codec.DOUBLE.optionalFieldOf("multiplier", 1.5).forGetter(BonusVsTagEffect::multiplier),
			Codec.INT.optionalFieldOf("trail_color", 0xE9EEF5).forGetter(BonusVsTagEffect::trailColor)
	).apply(b, BonusVsTagEffect::new));

	@Override
	public ShotEffectType<?> type() {
		return ShotEffectTypes.BONUS_VS_TAG;
	}

	@Override
	public void applyStats(ShotStats stats) {
		stats.addTrailColor(this.trailColor);
	}

	@Override
	public float modifyDamage(HitContext ctx, float damage) {
		return ctx.target().getType().is(this.tag) ? (float) (damage * this.multiplier) : damage;
	}
}
