package net.hautecapitale.fusils.gun.effect.effects;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

import net.hautecapitale.fusils.data.ShotStats;
import net.hautecapitale.fusils.gun.StunManager;
import net.hautecapitale.fusils.gun.effect.HitContext;
import net.hautecapitale.fusils.gun.effect.ShotEffect;
import net.hautecapitale.fusils.gun.effect.ShotEffectType;
import net.hautecapitale.fusils.gun.effect.ShotEffectTypes;
import net.minecraft.world.entity.LivingEntity;

/** Étourdissement bref : interrompt l'action en cours et immobilise pendant {@code ticks}. */
public record StunEffect(int ticks) implements ShotEffect {
	public static final MapCodec<StunEffect> CODEC = RecordCodecBuilder.mapCodec(b -> b.group(
			Codec.INT.optionalFieldOf("ticks", 15).forGetter(StunEffect::ticks)
	).apply(b, StunEffect::new));

	@Override
	public ShotEffectType<?> type() {
		return ShotEffectTypes.STUN;
	}

	@Override
	public void applyStats(ShotStats stats) {
		stats.addTrailColor(0xE8E8FF);
		stats.setMuzzleTint(0xD0D8FF);
	}

	@Override
	public void onHitEntity(HitContext ctx) {
		LivingEntity living = ctx.livingTarget();
		if (living != null) {
			StunManager.stun(living, this.ticks);
		}
	}
}
