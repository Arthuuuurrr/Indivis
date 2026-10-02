package net.hautecapitale.fusils.network.payload;

import net.hautecapitale.fusils.FusilsIds;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.world.phys.Vec3;

/** Client → serveur : le joueur a pressé la détente dans cette direction. */
public record FirePayload(Vec3 direction) implements CustomPacketPayload {
	public static final Type<FirePayload> TYPE = new Type<>(FusilsIds.id("tir"));
	public static final StreamCodec<RegistryFriendlyByteBuf, FirePayload> CODEC = Vec3.STREAM_CODEC.map(FirePayload::new, FirePayload::direction).cast();

	@Override
	public Type<? extends CustomPacketPayload> type() {
		return TYPE;
	}
}
