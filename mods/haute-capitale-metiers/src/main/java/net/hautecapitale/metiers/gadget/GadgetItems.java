package net.hautecapitale.metiers.gadget;

import net.fabricmc.fabric.api.itemgroup.v1.FabricItemGroup;
import net.hautecapitale.metiers.HauteCapitaleMetiers;
import net.hautecapitale.metiers.hearth.HearthstoneItem;
import net.minecraft.component.type.AttributeModifierSlot;
import net.minecraft.component.type.AttributeModifiersComponent;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.attribute.EntityAttributeModifier;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.item.equipment.ArmorMaterials;
import net.minecraft.item.equipment.EquipmentType;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import net.minecraft.util.Rarity;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;

/**
 * Les gadgets de l'Ingénieur, et la Pierre de foyer.
 *
 * <p>Trente et un objets. Les idées et les textures viennent de MBK's Useful
 * Items (CC0) et de Gadgets Against Grind (MIT) ; le code est réécrit ici, en
 * Java, pour Fabric 1.21.11 — et surtout pour la carte : aucun de ces objets ne
 * casse ni ne pose un bloc. Les gadgets qui creusaient chez MBK ont été écartés,
 * ceux qui posaient de la lumière donnent de la vision nocturne à la place.
 *
 * <p>Comme les matières, ils n'ont pas de recette ici : les recettes sont des
 * fichiers de données, dans l'atelier de l'Ingénieur.
 */
public final class GadgetItems {

    /** L'ordre de déclaration est l'ordre d'affichage dans l'onglet créatif. */
    private static final List<Item> ORDERED = new ArrayList<>();

    // --- Le foyer ---------------------------------------------------------

    public static final Item PIERRE_DE_FOYER = register("pierre_de_foyer",
            s -> new HearthstoneItem(false, s.maxCount(1).rarity(Rarity.UNCOMMON)));
    public static final Item PIERRE_DE_FOYER_AMELIOREE = register("pierre_de_foyer_amelioree",
            s -> new HearthstoneItem(true, s.maxCount(1).rarity(Rarity.RARE)));

    // --- Sur la tête, aux pieds, dans le sac --------------------------------

    public static final Item LUNETTES_NOCTURNES = register("lunettes_nocturnes",
            s -> new WornGadgetItem(WornGadgetItem.Kind.LUNETTES_NOCTURNES, s.maxCount(1).equippable(EquipmentSlot.HEAD)));
    public static final Item CASQUE_DE_MINEUR = register("casque_de_mineur",
            s -> new WornGadgetItem(WornGadgetItem.Kind.CASQUE_DE_MINEUR, s.armor(ArmorMaterials.IRON, EquipmentType.HELMET)));
    public static final Item LANTERNE_DE_SAC = register("lanterne_de_sac",
            s -> new WornGadgetItem(WornGadgetItem.Kind.LANTERNE_DE_SAC, s.maxCount(1)));
    public static final Item LUNETTES_DE_TRESOR = register("lunettes_de_tresor",
            s -> new WornGadgetItem(WornGadgetItem.Kind.LUNETTES_DE_TRESOR, s.maxCount(1).equippable(EquipmentSlot.HEAD)));
    public static final Item LUNETTES_DE_PROSPECTION = register("lunettes_de_prospection",
            s -> new WornGadgetItem(WornGadgetItem.Kind.LUNETTES_DE_PROSPECTION, s.maxCount(1).equippable(EquipmentSlot.HEAD)));
    public static final Item BOTTES_DE_LAVE = register("bottes_de_lave",
            s -> new WornGadgetItem(WornGadgetItem.Kind.BOTTES_DE_LAVE, s.armor(ArmorMaterials.IRON, EquipmentType.BOOTS).fireproof()));
    public static final Item BOTTES_STABILISATRICES = register("bottes_stabilisatrices",
            s -> new Item(s.armor(ArmorMaterials.LEATHER, EquipmentType.BOOTS)
                    .attributeModifiers(AttributeModifiersComponent.builder()
                            .add(EntityAttributes.ARMOR, modifier("stabilisatrices_armure", 1.0D), AttributeModifierSlot.FEET)
                            .add(EntityAttributes.KNOCKBACK_RESISTANCE, modifier("stabilisatrices_recul", 0.3D), AttributeModifierSlot.FEET)
                            .add(EntityAttributes.SAFE_FALL_DISTANCE, modifier("stabilisatrices_chute", 3.0D), AttributeModifierSlot.FEET)
                            .add(EntityAttributes.FALL_DAMAGE_MULTIPLIER, modifier("stabilisatrices_degats", -0.5D), AttributeModifierSlot.FEET)
                            .build())));
    public static final Item GANTS_DE_PORTEE = register("gants_de_portee",
            s -> new Item(s.maxCount(1)
                    .attributeModifiers(AttributeModifiersComponent.builder()
                            .add(EntityAttributes.BLOCK_INTERACTION_RANGE, modifier("gants_blocs", 2.0D), AttributeModifierSlot.OFFHAND)
                            .add(EntityAttributes.ENTITY_INTERACTION_RANGE, modifier("gants_entites", 2.0D), AttributeModifierSlot.OFFHAND)
                            .build())));

    // --- L'exosquelette -------------------------------------------------------

