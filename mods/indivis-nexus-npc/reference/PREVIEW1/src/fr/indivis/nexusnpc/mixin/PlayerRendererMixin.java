package fr.indivis.nexusnpc.mixin;
import fr.indivis.nexusnpc.client.NpcClientBridge;
import fr.indivis.nexusnpc.client.NpcCosmeticLayer;
import net.minecraft.*;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.*;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
@Mixin(targets="de.markusbordihn.easynpc.client.renderer.entity.standard.PlayerRenderer",remap=false)
public class PlayerRendererMixin {
 @SuppressWarnings("unchecked")
 @Inject(method="<init>(Lnet/minecraft/class_5617$class_5618;ZZ)V",at=@At("TAIL"),remap=false)
 private void indivis$features(class_5617.class_5618 context,boolean slim,boolean extra,CallbackInfo ci){
  ((LivingFeatureAccessor)this).indivis$addFeature(new NpcCosmeticLayer((class_3883<class_10055,class_591>)(Object)this));
 }
 @Inject(method="extractRenderState(Lnet/minecraft/class_1314;Lnet/minecraft/class_10055;F)V",at=@At("TAIL"),remap=false)
 private void indivis$appearance(class_1314 entity,class_10055 state,float delta,CallbackInfo ci){NpcClientBridge.extract(entity,state);}
}
