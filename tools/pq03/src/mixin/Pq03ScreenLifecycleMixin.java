package net.hautecapitale.quetes.pq03.mixin;

import net.hautecapitale.quetes.sequence.Pq03Client;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Coerce;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/** La capture PQ03 est possédée par sa session, pas par un conteneur vanilla. */
@Mixin(targets="net.minecraft.class_310",remap=false)
public abstract class Pq03ScreenLifecycleMixin {
 @Inject(method="method_1507(Lnet/minecraft/class_437;)V",at=@At("HEAD"),cancellable=true,remap=false)
 private void pq03$keepOwnedCapture(@Coerce Object nextScreen,CallbackInfo callback){
  if(Pq03Client.blockScreenReset(this,nextScreen))callback.cancel();
 }
}
