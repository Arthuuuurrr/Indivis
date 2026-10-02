package net.hautecapitale.dialogue.client.dev;

import net.fabricmc.fabric.api.client.networking.v1.ClientPlayConnectionEvents;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.hautecapitale.dialogue.HauteCapitaleDialogue;
import net.hautecapitale.dialogue.client.DialogueClientState;
import net.hautecapitale.dialogue.client.screen.EcranDialogue;
import net.hautecapitale.dialogue.dev.DevTools;
import net.hautecapitale.dialogue.session.SessionFlags;
import net.hautecapitale.rpg.client.camera.CameraFocus;
import net.hautecapitale.rpg.client.camera.CameraOverrideManager;
import net.hautecapitale.rpg.client.camera.RpgCameraManager;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.option.Perspective;
import net.minecraft.entity.Entity;
import net.minecraft.util.Hand;
import org.lwjgl.glfw.GLFW;

import java.util.Locale;

/**
 * Le client exécute les ordres du banc d'essai — par les vrais chemins.
 *
 * <p>« interagir » passe par {@code interactionManager.interactEntity}, donc
 * par le paquet d'interaction ; « choisir » par l'écran ; « etat » répond dans
 * le chat, ce qui le fait apparaître dans le journal du serveur.
 */
public final class DevToolsClient {

    private DevToolsClient() {
    }

    public static void init() {
        if (!DevTools.actifs()) {
            return;
        }
        ClientPlayNetworking.registerGlobalReceiver(DevTools.DevOrder.ID,
                (payload, context) -> context.client().execute(() -> executer(context.client(), payload)));
        // Un client de banc ne doit jamais tenir la souris ni le clavier de la
        // personne qui utilise la machine : fenetre reduite des l'entree en jeu,
        // et pas de menu pause quand elle perd le focus. Minecraft continue de
        // rendre et de simuler une fenetre reduite.
        ClientPlayConnectionEvents.JOIN.register((handler, sender, client) -> {
            client.options.pauseOnLostFocus = false;
            // Cachee, pas reduite : une fenetre reduite reste dans la barre des
            // taches, et quelqu'un l'a rouverte pour cliquer dedans.
            GLFW.glfwHideWindow(client.getWindow().getHandle());
            HauteCapitaleDialogue.LOGGER.warn("Client de banc : fenêtre cachée jusqu'à la fin.");
        });
        // Plus de serveur : plus de raison de vivre. Aucune fenetre cachee ne survit au banc.
        ClientPlayConnectionEvents.DISCONNECT.register((handler, client) -> client.scheduleStop());
        HauteCapitaleDialogue.LOGGER.warn("Outils de banc d'essai client ACTIFS.");
    }

    private static void executer(MinecraftClient client, DevTools.DevOrder ordre) {
        if (client.player == null || client.world == null) {
            return;
        }
        switch (ordre.op()) {
            case "interagir" -> {
                Entity cible = null;
                for (Entity e : client.world.getEntities()) {
                    if (e != client.player && e.isAlive() && ordre.texte().equals(e.getName().getString())) {
                        cible = e;
                        break;
                    }
                }
                if (cible == null) {
                    repondre(client, "introuvable " + ordre.texte());
                    return;
                }
                client.interactionManager.interactEntity(client.player, cible, Hand.MAIN_HAND);
                repondre(client, "interaction envoyee vers " + ordre.texte());
            }
            case "choisir" -> {
                if (client.currentScreen instanceof EcranDialogue ecran) {
                    ecran.choisir(ordre.n());
                    repondre(client, "reponse " + ordre.n() + " choisie");
                } else {
                    repondre(client, "pas d'ecran de dialogue (" + nomEcran(client) + ")");
                }
            }
            case "fermer" -> {
                if (client.currentScreen != null) {
                    client.currentScreen.close();
                    repondre(client, "ecran ferme");
                } else {
                    repondre(client, "aucun ecran");
                }
            }
            case "perspective" -> {
                client.options.setPerspective("first".equals(ordre.texte()) ? Perspective.FIRST_PERSON : Perspective.THIRD_PERSON_BACK);
                repondre(client, "perspective " + client.options.getPerspective());
            }
            case "cinematique" -> {
                if ("on".equals(ordre.texte())) {
                    CameraOverrideManager.enterCinematicMode("banc");
                } else {
                    CameraOverrideManager.exitCinematicMode("banc");
                }
                repondre(client, "cinematique " + ordre.texte());
            }
            case "etat" -> repondre(client, etat(client));
            default -> repondre(client, "ordre inconnu " + ordre.op());
        }
    }

    private static String etat(MinecraftClient client) {
        double distance = -1.0;
        if (DialogueClientState.controleur() != null) {
            distance = client.gameRenderer.getCamera().getCameraPos().distanceTo(DialogueClientState.controleur().derniereCible());
        }
        return String.format(Locale.ROOT, "etat=%s session=%d mode=%s drapeaux=%s pnj=%s ecran=%s camera=%s melange=%.2f cam-cible=%.2f perspective=%s raison=%s repliques=%d choix=%d",
                DialogueClientState.etat(), DialogueClientState.sessionId(), DialogueClientState.mode(),
                SessionFlags.decrire(DialogueClientState.flags()), DialogueClientState.pnjNom(),
                nomEcran(client), RpgCameraManager.etat().affichage(), CameraFocus.melange(), distance,
                client.options.getPerspective(), DialogueClientState.derniereRaison(),
                DialogueClientState.repliquesCapturees().size(), DialogueClientState.choixCaptures().size());
    }

    private static String nomEcran(MinecraftClient client) {
        return client.currentScreen == null ? "aucun" : client.currentScreen.getClass().getSimpleName();
    }

    private static void repondre(MinecraftClient client, String texte) {
        String message = "hcd-banc " + texte;
        HauteCapitaleDialogue.LOGGER.info(message);
        if (client.getNetworkHandler() != null) {
            client.getNetworkHandler().sendChatMessage(message.length() > 250 ? message.substring(0, 250) : message);
        }
    }
}
