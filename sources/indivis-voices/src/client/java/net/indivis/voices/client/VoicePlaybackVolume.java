package net.indivis.voices.client;

import net.indivis.voices.IndivisVoices;
import net.indivis.voices.session.PlaybackVolume;
import net.minecraft.client.sound.PositionedSoundInstance;
import net.minecraft.client.sound.SoundInstance;
import net.minecraft.sound.SoundCategory;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.random.Random;

/** Only marked Indivis sounds can opt a freshly allocated source into this volume profile. */
public final class VoicePlaybackVolume {
    public interface VoiceSound {}
    public interface SourceControl { void indivisVoices$enableVolume(); }
    private static volatile String diagnostic = "lecture ×" + PlaybackVolume.REQUESTED + " en attente de vérification audio";
    private VoicePlaybackVolume() {}

    public static String diagnostic() { return diagnostic; }
    public static synchronized void report(float multiplier, String reason) {
        String next = "lecture ×" + multiplier + " (" + reason + ")";
        if (next.equals(diagnostic)) return;
        diagnostic = next;
        IndivisVoices.LOG.info("Voices client : {}", next);
    }
    public static final class Preview extends PositionedSoundInstance implements VoiceSound {
        public Preview(String event, float volume) {
            super(Identifier.of("indivis_voices", event), SoundCategory.VOICE, volume, 1f, Random.create(),
                false, 0, SoundInstance.AttenuationType.NONE, 0, 0, 0, true);
        }
    }
}
