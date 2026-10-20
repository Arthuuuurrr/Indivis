package net.indivis.voices.client.mixin;

import java.util.Map;
import net.indivis.voices.client.VoicePlaybackVolume;
import net.minecraft.client.sound.Channel;
import net.minecraft.client.sound.SoundInstance;
import net.minecraft.client.sound.SoundSystem;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(SoundSystem.class)
public abstract class VoiceSoundSystemMixin {
    @Shadow @Final private Map<SoundInstance, Channel.SourceManager> sources;

    @Inject(method = "play(Lnet/minecraft/client/sound/SoundInstance;)Lnet/minecraft/client/sound/SoundSystem$PlayResult;",
        at = @At("RETURN"))
    private void indivisVoices$volume(SoundInstance sound, CallbackInfoReturnable<SoundSystem.PlayResult> cir) {
        if (!(sound instanceof VoicePlaybackVolume.VoiceSound) || cir.getReturnValue() != SoundSystem.PlayResult.STARTED) return;
        var manager = sources.get(sound);
        if (manager != null) {
            manager.run(source -> ((VoicePlaybackVolume.SourceControl) (Object) source).indivisVoices$enableVolume());
        }
    }
}
