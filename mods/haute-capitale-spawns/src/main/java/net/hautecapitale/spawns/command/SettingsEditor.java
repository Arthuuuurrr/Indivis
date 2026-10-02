package net.hautecapitale.spawns.command;

import net.hautecapitale.spawns.config.SpawnsConfig;
import net.hautecapitale.spawns.data.CreditMode;
import net.hautecapitale.spawns.data.Leash;
import net.hautecapitale.spawns.data.QuestDrop;
import net.hautecapitale.spawns.data.Respawn;
import net.hautecapitale.spawns.data.RespawnConditions;
import net.hautecapitale.spawns.data.SpawnSettings;
import net.hautecapitale.spawns.engine.Spawner;
import net.minecraft.registry.Registries;
import net.minecraft.util.Identifier;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Optional;

/**
 * Traduit {@code /mmospawn set <champ> <valeurs...>} en modification d'un
 * {@link SpawnSettings}. Sert pour un point, une zone (defaults) et un profil :
 * la meme grammaire partout.
 *
 * <p>Sans classe Minecraft dans la logique de parsing (sauf la verification des
 * identifiants de registre), donc testable hors jeu.
 */
public final class SettingsEditor {

    public static final List<String> FIELDS = List.of(
            "entity", "respawn", "leash", "leash_return", "leash_heal", "leash_invulnerable", "leash_timeout", "leash_speed",
            "wander", "activation", "rank", "tag", "untag", "name", "nbt", "initialize", "min_player_distance",
            "credit", "drop", "counter", "level", "attribute", "clear");

    /** Attributs proposes en completion (identifiants vanilla les plus utiles). */
    public static final List<String> COMMON_ATTRIBUTES = List.of(
            "minecraft:max_health", "minecraft:attack_damage", "minecraft:armor", "minecraft:armor_toughness",
            "minecraft:movement_speed", "minecraft:knockback_resistance", "minecraft:attack_knockback",
            "minecraft:follow_range", "minecraft:scale");

    /** Verification d'attribut, remplacable par le harnais hors jeu. */
    public static java.util.function.Predicate<Identifier> attributeCheck = id -> Registries.ATTRIBUTE.containsId(id);

    /** Verifications de registre, remplacables par le harnais hors jeu (pas de registres charges). */
    public static java.util.function.Predicate<Identifier> entityCheck = Spawner::entityTypeExists;
    public static java.util.function.Predicate<Identifier> itemCheck = id -> Registries.ITEM.containsId(id);

    public static final List<String> CLEARABLE = List.of(
            "entity", "respawn", "leash", "wander", "activation", "rank", "tags", "drops", "counters", "conditions",
            "nbt", "name", "initialize", "credit", "level", "attributes");

    public record Outcome(Optional<SpawnSettings> settings, String message) {
        static Outcome ok(SpawnSettings settings, String message) {
            return new Outcome(Optional.of(settings), message);
        }

        static Outcome fail(String message) {
            return new Outcome(Optional.empty(), message);
        }

        public boolean ok() {
            return this.settings.isPresent();
        }
    }

    private SettingsEditor() {
    }

    public static String usage() {
        return "set entity <type> | respawn <s> | respawn <min> <max> | respawn fixed <s> | leash <r> | leash_return <r> | "
                + "leash_heal <bool> | leash_invulnerable <bool> | leash_timeout <s> | leash_speed <x> | wander <r> | activation <r> | "
                + "rank <rang> | tag <cle> <valeur> | untag <cle> | name <texte...> | name clear | nbt <snbt...> | nbt clear | "
                + "initialize <bool> | min_player_distance <n> | credit killer|participants|party_nearby | "
                + "drop add <objet> [nombre] [chance%] [personal|ground] | drop remove <objet> | drop clear | "
                + "counter add <objectif> | counter remove <objectif> | counter clear | level <n> (PV x n) | "
                + "attribute <attribut> <valeur> | attribute clear <attribut> | clear <champ>";
    }

