package net.hautecapitale.dialogue.client;

import net.hautecapitale.dialogue.session.CameraProfile;
import net.hautecapitale.rpg.client.camera.CameraCollision;
import net.hautecapitale.rpg.client.camera.CameraFocus;
import net.hautecapitale.rpg.client.camera.CameraSettings;
import net.minecraft.client.render.Camera;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

/**
 * Où mettre la caméra pour cadrer une conversation.
 *
 * <p>Un plan d'épaule <b>ancré sur le joueur</b> : la caméra part de ses yeux,
 * recule derrière lui et se décale vers son épaule, puis regarde le personnage
 * en le laissant un peu à droite du centre (à gauche pour l'épaule gauche). Le
 * joueur reste ainsi au premier plan, sur le bord de l'image, quelle que soit
 * la distance du personnage ; recul, décalage et visée s'adaptent à cette
 * distance mais entre des bornes, jamais proportionnellement à elle.
 *
 * <p>Quand un mur gêne, on essaie l'autre épaule, puis des positions plus
 * hautes et plus rapprochées, et en dernier recours la caméra revient aux yeux
 * du joueur — un cadrage moins beau vaut mieux qu'une caméra dans un bloc.
 *
 * <p>Le calcul est une fonction pure ({@link #calculer}) pour être vérifiable
 * sans monde ; l'instance n'y ajoute que la lecture de l'entité, la mémoire du
 * candidat retenu (pour ne pas changer de côté à chaque image) et un lissage
 * temporel qui absorbe les micro-mouvements.
 */
public final class DialogueCameraController implements CameraFocus.Provider {

    /** Comment savoir si un déplacement traverse un mur. */
    @FunctionalInterface
    public interface Collision {
        /** Fraction praticable de {@code offset} depuis {@code ancre}, dans [0, 1]. */
        double facteur(Vec3d ancre, Vec3d offset);
    }

    /** Le résultat du calcul pur. */
    public record Cadrage(CameraFocus.Pose pose, int candidat, double longueur, boolean repli) {
    }

    /** En deçà de cette longueur libre, on cherche un autre angle. */
    static final double LONGUEUR_ACCEPTABLE = 1.2;

    /** En deçà, on renonce et la caméra revient aux yeux du joueur. */
    static final double LONGUEUR_MINIMALE = 0.8;

    /** Un candidat déjà retenu est gardé tant qu'un autre ne fait pas mieux d'autant. */
    static final double HYSTERESIS = 0.5;

    /** Les candidats de secours : plus haut, et un peu plus près du joueur. */
    private static final double SURELEVATION = 0.6;
    private static final double RAPPROCHEMENT = 0.85;
    private static final double SURELEVATION_CENTRE = 1.0;
    private static final double RAPPROCHEMENT_CENTRE = 0.7;

    /** Constante de temps du lissage de la cible, en secondes. */
    private static final double TAU = 0.12;

    private final int pnjEntityId;
    private final CameraProfile profil;

    private int candidatRetenu = -1;
    private long dernierNano;
    private Vec3d posLissee;
    private float yawLisse;
    private float pitchLisse;
    private Vec3d derniereCible = Vec3d.ZERO;
    private boolean dernierRepli;

    public DialogueCameraController(int pnjEntityId, CameraProfile profil) {
        this.pnjEntityId = pnjEntityId;
        this.profil = profil;
    }

    @Override
    @Nullable
    public CameraFocus.Pose compute(World world, Entity player, Camera camera, float tickProgress) {
        Entity pnj = world.getEntityById(this.pnjEntityId);
        if (pnj == null || !pnj.isAlive()) {
            return null;
        }

        Vec3d pieds = pnj.getLerpedPos(tickProgress);
        double hauteur = hauteurVisuelle(pnj);
        Vec3d cible = pieds.add(0.0, hauteur * this.profil.hauteurCible() + this.profil.decalageCibleY(), 0.0);
        Vec3d oeil = player.getCameraPosVec(tickProgress);
        Vec3d regard = player.getRotationVec(tickProgress);
        boolean coteDroit = CameraSettings.get().epaule_droite;
        double marge = CameraSettings.get().collision_marge;

        Cadrage cadrage = calculer(cible, oeil, regard, hauteur, this.profil, coteDroit, this.candidatRetenu,
                (ancre, offset) -> CameraCollision.facteur(world, player, ancre, offset, marge));
        this.candidatRetenu = cadrage.candidat();
        this.derniereCible = cible;
        this.dernierRepli = cadrage.repli();

        return lisser(cadrage.pose());
    }

