package net.hautecapitale.rpg.rpgclass;

import net.fabricmc.fabric.api.attachment.v1.AttachmentRegistry;
import net.fabricmc.fabric.api.attachment.v1.AttachmentSyncPredicate;
import net.fabricmc.fabric.api.attachment.v1.AttachmentType;
import net.hautecapitale.rpg.HauteCapitaleRpg;
import net.minecraft.network.codec.PacketCodecs;

/**
 * Stockage de la classe du joueur.
 *
 * <p>L'API d'attachement de Fabric couvre les trois besoins sans aucun mixin :
 * persistance entre sessions, conservation à la mort, et synchronisation vers le
 * seul client concerné. Zéro mixin, c'est zéro conflit avec les vingt-sept mods
 * déjà en place — la garantie principale de « ne rien casser » à cette étape.
 */
public final class ClassAttachments {

    public static final AttachmentType<PlayerClassState> CLASSE =
            AttachmentRegistry.create(HauteCapitaleRpg.id("classe"), builder -> builder
                    .initializer(PlayerClassState::empty)
                    .persistent(PlayerClassState.CODEC)
                    .copyOnDeath()
                    .syncWith(PacketCodecs.registryCodec(PlayerClassState.CODEC),
                            AttachmentSyncPredicate.targetOnly()));

    private ClassAttachments() {
    }

    /** Force le chargement de la classe, donc l'enregistrement de l'attachement. */
    public static void init() {
    }
}
