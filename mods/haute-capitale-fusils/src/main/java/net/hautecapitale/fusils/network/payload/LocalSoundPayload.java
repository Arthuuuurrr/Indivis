package net.hautecapitale.fusils.network.payload;

import net.hautecapitale.fusils.FusilsIds;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.Identifier;

/** Serveur → un client : son joué « dans la tête » du joueur (équipement de l'arme). */
public record LocalSoundPayload(Identifier sound, float volume, float pitch) implements CustomPacketPayload {
	public static final Type<LocalSoundPayload> TYPE = new Type<>(FusilsIds.id("son_local"));
	public static final StreamCodec<RegistryFriendlyByteBuf, LocalSoundPayload> CODEC = StreamCodec.of(
			(buf, p) -> { Identifier.STREAM_CODEC.encode(buf, p.sound); buf.writeFloat(p.volume); buf.writeFloat(p.pitch); },
			buf -> new LocalSoundPayload(Identifier.STREAM_CODEC.decode(buf), buf.readFloat(), buf.readFloat()));

	@Override
	public Type<? extends CustomPacketPayload> type() {
		return TYPE;
	}
}
