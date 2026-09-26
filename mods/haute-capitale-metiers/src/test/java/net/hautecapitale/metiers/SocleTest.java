package net.hautecapitale.metiers;

import com.mojang.serialization.JsonOps;
import net.hautecapitale.metiers.config.MetiersConfig;
import net.hautecapitale.metiers.profession.Profession;
import net.hautecapitale.metiers.profession.ProfessionProgress;
import net.hautecapitale.metiers.profession.ProfessionsState;
import net.hautecapitale.metiers.profession.Rank;
import net.hautecapitale.metiers.profession.XpCurve;

import java.util.Locale;

/**
 * Harnais de test de la logique pure du socle.
 *
 * <p>Ne dépend d'aucun serveur ni d'aucun joueur : tout ce qui est testé ici est
 * calculatoire. Les scénarios qui exigent un monde — persistance au
 * redémarrage, copie à la mort, synchronisation client — se vérifient en jeu.
 *
 * <p>Lancé par {@code gradle runSocleTest}.
 */
public final class SocleTest {

    private static int passed = 0;
    private static int failed = 0;

    public static void main(String[] args) {
        System.out.println("=== Haute Capitale — Métiers : tests du socle ===\n");

        testProfessions();
        testRanks();
        testCurve();
        testLevelUp();
        testMultiLevelUp();
        testLevelCap();
        testInvalidXp();
        testStateIndependence();
        testStateImmutability();
        testCodecRoundTrip();
        testNbtRoundTrip();
        testCorruptedData();

        System.out.printf(Locale.ROOT, "%n=== %d réussis, %d échoués ===%n", passed, failed);
        if (failed > 0) {
            System.exit(1);
        }
    }

    // ------------------------------------------------------------------

    private static void testProfessions() {
        section("Les onze métiers");
        check("onze métiers déclarés", Profession.values().length == 11);
        check("aucun métier Pêcheur", Profession.byId("pecheur") == null);
        check("aucun métier Enchanteur", Profession.byId("enchanteur") == null);
        check("recherche par identifiant", Profession.byId("joaillier") == Profession.JOAILLIER);
        check("identifiant inconnu renvoie null", Profession.byId("bidule") == null);
        int recolte = 0;
        int artisanat = 0;
        for (Profession p : Profession.values()) {
            if (p.getKind() == Profession.Kind.RECOLTE) {
                recolte++;
            } else {
                artisanat++;
            }
        }
        check("quatre métiers de récolte", recolte == 4);
        check("sept métiers d'artisanat", artisanat == 7);
    }

    private static void testRanks() {
        section("Rangs dérivés du niveau");
        check("niveau 1 = Apprenti", Rank.fromLevel(1) == Rank.APPRENTI);
        check("niveau 10 = Apprenti", Rank.fromLevel(10) == Rank.APPRENTI);
        check("niveau 11 = Compagnon", Rank.fromLevel(11) == Rank.COMPAGNON);
        check("niveau 20 = Compagnon", Rank.fromLevel(20) == Rank.COMPAGNON);
        check("niveau 21 = Expert", Rank.fromLevel(21) == Rank.EXPERT);
        check("niveau 30 = Expert", Rank.fromLevel(30) == Rank.EXPERT);
        check("niveau 31 = Maître", Rank.fromLevel(31) == Rank.MAITRE);
        check("niveau 40 = Maître", Rank.fromLevel(40) == Rank.MAITRE);
        check("niveau 41 = Grand Maître", Rank.fromLevel(41) == Rank.GRAND_MAITRE);
        check("niveau 50 = Grand Maître", Rank.fromLevel(50) == Rank.GRAND_MAITRE);
        check("niveau 0 ramené à Apprenti", Rank.fromLevel(0) == Rank.APPRENTI);
        check("niveau 999 ramené à Grand Maître", Rank.fromLevel(999) == Rank.GRAND_MAITRE);
    }

