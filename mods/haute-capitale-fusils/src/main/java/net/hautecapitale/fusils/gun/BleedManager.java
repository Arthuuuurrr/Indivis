package net.hautecapitale.fusils.gun;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import net.hautecapitale.fusils.registry.FusilsDamage;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.phys.Vec3;

/** Saignements en cours, tiqués par le serveur. Réappliquer un saignement rafraîchit la durée. */
public final class BleedManager {
	private static final class Bleed {
		final LivingEntity target;
		final LivingEntity source;
		int remaining;
		final int period;
		final float damage;
		int counter;

		Bleed(LivingEntity target, LivingEntity source, int remaining, int period, float damage) {
			this.target = target;
			this.source = source;
			this.remaining = remaining;
			this.period = Math.max(1, period);
			this.damage = damage;
		}
	}

	private static final List<Bleed> ACTIVE = new ArrayList<>();

	private BleedManager() {}

	public static void apply(LivingEntity target, LivingEntity source, int ticks, int period, float damagePerTick) {
		for (Bleed b : ACTIVE) {
			if (b.target == target) {
				b.remaining = Math.max(b.remaining, ticks);
				return;
			}
		}
		ACTIVE.add(new Bleed(target, source, ticks, period, damagePerTick));
	}

	public static boolean isBleeding(LivingEntity target) {
		for (Bleed b : ACTIVE) if (b.target == target) return true;
		return false;
	}

	public static void tick(MinecraftServer server) {
		if (ACTIVE.isEmpty()) return;
		Iterator<Bleed> it = ACTIVE.iterator();
		while (it.hasNext()) {
			Bleed b = it.next();
			if (!b.target.isAlive() || b.target.isRemoved() || b.remaining <= 0) {
				it.remove();
				continue;
			}
			b.remaining--;
			if (++b.counter >= b.period) {
				b.counter = 0;
				if (b.target.level() instanceof ServerLevel level) {
					int inv = b.target.invulnerableTime;
					b.target.invulnerableTime = 0;
					b.target.hurtServer(level, FusilsDamage.bleed(level, b.source), b.damage);
					b.target.invulnerableTime = inv;
					Vec3 c = b.target.getBoundingBox().getCenter();
					level.sendParticles(ParticleTypes.DAMAGE_INDICATOR, c.x, c.y, c.z, 2, 0.2, 0.2, 0.2, 0.0);
				}
			}
		}
	}

	public static void clear() {
		ACTIVE.clear();
	}
}
