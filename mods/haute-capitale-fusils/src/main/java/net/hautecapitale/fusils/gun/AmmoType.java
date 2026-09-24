package net.hautecapitale.fusils.gun;

import java.util.List;
import java.util.Optional;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

import net.hautecapitale.fusils.gun.effect.ShotEffect;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.ComponentSerialization;
import net.minecraft.resources.Identifier;

/**
 * Une munition spéciale : une charge posée sur l'arme pour ses N prochains tirs.
 * Lue depuis {@code data/<ns>/fusil_munition/<id>.json}. Un objet consommable peut la charger
 * ({@code item}), sinon elle vient d'une compétence ou d'une commande.
 */
public record AmmoType(Component name, int color, Optional<Integer> muzzleTint, List<ShotEffect> effects,
		int defaultShots, Optional<Identifier> item) {

	public static final Codec<AmmoType> CODEC = RecordCodecBuilder.create(b -> b.group(
			ComponentSerialization.CODEC.fieldOf("name").forGetter(AmmoType::name),
			Codec.INT.optionalFieldOf("color", 0xFFFFFF).forGetter(AmmoType::color),
			Codec.INT.optionalFieldOf("muzzle_tint").forGetter(AmmoType::muzzleTint),
			ShotEffect.CODEC.listOf().optionalFieldOf("effects", List.of()).forGetter(AmmoType::effects),
			Codec.INT.optionalFieldOf("default_shots", 1).forGetter(AmmoType::defaultShots),
			Identifier.CODEC.optionalFieldOf("item").forGetter(AmmoType::item)
	).apply(b, AmmoType::new));
}
