package net.hautecapitale.spawns.data;

import com.mojang.serialization.Codec;
import net.minecraft.util.StringIdentifiable;

/** L'etat persistant d'un point de spawn. */
public enum PointStatus implements StringIdentifiable {
    /** Point ou zone desactive : aucun mob, aucun timer. */
    DISABLED("disabled"),
    /** Pret a faire apparaitre son mob des que les conditions le permettent. */
    READY("ready"),
    /** Son mob existe (charge ou non). */
    ALIVE("alive"),
    /** Son mob est mort ; attend la fin du delai de reapparition. */
    DEAD_WAITING("dead_waiting");

    public static final Codec<PointStatus> CODEC = StringIdentifiable.createCodec(PointStatus::values);

    private final String id;

    PointStatus(String id) {
        this.id = id;
    }

    @Override
    public String asString() {
        return this.id;
    }

    public static PointStatus byId(String id) {
        for (PointStatus status : values()) {
            if (status.id.equals(id)) {
                return status;
            }
        }
        return null;
    }
}
