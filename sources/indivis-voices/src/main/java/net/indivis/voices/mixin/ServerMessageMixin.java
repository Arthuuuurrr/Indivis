package net.indivis.voices.mixin;

import net.indivis.voices.IndivisVoices;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.*;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ServerPlayerEntity.class)
public abstract class ServerMessageMixin {
    @Inject(method = "sendMessageToClient(Lnet/minecraft/text/Text;Z)V", at = @At("HEAD"))
    private void indivisVoices$message(Text text, boolean overlay, CallbackInfo ci) {
        if (!overlay && IndivisVoices.server != null) {
            var player = (ServerPlayerEntity) (Object) this;
            // Datapack functions run on this thread. Ignore unrelated asynchronous chat.
            if (player.getEntityWorld().getServer().isOnThread()) IndivisVoices.server.message(player, text.getString());
        }
    }
}
