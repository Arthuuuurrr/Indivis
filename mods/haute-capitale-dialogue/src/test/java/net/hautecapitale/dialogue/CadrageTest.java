package net.hautecapitale.dialogue;

import net.hautecapitale.dialogue.client.DialogueCameraController;
import net.hautecapitale.dialogue.session.CameraProfile;
import net.hautecapitale.rpg.client.camera.CameraFocus;
import net.minecraft.util.math.Vec3d;

/**
 * Harnais du cadrage — la fonction pure, avec une collision simulée.
 *
 * <p>Le cadrage est ce qui se voit le plus et se teste le moins bien en jeu :
 * un côté choisi à l'envers, un joueur qui sort du cadre quand le personnage
 * s'éloigne, ou une cascade de repli qui laisse la caméra dans un mur ne
 * lèvent aucune erreur. Ici, on pose le personnage à l'origine, le joueur au
 * sud à différentes distances, et on simule des murs.
 *
 * <p>Convention : face au sud (+z), la droite du joueur est −x. Les angles
 * d'écran sont signés, positif = à gauche du centre de l'image.
 */
public final class CadrageTest {

    private static int reussis = 0;
    private static int echecs = 0;

    private static final CameraProfile PROFIL = CameraProfile.DEFAUT;
    /** Visage du personnage, au-dessus de ses pieds en (0.5, 200, 0.5). */
    private static final Vec3d CIBLE = new Vec3d(0.5, 201.6, 0.5);
    /** Yeux du joueur, à 3 blocs au sud. */
    private static final Vec3d OEIL = new Vec3d(0.5, 201.62, -2.5);
    private static final Vec3d REGARD = new Vec3d(0.0, 0.0, 1.0);
    private static final DialogueCameraController.Collision LIBRE = (ancre, offset) -> 1.0;

    public static void main(String[] args) {
        espaceLibre();
        distances();
        coteBloque();
        toutBloque();
        hysteresis();
        hauteurs();

        System.out.println();
        System.out.printf("  %d vérifications réussies, %d échecs%n", reussis, echecs);
        if (echecs > 0) {
            System.exit(1);
        }
    }

    private static void espaceLibre() {
        section("Espace libre, distance normale (3 blocs)");
        // A 3 blocs : t = (3 - 1.5) / 3.5 = 0.4286 → recul 1.607, decalage 1.114, visee 8.29°.
        double recul = PROFIL.recul(3.0);
        double decalage = PROFIL.decalage(3.0);
        DialogueCameraController.Cadrage c = DialogueCameraController.calculer(
                CIBLE, OEIL, REGARD, 1.95, PROFIL, true, -1, LIBRE);
        CameraFocus.Pose p = c.pose();
        check("premier candidat retenu, pas de repli", c.candidat() == 0 && !c.repli());
        check("recul et décalage interpolés à 43 % (1,607 et 1,114)",
                proche(recul, 1.6071, 0.001) && proche(decalage, 1.1143, 0.001));
        check("la caméra est derrière le joueur : z = yeux − recul", proche(p.pos().z, OEIL.z - recul, 0.01));
        check("épaule droite : x = yeux − décalage (−x est la droite face au sud)", proche(p.pos().x, OEIL.x - decalage, 0.01));
        check("élévation au-dessus des yeux", proche(p.pos().y, OEIL.y + PROFIL.elevation(), 0.001));
        check("la longueur est celle de l'offset entier",
                proche(c.longueur(), Math.sqrt(recul * recul + decalage * decalage + 0.15 * 0.15), 0.01));
        double angleNpc = angleHorizontal(p, CIBLE);
        double angleJoueur = angleHorizontal(p, OEIL);
        check("le personnage est à droite du centre, de l'angle de visée (≈ −8,3°)", proche(angleNpc, -PROFIL.visee(3.0), 0.3));
        check("le joueur est à gauche, dans le cadre (≈ 13°)", angleJoueur > 8.0 && angleJoueur < 20.0);
        check("le tangage vise légèrement vers le bas", p.pitch() > 0.0 && p.pitch() < 10.0);

        DialogueCameraController.Cadrage g = DialogueCameraController.calculer(
                CIBLE, OEIL, REGARD, 1.95, PROFIL, false, -1, LIBRE);
        check("épaule gauche : x = yeux + décalage", proche(g.pose().pos().x, OEIL.x + decalage, 0.01));
        check("épaule gauche : personnage à gauche du centre, joueur à droite",
                proche(angleHorizontal(g.pose(), CIBLE), PROFIL.visee(3.0), 0.3)
                        && angleHorizontal(g.pose(), OEIL) < -8.0);
    }

