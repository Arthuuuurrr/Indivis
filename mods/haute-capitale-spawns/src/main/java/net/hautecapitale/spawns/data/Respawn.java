package net.hautecapitale.spawns.data;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.util.StringIdentifiable;

import java.util.Random;

/**
 * Le delai de reapparition d'un point.
 *
 * <p>{@code FIXED} : exactement {@code minSeconds}. {@code RANDOM} : tire entre
 * {@code minSeconds} et {@code maxSeconds} inclus, pour que trente orcs tues
 * ensemble ne reviennent pas tous a la meme seconde.
 */
public record Respawn(Mode mode, int minSeconds, int maxSeconds) {

    public enum Mode implements StringIdentifiable {
        FIXED("fixed"),
        RANDOM("random");

        public static final Codec<Mode> CODEC = StringIdentifiable.createCodec(Mode::values);

        private final String id;

        Mode(String id) {
            this.id = id;
        }

        @Override
        public String asString() {
            return this.id;
        }
    }

    public static final Codec<Respawn> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            Mode.CODEC.optionalFieldOf("mode", Mode.RANDOM).forGetter(Respawn::mode),
            Codec.INT.fieldOf("min_seconds").forGetter(Respawn::minSeconds),
            Codec.INT.optionalFieldOf("max_seconds", -1).forGetter(r -> r.mode == Mode.RANDOM ? r.maxSeconds : -1)
    ).apply(instance, Respawn::normalize));

    private static Respawn normalize(Mode mode, int min, int max) {
        int safeMin = Math.max(0, min);
        if (mode == Mode.FIXED) {
            return new Respawn(Mode.FIXED, safeMin, safeMin);
        }
        return new Respawn(Mode.RANDOM, safeMin, Math.max(safeMin, max));
    }

    public static Respawn fixed(int seconds) {
        return normalize(Mode.FIXED, seconds, seconds);
    }

    public static Respawn random(int min, int max) {
        return normalize(Mode.RANDOM, min, max);
    }

    /** Le delai effectif, en secondes. */
    public int roll(Random random) {
        if (this.mode == Mode.FIXED || this.maxSeconds <= this.minSeconds) {
            return this.minSeconds;
        }
        return this.minSeconds + random.nextInt(this.maxSeconds - this.minSeconds + 1);
    }

    public String describe() {
        return this.mode == Mode.FIXED ? this.minSeconds + " s" : this.minSeconds + "-" + this.maxSeconds + " s";
    }
}
