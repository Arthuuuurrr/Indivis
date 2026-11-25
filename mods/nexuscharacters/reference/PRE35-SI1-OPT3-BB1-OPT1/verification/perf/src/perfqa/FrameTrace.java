package perfqa.mixin;
import perfqa.PerfMain;
import net.minecraft.*;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.*;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
@Mixin(value=class_757.class,remap=false)
public class FrameTrace {
 @Inject(method="method_3192",at=@At("HEAD"),remap=false)
 private void start(class_9779 ticks,boolean tick,CallbackInfo ci){PerfMain.frameStart();}
 @Inject(method="method_3192",at=@At("RETURN"),remap=false)
 private void end(class_9779 ticks,boolean tick,CallbackInfo ci){PerfMain.frameEnd();}
}
