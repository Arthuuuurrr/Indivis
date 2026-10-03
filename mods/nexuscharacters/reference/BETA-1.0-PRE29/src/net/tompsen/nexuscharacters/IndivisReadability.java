package net.tompsen.nexuscharacters;

import net.minecraft.*;
import org.joml.Matrix3x2fStack;
import java.util.Optional;

/** Presentation-only changes: no skin geometry, DTO or input ranges are changed. */
public final class IndivisReadability {
    private static final int INK = IndivisTheme.INK;
    private IndivisReadability() {}

    public static boolean active() {
        class_437 screen = class_310.method_1551().field_1755;
        return screen != null && IndivisMenus.STATES.containsKey(screen);
    }

    public static IndivisMenus.State preview() {
        return IndivisMenus.STATES.get(class_310.method_1551().field_1755);
    }

    public static CharacterRace currentRace() {
        class_437 screen = class_310.method_1551().field_1755;
        if (screen instanceof CharacterCreationScreen) {
            return (CharacterRace) IndivisMenus.get(screen, "race");
        }
        if (screen instanceof CharacterSelectionScreen) {
            CharacterDto dto = (CharacterDto) IndivisMenus.get(screen, "previewCharacter");
            if (dto != null) return dto.characterRace();
        }
        return CharacterRace.HUMAN;
    }

    public static class_2960 banner(CharacterRace race) {
        String file = switch (race) {
            case NORDIC -> "capitals/nordic.png";
            case DWARF -> "capitals/dwarf.png";
            case HIGH_ELF -> "capitals/high_elf.png";
            case WOOD_ELF -> "capitals/wood_elf.png";
            default -> "empire.png";
        };
        return class_2960.method_60655("nexuscharacters", "textures/gui/indivis/" + file);
    }

    public static int contrastBackground(int color) {
        return 0xfff5e9c9;
    }

    public static void outlined(class_332 d, class_327 font, class_5481 text, int x, int y, int color) {
        int brightness = (((color >> 16) & 255) * 299 + ((color >> 8) & 255) * 587 + (color & 255) * 114) / 1000;
        if (brightness >= 140) {
            class_5481 edge = visitor -> text.accept((index, style, codepoint) -> visitor.accept(index, style.method_36139(INK), codepoint));
            Matrix3x2fStack matrix = d.method_51448();
            for (float[] offset : new float[][]{{-.35f, 0}, {.35f, 0}, {0, -.35f}, {0, .35f}}) {
                matrix.pushMatrix(); matrix.translate(offset[0], offset[1]);
                d.method_51430(font, edge, x, y, INK, false);
                matrix.popMatrix();
            }
        }
        d.method_51430(font, text, x, y, color, false);
    }

    public static void name(class_332 d, class_327 font, class_2561 text, int center, int y, int defaultColor) {
        int[] x = {center - font.method_27525(text) / 2};
        text.method_27658((style, part) -> {
            class_2561 run = class_2561.method_43470(part).method_10862(style);
            int color = style.method_10973() == null ? defaultColor : style.method_10973().method_27716();
            int width = font.method_27525(run);
            outlined(d, font, run.method_30937(), x[0], y, 0xff000000 | color);
            x[0] += width;
            return Optional.empty();
        }, class_2583.field_24360);
    }

    public static void slider(class_332 d, class_327 font, class_339 widget, int mx, int my) {
        int x = widget.method_46426(), y = widget.method_46427();
        int width = widget.method_25368(), height = widget.method_25364();
        boolean focus = widget.field_22763 && (widget.method_25405(mx, my) || widget.method_25370());
        d.method_25294(x, y, x + width, y + height, focus ? 0xff743629 : IndivisTheme.GOLD);
        d.method_25294(x + 1, y + 1, x + width - 1, y + height - 1, 0xfff5e9c9);
        class_2561 label = IndivisTheme.font(widget.method_25369());
        float scale = Math.min(1f, (width - 10f) / Math.max(1, font.method_27525(label)));
        Matrix3x2fStack matrix = d.method_51448();
        matrix.pushMatrix();
        matrix.translate(x + width / 2f, y + 1f);
        matrix.scale(scale, scale);
        d.method_51439(font, label, -font.method_27525(label) / 2, 0, IndivisTheme.INK, false);
        matrix.popMatrix();
        int left = x + 5, right = x + width - 6, trackY = y + height - 7;
        double fraction = Math.max(0d, Math.min(1d, ((Number) IndivisMenus.get(widget, "field_22753")).doubleValue()));
        int thumb = left + (int) Math.round(fraction * (right - left));
        d.method_25294(left - 1, trackY - 1, right + 2, trackY + 4, IndivisTheme.INK);
        d.method_25294(left, trackY, right + 1, trackY + 3, 0xffc5b48b);
        d.method_25294(left, trackY, thumb + 1, trackY + 3, IndivisTheme.WINE);
        d.method_25294(thumb - 3, trackY - 3, thumb + 4, trackY + 6, IndivisTheme.INK);
        d.method_25294(thumb - 2, trackY - 2, thumb + 3, trackY + 5, 0xffe6bc65);
        d.method_25294(thumb, trackY - 1, thumb + 1, trackY + 4, IndivisTheme.WINE);
    }
}
