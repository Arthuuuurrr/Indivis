package net.tompsen.nexuscharacters;

import java.util.Locale;

public final class NordicBeardSupport {
    private NordicBeardSupport() {}

    public static boolean isNordic(Object race) {
        if (race == null) return false;
        if (race instanceof Enum<?> e) return "NORDIC".equals(e.name());
        String s = race.toString().toLowerCase(Locale.ROOT);
        return s.contains("nordic") || s.contains("nordique");
    }

    public static boolean isDwarf(Object race) {
        if (race == null) return false;
        if (race instanceof Enum<?> e) return "DWARF".equals(e.name());
        String s = race.toString().toLowerCase(Locale.ROOT);
        return s.contains("dwarf") || s.contains("nain");
    }

    public static boolean isHuman(Object race) {
        if (race == null) return false;
        if (race instanceof Enum<?> e) return "HUMAN".equals(e.name());
        String s = race.toString().toLowerCase(Locale.ROOT);
        return s.contains("human") || s.contains("humain");
    }

    public static boolean isBeardedRace(Object race) {
        return isDwarf(race) || isNordic(race) || isHuman(race);
    }

    public static String defaultBeard(CharacterRace race) {
        return isDwarf(race) ? "full" : "none";
    }

    public static String normalize(Object race, String raw) {
        String style = CosmeticColorSupport.baseStyle(raw);
        String color = CosmeticColorSupport.colorId(raw);
        if (isDwarf(race)) {
            if ("none".equals(style)) style = "full";
            if (!("short".equals(style) || "full".equals(style) || "forked".equals(style)
                    || "braided".equals(style) || "long".equals(style))) style = "full";
            return style + "#" + color;
        }
        if (isNordic(race)) {
            if (!("short".equals(style) || "full".equals(style) || "long".equals(style))) return "none";
            return style + "#" + color;
        }
        if (isHuman(race)) {
            if (!"long".equals(style)) return "none";
            return style + "#" + color;
        }
        return "none";
    }

    public static CharacterCosmetics.BeardStyle nextForRace(CharacterRace race, CharacterCosmetics.BeardStyle current) {
        CharacterCosmetics.BeardStyle LONG = CharacterCosmetics.BeardStyle.valueOf("LONG");
        if (isHuman(race)) {
            return "long".equals(current.id()) ? CharacterCosmetics.BeardStyle.NONE : LONG;
        }
        if (isNordic(race)) {
            if (current == CharacterCosmetics.BeardStyle.NONE) return CharacterCosmetics.BeardStyle.SHORT;
            if (current == CharacterCosmetics.BeardStyle.SHORT) return CharacterCosmetics.BeardStyle.FULL;
            if (current == CharacterCosmetics.BeardStyle.FULL) return LONG;
            return CharacterCosmetics.BeardStyle.NONE;
        }
        if (isDwarf(race)) return current.next();
        return CharacterCosmetics.BeardStyle.NONE;
    }
}
