package net.hautecapitale.metiers.command;

import net.fabricmc.fabric.api.event.player.AttackBlockCallback;
import net.fabricmc.fabric.api.event.player.PlayerBlockBreakEvents;
import net.fabricmc.fabric.api.event.player.UseBlockCallback;
import net.hautecapitale.metiers.HauteCapitaleMetiers;
import net.hautecapitale.metiers.api.Metiers;
import net.hautecapitale.metiers.config.MetiersConfig;
import net.hautecapitale.metiers.data.HcmData;
import net.hautecapitale.metiers.node.NodeAttachment;
import net.hautecapitale.metiers.node.NodeEngine;
import net.hautecapitale.metiers.node.NodeEngine.Hit;
import net.hautecapitale.metiers.node.NodeEngine.HitOutcome;
import net.hautecapitale.metiers.node.NodeEngine.PlaceRefusal;
import net.hautecapitale.metiers.node.NodeStore;
import net.hautecapitale.metiers.node.NodeTool;
import net.hautecapitale.metiers.node.NodeType;
import net.hautecapitale.metiers.profession.Profession;
import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.entity.ItemEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.nbt.NbtOps;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.Identifier;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

import java.util.List;

/**
 * Diagnostic serveur du moteur de nodes.
 *
 * <p>Tout se joue sur deux joueurs en mémoire et deux nodes posés en hauteur,
 * au-dessus du point d'apparition — un chunk toujours chargé — dans des blocs
 * d'air, remis en air à la fin. Le scénario suit le cahier des charges de
 * l'étape : poser, récolter au bon niveau, refuser en dessous, vider sans
 * détruire, repousser, recharger le chunk, frapper à deux, et l'interdiction
 * de casser quoi que ce soit.
 *
 * <p>L'horloge du moteur est remplacée pour ne pas attendre cinq minutes de
 * repousse, puis remise à l'heure réelle.
 */
final class NodeDiagnostic {

    private static final Identifier IRON = HauteCapitaleMetiers.id("mineur/fer");
    private static final Identifier DANDELION = HauteCapitaleMetiers.id("herboriste/pissenlit");

    private NodeDiagnostic() {
    }

