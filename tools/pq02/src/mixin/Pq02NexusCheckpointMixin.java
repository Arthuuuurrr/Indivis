package net.hautecapitale.quetes.pq02.mixin;

import net.hautecapitale.quetes.sequence.Pq02ProfileCheckpoint;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Pseudo;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Coerce;
import org.spongepowered.asm.mixin.injection.Redirect;

/** The single direct save call in NexusCharacters is its 100-tick periodic callback. */
@Pseudo
@Mixin(targets="net.tompsen.nexuscharacters.NexusCharacters",remap=false)
public abstract class Pq02NexusCheckpointMixin {
 @Redirect(method="*",at=@At(value="INVOKE",target="Lnet/tompsen/nexuscharacters/ProfileStateBridge;save(Lnet/minecraft/class_3222;Lnet/tompsen/nexuscharacters/CharacterDto;)V"),require=1,remap=false)
 private static void pq02$saveWithoutLeaving(@Coerce Object player,@Coerce Object character){Pq02ProfileCheckpoint.save(player,character);}
}
