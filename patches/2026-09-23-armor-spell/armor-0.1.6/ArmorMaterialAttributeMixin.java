package fr.hautecapitale.armorbalance.mixin;

import fr.hautecapitale.armorbalance.RuntimeArmorScaler;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(targets = "net.minecraft.class_1741")
abstract class ArmorMaterialAttributeMixin {
    @Inject(method = "method_63993", at = @At("RETURN"), cancellable = true, require = 1)
    private void capitale$doubleArmorMaterialPoints(CallbackInfoReturnable<Object> cir) {
        Object original = cir.getReturnValue();
        Object scaled = RuntimeArmorScaler.scale(original);
        if (scaled != original) {
            cir.setReturnValue(scaled);
        }
    }
}
