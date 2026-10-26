package fr.indivis.nexusnpc.mixin;
import fr.indivis.nexusnpc.client.NpcEditor;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.*;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
@Mixin(targets="net.tompsen.nexuscharacters.CharacterCreationScreen",remap=false)
public class EditorMixin {
 @Inject(method="confirm",at=@At("HEAD"),cancellable=true,remap=false)
 private void indivis$exportOnly(CallbackInfo ci){if(NpcEditor.confirm(this))ci.cancel();}
}
