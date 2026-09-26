package net.hautecapitale.spawns.engine;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.fabricmc.fabric.api.attachment.v1.AttachmentRegistry;
import net.fabricmc.fabric.api.attachment.v1.AttachmentType;
import net.hautecapitale.spawns.HauteCapitaleSpawns;
import net.minecraft.entity.Entity;
import net.minecraft.util.Uuids;

import java.util.Optional;
import java.util.UUID;

/**
 * La marque d'un mob controle, attachee a l'entite elle-meme.
 *
 * <p>Persistante : ecrite avec l'entite, elle survit au dechargement du chunk et
 * au redemarrage. C'est ce qui permet de retrouver « son » orc apres coup sans
 * jamais en creer un second, et de ne jamais confondre un mob naturel ou un mob
 * de donjon du meme type avec un mob controle : seuls ceux que le gestionnaire
 * a crees portent la marque.
 *
 * @param zone identifiant de la zone
 * @param point identifiant du point dans la zone
 * @param token jeton unique de cette apparition : le point ne reconnait que le jeton qu'il a emis
 */
public record ControlledMarker(String zone, String point, UUID token) {

    public static final Codec<ControlledMarker> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            Codec.STRING.fieldOf("zone").forGetter(ControlledMarker::zone),
            Codec.STRING.fieldOf("point").forGetter(ControlledMarker::point),
            Uuids.INT_STREAM_CODEC.fieldOf("token").forGetter(ControlledMarker::token)
    ).apply(instance, ControlledMarker::new));

    public static final AttachmentType<ControlledMarker> TYPE =
            AttachmentRegistry.create(HauteCapitaleSpawns.id("controlled"), builder -> builder.persistent(CODEC));

    public String fullId() {
        return this.zone + "." + this.point;
    }

    public static void init() {
        // La constante s'enregistre en se chargeant.
    }

    public static Optional<ControlledMarker> of(Entity entity) {
        return entity == null ? Optional.empty() : Optional.ofNullable(entity.getAttached(TYPE));
    }

    public static boolean isControlled(Entity entity) {
        return entity != null && entity.hasAttached(TYPE);
    }

    public static void mark(Entity entity, ControlledMarker marker) {
        entity.setAttached(TYPE, marker);
    }

    public static void unmark(Entity entity) {
        entity.removeAttached(TYPE);
    }
}
