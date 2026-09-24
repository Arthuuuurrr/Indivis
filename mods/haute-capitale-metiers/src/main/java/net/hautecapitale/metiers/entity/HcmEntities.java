package net.hautecapitale.metiers.entity;

import net.hautecapitale.metiers.HauteCapitaleMetiers;
import net.hautecapitale.metiers.gadget.MiningDroneEntity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.SpawnGroup;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;

/**
 * Les deux entités du mod : la carcasse, et le drone minier.
 *
 * <p>Pas un PNJ, pas un mob — une chose posée au sol, sans IA ni
 * déplacement, qui se souvient de la créature qu'elle était pour lui
 * ressembler. Jamais sauvegardée : elle n'existe que pendant sa durée de vie.
 */
public final class HcmEntities {

    public static final RegistryKey<EntityType<?>> CARCASS_KEY =
            RegistryKey.of(RegistryKeys.ENTITY_TYPE, HauteCapitaleMetiers.id("carcasse"));

    public static final EntityType<CarcassEntity> CARCASS = Registry.register(Registries.ENTITY_TYPE, CARCASS_KEY,
            EntityType.Builder.create(CarcassEntity::new, SpawnGroup.MISC)
                    .dimensions(1.0F, 0.5F)
                    .disableSaving()
                    .disableSummon()
                    .maxTrackingRange(10)
                    .trackingTickInterval(20)
                    .build(CARCASS_KEY));

    public static final RegistryKey<EntityType<?>> DRONE_KEY =
            RegistryKey.of(RegistryKeys.ENTITY_TYPE, HauteCapitaleMetiers.id("drone_minier"));

    /** Le drone minier de l'Ingénieur : suit son maître, ramasse. Jamais sauvegardé non plus. */
    public static final EntityType<MiningDroneEntity> DRONE = Registry.register(Registries.ENTITY_TYPE, DRONE_KEY,
            EntityType.Builder.create(MiningDroneEntity::new, SpawnGroup.MISC)
                    .dimensions(0.5F, 0.5F)
                    .disableSaving()
                    .disableSummon()
                    .maxTrackingRange(8)
                    .trackingTickInterval(2)
                    .build(DRONE_KEY));

    private HcmEntities() {
    }

    public static void init() {
        // La constante ci-dessus s'enregistre en se chargeant.
    }
}
