package net.hautecapitale.spawns.data;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.util.Identifier;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * Un jeu de reglages partiel, superposable.
 *
 * <p>C'est la brique commune des trois niveaux de configuration : les valeurs
 * par defaut d'une zone, un profil, et les surcharges propres a un point.
 * Chaque champ absent signifie « herite du niveau du dessous ». La resolution
 * finale (voir {@link Resolved}) empile : configuration globale, puis zone,
 * puis profil, puis point.
 *
 * <p>{@code level} : 1 = mob tel quel, 2 = points de vie doubles, 3 = triples
 * (facteur reglable dans la configuration). {@code attributes} : valeurs de
 * base d'attributs imposees a l'apparition ({@code minecraft:attack_damage: 12},
 * {@code minecraft:max_health: 80}...), pour trafiquer un mob sans NBT.
 */
public record SpawnSettings(
        Optional<Identifier> entity,
        Optional<Respawn> respawn,
        Optional<Leash> leash,
        Optional<Integer> activationRadius,
        Optional<Integer> wanderRadius,
        Optional<String> rank,
        Map<String, String> tags,
        Optional<List<QuestDrop>> drops,
        Optional<List<String>> counters,
        Optional<RespawnConditions> conditions,
        Optional<String> nbt,
        Optional<String> customName,
        Optional<Boolean> initialize,
        Optional<CreditMode> creditMode,
        Optional<Integer> level,
        Map<String, Double> attributes) {

    public static final SpawnSettings EMPTY = new SpawnSettings(Optional.empty(), Optional.empty(), Optional.empty(),
            Optional.empty(), Optional.empty(), Optional.empty(), Map.of(), Optional.empty(), Optional.empty(),
            Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(), Map.of());

    /** Les champs a plat, pour les inliner dans un point ou un profil. */
    public static final MapCodec<SpawnSettings> MAP_CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
            Identifier.CODEC.optionalFieldOf("entity").forGetter(SpawnSettings::entity),
            Respawn.CODEC.optionalFieldOf("respawn").forGetter(SpawnSettings::respawn),
            Leash.CODEC.optionalFieldOf("leash").forGetter(SpawnSettings::leash),
            Codec.INT.optionalFieldOf("activation_radius").forGetter(SpawnSettings::activationRadius),
            Codec.INT.optionalFieldOf("wander_radius").forGetter(SpawnSettings::wanderRadius),
            Codec.STRING.optionalFieldOf("rank").forGetter(SpawnSettings::rank),
            Codec.unboundedMap(Codec.STRING, Codec.STRING).optionalFieldOf("tags", Map.of()).forGetter(SpawnSettings::tags),
            QuestDrop.CODEC.listOf().optionalFieldOf("quest_drops").forGetter(SpawnSettings::drops),
            Codec.STRING.listOf().optionalFieldOf("counters").forGetter(SpawnSettings::counters),
            RespawnConditions.CODEC.optionalFieldOf("respawn_conditions").forGetter(SpawnSettings::conditions),
            Codec.STRING.optionalFieldOf("nbt").forGetter(SpawnSettings::nbt),
            Codec.STRING.optionalFieldOf("custom_name").forGetter(SpawnSettings::customName),
            Codec.BOOL.optionalFieldOf("initialize").forGetter(SpawnSettings::initialize),
            CreditMode.CODEC.optionalFieldOf("credit_mode").forGetter(SpawnSettings::creditMode),
            Codec.INT.optionalFieldOf("level").forGetter(SpawnSettings::level),
            Codec.unboundedMap(Codec.STRING, Codec.DOUBLE).optionalFieldOf("attributes", Map.of()).forGetter(SpawnSettings::attributes)
    ).apply(instance, SpawnSettings::new));

    public static final Codec<SpawnSettings> CODEC = MAP_CODEC.codec();

    /** Un jeu ne contenant que l'entite. */
    public static SpawnSettings ofEntity(Identifier entity) {
        return EMPTY.withEntity(Optional.of(entity));
    }

    /** {@code over} l'emporte sur {@code this}, champ par champ ; les tags et attributs fusionnent. */
    public SpawnSettings overlay(SpawnSettings over) {
        Map<String, String> mergedTags = new LinkedHashMap<>(this.tags);
        mergedTags.putAll(over.tags);
        Map<String, Double> mergedAttributes = new LinkedHashMap<>(this.attributes);
        mergedAttributes.putAll(over.attributes);
        return new SpawnSettings(
                over.entity.or(() -> this.entity),
                over.respawn.or(() -> this.respawn),
                over.leash.or(() -> this.leash),
                over.activationRadius.or(() -> this.activationRadius),
                over.wanderRadius.or(() -> this.wanderRadius),
                over.rank.or(() -> this.rank),
                Map.copyOf(mergedTags),
                over.drops.or(() -> this.drops),
                over.counters.or(() -> this.counters),
                over.conditions.or(() -> this.conditions),
                over.nbt.or(() -> this.nbt),
                over.customName.or(() -> this.customName),
                over.initialize.or(() -> this.initialize),
                over.creditMode.or(() -> this.creditMode),
                over.level.or(() -> this.level),
                Map.copyOf(mergedAttributes));
    }

    public boolean isEmpty() {
        return this.equals(EMPTY);
    }

    // --- withers -----------------------------------------------------------------

    public SpawnSettings withEntity(Optional<Identifier> v) {
        return new SpawnSettings(v, respawn, leash, activationRadius, wanderRadius, rank, tags, drops, counters, conditions, nbt, customName, initialize, creditMode, level, attributes);
    }

    public SpawnSettings withRespawn(Optional<Respawn> v) {
        return new SpawnSettings(entity, v, leash, activationRadius, wanderRadius, rank, tags, drops, counters, conditions, nbt, customName, initialize, creditMode, level, attributes);
    }

    public SpawnSettings withLeash(Optional<Leash> v) {
        return new SpawnSettings(entity, respawn, v, activationRadius, wanderRadius, rank, tags, drops, counters, conditions, nbt, customName, initialize, creditMode, level, attributes);
    }

    public SpawnSettings withActivationRadius(Optional<Integer> v) {
        return new SpawnSettings(entity, respawn, leash, v, wanderRadius, rank, tags, drops, counters, conditions, nbt, customName, initialize, creditMode, level, attributes);
    }

    public SpawnSettings withWanderRadius(Optional<Integer> v) {
        return new SpawnSettings(entity, respawn, leash, activationRadius, v, rank, tags, drops, counters, conditions, nbt, customName, initialize, creditMode, level, attributes);
    }

    public SpawnSettings withRank(Optional<String> v) {
        return new SpawnSettings(entity, respawn, leash, activationRadius, wanderRadius, v, tags, drops, counters, conditions, nbt, customName, initialize, creditMode, level, attributes);
    }

    public SpawnSettings withTags(Map<String, String> v) {
        return new SpawnSettings(entity, respawn, leash, activationRadius, wanderRadius, rank, Map.copyOf(new LinkedHashMap<>(v)), drops, counters, conditions, nbt, customName, initialize, creditMode, level, attributes);
    }

    public SpawnSettings withTag(String key, String value) {
        Map<String, String> copy = new LinkedHashMap<>(this.tags);
        copy.put(key, value);
        return this.withTags(copy);
    }

    public SpawnSettings withoutTag(String key) {
        Map<String, String> copy = new LinkedHashMap<>(this.tags);
        copy.remove(key);
        return this.withTags(copy);
    }

    public SpawnSettings withDrops(Optional<List<QuestDrop>> v) {
        return new SpawnSettings(entity, respawn, leash, activationRadius, wanderRadius, rank, tags, v.map(List::copyOf), counters, conditions, nbt, customName, initialize, creditMode, level, attributes);
    }

    public SpawnSettings withCounters(Optional<List<String>> v) {
        return new SpawnSettings(entity, respawn, leash, activationRadius, wanderRadius, rank, tags, drops, v.map(List::copyOf), conditions, nbt, customName, initialize, creditMode, level, attributes);
    }

    public SpawnSettings withConditions(Optional<RespawnConditions> v) {
        return new SpawnSettings(entity, respawn, leash, activationRadius, wanderRadius, rank, tags, drops, counters, v, nbt, customName, initialize, creditMode, level, attributes);
    }

    public SpawnSettings withNbt(Optional<String> v) {
        return new SpawnSettings(entity, respawn, leash, activationRadius, wanderRadius, rank, tags, drops, counters, conditions, v, customName, initialize, creditMode, level, attributes);
    }

    public SpawnSettings withCustomName(Optional<String> v) {
        return new SpawnSettings(entity, respawn, leash, activationRadius, wanderRadius, rank, tags, drops, counters, conditions, nbt, v, initialize, creditMode, level, attributes);
    }

    public SpawnSettings withInitialize(Optional<Boolean> v) {
        return new SpawnSettings(entity, respawn, leash, activationRadius, wanderRadius, rank, tags, drops, counters, conditions, nbt, customName, v, creditMode, level, attributes);
    }

    public SpawnSettings withCreditMode(Optional<CreditMode> v) {
        return new SpawnSettings(entity, respawn, leash, activationRadius, wanderRadius, rank, tags, drops, counters, conditions, nbt, customName, initialize, v, level, attributes);
    }

    public SpawnSettings withLevel(Optional<Integer> v) {
        return new SpawnSettings(entity, respawn, leash, activationRadius, wanderRadius, rank, tags, drops, counters, conditions, nbt, customName, initialize, creditMode, v, attributes);
    }

    public SpawnSettings withAttributes(Map<String, Double> v) {
        return new SpawnSettings(entity, respawn, leash, activationRadius, wanderRadius, rank, tags, drops, counters, conditions, nbt, customName, initialize, creditMode, level, Map.copyOf(new LinkedHashMap<>(v)));
    }

    public SpawnSettings withAttribute(String id, double value) {
        Map<String, Double> copy = new LinkedHashMap<>(this.attributes);
        copy.put(id, value);
        return this.withAttributes(copy);
    }

    public SpawnSettings withoutAttribute(String id) {
        Map<String, Double> copy = new LinkedHashMap<>(this.attributes);
        copy.remove(id);
        return this.withAttributes(copy);
    }
}
