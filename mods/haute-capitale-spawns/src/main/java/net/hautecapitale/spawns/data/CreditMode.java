package net.hautecapitale.spawns.data;

import com.mojang.serialization.Codec;
import net.minecraft.util.StringIdentifiable;

/**
 * Qui recoit le credit d'un kill (compteurs de scoreboard, objets de quete personnels).
 *
 * <p>La liste des joueurs credites est aussi transmise dans l'evenement de mort :
 * un systeme de quete externe reste libre d'appliquer sa propre regle.
 */
public enum CreditMode implements StringIdentifiable {
    /** Le joueur qui a porte le dernier coup, seulement. */
    KILLER("killer"),
    /** Le tueur et tous les joueurs qui ont blesse le mob recemment. */
    PARTICIPANTS("participants"),
    /**
     * Le tueur, ses participants, et tous les membres en ligne de leur groupe
     * qui sont assez proches du kill. Sans mod de groupe, equivaut a PARTICIPANTS.
     */
    PARTY_NEARBY("party_nearby");

    public static final Codec<CreditMode> CODEC = StringIdentifiable.createCodec(CreditMode::values);

    private final String id;

    CreditMode(String id) {
        this.id = id;
    }

    @Override
    public String asString() {
        return this.id;
    }

    public static CreditMode byId(String id) {
        for (CreditMode mode : values()) {
            if (mode.id.equals(id)) {
                return mode;
            }
        }
        return null;
    }
}
