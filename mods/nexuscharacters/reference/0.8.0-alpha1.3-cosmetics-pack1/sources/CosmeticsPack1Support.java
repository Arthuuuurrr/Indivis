package net.tompsen.nexuscharacters;

import java.util.Locale;

/** Haute Capitale cosmetics pack 1 compatibility helpers. */
public final class CosmeticsPack1Support {
    public static final int HAIR_MAX = 12;
    public static final int OUTFIT_MAX = 35;
    public static final int FACIAL_HAIR_MAX = 5;

    private CosmeticsPack1Support() {}

    public static String withOutfit(String marker, int outfit) {
        if (marker == null) return null;
        int safe = Math.max(1, Math.min(OUTFIT_MAX, outfit));
        String replacement = String.format(Locale.ROOT, "_o%02d_", safe);
        return marker.replaceFirst("_o\\d{2}_", replacement);
    }

    public static String facialHairLabel(int style) {
        return switch (style) {
            case 0 -> "Aucune";
            case 1 -> "Trois jours";
            case 2 -> "Moustache courte";
            case 3 -> "Moustache épaisse";
            case 4 -> "Moustache 1";
            case 5 -> "Barbe légère";
            default -> "Aucune";
        };
    }

    public static String facialHairName(int style) {
        return switch (style) {
            case 1 -> "stubble.png";
            case 2 -> "moustache_short.png";
            case 3 -> "moustache_thick.png";
            case 4 -> "moustache_user_01.png";
            case 5 -> "beard_light.png";
            default -> "stubble.png";
        };
    }
}
