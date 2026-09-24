package net.hautecapitale.metiers.data;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.JsonOps;
import net.hautecapitale.metiers.config.MetiersConfig;
import net.hautecapitale.metiers.craft.CraftRecipe;
import net.hautecapitale.metiers.craft.Mastery;
import net.hautecapitale.metiers.craft.XpFalloff;
import net.hautecapitale.metiers.creature.CreatureEnums.Category;
import net.hautecapitale.metiers.creature.CreatureEnums.Rarity;
import net.hautecapitale.metiers.creature.CreatureEnums.SpawnOrigin;
import net.hautecapitale.metiers.creature.CreatureProfile;
import net.hautecapitale.metiers.creature.DropEntry;
import net.hautecapitale.metiers.hunt.OriginMarker;
import net.hautecapitale.metiers.meal.MealBuff;
import net.hautecapitale.metiers.node.NodeStore;
import net.hautecapitale.metiers.node.NodeType;
import net.hautecapitale.metiers.npc.NpcRole;
import net.hautecapitale.metiers.profession.Profession;
import net.hautecapitale.metiers.repair.RepairData;
import net.minecraft.entity.SpawnReason;
import net.minecraft.nbt.NbtOps;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;

import java.io.IOException;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;
import java.util.stream.Stream;

/**
 * Harnais de test du moteur de données.
 *
 * <p>Ne démarre ni serveur ni monde : on vérifie ici tout ce qui se décide avant
 * qu'un joueur existe — la lecture des fiches, les valeurs par défaut, le
 * nettoyage des valeurs aberrantes, la règle « le chemin est la clé », et le
 * fait que les fiches livrées avec le mod se relisent réellement.
 *
 * <p>Ce que ce harnais ne peut pas voir se teste en jeu : le rechargement à
 * chaud, les avertissements sur des objets ou des entités absents (qui exigent
 * les registres de Minecraft) et les commandes d'inspection.
 *
 * <p>Lancé par {@code gradle runDonneesTest}.
 */
public final class DonneesTest {

    private static int passed = 0;
    private static int failed = 0;

    public static void main(String[] args) {
        System.out.println("=== Haute Capitale — Métiers : tests du moteur de données ===\n");

        testCompleteSheet();
        testMinimalSheet();
        testRoundTrip();
        testRequiredFields();
        testUnknownVocabulary();
        testSanitizing();
        testXpOrigins();
        testDropDescription();
        testPathIsKey();
        testLoadReport();
        testRoleSheet();
        testRoleValidation();
        testShippedSheets();
        testShippedRoles();
        testMaterialAssets();
        testMaterialTags();
        testRecipeSheet();
        testXpFalloff();
        testMastery();
        testMasteryPersistence();
        testShippedRecipes();
        testNodeSheet();
        testNodeStore();
        testShippedNodes();
        testOriginMapping();
        testShippedPrey();
        testSkinningSheet();
        testShippedSkinning();
        testRepairConfig();
        testShippedSmithing();
        testGadgetConfig();
        testHearthRole();
        testShippedEngineering();
        testQualityConfig();
        testMealBuffSheet();
        testShippedTailoring();
        testShippedJewelry();
        testShippedCooking();
        testShippedAlchemy();
        testCottonNode();

        System.out.printf(Locale.ROOT, "%n=== %d réussis, %d échoués ===%n", passed, failed);
        if (failed > 0) {
            System.exit(1);
        }
    }

    // ------------------------------------------------------------------

    private static final String ROEDEER = """
            {
              "categorie": "animal",
              "rarete": "peu_commune",
              "chasseur": { "niveau": 4, "xp": 12.5 },
              "depecage": {
                "niveau": 6,
                "xp": 18.0,
                "duree_carcasse": 90,
                "matiere": { "id": "minecraft:leather", "min": 1, "max": 3 },
                "secondaires": [ { "id": "minecraft:bone", "chance": 0.3 } ]
              },
              "viande": { "id": "minecraft:mutton", "min": 1, "max": 2 },
              "loot_combat": [ { "id": "minecraft:string", "chance": 0.15 } ],
              "origines_xp": ["naturelle", "spawn_mmo", "spawner"],
              "commentaire": "chevreuil de test"
            }
            """;

    private static void testCompleteSheet() {
        section("Lecture d'une fiche complète");
        CreatureProfile profile = decode(ROEDEER);
        if (profile == null) {
            return;
        }
        check("catégorie lue", profile.category() == Category.ANIMAL);
        check("rareté lue", profile.rarity() == Rarity.PEU_COMMUNE);
        check("créature chassable", profile.isHuntable());
        check("niveau de chasse lu", profile.hunter().orElseThrow().level() == 4);
        check("XP de chasse lue", profile.hunter().orElseThrow().xp() == 12.5D);
        check("créature dépeçable", profile.isSkinnable());

        CreatureProfile.SkinningEntry skinning = profile.skinning().orElseThrow();
        check("niveau de dépeçage lu", skinning.level() == 6);
        check("durée de carcasse lue", skinning.carcassSeconds() == 90);
        check("matière lue", skinning.material().item().equals(Identifier.of("minecraft", "leather")));
        check("fourchette de matière lue",
                skinning.material().min() == 1 && skinning.material().max() == 3);
        check("un secondaire lu", skinning.secondary().size() == 1);
        check("chance du secondaire lue", skinning.secondary().get(0).chance() == 0.3D);
        check("quantité par défaut d'un secondaire",
                skinning.secondary().get(0).min() == 1 && skinning.secondary().get(0).max() == 1);

        check("viande lue", profile.meat().orElseThrow().item().getPath().equals("mutton"));
        check("loot de combat lu", profile.combatLoot().size() == 1);
        check("trois origines lues", profile.xpOrigins().size() == 3);
        check("commentaire lu", profile.comment().orElse("").equals("chevreuil de test"));
    }

    private static void testMinimalSheet() {
        section("Fiche minimale — tout est optionnel sauf le nécessaire");
        CreatureProfile profile = decode("{}");
        if (profile == null) {
            return;
        }
        check("catégorie par défaut", profile.category() == Category.AUTRE);
        check("rareté par défaut", profile.rarity() == Rarity.COMMUNE);
        check("non chassable par défaut", !profile.isHuntable());
        check("non dépeçable par défaut", !profile.isSkinnable());
        check("aucune viande par défaut", profile.meat().isEmpty());
        check("aucun loot de combat par défaut", profile.combatLoot().isEmpty());
        check("aucun commentaire par défaut", profile.comment().isEmpty());
        check("origines par défaut = naturelle + spawn MMO",
                profile.xpOrigins().equals(SpawnOrigin.DEFAUT));
    }

    private static void testRoundTrip() {
        section("Aller-retour d'écriture");
        CreatureProfile original = decode(ROEDEER);
        if (original == null) {
            return;
        }
        DataResult<com.google.gson.JsonElement> encoded =
                CreatureProfile.CODEC.encodeStart(JsonOps.INSTANCE, original);
        check("réécriture réussie", encoded.result().isPresent());
        if (encoded.result().isEmpty()) {
            System.out.println("      " + encoded.error().map(Object::toString).orElse("?"));
            return;
        }
        DataResult<CreatureProfile> reread = CreatureProfile.CODEC.parse(JsonOps.INSTANCE, encoded.result().get());
        check("relecture réussie", reread.result().isPresent());
        if (reread.result().isEmpty()) {
            System.out.println("      " + reread.error().map(Object::toString).orElse("?"));
            return;
        }
        check("fiche identique après aller-retour", reread.result().get().equals(original));
    }

    private static void testRequiredFields() {
        section("Champs obligatoires");
        DataResult<CreatureProfile> noMaterial = parse("""
                { "depecage": { "niveau": 3, "xp": 5.0 } }
                """);
        check("un dépeçage sans matière est refusé", noMaterial.error().isPresent());

        DataResult<CreatureProfile> noItemId = parse("""
                { "viande": { "min": 1, "max": 2 } }
                """);
        check("un objet sans identifiant est refusé", noItemId.error().isPresent());

        DataResult<CreatureProfile> badId = parse("""
                { "viande": { "id": "Pas Un Identifiant" } }
                """);
        check("un identifiant mal formé est refusé", badId.error().isPresent());
    }

    private static void testUnknownVocabulary() {
        section("Vocabulaire inconnu");
        String category = errorMessage("{ \"categorie\": \"dragonnet\" }");
        check("catégorie inconnue refusée", category != null);
        check("le message nomme le champ fautif", category != null && category.contains("categorie"));
        check("le message cite la valeur fautive", category != null && category.contains("dragonnet"));
        check("le message énumère les valeurs acceptées",
                category != null && category.contains("humanoide_monstrueux"));

        String rarity = errorMessage("{ \"rarete\": \"mythique\" }");
        check("rareté inconnue refusée", rarity != null);
        check("le message de rareté énumère les valeurs acceptées",
                rarity != null && rarity.contains("legendaire"));

        String origin = errorMessage("{ \"origines_xp\": [\"portail\"] }");
        check("origine inconnue refusée", origin != null);
        check("le message d'origine énumère les valeurs acceptées",
                origin != null && origin.contains("spawn_mmo"));

        check("un champ en trop est ignoré, pas refusé",
                parse("{ \"couleur_du_pelage\": \"roux\" }").result().isPresent());
    }

    private static void testSanitizing() {
        section("Valeurs aberrantes ramenées dans le domaine utilisable");
        CreatureProfile profile = decode("""
                {
                  "chasseur": { "niveau": 0, "xp": -50.0 },
                  "depecage": {
                    "niveau": -3,
                    "duree_carcasse": 0,
                    "matiere": { "id": "minecraft:leather", "min": -5, "max": -9, "chance": 12.0 }
                  }
                }
                """);
        if (profile == null) {
            return;
        }
        check("niveau de chasse nul ramené à 1", profile.hunter().orElseThrow().level() == 1);
        check("XP négative ramenée à 0", profile.hunter().orElseThrow().xp() == 0.0D);

        CreatureProfile.SkinningEntry skinning = profile.skinning().orElseThrow();
        check("niveau de dépeçage négatif ramené à 1", skinning.level() == 1);
        check("durée de carcasse nulle ramenée à 1 s", skinning.carcassSeconds() == 1);
        check("quantité négative ramenée à 0", skinning.material().min() == 0);
        check("maximum inférieur au minimum aligné", skinning.material().max() == 0);
        check("chance supérieure à 1 plafonnée", skinning.material().chance() == 1.0D);

        check("chance NaN ramenée à 0",
                new DropEntry(Identifier.of("minecraft", "bone"), 1, 1, Double.NaN).chance() == 0.0D);
    }

    private static void testXpOrigins() {
        section("Origines d'apparition — la clé de l'anti-farm");
        CreatureProfile bred = decode("{ \"origines_xp\": [\"naturelle\"] }");
        if (bred == null) {
            return;
        }
        check("l'apparition naturelle rapporte", bred.grantsXpFrom(SpawnOrigin.NATURELLE));
        check("l'élevage ne rapporte pas", !bred.grantsXpFrom(SpawnOrigin.ELEVAGE));
        check("l'œuf d'apparition ne rapporte pas", !bred.grantsXpFrom(SpawnOrigin.OEUF));

        CreatureProfile emptyList = decode("{ \"origines_xp\": [] }");
        if (emptyList == null) {
            return;
        }
        check("une liste vide retombe sur le défaut plutôt que sur le silence",
                emptyList.xpOrigins().equals(SpawnOrigin.DEFAUT));

        boolean immutable;
        try {
            emptyList.xpOrigins().add(SpawnOrigin.ELEVAGE);
            immutable = false;
        } catch (UnsupportedOperationException e) {
            immutable = true;
        }
        check("la liste exposée est non modifiable", immutable);
    }

    private static void testDropDescription() {
        section("Description lisible d'un objet donné");
        Identifier bone = Identifier.of("minecraft", "bone");
        check("quantité 1 sans pourcentage reste sobre",
                new DropEntry(bone, 1, 1, 1.0D).describe().equals("minecraft:bone"));
        check("quantité fixe affichée",
                new DropEntry(bone, 3, 3, 1.0D).describe().equals("minecraft:bone ×3"));
        check("fourchette affichée",
                new DropEntry(bone, 1, 4, 1.0D).describe().equals("minecraft:bone ×1-4"));
        check("pourcentage affiché",
                new DropEntry(bone, 1, 1, 0.25D).describe().equals("minecraft:bone (25 %)"));
        check("objet toujours donné reconnu", new DropEntry(bone, 1, 1, 1.0D).isAlwaysDropped());
        check("objet aléatoire reconnu", !new DropEntry(bone, 1, 1, 0.5D).isAlwaysDropped());
    }

    private static void testPathIsKey() {
        section("Le chemin du fichier est la clé");
        check("fichier de datapack → identifiant d'entité",
                Identifier.of("cubeanimals", "roedeer").equals(
                        DataRegistry.toKey(
                                Identifier.of("cubeanimals", "hcm/creatures/roedeer.json"),
                                "hcm/creatures")));
        check("les sous-dossiers font partie de la clé",
                Identifier.of("cubeanimals", "foret/cerf").equals(
                        DataRegistry.toKey(
                                Identifier.of("cubeanimals", "hcm/creatures/foret/cerf.json"),
                                "hcm/creatures")));
        check("un autre dossier est ignoré",
                DataRegistry.toKey(
                        Identifier.of("cubeanimals", "loot_tables/entities/roedeer.json"),
                        "hcm/creatures") == null);
        check("un fichier qui n'est pas du JSON est ignoré",
                DataRegistry.toKey(
                        Identifier.of("cubeanimals", "hcm/creatures/notes.txt"),
                        "hcm/creatures") == null);
        check("un nom de fichier vide est ignoré",
                DataRegistry.toKey(
                        Identifier.of("cubeanimals", "hcm/creatures/.json"),
                        "hcm/creatures") == null);
    }

