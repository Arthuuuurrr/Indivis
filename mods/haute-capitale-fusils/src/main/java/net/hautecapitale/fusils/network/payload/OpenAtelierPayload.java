package net.hautecapitale.fusils.network.payload;

import net.hautecapitale.fusils.FusilsIds;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;

/** Client → serveur : ouvrir l'atelier de l'arme en main. */
public record OpenAtelierPayload() implements CustomPacketPayload {
	public static final Type<OpenAtelierPayload> TYPE = new Type<>(FusilsIds.id("atelier"));
	public static final OpenAtelierPayload INSTANCE = new OpenAtelierPayload();
	public static final StreamCodec<RegistryFriendlyByteBuf, OpenAtelierPayload> CODEC = StreamCodec.unit(INSTANCE);

	@Override
	public Type<? extends CustomPacketPayload> type() {
		return TYPE;
	}
}
