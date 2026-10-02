package net.hautecapitale.rpg.content;

import net.hautecapitale.rpg.rpgclass.RpgClass;
import net.minecraft.item.ItemStack;
import net.minecraft.registry.Registries;
import net.minecraft.util.Identifier;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * Rattache un objet du jeu à la ou les classes qui le revendiquent.
 *
 * <p>La règle est le <b>namespace</b> : un objet de <code>berserker_rpg:</code>
 * appartient au Berserker, un objet de <code>paladins:</code> appartient au
 * Paladin <i>et</i> au Prêtre, puisque le mod en livre deux.
 *
 * <p>Un objet dont le namespace n'est revendiqué par aucune classe est
 * <b>libre</b>. C'est le cas de tout le vanilla, mais aussi — par décision
 * explicite — d'Arsenal, Armory, Relics et Jewelry, qui restent des mods à part :
 * leurs équipements servent toutes les classes.
 *
 * <p>La table est construite une fois, à partir des données de {@link RpgClass}.
 * Aucun objet n'est nommé en dur : ajouter une classe suffit.
 */
public final class ClassContent {

    private static final Map<String, Set<RpgClass>> PAR_NAMESPACE = construire();

    private ClassContent() {
    }

    private static Map<String, Set<RpgClass>> construire() {
        Map<String, Set<RpgClass>> table = new HashMap<>();
        for (RpgClass rpgClass : RpgClass.values()) {
            for (String namespace : rpgClass.namespaces()) {
                table.computeIfAbsent(namespace, k -> new HashSet<>()).add(rpgClass);
            }
        }
        table.replaceAll((k, v) -> Set.copyOf(v));
        return Map.copyOf(table);
    }

    /** Classes qui revendiquent ce namespace. Vide si le namespace est libre. */
    public static Set<RpgClass> owners(String namespace) {
        return PAR_NAMESPACE.getOrDefault(namespace, Set.of());
    }

    /** Classes qui revendiquent cet objet. Vide si l'objet est libre. */
    public static Set<RpgClass> owners(ItemStack stack) {
        if (stack.isEmpty()) {
            return Set.of();
        }
        Identifier id = Registries.ITEM.getId(stack.getItem());
        return owners(id.getNamespace());
    }

    /** Vrai si l'objet n'appartient à aucune classe : utilisable par tout le monde. */
    public static boolean isFree(ItemStack stack) {
        return owners(stack).isEmpty();
    }

    /**
     * Un joueur de cette classe peut-il porter cet objet ?
     *
     * <p>Un objet libre est toujours autorisé. Un joueur sans classe n'est
     * restreint que sur les objets revendiqués.
     */
    public static boolean allowed(ItemStack stack, RpgClass rpgClass) {
        Set<RpgClass> owners = owners(stack);
        return owners.isEmpty() || (rpgClass != null && owners.contains(rpgClass));
    }

    /** Tous les namespaces revendiqués, pour diagnostic. */
    public static Set<String> claimedNamespaces() {
        return PAR_NAMESPACE.keySet();
    }
}
