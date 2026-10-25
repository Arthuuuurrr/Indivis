package pre30test;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
@Mixin(targets="net.tompsen.nexuscharacters.GenericSkinLayerSupport",remap=false)
public class PlayerTraceMixin {
 @Inject(method="player",at=@At("RETURN"),remap=false)
 private static void pre35$trace(Object model,Object state,CallbackInfo ci)throws Exception{
  Class.forName("MultiPlayerWorld").getMethod("audit",Object.class,Object.class).invoke(null,model,state);
 }
}
