package net.hautecapitale.quetes.pq03.mixin;
import java.util.Optional;import net.hautecapitale.quetes.sequence.Pq03Runtime;import net.minecraft.class_3222;
import net.hautecapitale.quetes.waypoint.ResolvedTarget;
import org.spongepowered.asm.mixin.Mixin;import org.spongepowered.asm.mixin.injection.*;import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
@Mixin(targets="net.hautecapitale.quetes.waypoint.TargetResolver",remap=false)
public abstract class Pq03WaypointMixin {
 @Inject(method="nearestTagged",at=@At("HEAD"),cancellable=true,remap=false)
 private static void pq03$personal(class_3222 player,String tag,CallbackInfoReturnable<Optional<ResolvedTarget>> result){if(tag.equals("hcm_pq3_aurele"))result.setReturnValue(Pq03Runtime.waypoint(player,tag));}
}
