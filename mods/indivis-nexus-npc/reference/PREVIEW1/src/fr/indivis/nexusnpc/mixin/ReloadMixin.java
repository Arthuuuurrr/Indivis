package fr.indivis.nexusnpc.mixin;
import fr.indivis.nexusnpc.client.NpcClientBridge;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.*;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
@Mixin(targets="net.tompsen.nexuscharacters.GenericSkinLayerSupport",remap=false)
public class ReloadMixin {
 @Inject(method="clearCaches",at=@At("TAIL"),remap=false)
 private static void indivis$reload(CallbackInfo ci){NpcClientBridge.clear();}
}
