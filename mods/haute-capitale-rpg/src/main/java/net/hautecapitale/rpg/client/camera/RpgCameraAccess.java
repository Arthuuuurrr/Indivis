package net.hautecapitale.rpg.client.camera;

import net.minecraft.util.math.Vec3d;

/**
 * Le strict minimum que le mixin ouvre au reste du module.
 *
 * <p>{@code Camera#setPos} et {@code Camera#setRotation} sont {@code protected} :
 * c'est la seule raison d'être de cette interface. Tout le reste de ce dont la
 * caméra RPG a besoin — position, vecteurs de base, troisième personne — est
 * déjà public sur {@code Camera}.
 *
 * <p>La rotation n'est écrite que par {@link CameraFocus}, pendant un dialogue :
 * la caméra de gameplay, elle, ne tourne jamais — c'est ce qui la rend invisible
 * à Better Combat et à Spell Engine.
 *
 * <p>Le préfixe {@code hcrpg$} évite toute collision avec les membres qu'un autre
 * mod aurait déjà greffés sur la même classe : Bosses'Rise y pose les siens sous
 * {@code bosses_rise_java$}, Mythika et Axiom en posent d'autres.
 */
public interface RpgCameraAccess {

    /** Place la caméra à une position absolue, en coordonnées du monde. */
    void hcrpg$setPos(Vec3d position);

    /** Oriente la caméra. Lacet et tangage en degrés, convention vanilla. */
    void hcrpg$setRotation(float yaw, float pitch);
}
