package net.hautecapitale.rpg.client.camera;

import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.hautecapitale.rpg.HauteCapitaleRpg;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.option.Perspective;
import net.minecraft.client.render.Camera;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.math.Vec3d;
import org.joml.Vector3fc;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

/**
 * Vérification de la caméra <b>en jeu</b>, sans personne devant l'écran.
 *
 * <p>Le harnais hors jeu prouve les mathématiques ; il ne prouve pas que le mixin
 * s'applique, que la caméra bouge réellement, ni qu'une suspension rend bien la
 * vue au comportement vanilla. Ce scénario le fait : il place le joueur en l'air
 * pour dégager le champ, mesure la position de la caméra dans son propre repère,
 * ouvre puis referme une cinématique, coupe puis rallume le module, et compare
 * chaque mesure à ce qui est attendu.
 *
 * <p>Inerte par défaut. Il ne s'enregistre que si la propriété système
 * {@code hcrpg.camera.selftest} est posée :
 *
 * <pre>./gradlew runClient -Dhcrpg.camera.selftest=true</pre>
 *
 * <p>Le client s'arrête tout seul à la fin et le verdict est dans
 * {@code run/logs/latest.log}.
 */
public final class CameraSelfTest {

    private static final String PROPRIETE = "hcrpg.camera.selftest";
    private static final String RAISON = "autotest";

    /** Tolérance des mesures, en blocs. Le lissage a largement convergé. */
    private static final double TOLERANCE = 0.15;

    private static int tick = -1;
    private static final List<String> resultats = new ArrayList<>();
    private static boolean termine;
    private static int[] boite;

    private CameraSelfTest() {
    }

    static boolean demande() {
        return System.getProperty(PROPRIETE) != null;
    }

    static void register() {
        ClientTickEvents.END_CLIENT_TICK.register(CameraSelfTest::onTick);
        HauteCapitaleRpg.LOGGER.info("Camera RPG : autotest en jeu arme (-D{}).", PROPRIETE);
    }