    public static Outcome apply(SpawnSettings current, List<String> args, SpawnsConfig config) {
        if (args.isEmpty()) {
            return Outcome.fail("champ manquant. " + usage());
        }
        String field = args.get(0).toLowerCase(Locale.ROOT);
        List<String> rest = args.subList(1, args.size());
        try {
            return switch (field) {
                case "entity" -> entity(current, rest);
                case "respawn" -> respawn(current, rest);
                case "leash" -> leash(current, rest, config);
                case "leash_return" -> leashPart(current, rest, config, "return");
                case "leash_heal" -> leashPart(current, rest, config, "heal");
                case "leash_invulnerable" -> leashPart(current, rest, config, "invulnerable");
                case "leash_timeout" -> leashPart(current, rest, config, "timeout");
                case "leash_speed" -> leashPart(current, rest, config, "speed");
                case "wander" -> intField(rest, 0, 512, v -> Outcome.ok(current.withWanderRadius(Optional.of(v)), "zone de marche = " + v + " blocs"));
                case "activation" -> intField(rest, 16, config.maxActivationRadius, v -> Outcome.ok(current.withActivationRadius(Optional.of(v)), "rayon d'activation = " + v + " blocs"));
                case "rank" -> rank(current, rest, config);
                case "tag" -> tag(current, rest);
                case "untag" -> untag(current, rest);
                case "name" -> name(current, rest);
                case "nbt" -> nbt(current, rest);
                case "initialize" -> bool(rest, v -> Outcome.ok(current.withInitialize(Optional.of(v)), "initialize = " + v));
                case "min_player_distance" -> intField(rest, 0, 256, v -> {
                    RespawnConditions base = current.conditions().orElse(new RespawnConditions(true, config.defaultMinPlayerDistance));
                    return Outcome.ok(current.withConditions(Optional.of(base.withMinPlayerDistance(v))), "distance minimale d'un joueur = " + v + " blocs");
                });
                case "credit" -> credit(current, rest);
                case "drop" -> drop(current, rest);
                case "counter" -> counter(current, rest);
                case "level" -> intField(rest, 1, config.maxLevel, v -> Outcome.ok(current.withLevel(Optional.of(v)),
                        "niveau = " + v + " (PV x" + (1 + (v - 1) * config.levelHealthFactor) + ")"));
                case "attribute" -> attribute(current, rest);
                case "clear" -> clear(current, rest);
                default -> Outcome.fail("champ inconnu « " + field + " ». " + usage());
            };
        } catch (NumberFormatException e) {
            return Outcome.fail("nombre attendu : " + e.getMessage());
        }
    }

    private static Outcome entity(SpawnSettings current, List<String> rest) {
        if (rest.size() != 1) {
            return Outcome.fail("usage : set entity <namespace:type>");
        }
        Identifier id = Identifier.tryParse(rest.get(0));
        if (id == null) {
            return Outcome.fail("identifiant invalide : " + rest.get(0));
        }
        if (!entityCheck.test(id)) {
            return Outcome.fail("type d'entite inconnu sur ce serveur : " + id);
        }
        return Outcome.ok(current.withEntity(Optional.of(id)), "entite = " + id);
    }

    private static Outcome respawn(SpawnSettings current, List<String> rest) {
        if (rest.size() == 1) {
            int s = Integer.parseInt(rest.get(0));
            return Outcome.ok(current.withRespawn(Optional.of(Respawn.fixed(s))), "reapparition fixe = " + s + " s");
        }
        if (rest.size() == 2 && rest.get(0).equalsIgnoreCase("fixed")) {
            int s = Integer.parseInt(rest.get(1));
            return Outcome.ok(current.withRespawn(Optional.of(Respawn.fixed(s))), "reapparition fixe = " + s + " s");
        }
        if (rest.size() == 2) {
            int min = Integer.parseInt(rest.get(0));
            int max = Integer.parseInt(rest.get(1));
            if (max < min) {
                return Outcome.fail("le maximum doit etre >= au minimum");
            }
            return Outcome.ok(current.withRespawn(Optional.of(Respawn.random(min, max))), "reapparition aleatoire = " + min + "-" + max + " s");
        }
        return Outcome.fail("usage : set respawn <secondes> | set respawn <min> <max> | set respawn fixed <secondes>");
    }

    private static Leash baseLeash(SpawnSettings current, SpawnsConfig config) {
        return current.leash().orElseGet(() -> config.baseSettings().leash().orElseThrow());
    }

    private static Outcome leash(SpawnSettings current, List<String> rest, SpawnsConfig config) {
        if (rest.size() != 1) {
            return Outcome.fail("usage : set leash <rayon> (0 = aucune laisse)");
        }
        int radius = Integer.parseInt(rest.get(0));
        if (radius < 0 || radius > 1024) {
            return Outcome.fail("rayon entre 0 et 1024");
        }
        return Outcome.ok(current.withLeash(Optional.of(baseLeash(current, config).withRadius(radius))), "laisse = " + (radius == 0 ? "aucune" : radius + " blocs"));
    }

