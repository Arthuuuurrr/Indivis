package net.hautecapitale.metiers.node;

import net.fabricmc.fabric.api.attachment.v1.AttachmentRegistry;
import net.fabricmc.fabric.api.attachment.v1.AttachmentType;
import net.hautecapitale.metiers.HauteCapitaleMetiers;
import net.minecraft.server.world.ServerWorld;

/**
 * Le registre des nodes, attaché à chaque monde.
 *
 * <p>Sauvegardé avec le monde : il survit aux redémarrages et aux plantages, et
 * une dimension a ses propres nodes. Jamais envoyé au client — les nodes ne
 * sont que des blocs, pour lui.
 */
public final class NodeAttachment {

    public static final AttachmentType<NodeStore> NODES =
            AttachmentRegistry.create(HauteCapitaleMetiers.id("nodes"), builder -> builder
                    .initializer(NodeStore::new)
                    .persistent(NodeStore.CODEC));

    private NodeAttachment() {
    }

    public static void init() {
        // La constante ci-dessus s'enregistre en se chargeant.
    }

    public static NodeStore of(ServerWorld world) {
        return world.getAttachedOrCreate(NODES);
    }
}