    static List<String> run(MinecraftServer server, List<String> report) {
        ServerWorld world = server.getWorld(World.OVERWORLD);
        if (world == null) {
            return report;
        }
        NodeType iron = HcmData.NODES.get(IRON);
        NodeType dandelion = HcmData.NODES.get(DANDELION);
        if (iron == null || dandelion == null) {
            report.add("--- Nodes");
            report.add("  FAIL  types d'essai absents des données : " + IRON + ", " + DANDELION);
            return report;
        }

        // Une colonne d'air au-dessus du point d'apparition.
        BlockPos spawn = world.getSpawnPoint().getPos();
        BlockPos base = new BlockPos(spawn.getX(), Math.min(world.getTopYInclusive() - 12, 240), spawn.getZ());
        BlockPos orePos = base.up(2);
        BlockPos dirtPos = base.east(2);
        BlockPos flowerPos = dirtPos.up();
        BlockPos plainPos = base.west(2);
        // Sans joueur en ligne, rien ne garantit que le chunk d'apparition soit
        // chargé : on le force le temps du diagnostic, comme /forceload.
        ChunkPos chunk = new ChunkPos(base);
        boolean wasForced = world.getForcedChunks().contains(chunk.toLong());
        if (!wasForced) {
            world.setChunkForced(chunk.x, chunk.z, true);
        }
        world.getChunk(chunk.x, chunk.z);
        boolean chunkLoaded = world.isPosLoaded(base);
        boolean areaFree = chunkLoaded && world.isAir(orePos) && world.isAir(dirtPos) && world.isAir(flowerPos)
                && world.isAir(plainPos);
        report.add("--- Nodes — terrain d'essai");
        check(report, "chunk d'apparition chargé, colonne d'air libre en " + base.toShortString(), () -> areaFree);
        if (!areaFree) {
            if (!wasForced) {
                world.setChunkForced(chunk.x, chunk.z, false);
            }
            return report;
        }

        NodeStore store = NodeAttachment.of(world);
        ServerPlayerEntity miner = testPlayer(server, world, "NodeTestA");
        ServerPlayerEntity other = testPlayer(server, world, "NodeTestB");
        miner.setPosition(Vec3d.ofCenter(base));
        other.setPosition(Vec3d.ofCenter(base));
        long start = System.currentTimeMillis();
        long[] offset = {0L};
        NodeEngine.setClock(() -> System.currentTimeMillis() + offset[0]);
        NodeEngine.forgetProgress();

        try {
            scenarioPlace(world, store, orePos, dirtPos, flowerPos, report);
            scenarioRefusals(miner, world, orePos, report);
            scenarioHarvest(miner, world, store, iron, orePos, report);
            scenarioSecondHitter(other, world, orePos, report);
            scenarioRespawn(world, store, iron, orePos, offset, report);
            scenarioChunkLoad(miner, world, store, orePos, report);
            scenarioRace(miner, other, world, store, orePos, offset, report);
            scenarioFalloffAndFlower(miner, world, store, dandelion, flowerPos, offset, report);
            scenarioProtection(miner, world, store, orePos, plainPos, report);
            scenarioEvents(miner, world, orePos, report);
            scenarioPersistence(world, store, orePos, flowerPos, report);
            scenarioTool(report);
        } finally {
            NodeEngine.setClock(null);
            NodeEngine.forgetProgress();
            NodeEngine.remove(world, orePos);
            NodeEngine.remove(world, flowerPos);
            for (BlockPos pos : List.of(orePos, dirtPos, flowerPos, plainPos)) {
                world.setBlockState(pos, Blocks.AIR.getDefaultState(), Block.NOTIFY_ALL);
            }
            world.getEntitiesByClass(ItemEntity.class, new Box(base).expand(6.0D), entity -> true)
                    .forEach(ItemEntity::discard);
            Metiers.forget(miner, Profession.MINEUR);
            Metiers.forget(miner, Profession.HERBORISTE);
            Metiers.forget(other, Profession.MINEUR);
            if (!wasForced) {
                world.setChunkForced(chunk.x, chunk.z, false);
            }
        }
        report.add("  (durée " + (System.currentTimeMillis() - start) + " ms)");
        return report;
    }

    // ------------------------------------------------------------------

    /** Poser : le bloc plein apparaît, le registre le connaît, les refus sont nets. */
    private static void scenarioPlace(ServerWorld world, NodeStore store, BlockPos orePos, BlockPos dirtPos,
                                      BlockPos flowerPos, List<String> report) {
        section(report, "Nodes — poser");
        int before = store.size();
        check(report, "poser un filon de fer", () -> NodeEngine.place(world, orePos, IRON) == null);
        check(report, "le bloc plein est en place (minerai de fer)",
                () -> world.getBlockState(orePos).isOf(Blocks.IRON_ORE));
        check(report, "le registre du monde le connaît, plein",
                () -> store.contains(orePos) && store.get(orePos).isFull());
        check(report, "poser deux fois au même endroit est refusé",
                () -> NodeEngine.place(world, orePos, IRON) == PlaceRefusal.DEJA_UN_NODE);
        check(report, "type inconnu refusé", () -> NodeEngine.place(world, orePos.up(), HauteCapitaleMetiers.id("mineur/mithril"))
                == PlaceRefusal.TYPE_INCONNU);

        world.setBlockState(dirtPos, Blocks.DIRT.getDefaultState(), Block.NOTIFY_ALL);
        check(report, "poser une touffe de pissenlits", () -> NodeEngine.place(world, flowerPos, DANDELION) == null);
        check(report, "deux nodes de plus dans le registre", () -> store.size() == before + 2);
        check(report, "l'index par chunk les voit tous les deux",
                () -> store.inChunk(orePos.getX() >> 4, orePos.getZ() >> 4).containsAll(List.of(orePos, flowerPos)));
    }

