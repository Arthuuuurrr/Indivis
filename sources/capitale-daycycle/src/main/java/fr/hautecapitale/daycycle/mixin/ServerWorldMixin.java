package fr.hautecapitale.daycycle.mixin;

import net.minecraft.server.world.ServerWorld;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/**
 * Doubles only the automatic vanilla day/night cycle length.
 *
 * <p>The complete vanilla ServerWorld.tickTime() method still runs every server tick.
 * This preserves gameTime and every system driven by normal server ticks. At HEAD we
 * snapshot timeOfDay. At TAIL, if vanilla advanced it by exactly +1, every second
 * such automatic increment is neutralized. No other time change is intercepted.</p>
 *
 * <p>Consequences by design:
 * - doDaylightCycle=false remains fully vanilla (no +1 detected, nothing changed);
 * - /time commands remain immediate because they execute outside tickTime();
 * - sleep/time jumps remain immediate;
 * - gameTime, scheduled ticks, weather timers, random ticks, entities, redstone,
 *   cooldowns and effects are untouched.</p>
 */
@Mixin(ServerWorld.class)
public abstract class ServerWorldMixin {
    @Unique
    private long capitaleDaycycle$timeOfDayBeforeTick;

    @Unique
    private boolean capitaleDaycycle$suppressNextAutomaticIncrement;

    @Inject(method = "tickTime", at = @At("HEAD"), require = 1)
    private void capitaleDaycycle$captureTimeOfDay(CallbackInfo ci) {
        this.capitaleDaycycle$timeOfDayBeforeTick =
            ((ServerWorld) (Object) this).getTimeOfDay();
    }

    @Inject(method = "tickTime", at = @At("TAIL"), require = 1)
    private void capitaleDaycycle$halveAutomaticTimeOfDay(CallbackInfo ci) {
        ServerWorld world = (ServerWorld) (Object) this;
        long current = world.getTimeOfDay();

        // Only touch the exact vanilla automatic +1. Any other change is left alone.
        if (current == this.capitaleDaycycle$timeOfDayBeforeTick + 1L) {
            if (this.capitaleDaycycle$suppressNextAutomaticIncrement) {
                world.setTimeOfDay(this.capitaleDaycycle$timeOfDayBeforeTick);
            }
            this.capitaleDaycycle$suppressNextAutomaticIncrement =
                !this.capitaleDaycycle$suppressNextAutomaticIncrement;
        }
    }
}
