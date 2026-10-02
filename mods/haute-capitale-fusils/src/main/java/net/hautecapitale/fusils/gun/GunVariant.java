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
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.ComponentSerialization;
import net.minecraft.resources.Identifier;
import net.minecraft.util.StringRepresentable;

/**
 * Une variante d'arme : même mécanique que son profil de base, mais un nom, une rareté, des
 * statistiques retouchées, des effets permanents et des effets greffés aux compétences.
 * Lue depuis {@code data/<ns>/fusil_variante/<id>.json}. Exemple : la carabine épique d'un boss.
 */
public record GunVariant(
		Identifier profile,
		Rarity rarity,
		Optional<Component> name,
		List<Component> lore,
		Map<Stat, StatModifier> stats,
		List<ShotEffect> effects,
		Map<String, List<ShotEffect>> skillEffects,
		Optional<Identifier> texture,
		Optional<Identifier> model,
		Optional<Integer> magazine,
		Optional<Integer> reloadTicks) {

	public enum Rarity implements StringRepresentable {
		COMMUN(ChatFormatting.WHITE), INHABITUEL(ChatFormatting.GREEN), RARE(ChatFormatting.BLUE),
		EPIQUE(ChatFormatting.LIGHT_PURPLE), LEGENDAIRE(ChatFormatting.GOLD);

		public static final Codec<Rarity> CODEC = StringRepresentable.fromEnum(Rarity::values);
		public final ChatFormatting color;

		Rarity(ChatFormatting color) {
			this.color = color;
		}

		@Override
		public String getSerializedName() {
			return name().toLowerCase(Locale.ROOT);
		}

		public String translationKey() {
			return "rarete.haute_capitale_fusils." + getSerializedName();
		}
	}

	public static final Codec<GunVariant> CODEC = RecordCodecBuilder.create(b -> b.group(
			Identifier.CODEC.fieldOf("profile").forGetter(GunVariant::profile),
			Rarity.CODEC.optionalFieldOf("rarity", Rarity.COMMUN).forGetter(GunVariant::rarity),
			ComponentSerialization.CODEC.optionalFieldOf("name").forGetter(GunVariant::name),
			ComponentSerialization.CODEC.listOf().optionalFieldOf("lore", List.of()).forGetter(GunVariant::lore),
			Codec.unboundedMap(Stat.CODEC, StatModifier.CODEC).optionalFieldOf("stats", Map.of()).forGetter(GunVariant::stats),
			ShotEffect.CODEC.listOf().optionalFieldOf("effects", List.of()).forGetter(GunVariant::effects),
			Codec.unboundedMap(Codec.STRING, ShotEffect.CODEC.listOf()).optionalFieldOf("skill_effects", Map.of()).forGetter(GunVariant::skillEffects),
			Identifier.CODEC.optionalFieldOf("texture").forGetter(GunVariant::texture),
			Identifier.CODEC.optionalFieldOf("model").forGetter(GunVariant::model),
			Codec.INT.optionalFieldOf("magazine").forGetter(GunVariant::magazine),
			Codec.INT.optionalFieldOf("reload_ticks").forGetter(GunVariant::reloadTicks)
	).apply(b, GunVariant::new));
}
