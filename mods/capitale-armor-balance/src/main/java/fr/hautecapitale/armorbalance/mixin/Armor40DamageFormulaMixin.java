package fr.hautecapitale.armorbalance.mixin;

import net.minecraft.class_1282;
import net.minecraft.class_1309;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Constant;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyConstant;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

/**
 * Remaps vanilla armor mitigation from a 0..20 scale to 0..40.
 *
 * The input damage is doubled only inside DamageUtil's intermediate armor
 * calculation and the final return is halved again. Together with cap 20->40
 * and divisor 25->50, this makes a doubled modded armor value preserve exactly
 * the old protection curve (including toughness) instead of flattening every
 * high-tier set at vanilla's 20-point ceiling.
 */
@Mixin(targets = "net.minecraft.class_1280", remap = false)
public abstract class Armor40DamageFormulaMixin {
    @ModifyVariable(method = "method_5496", at = @At("HEAD"), argsOnly = true, ordinal = 0, remap = false)
    private static float capitale$armor40ScaleDamageInput(float damageAmount) {
        return damageAmount * 2.0F;
    }

    @ModifyConstant(method = "method_5496", constant = @Constant(floatValue = 20.0F), remap = false)
    private static float capitale$armor40Cap(float vanillaCap) {
        return 40.0F;
    }

    @ModifyConstant(method = "method_5496", constant = @Constant(floatValue = 25.0F), remap = false)
    private static float capitale$armor40Divisor(float vanillaDivisor) {
        return 50.0F;
    }

    @Inject(method = "method_5496", at = @At("RETURN"), cancellable = true, remap = false)
    private static void capitale$armor40RestoreDamageScale(
            class_1309 armorWearer,
            float damageAmount,
            class_1282 damageSource,
            float armor,
            float armorToughness,
            CallbackInfoReturnable<Float> cir) {
        cir.setReturnValue(cir.getReturnValueF() * 0.5F);
    }
}
