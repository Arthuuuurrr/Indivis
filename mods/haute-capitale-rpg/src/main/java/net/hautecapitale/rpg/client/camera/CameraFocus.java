package net.hautecapitale.rpg.client.camera;

import net.minecraft.client.render.Camera;
import net.minecraft.entity.Entity;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

/**
 * La prise de caméra par un dialogue — l'état {@link CameraState#NPC_DIALOGUE}.
 *
 * <p>C'est la seule surface publique du module caméra, avec
 * {@link CameraOverrideManager}. Un système de dialogue ne calcule pas de
 * position de caméra dans le moteur de rendu : il fournit un
 * {@link Provider}, appelé à chaque image, qui dit <i>où</i> la caméra devrait
 * être et <i>vers quoi</i> elle devrait regarder. Le module fait le reste :
 * l'entrée en fondu depuis la caméra de gameplay, la sortie en fondu vers elle,
 * et le retrait immédiat dès qu'une cinématique prend la main.
 *
 * <p><b>Pourquoi un fondu et non une téléportation.</b> Le cahier des charges
 * est explicite : pas de « clic → téléportation brutale ». Le fondu est calculé
 * sur le temps réel, en <i>smoothstep</i>, entre la pose que la caméra de gameplay
 * aurait eue à cette image et la pose demandée. Comme la caméra de gameplay
 * continue d'être calculée pendant tout le dialogue, la sortie retombe exactement
 * sur elle, sans à-coup, quelle que soit la façon dont le joueur a bougé.
 *
 * <p>Toutes les méthodes sont à appeler depuis le fil client.
 */
public final class CameraFocus {

    /** Une pose de caméra : position absolue, lacet et tangage en degrés. */
    public record Pose(Vec3d pos, float yaw, float pitch) {
    }

    /**
     * Ce qu'un dialogue fournit au module.
     *
     * <p>Appelé à chaque image tant que la prise est engagée. Renvoyer {@code null}
     * signifie « je ne sais pas » — le personnage n'est pas chargé, par exemple —
     * et le module conserve alors la dernière pose connue plutôt que de sauter.
     */
    @FunctionalInterface
    public interface Provider {
        @Nullable
        Pose compute(World world, Entity player, Camera camera, float tickProgress);
    }

    private static Provider provider;
    private static boolean demande;

    private static long debutNano;
    private static int dureeMs;
    private static double melangeDepart;
    private static double melange;

    private static Pose derniere;

    private CameraFocus() {
    }

    /**
     * Demande la caméra.
     *
     * @param nouveau      le calcul de pose, appelé à chaque image
     * @param transitionMs durée du fondu d'entrée ; 0 pour une prise instantanée
     */
    public static void request(Provider nouveau, int transitionMs) {
        provider = nouveau;
        demande = true;
        commencerTransition(transitionMs);
    }

    /**
     * Rend la caméra.
     *
     * <p>Le fournisseur reste interrogé pendant le fondu de sortie, pour que la
     * caméra parte de là où elle est et non d'une pose figée.
     *
     * @param transitionMs durée du fondu de sortie ; 0 pour un retour instantané
     */
    public static void release(int transitionMs) {
        if (!demande && provider == null) {
            return;
        }
        demande = false;
        commencerTransition(transitionMs);
    }

    /** Retrait immédiat, sans fondu. Changement de monde, cinématique. */
    public static void clear() {
        provider = null;
        demande = false;
        melange = 0.0;
        melangeDepart = 0.0;
        dureeMs = 0;
        derniere = null;
    }

    /** Un dialogue a demandé la caméra et ne l'a pas rendue. */
    public static boolean estDemande() {
        return demande;
    }

    /** La prise pèse encore sur la caméra : demandée, ou en fondu de sortie. */
    public static boolean estEngage() {
        return demande || provider != null;
    }

    /** Part de la pose de dialogue dans l'image courante, de 0 à 1. */
    public static double melange() {
        return melange;
    }

    private static void commencerTransition(int transitionMs) {
        melangeDepart = melange;
        dureeMs = Math.max(0, transitionMs);
        debutNano = System.nanoTime();
    }

    /**
     * Applique la prise sur la caméra, par-dessus la pose de gameplay.
     *
     * <p>Appelé par le gestionnaire à chaque image en état {@code NPC_DIALOGUE},
     * après que la caméra de gameplay a posé sa propre position.
     */
    static void appliquer(Camera camera, RpgCameraAccess acces, World world, Entity player,
                          float tickProgress, Vec3d basePos, float baseYaw, float basePitch) {
        melange = melangeCourant();

        Pose cible = null;
        if (provider != null && world != null && player != null) {
            try {
                cible = provider.compute(world, player, camera, tickProgress);
            } catch (Throwable t) {
                // Un fournisseur qui lève a chaque image est un fournisseur qu'on
                // retire : mieux vaut une camera rendue qu'un journal inonde.
                cible = null;
                provider = null;
                demande = false;
            }
        }
        if (cible == null) {
            cible = derniere;
        }

        if (!demande && melange <= 0.001) {
            // Fondu de sortie termine : la prise est levee, et le gestionnaire
            // reprendra l'etat GAMEPLAY_RPG a l'image suivante.
            provider = null;
            derniere = null;
            melange = 0.0;
            return;
        }

        if (cible == null) {
            return;
        }
        derniere = cible;

        if (melange >= 0.999) {
            acces.hcrpg$setRotation(cible.yaw(), cible.pitch());
            acces.hcrpg$setPos(cible.pos());
            return;
        }

        float t = (float) melange;
        float yaw = MathHelper.lerpAngleDegrees(t, baseYaw, cible.yaw());
        float pitch = MathHelper.lerp(t, basePitch, cible.pitch());
        Vec3d pos = MathHelper.lerp(melange, basePos, cible.pos());
        acces.hcrpg$setRotation(yaw, pitch);
        acces.hcrpg$setPos(pos);
    }

    private static double melangeCourant() {
        double objectif = demande ? 1.0 : 0.0;
        if (dureeMs <= 0) {
            return objectif;
        }
        double t = (System.nanoTime() - debutNano) / (dureeMs * 1_000_000.0);
        if (t >= 1.0) {
            return objectif;
        }
        if (t <= 0.0) {
            return melangeDepart;
        }
        return melangeDepart + (objectif - melangeDepart) * smoothstep(t);
    }

    /** L'accélération douce classique : nulle aux deux bouts, maximale au milieu. */
    public static double smoothstep(double t) {
        if (t <= 0.0) {
            return 0.0;
        }
        if (t >= 1.0) {
            return 1.0;
        }
        return t * t * (3.0 - 2.0 * t);
    }
}
