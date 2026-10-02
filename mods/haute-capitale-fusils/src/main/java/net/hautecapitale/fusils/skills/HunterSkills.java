package net.hautecapitale.fusils.skills;

import java.util.List;
import java.util.Map;
import java.util.Optional;

import net.hautecapitale.fusils.FusilsIds;
import net.hautecapitale.fusils.config.FusilsConfig;
import net.hautecapitale.fusils.data.Stat;
import net.hautecapitale.fusils.data.StatModifier;
import net.hautecapitale.fusils.gun.AmmoType;
import net.hautecapitale.fusils.gun.FusilsData;
import net.hautecapitale.fusils.gun.GunplayManager;
import net.hautecapitale.fusils.gun.HunterMark;
import net.hautecapitale.fusils.gun.effect.ShotEffect;
import net.hautecapitale.fusils.gun.effect.effects.StatEffect;
import net.hautecapitale.fusils.item.FusilItem;
import net.hautecapitale.fusils.registry.FusilsSounds;
import net.minecraft.resources.Identifier;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.phys.Vec3;

/**
 * Les compétences de la branche Fusil, toutes appliquées à l'arme réellement équipée.
 * Appelées par {@code /fusil skill}, par {@link net.hautecapitale.fusils.api.FusilAPI}, ou par vos
 * fonctions de datapack / CapSkills / Pufferfish.
 */
public final class HunterSkills {
	public static final String RAFALE = "rafale";
	public static final String TIR_INCAPACITANT = "tir_incapacitant";
	public static final String TIR_PERFORANT = "tir_perforant";
	public static final String TIR_EXPLOSIF = "tir_explosif";
	public static final String REPLI = "repli";
	public static final String MARQUE = "marque";
	public static final List<String> ALL = List.of(RAFALE, TIR_INCAPACITANT, TIR_PERFORANT, TIR_EXPLOSIF, REPLI, MARQUE);

	/** Munition chargée par chaque compétence de tir (une charge, un tir). */
	private static final Map<String, Identifier> SKILL_AMMO = Map.of(
			TIR_INCAPACITANT, FusilsIds.id("choc"),
			TIR_PERFORANT, FusilsIds.id("perforante"),
			TIR_EXPLOSIF, FusilsIds.id("explosive"));

	private HunterSkills() {}

	/** Effets intrinsèques d'un tir de compétence (en plus de la munition et de la variante). */
	public static List<ShotEffect> shotEffects(String skill) {
		return switch (skill) {
			case RAFALE -> List.of(new StatEffect(Stat.SPREAD, StatModifier.add(1.5)));
			default -> List.of();
		};
	}

	/**
	 * Exécute une compétence. {@code param} : nombre de coups pour la rafale, ticks pour la marque.
	 * Retourne un message d'erreur, ou vide si tout s'est bien passé.
	 */
	public static Optional<String> use(LivingEntity user, String skill, int param) {
		if (!(user.level() instanceof ServerLevel level)) return Optional.of("serveur uniquement");
		ItemStack gun = user.getMainHandItem();
		if (!FusilItem.isGun(gun)) return Optional.of("aucun fusil en main");
		FusilsConfig cfg = FusilsConfig.INSTANCE;
		switch (skill) {
			case RAFALE -> {
				int shots = param > 0 ? param : cfg.burst_shots;
				if (FusilItem.isReloading(gun)) return Optional.of("rechargement en cours");
				GunplayManager.FireOptions opts = new GunplayManager.FireOptions(true, cfg.burst_consumes_ammo, cfg.burst_consumes_ammo, RAFALE, 1.0);
				GunplayManager.scheduleBurst(user, shots, cfg.burst_interval_ticks, opts);
				return Optional.empty();
			}
			case TIR_INCAPACITANT, TIR_PERFORANT, TIR_EXPLOSIF -> {
				Identifier ammoId = SKILL_AMMO.get(skill);
				AmmoType ammo = FusilsData.ammo(ammoId);
				if (ammo == null) return Optional.of("munition " + ammoId + " absente des donnees");
				GunplayManager.charge(user, gun, ammoId, Math.max(1, param > 0 ? param : ammo.defaultShots()));
				gun.set(net.hautecapitale.fusils.registry.FusilsComponents.SKILL_SHOT, skill);
				return Optional.empty();
			}
			case REPLI -> {
				Vec3 back = user.getForward().scale(-1).normalize();
				double strength = cfg.retreat_strength;
				user.push(back.x * strength, 0.32, back.z * strength);
				user.hurtMarked = true;
				user.fallDistance = 0;
				level.playSound(null, user.getX(), user.getY(), user.getZ(), FusilsSounds.CUIR, SoundSource.PLAYERS, 1.0F, 0.9F);
				if (cfg.retreat_reload_rounds > 0) {
					GunplayManager.instantReload(user, gun, cfg.retreat_reload_rounds);
				}
				return Optional.empty();
			}
			case MARQUE -> {
				LivingEntity target = lookTarget(user, 48.0);
				if (target == null) return Optional.of("aucune cible dans la ligne de mire");
				HunterMark.apply(target, user, param > 0 ? param : cfg.mark_duration_ticks);
				return Optional.empty();
			}
			default -> {
				return Optional.of("competence inconnue : " + skill);
			}
		}
	}

	/** La créature visée (boîte gonflée de 0,5 bloc) à portée, la plus proche. */
	public static LivingEntity lookTarget(LivingEntity user, double range) {
		Vec3 from = user.getEyePosition();
		Vec3 to = from.add(user.getLookAngle().scale(range));
		LivingEntity best = null;
		double bestD = Double.MAX_VALUE;
		for (LivingEntity e : user.level().getEntitiesOfClass(LivingEntity.class, user.getBoundingBox().expandTowards(user.getLookAngle().scale(range)).inflate(2.0),
				e -> e != user && e.isAlive() && net.hautecapitale.fusils.entity.BulletEntity.canHarm(user, e))) {
			Optional<Vec3> clip = e.getBoundingBox().inflate(0.5).clip(from, to);
			if (clip.isPresent()) {
				double d = from.distanceToSqr(clip.get());
				if (d < bestD) {
					bestD = d;
					best = e;
				}
			}
		}
		return best;
	}
}
