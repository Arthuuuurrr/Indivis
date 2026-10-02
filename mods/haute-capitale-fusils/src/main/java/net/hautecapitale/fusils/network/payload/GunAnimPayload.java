package net.hautecapitale.fusils.network.payload;

import net.hautecapitale.fusils.FusilsIds;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;

/** Serveur → clients : jouer une animation d'arme (fire / reload / equip) sur l'objet d'une entité. */
public record GunAnimPayload(int entityId, long instanceId, boolean mainHand, String anim, double speed, double offset, double skipAt, double skipTo)
		implements CustomPacketPayload {
	public static final Type<GunAnimPayload> TYPE = new Type<>(FusilsIds.id("animation"));
	public static final StreamCodec<RegistryFriendlyByteBuf, GunAnimPayload> CODEC = StreamCodec.of(GunAnimPayload::write, GunAnimPayload::read);

	private static void write(RegistryFriendlyByteBuf buf, GunAnimPayload p) {
		buf.writeVarInt(p.entityId);
		buf.writeLong(p.instanceId);
		buf.writeBoolean(p.mainHand);
		buf.writeUtf(p.anim);
		buf.writeDouble(p.speed);
		buf.writeDouble(p.offset);
		buf.writeDouble(p.skipAt);
		buf.writeDouble(p.skipTo);
	}

	private static GunAnimPayload read(RegistryFriendlyByteBuf buf) {
		return new GunAnimPayload(buf.readVarInt(), buf.readLong(), buf.readBoolean(), buf.readUtf(), buf.readDouble(), buf.readDouble(), buf.readDouble(), buf.readDouble());
	}

	@Override
	public Type<? extends CustomPacketPayload> type() {
		return TYPE;
	}

	@SuppressWarnings("unused")
	private static final StreamCodec<RegistryFriendlyByteBuf, Integer> UNUSED = ByteBufCodecs.VAR_INT.cast();
}