    private static void onTick(MinecraftClient client) {
        if (termine || client.player == null || client.world == null) {
            return;
        }
        tick++;

        // Un joueur qui bouge fausse toute mesure : la camera est posee a l'image
        // precedente, la mesure est prise au tick suivant, et l'ecart vaut la
        // vitesse. On l'immobilise donc en vol stationnaire.
        if (tick >= 30) {
            immobiliser(client);
        }

        switch (tick) {
            case 20 -> preparer(client);
            case 40 -> orienter(client);
            case 100 -> mesurer(client, "caméra RPG, épaule droite", 5.5, 0.55, 0.25);
            case 102 -> CameraOverrideManager.enterCinematicMode(RAISON);
            case 115 -> mesurerVanilla(client, "pendant une cinématique");
            case 117 -> CameraOverrideManager.exitCinematicMode(RAISON);
            case 175 -> mesurer(client, "après la cinématique, cadrage restauré", 5.5, 0.55, 0.25);
            case 177 -> CameraSettings.get().actif = false;
            case 190 -> mesurerVanilla(client, "module coupé (OFF)");
            case 192 -> {
                CameraSettings.get().actif = true;
                CameraSettings.get().epaule_droite = false;
            }
            case 255 -> mesurer(client, "épaule gauche", 5.5, -0.55, 0.25);
            case 257 -> ViewCycle.styleActif().distance = 3.0;
            case 320 -> mesurer(client, "zoom à 3 blocs", 3.0, -0.55, 0.25);
            case 322 -> {
                ViewCycle.styleActif().distance = 5.5;
                CameraSettings.get().epaule_droite = true;
                // Regard a l'horizontale : la camera recule alors droit derriere le
                // joueur, et le mur qu'elle rencontre est a une distance connue. Avec
                // un tangage de vingt degres elle monte vers le plafond de la boite,
                // et la distance restante depend de la fraction de bloc ou se trouve
                // le joueur — mesure juste, mais irreproductible.
                client.player.setPitch(0.0f);
                murs(client, "minecraft:stone");
            }
            case 345 -> mesurerCollision(client);
            case 347 -> murs(client, "minecraft:air");
            case 420 -> mesurer(client, "sortie d'espace étroit, distance reprise", 5.5, 0.55, 0.25);

            // --- le cycle F5, position par position -------------------------------
            // On part de RPG « epaule » (1/2). Chaque avancer() vaut un appui sur F5.
            case 422 -> ViewCycle.avancer(client);
            case 424 -> verifierPosition(client, "F5 → RPG mmo", Perspective.THIRD_PERSON_BACK, true);
            case 480 -> mesurer(client, "style mmo : centré, haut, loin", 7.0, 0.0, 0.9);
            case 482 -> ViewCycle.avancer(client);
            case 484 -> verifierPosition(client, "F5 → 1re personne vanilla", Perspective.FIRST_PERSON, false);
            case 486 -> ViewCycle.avancer(client);
            case 488 -> verifierPosition(client, "F5 → 3e personne vanilla", Perspective.THIRD_PERSON_BACK, false);
            case 540 -> mesurerVanilla(client, "3e personne vanilla : intacte");
            case 542 -> ViewCycle.avancer(client);
            case 544 -> verifierPosition(client, "F5 → vue de face vanilla", Perspective.THIRD_PERSON_FRONT, false);
            case 546 -> ViewCycle.avancer(client);
            case 548 -> verifierPosition(client, "F5 → RPG epaule", Perspective.THIRD_PERSON_BACK, true);
            case 600 -> mesurer(client, "retour au style epaule par le cycle", 5.5, 0.55, 0.25);
            case 602 -> CameraOverrideManager.enterCinematicMode(RAISON);
            case 604 -> {
                // Un F5 pendant une cinematique doit etre avale : ni le jeu ni nous
                // ne devons changer la perspective sous Bosses'Rise.
                ViewCycle.tickDebutPourTest(client, 1);
                verifierPosition(client, "F5 pendant une cinématique : ignoré",
                        Perspective.THIRD_PERSON_BACK, true);
                CameraOverrideManager.exitCinematicMode(RAISON);
            }
            case 606 -> {
                // La vraie touche, par le vrai chemin : on incremente le compteur
                // d'appuis de F5 exactement comme le ferait GLFW, puis on laisse le
                // tick suivant se derouler. Si notre interception passe avant le
                // jeu, on avance d'une position RPG ; si le jeu passait avant, il
                // basculerait lui-meme en vue de face et l'ecart serait visible.
                net.minecraft.client.option.KeyBinding.onKeyPressed(
                        net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper
                                .getBoundKeyOf(client.options.togglePerspectiveKey));
            }
            case 610 -> {
                verifierPosition(client, "vraie touche F5 interceptée avant le jeu",
                        Perspective.THIRD_PERSON_BACK, true);
                boolean mmo = ViewCycle.indexStyle() == 1;
                String ligne = "    " + (mmo ? "ok   " : "ECHEC") + "  "
                        + String.format(Locale.ROOT, "%-42s", "et une seule position avancée (epaule → mmo)")
                        + "  style " + ViewCycle.styleActif().nom + "   (attendu mmo)";
                resultats.add(ligne);
                HauteCapitaleRpg.LOGGER.info(ligne);
            }
            case 620 -> conclure(client);
            default -> {
            }
        }
    }

    /** Vol stationnaire : plus de vitesse, donc plus d'écart entre pose et mesure. */
    private static void immobiliser(MinecraftClient client) {
        client.player.getAbilities().allowFlying = true;
        client.player.getAbilities().flying = true;
        client.player.setVelocity(Vec3d.ZERO);
    }

    /**
     * Une orientation quelconque, volontairement pas alignée sur les axes.
     *
     * <p>Avec un lacet et un tangage nuls, une erreur de signe entre « droite » et
     * « gauche », ou entre « haut » et « avant », passerait inaperçue.
     */
    private static void orienter(MinecraftClient client) {
        client.player.setYaw(45.0f);
        client.player.setPitch(20.0f);
    }

