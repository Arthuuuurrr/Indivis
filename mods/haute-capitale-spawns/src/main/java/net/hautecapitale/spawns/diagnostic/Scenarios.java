package net.hautecapitale.spawns.diagnostic;

import com.mojang.authlib.GameProfile;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import net.hautecapitale.spawns.HauteCapitaleSpawns;
import net.hautecapitale.spawns.api.KillReport;
import net.hautecapitale.spawns.api.OpResult;
import net.hautecapitale.spawns.api.SpawnEvents;
import net.hautecapitale.spawns.api.Spawns;
import net.hautecapitale.spawns.config.SpawnsConfig;
import net.hautecapitale.spawns.data.Leash;
import net.hautecapitale.spawns.data.PointStatus;
import net.hautecapitale.spawns.data.QuestDrop;
import net.hautecapitale.spawns.data.Respawn;
import net.hautecapitale.spawns.data.RespawnConditions;
import net.hautecapitale.spawns.data.SpawnPoint;
import net.hautecapitale.spawns.data.SpawnSettings;
import net.hautecapitale.spawns.data.SpawnZone;
import net.hautecapitale.spawns.engine.ControlledMarker;
import net.hautecapitale.spawns.engine.SpawnEngine;
import net.hautecapitale.spawns.state.PointState;
import net.hautecapitale.spawns.store.PointRef;
import net.hautecapitale.spawns.text.Msg;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.SpawnReason;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.item.Items;
import net.minecraft.network.ClientConnection;
import net.minecraft.network.NetworkSide;
import net.minecraft.network.packet.c2s.common.SyncedClientOptions;
import net.minecraft.network.packet.s2c.play.PositionFlag;
import net.minecraft.registry.Registries;
import net.minecraft.scoreboard.ScoreboardObjective;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.ConnectedClientData;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.Identifier;
import net.minecraft.util.TypeFilter;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.GameMode;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

import static net.minecraft.server.command.CommandManager.argument;
import static net.minecraft.server.command.CommandManager.literal;

/**
 * Banc d'essai en jeu, pilotable par RCON, sans client : un joueur « testeur »
 * est fabrique reellement en ligne (connexion netty en memoire), place pres
 * d'un camp de test, et chaque scenario verifie le comportement du moteur
 * (jamais plus d'un mob par point, reapparition, laisse, evenements, commandes,
 * objets de quete, persistance, charge).
 *
 * <p>Chaque sous-commande imprime ses verifications {@code OK}/{@code KO} et un
 * bilan. Les scenarios a delai s'executent en deux temps (« start » puis
 * « verify »), le temps etant fourni par l'orchestrateur RCON.
 */
public final class Scenarios {

    static final String ZONE = "hct";
    static final String POINT = "archer_01";
    static final String FULL = ZONE + "." + POINT;
    static final Identifier ORC_ARCHER = Identifier.of("autonomous_orc_mobs", "orc_archer");
    static final Identifier FALLBACK = Identifier.of("minecraft", "zombie");
    static final int SITE_X = 1000;
    static final int SITE_Z = 1000;

    private static ServerPlayerEntity tester;
    private static ServerPlayerEntity buddy;
    private static int siteY = -60;

    private static KillReport lastKill;
    private static int killEvents;
    private static int spawnEvents;
    private static int clearedEvents;
    private static String lastClearedZone;
    private static boolean listening;

    private static int ok;
    private static int ko;

    private Scenarios() {
    }

    public static void attach(LiteralArgumentBuilder<ServerCommandSource> root) {
        root.then(literal("setup").executes(ctx -> run(ctx, Scenarios::setup)));
        root.then(literal("cleanup").executes(ctx -> run(ctx, Scenarios::cleanup)));
        root.then(literal("count").executes(ctx -> run(ctx, Scenarios::count)));
        root.then(literal("t1").executes(ctx -> run(ctx, Scenarios::t1)));
        root.then(literal("t2").then(literal("leave").executes(ctx -> run(ctx, Scenarios::t2Leave)))
                .then(literal("check-unloaded").executes(ctx -> run(ctx, Scenarios::t2CheckUnloaded)))
                .then(literal("back").executes(ctx -> run(ctx, Scenarios::t2Back)))
                .then(literal("verify").executes(ctx -> run(ctx, Scenarios::t2Verify))));
        root.then(literal("t3").then(literal("kill").executes(ctx -> run(ctx, Scenarios::t3Kill)))
                .then(literal("verify").executes(ctx -> run(ctx, Scenarios::t3Verify))));
        root.then(literal("t4").then(literal("prepare").executes(ctx -> run(ctx, Scenarios::t4Prepare)))
                .then(literal("verify").executes(ctx -> run(ctx, Scenarios::t4Verify)))
                .then(literal("prepare-alive").executes(ctx -> run(ctx, Scenarios::t4PrepareAlive)))
                .then(literal("verify-alive").executes(ctx -> run(ctx, Scenarios::t4VerifyAlive))));
        root.then(literal("t5").then(literal("pull").executes(ctx -> run(ctx, Scenarios::t5Pull)))
                .then(literal("verify").executes(ctx -> run(ctx, Scenarios::t5Verify))));
        root.then(literal("t6").then(literal("start").executes(ctx -> run(ctx, Scenarios::t6Start)))
                .then(literal("verify").executes(ctx -> run(ctx, Scenarios::t6Verify)))
                .then(literal("killall").executes(ctx -> run(ctx, Scenarios::t6KillAll)))
                .then(literal("cleared").executes(ctx -> run(ctx, Scenarios::t6Cleared))));
        root.then(literal("t7").executes(ctx -> run(ctx, Scenarios::t7)));
        root.then(literal("t8").executes(ctx -> run(ctx, Scenarios::t8)));
        root.then(literal("t9").executes(ctx -> run(ctx, Scenarios::t9)));
        root.then(literal("t10").executes(ctx -> run(ctx, Scenarios::t10)));
        root.then(literal("t11").then(literal("start").executes(ctx -> run(ctx, Scenarios::t11Start)))
                .then(literal("verify").executes(ctx -> run(ctx, Scenarios::t11Verify))));
        root.then(literal("t12").then(literal("start").then(argument("zones", IntegerArgumentType.integer(1, 500))
                        .executes(ctx -> run(ctx, c -> t12Start(c, IntegerArgumentType.getInteger(c, "zones"))))))
                .then(literal("verify").executes(ctx -> run(ctx, Scenarios::t12Verify)))
                .then(literal("cleanup").executes(ctx -> run(ctx, Scenarios::t12Cleanup))));
        root.then(literal("tcamp").then(literal("start").executes(ctx -> run(ctx, Scenarios::tCampStart)))
                .then(literal("verify").executes(ctx -> run(ctx, Scenarios::tCampVerify)))
                .then(literal("leave").executes(ctx -> run(ctx, Scenarios::tCampLeave))));
        root.then(literal("connect").executes(ctx -> run(ctx, Scenarios::connectStep)));
        root.then(literal("t13").executes(ctx -> run(ctx, Scenarios::t13)));
        root.then(literal("t14").executes(ctx -> run(ctx, Scenarios::t14)));
        root.then(literal("cmd").then(argument("line", StringArgumentType.greedyString())
                .executes(ctx -> run(ctx, c -> cmd(c, tester, StringArgumentType.getString(c, "line"))))));
        root.then(literal("cmdbuddy").then(argument("line", StringArgumentType.greedyString())
                .executes(ctx -> run(ctx, c -> cmd(c, buddy, StringArgumentType.getString(c, "line"))))));
    }

