package net.tompsen.nexuscharacters;

import java.util.Locale;

public enum CharacterCosmetics$BeardStyle {
    NONE("none", "Aucune"),
    SHORT("short", "Courte"),
    FULL("full", "Pleine"),
    FORKED("forked", "Fourchue"),
    BRAIDED("braided", "Tressée"),
    LONG("long", "Longue");

    private final String id;
    private final String label;

    CharacterCosmetics$BeardStyle(String id, String label) {
        this.id = id;
        this.label = label;
    }

    public String id() { return id; }
    public String label() { return label; }

    public CharacterCosmetics$BeardStyle next() {
        return switch (this) {
            case NONE -> SHORT;
            case SHORT -> FULL;
            case FULL -> FORKED;
            case FORKED -> BRAIDED;
            case BRAIDED -> LONG;
            case LONG -> SHORT;
        };
    }

    public static CharacterCosmetics$BeardStyle fromId(String value) {
        String id = value == null ? "" : value.trim().toLowerCase(Locale.ROOT);
        int sep = id.indexOf('#');
        if (sep >= 0) id = id.substring(0, sep).trim();
        for (CharacterCosmetics$BeardStyle style : values()) {
            if (style.id.equals(id)) return style;
        }
        return NONE;
    }
}
