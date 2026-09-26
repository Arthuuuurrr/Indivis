package net.hautecapitale.rpg.ability;

import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import com.mojang.serialization.JsonOps;
import net.hautecapitale.rpg.HauteCapitaleRpg;
import net.minecraft.resource.ResourceManager;
import net.minecraft.util.Identifier;

import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Le catalogue des capacités de Haute Capitale, relu à chaque rechargement de données.
 *
 * <p>Les fichiers vivent dans {@code data/<espace>/capitale_abilities/<nom>.json} : n'importe
 * quel datapack peut donc en ajouter, et un pack de priorité supérieure peut en remplacer un
 * sans qu'aucun JAR ne soit touché.
 *
 * <p>Une définition invalide n'interrompt jamais le chargement. Elle est comptée, nommée
 * dans le journal, et écartée — un serveur qui refuse de démarrer parce qu'un identifiant a
 * été mal tapé coûte plus cher que la capacité manquante.
 *
 * <p>L'existence du sort n'est <b>pas</b> vérifiée ici : les sorts vivent dans un registre
 * dynamique qui n'est pas encore disponible au moment du rechargement des ressources. Cette
 * vérification-là est faite par {@link #auditSpells}, appelée une fois le monde ouvert.
 */
public final class AbilityRegistry {

    /** Dossier lu dans les datapacks. */
    public static final String DIRECTORY = "capitale_abilities";

    private static Map<Identifier, AbilityDefinition> definitions = Map.of();
    private static Report lastReport = Report.EMPTY;

    private AbilityRegistry() {
    }

    /** Compte rendu du dernier chargement, tel que la commande de diagnostic le restitue. */
    public record Report(int loaded, int valid, List<String> warnings, java.util.Set<Identifier> missingSpells) {
        public static final Report EMPTY = new Report(0, 0, List.of(), java.util.Set.of());
    }

    public static Map<Identifier, AbilityDefinition> all() {
        return definitions;
    }

    public static AbilityDefinition get(Identifier id) {
        return definitions.get(id);
    }

    public static Report report() {
        return lastReport;
    }

    // MARK: chargement

    public static void load(ResourceManager resourceManager) {
        Map<Identifier, AbilityDefinition> parsed = new LinkedHashMap<>();
        List<String> warnings = new ArrayList<>();
        int seen = 0;

        var resources = resourceManager.findResources(DIRECTORY,
                path -> path.getPath().endsWith(".json"));

        for (var entry : resources.entrySet()) {
            seen++;
            Identifier file = entry.getKey();
            Identifier id = abilityIdOf(file);
            if (id == null) {
                warnings.add(file + " — chemin inattendu, ignoré");
                continue;
            }
            try (var reader = new InputStreamReader(entry.getValue().getInputStream())) {
                JsonElement json = JsonParser.parseReader(reader);
                var result = AbilityDefinition.CODEC.parse(JsonOps.INSTANCE, json);
                var error = result.error();
                if (error.isPresent()) {
                    warnings.add(id + " — " + error.get().message());
                    continue;
                }
                AbilityDefinition definition = result.result().orElseThrow();
                if (definition.schema_version() > AbilityDefinition.SCHEMA) {
                    warnings.add(id + " — schema_version " + definition.schema_version()
                            + " est plus récent que " + AbilityDefinition.SCHEMA + ", ignoré");
                    continue;
                }
                parsed.put(id, definition);
            } catch (Exception e) {
                warnings.add(id + " — " + e.getClass().getSimpleName() + " : " + e.getMessage());
            }
        }

        definitions = Collections.unmodifiableMap(parsed);
        lastReport = new Report(seen, parsed.size(), List.copyOf(warnings), java.util.Set.of());
        logReport(lastReport);
    }

    /**
     * Vérifie, une fois le monde ouvert, que chaque capacité désigne un sort qui existe.
     *
     * <p>Séparée du chargement parce que le registre des sorts est dynamique : il n'est
     * peuplé qu'après le chargement des ressources. Une capacité dont le sort manque reste
     * dans le catalogue mais ne sera jamais accordée — {@link AbilityResolver} l'écarte.
     */
    public static void auditSpells(net.minecraft.server.MinecraftServer server) {
        var world = server.getOverworld();
        if (world == null) {
            return;
        }
        var registry = net.spell_engine.api.spell.registry.SpellRegistry.from(world);
        java.util.Set<Identifier> missing = new java.util.LinkedHashSet<>();
        for (var entry : definitions.entrySet()) {
            if (registry.getEntry(entry.getValue().spell()).isEmpty()) {
                missing.add(entry.getKey());
                HauteCapitaleRpg.LOGGER.warn("[Capacités] sort introuvable : {} → {}", entry.getKey(), entry.getValue().spell());
            }
            // Un tag d'objets vide n'est pas une erreur pour Minecraft : il ne correspond
            // simplement à rien. La capacité serait donc définie, apprise, et jamais
            // utilisable — sans le moindre message. C'est exactement le genre de panne
            // muette qu'un serveur doit signaler à voix haute.
            for (Identifier tagId : entry.getValue().weapon().allowed_tags()) {
                var tag = net.minecraft.registry.tag.TagKey.of(
                        net.minecraft.registry.RegistryKeys.ITEM, tagId);
                if (net.minecraft.registry.Registries.ITEM.getOptional(tag)
                        .map(list -> list.size() == 0).orElse(true)) {
                    HauteCapitaleRpg.LOGGER.warn(
                            "[Capacités] tag d'objets vide ou absent : {} exige #{}",
                            entry.getKey(), tagId);
                }
            }
        }
        lastReport = new Report(lastReport.loaded(), lastReport.valid() - missing.size(),
                lastReport.warnings(), java.util.Set.copyOf(missing));
        HauteCapitaleRpg.LOGGER.info("[Capacités] {} capacité(s) prête(s) sur {} définie(s).",
                lastReport.valid(), definitions.size());
    }

    /** Vrai si le sort de cette capacité existe réellement dans le monde ouvert. */
    public static boolean spellExists(Identifier abilityId) {
        return !lastReport.missingSpells().contains(abilityId);
    }

    // MARK: outils

    /**
     * {@code capitale:capitale_abilities/whirlwind.json} devient {@code capitale:whirlwind}.
     */
    private static Identifier abilityIdOf(Identifier file) {
        String path = file.getPath();
        String prefix = DIRECTORY + "/";
        if (!path.startsWith(prefix) || !path.endsWith(".json")) {
            return null;
        }
        String name = path.substring(prefix.length(), path.length() - ".json".length());
        if (name.isEmpty()) {
            return null;
        }
        return Identifier.of(file.getNamespace(), name);
    }

    private static void logReport(Report report) {
        HauteCapitaleRpg.LOGGER.info("[Capacités] {} définition(s) lue(s), {} valide(s), {} avertissement(s).",
                report.loaded(), report.valid(), report.warnings().size());
        report.warnings().forEach(w -> HauteCapitaleRpg.LOGGER.warn("  - {}", w));
    }
}