    // --- cadre -------------------------------------------------------------------------------

    private interface Step {
        void run(CommandContext<ServerCommandSource> ctx) throws Exception;
    }

    private static int run(CommandContext<ServerCommandSource> ctx, Step step) {
        ok = 0;
        ko = 0;
        listen();
        try {
            step.run(ctx);
        } catch (Throwable t) {
            HauteCapitaleSpawns.LOGGER.error("Scenario en erreur", t);
            fail(ctx, "exception : " + t);
        }
        Msg.feedback(ctx.getSource(), Text.literal("== bilan : " + ok + " OK, " + ko + " KO").formatted(ko == 0 ? Formatting.GREEN : Formatting.RED));
        return ko == 0 ? 1 : 0;
    }

    private static void check(CommandContext<ServerCommandSource> ctx, String label, boolean condition) {
        if (condition) {
            ok++;
            Msg.feedback(ctx.getSource(), Text.literal("OK  " + label).formatted(Formatting.GREEN));
        } else {
            fail(ctx, label);
        }
    }

    private static void fail(CommandContext<ServerCommandSource> ctx, String label) {
        ko++;
        Msg.feedback(ctx.getSource(), Text.literal("KO  " + label).formatted(Formatting.RED));
    }

    private static void note(CommandContext<ServerCommandSource> ctx, String text) {
        Msg.feedback(ctx.getSource(), Text.literal("    " + text).formatted(Formatting.GRAY));
    }

    private static void listen() {
        if (listening) {
            return;
        }
        listening = true;
        SpawnEvents.MOB_KILLED.register((server, report, entity) -> {
            lastKill = report;
            killEvents++;
        });
        SpawnEvents.MOB_SPAWNED.register((server, mob, entity, respawn) -> spawnEvents++);
        SpawnEvents.ZONE_CLEARED.register((server, zone, last) -> {
            clearedEvents++;
            lastClearedZone = zone;
        });
    }

    private static SpawnEngine engine(CommandContext<ServerCommandSource> ctx) {
        return SpawnEngine.get().orElseThrow(() -> new IllegalStateException("moteur arrete"));
    }

    private static Identifier entityId() {
        return Registries.ENTITY_TYPE.containsId(ORC_ARCHER) ? ORC_ARCHER : FALLBACK;
    }

    private static ServerPlayerEntity connect(MinecraftServer server, String name, double x, double y, double z) {
        ServerWorld overworld = server.getOverworld();
        GameProfile profile = new GameProfile(UUID.nameUUIDFromBytes(("hct:" + name).getBytes(StandardCharsets.UTF_8)), name);
        ServerPlayerEntity player = new ServerPlayerEntity(server, overworld, profile, SyncedClientOptions.createDefault());
        player.refreshPositionAndAngles(x, y, z, 0.0F, 0.0F);
        ClientConnection conn = new ClientConnection(NetworkSide.SERVERBOUND);
        new io.netty.channel.embedded.EmbeddedChannel(conn);
        server.getPlayerManager().onPlayerConnect(conn, player, ConnectedClientData.createDefault(profile, false));
        player.changeGameMode(GameMode.CREATIVE);
        player.setInvulnerable(true);
        move(player, x, y, z);
        return player;
    }

    /**
     * Teleporte un joueur fabrique ET deplace ses tickets de chunks : sans client,
     * aucun paquet de confirmation n'arrive, et c'est ce paquet qui, en jeu, fait
     * suivre les tickets ({@code ServerChunkManager.updatePosition}).
     */
    private static void move(ServerPlayerEntity player, double x, double y, double z) {
        ServerWorld world = player.getEntityWorld();
        player.teleport(world, x, y, z, Set.<PositionFlag>of(), 0.0F, 0.0F, false);
        world.getChunkManager().updatePosition(player);
    }

    private static void disconnect(MinecraftServer server, ServerPlayerEntity player) {
        if (player != null) {
            try {
                server.getPlayerManager().remove(player);
            } catch (Throwable ignored) {
            }
        }
    }

    private static int surfaceY(ServerWorld world, int x, int z) {
        world.getChunk(x >> 4, z >> 4);
        BlockPos.Mutable pos = new BlockPos.Mutable(x, 120, z);
        for (int y = 120; y > world.getBottomY(); y--) {
            pos.setY(y);
            if (!world.getBlockState(pos).isAir()) {
                return y + 1;
            }
        }
        return 64;
    }

    /** Les entites controlees vivantes portant une etiquette donnee (parcours global : test seulement). */
    private static List<LivingEntity> tagged(ServerWorld world, String tag) {
        List<LivingEntity> out = new ArrayList<>();
        for (LivingEntity e : world.getEntitiesByType(TypeFilter.instanceOf(LivingEntity.class), e -> e.isAlive() && e.getCommandTags().contains(tag))) {
            out.add(e);
        }
        return out;
    }

    private static List<LivingEntity> ofPoint(ServerWorld world, String fullId) {
        return tagged(world, Spawns.TAG_POINT_PREFIX + fullId);
    }

    private static SpawnSettings testSettings() {
        return SpawnSettings.ofEntity(entityId())
                .withRespawn(Optional.of(Respawn.random(3, 4)))
                .withLeash(Optional.of(new Leash(20, 3, true, true, 12, 1.2D)))
                .withWanderRadius(Optional.of(4))
                .withActivationRadius(Optional.of(96))
                .withRank(Optional.of("elite"))
                .withTag("mob_type", "orc").withTag("role", "archer")
                .withConditions(Optional.of(new RespawnConditions(true, 6)));
    }

    private static void killByTester(ServerWorld world, LivingEntity entity) {
        entity.damage(world, world.getDamageSources().playerAttack(tester), 100000.0F);
    }

    // --- scenarios ---------------------------------------------------------------------------

