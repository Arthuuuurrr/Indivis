package net.hautecapitale.spawns.engine;

import net.hautecapitale.spawns.HauteCapitaleSpawns;
import net.hautecapitale.spawns.config.SpawnsConfig;
import net.hautecapitale.spawns.data.Leash;
import net.hautecapitale.spawns.data.Resolved;
import net.hautecapitale.spawns.store.PointRef;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.brain.MemoryModuleType;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.math.Vec3d;

import java.util.ArrayList;
import java.util.concurrent.ConcurrentHashMap;
import java.util.Map;
import java.util.UUID;

/**
 * La laisse MMO : un mob attire trop loin de son point lache sa cible, rentre,
 * et recupere sa vie.
 *
 * <p>Generique : ne repose que sur {@code setTarget(null)}, l'oubli de la memoire
 * de cible (mobs a cerveau) et la navigation vanilla. L'IA propre au mob reste
 * intacte ; pendant le retour, le gestionnaire se contente de la contredire toutes
 * les 5 ticks, jusqu'a l'arrivee. Passe ce delai, le mob est teleporte : aucun
 * orc ne reste coince derriere une cloture a l'autre bout de la carte.
 */
public final class LeashController {

    private static final class Returning {
        final UUID entity;
        final Leash leash;
        final Vec3d home;
        final long startedTick;
        long lastPathTick;

        Returning(UUID entity, Leash leash, Vec3d home, long startedTick) {
            this.entity = entity;
            this.leash = leash;
            this.home = home;
            this.startedTick = startedTick;
            this.lastPathTick = startedTick;
        }
    }

    private final Map<UUID, Returning> returning = new ConcurrentHashMap<>();
    private int returnsStarted;
    private int teleports;

    public boolean isReturning(UUID entity) {
        return this.returning.containsKey(entity);
    }

    /** Vrai si l'entite rentre et que sa laisse la rend invulnerable pendant ce temps. */
    public boolean shouldIgnoreDamage(LivingEntity entity) {
        Returning r = this.returning.get(entity.getUuid());
        return r != null && r.leash.invulnerableWhileReturning();
    }

    /** Appele par le moteur pour chaque mob controle vivant et charge. */
    public void check(PointRef ref, Resolved resolved, MobEntity mob, long tick) {
        Leash leash = resolved.leash();
        if (!leash.isEnabled() || this.returning.containsKey(mob.getUuid())) {
            return;
        }
        double dx = mob.getX() - ref.point().x();
        double dz = mob.getZ() - ref.point().z();
        double radius = leash.radius();
        if (dx * dx + dz * dz > radius * radius) {
            this.begin(mob, ref, leash, tick);
        }
    }

    private void begin(MobEntity mob, PointRef ref, Leash leash, long tick) {
        Vec3d home = new Vec3d(ref.point().x(), ref.point().y(), ref.point().z());
        this.returning.put(mob.getUuid(), new Returning(mob.getUuid(), leash, home, tick));
        this.returnsStarted++;
        disengage(mob);
        path(mob, home, leash.speed());
        if (SpawnsConfig.get().logEvents) {
            HauteCapitaleSpawns.LOGGER.info("{} : hors laisse ({} blocs), retour au point", ref.fullId(), leash.radius());
        }
    }

    /**
     * Toutes les 5 ticks : ne parcourt que les mobs en train de rentrer.
     *
     * <p>La boucle travaille sur une copie : finish(), disengage(), le téléport de retour ou la navigation
     * peuvent, par les mods d'IA et les événements d'entité, rappeler {@link #forget(UUID)} ou {@link #check}
     * pendant l'itération — ce qui faisait planter le serveur en production (ConcurrentModificationException
     * dans {@code it.remove()}, b5, 2026-09-21). La table elle-même est concurrente pour la même raison.
     */
    public void tick(MinecraftServer server, long tick) {
        if (this.returning.isEmpty()) {
            return;
        }
        for (Returning r : new ArrayList<>(this.returning.values())) {
            Entity entity = server.getOverworld().getEntityAnyDimension(r.entity);
            if (!(entity instanceof MobEntity mob) || !mob.isAlive()) {
                this.returning.remove(r.entity);
                continue;
            }
            double dx = mob.getX() - r.home.x;
            double dz = mob.getZ() - r.home.z;
            double d2 = dx * dx + dz * dz;
            double done = r.leash.returnRadius();
            if (d2 <= done * done) {
                this.returning.remove(r.entity);
                finish(mob, r.leash);
                continue;
            }
            if (tick - r.startedTick > (long) r.leash.timeoutSeconds() * 20L) {
                this.returning.remove(r.entity);
                mob.getNavigation().stop();
                mob.refreshPositionAndAngles(r.home.x, r.home.y, r.home.z, mob.getYaw(), mob.getPitch());
                finish(mob, r.leash);
                this.teleports++;
                continue;
            }
            disengage(mob);
            if (mob.getNavigation().isIdle() || tick - r.lastPathTick >= 20L) {
                path(mob, r.home, r.leash.speed());
                r.lastPathTick = tick;
            }
        }
    }

    private static void disengage(MobEntity mob) {
        mob.setTarget(null);
        try {
            mob.getBrain().forget(MemoryModuleType.ATTACK_TARGET);
            mob.getBrain().forget(MemoryModuleType.ANGRY_AT);
        } catch (Throwable ignored) {
            // Un mob sans cerveau n'a rien a oublier.
        }
    }

    private static void path(MobEntity mob, Vec3d home, double speed) {
        mob.getNavigation().startMovingTo(home.x, home.y, home.z, speed);
    }

    private static void finish(MobEntity mob, Leash leash) {
        mob.setTarget(null);
        if (leash.healOnReturn()) {
            mob.setHealth(mob.getMaxHealth());
        }
    }

    public void forget(UUID entity) {
        this.returning.remove(entity);
    }

    public void clear() {
        this.returning.clear();
    }

    public int size() {
        return this.returning.size();
    }

    public int returnsStarted() {
        return this.returnsStarted;
    }

    public int teleports() {
        return this.teleports;
    }
}
