package net.hautecapitale.rpg;

import net.hautecapitale.rpg.client.camera.AimReticle;
import net.hautecapitale.rpg.client.camera.CameraOverrideManager;
import net.hautecapitale.rpg.client.camera.CameraSettings;
import net.hautecapitale.rpg.client.camera.RpgCameraManager;
import net.hautecapitale.rpg.client.camera.ViewCycle;
import net.minecraft.client.option.Perspective;

/**
 * Harnais de vérification de la caméra RPG — un {@code main()} classique, comme
 * {@link NoyauTest}.
 *
 * <p>Ce qui est vérifié ici est précisément ce qui ne se voit pas en jeu tant
 * qu'on n'a pas le nez dessus : le comptage des suspensions, l'indépendance du
 * lissage vis-à-vis du nombre d'images par seconde, et les signes de la
 * projection du réticule. Un réticule projeté à l'envers ou une caméra qui bouge
 * deux fois plus vite à 144 ips n'émettent aucune erreur.
 */
public final class CameraTest {

    private static int reussis = 0;
    private static int echecs = 0;

    public static void main(String[] args) {
        suspension();
        lissage();
        projection();
        bornage();
        prise();
        cycleF5();

        System.out.println();
        System.out.printf("  %d vérifications réussies, %d échecs%n", reussis, echecs);
        if (echecs > 0) {
            System.exit(1);
        }
    }

    // MARK: cycle F5

    private static void cycleF5() {
        section("Cycle F5");

        java.util.function.Function<ViewCycle.Etape,
                ViewCycle.Etape> f5 =
                e -> ViewCycle.suivante(e, 2, true);

        // L'ordre demandé : 1re → 3e → face → RPG 1 → RPG 2 → 1re.
        var e = new ViewCycle.Etape(
                ViewCycle.Position.VANILLA_FIRST, 0);
        StringBuilder parcours = new StringBuilder(e.position().name());
        for (int i = 0; i < 5; i++) {
            e = f5.apply(e);
            parcours.append(" → ").append(e.position().name());
            if (e.position() == ViewCycle.Position.RPG) {
                parcours.append('#').append(e.indexStyle());
            }
        }
        System.out.println("      " + parcours);
        check("cinq F5 font le tour complet et reviennent à la 1re personne",
                parcours.toString().equals("VANILLA_FIRST → VANILLA_BACK → VANILLA_FRONT → RPG#0 → RPG#1 → VANILLA_FIRST"));

        var apresFace = ViewCycle.suivante(
                new ViewCycle.Etape(
                        ViewCycle.Position.VANILLA_FRONT, 1), 2, false);
        check("module coupé : la vue de face revient à la 1re personne, sans passer par le RPG",
                apresFace.position() == ViewCycle.Position.VANILLA_FIRST);

        var sansStyle = ViewCycle.suivante(
                new ViewCycle.Etape(
                        ViewCycle.Position.VANILLA_FRONT, 0), 0, true);
        check("aucun style configuré : le cycle se referme sur les vues vanilla",
                sansStyle.position() == ViewCycle.Position.VANILLA_FIRST);

        var unSeul = ViewCycle.suivante(
                new ViewCycle.Etape(
                        ViewCycle.Position.RPG, 0), 1, true);
        check("un seul style : après lui, retour à la 1re personne",
                unSeul.position() == ViewCycle.Position.VANILLA_FIRST);

        // Resynchronisation : la position se lit sur la perspective reelle.
        check("en 1re personne, le drapeau RPG ne compte pas",
                ViewCycle.courante(
                        Perspective.FIRST_PERSON, true)
                        == ViewCycle.Position.VANILLA_FIRST);
        check("en 3e personne avec le drapeau, on est sur une position RPG",
                ViewCycle.courante(
                        Perspective.THIRD_PERSON_BACK, true)
                        == ViewCycle.Position.RPG);
        check("en 3e personne sans le drapeau, c'est la vue vanilla",
                ViewCycle.courante(
                        Perspective.THIRD_PERSON_BACK, false)
                        == ViewCycle.Position.VANILLA_BACK);
        check("la vue de face est reconnue",
                ViewCycle.courante(
                        Perspective.THIRD_PERSON_FRONT, false)
                        == ViewCycle.Position.VANILLA_FRONT);

        check("une position RPG demande la 3e personne (dos)",
                ViewCycle.perspectiveDe(
                        ViewCycle.Position.RPG)
                        == Perspective.THIRD_PERSON_BACK);
    }

    // MARK: prise de camera par un dialogue

