package net.indivis.voices.session;

/** Wall-clock envelope; independent of FPS/tick count. */
public final class FadeEnvelope {
    public static final long LENGTH_NANOS = 50_000_000L;
    private long start = Long.MAX_VALUE;
    public void begin(long nowNanos) { start = Math.min(start, nowNanos); }
    public float gain(long nowNanos) {
        if (start == Long.MAX_VALUE) return 1;
        return (float) Math.max(0, Math.min(1, 1.0 - (nowNanos - start) / (double) LENGTH_NANOS));
    }
    public boolean finished(long nowNanos) { return start != Long.MAX_VALUE && nowNanos - start >= LENGTH_NANOS; }
}
