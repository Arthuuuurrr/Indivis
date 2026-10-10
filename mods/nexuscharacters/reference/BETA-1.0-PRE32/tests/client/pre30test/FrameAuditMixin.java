package pre30test;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.*;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
@Mixin(targets="net.minecraft.class_757",remap=false)
public abstract class FrameAuditMixin {
 private static void invoke(String name){try{Class.forName("WorldDiagnostic").getDeclaredMethod(name).invoke(null);}catch(Exception e){throw new RuntimeException(e);}}
 @Inject(method="method_3192(Lnet/minecraft/class_9779;Z)V",at=@At("HEAD"),require=1,remap=false)
 private void begin(@Coerce Object tick,boolean b,CallbackInfo ci){invoke("frameStart");}
 @Inject(method="method_3192(Lnet/minecraft/class_9779;Z)V",at=@At("TAIL"),require=1,remap=false)
 private void end(@Coerce Object tick,boolean b,CallbackInfo ci){invoke("frameEnd");}
}

