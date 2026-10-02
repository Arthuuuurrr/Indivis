package net.hautecapitale.spawns.engine;

import it.unimi.dsi.fastutil.longs.LongOpenHashSet;
import net.hautecapitale.spawns.HauteCapitaleSpawns;
import net.hautecapitale.spawns.api.ControlledMob;
import net.hautecapitale.spawns.api.KillReport;
import net.hautecapitale.spawns.api.OpResult;
import net.hautecapitale.spawns.api.SpawnEvents;
import net.hautecapitale.spawns.config.SpawnsConfig;
import net.hautecapitale.spawns.data.PointStatus;
import net.hautecapitale.spawns.data.Resolved;
import net.hautecapitale.spawns.data.SpawnPoint;
import net.hautecapitale.spawns.data.SpawnZone;
import net.hautecapitale.spawns.state.PointState;
import net.hautecapitale.spawns.state.SpawnStateStore;
import net.hautecapitale.spawns.store.PointRef;
import net.hautecapitale.spawns.store.SpawnRegistry;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.registry.Registries;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.Identifier;
import net.minecraft.util.TypeFilter;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Random;
import java.util.Set;
import java.util.UUID;

/**
 * Le coeur du gestionnaire : activation par proximite, machine a etats des points,
 * apparitions, reconnaissance des entites, morts, operations d'administration.
 *
 * <p><b>Un point = au plus un mob.</b> Le point ne connait que l'UUID et le jeton
 * de l'entite qu'il a creee ; il ne cherche jamais « un mob du bon type dans le
 * coin », donc ni les mobs naturels ni les mobs de donjon ne peuvent etre
 * confondus avec le sien, et lui-meme ne peut jamais en avoir deux.
 *
 * <p><b>Cout borne par les joueurs, pas par les points.</b> A chaque passe, seuls
 * les chunks autour des joueurs sont regardes (acces direct a l'index spatial) ;
 * un point dont personne n'approche ne coute rien, quel que soit leur nombre.
 * Les timers sont en horloge murale et ne « tournent » pas : ils sont compares
 * a l'instant present quand le point redevient actif.
 */
public final class SpawnEngine {

    private static SpawnEngine current;

    private final MinecraftServer server;
    private final SpawnRegistry registry;
    private final SpawnStateStore store;
    private final LeashController leash = new LeashController();
    private final KillTracker kills = new KillTracker();
    private final Set<String> clearedZones = new HashSet<>();
    private final Random random = new Random();

    private final LongOpenHashSet chunkScratch = new LongOpenHashSet();
    private final List<PointRef> activeScratch = new ArrayList<>();

    // statistiques
    private long lastPassNanos;
    private long maxPassNanos;
    private long totalPassNanos;
    private int passes;
    private int lastActivePoints;
    private int lastScannedChunks;
    private int spawnsTotal;
    private int deathsTotal;
    private int lostTotal;
    private int duplicatesDiscarded;

    private SpawnEngine(MinecraftServer server, SpawnRegistry registry, SpawnStateStore store) {
        this.server = server;
        this.registry = registry;
        this.store = store;
    }

    public static Optional<SpawnEngine> get() {
        return Optional.ofNullable(current);
    }

    public static SpawnEngine start(MinecraftServer server, SpawnRegistry registry, SpawnStateStore store) {
        current = new SpawnEngine(server, registry, store);
        current.syncStatesWithRegistry();
        return current;
    }

    public static void stop() {
        current = null;
    }

    public MinecraftServer server() {
        return this.server;
    }

    public SpawnRegistry registry() {
        return this.registry;
    }

    public SpawnStateStore store() {
        return this.store;
    }

    public LeashController leash() {
        return this.leash;
    }

    public KillTracker kills() {
        return this.kills;
    }

    private SpawnsConfig config() {
        return SpawnsConfig.get();
    }

    // --- boucle -------------------------------------------------------------------------

    public void tick() {
        int tick = this.server.getTicks();
        SpawnsConfig config = this.config();
        if (tick % config.tickInterval == 0) {
            long start = System.nanoTime();
            try {
                this.pass(tick, System.currentTimeMillis(), config);
            } catch (Throwable t) {
                HauteCapitaleSpawns.LOGGER.error("Passe du moteur de spawns interrompue", t);
            }
            long elapsed = System.nanoTime() - start;
            this.lastPassNanos = elapsed;
            this.maxPassNanos = Math.max(this.maxPassNanos, elapsed);
            this.totalPassNanos += elapsed;
            this.passes++;
        }
        if (tick % 5 == 0) {
            this.leash.tick(this.server, tick);
        }
    }

