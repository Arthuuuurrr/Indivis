package npctest;
import net.minecraft.*;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.*;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
@Mixin(value=class_922.class,remap=false)
public class FeatureTrace {
 @Inject(method="method_4054",at=@At(value="INVOKE",target="Lnet/minecraft/class_3887;method_4199(Lnet/minecraft/class_4587;Lnet/minecraft/class_11659;ILnet/minecraft/class_10017;FF)V"),remap=false)
 private void trace(class_10042 s,class_4587 m,class_11659 q,class_12075 c,CallbackInfo ci){
  try{Class.forName("NpcIntegrationTest").getMethod("featureStage",Object.class,Object.class).invoke(null,this,s);}catch(Exception e){throw new RuntimeException(e);}
 }
}

