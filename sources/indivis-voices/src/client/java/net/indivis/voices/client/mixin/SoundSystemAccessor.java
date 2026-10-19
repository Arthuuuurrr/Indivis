package net.indivis.voices.client.mixin;

import java.util.Map;
import net.minecraft.client.sound.*;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.*;

@Mixin(SoundSystem.class)
public interface SoundSystemAccessor {
    @Accessor("sources") Map<SoundInstance, Channel.SourceManager> indivisVoices$sources();
    @Invoker("getAdjustedVolume") float indivisVoices$adjustedVolume(SoundInstance sound);
}
