package net.hautecapitale.fusils.network.payload;

import net.hautecapitale.fusils.FusilsIds;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.world.phys.Vec3;

/** Serveur → clients : flash de bouche à l'œil du tireur, décalé vers le canon. */
public record MuzzleFlashPayload(String flashType, int tint, int entityId, Vec3 entityMotion, Vec3 position, Vec3 offset) implements CustomPacketPayload {
	public static final Type<MuzzleFlashPayload> TYPE = new Type<>(FusilsIds.id("flash"));
	public static final StreamCodec<RegistryFriendlyByteBuf, MuzzleFlashPayload> CODEC = StreamCodec.of(
			(buf, p) -> {
				buf.writeUtf(p.flashType);
				buf.writeInt(p.tint);
				buf.writeVarInt(p.entityId);
				Vec3.STREAM_CODEC.encode(buf, p.entityMotion);
				Vec3.STREAM_CODEC.encode(buf, p.position);
				Vec3.STREAM_CODEC.encode(buf, p.offset);
			},
			buf -> new MuzzleFlashPayload(buf.readUtf(), buf.readInt(), buf.readVarInt(), Vec3.STREAM_CODEC.decode(buf), Vec3.STREAM_CODEC.decode(buf), Vec3.STREAM_CODEC.decode(buf)));

	@Override
	public Type<? extends CustomPacketPayload> type() {
		return TYPE;
	}
}
