package net.hautecapitale.spawns.command;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.DynamicCommandExceptionType;
import com.mojang.brigadier.suggestion.SuggestionProvider;
import net.hautecapitale.spawns.HauteCapitaleSpawns;
import net.hautecapitale.spawns.api.OpResult;
import net.hautecapitale.spawns.config.SpawnsConfig;
import net.hautecapitale.spawns.data.Ids;
import net.hautecapitale.spawns.data.PointStatus;
import net.hautecapitale.spawns.data.QuestDrop;
import net.hautecapitale.spawns.data.Resolved;
import net.hautecapitale.spawns.data.SpawnPoint;
import net.hautecapitale.spawns.data.SpawnProfile;
import net.hautecapitale.spawns.data.SpawnSettings;
import net.hautecapitale.spawns.data.SpawnZone;
import net.hautecapitale.spawns.engine.SpawnEngine;
import net.hautecapitale.spawns.engine.Spawner;
import net.hautecapitale.spawns.state.PointState;
import net.hautecapitale.spawns.store.LoadReport;
import net.hautecapitale.spawns.store.PointRef;
import net.hautecapitale.spawns.store.SpawnRegistry;
import net.hautecapitale.spawns.text.Msg;
import net.minecraft.command.CommandSource;
import net.minecraft.command.argument.IdentifierArgumentType;
import net.minecraft.network.packet.s2c.play.PositionFlag;
import net.minecraft.registry.Registries;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.Identifier;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import java.util.Optional;
import java.util.Set;

import static net.minecraft.server.command.CommandManager.argument;
import static net.minecraft.server.command.CommandManager.literal;

/**
 * {@code /mmospawn ...} — toute l'administration, en jeu et par Easy NPC (executeAsUser=false).
 *
 * <p>Convention : une commande qui echoue <b>leve</b> (echec brigadier, donc
 * {@code execute store success} = 0 et Easy NPC voit l'echec) ; une commande
 * qui reussit rend 1.
 */
public final class SpawnCommands {

    private static final DynamicCommandExceptionType FAIL = new DynamicCommandExceptionType(o -> Text.literal(String.valueOf(o)));

    private SpawnCommands() {
    }

    private static CommandSyntaxException fail(String message) {
        return FAIL.create(message);
    }

    private static SpawnEngine engine() throws CommandSyntaxException {
        return SpawnEngine.get().orElseThrow(() -> fail("gestionnaire de spawns non demarre"));
    }

    // --- suggestions -----------------------------------------------------------------------

    private static final SuggestionProvider<ServerCommandSource> ZONES = (ctx, builder) ->
            CommandSource.suggestMatching(SpawnEngine.get().map(e -> e.registry().zoneIds()).orElse(Set.of()), builder);

    private static final SuggestionProvider<ServerCommandSource> POINT_IDS = (ctx, builder) -> {
        List<String> ids = new ArrayList<>();
        SpawnEngine.get().ifPresent(e -> e.registry().refs().forEach(ref -> ids.add(ref.fullId())));
        return CommandSource.suggestMatching(ids, builder);
    };

    private static final SuggestionProvider<ServerCommandSource> PROFILES = (ctx, builder) ->
            CommandSource.suggestMatching(SpawnEngine.get().map(e -> e.registry().profileNames()).orElse(Set.of()), builder);

    private static final SuggestionProvider<ServerCommandSource> ENTITIES = (ctx, builder) ->
            CommandSource.suggestIdentifiers(Registries.ENTITY_TYPE.getIds(), builder);

    private static final SuggestionProvider<ServerCommandSource> FIELDS = (ctx, builder) -> {
        String remaining = builder.getRemaining();
        if (remaining.contains(" ")) {
            return builder.buildFuture();
        }
        return CommandSource.suggestMatching(SettingsEditor.FIELDS, builder);
    };

    // --- arbre -------------------------------------------------------------------------------

