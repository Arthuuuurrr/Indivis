package net.hautecapitale.spawns.store;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;
import com.google.gson.JsonParser;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.JsonOps;
import it.unimi.dsi.fastutil.longs.Long2ObjectOpenHashMap;
import net.hautecapitale.spawns.HauteCapitaleSpawns;
import net.hautecapitale.spawns.config.SpawnsConfig;
import net.hautecapitale.spawns.data.Ids;
import net.hautecapitale.spawns.data.Resolved;
import net.hautecapitale.spawns.data.SpawnPoint;
import net.hautecapitale.spawns.data.SpawnProfile;
import net.hautecapitale.spawns.data.SpawnSettings;
import net.hautecapitale.spawns.data.SpawnZone;
import net.minecraft.util.Identifier;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.TreeMap;
import java.util.stream.Stream;

/**
 * Les zones et les profils : source de verite en memoire, miroir sur le disque.
 *
 * <p>Fichiers : {@code <root>/zones/<zone>.json} et {@code <root>/profiles/<nom>.json}.
 * Chaque modification par commande reecrit le fichier concerne aussitot ; un
 * {@code /mmospawn reload} relit tout depuis le disque (pour des fichiers edites
 * a la main ou deposes par un gestionnaire de versions).
 *
 * <p>Index maintenus pour le moteur : point par identifiant complet, points par
 * zone, points par (dimension, chunk). Reconstruits en bloc a chaque modification
 * — les modifications sont rares (administration), les lectures constantes.
 */
public final class SpawnRegistry {

    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().disableHtmlEscaping().create();

    private final Path root;
    private final Map<String, SpawnZone> zones = new TreeMap<>();
    private final Map<String, SpawnProfile> profiles = new TreeMap<>();

    private final Map<String, PointRef> refs = new HashMap<>();
    private final Map<String, List<PointRef>> byZone = new HashMap<>();
    private final Map<Identifier, Long2ObjectOpenHashMap<List<PointRef>>> spatial = new HashMap<>();
    private int maxActivationRadius = 96;
    private int invalidPoints;

    public SpawnRegistry(Path root) {
        this.root = root;
    }

    public Path root() {
        return this.root;
    }

    public Path zonesDir() {
        return this.root.resolve("zones");
    }

    public Path profilesDir() {
        return this.root.resolve("profiles");
    }

    // --- lecture du disque -----------------------------------------------------------

    public LoadReport reload() {
        LoadReport report = new LoadReport();
        this.zones.clear();
        this.profiles.clear();
        try {
            Files.createDirectories(this.zonesDir());
            Files.createDirectories(this.profilesDir());
        } catch (IOException e) {
            report.errors.add("dossiers de donnees impossibles a creer : " + e.getMessage());
            this.rebuildIndex();
            return report;
        }
        for (Path file : listJson(this.profilesDir(), report)) {
            String name = stripJson(file);
            if (!Ids.isSimple(name)) {
                report.errors.add("profil « " + name + " » : nom invalide (a-z, 0-9, _ et - seulement)");
                continue;
            }
            Optional<SpawnSettings> settings = read(file, SpawnSettings.CODEC, report);
            settings.ifPresent(s -> this.profiles.put(name, new SpawnProfile(name, s)));
        }
        for (Path file : listJson(this.zonesDir(), report)) {
            String name = stripJson(file);
            if (!Ids.isSimple(name)) {
                report.errors.add("zone « " + name + " » : nom de fichier invalide (a-z, 0-9, _ et - seulement)");
                continue;
            }
            Optional<SpawnZone> zone = read(file, SpawnZone.CODEC, report);
            if (zone.isEmpty()) {
                continue;
            }
            SpawnZone loaded = zone.get();
            if (!loaded.id().equals(name)) {
                report.warnings.add("zone « " + name + " » : le champ id (« " + loaded.id() + " ») differe du nom de fichier ; le nom de fichier fait foi");
                loaded = new SpawnZone(name, loaded.displayName(), loaded.dimension(), loaded.enabled(), loaded.defaults(), loaded.points());
            }
            List<SpawnPoint> kept = new ArrayList<>();
            Set<String> seen = new java.util.HashSet<>();
            for (SpawnPoint point : loaded.points()) {
                if (!Ids.isSimple(point.id())) {
                    report.errors.add("zone « " + name + " » : point « " + point.id() + " » a un identifiant invalide, ignore");
                    continue;
                }
                if (!seen.add(point.id())) {
                    report.errors.add("zone « " + name + " » : point « " + point.id() + " » en double, second ignore");
                    continue;
                }
                kept.add(point);
            }
            this.zones.put(name, loaded.withPoints(kept));
        }
        this.rebuildIndex();
        report.zones = this.zones.size();
        report.profiles = this.profiles.size();
        report.points = this.refs.size();
        report.invalidPoints = this.invalidPoints;
        for (PointRef ref : this.refs.values()) {
            if (!ref.isValid()) {
                report.warnings.add(ref.fullId() + " : " + String.join(" ; ", ref.problems()));
            }
            ref.point().profile().ifPresent(p -> {
                if (!this.profiles.containsKey(p)) {
                    report.warnings.add(ref.fullId() + " : profil « " + p + " » introuvable");
                }
            });
        }
        return report;
    }