    public static final Item EXOSQUELETTE_CASQUE = register("exosquelette_casque",
            s -> new WornGadgetItem(WornGadgetItem.Kind.EXOSQUELETTE, s.armor(ArmorMaterials.IRON, EquipmentType.HELMET)));
    public static final Item EXOSQUELETTE_PLASTRON = register("exosquelette_plastron",
            s -> new WornGadgetItem(WornGadgetItem.Kind.EXOSQUELETTE, s.armor(ArmorMaterials.IRON, EquipmentType.CHESTPLATE)));
    public static final Item EXOSQUELETTE_JAMBIERES = register("exosquelette_jambieres",
            s -> new WornGadgetItem(WornGadgetItem.Kind.EXOSQUELETTE, s.armor(ArmorMaterials.IRON, EquipmentType.LEGGINGS)));
    public static final Item EXOSQUELETTE_BOTTES = register("exosquelette_bottes",
            s -> new WornGadgetItem(WornGadgetItem.Kind.EXOSQUELETTE, s.armor(ArmorMaterials.IRON, EquipmentType.BOOTS)));

    // --- Dans la main : détecteurs ---------------------------------------------

    public static final Item DETECTEUR_DE_MINERAI = register("detecteur_de_minerai",
            s -> new ScannerItem(ScannerItem.Kind.MINERAI, s.maxCount(1)));
    public static final Item CHERCHEUR_DE_GEODE = register("chercheur_de_geode",
            s -> new ScannerItem(ScannerItem.Kind.GEODE, s.maxCount(1)));
    public static final Item SONDE_A_ECHO = register("sonde_a_echo",
            s -> new ScannerItem(ScannerItem.Kind.ECHO, s.maxCount(1)));
    public static final Item RESONATEUR_DE_CRISTAL = register("resonateur_de_cristal",
            s -> new ScannerItem(ScannerItem.Kind.RESONATEUR, s.maxCount(1)));

    // --- Dans le sac : marche/arrêt ------------------------------------------------

    public static final Item AIMANT_A_BUTIN = register("aimant_a_butin",
            s -> new ToggleGadgetItem(ToggleGadgetItem.Kind.AIMANT, s.maxCount(1)));
    public static final Item COMPRESSEUR_DE_POCHE = register("compresseur_de_poche",
            s -> new ToggleGadgetItem(ToggleGadgetItem.Kind.COMPRESSEUR, s.maxCount(1)));
    public static final Item BROYEUR_AUTOMATIQUE = register("broyeur_automatique",
            s -> new ToggleGadgetItem(ToggleGadgetItem.Kind.BROYEUR, s.maxCount(1)));
    public static final Item DRONE_MINIER = register("drone_minier",
            s -> new MiningDroneItem(s.maxCount(1)));

    // --- Déplacement ---------------------------------------------------------------

    public static final Item GRAPPIN = register("grappin",
            s -> new RopeLauncherItem(s.maxDamage(128)));
    public static final Item CORDE_D_EVASION = register("corde_d_evasion",
            s -> new EscapeRopeItem(s.maxDamage(8)));
    public static final Item BALISE_DE_RAPPEL = register("balise_de_rappel",
            s -> new RecallBeaconItem(s.maxCount(1).rarity(Rarity.UNCOMMON)));

    // --- Consommables -----------------------------------------------------------------

    public static final Item KIT_DE_SECOURS = register("kit_de_secours",
            s -> new KitItem(KitItem.Kind.SECOURS, s.maxCount(16)));
    public static final Item KIT_DE_CAMPEMENT = register("kit_de_campement",
            s -> new KitItem(KitItem.Kind.CAMPEMENT, s.maxCount(16)));
    public static final Item KIT_DE_RAFFINAGE = register("kit_de_raffinage",
            s -> new KitItem(KitItem.Kind.RAFFINAGE, s.maxCount(1)));
    public static final Item SEL_SACRE = register("sel_sacre",
            s -> new RepellentItem(RepellentItem.Kind.SEL, s.maxCount(16)));
    public static final Item ONGUENT_SACRE = register("onguent_sacre",
            s -> new RepellentItem(RepellentItem.Kind.ONGUENT, s.maxCount(16)));
    public static final Item BAUME_SACRE = register("baume_sacre",
            s -> new RepellentItem(RepellentItem.Kind.BAUME, s.maxCount(16)));

    /** L'onglet créatif des gadgets, à côté de celui des matières. */
    public static final RegistryKey<ItemGroup> GROUP =
            RegistryKey.of(RegistryKeys.ITEM_GROUP, HauteCapitaleMetiers.id("gadgets"));

    private GadgetItems() {
    }

    public static void init() {
        Registry.register(Registries.ITEM_GROUP, GROUP, FabricItemGroup.builder()
                .displayName(Text.translatable("itemGroup.haute_capitale_metiers.gadgets"))
                .icon(() -> new ItemStack(PIERRE_DE_FOYER))
                .entries((context, entries) -> ORDERED.forEach(entries::add))
                .build());
    }

    /** Les trente et un gadgets, dans l'ordre du catalogue. */
    public static List<Item> all() {
        return List.copyOf(ORDERED);
    }

    private static EntityAttributeModifier modifier(String path, double value) {
        return new EntityAttributeModifier(HauteCapitaleMetiers.id(path), value, EntityAttributeModifier.Operation.ADD_VALUE);
    }

    private static Item register(String path, Function<Item.Settings, Item> factory) {
        Identifier id = HauteCapitaleMetiers.id(path);
        RegistryKey<Item> key = RegistryKey.of(RegistryKeys.ITEM, id);
        Item item = Registry.register(Registries.ITEM, key, factory.apply(new Item.Settings().registryKey(key)));
        ORDERED.add(item);
        return item;
    }
}
