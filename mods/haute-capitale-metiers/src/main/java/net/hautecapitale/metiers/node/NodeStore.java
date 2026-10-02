package net.hautecapitale.metiers.node;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Tous les nodes d'un monde, et où ils en sont.
 *
 * <p>Un node est une position, un type, et — s'il vient d'être récolté — l'heure
 * à laquelle il redeviendra plein. Rien d'autre. Aucun tick par node : on ne
 * compare l'horodatage qu'au chargement d'un chunk, quand un joueur frappe, et
 * lors d'un balayage léger des seuls nodes en attente.
 *
 * <p>Trois structures, une seule vérité : la carte par position est la
 * référence ; l'index par chunk et l'ensemble des nodes en attente s'en
 * déduisent et sont tenus à jour à chaque changement.
 *
 * <p>Sauvegardé avec le monde, sous forme de liste — voir {@link #CODEC}.
 */
public final class NodeStore {

    /** Un node posé. Mutable : son horodatage change à chaque récolte. */
    public static final class Node {
        public final Identifier type;
        /** Heure de repousse en millisecondes d'époque, ou 0 si le node est plein. */
        public long respawnAt;

        public Node(Identifier type, long respawnAt) {
            this.type = type;
            this.respawnAt = respawnAt;
        }

        public boolean isFull() {
            return respawnAt == 0L;
        }

        public boolean isReadyAt(long now) {
            return respawnAt != 0L && now >= respawnAt;
        }
    }

    /** Une ligne de sauvegarde. */
    private record Saved(BlockPos pos, Identifier type, long respawnAt) {
        static final Codec<Saved> CODEC = RecordCodecBuilder.create(instance -> instance.group(
                BlockPos.CODEC.fieldOf("pos").forGetter(Saved::pos),
                Identifier.CODEC.fieldOf("type").forGetter(Saved::type),
                Codec.LONG.optionalFieldOf("repousse", 0L).forGetter(Saved::respawnAt)
        ).apply(instance, Saved::new));
    }

    public static final Codec<NodeStore> CODEC = Saved.CODEC.listOf().xmap(NodeStore::fromSaved, NodeStore::toSaved);

    private final Map<BlockPos, Node> byPos = new HashMap<>();
    private final Map<Long, Set<BlockPos>> byChunk = new HashMap<>();
    private final Set<BlockPos> pending = new HashSet<>();

    public NodeStore() {
    }

    private static NodeStore fromSaved(List<Saved> lines) {
        NodeStore store = new NodeStore();
        for (Saved line : lines) {
            store.put(line.pos(), new Node(line.type(), line.respawnAt()));
        }
        return store;
    }

    private List<Saved> toSaved() {
        List<Saved> lines = new ArrayList<>(byPos.size());
        byPos.forEach((pos, node) -> lines.add(new Saved(pos, node.type, node.respawnAt)));
        return lines;
    }

    // ------------------------------------------------------------------

    public Node get(BlockPos pos) {
        return byPos.get(pos);
    }

    public boolean contains(BlockPos pos) {
        return byPos.containsKey(pos);
    }

    public int size() {
        return byPos.size();
    }

    public Map<BlockPos, Node> all() {
        return Collections.unmodifiableMap(byPos);
    }

    /** Les nodes d'un chunk — ce qu'on regarde quand il se charge. */
    public Collection<BlockPos> inChunk(ChunkPos chunk) {
        return inChunk(chunk.x, chunk.z);
    }

    public Collection<BlockPos> inChunk(int chunkX, int chunkZ) {
        Set<BlockPos> positions = byChunk.get(chunkKey(chunkX, chunkZ));
        return positions == null ? List.of() : List.copyOf(positions);
    }

    /**
     * La clé de chunk d'une position, calculée ici : la classe {@code ChunkPos}
     * entraîne au chargement les registres de génération, ce qui interdirait
     * de tester ce registre sans démarrer Minecraft.
     */
    static long chunkKey(BlockPos pos) {
        return chunkKey(pos.getX() >> 4, pos.getZ() >> 4);
    }

    static long chunkKey(int chunkX, int chunkZ) {
        return (chunkX & 0xFFFFFFFFL) | ((chunkZ & 0xFFFFFFFFL) << 32);
    }

    /** Les nodes récoltés qui attendent de repousser — les seuls que le balayage regarde. */
    public Collection<BlockPos> pending() {
        return List.copyOf(pending);
    }

    public void put(BlockPos pos, Node node) {
        BlockPos key = pos.toImmutable();
        byPos.put(key, node);
        byChunk.computeIfAbsent(chunkKey(key), ignored -> new HashSet<>()).add(key);
        if (node.isFull()) {
            pending.remove(key);
        } else {
            pending.add(key);
        }
    }

    public Node remove(BlockPos pos) {
        Node removed = byPos.remove(pos);
        if (removed != null) {
            long chunk = chunkKey(pos);
            Set<BlockPos> positions = byChunk.get(chunk);
            if (positions != null) {
                positions.remove(pos);
                if (positions.isEmpty()) {
                    byChunk.remove(chunk);
                }
            }
            pending.remove(pos);
        }
        return removed;
    }

    /** Marque un node récolté ; il repoussera à l'heure dite. */
    public void markEmpty(BlockPos pos, long respawnAt) {
        Node node = byPos.get(pos);
        if (node != null) {
            node.respawnAt = respawnAt;
            pending.add(pos.toImmutable());
        }
    }

    /** Marque un node plein. */
    public void markFull(BlockPos pos) {
        Node node = byPos.get(pos);
        if (node != null) {
            node.respawnAt = 0L;
            pending.remove(pos);
        }
    }

    public void clear() {
        byPos.clear();
        byChunk.clear();
        pending.clear();
    }
}
