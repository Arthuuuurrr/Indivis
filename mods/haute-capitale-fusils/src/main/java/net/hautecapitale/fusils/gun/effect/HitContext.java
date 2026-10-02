package net.hautecapitale.fusils.gun.effect;

import net.hautecapitale.fusils.data.ShotStats;
import net.hautecapitale.fusils.entity.BulletEntity;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.phys.EntityHitResult;

/** Ce qu'un effet sait au moment où une balle touche une créature. */
public record HitContext(ServerLevel level, BulletEntity bullet, ShotStats stats, EntityHitResult hit,
		Entity target, LivingEntity shooter, boolean marked, boolean critical) {
	public LivingEntity livingTarget() {
		return this.target instanceof LivingEntity living ? living : null;
	}
}