    public static void register(CommandDispatcher<ServerCommandSource> dispatcher) {
        var root = literal("mmospawn").requires(CommandManager.requirePermissionLevel(CommandManager.GAMEMASTERS_CHECK));

        root.then(literal("help").executes(ctx -> help(ctx.getSource())));

        root.then(literal("create")
                .then(argument("zone", StringArgumentType.word()).suggests(ZONES)
                        .then(argument("point", StringArgumentType.word())
                                .then(argument("entity", IdentifierArgumentType.identifier()).suggests(ENTITIES)
                                        .executes(ctx -> create(ctx, StringArgumentType.getString(ctx, "zone"), StringArgumentType.getString(ctx, "point"),
                                                Optional.of(IdentifierArgumentType.getIdentifier(ctx, "entity")), Optional.empty())))
                                .then(literal("profile")
                                        .then(argument("profile", StringArgumentType.word()).suggests(PROFILES)
                                                .executes(ctx -> create(ctx, StringArgumentType.getString(ctx, "zone"), StringArgumentType.getString(ctx, "point"),
                                                        Optional.empty(), Optional.of(StringArgumentType.getString(ctx, "profile")))))))));

        root.then(literal("select")
                .executes(SpawnCommands::selectNearest)
                .then(argument("id", StringArgumentType.word()).suggests(POINT_IDS)
                        .executes(ctx -> select(ctx, StringArgumentType.getString(ctx, "id")))));
        root.then(literal("deselect").executes(ctx -> {
            Selection.clear(ctx.getSource().getPlayerOrThrow().getUuid());
            Msg.feedback(ctx.getSource(), Msg.info("selection effacee"));
            return 1;
        }));

        root.then(literal("move")
                .executes(ctx -> move(ctx, null, false))
                .then(argument("id", StringArgumentType.word()).suggests(POINT_IDS)
                        .executes(ctx -> move(ctx, StringArgumentType.getString(ctx, "id"), false))));
        root.then(literal("rotate")
                .executes(ctx -> move(ctx, null, true))
                .then(argument("id", StringArgumentType.word()).suggests(POINT_IDS)
                        .executes(ctx -> move(ctx, StringArgumentType.getString(ctx, "id"), true))));

        root.then(literal("set")
                .then(argument("args", StringArgumentType.greedyString()).suggests(FIELDS)
                        .executes(ctx -> setPoint(ctx, null, StringArgumentType.getString(ctx, "args")))));
        root.then(literal("edit")
                .then(argument("id", StringArgumentType.word()).suggests(POINT_IDS)
                        .then(argument("args", StringArgumentType.greedyString()).suggests(FIELDS)
                                .executes(ctx -> setPoint(ctx, StringArgumentType.getString(ctx, "id"), StringArgumentType.getString(ctx, "args"))))));
        root.then(literal("apply")
                .then(argument("profile", StringArgumentType.word()).suggests(PROFILES)
                        .executes(ctx -> applyProfile(ctx, null, StringArgumentType.getString(ctx, "profile")))
                        .then(argument("id", StringArgumentType.word()).suggests(POINT_IDS)
                                .executes(ctx -> applyProfile(ctx, StringArgumentType.getString(ctx, "id"), StringArgumentType.getString(ctx, "profile"))))));

        root.then(literal("clone")
                .then(argument("new", StringArgumentType.word())
                        .executes(ctx -> clone(ctx, null, StringArgumentType.getString(ctx, "new"))))
                .then(argument("source", StringArgumentType.word()).suggests(POINT_IDS)
                        .then(argument("new", StringArgumentType.word())
                                .executes(ctx -> clone(ctx, StringArgumentType.getString(ctx, "source"), StringArgumentType.getString(ctx, "new"))))));

        root.then(literal("delete")
                .then(argument("id", StringArgumentType.word()).suggests(POINT_IDS)
                        .executes(ctx -> {
                            throw fail("ajoutez « confirm » : /mmospawn delete " + StringArgumentType.getString(ctx, "id") + " confirm");
                        })
                        .then(literal("confirm").executes(ctx -> deletePoint(ctx, StringArgumentType.getString(ctx, "id"))))));

        root.then(pointOp("enable", (e, id) -> e.setPointEnabled(id, true)));
        root.then(pointOp("disable", (e, id) -> e.setPointEnabled(id, false)));
        root.then(pointOp("reset", SpawnEngine::resetPoint));
        root.then(pointOp("spawn", SpawnEngine::forceSpawn));

        root.then(literal("info")
                .executes(ctx -> info(ctx, null))
                .then(argument("id", StringArgumentType.word()).suggests(POINT_IDS)
                        .executes(ctx -> info(ctx, StringArgumentType.getString(ctx, "id")))));
        root.then(literal("state")
                .then(argument("id", StringArgumentType.word()).suggests(POINT_IDS)
                        .executes(ctx -> state(ctx, StringArgumentType.getString(ctx, "id")))));
        root.then(literal("query")
                .then(argument("id", StringArgumentType.word()).suggests(POINT_IDS)
                        .then(argument("condition", StringArgumentType.word())
                                .suggests((c, b) -> CommandSource.suggestMatching(List.of("alive", "dead", "ready", "disabled", "enabled"), b))
                                .executes(ctx -> query(ctx, StringArgumentType.getString(ctx, "id"), StringArgumentType.getString(ctx, "condition"))))));
        root.then(literal("list")
                .executes(ctx -> list(ctx, null))
                .then(argument("zone", StringArgumentType.word()).suggests(ZONES)
                        .executes(ctx -> list(ctx, StringArgumentType.getString(ctx, "zone")))));
        root.then(literal("tp")
                .then(argument("id", StringArgumentType.word()).suggests(POINT_IDS)
                        .executes(ctx -> tp(ctx, StringArgumentType.getString(ctx, "id")))));

        var zone = literal("zone");
        zone.then(literal("create").then(argument("zone", StringArgumentType.word())
                .executes(ctx -> zoneCreate(ctx, StringArgumentType.getString(ctx, "zone")))));
        zone.then(literal("delete").then(argument("zone", StringArgumentType.word()).suggests(ZONES)
                .executes(ctx -> {
                    throw fail("ajoutez « confirm » : /mmospawn zone delete " + StringArgumentType.getString(ctx, "zone") + " confirm");
                })
                .then(literal("confirm").executes(ctx -> zoneDelete(ctx, StringArgumentType.getString(ctx, "zone"))))));
        zone.then(zoneOp("enable", (e, id) -> e.setZoneEnabled(id, true)));
        zone.then(zoneOp("disable", (e, id) -> e.setZoneEnabled(id, false)));
        zone.then(zoneOp("reset", SpawnEngine::resetZone));
        zone.then(literal("list").executes(SpawnCommands::zoneList));
        zone.then(literal("info").then(argument("zone", StringArgumentType.word()).suggests(ZONES)
                .executes(ctx -> zoneInfo(ctx, StringArgumentType.getString(ctx, "zone")))));
        zone.then(literal("set").then(argument("zone", StringArgumentType.word()).suggests(ZONES)
                .then(argument("args", StringArgumentType.greedyString()).suggests(FIELDS)
                        .executes(ctx -> zoneSet(ctx, StringArgumentType.getString(ctx, "zone"), StringArgumentType.getString(ctx, "args"))))));
        zone.then(literal("name").then(argument("zone", StringArgumentType.word()).suggests(ZONES)
                .then(argument("name", StringArgumentType.greedyString())
                        .executes(ctx -> zoneName(ctx, StringArgumentType.getString(ctx, "zone"), StringArgumentType.getString(ctx, "name"))))));
        root.then(zone);

        var profile = literal("profile");
        profile.then(literal("create").then(argument("name", StringArgumentType.word())
                .executes(ctx -> profileCreate(ctx, StringArgumentType.getString(ctx, "name"), Optional.empty()))
                .then(argument("entity", IdentifierArgumentType.identifier()).suggests(ENTITIES)
                        .executes(ctx -> profileCreate(ctx, StringArgumentType.getString(ctx, "name"), Optional.of(IdentifierArgumentType.getIdentifier(ctx, "entity")))))));
        profile.then(literal("from").then(argument("name", StringArgumentType.word())
                .executes(ctx -> profileFrom(ctx, StringArgumentType.getString(ctx, "name")))));
        profile.then(literal("delete").then(argument("name", StringArgumentType.word()).suggests(PROFILES)
                .executes(ctx -> profileDelete(ctx, StringArgumentType.getString(ctx, "name")))));
        profile.then(literal("list").executes(SpawnCommands::profileList));
        profile.then(literal("info").then(argument("name", StringArgumentType.word()).suggests(PROFILES)
                .executes(ctx -> profileInfo(ctx, StringArgumentType.getString(ctx, "name")))));
        profile.then(literal("set").then(argument("name", StringArgumentType.word()).suggests(PROFILES)
                .then(argument("args", StringArgumentType.greedyString()).suggests(FIELDS)
                        .executes(ctx -> profileSet(ctx, StringArgumentType.getString(ctx, "name"), StringArgumentType.getString(ctx, "args"))))));
        root.then(profile);

        root.then(literal("debug")
                .then(literal("on").executes(ctx -> debug(ctx, true)))
                .then(literal("off").executes(ctx -> debug(ctx, false))));

        root.then(literal("validate").executes(SpawnCommands::validate));
        root.then(literal("repair").executes(SpawnCommands::repair));
        root.then(literal("reload").executes(SpawnCommands::reload));
        root.then(literal("save").executes(ctx -> {
            engine().registry().saveAll();
            Msg.feedback(ctx.getSource(), Msg.ok("fichiers de zones et de profils reecrits"));
            return 1;
        }));
        root.then(literal("stats").executes(ctx -> {
            Msg.feedback(ctx.getSource(), Msg.info(engine().stats()));
            return 1;
        }));

        dispatcher.register(root);
    }

