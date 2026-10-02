package net.hautecapitale.rpg.client.camera;

import net.hautecapitale.rpg.HauteCapitaleRpg;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.option.Perspective;
import net.minecraft.client.render.Camera;
import net.minecraft.entity.Entity;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import org.joml.Vector3fc;

/**
 * Le cœur de la caméra RPG : machine à états, lissage, collision.
 *
 * <p>Une seule règle gouverne tout ce fichier : <b>la caméra est déplacée, le
 * joueur ne tourne jamais.</b> Aucun yaw, aucun pitch, aucun paquet. C'est ce qui
 * rend le module invisible à Better Combat, à Spell Engine et à l'autorité du
 * serveur, qui visent tous depuis la position et la rotation du joueur, jamais
 * depuis l'objet caméra.
 *
 * <p>La position finale est posée en absolu depuis l'ancre — les yeux du joueur —
 * et non ajoutée au recul que le jeu vient d'appliquer. Additionner les deux
 * ferait cohabiter deux ajustements de collision, l'un instantané (celui du jeu)
 * et l'autre lissé (le nôtre) : en sortant d'un couloir, la caméra ferait un bond
 * de la part du jeu puis un glissement de la nôtre. En posant la position en
 * absolu, notre lissage gouverne seul.
 */
public final class RpgCameraManager {

    // --- état ---------------------------------------------------------------

    private static CameraState etat = CameraState.GAMEPLAY_RPG;

    /** Coupe-circuit : après quelques erreurs, le module se retire de lui-même. */
    private static boolean panne = false;
    private static int erreurs = 0;

    // --- valeurs lissées ----------------------------------------------------

    private static double distanceLissee;
    private static double offsetXLisse;
    private static double offsetYLisse;
    private static double collisionLissee = 1.0;
    private static double hauteurOeilLissee;
    private static boolean ancreInitialisee;

    private static long dernierNano;
    private static long retourJusquaNano;

    // --- première personne d'appoint (§13, désactivée par défaut) ------------

    private static boolean premierePersonneForcee;
    private static Perspective perspectiveMemorisee;
    private static boolean demandeEtroit;

    // --- dernières valeurs appliquées, pour l'overlay et le réticule ---------

    private static double distanceAppliquee;
    private static Vec3d derniereAncre = Vec3d.ZERO;

    private RpgCameraManager() {
    }

    // ------------------------------------------------------------------------
    // Point d'entrée du mixin
    // ------------------------------------------------------------------------

    /**
     * Appelé à la fin de {@code Camera#update}, une fois par image.
     *
     * <p>Tout est enveloppé : une exception ici serait levée à chaque image, au
     * cœur du moteur de rendu. Mieux vaut que le module se retire.
     */
    public static void onCameraUpdated(Camera camera, RpgCameraAccess acces, World world,
                                       Entity focusedEntity, boolean thirdPerson, float tickProgress) {
        if (panne) {
            return;
        }
        try {
            appliquer(camera, acces, world, focusedEntity, thirdPerson, tickProgress);
        } catch (Throwable t) {
            erreurs++;
            HauteCapitaleRpg.LOGGER.error("Camera RPG : erreur pendant la mise a jour ({}/3).", erreurs, t);
            if (erreurs >= 3) {
                panne = true;
                HauteCapitaleRpg.LOGGER.error(
                        "Camera RPG desactivee pour cette session — la vue revient au comportement vanilla.");
            }
        }
    }

