package net.hautecapitale.fusils.registry;

import java.util.function.UnaryOperator;

import net.hautecapitale.fusils.FusilsIds;
import net.hautecapitale.fusils.data.component.ChargedAmmo;
import net.hautecapitale.fusils.data.component.FireDelayState;
import net.hautecapitale.fusils.data.component.InstalledMods;
import net.hautecapitale.fusils.data.component.Magazine;
import net.hautecapitale.fusils.data.component.ReloadState;
import net.hautecapitale.fusils.data.component.StatOverrides;
import net.minecraft.core.Registry;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.Identifier;

/** Composants d'objet du mod. Les états qui changent chaque tick ignorent l'animation de changement d'objet. */
public final class FusilsComponents {
	public static final DataComponentType<Magazine> MAGAZINE = register("chargeur",
			b -> b.persistent(Magazine.CODEC).networkSynchronized(Magazine.STREAM_CODEC).ignoreSwapAnimation());
	public static final DataComponentType<ReloadState> RELOAD = register("recharge",
			b -> b.persistent(ReloadState.CODEC).networkSynchronized(ReloadState.STREAM_CODEC).ignoreSwapAnimation());
	public static final DataComponentType<FireDelayState> FIRE_DELAY = register("delai_tir",
			b -> b.persistent(FireDelayState.CODEC).networkSynchronized(FireDelayState.STREAM_CODEC).ignoreSwapAnimation());
	public static final DataComponentType<ChargedAmmo> CHARGED_AMMO = register("munition_chargee",
			b -> b.persistent(ChargedAmmo.CODEC).networkSynchronized(ChargedAmmo.STREAM_CODEC).ignoreSwapAnimation());
	public static final DataComponentType<InstalledMods> MODS = register("modifications",
			b -> b.persistent(InstalledMods.CODEC).networkSynchronized(InstalledMods.STREAM_CODEC));
	public static final DataComponentType<StatOverrides> STATS = register("stats",
			b -> b.persistent(StatOverrides.CODEC).networkSynchronized(StatOverrides.STREAM_CODEC));
	/** Identifiant de variante ({@code fusil_variante/<id>.json}) porté par l'objet. */
	public static final DataComponentType<Identifier> VARIANT = register("variante",
			b -> b.persistent(Identifier.CODEC).networkSynchronized(Identifier.STREAM_CODEC));
	/** Bonus de compétence temporaire appliqué au prochain tir (nom de compétence). */
	public static final DataComponentType<String> SKILL_SHOT = register("tir_competence",
			b -> b.persistent(com.mojang.serialization.Codec.STRING).networkSynchronized(net.minecraft.network.codec.ByteBufCodecs.STRING_UTF8).ignoreSwapAnimation());

	private FusilsComponents() {}

	private static <T> DataComponentType<T> register(String name, UnaryOperator<DataComponentType.Builder<T>> op) {
		return Registry.register(BuiltInRegistries.DATA_COMPONENT_TYPE, FusilsIds.id(name), op.apply(DataComponentType.builder()).build());
	}

	public static void init() {}
}