    private static void setup(CommandContext<ServerCommandSource> ctx) {
        SpawnEngine engine = engine(ctx);
        MinecraftServer server = engine.server();
        ServerWorld world = server.getOverworld();
        siteY = surfaceY(world, SITE_X, SITE_Z);
        cleanupZones(engine);
        if (tester == null || tester.isRemoved()) {
            tester = connect(server, "hct_tester", SITE_X + 12, siteY, SITE_Z);
        } else {
            move(tester, SITE_X + 12, siteY, SITE_Z);
        }
        SpawnPoint point = SpawnPoint.at(POINT, SITE_X + 0.5D, siteY, SITE_Z + 0.5D, 90.0F, 0.0F, testSettings());
        engine.registry().putZone(SpawnZone.create(ZONE, world.getRegistryKey().getValue()).withPoint(point));
        engine.state(FULL);
        killEvents = 0;
        spawnEvents = 0;
        clearedEvents = 0;
        lastKill = null;
        check(ctx, "testeur en ligne a 12 blocs du point (y=" + siteY + ")", server.getPlayerManager().getPlayer(tester.getUuid()) != null);
        check(ctx, "point " + FULL + " cree avec " + entityId(), engine.registry().ref(FULL).map(PointRef::isValid).orElse(false));
        note(ctx, "attendre ~3 s puis /mmospawn-diagnostic t1");
    }

    /** (Re)connecte le testeur au site sans toucher aux zones : pour les tests de redemarrage. */
    private static void connectStep(CommandContext<ServerCommandSource> ctx) {
        SpawnEngine engine = engine(ctx);
        MinecraftServer server = engine.server();
        ServerWorld world = server.getOverworld();
        siteY = surfaceY(world, SITE_X, SITE_Z);
        if (tester == null || tester.isRemoved() || server.getPlayerManager().getPlayer(tester.getUuid()) == null) {
            tester = connect(server, "hct_tester", SITE_X + 12, siteY, SITE_Z);
        } else {
            move(tester, SITE_X + 12, siteY, SITE_Z);
        }
        check(ctx, "testeur en ligne a 12 blocs du point", server.getPlayerManager().getPlayer(tester.getUuid()) != null);
        note(ctx, "zone " + ZONE + " : " + (engine.registry().zone(ZONE).isPresent() ? "presente" : "absente") + " ; attendre ~8 s avant une verification");
    }

    private static void cleanupZones(SpawnEngine engine) {
        for (String zone : new ArrayList<>(engine.registry().zoneIds())) {
            if (zone.startsWith(ZONE)) {
                engine.deleteZone(zone);
            }
        }
    }

    private static void cleanup(CommandContext<ServerCommandSource> ctx) {
        SpawnEngine engine = engine(ctx);
        cleanupZones(engine);
        ServerWorld world = engine.server().getOverworld();
        int leftovers = tagged(world, Spawns.TAG).size();
        for (LivingEntity e : tagged(world, Spawns.TAG)) {
            e.discard();
        }
        disconnect(engine.server(), tester);
        disconnect(engine.server(), buddy);
        tester = null;
        buddy = null;
        check(ctx, "zones de test supprimees", engine.registry().zoneIds().stream().noneMatch(z -> z.startsWith(ZONE)));
        note(ctx, leftovers + " entite(s) controlee(s) residuelle(s) retiree(s)");
    }

    private static void count(CommandContext<ServerCommandSource> ctx) {
        SpawnEngine engine = engine(ctx);
        ServerWorld world = engine.server().getOverworld();
        note(ctx, "controlees chargees : " + tagged(world, Spawns.TAG).size() + " ; point " + FULL + " : " + ofPoint(world, FULL).size());
        engine.store().get(FULL).ifPresent(s -> note(ctx, "etat " + s + " respawn dans " + s.remainingSeconds(System.currentTimeMillis()) + " s, apparitions=" + s.spawnCount()));
        note(ctx, engine.stats());
        ok++;
    }

    /** TEST 1 : un seul point, jamais plus d'un mob. */
    private static void t1(CommandContext<ServerCommandSource> ctx) {
        SpawnEngine engine = engine(ctx);
        ServerWorld world = engine.server().getOverworld();
        PointState state = engine.state(FULL);
        List<LivingEntity> mobs = ofPoint(world, FULL);
        check(ctx, "etat ALIVE", state.status() == PointStatus.ALIVE);
        check(ctx, "exactement 1 mob pour le point (trouve " + mobs.size() + ")", mobs.size() == 1);
        check(ctx, "l'UUID du mob = celui de l'etat", mobs.size() == 1 && state.entityUuid().map(mobs.get(0).getUuid()::equals).orElse(false));
        check(ctx, "marque persistante avec le bon jeton", mobs.size() == 1 && ControlledMarker.of(mobs.get(0)).map(m -> m.fullId().equals(FULL) && state.token().map(m.token()::equals).orElse(false)).orElse(false));
        check(ctx, "mob persistant (hors plafond naturel)", mobs.size() == 1 && mobs.get(0) instanceof MobEntity mob && mob.isPersistent());
        check(ctx, "etiquettes hcspawn.*", mobs.size() == 1 && mobs.get(0).getCommandTags().containsAll(List.of(Spawns.TAG, Spawns.TAG_ZONE_PREFIX + ZONE, Spawns.TAG_RANK_PREFIX + "elite", Spawns.TAG_TAG_PREFIX + "role.archer", "hcm_origine_spawn_mmo")));
        check(ctx, "une seule apparition comptee (" + state.spawnCount() + ")", state.spawnCount() == 1);
        check(ctx, "Spawns.of(entite) rend l'identite", mobs.size() == 1 && Spawns.of(mobs.get(0)).map(m -> m.fullId().equals(FULL) && m.hasTag("mob_type", "orc") && m.isRank("elite")).orElse(false));
        check(ctx, "evenement MOB_SPAWNED recu", spawnEvents >= 1);
    }

    /** TEST 2 : le joueur part (chunk decharge) puis revient : aucun doublon. */
    private static void t2Leave(CommandContext<ServerCommandSource> ctx) {
        SpawnEngine engine = engine(ctx);
        ServerWorld world = engine.server().getOverworld();
        PointState state = engine.state(FULL);
        check(ctx, "avant depart : ALIVE, 1 mob", state.status() == PointStatus.ALIVE && ofPoint(world, FULL).size() == 1);
        move(tester, SITE_X + 6000, siteY, SITE_Z + 6000);
        check(ctx, "testeur teleporte a 6000 blocs", tester.getX() > SITE_X + 5000);
        note(ctx, "attendre ~15 s (dechargement) puis t2 check-unloaded");
    }

    private static void t2CheckUnloaded(CommandContext<ServerCommandSource> ctx) {
        SpawnEngine engine = engine(ctx);
        ServerWorld world = engine.server().getOverworld();
        PointRef ref = engine.registry().ref(FULL).orElseThrow();
        boolean ticking = world.shouldTickEntityAt(ref.homePos());
        check(ctx, "chunk du point inactif (entites non tickees)", !ticking);
        PointState state = engine.state(FULL);
        check(ctx, "etat toujours ALIVE (pas declare mort)", state.status() == PointStatus.ALIVE);
        check(ctx, "mob non charge (introuvable)", engine.loadedEntity(state).isEmpty());
        check(ctx, "aucun spawn supplementaire (apparitions=" + state.spawnCount() + ")", state.spawnCount() == 1);
    }

