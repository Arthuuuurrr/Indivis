package net.hautecapitale.spawns;

import com.google.gson.JsonElement;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.JsonOps;
import net.hautecapitale.spawns.command.SettingsEditor;
import net.hautecapitale.spawns.config.SpawnsConfig;
import net.hautecapitale.spawns.data.CreditMode;
import net.hautecapitale.spawns.data.Ids;
import net.hautecapitale.spawns.data.Leash;
import net.hautecapitale.spawns.data.PointStatus;
import net.hautecapitale.spawns.data.QuestDrop;
import net.hautecapitale.spawns.data.Resolved;
import net.hautecapitale.spawns.data.Respawn;
import net.hautecapitale.spawns.data.RespawnConditions;
import net.hautecapitale.spawns.data.SpawnPoint;
import net.hautecapitale.spawns.data.SpawnProfile;
import net.hautecapitale.spawns.data.SpawnSettings;
import net.hautecapitale.spawns.data.SpawnZone;
import net.hautecapitale.spawns.engine.ControlledMarker;
import net.hautecapitale.spawns.state.PointState;
import net.hautecapitale.spawns.state.SpawnStateStore;
import net.hautecapitale.spawns.store.PointRef;
import net.hautecapitale.spawns.store.SpawnRegistry;
import net.minecraft.Bootstrap;
import net.minecraft.SharedConstants;
import net.minecraft.nbt.NbtElement;
import net.minecraft.nbt.NbtOps;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Random;
import java.util.UUID;

/**
 * Harnais hors jeu : codecs (JSON et NBT), resolution des reglages, timers,
 * machine a etats, index spatial, grammaire de {@code set}.
 */
public final class SpawnsTest {

    private static int passed;
    private static int failed;

    public static void main(String[] args) throws Exception {
        // Pas de Bootstrap : les registres ne se chargent pas hors du runtime Loom
        // (IllegalAccessError sur RegistryEntry$Reference). Les verifications de
        // registre de l'editeur sont remplacees par une regle simple.
        SharedConstants.createGameVersion();
        SpawnsConfig.set(new SpawnsConfig());
        SettingsEditor.entityCheck = id -> !id.getNamespace().equals("nope");
        SettingsEditor.itemCheck = id -> !id.getNamespace().equals("nope");
        SettingsEditor.attributeCheck = id -> !id.getNamespace().equals("nope");

        ids();
        respawn();
        leashAndConditions();
        settingsOverlay();
        settingsCodec();
        zoneCodec();
        resolution();
        pointState();
        stateStoreCodec();
        markerCodec();
        chunkKeys();
        registryIndex();
        registryFiles();
        editor();

        System.out.println();
        System.out.println("SpawnsTest : " + passed + " OK, " + failed + " KO");
        if (failed > 0) {
            System.exit(1);
        }
    }

    // --- outils -------------------------------------------------------------------------

    private static void check(String label, boolean condition) {
        if (condition) {
            passed++;
        } else {
            failed++;
            System.out.println("KO  " + label);
        }
    }

    private static void eq(String label, Object expected, Object actual) {
        check(label + " (attendu " + expected + ", obtenu " + actual + ")", expected == null ? actual == null : expected.equals(actual));
    }

    private static <T> T roundTripJson(String label, Codec<T> codec, T value) {
        DataResult<JsonElement> encoded = codec.encodeStart(JsonOps.INSTANCE, value);
        check(label + " encode JSON", encoded.result().isPresent());
        T decoded = codec.parse(JsonOps.INSTANCE, encoded.result().orElseThrow()).result().orElse(null);
        eq(label + " JSON aller-retour", value, decoded);
        return decoded;
    }

    private static <T> T roundTripNbt(String label, Codec<T> codec, T value) {
        DataResult<NbtElement> encoded = codec.encodeStart(NbtOps.INSTANCE, value);
        check(label + " encode NBT" + encoded.error().map(e -> " : " + e.message()).orElse(""), encoded.result().isPresent());
        T decoded = codec.parse(NbtOps.INSTANCE, encoded.result().orElseThrow()).result().orElse(null);
        eq(label + " NBT aller-retour", value, decoded);
        return decoded;
    }

    private static Identifier id(String s) {
        return Identifier.of(s);
    }

    // --- tests --------------------------------------------------------------------------