    private static void prise() {
        section("Prise de caméra par un dialogue");

        net.hautecapitale.rpg.client.camera.CameraFocus.clear();
        check("au repos, aucune prise", !net.hautecapitale.rpg.client.camera.CameraFocus.estEngage());
        net.hautecapitale.rpg.client.camera.CameraFocus.request((w, p, c, t) -> null, 0);
        check("une demande engage la prise", net.hautecapitale.rpg.client.camera.CameraFocus.estDemande()
                && net.hautecapitale.rpg.client.camera.CameraFocus.estEngage());
        net.hautecapitale.rpg.client.camera.CameraFocus.release(0);
        check("rendue, la prise n'est plus demandée", !net.hautecapitale.rpg.client.camera.CameraFocus.estDemande());
        check("mais reste engagée jusqu'a la prochaine image", net.hautecapitale.rpg.client.camera.CameraFocus.estEngage());
        net.hautecapitale.rpg.client.camera.CameraFocus.clear();
        check("le retrait immédiat libère tout", !net.hautecapitale.rpg.client.camera.CameraFocus.estEngage());

        double s0 = net.hautecapitale.rpg.client.camera.CameraFocus.smoothstep(0.0);
        double s5 = net.hautecapitale.rpg.client.camera.CameraFocus.smoothstep(0.5);
        double s1 = net.hautecapitale.rpg.client.camera.CameraFocus.smoothstep(1.0);
        check("smoothstep : 0 → 0, 0.5 → 0.5, 1 → 1", s0 == 0.0 && Math.abs(s5 - 0.5) < 1e-9 && s1 == 1.0);
        boolean monotone = true;
        double prev = 0.0;
        for (int i = 1; i <= 100; i++) {
            double v = net.hautecapitale.rpg.client.camera.CameraFocus.smoothstep(i / 100.0);
            monotone &= v >= prev;
            prev = v;
        }
        check("smoothstep monotone", monotone);
        check("smoothstep borné hors [0,1]", net.hautecapitale.rpg.client.camera.CameraFocus.smoothstep(-2) == 0.0
                && net.hautecapitale.rpg.client.camera.CameraFocus.smoothstep(3) == 1.0);
    }

    // MARK: suspension pendant les cinématiques

    private static void suspension() {
        section("Suspension pendant les cinématiques");

        CameraOverrideManager.clear("preparation du test");
        check("au repos, aucune suspension", !CameraOverrideManager.isCinematic());
        check("au repos, l'état est GAMEPLAY_RPG ou DISABLED",
                RpgCameraManager.etat() != null);

        CameraOverrideManager.enterCinematicMode("bossesrise");
        check("une entrée suspend la caméra", CameraOverrideManager.isCinematic());
        check("profondeur 1", CameraOverrideManager.profondeur() == 1);

        CameraOverrideManager.enterCinematicMode("scenario");
        check("deux systèmes distincts, profondeur 2", CameraOverrideManager.profondeur() == 2);

        CameraOverrideManager.exitCinematicMode("bossesrise");
        check("une sortie sur deux ne rend pas la caméra", CameraOverrideManager.isCinematic());

        CameraOverrideManager.exitCinematicMode("scenario");
        check("la dernière sortie rend la caméra", !CameraOverrideManager.isCinematic());

        // Le cas qui a motivé le compteur : une cinématique de boss enchaîne
        // plusieurs handlers sous la même raison. Un booléen produirait ici un
        // clignotement entrée/sortie visible à l'écran.
        CameraOverrideManager.enterCinematicMode("bossesrise");
        CameraOverrideManager.enterCinematicMode("bossesrise");
        CameraOverrideManager.exitCinematicMode("bossesrise");
        check("même raison ouverte deux fois : une sortie ne suffit pas",
                CameraOverrideManager.isCinematic());
        CameraOverrideManager.exitCinematicMode("bossesrise");
        check("la seconde sortie libère", !CameraOverrideManager.isCinematic());

        CameraOverrideManager.exitCinematicMode("jamais ouverte");
        check("fermer une raison inconnue ne fait rien", !CameraOverrideManager.isCinematic());

        CameraOverrideManager.enterCinematicMode("a");
        CameraOverrideManager.enterCinematicMode("b");
        CameraOverrideManager.clear("test");
        check("clear() libère tout d'un coup", !CameraOverrideManager.isCinematic());
        check("profondeur remise à zéro", CameraOverrideManager.profondeur() == 0);

        CameraOverrideManager.enterCinematicMode(null);
        check("une raison nulle ne casse rien", CameraOverrideManager.isCinematic());
        CameraOverrideManager.exitCinematicMode(null);
        check("et se referme normalement", !CameraOverrideManager.isCinematic());
    }

    // MARK: lissage

