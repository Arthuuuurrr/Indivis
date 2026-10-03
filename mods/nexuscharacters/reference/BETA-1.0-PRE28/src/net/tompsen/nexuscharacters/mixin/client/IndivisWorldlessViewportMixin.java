package net.tompsen.nexuscharacters.mixin.client;

import net.minecraft.class_332;
import net.tompsen.nexuscharacters.*;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;

@Mixin(value = CharacterPreviewRenderer.class, remap = false)
public abstract class IndivisWorldlessViewportMixin {
    @ModifyVariable(method = "drawWorldless", at = @At("STORE"), index = 9, remap = false)
    private static int indivis$fullViewport(int original) {
        IndivisMenus.State state = IndivisReadability.preview();
        return state != null && state.pw >= 70 ? state.pw : original;
    }

    @ModifyVariable(method = "drawWorldless", at = @At("STORE"), index = 8, remap = false)
    private static int indivis$fitHeight(int original, class_332 d, CharacterDto dto, int center, int top, int bottom, int mx, int my) {
        IndivisMenus.State state = IndivisReadability.preview();
        if (state == null || state.pw < 70) return original;
        CharacterRace race = dto.characterRace();
        float availableHeight = Math.max(30, bottom - top - 12);
        float availableWidth = Math.max(30, state.pw - 12);
        // Reserve room for maximum build, arms, ears and long hair at every yaw.
        float base = Math.min(availableHeight / race.maxHeight(), availableWidth / (0.7f * race.maxBuild() * race.maxHeight()));
        return Math.max(30, Math.round(base * race.clampHeight(dto.heightScale())));
    }
}