    private static void appliquer(Camera camera, RpgCameraAccess acces, World world,
                                  Entity focusedEntity, boolean thirdPerson, float tickProgress) {
        // La sonde d'abord : une cinematique qui demarre entre deux ticks ne doit
        // pas laisser passer une seule image de camera RPG.
        CinematicBridge.poll();

        CameraSettings s = CameraSettings.get();
        MinecraftClient client = MinecraftClient.getInstance();

        etat = calculerEtat(s);

        if (etat == CameraState.CINEMATIC_OVERRIDE || etat == CameraState.DISABLED) {
            // Rien. Pas de lecture de reglage, pas de calcul, pas d'ecriture.
            // C'est ce qui rend l'ordre d'application des mixins sans objet face a
            // un autre systeme de camera.
            replier();
            return;
        }

        // En dialogue, la camera de gameplay continue d'etre calculee : elle est
        // le point de depart du fondu d'entree et le point d'arrivee du fondu de
        // sortie. Sans cela, la sortie retomberait sur une pose figee a l'entree,
        // et le joueur qui a tourne pendant la conversation verrait un saut.
        // ViewCycle.rpgSelectionne() : avec le cycle F5, les vues vanilla « dos » et
        // « face » sont des positions a part entiere, ou la camera RPG ne touche a
        // rien. Elle n'a sa place que sur une position RPG du cycle.
        boolean rpgPilote = s.actif && thirdPerson && ViewCycle.rpgSelectionne()
                && client.player != null && focusedEntity == client.player
                && !client.player.isSpectator() && world != null;

        if (!rpgPilote) {
            replier();
            if (etat == CameraState.NPC_DIALOGUE) {
                CameraFocus.appliquer(camera, acces, world, focusedEntity, tickProgress,
                        camera.getCameraPos(), camera.getYaw(), camera.getPitch());
            }
            return;
        }

        double dt = deltaSecondes();
        double tau = enRetourDeCinematique() ? s.retour_cinematique_ms / 1000.0 : s.lissage_ms / 1000.0;
        double alpha = coefficientLissage(dt, tau);

        // --- ancre : les yeux du joueur, hauteur d'oeil lissee ----------------
        // Ce qui est lisse est la *hauteur des yeux au-dessus des pieds*, pas
        // l'altitude. Lisser l'altitude ferait trainer la camera derriere un joueur
        // en chute : un filtre exponentiel retarde toujours une entree a vitesse
        // constante, et a quinze blocs par seconde le retard se compte en blocs.
        // La hauteur d'oeil, elle, ne bouge qu'a l'accroupissement — c'est
        // exactement le a-coup qu'on veut absorber, et rien d'autre.
        Vec3d pieds = focusedEntity.getLerpedPos(tickProgress);
        Vec3d ancreBrute = focusedEntity.getCameraPosVec(tickProgress);
        double hauteurOeil = ancreBrute.y - pieds.y;

        if (!ancreInitialisee || Math.abs(hauteurOeil - hauteurOeilLissee) > 1.0) {
            // Premiere image, changement de dimension, monture, transformation.
            hauteurOeilLissee = hauteurOeil;
            ancreInitialisee = true;
        } else {
            hauteurOeilLissee += (hauteurOeil - hauteurOeilLissee)
                    * coefficientLissage(dt, s.lissage_hauteur_ms / 1000.0);
        }
        Vec3d ancre = new Vec3d(ancreBrute.x, pieds.y + hauteurOeilLissee, ancreBrute.z);
        derniereAncre = ancre;

        // --- cibles : le style RPG courant du cycle F5 --------------------------
        CameraSettings.Style style = ViewCycle.styleActif();
        double distanceCible = style.distance;
        double offsetXCible = s.epaule_droite ? style.offset_horizontal : -style.offset_horizontal;
        double offsetYCible = style.offset_vertical;

        distanceLissee += (distanceCible - distanceLissee) * alpha;
        offsetXLisse += (offsetXCible - offsetXLisse) * alpha;
        offsetYLisse += (offsetYCible - offsetYLisse) * alpha;

        // --- base camera -------------------------------------------------------
        // La rotation reste celle que le jeu vient de poser : on ne la touche pas.
        Vector3fc avantF = camera.getHorizontalPlane();
        Vector3fc hautF = camera.getVerticalPlane();
        Vector3fc gaucheF = camera.getDiagonalPlane();

        Vec3d avant = new Vec3d(avantF.x(), avantF.y(), avantF.z());
        Vec3d haut = new Vec3d(hautF.x(), hautF.y(), hautF.z());
        Vec3d droite = new Vec3d(-gaucheF.x(), -gaucheF.y(), -gaucheF.z());

        Vec3d offsetVoulu = droite.multiply(offsetXLisse)
                .add(haut.multiply(offsetYLisse))
                .add(avant.multiply(-distanceLissee));

        // --- collision ---------------------------------------------------------
        double facteur = s.collision_active
                ? CameraCollision.facteur(world, focusedEntity, ancre, offsetVoulu, s.collision_marge)
                : 1.0;

        if (facteur < collisionLissee) {
            // Rapprochement immediat : lisser ici ferait passer la camera dans le mur
            // le temps de la convergence.
            collisionLissee = facteur;
        } else {
            collisionLissee += (facteur - collisionLissee) * coefficientLissage(dt, s.collision_retour_ms / 1000.0);
        }

        Vec3d offsetFinal = offsetVoulu.multiply(collisionLissee);
        distanceAppliquee = distanceLissee * collisionLissee;

        // --- espace tres etroit (§13) ------------------------------------------
        demandeEtroit = s.passage_premiere_personne
                && distanceAppliquee < s.seuil_premiere_personne;

        Vec3d position = ancre.add(offsetFinal);
        acces.hcrpg$setPos(position);

        if (etat == CameraState.NPC_DIALOGUE) {
            // Par-dessus la pose de gameplay, jamais a la place : c'est ce qui
            // garantit un retour sans a-coup.
            demandeEtroit = false;
            CameraFocus.appliquer(camera, acces, world, focusedEntity, tickProgress,
                    position, camera.getYaw(), camera.getPitch());
        }
    }

