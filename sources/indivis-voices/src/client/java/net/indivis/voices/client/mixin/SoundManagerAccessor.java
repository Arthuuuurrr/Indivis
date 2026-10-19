package net.indivis.voices.client.mixin;

import net.minecraft.client.sound.*;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(SoundManager.class)
public interface SoundManagerAccessor {
    @Accessor("soundSystem") SoundSystem indivisVoices$system();
}
