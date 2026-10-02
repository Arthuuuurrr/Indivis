package net.hautecapitale.fusils.gun.effect;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.function.Function;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;

import net.hautecapitale.fusils.FusilsIds;
import net.hautecapitale.fusils.gun.effect.effects.ArmorPierceEffect;
import net.hautecapitale.fusils.gun.effect.effects.BleedEffect;
import net.hautecapitale.fusils.gun.effect.effects.BonusVsTagEffect;
import net.hautecapitale.fusils.gun.effect.effects.ExplosionEffect;
import net.hautecapitale.fusils.gun.effect.effects.FreezeEffect;
import net.hautecapitale.fusils.gun.effect.effects.IgniteEffect;
import net.hautecapitale.fusils.gun.effect.effects.KnockbackEffect;
import net.hautecapitale.fusils.gun.effect.effects.PotionEffect;
import net.hautecapitale.fusils.gun.effect.effects.StatEffect;
import net.hautecapitale.fusils.gun.effect.effects.StunEffect;
import net.hautecapitale.fusils.gun.effect.effects.TrailEffect;
import net.minecraft.resources.Identifier;

/**
 * Registre des types d'effets. Pour en ajouter un : écrire une classe qui implémente
 * {@link ShotEffect} et l'enregistrer ici (ou depuis un autre mod via {@link #register}).
 */
public final class ShotEffectTypes {
	private static final Map<Identifier, ShotEffectType<?>> TYPES = new LinkedHashMap<>();

	public static final ShotEffectType<StatEffect> STAT = register("stat", StatEffect.CODEC);
	public static final ShotEffectType<ArmorPierceEffect> ARMOR_PIERCE = register("armor_pierce", ArmorPierceEffect.CODEC);
	public static final ShotEffectType<ExplosionEffect> EXPLOSION = register("explosion", ExplosionEffect.CODEC);
	public static final ShotEffectType<IgniteEffect> IGNITE = register("ignite", IgniteEffect.CODEC);
	public static final ShotEffectType<BleedEffect> BLEED = register("bleed", BleedEffect.CODEC);
	public static final ShotEffectType<KnockbackEffect> KNOCKBACK = register("knockback", KnockbackEffect.CODEC);
	public static final ShotEffectType<StunEffect> STUN = register("stun", StunEffect.CODEC);
	public static final ShotEffectType<PotionEffect> POTION = register("potion", PotionEffect.CODEC);
	public static final ShotEffectType<FreezeEffect> FREEZE = register("freeze", FreezeEffect.CODEC);
	public static final ShotEffectType<BonusVsTagEffect> BONUS_VS_TAG = register("bonus_vs_tag", BonusVsTagEffect.CODEC);
	public static final ShotEffectType<TrailEffect> TRAIL = register("trail", TrailEffect.CODEC);

	public static final Codec<ShotEffect> CODEC = Identifier.CODEC.<ShotEffectType<?>>flatXmap(
			id -> {
				ShotEffectType<?> t = TYPES.get(id);
				return t == null ? com.mojang.serialization.DataResult.error(() -> "Effet de tir inconnu : " + id) : com.mojang.serialization.DataResult.success(t);
			},
			t -> com.mojang.serialization.DataResult.success(t.id()))
			.dispatch("type", ShotEffect::type, t -> (MapCodec<ShotEffect>) (MapCodec) t.codec());

	private ShotEffectTypes() {}

	public static <E extends ShotEffect> ShotEffectType<E> register(String path, MapCodec<E> codec) {
		return register(FusilsIds.id(path), codec);
	}

	public static <E extends ShotEffect> ShotEffectType<E> register(Identifier id, MapCodec<E> codec) {
		ShotEffectType<E> type = new ShotEffectType<>(id, codec);
		TYPES.put(id, type);
		return type;
	}

	public static Map<Identifier, ShotEffectType<?>> all() {
		return TYPES;
	}

	/** Sert aux codecs des effets sans paramètre. */
	public static <E extends ShotEffect> MapCodec<E> unit(E instance) {
		return MapCodec.unit(instance);
	}

	@SuppressWarnings("unused")
	private static <E extends ShotEffect> Function<E, ShotEffectType<?>> typeOf() {
		return ShotEffect::type;
	}
}