    private static void ids() {
        check("id simple", Ids.isSimple("orc_camp_01") && Ids.isSimple("a-b") && Ids.isSimple("x"));
        check("id simple refuse majuscules/points/espaces", !Ids.isSimple("Orc") && !Ids.isSimple("a.b") && !Ids.isSimple("a b") && !Ids.isSimple(""));
        eq("full", "orc_camp_01.archer_01", Ids.full("orc_camp_01", "archer_01"));
        check("split ok", Ids.split("orc_camp_01.archer_01").map(p -> p[0].equals("orc_camp_01") && p[1].equals("archer_01")).orElse(false));
        check("split refuse", Ids.split("a").isEmpty() && Ids.split("a.").isEmpty() && Ids.split(".b").isEmpty() && Ids.split("a.b.c").isEmpty() && Ids.split(null).isEmpty());
    }

    private static void respawn() {
        Random random = new Random(42);
        Respawn fixed = Respawn.fixed(240);
        for (int i = 0; i < 50; i++) {
            check("fixe = 240", fixed.roll(random) == 240);
        }
        Respawn rnd = Respawn.random(240, 300);
        boolean sawMin = false;
        boolean sawMax = false;
        for (int i = 0; i < 2000; i++) {
            int v = rnd.roll(random);
            check("aleatoire dans [240;300]", v >= 240 && v <= 300);
            sawMin |= v == 240;
            sawMax |= v == 300;
        }
        check("bornes atteintes", sawMin && sawMax);
        eq("random inverse normalise", Respawn.random(300, 240), new Respawn(Respawn.Mode.RANDOM, 300, 300));
        eq("negatif borne a 0", Respawn.fixed(-5).minSeconds(), 0);
        roundTripJson("respawn fixe", Respawn.CODEC, fixed);
        roundTripJson("respawn aleatoire", Respawn.CODEC, rnd);
        roundTripNbt("respawn NBT", Respawn.CODEC, rnd);
        Respawn parsed = Respawn.CODEC.parse(JsonOps.INSTANCE, com.google.gson.JsonParser.parseString("{\"min_seconds\":480}")).result().orElse(null);
        check("respawn minimal = aleatoire 480-480", parsed != null && parsed.mode() == Respawn.Mode.RANDOM && parsed.minSeconds() == 480 && parsed.maxSeconds() == 480);
        eq("describe", "240-300 s", rnd.describe());
    }

    private static void leashAndConditions() {
        Leash leash = Leash.of(35);
        roundTripJson("leash", Leash.CODEC, leash);
        roundTripNbt("leash NBT", Leash.CODEC, leash.withHeal(false).withTimeout(45));
        check("leash desactivee a 0", !Leash.of(0).isEnabled() && leash.isEnabled());
        Leash parsed = Leash.CODEC.parse(JsonOps.INSTANCE, com.google.gson.JsonParser.parseString("{\"radius\":20}")).result().orElse(null);
        check("leash minimal = valeurs par defaut", parsed != null && parsed.returnRadius() == 3 && parsed.healOnReturn() && parsed.invulnerableWhileReturning() && parsed.timeoutSeconds() == 30);
        roundTripJson("conditions", RespawnConditions.CODEC, new RespawnConditions(false, 12));
        roundTripJson("quest drop", QuestDrop.CODEC, new QuestDrop(id("minecraft:bone"), 2, 35.0D, true));
    }

    private static SpawnSettings full() {
        return new SpawnSettings(Optional.of(id("minecraft:skeleton")), Optional.of(Respawn.random(240, 300)), Optional.of(Leash.of(35)),
                Optional.of(96), Optional.of(10), Optional.of("elite"), Map.of("mob_type", "skeleton", "role", "archer"),
                Optional.of(List.of(new QuestDrop(id("minecraft:bone"), 1, 35.0D, true), new QuestDrop(id("minecraft:stick"), 3, 100.0D, false))),
                Optional.of(List.of("skeleton_kills", "ruins_kills")), Optional.of(new RespawnConditions(true, 8)),
                Optional.of("{Health:40f}"), Optional.of("Archer maudit"), Optional.of(false), Optional.of(CreditMode.PARTICIPANTS),
                Optional.of(3), Map.of("minecraft:attack_damage", 12.0D, "minecraft:max_health", 80.0D));
    }