    private static List<Path> listJson(Path dir, LoadReport report) {
        List<Path> files = new ArrayList<>();
        try (Stream<Path> stream = Files.list(dir)) {
            stream.filter(p -> p.getFileName().toString().endsWith(".json")).sorted().forEach(files::add);
        } catch (IOException e) {
            report.errors.add("lecture de " + dir + " : " + e.getMessage());
        }
        return files;
    }

    private static String stripJson(Path file) {
        String name = file.getFileName().toString();
        return name.substring(0, name.length() - ".json".length());
    }

    private static <T> Optional<T> read(Path file, Codec<T> codec, LoadReport report) {
        String label = file.getFileName().toString();
        try {
            JsonElement json = JsonParser.parseString(Files.readString(file, StandardCharsets.UTF_8));
            DataResult<T> result = codec.parse(JsonOps.INSTANCE, json);
            Optional<T> value = result.result();
            if (value.isEmpty()) {
                report.errors.add(label + " : " + result.error().map(DataResult.Error::message).orElse("format invalide"));
            }
            return value;
        } catch (IOException e) {
            report.errors.add(label + " : illisible (" + e.getMessage() + ")");
        } catch (JsonParseException e) {
            Throwable cause = e;
            while (cause.getCause() != null) {
                cause = cause.getCause();
            }
            report.errors.add(label + " : JSON mal forme (" + cause.getMessage() + ")");
        }
        return Optional.empty();
    }

    // --- ecriture ----------------------------------------------------------------------

    private static <T> void write(Path file, Codec<T> codec, T value) {
        DataResult<JsonElement> encoded = codec.encodeStart(JsonOps.INSTANCE, value);
        JsonElement json = encoded.result().orElseThrow(() ->
                new IllegalStateException("encodage impossible : " + encoded.error().map(DataResult.Error::message).orElse("?")));
        try {
            Files.createDirectories(file.getParent());
            Path tmp = file.resolveSibling(file.getFileName() + ".tmp");
            Files.writeString(tmp, GSON.toJson(json), StandardCharsets.UTF_8);
            try {
                Files.move(tmp, file, StandardCopyOption.REPLACE_EXISTING, StandardCopyOption.ATOMIC_MOVE);
            } catch (IOException atomicFailed) {
                Files.move(tmp, file, StandardCopyOption.REPLACE_EXISTING);
            }
        } catch (IOException e) {
            HauteCapitaleSpawns.LOGGER.error("Ecriture impossible de {} : {}", file, e.getMessage());
        }
    }

    public Path zoneFile(String zoneId) {
        return this.zonesDir().resolve(zoneId + ".json");
    }

    public Path profileFile(String name) {
        return this.profilesDir().resolve(name + ".json");
    }

    /** Ajoute ou remplace une zone, l'ecrit sur le disque, reindexe. */
    public void putZone(SpawnZone zone) {
        this.zones.put(zone.id(), zone);
        write(this.zoneFile(zone.id()), SpawnZone.CODEC, zone);
        this.rebuildIndex();
    }

    public boolean removeZone(String zoneId) {
        if (this.zones.remove(zoneId) == null) {
            return false;
        }
        try {
            Files.deleteIfExists(this.zoneFile(zoneId));
        } catch (IOException e) {
            HauteCapitaleSpawns.LOGGER.warn("Fichier de zone {} non supprime : {}", zoneId, e.getMessage());
        }
        this.rebuildIndex();
        return true;
    }

    public void putProfile(SpawnProfile profile) {
        this.profiles.put(profile.name(), profile);
        write(this.profileFile(profile.name()), SpawnSettings.CODEC, profile.settings());
        this.rebuildIndex();
    }

    public boolean removeProfile(String name) {
        if (this.profiles.remove(name) == null) {
            return false;
        }
        try {
            Files.deleteIfExists(this.profileFile(name));
        } catch (IOException e) {
            HauteCapitaleSpawns.LOGGER.warn("Fichier de profil {} non supprime : {}", name, e.getMessage());
        }
        this.rebuildIndex();
        return true;
    }

    /** Reecrit tous les fichiers (apres une migration, par exemple). */
    public void saveAll() {
        for (SpawnZone zone : this.zones.values()) {
            write(this.zoneFile(zone.id()), SpawnZone.CODEC, zone);
        }
        for (SpawnProfile profile : this.profiles.values()) {
            write(this.profileFile(profile.name()), SpawnSettings.CODEC, profile.settings());
        }
    }

