package net.tompsen.nexuscharacters;

import java.util.Locale;

public final class BeardPaletteSupport {
    private static final String[] IDS = {
        "black", "brown", "chestnut", "blond", "light_blond", "dark_blond",
        "red", "bright_red", "auburn", "gray", "white", "silver"
    };
    private static final String[] LABELS = {
        "Noir", "Brun", "Châtain", "Blond", "Blond clair", "Blond foncé",
        "Roux", "Roux vif", "Auburn", "Gris", "Blanc", "Argenté"
    };
    private static final int[] ARGB = {
        0xFF1B1717, 0xFF563622, 0xFF7A5135, 0xFFD4B36A, 0xFFE7D59B, 0xFF9B783E,
        0xFFA94D2D, 0xFFD96526, 0xFF7E3428, 0xFF817A75, 0xFFE4DED1, 0xFFC8CCD0
    };
    private BeardPaletteSupport() {}

    public static String canonicalId(String raw) {
        if (raw == null || raw.isBlank()) return "brown";
        String s = raw;
        int hash = s.indexOf('#');
        if (hash >= 0 && hash + 1 < s.length()) s = s.substring(hash + 1);
        s = s.trim().toLowerCase(Locale.ROOT);
        if (s.equals("dark_brown")) return "brown";
        for (String id : IDS) if (id.equals(s)) return id;
        return "brown";
    }
    public static boolean isColorId(String raw) {
        if (raw == null) return false;
        String s = raw.trim().toLowerCase(Locale.ROOT);
        if (s.equals("dark_brown")) return true;
        for (String id : IDS) if (id.equals(s)) return true;
        return false;
    }
    public static int argb(String raw) {
        String id = canonicalId(raw);
        for (int i=0;i<IDS.length;i++) if (IDS[i].equals(id)) return ARGB[i];
        return ARGB[1];
    }
    public static String label(String raw) {
        String id = canonicalId(raw);
        for (int i=0;i<IDS.length;i++) if (IDS[i].equals(id)) return LABELS[i];
        return LABELS[1];
    }
    public static String nextId(String raw) {
        String id = canonicalId(raw);
        for (int i=0;i<IDS.length;i++) if (IDS[i].equals(id)) return IDS[(i+1)%IDS.length];
        return IDS[0];
    }
}
