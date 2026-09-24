package net.hautecapitale.metiers.hunt;

import net.fabricmc.fabric.api.attachment.v1.AttachmentRegistry;
import net.fabricmc.fabric.api.attachment.v1.AttachmentType;
import net.hautecapitale.metiers.HauteCapitaleMetiers;
import net.hautecapitale.metiers.creature.CreatureEnums.SpawnOrigin;
import net.minecraft.entity.Entity;

/**
 * L'origine d'une créature, attachée à l'entité elle-même.
 *
 * <p>Persistante : elle est écrite avec l'entité, donc elle survit au
 * déchargement du chunk et au redémarrage du serveur. C'est la condition de
 * l'anti-farm — sans ça, un élevage redeviendrait rentable après un
 * {@code /reload}. Jamais envoyée au client : l'origine ne regarde que le
 * serveur.
 */
public final class OriginAttachment {

    public static final AttachmentType<SpawnOrigin> ORIGIN =
            AttachmentRegistry.create(HauteCapitaleMetiers.id("origine"), builder -> builder
                    .persistent(SpawnOrigin.CODEC));

    private OriginAttachment() {
    }

    public static void init() {
        // La constante ci-dessus s'enregistre en se chargeant.
    }

    public static boolean isMarked(Entity entity) {
        return entity.hasAttached(ORIGIN);
    }

    /** L'origine marquée, ou {@code inconnue} si rien n'a jamais été écrit. */
    public static SpawnOrigin of(Entity entity) {
        SpawnOrigin origin = entity.getAttached(ORIGIN);
        return origin == null ? SpawnOrigin.INCONNUE : origin;
    }

    public static void mark(Entity entity, SpawnOrigin origin) {
        entity.setAttached(ORIGIN, origin);
    }
}
