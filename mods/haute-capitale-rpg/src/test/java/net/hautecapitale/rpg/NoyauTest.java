package net.hautecapitale.rpg;

import com.mojang.serialization.Codec;
import com.mojang.serialization.DynamicOps;
import com.mojang.serialization.JsonOps;
import net.hautecapitale.rpg.rpgclass.PlayerClassState;
import net.hautecapitale.rpg.rpgclass.RpgClass;
import net.hautecapitale.rpg.content.ClassContent;
import net.hautecapitale.rpg.rpgclass.SchoolAlias;
import net.minecraft.nbt.NbtOps;

import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

/**
 * Harnais de vérification du noyau — un {@code main()} classique, pas du JUnit,
 * pour rester exécutable sans dépendance de test supplémentaire.
 *
 * <p>Les codecs sont éprouvés en <b>NbtOps autant qu'en JsonOps</b> : c'est NBT
 * qui sert réellement à la sauvegarde du joueur, et un codec qui passe en JSON
 * peut échouer en NBT sans rien dire.
 */
public final class NoyauTest {

    private static int reussis = 0;
    private static int echecs = 0;

    public static void main(String[] args) {
        classes();
        codecs();
        alias();
        contenu();
        matrice();

        System.out.println();
        System.out.printf("  %d vérifications réussies, %d échecs%n", reussis, echecs);
        if (echecs > 0) {
            System.exit(1);
        }
    }

    // MARK: les douze classes

    private static void classes() {
        section("Classes");

        check("douze classes exactement", RpgClass.values().length == 12);

        Set<String> ids = new HashSet<>();
        boolean uniques = true;
        for (RpgClass c : RpgClass.values()) {
            if (!ids.add(c.getId())) {
                uniques = false;
            }
        }
        check("identifiants uniques", uniques);

        boolean lookup = true;
        for (RpgClass c : RpgClass.values()) {
            if (RpgClass.byId(c.getId()) != c) {
                lookup = false;
            }
        }
        check("byId retrouve chaque classe", lookup);
        check("byId sur inconnu rend null", RpgClass.byId("mage_de_pacotille") == null);

        boolean complet = true;
        for (RpgClass c : RpgClass.values()) {
            if (c.namespaces().isEmpty() || c.schools().isEmpty()) {
                complet = false;
                System.out.println("      manque namespace ou école : " + c.getId());
            }
        }
        check("chaque classe a namespace et écoles", complet);

        // Le Chasseur est la seule classe nourrie par deux mods.
        check("Chasseur couvre archers + expansion",
                RpgClass.CHASSEUR.namespaces().size() == 2
                        && RpgClass.CHASSEUR.namespaces().contains("archers")
                        && RpgClass.CHASSEUR.namespaces().contains("archers_expansion"));

        // Paladin et Prêtre viennent du même JAR.
        check("Paladin et Prêtre partagent le mod paladins",
                RpgClass.PALADIN.namespaces().equals(RpgClass.PRETRE.namespaces()));
    }

    // MARK: sauvegarde du joueur

    private static void codecs() {
        section("Codecs — JSON et NBT");

        roundTrip("état vide", PlayerClassState.empty());
        roundTrip("classe simple", new PlayerClassState(Optional.of(RpgClass.BARDE)));
        roundTrip("autre classe", new PlayerClassState(Optional.of(RpgClass.SORCELEUR)));

        for (RpgClass c : RpgClass.values()) {
            roundTripSilencieux(PlayerClassState.of(c));
        }
        check("aller-retour de chacune des douze classes", true);

        PlayerClassState relu = decode(NbtOps.INSTANCE,
                encode(NbtOps.INSTANCE, PlayerClassState.of(RpgClass.CHEVALIER_DE_LA_MORT)));
        check("classe conservée en NBT",
                relu.rpgClass().orElse(null) == RpgClass.CHEVALIER_DE_LA_MORT);

        // Compatibilité ascendante : un état écrit avant l'ajout de l'expérience
        // n'a pas le champ "xp" et doit se relire sans erreur.
        var ancien = com.google.gson.JsonParser.parseString(
                "{\"classe\":\"barde\",\"niveau\":9}");
        try {
            PlayerClassState relu2 = decode(JsonOps.INSTANCE, ancien);
            check("un état avec ancienne progression se relit, champs ignorés",
                    relu2.rpgClass().orElse(null) == RpgClass.BARDE);
        } catch (RuntimeException e) {
            check("un état avec ancienne progression se relit, champs ignorés", false);
        }
    }

    private static <T> T encode(DynamicOps<T> ops, PlayerClassState state) {
        return PlayerClassState.CODEC.encodeStart(ops, state)
                .getOrThrow(m -> new IllegalStateException("encodage impossible : " + m));
    }

    private static <T> PlayerClassState decode(DynamicOps<T> ops, T data) {
        return PlayerClassState.CODEC.parse(ops, data)
                .getOrThrow(m -> new IllegalStateException("décodage impossible : " + m));
    }

    private static void roundTrip(String nom, PlayerClassState state) {
        boolean json = essai(JsonOps.INSTANCE, state);
        boolean nbt = essai(NbtOps.INSTANCE, state);
        check(nom + " — JSON", json);
        check(nom + " — NBT", nbt);
    }

