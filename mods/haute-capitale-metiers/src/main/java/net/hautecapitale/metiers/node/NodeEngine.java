package net.hautecapitale.metiers.node;

import net.fabricmc.fabric.api.event.lifecycle.v1.ServerChunkEvents;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.fabricmc.fabric.api.event.player.AttackBlockCallback;
import net.fabricmc.fabric.api.event.player.PlayerBlockBreakEvents;
import net.fabricmc.fabric.api.event.player.UseBlockCallback;
import net.hautecapitale.metiers.HauteCapitaleMetiers;
import net.hautecapitale.metiers.api.Metiers;
import net.hautecapitale.metiers.config.MetiersConfig;
import net.hautecapitale.metiers.craft.XpFalloff;
import net.hautecapitale.metiers.creature.DropRoll;
import net.hautecapitale.metiers.data.HcmData;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.item.ItemStack;
import net.minecraft.registry.Registries;
import net.minecraft.registry.RegistryKey;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.util.math.random.Random;
import net.minecraft.world.World;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.function.LongSupplier;

/**
 * Le moteur de nodes.
 *
 * <p>Trois règles, et tout le reste en découle :
 * <ul>
 *   <li><b>Aucune mécanique ne détruit un bloc.</b> Récolter remplace le bloc
 *       plein par le bloc vide ; repousser fait l'inverse. Casser un node est
 *       refusé à tout le monde, et casser n'importe quoi d'autre est refusé aux
 *       joueurs en survie si la configuration le demande.</li>
 *   <li><b>Aucun tick par node.</b> Un node récolté porte une heure de repousse.
 *       On ne la compare qu'au chargement du chunk, quand un joueur frappe, et
 *       lors d'un balayage léger des seuls nodes en attente.</li>
 *   <li><b>Un seul récolteur.</b> Le passage plein → vide se fait sur le fil du
 *       serveur, avant de donner quoi que ce soit : le deuxième joueur qui
 *       frappe au même instant trouve le node vide.</li>
 * </ul>
 */
public final class NodeEngine {

    /** Horloge murale, remplaçable par le diagnostic. */
    static LongSupplier clock = System::currentTimeMillis;

    /** Pour le diagnostic : avancer ou remettre l'horloge. {@code null} = heure réelle. */
    public static void setClock(LongSupplier supplier) {
        clock = supplier == null ? System::currentTimeMillis : supplier;
    }

    public static long now() {
        return clock.getAsLong();
    }

    /** Après ce silence, les coups déjà portés sur un node sont oubliés. */
    private static final long HIT_MEMORY_MILLIS = 4_000L;

    /** Où en est chaque joueur de ses coups sur un node. */
    private record Progress(BlockPos pos, int hits, long lastHit) {
    }

    private static final Map<UUID, Progress> PROGRESS = new HashMap<>();

    /**
     * Les nodes à faire repousser au prochain tick de leur monde : ceux dont
     * l'heure est passée pendant que le chunk était déchargé. On ne modifie pas
     * un chunk pendant qu'il se charge ; on attend la fin du tick.
     */
    private static final Map<RegistryKey<World>, Set<BlockPos>> DUE = new HashMap<>();

    private NodeEngine() {
    }

    // ------------------------------------------------------------------
    // Résultats

    /** Ce qu'un coup a produit. */
    public enum Hit {
        PAS_UN_NODE,
        NODE_VIDE,
        METIER_NON_APPRIS,
        NIVEAU_INSUFFISANT,
        MAUVAIS_OUTIL,
        EN_COURS,
        RECOLTE
    }

    public record HitOutcome(Hit hit, int hitsDone, int hitsNeeded, double xpGained,
                             List<ItemStack> loot, long secondsUntilRespawn) {
        public boolean harvested() {
            return hit == Hit.RECOLTE;
        }

        static HitOutcome of(Hit hit) {
            return new HitOutcome(hit, 0, 0, 0.0D, List.of(), 0L);
        }
    }

