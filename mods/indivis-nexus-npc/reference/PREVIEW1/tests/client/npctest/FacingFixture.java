package npctest;
import net.minecraft.*;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.*;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import fr.indivis.nexusnpc.client.NpcRenderState;
/** Freeze the visual fixture's facing; do not let look-at behavior turn it toward the camera. */
@Mixin(targets="de.markusbordihn.easynpc.client.renderer.entity.standard.PlayerRenderer",remap=false)
public class FacingFixture {
 @Inject(method="extractRenderState(Lnet/minecraft/class_1314;Lnet/minecraft/class_10055;F)V",at=@At("TAIL"),remap=false)
 private void facing(class_1314 e,class_10055 s,float d,CallbackInfo ci){
  if(Boolean.getBoolean("npcqa.visual")&&((NpcRenderState)s).indivis$hasNexusPreset()){s.field_53446=180;s.field_53447=0;s.field_53448=0;}
 }
}
