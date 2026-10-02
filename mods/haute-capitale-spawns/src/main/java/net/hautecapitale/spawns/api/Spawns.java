package net.hautecapitale.spawns.api;

import net.hautecapitale.spawns.data.PointStatus;
import net.hautecapitale.spawns.engine.SpawnEngine;
import net.hautecapitale.spawns.engine.Spawner;
import net.hautecapitale.spawns.state.PointState;
import net.minecraft.entity.Entity;

import java.util.List;
import java.util.Optional;
import java.util.Set;

/**
 * Facade publique du gestionnaire de spawns.
 *
 * <p>Point d'entree unique et stable pour les autres modules (quetes, groupe,
 * Easy NPC, HUD). Tout ce qui est faisable par commande l'est aussi par ici,
 * sans passer par une commande. Les evenements sont dans {@link SpawnEvents}.
 *
 * <p>Sans serveur demarre, toutes les operations echouent proprement.
 */
public final class Spawns {

    /** Etiquette de commande portee par tout mob controle. */
    public static final String TAG = Spawner.TAG_ROOT;
    /** Prefixe des etiquettes {@code hcspawn.zone.<zone>}. */
    public static final String TAG_ZONE_PREFIX = Spawner.TAG_ROOT + ".zone.";
    /** Prefixe des etiquettes {@code hcspawn.point.<zone>.<point>}. */
    public static final String TAG_POINT_PREFIX = Spawner.TAG_ROOT + ".point.";
    /** Prefixe des etiquettes {@code hcspawn.rank.<rang>}. */
    public static final String TAG_RANK_PREFIX = Spawner.TAG_ROOT + ".rank.";
    /** Prefixe des etiquettes {@code hcspawn.tag.<cle>.<valeur>}. */
    public static final String TAG_TAG_PREFIX = Spawner.TAG_ROOT + ".tag.";

    private Spawns() {
    }

    private static OpResult offline() {
        return OpResult.fail("gestionnaire de spawns non demarre");
    }

    /** L'identite du mob controle derriere une entite ; vide pour tout autre mob. */
    public static Optional<ControlledMob> of(Entity entity) {
        return SpawnEngine.get().flatMap(engine -> engine.mobOf(entity));
    }

    public static boolean isControlled(Entity entity) {
        return of(entity).isPresent();
    }

    /**
     * Position d'un point de spawn, pour les waypoints de quete (journal de quetes) : la position du
     * point lui-meme, jamais celle du mob.
     *
     * @param dimension monde de la zone
     * @param leash     rayon de laisse resolu (0 si le point est invalide)
     */
    public record PointPosition(String fullId, net.minecraft.util.Identifier dimension, double x, double y, double z, int leash) {
    }

    /** Position d'un point {@code zone.point} ; vide si le point n'existe pas ou si le moteur est arrete. */
    public static Optional<PointPosition> pointPosition(String fullId) {
        return SpawnEngine.get().flatMap(engine -> engine.registry().ref(fullId)).map(Spawns::position);
    }

    /** Positions de tous les points d'une zone (liste vide si la zone n'existe pas). */
    public static List<PointPosition> zonePoints(String zoneId) {
        return SpawnEngine.get().map(engine -> engine.registry().refsOfZone(zoneId).stream().map(Spawns::position).toList())
                .orElse(List.of());
    }

    private static PointPosition position(net.hautecapitale.spawns.store.PointRef ref) {
        int leash = ref.resolved().map(r -> r.leash().radius()).orElse(0);
        return new PointPosition(ref.fullId(), ref.zone().dimension(), ref.point().x(), ref.point().y(), ref.point().z(), leash);
    }

    public static Optional<PointStatus> statusOf(String fullId) {
        return SpawnEngine.get().flatMap(engine -> engine.store().get(fullId)).map(PointState::status);
    }

    /** Secondes avant la reapparition ; 0 si sans objet. */
    public static long respawnInSeconds(String fullId) {
        return SpawnEngine.get().flatMap(engine -> engine.store().get(fullId))
                .map(state -> state.remainingSeconds(System.currentTimeMillis())).orElse(0L);
    }

    public static boolean pointExists(String fullId) {
        return SpawnEngine.get().map(engine -> engine.registry().ref(fullId).isPresent()).orElse(false);
    }

    public static boolean zoneExists(String zoneId) {
        return SpawnEngine.get().map(engine -> engine.registry().zone(zoneId).isPresent()).orElse(false);
    }

    public static Set<String> zoneIds() {
        return SpawnEngine.get().map(engine -> engine.registry().zoneIds()).orElse(Set.of());
    }

    public static List<String> pointIds(String zoneId) {
        return SpawnEngine.get().map(engine -> engine.registry().refsOfZone(zoneId).stream()
                .map(ref -> ref.fullId()).toList()).orElse(List.of());
    }

    public static boolean isZoneEnabled(String zoneId) {
        return SpawnEngine.get().flatMap(engine -> engine.registry().zone(zoneId)).map(z -> z.enabled()).orElse(false);
    }

    /** Vrai si tous les points actifs de la zone sont morts en ce moment. */
    public static boolean isZoneCleared(String zoneId) {
        return SpawnEngine.get().map(engine -> engine.isZoneCleared(zoneId)).orElse(false);
    }

    public static OpResult enablePoint(String fullId) {
        return SpawnEngine.get().map(engine -> engine.setPointEnabled(fullId, true)).orElse(offline());
    }

    public static OpResult disablePoint(String fullId) {
        return SpawnEngine.get().map(engine -> engine.setPointEnabled(fullId, false)).orElse(offline());
    }

    public static OpResult resetPoint(String fullId) {
        return SpawnEngine.get().map(engine -> engine.resetPoint(fullId)).orElse(offline());
    }

    /** Apparition immediate si le chunk est actif, meme si le timer court encore. */
    public static OpResult spawnNow(String fullId) {
        return SpawnEngine.get().map(engine -> engine.forceSpawn(fullId)).orElse(offline());
    }

    public static OpResult enableZone(String zoneId) {
        return SpawnEngine.get().map(engine -> engine.setZoneEnabled(zoneId, true)).orElse(offline());
    }

    public static OpResult disableZone(String zoneId) {
        return SpawnEngine.get().map(engine -> engine.setZoneEnabled(zoneId, false)).orElse(offline());
    }

    public static OpResult resetZone(String zoneId) {
        return SpawnEngine.get().map(engine -> engine.resetZone(zoneId)).orElse(offline());
    }
}