    /** Hauteur du personnage telle qu'elle se voit : boîte × échelle d'attribut. */
    public static double hauteurVisuelle(Entity pnj) {
        double h = pnj.getHeight();
        if (pnj instanceof LivingEntity vivant) {
            h *= vivant.getScale();
        }
        return Math.max(0.5, h);
    }

    /**
     * Le calcul pur.
     *
     * @param cible     point visé (visage/torse du personnage)
     * @param oeil      yeux du joueur — l'ancre de la caméra
     * @param regard    direction du regard du joueur, pour le cas où il est sur le personnage
     * @param hauteur   hauteur visuelle du personnage (réservée ; le point visé la contient déjà)
     * @param profil    profil de caméra
     * @param coteDroit côté d'épaule
     * @param prefere   candidat retenu à l'image précédente, ou -1
     * @param collision test de collision depuis les yeux du joueur
     */
    public static Cadrage calculer(Vec3d cible, Vec3d oeil, Vec3d regard, double hauteur, CameraProfile profil,
                                   boolean coteDroit, int prefere, Collision collision) {
        Vec3d versPnj = new Vec3d(cible.x - oeil.x, 0.0, cible.z - oeil.z);
        double distance = versPnj.length();
        if (distance < 1.0e-4) {
            versPnj = new Vec3d(regard.x, 0.0, regard.z);
        }
        if (versPnj.lengthSquared() < 1.0e-4) {
            versPnj = new Vec3d(0.0, 0.0, 1.0);
        }
        Vec3d direction = versPnj.normalize();
        // La droite du joueur quand il fait face a `direction` : face au sud (+z), c'est -x.
        Vec3d droite = new Vec3d(-direction.z, 0.0, direction.x);
        double cote = coteDroit ? 1.0 : -1.0;

        double recul = profil.recul(distance);
        double decalage = profil.decalage(distance);
        double visee = profil.visee(distance);
        double haut = profil.elevation();

        // Les candidats, du plus souhaitable au plus modeste : l'epaule choisie,
        // l'autre epaule, les deux plus haut et plus pres, et le centre haut.
        Vec3d[] offsets = {
                offset(direction, droite, recul, decalage * cote, haut),
                offset(direction, droite, recul, -decalage * cote, haut),
                offset(direction, droite, recul * RAPPROCHEMENT, decalage * cote, haut + SURELEVATION),
                offset(direction, droite, recul * RAPPROCHEMENT, -decalage * cote, haut + SURELEVATION),
                offset(direction, droite, recul * RAPPROCHEMENT_CENTRE, 0.0, haut + SURELEVATION_CENTRE)
        };

        int meilleur = -1;
        double meilleureLongueur = -1.0;
        Vec3d meilleurOffset = null;

        for (int i = 0; i < offsets.length; i++) {
            Vec3d offset = offsets[i];
            double pleine = offset.length();
            double f = Math.max(0.0, Math.min(1.0, collision.facteur(oeil, offset)));
            double longueur = f * pleine;

            if (i == prefere && longueur >= LONGUEUR_ACCEPTABLE) {
                // Le candidat de l'image precedente reste acceptable : on le garde,
                // sauf si l'un des precedents fait nettement mieux.
                if (meilleur < 0 || longueur + HYSTERESIS >= meilleureLongueur) {
                    meilleur = i;
                    meilleureLongueur = longueur;
                    meilleurOffset = offset.multiply(f);
                }
                break;
            }
            if (longueur > meilleureLongueur + 1.0e-6) {
                meilleur = i;
                meilleureLongueur = longueur;
                meilleurOffset = offset.multiply(f);
            }
            if (longueur >= pleine - 1.0e-6 && i != prefere) {
                // Entierement libre : inutile d'aller chercher plus modeste.
                if (prefere < 0 || prefere >= offsets.length || i <= prefere) {
                    break;
                }
            }
        }

        boolean repli = meilleurOffset == null || meilleureLongueur < LONGUEUR_MINIMALE;
        Vec3d position = repli ? oeil : oeil.add(meilleurOffset);

        // La camera ne doit pas se retrouver dans la tete du joueur : si le
        // cadrage la pose a moins de 60 cm des yeux, autant y etre franchement.
        if (!repli && position.squaredDistanceTo(oeil) < 0.36) {
            position = oeil;
            repli = true;
        }

        // La camera regarde le personnage, en le laissant un peu du cote oppose a
        // l'epaule retenue : le joueur occupe le bord, le personnage la zone
        // centrale. Sur l'autre epaule, l'ecart s'inverse ; au centre, il n'y en
        // a pas ; en repli aux yeux, on vise droit, comme en premiere personne.
        Vec3d delta = cible.subtract(position);
        float yaw = (float) (Math.toDegrees(Math.atan2(delta.z, delta.x)) - 90.0);
        if (!repli) {
            double signe = switch (meilleur) {
                case 0, 2 -> cote;
                case 1, 3 -> -cote;
                default -> 0.0;
            };
            yaw -= (float) (visee * signe);
        }
        float pitch = (float) -Math.toDegrees(Math.atan2(delta.y, Math.sqrt(delta.x * delta.x + delta.z * delta.z)));
        return new Cadrage(new CameraFocus.Pose(position, yaw, pitch), repli ? -1 : meilleur,
                repli ? 0.0 : meilleureLongueur, repli);
    }

