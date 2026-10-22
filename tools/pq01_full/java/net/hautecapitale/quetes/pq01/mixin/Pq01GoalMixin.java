package net.hautecapitale.quetes.pq01.mixin;
import net.minecraft.class_1352;
import net.hautecapitale.quetes.sequence.Pq01Actors;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(targets="net.minecraft.class_1355")
public abstract class Pq01GoalMixin {
  @Inject(method="method_6277(ILnet/minecraft/class_1352;)V",at=@At("HEAD"),cancellable=true)
  private void indivisNoAutonomousGoals(int priority,class_1352 goal,CallbackInfo ci){
    if(Pq01Actors.protectedSelector(this))ci.cancel();
  }
}