    private static void t2Back(CommandContext<ServerCommandSource> ctx) {
        SpawnEngine engine = engine(ctx);
        ServerWorld world = engine.server().getOverworld();
        move(tester, SITE_X + 12, siteY, SITE_Z);
        check(ctx, "testeur de retour", Math.abs(tester.getX() - (SITE_X + 12)) < 1);
        note(ctx, "attendre ~8 s puis t2 verify");
    }

    private static void t2Verify(CommandContext<ServerCommandSource> ctx) {
        SpawnEngine engine = engine(ctx);
        ServerWorld world = engine.server().getOverworld();
        PointState state = engine.state(FULL);
        List<LivingEntity> mobs = ofPoint(world, FULL);
        check(ctx, "1 mob apres retour (trouve " + mobs.size() + ")", mobs.size() == 1);
        check(ctx, "etat ALIVE", state.status() == PointStatus.ALIVE);
        check(ctx, "meme entite qu'avant (aucune nouvelle apparition, apparitions=" + state.spawnCount() + ")", state.spawnCount() == 1);
        check(ctx, "entite rechargee reconnue (UUID = etat)", mobs.size() == 1 && state.entityUuid().map(mobs.get(0).getUuid()::equals).orElse(false));
        check(ctx, "zone de marche re-appliquee", mobs.size() == 1 && mobs.get(0) instanceof MobEntity mob && mob.hasPositionTarget());
    }

    /** TEST 3 : mort -> timer -> reapparition, sans doublon. */
    private static void t3Kill(CommandContext<ServerCommandSource> ctx) {
        SpawnEngine engine = engine(ctx);
        ServerWorld world = engine.server().getOverworld();
        List<LivingEntity> mobs = ofPoint(world, FULL);
        check(ctx, "1 mob a tuer", mobs.size() == 1);
        if (mobs.isEmpty()) {
            return;
        }
        int before = killEvents;
        killByTester(world, mobs.get(0));
        PointState state = engine.state(FULL);
        check(ctx, "etat DEAD_WAITING juste apres la mort", state.status() == PointStatus.DEAD_WAITING);
        long remaining = state.remainingSeconds(System.currentTimeMillis());
        check(ctx, "timer entre 3 et 4 s (" + remaining + ")", remaining >= 2 && remaining <= 4);
        check(ctx, "evenement MOB_KILLED emis", killEvents == before + 1);
        check(ctx, "dernier tueur = testeur", state.lastKillerName().map("hct_tester"::equals).orElse(false));
        note(ctx, "attendre ~6 s puis t3 verify");
    }

    private static void t3Verify(CommandContext<ServerCommandSource> ctx) {
        SpawnEngine engine = engine(ctx);
        ServerWorld world = engine.server().getOverworld();
        PointState state = engine.state(FULL);
        List<LivingEntity> mobs = ofPoint(world, FULL);
        check(ctx, "reapparu : ALIVE", state.status() == PointStatus.ALIVE);
        check(ctx, "exactement 1 mob (trouve " + mobs.size() + ")", mobs.size() == 1);
        check(ctx, "2 apparitions au total (" + state.spawnCount() + ")", state.spawnCount() == 2);
        check(ctx, "1 mort comptee", state.killCount() >= 1);
    }

    /** TEST 4 : redemarrage pendant le timer. */
    private static Path t4File(MinecraftServer server) {
        return HauteCapitaleSpawns.dataRoot().resolve("diagnostic-t4.txt");
    }

    private static void t4Prepare(CommandContext<ServerCommandSource> ctx) throws IOException {
        SpawnEngine engine = engine(ctx);
        ServerWorld world = engine.server().getOverworld();
        PointRef ref = engine.registry().ref(FULL).orElseThrow();
        engine.registry().putZone(ref.zone().withPoint(ref.point().withSettings(ref.point().settings().withRespawn(Optional.of(Respawn.fixed(90))))));
        List<LivingEntity> mobs = ofPoint(world, FULL);
        check(ctx, "1 mob a tuer", mobs.size() == 1);
        if (mobs.isEmpty()) {
            return;
        }
        killByTester(world, mobs.get(0));
        PointState state = engine.state(FULL);
        check(ctx, "DEAD_WAITING avec ~90 s", state.status() == PointStatus.DEAD_WAITING && state.remainingSeconds(System.currentTimeMillis()) >= 85);
        Files.writeString(t4File(engine.server()), state.respawnAtMillis() + "\n" + state.spawnCount() + "\n", StandardCharsets.UTF_8);
        engine.server().getPlayerManager().saveAllPlayerData();
        engine.server().getOverworld().getPersistentStateManager().save();
        note(ctx, "respawn_at=" + state.respawnAtMillis() + " ecrit ; arreter le serveur maintenant, redemarrer, puis t4 verify");
    }

    private static void t4Verify(CommandContext<ServerCommandSource> ctx) throws IOException {
        SpawnEngine engine = engine(ctx);
        ServerWorld world = engine.server().getOverworld();
        String[] lines = Files.readString(t4File(engine.server()), StandardCharsets.UTF_8).split("\n");
        long expectedAt = Long.parseLong(lines[0].trim());
        int expectedSpawns = Integer.parseInt(lines[1].trim());
        PointState state = engine.state(FULL);
        long now = System.currentTimeMillis();
        boolean stillWaiting = now < expectedAt;
        if (stillWaiting) {
            check(ctx, "apres redemarrage : DEAD_WAITING", state.status() == PointStatus.DEAD_WAITING);
            check(ctx, "respawn_at identique (" + state.respawnAtMillis() + ")", state.respawnAtMillis() == expectedAt);
            check(ctx, "aucune apparition pendant l'attente", state.spawnCount() == expectedSpawns);
            check(ctx, "aucun mob charge pour le point", ofPoint(world, FULL).isEmpty());
        } else {
            check(ctx, "delai ecoule pendant l'arret : reapparu une seule fois", state.status() == PointStatus.ALIVE && state.spawnCount() == expectedSpawns + 1);
            check(ctx, "1 mob", ofPoint(world, FULL).size() == 1);
        }
        check(ctx, "testeur reconnecte par le scenario", tester != null && engine.server().getPlayerManager().getPlayer(tester.getUuid()) != null);
    }

    private static void t4PrepareAlive(CommandContext<ServerCommandSource> ctx) throws IOException {
        SpawnEngine engine = engine(ctx);
        ServerWorld world = engine.server().getOverworld();
        PointState state = engine.state(FULL);
        List<LivingEntity> mobs = ofPoint(world, FULL);
        check(ctx, "ALIVE avec 1 mob avant arret", state.status() == PointStatus.ALIVE && mobs.size() == 1);
        Files.writeString(t4File(engine.server()), "alive\n" + state.entityUuid().map(UUID::toString).orElse("?") + "\n" + state.spawnCount() + "\n", StandardCharsets.UTF_8);
        engine.server().getOverworld().getPersistentStateManager().save();
        note(ctx, "uuid=" + state.entityUuid().orElse(null) + " ; arreter le serveur, redemarrer, setup-less : t4 verify-alive");
    }

