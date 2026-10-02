package net.hautecapitale.metiers.data;

import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;
import com.google.gson.JsonParser;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.JsonOps;
import net.fabricmc.fabric.api.resource.v1.ResourceLoader;
import net.fabricmc.fabric.api.resource.v1.reloader.SimpleResourceReloader;
import net.hautecapitale.metiers.HauteCapitaleMetiers;
import net.minecraft.resource.Resource;
import net.minecraft.resource.ResourceManager;
import net.minecraft.resource.ResourceReloader;
import net.minecraft.resource.ResourceType;
import net.minecraft.util.Identifier;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.Collections;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.function.BiConsumer;

/**
 * Registre de données rechargeable à chaud.
 *
 * <p>Un registre lit tous les fichiers d'un dossier de datapack et les décode
 * avec un {@link Codec}. La clé d'une entrée est le chemin du fichier :
 * {@code data/cubeanimals/hcm/creatures/roedeer.json} décrit
 * {@code cubeanimals:roedeer}. C'est la convention de Minecraft pour les tables
 * de butin et les recettes ; elle rend les doublons impossibles et évite de
 * répéter l'identifiant à l'intérieur du fichier.
 *
 * <p>La lecture se fait hors du fil principal, et le remplacement du contenu en
 * un seul geste : à aucun moment le serveur ne voit un registre à moitié chargé.
 * Un fichier fautif est écarté seul — les autres se chargent quand même.
 *
 * <p>Tout le contenu du MMO passe par là. Les étapes suivantes ajoutent leur
 * registre en une ligne — voir {@link HcmData}.
 *
 * @param <T> le type décodé
 */
public final class DataRegistry<T> {

    private final String domain;
    private final Codec<T> codec;
    private final BiConsumer<T, Validator> validator;

    private volatile Map<Identifier, T> entries = Collections.emptyMap();
    private volatile LoadReport report = new LoadReport();

    /**
     * @param domain    dossier lu sous {@code data/<namespace>/hcm/}
     * @param codec     décodeur d'une entrée
     * @param validator contrôles qui ne relèvent pas du codec — références vers
     *                  des objets ou des entités inexistants, valeurs
     *                  incohérentes entre elles. Peut être {@code null}.
     */
    public DataRegistry(String domain, Codec<T> codec, BiConsumer<T, Validator> validator) {
        this.domain = domain;
        this.codec = codec;
        this.validator = validator;
    }

    public String domain() {
        return domain;
    }

    public T get(Identifier id) {
        return entries.get(id);
    }

    public boolean contains(Identifier id) {
        return entries.containsKey(id);
    }

    public Set<Identifier> ids() {
        return entries.keySet();
    }

    public Map<Identifier, T> all() {
        return entries;
    }

    public int size() {
        return entries.size();
    }

    public LoadReport report() {
        return report;
    }

    /** Branche ce registre sur le rechargement des datapacks. */
    public void register() {
        ResourceLoader.get(ResourceType.SERVER_DATA).registerReloader(
                HauteCapitaleMetiers.id(domain),
                new SimpleResourceReloader<Snapshot<T>>() {
                    @Override
                    protected Snapshot<T> prepare(ResourceReloader.Store store) {
                        return read(store.getResourceManager());
                    }

                    @Override
                    protected void apply(Snapshot<T> snapshot, ResourceReloader.Store store) {
                        install(snapshot);
                    }
                });
    }

    /** Lecture puis publication d'un seul tenant — pour un rechargement manuel. */
    void load(ResourceManager manager) {
        install(read(manager));
    }

    /**
     * Ce qu'un chargement a produit. Tant que cet objet n'est pas publié, le
     * registre continue de servir les données précédentes.
     */
    private record Snapshot<U>(Map<Identifier, U> entries, LoadReport report) {
    }

