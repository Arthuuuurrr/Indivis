package fr.indivis.nexusnpc.mixin;
import de.markusbordihn.easynpc.client.renderer.entity.state.EasyNPCRenderStateExtension;
import fr.indivis.nexusnpc.client.NpcRenderState;
import net.minecraft.*;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.*;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

/** Vanilla dispatches any AvatarRenderState to the player renderer, even an EasyNPC.
 * Keep this bridge's NPCs on their real renderer: correct root scale, attachments and layers. */
@Mixin(value=class_898.class,remap=false)
public class RenderDispatcherMixin {
 @Inject(method="method_68832",at=@At("HEAD"),cancellable=true,remap=false)
 private void indivis$keepNpcRenderer(class_10017 state,CallbackInfoReturnable<class_897<?,?>> ci){
  if(state instanceof NpcRenderState flag&&flag.indivis$hasNexusPreset()
    &&state instanceof EasyNPCRenderStateExtension ext&&ext.getEasyNPC()!=null){
   ci.setReturnValue(((class_898)(Object)this).method_3953(ext.getEasyNPC().getEntity()));
  }
 }
}
