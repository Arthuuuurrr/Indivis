package net.hautecapitale.spawns.store;

import net.hautecapitale.spawns.data.Ids;
import net.hautecapitale.spawns.data.Resolved;
import net.hautecapitale.spawns.data.SpawnPoint;
import net.hautecapitale.spawns.data.SpawnZone;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import java.util.List;
import java.util.Optional;

/**
 * Un point vu par le moteur : sa zone, sa configuration resolue, sa cle de chunk.
 *
 * <p>Immuable ; le registre en reconstruit un a chaque modification du point ou de
 * sa zone. Un point dont la resolution echoue (pas d'entite, par exemple) garde
 * une reference, avec {@code resolved} vide et la liste des problemes, pour que
 * {@code validate} puisse le signaler.
 */
public record PointRef(SpawnZone zone, SpawnPoint point, Optional<Resolved> resolved, List<String> problems) {

    public String fullId() {
        return Ids.full(this.zone.id(), this.point.id());
    }

    public boolean isValid() {
        return this.resolved.isPresent();
    }

    public boolean effectivelyEnabled() {
        return this.zone.enabled() && this.point.enabled();
    }

    public RegistryKey<World> dimension() {
        return RegistryKey.of(RegistryKeys.WORLD, this.zone.dimension());
    }

    /** Cle de chunk, calculee sans {@code ChunkPos} pour rester testable hors jeu. */
    public long chunkKey() {
        return chunkKey(this.point.chunkX(), this.point.chunkZ());
    }

    public static long chunkKey(int chunkX, int chunkZ) {
        return (chunkX & 0xFFFFFFFFL) | ((chunkZ & 0xFFFFFFFFL) << 32);
    }

    public BlockPos homePos() {
        return BlockPos.ofFloored(this.point.x(), this.point.y(), this.point.z());
    }

    public int activationRadius(int fallback) {
        return this.resolved.map(Resolved::activationRadius).orElse(fallback);
    }
}
