package net.tompsen.nexuscharacters.mixin.client;

import net.tompsen.nexuscharacters.*;
import net.minecraft.*;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArgs;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.invoke.arg.Args;

@Mixin(value = IndivisTheme.class, remap = false)
public abstract class IndivisBannerMixin {
    @Redirect(method = "button", at = @At(value = "INVOKE", target = "Lnet/minecraft/class_332;method_27534(Lnet/minecraft/class_327;Lnet/minecraft/class_2561;III)V"), remap = false)
    private static void indivis$coloredButtonContrast(class_332 d, class_327 font, class_2561 text, int x, int y, int defaultColor) {
        boolean[] hasColor = {false};
        text.method_27658((style, part) -> {
            if (style.method_10973() != null) hasColor[0] = true;
            return java.util.Optional.empty();
        }, class_2583.field_24360);
        if (hasColor[0]) IndivisReadability.name(d, font, text, x, y, defaultColor);
        else d.method_51439(font, text, x - font.method_27525(text) / 2, y, defaultColor, false);
    }
    @ModifyArgs(method = "background", at = @At(value = "INVOKE", target = "Lnet/tompsen/nexuscharacters/IndivisTheme;texture(Lnet/minecraft/class_332;Lnet/minecraft/class_2960;IIIIII)V", ordinal = 1), remap = false)
    private static void indivis$capitalBanner(Args args) {
        CharacterRace race = IndivisReadability.currentRace();
        args.set(1, IndivisReadability.banner(race));
        int sourceWidth = race == CharacterRace.DWARF ? 890 : 889;
        int sourceHeight = race == CharacterRace.DWARF ? 1979 : 1981;
        if (race == CharacterRace.HUMAN || race == CharacterRace.VILLAGER) {
            sourceWidth = 890;
            sourceHeight = 1979;
        }
        int oldWidth = args.get(4), height = args.get(5), x = args.get(2);
        int width = height * sourceWidth / sourceHeight;
        args.set(2, x + oldWidth - width);
        args.set(4, width);
        args.set(6, sourceWidth);
        args.set(7, sourceHeight);
    }
}
