package net.hautecapitale.metiers.meal;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.fabricmc.fabric.api.attachment.v1.AttachmentRegistry;
import net.fabricmc.fabric.api.attachment.v1.AttachmentType;
import net.hautecapitale.metiers.HauteCapitaleMetiers;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.Identifier;

/**
 * Le repas en cours d'un joueur : quel buff, à quelle valeur, jusqu'à quand.
 *
 * <p>Sauvegardé avec le joueur — un buff de vingt minutes survit à une
 * déconnexion — mais <em>pas</em> gardé à la mort : mourir retire le repas,
 * comme demandé.
 *
 * @param buff      la fiche de buff
 * @param value     la valeur appliquée (Excellent déjà compté)
 * @param expiresAt échéance, en millisecondes d'horloge
 */
public record MealAttachment(Identifier buff, double value, long expiresAt) {

    public static final Codec<MealAttachment> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            Identifier.CODEC.fieldOf("buff").forGetter(MealAttachment::buff),
            Codec.DOUBLE.fieldOf("valeur").forGetter(MealAttachment::value),
            Codec.LONG.fieldOf("echeance").forGetter(MealAttachment::expiresAt)
    ).apply(instance, MealAttachment::new));

    public static final AttachmentType<MealAttachment> REPAS =
            AttachmentRegistry.create(HauteCapitaleMetiers.id("repas"), builder -> builder.persistent(CODEC));

    public static void init() {
        // La constante ci-dessus s'enregistre en se chargeant.
    }

    public static MealAttachment of(ServerPlayerEntity player) {
        return player.getAttached(REPAS);
    }

    public boolean expired(long now) {
        return now >= expiresAt;
    }

    /** Ce qu'il reste, en secondes, jamais négatif. */
    public int remainingSeconds(long now) {
        return (int) Math.max(0L, (expiresAt - now) / 1000L);
    }
}
