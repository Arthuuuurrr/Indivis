package perfqa.mixin;
import perfqa.PerfMain;
import net.minecraft.*;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.*;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
@Mixin(targets="de.markusbordihn.easynpc.client.renderer.entity.standard.PlayerRenderer",remap=false)
public class NpcPose {
 @Inject(method="extractRenderState(Lnet/minecraft/class_1314;Lnet/minecraft/class_10055;F)V",at=@At("TAIL"),remap=false)
 private void pose(class_1314 entity,class_10055 state,float delta,CallbackInfo ci){PerfMain.pose(entity,state);}
}