    private static void t4VerifyAlive(CommandContext<ServerCommandSource> ctx) throws IOException {
        SpawnEngine engine = engine(ctx);
        ServerWorld world = engine.server().getOverworld();
        String[] lines = Files.readString(t4File(engine.server()), StandardCharsets.UTF_8).split("\n");
        UUID expected = UUID.fromString(lines[1].trim());
        int expectedSpawns = Integer.parseInt(lines[2].trim());
        PointState state = engine.state(FULL);
        List<LivingEntity> mobs = ofPoint(world, FULL);
        check(ctx, "apres redemarrage : 1 mob (trouve " + mobs.size() + ")", mobs.size() == 1);
        check(ctx, "meme UUID qu'avant l'arret", mobs.size() == 1 && mobs.get(0).getUuid().equals(expected));
        check(ctx, "etat ALIVE, aucune apparition en plus (" + state.spawnCount() + ")", state.status() == PointStatus.ALIVE && state.spawnCount() == expectedSpawns);
    }

    /** TEST 5 : laisse. */
    private static void t5Pull(CommandContext<ServerCommandSource> ctx) {
        SpawnEngine engine = engine(ctx);
        ServerWorld world = engine.server().getOverworld();
        List<LivingEntity> mobs = ofPoint(world, FULL);
        check(ctx, "1 mob a attirer", mobs.size() == 1);
        if (mobs.isEmpty()) {
            return;
        }
        LivingEntity mob = mobs.get(0);
        mob.refreshPositionAndAngles(SITE_X + 40.5D, siteY, SITE_Z + 0.5D, 0.0F, 0.0F);
        if (mob instanceof MobEntity m) {
            m.setTarget(tester);
        }
        double d = Math.sqrt(mob.squaredDistanceTo(SITE_X + 0.5D, siteY, SITE_Z + 0.5D));
        check(ctx, "mob deplace a ~40 blocs (laisse 20) : " + String.format("%.1f", d), d > 30);
        mob.setHealth(Math.max(1.0F, mob.getMaxHealth() / 2));
        note(ctx, "vie reduite a " + mob.getHealth() + "/" + mob.getMaxHealth() + " ; attendre ~8 s puis t5 verify");
    }

    private static void t5Verify(CommandContext<ServerCommandSource> ctx) {
        SpawnEngine engine = engine(ctx);
        ServerWorld world = engine.server().getOverworld();
        List<LivingEntity> mobs = ofPoint(world, FULL);
        check(ctx, "toujours 1 mob", mobs.size() == 1);
        if (mobs.isEmpty()) {
            return;
        }
        LivingEntity mob = mobs.get(0);
        double d = Math.sqrt(mob.squaredDistanceTo(SITE_X + 0.5D, mob.getY(), SITE_Z + 0.5D));
        check(ctx, "retour declenche (retours=" + engine.leash().returnsStarted() + ")", engine.leash().returnsStarted() >= 1);
        check(ctx, "mob revenu pres de son point (" + String.format("%.1f", d) + " blocs)", d <= 6.0D);
        check(ctx, "cible lachee", !(mob instanceof MobEntity m) || m.getTarget() == null);
        check(ctx, "vie restauree (" + mob.getHealth() + "/" + mob.getMaxHealth() + ")", mob.getHealth() >= mob.getMaxHealth() - 0.01F);
        check(ctx, "plus en retour", !engine.leash().isReturning(mob.getUuid()));
    }

    /** TEST 6 : 10 points -> exactement 10 mobs ; puis zone nettoyee. */
    private static final String ZONE6 = ZONE + "6";

    private static void t6Start(CommandContext<ServerCommandSource> ctx) {
        SpawnEngine engine = engine(ctx);
        ServerWorld world = engine.server().getOverworld();
        SpawnZone zone = SpawnZone.create(ZONE6, world.getRegistryKey().getValue())
                .withDefaults(testSettings().withRespawn(Optional.of(Respawn.fixed(600))).withConditions(Optional.of(new RespawnConditions(true, 0))));
        for (int i = 0; i < 10; i++) {
            zone = zone.withPoint(SpawnPoint.at("w" + i, SITE_X + 20 + (i % 5) * 3 + 0.5D, siteY, SITE_Z + 20 + (i / 5) * 3 + 0.5D, 0.0F, 0.0F, SpawnSettings.EMPTY));
        }
        engine.registry().putZone(zone);
        clearedEvents = 0;
        check(ctx, "zone " + ZONE6 + " avec 10 points", engine.registry().refsOfZone(ZONE6).size() == 10);
        note(ctx, "attendre ~6 s puis t6 verify");
    }

    private static void t6Verify(CommandContext<ServerCommandSource> ctx) {
        SpawnEngine engine = engine(ctx);
        ServerWorld world = engine.server().getOverworld();
        List<LivingEntity> mobs = tagged(world, Spawns.TAG_ZONE_PREFIX + ZONE6);
        check(ctx, "exactement 10 mobs dans la zone (trouve " + mobs.size() + ")", mobs.size() == 10);
        int alive = 0;
        for (PointRef ref : engine.registry().refsOfZone(ZONE6)) {
            PointState s = engine.state(ref.fullId());
            if (s.status() == PointStatus.ALIVE && ofPoint(world, ref.fullId()).size() == 1) {
                alive++;
            }
        }
        check(ctx, "un mob par point (" + alive + "/10)", alive == 10);
        check(ctx, "zone pas nettoyee", !engine.isZoneCleared(ZONE6));
    }

    private static void t6KillAll(CommandContext<ServerCommandSource> ctx) {
        SpawnEngine engine = engine(ctx);
        ServerWorld world = engine.server().getOverworld();
        List<LivingEntity> mobs = tagged(world, Spawns.TAG_ZONE_PREFIX + ZONE6);
        int before = clearedEvents;
        for (int i = 0; i < mobs.size(); i++) {
            killByTester(world, mobs.get(i));
            if (i == 4) {
                check(ctx, "apres 5 morts : pas de ZONE_CLEARED", clearedEvents == before);
            }
        }
        check(ctx, "10 mobs tues", mobs.size() == 10);
        check(ctx, "ZONE_CLEARED emis une fois (" + (clearedEvents - before) + ")", clearedEvents == before + 1 && ZONE6.equals(lastClearedZone));
        check(ctx, "isZoneCleared vrai", engine.isZoneCleared(ZONE6));
        for (LivingEntity e : mobs) {
            if (e.isAlive()) {
                killByTester(world, e);
            }
        }
        check(ctx, "toujours une seule emission apres re-verification", clearedEvents == before + 1);
    }

    private static void t6Cleared(CommandContext<ServerCommandSource> ctx) {
        SpawnEngine engine = engine(ctx);
        OpResult r = engine.deleteZone(ZONE6);
        check(ctx, "zone " + ZONE6 + " supprimee", r.ok() && engine.registry().zone(ZONE6).isEmpty());
    }