    private static void settingsOverlay() {
        SpawnSettings base = full();
        SpawnSettings over = SpawnSettings.EMPTY.withRank(Optional.of("mini_boss")).withTag("role", "chief").withTag("zone_kind", "camp")
                .withLeash(Optional.of(Leash.of(60)));
        SpawnSettings merged = base.overlay(over);
        eq("overlay : rang surcharge", "mini_boss", merged.rank().orElse(null));
        eq("overlay : entite heritee", id("minecraft:skeleton"), merged.entity().orElse(null));
        eq("overlay : laisse surchargee", 60, merged.leash().map(Leash::radius).orElse(-1));
        eq("overlay : respawn herite", Respawn.random(240, 300), merged.respawn().orElse(null));
        eq("overlay : tags fusionnes", Map.of("mob_type", "skeleton", "role", "chief", "zone_kind", "camp"), merged.tags());
        check("overlay EMPTY neutre", base.overlay(SpawnSettings.EMPTY).equals(base) && SpawnSettings.EMPTY.overlay(base).equals(base));
        check("isEmpty", SpawnSettings.EMPTY.isEmpty() && !base.isEmpty());
        check("withoutTag", !base.withoutTag("role").tags().containsKey("role"));
    }

    private static void settingsCodec() {
        roundTripJson("settings complets", SpawnSettings.CODEC, full());
        roundTripJson("settings vides", SpawnSettings.CODEC, SpawnSettings.EMPTY);
        roundTripNbt("settings NBT", SpawnSettings.CODEC, full());
        JsonElement encodedEmpty = SpawnSettings.CODEC.encodeStart(JsonOps.INSTANCE, SpawnSettings.EMPTY).result().orElseThrow();
        eq("settings vides = objet JSON vide", "{}", encodedEmpty.toString());
        SpawnSettings parsed = SpawnSettings.CODEC.parse(JsonOps.INSTANCE, com.google.gson.JsonParser.parseString(
                "{\"entity\":\"minecraft:zombie\",\"respawn\":{\"mode\":\"fixed\",\"min_seconds\":120},\"tags\":{\"mob_type\":\"undead\"}}")).result().orElse(null);
        check("settings partiels lus", parsed != null && parsed.entity().get().equals(id("minecraft:zombie")) && parsed.respawn().get().equals(Respawn.fixed(120))
                && parsed.tags().get("mob_type").equals("undead") && parsed.leash().isEmpty());
        DataResult<SpawnSettings> bad = SpawnSettings.CODEC.parse(JsonOps.INSTANCE, com.google.gson.JsonParser.parseString("{\"entity\":\"MAJUSCULE:x\"}"));
        check("identifiant d'entite invalide refuse", bad.result().isEmpty());
    }

    private static SpawnZone sampleZone() {
        SpawnPoint a = SpawnPoint.at("archer_01", 100.5D, 64.0D, -200.5D, 90.0F, 0.0F, SpawnSettings.ofEntity(id("minecraft:skeleton")));
        SpawnPoint b = SpawnPoint.at("chief", 110.0D, 65.0D, -210.0D, 180.0F, 5.0F, SpawnSettings.EMPTY.withRank(Optional.of("mini_boss")))
                .withProfile(Optional.of("skeleton_chief")).withEnabled(false);
        return new SpawnZone("ruins_02", Optional.of("Ruines du nord"), SpawnZone.OVERWORLD, true,
                SpawnSettings.EMPTY.withEntity(Optional.of(id("minecraft:zombie"))).withTag("zone_kind", "ruins"), List.of(a, b));
    }

    private static void zoneCodec() {
        SpawnZone zone = sampleZone();
        roundTripJson("zone", SpawnZone.CODEC, zone);
        roundTripNbt("zone NBT", SpawnZone.CODEC, zone);
        roundTripJson("point", SpawnPoint.CODEC, zone.points().get(0));
        JsonElement encoded = SpawnZone.CODEC.encodeStart(JsonOps.INSTANCE, zone).result().orElseThrow();
        String json = encoded.toString();
        check("settings du point inlines (pas de sous-objet settings)", json.contains("\"entity\":\"minecraft:skeleton\"") && !json.contains("\"settings\""));
        check("champs optionnels absents quand par defaut", !json.contains("\"enabled\":true") || json.contains("\"enabled\":false"));
        SpawnZone minimal = SpawnZone.CODEC.parse(JsonOps.INSTANCE, com.google.gson.JsonParser.parseString("{\"id\":\"x\"}")).result().orElse(null);
        check("zone minimale", minimal != null && minimal.enabled() && minimal.dimension().equals(SpawnZone.OVERWORLD) && minimal.points().isEmpty());
        SpawnZone edited = zone.withPoint(zone.points().get(0).withPosition(1, 2, 3, 0, 0)).withoutPoint("chief");
        check("withPoint remplace, withoutPoint retire", edited.points().size() == 1 && edited.points().get(0).x() == 1.0D);
        check("withPoint ajoute", zone.withPoint(SpawnPoint.at("new", 0, 0, 0, 0, 0, SpawnSettings.EMPTY)).points().size() == 3);
    }

