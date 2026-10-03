package net.tompsen.nexuscharacters.mixin.client;

import net.tompsen.nexuscharacters.*;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArgs;
import org.spongepowered.asm.mixin.injection.invoke.arg.Args;

@Mixin(value = IndivisTheme.class, remap = false)
public abstract class IndivisBannerMixin {
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
