package net.tompsen.nexuscharacters;

import java.util.Locale;
import java.util.Set;

/** BETA 1.0 PRE4: canonical style/color/ornament codec for cultural beards. */
public final class CulturalBeardSupport {
    public static final String NONE = "none";

    private static final Set<String> ALL_STYLES = Set.of(
            "none", "short", "full", "long", "forked", "braided",
            "double_braid", "triple_braid", "runic", "segmented",
            "wild", "split", "trimmed", "noble_goatee"
    );

    private static final Set<String> ALL_ORNAMENTS = Set.of(
            "none", "iron_ring", "bronze_ring", "silver_ring", "gold_ring",
            "runic_bead", "engraved_clasp", "braid_tip"
    );

    private CulturalBeardSupport() {}

    public static String baseStyle(String raw) {
        if (raw == null || raw.isBlank()) return NONE;
        String s = raw.trim().toLowerCase(Locale.ROOT);
        int hash = s.indexOf('#');
        if (hash >= 0) s = s.substring(0, hash);
        int tilde = s.indexOf('~');
        if (tilde >= 0) s = s.substring(0, tilde);
        s = s.trim();
        return ALL_STYLES.contains(s) ? s : NONE;
    }

    public static String colorId(String raw) {
        if (raw == null || raw.isBlank()) return "brown";
        String s = raw.trim().toLowerCase(Locale.ROOT);
        int hash = s.indexOf('#');
        if (hash >= 0 && hash + 1 < s.length()) s = s.substring(hash + 1);
        int tilde = s.indexOf('~');
        if (tilde >= 0) s = s.substring(0, tilde);
        s = s.trim();
        if (s.equals("dark_brown")) return "brown";
        return switch (s) {
            case "black", "brown", "chestnut", "blond", "light_blond", "dark_blond",
                 "red", "bright_red", "auburn", "gray", "white", "silver" -> s;
            default -> "brown";
        };
    }

    public static String ornamentId(String raw) {
        if (raw == null || raw.isBlank()) return NONE;
        String s = raw.trim().toLowerCase(Locale.ROOT);
        int tilde = s.indexOf('~');
        if (tilde < 0 || tilde + 1 >= s.length()) return NONE;
        String o = s.substring(tilde + 1).trim();
        int hash = o.indexOf('#');
        if (hash >= 0) o = o.substring(0, hash);
        return ALL_ORNAMENTS.contains(o) ? o : NONE;
    }

    public static String compose(String style, String color, String ornament) {
        style = baseStyle(style);
        if (NONE.equals(style)) return NONE;
        color = colorId("x#" + (color == null ? "brown" : color));
        ornament = canonicalOrnament(ornament);
        return style + "#" + color + (NONE.equals(ornament) ? "" : "~" + ornament);
    }

    public static String normalize(Object race, String raw) {
        String style = baseStyle(raw);
        if (NONE.equals(style)) return NONE;
        if (!styleAllowed(race, style)) return NONE;
        String color = colorId(raw);
        String ornament = ornamentId(raw);
        if (!ornamentAllowed(race, style, ornament)) ornament = NONE;
        return compose(style, color, ornament);
    }

    public static boolean styleAllowed(Object race, String style) {
        style = baseStyle(style);
        if (NONE.equals(style)) return true;
        String r = raceName(race);
        return switch (r) {
            case "HUMAN" -> Set.of("long", "trimmed", "noble_goatee").contains(style);
            case "NORDIC" -> Set.of("short", "full", "long", "wild", "split").contains(style);
            case "DWARF" -> Set.of("short", "full", "long", "forked", "braided",
                    "double_braid", "triple_braid", "runic", "segmented").contains(style);
            default -> false;
        };
    }

    public static boolean ornamentAllowed(Object race, String style, String ornament) {
        ornament = canonicalOrnament(ornament);
        if (NONE.equals(ornament)) return true;
        style = baseStyle(style);
        if (NONE.equals(style)) return false;
        String r = raceName(race);
        if ("HUMAN".equals(r)) {
            return Set.of("silver_ring", "gold_ring", "engraved_clasp").contains(ornament);
        }
        if ("NORDIC".equals(r)) {
            if ("braid_tip".equals(ornament)) return Set.of("split", "long", "wild").contains(style);
            return Set.of("iron_ring", "bronze_ring", "silver_ring").contains(ornament);
        }
        if ("DWARF".equals(r)) {
            if ("braid_tip".equals(ornament)) {
                return Set.of("braided", "double_braid", "triple_braid", "forked", "segmented").contains(style);
            }
            return Set.of("iron_ring", "bronze_ring", "silver_ring", "gold_ring",
                    "runic_bead", "engraved_clasp").contains(ornament);
        }
        return false;
    }

