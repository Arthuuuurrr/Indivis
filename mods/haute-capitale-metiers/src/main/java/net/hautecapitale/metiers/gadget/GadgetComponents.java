package net.hautecapitale.metiers.gadget;

import com.mojang.serialization.Codec;
import net.hautecapitale.metiers.HauteCapitaleMetiers;
import net.minecraft.component.ComponentType;
import net.minecraft.network.codec.PacketCodecs;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.util.Identifier;
import net.minecraft.util.Uuids;
import net.minecraft.util.math.GlobalPos;

import java.util.UUID;

/**
 * Ce que les gadgets retiennent sur eux : un interrupteur, un filtre, une
 * position, un drone, une échéance. Tous synchronisés au client, pour les
 * infobulles.
 */
public final class GadgetComponents {

    /** Gadget allumé ou éteint — aimant, compresseur, broyeur. */
    public static final ComponentType<Boolean> ACTIF = register("actif",
            ComponentType.<Boolean>builder().codec(Codec.BOOL).packetCodec(PacketCodecs.BOOLEAN).build());

    /** L'objet que le broyeur automatique détruit. */
    public static final ComponentType<Identifier> FILTRE = register("filtre",
            ComponentType.<Identifier>builder().codec(Identifier.CODEC).packetCodec(Identifier.PACKET_CODEC).build());

    /** La position enregistrée par la balise de rappel. */
    public static final ComponentType<GlobalPos> POSITION = register("position",
            ComponentType.<GlobalPos>builder().codec(GlobalPos.CODEC).packetCodec(GlobalPos.PACKET_CODEC).build());

    /** Le drone en vol, s'il y en a un. */
    public static final ComponentType<UUID> DRONE = register("drone",
            ComponentType.<UUID>builder().codec(Uuids.INT_STREAM_CODEC).packetCodec(Uuids.PACKET_CODEC).build());

    /** Jusqu'à quand le kit de raffinage travaille, en millisecondes d'époque. */
    public static final ComponentType<Long> ECHEANCE = register("echeance",
            ComponentType.<Long>builder().codec(Codec.LONG).packetCodec(PacketCodecs.VAR_LONG).build());

    private GadgetComponents() {
    }

    private static <T> ComponentType<T> register(String path, ComponentType<T> type) {
        return Registry.register(Registries.DATA_COMPONENT_TYPE, HauteCapitaleMetiers.id(path), type);
    }

    public static void init() {
        // Les constantes ci-dessus s'enregistrent en se chargeant.
    }
}
