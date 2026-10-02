package pre24test;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.*;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(targets="net.minecraft.class_591", priority=4000, remap=false)
public abstract class ClothingComparisonMixin {
  @Inject(method="method_62110(Lnet/minecraft/class_10055;)V", at=@At("TAIL"), require=1, remap=false)
  private void comparison$geometry(@Coerce Object state, CallbackInfo ci) {
    try { Class.forName("ClothingComparison").getMethod("apply",Object.class).invoke(null,this); }
    catch (Exception e) { throw new RuntimeException(e); }
  }
}
