package net.hautecapitale.fusils.data;

import java.util.Locale;
import java.util.Optional;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

import net.minecraft.util.StringRepresentable;

/**
 * Une retouche d'une statistique. Dans le JSON : {@code {"op":"add","value":2}},
 * {@code {"op":"mul","value":0.25}} (= +25 %) ou {@code {"op":"set","value":3}}.
 * La forme courte {@code "damage": 2} équivaut à {@code add}.
 */
public record StatModifier(Op op, double value) {
	public enum Op implements StringRepresentable {
		ADD, MUL, SET;

		public static final Codec<Op> CODEC = StringRepresentable.fromEnum(Op::values);

		@Override
		public String getSerializedName() {
			return name().toLowerCase(Locale.ROOT);
		}
	}

	private static final Codec<StatModifier> FULL = RecordCodecBuilder.create(b -> b.group(
			Op.CODEC.optionalFieldOf("op", Op.ADD).forGetter(StatModifier::op),
			Codec.DOUBLE.fieldOf("value").forGetter(StatModifier::value)
	).apply(b, StatModifier::new));

	public static final Codec<StatModifier> CODEC = Codec.either(Codec.DOUBLE, FULL).xmap(
			e -> e.map(d -> new StatModifier(Op.ADD, d), m -> m),
			m -> m.op == Op.ADD ? com.mojang.datafixers.util.Either.left(m.value) : com.mojang.datafixers.util.Either.right(m));

	public static StatModifier add(double v) { return new StatModifier(Op.ADD, v); }
	public static StatModifier mul(double v) { return new StatModifier(Op.MUL, v); }
	public static StatModifier set(double v) { return new StatModifier(Op.SET, v); }

	public Optional<Double> asSet() {
		return this.op == Op.SET ? Optional.of(this.value) : Optional.empty();
	}
}
