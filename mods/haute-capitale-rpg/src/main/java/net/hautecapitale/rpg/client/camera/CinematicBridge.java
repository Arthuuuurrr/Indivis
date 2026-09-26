package net.hautecapitale.rpg.client.camera;

import net.fabricmc.loader.api.FabricLoader;
import net.hautecapitale.rpg.HauteCapitaleRpg;

import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandles;
import java.lang.invoke.MethodType;

/**
 * Sonde de secours branchée sur le système de cinématiques de Bosses'Rise.
 *
 * <p>La voie normale reste l'appel explicite à {@link CameraOverrideManager} : un
 * système de cinématiques annonce lui-même son début et sa fin. Cette classe
 * existe pour le cas où un plan démarre par un chemin qui n'a pas été instrumenté
 * — et parce que Bosses'Rise n'expose aujourd'hui <b>aucune API</b> : ses
 * cinématiques sont déclenchées par des keyframes GeckoLib, sans paquet ni
 * évènement à écouter.
 *
 * <p>Le seul point d'observation stable qu'il offre est
 * {@code BossesRiseClientCinematicCamera.isCameraActive()}, publique et statique.
 * On la lit par {@link MethodHandle} plutôt que par dépendance de compilation :
 * cela évite d'accrocher le build à un JAR de 24 Mo sous licence propriétaire, et
 * une évolution de Bosses'Rise dégrade la sonde au lieu de casser le mod.
 *
 * <p>Ce n'est pas une heuristique : on lit un état déclaré, pas un symptôme.
 */
final class CinematicBridge {

    private static final String CLASSE =
            "net.unusual.block_factorys_bosses.client.camera.BossesRiseClientCinematicCamera";
    private static final String MOD_ID = "block_factorys_bosses";
    private static final String RAISON = "bossesrise";

    private static MethodHandle isCameraActive;
    private static Etat etat = Etat.ABSENT;
    private static boolean suspensionOuverte;

    enum Etat {
        /** Bosses'Rise n'est pas installé. */
        ABSENT,
        /** Sonde branchée et fonctionnelle. */
        ACTIVE,
        /** Bosses'Rise est là, mais son point d'observation a changé. */
        INDISPONIBLE
    }

    private CinematicBridge() {
    }

    static void init() {
        if (!FabricLoader.getInstance().isModLoaded(MOD_ID)) {
            etat = Etat.ABSENT;
            return;
        }
        try {
            Class<?> cible = Class.forName(CLASSE);
            isCameraActive = MethodHandles.lookup()
                    .findStatic(cible, "isCameraActive", MethodType.methodType(boolean.class));
            etat = Etat.ACTIVE;
            HauteCapitaleRpg.LOGGER.info("Camera RPG : sonde cinematique branchee sur Bosses'Rise.");
        } catch (Throwable t) {
            etat = Etat.INDISPONIBLE;
            isCameraActive = null;
            HauteCapitaleRpg.LOGGER.warn(
                    "Camera RPG : Bosses'Rise present mais son etat cinematique est illisible ({}). "
                            + "La suspension reposera uniquement sur les appels explicites a CameraOverrideManager.",
                    t.toString());
        }
    }

    /**
     * Confronte l'état observé à l'état déclaré et corrige l'écart.
     *
     * <p>Appelée à chaque image plutôt qu'à chaque tick : une cinématique qui
     * démarre entre deux ticks ne doit pas laisser passer une seule image de
     * caméra RPG.
     */
    static void poll() {
        if (isCameraActive == null) {
            return;
        }
        boolean actif;
        try {
            actif = (boolean) isCameraActive.invokeExact();
        } catch (Throwable t) {
            // Une exception ici serait rejouée soixante fois par seconde : on coupe
            // la sonde plutot que d'inonder le journal.
            etat = Etat.INDISPONIBLE;
            isCameraActive = null;
            HauteCapitaleRpg.LOGGER.warn("Camera RPG : sonde cinematique coupee apres une erreur ({}).",
                    t.toString());
            return;
        }

        if (actif) {
            if (!suspensionOuverte) {
                suspensionOuverte = true;
                CameraOverrideManager.enterCinematicMode(RAISON);
            } else {
                CameraOverrideManager.refresh();
            }
        } else if (suspensionOuverte) {
            suspensionOuverte = false;
            CameraOverrideManager.exitCinematicMode(RAISON);
        }
    }

    /** Referme la suspension détenue par la sonde. Changement de monde, déconnexion. */
    static void reset() {
        if (suspensionOuverte) {
            suspensionOuverte = false;
            CameraOverrideManager.exitCinematicMode(RAISON);
        }
    }

    static Etat etat() {
        return etat;
    }
}
