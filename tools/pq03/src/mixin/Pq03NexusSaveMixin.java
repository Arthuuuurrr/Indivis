package net.hautecapitale.quetes.pq03.mixin;
import net.hautecapitale.quetes.sequence.Pq03Runtime;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Pseudo;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Coerce;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
/** La sortie de personnage ne dépend pas des permissions de sa fonction Nexus. */
@Pseudo
@Mixin(targets="net.tompsen.nexuscharacters.ProfileStateBridge",remap=false)
public abstract class Pq03NexusSaveMixin {
 @Inject(method="save",at=@At("HEAD"),remap=false,require=1)
 private static void pq03$finalSave(@Coerce Object player,@Coerce Object dto,CallbackInfo callback){Pq03Runtime.beforeProfileSave(player,dto);}
}
