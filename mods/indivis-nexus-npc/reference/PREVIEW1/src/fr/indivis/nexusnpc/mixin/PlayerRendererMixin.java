package fr.indivis.nexusnpc.mixin;
import fr.indivis.nexusnpc.client.NpcClientBridge;
import net.minecraft.*;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.*;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
@Mixin(targets="de.markusbordihn.easynpc.client.renderer.entity.standard.PlayerRenderer",remap=false)
public class PlayerRendererMixin {
 @Inject(method="extractRenderState(Lnet/minecraft/class_1314;Lnet/minecraft/class_10055;F)V",at=@At("TAIL"),remap=false)
 private void indivis$appearance(class_1314 entity,class_10055 state,float delta,CallbackInfo ci){NpcClientBridge.extract(entity,state);}
}
