package net.hautecapitale.quetes.pq03.mixin;
import net.hautecapitale.quetes.sequence.Pq03Runtime;
import net.minecraft.class_1297;import net.minecraft.class_3222;
import org.spongepowered.asm.mixin.Mixin;import org.spongepowered.asm.mixin.injection.*;import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
@Mixin(targets="net.hautecapitale.quetes.visibility.QuestVisibility",remap=false)
public abstract class Pq03VisibilityMixin {
 @Inject(method="isVisibleTo",at=@At("HEAD"),cancellable=true,remap=false)
 private static void pq03$original(class_1297 entity,class_3222 player,CallbackInfoReturnable<Boolean> result){if(!Pq03Runtime.visibleOriginal(entity,player))result.setReturnValue(false);}
}