    private void pass(int tick, long now, SpawnsConfig config) {
        int chunkRadius = (this.registry.maxActivationRadius() >> 4) + 1;
        int active = 0;
        int scanned = 0;
        for (ServerWorld world : this.server.getWorlds()) {
            Identifier dimension = world.getRegistryKey().getValue();
            if (!this.registry.hasPointsIn(dimension)) {
                continue;
            }
            List<ServerPlayerEntity> players = world.getPlayers();
            if (players.isEmpty()) {
                continue;
            }
            this.chunkScratch.clear();
            this.activeScratch.clear();
            for (ServerPlayerEntity player : players) {
                if (player.isSpectator()) {
                    continue;
                }
                int pcx = player.getChunkPos().x;
                int pcz = player.getChunkPos().z;
                for (int dx = -chunkRadius; dx <= chunkRadius; dx++) {
                    for (int dz = -chunkRadius; dz <= chunkRadius; dz++) {
                        long key = PointRef.chunkKey(pcx + dx, pcz + dz);
                        if (this.chunkScratch.add(key)) {
                            scanned++;
                            List<PointRef> refs = this.registry.refsInChunk(dimension, key);
                            if (!refs.isEmpty()) {
                                this.activeScratch.addAll(refs);
                            }
                        }
                    }
                }
            }
            for (PointRef ref : this.activeScratch) {
                if (this.process(world, ref, players, now, tick, config)) {
                    active++;
                }
            }
        }
        this.lastActivePoints = active;
        this.lastScannedChunks = scanned;
    }

    /** Vrai si le point etait dans le rayon d'activation d'un joueur. */
    private boolean process(ServerWorld world, PointRef ref, List<ServerPlayerEntity> players, long now, int tick, SpawnsConfig config) {
        PointState state = this.store.getOrCreate(ref.fullId());
        if (!ref.effectivelyEnabled()) {
            if (state.status() != PointStatus.DISABLED) {
                this.disableState(ref, state);
            }
            return false;
        }
        if (!ref.isValid()) {
            return false;
        }
        Resolved resolved = ref.resolved().get();
        double nearestSq = nearestPlayerSq(players, ref.point());
        double radius = resolved.activationRadius();
        if (nearestSq > radius * radius) {
            return false;
        }
        if (state.status() == PointStatus.DISABLED) {
            this.transition(state, PointStatus.READY);
        }
        if (state.status() == PointStatus.DEAD_WAITING) {
            PointStatus previous = state.status();
            if (state.advanceTimer(now)) {
                this.store.changed();
                this.fireStatus(state.fullId(), previous, state.status());
            } else {
                return true;
            }
        }
        if (state.status() == PointStatus.ALIVE) {
            this.checkAlive(world, ref, resolved, state, now, tick, config);
            return true;
        }
        if (state.status() == PointStatus.READY) {
            this.trySpawn(world, ref, resolved, state, now, nearestSq, false);
        }
        return true;
    }

    private static double nearestPlayerSq(List<ServerPlayerEntity> players, SpawnPoint point) {
        double best = Double.MAX_VALUE;
        for (ServerPlayerEntity player : players) {
            if (player.isSpectator()) {
                continue;
            }
            double d = point.squaredDistanceTo(player.getX(), player.getY(), player.getZ());
            if (d < best) {
                best = d;
            }
        }
        return best;
    }

    private void checkAlive(ServerWorld world, PointRef ref, Resolved resolved, PointState state, long now, int tick, SpawnsConfig config) {
        UUID uuid = state.entityUuid().orElse(null);
        if (uuid == null) {
            this.transition(state, PointStatus.READY);
            return;
        }
        Entity entity = world.getEntityAnyDimension(uuid);
        if (entity instanceof LivingEntity living && living.isAlive()) {
            state.seen(living.getBlockPos());
            if (living instanceof MobEntity mob) {
                this.leash.check(ref, resolved, mob, tick);
            }
            return;
        }
        if (entity != null) {
            return; // en train de mourir : l'evenement de mort suit
        }
        BlockPos probe = state.lastKnownPos().orElse(ref.homePos());
        if (!world.shouldTickEntityAt(probe)) {
            return; // chunk inactif : le mob y dort, on ne conclut rien
        }
        int misses = state.unresolved();
        if (misses >= config.lostGraceChecks()) {
            this.lost(ref, state, now, config);
        }
    }