    /** Pourquoi un node ne peut pas être posé. */
    public enum PlaceRefusal {
        TYPE_INCONNU,
        BLOC_INCONNU,
        DEJA_UN_NODE
    }

    // ------------------------------------------------------------------
    // Branchement

    public static void init() {
        AttackBlockCallback.EVENT.register((player, world, hand, pos, direction) -> {
            if (!(player instanceof ServerPlayerEntity serverPlayer) || !(world instanceof ServerWorld serverWorld)) {
                return ActionResult.PASS;
            }
            if (!NodeAttachment.of(serverWorld).contains(pos)) {
                return ActionResult.PASS;
            }
            HitOutcome outcome = hit(serverPlayer, serverWorld, pos);
            NodeFeedback.overlay(serverPlayer, outcome);
            // FAIL : Minecraft ne commence pas à casser le bloc, quel que soit le mode.
            return ActionResult.FAIL;
        });

        UseBlockCallback.EVENT.register((player, world, hand, hitResult) -> {
            if (player.isCreative() || !(world instanceof ServerWorld serverWorld)) {
                return ActionResult.PASS;
            }
            if (!NodeAttachment.of(serverWorld).contains(hitResult.getBlockPos())) {
                return ActionResult.PASS;
            }
            // Un buisson de baies se cueille au clic droit en vanilla : sur un node,
            // ce raccourci contournerait le métier. Seul le coup compte.
            if (player instanceof ServerPlayerEntity serverPlayer && hand == Hand.MAIN_HAND) {
                NodeFeedback.hint(serverPlayer);
            }
            return ActionResult.FAIL;
        });

        PlayerBlockBreakEvents.BEFORE.register((world, player, pos, state, blockEntity) -> {
            if (!(world instanceof ServerWorld serverWorld)) {
                return true;
            }
            if (NodeAttachment.of(serverWorld).contains(pos)) {
                // Un node ne se casse jamais, même en créatif : on le retire avec
                // l'outil ou la commande, pas à coups de pioche.
                return false;
            }
            return !MetiersConfig.get().proteger_les_blocs || player.isCreative();
        });

        ServerChunkEvents.CHUNK_LOAD.register((world, chunk) -> onChunkLoad(world, chunk.getPos().x, chunk.getPos().z));

        ServerTickEvents.END_WORLD_TICK.register(NodeEngine::endTick);
    }

    /**
     * Au chargement d'un chunk, les nodes dont l'heure est passée pendant qu'il
     * était déchargé repoussent — au tick suivant, pas pendant le chargement
     * lui-même, où l'on ne modifie pas un chunk.
     */
    public static int onChunkLoad(ServerWorld world, int chunkX, int chunkZ) {
        NodeStore store = NodeAttachment.of(world);
        long now = clock.getAsLong();
        int scheduled = 0;
        for (BlockPos pos : store.inChunk(chunkX, chunkZ)) {
            NodeStore.Node node = store.get(pos);
            if (node != null && node.isReadyAt(now)) {
                DUE.computeIfAbsent(world.getRegistryKey(), ignored -> new HashSet<>()).add(pos);
                scheduled++;
            }
        }
        return scheduled;
    }

    /**
     * Fin de tick d'un monde : les repousses différées, puis — toutes les
     * {@code nodes_balayage_secondes} — le balayage des nodes en attente.
     */
    public static int endTick(ServerWorld world) {
        int restored = 0;
        Set<BlockPos> due = DUE.remove(world.getRegistryKey());
        if (due != null) {
            for (BlockPos pos : due) {
                if (restore(world, pos)) {
                    restored++;
                }
            }
        }
        int every = Math.max(20, MetiersConfig.get().nodes_balayage_secondes * 20);
        if (world.getTime() % every == 0) {
            restored += sweep(world);
        }
        return restored;
    }