    private static void testCurve() {
        section("Courbe d'expérience");
        double l1 = XpCurve.xpToNextLevel(1);
        double l10 = XpCurve.xpToNextLevel(10);
        double l49 = XpCurve.xpToNextLevel(49);
        check("niveau 1 → 2 coûte 75 XP", l1 == 75.0D);
        check("courbe strictement croissante", l1 < l10 && l10 < l49);
        check("niveau 50 est un mur", Double.isInfinite(XpCurve.xpToNextLevel(50)));
        System.out.printf(Locale.ROOT, "      1→2 : %.0f   10→11 : %.0f   49→50 : %.0f%n", l1, l10, l49);

        double cumul = 0.0D;
        for (int level = 1; level < 50; level++) {
            cumul += XpCurve.xpToNextLevel(level);
        }
        System.out.printf(Locale.ROOT, "      XP totale pour atteindre 50 : %.0f%n", cumul);
        check("XP totale 1→50 dans une fourchette jouable", cumul > 80_000 && cumul < 200_000);
    }

    private static void testLevelUp() {
        section("Passage de niveau simple");
        int max = MetiersConfig.get().niveau_max;

        // Les seuils sont lus depuis la courbe : le test reste valide même si
        // tu règles la progression dans la configuration.
        double cost1 = XpCurve.xpToNextLevel(1);

        XpCurve.Gain none = XpCurve.applyXp(1, 0.0D, cost1 - 25.0D, max);
        check("un gain insuffisant ne fait pas monter", none.level() == 1 && none.levelsGained() == 0);
        check("l'XP est bien conservée", none.xp() == cost1 - 25.0D);

        XpCurve.Gain exact = XpCurve.applyXp(1, 0.0D, cost1, max);
        check("le seuil exact fait passer au niveau 2", exact.level() == 2 && exact.levelsGained() == 1);
        check("aucun reste après un passage exact", exact.xp() == 0.0D);

        XpCurve.Gain over = XpCurve.applyXp(1, 0.0D, cost1 + 30.0D, max);
        check("le surplus est reporté sur le palier suivant",
                over.level() == 2 && over.xp() == 30.0D);
    }

    private static void testMultiLevelUp() {
        section("Passages de niveau multiples");
        int max = MetiersConfig.get().niveau_max;
        XpCurve.Gain gain = XpCurve.applyXp(1, 0.0D, 10_000.0D, max);
        check("un gain massif franchit plusieurs niveaux", gain.levelsGained() > 1);
        check("le niveau reste sous le plafond", gain.level() <= max);
        System.out.printf(Locale.ROOT, "      10 000 XP depuis le niveau 1 → niveau %d (+%d), reste %.0f XP%n",
                gain.level(), gain.levelsGained(), gain.xp());

        // Cohérence : additionner l'XP en une fois ou en plusieurs doit donner le même niveau.
        int level = 1;
        double xp = 0.0D;
        for (int i = 0; i < 100; i++) {
            XpCurve.Gain step = XpCurve.applyXp(level, xp, 100.0D, max);
            level = step.level();
            xp = step.xp();
        }
        XpCurve.Gain bulk = XpCurve.applyXp(1, 0.0D, 10_000.0D, max);
        check("100 × 100 XP équivaut à 10 000 XP d'un coup", level == bulk.level());
    }

    private static void testLevelCap() {
        section("Plafond au niveau maximum");
        int max = MetiersConfig.get().niveau_max;

        XpCurve.Gain atCap = XpCurve.applyXp(max, 0.0D, 1_000_000.0D, max);
        check("l'XP au niveau max ne fait rien gagner", atCap.levelsGained() == 0);
        check("le niveau reste au plafond", atCap.level() == max);
        check("l'XP excédentaire est écartée", atCap.xp() == 0.0D);

        XpCurve.Gain huge = XpCurve.applyXp(1, 0.0D, 1e12D, max);
        check("un gain démesuré s'arrête au plafond", huge.level() == max);
        check("et ne laisse aucune XP résiduelle", huge.xp() == 0.0D);
    }

