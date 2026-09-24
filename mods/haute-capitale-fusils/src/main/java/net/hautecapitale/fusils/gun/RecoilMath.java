package net.hautecapitale.fusils.gun;

import net.hautecapitale.fusils.data.ShotStats;
import net.hautecapitale.fusils.data.Stat;
import net.minecraft.util.Mth;
import net.minecraft.world.phys.Vec2;

/**
 * Recul : une part permanente (la visée du joueur monte réellement) et une part caméra qui
 * revient seule. Le motif horizontal dépend de l'index du coup dans le chargeur, donc chaque arme
 * a sa « signature » de recul reproductible.
 */
public final class RecoilMath {
	public static final float RETENTION_PER_TICK = 0.75F;
	public static final float PERMANENT_FRACTION = 0.15F;

	private RecoilMath() {}

	public static Vec2 fullRecoil(ShotStats stats, GunProfile.Recoil recoil, int bulletIndex) {
		float strength = (float) stats.get(Stat.RECOIL_MULTIPLIER);
		float pitch = recoil.magnitude();
		float yaw = recoil.magnitude() * recoil.horizontalRatio() * Mth.sin((bulletIndex + recoil.seed()) * recoil.frequency());
		return new Vec2(pitch, yaw).scale(strength);
	}

	public static Vec2 permanentRecoil(ShotStats stats, GunProfile.Recoil recoil, int bulletIndex) {
		return fullRecoil(stats, recoil, bulletIndex).scale(PERMANENT_FRACTION);
	}

	public static Vec2 cameraRecoil(ShotStats stats, GunProfile.Recoil recoil, int bulletIndex) {
		return fullRecoil(stats, recoil, bulletIndex).scale(1.0F - PERMANENT_FRACTION);
	}

	public static float decay(float value, int ticks) {
		return ticks <= 0 ? value : value * (float) Math.pow(RETENTION_PER_TICK, ticks);
	}
}