    private void lost(PointRef ref, PointState state, long now, SpawnsConfig config) {
        PointStatus previous = state.status();
        state.markDead(now, config.lostEntityRespawnSeconds, null, false);
        state.problem("entite perdue (disparue sans mourir), reapparition dans " + config.lostEntityRespawnSeconds + " s");
        this.store.changed();
        this.lostTotal++;
        this.leash.forget(state.entityUuid().orElse(null));
        HauteCapitaleSpawns.LOGGER.warn("{} : mob controle introuvable dans un chunk actif ; point repare, reapparition dans {} s",
                ref.fullId(), config.lostEntityRespawnSeconds);
        this.fireStatus(state.fullId(), previous, state.status());
    }

    private OpResult trySpawn(ServerWorld world, PointRef ref, Resolved resolved, PointState state, long now, double nearestSq, boolean force) {
        if (!world.shouldTickEntityAt(ref.homePos())) {
            return OpResult.fail("le chunk du point n'est pas actif");
        }
        if (!force) {
            int minDistance = resolved.conditions().minPlayerDistance();
            if (minDistance > 0 && nearestSq < (double) minDistance * minDistance) {
                return OpResult.fail("un joueur est trop pres du point");
            }
            if (now - state.lastSpawnAttemptMillis() < 2000L) {
                return OpResult.fail("nouvelle tentative dans 2 s");
            }
        }
        state.attemptedSpawn(now);
        UUID token = UUID.randomUUID();
        Spawner.Result result = Spawner.prepare(world, ref, resolved, token, this.config());
        if (result.entity().isEmpty()) {
            state.problem(result.error());
            HauteCapitaleSpawns.LOGGER.warn("{} : apparition impossible : {}", ref.fullId(), result.error());
            return OpResult.fail(result.error());
        }
        LivingEntity entity = result.entity().get();
        boolean respawn = state.spawnCount() > 0;
        PointStatus previous = state.status();
        // L'etat reconnait le jeton AVANT l'ajout au monde : l'evenement de chargement
        // d'entite part pendant l'ajout, et il ne doit pas prendre ce mob pour un doublon.
        state.markAlive(entity.getUuid(), token, entity.getBlockPos());
        Optional<String> placeError = Spawner.place(world, entity);
        if (placeError.isPresent()) {
            state.rollbackSpawn(previous);
            state.problem(placeError.get());
            this.store.changed();
            HauteCapitaleSpawns.LOGGER.warn("{} : apparition impossible : {}", ref.fullId(), placeError.get());
            return OpResult.fail(placeError.get());
        }
        this.store.changed();
        this.clearedZones.remove(ref.zone().id());
        this.spawnsTotal++;
        if (this.config().logEvents) {
            HauteCapitaleSpawns.LOGGER.info("{} : {} {} en {}", ref.fullId(), resolved.entity(), respawn ? "reapparu" : "apparu", entity.getBlockPos().toShortString());
        }
        this.fireStatus(state.fullId(), previous, state.status());
        SpawnEvents.MOB_SPAWNED.invoker().onSpawned(this.server, this.mobOf(ref, resolved, token), entity, respawn);
        return OpResult.ok("apparu");
    }

    // --- evenements d'entites ------------------------------------------------------------

    public void onEntityLoad(Entity entity, ServerWorld world) {
        ControlledMarker marker = entity.getAttached(ControlledMarker.TYPE);
        if (marker == null) {
            return;
        }
        if (!(entity instanceof LivingEntity living)) {
            return;
        }
        Optional<PointRef> refOpt = this.registry.ref(marker.fullId());
        if (refOpt.isEmpty()) {
            if (this.config().discardOrphans) {
                HauteCapitaleSpawns.LOGGER.info("{} : mob orphelin (point supprime) retire", marker.fullId());
                entity.discard();
            } else {
                Spawner.release(entity);
            }
            return;
        }
        PointRef ref = refOpt.get();
        PointState state = this.store.getOrCreate(ref.fullId());
        if (!ref.effectivelyEnabled()) {
            if (this.config().despawnOnDisable) {
                entity.discard();
            }
            if (state.status() != PointStatus.DISABLED) {
                this.transition(state, PointStatus.DISABLED);
            }
            return;
        }
        boolean sameToken = state.token().map(marker.token()::equals).orElse(false);
        if (!sameToken) {
            // Jeton inconnu : une apparition anterieure a un reset, ou un chunk restaure
            // depuis une sauvegarde plus ancienne que l'etat. Dans tous les cas, ce n'est
            // plus le mob du point : le garder ferait un doublon.
            this.duplicatesDiscarded++;
            HauteCapitaleSpawns.LOGGER.info("{} : mob controle perime (jeton {}), retire pour eviter un doublon", ref.fullId(), marker.token());
            entity.discard();
            return;
        }
        PointStatus previous = state.status();
        if (previous != PointStatus.ALIVE || !state.entityUuid().map(entity.getUuid()::equals).orElse(false)) {
            state.adopt(entity.getUuid(), marker.token(), entity.getBlockPos());
            this.store.changed();
            this.clearedZones.remove(ref.zone().id());
            if (previous != PointStatus.ALIVE) {
                this.fireStatus(state.fullId(), previous, state.status());
            }
        } else {
            state.seen(entity.getBlockPos());
        }
        ref.resolved().ifPresent(resolved -> Spawner.reapply(living, ref, resolved, this.config()));
    }

