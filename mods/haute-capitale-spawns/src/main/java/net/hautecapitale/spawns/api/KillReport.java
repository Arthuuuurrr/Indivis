package net.hautecapitale.spawns.api;

import net.hautecapitale.spawns.data.QuestDrop;
import net.minecraft.util.math.Vec3d;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Tout ce qu'un systeme de quete, de groupe ou de butin a besoin de savoir sur
 * la mort d'un mob controle.
 *
 * @param mob identite du mob (zone, point, type, rang, tags)
 * @param entityUuid UUID de l'entite morte
 * @param killer joueur ayant porte le dernier coup, s'il y en a un
 * @param killerName son nom, pour les journaux
 * @param participants joueurs ayant blesse le mob dans la fenetre de participation (tueur compris)
 * @param credited joueurs credites selon le mode du point (tueur / participants / groupe proche) ;
 *                 c'est a eux que vont les compteurs et les objets de quete personnels
 * @param position position du kill
 * @param timeMillis horloge murale du kill
 * @param drops objets de quete definis pour ce mob (deja attribues par le gestionnaire si
 *              {@code builtInQuestDrops} est actif, sinon a la charge du module externe)
 */
public record KillReport(ControlledMob mob, UUID entityUuid, Optional<UUID> killer, Optional<String> killerName,
                         List<UUID> participants, List<UUID> credited, Vec3d position, long timeMillis,
                         List<QuestDrop> drops) {

    public boolean killedBy(UUID player) {
        return this.killer.isPresent() && this.killer.get().equals(player);
    }

    public boolean credits(UUID player) {
        return this.credited.contains(player);
    }
}