    private static Outcome leashPart(SpawnSettings current, List<String> rest, SpawnsConfig config, String part) {
        if (rest.size() != 1) {
            return Outcome.fail("usage : set leash_" + part + " <valeur>");
        }
        Leash base = baseLeash(current, config);
        String v = rest.get(0);
        Leash updated = switch (part) {
            case "return" -> base.withReturnRadius(Math.max(1, Integer.parseInt(v)));
            case "heal" -> base.withHeal(parseBool(v));
            case "invulnerable" -> base.withInvulnerable(parseBool(v));
            case "timeout" -> base.withTimeout(Math.max(1, Integer.parseInt(v)));
            case "speed" -> base.withSpeed(Math.max(0.1D, Double.parseDouble(v)));
            default -> base;
        };
        return Outcome.ok(current.withLeash(Optional.of(updated)), "laisse : " + updated.describe());
    }

    private static Outcome rank(SpawnSettings current, List<String> rest, SpawnsConfig config) {
        if (rest.size() != 1) {
            return Outcome.fail("usage : set rank <" + String.join("|", config.ranks) + ">");
        }
        String rank = rest.get(0).toLowerCase(Locale.ROOT);
        if (!config.ranks.contains(rank)) {
            return Outcome.fail("rang inconnu « " + rank + "» ; rangs de la configuration : " + String.join(", ", config.ranks));
        }
        return Outcome.ok(current.withRank(Optional.of(rank)), "rang = " + rank);
    }

    private static Outcome tag(SpawnSettings current, List<String> rest) {
        if (rest.size() != 2) {
            return Outcome.fail("usage : set tag <cle> <valeur>");
        }
        String key = rest.get(0).toLowerCase(Locale.ROOT);
        String value = rest.get(1).toLowerCase(Locale.ROOT);
        if (!key.matches("[a-z0-9_-]+") || !value.matches("[a-z0-9_-]+")) {
            return Outcome.fail("cle et valeur : a-z, 0-9, _ et - seulement");
        }
        return Outcome.ok(current.withTag(key, value), "tag " + key + " = " + value);
    }

    private static Outcome untag(SpawnSettings current, List<String> rest) {
        if (rest.size() != 1) {
            return Outcome.fail("usage : set untag <cle>");
        }
        return Outcome.ok(current.withoutTag(rest.get(0).toLowerCase(Locale.ROOT)), "tag " + rest.get(0) + " retire");
    }

    private static Outcome name(SpawnSettings current, List<String> rest) {
        if (rest.isEmpty()) {
            return Outcome.fail("usage : set name <texte...> | set name clear");
        }
        if (rest.size() == 1 && rest.get(0).equalsIgnoreCase("clear")) {
            return Outcome.ok(current.withCustomName(Optional.empty()), "nom personnalise retire");
        }
        String name = String.join(" ", rest);
        return Outcome.ok(current.withCustomName(Optional.of(name)), "nom = " + name);
    }

    private static Outcome nbt(SpawnSettings current, List<String> rest) {
        if (rest.isEmpty()) {
            return Outcome.fail("usage : set nbt <snbt...> | set nbt clear");
        }
        if (rest.size() == 1 && rest.get(0).equalsIgnoreCase("clear")) {
            return Outcome.ok(current.withNbt(Optional.empty()), "NBT retire");
        }
        String snbt = String.join(" ", rest);
        if (!snbt.trim().startsWith("{") || !snbt.trim().endsWith("}")) {
            return Outcome.fail("le NBT doit etre un compose { ... }");
        }
        return Outcome.ok(current.withNbt(Optional.of(snbt)), "NBT = " + snbt);
    }

    private static Outcome credit(SpawnSettings current, List<String> rest) {
        if (rest.size() != 1 || CreditMode.byId(rest.get(0).toLowerCase(Locale.ROOT)) == null) {
            return Outcome.fail("usage : set credit killer|participants|party_nearby");
        }
        CreditMode mode = CreditMode.byId(rest.get(0).toLowerCase(Locale.ROOT));
        return Outcome.ok(current.withCreditMode(Optional.of(mode)), "credit = " + mode.asString());
    }

