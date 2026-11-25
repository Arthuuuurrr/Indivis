package perfqa.mixin;
import perfqa.PerfMain;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.*;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
@Mixin(targets="net.tompsen.nexuscharacters.GenericSkinLayerSupport",remap=false)
public class NativeTrace {
 @Inject(method="player",at=@At("HEAD"),remap=false)
 private static void start(Object model,Object state,CallbackInfo ci){PerfMain.nativeStart();}
 @Inject(method="player",at=@At("RETURN"),remap=false)
 private static void end(Object model,Object state,CallbackInfo ci){PerfMain.nativeEnd();}
}
