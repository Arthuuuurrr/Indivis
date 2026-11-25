package perfqa.mixin;
import perfqa.PerfMain;
import net.minecraft.*;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.*;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
@Mixin(targets="net.tompsen.nexuscharacters.CharacterCosmeticFeatureRenderer",remap=false)
public class CosmeticTrace {
 @Inject(method="render",at=@At("HEAD"),remap=false)
 private void start(class_4587 matrices,class_11659 queue,int light,class_10055 state,float yaw,float pitch,CallbackInfo ci){PerfMain.cosmeticStart();}
 @Inject(method="render",at=@At("RETURN"),remap=false)
 private void end(class_4587 matrices,class_11659 queue,int light,class_10055 state,float yaw,float pitch,CallbackInfo ci){PerfMain.cosmeticEnd();}
}
