package net.hautecapitale.fusils.network.payload;

import net.hautecapitale.fusils.FusilsIds;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;

/** Serveur → clients : arrêter l'animation d'action en cours. */
public record CancelAnimPayload(int entityId, long instanceId, boolean mainHand) implements CustomPacketPayload {
	public static final Type<CancelAnimPayload> TYPE = new Type<>(FusilsIds.id("annule_animation"));
	public static final StreamCodec<RegistryFriendlyByteBuf, CancelAnimPayload> CODEC = StreamCodec.of(
			(buf, p) -> { buf.writeVarInt(p.entityId); buf.writeLong(p.instanceId); buf.writeBoolean(p.mainHand); },
			buf -> new CancelAnimPayload(buf.readVarInt(), buf.readLong(), buf.readBoolean()));

	@Override
	public Type<? extends CustomPacketPayload> type() {
		return TYPE;
	}
}