    private static void resolution() {
        SpawnSettings base = SpawnsConfig.get().baseSettings();
        check("base complete sauf entite", base.entity().isEmpty() && base.respawn().isPresent() && base.leash().isPresent() && base.activationRadius().isPresent()
                && base.wanderRadius().isPresent() && base.rank().isPresent() && base.conditions().isPresent() && base.creditMode().isPresent());
        Resolved.Result none = Resolved.of(base, SpawnSettings.EMPTY, SpawnSettings.EMPTY, SpawnSettings.EMPTY);
        check("sans entite : invalide avec probleme", !none.ok() && none.problems().size() == 1);

        SpawnSettings zone = SpawnSettings.EMPTY.withEntity(Optional.of(id("minecraft:zombie"))).withRespawn(Optional.of(Respawn.fixed(600))).withTag("zone_kind", "camp");
        SpawnSettings profile = SpawnSettings.EMPTY.withEntity(Optional.of(id("minecraft:skeleton"))).withLeash(Optional.of(Leash.of(50))).withRank(Optional.of("elite")).withTag("role", "archer");
        SpawnSettings point = SpawnSettings.EMPTY.withRank(Optional.of("mini_boss")).withTag("role", "chief");
        Resolved r = Resolved.of(base, zone, profile, point).resolved().orElseThrow();
        eq("precedence entite : profil > zone", id("minecraft:skeleton"), r.entity());
        eq("precedence respawn : zone > config", Respawn.fixed(600), r.respawn());
        eq("precedence laisse : profil > config", 50, r.leash().radius());
        eq("precedence rang : point > profil", "mini_boss", r.rank());
        eq("tags fusionnes sur 3 niveaux", Map.of("zone_kind", "camp", "role", "chief"), r.tags());
        eq("activation par defaut", SpawnsConfig.get().defaultActivationRadius, r.activationRadius());
        eq("credit par defaut", CreditMode.PARTY_NEARBY, r.creditMode());
        check("initialize par defaut vrai", r.initialize());
        Resolved small = Resolved.of(base, SpawnSettings.ofEntity(id("minecraft:pig")).withActivationRadius(Optional.of(4))).resolved().orElseThrow();
        eq("activation plancher 16", 16, small.activationRadius());
    }

    private static void pointState() {
        PointState state = new PointState("z.p");
        eq("etat initial READY", PointStatus.READY, state.status());
        UUID entity = UUID.randomUUID();
        UUID token = UUID.randomUUID();
        state.markAlive(entity, token, new BlockPos(1, 2, 3));
        check("ALIVE avec entite et jeton", state.status() == PointStatus.ALIVE && state.entityUuid().get().equals(entity) && state.token().get().equals(token) && state.spawnCount() == 1);
        long t0 = 1_000_000L;
        state.markDead(t0, 240, "Koa", true);
        check("DEAD_WAITING sans entite", state.status() == PointStatus.DEAD_WAITING && state.entityUuid().isEmpty() && state.token().isEmpty() && state.killCount() == 1);
        eq("timer 240 s", 240L, state.remainingSeconds(t0));
        eq("timer apres 100 s", 140L, state.remainingSeconds(t0 + 100_000L));
        eq("timer arrondi au superieur", 1L, state.remainingSeconds(t0 + 239_001L));
        check("timer pas ecoule", !state.advanceTimer(t0 + 239_999L) && state.status() == PointStatus.DEAD_WAITING);
        check("timer ecoule -> READY", state.advanceTimer(t0 + 240_000L) && state.status() == PointStatus.READY);
        eq("timer sans objet = 0", 0L, state.remainingSeconds(t0));
        eq("dernier tueur", "Koa", state.lastKillerName().orElse(null));
        state.markDead(t0, 30, null, false);
        check("perte : pas un kill", state.killCount() == 1 && state.lastKillerName().get().equals("Koa"));
        state.markDisabled();
        check("DISABLED", state.status() == PointStatus.DISABLED && state.respawnAtMillis() == 0L);
        eq("unresolved compte", 3, thrice(state));
        state.seen(new BlockPos(0, 0, 0));
        eq("seen remet a 0", 0, state.unresolvedChecks());
        UUID other = UUID.randomUUID();
        state.adopt(other, token, new BlockPos(5, 5, 5));
        check("adopt -> ALIVE sans compter une apparition", state.status() == PointStatus.ALIVE && state.entityUuid().get().equals(other) && state.spawnCount() == 1);
    }

