package net.hautecapitale.fusils.data.component;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.Identifier;

/** Munition spéciale chargée : ses {@code shots} prochains tirs portent ses effets. */
public record ChargedAmmo(Identifier ammo, int shots) {
	public static final Codec<ChargedAmmo> CODEC = RecordCodecBuilder.create(b -> b.group(
			Identifier.CODEC.fieldOf("ammo").forGetter(ChargedAmmo::ammo),
			Codec.INT.fieldOf("shots").forGetter(ChargedAmmo::shots)
	).apply(b, ChargedAmmo::new));
	public static final StreamCodec<RegistryFriendlyByteBuf, ChargedAmmo> STREAM_CODEC = StreamCodec.composite(
			Identifier.STREAM_CODEC, ChargedAmmo::ammo,
			ByteBufCodecs.VAR_INT, ChargedAmmo::shots,
			ChargedAmmo::new);

	public ChargedAmmo consume() {
		return new ChargedAmmo(this.ammo, this.shots - 1);
	}
}
