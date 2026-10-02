package net.hautecapitale.rpg.rpgclass;

import com.mojang.serialization.Codec;
import net.minecraft.util.StringIdentifiable;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * Les douze classes jouables de Haute Capitale.
 *
 * <p>Le compte n'est pas celui des mods : {@code archers_expansion} enrichit le
 * Chasseur au lieu de former une classe, et {@code paladins} en livre deux
 * (Paladin et Prêtre). Douze mods de classe donnent donc douze classes
 * (la douzième, le Nécromancien, est un portage maison : {@code hc_necromancer}).
 *
 * <p>Chaque entrée décrit <b>où reconnaître la classe</b> dans la pile existante :
 * les namespaces des mods qui la fournissent, et les écoles de puissance de sort
 * qu'elle utilise. Le noyau ne possède aucun de ces contenus — il les observe.
 * C'est ce qui permet d'ajouter ce mod à côté des vingt-sept autres sans rien
 * revendiquer ni rien casser.
 *
 * <p>La liste est fixée par le design, d'où l'énumération. Ce qui doit rester
 * réglable — niveaux, déblocages, équilibrage — vit dans la configuration et
 * référence ces classes par leur {@link #id}.
 */
public enum RpgClass implements StringIdentifiable {

    SORCIER("sorcier", Archetype.MAGIE,
            List.of("wizards"),
            List.of("spell_power:arcane", "spell_power:fire", "spell_power:frost")),

    SORCIER_ELEMENTAIRE("sorcier_elementaire", Archetype.MAGIE,
            List.of("elemental_wizards_rpg"),
            List.of("spell_power:air", "spell_power:earth", "spell_power:water")),

    SORCELEUR("sorceleur", Archetype.HYBRIDE,
            List.of("witcher_rpg"),
            List.of("spell_power:aard", "spell_power:igni", "spell_power:quen",
                    "spell_power:yrden", "spell_power:axii", "spell_power:sign",
                    "spell_power:witcher_melee")),

    PALADIN("paladin", Archetype.MELEE,
            List.of("paladins"),
            List.of("spell_power:healing", "spell_power:physical_melee")),

    PRETRE("pretre", Archetype.SOUTIEN,
            List.of("paladins"),
            List.of("spell_power:healing")),

    CHEVALIER_DE_LA_MORT("chevalier_de_la_mort", Archetype.MELEE,
            List.of("death_knights"),
            // Seule classe qui n'utilise pas le namespace spell_power. Le noyau
            // l'accepte telle quelle et la traduit via SchoolAlias : renommer ces
            // attributs viderait de leur bonus tout l'équipement déjà porté.
            List.of("eternal_attributes:blood", "eternal_attributes:unholy")),

    BERSERKER("berserker", Archetype.MELEE,
            List.of("berserker_rpg"),
            List.of("spell_power:rage_melee")),

    VOLEUR("voleur", Archetype.MELEE,
            List.of("rogues"),
            List.of("spell_power:physical_melee_dual", "spell_power:physical_melee",
                    "spell_power:health")),

    FORCEMASTER("forcemaster", Archetype.HYBRIDE,
            List.of("forcemaster_rpg"),
            List.of("spell_power:arcane", "spell_power:physical_melee")),

    CHASSEUR("chasseur", Archetype.DISTANCE,
            // Archers Expansion est une extension, pas une classe : les deux
            // namespaces nourrissent le même Chasseur.
            List.of("archers", "archers_expansion"),
            List.of("spell_power:physical_ranged", "spell_power:fire_ranged",
                    "spell_power:frost_ranged")),

    BARDE("barde", Archetype.SOUTIEN,
            List.of("bards_rpg"),
            List.of("spell_power:arcane", "spell_power:healing", "spell_power:generic")),

    // Nécromancien Éveillé (portage SamusDev en Fabric, mod hc_necromancer) : bâton + faux spectrale,
    // serviteurs squelettes, école « soul » de Spell Power.
    NECROMANCIEN("necromancien", Archetype.MAGIE,
            List.of("hc_necromancer"),
            List.of("spell_power:soul"));

    /** Famille de jeu. Sert au tri et aux filtres d'interface, jamais aux règles. */
    public enum Archetype { MAGIE, MELEE, DISTANCE, SOUTIEN, HYBRIDE }

    public static final Codec<RpgClass> CODEC = StringIdentifiable.createCodec(RpgClass::values);

    private static final Map<String, RpgClass> BY_ID = Arrays.stream(values())
            .collect(Collectors.toUnmodifiableMap(RpgClass::getId, Function.identity()));

    private final String id;
    private final Archetype archetype;
    private final List<String> namespaces;
    private final List<String> schools;

    RpgClass(String id, Archetype archetype, List<String> namespaces, List<String> schools) {
        this.id = id;
        this.archetype = archetype;
        this.namespaces = List.copyOf(namespaces);
        this.schools = List.copyOf(schools);
    }

    public String getId() {
        return id;
    }

    public Archetype archetype() {
        return archetype;
    }

    /** Namespaces des mods qui fournissent le contenu de cette classe. */
    public List<String> namespaces() {
        return namespaces;
    }

    /** Écoles de puissance de sort, telles qu'elles sont réellement enregistrées. */
    public List<String> schools() {
        return schools;
    }

    /** Clé de traduction du nom affiché. */
    public String translationKey() {
        return "rpgclass.haute_capitale_rpg." + id;
    }

    @Override
    public String asString() {
        return id;
    }

    public static RpgClass byId(String id) {
        return BY_ID.get(id);
    }

    public static List<String> ids() {
        return Arrays.stream(values()).map(RpgClass::getId).toList();
    }
}