    private static void testLoadReport() {
        section("Compte rendu de chargement");
        LoadReport report = new LoadReport();
        check("un rapport neuf est propre", report.isClean());

        Identifier file = Identifier.of("cubeanimals", "hcm/creatures/roedeer.json");
        report.countLoaded();
        report.warn(file, "objet inconnu");
        report.error(file, "JSON invalide");
        report.warn(file, "carcasse trop longue");

        check("les fiches chargées sont comptées", report.loaded() == 1);
        check("les erreurs sont comptées", report.errorCount() == 1);
        check("les avertissements sont comptés", report.warningCount() == 2);
        check("un rapport avec un problème n'est plus propre", !report.isClean());
        check("le message nomme le fichier fautif",
                report.entries().get(0).toString().contains("hcm/creatures/roedeer.json"));
        check("le message dit s'il s'agit d'une erreur ou d'un avertissement",
                report.entries().get(1).toString().startsWith("ERREUR"));

        boolean immutable;
        try {
            report.entries().clear();
            immutable = false;
        } catch (UnsupportedOperationException e) {
            immutable = true;
        }
        check("la liste exposée est non modifiable", immutable);
    }

    private static void testRoleSheet() {
        section("Fiche de rôle de PNJ");
        NpcRole full = decodeRole("""
                {
                  "interface": "formation",
                  "metier": "mineur",
                  "titre": "Maître mineur",
                  "accueil": "Je peux t'apprendre à lire la roche.",
                  "peut_enseigner": false
                }
                """);
        if (full == null) {
            return;
        }
        check("interface lue", full.screen() == NpcRole.Interface.FORMATION);
        check("métier lu", full.profession().orElseThrow() == Profession.MINEUR);
        check("titre lu", full.title().orElse("").equals("Maître mineur"));
        check("accueil lu", full.greeting().isPresent());
        check("refus d'enseigner lu", !full.canTeach());

        NpcRole minimal = decodeRole("{ \"metier\": \"forgeron\" }");
        if (minimal == null) {
            return;
        }
        check("interface par défaut = atelier", minimal.screen() == NpcRole.Interface.ATELIER);
        check("un PNJ enseigne par défaut", minimal.canTeach());
        check("aucun titre par défaut", minimal.title().isEmpty());

        check("un atelier a besoin d'un métier",
                minimal.needsProfession());
        NpcRole registry = decodeRole("{ \"interface\": \"registre\" }");
        check("un registre n'a besoin d'aucun métier",
                registry != null && !registry.needsProfession());

        String badScreen = errorMessageRole("{ \"interface\": \"boutique\" }");
        check("interface inconnue refusée", badScreen != null);
        check("le message énumère les interfaces acceptées",
                badScreen != null && badScreen.contains("formation") && badScreen.contains("registre"));

        String badJob = errorMessageRole("{ \"metier\": \"bucheron\" }");
        check("métier inconnu refusé", badJob != null);
        check("le message énumère les métiers acceptés",
                badJob != null && badJob.contains("travailleur_du_cuir"));

        check("une interface inconnue reçue du serveur retombe sur l'atelier",
                NpcRole.Interface.byIdOrDefault("venu_du_futur") == NpcRole.Interface.ATELIER);
    }

    private static void testRoleValidation() {
        section("Cohérence d'un rôle");

        check("un atelier sans métier est écarté",
                rejects("{ \"interface\": \"atelier\" }"));
        check("une formation sans métier est écartée",
                rejects("{ \"interface\": \"formation\" }"));
        check("un registre sans métier est accepté",
                !rejects("{ \"interface\": \"registre\" }"));

        String surplus = firstIssue("{ \"interface\": \"registre\", \"metier\": \"mineur\" }");
        check("un métier sur un registre est signalé", surplus != null);
        check("et le message dit qu'il est ignoré",
                surplus != null && surplus.contains("ignoré"));

        String gathering = firstIssue("{ \"interface\": \"atelier\", \"metier\": \"chasseur\" }");
        check("un atelier de métier de récolte est signalé", gathering != null);
        check("et le message oriente vers « formation »",
                gathering != null && gathering.contains("formation"));
        check("mais le rôle reste chargé",
                !rejects("{ \"interface\": \"atelier\", \"metier\": \"chasseur\" }"));

        check("un atelier d'artisan ne dit rien",
                firstIssue("{ \"interface\": \"atelier\", \"metier\": \"joaillier\" }") == null);
    }

    /**
     * Les fiches livrées dans le jar sont du contenu comme un autre : si l'une
     * d'elles contient une faute de frappe, le serveur l'écarterait en silence au
     * démarrage. Autant s'en apercevoir à la compilation.
     */
    private static void testShippedSheets() {
        section("Fiches livrées avec le mod");
        Path root = locateData();
        check("dossier data/ localisé", root != null);
        if (root == null) {
            return;
        }

        List<Path> sheets = new ArrayList<>();
        try (Stream<Path> walk = Files.walk(root)) {
            walk.filter(Files::isRegularFile)
                    .filter(path -> path.toString().replace('\\', '/').contains("/hcm/creatures/"))
                    .filter(path -> path.toString().endsWith(".json"))
                    .forEach(sheets::add);
        } catch (IOException e) {
            check("parcours du dossier data/", false);
            return;
        }

        check("au moins trois fiches livrées", sheets.size() >= 3);
        for (Path sheet : sheets) {
            String label = sheet.getFileName().toString();
            try {
                String json = Files.readString(sheet, StandardCharsets.UTF_8);
                DataResult<CreatureProfile> result =
                        CreatureProfile.CODEC.parse(JsonOps.INSTANCE, JsonParser.parseString(json));
                check(label + " se relit sans erreur", result.result().isPresent());
                result.error().ifPresent(error -> System.out.println("      " + error));
            } catch (IOException | RuntimeException e) {
                check(label + " se relit sans erreur", false);
                System.out.println("      " + e);
            }
        }
    }

    /** Les dix-sept rôles livrés doivent se relire et passer leurs propres contrôles. */
    private static void testShippedRoles() {
        section("Rôles livrés avec le mod");
        Path root = locateData();
        if (root == null) {
            check("dossier data/ localisé", false);
            return;
        }

        List<Path> roles = new ArrayList<>();
        try (Stream<Path> walk = Files.walk(root)) {
            walk.filter(Files::isRegularFile)
                    .filter(path -> path.toString().replace('\\', '/').contains("/hcm/roles/"))
                    .filter(path -> path.toString().endsWith(".json"))
                    .forEach(roles::add);
        } catch (IOException e) {
            check("parcours du dossier des rôles", false);
            return;
        }

        // Six ateliers, dix maîtres — un par métier —, l'aubergiste.
        check("les dix-neuf rôles prévus sont livrés", roles.size() == 19);
        check("chaque métier a son maître, fichier maitre_<metier>.json", Arrays.stream(Profession.values())
                .allMatch(profession -> roles.stream().anyMatch(path -> path.getFileName().toString()
                        .equals("maitre_" + profession.getId() + ".json"))));

        int workshops = 0;
        for (Path file : roles) {
            String label = file.getFileName().toString();
            NpcRole role;
            try {
                role = NpcRole.CODEC.parse(JsonOps.INSTANCE,
                        JsonParser.parseString(Files.readString(file, StandardCharsets.UTF_8)))
                        .result().orElse(null);
            } catch (IOException | RuntimeException e) {
                check(label + " se relit sans erreur", false);
                System.out.println("      " + e);
                continue;
            }
            check(label + " se relit sans erreur", role != null);
            if (role == null) {
                continue;
            }
            LoadReport report = new LoadReport();
            Identifier id = Identifier.of("haute_capitale_metiers", label.replace(".json", ""));
            DataRegistry.Validator checks = new DataRegistry.Validator(id, id, report);
            HcmData.validateRole(role, checks);
            check(label + " passe la validation sans rien signaler", report.isClean());
            report.entries().forEach(entry -> System.out.println("      " + entry));

            if (role.screen() == NpcRole.Interface.ATELIER) {
                workshops++;
            }
        }
        check("les sept métiers d'artisanat ont leur atelier", workshops == 7);
    }

    /** Les dix matières, nommées ici pour que le test les réclame une à une. */
    private static final List<String> MATERIALS = List.of("peau_epaisse", "peau_rare", "fourrure_commune",
            "fourrure_epaisse", "fourrure_rare", "tendon", "fourrure_travaillee", "ecailles_preparees",
            "cuir_exotique", "cuir_rare");

    /** Les trente et un gadgets de l'étape 10, tels que livrés en textures. */
    private static final List<String> GADGETS = List.of("pierre_de_foyer", "pierre_de_foyer_amelioree",
            "lunettes_nocturnes", "casque_de_mineur", "lanterne_de_sac", "lunettes_de_tresor", "lunettes_de_prospection",
            "bottes_de_lave", "bottes_stabilisatrices", "gants_de_portee", "exosquelette_casque", "exosquelette_plastron",
            "exosquelette_jambieres", "exosquelette_bottes", "detecteur_de_minerai", "chercheur_de_geode", "sonde_a_echo",
            "resonateur_de_cristal", "aimant_a_butin", "compresseur_de_poche", "broyeur_automatique", "drone_minier",
            "grappin", "corde_d_evasion", "balise_de_rappel", "kit_de_secours", "kit_de_campement", "kit_de_raffinage",
            "sel_sacre", "onguent_sacre", "baume_sacre");

    /** Les trente-neuf objets textiles du Couturier (étape 11) : chaîne Weaver's Paradise, vêtements dessinés ici. */
    private static final List<String> TEXTILES = List.of("coton", "fibre_de_coton", "bobine_vide", "bobine_de_coton",
            "bobine_de_laine", "bobine_de_jean", "bobine_de_soie", "tissu_de_coton", "tissu_de_laine", "tissu_de_jean",
            "tissu_de_soie", "aiguille", "aiguille_enfilee", "base_de_haut_en_coton", "base_de_haut_en_laine",
            "base_de_haut_en_soie", "manche_courte_de_coton", "manche_longue_de_coton", "manche_courte_de_laine",
            "manche_longue_de_laine", "manche_courte_de_soie", "manche_longue_de_soie", "jambe_de_coton", "jambe_de_laine",
            "jambe_de_jean", "jambe_de_soie", "debardeur", "chemise_de_coton", "chemise_de_soie", "pull_de_laine",
            "pantalon_de_coton", "pantalon_de_laine", "pantalon_de_jean", "pantalon_de_soie", "bas_de_soie",
            "bonnet_de_laine", "cape_de_coton", "cape_de_laine", "cape_de_soie");

    /**
     * Les objets sont ce qui casse le plus silencieusement.
     *
     * <p>Depuis 1.21.4 un objet a besoin de deux fichiers : le modèle, et un
     * calque {@code items/} qui désigne ce modèle. S'il manque, l'objet
     * s'enregistre sans erreur, se donne sans erreur, et n'affiche rien du tout.
     * Une clé de traduction absente se voit tout autant : l'objet s'appelle
     * {@code item.haute_capitale_metiers.…} en jeu. Aucun de ces deux défauts
     * n'apparaît dans un journal — d'où ce test.
     */
    private static void testMaterialAssets() {
        section("Matières — textures, modèles et noms");
        Path assets = locateResource("/assets");
        if (assets == null) {
            check("dossier assets/ localisé", false);
            return;
        }

        Path root = assets.resolve("haute_capitale_metiers");
        Path textures = root.resolve("textures").resolve("item");
        List<Path> pngs = new ArrayList<>();
        try (Stream<Path> walk = Files.list(textures)) {
            walk.filter(path -> path.toString().endsWith(".png")).sorted().forEach(pngs::add);
        } catch (IOException e) {
            check("dossier des textures lisible", false);
            return;
        }

        Set<String> shipped = new HashSet<>();
        pngs.forEach(png -> shipped.add(png.getFileName().toString().replace(".png", "")));
        check("quatre-vingt-deux textures livrées : dix matières, la baguette, trente et un gadgets, le sprite du drone, trente-neuf textiles",
                pngs.size() == 82);
        check("les trente-neuf objets textiles ont leur texture", shipped.containsAll(TEXTILES));
        check("les dix matières ont leur texture", shipped.containsAll(MATERIALS));
        check("les trente et un gadgets ont leur texture", shipped.containsAll(GADGETS));
        check("rien d'autre", shipped.size() == MATERIALS.size() + GADGETS.size() + TEXTILES.size() + 2
                && shipped.contains("outil_node") && shipped.contains("drone_en_vol"));

        for (Path png : pngs) {
            String name = png.getFileName().toString().replace(".png", "");

            // Seule la pierre améliorée est animée (bande verticale de 16×16 + .mcmeta), et seul le
            // sprite du drone en vol — celui de MBK, gardé tel quel — est en 32×32 ; tout le reste est en 16×16.
            boolean animated = name.equals("pierre_de_foyer_amelioree");
            boolean sprite = name.equals("drone_en_vol");
            check(name + (animated ? " : bande animée 16×N avec son .mcmeta" : sprite ? " : sprite 32×32" : " : texture 16×16"),
                    animated ? isItemTexture(png) : sprite ? isSquare(png, 32) : is16x16(png));
            check(name + " : modèle présent et correct",
                    modelPointsAtTexture(root.resolve("models").resolve("item").resolve(name + ".json"), name));
            check(name + " : calque items/ présent (sans lui, rien ne s'affiche)",
                    Files.isRegularFile(root.resolve("items").resolve(name + ".json")));
        }

        // Les vêtements ne se portent pas : aucun calque d'équipement ne doit traîner.
        check("aucun calque d'équipement livré : les vêtements sont des composants",
                !Files.isDirectory(root.resolve("equipment")) && !Files.isDirectory(root.resolve("textures").resolve("entity").resolve("equipment")));

        for (String lang : List.of("fr_fr", "en_us")) {
            JsonObject entries = readJson(root.resolve("lang").resolve(lang + ".json"));
            if (entries == null) {
                check(lang + " lisible", false);
                continue;
            }
            List<String> missing = MATERIALS.stream()
                    .filter(name -> !entries.has("item.haute_capitale_metiers." + name))
                    .toList();
            check(lang + " : les dix matières sont traduites", missing.isEmpty());
            if (!missing.isEmpty()) {
                System.out.println("      manque : " + missing);
            }
            check(lang + " : la baguette de node est traduite", entries.has("item.haute_capitale_metiers.outil_node"));
            List<String> missingGadgets = GADGETS.stream()
                    .filter(name -> !entries.has("item.haute_capitale_metiers." + name))
                    .toList();
            check(lang + " : les trente et un gadgets sont traduits", missingGadgets.isEmpty());
            if (!missingGadgets.isEmpty()) {
                System.out.println("      manque : " + missingGadgets);
            }
            List<String> missingTextiles = TEXTILES.stream()
                    .filter(name -> !entries.has("item.haute_capitale_metiers." + name))
                    .toList();
            check(lang + " : les trente-neuf objets textiles sont traduits, et les deux cotonniers", missingTextiles.isEmpty()
                    && entries.has("block.haute_capitale_metiers.cotonnier") && entries.has("block.haute_capitale_metiers.cotonnier_jeune"));
            if (!missingTextiles.isEmpty()) {
                System.out.println("      manque : " + missingTextiles);
            }
            // Les objets sans infobulle propre — matières, baguette, textiles, deux
            // gadgets passifs — portent leur description dans la langue : item.<id>.desc.
            List<String> described = new ArrayList<>(MATERIALS);
            described.add("outil_node");
            described.add("bottes_stabilisatrices");
            described.add("gants_de_portee");
            described.addAll(TEXTILES);
            List<String> missingDesc = described.stream()
                    .filter(name -> !entries.has("item.haute_capitale_metiers." + name + ".desc"))
                    .toList();
            check(lang + " : les cinquante-deux objets sans infobulle propre ont leur description", missingDesc.isEmpty());
            if (!missingDesc.isEmpty()) {
                System.out.println("      manque : " + missingDesc);
            }
            check(lang + " : l'effet repas, la qualité et le buff ont leurs textes",
                    entries.has("effect.haute_capitale_metiers.repas") && entries.has("hcm.qualite.excellent")
                            && entries.has("hcm.repas.buff") && entries.has("hcm.fabrication.excellent"));
            check(lang + " : les onglets créatifs sont traduits",
                    entries.has("itemGroup.haute_capitale_metiers.matieres")
                            && entries.has("itemGroup.haute_capitale_metiers.gadgets"));
            check(lang + " : le drone et l'effet repoussant sont nommés",
                    entries.has("entity.haute_capitale_metiers.drone_minier")
                            && entries.has("effect.haute_capitale_metiers.repoussant"));
        }

        // Les crédits voyagent dans le jar : la copie de src/main/resources doit être
        // celle de la racine du projet, sinon le jar publie une mention périmée.
        Path rootCredits = Path.of("CREDITS.md");
        Path packagedCredits = Path.of("src", "main", "resources", "CREDITS.md");
        boolean sameCredits;
        try {
            sameCredits = Files.isRegularFile(rootCredits) && Files.isRegularFile(packagedCredits)
                    && Files.readString(rootCredits, StandardCharsets.UTF_8)
                            .equals(Files.readString(packagedCredits, StandardCharsets.UTF_8));
        } catch (IOException e) {
            sameCredits = false;
        }
        check("CREDITS.md du jar identique à celui de la racine", sameCredits);
        check("l'effet repoussant a son icône (mob_effect/repoussant.png, 16×16 — sans elle, un damier rose dans le HUD)",
                is16x16(root.resolve("textures").resolve("mob_effect").resolve("repoussant.png")));
        check("l'effet repas a son icône", is16x16(root.resolve("textures").resolve("mob_effect").resolve("repas.png")));
        for (String block : List.of("cotonnier", "cotonnier_jeune")) {
            check(block + " : texture de bloc 16×16, état de bloc, modèle en croix, objet-bloc",
                    is16x16(root.resolve("textures").resolve("block").resolve(block + ".png"))
                            && Files.isRegularFile(root.resolve("blockstates").resolve(block + ".json"))
                            && Files.isRegularFile(root.resolve("models").resolve("block").resolve(block + ".json"))
                            && Files.isRegularFile(root.resolve("items").resolve(block + ".json")));
        }
    }