    private static void roundTripSilencieux(PlayerClassState state) {
        if (!essai(JsonOps.INSTANCE, state) || !essai(NbtOps.INSTANCE, state)) {
            System.out.println("      aller-retour raté : " + state);
            echecs++;
        }
    }

    private static <T> boolean essai(DynamicOps<T> ops, PlayerClassState state) {
        try {
            return decode(ops, encode(ops, state)).equals(state);
        } catch (RuntimeException e) {
            System.out.println("      " + e.getMessage());
            return false;
        }
    }

    // MARK: alignement des écoles

    private static void alias() {
        section("Alignement des écoles");

        check("blood est traduit",
                SchoolAlias.canonical("eternal_attributes:blood").equals("spell_power:blood"));
        check("unholy est traduit",
                SchoolAlias.canonical("eternal_attributes:unholy").equals("spell_power:unholy"));
        check("une école spell_power passe intacte",
                SchoolAlias.canonical("spell_power:frost").equals("spell_power:frost"));
        check("isAliased ne vise que le dialecte divergent",
                SchoolAlias.isAliased("eternal_attributes:blood")
                        && !SchoolAlias.isAliased("spell_power:frost"));

        // Une école exclusive désigne sa classe ; une école partagée n'en désigne aucune.
        check("rage_melee désigne le Berserker",
                SchoolAlias.ownerOf("spell_power:rage_melee").orElse(null) == RpgClass.BERSERKER);
        check("blood désigne le Chevalier de la mort",
                SchoolAlias.ownerOf("eternal_attributes:blood").orElse(null)
                        == RpgClass.CHEVALIER_DE_LA_MORT);
        check("arcane est partagée, donc ne désigne personne",
                SchoolAlias.ownerOf("spell_power:arcane").isEmpty());
        check("une école inconnue ne désigne personne",
                SchoolAlias.ownerOf("spell_power:inexistante").isEmpty());
    }

    // MARK: rattachement du contenu aux classes

    private static void contenu() {
        section("Rattachement du contenu");

        check("berserker_rpg appartient au seul Berserker",
                ClassContent.owners("berserker_rpg").equals(Set.of(RpgClass.BERSERKER)));

        // Un mod, deux classes.
        check("paladins appartient au Paladin et au Prêtre",
                ClassContent.owners("paladins").equals(Set.of(RpgClass.PALADIN, RpgClass.PRETRE)));

        // Une classe, deux mods.
        check("archers et son extension appartiennent au Chasseur",
                ClassContent.owners("archers").equals(Set.of(RpgClass.CHASSEUR))
                        && ClassContent.owners("archers_expansion").equals(Set.of(RpgClass.CHASSEUR)));

        // Décision F : ces quatre mods restent à part, donc libres pour tous.
        boolean libres = ClassContent.owners("arsenal").isEmpty()
                && ClassContent.owners("armory_rpgs").isEmpty()
                && ClassContent.owners("relics_rpgs").isEmpty()
                && ClassContent.owners("jewelry").isEmpty();
        check("Arsenal, Armory, Relics et Jewelry restent libres", libres);

        check("le vanilla est libre", ClassContent.owners("minecraft").isEmpty());

        // Douze namespaces revendiqués : Paladin et Prêtre partagent le leur.
        check("douze namespaces revendiqués", ClassContent.claimedNamespaces().size() == 12);
    }

    // MARK: matrice des douze classes

    private static void matrice() {
        section("Matrice des douze classes");

        // Chaque classe doit accepter le contenu de ses propres namespaces et
        // refuser celui de toutes les autres. C'est la règle que le verrou applique.
        int bons = 0, mauvais = 0;
        for (RpgClass moi : RpgClass.values()) {
            for (String ns : moi.namespaces()) {
                if (ClassContent.owners(ns).contains(moi)) { bons++; } else { mauvais++;
                    System.out.println("      " + moi.getId() + " ne possède pas " + ns); }
            }
            for (RpgClass autre : RpgClass.values()) {
                if (autre == moi) continue;
                for (String ns : autre.namespaces()) {
                    boolean partage = moi.namespaces().contains(ns);
                    boolean accepte = ClassContent.owners(ns).contains(moi);
                    if (partage == accepte) { bons++; } else { mauvais++;
                        System.out.println("      " + moi.getId() + " vs " + ns + " : incohérent"); }
                }
            }
        }
        System.out.printf("      %d combinaisons classe/namespace vérifiées%n", bons + mauvais);
        check("chaque classe possède ses namespaces et refuse les autres", mauvais == 0);

        // Paladin et Prêtre sont le seul couple qui partage un namespace.
        int partages = 0;
        for (RpgClass a : RpgClass.values()) {
            for (RpgClass b : RpgClass.values()) {
                if (a.ordinal() >= b.ordinal()) continue;
                for (String ns : a.namespaces()) {
                    if (b.namespaces().contains(ns)) partages++;
                }
            }
        }
        check("un seul namespace partagé entre deux classes", partages == 1);
    }


    // MARK: sortie

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

    private NoyauTest() {
    }
}
