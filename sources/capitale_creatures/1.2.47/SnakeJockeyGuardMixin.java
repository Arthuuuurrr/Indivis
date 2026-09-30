package fr.hautecapitale.creatures.mixin;

import net.minecraft.class_1297;
import net.minecraft.class_2960;
import net.minecraft.class_7923;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

/**
 * Prevents vanilla skeleton jockeys on Haute Capitale snake entities at the
 * moment the riding relationship is attempted. No tick scan is used.
 */
@Mixin(value = class_1297.class, remap = false)
public abstract class SnakeJockeyGuardMixin {
    private static final String SKELETON = "minecraft:skeleton";
    private static final String RATTLESNAKE = "cubeanimals:rattlesnake";
    private static final String IMPERIAL_SNAKE = "capitale_entities:snake";

    @Inject(
        method = "method_5873(Lnet/minecraft/class_1297;ZZ)Z",
        at = @At("HEAD"),
        cancellable = true,
        remap = false
    )
    private void hauteCapitale$blockSkeletonSnakeJockey(
        class_1297 vehicle,
        boolean force,
        boolean emitEvent,
        CallbackInfoReturnable<Boolean> cir
    ) {
        class_1297 rider = (class_1297) (Object) this;
        if (!SKELETON.equals(hauteCapitale$entityTypeId(rider))) {
            return;
        }

        String vehicleId = hauteCapitale$entityTypeId(vehicle);
        if (RATTLESNAKE.equals(vehicleId) || IMPERIAL_SNAKE.equals(vehicleId)) {
            rider.method_31472();
            cir.setReturnValue(Boolean.FALSE);
        }
    }

    private static String hauteCapitale$entityTypeId(class_1297 entity) {
        class_2960 id = class_7923.field_41177.method_10221(entity.method_5864());
        return id == null ? "" : id.toString();
    }
}
