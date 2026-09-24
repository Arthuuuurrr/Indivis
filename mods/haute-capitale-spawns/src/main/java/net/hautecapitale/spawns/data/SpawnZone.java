package net.hautecapitale.spawns.data;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.util.Identifier;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * Une zone : un camp, des ruines, un cimetiere... un groupe nomme de points.
 *
 * <p>Tous les points d'une zone vivent dans la meme dimension. La zone porte
 * des reglages par defaut herites par ses points, et un interrupteur global.
 *
 * @param id identifiant simple (= nom du fichier)
 * @param displayName nom lisible facultatif
 * @param dimension identifiant de la dimension ({@code minecraft:overworld} par defaut)
 * @param enabled faux = aucun point de la zone n'agit
 * @param defaults reglages herites par tous les points
 * @param points les points, dans l'ordre du fichier
 */
public record SpawnZone(String id, Optional<String> displayName, Identifier dimension, boolean enabled,
                        SpawnSettings defaults, List<SpawnPoint> points) {

    public static final Identifier OVERWORLD = Identifier.of("minecraft", "overworld");

    public static final Codec<SpawnZone> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            Codec.STRING.fieldOf("id").forGetter(SpawnZone::id),
            Codec.STRING.optionalFieldOf("display_name").forGetter(SpawnZone::displayName),
            Identifier.CODEC.optionalFieldOf("dimension", OVERWORLD).forGetter(SpawnZone::dimension),
            Codec.BOOL.optionalFieldOf("enabled", true).forGetter(SpawnZone::enabled),
            SpawnSettings.CODEC.optionalFieldOf("defaults", SpawnSettings.EMPTY).forGetter(SpawnZone::defaults),
            SpawnPoint.CODEC.listOf().optionalFieldOf("points", List.of()).forGetter(SpawnZone::points)
    ).apply(instance, SpawnZone::new));

    public static SpawnZone create(String id, Identifier dimension) {
        return new SpawnZone(id, Optional.empty(), dimension, true, SpawnSettings.EMPTY, List.of());
    }

    public Optional<SpawnPoint> point(String pointId) {
        for (SpawnPoint point : this.points) {
            if (point.id().equals(pointId)) {
                return Optional.of(point);
            }
        }
        return Optional.empty();
    }

    public boolean hasPoint(String pointId) {
        return this.point(pointId).isPresent();
    }

    public SpawnZone withEnabled(boolean v) {
        return new SpawnZone(id, displayName, dimension, v, defaults, points);
    }

    public SpawnZone withDisplayName(Optional<String> v) {
        return new SpawnZone(id, v, dimension, enabled, defaults, points);
    }

    public SpawnZone withDefaults(SpawnSettings v) {
        return new SpawnZone(id, displayName, dimension, enabled, v, points);
    }

    public SpawnZone withDimension(Identifier v) {
        return new SpawnZone(id, displayName, v, enabled, defaults, points);
    }

    /** Remplace le point de meme identifiant, ou l'ajoute a la fin. */
    public SpawnZone withPoint(SpawnPoint point) {
        List<SpawnPoint> copy = new ArrayList<>(this.points);
        boolean replaced = false;
        for (int i = 0; i < copy.size(); i++) {
            if (copy.get(i).id().equals(point.id())) {
                copy.set(i, point);
                replaced = true;
                break;
            }
        }
        if (!replaced) {
            copy.add(point);
        }
        return new SpawnZone(id, displayName, dimension, enabled, defaults, List.copyOf(copy));
    }

    public SpawnZone withoutPoint(String pointId) {
        List<SpawnPoint> copy = new ArrayList<>(this.points);
        copy.removeIf(p -> p.id().equals(pointId));
        return new SpawnZone(id, displayName, dimension, enabled, defaults, List.copyOf(copy));
    }

    public SpawnZone withPoints(List<SpawnPoint> v) {
        return new SpawnZone(id, displayName, dimension, enabled, defaults, List.copyOf(v));
    }

    public String label() {
        return this.displayName.map(n -> n + " (" + this.id + ")").orElse(this.id);
    }
}