    private static int thrice(PointState state) {
        state.unresolved();
        state.unresolved();
        return state.unresolved();
    }

    private static void stateStoreCodec() {
        PointState a = new PointState("orc_camp_01.archer_01");
        a.markAlive(UUID.randomUUID(), UUID.randomUUID(), new BlockPos(10, 64, -20));
        PointState b = new PointState("orc_camp_01.chief");
        b.markDead(1_700_000_000_000L, 900, "Koa", true);
        PointState c = new PointState("ruins.s1");
        c.markDisabled();
        roundTripNbt("saved ALIVE", PointState.Saved.CODEC, a.toSaved());
        roundTripNbt("saved DEAD", PointState.Saved.CODEC, b.toSaved());
        roundTripJson("saved DISABLED", PointState.Saved.CODEC, c.toSaved());
        PointState back = PointState.fromSaved(b.toSaved());
        check("fromSaved conserve timer et tueur", back.respawnAtMillis() == b.respawnAtMillis() && back.lastKillerName().get().equals("Koa") && back.killCount() == 1);
        List<PointState.Saved> list = List.of(a.toSaved(), b.toSaved(), c.toSaved());
        DataResult<NbtElement> encoded = SpawnStateStore.codec().encodeStart(NbtOps.INSTANCE, SpawnStateStore.empty()).map(x -> x);
        check("store vide encode", encoded.result().isPresent());
        NbtElement listNbt = PointState.Saved.CODEC.listOf().encodeStart(NbtOps.INSTANCE, list).result().orElseThrow();
        SpawnStateStore store = SpawnStateStore.codec().parse(NbtOps.INSTANCE, listNbt).result().orElse(null);
        check("store relu : 3 etats", store != null && store.size() == 3 && store.get("orc_camp_01.chief").map(s -> s.status() == PointStatus.DEAD_WAITING).orElse(false));
        NbtElement reencoded = SpawnStateStore.codec().encodeStart(NbtOps.INSTANCE, store).result().orElseThrow();
        SpawnStateStore again = SpawnStateStore.codec().parse(NbtOps.INSTANCE, reencoded).result().orElseThrow();
        eq("store double aller-retour", 3, again.size());
    }

    private static void markerCodec() {
        ControlledMarker marker = new ControlledMarker("orc_camp_01", "archer_01", UUID.randomUUID());
        roundTripNbt("marker", ControlledMarker.CODEC, marker);
        eq("marker fullId", "orc_camp_01.archer_01", marker.fullId());
    }

    private static void chunkKeys() {
        eq("chunkX positif", 6, SpawnPoint.at("p", 100.5D, 0, 0, 0, 0, SpawnSettings.EMPTY).chunkX());
        eq("chunkZ negatif", -13, SpawnPoint.at("p", 0, 0, -200.5D, 0, 0, SpawnSettings.EMPTY).chunkZ());
        eq("chunkX -0.5 -> -1", -1, SpawnPoint.at("p", -0.5D, 0, 0, 0, 0, SpawnSettings.EMPTY).chunkX());
        check("cles distinctes", PointRef.chunkKey(1, 2) != PointRef.chunkKey(2, 1) && PointRef.chunkKey(-1, 0) != PointRef.chunkKey(0, -1));
        // ChunkPos charge les registres (ChunkGenerationSteps) : la formule vanilla est
        // recopiee ici, `(x & 0xFFFFFFFFL) | ((z & 0xFFFFFFFFL) << 32)`.
        eq("cle = formule ChunkPos.toLong", (-7L & 0xFFFFFFFFL) | ((12L & 0xFFFFFFFFL) << 32), PointRef.chunkKey(-7, 12));
        eq("cle = formule ChunkPos.toLong (positifs)", 3L | (5L << 32), PointRef.chunkKey(3, 5));
    }