    /** Sans métier, sans niveau, sans outil : refus, et le bloc ne bouge pas. */
    private static void scenarioRefusals(ServerPlayerEntity miner, ServerWorld world, BlockPos orePos,
                                         List<String> report) {
        section(report, "Nodes — refus");
        check(report, "sans le métier : refus", () -> NodeEngine.hit(miner, world, orePos).hit() == Hit.METIER_NON_APPRIS);
        Metiers.learn(miner, Profession.MINEUR);
        check(report, "Mineur niveau 1 sur un filon de niveau 5 : refus",
                () -> NodeEngine.hit(miner, world, orePos).hit() == Hit.NIVEAU_INSUFFISANT);
        Metiers.setLevel(miner, Profession.MINEUR, 5);
        check(report, "au niveau, mais sans pioche : refus",
                () -> NodeEngine.hit(miner, world, orePos).hit() == Hit.MAUVAIS_OUTIL);
        check(report, "le bloc est intact après trois refus", () -> world.getBlockState(orePos).isOf(Blocks.IRON_ORE));
        check(report, "un bloc qui n'est pas un node : rien",
                () -> NodeEngine.hit(miner, world, orePos.up()).hit() == Hit.PAS_UN_NODE);
        check(report, "aucun objet donné par un refus", () -> miner.getInventory().isEmpty());
    }

    /** Trois coups, puis la récolte : bloc vide, butin, XP pleine à niveau égal. */
    private static void scenarioHarvest(ServerPlayerEntity miner, ServerWorld world, NodeStore store, NodeType iron,
                                        BlockPos orePos, List<String> report) {
        section(report, "Nodes — récolte au bon niveau");
        miner.setStackInHand(Hand.MAIN_HAND, new ItemStack(Items.IRON_PICKAXE));
        double xpBefore = Metiers.getXp(miner, Profession.MINEUR);

        HitOutcome first = NodeEngine.hit(miner, world, orePos);
        check(report, "premier coup : 1 / " + iron.hits(), () -> first.hit() == Hit.EN_COURS && first.hitsDone() == 1);
        HitOutcome second = NodeEngine.hit(miner, world, orePos);
        check(report, "deuxième coup : 2 / " + iron.hits(), () -> second.hit() == Hit.EN_COURS && second.hitsDone() == 2);
        check(report, "le bloc est toujours plein entre les coups", () -> world.getBlockState(orePos).isOf(Blocks.IRON_ORE));

        HitOutcome third = NodeEngine.hit(miner, world, orePos);
        check(report, "troisième coup : récolté", third::harvested);
        check(report, "le bloc devient vide (pierre), pas de l'air",
                () -> world.getBlockState(orePos).isOf(Blocks.STONE));
        check(report, "le registre le dit vide, avec une heure de repousse",
                () -> !store.get(orePos).isFull() && store.get(orePos).respawnAt > NodeEngine.now());
        check(report, "il est dans la liste d'attente", () -> store.pending().contains(orePos));
        check(report, "du fer brut est donné (1 à 2)",
                () -> count(miner, Items.RAW_IRON) >= 1 && count(miner, Items.RAW_IRON) <= 2
                        && third.loot().stream().allMatch(stack -> stack.isOf(Items.RAW_IRON)));
        check(report, "XP pleine à niveau égal : +" + (int) iron.xp(),
                () -> third.xpGained() == iron.xp() && Metiers.getXp(miner, Profession.MINEUR) == xpBefore + iron.xp());
        check(report, "délai annoncé = repousse du type", () -> third.secondsUntilRespawn() == iron.respawnSeconds());
    }

    /** Un autre joueur arrive juste après : rien à récolter. */
    private static void scenarioSecondHitter(ServerPlayerEntity other, ServerWorld world, BlockPos orePos,
                                             List<String> report) {
        section(report, "Nodes — node vide");
        Metiers.learn(other, Profession.MINEUR);
        Metiers.setLevel(other, Profession.MINEUR, 10);
        other.setStackInHand(Hand.MAIN_HAND, new ItemStack(Items.IRON_PICKAXE));
        HitOutcome outcome = NodeEngine.hit(other, world, orePos);
        check(report, "le second joueur trouve le node vide", () -> outcome.hit() == Hit.NODE_VIDE);
        check(report, "avec le temps restant annoncé",
                () -> outcome.secondsUntilRespawn() >= 1 && outcome.secondsUntilRespawn() <= 300);
        check(report, "et ne reçoit rien", () -> other.getInventory().count(Items.RAW_IRON) == 0);
    }

