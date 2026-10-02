package net.hautecapitale.spawns.api;

import net.fabricmc.fabric.api.event.Event;
import net.fabricmc.fabric.api.event.EventFactory;
import net.hautecapitale.spawns.data.PointStatus;
import net.minecraft.entity.LivingEntity;
import net.minecraft.server.MinecraftServer;

/**
 * Les evenements du gestionnaire, a ecouter depuis n'importe quel autre mod.
 *
 * <pre>{@code
 * SpawnEvents.MOB_KILLED.register((server, report, entity) -> {
 *     if (report.mob().zoneId().equals("orc_camp_01") && report.mob().hasTag("role", "archer")) {
 *         for (UUID player : report.credited()) { ... progression de quete ... }
 *     }
 * });
 * }</pre>
 *
 * <p>Tous sont emis sur le fil serveur, apres que l'etat du point a ete mis a jour.
 */
public final class SpawnEvents {

    private SpawnEvents() {
    }

    /** Un mob controle vient d'apparaitre (premiere fois ou reapparition). */
    public static final Event<MobSpawned> MOB_SPAWNED = EventFactory.createArrayBacked(MobSpawned.class,
            listeners -> (server, mob, entity, respawn) -> {
                for (MobSpawned listener : listeners) {
                    listener.onSpawned(server, mob, entity, respawn);
                }
            });

    /** Un mob controle est mort. L'entite est encore accessible mais deja morte. */
    public static final Event<MobKilled> MOB_KILLED = EventFactory.createArrayBacked(MobKilled.class,
            listeners -> (server, report, entity) -> {
                for (MobKilled listener : listeners) {
                    listener.onKilled(server, report, entity);
                }
            });

    /**
     * Plus aucun mob vivant dans la zone. Emis une fois par « cycle » : il faudra
     * qu'un mob reapparaisse avant qu'il puisse etre emis a nouveau. Ne change rien
     * aux reapparitions : la quete decide seule quoi en faire.
     */
    public static final Event<ZoneCleared> ZONE_CLEARED = EventFactory.createArrayBacked(ZoneCleared.class,
            listeners -> (server, zoneId, lastKill) -> {
                for (ZoneCleared listener : listeners) {
                    listener.onCleared(server, zoneId, lastKill);
                }
            });

    /** Une zone a ete activee ou desactivee (commande, API, action Easy NPC). */
    public static final Event<ZoneToggled> ZONE_TOGGLED = EventFactory.createArrayBacked(ZoneToggled.class,
            listeners -> (server, zoneId, enabled) -> {
                for (ZoneToggled listener : listeners) {
                    listener.onToggled(server, zoneId, enabled);
                }
            });

    /** L'etat d'un point a change (ALIVE, DEAD_WAITING, READY, DISABLED). */
    public static final Event<PointStatusChanged> POINT_STATUS_CHANGED = EventFactory.createArrayBacked(PointStatusChanged.class,
            listeners -> (server, fullId, previous, current) -> {
                for (PointStatusChanged listener : listeners) {
                    listener.onChanged(server, fullId, previous, current);
                }
            });

    @FunctionalInterface
    public interface MobSpawned {
        void onSpawned(MinecraftServer server, ControlledMob mob, LivingEntity entity, boolean respawn);
    }

    @FunctionalInterface
    public interface MobKilled {
        void onKilled(MinecraftServer server, KillReport report, LivingEntity entity);
    }

    @FunctionalInterface
    public interface ZoneCleared {
        void onCleared(MinecraftServer server, String zoneId, KillReport lastKill);
    }

    @FunctionalInterface
    public interface ZoneToggled {
        void onToggled(MinecraftServer server, String zoneId, boolean enabled);
    }

    @FunctionalInterface
    public interface PointStatusChanged {
        void onChanged(MinecraftServer server, String fullId, PointStatus previous, PointStatus current);
    }
}