    private static void testInvalidXp() {
        section("Valeurs invalides");
        int max = MetiersConfig.get().niveau_max;
        check("XP négative ignorée", XpCurve.applyXp(5, 10.0D, -100.0D, max).level() == 5);
        check("XP nulle ignorée", XpCurve.applyXp(5, 10.0D, 0.0D, max).levelsGained() == 0);
        check("NaN ignoré", XpCurve.applyXp(5, 10.0D, Double.NaN, max).level() == 5);
        check("infini ignoré", XpCurve.applyXp(5, 10.0D, Double.POSITIVE_INFINITY, max).level() == 5);
    }

    private static void testStateIndependence() {
        section("Indépendance des métiers");
        ProfessionsState state = ProfessionsState.empty()
                .with(Profession.MINEUR, new ProfessionProgress(24, 0.0D))
                .with(Profession.HERBORISTE, new ProfessionProgress(8, 0.0D))
                .with(Profession.TRAVAILLEUR_DU_CUIR, new ProfessionProgress(30, 0.0D))
                .with(Profession.FORGERON, new ProfessionProgress(3, 0.0D));

        check("quatre métiers appris", state.count() == 4);
        check("Mineur au niveau 24", state.get(Profession.MINEUR).level() == 24);
        check("Herboriste au niveau 8", state.get(Profession.HERBORISTE).level() == 8);
        check("Travailleur du cuir au niveau 30", state.get(Profession.TRAVAILLEUR_DU_CUIR).level() == 30);
        check("Forgeron au niveau 3", state.get(Profession.FORGERON).level() == 3);
        check("Joaillier non appris", state.get(Profession.JOAILLIER) == null);
        check("un métier non appris ne compte pas", !state.has(Profession.CUISINIER));

        // Monter un métier n'en touche aucun autre.
        ProfessionsState after = state.with(Profession.MINEUR, new ProfessionProgress(25, 0.0D));
        check("monter Mineur ne change pas Forgeron",
                after.get(Profession.FORGERON).level() == 3);
        check("monter Mineur ne change pas Herboriste",
                after.get(Profession.HERBORISTE).level() == 8);

        check("les rangs suivent chaque métier séparément",
                after.get(Profession.MINEUR).rank() == Rank.EXPERT
                        && after.get(Profession.HERBORISTE).rank() == Rank.APPRENTI
                        && after.get(Profession.TRAVAILLEUR_DU_CUIR).rank() == Rank.EXPERT);
    }

    private static void testStateImmutability() {
        section("Immuabilité de l'état");
        ProfessionsState original = ProfessionsState.empty().with(Profession.MINEUR, new ProfessionProgress(5, 0.0D));
        ProfessionsState modified = original.with(Profession.CHASSEUR, new ProfessionProgress(1, 0.0D));
        check("l'original n'est pas modifié", original.count() == 1);
        check("la copie contient le nouveau métier", modified.count() == 2);

        ProfessionsState removed = modified.without(Profession.MINEUR);
        check("le retrait produit un nouvel état", removed.count() == 1 && modified.count() == 2);
        check("retirer un métier absent ne change rien",
                removed.without(Profession.JOAILLIER).count() == 1);

        boolean immutable;
        try {
            original.entries().put(Profession.FORGERON, ProfessionProgress.INITIAL);
            immutable = false;
        } catch (UnsupportedOperationException e) {
            immutable = true;
        }
        check("la carte exposée est non modifiable", immutable);
    }

