package net.indivis.voices;

import net.indivis.voices.session.PlaybackVolume;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class PlaybackVolumeTest {
    @Test void enablesThreeTimesPlaybackOnlyWhenProtectedAndSupported() {
        assertEquals(3f, PlaybackVolume.allowed(true, true, Float.MAX_VALUE));
        assertEquals(1f, PlaybackVolume.allowed(false, true, Float.MAX_VALUE));
        assertEquals(1f, PlaybackVolume.allowed(true, false, Float.MAX_VALUE));
    }
    @Test void respectsTheImplementationLimit() {
        assertEquals(2f, PlaybackVolume.allowed(true, true, 2));
        assertEquals(1f, PlaybackVolume.allowed(true, true, 1));
    }
    @Test void retainsMuteAndTheUserSliderProportion() {
        assertEquals(0f, PlaybackVolume.apply(0, 3));
        assertEquals(.75f, PlaybackVolume.apply(.25f, 3));
        assertEquals(3f, PlaybackVolume.apply(1, 3));
    }
    @Test void preservesFadeShapeThroughTheNativeVolumeRange() {
        assertEquals(1.5f, PlaybackVolume.apply(.5f, 3));
        assertEquals(.3f, PlaybackVolume.apply(.1f, 3), .0001f);
        assertEquals(0f, PlaybackVolume.apply(0, 3));
    }
    @Test void rejectsInvalidBackendLimits() {
        for (float limit : new float[]{Float.NaN, Float.POSITIVE_INFINITY, -1, 0}) {
            assertEquals(1f, PlaybackVolume.allowed(true, true, limit));
        }
    }
    @Test void neverSendsNegativeOrUnboundedGainToTheSource() {
        assertEquals(0f, PlaybackVolume.apply(Float.NaN, 3));
        assertEquals(0f, PlaybackVolume.apply(1, Float.POSITIVE_INFINITY));
        assertEquals(0f, PlaybackVolume.apply(-1, 3));
        assertEquals(3f, PlaybackVolume.apply(9, 9));
        assertEquals(.5f, PlaybackVolume.apply(.5f, 1));
    }
}
