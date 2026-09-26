package net.hautecapitale.fusils.data;

import java.util.ArrayList;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;

import net.hautecapitale.fusils.gun.effect.ShotEffect;

/**
 * La feuille de statistiques d'un tir, composée au moment de tirer :
 * profil de l'arme → variante → surcharges de l'objet → modifications installées → munition chargée
 * → bonus de compétence. Tout ce qui décrit le tir passe par ici, jamais par des constantes.
 */
public final class ShotStats {
	private final EnumMap<Stat, StatValue> values = new EnumMap<>(Stat.class);
	private final List<ShotEffect> effects = new ArrayList<>();
	private final List<Integer> trailColors = new ArrayList<>();
	private int muzzleTint = -1;
	private boolean forceAuto;
	/** Identifiant de la munition spéciale qui a composé ce tir, ou null. */
	private String ammoId;

	public StatValue value(Stat stat) {
		return this.values.computeIfAbsent(stat, s -> new StatValue(s.defaultValue()));
	}

	public double get(Stat stat) {
		StatValue v = this.values.get(stat);
		return v == null ? stat.defaultValue() : v.compute();
	}

	public void setBase(Stat stat, double base) {
		value(stat).setBase(base);
	}

	public void apply(Map<Stat, StatModifier> mods) {
		mods.forEach((stat, m) -> value(stat).add(m));
	}

	public void addEffect(ShotEffect effect) {
		this.effects.add(effect);
		effect.applyStats(this);
	}

	public List<ShotEffect> effects() {
		return this.effects;
	}

	public void addTrailColor(int rgb) {
		this.trailColors.add(rgb);
	}

	public List<Integer> trailColors() {
		return this.trailColors;
	}

	public int muzzleTint() {
		return this.muzzleTint;
	}

	public void setMuzzleTint(int rgb) {
		this.muzzleTint = rgb;
	}

	public boolean forceAuto() {
		return this.forceAuto;
	}

	public void setForceAuto(boolean forceAuto) {
		this.forceAuto = forceAuto;
	}

	public String ammoId() {
		return this.ammoId;
	}

	public void setAmmoId(String ammoId) {
		this.ammoId = ammoId;
	}

	/** Délai réel entre deux coups, en ticks. */
	public double fireDelayTicks() {
		return get(Stat.FIRE_DELAY) / Math.max(1.0E-6, get(Stat.FIRE_RATE));
	}

	public ShotStats copy() {
		ShotStats c = new ShotStats();
		this.values.forEach((k, v) -> c.values.put(k, v.copy()));
		c.effects.addAll(this.effects);
		c.trailColors.addAll(this.trailColors);
		c.muzzleTint = this.muzzleTint;
		c.forceAuto = this.forceAuto;
		c.ammoId = this.ammoId;
		return c;
	}
}
