package net.hautecapitale.fusils.gun;

import net.hautecapitale.fusils.data.ShotStats;
import net.hautecapitale.fusils.registry.FusilsAttachments;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.phys.Vec2;

/** Recul accumulé côté serveur : le serveur en tient compte pour la direction réelle du tir. */
public record RecoilState(float pitch, float yaw, long tick) {
	public static final RecoilState NONE = new RecoilState(0.0F, 0.0F, 0L);

	public static RecoilState current(LivingEntity living, long now) {
		RecoilState s = living.getAttachedOrElse(FusilsAttachments.RECOIL, NONE);
		int elapsed = (int) Math.max(0L, now - s.tick());
		return elapsed == 0 ? s : new RecoilState(RecoilMath.decay(s.pitch(), elapsed), RecoilMath.decay(s.yaw(), elapsed), now);
	}

	public static void addImpulse(LivingEntity living, long now, ShotStats stats, GunProfile.Recoil recoil, int bulletIndex) {
		RecoilState decayed = current(living, now);
		Vec2 r = RecoilMath.cameraRecoil(stats, recoil, bulletIndex);
		living.setAttached(FusilsAttachments.RECOIL, new RecoilState(decayed.pitch() + r.x, decayed.yaw() + r.y, now));
	}
}
