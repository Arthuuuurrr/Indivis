package net.tompsen.nexuscharacters.mixin.client;

import net.minecraft.class_332;
import net.tompsen.nexuscharacters.IndivisReadability;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;

@Mixin(class_332.class)
public abstract class IndivisTextContrastMixin {
    @ModifyVariable(method = {"method_51433", "method_51430", "method_51439"}, at = @At("HEAD"), argsOnly = true, ordinal = 0, remap = false)
    private boolean indivis$noMenuShadow(boolean shadow) {
        return IndivisReadability.active() ? false : shadow;
    }
}