    public void onEntityUnload(Entity entity, ServerWorld world) {
        ControlledMarker marker = entity.getAttached(ControlledMarker.TYPE);
        if (marker == null) {
            return;
        }
        this.leash.forget(entity.getUuid());
        this.store.get(marker.fullId()).ifPresent(state -> {
            if (state.entityUuid().map(entity.getUuid()::equals).orElse(false)) {
                state.seen(entity.getBlockPos());
            }
        });
    }

    public boolean allowDamage(LivingEntity entity, DamageSource source) {
        return !(ControlledMarker.isControlled(entity) && this.leash.shouldIgnoreDamage(entity));
    }

    public void onDamage(LivingEntity entity, DamageSource source) {
        if (ControlledMarker.isControlled(entity)) {
            this.kills.onDamage(entity, source, this.server.getTicks());
        }
    }

    public void onDeath(LivingEntity entity, DamageSource source) {
        ControlledMarker marker = entity.getAttached(ControlledMarker.TYPE);
        if (marker == null) {
            return;
        }
        UUID uuid = entity.getUuid();
        int tick = this.server.getTicks();
        long now = System.currentTimeMillis();
        SpawnsConfig config = this.config();
        Optional<PointRef> refOpt = this.registry.ref(marker.fullId());
        if (refOpt.isEmpty() || refOpt.get().resolved().isEmpty()) {
            this.kills.forget(uuid);
            this.leash.forget(uuid);
            return;
        }
        PointRef ref = refOpt.get();
        Resolved resolved = ref.resolved().get();
        PointState state = this.store.getOrCreate(ref.fullId());
        boolean ours = state.token().map(marker.token()::equals).orElse(false);

        Optional<ServerPlayerEntity> killer = KillTracker.playerFrom(source);
        if (killer.isEmpty()) {
            killer = KillTracker.playerFrom(entity.getPrimeAdversary());
        }
        if (killer.isEmpty()) {
            killer = KillTracker.playerFrom(entity.getAttacker());
        }
        List<UUID> participants = new ArrayList<>(this.kills.participants(uuid, tick, config.participationWindowSeconds * 20));
        killer.ifPresent(k -> {
            if (!participants.contains(k.getUuid())) {
                participants.add(0, k.getUuid());
            }
        });
        Vec3d position = entity.getEntityPos();
        List<UUID> credited = Rewards.credited(this.server, resolved.creditMode(), killer, participants, position, config.creditRadius);
        KillReport report = new KillReport(this.mobOf(ref, resolved, marker.token()), uuid, killer.map(Entity::getUuid),
                killer.map(k -> k.getName().getString()), List.copyOf(participants), credited, position, now, resolved.drops());

        if (ours) {
            PointStatus previous = state.status();
            int delay = resolved.respawn().roll(this.random);
            state.markDead(now, delay, report.killerName().orElse(null), true);
            this.store.changed();
            this.deathsTotal++;
            if (config.logEvents) {
                HauteCapitaleSpawns.LOGGER.info("{} : tue par {} ; reapparition dans {} s", ref.fullId(), report.killerName().orElse("?"), delay);
            }
            this.fireStatus(state.fullId(), previous, state.status());
        }
        this.kills.forget(uuid);
        this.leash.forget(uuid);
        SpawnEvents.MOB_KILLED.invoker().onKilled(this.server, report, entity);
        Rewards.apply(this.server, report, resolved, config);
        if (ours) {
            this.checkZoneCleared(ref.zone().id(), report);
        }
    }