    /**
     * Ramène les valeurs lissées au repos.
     *
     * <p>Appelé dès que la caméra RPG ne pilote pas : première personne,
     * cinématique, module coupé. La caméra repart ensuite <i>depuis le joueur</i>
     * et glisse jusqu'à sa distance — c'est la transition douce demandée entre la
     * première et la troisième personne, et le retour propre en fin de cinématique.
     */
    private static void replier() {
        distanceLissee = 0.0;
        offsetXLisse = 0.0;
        offsetYLisse = 0.0;
        collisionLissee = 1.0;
        distanceAppliquee = 0.0;
        ancreInitialisee = false;
        demandeEtroit = false;
    }

    /**
     * La priorité du cahier des charges : cinématique, puis dialogue, puis
     * gameplay. Un dialogue prend la caméra même si le joueur a coupé le module :
     * couper la caméra d'épaule n'est pas renoncer aux conversations.
     */
    private static CameraState calculerEtat(CameraSettings s) {
        if (CameraOverrideManager.isCinematic()) {
            return CameraState.CINEMATIC_OVERRIDE;
        }
        if (CameraFocus.estEngage()) {
            return CameraState.NPC_DIALOGUE;
        }
        if (!s.actif) {
            return CameraState.DISABLED;
        }
        return CameraState.GAMEPLAY_RPG;
    }

    // ------------------------------------------------------------------------
    // Cinématiques
    // ------------------------------------------------------------------------

    static void onCinematicEnter() {
        // Aucune sauvegarde de l'etat vanilla ici : la perspective, l'ATH et la
        // position de la camera appartiennent au systeme de cinematiques, qui a
        // deja son propre instantane. Deux systemes qui sauvegardent et restaurent
        // la meme variable, c'est le conflit qu'on veut eviter. On ne range que nos
        // propres valeurs.
        replier();
        relacherPremierePersonne();
    }

    static void onCinematicExit() {
        // Le repli a deja mis les valeurs a zero : la camera va donc ressortir en
        // glissant jusqu'a sa distance, sur la duree de retour configuree, au lieu
        // de reapparaitre d'un coup a 5,5 blocs.
        retourJusquaNano = System.nanoTime()
                + CameraSettings.get().retour_cinematique_ms * 1_000_000L;
    }

    private static boolean enRetourDeCinematique() {
        return System.nanoTime() < retourJusquaNano;
    }

    // ------------------------------------------------------------------------
    // Tick client
    // ------------------------------------------------------------------------

    /** Garde-fou et gestion de la première personne d'appoint. */
    public static void tick() {
        CameraOverrideManager.tickWatchdog();
        if (panne) {
            return;
        }
        gererPremierePersonne();
    }

    /**
     * Bascule d'appoint en première personne dans les espaces très étroits.
     *
     * <p>Faite au tick et non à l'image : changer de perspective en plein rendu
     * n'a rien à y faire. Et si le joueur appuie sur F5 pendant que le module tient
     * la perspective, le module lâche prise sans rien restaurer — c'est son choix
     * qui prime.
     */
    private static void gererPremierePersonne() {
        MinecraftClient client = MinecraftClient.getInstance();
        if (client.options == null) {
            return;
        }

        if (premierePersonneForcee && client.options.getPerspective() != Perspective.FIRST_PERSON) {
            premierePersonneForcee = false;
            perspectiveMemorisee = null;
            return;
        }

        if (etat != CameraState.GAMEPLAY_RPG) {
            relacherPremierePersonne();
            return;
        }

        if (demandeEtroit && !premierePersonneForcee) {
            perspectiveMemorisee = client.options.getPerspective();
            client.options.setPerspective(Perspective.FIRST_PERSON);
            premierePersonneForcee = true;
        } else if (premierePersonneForcee && espaceRedevenuLibre(client)) {
            relacherPremierePersonne();
        }
    }

