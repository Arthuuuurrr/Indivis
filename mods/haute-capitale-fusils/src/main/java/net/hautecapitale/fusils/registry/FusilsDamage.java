package net.hautecapitale.fusils.registry;

import net.hautecapitale.fusils.FusilsIds;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.damagesource.DamageType;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.Level;

/** Sources de dégâts : balle (projectile, créditée au tireur), explosion de balle, saignement. */
public final class FusilsDamage {
	public static final ResourceKey<DamageType> BULLET = ResourceKey.create(Registries.DAMAGE_TYPE, FusilsIds.id("balle"));
	public static final ResourceKey<DamageType> EXPLOSION = ResourceKey.create(Registries.DAMAGE_TYPE, FusilsIds.id("balle_explosive"));
	public static final ResourceKey<DamageType> BLEED = ResourceKey.create(Registries.DAMAGE_TYPE, FusilsIds.id("saignement"));

	private FusilsDamage() {}

	public static DamageSource bullet(Level level, Entity bullet, Entity shooter) {
		return new DamageSource(level.registryAccess().lookupOrThrow(Registries.DAMAGE_TYPE).getOrThrow(BULLET), bullet, shooter);
	}

	public static DamageSource explosion(Level level, Entity bullet, Entity shooter) {
		return new DamageSource(level.registryAccess().lookupOrThrow(Registries.DAMAGE_TYPE).getOrThrow(EXPLOSION), bullet, shooter);
	}

	public static DamageSource bleed(Level level, Entity shooter) {
		return new DamageSource(level.registryAccess().lookupOrThrow(Registries.DAMAGE_TYPE).getOrThrow(BLEED), shooter, shooter);
	}
}