    private static void distances() {
        section("Distances : collé, éloigné, très éloigné");
        Vec3d colle = new Vec3d(0.5, 201.62, -0.5);
        DialogueCameraController.Cadrage c = DialogueCameraController.calculer(
                CIBLE, colle, REGARD, 1.95, PROFIL, true, -1, LIBRE);
        check("collé (1 bloc) : recul 1,5 et décalage 1,35 — la caméra s'ouvre",
                proche(c.pose().pos().z, colle.z - 1.5, 0.01) && proche(c.pose().pos().x, colle.x - 1.35, 0.01));
        check("collé : personnage à droite du centre (−10°)", proche(angleHorizontal(c.pose(), CIBLE), -10.0, 0.3));
        double joueurColle = angleHorizontal(c.pose(), colle);
        check("collé : le joueur reste à gauche du centre, sans le masquer", joueurColle > 0.0 && joueurColle < 45.0);

        Vec3d loin = new Vec3d(0.5, 201.62, -5.5);
        DialogueCameraController.Cadrage l = DialogueCameraController.calculer(
                CIBLE, loin, REGARD, 1.95, PROFIL, true, -1, LIBRE);
        check("éloigné (6 blocs) : recul 1,75 et décalage 0,8 — bornes lointaines",
                proche(l.pose().pos().z, loin.z - 1.75, 0.01) && proche(l.pose().pos().x, loin.x - 0.8, 0.01));
        double joueurLoin = angleHorizontal(l.pose(), loin);
        check("éloigné : le joueur reste dans le cadre, à gauche (≈ 13°)", joueurLoin > 5.0 && joueurLoin < 45.0);
        check("éloigné : personnage à droite du centre (−6°)", proche(angleHorizontal(l.pose(), CIBLE), -6.0, 0.3));

        Vec3d tresLoin = new Vec3d(0.5, 201.62, -11.5);
        DialogueCameraController.Cadrage t = DialogueCameraController.calculer(
                CIBLE, tresLoin, REGARD, 1.95, PROFIL, true, -1, LIBRE);
        check("très éloigné (12 blocs) : le recul ne croît plus, la caméra reste sur le joueur",
                proche(t.pose().pos().z, tresLoin.z - 1.75, 0.01) && t.pose().pos().distanceTo(tresLoin) < 2.0);
    }

    private static void coteBloque() {
        section("Côté droit bloqué");
        // Un mur en -x, du cote de l'epaule droite : tout deplacement vers x < 0 est coupe a 30 %.
        DialogueCameraController.Collision mur = (ancre, offset) ->
                ancre.x + offset.x < 0.0 ? 0.3 : 1.0;
        DialogueCameraController.Cadrage c = DialogueCameraController.calculer(
                CIBLE, OEIL, REGARD, 1.95, PROFIL, true, -1, mur);
        check("l'autre épaule est choisie (candidat 1)", c.candidat() == 1 && !c.repli());
        check("la caméra est passée à gauche (+x)", c.pose().pos().x > OEIL.x);
        check("pleine longueur conservée", proche(c.longueur(), Math.sqrt(1.6071 * 1.6071 + 1.1143 * 1.1143 + 0.0225), 0.01));
        check("la visée s'inverse avec l'épaule : personnage à gauche du centre, joueur à droite",
                proche(angleHorizontal(c.pose(), CIBLE), PROFIL.visee(3.0), 0.3) && angleHorizontal(c.pose(), OEIL) < 0.0);
    }

    private static void toutBloque() {
        section("Tout bloqué");
        DialogueCameraController.Cadrage c = DialogueCameraController.calculer(
                CIBLE, OEIL, REGARD, 1.95, PROFIL, true, -1, (ancre, offset) -> 0.2);
        check("repli aux yeux du joueur", c.repli() && c.pose().pos().equals(OEIL));
        check("candidat -1 en repli", c.candidat() == -1);
        check("en repli, la caméra vise droit le personnage", angleVers(c.pose(), CIBLE) < 0.5);

        // Partiellement bloque : 75 % de l'offset (≈ 1,47 bloc) → acceptable, on garde
        // l'epaule mais plus pres.
        DialogueCameraController.Cadrage d = DialogueCameraController.calculer(
                CIBLE, OEIL, REGARD, 1.95, PROFIL, true, -1, (ancre, offset) -> 0.75);
        check("bloqué à 75 % : raccourci, pas de repli", !d.repli() && d.candidat() == 0
                && proche(d.longueur(), 0.75 * Math.sqrt(1.6071 * 1.6071 + 1.1143 * 1.1143 + 0.0225), 0.02));
        check("la position est aux trois quarts du chemin", proche(d.pose().pos().distanceTo(OEIL), d.longueur(), 0.02));
    }