    private interface Op {
        OpResult run(SpawnEngine engine, String id);
    }

    private static com.mojang.brigadier.builder.LiteralArgumentBuilder<ServerCommandSource> pointOp(String name, Op op) {
        return literal(name).then(argument("id", StringArgumentType.word()).suggests(POINT_IDS).executes(ctx -> {
            SpawnEngine engine = engine();
            String id = StringArgumentType.getString(ctx, "id");
            if (engine.registry().ref(id).isEmpty()) {
                throw fail("point inconnu : " + id);
            }
            OpResult result = op.run(engine, id);
            if (!result.ok()) {
                throw fail(result.message());
            }
            Msg.feedback(ctx.getSource(), Msg.ok(result.message()));
            return 1;
        }));
    }

    private static com.mojang.brigadier.builder.LiteralArgumentBuilder<ServerCommandSource> zoneOp(String name, Op op) {
        return literal(name).then(argument("zone", StringArgumentType.word()).suggests(ZONES).executes(ctx -> {
            SpawnEngine engine = engine();
            String id = StringArgumentType.getString(ctx, "zone");
            OpResult result = op.run(engine, id);
            if (!result.ok()) {
                throw fail(result.message());
            }
            Msg.feedback(ctx.getSource(), Msg.ok(result.message()));
            return 1;
        }));
    }

    // --- aide ------------------------------------------------------------------------------

    private static int help(ServerCommandSource source) {
        String[] lines = {
                "create <zone> <point> <entite> | create <zone> <point> profile <profil> — a votre position",
                "select [id] | deselect | move [id] | rotate [id] | tp <id>",
                "set <champ> <valeurs...> (point selectionne) | edit <id> <champ> ... | apply <profil> [id]",
                "clone <nouveau> | clone <source> <nouveau> — copie a votre position",
                "enable|disable|reset|spawn <id> | delete <id> confirm | info [id] | state <id> | query <id> <cond> | list [zone]",
                "zone create|delete|enable|disable|reset|info|set|name <zone> | zone list",
                "profile create <nom> [entite] | profile from <nom> | profile set|info|delete <nom> | profile list",
                "debug on|off | validate | repair | reload | save | stats",
                "champs : " + SettingsEditor.usage()
        };
        for (String line : lines) {
            Msg.feedback(source, Text.literal(line).formatted(Formatting.GRAY));
        }
        return 1;
    }

    // --- points ------------------------------------------------------------------------------

    private static String resolveId(CommandContext<ServerCommandSource> ctx, String explicit) throws CommandSyntaxException {
        if (explicit != null) {
            if (Ids.split(explicit).isEmpty()) {
                throw fail("identifiant attendu sous la forme zone.point : " + explicit);
            }
            return explicit;
        }
        ServerPlayerEntity player = ctx.getSource().getPlayerOrThrow();
        return Selection.selected(player.getUuid()).orElseThrow(() -> fail("aucun point selectionne : /mmospawn select <zone.point>"));
    }

    private static PointRef refOrFail(SpawnEngine engine, String fullId) throws CommandSyntaxException {
        return engine.registry().ref(fullId).orElseThrow(() -> fail("point inconnu : " + fullId));
    }

