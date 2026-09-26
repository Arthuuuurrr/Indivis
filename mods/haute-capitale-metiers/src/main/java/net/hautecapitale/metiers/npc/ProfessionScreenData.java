package net.hautecapitale.metiers.npc;

import net.hautecapitale.metiers.repair.RepairData;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.codec.PacketCodecs;
import net.minecraft.text.Text;
import net.minecraft.text.TextCodecs;
import net.minecraft.util.Identifier;

import java.util.List;
import java.util.Optional;

/**
 * Tout ce que le client doit savoir pour dessiner l'écran d'un rôle.
 *
 * <p>Le serveur envoie le résultat, jamais les règles : il n'y a ici aucun
 * niveau requis à comparer, aucune matière à vérifier, aucun droit à évaluer.
 * Le client affiche ce qu'on lui donne. Un client modifié ne peut donc rien
 * s'accorder — au pire il s'affiche n'importe quoi à lui-même.
 *
 * <p>Cette structure est aussi ce qui rend les six ateliers identiques : ils ne
 * diffèrent que par leur contenu.
 */
public record ProfessionScreenData(
        Identifier role,
        NpcRole.Interface screen,
        Text title,
        Optional<Text> greeting,
        boolean offersLearning,
        int masteryTarget,
        List<ProfessionEntry> entries,
        Optional<RepairData> repair
) {

    /** Au-delà, la liste est tronquée plutôt que de faire sauter la connexion. */
    public static final int MAX_ENTRIES = 512;

    public static final PacketCodec<RegistryByteBuf, ProfessionScreenData> PACKET_CODEC = PacketCodec.tuple(
            Identifier.PACKET_CODEC, ProfessionScreenData::role,
            NpcRole.Interface.PACKET_CODEC, ProfessionScreenData::screen,
            TextCodecs.REGISTRY_PACKET_CODEC, ProfessionScreenData::title,
            TextCodecs.OPTIONAL_PACKET_CODEC, ProfessionScreenData::greeting,
            PacketCodecs.BOOLEAN, ProfessionScreenData::offersLearning,
            PacketCodecs.INTEGER, ProfessionScreenData::masteryTarget,
            ProfessionEntry.PACKET_CODEC.collect(PacketCodecs.toList(MAX_ENTRIES)),
            ProfessionScreenData::entries,
            PacketCodecs.optional(RepairData.PACKET_CODEC), ProfessionScreenData::repair,
            ProfessionScreenData::new);

    public ProfessionScreenData {
        entries = entries.size() > MAX_ENTRIES
                ? List.copyOf(entries.subList(0, MAX_ENTRIES))
                : List.copyOf(entries);
    }
}
