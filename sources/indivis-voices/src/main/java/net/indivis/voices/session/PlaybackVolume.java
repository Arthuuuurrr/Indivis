package net.indivis.voices.session;

/** Playback-only amplification; never edits an audio buffer or another sound's volume. */
public final class PlaybackVolume {
    public static final float REQUESTED = 3f;
    private PlaybackVolume() {}

    public static float allowed(boolean extendedGain, boolean outputLimiter, float implementationLimit) {
        if (!extendedGain || !outputLimiter || !Float.isFinite(implementationLimit) || implementationLimit < 1) return 1;
        return Math.min(REQUESTED, implementationLimit);
    }
    public static float apply(float volume, float multiplier) {
        if (!Float.isFinite(volume) || !Float.isFinite(multiplier)) return 0;
        return Math.clamp(volume, 0f, 1f) * Math.clamp(multiplier, 1f, REQUESTED);
    }
}