    private void checkZoneCleared(String zoneId, KillReport lastKill) {
        if (this.clearedZones.contains(zoneId) || !this.isZoneCleared(zoneId)) {
            return;
        }
        this.clearedZones.add(zoneId);
        SpawnEvents.ZONE_CLEARED.invoker().onCleared(this.server, zoneId, lastKill);
    }

    /** Vrai si tous les points actifs et valides de la zone sont morts (et au moins un). */
    public boolean isZoneCleared(String zoneId) {
        int dead = 0;
        for (PointRef ref : this.registry.refsOfZone(zoneId)) {
            if (!ref.effectivelyEnabled() || !ref.isValid()) {
                continue;
            }
            PointStatus status = this.store.get(ref.fullId()).map(PointState::status).orElse(PointStatus.READY);
            if (status != PointStatus.DEAD_WAITING) {
                return false;
            }
            dead++;
        }
        return dead > 0;
    }

    // --- identite ------------------------------------------------------------------------

    public ControlledMob mobOf(PointRef ref, Resolved resolved, UUID token) {
        SpawnPoint p = ref.point();
        return new ControlledMob(ref.zone().id(), ref.point().id(), ref.fullId(), resolved.entity(), resolved.rank(),
                resolved.tags(), ref.dimension(), new Vec3d(p.x(), p.y(), p.z()), token, resolved.level());
    }

    public Optional<ControlledMob> mobOf(Entity entity) {
        ControlledMarker marker = entity == null ? null : entity.getAttached(ControlledMarker.TYPE);
        if (marker == null) {
            return Optional.empty();
        }
        return this.registry.ref(marker.fullId())
                .filter(PointRef::isValid)
                .map(ref -> this.mobOf(ref, ref.resolved().get(), marker.token()));
    }

    public Optional<LivingEntity> loadedEntity(PointState state) {
        return state.entityUuid()
                .map(this.server.getOverworld()::getEntityAnyDimension)
                .filter(e -> e instanceof LivingEntity && e.isAlive())
                .map(e -> (LivingEntity) e);
    }

    public PointState state(String fullId) {
        return this.store.getOrCreate(fullId);
    }

    // --- operations d'administration -------------------------------------------------------

    private void discardEntityOf(PointState state) {
        state.entityUuid().ifPresent(uuid -> {
            Entity entity = this.server.getOverworld().getEntityAnyDimension(uuid);
            if (entity != null) {
                entity.discard();
            }
            this.leash.forget(uuid);
            this.kills.forget(uuid);
        });
    }

    private void disableState(PointRef ref, PointState state) {
        if (this.config().despawnOnDisable) {
            this.discardEntityOf(state);
        }
        this.transition(state, PointStatus.DISABLED);
    }

    private void transition(PointState state, PointStatus target) {
        PointStatus previous = state.status();
        switch (target) {
            case DISABLED -> state.markDisabled();
            case READY -> state.markReady();
            default -> throw new IllegalArgumentException("transition directe interdite vers " + target);
        }
        this.store.changed();
        if (previous != target) {
            this.fireStatus(state.fullId(), previous, target);
        }
    }

    private void fireStatus(String fullId, PointStatus previous, PointStatus current) {
        SpawnEvents.POINT_STATUS_CHANGED.invoker().onChanged(this.server, fullId, previous, current);
    }

    public OpResult setPointEnabled(String fullId, boolean enabled) {
        Optional<PointRef> refOpt = this.registry.ref(fullId);
        if (refOpt.isEmpty()) {
            return OpResult.fail("point inconnu : " + fullId);
        }
        PointRef ref = refOpt.get();
        if (ref.point().enabled() != enabled) {
            this.registry.putZone(ref.zone().withPoint(ref.point().withEnabled(enabled)));
        }
        PointRef updated = this.registry.ref(fullId).orElse(ref);
        PointState state = this.store.getOrCreate(fullId);
        if (!updated.effectivelyEnabled()) {
            this.disableState(updated, state);
            return OpResult.ok(fullId + " desactive" + (updated.zone().enabled() ? "" : " (la zone l'est aussi)"));
        }
        if (state.status() == PointStatus.DISABLED) {
            this.transition(state, PointStatus.READY);
        }
        return OpResult.ok(fullId + " active");
    }

