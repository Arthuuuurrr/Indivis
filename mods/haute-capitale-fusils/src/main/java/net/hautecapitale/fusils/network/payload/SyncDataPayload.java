package net.hautecapitale.fusils.network.payload;

import java.util.LinkedHashMap;
import java.util.Map;

import net.hautecapitale.fusils.FusilsIds;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.Identifier;

/** Serveur → client : toutes les définitions (profils, variantes, munitions, modifications) en JSON. */
public record SyncDataPayload(Map<String, Map<Identifier, String>> raw) implements CustomPacketPayload {
	public static final Type<SyncDataPayload> TYPE = new Type<>(FusilsIds.id("definitions"));
	public static final StreamCodec<RegistryFriendlyByteBuf, SyncDataPayload> CODEC = StreamCodec.of(SyncDataPayload::write, SyncDataPayload::read);

	private static void write(RegistryFriendlyByteBuf buf, SyncDataPayload p) {
		buf.writeVarInt(p.raw.size());
		for (Map.Entry<String, Map<Identifier, String>> dir : p.raw.entrySet()) {
			buf.writeUtf(dir.getKey());
			buf.writeVarInt(dir.getValue().size());
			for (Map.Entry<Identifier, String> e : dir.getValue().entrySet()) {
				Identifier.STREAM_CODEC.encode(buf, e.getKey());
				buf.writeUtf(e.getValue(), 262144);
			}
		}
	}

	private static SyncDataPayload read(RegistryFriendlyByteBuf buf) {
		int dirs = buf.readVarInt();
		Map<String, Map<Identifier, String>> raw = new LinkedHashMap<>();
		for (int i = 0; i < dirs; i++) {
			String dir = buf.readUtf();
			int n = buf.readVarInt();
			Map<Identifier, String> entries = new LinkedHashMap<>();
			for (int j = 0; j < n; j++) {
				entries.put(Identifier.STREAM_CODEC.decode(buf), buf.readUtf(262144));
			}
			raw.put(dir, entries);
		}
		return new SyncDataPayload(raw);
	}

	@Override
	public Type<? extends CustomPacketPayload> type() {
		return TYPE;
	}
}
