package net.hautecapitale.rpg.client.camera;

import net.fabricmc.fabric.api.client.rendering.v1.hud.HudElement;
import net.hautecapitale.rpg.mixin.client.GameRendererAccessor;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.render.Camera;
import net.minecraft.client.render.RenderTickCounter;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.hit.EntityHitResult;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.RaycastContext;
import org.joml.Vector3fc;

/**
 * Marque à l'écran l'endroit réellement visé par le joueur.
 *
 * <p>Le paradoxe de l'épaule : la caméra regarde depuis un point, le tir part
 * d'un autre. Deux réponses existent, et une seule est acceptable ici.
 *
 * <p>Tourner le joueur vers ce que désigne le centre de l'écran corrigerait le
 * réticule — au prix de réécrire yaw et pitch et de renvoyer des paquets de
 * rotation. Better Combat construit son cône d'attaque sur la rotation du joueur,
 * Spell Engine vise depuis {@code getEyePos()} et {@code getRotationVec()}, et le
 * serveur rejoue l'attaque avec la rotation qu'il a reçue : les trois se
 * retrouveraient en désaccord avec le client.
 *
 * <p>On fait donc l'inverse : la visée n'est jamais touchée, et c'est le réticule
 * qui va la rejoindre. Le rayon reste celui du joueur — le jeu lui-même le tire
 * depuis le joueur et non depuis la caméra, ce qui laisse portée, minage,
 * ciblage, flèches et sorts rigoureusement inchangés. Il ne reste qu'un décalage
 * d'affichage, et il est corrigé ici, en projetant le point visé dans le repère
 * de la caméra.
 */
public final class AimReticle implements HudElement {

    /** En deçà de ce décalage en pixels, le réticule vanilla suffit. */
    private static final double SEUIL_PIXELS = 4.0;

    /** Demi-longueur des branches du marqueur, en pixels d'interface. */
    private static final int BRANCHE = 3;

    @Override
    public void render(DrawContext context, RenderTickCounter tickCounter) {
        CameraSettings s = CameraSettings.get();
        if (!s.reticule_projete || !RpgCameraManager.pilote()) {
            return;
        }

        MinecraftClient client = MinecraftClient.getInstance();
        if (client.player == null || client.world == null || client.options == null
                || client.options.hudHidden) {
            return;
        }

        float tickProgress = tickCounter.getTickProgress(true);
        Camera camera = client.gameRenderer.getCamera();

        Vec3d oeil = client.player.getCameraPosVec(tickProgress);
        Vec3d direction = client.player.getRotationVec(tickProgress);
        Vec3d vise = pointVise(client, oeil, direction, s.reticule_portee);

        // Repere de la camera : exactement celui du rendu, rotation comprise.
        Vector3fc avantF = camera.getHorizontalPlane();
        Vector3fc hautF = camera.getVerticalPlane();
        Vector3fc gaucheF = camera.getDiagonalPlane();

        Vec3d avant = new Vec3d(avantF.x(), avantF.y(), avantF.z());
        Vec3d haut = new Vec3d(hautF.x(), hautF.y(), hautF.z());
        Vec3d droite = new Vec3d(-gaucheF.x(), -gaucheF.y(), -gaucheF.z());

        Vec3d versCible = vise.subtract(camera.getCameraPos());
        double profondeur = versCible.dotProduct(avant);
        if (profondeur < 0.1) {
            // Le point vise est derriere la camera : rien a dessiner.
            return;
        }

        float fov;
        try {
            fov = ((GameRendererAccessor) client.gameRenderer).hcrpg$getFov(camera, tickProgress, true);
        } catch (Throwable t) {
            return;
        }
        if (fov <= 0.0f || fov >= 180.0f) {
            return;
        }

        int largeur = context.getScaledWindowWidth();
        int hauteur = context.getScaledWindowHeight();

        double[] ecran = projeter(versCible.dotProduct(droite), versCible.dotProduct(haut),
                profondeur, largeur, hauteur, fov);
        double x = ecran[0];
        double y = ecran[1];

        if (Math.hypot(x - largeur / 2.0, y - hauteur / 2.0) < SEUIL_PIXELS) {
            return;
        }
        if (x < 0 || y < 0 || x > largeur || y > hauteur) {
            return;
        }

        dessiner(context, (int) Math.round(x), (int) Math.round(y));
    }

    /**
     * Projette un point du repère caméra sur l'écran.
     *
     * <p>Minecraft projette avec un champ de vision <b>vertical</b> : une fois la
     * focale exprimée en pixels de hauteur, elle vaut pour les deux axes, l'aspect
     * de l'écran étant déjà porté par le rapport largeur/hauteur. C'est la seule
     * subtilité de ce calcul, et c'est aussi la seule façon de s'y tromper sans que
     * rien ne le signale : un réticule décalé n'émet aucune erreur.
     *
     * @param lateral    composante droite du vecteur caméra→cible
     * @param vertical   composante haute du vecteur caméra→cible
     * @param profondeur composante avant, strictement positive
     * @param fovDegres  champ de vision vertical, en degrés
     * @return les coordonnées écran {@code {x, y}}, en pixels d'interface
     */
    public static double[] projeter(double lateral, double vertical, double profondeur,
                                    int largeur, int hauteur, double fovDegres) {
        double focale = (hauteur / 2.0) / Math.tan(Math.toRadians(fovDegres) / 2.0);
        return new double[]{
                largeur / 2.0 + (lateral / profondeur) * focale,
                hauteur / 2.0 - (vertical / profondeur) * focale
        };
    }

    /**
     * Où pointe réellement le joueur.
     *
     * <p>L'entité sous le réticule vanilla est privilégiée quand il y en a une :
     * c'est elle que le joueur veut frapper, et le jeu l'a déjà calculée depuis le
     * joueur. Sinon on tire un rayon sur les blocs, et à défaut on prend le bout de
     * la portée.
     */
    private static Vec3d pointVise(MinecraftClient client, Vec3d oeil, Vec3d direction, double portee) {
        if (client.crosshairTarget instanceof EntityHitResult entite) {
            return entite.getPos();
        }

        Vec3d bout = oeil.add(direction.multiply(portee));
        BlockHitResult touche = client.world.raycast(new RaycastContext(
                oeil, bout,
                RaycastContext.ShapeType.COLLIDER,
                RaycastContext.FluidHandling.NONE,
                client.player));

        return touche.getType() == HitResult.Type.MISS ? bout : touche.getPos();
    }

    /** Quatre branches courtes, cerclées de noir pour rester lisibles sur tout fond. */
    private static void dessiner(DrawContext context, int x, int y) {
        final int ombre = 0xC0000000;
        final int trait = 0xFFFFFFFF;

        // Ombre : la meme croix, un pixel plus large dans chaque direction.
        context.fill(x - BRANCHE - 1, y - 1, x + BRANCHE + 2, y + 2, ombre);
        context.fill(x - 1, y - BRANCHE - 1, x + 2, y + BRANCHE + 2, ombre);

        context.fill(x - BRANCHE, y, x + BRANCHE + 1, y + 1, trait);
        context.fill(x, y - BRANCHE, x + 1, y + BRANCHE + 1, trait);
    }
}
