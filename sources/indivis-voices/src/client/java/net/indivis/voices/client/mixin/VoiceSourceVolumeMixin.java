package net.indivis.voices.client.mixin;

import net.indivis.voices.client.VoicePlaybackVolume;
import net.indivis.voices.session.PlaybackVolume;
import net.minecraft.client.sound.Source;
import org.lwjgl.openal.AL;
import org.lwjgl.openal.AL10;
import org.lwjgl.openal.ALC10;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Source.class)
public abstract class VoiceSourceVolumeMixin implements VoicePlaybackVolume.SourceControl {
    @Shadow @Final private int pointer;
    @Unique private float indivisVoices$multiplier = 1f;
    @Unique private boolean indivisVoices$raisedCeiling;

    @Override public void indivisVoices$enableVolume() {
        if (indivisVoices$multiplier > 1) return;
        try {
            boolean extended = AL.getCapabilities().AL_SOFT_gain_clamp_ex;
            long context = ALC10.alcGetCurrentContext();
            long device = context == 0 ? 0 : ALC10.alcGetContextsDevice(context);
            // Official extension tokens: AL_GAIN_LIMIT_SOFT and ALC_OUTPUT_LIMITER_SOFT.
            boolean limiter = device != 0 && ALC10.alcIsExtensionPresent(device, "ALC_SOFT_output_limiter") &&
                ALC10.alcGetInteger(device, 0x199A) == 1;
            float limit = extended ? AL10.alGetFloat(0x200E) : 1;
            float multiplier = PlaybackVolume.allowed(extended, limiter, limit);
            if (multiplier <= 1) {
                VoicePlaybackVolume.report(1, limiter ? "amplification native indisponible" : "limiteur de sortie indisponible");
                return;
            }
            float previous = AL10.alGetSourcef(pointer, AL10.AL_GAIN);
            AL10.alSourcef(pointer, AL10.AL_MAX_GAIN, multiplier);
            indivisVoices$raisedCeiling = true;
            if (AL10.alGetSourcef(pointer, AL10.AL_MAX_GAIN) < multiplier) {
                AL10.alSourcef(pointer, AL10.AL_MAX_GAIN, 1);
                indivisVoices$raisedCeiling = false;
                VoicePlaybackVolume.report(1, "plafond de la source inchangé");
                return;
            }
            indivisVoices$multiplier = multiplier;
            ((Source) (Object) this).setVolume(previous);
            VoicePlaybackVolume.report(multiplier, "limiteur de sortie actif, fichiers intacts");
        } catch (RuntimeException | LinkageError unavailable) {
            indivisVoices$multiplier = 1;
            if (indivisVoices$raisedCeiling) {
                indivisVoices$raisedCeiling = false;
                try { AL10.alSourcef(pointer, AL10.AL_MAX_GAIN, 1); }
                catch (RuntimeException | LinkageError closedContext) { /* Normal audio remains authoritative. */ }
            }
            VoicePlaybackVolume.report(1, "profil audio indisponible ; lecture normale conservée");
        }
    }

    @ModifyVariable(method = "setVolume", at = @At("HEAD"), argsOnly = true)
    private float indivisVoices$volume(float volume) {
        return indivisVoices$multiplier == 1 ? volume : PlaybackVolume.apply(volume, indivisVoices$multiplier);
    }

    @Inject(method = {"stop", "close"}, at = @At("HEAD"))
    private void indivisVoices$restoreSource(CallbackInfo ci) {
        if (!indivisVoices$raisedCeiling) return;
        indivisVoices$multiplier = 1;
        indivisVoices$raisedCeiling = false;
        AL10.alSourcef(pointer, AL10.AL_MAX_GAIN, 1);
    }
}