    private static void registryIndex() {
        SpawnRegistry registry = new SpawnRegistry(Path.of("build", "test-registry-unused"));
        registry.putProfileInMemory(new SpawnProfile("skeleton_chief", SpawnSettings.ofEntity(id("minecraft:wither_skeleton")).withLeash(Optional.of(Leash.of(60))).withActivationRadius(Optional.of(200))));
        registry.putZoneInMemory(sampleZone());
        eq("2 points indexes", 2, registry.pointCount());
        PointRef archer = registry.ref("ruins_02.archer_01").orElseThrow();
        PointRef chief = registry.ref("ruins_02.chief").orElseThrow();
        check("archer valide, entite du point", archer.isValid() && archer.resolved().get().entity().equals(id("minecraft:skeleton")));
        check("chief : entite du profil, laisse du profil, rang du point", chief.isValid() && chief.resolved().get().entity().equals(id("minecraft:wither_skeleton"))
                && chief.resolved().get().leash().radius() == 60 && chief.resolved().get().rank().equals("mini_boss"));
        check("chief desactive", !chief.effectivelyEnabled() && archer.effectivelyEnabled());
        eq("tag de zone herite", "ruins", chief.resolved().get().tags().get("zone_kind"));
        eq("rayon max = 200 (profil)", 200, registry.maxActivationRadius());
        eq("refsOfZone", 2, registry.refsOfZone("ruins_02").size());
        eq("refsInChunk archer", 1, registry.refsInChunk(SpawnZone.OVERWORLD, PointRef.chunkKey(6, -13)).size());
        eq("refsInChunk chief", 1, registry.refsInChunk(SpawnZone.OVERWORLD, PointRef.chunkKey(6, -14)).size());
        eq("refsInChunk vide", 0, registry.refsInChunk(SpawnZone.OVERWORLD, PointRef.chunkKey(0, 0)).size());
        eq("refsInChunk autre dimension", 0, registry.refsInChunk(id("minecraft:the_nether"), PointRef.chunkKey(6, -13)).size());
        check("hasPointsIn", registry.hasPointsIn(SpawnZone.OVERWORLD) && !registry.hasPointsIn(id("minecraft:the_end")));

        SpawnZone broken = SpawnZone.create("broken", SpawnZone.OVERWORLD).withPoint(SpawnPoint.at("nothing", 0, 0, 0, 0, 0, SpawnSettings.EMPTY))
                .withPoint(SpawnPoint.at("ghost", 0, 0, 0, 0, 0, SpawnSettings.EMPTY).withProfile(Optional.of("missing")));
        registry.putZoneInMemory(broken);
        eq("2 points invalides", 2, registry.invalidPointCount());
        check("probleme explicite", registry.ref("broken.ghost").get().problems().stream().anyMatch(p -> p.contains("missing")));
        eq("rayon max plafonne", Math.min(200, SpawnsConfig.get().maxActivationRadius), registry.maxActivationRadius());
    }

