package net.spellcraftgaming.rpghud.mixin;

import net.minecraft.class_1937;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

/**
 * Haute Capitale compatibility for the 40-minute day/night cycle.
 *
 * RPG-HUD normally reads ClientWorld#getTimeOfDay directly. The client predicts
 * that value at vanilla 1.0x speed between server time packets, while the Haute
 * Capitale server runs the actual cycle at 0.5x. The next server packet therefore
 * corrects the client backwards, which makes the HUD clock visibly advance and
 * then go back.
 *
 * This redirect changes only RPG-HUD's clock reads. It anchors display time to
 * monotonic gameTime and advances the displayed timeOfDay by 0.5 per game tick.
 * Small backward server corrections are ignored. Real time jumps (/time, sleep,
 * dimension/world changes) resynchronize the display immediately.
 */
@Mixin(targets = "net.spellcraftgaming.rpghud.gui.hud.element.vanilla.HudElementClockVanilla", remap = false)
public abstract class HauteCapitaleClockMixin {
    private static Object hc$world;
    private static long hc$anchorGameTime;
    private static double hc$anchorDayTime;
    private static long hc$lastGameTime = Long.MIN_VALUE;
    private static long hc$lastRawDayTime = Long.MIN_VALUE;
    private static long hc$lastRawChangeGameTime = Long.MIN_VALUE;
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
    private long hc$smoothClockTime(class_1937 world) {
        return hc$getSmoothedTimeOfDay(world);
    }

    private static synchronized long hc$getSmoothedTimeOfDay(class_1937 world) {
        final long raw = world.method_8532();
        final long game = world.method_8510();

        if (hc$world != world || hc$lastGameTime == Long.MIN_VALUE) {
            hc$reset(world, raw, game);
            return raw;
        }

        final long gameDelta = game - hc$lastGameTime;
        final long rawDelta = raw - hc$lastRawDayTime;

        if (gameDelta < 0L || gameDelta > 400L) {
            hc$reset(world, raw, game);
            return raw;
        }

        if (gameDelta > 0L) {
            // Normal HC server corrections are small and backwards.
            // Substantial jumps are real /time/sleep synchronization events.
            if (rawDelta > gameDelta + 8L || rawDelta < gameDelta - 32L) {
                hc$reset(world, raw, game);
                return raw;
            }

            if (raw != hc$lastRawDayTime) {
                hc$lastRawChangeGameTime = game;
                if (hc$frozen) {
                    hc$reset(world, raw, game);
                    return raw;
                }
            }

            // Respect a genuinely frozen day cycle; tolerate one isolated sync tick.
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
            final double smooth = hc$anchorDayTime + (game - hc$anchorGameTime) * 0.5D;
            result = (long)Math.floor(smooth);
        }

        hc$lastGameTime = game;
        hc$lastRawDayTime = raw;
        return result;
    }

    private static void hc$reset(Object world, long raw, long game) {
        hc$world = world;
        hc$anchorGameTime = game;
        hc$anchorDayTime = raw;
        hc$lastGameTime = game;
        hc$lastRawDayTime = raw;
        hc$lastRawChangeGameTime = game;
        hc$frozen = false;
    }
}
