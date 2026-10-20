package net.indivis.voices;

import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.event.lifecycle.v1.*;
import net.fabricmc.fabric.api.networking.v1.*;
import net.indivis.voices.catalog.VoiceCatalog;
import net.indivis.voices.network.VoicePackets;
import net.indivis.voices.server.VoicesServer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public final class IndivisVoices implements ModInitializer {
    public static final Logger LOG = LoggerFactory.getLogger("indivis_voices");
    public static final VoiceCatalog CATALOG = VoiceCatalog.load();
    public static VoicesServer server;
    @Override public void onInitialize() {
        PayloadTypeRegistry.playC2S().register(VoicePackets.Hello.ID, VoicePackets.Hello.CODEC);
        PayloadTypeRegistry.playC2S().register(VoicePackets.End.ID, VoicePackets.End.CODEC);
        PayloadTypeRegistry.playS2C().register(VoicePackets.Start.ID, VoicePackets.Start.CODEC);
        PayloadTypeRegistry.playS2C().register(VoicePackets.Stop.ID, VoicePackets.Stop.CODEC);
        ServerPlayNetworking.registerGlobalReceiver(VoicePackets.Hello.ID, (payload, context) -> {
            if (server != null) server.hello(context.player(), payload);
        });
        ServerPlayNetworking.registerGlobalReceiver(VoicePackets.End.ID, (payload, context) -> {
            if (server != null) server.end(context.player(), payload);
        });
        ServerLifecycleEvents.SERVER_STARTED.register(s -> {
            server = new VoicesServer(CATALOG);
            LOG.info("Indivis Voices {} : {} prises, sans remplacement des quêtes", CATALOG.revision(), CATALOG.lines().size());
        });
        ServerTickEvents.START_SERVER_TICK.register(s -> { if (server != null) server.beginTick(s); });
        ServerTickEvents.END_SERVER_TICK.register(s -> { if (server != null) server.endTick(s); });
        ServerPlayConnectionEvents.DISCONNECT.register((handler, s) -> { if (server != null) server.disconnect(handler.player); });
        ServerLifecycleEvents.SERVER_STOPPING.register(s -> { if (server != null) { server.stop(s); server = null; } });
    }
}