    /**
     * Un tag qui nomme un objet absent fait échouer tout le tag, silencieusement
     * pour le joueur. Nos propres objets doivent donc exister ; ceux des autres
     * mods doivent être déclarés facultatifs, sinon retirer un mod du pack casse
     * le tannage.
     */
    private static void testMaterialTags() {
        section("Tags de matières");
        Path data = locateData();
        if (data == null) {
            check("dossier data/ localisé", false);
            return;
        }

        Path tags = data.resolve("haute_capitale_metiers").resolve("tags").resolve("item");
        List<Path> files = new ArrayList<>();
        try (Stream<Path> walk = Files.walk(tags)) {
            walk.filter(Files::isRegularFile)
                    .filter(path -> path.toString().endsWith(".json"))
                    // Les couteaux sont des outils, pas des matières : ils ont leur propre test.
                    .filter(path -> !path.toString().contains("couteaux") && !path.toString().contains("reparable_si_brise")
                            && !path.toString().contains("legendaire"))
                    .sorted()
                    .forEach(files::add);
        } catch (IOException e) {
            check("dossier des tags lisible", false);
            return;
        }

        check("dix tags livrés", files.size() == 10);

        for (Path file : files) {
            String name = tags.relativize(file).toString().replace('\\', '/').replace(".json", "");
            JsonObject json = readJson(file);
            if (json == null || !json.has("values")) {
                check(name + " : fichier de tag valide", false);
                continue;
            }

            boolean ownItemsExist = true;
            boolean foreignOptional = true;
            for (JsonElement value : json.getAsJsonArray("values")) {
                String id;
                boolean required = true;
                if (value.isJsonObject()) {
                    id = value.getAsJsonObject().get("id").getAsString();
                    required = !value.getAsJsonObject().has("required")
                            || value.getAsJsonObject().get("required").getAsBoolean();
                } else {
                    id = value.getAsString();
                }

                if (id.startsWith("haute_capitale_metiers:")) {
                    ownItemsExist &= MATERIALS.contains(id.substring(id.indexOf(':') + 1));
                } else if (!id.startsWith("minecraft:") && required) {
                    foreignOptional = false;
                    System.out.println("      " + name + " exige " + id + " sans « required »: false");
                }
            }

            check(name + " : ne nomme que des matières qui existent", ownItemsExist);
            check(name + " : les objets d'autres mods sont facultatifs", foreignOptional);
        }
    }

    private static void testRecipeSheet() {
        section("Fiche de recette");
        CraftRecipe full = decodeRecipe("""
                {
                  "titre": "Tannage simple",
                  "metier": "travailleur_du_cuir",
                  "niveau": 9,
                  "xp": 39.0,
                  "ingredients": [
                    { "tag": "haute_capitale_metiers:peaux/communes", "quantite": 3, "nom": "Peau commune" },
                    { "id": "farmersdelight:tree_bark" }
                  ],
                  "cout": 1,
                  "resultat": { "id": "minecraft:leather", "quantite": 2 }
                }
                """);
        if (full == null) {
            return;
        }
        check("métier lu", full.profession() == Profession.TRAVAILLEUR_DU_CUIR);
        check("niveau lu", full.level() == 9);
        check("XP lue", full.xp() == 39.0D);
        check("deux ingrédients lus", full.ingredients().size() == 2);
        check("ingrédient par famille", full.ingredients().get(0).tag().isPresent()
                && full.ingredients().get(0).tag().get().id().getPath().equals("peaux/communes"));
        check("quantité lue", full.ingredients().get(0).count() == 3);
        check("nom d'ingrédient lu", full.ingredients().get(0).name().orElse("").equals("Peau commune"));
        check("ingrédient par objet précis", full.ingredients().get(1).item().isPresent());
        check("quantité d'ingrédient par défaut = 1", full.ingredients().get(1).count() == 1);
        check("prix lu", full.cost() == 1);
        check("résultat lu", full.result().item().getPath().equals("leather") && full.result().count() == 2);

        CraftRecipe minimal = decodeRecipe("""
                { "metier": "forgeron", "ingredients": [ { "id": "minecraft:iron_ingot" } ],
                  "resultat": { "id": "minecraft:iron_nugget" } }
                """);
        if (minimal == null) {
            return;
        }
        check("niveau par défaut = 1", minimal.level() == 1);
        check("XP par défaut = 0", minimal.xp() == 0.0D);
        check("prix par défaut = 0", minimal.cost() == 0);
        check("résultat ×1 par défaut", minimal.result().count() == 1);

        check("un ingrédient sans tag ni id est refusé",
                recipeError("{ \"metier\": \"forgeron\", \"ingredients\": [ { \"quantite\": 2 } ], "
                        + "\"resultat\": { \"id\": \"minecraft:stick\" } }") != null);
        String both = recipeError("{ \"metier\": \"forgeron\", \"ingredients\": [ { \"tag\": \"a:b\", \"id\": \"a:c\" } ], "
                + "\"resultat\": { \"id\": \"minecraft:stick\" } }");
        check("un ingrédient avec tag ET id est refusé", both != null);
        check("et le message explique la règle", both != null && both.contains("exactement l'un des deux"));
        check("une recette sans métier est refusée",
                recipeError("{ \"ingredients\": [ { \"id\": \"a:b\" } ], \"resultat\": { \"id\": \"a:c\" } }") != null);
        check("une recette sans résultat est refusée",
                recipeError("{ \"metier\": \"forgeron\", \"ingredients\": [ { \"id\": \"a:b\" } ] }") != null);
        String badJob = recipeError("{ \"metier\": \"barde\", \"ingredients\": [ { \"id\": \"a:b\" } ], "
                + "\"resultat\": { \"id\": \"a:c\" } }");
        check("un métier inconnu est refusé en nommant les métiers acceptés",
                badJob != null && badJob.contains("travailleur_du_cuir"));

        CraftRecipe odd = decodeRecipe("""
                { "metier": "forgeron", "niveau": -4, "xp": -10, "cout": -3,
                  "ingredients": [ { "id": "a:b", "quantite": 0 } ],
                  "resultat": { "id": "a:c", "quantite": 0 } }
                """);
        check("valeurs aberrantes ramenées dans le domaine utilisable",
                odd != null && odd.level() == 1 && odd.xp() == 0.0D && odd.cost() == 0
                        && odd.ingredients().get(0).count() == 1 && odd.result().count() == 1);
    }

    /** La décote, sur les paliers par défaut : la table du cahier des charges, palier par palier. */
    private static void testXpFalloff() {
        section("Décote d'XP");
        List<MetiersConfig.Palier> paliers = List.of(
                new MetiersConfig.Palier(5, 100),
                new MetiersConfig.Palier(10, 60),
                new MetiersConfig.Palier(15, 25),
                new MetiersConfig.Palier(20, 5));

        check("au niveau de la recette : 100 %", XpFalloff.factor(0, paliers) == 1.0D);
        check("écart 5 : encore 100 %", XpFalloff.factor(5, paliers) == 1.0D);
        check("écart 6 : 60 %", XpFalloff.factor(6, paliers) == 0.6D);
        check("écart 10 : 60 %", XpFalloff.factor(10, paliers) == 0.6D);
        check("écart 11 : 25 %", XpFalloff.factor(11, paliers) == 0.25D);
        check("écart 12 : 25 % — le cas du cahier des charges", XpFalloff.factor(12, paliers) == 0.25D);
        check("écart 15 : 25 %", XpFalloff.factor(15, paliers) == 0.25D);
        check("écart 16 : 5 %", XpFalloff.factor(16, paliers) == 0.05D);
        check("écart 20 : 5 %", XpFalloff.factor(20, paliers) == 0.05D);
        check("écart 21 : 0 %", XpFalloff.factor(21, paliers) == 0.0D);
        check("écart 25 : 0 % — le cas du cahier des charges", XpFalloff.factor(25, paliers) == 0.0D);
        check("recette au-dessus du joueur : plein tarif si jamais elle passait",
                XpFalloff.factor(-3, paliers) == 1.0D);
        check("aucun palier : tout à 0 %", XpFalloff.factor(0, List.of()) == 0.0D);
        check("un pourcentage hors bornes est ramené entre 0 et 100",
                XpFalloff.factor(0, List.of(new MetiersConfig.Palier(50, 250))) == 1.0D);
    }

    private static void testMastery() {
        section("Maîtrise");
        check("0 fabrication : rien", Mastery.of(0, 5, 15) == Mastery.NONE);
        check("4 fabrications : rien", Mastery.of(4, 5, 15) == Mastery.NONE);
        check("5 fabrications : en apprentissage", Mastery.of(5, 5, 15) == Mastery.LEARNING);
        check("14 fabrications : en apprentissage", Mastery.of(14, 5, 15) == Mastery.LEARNING);
        check("15 fabrications : maîtrisée — le cas du cahier des charges", Mastery.of(15, 5, 15) == Mastery.MASTERED);
        check("100 fabrications : maîtrisée", Mastery.of(100, 5, 15) == Mastery.MASTERED);
        check("seuls les ×5 et ×10 de la maîtrise", Mastery.MASTERED.batches().length == 3
                && Mastery.LEARNING.batches().length == 1 && Mastery.NONE.batches().length == 1);
    }

    /**
     * Le compteur de maîtrise est une carte identifiant → entier, sauvegardée en
     * NBT. Une carte à clés non-String y casse ; on vérifie que celle-ci passe.
     */
    private static void testMasteryPersistence() {
        section("Persistance du compteur de maîtrise — NBT");
        Codec<Map<Identifier, Integer>> codec = Codec.unboundedMap(Identifier.CODEC, Codec.INT);
        Map<Identifier, Integer> original = Map.of(
                Identifier.of("haute_capitale_metiers", "travailleur_du_cuir/tannage_simple"), 14,
                Identifier.of("haute_capitale_metiers", "travailleur_du_cuir/assemblage_de_chutes"), 3);

        var encoded = codec.encodeStart(NbtOps.INSTANCE, original);
        check("encodage NBT réussi", encoded.result().isPresent());
        if (encoded.result().isEmpty()) {
            System.out.println("      " + encoded.error().map(DataResult.Error::message).orElse("?"));
            return;
        }
        var decoded = codec.parse(NbtOps.INSTANCE, encoded.result().get());
        check("décodage NBT réussi", decoded.result().isPresent());
        check("compteurs relus à l'identique", decoded.result().map(original::equals).orElse(false));
    }

