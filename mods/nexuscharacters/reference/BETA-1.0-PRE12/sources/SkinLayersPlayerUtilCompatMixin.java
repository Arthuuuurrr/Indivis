package net.tompsen.nexuscharacters.mixin.client;

import net.tompsen.nexuscharacters.SkinLayersDynamicSkinBridge;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Pseudo;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Coerce;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

/**
 * Optional compatibility hook.
 *
 * Only Nexus-tagged players are overridden. With no 3D Skin Layers/TRansition
 * installation the @Pseudo target is simply absent and Nexus remains unaffected.
 */
@Pseudo
@Mixin(targets = "dev.tr7zw.transition.mc.PlayerUtil", remap = false, priority = 500)
public abstract class SkinLayersPlayerUtilCompatMixin {
    @Inject(
            method = "getPlayerSkin(Lnet/minecraft/class_11890;)Lnet/minecraft/class_2960;",
            at = @At("RETURN"),
            cancellable = true,
            remap = false,
            require = 0
    )
    private static void nexuscharacters$useDynamicAppearance(
            @Coerce Object player,
            CallbackInfoReturnable<Object> cir) {
        Object replacement = SkinLayersDynamicSkinBridge.resolve(player);
        if (replacement != null) {
            cir.setReturnValue(replacement);
        }
    }
}