    /**
     * Le couloir s'est-il rouvert ?
     *
     * <p>En première personne, {@code demandeEtroit} n'est plus recalculé : le
     * module ne pilote plus la caméra. On refait donc le test de collision à la
     * distance voulue, avec une marge d'hystérésis pour ne pas osciller à la
     * frontière.
     */
    private static boolean espaceRedevenuLibre(MinecraftClient client) {
        if (client.player == null || client.world == null) {
            return true;
        }
        CameraSettings s = CameraSettings.get();
        CameraSettings.Style style = ViewCycle.styleActif();
        Camera camera = client.gameRenderer.getCamera();

        Vector3fc avantF = camera.getHorizontalPlane();
        Vector3fc hautF = camera.getVerticalPlane();
        Vector3fc gaucheF = camera.getDiagonalPlane();

        Vec3d ancre = client.player.getCameraPosVec(1.0f);
        Vec3d offset = new Vec3d(-gaucheF.x(), -gaucheF.y(), -gaucheF.z())
                .multiply(s.epaule_droite ? style.offset_horizontal : -style.offset_horizontal)
                .add(new Vec3d(hautF.x(), hautF.y(), hautF.z()).multiply(style.offset_vertical))
                .add(new Vec3d(avantF.x(), avantF.y(), avantF.z()).multiply(-style.distance));

        double facteur = CameraCollision.facteur(client.world, client.player, ancre, offset, s.collision_marge);
        return style.distance * facteur > s.seuil_premiere_personne * 1.35;
    }

    private static void relacherPremierePersonne() {
        if (!premierePersonneForcee) {
            return;
        }
        MinecraftClient client = MinecraftClient.getInstance();
        if (client.options != null && perspectiveMemorisee != null
                && client.options.getPerspective() == Perspective.FIRST_PERSON) {
            client.options.setPerspective(perspectiveMemorisee);
        }
        premierePersonneForcee = false;
        perspectiveMemorisee = null;
    }

    /** Changement de monde, déconnexion, mort : on repart d'une ardoise propre. */
    public static void reset() {
        replier();
        relacherPremierePersonne();
        retourJusquaNano = 0L;
        dernierNano = 0L;
        CinematicBridge.reset();
        CameraOverrideManager.clear("changement de monde");
        CameraFocus.clear();
    }

    // ------------------------------------------------------------------------
    // Outils
    // ------------------------------------------------------------------------

    /**
     * Temps réel écoulé depuis l'image précédente, en secondes.
     *
     * <p>Le lissage se fait sur le temps et non sur le nombre d'images : la caméra
     * met le même temps à se remettre en place à 30 et à 144 images par seconde.
     * Le plafond de 100 ms évite qu'un à-coup — chargement de chunks, capture
     * d'écran — ne téléporte la caméra à l'image suivante.
     */
    private static double deltaSecondes() {
        long maintenant = System.nanoTime();
        if (dernierNano == 0L) {
            dernierNano = maintenant;
            return 0.0;
        }
        double dt = (maintenant - dernierNano) / 1_000_000_000.0;
        dernierNano = maintenant;
        return Math.min(dt, 0.1);
    }

    /** Coefficient d'un lissage exponentiel de constante {@code tau} secondes. */
    public static double coefficientLissage(double dt, double tau) {
        if (tau <= 0.0 || dt <= 0.0) {
            return 1.0;
        }
        return 1.0 - Math.exp(-dt / tau);
    }

    // ------------------------------------------------------------------------
    // Lecture, pour l'overlay de debug et le réticule
    // ------------------------------------------------------------------------

    public static CameraState etat() {
        return panne ? CameraState.DISABLED : etat;
    }

    public static boolean enPanne() {
        return panne;
    }

    public static double distanceAppliquee() {
        return distanceAppliquee;
    }

    public static double distanceLissee() {
        return distanceLissee;
    }

    public static double offsetXLisse() {
        return offsetXLisse;
    }

    public static double offsetYLisse() {
        return offsetYLisse;
    }

    public static double facteurCollision() {
        return collisionLissee;
    }

    public static boolean premierePersonneForcee() {
        return premierePersonneForcee;
    }

    public static Vec3d derniereAncre() {
        return derniereAncre;
    }

    /** Le module pilote-t-il réellement la caméra en ce moment ? */
    public static boolean pilote() {
        return !panne && etat == CameraState.GAMEPLAY_RPG && distanceAppliquee > 1.0e-3;
    }
}
