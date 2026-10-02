package net.hautecapitale.fusils.gun;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import net.hautecapitale.fusils.config.FusilsConfig;
import net.hautecapitale.fusils.data.ShotStats;
import net.hautecapitale.fusils.data.Stat;
import net.hautecapitale.fusils.data.StatModifier;
import net.hautecapitale.fusils.data.component.ChargedAmmo;
import net.hautecapitale.fusils.data.component.InstalledMods;
import net.hautecapitale.fusils.data.component.StatOverrides;
import net.hautecapitale.fusils.gun.effect.ShotEffect;
import net.hautecapitale.fusils.item.FusilItem;
import net.hautecapitale.fusils.registry.FusilsAttachments;
import net.hautecapitale.fusils.registry.FusilsComponents;
import net.hautecapitale.fusils.skills.HunterSkills;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;

/**
 * Compose la feuille de tir d'un objet, dans l'ordre : profil → variante → surcharges de l'objet
 * → modifications installées → munition chargée → compétence en attente → état du tireur.
 * Même code des deux côtés, donc la prédiction client et le serveur voient les mêmes chiffres.
 */
public final class ShotComposer {
	private ShotComposer() {}

	public static ComposedShot compose(LivingEntity shooter, ItemStack gun) {
		GunProfile profile = FusilItem.profileOf(gun);
		Identifier profileId = gun.getItem() instanceof FusilItem fi ? fi.profileId() : Identifier.parse("minecraft:air");
		GunVariant variant = FusilItem.variantOf(gun);
		if (variant != null) profileId = variant.profile();

		ShotStats stats = new ShotStats();
		profile.stats().forEach(stats::setBase);

		if (variant != null) {
			stats.apply(variant.stats());
			variant.effects().forEach(stats::addEffect);
		}

		StatOverrides overrides = gun.get(FusilsComponents.STATS);
		if (overrides != null) stats.apply(overrides.overrides());

		boolean scope = false;
		List<Identifier> installed = new ArrayList<>();
		List<GunModification.Attachment> attachments = new ArrayList<>();
		InstalledMods mods = gun.getOrDefault(FusilsComponents.MODS, InstalledMods.EMPTY);
		for (Map.Entry<String, Identifier> e : mods.byCategory().entrySet()) {
			GunModification mod = FusilsData.modification(e.getValue());
			if (mod == null) continue;
			installed.add(e.getValue());
			stats.apply(mod.stats());
			mod.effects().forEach(stats::addEffect);
			if (mod.forceAuto()) stats.setForceAuto(true);
			if (mod.scope()) scope = true;
			mod.attachment().ifPresent(attachments::add);
		}

		ChargedAmmo charged = gun.get(FusilsComponents.CHARGED_AMMO);
		if (charged != null && charged.shots() > 0) {
			AmmoType ammo = FusilsData.ammo(charged.ammo());
			if (ammo != null) {
				stats.setAmmoId(charged.ammo().toString());
				stats.addTrailColor(ammo.color());
				ammo.muzzleTint().ifPresent(stats::setMuzzleTint);
				ammo.effects().forEach(stats::addEffect);
			}
		}

		String skill = gun.get(FusilsComponents.SKILL_SHOT);
		if (skill != null) {
			for (ShotEffect effect : HunterSkills.shotEffects(skill)) stats.addEffect(effect);
			if (variant != null) {
				List<ShotEffect> extra = variant.skillEffects().get(skill);
				if (extra != null) extra.forEach(stats::addEffect);
			}
		}

		double global = FusilsConfig.INSTANCE.global_damage_multiplier;
		if (global != 1.0) stats.value(Stat.DAMAGE).add(StatModifier.mul(global - 1.0));

		if (shooter != null) {
			double accel = stats.get(Stat.ACCELERATING);
			if (accel > 0) {
				int recent = recentShots(shooter);
				if (recent > 0) stats.value(Stat.DAMAGE).add(StatModifier.mul(accel * recent));
			}
		}
		return new ComposedShot(profileId, profile, variant, stats, scope, installed, attachments);
	}

	/** Tirs dans la dernière seconde, pour l'accélérateur à volant. */
	public static int recentShots(LivingEntity shooter) {
		long[] shots = shooter.getAttached(FusilsAttachments.RECENT_SHOTS);
		if (shots == null) return 0;
		long now = shooter.level().getGameTime();
		int n = 0;
		for (long t : shots) if (t > 0 && now - t <= 20) n++;
		return n;
	}

	public static void recordShot(LivingEntity shooter) {
		long[] shots = shooter.getAttached(FusilsAttachments.RECENT_SHOTS);
		if (shots == null) shots = new long[12];
		long now = shooter.level().getGameTime();
		int oldest = 0;
		for (int i = 0; i < shots.length; i++) if (shots[i] < shots[oldest]) oldest = i;
		shots[oldest] = now;
		shooter.setAttached(FusilsAttachments.RECENT_SHOTS, shots);
	}

	/** Texte lisible d'une retouche : « +25 % Dégâts », « −1 Dispersion ». */
	public static Component describe(Stat stat, StatModifier m) {
		boolean harmful = stat == Stat.SPREAD || stat == Stat.FIRE_DELAY || stat == Stat.IN_AIR_PENALTY || stat == Stat.RECOIL_MULTIPLIER || stat == Stat.BLOWBACK || stat == Stat.AMMO_CONSUME_CHANCE || stat == Stat.GRAVITY || stat == Stat.AIM_SPREAD_MULTIPLIER;
		boolean positive = m.value() >= 0;
		ChatFormatting color = (positive ^ harmful) ? ChatFormatting.GREEN : ChatFormatting.RED;
		String text = switch (m.op()) {
			case ADD -> (positive ? "+" : "−") + trim(Math.abs(m.value()));
			case MUL -> (positive ? "+" : "−") + Math.round(Math.abs(m.value()) * 100) + " %";
			case SET -> "= " + trim(m.value());
		};
		return Component.literal(text + " ").append(Component.translatable(stat.translationKey())).withStyle(color);
	}

	private static String trim(double v) {
		return v == Math.floor(v) ? String.valueOf((long) v) : String.format(java.util.Locale.ROOT, "%.2f", v);
	}
}
