package net.hautecapitale.metiers.profession;

import net.fabricmc.fabric.api.attachment.v1.AttachmentRegistry;
import net.fabricmc.fabric.api.attachment.v1.AttachmentSyncPredicate;
import net.fabricmc.fabric.api.attachment.v1.AttachmentType;
import net.hautecapitale.metiers.HauteCapitaleMetiers;
import net.minecraft.network.codec.PacketCodecs;

/**
 * Stockage des données de métier sur le joueur.
 *
 * <p>L'API d'attachement de Fabric couvre à elle seule les trois exigences du
 * cahier des charges, sans aucun mixin :
 *
 * <ul>
 *   <li>{@code persistent} — survit à la déconnexion et au redémarrage ;</li>
 *   <li>{@code copyOnDeath} — la mort ne réinitialise rien, et la copie suit
 *       aussi le joueur lors d'un changement de dimension ;</li>
 *   <li>{@code syncWith(..., targetOnly)} — le serveur pousse l'état au client
 *       concerné, et seulement à lui. Le client reçoit, il ne décide jamais.</li>
 * </ul>
 */
public final class ProfessionAttachments {

    public static final AttachmentType<ProfessionsState> PROFESSIONS =
            AttachmentRegistry.create(HauteCapitaleMetiers.id("professions"), builder -> builder
                    .initializer(ProfessionsState::empty)
                    .persistent(ProfessionsState.CODEC)
                    .copyOnDeath()
                    .syncWith(PacketCodecs.registryCodec(ProfessionsState.CODEC),
                            AttachmentSyncPredicate.targetOnly()));

    private ProfessionAttachments() {
    }

    /** Force le chargement de la classe et donc l'enregistrement de l'attachement. */
    public static void init() {
    }
}
