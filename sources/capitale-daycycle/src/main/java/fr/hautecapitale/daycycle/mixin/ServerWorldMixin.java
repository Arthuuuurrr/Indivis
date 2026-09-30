package fr.hautecapitale.daycycle.mixin;

import net.minecraft.server.world.ServerWorld;
import net.minecraft.world.level.ServerWorldProperties;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArg;

/**
 * Changes only the vanilla time-of-day increment performed by ServerWorld.tickTime().
 *
 * <p>Vanilla still executes tickTime() every server tick. gameTime, scheduled ticks,
 * weather timers, random ticks, entities, redstone, cooldowns and every other
 * tick-based subsystem therefore keep their vanilla cadence. Only every second
 * automatic +1 of timeOfDay is neutralized.</p>
 *
 * <p>/time commands and sleep jumps call setTimeOfDay outside this injection point
 * and are intentionally left untouched. doDaylightCycle is neither changed nor
 * bypassed: when vanilla disables the automatic increment, this injector simply
 * has no invocation to modify.</p>
 */
@Mixin(ServerWorld.class)
public abstract class ServerWorldMixin {
    @Unique
    private boolean capitaleDaycycle$advanceThisInvocation;

    @ModifyArg(
        method = "tickTime",
        at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/world/level/ServerWorldProperties;setTimeOfDay(J)V"
        ),
        index = 0,
        require = 1
    )
    private long capitaleDaycycle$halveAutomaticTimeOfDay(long vanillaNextTimeOfDay) {
        this.capitaleDaycycle$advanceThisInvocation = !this.capitaleDaycycle$advanceThisInvocation;

        if (this.capitaleDaycycle$advanceThisInvocation) {
            return vanillaNextTimeOfDay;
        }

        return ((ServerWorld) (Object) this).getTimeOfDay();
    }
}