    // --- pour les tests : sans disque ---------------------------------------------------

    public void putZoneInMemory(SpawnZone zone) {
        this.zones.put(zone.id(), zone);
        this.rebuildIndex();
    }

    public void putProfileInMemory(SpawnProfile profile) {
        this.profiles.put(profile.name(), profile);
        this.rebuildIndex();
    }

    // --- index ---------------------------------------------------------------------------

    public void rebuildIndex() {
        this.refs.clear();
        this.byZone.clear();
        this.spatial.clear();
        this.invalidPoints = 0;
        SpawnsConfig config = SpawnsConfig.get();
        SpawnSettings base = config.baseSettings();
        int maxRadius = config.defaultActivationRadius;
        for (SpawnZone zone : this.zones.values()) {
            List<PointRef> zoneRefs = new ArrayList<>(zone.points().size());
            for (SpawnPoint point : zone.points()) {
                PointRef ref = this.resolve(base, zone, point);
                if (!ref.isValid()) {
                    this.invalidPoints++;
                }
                this.refs.put(ref.fullId(), ref);
                zoneRefs.add(ref);
                this.spatial.computeIfAbsent(zone.dimension(), d -> new Long2ObjectOpenHashMap<>())
                        .computeIfAbsent(ref.chunkKey(), k -> new ArrayList<>(2)).add(ref);
                maxRadius = Math.max(maxRadius, ref.activationRadius(config.defaultActivationRadius));
            }
            this.byZone.put(zone.id(), Collections.unmodifiableList(zoneRefs));
        }
        this.maxActivationRadius = Math.min(maxRadius, config.maxActivationRadius);
    }

    private PointRef resolve(SpawnSettings base, SpawnZone zone, SpawnPoint point) {
        SpawnSettings profileSettings = point.profile().map(this.profiles::get).map(SpawnProfile::settings).orElse(SpawnSettings.EMPTY);
        List<String> problems = new ArrayList<>();
        if (point.profile().isPresent() && !this.profiles.containsKey(point.profile().get())) {
            problems.add("profil « " + point.profile().get() + " » introuvable");
        }
        Resolved.Result result = Resolved.of(base, zone.defaults(), profileSettings, point.settings());
        problems.addAll(result.problems());
        return new PointRef(zone, point, result.resolved(), List.copyOf(problems));
    }

    // --- lecture -------------------------------------------------------------------------

    public Optional<SpawnZone> zone(String id) {
        return Optional.ofNullable(this.zones.get(id));
    }

    public Collection<SpawnZone> zones() {
        return Collections.unmodifiableCollection(this.zones.values());
    }

    public Set<String> zoneIds() {
        return Collections.unmodifiableSet(this.zones.keySet());
    }

    public Optional<SpawnProfile> profile(String name) {
        return Optional.ofNullable(this.profiles.get(name));
    }

    public Collection<SpawnProfile> profiles() {
        return Collections.unmodifiableCollection(this.profiles.values());
    }

    public Set<String> profileNames() {
        return Collections.unmodifiableSet(this.profiles.keySet());
    }

    public Optional<PointRef> ref(String fullId) {
        return Optional.ofNullable(this.refs.get(fullId));
    }

    public Optional<PointRef> ref(String zoneId, String pointId) {
        return this.ref(Ids.full(zoneId, pointId));
    }

    public Collection<PointRef> refs() {
        return Collections.unmodifiableCollection(this.refs.values());
    }

    public List<PointRef> refsOfZone(String zoneId) {
        return this.byZone.getOrDefault(zoneId, List.of());
    }

    /** Les points d'un chunk ; liste vide si aucun. Acces O(1), jamais de parcours. */
    public List<PointRef> refsInChunk(Identifier dimension, long chunkKey) {
        Long2ObjectOpenHashMap<List<PointRef>> byChunk = this.spatial.get(dimension);
        if (byChunk == null) {
            return List.of();
        }
        List<PointRef> list = byChunk.get(chunkKey);
        return list == null ? List.of() : list;
    }

    public boolean hasPointsIn(Identifier dimension) {
        Long2ObjectOpenHashMap<List<PointRef>> byChunk = this.spatial.get(dimension);
        return byChunk != null && !byChunk.isEmpty();
    }

    public Set<Identifier> dimensions() {
        return Collections.unmodifiableSet(this.spatial.keySet());
    }

    /** Le plus grand rayon d'activation en service : borne le balayage des chunks. */
    public int maxActivationRadius() {
        return this.maxActivationRadius;
    }

    public int pointCount() {
        return this.refs.size();
    }

    public int invalidPointCount() {
        return this.invalidPoints;
    }
}
