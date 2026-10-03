package net.tompsen.nexuscharacters.mixin.client;

import net.minecraft.*;
import com.mojang.blaze3d.pipeline.RenderPipeline;
import net.tompsen.nexuscharacters.*;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.*;

@Mixin(class_342.class)
public abstract class IndivisNameFieldContrastMixin {
    @Redirect(method = "method_48579", at = @At(value = "INVOKE", target = "Lnet/minecraft/class_332;method_52706(Lcom/mojang/blaze3d/pipeline/RenderPipeline;Lnet/minecraft/class_2960;IIII)V"), remap = false)
    private void indivis$contrastingInput(class_332 d, RenderPipeline pipeline, class_2960 texture, int originalX, int originalY, int originalWidth, int originalHeight) {
        if (!IndivisReadability.active()) {
            d.method_52706(pipeline, texture, originalX, originalY, originalWidth, originalHeight);
            return;
        }
        class_342 field = (class_342)(Object)this;
        int x = field.method_46426(), y = field.method_46427(), w = field.method_25368(), h = field.method_25364();
        int color = ((Number)IndivisMenus.get(field, "field_2100")).intValue();
        d.method_25294(x, y, x + w, y + h, field.method_25370() ? IndivisTheme.WINE : IndivisTheme.GOLD);
        d.method_25294(x + 1, y + 1, x + w - 1, y + h - 1, IndivisReadability.contrastBackground(color));
    }
}
