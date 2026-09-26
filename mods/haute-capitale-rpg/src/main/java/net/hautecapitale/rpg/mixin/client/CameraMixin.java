package net.hautecapitale.rpg.mixin.client;

import net.hautecapitale.rpg.client.camera.RpgCameraAccess;
import net.hautecapitale.rpg.client.camera.RpgCameraManager;
import net.minecraft.client.render.Camera;
import net.minecraft.entity.Entity;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/**
 * Le seul endroit où la caméra RPG modifie le moteur de rendu.
 *
 * <p>Fabric API n'expose aucun évènement de caméra : cette injection est donc
 * inévitable. Elle est aussi suffisante — tout le reste du module passe par des
 * évènements publics.
 *
 * <p>Le point choisi est la <b>fin</b> de {@code Camera#update}, c'est-à-dire
 * après que le jeu a posé rotation et position, et <b>avant</b> que
 * {@code GameRenderer} ne construise le frustum à partir de cette caméra. Sodium
 * et Distant Horizons voient donc une caméra cohérente : déplacer la vue plus tard
 * dans l'image corromprait leur élimination des faces cachées.
 *
 * <p>Aucune annulation, aucune redirection : cette classe ne fait qu'ajouter un
 * appel en queue de méthode. Un autre mod injecté au même endroit — Bosses'Rise,
 * Mythika, Axiom le sont — n'en est pas affecté, et l'ordre d'application n'a pas
 * d'importance puisque le gestionnaire ressort à sa première instruction dès
 * qu'une cinématique tient la caméra.
 *
 * <p>Le second ombrage, {@code setRotation}, n'est appelé que pendant un
 * dialogue, pour que la caméra regarde le personnage. En gameplay, la rotation
 * reste strictement celle posée par le jeu.
 */
@Mixin(Camera.class)
public abstract class CameraMixin implements RpgCameraAccess {

    @Shadow
    protected abstract void setPos(Vec3d pos);

    @Shadow
    protected abstract void setRotation(float yaw, float pitch);

    @Override
    public void hcrpg$setPos(Vec3d position) {
        this.setPos(position);
    }

    @Override
    public void hcrpg$setRotation(float yaw, float pitch) {
        this.setRotation(yaw, pitch);
    }

    @Inject(method = "update", at = @At("TAIL"))
    private void hcrpg$apresMiseAJour(World area, Entity focusedEntity, boolean thirdPerson,
                                      boolean inverseView, float tickProgress, CallbackInfo ci) {
        RpgCameraManager.onCameraUpdated(
                (Camera) (Object) this, this, area, focusedEntity, thirdPerson, tickProgress);
    }
}