    private static int create(CommandContext<ServerCommandSource> ctx, String zoneId, String pointId, Optional<Identifier> entity, Optional<String> profile) throws CommandSyntaxException {
        SpawnEngine engine = engine();
        SpawnRegistry registry = engine.registry();
        ServerPlayerEntity player = ctx.getSource().getPlayerOrThrow();
        if (!Ids.isSimple(zoneId) || !Ids.isSimple(pointId)) {
            throw fail("identifiants : a-z, 0-9, _ et - seulement (sans point)");
        }
        if (entity.isPresent() && !Spawner.entityTypeExists(entity.get())) {
            throw fail("type d'entite inconnu sur ce serveur : " + entity.get());
        }
        if (profile.isPresent() && registry.profile(profile.get()).isEmpty()) {
            throw fail("profil inconnu : " + profile.get());
        }
        ServerWorld world = player.getEntityWorld();
        Identifier dimension = world.getRegistryKey().getValue();
        SpawnZone zone = registry.zone(zoneId).orElse(null);
        boolean newZone = zone == null;
        if (newZone) {
            zone = SpawnZone.create(zoneId, dimension);
        } else if (!zone.dimension().equals(dimension)) {
            throw fail("la zone " + zoneId + " est dans " + zone.dimension() + ", vous etes dans " + dimension);
        }
        if (zone.hasPoint(pointId)) {
            throw fail("le point " + Ids.full(zoneId, pointId) + " existe deja (clone ou move ?)");
        }
        SpawnSettings settings = entity.map(SpawnSettings::ofEntity).orElse(SpawnSettings.EMPTY);
        SpawnPoint point = SpawnPoint.at(pointId, player.getX(), player.getY(), player.getZ(), player.getYaw(), player.getPitch(), settings)
                .withProfile(profile);
        registry.putZone(zone.withPoint(point));
        String fullId = Ids.full(zoneId, pointId);
        Selection.select(player.getUuid(), fullId);
        PointRef ref = refOrFail(engine, fullId);
        engine.state(fullId);
        Msg.feedback(ctx.getSource(), Msg.ok("point " + fullId + " cree" + (newZone ? " (zone " + zoneId + " creee dans " + dimension + ")" : "") + " et selectionne"));
        if (!ref.isValid()) {
            Msg.feedback(ctx.getSource(), Msg.warn("point invalide pour l'instant : " + String.join(" ; ", ref.problems())));
        } else {
            Resolved r = ref.resolved().get();
            Msg.feedback(ctx.getSource(), Msg.info(r.entity() + " — respawn " + r.respawn().describe() + ", laisse " + r.leash().radius() + ", rang " + r.rank()));
        }
        return 1;
    }

    private static int select(CommandContext<ServerCommandSource> ctx, String fullId) throws CommandSyntaxException {
        SpawnEngine engine = engine();
        PointRef ref = refOrFail(engine, fullId);
        ServerPlayerEntity player = ctx.getSource().getPlayerOrThrow();
        Selection.select(player.getUuid(), ref.fullId());
        Msg.feedback(ctx.getSource(), Msg.ok(ref.fullId() + " selectionne"));
        return 1;
    }

    private static int selectNearest(CommandContext<ServerCommandSource> ctx) throws CommandSyntaxException {
        SpawnEngine engine = engine();
        ServerPlayerEntity player = ctx.getSource().getPlayerOrThrow();
        Identifier dimension = player.getEntityWorld().getRegistryKey().getValue();
        PointRef best = null;
        double bestSq = 32 * 32;
        int pcx = player.getChunkPos().x;
        int pcz = player.getChunkPos().z;
        for (int dx = -3; dx <= 3; dx++) {
            for (int dz = -3; dz <= 3; dz++) {
                for (PointRef ref : engine.registry().refsInChunk(dimension, PointRef.chunkKey(pcx + dx, pcz + dz))) {
                    double d = ref.point().squaredDistanceTo(player.getX(), player.getY(), player.getZ());
                    if (d < bestSq) {
                        bestSq = d;
                        best = ref;
                    }
                }
            }
        }
        if (best == null) {
            throw fail("aucun point a moins de 32 blocs");
        }
        Selection.select(player.getUuid(), best.fullId());
        Msg.feedback(ctx.getSource(), Msg.ok(best.fullId() + " selectionne (" + String.format("%.1f", Math.sqrt(bestSq)) + " m)"));
        return 1;
    }

    private static int move(CommandContext<ServerCommandSource> ctx, String explicit, boolean rotationOnly) throws CommandSyntaxException {
        SpawnEngine engine = engine();
        String fullId = resolveId(ctx, explicit);
        PointRef ref = refOrFail(engine, fullId);
        ServerPlayerEntity player = ctx.getSource().getPlayerOrThrow();
        Identifier dimension = player.getEntityWorld().getRegistryKey().getValue();
        if (!rotationOnly && !ref.zone().dimension().equals(dimension)) {
            throw fail("la zone est dans " + ref.zone().dimension() + ", vous etes dans " + dimension);
        }
        SpawnPoint moved = rotationOnly
                ? ref.point().withRotation(player.getYaw(), player.getPitch())
                : ref.point().withPosition(player.getX(), player.getY(), player.getZ(), player.getYaw(), player.getPitch());
        engine.registry().putZone(ref.zone().withPoint(moved));
        engine.onPointEdited(fullId);
        Msg.feedback(ctx.getSource(), Msg.ok(fullId + (rotationOnly ? " reoriente" : " deplace") + " : " + describePos(moved)));
        return 1;
    }

    private static String describePos(SpawnPoint p) {
        return String.format(Locale.ROOT, "%.1f %.1f %.1f (yaw %.0f)", p.x(), p.y(), p.z(), p.yaw());
    }

    private static List<String> splitArgs(String args) {
        return Arrays.stream(args.trim().split("\\s+")).filter(s -> !s.isEmpty()).toList();
    }

    private static int setPoint(CommandContext<ServerCommandSource> ctx, String explicit, String args) throws CommandSyntaxException {
        SpawnEngine engine = engine();
        String fullId = resolveId(ctx, explicit);
        PointRef ref = refOrFail(engine, fullId);
        SettingsEditor.Outcome outcome = SettingsEditor.apply(ref.point().settings(), splitArgs(args), SpawnsConfig.get());
        if (!outcome.ok()) {
            throw fail(outcome.message());
        }
        engine.registry().putZone(ref.zone().withPoint(ref.point().withSettings(outcome.settings().get())));
        engine.onPointEdited(fullId);
        Msg.feedback(ctx.getSource(), Msg.ok(fullId + " : " + outcome.message()));
        afterEdit(ctx, engine, fullId);
        return 1;
    }