    /** L'heure avance : le balayage remet le bloc plein. */
    private static void scenarioRespawn(ServerWorld world, NodeStore store, NodeType iron, BlockPos orePos,
                                        long[] offset, List<String> report) {
        section(report, "Nodes — repousse");
        check(report, "avant l'heure, le balayage ne fait rien", () -> NodeEngine.sweep(world) == 0
                && world.getBlockState(orePos).isOf(Blocks.STONE));
        offset[0] += iron.respawnMillis() + 1_000L;
        check(report, "l'heure passée, le balayage remet un node", () -> NodeEngine.sweep(world) == 1);
        check(report, "le bloc plein est revenu", () -> world.getBlockState(orePos).isOf(Blocks.IRON_ORE));
        check(report, "le registre le dit plein, plus en attente",
                () -> store.get(orePos).isFull() && !store.pending().contains(orePos));
        check(report, "un second balayage ne fait rien", () -> NodeEngine.sweep(world) == 0);
    }

    /**
     * Le chunk se recharge alors que l'heure est passée : le node repousse à la
     * fin du tick, sans attendre le balayage.
     */
    private static void scenarioChunkLoad(ServerPlayerEntity miner, ServerWorld world, NodeStore store,
                                          BlockPos orePos, List<String> report) {
        section(report, "Nodes — rechargement du chunk");
        // On simule « récolté il y a longtemps » : vide, heure de repousse déjà passée.
        store.markEmpty(orePos, NodeEngine.now() - 1L);
        world.setBlockState(orePos, Blocks.STONE.getDefaultState(), Block.NOTIFY_ALL);
        int scheduled = NodeEngine.onChunkLoad(world, orePos.getX() >> 4, orePos.getZ() >> 4);
        check(report, "au chargement, une repousse est programmée", () -> scheduled == 1);
        check(report, "pas pendant le chargement lui-même", () -> world.getBlockState(orePos).isOf(Blocks.STONE));
        int restored = NodeEngine.endTick(world);
        check(report, "à la fin du tick, le node est plein", () -> restored >= 1 && store.get(orePos).isFull()
                && world.getBlockState(orePos).isOf(Blocks.IRON_ORE));
        check(report, "un chunk sans node en retard ne programme rien",
                () -> NodeEngine.onChunkLoad(world, orePos.getX() >> 4, orePos.getZ() >> 4) == 0);

        // Un coup sur un node vide dont l'heure est passée le restaure aussi,
        // sans attendre : le joueur qui arrive ne voit jamais un node « en retard ».
        store.markEmpty(orePos, NodeEngine.now() - 1L);
        world.setBlockState(orePos, Blocks.STONE.getDefaultState(), Block.NOTIFY_ALL);
        NodeEngine.forgetProgress();
        miner.setStackInHand(Hand.MAIN_HAND, new ItemStack(Items.IRON_PICKAXE));
        HitOutcome late = NodeEngine.hit(miner, world, orePos);
        check(report, "un coup sur un node en retard le restaure, puis compte",
                () -> late.hit() == Hit.EN_COURS && late.hitsDone() == 1 && store.get(orePos).isFull()
                        && world.getBlockState(orePos).isOf(Blocks.IRON_ORE));
        NodeEngine.forgetProgress();
    }

