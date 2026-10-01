package fr.hautecapitale.rpghud.mixin;

import net.minecraft.world.World;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

/**
 * Smooths only RPG-HUD's visual clock for Haute Capitale's 0.5x timeOfDay cycle.
 *
 * The server remains authoritative. This code never writes world time or gamerules.
 * It only replaces the value read by RPG-HUD's clock renderer.
 */
@Mixin(targets = "net.spellcraftgaming.rpghud.gui.hud.element.vanilla.HudElementClockVanilla", remap = false)
public abstract class HudElementClockVanillaMixin {
    @Unique
    private static Object hc$worldIdentity;

    @Unique
    private static long hc$anchorGameTime;

    @Unique
    private static double hc$anchorDayTime;

    @Unique
    private static long hc$lastGameTime = Long.MIN_VALUE;

    @Unique
    private static long hc$lastRawDayTime = Long.MIN_VALUE;

    @Unique
    private static long hc$lastRawChangeGameTime = Long.MIN_VALUE;

    @Unique
    private static boolean hc$frozen;

    @Redirect(
        method = {"getTime", "getClockColor"},
        at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/class_1937;method_8532()J",
            remap = false
        ),
        remap = false,
        require = 1
    )
    private long hc$useSmoothedTimeOfDay(World world) {
        return hc$getSmoothedTimeOfDay(world);
    }

    @Unique
    private static synchronized long hc$getSmoothedTimeOfDay(World world) {
        final long raw = world.getTimeOfDay();
        final long game = world.getTime();

        if (hc$worldIdentity != world || hc$lastGameTime == Long.MIN_VALUE) {
            hc$reset(world, raw, game);
            return raw;
        }

        final long gameDelta = game - hc$lastGameTime;
        final long rawDelta = raw - hc$lastRawDayTime;

        // Dimension/world change, reconnect, pause discontinuity or other large gap.
        if (gameDelta < 0L || gameDelta > 400L) {
            hc$reset(world, raw, game);
            return raw;
        }

        if (gameDelta > 0L) {
            // Normal HC sync corrections are small backwards corrections.
            // A larger discrepancy is a genuine time jump (/time, sleep, etc.).
            if (rawDelta > gameDelta + 8L || rawDelta < gameDelta - 32L) {
                hc$reset(world, raw, game);
                return raw;
            }

            if (raw != hc$lastRawDayTime) {
                hc$lastRawChangeGameTime = game;

                // Resume after a genuinely frozen cycle: re-anchor instantly.
                if (hc$frozen) {
                    hc$reset(world, raw, game);
                    return raw;
                }
            }

            // If raw time itself really stops changing, stop the visual clock too.
            if (game - hc$lastRawChangeGameTime >= 2L) {
                hc$frozen = true;
                hc$anchorDayTime = raw;
                hc$anchorGameTime = game;
            }
        }

        final long result;
        if (hc$frozen) {
            result = raw;
        } else {
            result = (long) Math.floor(
                hc$anchorDayTime + (game - hc$anchorGameTime) * 0.5D
            );
        }

        hc$lastGameTime = game;
        hc$lastRawDayTime = raw;
        return result;
    }

    @Unique
    private static void hc$reset(Object world, long raw, long game) {
        hc$worldIdentity = world;
        hc$anchorGameTime = game;
        hc$anchorDayTime = raw;
        hc$lastGameTime = game;
        hc$lastRawDayTime = raw;
        hc$lastRawChangeGameTime = game;
        hc$frozen = false;
    }
}
