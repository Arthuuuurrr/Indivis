package npctest;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.*;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
@Mixin(targets="net.tompsen.nexuscharacters.GenericSkinLayerSupport",remap=false)
public class NpcTrace {
 @Inject(method="player",at=@At("RETURN"),remap=false)
 private static void indivis$trace(Object model,Object state,CallbackInfo ci)throws Exception{Class.forName("NpcIntegrationTest").getMethod("audit",Object.class,Object.class).invoke(null,model,state);}
}


