package net.tompsen.nexuscharacters.mixin.client;

import net.minecraft.*;
import net.tompsen.nexuscharacters.*;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.*;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(class_342.class)
public abstract class IndivisNameFieldContrastMixin {
    @Inject(method = "method_48579", at = @At("HEAD"), cancellable = true, remap = false)
    private void indivis$contrastingInput(class_332 d, int mx, int my, float delta, CallbackInfo ci) {
        if (!IndivisReadability.active()) return;
        class_342 field = (class_342)(Object)this;
        if (!field.field_22764 || !field.method_1851()) return;
        int x = field.method_46426(), y = field.method_46427(), w = field.method_25368(), h = field.method_25364();
        int color = ((Number)IndivisMenus.get(field, "field_2100")).intValue();
        d.method_25294(x, y, x + w, y + h, field.method_25370() ? IndivisTheme.WINE : IndivisTheme.GOLD);
        d.method_25294(x + 1, y + 1, x + w - 1, y + h - 1, IndivisReadability.contrastBackground(color));
        ci.cancel();
    }
}
