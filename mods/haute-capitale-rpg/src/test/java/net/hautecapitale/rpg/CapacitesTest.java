package net.hautecapitale.rpg;

import com.google.gson.JsonParser;
import com.mojang.serialization.JsonOps;
import net.hautecapitale.rpg.ability.AbilityDefinition;
import net.minecraft.util.Identifier;

import java.util.List;

/**
 * Vérifications de la couche de capacités, sans Minecraft en marche.
 *
 * <p>Ce qui est éprouvé ici est ce qui casse en silence : un codec qui accepte un
 * fichier mal formé, une valeur par défaut qui change de sens, un champ renommé. Ce qui
 * demande un monde — la résolution d'arme, l'écriture du conteneur — se vérifie en jeu,
 * pas ici.
 */
public final class CapacitesTest {

    private static int reussis = 0;
    private static int echecs = 0;

    public static void main(String[] args) {
        codec();
        defauts();
        refus();
        exigences();

        System.out.println();
        System.out.printf("  %d vérifications réussies, %d échecs%n", reussis, echecs);
        if (echecs > 0) {
            System.exit(1);
        }
    }

    // MARK: lecture d'un fichier complet

    private static void codec() {
        section("Lecture des définitions");

        AbilityDefinition complete = parse("""
                {
                  "schema_version": 1,
                  "spell": "rpg_series:whirlwind",
                  "ability_categories": ["capitale:melee", "capitale:area"],
                  "weapon_requirements": {
                    "allowed_tags": ["capitale:swords", "capitale:axes"],
                    "allowed_items": ["capitale:dragon_blade"],
                    "hands": "main_hand"
                  },
                  "requires_skill": true
                }
                """);

        check("un fichier complet est lu", complete != null);
        if (complete == null) {
            return;
        }
        check("le sort est conservé",
                complete.spell().equals(Identifier.of("rpg_series", "whirlwind")));
        check("les deux catégories sont conservées", complete.categories().size() == 2);
        check("les deux tags d'armes sont conservés",
                complete.weapon().allowed_tags().size() == 2);
        check("l'objet précis est conservé",
                complete.weapon().allowed_items().size() == 1);
        check("la main est lue en minuscules",
                complete.weapon().hands() == AbilityDefinition.WeaponRequirement.Hand.MAIN_HAND);
        check("une exigence renseignée n'est pas « aucune »", !complete.weapon().unrestricted());
    }

    // MARK: ce qui se passe quand on n'écrit rien

    private static void defauts() {
        section("Valeurs par défaut");

        AbilityDefinition minimal = parse("{ \"spell\": \"wizards:fireball\" }");
        check("le sort seul suffit", minimal != null);
        if (minimal == null) {
            return;
        }
        check("le schéma par défaut est celui du code",
                minimal.schema_version() == AbilityDefinition.SCHEMA);
        check("sans exigence d'arme, la capacité passe partout",
                minimal.weapon().unrestricted());
        check("une capacité demande une compétence par défaut",
                minimal.requires_skill());
        check("aucune catégorie par défaut", minimal.categories().isEmpty());
        check("la description d'une exigence vide se lit",
                minimal.weapon().describe().equals("aucune"));

        AbilityDefinition intrinseque = parse(
                "{ \"spell\": \"wizards:fireball\", \"requires_skill\": false }");
        check("une capacité intrinsèque se déclare",
                intrinseque != null && !intrinseque.requires_skill());
    }

    // MARK: ce qui doit être refusé plutôt que mal lu

    private static void refus() {
        section("Refus");

        check("un fichier sans sort est refusé", parse("{ }") == null);
        check("un identifiant de sort invalide est refusé",
                parse("{ \"spell\": \"Pas Un Identifiant\" }") == null);
        check("une main inconnue est refusée",
                parse("{ \"spell\": \"a:b\", \"weapon_requirements\": { \"hands\": \"troisieme\" } }") == null);

        // Un schéma futur doit être écarté par le chargeur, pas mal interprété. Le codec le
        // lit — c'est AbilityRegistry qui compare et refuse — donc on vérifie que la valeur
        // remonte intacte jusqu'à cette comparaison.
        AbilityDefinition futur = parse("{ \"spell\": \"a:b\", \"schema_version\": 99 }");
        check("un schéma futur remonte tel quel pour être écarté",
                futur != null && futur.schema_version() == 99);
    }

    // MARK: la description lisible, qui sert de message de refus en jeu

    private static void exigences() {
        section("Description des exigences");

        var requirement = new AbilityDefinition.WeaponRequirement(
                List.of(Identifier.of("capitale", "swords")),
                List.of(Identifier.of("capitale", "dragon_blade")),
                AbilityDefinition.WeaponRequirement.Hand.MAIN_HAND);

        String description = requirement.describe();
        check("le tag est préfixé d'un dièse", description.contains("#capitale:swords"));
        check("l'objet précis apparaît sans dièse", description.contains("capitale:dragon_blade"));
        check("les deux sont séparés", description.contains(", "));

        var vide = AbilityDefinition.WeaponRequirement.ANY;
        check("l'exigence vide est bien vide", vide.unrestricted());
        check("l'exigence vide vise la main principale",
                vide.hands() == AbilityDefinition.WeaponRequirement.Hand.MAIN_HAND);
    }

    // MARK: outils

    /** Rend la définition, ou {@code null} si le codec l'a refusée. */
    private static AbilityDefinition parse(String json) {
        try {
            var element = JsonParser.parseString(json);
            return AbilityDefinition.CODEC.parse(JsonOps.INSTANCE, element).result().orElse(null);
        } catch (Exception e) {
            return null;
        }
    }

    private static void section(String titre) {
        System.out.println();
        System.out.println("  " + titre);
    }

    private static void check(String nom, boolean ok) {
        if (ok) {
            reussis++;
            System.out.println("    ok    " + nom);
        } else {
            echecs++;
            System.out.println("    ECHEC " + nom);
        }
    }

    private CapacitesTest() {
    }
}
