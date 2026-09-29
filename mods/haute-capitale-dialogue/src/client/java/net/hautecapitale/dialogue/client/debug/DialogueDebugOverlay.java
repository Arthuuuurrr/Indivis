package net.hautecapitale.dialogue.client.debug;

import net.fabricmc.fabric.api.client.rendering.v1.hud.HudElement;
import net.hautecapitale.dialogue.client.DialogueCameraController;
import net.hautecapitale.dialogue.client.DialogueClientSettings;
import net.hautecapitale.dialogue.client.DialogueClientState;
import net.hautecapitale.dialogue.client.screen.EcranDialogue;
import net.hautecapitale.dialogue.session.SessionFlags;
import net.hautecapitale.rpg.client.camera.CameraFocus;
import net.hautecapitale.rpg.client.camera.RpgCameraManager;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.render.RenderTickCounter;
import net.minecraft.entity.Entity;

import java.util.Locale;

/**
 * L'état du dialogue en haut à droite, pour ne pas diagnostiquer à l'aveugle.
 *
 * <p>Activé par {@code /dialoguec debug}. Se dessine par-dessus le masque du HUD.
 */
public final class DialogueDebugOverlay implements HudElement {

    private static final int LIGNE = 10;

    @Override
    public void render(DrawContext context, RenderTickCounter tickCounter) {
        if (!DialogueClientSettings.get().overlay_debug) {
            return;
        }
        MinecraftClient client = MinecraftClient.getInstance();
        if (client.textRenderer == null || client.options == null || client.options.hudHidden) {
            return;
        }

        int x = context.getScaledWindowWidth() - 4;
        int y = 4;
        DialogueClientState.Etat etat = DialogueClientState.etat();
        int couleur = switch (etat) {
            case IDLE -> 0xFF9AA6AC;
            case ENTERING_DIALOGUE, EXITING_DIALOGUE, TRANSITIONING_NODE -> 0xFFFFC857;
            case DIALOGUE_ACTIVE -> 0xFF7FE3A1;
            case CINEMATIC_OVERRIDE -> 0xFFE2786F;
        };

        y = ligne(context, x, y, "Dialogue RPG", 0xFFE6EDEF);
        y = ligne(context, x, y, "etat " + etat + "   camera " + RpgCameraManager.etat().affichage(), couleur);
        if (etat == DialogueClientState.Etat.IDLE) {
            if (DialogueClientState.derniereRaison() != null) {
                ligne(context, x, y, "derniere fermeture : " + DialogueClientState.derniereRaison(), 0xFFC7D2D6);
            }
            return;
        }

        Entity pnj = client.world == null ? null : client.world.getEntityById(DialogueClientState.pnjEntityId());
        double distance = pnj == null || client.player == null ? -1.0 : Math.sqrt(client.player.squaredDistanceTo(pnj));
        y = ligne(context, x, y, String.format(Locale.ROOT, "session #%d  %s  pnj %s (id %d)",
                DialogueClientState.sessionId(), DialogueClientState.mode(), DialogueClientState.pnjNom(),
                DialogueClientState.pnjEntityId()), 0xFFC7D2D6);
        y = ligne(context, x, y, String.format(Locale.ROOT, "distance %.2f   drapeaux %s   perspective %s",
                distance, SessionFlags.decrire(DialogueClientState.flags()),
                DialogueClientState.perspectiveForcee() ? "forcee 3e" : "inchangee"), 0xFFC7D2D6);
        y = ligne(context, x, y, String.format(Locale.ROOT, "melange %.2f   ecran %s",
                CameraFocus.melange(),
                client.currentScreen == null ? "aucun" : client.currentScreen.getClass().getSimpleName()), 0xFFC7D2D6);

        DialogueCameraController c = DialogueClientState.controleur();
        if (c != null && client.player != null) {
            double dCam = client.gameRenderer.getCamera().getCameraPos().distanceTo(c.derniereCible());
            y = ligne(context, x, y, String.format(Locale.ROOT, "cadrage : candidat %d  %s  camera-cible %.2f",
                    c.candidatRetenu(), c.dernierRepli() ? "REPLI (yeux)" : "ok", dCam), 0xFFC7D2D6);
            y = ligne(context, x, y, String.format(Locale.ROOT, "profil : recul %.2f→%.2f  decalage %.2f→%.2f  visee %.0f→%.0f°  h %.2f",
                    c.profil().reculProche(), c.profil().reculLoin(), c.profil().decalageProche(), c.profil().decalageLoin(),
                    c.profil().viseeProche(), c.profil().viseeLoin(), c.profil().hauteurCible()), 0xFFC7D2D6);
        }
        if (client.currentScreen instanceof EcranDialogue ecran) {
            y = ligne(context, x, y, "noeud " + ecran.libelleDialogue() + "   historique "
                    + DialogueClientState.historique().size(), 0xFFC7D2D6);
            for (String reponse : ecran.reponsesDebug()) {
                y = ligne(context, x, y, reponse, reponse.contains("verrouill") ? 0xFF9AA6AC : 0xFFC7D2D6);
            }
        }
    }

    private static int ligne(DrawContext context, int droite, int y, String texte, int couleur) {
        int largeur = MinecraftClient.getInstance().textRenderer.getWidth(texte);
        context.drawText(MinecraftClient.getInstance().textRenderer, texte, droite - largeur, y, couleur, true);
        return y + LIGNE;
    }
}
