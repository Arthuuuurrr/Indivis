package net.hautecapitale.rpg.client.camera;

import net.minecraft.entity.Entity;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.RaycastContext;
import net.minecraft.world.World;

/**
 * Empêche la caméra de passer à travers la géométrie du monde.
 *
 * <p>Même principe que le recul vanilla — huit rayons tirés depuis les coins d'un
 * petit cube autour du joueur, et non un rayon unique — mais appliqué à un
 * déplacement quelconque et non au seul axe de recul. Les huit rayons sont ce qui
 * évite que la caméra se faufile par l'angle d'un mur : un rayon central passerait
 * dans l'interstice là où un coin touche.
 *
 * <p>Le test porte sur la forme <i>visuelle</i> des blocs : c'est celui que le
 * jeu emploie pour son propre recul, et il ignore les blocs sans collision comme
 * les hautes herbes, qu'il serait absurde de laisser repousser la caméra.
 */
public final class CameraCollision {

    /** Demi-côté du cube d'échantillonnage, en blocs. Valeur vanilla. */
    private static final double ECART = 0.1;

    private CameraCollision() {
    }

    /**
     * Fraction du déplacement réellement praticable.
     *
     * @param world  monde client
     * @param entity entité ignorée par les rayons — le joueur lui-même
     * @param ancre  origine du déplacement, à hauteur des yeux du joueur
     * @param offset déplacement souhaité de l'ancre vers la caméra
     * @param marge  distance conservée entre la caméra et la surface touchée
     * @return un facteur dans {@code [0, 1]} à appliquer à {@code offset}
     */
    public static double facteur(World world, Entity entity, Vec3d ancre, Vec3d offset, double marge) {
        double longueur = offset.length();
        if (longueur < 1.0e-4) {
            return 1.0;
        }

        double praticable = longueur;
        boolean touche = false;

        for (int i = 0; i < 8; i++) {
            double dx = ((i & 1) * 2 - 1) * ECART;
            double dy = (((i >> 1) & 1) * 2 - 1) * ECART;
            double dz = (((i >> 2) & 1) * 2 - 1) * ECART;

            Vec3d depart = ancre.add(dx, dy, dz);
            Vec3d arrivee = depart.add(offset);

            BlockHitResult resultat = world.raycast(new RaycastContext(
                    depart, arrivee,
                    RaycastContext.ShapeType.VISUAL,
                    RaycastContext.FluidHandling.NONE,
                    entity));

            if (resultat.getType() != HitResult.Type.MISS) {
                touche = true;
                // Distance mesuree depuis l'ancre, pas depuis le coin decale :
                // sinon les huit rayons ne seraient pas comparables entre eux.
                double distance = resultat.getPos().distanceTo(ancre);
                if (distance < praticable) {
                    praticable = distance;
                }
            }
        }

        // La marge ne se retranche que d'une surface réellement touchée. L'appliquer
        // aussi en terrain dégagé rapprocherait la caméra de sa valeur en permanence
        // — un cadrage systématiquement trop court, sans rien pour le signaler.
        if (!touche) {
            return 1.0;
        }
        praticable = Math.max(0.0, praticable - marge);
        return Math.min(1.0, praticable / longueur);
    }
}
