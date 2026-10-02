package net.hautecapitale.fusils.network.payload;

import net.hautecapitale.fusils.FusilsIds;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;

/** Serveur → tireur : une balle a touché (marqueur au réticule, son). */
public record HitmarkerPayload(boolean critical, boolean kill) implements CustomPacketPayload {
	public static final Type<HitmarkerPayload> TYPE = new Type<>(FusilsIds.id("touche"));
	public static final StreamCodec<RegistryFriendlyByteBuf, HitmarkerPayload> CODEC = StreamCodec.of(
			(buf, p) -> { buf.writeBoolean(p.critical); buf.writeBoolean(p.kill); },
			buf -> new HitmarkerPayload(buf.readBoolean(), buf.readBoolean()));

	@Override
	public Type<? extends CustomPacketPayload> type() {
		return TYPE;
	}
}
