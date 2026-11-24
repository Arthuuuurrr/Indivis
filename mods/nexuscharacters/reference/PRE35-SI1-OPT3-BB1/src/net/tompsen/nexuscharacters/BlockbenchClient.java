package net.tompsen.nexuscharacters;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
public final class BlockbenchClient implements ClientModInitializer {
    public void onInitializeClient(){ClientTickEvents.END_CLIENT_TICK.register(client->BlockbenchTextureCache.tick());}
    public static void clear(){BlockbenchModelBaker.clear();BlockbenchTextureCache.clear();}
}