    // ------------------------------------------------------------------
    // Poser et retirer

    /** Pose un node : enregistre la position et met le bloc plein en place. */
    public static PlaceRefusal place(ServerWorld world, BlockPos pos, Identifier typeId) {
        NodeType type = HcmData.NODES.get(typeId);
        if (type == null) {
            return PlaceRefusal.TYPE_INCONNU;
        }
        BlockState full = type.fullState();
        if (full == null) {
            return PlaceRefusal.BLOC_INCONNU;
        }
        NodeStore store = NodeAttachment.of(world);
        if (store.contains(pos)) {
            return PlaceRefusal.DEJA_UN_NODE;
        }
        store.put(pos, new NodeStore.Node(typeId, 0L));
        world.setBlockState(pos, full, Block.NOTIFY_ALL);
        return null;
    }

    /**
     * Retire un node du registre. Le bloc reste tel qu'il est : décider ce qui
     * doit se trouver là est le travail de l'administrateur, pas du moteur.
     */
    public static boolean remove(ServerWorld world, BlockPos pos) {
        return NodeAttachment.of(world).remove(pos) != null;
    }

    // ------------------------------------------------------------------
    // Frapper

    /**
     * Un coup de joueur sur un node. Vérifie tout, compte les coups, et à la
     * dernière frappe récolte — le passage plein → vide d'abord, le butin
     * ensuite.
     */
    public static HitOutcome hit(ServerPlayerEntity player, ServerWorld world, BlockPos pos) {
        NodeStore store = NodeAttachment.of(world);
        NodeStore.Node node = store.get(pos);
        if (node == null) {
            return HitOutcome.of(Hit.PAS_UN_NODE);
        }
        NodeType type = HcmData.NODES.get(node.type);
        if (type == null) {
            // Type retiré des données depuis la pose : le node reste, inerte.
            return HitOutcome.of(Hit.PAS_UN_NODE);
        }

        long now = clock.getAsLong();
        if (!node.isFull()) {
            if (node.isReadyAt(now)) {
                restore(world, pos);
            } else {
                long remaining = Math.max(1L, (node.respawnAt - now + 999L) / 1000L);
                return new HitOutcome(Hit.NODE_VIDE, 0, 0, 0.0D, List.of(), remaining);
            }
        }

        if (!Metiers.hasProfession(player, type.profession())) {
            return HitOutcome.of(Hit.METIER_NON_APPRIS);
        }
        int playerLevel = Metiers.getLevel(player, type.profession());
        if (playerLevel < type.level()) {
            return HitOutcome.of(Hit.NIVEAU_INSUFFISANT);
        }
        if (type.tool().isPresent() && !player.getMainHandStack().isIn(type.tool().get())) {
            return HitOutcome.of(Hit.MAUVAIS_OUTIL);
        }

        // --- Compter les coups.
        Progress previous = PROGRESS.get(player.getUuid());
        int hits = previous != null && previous.pos().equals(pos) && now - previous.lastHit() <= HIT_MEMORY_MILLIS
                ? previous.hits() + 1
                : 1;
        if (hits < type.hits()) {
            PROGRESS.put(player.getUuid(), new Progress(pos.toImmutable(), hits, now));
            return new HitOutcome(Hit.EN_COURS, hits, type.hits(), 0.0D, List.of(), 0L);
        }
        PROGRESS.remove(player.getUuid());

        // --- Récolte. Le verrou : le node passe à vide avant que quiconque soit
        // crédité. Sur le fil du serveur, personne ne peut passer entre les deux.
        store.markEmpty(pos, now + type.respawnMillis());
        BlockState empty = type.emptyState();
        if (empty != null) {
            world.setBlockState(pos, empty, Block.NOTIFY_ALL);
        }

        List<ItemStack> loot = roll(type, world.getRandom());
        for (ItemStack stack : loot) {
            give(player, stack);
        }
        double xp = XpFalloff.apply(type.xp(), playerLevel, type.level());
        if (xp > 0.0D) {
            Metiers.addXp(player, type.profession(), xp);
        }
        return new HitOutcome(Hit.RECOLTE, type.hits(), type.hits(), xp, loot, type.respawnSeconds());
    }

