package net.hautecapitale.rpg.ability;

import net.minecraft.util.Identifier;

/**
 * Pourquoi une capacité est, ou n'est pas, utilisable à cet instant.
 *
 * <p>Sur un arbre de plusieurs dizaines de capacités, « elle n'apparaît pas » est une
 * plainte impossible à instruire. Cet objet garde les quatre réponses séparées pour que
 * la commande de diagnostic puisse dire laquelle a manqué, plutôt qu'un simple non.
 */
public record AbilityStatus(
        Identifier id,
        AbilityDefinition definition,
        boolean defined,
        boolean learned,
        boolean weaponCompatible) {

    public boolean available() {
        return defined && learned && weaponCompatible;
    }

    /** La première raison qui bloque, dans l'ordre où elle est vérifiée. */
    public String reason() {
        if (!defined) {
            return "le sort " + definition.spell() + " n'existe pas sur ce serveur";
        }
        if (!learned) {
            return definition.requires_skill()
                    ? "capacité non débloquée dans l'arbre"
                    : "capacité intrinsèque non accordée (anomalie)";
        }
        if (!weaponCompatible) {
            return "l'arme tenue ne correspond pas à " + definition.weapon().describe();
        }
        return "disponible";
    }
}
