package net.hautecapitale.spawns.state;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.hautecapitale.spawns.data.PointStatus;
import net.minecraft.util.Uuids;
import net.minecraft.util.math.BlockPos;

import java.util.Optional;
import java.util.UUID;

/**
 * L'etat vivant d'un point : ce qui change en jeu et doit survivre a un redemarrage.
 *
 * <p>Separe volontairement de la configuration du point (fichiers JSON, versionnables)
 * : l'etat vit dans la sauvegarde du monde. Les timers sont en horloge murale
 * (millisecondes epoch), pour rester coherents a travers un arret du serveur :
 * un orc tue a 14:00 avec 5 minutes de delai revient a 14:05, serveur redemarre ou pas.
 */
public final class PointState {

    /** La forme sauvegardee. Les compteurs transitoires n'y figurent pas. */
    public record Saved(String fullId, PointStatus status, Optional<UUID> entityUuid, Optional<UUID> token,
                        long respawnAtMillis, Optional<BlockPos> lastKnownPos, int spawnCount, int killCount,
                        long lastKilledAtMillis, Optional<String> lastKillerName) {

        public static final Codec<Saved> CODEC = RecordCodecBuilder.create(instance -> instance.group(
                Codec.STRING.fieldOf("id").forGetter(Saved::fullId),
                PointStatus.CODEC.fieldOf("status").forGetter(Saved::status),
                Uuids.INT_STREAM_CODEC.optionalFieldOf("entity").forGetter(Saved::entityUuid),
                Uuids.INT_STREAM_CODEC.optionalFieldOf("token").forGetter(Saved::token),
                Codec.LONG.optionalFieldOf("respawn_at", 0L).forGetter(Saved::respawnAtMillis),
                BlockPos.CODEC.optionalFieldOf("last_pos").forGetter(Saved::lastKnownPos),
                Codec.INT.optionalFieldOf("spawns", 0).forGetter(Saved::spawnCount),
                Codec.INT.optionalFieldOf("kills", 0).forGetter(Saved::killCount),
                Codec.LONG.optionalFieldOf("last_killed_at", 0L).forGetter(Saved::lastKilledAtMillis),
                Codec.STRING.optionalFieldOf("last_killer").forGetter(Saved::lastKillerName)
        ).apply(instance, Saved::new));
    }

    private final String fullId;
    private PointStatus status;
    private UUID entityUuid;
    private UUID token;
    private long respawnAtMillis;
    private BlockPos lastKnownPos;
    private int spawnCount;
    private int killCount;
    private long lastKilledAtMillis;
    private String lastKillerName;

    // --- transitoire (jamais sauvegarde) ---
    private int unresolvedChecks;
    private long lastSpawnAttemptMillis;
    private String lastProblem;

    public PointState(String fullId) {
        this.fullId = fullId;
        this.status = PointStatus.READY;
    }

    public static PointState fromSaved(Saved saved) {
        PointState state = new PointState(saved.fullId());
        state.status = saved.status();
        state.entityUuid = saved.entityUuid().orElse(null);
        state.token = saved.token().orElse(null);
        state.respawnAtMillis = saved.respawnAtMillis();
        state.lastKnownPos = saved.lastKnownPos().orElse(null);
        state.spawnCount = saved.spawnCount();
        state.killCount = saved.killCount();
        state.lastKilledAtMillis = saved.lastKilledAtMillis();
        state.lastKillerName = saved.lastKillerName().orElse(null);
        return state;
    }

    public Saved toSaved() {
        return new Saved(this.fullId, this.status, Optional.ofNullable(this.entityUuid), Optional.ofNullable(this.token),
                this.respawnAtMillis, Optional.ofNullable(this.lastKnownPos), this.spawnCount, this.killCount,
                this.lastKilledAtMillis, Optional.ofNullable(this.lastKillerName));
    }

    // --- lecture ---------------------------------------------------------------------

    public String fullId() {
        return this.fullId;
    }

    public PointStatus status() {
        return this.status;
    }

    public Optional<UUID> entityUuid() {
        return Optional.ofNullable(this.entityUuid);
    }

    public Optional<UUID> token() {
        return Optional.ofNullable(this.token);
    }