    public OpResult resetPoint(String fullId) {
        Optional<PointRef> refOpt = this.registry.ref(fullId);
        if (refOpt.isEmpty()) {
            return OpResult.fail("point inconnu : " + fullId);
        }
        PointRef ref = refOpt.get();
        PointState state = this.store.getOrCreate(fullId);
        this.discardEntityOf(state);
        this.transition(state, ref.effectivelyEnabled() ? PointStatus.READY : PointStatus.DISABLED);
        this.clearedZones.remove(ref.zone().id());
        return OpResult.ok(fullId + " reinitialise (" + state.status().asString() + ")");
    }

    public OpResult setZoneEnabled(String zoneId, boolean enabled) {
        Optional<SpawnZone> zoneOpt = this.registry.zone(zoneId);
        if (zoneOpt.isEmpty()) {
            return OpResult.fail("zone inconnue : " + zoneId);
        }
        if (zoneOpt.get().enabled() != enabled) {
            this.registry.putZone(zoneOpt.get().withEnabled(enabled));
        }
        int touched = 0;
        for (PointRef ref : this.registry.refsOfZone(zoneId)) {
            PointState state = this.store.getOrCreate(ref.fullId());
            if (!ref.effectivelyEnabled()) {
                if (state.status() != PointStatus.DISABLED) {
                    this.disableState(ref, state);
                    touched++;
                }
            } else if (state.status() == PointStatus.DISABLED) {
                this.transition(state, PointStatus.READY);
                touched++;
            }
        }
        this.clearedZones.remove(zoneId);
        SpawnEvents.ZONE_TOGGLED.invoker().onToggled(this.server, zoneId, enabled);
        return OpResult.ok("zone " + zoneId + (enabled ? " activee" : " desactivee") + " (" + touched + " point(s) changes)");
    }

    public OpResult resetZone(String zoneId) {
        if (this.registry.zone(zoneId).isEmpty()) {
            return OpResult.fail("zone inconnue : " + zoneId);
        }
        int count = 0;
        for (PointRef ref : this.registry.refsOfZone(zoneId)) {
            this.resetPoint(ref.fullId());
            count++;
        }
        return OpResult.ok("zone " + zoneId + " reinitialisee (" + count + " point(s))");
    }

    public OpResult deletePoint(String fullId) {
        Optional<PointRef> refOpt = this.registry.ref(fullId);
        if (refOpt.isEmpty()) {
            return OpResult.fail("point inconnu : " + fullId);
        }
        PointRef ref = refOpt.get();
        this.store.get(fullId).ifPresent(this::discardEntityOf);
        this.registry.putZone(ref.zone().withoutPoint(ref.point().id()));
        this.store.remove(fullId);
        return OpResult.ok(fullId + " supprime");
    }

    public OpResult deleteZone(String zoneId) {
        if (this.registry.zone(zoneId).isEmpty()) {
            return OpResult.fail("zone inconnue : " + zoneId);
        }
        int count = 0;
        for (PointRef ref : this.registry.refsOfZone(zoneId)) {
            this.store.get(ref.fullId()).ifPresent(this::discardEntityOf);
            this.store.remove(ref.fullId());
            count++;
        }
        this.registry.removeZone(zoneId);
        this.clearedZones.remove(zoneId);
        return OpResult.ok("zone " + zoneId + " supprimee (" + count + " point(s))");
    }

    /** Apparition immediate, en ignorant timer et distance minimale ; le chunk doit etre actif. */
    public OpResult forceSpawn(String fullId) {
        Optional<PointRef> refOpt = this.registry.ref(fullId);
        if (refOpt.isEmpty()) {
            return OpResult.fail("point inconnu : " + fullId);
        }
        PointRef ref = refOpt.get();
        if (!ref.isValid()) {
            return OpResult.fail(fullId + " invalide : " + String.join(" ; ", ref.problems()));
        }
        if (!ref.effectivelyEnabled()) {
            return OpResult.fail(fullId + " est desactive");
        }
        ServerWorld world = this.server.getWorld(ref.dimension());
        if (world == null) {
            return OpResult.fail("dimension " + ref.zone().dimension() + " non chargee");
        }
        PointState state = this.store.getOrCreate(fullId);
        if (state.status() == PointStatus.ALIVE && this.loadedEntity(state).isPresent()) {
            return OpResult.fail(fullId + " a deja son mob vivant");
        }
        this.discardEntityOf(state);
        state.markReady();
        return this.trySpawn(world, ref, ref.resolved().get(), state, System.currentTimeMillis(), Double.MAX_VALUE, true);
    }

