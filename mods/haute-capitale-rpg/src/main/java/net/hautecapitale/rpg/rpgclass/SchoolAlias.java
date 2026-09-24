package net.hautecapitale.rpg.rpgclass;

import java.util.Map;
import java.util.Optional;

/**
 * Alignement des écoles de puissance divergentes.
 *
 * <p>Onze classes sur douze enregistrent leurs écoles sous {@code spell_power:}.
 * Le Chevalier de la mort fait exception : il utilise
 * {@code eternal_attributes:blood} et {@code eternal_attributes:unholy}.
 *
 * <p>La correspondance est faite <b>ici, en lecture</b>, et non par un renommage
 * des attributs dans le mod d'origine. La raison est concrète : ces identifiants
 * sont inscrits dans les modificateurs d'attribut de chaque arme et de chaque
 * armure déjà portées par les joueurs. Renommer l'attribut ne renomme pas ce qui
 * dort dans les inventaires — l'équipement existant perdrait son bonus, sans le
 * moindre message d'erreur.
 *
 * <p>Le noyau traite donc les deux écritures comme équivalentes. Un renommage
 * réel reste possible plus tard, mais il exigera une commande de migration
 * éprouvée sur une copie du monde.
 */
public final class SchoolAlias {

    private static final Map<String, String> VERS_SPELL_POWER = Map.of(
            "eternal_attributes:blood", "spell_power:blood",
            "eternal_attributes:unholy", "spell_power:unholy");

    private SchoolAlias() {
    }

    /**
     * Forme canonique d'une école, pour comparaison entre classes.
     * Une école déjà en {@code spell_power:} est rendue telle quelle.
     */
    public static String canonical(String school) {
        return VERS_SPELL_POWER.getOrDefault(school, school);
    }

    /** Vraie si cette école appartient au dialecte divergent d'une classe. */
    public static boolean isAliased(String school) {
        return VERS_SPELL_POWER.containsKey(school);
    }

    /** Classe qui possède cette école, si une seule la revendique. */
    public static Optional<RpgClass> ownerOf(String school) {
        String canon = canonical(school);
        RpgClass found = null;
        for (RpgClass rpgClass : RpgClass.values()) {
            for (String own : rpgClass.schools()) {
                if (canonical(own).equals(canon)) {
                    if (found != null && found != rpgClass) {
                        // École partagée (arcane, healing, physical_melee...) :
                        // elle ne désigne aucune classe à elle seule.
                        return Optional.empty();
                    }
                    found = rpgClass;
                }
            }
        }
        return Optional.ofNullable(found);
    }
}
