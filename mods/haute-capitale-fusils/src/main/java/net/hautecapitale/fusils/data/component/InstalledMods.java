package net.hautecapitale.fusils.data.component;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import com.mojang.serialization.Codec;

import net.hautecapitale.fusils.gun.GunModification;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.Identifier;

/** Modifications installées, une par catégorie : {@code {"canon": "ns:canon_raye", "mecanisme": "..."}}. */
public record InstalledMods(Map<String, Identifier> byCategory) {
	public static final InstalledMods EMPTY = new InstalledMods(Map.of());
	public static final Codec<InstalledMods> CODEC = Codec.unboundedMap(Codec.STRING, Identifier.CODEC).xmap(InstalledMods::new, InstalledMods::byCategory);
	public static final StreamCodec<RegistryFriendlyByteBuf, InstalledMods> STREAM_CODEC =
			ByteBufCodecs.map(HashMap::new, ByteBufCodecs.STRING_UTF8, Identifier.STREAM_CODEC).map(InstalledMods::new, m -> new HashMap<>(m.byCategory())).cast();

	public InstalledMods {
		byCategory = Map.copyOf(byCategory);
	}

	public Optional<Identifier> get(GunModification.Category category) {
		return Optional.ofNullable(this.byCategory.get(category.getSerializedName()));
	}

	public InstalledMods with(GunModification.Category category, Identifier id) {
		Map<String, Identifier> m = new HashMap<>(this.byCategory);
		if (id == null) m.remove(category.getSerializedName());
		else m.put(category.getSerializedName(), id);
		return new InstalledMods(m);
	}

	public boolean isEmpty() {
		return this.byCategory.isEmpty();
	}
}
