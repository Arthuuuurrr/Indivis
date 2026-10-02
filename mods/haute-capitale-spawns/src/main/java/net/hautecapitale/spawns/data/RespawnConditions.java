package net.hautecapitale.spawns.data;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

/**
 * Conditions supplementaires avant une reapparition.
 *
 * @param onlyIfPlayerNearby si vrai, exige qu'un joueur soit dans le rayon d'activation du point
 *                           (c'est deja la condition pour que le point soit traite ; garde pour la
 *                           lisibilite des fichiers de donnees)
 * @param minPlayerDistance aucun joueur ne doit se trouver a moins de cette distance du point ;
 *                          0 = aucune contrainte. Evite qu'un orc apparaisse dans le visage d'un
 *                          joueur qui campe sur son point.
 */
public record RespawnConditions(boolean onlyIfPlayerNearby, int minPlayerDistance) {

    public static final RespawnConditions DEFAULT = new RespawnConditions(true, 6);

    public static final Codec<RespawnConditions> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            Codec.BOOL.optionalFieldOf("only_if_player_nearby", true).forGetter(RespawnConditions::onlyIfPlayerNearby),
            Codec.INT.optionalFieldOf("min_player_distance", 6).forGetter(RespawnConditions::minPlayerDistance)
    ).apply(instance, RespawnConditions::new));

    public RespawnConditions withMinPlayerDistance(int v) {
        return new RespawnConditions(this.onlyIfPlayerNearby, Math.max(0, v));
    }

    public RespawnConditions withOnlyIfPlayerNearby(boolean v) {
        return new RespawnConditions(v, this.minPlayerDistance);
    }
}
