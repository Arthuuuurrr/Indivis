package net.hautecapitale.quetes.pq02.mixin;
import net.hautecapitale.quetes.sequence.Pq02Runtime;import net.minecraft.class_3222;
import net.hautecapitale.dialogue.session.DialogueSession;import net.hautecapitale.dialogue.session.CloseReason;
import org.spongepowered.asm.mixin.Mixin;import org.spongepowered.asm.mixin.injection.*;import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
@Mixin(targets="net.hautecapitale.dialogue.session.DialogueManager",remap=false)
public abstract class Pq02DialogueCloseMixin {
 @Inject(method="terminer",at=@At("HEAD"),remap=false)
 private static void pq02$closed(class_3222 player,DialogueSession session,CloseReason reason,boolean send,CallbackInfo callback){Pq02Runtime.onClosing(player,session,reason.toString());}
}
