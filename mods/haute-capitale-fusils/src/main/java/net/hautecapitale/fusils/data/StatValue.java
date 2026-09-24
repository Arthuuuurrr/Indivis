package net.hautecapitale.fusils.data;

import java.util.ArrayList;
import java.util.List;

/** Une statistique composée : base, puis additions, puis multiplications, puis éventuel forçage. */
public final class StatValue {
	private double base;
	private final List<StatModifier> modifiers = new ArrayList<>();

	public StatValue(double base) {
		this.base = base;
	}

	public double base() {
		return this.base;
	}

	public void setBase(double base) {
		this.base = base;
	}

	public void add(StatModifier m) {
		this.modifiers.add(m);
	}

	public List<StatModifier> modifiers() {
		return this.modifiers;
	}

	public double compute() {
		double set = Double.NaN;
		double sum = this.base;
		double mul = 1.0;
		for (StatModifier m : this.modifiers) {
			switch (m.op()) {
				case ADD -> sum += m.value();
				case MUL -> mul *= 1.0 + m.value();
				case SET -> set = m.value();
			}
		}
		return Double.isNaN(set) ? sum * mul : set;
	}

	public StatValue copy() {
		StatValue c = new StatValue(this.base);
		c.modifiers.addAll(this.modifiers);
		return c;
	}
}
