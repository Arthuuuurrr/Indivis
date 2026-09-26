package net.hautecapitale.metiers.mixin;

import net.hautecapitale.metiers.repair.Breakage;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.server.network.ServerPlayerEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.function.Consumer;

/**
 * Le second mixin du mod : brisé plutôt que détruit.
 *
 * <p>Toutes les façons d'user un objet — frapper, creuser, parer, encaisser —
 * finissent dans {@code ItemStack.onDurabilityChange}, qui écrit la nouvelle
 * durabilité et détruit l'objet si elle atteint le maximum. On regarde juste
 * avant : si l'objet est de ceux qui se brisent, {@link Breakage} le marque et
 * le garde, et Minecraft ne fait rien. Sinon, rien ne change.
 */
@Mixin(ItemStack.class)
abstract class ItemStackMixin {

    @Inject(method = "onDurabilityChange", at = @At("HEAD"), cancellable = true)
    private void hcm$breakInsteadOfDestroy(int newDamage, ServerPlayerEntity player, Consumer<Item> breakCallback,
                                           CallbackInfo info) {
        if (Breakage.intercept((ItemStack) (Object) this, newDamage, player)) {
            info.cancel();
        }
    }
}