    /**
     * Les neuf recettes du Travailleur du cuir : celles du cahier des charges,
     * ni plus ni moins, chacune référençant des familles et des résultats qui
     * existent.
     */
    private static void testShippedRecipes() {
        section("Recettes livrées — Travailleur du cuir");
        Path data = locateData();
        if (data == null) {
            check("dossier data/ localisé", false);
            return;
        }
        Path folder = data.resolve("haute_capitale_metiers").resolve("hcm").resolve("recipes").resolve("travailleur_du_cuir");
        List<Path> files = new ArrayList<>();
        try (Stream<Path> walk = Files.walk(folder)) {
            walk.filter(Files::isRegularFile).filter(path -> path.toString().endsWith(".json"))
                    .sorted().forEach(files::add);
        } catch (IOException e) {
            check("dossier des recettes lisible", false);
            return;
        }
        check("dix recettes livrées (huit transformations, le tannage rare en deux, les lanières)", files.size() == 10);

        Set<String> knownTags = new HashSet<>();
        Path tags = data.resolve("haute_capitale_metiers").resolve("tags").resolve("item");
        try (Stream<Path> walk = Files.walk(tags)) {
            walk.filter(Files::isRegularFile).forEach(path ->
                    knownTags.add(tags.relativize(path).toString().replace('\\', '/').replace(".json", "")));
        } catch (IOException e) {
            check("dossier des tags lisible", false);
            return;
        }
        Set<String> knownResults = new HashSet<>(MATERIALS.stream().map(m -> "haute_capitale_metiers:" + m).toList());
        knownResults.addAll(List.of("fleshz:hide", "minecraft:leather", "more_rpg_classes:hardened_leather",
                "magistuarmory:leather_strip"));

        List<Integer> levels = new ArrayList<>();
        for (Path file : files) {
            String label = file.getFileName().toString();
            CraftRecipe recipe;
            try {
                recipe = CraftRecipe.CODEC.parse(JsonOps.INSTANCE,
                        JsonParser.parseString(Files.readString(file, StandardCharsets.UTF_8))).result().orElse(null);
            } catch (IOException | RuntimeException e) {
                check(label + " se relit", false);
                continue;
            }
            check(label + " se relit", recipe != null);
            if (recipe == null) {
                continue;
            }
            check(label + " : Travailleur du cuir", recipe.profession() == Profession.TRAVAILLEUR_DU_CUIR);
            check(label + " : XP positive", recipe.xp() > 0.0D);
            check(label + " : familles connues", recipe.ingredients().stream().allMatch(ingredient ->
                    ingredient.tag().map(tag -> tag.id().getNamespace().equals("haute_capitale_metiers")
                            && knownTags.contains(tag.id().getPath())).orElse(true)));
            check(label + " : résultat connu", knownResults.contains(recipe.result().item().toString()));
            levels.add(recipe.level());
        }
        check("les niveaux couvrent la progression 1 → 45",
                levels.contains(1) && levels.contains(9) && levels.contains(45));
    }

    // ------------------------------------------------------------------
    // Étape 6 — nodes

    private static final String IRON_NODE = """
            {
              "metier": "mineur",
              "niveau": 5,
              "xp": 13,
              "bloc_plein": "minecraft:iron_ore",
              "bloc_vide": "minecraft:stone",
              "loot": [ { "id": "minecraft:raw_iron", "min": 1, "max": 2 } ],
              "respawn": 300,
              "coups": 3,
              "outil": "minecraft:pickaxes",
              "titre": "Filon de fer",
              "commentaire": "test"
            }
            """;

    /** La fiche de node : tout ce que le moteur lit, et ce qui a une valeur par défaut. */
    private static void testNodeSheet() {
        section("Fiche de node");
        NodeType iron = decodeNode(IRON_NODE);
        if (iron == null) {
            return;
        }
        check("métier lu", iron.profession() == Profession.MINEUR);
        check("niveau lu", iron.level() == 5);
        check("XP lue", iron.xp() == 13.0D);
        check("bloc plein lu", iron.fullBlock().equals("minecraft:iron_ore"));
        check("bloc vide lu", iron.emptyBlock().equals("minecraft:stone"));
        check("loot lu", iron.loot().size() == 1 && iron.loot().get(0).max() == 2);
        check("repousse lue", iron.respawnSeconds() == 300 && iron.respawnMillis() == 300_000L);
        check("coups lus", iron.hits() == 3);
        check("outil lu comme famille d'objets",
                iron.tool().map(tag -> tag.id().equals(Identifier.of("minecraft", "pickaxes"))).orElse(false));
        check("titre lu", iron.title().orElse("").equals("Filon de fer"));

        NodeType minimal = decodeNode("""
                { "metier": "herboriste", "bloc_plein": "minecraft:dandelion", "bloc_vide": "minecraft:air", "loot": [] }
                """);
        check("fiche minimale acceptée", minimal != null);
        if (minimal != null) {
            check("niveau par défaut 1", minimal.level() == 1);
            check("XP par défaut 0", minimal.xp() == 0.0D);
            check("repousse par défaut 300 s", minimal.respawnSeconds() == 300);
            check("trois coups par défaut", minimal.hits() == 3);
            check("aucun outil par défaut", minimal.tool().isEmpty());
            check("aucun titre par défaut", minimal.title().isEmpty());
        }

        NodeType odd = decodeNode("""
                { "metier": "mineur", "niveau": -3, "xp": -1, "respawn": 0, "coups": 0,
                  "bloc_plein": "minecraft:coal_ore", "bloc_vide": "minecraft:stone", "loot": [] }
                """);
        check("valeurs aberrantes ramenées : niveau 1, XP 0, repousse 1 s, un coup",
                odd != null && odd.level() == 1 && odd.xp() == 0.0D && odd.respawnSeconds() == 1 && odd.hits() == 1);

        check("bloc plein obligatoire", nodeError("""
                { "metier": "mineur", "bloc_vide": "minecraft:stone", "loot": [] }
                """) != null);
        check("loot obligatoire (même vide)", nodeError("""
                { "metier": "mineur", "bloc_plein": "minecraft:coal_ore", "bloc_vide": "minecraft:stone" }
                """) != null);
        String badProfession = nodeError("""
                { "metier": "pecheur", "bloc_plein": "minecraft:coal_ore", "bloc_vide": "minecraft:stone", "loot": [] }
                """);
        check("métier inconnu refusé, avec la liste des métiers",
                badProfession != null && badProfession.contains("mineur") && badProfession.contains("pecheur"));

        var encoded = NodeType.CODEC.encodeStart(JsonOps.INSTANCE, iron);
        check("réécriture réussie", encoded.result().isPresent());
        var reread = encoded.result().flatMap(json -> NodeType.CODEC.parse(JsonOps.INSTANCE, json).result());
        check("fiche identique après aller-retour", reread.map(iron::equals).orElse(false));
    }

    /**
     * Le registre des nodes d'un monde : la carte par position fait foi, l'index
     * par chunk et l'ensemble des nodes en attente la suivent, et tout survit
     * à un passage en NBT — c'est ce qui est écrit dans la sauvegarde du monde.
     */
    private static void testNodeStore() {
        section("Registre des nodes — index et persistance");
        NodeStore store = new NodeStore();
        Identifier iron = Identifier.of("haute_capitale_metiers", "mineur/fer");
        Identifier poppy = Identifier.of("haute_capitale_metiers", "herboriste/coquelicot");
        BlockPos a = new BlockPos(10, 64, 10);
        BlockPos b = new BlockPos(12, 65, 14);   // même chunk que a
        BlockPos c = new BlockPos(100, 70, -40); // autre chunk

        store.put(a, new NodeStore.Node(iron, 0L));
        store.put(b, new NodeStore.Node(iron, 0L));
        store.put(c, new NodeStore.Node(poppy, 0L));
        check("trois nodes", store.size() == 3);
        check("un node se retrouve par sa position", store.contains(a) && store.get(a).type.equals(iron));
        check("une position mutable retrouve le même node", store.contains(new BlockPos.Mutable(10, 64, 10)));
        check("index par chunk : deux nodes dans le premier chunk",
                store.inChunk(a.getX() >> 4, a.getZ() >> 4).size() == 2 && store.inChunk(c.getX() >> 4, c.getZ() >> 4).size() == 1);
        check("chunk sans node : liste vide", store.inChunk(50, 50).isEmpty());
        check("aucun node en attente au départ", store.pending().isEmpty());

        store.markEmpty(a, 1_000L);
        check("récolté : plus plein", !store.get(a).isFull());
        check("récolté : en attente", store.pending().size() == 1 && store.pending().contains(a));
        check("pas encore prêt avant l'heure", !store.get(a).isReadyAt(999L));
        check("prêt à l'heure", store.get(a).isReadyAt(1_000L));

        var encoded = NodeStore.CODEC.encodeStart(NbtOps.INSTANCE, store);
        check("encodage NBT réussi", encoded.result().isPresent());
        if (encoded.result().isEmpty()) {
            System.out.println("      " + encoded.error().map(DataResult.Error::message).orElse("?"));
            return;
        }
        var decoded = NodeStore.CODEC.parse(NbtOps.INSTANCE, encoded.result().get());
        check("décodage NBT réussi", decoded.result().isPresent());
        NodeStore copy = decoded.result().orElse(null);
        if (copy == null) {
            return;
        }
        check("trois nodes relus", copy.size() == 3);
        check("types relus", copy.get(a).type.equals(iron) && copy.get(c).type.equals(poppy));
        check("horodatage de repousse relu", copy.get(a).respawnAt == 1_000L && copy.get(b).isFull());
        check("index par chunk reconstruit", copy.inChunk(a.getX() >> 4, a.getZ() >> 4).size() == 2);
        check("ensemble en attente reconstruit", copy.pending().size() == 1 && copy.pending().contains(a));

        store.markFull(a);
        check("repoussé : plein, plus en attente", store.get(a).isFull() && store.pending().isEmpty());
        store.remove(b);
        check("retrait : plus dans la carte ni dans l'index",
                !store.contains(b) && store.inChunk(a.getX() >> 4, a.getZ() >> 4).size() == 1);
        store.remove(a);
        check("dernier node du chunk retiré : l'index oublie le chunk", store.inChunk(a.getX() >> 4, a.getZ() >> 4).isEmpty());
        check("retirer une position vide ne fait rien", store.remove(a) == null);

        var empty = NodeStore.CODEC.parse(NbtOps.INSTANCE,
                NodeStore.CODEC.encodeStart(NbtOps.INSTANCE, new NodeStore()).result().orElseThrow());
        check("registre vide : aller-retour", empty.result().map(s -> s.size() == 0).orElse(false));
    }

    /** Les types de node livrés : huit filons et six plantes, cohérents avec les métiers. */
    private static void testShippedNodes() {
        section("Types de node livrés — Mineur et Herboriste");
        Path data = locateData();
        if (data == null) {
            check("dossier data/ localisé", false);
            return;
        }
        Path folder = data.resolve("haute_capitale_metiers").resolve("hcm").resolve("nodes");
        List<Path> files = new ArrayList<>();
        try (Stream<Path> walk = Files.walk(folder)) {
            walk.filter(Files::isRegularFile).filter(path -> path.toString().endsWith(".json"))
                    .sorted().forEach(files::add);
        } catch (IOException e) {
            check("dossier des nodes lisible", false);
            return;
        }
        // Huit filons vanilla, dix-sept filons des mods du pack (Icaria, Witcher), sept plantes.
        check("soixante et un types livrés : vingt-cinq filons, trente-six plantes", files.size() == 61);

        int miner = 0;
        int herbalist = 0;
        List<Integer> minerLevels = new ArrayList<>();
        for (Path file : files) {
            String label = folder.relativize(file).toString().replace('\\', '/');
            NodeType type;
            try {
                type = NodeType.CODEC.parse(JsonOps.INSTANCE,
                        JsonParser.parseString(Files.readString(file, StandardCharsets.UTF_8))).result().orElse(null);
            } catch (IOException | RuntimeException e) {
                check(label + " se relit", false);
                continue;
            }
            check(label + " se relit", type != null);
            if (type == null) {
                continue;
            }
            check(label + " : dossier = métier", label.startsWith(type.profession().getId() + "/"));
            check(label + " : métier de récolte", type.profession() == Profession.MINEUR
                    || type.profession() == Profession.HERBORISTE);
            check(label + " : XP positive et butin non vide", type.xp() > 0.0D && !type.loot().isEmpty());
            check(label + " : XP = 3 × niveau + 12, la règle commune", type.xp() == 3 * type.level() + 12);
            check(label + " : bloc plein ≠ bloc vide", !type.fullBlock().equals(type.emptyBlock()));
            // Vanilla, le mod lui-même, ou les deux mods du pack dont on reprend les minerais :
            // une fiche d'un mod absent est écartée au chargement, comme une recette.
            List<String> known = List.of("minecraft:", "haute_capitale_metiers:", "landsoficaria:", "witcher_rpg:", "farmersdelight:");
            check(label + " : blocs vanilla, du mod, d'Icaria, de Witcher ou de Farmer's Delight",
                    known.stream().anyMatch(type.fullBlock()::startsWith) && known.stream().anyMatch(type.emptyBlock()::startsWith));
            check(label + " : un filon d'un autre mod le dit dans son commentaire",
                    type.fullBlock().startsWith("minecraft:") || type.fullBlock().startsWith("haute_capitale_metiers:")
                            || type.comment().map(c -> c.contains("requis")).orElse(false));

            if (type.profession() == Profession.MINEUR) {
                miner++;
                minerLevels.add(type.level());
                check(label + " : exige une pioche",
                        type.tool().map(tag -> tag.id().getPath().equals("pickaxes")).orElse(false));
            } else {
                herbalist++;
                check(label + " : se cueille à la main", type.tool().isEmpty());
            }
        }
        check("vingt-cinq filons de Mineur : huit vanilla, dix-sept des mods du pack", miner == 25);
        check("trente-six plantes d'Herboriste : sept vanilla, vingt d'Icaria, neuf de Farmer's Delight", herbalist == 36);
        check("les filons couvrent 1 → 40", minerLevels.contains(1) && minerLevels.contains(40));
    }

    // ------------------------------------------------------------------
    // Étape 7 — Chasseur et origines

