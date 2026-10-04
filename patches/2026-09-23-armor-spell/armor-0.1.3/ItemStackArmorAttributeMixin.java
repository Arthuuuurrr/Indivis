package fr.hautecapitale.armorbalance.mixin;

import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import fr.hautecapitale.armorbalance.RuntimeArmorScaler;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(targets = "net.minecraft.class_1799")
abstract class ItemStackArmorAttributeMixin {
    @ModifyReturnValue(method = "method_58695", at = @At("RETURN"))
    private Object capitale$scaleEffectiveArmorComponent(Object original) {
        return RuntimeArmorScaler.scale(original);
    }
}
