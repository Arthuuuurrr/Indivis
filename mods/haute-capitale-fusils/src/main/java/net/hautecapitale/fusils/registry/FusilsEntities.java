package net.hautecapitale.fusils.registry;

import net.hautecapitale.fusils.FusilsIds;
import net.hautecapitale.fusils.entity.BulletEntity;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;

/** La seule entité du mod : la balle. Invisible, jamais sauvegardée, jamais de spawn naturel. */
public final class FusilsEntities {
	public static final ResourceKey<EntityType<?>> BULLET_KEY = ResourceKey.create(Registries.ENTITY_TYPE, FusilsIds.id("balle"));
	public static final EntityType<BulletEntity> BULLET = Registry.register(BuiltInRegistries.ENTITY_TYPE, BULLET_KEY,
			EntityType.Builder.<BulletEntity>of(BulletEntity::new, MobCategory.MISC).sized(0.25F, 0.25F)
					.clientTrackingRange(8).updateInterval(20).noSave().noSummon().build(BULLET_KEY));

	private FusilsEntities() {}

	public static void init() {}
}
