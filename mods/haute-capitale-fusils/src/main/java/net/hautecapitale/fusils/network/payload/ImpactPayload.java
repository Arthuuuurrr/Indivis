package net.hautecapitale.fusils.network.payload;

import net.hautecapitale.fusils.FusilsIds;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.world.phys.Vec3;

/** Serveur → clients : impact d'une balle sur un bloc (éclats, poussière, étincelle). */
public record ImpactPayload(Vec3 position, Vec3 motion, Vec3 normal, float damage, int blockStateId) implements CustomPacketPayload {
	public static final Type<ImpactPayload> TYPE = new Type<>(FusilsIds.id("impact"));
	public static final StreamCodec<RegistryFriendlyByteBuf, ImpactPayload> CODEC = StreamCodec.of(
			(buf, p) -> {
				Vec3.STREAM_CODEC.encode(buf, p.position);
				Vec3.STREAM_CODEC.encode(buf, p.motion);
				Vec3.STREAM_CODEC.encode(buf, p.normal);
				buf.writeFloat(p.damage);
				buf.writeVarInt(p.blockStateId);
			},
			buf -> new ImpactPayload(Vec3.STREAM_CODEC.decode(buf), Vec3.STREAM_CODEC.decode(buf), Vec3.STREAM_CODEC.decode(buf), buf.readFloat(), buf.readVarInt()));

	@Override
	public Type<? extends CustomPacketPayload> type() {
		return TYPE;
	}
}