    private static void preparer(MinecraftClient client) {
        CameraSettings s = CameraSettings.get();
        s.actif = true;
        s.f5_cycle = true;
        s.styles = CameraSettings.stylesParDefaut();
        s.epaule_droite = true;
        s.collision_active = true;
        s.passage_premiere_personne = false;

        // Position RPG du cycle F5, et non un simple passage en troisieme personne :
        // avec le cycle, la troisieme personne seule est une vue vanilla intacte.
        ViewCycle.selectionnerRpg(client, 0);

        // Degager le champ, et toujours au meme endroit. Au sol, la collision
        // rapprocherait la camera et la mesure ne dirait plus rien du cadrage. Le
        // vidage prealable n'est pas une precaution de style : le monde de test
        // survit d'une execution a l'autre, et une coquille laissee par un essai
        // interrompu fausserait tout le scenario suivant sans qu'on comprenne
        // pourquoi.
        MinecraftServer serveur = client.getServer();
        if (serveur != null) {
            String nom = client.player.getNameForScoreboard();
            serveur.execute(() -> {
                try {
                    var manager = serveur.getCommandManager();
                    manager.parseAndExecute(serveur.getCommandSource(),
                            "fill -8 192 -8 8 208 8 minecraft:air replace");
                    manager.parseAndExecute(serveur.getCommandSource(),
                            "tp " + nom + " 0.5 200 0.5");
                    manager.parseAndExecute(serveur.getCommandSource(),
                            "gamemode creative " + nom);
                } catch (Exception e) {
                    HauteCapitaleRpg.LOGGER.warn("Autotest : mise en l'air impossible ({}).", e.getMessage());
                }
            });
        }
    }

    /**
     * Enferme le joueur dans une coquille creuse, ou la retire.
     *
     * <p>C'est le seul moyen d'éprouver la collision sans dépendre du relief : une
     * boîte de sept blocs de côté, posée autour du joueur en plein ciel, met un mur
     * exactement à trois blocs dans toutes les directions.
     */
    private static void murs(MinecraftClient client, String bloc) {
        MinecraftServer serveur = client.getServer();
        if (serveur == null) {
            return;
        }
        // La boite est memorisee a sa construction et reutilisee telle quelle pour
        // la demolition : recalculer les coordonnees depuis la position du joueur
        // laisserait des pans de mur si celui-ci a derive d'un bloc entre-temps, et
        // la mesure de sortie mesurerait alors autre chose que ce qu'elle croit.
        if (boite == null) {
            boite = new int[]{
                    (int) Math.floor(client.player.getX()),
                    (int) Math.floor(client.player.getY()),
                    (int) Math.floor(client.player.getZ())
            };
        }
        int x = boite[0];
        int y = boite[1];
        int z = boite[2];
        String forme = bloc.equals("minecraft:air") ? "replace" : "hollow";
        String commande = String.format(Locale.ROOT, "fill %d %d %d %d %d %d %s %s",
                x - 3, y - 3, z - 3, x + 3, y + 3, z + 3, bloc, forme);
        serveur.execute(() -> {
            try {
                serveur.getCommandManager().parseAndExecute(serveur.getCommandSource(), commande);
            } catch (Exception e) {
                HauteCapitaleRpg.LOGGER.warn("Autotest : coquille impossible ({}).", e.getMessage());
            }
        });
    }

    /**
     * La caméra doit être rentrée bien en deçà de la distance demandée.
     *
     * <p>Les murs sont à deux blocs ; une caméra encore à 5,5 traverserait la
     * pierre, et une caméra à zéro serait dans la tête du joueur.
     */
    private static void mesurerCollision(MinecraftClient client) {
        double[] m = mesure(client);

        // La propriete qui compte n'est pas la distance exacte — elle depend de la
        // fraction de bloc ou se tient le joueur — mais le fait que la camera soit
        // nettement rentree et, surtout, qu'elle ne soit pas dans la pierre.
        boolean rentree = m[0] < 3.6 && RpgCameraManager.facteurCollision() < 0.999;
        boolean dehors = client.world.getBlockState(net.minecraft.util.math.BlockPos.ofFloored(
                client.gameRenderer.getCamera().getCameraPos())).isAir();

        noter(rentree && dehors, "espace étroit, caméra rentrée et hors des murs", m,
                dehors ? "attendu distance < 3.60" : "CAMERA DANS UN BLOC");
    }

