package net.hautecapitale.fusils.network.payload;

import java.util.ArrayList;
import java.util.List;

import net.hautecapitale.fusils.FusilsIds;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.world.phys.Vec3;

/** Serveur → clients : segment de traînée d'une balle et ses couleurs. */
public record TrailPayload(Vec3 from, Vec3 to, List<Integer> colors) implements CustomPacketPayload {
	public static final Type<TrailPayload> TYPE = new Type<>(FusilsIds.id("trainee"));
	public static final StreamCodec<RegistryFriendlyByteBuf, TrailPayload> CODEC = StreamCodec.of(
			(buf, p) -> {
				Vec3.STREAM_CODEC.encode(buf, p.from);
				Vec3.STREAM_CODEC.encode(buf, p.to);
				buf.writeVarInt(p.colors.size());
				for (int c : p.colors) buf.writeInt(c);
			},
			buf -> {
				Vec3 from = Vec3.STREAM_CODEC.decode(buf);
				Vec3 to = Vec3.STREAM_CODEC.decode(buf);
				int n = buf.readVarInt();
				List<Integer> colors = new ArrayList<>(n);
				for (int i = 0; i < n; i++) colors.add(buf.readInt());
				return new TrailPayload(from, to, colors);
			});

	@Override
	public Type<? extends CustomPacketPayload> type() {
		return TYPE;
	}
}
