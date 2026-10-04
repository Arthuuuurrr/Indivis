package fr.hautecapitale.armorbalance.mixin;

import fr.hautecapitale.armorbalance.RuntimeArmorScaler;
import java.util.function.BiConsumer;
import net.minecraft.class_1304;
import net.minecraft.class_9274;
import org.apache.commons.lang3.function.TriConsumer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(targets = "net.minecraft.class_9285")
abstract class AttributeModifiersComponentMixin {
    @Inject(method = "method_57482", at = @At("HEAD"), cancellable = true, require = 0)
    private void capitale$equipmentSlot(class_1304 slot, BiConsumer<Object, Object> consumer, CallbackInfo ci) {
        if (RuntimeArmorScaler.routeApply(this, "method_57482", slot, consumer)) ci.cancel();
    }

    @Inject(method = "method_60618", at = @At("HEAD"), cancellable = true, require = 0)
    private void capitale$attributeSlotBi(class_9274 slot, BiConsumer<Object, Object> consumer, CallbackInfo ci) {
        if (RuntimeArmorScaler.routeApply(this, "method_60618", slot, consumer)) ci.cancel();
    }

    @Inject(method = "method_70727", at = @At("HEAD"), cancellable = true, require = 0)
    private void capitale$attributeSlotTri(class_9274 slot, TriConsumer<Object, Object, Object> consumer, CallbackInfo ci) {
        if (RuntimeArmorScaler.routeApply(this, "method_70727", slot, consumer)) ci.cancel();
    }
}