    /** Apres un deplacement ou une modification : re-applique la zone de marche au mob charge. */
    public void onPointEdited(String fullId) {
        this.registry.ref(fullId).ifPresent(ref -> this.store.get(fullId).ifPresent(state ->
                this.loadedEntity(state).ifPresent(entity -> {
                    ref.resolved().ifPresent(resolved -> Spawner.reapply(entity, ref, resolved, this.config()));
                    if (!ref.effectivelyEnabled()) {
                        this.disableState(ref, state);
                    }
                })));
    }

    /** Apres {@code /mmospawn reload} : realigne les etats sur les fichiers relus. */
    public void afterReload() {
        this.clearedZones.clear();
        this.syncStatesWithRegistry();
    }

    /** Au demarrage : etats sans point (supprimes hors ligne) et points sans etat. */
    private void syncStatesWithRegistry() {
        List<String> stale = new ArrayList<>();
        for (PointState state : this.store.all()) {
            if (this.registry.ref(state.fullId()).isEmpty()) {
                stale.add(state.fullId());
            }
        }
        for (String id : stale) {
            this.store.remove(id);
        }
        if (!stale.isEmpty()) {
            HauteCapitaleSpawns.LOGGER.info("{} etat(s) de points disparus purges", stale.size());
        }
        for (PointRef ref : this.registry.refs()) {
            PointState state = this.store.getOrCreate(ref.fullId());
            if (!ref.effectivelyEnabled() && state.status() != PointStatus.DISABLED) {
                state.markDisabled();
                this.store.changed();
            } else if (ref.effectivelyEnabled() && state.status() == PointStatus.DISABLED) {
                state.markReady();
                this.store.changed();
            }
        }
    }

    // --- integrite ---------------------------------------------------------------------------

    public List<String> validate() {
        List<String> issues = new ArrayList<>();
        for (PointRef ref : this.registry.refs()) {
            for (String problem : ref.problems()) {
                issues.add("[point] " + ref.fullId() + " : " + problem);
            }
            ref.resolved().ifPresent(resolved -> {
                if (!Registries.ENTITY_TYPE.containsId(resolved.entity())) {
                    issues.add("[entite] " + ref.fullId() + " : type inconnu " + resolved.entity());
                }
                if (resolved.leash().isEnabled() && resolved.wanderRadius() > resolved.leash().radius()) {
                    issues.add("[laisse] " + ref.fullId() + " : zone de marche (" + resolved.wanderRadius() + ") plus grande que la laisse (" + resolved.leash().radius() + ")");
                }
                if (!this.config().ranks.contains(resolved.rank())) {
                    issues.add("[rang] " + ref.fullId() + " : rang « " + resolved.rank() + " » hors de la liste de la configuration");
                }
                for (var drop : resolved.drops()) {
                    if (!Registries.ITEM.containsId(drop.item())) {
                        issues.add("[quete] " + ref.fullId() + " : objet inconnu " + drop.item());
                    }
                }
            });
            if (this.server.getWorld(ref.dimension()) == null) {
                issues.add("[dimension] " + ref.fullId() + " : dimension " + ref.zone().dimension() + " absente du serveur");
            }
        }
        // positions identiques (souvent un clone jamais deplace)
        List<PointRef> all = new ArrayList<>(this.registry.refs());
        for (int i = 0; i < all.size(); i++) {
            for (int j = i + 1; j < all.size(); j++) {
                PointRef a = all.get(i);
                PointRef b = all.get(j);
                if (a.zone().dimension().equals(b.zone().dimension())
                        && a.point().squaredDistanceTo(b.point().x(), b.point().y(), b.point().z()) < 0.01D) {
                    issues.add("[position] " + a.fullId() + " et " + b.fullId() + " sont au meme endroit");
                }
            }
        }
        for (PointState state : this.store.all()) {
            Optional<PointRef> ref = this.registry.ref(state.fullId());
            if (ref.isEmpty()) {
                issues.add("[etat] " + state.fullId() + " : etat sans point (a purger par repair)");
                continue;
            }
            if (state.status() == PointStatus.ALIVE) {
                if (state.entityUuid().isEmpty()) {
                    issues.add("[etat] " + state.fullId() + " : ALIVE sans entite (a reparer)");
                } else if (this.loadedEntity(state).isEmpty()) {
                    ServerWorld world = this.server.getWorld(ref.get().dimension());
                    BlockPos probe = state.lastKnownPos().orElse(ref.get().homePos());
                    if (world != null && world.shouldTickEntityAt(probe)) {
                        issues.add("[etat] " + state.fullId() + " : ALIVE mais entite introuvable dans un chunk actif (perdue)");
                    }
                }
            }
            state.lastProblem().ifPresent(p -> issues.add("[dernier probleme] " + state.fullId() + " : " + p));
        }
        for (ServerWorld world : this.server.getWorlds()) {
            for (LivingEntity entity : world.getEntitiesByType(TypeFilter.instanceOf(LivingEntity.class), ControlledMarker::isControlled)) {
                ControlledMarker marker = entity.getAttached(ControlledMarker.TYPE);
                Optional<PointState> state = this.store.get(marker.fullId());
                boolean legit = state.isPresent() && state.get().token().map(marker.token()::equals).orElse(false);
                if (!legit) {
                    issues.add("[doublon] " + marker.fullId() + " : entite controlee chargee " + entity.getUuid() + " sans etat correspondant (a retirer par repair)");
                }
            }
        }
        return issues;
    }

