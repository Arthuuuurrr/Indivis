package net.hautecapitale.fusils.gun;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.hautecapitale.fusils.config.FusilsConfig;
import net.hautecapitale.fusils.data.Stat;
import net.hautecapitale.fusils.entity.BulletEntity;
import net.hautecapitale.fusils.gun.effect.HitContext;
import net.hautecapitale.fusils.gun.effect.ShotEffect;
import net.hautecapitale.fusils.network.payload.HitmarkerPayload;
import net.hautecapitale.fusils.registry.FusilsDamage;
import net.hautecapitale.fusils.registry.FusilsSounds;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.Vec3;

/**
 * Les plombs d'un même coup de tromblon qui touchent la même créature dans le même tick sont
 * additionnés et appliqués en <b>un seul</b> {@code hurtServer}. Sur un serveur chargé de mods de
 * combat, huit blessures d'un coup déclenchaient huit fois toute la chaîne de crochets (Spell
 * Engine, noyau RPG, statistiques…) : c'était le gel ressenti au tir. Les dégâts totaux sont
 * identiques, les effets ne sont appliqués qu'une fois par cible et par coup.
 */
public final class PelletAccumulator {
	private record Key(UUID shotId, int targetId) {}

	private static final class Entry {
		final BulletEntity bullet;
		final Entity target;
		final EntityHitResult hit;
		float damage;
		int pellets;
		boolean crit;
		boolean marked;

		Entry(BulletEntity bullet, Entity target, EntityHitResult hit) {
			this.bullet = bullet;
			this.target = target;
			this.hit = hit;
		}
	}

	private static final Map<Key, Entry> PENDING = new LinkedHashMap<>();

	private PelletAccumulator() {}

	public static void add(UUID shotId, BulletEntity bullet, Entity target, EntityHitResult hit, float damage, boolean crit, boolean marked) {
		Entry e = PENDING.computeIfAbsent(new Key(shotId, target.getId()), k -> new Entry(bullet, target, hit));
		e.damage += damage;
		e.pellets++;
		e.crit |= crit;
		e.marked |= marked;
	}

	/** Fin de tick du monde : applique chaque blessure groupée. */
	public static void flush(ServerLevel level) {
		if (PENDING.isEmpty()) return;
		List<Entry> entries = new ArrayList<>(PENDING.values());
		PENDING.clear();
		for (Entry e : entries) {
			if (e.target.level() != level || !e.target.isAlive()) continue;
			BulletEntity bullet = e.bullet;
			LivingEntity shooter = bullet.getOwner() instanceof LivingEntity l ? l : null;
			HitContext ctx = new HitContext(level, bullet, bullet.stats(), e.hit, e.target, shooter, e.marked, e.crit);
			float damage = e.damage;
			for (ShotEffect effect : bullet.stats().effects()) {
				damage = effect.modifyDamage(ctx, damage);
			}
			DamageSource source = FusilsDamage.bullet(level, bullet, shooter);
			if (e.target instanceof LivingEntity living) {
				damage += bullet.armorPierceBonus(living, damage, source);
			}
			boolean hurt = e.target.hurtServer(level, source, damage);
			if (!hurt) continue;
			if (e.target instanceof LivingEntity living) {
				float kb = (float) bullet.stats().get(Stat.KNOCKBACK) * (1.0F + 0.15F * Math.min(4, e.pellets - 1));
				if (kb > 0) {
					Vec3 v = bullet.getDeltaMovement();
					if (v.lengthSqr() < 1.0E-4) v = e.hit.getLocation().subtract(shooter != null ? shooter.position() : e.hit.getLocation());
					living.knockback(kb, -v.x, -v.z);
				}
				living.invulnerableTime = 0;
				for (ShotEffect effect : bullet.stats().effects()) {
					effect.onHitEntity(ctx);
				}
			}
			if (shooter instanceof ServerPlayer player && FusilsConfig.INSTANCE.hitmarker_enabled) {
				boolean kill = e.target instanceof LivingEntity l && !l.isAlive();
				ServerPlayNetworking.send(player, new HitmarkerPayload(e.crit, kill));
			}
			Vec3 p = e.hit.getLocation();
			level.playSound(null, p.x, p.y, p.z, FusilsSounds.IMPACT, SoundSource.NEUTRAL, 1.0F, 0.85F + level.getRandom().nextFloat() * 0.2F);
		}
	}

	public static void clear() {
		PENDING.clear();
	}
}