    private static void afterEdit(CommandContext<ServerCommandSource> ctx, SpawnEngine engine, String fullId) {
        engine.registry().ref(fullId).ifPresent(ref -> {
            if (!ref.isValid()) {
                Msg.feedback(ctx.getSource(), Msg.warn("point invalide : " + String.join(" ; ", ref.problems())));
            } else if (engine.state(fullId).status() == PointStatus.ALIVE) {
                Msg.feedback(ctx.getSource(), Msg.info("le mob vivant garde son type et son equipement ; /mmospawn reset " + fullId + " pour le remplacer"));
            }
        });
    }

    private static int applyProfile(CommandContext<ServerCommandSource> ctx, String explicit, String profile) throws CommandSyntaxException {
        SpawnEngine engine = engine();
        String fullId = resolveId(ctx, explicit);
        PointRef ref = refOrFail(engine, fullId);
        Optional<String> target = profile.equalsIgnoreCase("none") ? Optional.empty() : Optional.of(profile);
        if (target.isPresent() && engine.registry().profile(profile).isEmpty()) {
            throw fail("profil inconnu : " + profile);
        }
        engine.registry().putZone(ref.zone().withPoint(ref.point().withProfile(target)));
        engine.onPointEdited(fullId);
        Msg.feedback(ctx.getSource(), Msg.ok(fullId + " : profil " + target.orElse("(aucun)")));
        afterEdit(ctx, engine, fullId);
        return 1;
    }

    private static int clone(CommandContext<ServerCommandSource> ctx, String explicitSource, String newPointId) throws CommandSyntaxException {
        SpawnEngine engine = engine();
        String sourceId = resolveId(ctx, explicitSource);
        PointRef source = refOrFail(engine, sourceId);
        ServerPlayerEntity player = ctx.getSource().getPlayerOrThrow();
        if (!Ids.isSimple(newPointId)) {
            throw fail("identifiant de point invalide : " + newPointId);
        }
        if (source.zone().hasPoint(newPointId)) {
            throw fail("le point " + Ids.full(source.zone().id(), newPointId) + " existe deja");
        }
        Identifier dimension = player.getEntityWorld().getRegistryKey().getValue();
        if (!source.zone().dimension().equals(dimension)) {
            throw fail("la zone est dans " + source.zone().dimension() + ", vous etes dans " + dimension);
        }
        SpawnPoint copy = source.point().withId(newPointId)
                .withPosition(player.getX(), player.getY(), player.getZ(), player.getYaw(), player.getPitch());
        engine.registry().putZone(source.zone().withPoint(copy));
        String fullId = Ids.full(source.zone().id(), newPointId);
        engine.state(fullId);
        Selection.select(player.getUuid(), fullId);
        Msg.feedback(ctx.getSource(), Msg.ok(fullId + " cree a votre position (copie de " + sourceId + ") et selectionne"));
        return 1;
    }

    private static int deletePoint(CommandContext<ServerCommandSource> ctx, String fullId) throws CommandSyntaxException {
        SpawnEngine engine = engine();
        OpResult result = engine.deletePoint(fullId);
        if (!result.ok()) {
            throw fail(result.message());
        }
        Selection.forget(fullId);
        Msg.feedback(ctx.getSource(), Msg.ok(result.message()));
        return 1;
    }

    private static int info(CommandContext<ServerCommandSource> ctx, String explicit) throws CommandSyntaxException {
        SpawnEngine engine = engine();
        String fullId = resolveId(ctx, explicit);
        PointRef ref = refOrFail(engine, fullId);
        PointState state = engine.state(fullId);
        long now = System.currentTimeMillis();
        ServerCommandSource source = ctx.getSource();
        Msg.feedback(source, Msg.info("Point " + fullId + (ref.effectivelyEnabled() ? "" : " (DESACTIVE)")));
        Msg.feedback(source, Msg.line("position", describePos(ref.point()) + " dans " + ref.zone().dimension()));
        Msg.feedback(source, Msg.line("profil", ref.point().profile().orElse("(aucun)")));
        String status = state.status().asString().toUpperCase();
        if (state.status() == PointStatus.DEAD_WAITING) {
            status += " (reapparition dans " + Msg.mmss(state.remainingSeconds(now)) + ")";
        } else if (state.status() == PointStatus.ALIVE) {
            status += engine.loadedEntity(state).map(e -> String.format(Locale.ROOT, " (mob charge, %.0f/%.0f PV, a %.1f m)", e.getHealth(), e.getMaxHealth(),
                    Math.sqrt(ref.point().squaredDistanceTo(e.getX(), e.getY(), e.getZ())))).orElse(" (mob non charge)");
        }
        Msg.feedback(source, Msg.line("etat", status));
        Msg.feedback(source, Msg.line("historique", state.spawnCount() + " apparition(s), " + state.killCount() + " mort(s)"
                + state.lastKillerName().map(n -> ", dernier tueur " + n).orElse("")));
        if (ref.isValid()) {
            Resolved r = ref.resolved().get();
            Msg.feedback(source, Msg.line("entite", r.entity() + "  rang " + r.rank() + "  niveau " + r.level()
                    + " (PV x" + (1 + (r.level() - 1) * SpawnsConfig.get().levelHealthFactor) + ")"));
            if (!r.attributes().isEmpty()) {
                Msg.feedback(source, Msg.line("attributs imposes", r.attributes().toString()));
            }
            Msg.feedback(source, Msg.line("respawn", r.respawn().describe() + "  |  activation " + r.activationRadius() + "  |  marche " + r.wanderRadius()
                    + "  |  joueur a plus de " + r.conditions().minPlayerDistance()));
            Msg.feedback(source, Msg.line("laisse", r.leash().describe()));
            Msg.feedback(source, Msg.line("tags", r.tags().isEmpty() ? "(aucun)" : r.tags().toString()));
            Msg.feedback(source, Msg.line("credit", r.creditMode().asString() + "  |  compteurs " + (r.counters().isEmpty() ? "(aucun)" : r.counters())));
            Msg.feedback(source, Msg.line("objets de quete", r.drops().isEmpty() ? "(aucun)" : r.drops().stream().map(QuestDrop::describe).toList().toString()));
            r.customName().ifPresent(n -> Msg.feedback(source, Msg.line("nom", n)));
            r.nbt().ifPresent(n -> Msg.feedback(source, Msg.line("nbt", n)));
        } else {
            Msg.feedback(source, Msg.error("invalide : " + String.join(" ; ", ref.problems())));
        }
        List<String> overrides = SettingsEditor.describe(ref.point().settings());
        Msg.feedback(source, Msg.line("surcharges du point", overrides.isEmpty() ? "(aucune)" : String.join(" ; ", overrides)));
        state.lastProblem().ifPresent(p -> Msg.feedback(source, Msg.warn("dernier probleme : " + p)));
        return 1;
    }

