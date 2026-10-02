package net.hautecapitale.metiers.textile;

import net.hautecapitale.metiers.HauteCapitaleMetiers;
import net.minecraft.item.Item;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.util.Identifier;

import java.util.ArrayList;
import java.util.List;

/**
 * La chaîne textile du Couturier : coton → fibre → bobine → tissu →
 * composants → vêtement.
 *
 * <p>C'est la chaîne de ressources de Weaver's Paradise (MIT, Vortianski),
 * réécrite ici : ses objets, ses proportions, ses textures d'icônes pour les
 * matières et composants ; les vêtements ont des icônes dessinées pour ce mod,
 * parce que Weaver's les rendait par un modèle en couches sur le joueur. Ici,
 * un vêtement ne se porte pas : c'est un composant, la doublure d'une vraie
 * armure — décision du serveur, qui garde ainsi une seule chose à porter.
 *
 * <p>Aucun bloc à poser par le joueur : le cotonnier est un node de
 * l'Herboriste, posé par l'administrateur.
 */
public final class TextileItems {

    private static final List<Item> ORDERED = new ArrayList<>();

    // --- Fibres et fils
    public static final Item COTON = register("coton");
    public static final Item FIBRE_DE_COTON = register("fibre_de_coton");
    public static final Item BOBINE_VIDE = register("bobine_vide");
    public static final Item BOBINE_DE_COTON = register("bobine_de_coton");
    public static final Item BOBINE_DE_LAINE = register("bobine_de_laine");
    public static final Item BOBINE_DE_JEAN = register("bobine_de_jean");
    public static final Item BOBINE_DE_SOIE = register("bobine_de_soie");

    // --- Tissus
    public static final Item TISSU_DE_COTON = register("tissu_de_coton");
    public static final Item TISSU_DE_LAINE = register("tissu_de_laine");
    public static final Item TISSU_DE_JEAN = register("tissu_de_jean");
    public static final Item TISSU_DE_SOIE = register("tissu_de_soie");

    // --- Outils de couture
    public static final Item AIGUILLE = register("aiguille", s -> s.maxCount(16));
    public static final Item AIGUILLE_ENFILEE = register("aiguille_enfilee", s -> s.maxCount(16));

    // --- Composants de vêtement
    public static final Item BASE_DE_HAUT_EN_COTON = register("base_de_haut_en_coton");
    public static final Item BASE_DE_HAUT_EN_LAINE = register("base_de_haut_en_laine");
    public static final Item BASE_DE_HAUT_EN_SOIE = register("base_de_haut_en_soie");
    public static final Item MANCHE_COURTE_DE_COTON = register("manche_courte_de_coton");
    public static final Item MANCHE_LONGUE_DE_COTON = register("manche_longue_de_coton");
    public static final Item MANCHE_COURTE_DE_LAINE = register("manche_courte_de_laine");
    public static final Item MANCHE_LONGUE_DE_LAINE = register("manche_longue_de_laine");
    public static final Item MANCHE_COURTE_DE_SOIE = register("manche_courte_de_soie");
    public static final Item MANCHE_LONGUE_DE_SOIE = register("manche_longue_de_soie");
    public static final Item JAMBE_DE_COTON = register("jambe_de_coton");
    public static final Item JAMBE_DE_LAINE = register("jambe_de_laine");
    public static final Item JAMBE_DE_JEAN = register("jambe_de_jean");
    public static final Item JAMBE_DE_SOIE = register("jambe_de_soie");

    // --- Vêtements : des composants, pas des pièces d'équipement
    // Décision du serveur : un vêtement ne se porte pas, il entre dans la
    // fabrication d'une vraie armure (celle-là se porte). Un seul par pile.
    public static final Item DEBARDEUR = register("debardeur", s -> s.maxCount(1));
    public static final Item CHEMISE_DE_COTON = register("chemise_de_coton", s -> s.maxCount(1));
    public static final Item CHEMISE_DE_SOIE = register("chemise_de_soie", s -> s.maxCount(1));
    public static final Item PULL_DE_LAINE = register("pull_de_laine", s -> s.maxCount(1));
    public static final Item PANTALON_DE_COTON = register("pantalon_de_coton", s -> s.maxCount(1));
    public static final Item PANTALON_DE_LAINE = register("pantalon_de_laine", s -> s.maxCount(1));
    public static final Item PANTALON_DE_JEAN = register("pantalon_de_jean", s -> s.maxCount(1));
    public static final Item PANTALON_DE_SOIE = register("pantalon_de_soie", s -> s.maxCount(1));
    public static final Item BAS_DE_SOIE = register("bas_de_soie", s -> s.maxCount(1));
    public static final Item BONNET_DE_LAINE = register("bonnet_de_laine", s -> s.maxCount(1));
    public static final Item CAPE_DE_COTON = register("cape_de_coton", s -> s.maxCount(1));
    public static final Item CAPE_DE_LAINE = register("cape_de_laine", s -> s.maxCount(1));
    public static final Item CAPE_DE_SOIE = register("cape_de_soie", s -> s.maxCount(1));

    private TextileItems() {
    }

    public static void init() {
        // Les constantes ci-dessus s'enregistrent en se chargeant.
    }

    /** Les quarante objets textiles, dans l'ordre du catalogue. */
    public static List<Item> all() {
        return List.copyOf(ORDERED);
    }

    private static Item register(String path) {
        return register(path, settings -> settings);
    }

    private static Item register(String path, java.util.function.UnaryOperator<Item.Settings> tweak) {
        Identifier id = HauteCapitaleMetiers.id(path);
        RegistryKey<Item> key = RegistryKey.of(RegistryKeys.ITEM, id);
        Item item = Registry.register(Registries.ITEM, key, new Item(tweak.apply(new Item.Settings().registryKey(key))));
        ORDERED.add(item);
        return item;
    }
}