    /** Deux joueurs à 2/3 chacun : le premier qui frappe récolte, l'autre trouve vide. */
    private static void scenarioRace(ServerPlayerEntity miner, ServerPlayerEntity other, ServerWorld world,
                                     NodeStore store, BlockPos orePos, long[] offset, List<String> report) {
        section(report, "Nodes — deux joueurs frappent ensemble");
        NodeEngine.forgetProgress();
        clear(miner);
        clear(other);
        miner.setStackInHand(Hand.MAIN_HAND, new ItemStack(Items.IRON_PICKAXE));
        other.setStackInHand(Hand.MAIN_HAND, new ItemStack(Items.IRON_PICKAXE));

        check(report, "A : 1/3", () -> NodeEngine.hit(miner, world, orePos).hitsDone() == 1);
        check(report, "B : 1/3 — les coups ne s'additionnent pas entre joueurs",
                () -> NodeEngine.hit(other, world, orePos).hitsDone() == 1);
        check(report, "A : 2/3", () -> NodeEngine.hit(miner, world, orePos).hitsDone() == 2);
        check(report, "B : 2/3", () -> NodeEngine.hit(other, world, orePos).hitsDone() == 2);
        HitOutcome a = NodeEngine.hit(miner, world, orePos);
        HitOutcome b = NodeEngine.hit(other, world, orePos);
        check(report, "A récolte", a::harvested);
        check(report, "B, un instant après, trouve le node vide", () -> b.hit() == Hit.NODE_VIDE);
        check(report, "un seul butin distribué",
                () -> count(miner, Items.RAW_IRON) >= 1 && count(other, Items.RAW_IRON) == 0);

        // Les coups s'oublient après le délai de mémoire.
        offset[0] += 400_000L;
        NodeEngine.sweep(world);
        check(report, "après la repousse, A repart de 1/3", () -> NodeEngine.hit(miner, world, orePos).hitsDone() == 1);
        offset[0] += 5_000L;
        check(report, "cinq secondes de silence : les coups sont oubliés",
                () -> NodeEngine.hit(miner, world, orePos).hitsDone() == 1);
        NodeEngine.forgetProgress();
    }

    /** La décote joue comme en fabrication ; une plante se cueille en un coup, sans outil. */
    private static void scenarioFalloffAndFlower(ServerPlayerEntity miner, ServerWorld world, NodeStore store,
                                                 NodeType dandelion, BlockPos flowerPos, long[] offset,
                                                 List<String> report) {
        section(report, "Nodes — décote et cueillette");
        BlockPos orePos = flowerPos.west(2).up();
        Metiers.setLevel(miner, Profession.MINEUR, 30);
        clear(miner);
        miner.setStackInHand(Hand.MAIN_HAND, new ItemStack(Items.IRON_PICKAXE));
        NodeEngine.hit(miner, world, orePos);
        NodeEngine.hit(miner, world, orePos);
        HitOutcome outcome = NodeEngine.hit(miner, world, orePos);
        check(report, "niveau 30 sur un filon de niveau 5 : récolté, 0 XP",
                () -> outcome.harvested() && outcome.xpGained() == 0.0D);
        check(report, "mais le butin est bien donné", () -> count(miner, Items.RAW_IRON) >= 1);

        check(report, "Herboriste non appris : refus sur la fleur",
                () -> NodeEngine.hit(miner, world, flowerPos).hit() == Hit.METIER_NON_APPRIS);
        Metiers.learn(miner, Profession.HERBORISTE);
        miner.setStackInHand(Hand.MAIN_HAND, ItemStack.EMPTY);
        HitOutcome picked = NodeEngine.hit(miner, world, flowerPos);
        check(report, "Herboriste niveau 1, main nue : cueilli en un coup", picked::harvested);
        check(report, "la fleur laisse de l'air (bloc vide du type)", () -> world.isAir(flowerPos));
        check(report, "des pissenlits sont donnés", () -> count(miner, Items.DANDELION) >= 1);
        check(report, "XP pleine : +" + (int) dandelion.xp(), () -> picked.xpGained() == dandelion.xp());
        offset[0] += dandelion.respawnMillis() + 1_000L;
        NodeEngine.sweep(world);
        check(report, "la fleur repousse", () -> world.getBlockState(flowerPos).isOf(Blocks.DANDELION));
        offset[0] += 400_000L;
        NodeEngine.sweep(world);
        Metiers.setLevel(miner, Profession.MINEUR, 5);
    }

