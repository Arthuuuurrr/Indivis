package net.indivis.voices;

import io.netty.buffer.Unpooled;
import java.util.*;
import net.indivis.voices.network.VoicePackets;
import net.indivis.voices.session.PlaybackLease;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.registry.DynamicRegistryManager;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class VoicePacketsTest {
    RegistryByteBuf buffer() { return new RegistryByteBuf(Unpooled.buffer(), DynamicRegistryManager.EMPTY); }
    @Test void capabilityRoundTrip() {
        var b = buffer();
        try {
            var packet = new VoicePackets.Hello(1, "demo-1", true, List.of("prologue.geraud.reveil.01"));
            VoicePackets.Hello.CODEC.encode(b, packet);
            assertEquals(packet, VoicePackets.Hello.CODEC.decode(b));
        } finally { b.release(); }
    }
    @Test void rejectsAnUnboundedCapabilityListBeforeAllocation() {
        var b = buffer();
        try {
            b.writeVarInt(1); b.writeString("demo-1"); b.writeBoolean(true); b.writeVarInt(Integer.MAX_VALUE);
            assertThrows(IllegalArgumentException.class, () -> VoicePackets.Hello.CODEC.decode(b));
        } finally { b.release(); }
    }
    @Test void startAndEndRetainTheExactAuthorityToken() {
        var b = buffer();
        var token = new VoicePackets.Token(UUID.randomUUID(), 71, 999, "prologue.geraud.reveil.01");
        try {
            var start = new VoicePackets.Start(token, UUID.randomUUID(), 15);
            VoicePackets.Start.CODEC.encode(b, start);
            assertEquals(start, VoicePackets.Start.CODEC.decode(b));
            b.clear();
            var end = new VoicePackets.End(token, PlaybackLease.End.SKIP);
            VoicePackets.End.CODEC.encode(b, end);
            assertEquals(end, VoicePackets.End.CODEC.decode(b));
        } finally { b.release(); }
    }
    @Test void rejectsUnknownEndReasons() {
        var b = buffer();
        try {
            b.writeUuid(UUID.randomUUID()); b.writeVarInt(1); b.writeVarLong(1); b.writeString("known.line");
            b.writeVarInt(99);
            assertThrows(IllegalArgumentException.class, () -> VoicePackets.End.CODEC.decode(b));
        } finally { b.release(); }
    }
}
