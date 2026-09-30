package net.tompsen.nexuscharacters;

import java.util.Locale;

public final class CosmeticsPack1Support {
    public static final int HAIR_MAX = 13;
    public static final int OUTFIT_MAX = 38;
    public static final int FACIAL_HAIR_MAX = 6;

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
            case 1 -> "Barbe de trois jours";
            case 2 -> "Moustache courte";
            case 3 -> "Moustache épaisse";
            case 4 -> "Moustache mince";
            case 5 -> "Barbe mince 1";
            case 6 -> "Barbe mince 2";
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
            case 6 -> "beard_light_02.png";
            default -> "stubble.png";
        };
    }
}
