package net.tompsen.nexuscharacters.mixin.client;

import java.util.concurrent.CompletableFuture;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import net.tompsen.nexuscharacters.GenericSkinLayerSupport;

@Mixin(targets="net.minecraft.class_310",remap=false)
public abstract class GenericLayerReloadMixin {
    @Inject(method="method_1521()Ljava/util/concurrent/CompletableFuture;",at=@At("HEAD"),require=1,remap=false)
    private void nexus$reloadLayers(CallbackInfoReturnable<CompletableFuture<Void>> ci){GenericSkinLayerSupport.clearCaches();}
}