    /**
     * Personne ne casse rien : ni un node, ni — en survie — n'importe quel bloc.
     *
     * <p>On interroge le crochet Fabric lui-même ({@code PlayerBlockBreakEvents.BEFORE}),
     * celui que {@code tryBreakBlock} consulte pour tout cassage, créatif compris.
     * Appeler {@code tryBreakBlock} sur un joueur en mémoire n'est pas possible :
     * en cas de refus, Fabric renvoie au joueur un paquet de mise à jour du bloc,
     * et un joueur sans connexion n'a personne à qui l'envoyer.
     */
    private static void scenarioProtection(ServerPlayerEntity miner, ServerWorld world, NodeStore store,
                                           BlockPos orePos, BlockPos plainPos, List<String> report) {
        section(report, "Nodes — protection des blocs");
        // Le joueur en mémoire naît dans le mode par défaut du serveur — survie ou
        // créatif —, et on ne peut pas l'en changer sans connexion. Les deux
        // modes ont leur vérité : on teste celle du serveur où l'on est.
        boolean creative = miner.isCreative();
        report.add("      joueur d'essai en mode " + (creative ? "créatif" : "survie") + " (mode par défaut du serveur)");
        check(report, "casser un node : refusé par le crochet de cassage" + (creative ? ", même en créatif" : ""),
                () -> !PlayerBlockBreakEvents.BEFORE.invoker().beforeBlockBreak(world, miner, orePos,
                        world.getBlockState(orePos), null));
        check(report, "le node est toujours là", () -> world.getBlockState(orePos).isOf(Blocks.IRON_ORE)
                && store.contains(orePos));

        world.setBlockState(plainPos, Blocks.COBBLESTONE.getDefaultState(), Block.NOTIFY_ALL);
        boolean protect = MetiersConfig.get().proteger_les_blocs;
        boolean allowed = PlayerBlockBreakEvents.BEFORE.invoker().beforeBlockBreak(world, miner, plainPos,
                world.getBlockState(plainPos), null);
        if (creative) {
            check(report, "casser un bloc ordinaire en créatif : permis (le créatif n'est pas concerné)", () -> allowed);
        } else {
            check(report, protect ? "casser un bloc ordinaire en survie : refusé (proteger_les_blocs)"
                            : "casser un bloc ordinaire en survie : permis (proteger_les_blocs = false)",
                    () -> allowed != protect);
        }
        check(report, "le bloc ordinaire est intact : le crochet ne touche à rien",
                () -> world.getBlockState(plainPos).isOf(Blocks.COBBLESTONE));
    }

    /** Les branchements Fabric : clic gauche → coup, clic droit → rien ne passe. */
    private static void scenarioEvents(ServerPlayerEntity miner, ServerWorld world, BlockPos orePos, List<String> report) {
        section(report, "Nodes — événements de clic");
        NodeEngine.forgetProgress();
        miner.setStackInHand(Hand.MAIN_HAND, new ItemStack(Items.IRON_PICKAXE));
        ActionResult attack = AttackBlockCallback.EVENT.invoker().interact(miner, world, Hand.MAIN_HAND, orePos, Direction.UP);
        check(report, "clic gauche sur un node : le moteur prend la main (FAIL pour Minecraft)",
                () -> attack == ActionResult.FAIL);
        check(report, "et ce clic a compté pour un coup",
                () -> NodeEngine.hit(miner, world, orePos).hitsDone() == 2);
        NodeEngine.forgetProgress();
        ActionResult attackElsewhere = AttackBlockCallback.EVENT.invoker().interact(miner, world, Hand.MAIN_HAND,
                orePos.up(3), Direction.UP);
        check(report, "clic gauche ailleurs : le moteur laisse passer", () -> attackElsewhere == ActionResult.PASS);

        BlockHitResult hit = new BlockHitResult(Vec3d.ofCenter(orePos), Direction.UP, orePos, false);
        ActionResult use = UseBlockCallback.EVENT.invoker().interact(miner, world, Hand.MAIN_HAND, hit);
        if (miner.isCreative()) {
            check(report, "clic droit sur un node en créatif : laissé passer (la baguette a besoin du clic)",
                    () -> use == ActionResult.PASS);
        } else {
            check(report, "clic droit sur un node en survie : bloqué (pas de cueillette vanilla)",
                    () -> use == ActionResult.FAIL);
        }
        check(report, "l'événement de cassage refuse un node",
                () -> !PlayerBlockBreakEvents.BEFORE.invoker().beforeBlockBreak(world, miner, orePos,
                        world.getBlockState(orePos), null));
    }

