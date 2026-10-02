package net.hautecapitale.spawns.api;

import net.minecraft.registry.RegistryKey;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

import java.util.Map;
import java.util.UUID;

/**
 * La carte d'identite d'un mob controle, telle que les autres modules la voient.
 *
 * <p>Immuable et sans reference au monde : peut etre gardee, journalisee, envoyee.
 *
 * @param zoneId zone d'appartenance ({@code orc_camp_01})
 * @param pointId point dans la zone ({@code archer_01})
 * @param fullId {@code zone.point}
 * @param entityType identifiant de registre du type d'entite
 * @param rank {@code normal}, {@code elite}, {@code mini_boss}, {@code world_boss}... (libre, voir config)
 * @param tags tags de gameplay ({@code mob_type=orc}, {@code role=archer}...)
 * @param dimension dimension du point
 * @param home position d'origine (le point)
 * @param token jeton de cette apparition precise
 */
public record ControlledMob(String zoneId, String pointId, String fullId, Identifier entityType, String rank,
                            Map<String, String> tags, RegistryKey<World> dimension, Vec3d home, UUID token, int level) {

    public String tag(String key) {
        return this.tags.get(key);
    }

    public boolean hasTag(String key, String value) {
        return value != null && value.equals(this.tags.get(key));
    }

    public boolean isRank(String rank) {
        return this.rank.equalsIgnoreCase(rank);
    }
}
