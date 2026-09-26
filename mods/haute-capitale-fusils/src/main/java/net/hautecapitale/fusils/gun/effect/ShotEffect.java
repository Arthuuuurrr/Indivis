package net.hautecapitale.fusils.gun.effect;

import com.mojang.serialization.Codec;

import net.hautecapitale.fusils.data.ShotStats;

/**
 * Un effet attaché à un tir (munition spéciale, modification, variante d'arme, compétence).
 * <p>
 * Trois moments : {@link #applyStats} quand le tir est composé, {@link #onHitEntity} quand une
 * balle touche une créature, {@link #onImpact} à tout impact (bloc ou créature). Un effet est
 * décrit en JSON par {@code {"type": "<id>", ...}} ; voir {@link ShotEffectTypes} pour la liste.
 */
public interface ShotEffect {
	Codec<ShotEffect> CODEC = ShotEffectTypes.CODEC;

	ShotEffectType<?> type();

	default void applyStats(ShotStats stats) {}

	/** Retourne les dégâts éventuellement modifiés. */
	default float modifyDamage(HitContext ctx, float damage) {
		return damage;
	}

	default void onHitEntity(HitContext ctx) {}

	default void onImpact(ImpactContext ctx) {}
}
