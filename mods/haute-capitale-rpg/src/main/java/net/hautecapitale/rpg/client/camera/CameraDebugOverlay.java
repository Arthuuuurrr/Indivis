package net.hautecapitale.rpg.client.camera;

import net.fabricmc.fabric.api.client.rendering.v1.hud.HudElement;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.render.RenderTickCounter;

import java.util.Locale;

/**
 * L'affichage qui évite de diagnostiquer à l'aveugle.
 *
 * <p>Un conflit de caméra ne produit ni erreur ni message : la vue bouge mal, et
 * c'est tout. Savoir en un coup d'œil quel état est actif, qui retient la caméra
 * et de combien la collision a rapproché la vue transforme une session de tâtonnement
 * en une lecture.
 */
public final class CameraDebugOverlay implements HudElement {

    private static final int MARGE = 4;
    private static final int LIGNE = 10;

    @Override
    public void render(DrawContext context, RenderTickCounter tickCounter) {
        if (!CameraSettings.get().overlay_debug) {
            return;
        }
        MinecraftClient client = MinecraftClient.getInstance();
        if (client.textRenderer == null || client.options == null || client.options.hudHidden) {
            return;
        }

        CameraSettings s = CameraSettings.get();
        CameraState etat = RpgCameraManager.etat();

        int couleurEtat = switch (etat) {
            case GAMEPLAY_RPG -> 0xFF7FE3A1;
            case NPC_DIALOGUE -> 0xFF8FB0D3;
            case CINEMATIC_OVERRIDE -> 0xFFFFC857;
            case DISABLED -> 0xFF9AA6AC;
        };

        int y = MARGE;
        y = ligne(context, "Camera RPG", 0xFFE6EDEF, y);
        y = ligne(context, "  etat        " + etat.affichage(), couleurEtat, y);
        y = ligne(context, "  vue F5      " + ViewCycle.affichage(client.options.getPerspective()),
                0xFFC7D2D6, y);
        y = ligne(context, String.format(Locale.ROOT, "  dialogue    %s  (melange %.2f)",
                CameraFocus.estDemande() ? "PRISE" : (CameraFocus.estEngage() ? "sortie" : "non"),
                CameraFocus.melange()), 0xFFC7D2D6, y);
        y = ligne(context, "  cinematique " + (CameraOverrideManager.isCinematic()
                ? "OUI  (" + CameraOverrideManager.raisonsAffichees()
                        + ", profondeur " + CameraOverrideManager.profondeur() + ")"
                : "non"), 0xFFC7D2D6, y);
        y = ligne(context, String.format(Locale.ROOT, "  distance    %.2f / %.2f  (collision ×%.2f)",
                RpgCameraManager.distanceAppliquee(), ViewCycle.styleActif().distance,
                RpgCameraManager.facteurCollision()), 0xFFC7D2D6, y);
        y = ligne(context, String.format(Locale.ROOT, "  offset      X %+.2f  Y %+.2f  (epaule %s)",
                RpgCameraManager.offsetXLisse(), RpgCameraManager.offsetYLisse(),
                s.epaule_droite ? "droite" : "gauche"), 0xFFC7D2D6, y);
        y = ligne(context, "  collision   " + (s.collision_active ? "active" : "coupee")
                + "   reticule " + (s.reticule_projete ? "projete" : "vanilla"), 0xFFC7D2D6, y);
        y = ligne(context, "  1re pers.   " + (s.passage_premiere_personne
                ? (RpgCameraManager.premierePersonneForcee() ? "FORCEE" : "armee")
                : "desactivee"), 0xFFC7D2D6, y);
        y = ligne(context, "  sonde       " + switch (CinematicBridge.etat()) {
            case ACTIVE -> "Bosses'Rise branchee";
            case ABSENT -> "aucun mod de cinematique";
            case INDISPONIBLE -> "Bosses'Rise illisible — API explicite seule";
        }, 0xFFC7D2D6, y);

        if (RpgCameraManager.enPanne()) {
            ligne(context, "  MODULE EN PANNE — vue vanilla jusqu'a la prochaine session", 0xFFE2786F, y);
        }
    }

    private static int ligne(DrawContext context, String texte, int couleur, int y) {
        context.drawText(MinecraftClient.getInstance().textRenderer, texte, MARGE, y, couleur, true);
        return y + LIGNE;
    }
}
