package net.hautecapitale.metiers.npc;

import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.codec.PacketCodecs;
import net.minecraft.text.Text;
import net.minecraft.text.TextCodecs;
import net.minecraft.util.Identifier;

import java.util.Locale;
import java.util.Optional;

/**
 * Une ligne de la liste, quelle que soit l'interface.
 *
 * <p>C'est volontairement une ligne d'affichage et non une recette : un métier
 * dans un registre, une progression chez un maître et une recette chez un
 * artisan se présentent de la même manière — un libellé, un détail, un niveau
 * requis, et le fait qu'elle soit accessible ou non. Le tri, la recherche et le
 * filtre travaillent là-dessus.
 *
 * <p>Une ligne peut porter une <em>action</em> : l'identifiant d'une recette
 * que le clic déclenche. Le client renvoie cet identifiant, et rien d'autre ;
 * c'est le serveur qui décide ce qu'il en fait.
 *
 * @param label         ce qui est proposé
 * @param detail        la ligne grise en dessous — progression, coût, matières
 * @param requiredLevel niveau de métier nécessaire, 0 si aucun
 * @param available     le joueur peut-il s'en servir tout de suite
 * @param action        la recette à fabriquer, s'il y en a une
 * @param mastery       0 rien, 1 en apprentissage, 2 maîtrisée
 * @param masteryCount  fabrications déjà faites, pour la jauge
 * @param batches       multiplicateurs de fabrication ouverts (1 ; ou 1, 5, 10)
 */
public record ProfessionEntry(
        Text label,
        Text detail,
        int requiredLevel,
        boolean available,
        Optional<Identifier> action,
        int mastery,
        int masteryCount,
        int batches
) {

    /** Une ligne sans action — progression d'un métier, information. */
    public ProfessionEntry(Text label, Text detail, int requiredLevel, boolean available) {
        this(label, detail, requiredLevel, available, Optional.empty(), 0, 0, 1);
    }

    public static final PacketCodec<RegistryByteBuf, ProfessionEntry> PACKET_CODEC = PacketCodec.tuple(
            TextCodecs.REGISTRY_PACKET_CODEC, ProfessionEntry::label,
            TextCodecs.REGISTRY_PACKET_CODEC, ProfessionEntry::detail,
            PacketCodecs.INTEGER, ProfessionEntry::requiredLevel,
            PacketCodecs.BOOLEAN, ProfessionEntry::available,
            PacketCodecs.optional(Identifier.PACKET_CODEC), ProfessionEntry::action,
            PacketCodecs.INTEGER, ProfessionEntry::mastery,
            PacketCodecs.INTEGER, ProfessionEntry::masteryCount,
            PacketCodecs.INTEGER, ProfessionEntry::batches,
            ProfessionEntry::new);

    public boolean isMastered() {
        return mastery >= 2;
    }

    public boolean isLearning() {
        return mastery == 1;
    }

    /** Texte sur lequel porte la recherche. */
    public String searchable() {
        return (label.getString() + " " + detail.getString()).toLowerCase(Locale.ROOT);
    }
}