    /** Lit et valide tous les fichiers du domaine. Ne touche à rien de partagé. */
    private Snapshot<T> read(ResourceManager manager) {
        String prefix = "hcm/" + domain;
        Map<Identifier, T> loaded = new TreeMap<>(Identifier::compareTo);
        LoadReport freshReport = new LoadReport();

        Map<Identifier, Resource> files =
                manager.findResources(prefix, path -> path.getPath().endsWith(".json"));

        for (Map.Entry<Identifier, Resource> file : files.entrySet()) {
            Identifier fileId = file.getKey();
            Identifier key = toKey(fileId, prefix);
            if (key == null) {
                freshReport.error(fileId, "chemin de fichier inattendu");
                continue;
            }

            T value;
            try (BufferedReader reader = new BufferedReader(
                    new InputStreamReader(file.getValue().getInputStream(), StandardCharsets.UTF_8))) {
                JsonElement json = JsonParser.parseReader(reader);
                DataResult<T> result = codec.parse(JsonOps.INSTANCE, json);
                if (result.isError()) {
                    freshReport.error(fileId,
                            result.error().map(DataResult.Error::message).orElse("décodage impossible"));
                    continue;
                }
                value = result.getOrThrow();
            } catch (IOException e) {
                freshReport.error(fileId, "lecture impossible : " + e.getMessage());
                continue;
            } catch (JsonParseException | IllegalStateException e) {
                freshReport.error(fileId, "JSON invalide : " + syntaxMessage(e));
                continue;
            }

            if (validator != null) {
                Validator checks = new Validator(fileId, key, freshReport);
                try {
                    validator.accept(value, checks);
                } catch (Exception e) {
                    freshReport.error(fileId, "validation impossible : " + e);
                    continue;
                }
                if (checks.rejected()) {
                    continue;
                }
            }

            loaded.put(key, value);
            freshReport.countLoaded();
        }

        return new Snapshot<>(Collections.unmodifiableMap(loaded), freshReport);
    }

    /** Publie un chargement et le résume dans le journal du serveur. */
    private void install(Snapshot<T> snapshot) {
        this.entries = snapshot.entries();
        this.report = snapshot.report();

        LoadReport loadReport = snapshot.report();
        if (loadReport.isClean()) {
            HauteCapitaleMetiers.LOGGER.info("{} : {} entrée(s) chargée(s).", domain, snapshot.entries().size());
        } else {
            HauteCapitaleMetiers.LOGGER.warn("{} : {} entrée(s), {} erreur(s), {} avertissement(s).",
                    domain, snapshot.entries().size(), loadReport.errorCount(), loadReport.warningCount());
            loadReport.entries().forEach(entry -> HauteCapitaleMetiers.LOGGER.warn("  {}", entry));
        }
    }

    /**
     * Message de syntaxe le plus précis possible. Gson emballe l'erreur réelle —
     * celle qui porte la ligne et la colonne — dans une exception de plus haut
     * niveau ; sans la dépiler, l'administrateur ne verrait que « JSON invalide ».
     */
    private static String syntaxMessage(Throwable error) {
        Throwable deepest = error;
        while (deepest.getCause() != null && deepest.getCause() != deepest) {
            deepest = deepest.getCause();
        }
        String message = deepest.getMessage();
        return message == null || message.isBlank() ? error.toString() : message;
    }

    /** {@code cubeanimals:hcm/creatures/roedeer.json} → {@code cubeanimals:roedeer} */
    static Identifier toKey(Identifier fileId, String prefix) {
        String path = fileId.getPath();
        String head = prefix + "/";
        if (!path.startsWith(head) || !path.endsWith(".json")) {
            return null;
        }
        String middle = path.substring(head.length(), path.length() - ".json".length());
        if (middle.isEmpty()) {
            return null;
        }
        return Identifier.tryParse(fileId.getNamespace() + ":" + middle);
    }

    /** Passé au validateur pour signaler un problème sans lever d'exception. */
    public static final class Validator {
        private final Identifier file;
        private final Identifier key;
        private final LoadReport report;
        private boolean rejected;

        Validator(Identifier file, Identifier key, LoadReport report) {
            this.file = file;
            this.key = key;
            this.report = report;
        }

        /** Le fichier est inutilisable : il ne sera pas chargé. */
        public void reject(String message) {
            report.error(file, message);
            rejected = true;
        }

        /** Le fichier reste chargé, mais quelque chose mérite d'être signalé. */
        public void warn(String message) {
            report.warn(file, message);
        }

        public Identifier file() {
            return file;
        }

        /** Clé déduite du chemin — pour une créature, l'identifiant de son type d'entité. */
        public Identifier key() {
            return key;
        }

        boolean rejected() {
            return rejected;
        }
    }
}
