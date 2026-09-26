package net.hautecapitale.rpg.client.camera;

import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.hautecapitale.rpg.HauteCapitaleRpg;
import net.minecraft.client.option.KeyBinding;
import org.lwjgl.glfw.GLFW;

/**
 * Les commandes de la caméra.
 *
 * <p>Le choix des touches n'est pas libre : dans ce modpack, <b>C est déjà liée
 * quatre fois</b>, V et H trois fois, et la molette appartient à Zoomify, qui
 * l'utilise pour son zoom. Le zoom caméra passe donc par deux touches dédiées et
 * non par la molette : partager la molette imposerait d'injecter dans
 * {@code Mouse#onMouseScroll}, où Zoomify est déjà installé, pour un gain de
 * confort qui ne vaut pas ce risque.
 *
 * <p>Touches retenues parce qu'elles sont réellement libres dans le fichier
 * d'options du serveur : O, virgule, point, F9. Toutes réassignables dans le menu.
 */
public final class CameraKeybinds {

    private static final KeyBinding.Category CATEGORIE =
            KeyBinding.Category.create(HauteCapitaleRpg.id("camera"));

    public static KeyBinding epaule;
    public static KeyBinding eloigner;
    public static KeyBinding rapprocher;
    public static KeyBinding bascule;
    public static KeyBinding debug;

    private CameraKeybinds() {
    }

    static void register() {
        epaule = enregistrer("epaule", GLFW.GLFW_KEY_O);
        eloigner = enregistrer("eloigner", GLFW.GLFW_KEY_COMMA);
        rapprocher = enregistrer("rapprocher", GLFW.GLFW_KEY_PERIOD);
        // Non liee par defaut : couper la camera est une action de depannage, elle
        // n'a pas a occuper une touche sur un clavier deja sature.
        bascule = enregistrer("bascule", GLFW.GLFW_KEY_UNKNOWN);
        debug = enregistrer("debug", GLFW.GLFW_KEY_F9);
    }

    private static KeyBinding enregistrer(String nom, int touche) {
        return KeyBindingHelper.registerKeyBinding(new KeyBinding(
                "key." + HauteCapitaleRpg.MOD_ID + ".camera." + nom, touche, CATEGORIE));
    }

    /**
     * Traite les appuis accumulés depuis le tick précédent.
     *
     * <p>{@code wasPressed()} se consomme : la boucle vide la file, ce qui évite
     * de perdre un appui rapide et de rejouer indéfiniment le dernier.
     */
    static void tick() {
        CameraSettings s = CameraSettings.get();
        boolean modifie = false;

        while (epaule.wasPressed()) {
            s.epaule_droite = !s.epaule_droite;
            modifie = true;
        }
        // Le zoom agit sur le style RPG courant du cycle F5, et y reste : chaque
        // style garde sa propre distance.
        CameraSettings.Style style = ViewCycle.styleActif();
        while (eloigner.wasPressed()) {
            style.distance = Math.min(s.distance_max, style.distance + s.pas_de_zoom);
            modifie = true;
        }
        while (rapprocher.wasPressed()) {
            style.distance = Math.max(s.distance_min, style.distance - s.pas_de_zoom);
            modifie = true;
        }
        while (bascule.wasPressed()) {
            s.actif = !s.actif;
            modifie = true;
        }
        while (debug.wasPressed()) {
            s.overlay_debug = !s.overlay_debug;
            modifie = true;
        }

        if (modifie) {
            CameraSettings.save();
        }
    }
}