    /** Ce que la sauvegarde du monde écrit se relit, positions et heures comprises. */
    private static void scenarioPersistence(ServerWorld world, NodeStore store, BlockPos orePos, BlockPos flowerPos,
                                            List<String> report) {
        section(report, "Nodes — persistance du monde");
        check(report, "le registre est attaché au monde", () -> world.hasAttached(NodeAttachment.NODES)
                && world.getAttached(NodeAttachment.NODES) == store);
        store.markEmpty(orePos, 4_242L);
        var encoded = NodeStore.CODEC.encodeStart(NbtOps.INSTANCE, store);
        check(report, "le registre s'écrit en NBT", () -> encoded.result().isPresent());
        var decoded = encoded.result().flatMap(nbt -> NodeStore.CODEC.parse(NbtOps.INSTANCE, nbt).result());
        check(report, "et se relit avec les deux nodes d'essai",
                () -> decoded.map(copy -> copy.contains(orePos) && copy.contains(flowerPos)).orElse(false));
        check(report, "l'heure de repousse survit", () -> decoded.map(copy -> copy.get(orePos).respawnAt == 4_242L).orElse(false));
        check(report, "l'index par chunk est reconstruit à la relecture",
                () -> decoded.map(copy -> copy.inChunk(orePos.getX() >> 4, orePos.getZ() >> 4).contains(orePos)).orElse(false));
        store.markFull(orePos);
    }

    /** La baguette garde son type, et une baguette vierge n'en a pas. */
    private static void scenarioTool(List<String> report) {
        section(report, "Nodes — baguette");
        check(report, "une baguette liée rend son type", () -> IRON.equals(NodeTool.boundType(NodeTool.create(IRON))));
        check(report, "une baguette vierge n'a pas de type", () -> NodeTool.boundType(new ItemStack(NodeTool.ITEM)) == null);
        ItemStack wand = NodeTool.create(IRON);
        NodeTool.bind(wand, DANDELION);
        check(report, "relier change le type", () -> DANDELION.equals(NodeTool.boundType(wand)));
        check(report, "la baguette ne s'empile pas", () -> wand.getMaxCount() == 1);
    }

    // ------------------------------------------------------------------

    private static ServerPlayerEntity testPlayer(MinecraftServer server, ServerWorld world, String name) {
        // Un FakePlayer : d'autres mods du pack envoient des paquets au joueur qui frappe, mange ou
        // réapparaît (zones musicales, attributs, accessoires) — un joueur en mémoire ordinaire ne peut pas les recevoir.
        return DiagnosticPlayer.create(world, name);
    }

    private static void clear(ServerPlayerEntity player) {
        player.getInventory().getMainStacks().replaceAll(stack -> ItemStack.EMPTY);
    }

    private static int count(ServerPlayerEntity player, net.minecraft.item.Item item) {
        return player.getInventory().count(item);
    }

    private static void section(List<String> report, String title) {
        report.add("--- " + title);
    }

    private static void check(List<String> report, String label, Check condition) {
        boolean ok;
        try {
            ok = condition.test();
        } catch (Exception e) {
            report.add("  FAIL  " + label + "  (" + e + ")");
            return;
        }
        report.add((ok ? "  PASS  " : "  FAIL  ") + label);
    }

    @FunctionalInterface
    private interface Check {
        boolean test() throws Exception;
    }
}
