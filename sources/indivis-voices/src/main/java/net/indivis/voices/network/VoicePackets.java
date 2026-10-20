package net.indivis.voices.network;

import java.util.*;
import net.indivis.voices.session.PlaybackLease;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.packet.CustomPayload;
import net.minecraft.util.Identifier;

/** Bounded version 1 protocol. The client never supplies a quest action or sequence. */
public final class VoicePackets {
    public static final int PROTOCOL = 1;
    private static <T extends CustomPayload> CustomPayload.Id<T> id(String name) {
        return new CustomPayload.Id<>(Identifier.of("indivis_voices", name));
    }
    public record Hello(int protocol, String revision, boolean enabled, List<String> available) implements CustomPayload {
        public static final Id<Hello> ID = id("hello_v1");
        public static final PacketCodec<RegistryByteBuf, Hello> CODEC = new PacketCodec<>() {
            public Hello decode(RegistryByteBuf b) {
                int version = b.readVarInt(); String revision = b.readString(96); boolean enabled = b.readBoolean();
                int size = b.readVarInt();
                if (size < 0 || size > 32) throw new IllegalArgumentException("Voice capability limit");
                List<String> lines = new ArrayList<>(size);
                for (int i = 0; i < size; i++) lines.add(b.readString(96));
                return new Hello(version, revision, enabled, List.copyOf(lines));
            }
            public void encode(RegistryByteBuf b, Hello p) {
                if (p.available.size() > 32) throw new IllegalArgumentException("Voice capability limit");
                b.writeVarInt(p.protocol); b.writeString(p.revision, 96); b.writeBoolean(p.enabled);
                b.writeVarInt(p.available.size()); p.available.forEach(s -> b.writeString(s, 96));
            }
        };
        public Id<Hello> getId() { return ID; }
    }
    public record Token(UUID epoch, int session, long generation, String line) {
        static Token read(RegistryByteBuf b) { return new Token(b.readUuid(), b.readVarInt(), b.readVarLong(), b.readString(96)); }
        void write(RegistryByteBuf b) { b.writeUuid(epoch); b.writeVarInt(session); b.writeVarLong(generation); b.writeString(line, 96); }
    }
    public record Start(Token token, UUID npc, int entityId) implements CustomPayload {
        public static final Id<Start> ID = id("start_v1");
        public static final PacketCodec<RegistryByteBuf, Start> CODEC = new PacketCodec<>() {
            public Start decode(RegistryByteBuf b) { return new Start(Token.read(b), b.readUuid(), b.readVarInt()); }
            public void encode(RegistryByteBuf b, Start p) { p.token.write(b); b.writeUuid(p.npc); b.writeVarInt(p.entityId); }
        };
        public Id<Start> getId() { return ID; }
    }
    public record Stop(Token token) implements CustomPayload {
        public static final Id<Stop> ID = id("stop_v1");
        public static final PacketCodec<RegistryByteBuf, Stop> CODEC = new PacketCodec<>() {
            public Stop decode(RegistryByteBuf b) { return new Stop(Token.read(b)); }
            public void encode(RegistryByteBuf b, Stop p) { p.token.write(b); }
        };
        public Id<Stop> getId() { return ID; }
    }
    public record End(Token token, PlaybackLease.End reason) implements CustomPayload {
        public static final Id<End> ID = id("end_v1");
        public static final PacketCodec<RegistryByteBuf, End> CODEC = new PacketCodec<>() {
            public End decode(RegistryByteBuf b) {
                var token = Token.read(b); int reason = b.readVarInt();
                if (reason < 0 || reason >= PlaybackLease.End.values().length) throw new IllegalArgumentException("Invalid voice end");
                return new End(token, PlaybackLease.End.values()[reason]);
            }
            public void encode(RegistryByteBuf b, End p) { p.token.write(b); b.writeVarInt(p.reason.ordinal()); }
        };
        public Id<End> getId() { return ID; }
    }
}
