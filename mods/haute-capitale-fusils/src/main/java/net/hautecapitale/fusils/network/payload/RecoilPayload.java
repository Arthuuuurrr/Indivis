package net.hautecapitale.fusils.network.payload;

import net.hautecapitale.fusils.FusilsIds;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;

/** Serveur → un client : recul d'un tir déclenché par le serveur (compétence). */
public record RecoilPayload(float pitch, float yaw) implements CustomPacketPayload {
	public static final Type<RecoilPayload> TYPE = new Type<>(FusilsIds.id("recul"));
	public static final StreamCodec<RegistryFriendlyByteBuf, RecoilPayload> CODEC = StreamCodec.of(
			(buf, p) -> { buf.writeFloat(p.pitch); buf.writeFloat(p.yaw); },
			buf -> new RecoilPayload(buf.readFloat(), buf.readFloat()));

	@Override
	public Type<? extends CustomPacketPayload> type() {
		return TYPE;
	}
}
