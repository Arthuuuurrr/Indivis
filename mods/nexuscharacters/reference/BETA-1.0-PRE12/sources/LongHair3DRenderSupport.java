package net.tompsen.nexuscharacters;

import java.util.Locale;

/**
 * BETA 1.0 PRE12.
 *
 * PRE10/PRE11 attempted a second custom rendering path for long hair. PRE12
 * removes that experimental path: 3D Skin Layers now receives the Nexus dynamic
 * skin directly through SkinLayersPlayerUtilCompatMixin and performs its normal
 * player-layer rendering itself.
 */
public final class LongHair3DRenderSupport {
    private LongHair3DRenderSupport() {}

    public static String hairAssetName(int hairIndex) {
        if (hairIndex <= 5) {
            return String.format(Locale.ROOT, "hair_short_%02d.png", hairIndex);
        }
        return String.format(Locale.ROOT, "hair_long_%02d.png", hairIndex - 5);
    }

    /** Kept for binary compatibility with the PRE10 hook in the cosmetic renderer. */
    public static void render(Object renderer, Object matrices, Object queue, int light, Object state) {
        // Intentionally empty in PRE12.
    }
}
