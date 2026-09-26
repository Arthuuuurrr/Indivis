package net.hautecapitale.fusils.gun.effect;

import java.util.Set;

import net.hautecapitale.fusils.data.ShotStats;
import net.hautecapitale.fusils.entity.BulletEntity;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;

/** Ce qu'un effet sait à tout impact, bloc ou créature. {@code alreadyHit} évite les doubles dégâts. */
public record ImpactContext(ServerLevel level, BulletEntity bullet, ShotStats stats, HitResult hit, Vec3 position,
		LivingEntity shooter, Set<Integer> alreadyHit) {
	public boolean hitEntity() {
		return this.hit.getType() == HitResult.Type.ENTITY;
	}

	public boolean canHarm(Entity target) {
		return BulletEntity.canHarm(this.shooter, target);
	}
}
