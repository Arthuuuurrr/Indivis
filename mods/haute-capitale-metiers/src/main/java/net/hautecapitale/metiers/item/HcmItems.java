package net.hautecapitale.metiers.item;

import net.fabricmc.fabric.api.itemgroup.v1.FabricItemGroup;
import net.hautecapitale.metiers.HauteCapitaleMetiers;
import net.hautecapitale.metiers.node.NodeTool;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;

import java.util.ArrayList;
import java.util.List;

/**
 * Les matières que le MMO devait créer.
 *
 * <p>Dix objets, pas un de plus. Chaque fois qu'un équivalent existait déjà dans
 * le modpack, il a été gardé : la petite peau est le cuir de lapin de Minecraft,
 * la peau commune vient de FleshZ, les écailles brutes de Lands of Icaria, le
 * tanin est l'écorce de Farmer's Delight. Ne sont créés que les paliers que
 * personne ne remplissait, et les deux fourrures dont les équivalents portent un
 * nom d'espèce — « Wolf Fur » sur un tigre serait illisible.
 *
 * <p>Aucun de ces objets n'a de comportement : ce sont des matières. Les
 * recettes qui les consomment vivent dans des fichiers de données, pas ici.
 */
public final class HcmItems {

    /** L'ordre de déclaration est l'ordre d'affichage dans l'onglet créatif. */
    private static final List<Item> ORDERED = new ArrayList<>();

    // --- Matières brutes, sorties du dépeçage -------------------------

    /** Peau épaisse — grands animaux et bêtes robustes. Dépeceur 21. */
    public static final Item PEAU_EPAISSE = register("peau_epaisse");
    /** Peau rare — créatures d'exception. Dépeceur 41. */
    public static final Item PEAU_RARE = register("peau_rare");
    /** Fourrure commune — loups, renards, ours noirs. Dépeceur 13. */
    public static final Item FOURRURE_COMMUNE = register("fourrure_commune");
    /** Fourrure épaisse — ours polaires, morses, grands ongulés du nord. Dépeceur 24. */
    public static final Item FOURRURE_EPAISSE = register("fourrure_epaisse");
    /** Fourrure rare — bêtes légendaires. Dépeceur 40. */
    public static final Item FOURRURE_RARE = register("fourrure_rare");
    /** Tendon — sous-produit occasionnel des grandes bêtes. Dépeceur 25. */
    public static final Item TENDON = register("tendon");

    // --- Matières travaillées, sorties du travail du cuir -------------

    /** Fourrure travaillée — Travailleur du cuir 23. */
    public static final Item FOURRURE_TRAVAILLEE = register("fourrure_travaillee");
    /** Écailles préparées — Travailleur du cuir 33. */
    public static final Item ECAILLES_PREPAREES = register("ecailles_preparees");
    /** Cuir exotique — Travailleur du cuir 38. */
    public static final Item CUIR_EXOTIQUE = register("cuir_exotique");
    /** Cuir rare — Travailleur du cuir 45. */
    public static final Item CUIR_RARE = register("cuir_rare");

    /**
     * L'onglet créatif du mod. Il existe pour que l'administrateur retrouve les
     * matières sans les chercher dans l'onglet « divers » de Minecraft.
     */
    public static final RegistryKey<ItemGroup> GROUP =
            RegistryKey.of(RegistryKeys.ITEM_GROUP, HauteCapitaleMetiers.id("matieres"));

    private HcmItems() {
    }

    public static void init() {
        Registry.register(Registries.ITEM_GROUP, GROUP, FabricItemGroup.builder()
                .displayName(Text.translatable("itemGroup.haute_capitale_metiers.matieres"))
                .icon(() -> new ItemStack(FOURRURE_COMMUNE))
                .entries((context, entries) -> {
                    ORDERED.forEach(entries::add);
                    // La chaîne textile du Couturier, et son cotonnier.
                    net.hautecapitale.metiers.textile.TextileItems.all().forEach(entries::add);
                    entries.add(net.hautecapitale.metiers.textile.CottonBlocks.COTONNIER_ITEM);
                    entries.add(net.hautecapitale.metiers.textile.CottonBlocks.COTONNIER_JEUNE_ITEM);
                    // L'outil d'administration des nodes, à portée de main en créatif.
                    entries.add(NodeTool.ITEM);
                })
                .build());
    }

    /** Les dix matières, dans l'ordre du catalogue. */
    public static List<Item> all() {
        return List.copyOf(ORDERED);
    }

    /**
     * Depuis 1.21.2 un objet doit connaître sa clé de registre avant d'exister :
     * elle sert à dériver sa clé de traduction et son modèle. L'oublier donne un
     * objet qui s'enregistre sans erreur mais s'affiche « item.minecraft.air ».
     */
    private static Item register(String path) {
        Identifier id = HauteCapitaleMetiers.id(path);
        RegistryKey<Item> key = RegistryKey.of(RegistryKeys.ITEM, id);
        Item item = Registry.register(Registries.ITEM, key, new Item(new Item.Settings().registryKey(key)));
        ORDERED.add(item);
        return item;
    }
}