    private static int state(CommandContext<ServerCommandSource> ctx, String fullId) throws CommandSyntaxException {
        SpawnEngine engine = engine();
        refOrFail(engine, fullId);
        PointState state = engine.state(fullId);
        String text = state.status().asString();
        if (state.status() == PointStatus.DEAD_WAITING) {
            text += " " + state.remainingSeconds(System.currentTimeMillis());
        }
        Msg.feedback(ctx.getSource(), Text.literal(fullId + " " + text));
        return switch (state.status()) {
            case ALIVE -> 1;
            case DEAD_WAITING -> 2;
            case READY -> 3;
            case DISABLED -> 4;
        };
    }

    /** Reussit (1) si la condition est vraie, echoue sinon : pour {@code execute store success}. */
    private static int query(CommandContext<ServerCommandSource> ctx, String fullId, String condition) throws CommandSyntaxException {
        SpawnEngine engine = engine();
        PointRef ref = refOrFail(engine, fullId);
        PointState state = engine.state(fullId);
        boolean result = switch (condition.toLowerCase(Locale.ROOT)) {
            case "alive" -> state.status() == PointStatus.ALIVE;
            case "dead" -> state.status() == PointStatus.DEAD_WAITING;
            case "ready" -> state.status() == PointStatus.READY;
            case "disabled" -> state.status() == PointStatus.DISABLED;
            case "enabled" -> ref.effectivelyEnabled();
            default -> throw fail("condition inconnue : alive|dead|ready|disabled|enabled");
        };
        if (!result) {
            throw fail(fullId + " : " + condition + " = non (" + state.status().asString() + ")");
        }
        Msg.feedback(ctx.getSource(), Text.literal(fullId + " : " + condition + " = oui"));
        return 1;
    }

    private static int list(CommandContext<ServerCommandSource> ctx, String zoneId) throws CommandSyntaxException {
        SpawnEngine engine = engine();
        ServerCommandSource source = ctx.getSource();
        if (zoneId == null) {
            Msg.feedback(source, Msg.info(engine.registry().pointCount() + " point(s) dans " + engine.registry().zones().size() + " zone(s) — /mmospawn list <zone> pour le detail"));
            for (SpawnZone zone : engine.registry().zones()) {
                Msg.feedback(source, Text.literal("  " + zoneSummary(engine, zone)).formatted(Formatting.GRAY));
            }
            return 1;
        }
        SpawnZone zone = engine.registry().zone(zoneId).orElseThrow(() -> fail("zone inconnue : " + zoneId));
        Msg.feedback(source, Msg.info(zoneSummary(engine, zone)));
        long now = System.currentTimeMillis();
        for (PointRef ref : engine.registry().refsOfZone(zoneId)) {
            PointState state = engine.state(ref.fullId());
            String status = state.status().asString();
            if (state.status() == PointStatus.DEAD_WAITING) {
                status += " " + Msg.mmss(state.remainingSeconds(now));
            }
            Formatting color = ref.isValid() ? (ref.effectivelyEnabled() ? Formatting.WHITE : Formatting.DARK_GRAY) : Formatting.RED;
            Msg.feedback(source, Text.literal("  " + ref.point().id() + "  " + ref.resolved().map(r -> r.entity().toString()).orElse("(invalide)")
                    + "  " + status + "  " + describePos(ref.point())).formatted(color));
        }
        return 1;
    }

    private static String zoneSummary(SpawnEngine engine, SpawnZone zone) {
        int alive = 0;
        int dead = 0;
        int disabled = 0;
        for (PointRef ref : engine.registry().refsOfZone(zone.id())) {
            PointStatus s = engine.store().get(ref.fullId()).map(PointState::status).orElse(PointStatus.READY);
            switch (s) {
                case ALIVE -> alive++;
                case DEAD_WAITING -> dead++;
                case DISABLED -> disabled++;
                default -> { }
            }
        }
        return zone.label() + (zone.enabled() ? "" : " [DESACTIVEE]") + " : " + zone.points().size() + " point(s), " + alive + " vivant(s), "
                + dead + " mort(s), " + disabled + " desactive(s), " + zone.dimension();
    }

    private static int tp(CommandContext<ServerCommandSource> ctx, String fullId) throws CommandSyntaxException {
        SpawnEngine engine = engine();
        PointRef ref = refOrFail(engine, fullId);
        ServerPlayerEntity player = ctx.getSource().getPlayerOrThrow();
        ServerWorld world = engine.server().getWorld(ref.dimension());
        if (world == null) {
            throw fail("dimension non chargee : " + ref.zone().dimension());
        }
        player.teleport(world, ref.point().x(), ref.point().y(), ref.point().z(), Set.<PositionFlag>of(), ref.point().yaw(), ref.point().pitch(), false);
        Selection.select(player.getUuid(), fullId);
        Msg.feedback(ctx.getSource(), Msg.ok("teleporte sur " + fullId + " (selectionne)"));
        return 1;
    }

