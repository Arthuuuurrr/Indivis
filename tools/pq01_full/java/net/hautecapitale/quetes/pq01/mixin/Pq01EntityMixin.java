package net.hautecapitale.quetes.pq01.mixin;
import net.minecraft.class_1297;
import net.hautecapitale.quetes.sequence.Pq01Actors;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(targets="net.minecraft.class_1297")
public abstract class Pq01EntityMixin {
  @Inject(method="method_30949(Lnet/minecraft/class_1297;)Z",at=@At("HEAD"),cancellable=true)
  private void indivisCollision(class_1297 other,CallbackInfoReturnable<Boolean> ci){
    if(Pq01Actors.actor((class_1297)(Object)this)||Pq01Actors.actor(other))ci.setReturnValue(false);
  }
  @Inject(method="method_5697(Lnet/minecraft/class_1297;)V",at=@At("HEAD"),cancellable=true)
  private void indivisPush(class_1297 other,CallbackInfo ci){
    if(Pq01Actors.actor((class_1297)(Object)this)||Pq01Actors.actor(other))ci.cancel();
  }
  @Inject(method={"method_5696()Z","method_21749()Z","method_21750()Z","method_33189()Z"},at=@At("HEAD"),cancellable=true)
  private void indivisWorldEffects(CallbackInfoReturnable<Boolean> ci){
    if(Pq01Actors.actor((class_1297)(Object)this))ci.setReturnValue(true);
  }
  @Inject(method={"method_49108()Z","method_5732()Z","method_5810()Z"},at=@At("HEAD"),cancellable=true)
  private void indivisNoHitOrPush(CallbackInfoReturnable<Boolean> ci){
    if(Pq01Actors.actor((class_1297)(Object)this))ci.setReturnValue(false);
  }
}
