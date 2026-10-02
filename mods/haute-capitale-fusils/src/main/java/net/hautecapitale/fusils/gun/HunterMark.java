package net.hautecapitale.fusils.gun;

import java.util.UUID;

import net.hautecapitale.fusils.registry.FusilsAttachments;
import net.hautecapitale.fusils.registry.FusilsSounds;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.phys.Vec3;

/** Marque du Chasseur : une cible marquée subit un bonus de dégâts des balles du marqueur. */
public record HunterMark(UUID marker, long expiresAt) {
	public static void apply(LivingEntity target, Entity marker, int ticks) {
		long now = target.level().getGameTime();
		target.setAttached(FusilsAttachments.MARK, new HunterMark(marker.getUUID(), now + ticks));
		if (target.level() instanceof ServerLevel level) {
			Vec3 c = target.getBoundingBox().getCenter();
			level.sendParticles(ParticleTypes.CRIT, c.x, c.y + target.getBbHeight() * 0.5, c.z, 12, 0.3, 0.3, 0.3, 0.05);
			level.playSound(null, c.x, c.y, c.z, FusilsSounds.MARQUE, SoundSource.PLAYERS, 0.8F, 1.0F);
		}
	}

	public static boolean isMarkedBy(Entity target, Entity marker) {
		HunterMark m = target.getAttached(FusilsAttachments.MARK);
		if (m == null || marker == null) return false;
		if (target.level().getGameTime() > m.expiresAt) {
			target.removeAttached(FusilsAttachments.MARK);
			return false;
		}
		return m.marker.equals(marker.getUUID());
	}

	public static boolean isMarked(Entity target) {
		HunterMark m = target.getAttached(FusilsAttachments.MARK);
		return m != null && target.level().getGameTime() <= m.expiresAt;
	}
}