    private static void registryFiles() throws Exception {
        Path root = Files.createTempDirectory("hcspawns-test");
        SpawnRegistry registry = new SpawnRegistry(root);
        var report = registry.reload();
        check("dossier vide : 0 zone, 0 erreur", report.ok() && report.zones == 0 && Files.isDirectory(root.resolve("zones")) && Files.isDirectory(root.resolve("profiles")));
        registry.putProfile(new SpawnProfile("skeleton_chief", SpawnSettings.ofEntity(id("minecraft:wither_skeleton"))));
        registry.putZone(sampleZone());
        check("fichiers ecrits", Files.exists(root.resolve("zones/ruins_02.json")) && Files.exists(root.resolve("profiles/skeleton_chief.json")));
        SpawnRegistry fresh = new SpawnRegistry(root);
        var report2 = fresh.reload();
        check("relecture : 1 zone, 2 points, 1 profil, 0 erreur", report2.ok() && report2.zones == 1 && report2.points == 2 && report2.profiles == 1);
        eq("zone relue identique", sampleZone(), fresh.zone("ruins_02").orElse(null));
        Files.writeString(root.resolve("zones/bad.json"), "{ \"id\": \"bad\", \"points\": [ { \"id\": \"p\" } ] }");
        Files.writeString(root.resolve("zones/Broken Name.json"), "{}");
        Files.writeString(root.resolve("zones/syntax.json"), "{ not json");
        Files.writeString(root.resolve("zones/dup.json"), "{ \"id\": \"other\", \"points\": [ { \"id\": \"a\", \"x\":0, \"y\":0, \"z\":0 }, { \"id\": \"a\", \"x\":1, \"y\":0, \"z\":0 } ] }");
        var report3 = fresh.reload();
        check("3 erreurs (point sans x, nom de fichier, JSON casse) + 1 doublon", report3.errors.size() == 4);
        check("avertissement id != nom de fichier", report3.warnings.stream().anyMatch(w -> w.contains("dup") && w.contains("other")));
        check("zone dup gardee avec 1 point", fresh.zone("dup").map(z -> z.points().size() == 1).orElse(false));
        check("zone valide toujours la", fresh.zone("ruins_02").isPresent());
        check("removeZone", fresh.removeZone("dup") && !Files.exists(root.resolve("zones/dup.json")) && fresh.zone("dup").isEmpty());
        check("removeProfile", fresh.removeProfile("skeleton_chief") && !Files.exists(root.resolve("profiles/skeleton_chief.json")));
        // Un profil manquant ne casse pas le point (la zone fournit l'entite) mais il est signale.
        check("point orphelin de profil : valide mais signale", fresh.ref("ruins_02.chief")
                .map(r -> r.isValid() && r.problems().stream().anyMatch(p -> p.contains("skeleton_chief"))).orElse(false));
    }

