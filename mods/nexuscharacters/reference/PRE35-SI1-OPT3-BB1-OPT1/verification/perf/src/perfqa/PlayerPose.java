package perfqa.mixin;
import perfqa.PerfMain;
import net.minecraft.*;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.*;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
@Mixin(value=class_1007.class,remap=false)
public class PlayerPose {
 @Inject(method="method_62604(Lnet/minecraft/class_11890;Lnet/minecraft/class_10055;F)V",at=@At("TAIL"),remap=false)
 private void pose(class_11890 entity,class_10055 state,float delta,CallbackInfo ci){PerfMain.pose(entity,state);}
}
