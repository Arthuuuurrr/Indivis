package net.hautecapitale.fusils.network.payload;

import net.hautecapitale.fusils.FusilsIds;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;

/** Client → serveur : touche de rechargement. */
public record ReloadPayload() implements CustomPacketPayload {
	public static final Type<ReloadPayload> TYPE = new Type<>(FusilsIds.id("recharger"));
	public static final ReloadPayload INSTANCE = new ReloadPayload();
	public static final StreamCodec<RegistryFriendlyByteBuf, ReloadPayload> CODEC = StreamCodec.unit(INSTANCE);

	@Override
	public Type<? extends CustomPacketPayload> type() {
		return TYPE;
	}
}