    /** La position du cycle F5 est-elle celle attendue, perspective et drapeau RPG ? */
    private static void verifierPosition(MinecraftClient client, String etape,
                                         Perspective attendue, boolean rpgAttendu) {
        Perspective reelle = client.options.getPerspective();
        boolean rpg = ViewCycle.rpgSelectionne();
        boolean ok = reelle == attendue && rpg == rpgAttendu;
        String ligne = String.format(Locale.ROOT,
                "    %s  %-42s  %s%s   (attendu %s%s)",
                ok ? "ok   " : "ECHEC", etape,
                reelle.name(), rpg ? " + RPG" : "",
                attendue.name(), rpgAttendu ? " + RPG" : "");
        resultats.add(ligne);
        HauteCapitaleRpg.LOGGER.info(ligne);
    }

    /** Décompose la position de la caméra dans son propre repère. */
    private static double[] mesure(MinecraftClient client) {
        Camera camera = client.gameRenderer.getCamera();
        Vector3fc avantF = camera.getHorizontalPlane();
        Vector3fc hautF = camera.getVerticalPlane();
        Vector3fc gaucheF = camera.getDiagonalPlane();

        Vec3d avant = new Vec3d(avantF.x(), avantF.y(), avantF.z());
        Vec3d haut = new Vec3d(hautF.x(), hautF.y(), hautF.z());
        Vec3d droite = new Vec3d(-gaucheF.x(), -gaucheF.y(), -gaucheF.z());

        Vec3d delta = camera.getCameraPos().subtract(client.player.getCameraPosVec(1.0f));
        return new double[]{
                -delta.dotProduct(avant),
                delta.dotProduct(droite),
                delta.dotProduct(haut)
        };
    }

    private static void mesurer(MinecraftClient client, String etape,
                                double distance, double lateral, double vertical) {
        double[] m = mesure(client);
        boolean ok = proche(m[0], distance) && proche(m[1], lateral) && proche(m[2], vertical);
        noter(ok, etape, m, String.format(Locale.ROOT, "attendu %.2f / %+.2f / %+.2f",
                distance, lateral, vertical));
    }

    private static void mesurerVanilla(MinecraftClient client, String etape) {
        double[] m = mesure(client);
        // Vanilla : recul d'environ quatre blocs, strictement aucun decalage lateral
        // ni vertical. C'est l'absence de decalage qui prouve que la camera RPG
        // n'ecrit plus rien.
        boolean ok = Math.abs(m[1]) < 0.05 && Math.abs(m[2]) < 0.05 && m[0] < 4.5;
        noter(ok, etape, m, "attendu ~4.00 / +0.00 / +0.00 (vanilla)");
    }

    private static void noter(boolean ok, String etape, double[] m, String attendu) {
        String ligne = String.format(Locale.ROOT,
                "    %s  %-42s  mesuré %.2f / %+.2f / %+.2f   (%s)",
                ok ? "ok   " : "ECHEC", etape, m[0], m[1], m[2], attendu);
        resultats.add(ligne);
        HauteCapitaleRpg.LOGGER.info(ligne);
    }

    private static boolean proche(double a, double b) {
        return Math.abs(a - b) <= TOLERANCE;
    }

    private static void conclure(MinecraftClient client) {
        termine = true;
        long echecs = resultats.stream().filter(l -> l.contains("ECHEC")).count();

        HauteCapitaleRpg.LOGGER.info("");
        HauteCapitaleRpg.LOGGER.info("  === AUTOTEST CAMERA RPG : distance / latéral / vertical ===");
        resultats.forEach(HauteCapitaleRpg.LOGGER::info);
        HauteCapitaleRpg.LOGGER.info("  === {} mesures, {} échecs ===", resultats.size(), echecs);
        HauteCapitaleRpg.LOGGER.info("");

        client.scheduleStop();
    }
}