    public long respawnAtMillis() {
        return this.respawnAtMillis;
    }

    public Optional<BlockPos> lastKnownPos() {
        return Optional.ofNullable(this.lastKnownPos);
    }

    public int spawnCount() {
        return this.spawnCount;
    }

    public int killCount() {
        return this.killCount;
    }

    public long lastKilledAtMillis() {
        return this.lastKilledAtMillis;
    }

    public Optional<String> lastKillerName() {
        return Optional.ofNullable(this.lastKillerName);
    }

    public int unresolvedChecks() {
        return this.unresolvedChecks;
    }

    public Optional<String> lastProblem() {
        return Optional.ofNullable(this.lastProblem);
    }

    /** Secondes restantes avant la reapparition ; 0 si le delai est ecoule ou sans objet. */
    public long remainingSeconds(long nowMillis) {
        if (this.status != PointStatus.DEAD_WAITING) {
            return 0;
        }
        return Math.max(0, (this.respawnAtMillis - nowMillis + 999) / 1000);
    }

    // --- transitions -----------------------------------------------------------------

    /** Le mob vient d'apparaitre. */
    public void markAlive(UUID entity, UUID newToken, BlockPos pos) {
        this.status = PointStatus.ALIVE;
        this.entityUuid = entity;
        this.token = newToken;
        this.respawnAtMillis = 0L;
        this.lastKnownPos = pos;
        this.spawnCount++;
        this.unresolvedChecks = 0;
        this.lastProblem = null;
    }

    /** L'ajout au monde a echoue juste apres {@link #markAlive} : on revient en arriere. */
    public void rollbackSpawn(PointStatus previous) {
        this.status = previous == PointStatus.ALIVE ? PointStatus.READY : previous;
        this.entityUuid = null;
        this.token = null;
        this.spawnCount = Math.max(0, this.spawnCount - 1);
    }

    /** Une entite existante est reconnue comme le mob de ce point. */
    public void adopt(UUID entity, UUID entityToken, BlockPos pos) {
        this.status = PointStatus.ALIVE;
        this.entityUuid = entity;
        this.token = entityToken;
        this.respawnAtMillis = 0L;
        this.lastKnownPos = pos;
        this.unresolvedChecks = 0;
    }

    /** Le mob est mort (ou perdu) : attendre {@code delaySeconds}. */
    public void markDead(long nowMillis, int delaySeconds, String killerName, boolean realKill) {
        this.status = PointStatus.DEAD_WAITING;
        this.entityUuid = null;
        this.token = null;
        this.respawnAtMillis = nowMillis + Math.max(0, delaySeconds) * 1000L;
        this.unresolvedChecks = 0;
        if (realKill) {
            this.killCount++;
            this.lastKilledAtMillis = nowMillis;
            this.lastKillerName = killerName;
        }
    }

    public void markReady() {
        this.status = PointStatus.READY;
        this.entityUuid = null;
        this.token = null;
        this.respawnAtMillis = 0L;
        this.unresolvedChecks = 0;
    }

    public void markDisabled() {
        this.status = PointStatus.DISABLED;
        this.entityUuid = null;
        this.token = null;
        this.respawnAtMillis = 0L;
        this.unresolvedChecks = 0;
    }

    /** Vrai si le delai est ecoule : passe en READY. */
    public boolean advanceTimer(long nowMillis) {
        if (this.status == PointStatus.DEAD_WAITING && nowMillis >= this.respawnAtMillis) {
            this.markReady();
            return true;
        }
        return false;
    }

    public void seen(BlockPos pos) {
        this.lastKnownPos = pos;
        this.unresolvedChecks = 0;
    }

    /** Un controle de plus sans retrouver l'entite ; rend le nouveau total. */
    public int unresolved() {
        return ++this.unresolvedChecks;
    }

    public void problem(String message) {
        this.lastProblem = message;
    }

    public long lastSpawnAttemptMillis() {
        return this.lastSpawnAttemptMillis;
    }

    public void attemptedSpawn(long nowMillis) {
        this.lastSpawnAttemptMillis = nowMillis;
    }

    @Override
    public String toString() {
        return this.fullId + "[" + this.status + (this.entityUuid != null ? " " + this.entityUuid : "") + "]";
    }
}