    public List<String> repair() {
        List<String> actions = new ArrayList<>();
        List<String> stale = new ArrayList<>();
        for (PointState state : this.store.all()) {
            if (this.registry.ref(state.fullId()).isEmpty()) {
                stale.add(state.fullId());
            }
        }
        for (String id : stale) {
            this.store.remove(id);
            actions.add("etat purge : " + id);
        }
        long now = System.currentTimeMillis();
        for (PointState state : this.store.all()) {
            PointRef ref = this.registry.ref(state.fullId()).orElse(null);
            if (ref == null) {
                continue;
            }
            if (state.status() == PointStatus.ALIVE) {
                if (state.entityUuid().isEmpty()) {
                    state.markReady();
                    this.store.changed();
                    actions.add(state.fullId() + " : ALIVE sans entite -> READY");
                } else if (this.loadedEntity(state).isEmpty()) {
                    ServerWorld world = this.server.getWorld(ref.dimension());
                    BlockPos probe = state.lastKnownPos().orElse(ref.homePos());
                    if (world != null && world.shouldTickEntityAt(probe)) {
                        this.lost(ref, state, now, this.config());
                        actions.add(state.fullId() + " : entite perdue -> reapparition programmee");
                    }
                }
            }
            if (!ref.effectivelyEnabled() && state.status() != PointStatus.DISABLED) {
                this.disableState(ref, state);
                actions.add(state.fullId() + " : desactive");
            }
        }
        for (ServerWorld world : this.server.getWorlds()) {
            for (LivingEntity entity : world.getEntitiesByType(TypeFilter.instanceOf(LivingEntity.class), ControlledMarker::isControlled)) {
                ControlledMarker marker = entity.getAttached(ControlledMarker.TYPE);
                Optional<PointState> state = this.store.get(marker.fullId());
                boolean legit = state.isPresent() && state.get().token().map(marker.token()::equals).orElse(false);
                if (!legit) {
                    entity.discard();
                    this.duplicatesDiscarded++;
                    actions.add(marker.fullId() + " : doublon " + entity.getUuid() + " retire");
                }
            }
        }
        return actions;
    }

    public String stats() {
        double avgMs = this.passes == 0 ? 0 : (this.totalPassNanos / (double) this.passes) / 1_000_000.0D;
        return String.format("points=%d (invalides %d) zones=%d | derniere passe : %d point(s) actifs, %d chunk(s) balayes, %.3f ms (max %.3f, moyenne %.3f sur %d passes) | laisse : %d en retour, %d retours, %d teleports | apparitions=%d morts=%d perdus=%d doublons retires=%d | combats suivis=%d",
                this.registry.pointCount(), this.registry.invalidPointCount(), this.registry.zones().size(),
                this.lastActivePoints, this.lastScannedChunks, this.lastPassNanos / 1_000_000.0D, this.maxPassNanos / 1_000_000.0D, avgMs, this.passes,
                this.leash.size(), this.leash.returnsStarted(), this.leash.teleports(),
                this.spawnsTotal, this.deathsTotal, this.lostTotal, this.duplicatesDiscarded, this.kills.trackedCount());
    }

    public void resetStats() {
        this.maxPassNanos = 0;
        this.totalPassNanos = 0;
        this.passes = 0;
    }

    public double lastPassMillis() {
        return this.lastPassNanos / 1_000_000.0D;
    }

    public double maxPassMillis() {
        return this.maxPassNanos / 1_000_000.0D;
    }

    public int lastActivePoints() {
        return this.lastActivePoints;
    }
}
