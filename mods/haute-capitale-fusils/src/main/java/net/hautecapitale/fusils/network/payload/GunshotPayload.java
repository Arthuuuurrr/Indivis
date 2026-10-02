package net.hautecapitale.fusils.network.payload;

import net.hautecapitale.fusils.FusilsIds;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.Identifier;
import net.minecraft.world.phys.Vec3;

/** Serveur → clients : un coup de feu (détonation immédiate + écho retardé selon la distance). */
public record GunshotPayload(Vec3 position, Identifier shoot, float shootVolume, float shootPitch, Identifier echo, float echoVolume, float echoPitch)
		implements CustomPacketPayload {
	public static final Type<GunshotPayload> TYPE = new Type<>(FusilsIds.id("coup_de_feu"));
	public static final StreamCodec<RegistryFriendlyByteBuf, GunshotPayload> CODEC = StreamCodec.of(
			(buf, p) -> {
				Vec3.STREAM_CODEC.encode(buf, p.position);
				Identifier.STREAM_CODEC.encode(buf, p.shoot);
				buf.writeFloat(p.shootVolume);
				buf.writeFloat(p.shootPitch);
				Identifier.STREAM_CODEC.encode(buf, p.echo);
				buf.writeFloat(p.echoVolume);
				buf.writeFloat(p.echoPitch);
			},
			buf -> new GunshotPayload(Vec3.STREAM_CODEC.decode(buf), Identifier.STREAM_CODEC.decode(buf), buf.readFloat(), buf.readFloat(),
					Identifier.STREAM_CODEC.decode(buf), buf.readFloat(), buf.readFloat()));

	@Override
	public Type<? extends CustomPacketPayload> type() {
		return TYPE;
	}
}
