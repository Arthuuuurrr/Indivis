package net.tompsen.nexuscharacters.mixin.client;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Coerce;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import net.tompsen.nexuscharacters.GenericSkinLayerSupport;

/** Install after native first-person preparation and before the arm is submitted. */
@Mixin(targets="net.minecraft.class_1007",remap=false)
public abstract class GenericFirstPersonLayersMixin {
    @Inject(method="method_23205(Lnet/minecraft/class_4587;Lnet/minecraft/class_11659;ILnet/minecraft/class_2960;Lnet/minecraft/class_630;Z)V",
        at=@At(value="INVOKE",target="Lnet/minecraft/class_11659;method_73491(Lnet/minecraft/class_630;Lnet/minecraft/class_4587;Lnet/minecraft/class_1921;IILnet/minecraft/class_1058;)V"),require=1,remap=false)
    private void nexus$firstPerson(@Coerce Object matrices,@Coerce Object submits,int light,@Coerce Object texture,@Coerce Object arm,boolean sleeve,CallbackInfo ci) {
        GenericSkinLayerSupport.firstPerson(this,texture,arm);
    }
}
