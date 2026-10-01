package fr.hautecapitale.nexusbridge.mixin;

import java.nio.file.Path;
import java.util.UUID;

import fr.hautecapitale.nexusbridge.NexusCapitaleFirstSpawnBridge;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(targets = "net.tompsen.nexuscharacters.ServerAuthorityV080", remap = false)
public abstract class ServerAuthorityV080Mixin {
    @Inject(method = "createEmptyAuthoritativeVault", at = @At("TAIL"), remap = false)
    private static void hc$afterEmptyVaultCreated(Path vault, UUID owner, CallbackInfo ci) {
        NexusCapitaleFirstSpawnBridge.markNewAuthoritativeVault(owner, vault);
    }
}