    // --- zones -------------------------------------------------------------------------------

    private static int zoneCreate(CommandContext<ServerCommandSource> ctx, String zoneId) throws CommandSyntaxException {
        SpawnEngine engine = engine();
        if (!Ids.isSimple(zoneId)) {
            throw fail("identifiant de zone invalide : " + zoneId);
        }
        if (engine.registry().zone(zoneId).isPresent()) {
            throw fail("la zone " + zoneId + " existe deja");
        }
        Identifier dimension = ctx.getSource().getWorld().getRegistryKey().getValue();
        engine.registry().putZone(SpawnZone.create(zoneId, dimension));
        Msg.feedback(ctx.getSource(), Msg.ok("zone " + zoneId + " creee dans " + dimension));
        return 1;
    }

    private static int zoneDelete(CommandContext<ServerCommandSource> ctx, String zoneId) throws CommandSyntaxException {
        SpawnEngine engine = engine();
        OpResult result = engine.deleteZone(zoneId);
        if (!result.ok()) {
            throw fail(result.message());
        }
        Selection.forgetZone(zoneId);
        Msg.feedback(ctx.getSource(), Msg.ok(result.message()));
        return 1;
    }

    private static int zoneList(CommandContext<ServerCommandSource> ctx) throws CommandSyntaxException {
        SpawnEngine engine = engine();
        if (engine.registry().zones().isEmpty()) {
            Msg.feedback(ctx.getSource(), Msg.info("aucune zone — /mmospawn create <zone> <point> <entite>"));
            return 1;
        }
        for (SpawnZone zone : engine.registry().zones()) {
            Msg.feedback(ctx.getSource(), Text.literal(zoneSummary(engine, zone)).formatted(zone.enabled() ? Formatting.WHITE : Formatting.DARK_GRAY));
        }
        return 1;
    }

    private static int zoneInfo(CommandContext<ServerCommandSource> ctx, String zoneId) throws CommandSyntaxException {
        SpawnEngine engine = engine();
        SpawnZone zone = engine.registry().zone(zoneId).orElseThrow(() -> fail("zone inconnue : " + zoneId));
        Msg.feedback(ctx.getSource(), Msg.info(zoneSummary(engine, zone)));
        List<String> defaults = SettingsEditor.describe(zone.defaults());
        Msg.feedback(ctx.getSource(), Msg.line("valeurs par defaut", defaults.isEmpty() ? "(aucune)" : String.join(" ; ", defaults)));
        Msg.feedback(ctx.getSource(), Msg.line("nettoyee", engine.isZoneCleared(zoneId) ? "oui (tous les mobs morts)" : "non"));
        Msg.feedback(ctx.getSource(), Msg.line("fichier", engine.registry().zoneFile(zoneId).toString()));
        return list(ctx, zoneId);
    }

    private static int zoneSet(CommandContext<ServerCommandSource> ctx, String zoneId, String args) throws CommandSyntaxException {
        SpawnEngine engine = engine();
        SpawnZone zone = engine.registry().zone(zoneId).orElseThrow(() -> fail("zone inconnue : " + zoneId));
        SettingsEditor.Outcome outcome = SettingsEditor.apply(zone.defaults(), splitArgs(args), SpawnsConfig.get());
        if (!outcome.ok()) {
            throw fail(outcome.message());
        }
        engine.registry().putZone(zone.withDefaults(outcome.settings().get()));
        for (PointRef ref : engine.registry().refsOfZone(zoneId)) {
            engine.onPointEdited(ref.fullId());
        }
        Msg.feedback(ctx.getSource(), Msg.ok("zone " + zoneId + " : " + outcome.message()));
        return 1;
    }

    private static int zoneName(CommandContext<ServerCommandSource> ctx, String zoneId, String name) throws CommandSyntaxException {
        SpawnEngine engine = engine();
        SpawnZone zone = engine.registry().zone(zoneId).orElseThrow(() -> fail("zone inconnue : " + zoneId));
        Optional<String> value = name.equalsIgnoreCase("clear") ? Optional.empty() : Optional.of(name);
        engine.registry().putZone(zone.withDisplayName(value));
        Msg.feedback(ctx.getSource(), Msg.ok("zone " + zoneId + " : nom " + value.orElse("(aucun)")));
        return 1;
    }

    // --- profils -----------------------------------------------------------------------------

    private static int profileCreate(CommandContext<ServerCommandSource> ctx, String name, Optional<Identifier> entity) throws CommandSyntaxException {
        SpawnEngine engine = engine();
        if (!Ids.isSimple(name)) {
            throw fail("nom de profil invalide : " + name);
        }
        if (engine.registry().profile(name).isPresent()) {
            throw fail("le profil " + name + " existe deja");
        }
        if (entity.isPresent() && !Spawner.entityTypeExists(entity.get())) {
            throw fail("type d'entite inconnu : " + entity.get());
        }
        engine.registry().putProfile(new SpawnProfile(name, entity.map(SpawnSettings::ofEntity).orElse(SpawnSettings.EMPTY)));
        Msg.feedback(ctx.getSource(), Msg.ok("profil " + name + " cree — /mmospawn profile set " + name + " <champ> ..."));
        return 1;
    }

    private static int profileFrom(CommandContext<ServerCommandSource> ctx, String name) throws CommandSyntaxException {
        SpawnEngine engine = engine();
        String fullId = resolveId(ctx, null);
        PointRef ref = refOrFail(engine, fullId);
        if (!Ids.isSimple(name)) {
            throw fail("nom de profil invalide : " + name);
        }
        SpawnSettings settings = ref.point().profile().flatMap(engine.registry()::profile).map(SpawnProfile::settings).orElse(SpawnSettings.EMPTY)
                .overlay(ref.point().settings());
        engine.registry().putProfile(new SpawnProfile(name, settings));
        Msg.feedback(ctx.getSource(), Msg.ok("profil " + name + " cree depuis " + fullId + " : " + String.join(" ; ", SettingsEditor.describe(settings))));
        return 1;
    }

