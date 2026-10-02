package net.hautecapitale.rpg.mixin.client;

import net.minecraft.client.render.Camera;
import net.minecraft.client.render.GameRenderer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

/**
 * Lit le champ de vision réellement employé pour l'image en cours.
 *
 * <p>Le réticule projeté a besoin de la focale exacte, sinon il se pose à côté.
 * {@code GameRenderer#getFov} est privée ; un {@link Invoker} en ouvre l'accès
 * <b>sans toucher au corps de la méthode</b> : aucune injection, aucun code
 * ajouté dans le flux, juste un pont synthétique. Bosses'Rise et Zoomify injectent
 * tous deux au RETURN de cette même méthode pour en modifier la valeur — passer
 * par elle est précisément ce qui garantit que le réticule suit leurs
 * modifications au lieu de les ignorer.
 */
@Mixin(GameRenderer.class)
public interface GameRendererAccessor {

    @Invoker("getFov")
    float hcrpg$getFov(Camera camera, float tickProgress, boolean changingFov);
}
