package net.tompsen.nexuscharacters.mixin.client;

import net.minecraft.*;
import net.tompsen.nexuscharacters.*;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.*;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value = IndivisMenus.class, remap = false)
public abstract class IndivisControlsReadabilityMixin {
    @Redirect(method = "render", at = @At(value = "INVOKE", target = "Lnet/minecraft/class_332;method_27534(Lnet/minecraft/class_327;Lnet/minecraft/class_2561;III)V"), remap = false)
    private static void indivis$contrastEachName(class_332 d, class_327 font, class_2561 name, int x, int y, int color) {
        IndivisReadability.name(d, font, name, x, y, color);
    }

    @ModifyConstant(method = "render", constant = @Constant(intValue = -719183840), remap = false)
    private static int indivis$lightNameplate(int original) {
        return 0xfff5e9c9;
    }

    @Inject(method = "drawWidget", at = @At("HEAD"), cancellable = true, remap = false)
    private static void indivis$readableSliders(class_332 d, class_327 font, class_339 widget, int mx, int my, float delta, IndivisMenus.State state, CallbackInfo ci) {
        if (widget instanceof class_357 && widget.field_22764 && widget.method_46427() >= 0) {
            IndivisReadability.slider(d, font, widget, mx, my);
            ci.cancel();
        }
    }
}
