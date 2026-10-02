package net.hautecapitale.fusils.gun.effect;

import com.mojang.serialization.MapCodec;

import net.minecraft.resources.Identifier;

/** Un type d'effet enregistré : identifiant + codec de ses paramètres. */
public record ShotEffectType<E extends ShotEffect>(Identifier id, MapCodec<E> codec) {
}