    /** TEST 7 / 8 : un mob du meme type, naturel ou de spawner, n'est jamais adopte. */
    private static void t7(CommandContext<ServerCommandSource> ctx) {
        foreign(ctx, SpawnReason.NATURAL, "naturel");
    }

    private static void t8(CommandContext<ServerCommandSource> ctx) {
        foreign(ctx, SpawnReason.SPAWNER, "de spawner (donjon)");
    }

    private static void foreign(CommandContext<ServerCommandSource> ctx, SpawnReason reason, String label) {
        SpawnEngine engine = engine(ctx);
        ServerWorld world = engine.server().getOverworld();
        PointState state = engine.state(FULL);
        int spawnsBefore = state.spawnCount();
        UUID ours = state.entityUuid().orElse(null);
        EntityType<?> type = Registries.ENTITY_TYPE.get(entityId());
        Entity foreign = type.spawn(world, BlockPos.ofFloored(SITE_X + 0.5D, siteY, SITE_Z + 0.5D), reason);
        check(ctx, "mob " + label + " cree sur le point", foreign instanceof LivingEntity && foreign.isAlive());
        if (!(foreign instanceof LivingEntity living)) {
            return;
        }
        check(ctx, "sans marque de controle", !ControlledMarker.isControlled(living) && Spawns.of(living).isEmpty());
        check(ctx, "le point garde son propre mob (UUID inchange)", state.entityUuid().map(u -> u.equals(ours)).orElse(false));
        check(ctx, "1 seul mob controle pour le point", ofPoint(world, FULL).size() == 1);
        int killsBefore = killEvents;
        killByTester(world, living);
        check(ctx, "sa mort n'emet pas MOB_KILLED", killEvents == killsBefore);
        check(ctx, "etat du point intact (ALIVE, apparitions=" + state.spawnCount() + ")", state.status() == PointStatus.ALIVE && state.spawnCount() == spawnsBefore);
    }

    /** TEST 9 : contenu de l'evenement de mort. */
    private static void t9(CommandContext<ServerCommandSource> ctx) {
        SpawnEngine engine = engine(ctx);
        ServerWorld world = engine.server().getOverworld();
        List<LivingEntity> mobs = ofPoint(world, FULL);
        check(ctx, "1 mob a tuer", mobs.size() == 1);
        if (mobs.isEmpty()) {
            return;
        }
        LivingEntity mob = mobs.get(0);
        // deux coups du testeur puis le coup fatal : participation + tueur
        mob.damage(world, world.getDamageSources().playerAttack(tester), 1.0F);
        lastKill = null;
        killByTester(world, mob);
        KillReport r = lastKill;
        check(ctx, "rapport recu", r != null);
        if (r == null) {
            return;
        }
        check(ctx, "zone/point/id", r.mob().zoneId().equals(ZONE) && r.mob().pointId().equals(POINT) && r.mob().fullId().equals(FULL));
        check(ctx, "type d'entite", r.mob().entityType().equals(entityId()));
        check(ctx, "rang elite", r.mob().isRank("elite"));
        check(ctx, "tags mob_type=orc role=archer", r.mob().hasTag("mob_type", "orc") && r.mob().hasTag("role", "archer"));
        check(ctx, "tueur = testeur", r.killedBy(tester.getUuid()) && r.killerName().map("hct_tester"::equals).orElse(false));
        check(ctx, "participants contiennent le testeur", r.participants().contains(tester.getUuid()));
        check(ctx, "credites contiennent le testeur", r.credits(tester.getUuid()));
        check(ctx, "position du kill pres du point", r.position().squaredDistanceTo(SITE_X + 0.5D, siteY, SITE_Z + 0.5D) < 30 * 30);
        check(ctx, "dimension overworld", r.mob().dimension().equals(world.getRegistryKey()));
        check(ctx, "UUID de l'entite", r.entityUuid().equals(mob.getUuid()));
        note(ctx, "attendre ~6 s (reapparition) avant la suite");
    }

    /** TEST 10 : activation/desactivation par commande (Easy NPC) et par API. */
    private static void t10(CommandContext<ServerCommandSource> ctx) throws Exception {
        SpawnEngine engine = engine(ctx);
        MinecraftServer server = engine.server();
        ServerWorld world = server.getOverworld();
        PointState state = engine.state(FULL);
        check(ctx, "depart : ALIVE avec 1 mob", state.status() == PointStatus.ALIVE && ofPoint(world, FULL).size() == 1);
        int r1 = exec(server, "mmospawn disable " + FULL);
        check(ctx, "commande disable reussie", r1 == 1);
        check(ctx, "etat DISABLED et mob retire", state.status() == PointStatus.DISABLED && ofPoint(world, FULL).isEmpty());
        check(ctx, "query disabled = succes", exec(server, "mmospawn query " + FULL + " disabled") == 1);
        check(ctx, "query alive = echec", exec(server, "mmospawn query " + FULL + " alive") == 0);
        check(ctx, "point.enabled=false ecrit dans le registre", engine.registry().ref(FULL).map(ref -> !ref.point().enabled()).orElse(false));
        int r2 = exec(server, "mmospawn enable " + FULL);
        check(ctx, "commande enable reussie", r2 == 1);
        check(ctx, "etat READY (apparition au prochain passage)", state.status() == PointStatus.READY);
        OpResult z1 = Spawns.disableZone(ZONE);
        check(ctx, "API disableZone", z1.ok() && !Spawns.isZoneEnabled(ZONE) && state.status() == PointStatus.DISABLED);
        OpResult z2 = Spawns.enableZone(ZONE);
        check(ctx, "API enableZone", z2.ok() && Spawns.isZoneEnabled(ZONE) && state.status() == PointStatus.READY);
        check(ctx, "API sur point inconnu = echec propre", !Spawns.enablePoint("nope.nothing").ok());
        check(ctx, "commande sur point inconnu = echec", exec(server, "mmospawn enable nope.nothing") == 0);
        OpResult forced = Spawns.spawnNow(FULL);
        check(ctx, "API spawnNow : " + forced.message(), forced.ok() && state.status() == PointStatus.ALIVE && ofPoint(world, FULL).size() == 1);
        OpResult again = Spawns.spawnNow(FULL);
        check(ctx, "spawnNow refuse un second mob", !again.ok() && ofPoint(world, FULL).size() == 1);
        OpResult reset = Spawns.resetPoint(FULL);
        check(ctx, "API resetPoint : mob retire, READY", reset.ok() && state.status() == PointStatus.READY && ofPoint(world, FULL).isEmpty());
        note(ctx, "attendre ~3 s : le point doit reapparaitre seul");
    }

    private static int exec(MinecraftServer server, String command) {
        try {
            return server.getCommandManager().getDispatcher().execute(command, server.getCommandSource().withSilent());
        } catch (Exception e) {
            return 0;
        }
    }

