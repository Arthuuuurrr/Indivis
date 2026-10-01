package net.hautecapitale.dialogue.patch.mixin;

import java.util.List;
import net.minecraft.text.ClickEvent;
import net.minecraft.text.Style;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(targets = "net.hautecapitale.dialogue.client.capture.MessageCapture", remap = false)
public abstract class MessageCaptureMixin {

    @Inject(method = "terminer", at = @At("HEAD"), cancellable = true, remap = false)
    private static void hc$b9OnlyRunCommandChoices(
            List<?> choix,
            ClickEvent clickEvent,
            StringBuilder libelle,
            Style style,
            CallbackInfo ci) {
        if (clickEvent != null && !(clickEvent instanceof ClickEvent.RunCommand)) {
            libelle.setLength(0);
            ci.cancel();
        }
    }
}
