package net.hautecapitale.quetes.pq03.mixin;
import net.minecraft.class_1297;
import org.spongepowered.asm.mixin.Mixin;import org.spongepowered.asm.mixin.injection.*;import org.spongepowered.asm.mixin.injection.callback.*;
/** Même protection physique que les acteurs PQ01, limitée aux copies PQ03. */
@Mixin(targets="net.minecraft.class_1297")
public abstract class Pq03EntityMixin {
 private static boolean pq03$actor(class_1297 e){return e!=null&&e.method_5752().contains("hcm_pq3_actor");}
 @Inject(method="method_30949(Lnet/minecraft/class_1297;)Z",at=@At("HEAD"),cancellable=true)
 private void pq03$collision(class_1297 other,CallbackInfoReturnable<Boolean> c){if(pq03$actor((class_1297)(Object)this)||pq03$actor(other))c.setReturnValue(false);}
 @Inject(method="method_5697(Lnet/minecraft/class_1297;)V",at=@At("HEAD"),cancellable=true)
 private void pq03$push(class_1297 other,CallbackInfo c){if(pq03$actor((class_1297)(Object)this)||pq03$actor(other))c.cancel();}
 @Inject(method={"method_5696()Z","method_21749()Z","method_21750()Z","method_33189()Z"},at=@At("HEAD"),cancellable=true)
 private void pq03$world(CallbackInfoReturnable<Boolean> c){if(pq03$actor((class_1297)(Object)this))c.setReturnValue(true);}
 @Inject(method={"method_49108()Z","method_5732()Z","method_5810()Z"},at=@At("HEAD"),cancellable=true)
 private void pq03$noHit(CallbackInfoReturnable<Boolean> c){if(pq03$actor((class_1297)(Object)this))c.setReturnValue(false);}
}