    private static Outcome drop(SpawnSettings current, List<String> rest) {
        if (rest.isEmpty()) {
            return Outcome.fail("usage : set drop add <objet> [nombre] [chance%] [personal|ground] | drop remove <objet> | drop clear");
        }
        String sub = rest.get(0).toLowerCase(Locale.ROOT);
        List<QuestDrop> drops = new ArrayList<>(current.drops().orElse(List.of()));
        switch (sub) {
            case "clear" -> {
                return Outcome.ok(current.withDrops(Optional.of(List.of())), "objets de quete retires");
            }
            case "remove" -> {
                if (rest.size() != 2) {
                    return Outcome.fail("usage : set drop remove <objet>");
                }
                Identifier id = Identifier.tryParse(rest.get(1));
                boolean removed = id != null && drops.removeIf(d -> d.item().equals(id));
                return removed ? Outcome.ok(current.withDrops(Optional.of(drops)), "objet " + id + " retire")
                        : Outcome.fail("aucun objet de quete " + rest.get(1));
            }
            case "add" -> {
                if (rest.size() < 2) {
                    return Outcome.fail("usage : set drop add <objet> [nombre] [chance%] [personal|ground]");
                }
                Identifier id = Identifier.tryParse(rest.get(1));
                if (id == null) {
                    return Outcome.fail("identifiant d'objet invalide : " + rest.get(1));
                }
                if (!itemCheck.test(id)) {
                    return Outcome.fail("objet inconnu sur ce serveur : " + id);
                }
                int count = rest.size() > 2 ? Integer.parseInt(rest.get(2)) : 1;
                double chance = rest.size() > 3 ? Double.parseDouble(rest.get(3).replace("%", "")) : 100.0D;
                boolean personal = rest.size() > 4 ? !rest.get(4).equalsIgnoreCase("ground") : true;
                if (count < 1 || chance <= 0 || chance > 100) {
                    return Outcome.fail("nombre >= 1 et chance dans ]0 ; 100]");
                }
                drops.removeIf(d -> d.item().equals(id));
                QuestDrop drop = new QuestDrop(id, count, chance, personal);
                drops.add(drop);
                return Outcome.ok(current.withDrops(Optional.of(drops)), "objet de quete : " + drop.describe());
            }
            default -> {
                return Outcome.fail("usage : set drop add|remove|clear ...");
            }
        }
    }

    private static Outcome counter(SpawnSettings current, List<String> rest) {
        if (rest.isEmpty()) {
            return Outcome.fail("usage : set counter add <objectif> | counter remove <objectif> | counter clear");
        }
        String sub = rest.get(0).toLowerCase(Locale.ROOT);
        List<String> counters = new ArrayList<>(current.counters().orElse(List.of()));
        switch (sub) {
            case "clear" -> {
                return Outcome.ok(current.withCounters(Optional.of(List.of())), "compteurs retires");
            }
            case "add" -> {
                if (rest.size() != 2 || !rest.get(1).matches("[A-Za-z0-9_.+-]{1,64}")) {
                    return Outcome.fail("usage : set counter add <objectif> (lettres, chiffres, _ . + -)");
                }
                if (!counters.contains(rest.get(1))) {
                    counters.add(rest.get(1));
                }
                return Outcome.ok(current.withCounters(Optional.of(counters)), "compteur ajoute : " + rest.get(1));
            }
            case "remove" -> {
                if (rest.size() != 2) {
                    return Outcome.fail("usage : set counter remove <objectif>");
                }
                boolean removed = counters.remove(rest.get(1));
                return removed ? Outcome.ok(current.withCounters(Optional.of(counters)), "compteur retire : " + rest.get(1))
                        : Outcome.fail("compteur absent : " + rest.get(1));
            }
            default -> {
                return Outcome.fail("usage : set counter add|remove|clear ...");
            }
        }
    }

    private static Outcome attribute(SpawnSettings current, List<String> rest) {
        if (rest.size() == 2 && rest.get(0).equalsIgnoreCase("clear")) {
            Identifier id = Identifier.tryParse(rest.get(1).contains(":") ? rest.get(1) : "minecraft:" + rest.get(1));
            if (id == null || !current.attributes().containsKey(id.toString())) {
                return Outcome.fail("aucun attribut " + rest.get(1) + " a ce niveau");
            }
            return Outcome.ok(current.withoutAttribute(id.toString()), "attribut " + id + " retire");
        }
        if (rest.size() != 2) {
            return Outcome.fail("usage : set attribute <attribut> <valeur> | set attribute clear <attribut> — ex. attack_damage 12, max_health 80, movement_speed 0.3, scale 1.5");
        }
        Identifier id = Identifier.tryParse(rest.get(0).contains(":") ? rest.get(0) : "minecraft:" + rest.get(0));
        if (id == null || !attributeCheck.test(id)) {
            return Outcome.fail("attribut inconnu : " + rest.get(0) + " (ex. " + String.join(", ", COMMON_ATTRIBUTES) + ")");
        }
        double value = Double.parseDouble(rest.get(1));
        if (Double.isNaN(value) || Double.isInfinite(value)) {
            return Outcome.fail("valeur invalide");
        }
        return Outcome.ok(current.withAttribute(id.toString(), value), "attribut " + id + " = " + value);
    }