    /** La raison d'apparition de Minecraft se traduit sans trou dans notre vocabulaire. */
    private static void testOriginMapping() {
        section("Origines — traduction des raisons d'apparition");
        check("naturelle ← NATURAL", OriginMarker.fromReason(SpawnReason.NATURAL) == SpawnOrigin.NATURELLE);
        check("naturelle ← CHUNK_GENERATION, STRUCTURE, PATROL, EVENT",
                OriginMarker.fromReason(SpawnReason.CHUNK_GENERATION) == SpawnOrigin.NATURELLE
                        && OriginMarker.fromReason(SpawnReason.STRUCTURE) == SpawnOrigin.NATURELLE
                        && OriginMarker.fromReason(SpawnReason.PATROL) == SpawnOrigin.NATURELLE
                        && OriginMarker.fromReason(SpawnReason.EVENT) == SpawnOrigin.NATURELLE);
        check("générateur ← SPAWNER et TRIAL_SPAWNER",
                OriginMarker.fromReason(SpawnReason.SPAWNER) == SpawnOrigin.SPAWNER
                        && OriginMarker.fromReason(SpawnReason.TRIAL_SPAWNER) == SpawnOrigin.SPAWNER);
        check("élevage ← BREEDING", OriginMarker.fromReason(SpawnReason.BREEDING) == SpawnOrigin.ELEVAGE);
        check("œuf ← SPAWN_ITEM_USE, DISPENSER, BUCKET",
                OriginMarker.fromReason(SpawnReason.SPAWN_ITEM_USE) == SpawnOrigin.OEUF
                        && OriginMarker.fromReason(SpawnReason.DISPENSER) == SpawnOrigin.OEUF
                        && OriginMarker.fromReason(SpawnReason.BUCKET) == SpawnOrigin.OEUF);
        check("commande ← COMMAND", OriginMarker.fromReason(SpawnReason.COMMAND) == SpawnOrigin.COMMANDE);
        check("invocation ← MOB_SUMMONED", OriginMarker.fromReason(SpawnReason.MOB_SUMMONED) == SpawnOrigin.INVOCATION);
        check("conversion, chargement, voyage : rien à décider",
                OriginMarker.fromReason(SpawnReason.CONVERSION) == null
                        && OriginMarker.fromReason(SpawnReason.LOAD) == null
                        && OriginMarker.fromReason(SpawnReason.DIMENSION_TRAVEL) == null);
        check("raison absente : rien à décider", OriginMarker.fromReason(null) == null);

        int decided = 0;
        for (SpawnReason reason : SpawnReason.values()) {
            if (OriginMarker.fromReason(reason) != null) {
                decided++;
            }
        }
        check("toutes les raisons sauf trois décident une origine", decided == SpawnReason.values().length - 3);

        check("étiquette : identifiant → origine", OriginMarker.byId("spawn_mmo") == SpawnOrigin.SPAWN_MMO
                && OriginMarker.byId("elevage") == SpawnOrigin.ELEVAGE);
        check("étiquette inconnue → rien", OriginMarker.byId("dragon") == null);
        check("préfixe d'étiquette documenté", OriginMarker.TAG_PREFIX.equals("hcm_origine_"));

        check("origine par défaut de la configuration : naturelle", MetiersConfig.get().defaultOrigin() == SpawnOrigin.NATURELLE);
        check("la fiche par défaut accepte naturelle et spawn_mmo, refuse élevage, générateur, œuf, commande",
                SpawnOrigin.DEFAUT.contains(SpawnOrigin.NATURELLE) && SpawnOrigin.DEFAUT.contains(SpawnOrigin.SPAWN_MMO)
                        && !SpawnOrigin.DEFAUT.contains(SpawnOrigin.ELEVAGE) && !SpawnOrigin.DEFAUT.contains(SpawnOrigin.SPAWNER)
                        && !SpawnOrigin.DEFAUT.contains(SpawnOrigin.OEUF) && !SpawnOrigin.DEFAUT.contains(SpawnOrigin.COMMANDE));
    }

    /**
     * Les fiches de proies livrées : la table du Chasseur de l'audit, les orcs
     * en butin de Martins, et rien qui contredise les décisions prises.
     */
    private static void testShippedPrey() {
        section("Proies livrées — table du Chasseur et Martins des orcs");
        Path root = locateData();
        if (root == null) {
            check("dossier data/ localisé", false);
            return;
        }
        Map<String, CreatureProfile> sheets = new java.util.TreeMap<>();
        try (Stream<Path> walk = Files.walk(root)) {
            for (Path path : walk.filter(Files::isRegularFile)
                    .filter(p -> p.toString().replace('\\', '/').contains("/hcm/creatures/"))
                    .filter(p -> p.toString().endsWith(".json")).toList()) {
                String rel = root.relativize(path).toString().replace('\\', '/');
                String ns = rel.substring(0, rel.indexOf('/'));
                String name = path.getFileName().toString().replace(".json", "");
                CreatureProfile profile = CreatureProfile.CODEC.parse(JsonOps.INSTANCE,
                        JsonParser.parseString(Files.readString(path, StandardCharsets.UTF_8))).result().orElse(null);
                if (profile != null) {
                    sheets.put(ns + ":" + name, profile);
                }
            }
        } catch (IOException | RuntimeException e) {
            check("parcours des fiches", false);
            return;
        }
        check("quarante-sept fiches livrées", sheets.size() == 47);

        long prey = sheets.values().stream().filter(CreatureProfile::isHuntable).count();
        check("trente-quatre proies déclarées", prey == 34);
        check("les dix-huit orcs ont leur fiche",
                sheets.keySet().stream().filter(k -> k.startsWith("autonomous_orc_mobs:")).count() == 18);

        for (Map.Entry<String, CreatureProfile> entry : sheets.entrySet()) {
            String id = entry.getKey();
            CreatureProfile profile = entry.getValue();
            profile.hunter().ifPresent(hunter -> check(id + " : niveau 1-50 et XP positive",
                    hunter.level() >= 1 && hunter.level() <= 50 && hunter.xp() > 0.0D));
            profile.hunter().ifPresent(hunter -> check(id + " : XP de chasse = 3 × niveau + 12, la règle commune",
                    hunter.xp() == 3 * hunter.level() + 12));
            profile.skinning().ifPresent(s -> check(id + " : XP de dépeçage = 1,5 × celle de la chasse",
                    s.xp() == Math.round(1.5D * (3 * s.level() + 12))));
            check(id + " : aucune origine d'élevage, de générateur, d'œuf ou de commande n'est éligible par défaut",
                    !profile.grantsXpFrom(SpawnOrigin.ELEVAGE) && !profile.grantsXpFrom(SpawnOrigin.OEUF)
                            && !profile.grantsXpFrom(SpawnOrigin.COMMANDE)
                            && (!profile.grantsXpFrom(SpawnOrigin.SPAWNER) || id.equals("autonomous_orc_mobs:troll")));
            for (DropEntry drop : profile.combatLoot()) {
                check(id + " : butin de combat = Martins", drop.item().toString().equals("capitale_currency:martin_dor")
                        || !id.startsWith("autonomous_orc_mobs:"));
            }
        }

        // Les décisions de l'audit, relues dans les fichiers.
        check("les orcs ne sont pas des proies (exclus à l'audit)",
                Stream.of("orc_warrior", "orc_archer", "orc_chief", "orc_champion", "female_orc")
                        .noneMatch(name -> sheets.get("autonomous_orc_mobs:" + name).isHuntable()));
        check("troll, ogre, minotaure : proies de niveau 39-40",
                sheets.get("autonomous_orc_mobs:troll").hunter().map(h -> h.level() == 39).orElse(false)
                        && sheets.get("autonomous_orc_mobs:ogre").hunter().map(h -> h.level() == 39).orElse(false)
                        && sheets.get("autonomous_orc_mobs:minotaur").hunter().map(h -> h.level() == 40).orElse(false));
        check("les civils orcs lâchent des Martins, mais peu et rarement",
                Stream.of("female_orc", "female_orc_seller", "female_orc_blacksmith")
                        .allMatch(name -> sheets.get("autonomous_orc_mobs:" + name).combatLoot().get(0).chance() <= 0.5D));
        check("les guerriers, archers, chefs lâchent des Martins",
                Stream.of("orc_warrior", "orc_archer", "orc_chief", "orc_champion", "orc_warlock", "orc_ravager")
                        .allMatch(name -> !sheets.get("autonomous_orc_mobs:" + name).combatLoot().isEmpty()));
        check("le chef lâche toujours", sheets.get("autonomous_orc_mobs:orc_chief").combatLoot().get(0).chance() == 1.0D);
        check("troll, ogre, minotaure : 15 à 30 Martins", Stream.of("troll", "ogre", "minotaur").allMatch(name ->
                sheets.get("autonomous_orc_mobs:" + name).combatLoot().get(0).min() == 15));
        check("lapin niveau 1, chevreuil niveau 6, tigre niveau 24 — la table de l'audit",
                sheets.get("minecraft:rabbit").hunter().map(h -> h.level() == 1).orElse(false)
                        && sheets.get("cubeanimals:roedeer").hunter().map(h -> h.level() == 6).orElse(false)
                        && sheets.get("cubeanimals:tiger").hunter().map(h -> h.level() == 24).orElse(false));
        check("la viande du chevreuil est la venaison de Cube Animals",
                sheets.get("cubeanimals:roedeer").meat().map(m -> m.item().toString().equals("cubeanimals:raw_venison")).orElse(false));
    }

    // ------------------------------------------------------------------
    // Étape 8 — carcasses et Dépeceur

    /** Le bloc « depecage » d'une fiche : ses nouveaux champs et leurs défauts. */
    private static void testSkinningSheet() {
        section("Fiche de dépeçage — canalisation et outil");
        CreatureProfile full = decode("""
                { "depecage": { "niveau": 22, "xp": 42, "duree_carcasse": 90, "canalisation": 5,
                                "outil": "haute_capitale_metiers:couteaux/expert",
                                "matiere": { "id": "haute_capitale_metiers:peau_epaisse", "min": 1, "max": 2 },
                                "secondaires": [ { "id": "minecraft:bone", "chance": 0.5 } ] } }
                """);
        CreatureProfile.SkinningEntry entry = full.skinning().orElseThrow();
        check("canalisation lue", entry.channelSeconds() == 5 && entry.channelMillis() == 5_000L);
        check("outil lu", entry.tool().map(id -> id.getPath().equals("couteaux/expert")).orElse(false));
        check("durée de carcasse en millisecondes", entry.carcassMillis() == 90_000L);

        CreatureProfile minimal = decode("""
                { "depecage": { "matiere": { "id": "minecraft:leather" } } }
                """);
        CreatureProfile.SkinningEntry defaults = minimal.skinning().orElseThrow();
        check("canalisation par défaut : 3 s", defaults.channelSeconds() == 3);
        check("outil par défaut : absent (celui de la configuration)", defaults.tool().isEmpty());
        check("carcasse par défaut : 60 s", defaults.carcassSeconds() == 60);

        CreatureProfile odd = decode("""
                { "depecage": { "canalisation": -4, "matiere": { "id": "minecraft:leather" } } }
                """);
        check("canalisation négative ramenée à 0 (instantanée)", odd.skinning().orElseThrow().channelSeconds() == 0);

        CreatureProfile.SkinningEntry shortForm = new CreatureProfile.SkinningEntry(3, 10.0D, 45,
                new DropEntry(Identifier.of("minecraft", "leather"), 1, 1, 1.0D), List.of());
        check("la forme courte garde les défauts", shortForm.channelSeconds() == 3 && shortForm.tool().isEmpty());

        check("aller-retour avec canalisation et outil", CreatureProfile.CODEC.encodeStart(JsonOps.INSTANCE, full)
                .flatMap(json -> CreatureProfile.CODEC.parse(JsonOps.INSTANCE, json)).result()
                .map(full::equals).orElse(false));
    }

    /**
     * Les fiches livrées : trente créatures dépeçables, le couteau du bon palier,
     * une matière d'une famille connue, et le tendon réservé aux niveaux 25 et plus.
     */
    private static void testShippedSkinning() {
        section("Dépeçage livré — paliers, matières, couteaux");
        Path root = locateData();
        if (root == null) {
            check("dossier data/ localisé", false);
            return;
        }
        Map<String, CreatureProfile> sheets = new java.util.TreeMap<>();
        try (Stream<Path> walk = Files.walk(root)) {
            for (Path path : walk.filter(Files::isRegularFile)
                    .filter(p -> p.toString().replace('\\', '/').contains("/hcm/creatures/"))
                    .filter(p -> p.toString().endsWith(".json")).toList()) {
                String rel = root.relativize(path).toString().replace('\\', '/');
                String ns = rel.substring(0, rel.indexOf('/'));
                String name = path.getFileName().toString().replace(".json", "");
                CreatureProfile profile = CreatureProfile.CODEC.parse(JsonOps.INSTANCE,
                        JsonParser.parseString(Files.readString(path, StandardCharsets.UTF_8))).result().orElse(null);
                if (profile != null) {
                    sheets.put(ns + ":" + name, profile);
                }
            }
        } catch (IOException | RuntimeException e) {
            check("parcours des fiches", false);
            return;
        }

        Set<String> materials = Set.of("minecraft:rabbit_hide", "fleshz:hide", "minecraft:feather",
                "haute_capitale_metiers:fourrure_commune", "haute_capitale_metiers:fourrure_epaisse",
                "haute_capitale_metiers:fourrure_rare", "haute_capitale_metiers:peau_epaisse",
                "landsoficaria:aeternae_hide", "hmobs:brown_bear_hide");
        long skinnable = sheets.values().stream().filter(CreatureProfile::isSkinnable).count();
        check("trente-quatre créatures dépeçables : chaque proie l'est, et seulement elles", skinnable == 34
                && sheets.values().stream().allMatch(p -> p.isSkinnable() == p.isHuntable()));

        for (Map.Entry<String, CreatureProfile> entry : sheets.entrySet()) {
            String id = entry.getKey();
            CreatureProfile profile = entry.getValue();
            if (profile.skinning().isEmpty()) {
                continue;
            }
            CreatureProfile.SkinningEntry s = profile.skinning().get();
            int level = s.level();
            check(id + " : même niveau que la chasse, XP positive, carcasse ≥ 60 s, canalisation 2-6 s",
                    profile.hunter().map(h -> h.level() == level).orElse(false) && s.xp() > 0.0D
                            && s.carcassSeconds() >= 60 && s.channelSeconds() >= 2 && s.channelSeconds() <= 6);
            String expectedTool = level <= 10 ? null : level <= 20 ? "couteaux/compagnon" : level <= 30 ? "couteaux/expert"
                    : level <= 40 ? "couteaux/maitre" : "couteaux/grand_maitre";
            check(id + " : le couteau du palier (" + (expectedTool == null ? "défaut" : expectedTool) + ")",
                    expectedTool == null ? s.tool().isEmpty() : s.tool().map(t -> t.getPath().equals(expectedTool)).orElse(false));
            check(id + " : matière d'une famille connue", materials.contains(s.material().item().toString()));
            boolean tendon = s.secondary().stream().anyMatch(d -> d.item().getPath().equals("tendon"));
            check(id + " : tendon seulement à partir du niveau 25", !tendon || level >= 25);
            if (level >= 26 && !id.contains("tiger") && !id.contains("crocodile") && !id.contains("komodo")) {
                check(id + " : à partir du niveau 26, du tendon en secondaire", tendon);
            }
        }

        check("troll, ogre, minotaure : une à deux peaux seulement (décision de l'audit)",
                Stream.of("troll", "ogre", "minotaur").allMatch(name -> {
                    CreatureProfile.SkinningEntry s = sheets.get("autonomous_orc_mobs:" + name).skinning().orElseThrow();
                    return s.material().min() == 1 && s.material().max() == 2;
                }));
        check("les civils orcs lâchent peu, rarement (1-3, une fois sur deux)",
                Stream.of("female_orc", "female_orc_seller", "female_orc_blacksmith").allMatch(name -> {
                    List<DropEntry> loot = sheets.get("autonomous_orc_mobs:" + name).combatLoot();
                    return loot.size() == 1 && loot.get(0).max() == 3 && loot.get(0).chance() == 0.5D;
                }));
        check("les élites lâchent toujours 10 à 25 Martins",
                Stream.of("female_orc_elite", "orc_champion", "orc_chief").allMatch(name -> {
                    List<DropEntry> loot = sheets.get("autonomous_orc_mobs:" + name).combatLoot();
                    return loot.size() == 1 && loot.get(0).min() == 10 && loot.get(0).max() == 25 && loot.get(0).chance() == 1.0D;
                }));

        // Les cinq familles de couteaux : Farmer's Delight, les dagues d'Icaria, le croc
        // de lion — tout facultatif — et chaque palier inclus dans le précédent.
        Path tags = root.resolve("haute_capitale_metiers").resolve("tags").resolve("item");
        List<String> knifeTags = List.of("couteaux.json", "couteaux/compagnon.json", "couteaux/expert.json",
                "couteaux/maitre.json", "couteaux/grand_maitre.json");
        Set<String> previous = null;
        for (String tag : knifeTags) {
            JsonObject json = readJson(tags.resolve(tag));
            if (json == null) {
                check("tag " + tag + " lisible", false);
                continue;
            }
            Set<String> ids = new HashSet<>();
            boolean allOptional = true;
            for (var value : json.getAsJsonArray("values")) {
                ids.add(value.getAsJsonObject().get("id").getAsString());
                allOptional &= !value.getAsJsonObject().get("required").getAsBoolean();
            }
            Set<String> ns = new HashSet<>();
            ids.forEach(id -> ns.add(id.substring(0, id.indexOf(':'))));
            check("tag " + tag + " : facultatif, Farmer's Delight + Icaria, palier inclus dans le précédent",
                    allOptional && ns.contains("farmersdelight") && ns.contains("landsoficaria")
                            && (previous == null || previous.containsAll(ids)) && (previous == null || ids.size() < previous.size()));
            previous = ids;
        }
    }