    private static void editor() {
        SpawnsConfig config = SpawnsConfig.get();
        SpawnSettings s = SpawnSettings.EMPTY;
        s = apply(s, "entity minecraft:skeleton");
        eq("editor entity", id("minecraft:skeleton"), s.entity().orElse(null));
        check("editor entity inconnue refusee", !SettingsEditor.apply(s, List.of("entity", "nope:nothing"), config).ok());
        s = apply(s, "respawn 240 300");
        eq("editor respawn aleatoire", Respawn.random(240, 300), s.respawn().orElse(null));
        s = apply(s, "respawn 120");
        eq("editor respawn fixe", Respawn.fixed(120), s.respawn().orElse(null));
        s = apply(s, "respawn fixed 90");
        eq("editor respawn fixed mot-cle", Respawn.fixed(90), s.respawn().orElse(null));
        check("editor respawn max<min refuse", !SettingsEditor.apply(s, List.of("respawn", "300", "240"), config).ok());
        s = apply(s, "leash 35");
        eq("editor leash", 35, s.leash().map(Leash::radius).orElse(-1));
        s = apply(s, "leash_heal false");
        check("editor leash_heal garde le rayon", s.leash().get().radius() == 35 && !s.leash().get().healOnReturn());
        s = apply(s, "rank elite");
        eq("editor rank", "elite", s.rank().orElse(null));
        check("editor rank inconnu refuse", !SettingsEditor.apply(s, List.of("rank", "dieu"), config).ok());
        s = apply(s, "tag mob_type orc");
        s = apply(s, "tag role archer");
        eq("editor tags", Map.of("mob_type", "orc", "role", "archer"), s.tags());
        s = apply(s, "untag role");
        eq("editor untag", Map.of("mob_type", "orc"), s.tags());
        s = apply(s, "name Archer de la Falaise");
        eq("editor name multi-mots", "Archer de la Falaise", s.customName().orElse(null));
        s = apply(s, "name clear");
        check("editor name clear", s.customName().isEmpty());
        s = apply(s, "min_player_distance 12");
        eq("editor min_player_distance", 12, s.conditions().map(RespawnConditions::minPlayerDistance).orElse(-1));
        s = apply(s, "credit killer");
        eq("editor credit", CreditMode.KILLER, s.creditMode().orElse(null));
        s = apply(s, "drop add minecraft:bone 2 35 personal");
        s = apply(s, "drop add minecraft:stick 1 100% ground");
        check("editor drops", s.drops().get().size() == 2 && s.drops().get().get(0).personal() && !s.drops().get().get(1).personal() && s.drops().get().get(0).chancePercent() == 35.0D);
        s = apply(s, "drop add minecraft:bone 1 50");
        check("editor drop add remplace le meme objet", s.drops().get().size() == 2 && s.drops().get().stream().filter(d -> d.item().equals(id("minecraft:bone"))).findFirst().get().chancePercent() == 50.0D);
        check("editor drop objet inconnu refuse", !SettingsEditor.apply(s, List.of("drop", "add", "nope:thing"), config).ok());
        s = apply(s, "drop remove minecraft:stick");
        eq("editor drop remove", 1, s.drops().get().size());
        s = apply(s, "counter add orc_kills");
        s = apply(s, "counter add orc_kills");
        eq("editor counter sans doublon", List.of("orc_kills"), s.counters().orElse(null));
        s = apply(s, "nbt {Health:40f}");
        eq("editor nbt", "{Health:40f}", s.nbt().orElse(null));
        check("editor nbt non compose refuse", !SettingsEditor.apply(s, List.of("nbt", "Health:40f"), config).ok());
        s = apply(s, "wander 12");
        s = apply(s, "activation 120");
        s = apply(s, "initialize false");
        check("editor wander/activation/initialize", s.wanderRadius().get() == 12 && s.activationRadius().get() == 120 && !s.initialize().get());
        check("editor activation hors bornes refuse", !SettingsEditor.apply(s, List.of("activation", "5"), config).ok());
        s = apply(s, "level 3");
        eq("editor level", 3, s.level().orElse(-1));
        check("editor level 0 refuse", !SettingsEditor.apply(s, List.of("level", "0"), config).ok());
        check("editor level > max refuse", !SettingsEditor.apply(s, List.of("level", String.valueOf(config.maxLevel + 1)), config).ok());
        s = apply(s, "attribute attack_damage 12");
        s = apply(s, "attribute minecraft:max_health 80");
        eq("editor attributes (prefixe minecraft ajoute)", Map.of("minecraft:attack_damage", 12.0D, "minecraft:max_health", 80.0D), s.attributes());
        check("editor attribut inconnu refuse", !SettingsEditor.apply(s, List.of("attribute", "nope:thing", "1"), config).ok());
        check("editor attribut valeur non numerique refuse", !SettingsEditor.apply(s, List.of("attribute", "armor", "beaucoup"), config).ok());
        s = apply(s, "attribute clear attack_damage");
        eq("editor attribute clear", Map.of("minecraft:max_health", 80.0D), s.attributes());
        SpawnSettings lvl = SpawnSettings.EMPTY.withLevel(Optional.of(2)).withAttribute("minecraft:armor", 4.0D);
        SpawnSettings merged = s.overlay(lvl);
        check("overlay niveau et attributs fusionnes", merged.level().get() == 2 && merged.attributes().size() == 2);
        Resolved lvlResolved = Resolved.of(config.baseSettings(), SpawnSettings.ofEntity(id("minecraft:zombie")).withLevel(Optional.of(3))).resolved().orElseThrow();
        eq("resolution niveau", 3, lvlResolved.level());
        eq("resolution niveau par defaut", 1, Resolved.of(config.baseSettings(), SpawnSettings.ofEntity(id("minecraft:zombie"))).resolved().orElseThrow().level());
        s = apply(s, "clear attributes");
        check("editor clear attributes", s.attributes().isEmpty());
        s = apply(s, "clear level");
        check("editor clear level", s.level().isEmpty());
        s = apply(s, "clear leash");
        check("editor clear leash", s.leash().isEmpty());
        s = apply(s, "clear tags");
        check("editor clear tags", s.tags().isEmpty());
        check("editor champ inconnu refuse", !SettingsEditor.apply(s, List.of("couleur", "rouge"), config).ok());
        check("editor vide refuse", !SettingsEditor.apply(s, List.of(), config).ok());
        check("editor nombre invalide refuse proprement", !SettingsEditor.apply(s, List.of("leash", "abc"), config).ok());
        check("describe non vide", !SettingsEditor.describe(s).isEmpty());
        roundTripJson("settings edites", SpawnSettings.CODEC, s);
    }

    private static SpawnSettings apply(SpawnSettings current, String line) {
        SettingsEditor.Outcome outcome = SettingsEditor.apply(current, List.of(line.split(" ")), SpawnsConfig.get());
        check("editor « " + line + " » : " + outcome.message(), outcome.ok());
        return outcome.settings().orElse(current);
    }
}
