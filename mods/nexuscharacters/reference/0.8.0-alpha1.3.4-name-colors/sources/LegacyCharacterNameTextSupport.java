package net.tompsen.nexuscharacters;

import net.minecraft.class_124;
import net.minecraft.class_2561;
import net.minecraft.class_5250;

/** Parses classic Minecraft &/section formatting codes in character display names. */
public final class LegacyCharacterNameTextSupport {
    private static final String SEPARATOR = "  —  ";

    private LegacyCharacterNameTextSupport() {}

    public static class_5250 parseSelectionLabel(String raw) {
        if (raw == null || raw.isEmpty()) return class_2561.method_43470("");
        int sep = raw.lastIndexOf(SEPARATOR);
        if (sep < 0) return parseLegacy(raw);

        class_5250 out = parseLegacy(raw.substring(0, sep));
        out.method_10852(class_2561.method_43470(raw.substring(sep)));
        return out;
    }

    public static class_5250 parseLegacy(String raw) {
        class_5250 out = class_2561.method_43473();
        if (raw == null || raw.isEmpty()) return out;

        char color = 0;
        boolean obfuscated = false;
        boolean bold = false;
        boolean strike = false;
        boolean underline = false;
        boolean italic = false;
        StringBuilder segment = new StringBuilder();

        for (int i = 0; i < raw.length(); i++) {
            char ch = raw.charAt(i);
            if ((ch == '&' || ch == '\u00a7') && i + 1 < raw.length()) {
                char code = Character.toLowerCase(raw.charAt(i + 1));
                if (isLegacyCode(code)) {
                    append(out, segment, color, obfuscated, bold, strike, underline, italic);
                    segment.setLength(0);

                    if (isColor(code)) {
                        color = code;
                        obfuscated = false;
                        bold = false;
                        strike = false;
                        underline = false;
                        italic = false;
                    } else {
                        switch (code) {
                            case 'k' -> obfuscated = true;
                            case 'l' -> bold = true;
                            case 'm' -> strike = true;
                            case 'n' -> underline = true;
                            case 'o' -> italic = true;
                            case 'r' -> {
                                color = 0;
                                obfuscated = false;
                                bold = false;
                                strike = false;
                                underline = false;
                                italic = false;
                            }
                            default -> { }
                        }
                    }
                    i++;
                    continue;
                }
            }
            segment.append(ch);
        }
        append(out, segment, color, obfuscated, bold, strike, underline, italic);
        return out;
    }

    private static void append(class_5250 out, StringBuilder text, char color,
                               boolean obfuscated, boolean bold, boolean strike,
                               boolean underline, boolean italic) {
        if (text.length() == 0) return;
        class_5250 piece = class_2561.method_43470(text.toString());
        if (color != 0) piece.method_27692(class_124.method_544(color));
        if (obfuscated) piece.method_27692(class_124.method_544('k'));
        if (bold) piece.method_27692(class_124.method_544('l'));
        if (strike) piece.method_27692(class_124.method_544('m'));
        if (underline) piece.method_27692(class_124.method_544('n'));
        if (italic) piece.method_27692(class_124.method_544('o'));
        out.method_10852(piece);
    }

    private static boolean isColor(char c) {
        return (c >= '0' && c <= '9') || (c >= 'a' && c <= 'f');
    }

    private static boolean isLegacyCode(char c) {
        return isColor(c) || c == 'k' || c == 'l' || c == 'm' || c == 'n' || c == 'o' || c == 'r';
    }
}