    /** Ce que la récolte donne, tiré au sort. */
    static List<ItemStack> roll(NodeType type, Random random) {
        return DropRoll.roll(type.loot(), random);
    }

    private static void give(ServerPlayerEntity player, ItemStack stack) {
        ItemStack copy = stack.copy();
        if (!player.getInventory().insertStack(copy) || !copy.isEmpty()) {
            player.dropItem(copy, false);
        }
    }

    // ------------------------------------------------------------------
    // Repousser

    /** Remet le bloc plein d'un node dont l'heure est venue. */
    public static boolean restore(ServerWorld world, BlockPos pos) {
        NodeStore store = NodeAttachment.of(world);
        NodeStore.Node node = store.get(pos);
        if (node == null || node.isFull()) {
            return false;
        }
        NodeType type = HcmData.NODES.get(node.type);
        if (type == null) {
            return false;
        }
        BlockState full = type.fullState();
        if (full == null) {
            return false;
        }
        if (!world.isPosLoaded(pos)) {
            // isPosLoaded = le chunk est chargé au niveau FULL (pas ServerWorld.isChunkLoaded,
            // qui répond « les entités y tickent-elles ? », faux dans un chunk forcé sans joueur).
            // Chunk déchargé : on ne touche à rien, le node reste en attente et
            // repoussera au chargement. Marquer plein sans poser le bloc laisserait
            // un registre qui ment.
            return false;
        }
        store.markFull(pos);
        world.setBlockState(pos, full, Block.NOTIFY_ALL);
        return true;
    }

    /**
     * Vide un node comme si on venait de le récolter, sans rien donner à
     * personne. Outil d'administration : voir la repousse sans attendre un joueur.
     */
    public static boolean forceEmpty(ServerWorld world, BlockPos pos) {
        NodeStore store = NodeAttachment.of(world);
        NodeStore.Node node = store.get(pos);
        if (node == null) {
            return false;
        }
        NodeType type = HcmData.NODES.get(node.type);
        if (type == null) {
            return false;
        }
        store.markEmpty(pos, clock.getAsLong() + type.respawnMillis());
        BlockState empty = type.emptyState();
        if (empty != null && world.isPosLoaded(pos)) {
            world.setBlockState(pos, empty, Block.NOTIFY_ALL);
        }
        return true;
    }

    /** Force la repousse d'un node, prêt ou non. Outil d'administration. */
    public static boolean forceRestore(ServerWorld world, BlockPos pos) {
        NodeStore store = NodeAttachment.of(world);
        NodeStore.Node node = store.get(pos);
        if (node == null) {
            return false;
        }
        node.respawnAt = 1L;
        return restore(world, pos);
    }

    /**
     * Le balayage : seulement les nodes en attente, seulement ceux dont le chunk
     * est chargé. Quelques dizaines d'entrées toutes les cinq secondes, jamais
     * un parcours de tous les nodes.
     */
    public static int sweep(ServerWorld world) {
        NodeStore store = NodeAttachment.of(world);
        long now = clock.getAsLong();
        int restored = 0;
        for (BlockPos pos : store.pending()) {
            NodeStore.Node node = store.get(pos);
            if (node != null && node.isReadyAt(now)
                    && world.isPosLoaded(pos)
                    && restore(world, pos)) {
                restored++;
            }
        }
        return restored;
    }

    /** Pour le diagnostic : oublier les coups en cours. */
    public static void forgetProgress() {
        PROGRESS.clear();
    }

    static void log(String message, Object... args) {
        HauteCapitaleMetiers.LOGGER.info(message, args);
    }
}
