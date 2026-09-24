package net.hautecapitale.metiers.mixin;

import net.hautecapitale.metiers.gadget.RepellentItem;
import net.hautecapitale.metiers.hunt.OriginMarker;
import net.minecraft.entity.EntityData;
import net.minecraft.entity.SpawnReason;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.world.LocalDifficulty;
import net.minecraft.world.ServerWorldAccess;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

/**
 * Le premier mixin du mod, et le plus petit possible.
 *
 * <p>La raison d'apparition d'un mob — naturelle, générateur, œuf, commande —
 * n'est connue de Minecraft qu'au moment de {@code initialize}, puis oubliée :
 * elle n'est ni sauvegardée ni exposée par un événement. Sans elle, l'anti-farm
 * n'a rien sur quoi s'appuyer. On la lit ici, en tête de méthode, et on la
 * confie à {@link OriginMarker} qui l'écrit sur l'entité. Rien d'autre n'est
 * touché : pas de modification du comportement, pas de valeur de retour.
 *
 * <p>Le même point sert au sel sacré (étape 10) : un monstre qui apparaît
 * naturellement près d'un joueur protégé est marqué pour disparaître avant
 * d'avoir été vu. Là encore, {@code initialize} est le seul endroit où la
 * raison d'apparition existe.
 */
@Mixin(MobEntity.class)
abstract class MobEntityMixin {

    @Inject(method = "initialize", at = @At("HEAD"))
    private void hcm$markOrigin(ServerWorldAccess world, LocalDifficulty difficulty, SpawnReason reason,
                                EntityData data, CallbackInfoReturnable<EntityData> info) {
        OriginMarker.onInitialize((MobEntity) (Object) this, reason);
        RepellentItem.onInitialize((MobEntity) (Object) this, reason);
    }
}
