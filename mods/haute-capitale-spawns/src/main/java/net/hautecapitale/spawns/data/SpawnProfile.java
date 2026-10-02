package net.hautecapitale.spawns.data;

/**
 * Un profil : un jeu de reglages nomme et reutilisable ({@code orc_warrior_normal},
 * {@code skeleton_elite}...). Un point qui le reference herite de tout ce qu'il
 * contient et ne garde que ses propres surcharges.
 *
 * <p>Le nom est celui du fichier {@code profiles/<nom>.json} ; le contenu du
 * fichier est un {@link SpawnSettings} a plat.
 */
public record SpawnProfile(String name, SpawnSettings settings) {

    public SpawnProfile withSettings(SpawnSettings v) {
        return new SpawnProfile(this.name, v);
    }
}