    private static void testCodecRoundTrip() {
        section("Sérialisation");
        ProfessionsState original = ProfessionsState.empty()
                .with(Profession.MINEUR, new ProfessionProgress(24, 137.5D))
                .with(Profession.JOAILLIER, new ProfessionProgress(15, 0.0D))
                .with(Profession.INGENIEUR, new ProfessionProgress(50, 0.0D));

        var encoded = ProfessionsState.CODEC.encodeStart(JsonOps.INSTANCE, original);
        check("encodage réussi", encoded.result().isPresent());
        if (encoded.result().isEmpty()) {
            System.out.println("      " + encoded.error().map(Object::toString).orElse("?"));
            return;
        }
        System.out.println("      " + encoded.result().get());

        var decoded = ProfessionsState.CODEC.parse(JsonOps.INSTANCE, encoded.result().get());
        check("décodage réussi", decoded.result().isPresent());
        if (decoded.result().isEmpty()) {
            return;
        }
        ProfessionsState back = decoded.result().get();
        check("même nombre de métiers", back.count() == original.count());
        check("niveau conservé", back.get(Profession.MINEUR).level() == 24);
        check("XP conservée", back.get(Profession.MINEUR).xp() == 137.5D);
        check("métier au plafond conservé", back.get(Profession.INGENIEUR).level() == 50);
        check("métier non appris toujours absent", back.get(Profession.CHASSEUR) == null);
    }

    /**
     * Le jeu ne sauvegarde pas en JSON mais en NBT. Un codec peut très bien
     * fonctionner avec {@code JsonOps} et échouer en NBT — typiquement sur une
     * carte dont les clés ne sont pas des chaînes. C'est donc ce test-là qui
     * atteste réellement que la progression survivra à un redémarrage.
     */
    private static void testNbtRoundTrip() {
        section("Sérialisation NBT — format réel de sauvegarde");
        ProfessionsState original = ProfessionsState.empty()
                .with(Profession.MINEUR, new ProfessionProgress(24, 137.5D))
                .with(Profession.CHASSEUR, new ProfessionProgress(17, 42.0D))
                .with(Profession.INGENIEUR, new ProfessionProgress(50, 0.0D));

        var encoded = ProfessionsState.CODEC.encodeStart(net.minecraft.nbt.NbtOps.INSTANCE, original);
        check("encodage NBT réussi", encoded.result().isPresent());
        if (encoded.result().isEmpty()) {
            System.out.println("      " + encoded.error().map(Object::toString).orElse("?"));
            return;
        }
        System.out.println("      " + encoded.result().get());

        var decoded = ProfessionsState.CODEC.parse(net.minecraft.nbt.NbtOps.INSTANCE, encoded.result().get());
        check("décodage NBT réussi", decoded.result().isPresent());
        if (decoded.result().isEmpty()) {
            System.out.println("      " + decoded.error().map(Object::toString).orElse("?"));
            return;
        }
        ProfessionsState back = decoded.result().get();
        check("trois métiers relus", back.count() == 3);
        check("niveau conservé en NBT", back.get(Profession.MINEUR).level() == 24);
        check("XP décimale conservée en NBT", back.get(Profession.MINEUR).xp() == 137.5D);
        check("second métier conservé", back.get(Profession.CHASSEUR).level() == 17);
        check("métier au plafond conservé", back.get(Profession.INGENIEUR).level() == 50);
        check("métier non appris toujours absent", back.get(Profession.FORGERON) == null);
    }

    private static void testCorruptedData() {
        section("Robustesse aux données corrompues");
        check("niveau négatif ramené à 1", new ProfessionProgress(-5, 0.0D).level() == 1);
        check("niveau zéro ramené à 1", new ProfessionProgress(0, 0.0D).level() == 1);
        check("XP négative ramenée à 0", new ProfessionProgress(5, -100.0D).xp() == 0.0D);
        check("XP NaN ramenée à 0", new ProfessionProgress(5, Double.NaN).xp() == 0.0D);
        check("XP infinie ramenée à 0", new ProfessionProgress(5, Double.POSITIVE_INFINITY).xp() == 0.0D);

        var parsed = ProfessionsState.CODEC.parse(JsonOps.INSTANCE,
                com.google.gson.JsonParser.parseString("{\"metiers\":{}}"));
        check("un état vide se relit sans erreur", parsed.result().isPresent());
    }

    // ------------------------------------------------------------------

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