    /** TEST 11 : objets de quete personnels + compteur de scoreboard + groupe. */
    private static void t11Start(CommandContext<ServerCommandSource> ctx) {
        SpawnEngine engine = engine(ctx);
        ServerWorld world = engine.server().getOverworld();
        PointRef ref = engine.registry().ref(FULL).orElseThrow();
        SpawnSettings s = ref.point().settings()
                .withDrops(Optional.of(List.of(new QuestDrop(Identifier.of("minecraft", "bone"), 2, 100.0D, true),
                        new QuestDrop(Identifier.of("minecraft", "stick"), 1, 100.0D, false))))
                .withCounters(Optional.of(List.of("hct_kills")));
        engine.registry().putZone(ref.zone().withPoint(ref.point().withSettings(s)));
        if (buddy == null || buddy.isRemoved()) {
            buddy = connect(engine.server(), "hct_buddy", SITE_X + 15, siteY, SITE_Z + 3);
        }
        tester.getInventory().clear();
        buddy.getInventory().clear();
        ScoreboardObjective objective = engine.server().getScoreboard().getNullableObjective("hct_kills");
        if (objective != null) {
            engine.server().getScoreboard().getOrCreateScore(tester, objective).setScore(0);
            engine.server().getScoreboard().getOrCreateScore(buddy, objective).setScore(0);
        }
        check(ctx, "objets de quete et compteur configures", engine.registry().ref(FULL).map(r -> r.resolved().get().drops().size() == 2 && r.resolved().get().counters().contains("hct_kills")).orElse(false));
        check(ctx, "second joueur (hct_buddy) en ligne a 15 blocs", engine.server().getPlayerManager().getPlayer(buddy.getUuid()) != null);
        note(ctx, "si le mod de groupe est present : /party ... via cmd ; puis t11 verify");
    }

    private static void t11Verify(CommandContext<ServerCommandSource> ctx) {
        SpawnEngine engine = engine(ctx);
        ServerWorld world = engine.server().getOverworld();
        List<LivingEntity> mobs = ofPoint(world, FULL);
        check(ctx, "1 mob a tuer", mobs.size() == 1);
        if (mobs.isEmpty()) {
            return;
        }
        lastKill = null;
        killByTester(world, mobs.get(0));
        KillReport r = lastKill;
        check(ctx, "rapport recu avec 2 objets de quete declares", r != null && r.drops().size() == 2);
        int bones = tester.getInventory().count(Items.BONE);
        check(ctx, "testeur a recu 2 os (personnel, 100 %) : " + bones, bones == 2);
        ScoreboardObjective objective = engine.server().getScoreboard().getNullableObjective("hct_kills");
        int score = objective == null ? -1 : engine.server().getScoreboard().getOrCreateScore(tester, objective).getScore();
        check(ctx, "objectif hct_kills cree et testeur = 1 (" + score + ")", score == 1);
        boolean inParty = r != null && r.credits(buddy.getUuid());
        int buddyBones = buddy.getInventory().count(Items.BONE);
        int buddyScore = objective == null ? -1 : engine.server().getScoreboard().getOrCreateScore(buddy, objective).getScore();
        if (inParty) {
            check(ctx, "camarade de groupe credite : 2 os et compteur 1 (" + buddyBones + ", " + buddyScore + ")", buddyBones == 2 && buddyScore == 1);
        } else {
            check(ctx, "joueur hors groupe non credite : 0 os, compteur 0 (" + buddyBones + ", " + buddyScore + ")", buddyBones == 0 && buddyScore <= 0);
        }
        int sticks = world.getEntitiesByType(TypeFilter.instanceOf(net.minecraft.entity.ItemEntity.class), e -> e.getStack().isOf(Items.STICK)).size();
        check(ctx, "1 baton au sol (drop non personnel) : " + sticks, sticks >= 1);
        note(ctx, "credites : " + (r == null ? "?" : r.credited().size()) + " ; groupe present : " + net.hautecapitale.spawns.bridge.PartyBridge.available());
    }

    /** TEST 12 : des milliers de points loin des joueurs. */
    private static void t12Start(CommandContext<ServerCommandSource> ctx, int zones) {
        SpawnEngine engine = engine(ctx);
        ServerWorld world = engine.server().getOverworld();
        long t0 = System.nanoTime();
        for (int z = 0; z < zones; z++) {
            SpawnZone zone = SpawnZone.create(ZONE + "12_" + z, world.getRegistryKey().getValue()).withDefaults(testSettings());
            List<SpawnPoint> points = new ArrayList<>(100);
            for (int p = 0; p < 100; p++) {
                points.add(SpawnPoint.at("p" + p, 30000 + z * 400 + (p % 10) * 8 + 0.5D, siteY, 30000 + (p / 10) * 8 + 0.5D, 0.0F, 0.0F, SpawnSettings.EMPTY));
            }
            engine.registry().putZoneInMemory(zone.withPoints(points));
        }
        double ms = (System.nanoTime() - t0) / 1_000_000.0D;
        engine.resetStats();
        check(ctx, zones * 100 + " points enregistres (" + engine.registry().pointCount() + ") en " + String.format("%.0f", ms) + " ms", engine.registry().pointCount() >= zones * 100);
        note(ctx, "attendre ~10 s puis t12 verify");
    }

    private static void t12Verify(CommandContext<ServerCommandSource> ctx) {
        SpawnEngine engine = engine(ctx);
        String stats = engine.stats();
        note(ctx, stats);
        check(ctx, "passe max < 5 ms (" + String.format("%.3f", engine.maxPassMillis()) + ")", engine.maxPassMillis() < 5.0D);
        check(ctx, "seuls les points proches sont actifs (" + engine.lastActivePoints() + ")", engine.lastActivePoints() <= 12);
        ServerWorld world = engine.server().getOverworld();
        check(ctx, "aucun mob des zones lointaines charge", tagged(world, Spawns.TAG_ZONE_PREFIX + ZONE + "12_0").isEmpty());
    }

    private static void t12Cleanup(CommandContext<ServerCommandSource> ctx) {
        SpawnEngine engine = engine(ctx);
        int removed = 0;
        for (String zone : new ArrayList<>(engine.registry().zoneIds())) {
            if (zone.startsWith(ZONE + "12_")) {
                engine.registry().removeZone(zone);
                removed++;
            }
        }
        for (PointState s : new ArrayList<>(engine.store().all())) {
            if (s.fullId().startsWith(ZONE + "12_")) {
                engine.store().remove(s.fullId());
            }
        }
        check(ctx, removed + " zones de charge retirees", engine.registry().zoneIds().stream().noneMatch(z -> z.startsWith(ZONE + "12_")));
    }