    // ------------------------------------------------------------------
    // Étape 9 — Forgeron : fabrication et réparation

    /** Le barème par défaut, la marque brisée, le rôle qui répare. */
    private static void testRepairConfig() {
        section("Réparation — barème et rôle");
        MetiersConfig.Reparation scale = MetiersConfig.get().reparation;
        check("coefficients de l'audit : 0,05 · 0,08 · 0,12 · 0,20 · 0,35",
                scale.commun == 0.05D && scale.peu_commun == 0.08D && scale.rare == 0.12D
                        && scale.epique == 0.20D && scale.legendaire == 0.35D);
        check("objet brisé × 2,5, minimum 1 Martin, aucune exception", scale.brise == 2.5D && scale.minimum == 1
                && scale.exceptions.isEmpty());

        NpcRole repairs = decodeRole("""
                { "interface": "atelier", "metier": "forgeron", "reparation": true }
                """);
        NpcRole plain = decodeRole("""
                { "interface": "atelier", "metier": "forgeron" }
                """);
        check("« reparation »: true se lit", repairs != null && repairs.repairs());
        check("absent : pas de réparation", plain != null && !plain.repairs());

        Path data = locateData();
        JsonObject forgeron = data == null ? null
                : readJson(data.resolve("haute_capitale_metiers").resolve("hcm").resolve("roles").resolve("forgeron.json"));
        check("le rôle forgeron livré répare", forgeron != null && forgeron.has("reparation")
                && forgeron.get("reparation").getAsBoolean());
        long repairingRoles = 0;
        if (data != null) {
            try (Stream<Path> walk = Files.walk(data.resolve("haute_capitale_metiers").resolve("hcm").resolve("roles"))) {
                repairingRoles = walk.filter(Files::isRegularFile).map(DonneesTest::readJson)
                        .filter(json -> json != null && json.has("reparation") && json.get("reparation").getAsBoolean()).count();
            } catch (IOException e) {
                repairingRoles = -1;
            }
        }
        check("et lui seul", repairingRoles == 1);

        RepairData.RepairEntry entry = new RepairData.RepairEntry(3, Text.literal("Pioche"), 100, 250, false, 5);
        check("pourcentage de durabilité : 150/250 = 60 %", entry.percent() == 60);
        RepairData.RepairEntry broken = new RepairData.RepairEntry(3, Text.literal("Pioche"), 250, 250, true, 32);
        check("brisé : 0 %", broken.percent() == 0);

        Path tags = data == null ? null : data.resolve("haute_capitale_metiers").resolve("tags").resolve("item");
        JsonObject repairable = tags == null ? null : readJson(tags.resolve("reparable_si_brise.json"));
        check("la famille reparable_si_brise couvre tout ce qui s'enchante par la durabilité",
                repairable != null && repairable.getAsJsonArray("values").asList().stream()
                        .anyMatch(v -> v.getAsString().equals("#minecraft:enchantable/durability")));
        JsonObject legendary = tags == null ? null : readJson(tags.resolve("legendaire.json"));
        check("la famille légendaire existe, vide par défaut", legendary != null && legendary.getAsJsonArray("values").isEmpty());
    }

    /** Les recettes du Forgeron : composants d'Epic Knights et dagues d'Icaria, par palier. */
    private static void testShippedSmithing() {
        section("Recettes livrées — Forgeron");
        Path data = locateData();
        if (data == null) {
            check("dossier data/ localisé", false);
            return;
        }
        Path folder = data.resolve("haute_capitale_metiers").resolve("hcm").resolve("recipes").resolve("forgeron");
        List<Path> files = new ArrayList<>();
        try (Stream<Path> walk = Files.walk(folder)) {
            walk.filter(Files::isRegularFile).filter(path -> path.toString().endsWith(".json")).sorted().forEach(files::add);
        } catch (IOException e) {
            check("dossier des recettes du Forgeron lisible", false);
            return;
        }
        check("douze recettes de Forgeron : six composants, six dagues", files.size() == 12);
        List<Integer> daggerLevels = new ArrayList<>();
        for (Path file : files) {
            String label = file.getFileName().toString();
            CraftRecipe recipe;
            try {
                recipe = CraftRecipe.CODEC.parse(JsonOps.INSTANCE,
                        JsonParser.parseString(Files.readString(file, StandardCharsets.UTF_8))).result().orElse(null);
            } catch (IOException | RuntimeException e) {
                check(label + " se relit", false);
                continue;
            }
            check(label + " se relit, Forgeron, XP = 3 × niveau + 12", recipe != null
                    && recipe.profession() == Profession.FORGERON && recipe.xp() == 3 * recipe.level() + 12);
            if (recipe == null) {
                continue;
            }
            String result = recipe.result().item().toString();
            check(label + " : résultat d'Epic Knights ou d'Icaria",
                    result.startsWith("magistuarmory:") || result.startsWith("landsoficaria:"));
            if (result.endsWith("_dagger")) {
                daggerLevels.add(recipe.level());
            }
        }
        check("les dagues couvrent les cinq paliers du Dépeceur (1, 11, 21, 31, 41)",
                daggerLevels.containsAll(List.of(1, 11, 21, 31, 41)));
        check("la lanière de cuir est une recette du Travailleur du cuir", Files.isRegularFile(
                folder.getParent().resolve("travailleur_du_cuir").resolve("lanieres_de_cuir.json")));
    }

    // ------------------------------------------------------------------
    // Étape 10 — gadgets et foyer

    /** Les réglages des gadgets et du foyer : valeurs de l'audit, bornes de sécurité. */
    private static void testGadgetConfig() {
        section("Gadgets et foyer — réglages");
        MetiersConfig.Gadgets gadgets = MetiersConfig.get().gadgets;
        check("rayons par défaut : aimant 6, scanner 12, géode 48, écho 96, trésor 12",
                gadgets.aimant_rayon == 6.0D && gadgets.scanner_rayon == 12 && gadgets.geode_rayon == 48
                        && gadgets.echo_rayon == 96 && gadgets.tresor_rayon == 12);
        check("repoussants croissants : sel 5 min/8, onguent 15 min/16, baume 60 min/24",
                gadgets.sel_secondes == 300 && gadgets.sel_rayon == 8
                        && gadgets.onguent_secondes == 900 && gadgets.onguent_rayon == 16
                        && gadgets.baume_secondes == 3600 && gadgets.baume_rayon == 24);
        check("rappel : 4 s de canalisation, 3 min de recharge, 1500 blocs",
                gadgets.rappel_canalisation_secondes == 4 && gadgets.rappel_recharge_secondes == 180
                        && gadgets.rappel_portee == 1500.0D);

        MetiersConfig.Foyer foyer = MetiersConfig.get().foyer;
        check("foyer : 5 s de canalisation, 15 min de recharge (5 améliorée), 4000 blocs ×2, pierre offerte",
                foyer.canalisation_secondes == 5 && foyer.recharge_secondes == 900
                        && foyer.amelioree_recharge_secondes == 300 && foyer.portee == 4000.0D
                        && foyer.amelioree_portee_facteur == 2.0D && !foyer.autre_dimension && foyer.pierre_offerte);

        MetiersConfig wild = MetiersConfig.fromJson("""
                {
                  "gadgets": { "aimant_rayon": 999, "scanner_rayon": -4, "obscurite": 40, "grappin_portee": "NaN",
                               "drone_ramassage": 0 },
                  "foyer": { "canalisation_secondes": -3, "portee": -1, "amelioree_portee_facteur": 0.1 }
                }
                """);
        check("valeurs folles bornées : aimant 32, scanner 2, obscurité 15, grappin 24, ramassage 1",
                wild != null && wild.gadgets.aimant_rayon == 32.0D && wild.gadgets.scanner_rayon == 2
                        && wild.gadgets.obscurite == 15 && wild.gadgets.grappin_portee == 24.0D
                        && wild.gadgets.drone_ramassage == 1.0D);
        check("foyer borné : canalisation 0, portée 0 (illimitée), facteur 1",
                wild != null && wild.foyer.canalisation_secondes == 0 && wild.foyer.portee == 0.0D
                        && wild.foyer.amelioree_portee_facteur == 1.0D);
    }

    /** Le rôle qui lie le foyer : l'aubergiste, et lui seul. */
    private static void testHearthRole() {
        section("Foyer — rôle de l'aubergiste");
        NpcRole hearth = decodeRole("""
                { "interface": "registre", "foyer": true }
                """);
        NpcRole plain = decodeRole("""
                { "interface": "registre" }
                """);
        check("« foyer »: true se lit", hearth != null && hearth.hearth());
        check("absent : pas de foyer", plain != null && !plain.hearth());

        Path data = locateData();
        Path roles = data == null ? null : data.resolve("haute_capitale_metiers").resolve("hcm").resolve("roles");
        JsonObject aubergiste = roles == null ? null : readJson(roles.resolve("aubergiste.json"));
        check("l'aubergiste livré lie le foyer", aubergiste != null && aubergiste.has("foyer")
                && aubergiste.get("foyer").getAsBoolean());
        long hearthRoles = 0;
        if (roles != null) {
            try (Stream<Path> walk = Files.walk(roles)) {
                hearthRoles = walk.filter(Files::isRegularFile).map(DonneesTest::readJson)
                        .filter(json -> json != null && json.has("foyer") && json.get("foyer").getAsBoolean()).count();
            } catch (IOException e) {
                hearthRoles = -1;
            }
        }
        check("et lui seul", hearthRoles == 1);
    }

    /** Les recettes de l'Ingénieur : un gadget par palier, tout ce qu'il faut pour les fabriquer existe. */
    private static void testShippedEngineering() {
        section("Recettes livrées — Ingénieur");
        Path data = locateData();
        if (data == null) {
            check("dossier data/ localisé", false);
            return;
        }
        Path folder = data.resolve("haute_capitale_metiers").resolve("hcm").resolve("recipes").resolve("ingenieur");
        List<Path> files = new ArrayList<>();
        try (Stream<Path> walk = Files.walk(folder)) {
            walk.filter(Files::isRegularFile).filter(path -> path.toString().endsWith(".json")).sorted().forEach(files::add);
        } catch (IOException e) {
            check("dossier des recettes de l'Ingénieur lisible", false);
            return;
        }
        check("trente recettes : chaque gadget sauf la Pierre de foyer simple, offerte par l'aubergiste",
                files.size() == 30);

        Set<String> ownItems = new HashSet<>(MATERIALS);
        ownItems.addAll(GADGETS);
        Set<String> crafted = new HashSet<>();
        List<Integer> levels = new ArrayList<>();
        for (Path file : files) {
            String label = file.getFileName().toString();
            CraftRecipe recipe;
            try {
                recipe = CraftRecipe.CODEC.parse(JsonOps.INSTANCE,
                        JsonParser.parseString(Files.readString(file, StandardCharsets.UTF_8))).result().orElse(null);
            } catch (IOException | RuntimeException e) {
                check(label + " se relit", false);
                continue;
            }
            check(label + " se relit, Ingénieur, XP = 3 × niveau + 12", recipe != null
                    && recipe.profession() == Profession.INGENIEUR && recipe.xp() == 3 * recipe.level() + 12);
            if (recipe == null) {
                continue;
            }
            Identifier result = recipe.result().item();
            check(label + " : le résultat est un gadget du mod, nommé comme le fichier",
                    result.getNamespace().equals("haute_capitale_metiers") && GADGETS.contains(result.getPath())
                            && label.equals(result.getPath() + ".json"));
            check(label + " : nos ingrédients existent, les autres sont nommés",
                    recipe.ingredients().stream().allMatch(ingredient -> ingredient.item()
                            .map(id -> !id.getNamespace().equals("haute_capitale_metiers") || ownItems.contains(id.getPath()))
                            .orElse(false) && ingredient.name().isPresent()));
            check(label + " : coût en Martins non négatif", recipe.cost() >= 0);
            crafted.add(result.getPath());
            levels.add(recipe.level());
        }
        Set<String> uncrafted = new HashSet<>(GADGETS);
        uncrafted.removeAll(crafted);
        check("seule la Pierre de foyer simple n'a pas de recette", uncrafted.equals(Set.of("pierre_de_foyer")));
        check("les paliers de l'audit sont là : 6, 8, 12, 20, 30, 40, 45",
                levels.containsAll(List.of(6, 8, 12, 20, 30, 40, 45)));
        check("aucune recette au-delà du niveau 50", levels.stream().allMatch(level -> level >= 1 && level <= 50));
    }

