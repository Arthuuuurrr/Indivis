package net.hautecapitale.quetes.pq01.mixin;
import java.util.List;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Coerce;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import net.hautecapitale.quetes.sequence.Pq01CaptureFilter;

@Mixin(targets="net.hautecapitale.dialogue.client.capture.MessageCapture",remap=false)
public abstract class Pq01CaptureMixin {
  @Inject(method="classer(Lnet/minecraft/class_2561;Ljava/lang/String;ZLjava/util/List;)Lnet/hautecapitale/dialogue/client/capture/MessageCapture$Classification;",at=@At("HEAD"),cancellable=true,remap=false)
  private static void indivis$excludeConnection(@Coerce Object text,String npc,boolean window,List<String> ignored,CallbackInfoReturnable<Object> result){
    if(Pq01CaptureFilter.ignore(text))result.setReturnValue(null);
  }
}
