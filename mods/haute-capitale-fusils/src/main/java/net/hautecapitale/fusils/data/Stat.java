package net.hautecapitale.fusils.data;

import java.util.Locale;

import com.mojang.serialization.Codec;

import net.minecraft.util.StringRepresentable;

/**
 * Toutes les statistiques numériques d'un tir. Chaque arme, variante, modification et munition
 * n'a qu'à écrire des valeurs pour ces clés (en minuscules dans le JSON) : {@code "damage": 18}.
 */
public enum Stat implements StringRepresentable {
	/** Dégâts totaux d'un coup (en demi-cœurs), répartis entre les plombs. */
	DAMAGE(6.0),
	/** Nombre de projectiles par coup. */
	PROJECTILE_COUNT(1.0),
	/** Dispersion de base en degrés. */
	SPREAD(1.0),
	/** Multiplicateur de dispersion quand le tireur n'est pas au sol. */
	IN_AIR_PENALTY(1.5),
	/** Ticks entre deux coups. */
	FIRE_DELAY(20.0),
	/** Diviseur du délai (1,5 = 50 % plus rapide). */
	FIRE_RATE(1.0),
	/** Vitesse initiale de la balle, blocs par tick. */
	BULLET_SPEED(12.0),
	/** Gravité par tick. */
	GRAVITY(0.05),
	/** Recul infligé à la cible. */
	KNOCKBACK(0.3),
	/** Freinage dans l'air par tick. */
	DRAG(0.98),
	/** Freinage sous l'eau par tick. */
	UNDERWATER_DRAG(0.95),
	/** Multiplicateur de vitesse de rechargement. */
	RELOAD_SPEED(1.0),
	/** Nombre de créatures traversées. */
	PIERCING(0.0),
	/** Nombre de ricochets sur les blocs. */
	RICOCHET(0.0),
	/** Fraction d'armure ignorée (0 à 1). */
	ARMOR_PIERCE(0.0),
	/** Chance de coup critique (0 à 1). */
	CRIT_CHANCE(0.05),
	/** Multiplicateur de dégâts d'un critique. */
	CRIT_MULTIPLIER(1.5),
	/** Multiplicateur du recul de caméra. */
	RECOIL_MULTIPLIER(1.0),
	/** Souffle arrière subi par le tireur. */
	BLOWBACK(0.0),
	/** Portée maximale en blocs avant que la balle ne disparaisse. */
	RANGE(96.0),
	/** Chance de consommer une munition par coup. */
	AMMO_CONSUME_CHANCE(1.0),
	/** Coups supplémentaires dans le chargeur. */
	MAGAZINE_BONUS(0.0),
	/** Bonus de dégâts par coup tiré dans la dernière seconde. */
	ACCELERATING(0.0),
	/** Force de guidage vers la cible (0 à 1). */
	SEEKING(0.0),
	/** Bonus de dégâts contre une cible marquée par le tireur. */
	DAMAGE_VS_MARKED(0.2),
	/** Multiplicateur de dispersion en visée. */
	AIM_SPREAD_MULTIPLIER(0.6);

	public static final Codec<Stat> CODEC = StringRepresentable.fromEnum(Stat::values);

	private final double defaultValue;
	private final String name = name().toLowerCase(Locale.ROOT);

	Stat(double defaultValue) {
		this.defaultValue = defaultValue;
	}

	public double defaultValue() {
		return this.defaultValue;
	}

	@Override
	public String getSerializedName() {
		return this.name;
	}

	public String translationKey() {
		return "stat.haute_capitale_fusils." + this.name;
	}
}
