package net.hautecapitale.fusils.gun.effect.effects;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

import net.hautecapitale.fusils.gun.effect.HitContext;
import net.hautecapitale.fusils.gun.effect.ShotEffect;
import net.hautecapitale.fusils.gun.effect.ShotEffectType;
import net.hautecapitale.fusils.gun.effect.ShotEffectTypes;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.LivingEntity;

/** Applique un effet de potion vanilla ou modé : {@code {"type":"potion","effect":"minecraft:slowness","ticks":80,"amplifier":1}}. */
public record PotionEffect(Holder<MobEffect> effect, int ticks, int amplifier) implements ShotEffect {
	public static final MapCodec<PotionEffect> CODEC = RecordCodecBuilder.mapCodec(b -> b.group(
			BuiltInRegistries.MOB_EFFECT.holderByNameCodec().fieldOf("effect").forGetter(PotionEffect::effect),
			Codec.INT.optionalFieldOf("ticks", 80).forGetter(PotionEffect::ticks),
			Codec.INT.optionalFieldOf("amplifier", 0).forGetter(PotionEffect::amplifier)
	).apply(b, PotionEffect::new));

	@Override
	public ShotEffectType<?> type() {
		return ShotEffectTypes.POTION;
	}

	@Override
	public void onHitEntity(HitContext ctx) {
		LivingEntity living = ctx.livingTarget();
		if (living != null) {
			living.addEffect(new MobEffectInstance(this.effect, this.ticks, this.amplifier), ctx.shooter());
		}
	}
}