    private static void hysteresis() {
        section("Hystérésis du côté");
        // Epaule droite partiellement genee (70 %), epaule gauche libre.
        DialogueCameraController.Collision gene = (ancre, offset) ->
                ancre.x + offset.x < 0.0 ? 0.7 : 1.0;
        DialogueCameraController.Cadrage sans = DialogueCameraController.calculer(
                CIBLE, OEIL, REGARD, 1.95, PROFIL, true, -1, gene);
        DialogueCameraController.Cadrage avec = DialogueCameraController.calculer(
                CIBLE, OEIL, REGARD, 1.95, PROFIL, true, 0, gene);
        check("sans mémoire : le côté entièrement libre gagne", sans.candidat() == 1);
        check("avec mémoire : le côté retenu, encore acceptable, est gardé", avec.candidat() == 0);

        DialogueCameraController.Collision trop = (ancre, offset) ->
                ancre.x + offset.x < 0.0 ? 0.5 : 1.0;
        DialogueCameraController.Cadrage bascule = DialogueCameraController.calculer(
                CIBLE, OEIL, REGARD, 1.95, PROFIL, true, 0, trop);
        check("côté retenu devenu trop court : on change", bascule.candidat() == 1);
    }

    private static void hauteurs() {
        section("Personnages petits et grands, joueur sur le personnage");
        Vec3d troll = new Vec3d(0.5, 203.0, 0.5);
        DialogueCameraController.Cadrage t = DialogueCameraController.calculer(
                troll, OEIL, REGARD, 3.5, PROFIL, true, -1, LIBRE);
        check("troll : la caméra reste à hauteur du joueur et lève les yeux",
                proche(t.pose().pos().y, OEIL.y + PROFIL.elevation(), 0.001) && t.pose().pitch() < -5.0);
        Vec3d gobelin = new Vec3d(0.5, 200.9, 0.5);
        DialogueCameraController.Cadrage g = DialogueCameraController.calculer(
                gobelin, OEIL, REGARD, 1.2, PROFIL, true, -1, LIBRE);
        check("gobelin : la caméra baisse les yeux", g.pose().pitch() > 5.0);

        Vec3d dessusOeil = new Vec3d(0.5, 201.62, 0.5);
        DialogueCameraController.Cadrage dessus = DialogueCameraController.calculer(
                CIBLE, dessusOeil, REGARD, 1.95, PROFIL, true, -1, LIBRE);
        check("joueur sur le personnage : caméra derrière lui selon son regard, pas d'exception",
                dessus.pose().pos().z < dessusOeil.z);
    }

    // --- outils ---------------------------------------------------------------

    /** Angle entre l'axe de la caméra et la direction d'un point, en degrés (non signé). */
    private static double angleVers(CameraFocus.Pose p, Vec3d cible) {
        double yaw = Math.toRadians(p.yaw());
        double pitch = Math.toRadians(p.pitch());
        Vec3d avant = new Vec3d(-Math.sin(yaw) * Math.cos(pitch), -Math.sin(pitch), Math.cos(yaw) * Math.cos(pitch));
        Vec3d vers = cible.subtract(p.pos()).normalize();
        return Math.toDegrees(Math.acos(Math.max(-1.0, Math.min(1.0, avant.dotProduct(vers)))));
    }

    /** Angle horizontal signé d'un point sur l'image : positif à gauche du centre, négatif à droite. */
    private static double angleHorizontal(CameraFocus.Pose p, Vec3d point) {
        double yaw = Math.toRadians(p.yaw());
        Vec3d avant = new Vec3d(-Math.sin(yaw), 0.0, Math.cos(yaw));
        Vec3d vers = new Vec3d(point.x - p.pos().x, 0.0, point.z - p.pos().z).normalize();
        double croix = avant.z * vers.x - avant.x * vers.z;
        double produit = avant.x * vers.x + avant.z * vers.z;
        return Math.toDegrees(Math.atan2(croix, produit));
    }

    private static void section(String titre) {
        System.out.println();
        System.out.println("  " + titre);
    }

    private static void check(String libelle, boolean ok) {
        System.out.println("    " + (ok ? "ok   " : "ECHEC") + " " + libelle);
        if (ok) {
            reussis++;
        } else {
            echecs++;
        }
    }

    private static boolean proche(double a, double b, double tolerance) {
        return Math.abs(a - b) <= tolerance;
    }
}