    /** Depuis les yeux : {@code recul} derrière, {@code cote} vers la droite, {@code haut} au-dessus. */
    private static Vec3d offset(Vec3d direction, Vec3d droite, double recul, double cote, double haut) {
        return direction.multiply(-recul).add(droite.multiply(cote)).add(0.0, haut, 0.0);
    }

    /** Lissage exponentiel sur le temps réel : même vitesse à 30 et à 144 images par seconde. */
    private CameraFocus.Pose lisser(CameraFocus.Pose brute) {
        long maintenant = System.nanoTime();
        if (this.posLissee == null) {
            this.posLissee = brute.pos();
            this.yawLisse = brute.yaw();
            this.pitchLisse = brute.pitch();
            this.dernierNano = maintenant;
            return brute;
        }
        double dt = Math.min(0.1, (maintenant - this.dernierNano) / 1.0e9);
        this.dernierNano = maintenant;
        float alpha = (float) (1.0 - Math.exp(-dt / TAU));

        // Un repli aux yeux du joueur, ou un changement de cote, ne se lisse pas :
        // lisser ferait passer la camera a travers le mur qui l'a fait reculer.
        if (brute.pos().squaredDistanceTo(this.posLissee) > 4.0) {
            alpha = 1.0f;
        }

        this.posLissee = MathHelper.lerp(alpha, this.posLissee, brute.pos());
        this.yawLisse = MathHelper.lerpAngleDegrees(alpha, this.yawLisse, brute.yaw());
        this.pitchLisse = MathHelper.lerp(alpha, this.pitchLisse, brute.pitch());
        return new CameraFocus.Pose(this.posLissee, this.yawLisse, this.pitchLisse);
    }

    public Vec3d derniereCible() {
        return this.derniereCible;
    }

    public boolean dernierRepli() {
        return this.dernierRepli;
    }

    public int candidatRetenu() {
        return this.candidatRetenu;
    }

    public CameraProfile profil() {
        return this.profil;
    }
}