    private static Outcome clear(SpawnSettings current, List<String> rest) {
        if (rest.size() != 1) {
            return Outcome.fail("usage : set clear <" + String.join("|", CLEARABLE) + ">");
        }
        String field = rest.get(0).toLowerCase(Locale.ROOT);
        SpawnSettings updated = switch (field) {
            case "entity" -> current.withEntity(Optional.empty());
            case "respawn" -> current.withRespawn(Optional.empty());
            case "leash" -> current.withLeash(Optional.empty());
            case "wander" -> current.withWanderRadius(Optional.empty());
            case "activation" -> current.withActivationRadius(Optional.empty());
            case "rank" -> current.withRank(Optional.empty());
            case "tags" -> current.withTags(Map.of());
            case "drops" -> current.withDrops(Optional.empty());
            case "counters" -> current.withCounters(Optional.empty());
            case "conditions" -> current.withConditions(Optional.empty());
            case "nbt" -> current.withNbt(Optional.empty());
            case "name" -> current.withCustomName(Optional.empty());
            case "initialize" -> current.withInitialize(Optional.empty());
            case "credit" -> current.withCreditMode(Optional.empty());
            case "level" -> current.withLevel(Optional.empty());
            case "attributes" -> current.withAttributes(Map.of());
            default -> null;
        };
        if (updated == null) {
            return Outcome.fail("champ inconnu « " + field + " » ; effacables : " + String.join(", ", CLEARABLE));
        }
        return Outcome.ok(updated, "champ " + field + " efface (herite du niveau du dessous)");
    }

    private static Outcome intField(List<String> rest, int min, int max, java.util.function.IntFunction<Outcome> apply) {
        if (rest.size() != 1) {
            return Outcome.fail("une valeur entiere attendue");
        }
        int v = Integer.parseInt(rest.get(0));
        if (v < min || v > max) {
            return Outcome.fail("valeur entre " + min + " et " + max);
        }
        return apply.apply(v);
    }

    private static Outcome bool(List<String> rest, java.util.function.Function<Boolean, Outcome> apply) {
        if (rest.size() != 1) {
            return Outcome.fail("true ou false attendu");
        }
        return apply.apply(parseBool(rest.get(0)));
    }

    private static boolean parseBool(String s) {
        return s.equalsIgnoreCase("true") || s.equalsIgnoreCase("oui") || s.equalsIgnoreCase("yes") || s.equals("1");
    }

    /** Les champs definis a ce niveau, une ligne par champ. */
    public static List<String> describe(SpawnSettings s) {
        List<String> lines = new ArrayList<>();
        s.entity().ifPresent(v -> lines.add("entity = " + v));
        s.respawn().ifPresent(v -> lines.add("respawn = " + v.describe()));
        s.leash().ifPresent(v -> lines.add("leash = " + v.describe()));
        s.wanderRadius().ifPresent(v -> lines.add("wander = " + v));
        s.activationRadius().ifPresent(v -> lines.add("activation = " + v));
        s.rank().ifPresent(v -> lines.add("rank = " + v));
        if (!s.tags().isEmpty()) {
            lines.add("tags = " + s.tags());
        }
        s.drops().ifPresent(v -> lines.add("quest_drops = " + v.stream().map(QuestDrop::describe).toList()));
        s.counters().ifPresent(v -> lines.add("counters = " + v));
        s.conditions().ifPresent(v -> lines.add("min_player_distance = " + v.minPlayerDistance()));
        s.nbt().ifPresent(v -> lines.add("nbt = " + v));
        s.customName().ifPresent(v -> lines.add("name = " + v));
        s.initialize().ifPresent(v -> lines.add("initialize = " + v));
        s.creditMode().ifPresent(v -> lines.add("credit = " + v.asString()));
        s.level().ifPresent(v -> lines.add("level = " + v));
        if (!s.attributes().isEmpty()) {
            lines.add("attributes = " + s.attributes());
        }
        return lines;
    }
}
