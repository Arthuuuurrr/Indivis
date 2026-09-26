package net.hautecapitale.spawns.data;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

import java.util.Optional;

/**
 * Un point de spawn : un lieu d'apparition pour exactement un mob controle.
 *
 * <p>Le point n'est que l'origine du mob. Une fois apparu, le mob vit sa vie
 * normale (marche, patrouille, combat) dans la limite de sa laisse.
 *
 * @param id identifiant simple, unique dans la zone
 * @param enabled faux = aucun mob, aucun timer
 * @param profile profil dont heriter, s'il y en a un
 * @param settings surcharges propres au point (l'entite peut venir du profil ou de la zone)
 */
public record SpawnPoint(String id, boolean enabled, Optional<String> profile,
                         double x, double y, double z, float yaw, float pitch,
                         SpawnSettings settings) {

    public static final Codec<SpawnPoint> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            Codec.STRING.fieldOf("id").forGetter(SpawnPoint::id),
            Codec.BOOL.optionalFieldOf("enabled", true).forGetter(SpawnPoint::enabled),
            Codec.STRING.optionalFieldOf("profile").forGetter(SpawnPoint::profile),
            Codec.DOUBLE.fieldOf("x").forGetter(SpawnPoint::x),
            Codec.DOUBLE.fieldOf("y").forGetter(SpawnPoint::y),
            Codec.DOUBLE.fieldOf("z").forGetter(SpawnPoint::z),
            Codec.FLOAT.optionalFieldOf("yaw", 0.0F).forGetter(SpawnPoint::yaw),
            Codec.FLOAT.optionalFieldOf("pitch", 0.0F).forGetter(SpawnPoint::pitch),
            SpawnSettings.MAP_CODEC.forGetter(SpawnPoint::settings)
    ).apply(instance, SpawnPoint::new));

    public static SpawnPoint at(String id, double x, double y, double z, float yaw, float pitch, SpawnSettings settings) {
        return new SpawnPoint(id, true, Optional.empty(), x, y, z, yaw, pitch, settings);
    }

    public SpawnPoint withEnabled(boolean v) {
        return new SpawnPoint(id, v, profile, x, y, z, yaw, pitch, settings);
    }

    public SpawnPoint withProfile(Optional<String> v) {
        return new SpawnPoint(id, enabled, v, x, y, z, yaw, pitch, settings);
    }

    public SpawnPoint withPosition(double nx, double ny, double nz, float nyaw, float npitch) {
        return new SpawnPoint(id, enabled, profile, nx, ny, nz, nyaw, npitch, settings);
    }

    public SpawnPoint withRotation(float nyaw, float npitch) {
        return new SpawnPoint(id, enabled, profile, x, y, z, nyaw, npitch, settings);
    }

    public SpawnPoint withSettings(SpawnSettings v) {
        return new SpawnPoint(id, enabled, profile, x, y, z, yaw, pitch, v);
    }

    public SpawnPoint withId(String newId) {
        return new SpawnPoint(newId, enabled, profile, x, y, z, yaw, pitch, settings);
    }

    public double squaredDistanceTo(double px, double py, double pz) {
        double dx = this.x - px;
        double dy = this.y - py;
        double dz = this.z - pz;
        return dx * dx + dy * dy + dz * dz;
    }

    public double squaredHorizontalDistanceTo(double px, double pz) {
        double dx = this.x - px;
        double dz = this.z - pz;
        return dx * dx + dz * dz;
    }

    public int chunkX() {
        return ((int) Math.floor(this.x)) >> 4;
    }

    public int chunkZ() {
        return ((int) Math.floor(this.z)) >> 4;
    }
}