    /** Condition de reapparition : un joueur qui campe sur le point bloque la reapparition. */
    private static void tCampStart(CommandContext<ServerCommandSource> ctx) {
        SpawnEngine engine = engine(ctx);
        ServerWorld world = engine.server().getOverworld();
        List<LivingEntity> mobs = ofPoint(world, FULL);
        check(ctx, "1 mob a tuer", mobs.size() == 1);
        if (mobs.isEmpty()) {
            return;
        }
        killByTester(world, mobs.get(0));
        move(tester, SITE_X + 1.5D, siteY, SITE_Z + 0.5D);
        check(ctx, "testeur campe a 1 bloc du point", tester.squaredDistanceTo(SITE_X + 0.5D, siteY, SITE_Z + 0.5D) < 4);
        note(ctx, "attendre ~8 s (timer 3-4 s ecoule) puis tcamp verify");
    }

    private static void tCampVerify(CommandContext<ServerCommandSource> ctx) {
        SpawnEngine engine = engine(ctx);
        ServerWorld world = engine.server().getOverworld();
        PointState state = engine.state(FULL);
        check(ctx, "timer ecoule mais pas de mob (joueur trop pres) : " + state.status(), state.status() == PointStatus.READY && ofPoint(world, FULL).isEmpty());
    }

    private static void tCampLeave(CommandContext<ServerCommandSource> ctx) {
        SpawnEngine engine = engine(ctx);
        ServerWorld world = engine.server().getOverworld();
        move(tester, SITE_X + 12, siteY, SITE_Z);
        check(ctx, "testeur recule a 12 blocs", tester.getX() > SITE_X + 10);
        note(ctx, "attendre ~3 s puis count : le mob doit etre reapparu");
    }

    /** TEST 13 : niveau (PV x n) et attributs imposes, a l'apparition puis en direct sur le mob vivant. */
    private static void t13(CommandContext<ServerCommandSource> ctx) {
        SpawnEngine engine = engine(ctx);
        MinecraftServer server = engine.server();
        ServerWorld world = server.getOverworld();
        List<LivingEntity> before = ofPoint(world, FULL);
        check(ctx, "1 mob de reference vivant", before.size() == 1);
        if (before.isEmpty()) {
            return;
        }
        float baseMax = before.get(0).getMaxHealth();
        note(ctx, "PV max de base = " + baseMax);
        // niveau 3 + degats imposes, puis reset pour une apparition neuve
        check(ctx, "edit level 3", exec(server, "mmospawn edit " + FULL + " level 3") == 1);
        check(ctx, "edit attribute attack_damage 21", exec(server, "mmospawn edit " + FULL + " attribute attack_damage 21") == 1);
        // le mob vivant doit deja avoir change (application en direct)
        LivingEntity live = ofPoint(world, FULL).get(0);
        check(ctx, "mob vivant : PV max triples en direct (" + live.getMaxHealth() + ")", Math.abs(live.getMaxHealth() - baseMax * 3) < 0.01F);
        check(ctx, "mob vivant : etiquette hcspawn.level.3", live.getCommandTags().contains(Spawns.TAG + ".level.3") && !live.getCommandTags().contains(Spawns.TAG + ".level.1"));
        check(ctx, "reset", Spawns.resetPoint(FULL).ok() && Spawns.spawnNow(FULL).ok());
        List<LivingEntity> after = ofPoint(world, FULL);
        check(ctx, "1 mob reapparu", after.size() == 1);
        if (after.isEmpty()) {
            return;
        }
        LivingEntity mob = after.get(0);
        check(ctx, "PV max = base x3 (" + mob.getMaxHealth() + " pour " + baseMax + ")", Math.abs(mob.getMaxHealth() - baseMax * 3) < 0.01F);
        check(ctx, "PV courants au maximum", Math.abs(mob.getHealth() - mob.getMaxHealth()) < 0.01F);
        double attack = mob.getAttributeValue(net.minecraft.entity.attribute.EntityAttributes.ATTACK_DAMAGE);
        check(ctx, "degats d'attaque imposes = 21 (" + attack + ")", Math.abs(attack - 21.0D) < 0.01D);
        check(ctx, "identite : niveau 3", Spawns.of(mob).map(m -> m.level() == 3).orElse(false));
        // retour au niveau 1 en direct : PV max de base, vie ajustee en proportion
        mob.setHealth(mob.getMaxHealth() / 2);
        check(ctx, "edit level 1", exec(server, "mmospawn edit " + FULL + " level 1") == 1);
        check(ctx, "PV max de retour a la base (" + mob.getMaxHealth() + ")", Math.abs(mob.getMaxHealth() - baseMax) < 0.01F);
        check(ctx, "vie conservee en proportion (moitie : " + mob.getHealth() + ")", Math.abs(mob.getHealth() - baseMax / 2) < 0.01F);
        check(ctx, "edit clear attributes", exec(server, "mmospawn edit " + FULL + " clear attributes") == 1);
        note(ctx, "attaque apres clear (valeur de base conservee jusqu'a la prochaine apparition) = " + mob.getAttributeValue(net.minecraft.entity.attribute.EntityAttributes.ATTACK_DAMAGE));
    }

    /** TEST 14 : renommer le mob en direct, puis retirer le nom. */
    private static void t14(CommandContext<ServerCommandSource> ctx) {
        SpawnEngine engine = engine(ctx);
        MinecraftServer server = engine.server();
        ServerWorld world = server.getOverworld();
        List<LivingEntity> mobs = ofPoint(world, FULL);
        check(ctx, "1 mob vivant", mobs.size() == 1);
        if (mobs.isEmpty()) {
            return;
        }
        LivingEntity mob = mobs.get(0);
        check(ctx, "edit name Chef Grakk", exec(server, "mmospawn edit " + FULL + " name Chef Grakk") == 1);
        check(ctx, "mob vivant renomme en direct (" + (mob.getCustomName() == null ? "aucun" : mob.getCustomName().getString()) + ")",
                mob.getCustomName() != null && mob.getCustomName().getString().equals("Chef Grakk"));
        check(ctx, "nom visible au-dessus de la tete", mob.isCustomNameVisible());
        check(ctx, "reset + spawn", Spawns.resetPoint(FULL).ok() && Spawns.spawnNow(FULL).ok());
        LivingEntity fresh = ofPoint(world, FULL).get(0);
        check(ctx, "nouveau mob nomme des l'apparition", fresh.getCustomName() != null && fresh.getCustomName().getString().equals("Chef Grakk") && fresh.isCustomNameVisible());
        check(ctx, "edit name clear", exec(server, "mmospawn edit " + FULL + " name clear") == 1);
        check(ctx, "nom retire en direct", fresh.getCustomName() == null && !fresh.isCustomNameVisible());
    }

    private static void cmd(CommandContext<ServerCommandSource> ctx, ServerPlayerEntity as, String line) {
        MinecraftServer server = engine(ctx).server();
        ServerCommandSource source = as != null ? as.getCommandSource() : server.getCommandSource();
        try {
            int result = server.getCommandManager().getDispatcher().execute(line, source);
            check(ctx, "« " + line + " » = " + result, true);
        } catch (Exception e) {
            fail(ctx, "« " + line + " » : " + e.getMessage());
        }
    }
}
