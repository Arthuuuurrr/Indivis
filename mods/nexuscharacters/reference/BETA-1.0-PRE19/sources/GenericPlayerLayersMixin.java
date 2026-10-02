package net.tompsen.nexuscharacters.mixin.client;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Coerce;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import net.tompsen.nexuscharacters.GenericSkinLayerSupport;

/** Tail callbacks run in reverse mixin priority: install after Skin Layers and its LOD/helmet checks. */
@Mixin(targets="net.minecraft.class_591",priority=1500,remap=false)
public abstract class GenericPlayerLayersMixin {
    @Inject(method="method_62110(Lnet/minecraft/class_10055;)V",at=@At("TAIL"),require=1,remap=false)
    private void nexus$genericLayers(@Coerce Object state,CallbackInfo ci){GenericSkinLayerSupport.player(this,state);}
}