    private static void lissage() {
        section("Lissage");

        check("constante de temps nulle : convergence immédiate",
                RpgCameraManager.coefficientLissage(0.016, 0.0) == 1.0);
        check("delta nul : aucun mouvement demandé",
                RpgCameraManager.coefficientLissage(0.0, 0.13) == 1.0);

        double alpha = RpgCameraManager.coefficientLissage(0.13, 0.13);
        check("dt = tau donne 1 - 1/e", proche(alpha, 1.0 - 1.0 / Math.E, 1e-9));

        double a30 = RpgCameraManager.coefficientLissage(1.0 / 30.0, 0.13);
        double a144 = RpgCameraManager.coefficientLissage(1.0 / 144.0, 0.13);
        check("plus d'images par seconde, plus petit pas", a144 < a30);
        check("les deux coefficients restent dans ]0,1[",
                a30 > 0 && a30 < 1 && a144 > 0 && a144 < 1);

        // La propriete qui compte vraiment : la camera doit mettre le meme temps a
        // se remettre en place quel que soit le nombre d'images par seconde.
        double resteUnPas = 1.0 - RpgCameraManager.coefficientLissage(0.1, 0.13);
        double resteDixPas = 1.0;
        for (int i = 0; i < 10; i++) {
            resteDixPas *= 1.0 - RpgCameraManager.coefficientLissage(0.01, 0.13);
        }
        check("dix petits pas valent un grand pas (indépendance des ips)",
                proche(resteUnPas, resteDixPas, 1e-9));
    }

    // MARK: projection du réticule

    private static void projection() {
        section("Projection du réticule");

        final int largeur = 1920;
        final int hauteur = 1080;
        final double fov = 90.0;
        // focale = (hauteur / 2) / tan(45°) = 540
        final double focale = 540.0;

        double[] centre = AimReticle.projeter(0.0, 0.0, 10.0, largeur, hauteur, fov);
        check("un point droit devant tombe au centre",
                proche(centre[0], largeur / 2.0, 1e-9) && proche(centre[1], hauteur / 2.0, 1e-9));

        double[] droite = AimReticle.projeter(1.0, 0.0, 1.0, largeur, hauteur, fov);
        check("un point à droite se projette à droite", droite[0] > largeur / 2.0);
        check("focale attendue à 90° de champ", proche(droite[0], largeur / 2.0 + focale, 1e-6));

        double[] haut = AimReticle.projeter(0.0, 1.0, 1.0, largeur, hauteur, fov);
        // L'axe des ordonnees de l'interface descend : un point au-dessus doit avoir
        // un y plus petit. C'est le signe qu'on inverse sans s'en apercevoir.
        check("un point au-dessus se projette plus haut", haut[1] < hauteur / 2.0);

        double[] loin = AimReticle.projeter(2.0, 0.0, 2.0, largeur, hauteur, fov);
        check("deux fois plus loin, deux fois plus décalé : même point à l'écran",
                proche(loin[0], droite[0], 1e-9));

        double[] etroit = AimReticle.projeter(1.0, 0.0, 10.0, largeur, hauteur, 30.0);
        double[] large = AimReticle.projeter(1.0, 0.0, 10.0, largeur, hauteur, 110.0);
        check("un champ de vision étroit écarte davantage le réticule",
                etroit[0] > large[0]);

        // Cas reel : epaule a 0,55 bloc, cible a 10 blocs, champ de vision 70.
        double[] reel = AimReticle.projeter(-0.55, 0.0, 10.0, largeur, hauteur, 70.0);
        double ecart = largeur / 2.0 - reel[0];
        check("décalage plausible à 10 blocs (20 à 60 px)", ecart > 20 && ecart < 60);
    }

    // MARK: bornage de la configuration

    private static void bornage() {
        section("Bornage de la configuration");

        CameraSettings s = CameraSettings.get();

        s.distance_min = 1.5;
        s.distance_max = 9.0;
        s.styles.get(0).distance = 400.0;
        s.borner();
        check("une distance absurde est ramenée au maximum", s.styles.get(0).distance == 9.0);

        s.styles.get(0).distance = -3.0;
        s.borner();
        check("une distance négative est ramenée au minimum", s.styles.get(0).distance == 1.5);

        s.styles = null;
        s.borner();
        check("sans style, les deux styles par défaut reviennent",
                s.styles != null && s.styles.size() == 2 && s.styles.get(0).nom.equals("epaule")
                        && s.styles.get(1).nom.equals("mmo"));

        s.styles.get(1).nom = "  ";
        s.borner();
        check("un style sans nom en reçoit un", !s.styles.get(1).nom.isBlank());

        s.collision_marge = -1.0;
        s.borner();
        check("une marge de collision négative est refusée", s.collision_marge == 0.0);

        s.distance_max = 0.5;
        s.distance_min = 4.0;
        s.borner();
        check("un maximum sous le minimum est corrigé", s.distance_max >= s.distance_min);

        s.lissage_ms = -50;
        s.borner();
        check("un lissage négatif devient instantané", s.lissage_ms == 0);

        s.reticule_portee = 5000.0;
        s.borner();
        check("la portée du réticule reste raisonnable", s.reticule_portee <= 128.0);
    }

    // MARK: sortie

    private static boolean proche(double a, double b, double tolerance) {
        return Math.abs(a - b) <= tolerance;
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

    private CameraTest() {
    }
}
