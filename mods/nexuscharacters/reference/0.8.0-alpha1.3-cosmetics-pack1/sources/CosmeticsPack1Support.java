package net.tompsen.nexuscharacters;

import java.util.Locale;

/** Haute Capitale cosmetics pack 1 compatibility helpers. */
public final class CosmeticsPack1Support {
    public static final int HAIR_MAX = 12;
    public static final int OUTFIT_MAX = 35;

    private CosmeticsPack1Support() {}

    /**
     * Appearance69Support historically derives the outfit from the legacy modular index,
     * whose encoding is intentionally left untouched for backward compatibility (20 outfits).
     * Character creation can now select 21..35, so only the v69 marker's outfit field is
     * rewritten from the direct UI value. Existing marker IDs and legacy indexes stay stable.
     */
    public static String withOutfit(String marker, int outfit) {
        if (marker == null) return null;
        int safe = Math.max(1, Math.min(OUTFIT_MAX, outfit));
        String replacement = String.format(Locale.ROOT, "_o%02d_", safe);
        return marker.replaceFirst("_o\\d{2}_", replacement);
    }
}
