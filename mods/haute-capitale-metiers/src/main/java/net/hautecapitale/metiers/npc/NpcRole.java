package net.hautecapitale.metiers.npc;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.netty.buffer.ByteBuf;
import net.hautecapitale.metiers.profession.Profession;
import net.hautecapitale.metiers.util.Vocabulary;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.codec.PacketCodecs;
import net.minecraft.util.StringIdentifiable;

import java.util.Optional;

/**
 * Le rôle qu'un PNJ tient auprès des joueurs.
 *
 * <p>Un rôle n'est pas un métier. Le métier appartient au joueur ; le rôle
 * décrit ce qu'un personnage non-joueur sait en faire. Le forgeron de la Haute
 * Capitale et le forgeron d'un village de bûcherons portent le même rôle
 * {@code forgeron} et ouvrent la même interface, quel que soit leur apparence,
 * leur nom ou leur emplacement.
 *
 * <p>Aucun PNJ n'est créé par le mod. Le joueur pose ses Easy NPC comme
 * d'habitude et leur attache une action ; le rôle est le seul lien entre son
 * personnage et le système de métiers.
 *
 * <p>Le fichier {@code data/haute_capitale_metiers/hcm/roles/forgeron.json}
 * décrit le rôle {@code haute_capitale_metiers:forgeron} — le chemin est la clé,
 * comme partout ailleurs.
 */
public record NpcRole(
        Interface screen,
        Optional<Profession> profession,
        Optional<String> title,
        Optional<String> greeting,
        boolean canTeach,
        boolean repairs,
        boolean hearth,
        Optional<String> comment
) {

    /**
     * Ce que l'interface du rôle propose.
     *
     * <p>Trois formes seulement, parce que les neuf rôles prévus s'y rangent
     * tous. Un quatrième cas se déclarera ici le jour où il existera, pas avant.
     */
    public enum Interface implements StringIdentifiable {
        /** Atelier d'artisan : fabriquer avec le métier du rôle. Les six artisans. */
        ATELIER("atelier"),
        /** Maître de métier : apprendre le métier et suivre sa progression. */
        FORMATION("formation"),
        /** Registre : la vue d'ensemble des métiers du joueur. L'aubergiste. */
        REGISTRE("registre");

        public static final Codec<Interface> CODEC = Vocabulary.of("interface", Interface::values);

        /**
         * Le client reçoit la forme d'interface avec le reste de l'écran. Une
         * valeur qu'il ne connaîtrait pas — mod plus ancien que le serveur —
         * retombe sur l'atelier plutôt que de faire échouer la lecture du paquet.
         */
        public static final PacketCodec<ByteBuf, Interface> PACKET_CODEC =
                PacketCodecs.STRING.xmap(Interface::byIdOrDefault, Interface::asString);

        private final String id;

        Interface(String id) {
            this.id = id;
        }

        public static Interface byIdOrDefault(String id) {
            for (Interface value : values()) {
                if (value.id.equals(id)) {
                    return value;
                }
            }
            return ATELIER;
        }

        @Override
        public String asString() {
            return id;
        }
    }

    public static final Codec<NpcRole> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            Interface.CODEC.optionalFieldOf("interface", Interface.ATELIER).forGetter(NpcRole::screen),
            Profession.CODEC.optionalFieldOf("metier").forGetter(NpcRole::profession),
            Codec.STRING.optionalFieldOf("titre").forGetter(NpcRole::title),
            Codec.STRING.optionalFieldOf("accueil").forGetter(NpcRole::greeting),
            Codec.BOOL.optionalFieldOf("peut_enseigner", true).forGetter(NpcRole::canTeach),
            Codec.BOOL.optionalFieldOf("reparation", false).forGetter(NpcRole::repairs),
            Codec.BOOL.optionalFieldOf("foyer", false).forGetter(NpcRole::hearth),
            Codec.STRING.optionalFieldOf("commentaire").forGetter(NpcRole::comment)
    ).apply(instance, NpcRole::new));

    /** Un atelier et une formation servent forcément un métier ; le registre, non. */
    public boolean needsProfession() {
        return screen != Interface.REGISTRE;
    }
}
