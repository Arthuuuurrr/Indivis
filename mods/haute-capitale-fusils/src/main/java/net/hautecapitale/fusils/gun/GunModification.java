package net.hautecapitale.fusils.gun;

import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Optional;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

import net.hautecapitale.fusils.data.Stat;
import net.hautecapitale.fusils.data.StatModifier;
import net.hautecapitale.fusils.gun.effect.ShotEffect;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.ComponentSerialization;
import net.minecraft.resources.Identifier;
import net.minecraft.util.StringRepresentable;

/**
 * Une modification installable dans l'atelier : catégorie Canon ou Mécanisme, retouches de
 * statistiques, effets, et éventuellement un accessoire rendu sur un os du modèle
 * ({@code attachment_optic}, {@code attachment_bayonet}).
 * Lue depuis {@code data/<ns>/fusil_modification/<id>.json}.
 */
public record GunModification(Category category, Component name, List<Component> description,
		Map<Stat, StatModifier> stats, List<ShotEffect> effects, boolean forceAuto, boolean scope,
		Optional<Attachment> attachment, Optional<Identifier> item) {

	public enum Category implements StringRepresentable {
		CANON, MECANISME;
		public static final Codec<Category> CODEC = StringRepresentable.fromEnum(Category::values);
		@Override public String getSerializedName() { return name().toLowerCase(Locale.ROOT); }
		public String translationKey() { return "categorie.haute_capitale_fusils." + getSerializedName(); }
	}

	/** Accessoire visuel : os du modèle d'arme + modèle/texture GeckoLib de l'accessoire. */
	public record Attachment(String bone, Identifier model, Identifier texture) {
		public static final Codec<Attachment> CODEC = RecordCodecBuilder.create(b -> b.group(
				Codec.STRING.fieldOf("bone").forGetter(Attachment::bone),
				Identifier.CODEC.fieldOf("model").forGetter(Attachment::model),
				Identifier.CODEC.fieldOf("texture").forGetter(Attachment::texture)
		).apply(b, Attachment::new));
	}

	public static final Codec<GunModification> CODEC = RecordCodecBuilder.create(b -> b.group(
			Category.CODEC.fieldOf("category").forGetter(GunModification::category),
			ComponentSerialization.CODEC.fieldOf("name").forGetter(GunModification::name),
			ComponentSerialization.CODEC.listOf().optionalFieldOf("description", List.of()).forGetter(GunModification::description),
			Codec.unboundedMap(Stat.CODEC, StatModifier.CODEC).optionalFieldOf("stats", Map.of()).forGetter(GunModification::stats),
			ShotEffect.CODEC.listOf().optionalFieldOf("effects", List.of()).forGetter(GunModification::effects),
			Codec.BOOL.optionalFieldOf("force_auto", false).forGetter(GunModification::forceAuto),
			Codec.BOOL.optionalFieldOf("scope", false).forGetter(GunModification::scope),
			Attachment.CODEC.optionalFieldOf("attachment").forGetter(GunModification::attachment),
			Identifier.CODEC.optionalFieldOf("item").forGetter(GunModification::item)
	).apply(b, GunModification::new));
}