    private static int profileDelete(CommandContext<ServerCommandSource> ctx, String name) throws CommandSyntaxException {
        SpawnEngine engine = engine();
        if (!engine.registry().removeProfile(name)) {
            throw fail("profil inconnu : " + name);
        }
        Msg.feedback(ctx.getSource(), Msg.ok("profil " + name + " supprime (les points qui l'utilisaient sont a corriger : /mmospawn validate)"));
        return 1;
    }

    private static int profileList(CommandContext<ServerCommandSource> ctx) throws CommandSyntaxException {
        SpawnEngine engine = engine();
        if (engine.registry().profiles().isEmpty()) {
            Msg.feedback(ctx.getSource(), Msg.info("aucun profil — /mmospawn profile create <nom> <entite>"));
            return 1;
        }
        for (SpawnProfile profile : engine.registry().profiles()) {
            Msg.feedback(ctx.getSource(), Text.literal(profile.name() + " : " + String.join(" ; ", SettingsEditor.describe(profile.settings()))).formatted(Formatting.WHITE));
        }
        return 1;
    }

    private static int profileInfo(CommandContext<ServerCommandSource> ctx, String name) throws CommandSyntaxException {
        SpawnEngine engine = engine();
        SpawnProfile profile = engine.registry().profile(name).orElseThrow(() -> fail("profil inconnu : " + name));
        Msg.feedback(ctx.getSource(), Msg.info("Profil " + name));
        List<String> lines = SettingsEditor.describe(profile.settings());
        if (lines.isEmpty()) {
            Msg.feedback(ctx.getSource(), Msg.line("contenu", "(vide)"));
        }
        for (String line : lines) {
            Msg.feedback(ctx.getSource(), Text.literal("  " + line).formatted(Formatting.WHITE));
        }
        long users = engine.registry().refs().stream().filter(r -> r.point().profile().map(name::equals).orElse(false)).count();
        Msg.feedback(ctx.getSource(), Msg.line("utilise par", users + " point(s)"));
        return 1;
    }

    private static int profileSet(CommandContext<ServerCommandSource> ctx, String name, String args) throws CommandSyntaxException {
        SpawnEngine engine = engine();
        SpawnProfile profile = engine.registry().profile(name).orElseThrow(() -> fail("profil inconnu : " + name));
        SettingsEditor.Outcome outcome = SettingsEditor.apply(profile.settings(), splitArgs(args), SpawnsConfig.get());
        if (!outcome.ok()) {
            throw fail(outcome.message());
        }
        engine.registry().putProfile(profile.withSettings(outcome.settings().get()));
        for (PointRef ref : engine.registry().refs()) {
            if (ref.point().profile().map(name::equals).orElse(false)) {
                engine.onPointEdited(ref.fullId());
            }
        }
        Msg.feedback(ctx.getSource(), Msg.ok("profil " + name + " : " + outcome.message()));
        return 1;
    }

    // --- outils ------------------------------------------------------------------------------

    private static int debug(CommandContext<ServerCommandSource> ctx, boolean on) throws CommandSyntaxException {
        ServerPlayerEntity player = ctx.getSource().getPlayerOrThrow();
        DebugView.set(player, on);
        Msg.feedback(ctx.getSource(), Msg.ok("affichage de debogage " + (on ? "active (portee " + SpawnsConfig.get().debugRange + " blocs)" : "desactive")));
        return 1;
    }

    private static int validate(CommandContext<ServerCommandSource> ctx) throws CommandSyntaxException {
        SpawnEngine engine = engine();
        List<String> issues = engine.validate();
        if (issues.isEmpty()) {
            Msg.feedback(ctx.getSource(), Msg.ok("aucun probleme : " + engine.registry().pointCount() + " point(s), " + engine.registry().zones().size() + " zone(s), " + engine.registry().profiles().size() + " profil(s)"));
            return 1;
        }
        Msg.feedback(ctx.getSource(), Msg.warn(issues.size() + " probleme(s) :"));
        for (String issue : issues) {
            Msg.feedback(ctx.getSource(), Text.literal("  " + issue).formatted(Formatting.YELLOW));
        }
        return 1;
    }

    private static int repair(CommandContext<ServerCommandSource> ctx) throws CommandSyntaxException {
        SpawnEngine engine = engine();
        List<String> actions = engine.repair();
        if (actions.isEmpty()) {
            Msg.feedback(ctx.getSource(), Msg.ok("rien a reparer"));
            return 1;
        }
        Msg.feedback(ctx.getSource(), Msg.ok(actions.size() + " reparation(s) :"));
        for (String action : actions) {
            Msg.feedback(ctx.getSource(), Text.literal("  " + action).formatted(Formatting.GREEN));
        }
        return 1;
    }

    private static int reload(CommandContext<ServerCommandSource> ctx) throws CommandSyntaxException {
        SpawnEngine engine = engine();
        SpawnsConfig.load(HauteCapitaleSpawns.dataRoot());
        LoadReport report = engine.registry().reload();
        engine.afterReload();
        Msg.feedback(ctx.getSource(), report.ok() ? Msg.ok("rechargement : " + report.summary()) : Msg.warn("rechargement : " + report.summary()));
        for (String error : report.errors) {
            Msg.feedback(ctx.getSource(), Text.literal("  " + error).formatted(Formatting.RED));
        }
        for (String warning : report.warnings) {
            Msg.feedback(ctx.getSource(), Text.literal("  " + warning).formatted(Formatting.YELLOW));
        }
        return 1;
    }
}