    // ------------------------------------------------------------------
    // Étape 11 — qualité, repas, Couturier, Joaillier, Cuisinier

    /** La qualité Excellent : la chance monte avec l'écart de niveau, jamais en dessous de l'écart minimum. */
    private static void testQualityConfig() {
        section("Qualité — réglages et chance");
        MetiersConfig.Qualite quality = MetiersConfig.get().qualite;
        check("défauts : écart minimum 3, 5 % de base, +1,5 % par niveau, plafond 35 %",
                quality.ecart_minimum == 3 && quality.chance_base == 0.05D && quality.chance_par_niveau == 0.015D
                        && quality.chance_max == 0.35D);
        check("à niveau égal, ou en dessous de l'écart : aucune chance",
                quality.chance(10, 10) == 0.0D && quality.chance(12, 10) == 0.0D && quality.chance(5, 10) == 0.0D);
        check("à l'écart minimum : la chance de base", quality.chance(13, 10) == 0.05D);
        check("+10 niveaux d'écart : 5 % + 7 × 1,5 % = 15,5 %", Math.abs(quality.chance(20, 10) - 0.155D) < 1e-9);
        check("très au-dessus : le plafond", quality.chance(50, 1) == 0.35D);
        MetiersConfig.Repas meal = MetiersConfig.get().repas;
        check("repas : Excellent × 4/3, 15 minutes par défaut",
                Math.abs(meal.excellent_facteur - 4.0D / 3.0D) < 1e-9 && meal.duree_secondes == 900);
        MetiersConfig wild = MetiersConfig.fromJson("""
                { "qualite": { "chance_base": 7, "chance_max": -1, "ecart_minimum": -4 }, "repas": { "excellent_facteur": 0.2 } }
                """);
        check("valeurs folles bornées : base 1, plafond 0, écart 0, facteur 1",
                wild != null && wild.qualite.chance_base == 1.0D && wild.qualite.chance_max == 0.0D
                        && wild.qualite.ecart_minimum == 0 && wild.repas.excellent_facteur == 1.0D);

        CraftRecipe plain = decodeRecipe("""
                { "metier": "cuisinier", "niveau": 6, "xp": 30, "ingredients": [ { "id": "minecraft:bone" } ],
                  "resultat": { "id": "farmersdelight:bone_broth" } }
                """);
        CraftRecipe material = decodeRecipe("""
                { "metier": "couturier", "niveau": 3, "xp": 21, "qualite": false,
                  "ingredients": [ { "id": "haute_capitale_metiers:fibre_de_coton", "quantite": 4 } ],
                  "resultat": { "id": "haute_capitale_metiers:bobine_de_coton" } }
                """);
        check("« qualite » vaut vrai par défaut, et se refuse pour les matières",
                plain != null && plain.quality() && material != null && !material.quality());
    }

    /** La fiche de buff, et les recettes qui la portent. */
    private static void testMealBuffSheet() {
        section("Repas — fiche de buff");
        MealBuff buff = decodeBuff("""
                { "titre": "Vigueur", "attribut": "minecraft:max_health", "operation": "pourcent_base",
                  "valeur": 0.05, "duree": 900, "commentaire": "test" }
                """);
        check("une fiche complète se relit", buff != null && buff.attribute().equals(Identifier.ofVanilla("max_health"))
                && buff.operation() == MealBuff.Operation.POURCENT_BASE && buff.value() == 0.05D
                && buff.seconds().orElse(0) == 900 && buff.title().orElse("").equals("Vigueur"));
        MealBuff minimal = decodeBuff("""
                { "attribut": "puffish_attributes:stamina", "valeur": 0.05 }
                """);
        check("fiche minimale : opération pourcent_base, durée et titre absents",
                minimal != null && minimal.operation() == MealBuff.Operation.POURCENT_BASE
                        && minimal.seconds().isEmpty() && minimal.title().isEmpty());
        check("les trois opérations ont leur mot : ajout, pourcent_base, pourcent_total",
                decodeBuff("{ \"attribut\": \"minecraft:armor\", \"operation\": \"ajout\", \"valeur\": 2 }").operation() == MealBuff.Operation.AJOUT
                        && decodeBuff("{ \"attribut\": \"minecraft:armor\", \"operation\": \"pourcent_total\", \"valeur\": 0.1 }").operation() == MealBuff.Operation.POURCENT_TOTAL);
        String error = MealBuff.CODEC.parse(JsonOps.INSTANCE, JsonParser.parseString(
                "{ \"attribut\": \"minecraft:armor\", \"operation\": \"multiplier\", \"valeur\": 1 }"))
                .error().map(DataResult.Error::message).orElse("");
        check("une opération inconnue est refusée en nommant les valeurs acceptées",
                error.contains("operation") && error.contains("pourcent_base"));
        check("une durée nulle ou négative vaut « absente »",
                decodeBuff("{ \"attribut\": \"minecraft:armor\", \"valeur\": 1, \"duree\": 0 }").seconds().isEmpty());

        CraftRecipe meal = decodeRecipe("""
                { "metier": "cuisinier", "niveau": 22, "xp": 78, "ingredients": [ { "tag": "c:foods/raw_beef" } ],
                  "repas": { "buff": "haute_capitale_metiers:melee", "valeur": 0.057 },
                  "resultat": { "id": "farmersdelight:beef_stew" } }
                """);
        check("une recette lit son « repas » : buff et valeur", meal != null && meal.meal().isPresent()
                && meal.meal().get().buff().equals(Identifier.of("haute_capitale_metiers", "melee"))
                && meal.meal().get().value().orElse(0.0D) == 0.057D);

        Path data = locateData();
        Path folder = data == null ? null : data.resolve("haute_capitale_metiers").resolve("hcm").resolve("buffs");
        List<Path> files = new ArrayList<>();
        if (folder != null) {
            try (Stream<Path> walk = Files.walk(folder)) {
                walk.filter(Files::isRegularFile).sorted().forEach(files::add);
            } catch (IOException e) {
                check("dossier des buffs lisible", false);
                return;
            }
        }
        check("quinze buffs livrés", files.size() == 15);
        Set<String> namespaces = new HashSet<>();
        boolean allGood = true;
        for (Path file : files) {
            MealBuff shipped;
            try {
                shipped = MealBuff.CODEC.parse(JsonOps.INSTANCE,
                        JsonParser.parseString(Files.readString(file, StandardCharsets.UTF_8))).result().orElse(null);
            } catch (IOException | RuntimeException e) {
                shipped = null;
            }
            allGood &= shipped != null && shipped.title().isPresent() && shipped.value() > 0.0D
                    && shipped.value() <= 0.5D && shipped.seconds().orElse(0) >= 600;
            if (shipped != null) {
                namespaces.add(shipped.attribute().getNamespace());
            }
        }
        check("chacun se relit, titré, en pourcentage raisonnable, dix minutes au moins", allGood);
        check("les attributs viennent de Minecraft, Puffish Attributes et Spell Power — rien d'autre",
                namespaces.equals(Set.of("minecraft", "puffish_attributes", "spell_power")));
        check("le buff « festin » dure plus longtemps que les autres", files.stream().anyMatch(f -> f.getFileName().toString().equals("festin.json")));
    }

    /** Les recettes livrées d'un artisan de l'étape 11, lues et contrôlées avec la même grille. */
    private static List<CraftRecipe> shippedRecipes(String profession, int expected, String label) {
        Path data = locateData();
        List<CraftRecipe> recipes = new ArrayList<>();
        if (data == null) {
            check("dossier data/ localisé", false);
            return recipes;
        }
        Path folder = data.resolve("haute_capitale_metiers").resolve("hcm").resolve("recipes").resolve(profession);
        List<Path> files = new ArrayList<>();
        try (Stream<Path> walk = Files.walk(folder)) {
            walk.filter(Files::isRegularFile).filter(path -> path.toString().endsWith(".json")).sorted().forEach(files::add);
        } catch (IOException e) {
            check("dossier des recettes du " + label + " lisible", false);
            return recipes;
        }
        check(expected + " recettes de " + label, files.size() == expected);
        boolean allGood = true;
        for (Path file : files) {
            CraftRecipe recipe;
            try {
                recipe = CraftRecipe.CODEC.parse(JsonOps.INSTANCE,
                        JsonParser.parseString(Files.readString(file, StandardCharsets.UTF_8))).result().orElse(null);
            } catch (IOException | RuntimeException e) {
                recipe = null;
            }
            if (recipe == null || !recipe.profession().getId().equals(profession) || recipe.xp() != 3 * recipe.level() + 12
                    || recipe.ingredients().stream().anyMatch(i -> i.name().isEmpty())) {
                System.out.println("      fautive : " + file.getFileName());
                allGood = false;
                continue;
            }
            recipes.add(recipe);
        }
        check("chacune se relit : bon métier, XP = 3 × niveau + 12, ingrédients nommés", allGood);
        return recipes;
    }

    /** Le Couturier : la chaîne textile, puis les vêtements des mods RPG. */
    private static void testShippedTailoring() {
        section("Recettes livrées — Couturier");
        List<CraftRecipe> recipes = shippedRecipes("couturier", 88, "Couturier");
        Set<String> own = new HashSet<>(TEXTILES);
        check("toutes les matières et composants textiles ont leur recette, jamais Excellent", recipes.stream()
                .filter(r -> r.result().item().getNamespace().equals("haute_capitale_metiers"))
                .filter(r -> !r.quality())
                .map(r -> r.result().item().getPath()).collect(java.util.stream.Collectors.toSet())
                .containsAll(TEXTILES.subList(1, 26)));
        check("les vêtements du mod peuvent être Excellent", recipes.stream()
                .filter(r -> own.contains(r.result().item().getPath()) && TEXTILES.indexOf(r.result().item().getPath()) >= 26)
                .allMatch(CraftRecipe::quality));
        Set<String> external = recipes.stream().map(r -> r.result().item().getNamespace())
                .filter(ns -> !ns.equals("haute_capitale_metiers")).collect(java.util.stream.Collectors.toSet());
        check("les produits finis viennent des mods RPG du pack : accents, wizards, armory, bards, elemental, forcemaster, death_knights, runes, mmo_accessories, hazennstuff",
                external.equals(Set.of("accents", "wizards", "armory_rpgs", "bards_rpg", "elemental_wizards_rpg",
                        "forcemaster_rpg", "death_knights", "runes", "mmo_accessories", "hazennstuff")));
        List<Integer> levels = recipes.stream().map(CraftRecipe::level).toList();
        check("la progression couvre 1 → 50 : bobine 1, tissu 6, chemise 13, soie 28, cape royale 40, diadème 50",
                levels.containsAll(List.of(1, 6, 13, 28, 40, 50)));
        check("le diadème étoilé demande des gemmes du Joaillier", recipes.stream()
                .filter(r -> r.result().item().getPath().equals("starlit_diadem"))
                .anyMatch(r -> r.ingredients().stream().anyMatch(i -> i.item().map(id -> id.getNamespace().equals("jewelry")).orElse(false))));
        check("le coton entre par la fibre : 1 coton → 3 fibres", recipes.stream()
                .anyMatch(r -> r.result().item().getPath().equals("fibre_de_coton") && r.result().count() == 3
                        && r.ingredients().size() == 1 && r.ingredients().get(0).item().map(id -> id.getPath().equals("coton")).orElse(false)));
    }

    /** Le Joaillier : rien à créer, que des fichiers. */
    private static void testShippedJewelry() {
        section("Recettes livrées — Joaillier");
        List<CraftRecipe> recipes = shippedRecipes("joaillier", 53, "Joaillier");
        Set<String> external = recipes.stream().map(r -> r.result().item().getNamespace()).collect(java.util.stream.Collectors.toSet());
        check("les bijoux viennent de jewelry et mmo_accessories, les gemmes taillées d'Icaria et Hazen's Stuff",
                external.equals(Set.of("jewelry", "mmo_accessories", "landsoficaria", "hazennstuff")));
        check("aucune recette ne produit un objet de notre mod", recipes.stream().noneMatch(r -> r.result().item().getNamespace().equals("haute_capitale_metiers")));
        check("les tailles de gemme ne sont jamais Excellent, les bijoux peuvent l'être", recipes.stream()
                .allMatch(r -> r.quality() == (r.result().item().getPath().endsWith("_ring") || r.result().item().getPath().endsWith("_necklace"))));
        List<Integer> levels = recipes.stream().map(CraftRecipe::level).toList();
        check("cuivre 1, citrine 12, rubis 21, tanzanite 31, netherite 36 → 46",
                levels.containsAll(List.of(1, 12, 21, 31, 36, 46)));
        check("les douze bijoux jewelry en netherite sont là, entre 36 et 46", recipes.stream()
                .filter(r -> r.result().item().getNamespace().equals("jewelry") && r.result().item().getPath().startsWith("netherite_"))
                .filter(r -> r.level() >= 36 && r.level() <= 46).count() == 12);
    }