    public static String nextOrnament(Object race, String style, String current) {
        String[] order = {"none", "iron_ring", "bronze_ring", "silver_ring", "gold_ring",
                "runic_bead", "engraved_clasp", "braid_tip"};
        String c = canonicalOrnament(current);
        int start = 0;
        for (int i = 0; i < order.length; i++) if (order[i].equals(c)) { start = i; break; }
        for (int step = 1; step <= order.length; step++) {
            String candidate = order[(start + step) % order.length];
            if (ornamentAllowed(race, style, candidate)) return candidate;
        }
        return NONE;
    }

    public static String styleLabel(String style) {
        return switch (baseStyle(style)) {
            case "short" -> "Barbe courte";
            case "full" -> "Barbe pleine";
            case "long" -> "Barbe ajourée";
            case "forked" -> "Barbe fourchue";
            case "braided" -> "Barbe tressée";
            case "double_braid" -> "Double natte";
            case "triple_braid" -> "Triple tresse";
            case "runic" -> "Barbe runique";
            case "segmented" -> "Barbe segmentée";
            case "wild" -> "Barbe sauvage";
            case "split" -> "Barbe fendue";
            case "trimmed" -> "Barbe taillée";
            case "noble_goatee" -> "Menton noble";
            default -> "Aucune";
        };
    }

    public static String ornamentLabel(String ornament) {
        return switch (canonicalOrnament(ornament)) {
            case "iron_ring" -> "Anneau de fer";
            case "bronze_ring" -> "Anneau de bronze";
            case "silver_ring" -> "Anneau d'argent";
            case "gold_ring" -> "Anneau d'or";
            case "runic_bead" -> "Perle runique";
            case "engraved_clasp" -> "Pince gravée";
            case "braid_tip" -> "Embout de tresse";
            default -> "Aucun";
        };
    }

    public static int ornamentArgb(String ornament) {
        return switch (canonicalOrnament(ornament)) {
            case "iron_ring" -> 0xFF74787C;
            case "bronze_ring" -> 0xFF9A6335;
            case "silver_ring" -> 0xFFC5CBD1;
            case "gold_ring" -> 0xFFD4A83E;
            case "runic_bead" -> 0xFF4B8E94;
            case "engraved_clasp" -> 0xFFB29652;
            case "braid_tip" -> 0xFF85898D;
            default -> 0xFFFFFFFF;
        };
    }

    public static String previewModelKey(String raw) {
        String s = baseStyle(raw);
        String o = ornamentId(raw);
        return NONE.equals(o) ? s : s + "~" + o;
    }

    public static String previewTextureKey(String raw) {
        String color = colorId(raw);
        String o = ornamentId(raw);
        return NONE.equals(o) ? color : color + "_" + o;
    }

    public static String safePreviewTextureKey(String key) {
        if (key == null || !key.matches("[a-z_]{1,48}")) return "brown";
        if (key.startsWith("black") || key.startsWith("brown") || key.startsWith("chestnut")
                || key.startsWith("blond") || key.startsWith("light_blond") || key.startsWith("dark_blond")
                || key.startsWith("red") || key.startsWith("bright_red") || key.startsWith("auburn")
                || key.startsWith("gray") || key.startsWith("white") || key.startsWith("silver")) return key;
        return "brown";
    }

    /** Used by CharacterCosmetics tag fallback; keeps old ear ids and the extended beard codec. */
    public static String sanitizeStoredStyle(String raw) {
        if (raw == null) return NONE;
        String s = raw.trim().toLowerCase(Locale.ROOT);
        if (s.matches("[a-z_]{1,32}(#[a-z_]{1,24}(~[a-z_]{1,24})?)?")) return s;
        return NONE;
    }

    public static String canonicalOrnament(String ornament) {
        if (ornament == null) return NONE;
        String o = ornament.trim().toLowerCase(Locale.ROOT);
        return ALL_ORNAMENTS.contains(o) ? o : NONE;
    }

    private static String raceName(Object race) {
        return race instanceof Enum<?> e ? e.name() : String.valueOf(race).trim().toUpperCase(Locale.ROOT);
    }
}