    /** L'Alchimiste : des potions vanilla, à partir des plantes de l'Herboriste. */
    private static void testShippedAlchemy() {
        section("Recettes livrées — Alchimiste");
        List<CraftRecipe> recipes = shippedRecipes("alchimiste", 33, "Alchimiste");
        check("chaque recette produit une potion vanilla : à boire, jetable ou persistante", recipes.stream()
                .allMatch(r -> r.result().item().getNamespace().equals("minecraft")
                        && Set.of("potion", "splash_potion", "lingering_potion").contains(r.result().item().getPath())));
        check("chaque résultat porte son contenu de potion en composant", recipes.stream().allMatch(r -> r.result().components()
                .flatMap(d -> d.get("minecraft:potion_contents").get("potion").asString().result())
                .map(potion -> potion.startsWith("minecraft:")).orElse(false)));
        check("une potion n'est jamais Excellent", recipes.stream().noneMatch(CraftRecipe::quality));
        check("chaque recette consomme une fiole", recipes.stream().allMatch(r -> r.ingredients().stream()
                .anyMatch(i -> i.item().map(id -> id.toString().equals("minecraft:glass_bottle")).orElse(false))));
        check("chaque recette consomme une plante de l'Herboriste — d'Icaria, ou une fleur vanilla", recipes.stream().allMatch(r -> r.ingredients().stream()
                .anyMatch(i -> i.item().map(id -> id.getNamespace().equals("landsoficaria")
                        || List.of("minecraft:dandelion", "minecraft:poppy").contains(id.toString())).orElse(false))));
        List<Integer> levels = recipes.stream().map(CraftRecipe::level).toList();
        check("du niveau 1 au niveau 50, les paliers II, prolongés, jetables et persistants aux bons endroits",
                levels.contains(1) && levels.contains(50) && levels.stream().allMatch(l -> l >= 1 && l <= 50)
                        && recipes.stream().filter(r -> r.result().item().getPath().equals("splash_potion")).allMatch(r -> r.level() >= 36)
                        && recipes.stream().filter(r -> r.result().item().getPath().equals("lingering_potion")).allMatch(r -> r.level() >= 46));
        check("XP = 3 × niveau + 12", recipes.stream().allMatch(r -> r.xp() == 3 * r.level() + 12));
    }

    /** Le Cuisinier : les plats de Farmer's Delight et d'Icaria, chacun avec son buff. */
    private static void testShippedCooking() {
        section("Recettes livrées — Cuisinier");
        List<CraftRecipe> recipes = shippedRecipes("cuisinier", 68, "Cuisinier");
        Set<String> external = recipes.stream().map(r -> r.result().item().getNamespace()).collect(java.util.stream.Collectors.toSet());
        check("les plats viennent de Farmer's Delight, Icaria, Hazen's Stuff, Cube Animals — et la tarte à la citrouille de Minecraft",
                external.equals(Set.of("farmersdelight", "landsoficaria", "hazennstuff", "cubeanimals", "minecraft")));
        Path data = locateData();
        Set<String> buffs = new HashSet<>();
        if (data != null) {
            try (Stream<Path> walk = Files.walk(data.resolve("haute_capitale_metiers").resolve("hcm").resolve("buffs"))) {
                walk.filter(Files::isRegularFile).forEach(p -> buffs.add(p.getFileName().toString().replace(".json", "")));
            } catch (IOException e) {
                check("dossier des buffs lisible", false);
            }
        }
        List<CraftRecipe> meals = recipes.stream().filter(r -> r.meal().isPresent()).toList();
        check("tout plat porte un buff qui existe ; les cinq intermédiaires et les cinq résultats non consommables (gâteaux d'Icaria, corned-beef, œuf frit) n'en portent pas",
                meals.size() == recipes.size() - 10 && meals.stream().allMatch(r -> buffs.contains(r.meal().get().buff().getPath())));
        check("les intermédiaires ne sont jamais Excellent, les plats peuvent l'être",
                recipes.stream().allMatch(r -> r.quality() == r.meal().isPresent()));
        check("la valeur du buff suit le niveau : 3 % à 10, 12 % à 50, festins à 10 %", meals.stream().allMatch(r -> {
            double value = r.meal().get().value().orElse(-1.0D);
            if (r.meal().get().buff().getPath().equals("festin")) {
                return value == 0.10D;
            }
            double expected = Math.max(0.03D, Math.min(0.12D, 0.03D + 0.09D * (r.level() - 10) / 40.0D));
            return Math.abs(value - expected) < 0.0006D;
        }));
        check("cinq festins, en portions de quatre", meals.stream().filter(r -> r.meal().get().buff().getPath().equals("festin"))
                .filter(r -> r.result().count() == 4).count() == 5);
        List<Integer> levels = recipes.stream().map(CraftRecipe::level).toList();
        check("œuf au plat 3, hamburger 21, poulet rôti 35, ragoût de thog 45, œuf d'araignée 47",
                levels.containsAll(List.of(3, 21, 35, 45, 47)));
        check("les familles d'ingrédients sont celles de Farmer's Delight (c:) et de Minecraft", recipes.stream()
                .flatMap(r -> r.ingredients().stream()).flatMap(i -> i.tag().stream())
                .allMatch(tag -> tag.id().getNamespace().equals("c") || tag.id().getNamespace().equals("minecraft")));

        // La marmite bridée : chaque recette retirée correspond à un plat de Cuisinier de niveau 20+.
        Path pot = locateResource("/resourcepacks");
        Path overrides = pot == null ? null : pot.resolve("marmite").resolve("data").resolve("farmersdelight").resolve("recipe").resolve("cooking");
        List<Path> removed = new ArrayList<>();
        if (overrides != null) {
            try (Stream<Path> walk = Files.walk(overrides)) {
                walk.filter(Files::isRegularFile).sorted().forEach(removed::add);
            } catch (IOException e) {
                check("datapack marmite lisible", false);
            }
        }
        check("quatorze recettes de marmite retirées, et le pack a son pack.mcmeta", removed.size() == 14
                && pot != null && Files.isRegularFile(pot.resolve("marmite").resolve("pack.mcmeta")));
        Set<String> highLevelDishes = recipes.stream().filter(r -> r.level() >= 20)
                .map(r -> r.result().item().getPath()).collect(java.util.stream.Collectors.toSet());
        boolean coherent = true;
        for (Path file : removed) {
            JsonObject json = readJson(file);
            String result = json == null ? "" : json.getAsJsonObject("result").get("id").getAsString().replace("farmersdelight:", "");
            String dish = result.replace("_block", "");
            boolean matches = highLevelDishes.contains(result) || highLevelDishes.contains(dish);
            boolean disabled = json != null && json.has("fabric:load_conditions");
            if (!matches || !disabled) {
                System.out.println("      incohérente : " + file.getFileName());
                coherent = false;
            }
        }
        check("chacune vise un plat de niveau 20+ et porte une condition de chargement fausse", coherent);
        check("les plats simples restent à la marmite : bouillon d'os, soupe à l'oignon, riz cuit",
                removed.stream().map(p -> p.getFileName().toString()).noneMatch(n -> n.equals("bone_broth.json") || n.equals("onion_soup.json") || n.equals("cooked_rice.json")));
    }

    /** Le cotonnier : un node de l'Herboriste sur nos deux blocs, qui donne notre coton. */
    private static void testCottonNode() {
        section("Nodes — le cotonnier");
        Path data = locateData();
        Path file = data == null ? null : data.resolve("haute_capitale_metiers").resolve("hcm").resolve("nodes").resolve("herboriste").resolve("coton.json");
        NodeType node = null;
        if (file != null && Files.isRegularFile(file)) {
            try {
                node = NodeType.CODEC.parse(JsonOps.INSTANCE, JsonParser.parseString(Files.readString(file, StandardCharsets.UTF_8))).result().orElse(null);
            } catch (IOException | RuntimeException e) {
                node = null;
            }
        }
        NodeType cotton = node;
        check("le node coton se relit : Herboriste, niveau 3, deux coups", cotton != null
                && cotton.profession() == Profession.HERBORISTE && cotton.level() == 3 && cotton.hits() == 2);
        check("plein = cotonnier, vide = cotonnier jeune, butin = coton 2 à 4", cotton != null
                && cotton.fullBlock().equals("haute_capitale_metiers:cotonnier")
                && cotton.emptyBlock().equals("haute_capitale_metiers:cotonnier_jeune")
                && cotton.loot().size() == 1 && cotton.loot().get(0).item().getPath().equals("coton"));
    }

    private static MealBuff decodeBuff(String json) {
        DataResult<MealBuff> result = MealBuff.CODEC.parse(JsonOps.INSTANCE, JsonParser.parseString(json));
        if (result.result().isEmpty()) {
            check("décodage du buff", false);
            System.out.println("      " + result.error().map(DataResult.Error::message).orElse("?"));
            return null;
        }
        return result.result().get();
    }

    private static NodeType decodeNode(String json) {
        DataResult<NodeType> result = NodeType.CODEC.parse(JsonOps.INSTANCE, JsonParser.parseString(json));
        if (result.result().isEmpty()) {
            check("décodage du node", false);
            System.out.println("      " + result.error().map(DataResult.Error::message).orElse("?"));
            return null;
        }
        return result.result().get();
    }

    private static String nodeError(String json) {
        return NodeType.CODEC.parse(JsonOps.INSTANCE, JsonParser.parseString(json))
                .error().map(DataResult.Error::message).orElse(null);
    }

    private static CraftRecipe decodeRecipe(String json) {
        DataResult<CraftRecipe> result = CraftRecipe.CODEC.parse(JsonOps.INSTANCE, JsonParser.parseString(json));
        if (result.result().isEmpty()) {
            check("décodage de la recette", false);
            System.out.println("      " + result.error().map(DataResult.Error::message).orElse("?"));
            return null;
        }
        return result.result().get();
    }

    private static String recipeError(String json) {
        return CraftRecipe.CODEC.parse(JsonOps.INSTANCE, JsonParser.parseString(json))
                .error().map(DataResult.Error::message).orElse(null);
    }

    private static boolean is16x16(Path png) {
        return isSquare(png, 16);
    }

    private static boolean isSquare(Path png, int side) {
        try {
            java.awt.image.BufferedImage image = javax.imageio.ImageIO.read(png.toFile());
            return image != null && image.getWidth() == side && image.getHeight() == side;
        } catch (IOException e) {
            return false;
        }
    }

    /** Une bande verticale de carrés de 16, accompagnée de son .mcmeta d'animation. */
    private static boolean isItemTexture(Path png) {
        try {
            java.awt.image.BufferedImage image = javax.imageio.ImageIO.read(png.toFile());
            if (image == null || image.getWidth() != 16 || image.getHeight() % 16 != 0) {
                return false;
            }
            return image.getHeight() == 16
                    || Files.isRegularFile(png.resolveSibling(png.getFileName() + ".mcmeta"));
        } catch (IOException e) {
            return false;
        }
    }

    private static boolean modelPointsAtTexture(Path model, String name) {
        JsonObject json = readJson(model);
        if (json == null || !json.has("textures")) {
            return false;
        }
        JsonObject textures = json.getAsJsonObject("textures");
        return textures.has("layer0")
                && textures.get("layer0").getAsString().equals("haute_capitale_metiers:item/" + name);
    }

    private static JsonObject readJson(Path file) {
        try {
            return JsonParser.parseString(Files.readString(file, StandardCharsets.UTF_8)).getAsJsonObject();
        } catch (IOException | RuntimeException e) {
            return null;
        }
    }

    /** Le dossier de données du mod, vu depuis le classpath ou, à défaut, depuis les sources. */
    private static Path locateData() {
        return locateResource("/data");
    }

    /** Une racine de ressources du mod, vue depuis le classpath ou, à défaut, depuis les sources. */
    private static Path locateResource(String name) {
        try {
            URL url = DonneesTest.class.getResource(name);
            if (url != null && "file".equals(url.getProtocol())) {
                return Path.of(url.toURI());
            }
        } catch (Exception e) {
            // On retombe sur les sources.
        }
        Path fallback = Path.of("src", "main", "resources", name.substring(1));
        return Files.isDirectory(fallback) ? fallback : null;
    }

    // ------------------------------------------------------------------

    private static DataResult<CreatureProfile> parse(String json) {
        return CreatureProfile.CODEC.parse(JsonOps.INSTANCE, JsonParser.parseString(json));
    }

    /** Le message d'erreur seul, tel que l'administrateur le lira, ou {@code null} si la fiche passe. */
    private static String errorMessage(String json) {
        return parse(json).error().map(DataResult.Error::message).orElse(null);
    }

    private static NpcRole decodeRole(String json) {
        DataResult<NpcRole> result = NpcRole.CODEC.parse(JsonOps.INSTANCE, JsonParser.parseString(json));
        if (result.result().isEmpty()) {
            check("décodage du rôle", false);
            System.out.println("      " + result.error().map(DataResult.Error::message).orElse("?"));
            return null;
        }
        return result.result().get();
    }

    private static String errorMessageRole(String json) {
        return NpcRole.CODEC.parse(JsonOps.INSTANCE, JsonParser.parseString(json))
                .error().map(DataResult.Error::message).orElse(null);
    }

    /** Fait tourner le validateur de rôle et dit s'il a écarté la fiche. */
    private static boolean rejects(String json) {
        return validateRole(json) == null;
    }

    /** Le premier problème signalé par le validateur de rôle, ou {@code null}. */
    private static String firstIssue(String json) {
        LoadReport report = validateRole(json);
        if (report == null || report.entries().isEmpty()) {
            return null;
        }
        return report.entries().get(0).message();
    }

    /** @return le rapport, ou {@code null} si la fiche a été écartée */
    private static LoadReport validateRole(String json) {
        NpcRole role = NpcRole.CODEC.parse(JsonOps.INSTANCE, JsonParser.parseString(json))
                .result().orElseThrow();
        LoadReport report = new LoadReport();
        Identifier id = Identifier.of("haute_capitale_metiers", "test");
        DataRegistry.Validator checks = new DataRegistry.Validator(id, id, report);
        HcmData.validateRole(role, checks);
        return checks.rejected() ? null : report;
    }

    private static CreatureProfile decode(String json) {
        DataResult<CreatureProfile> result = parse(json);
        if (result.result().isEmpty()) {
            check("décodage de la fiche", false);
            System.out.println("      " + result.error().map(Object::toString).orElse("?"));
            return null;
        }
        return result.result().get();
    }

    private static void section(String title) {
        System.out.println("--- " + title);
    }

    private static void check(String label, boolean condition) {
        if (condition) {
            passed++;
            System.out.println("  PASS  " + label);
        } else {
            failed++;
            System.out.println("  FAIL  " + label);
        }
    }
}
