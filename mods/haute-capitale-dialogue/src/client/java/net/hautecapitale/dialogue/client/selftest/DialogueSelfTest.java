package net.hautecapitale.dialogue.client.selftest;

import de.markusbordihn.easynpc.entity.easynpc.EasyNPC;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.hautecapitale.dialogue.HauteCapitaleDialogue;
import net.hautecapitale.dialogue.api.Dialogues;
import net.hautecapitale.dialogue.config.DialogueConfig;
import net.hautecapitale.dialogue.session.SessionFlags;
import net.hautecapitale.dialogue.client.DialogueCameraController;
import net.hautecapitale.dialogue.client.DialogueClientSettings;
import net.hautecapitale.dialogue.client.DialogueClientState;
import net.hautecapitale.dialogue.client.HudMask;
import net.hautecapitale.dialogue.client.capture.CaptureDialogueScreen;
import net.hautecapitale.dialogue.client.compat.easynpc.RpgDialogueScreen;
import net.hautecapitale.dialogue.client.screen.DialoguePanel;
import net.hautecapitale.dialogue.client.screen.EcranDialogue;
import net.hautecapitale.dialogue.session.CloseReason;
import net.hautecapitale.dialogue.session.DialogueManager;
import net.hautecapitale.dialogue.session.DialogueMode;
import net.hautecapitale.rpg.client.camera.CameraFocus;
import net.hautecapitale.rpg.client.camera.CameraOverrideManager;
import net.hautecapitale.rpg.client.camera.CameraState;
import net.hautecapitale.rpg.client.camera.RpgCameraManager;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.ingame.MerchantScreen;
import net.minecraft.client.option.Perspective;
import net.minecraft.client.render.Camera;
import net.minecraft.entity.Entity;
import net.minecraft.scoreboard.ReadableScoreboardScore;
import net.minecraft.scoreboard.ScoreHolder;
import net.minecraft.scoreboard.ScoreboardObjective;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import org.joml.Vector3fc;
import org.lwjgl.glfw.GLFW;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * Le dialogue vérifié <b>en jeu</b>, sans personne devant l'écran.
 *
 * <p>Partie solo : le serveur intégré tourne dans le même processus, on peut
 * donc lire ses sessions directement. Le scénario pose des personnages Easy NPC
 * par commande, clique dessus par le vrai chemin d'interaction (paquet compris),
 * choisit des réponses, mesure la caméra et l'état, puis coupe le client.
 *
 * <p>Deux familles de personnages : à menu Easy NPC (Capitaine, Magistrat), et
 * à commandes {@code tellraw} comme les quêtes du datapack (Roch, Crieur) — ces
 * derniers exercent la capture des dialogues de chat.
 *
 * <p>Inerte par défaut : {@code ./gradlew runClient -PdialogueSelfTest}.
 * Verdict dans {@code run-client/logs/latest.log}.
 */
public final class DialogueSelfTest {

    private static final String PROPRIETE = "hcd.selftest";
    private static final String CAPITAINE = "Capitaine de test";
    private static final String MAGISTRAT = "Magistrat de test";
    private static final String ROCH = "Roch de test";
    private static final String CRIEUR = "Crieur de test";
    private static final String COCHON = "Cochon de test";
    private static final String PASSEUR = "Passeur de test";

    /** Un personnage « porté » (sans IA, comme un capitaine épinglé à sa barre), avec un dialogue à menu. */
    private static final String NBT_PASSEUR = "{CustomName:\"" + PASSEUR + "\",Tags:[\"hc_dialogue\"],Rotation:[180f,0f],NoAI:1b,"
            + "DialogData:{Type:\"STANDARD\",DialogDataSet:["
            + "{Name:\"Traversée\",Label:\"default\",Texts:[{Text:\"Montez, on largue les amarres dès que le vent tourne.\"}],"
            + "Buttons:[{Name:\"Où allons-nous ?\",Label:\"ou\",Actions:[{Type:\"OPEN_NAMED_DIALOG\",Cmd:\"default\"}]},"
            + "{Name:\"Au revoir.\",Label:\"bye\",Type:\"CLOSE\"}]}]},"
            + "ActionData:{ActionEventSet:{ON_INTERACTION:[{Type:\"OPEN_DEFAULT_DIALOG\"}]}}}";

    private static final String MARCHAND = "Marchand de test";

    /** Un dialogue dont une réponse ouvre le commerce d'Easy NPC (une offre : une émeraude contre un pain). */
    private static final String NBT_MARCHAND = "{CustomName:\"" + MARCHAND + "\",Tags:[\"hc_dialogue\"],Rotation:[180f,0f],"
            + "DialogData:{Type:\"STANDARD\",DialogDataSet:["
            + "{Name:\"Étal\",Label:\"default\",Texts:[{Text:\"Du pain frais, contre une émeraude. Regardez donc.\"}],"
            + "Buttons:[{Name:\"Voir vos marchandises.\",Label:\"commerce\",Actions:[{Type:\"OPEN_TRADING_SCREEN\"}]},"
            + "{Name:\"Au revoir.\",Label:\"bye\",Type:\"CLOSE\"}]}]},"
            + "TradingData:{TradingDataSet:{Type:\"BASIC\"}},"
            + "Offers:{Recipes:[{buy:{id:\"minecraft:emerald\",count:1},sell:{id:\"minecraft:bread\",count:1},maxUses:99}]},"
            + "ActionData:{ActionEventSet:{ON_INTERACTION:[{Type:\"OPEN_DEFAULT_DIALOG\"}]}}}";

    private static final String STYLISTE = "Styliste de test";

    /** Des réponses étiquetées par leur auteur : la nuance vient de l'étiquette, qui disparaît du libellé. */
    private static final String NBT_STYLISTE = "{CustomName:\"" + STYLISTE + "\",Tags:[\"hc_dialogue\"],Rotation:[180f,0f],"
            + "DialogData:{Type:\"STANDARD\",DialogDataSet:["
            + "{Name:\"Accueil\",Label:\"default\",Texts:[{Text:\"Que puis-je pour vous ?\"}],"
            + "Buttons:[{Name:\"[quête] Accepter la tâche.\",Label:\"quete\",Actions:[{Type:\"OPEN_NAMED_DIALOG\",Cmd:\"suite\"}]},"
            + "{Name:\"[commerce] Voir l'étal.\",Label:\"commerce\",Actions:[{Type:\"OPEN_NAMED_DIALOG\",Cmd:\"suite\"}]},"
            + "{Name:\"[danger] Vous allez le regretter.\",Label:\"danger\",Actions:[{Type:\"OPEN_NAMED_DIALOG\",Cmd:\"suite\"}]},"
            + "{Name:\"Au revoir.\",Label:\"bye\",Type:\"CLOSE\"}]},"
            + "{Name:\"Suite\",Label:\"suite\",Texts:[{Text:\"Bien. Revenez me voir.\"}],"
            + "Buttons:[{Name:\"Merci.\",Label:\"merci\",Type:\"CLOSE\"}]}]},"
            + "ActionData:{ActionEventSet:{ON_INTERACTION:[{Type:\"OPEN_DEFAULT_DIALOG\"}]}}}";

    /** Le véhicule simulé : le personnage est déplacé d'un pas par tick, comme un pont qui glisse. */
    private static int deplacementJusqua = -1;
    private static double deplacementPas;

    /** La sonde d'un mod de transport, pour le scénario « refuser ». */
    private static boolean sondeEnRoute;
    private static final Function<Entity, Optional<Boolean>> SONDE =
            e -> PASSEUR.equals(e.getName().getString()) ? Optional.of(sondeEnRoute) : Optional.empty();

    private static final String NBT_CAPITAINE = "{CustomName:\"" + CAPITAINE + "\",Tags:[\"hc_dialogue\"],Rotation:[180f,0f],"
            + "DialogData:{Type:\"STANDARD\",DialogDataSet:["
            + "{Name:\"Accueil\",Label:\"default\",Texts:[{Text:\"Vous n'êtes pas d'ici, n'est-ce pas ? Le port manque de bras, et les Profondeurs de courage.\"}],"
            + "Buttons:[{Name:\"Je viens chercher du travail.\",Label:\"travail\",Actions:[{Type:\"OPEN_NAMED_DIALOG\",Cmd:\"suite\"}]},"
            + "{Name:\"Que savez-vous des Profondeurs ?\",Label:\"profondeurs\",Actions:[{Type:\"OPEN_NAMED_DIALOG\",Cmd:\"suite\"}]},"
            + "{Name:\"Au revoir.\",Label:\"bye\",Type:\"CLOSE\"}]},"
            + "{Name:\"Suite\",Label:\"suite\",Texts:[{Text:\"Voyez le contremaître sur les quais. Dites-lui que je vous envoie.\"}],"
            + "Buttons:[{Name:\"Merci.\",Label:\"merci\",Type:\"CLOSE\"}]}]},"
            + "ActionData:{ActionEventSet:{ON_INTERACTION:[{Type:\"OPEN_DEFAULT_DIALOG\"}]}}}";

    private static final String NBT_MAGISTRAT = "{CustomName:\"" + MAGISTRAT + "\",Tags:[\"hc_dialogue\"],Rotation:[180f,0f],"
            + "DialogData:{Type:\"STANDARD\",DialogDataSet:["
            + "{Name:\"Papiers\",Label:\"default\",Texts:[{Text:\"Vos papiers ne sont pas en règle.\"}],"
            + "Buttons:[{Name:\"Payer l'amende.\",Label:\"amende\","
            + "Conditions:[{Type:\"SCOREBOARD\",Name:\"hcd_test\",Operation:\"GREATER_THAN_OR_EQUALS\",Value:1}],"
            + "Actions:[{Type:\"CLOSE_DIALOG\"}]},"
            + "{Name:\"Partir.\",Label:\"partir\",Type:\"CLOSE\"}]}]},"
            + "ActionData:{ActionEventSet:{ON_INTERACTION:[{Type:\"OPEN_DEFAULT_DIALOG\"}]}}}";

    /**
     * La grammaire exacte des quêtes du datapack : {@code tellraw @s [Nom] …},
     * puis des lignes {@code [Choix]} cliquables qui posent un {@code /trigger}.
     * Exécuté en tant que joueur, avec l'élévation de permission d'Easy NPC.
     */
    private static final String NBT_ROCH = "{CustomName:\"" + ROCH + "\",Tags:[\"hc_dialogue\"],Rotation:[180f,0f],"
            + "ActionData:{ActionPermissionLevel:2,ActionEventSet:{ON_INTERACTION:["
            + "{Type:\"COMMAND\",Cmd:'scoreboard players enable @s hcd_choix',PermLevel:2,ExecAsUser:1b},"
            + "{Type:\"COMMAND\",Cmd:'tellraw @s [{\"text\":\"[Roch de test]\",\"color\":\"yellow\"},"
            + "{\"text\":\" Vous voilà enfin. Le port manque de bras, et les quais de courage.\",\"color\":\"white\"}]',PermLevel:2,ExecAsUser:1b},"
            + "{Type:\"COMMAND\",Cmd:'tellraw @s [{\"text\":\"[Choix] \",\"color\":\"gray\"},"
            + "{\"text\":\"[Je vous suis.]\",\"color\":\"green\",\"click_event\":{\"action\":\"run_command\",\"command\":\"/trigger hcd_choix set 1\"}}]',PermLevel:2,ExecAsUser:1b},"
            + "{Type:\"COMMAND\",Cmd:'tellraw @s [{\"text\":\"[Choix] \",\"color\":\"gray\"},"
            + "{\"text\":\"[Pas maintenant.]\",\"color\":\"red\",\"click_event\":{\"action\":\"run_command\",\"command\":\"/trigger hcd_choix set 9\"}}]',PermLevel:2,ExecAsUser:1b}"
            + "]}}}";

    /** Une seule réplique, aucune réponse : la conversation doit se refermer d'elle-même. */
    private static final String NBT_CRIEUR = "{CustomName:\"" + CRIEUR + "\",Tags:[\"hc_dialogue\"],Rotation:[180f,0f],"
            + "ActionData:{ActionPermissionLevel:2,ActionEventSet:{ON_INTERACTION:["
            + "{Type:\"COMMAND\",Cmd:'tellraw @s [{\"text\":\"[Crieur de test]\",\"color\":\"yellow\"},"
            + "{\"text\":\" Oyez, oyez ! Le marché ouvre au lever du jour.\",\"color\":\"white\"}]',PermLevel:2,ExecAsUser:1b}"
            + "]}}}";

    private static int tick = -1;
    private static boolean termine;
    private static final List<String> resultats = new ArrayList<>();
    private static Vec3d cameraDialogue;

    private DialogueSelfTest() {
    }

    public static boolean demande() {
        return System.getProperty(PROPRIETE) != null;
    }

    public static void register() {
        ClientTickEvents.END_CLIENT_TICK.register(DialogueSelfTest::onTick);
        HauteCapitaleDialogue.LOGGER.info("Dialogue : autotest en jeu armé (-D{}).", PROPRIETE);
    }

    private static void onTick(MinecraftClient client) {
        if (termine || client.player == null || client.world == null || client.getServer() == null) {
            return;
        }
        tick++;
        try {
            etape(client);
        } catch (Throwable t) {
            noter(false, "exception au tick " + tick, t.toString());
            conclure(client);
        }
    }

    private static void etape(MinecraftClient client) {
        String nom = client.player.getNameForScoreboard();
        if (tick < deplacementJusqua) {
            commande(client, String.format(Locale.ROOT, "execute as @e[name=\"%s\",limit=1] at @s run tp @s ~%.2f ~ ~",
                    PASSEUR, deplacementPas));
        }
        switch (tick) {
            case 20 -> preparer(client);
            case 60 -> poser(client);
            // --- 1. dialogue standard --------------------------------------
            case 110 -> interagir(client, CAPITAINE);
            case 140 -> mesurerOuverture(client, "dialogue standard ouvert");
            case 142 -> choisir(client, 0);
            case 170 -> mesurerNoeud(client, "transition de nœud (suite)", "suite");
            case 172 -> choisir(client, 0);
            case 200 -> mesurerFermeture(client, "fermeture par une réponse de congé", CloseReason.ADIEU);
            // --- 2. première personne ---------------------------------------
            case 205 -> client.options.setPerspective(Perspective.FIRST_PERSON);
            case 210 -> interagir(client, CAPITAINE);
            case 240 -> {
                boolean forcee = client.options.getPerspective() == Perspective.THIRD_PERSON_BACK
                        && DialogueClientState.perspectiveForcee();
                noter(forcee && DialogueClientState.etat() == DialogueClientState.Etat.DIALOGUE_ACTIVE,
                        "première personne → troisième forcée pendant le dialogue",
                        "perspective " + client.options.getPerspective() + ", état " + DialogueClientState.etat());
            }
            case 242 -> fermerEcran(client);
            case 272 -> {
                boolean ok = client.options.getPerspective() == Perspective.FIRST_PERSON
                        && DialogueClientState.etat() == DialogueClientState.Etat.IDLE;
                noter(ok, "Échap : retour en première personne", "perspective " + client.options.getPerspective()
                        + ", état " + DialogueClientState.etat());
                client.options.setPerspective(Perspective.THIRD_PERSON_BACK);
            }
            // --- 3. cinématique ---------------------------------------------
            case 280 -> interagir(client, CAPITAINE);
            case 310 -> {
                noter(DialogueClientState.etat() == DialogueClientState.Etat.DIALOGUE_ACTIVE,
                        "dialogue rouvert avant cinématique", "état " + DialogueClientState.etat());
                CameraOverrideManager.enterCinematicMode("autotest");
            }
            case 325 -> {
                boolean ok = DialogueClientState.etat() == DialogueClientState.Etat.CINEMATIC_OVERRIDE
                        && client.currentScreen == null
                        && RpgCameraManager.etat() == CameraState.CINEMATIC_OVERRIDE
                        && !sessionServeur(client);
                noter(ok, "cinématique : dialogue suspendu, écran fermé, session serveur close",
                        "état " + DialogueClientState.etat() + ", caméra " + RpgCameraManager.etat()
                                + ", écran " + nomEcran(client) + ", session serveur " + sessionServeur(client));
                CameraOverrideManager.exitCinematicMode("autotest");
            }
            case 340 -> noter(DialogueClientState.etat() == DialogueClientState.Etat.IDLE
                            && RpgCameraManager.etat() == CameraState.GAMEPLAY_RPG,
                    "fin de cinématique : retour au gameplay",
                    "état " + DialogueClientState.etat() + ", caméra " + RpgCameraManager.etat());
            // --- 4. distance --------------------------------------------------
            case 350 -> interagir(client, CAPITAINE);
            case 380 -> {
                noter(DialogueClientState.etat() == DialogueClientState.Etat.DIALOGUE_ACTIVE,
                        "dialogue rouvert avant téléportation", "état " + DialogueClientState.etat());
                commande(client, "tp " + nom + " 0.5 200 -24.5");
            }
            case 410 -> {
                boolean ok = DialogueClientState.etat() == DialogueClientState.Etat.IDLE
                        && client.currentScreen == null && !sessionServeur(client)
                        && (DialogueClientState.derniereRaison() == CloseReason.DISTANCE
                        || DialogueClientState.derniereRaison() == CloseReason.TELEPORTATION);
                noter(ok, "joueur éloigné : session fermée par le serveur",
                        "état " + DialogueClientState.etat() + ", raison " + DialogueClientState.derniereRaison()
                                + ", écran " + nomEcran(client));
                commande(client, "tp " + nom + " 0.5 200 -2.5 0 0");
            }
            // --- 5. conditions ------------------------------------------------
            case 440 -> interagir(client, MAGISTRAT);
            case 470 -> {
                RpgDialogueScreen ecran = ecranEasy(client);
                boolean ok = ecran != null && ecran.reponsesVisibles().size() == 2
                        && ecran.reponsesVisibles().get(0).verrouillee
                        && !ecran.reponsesVisibles().get(1).verrouillee;
                noter(ok, "condition de score non remplie : réponse grisée",
                        ecran == null ? "aucun écran" : decrire(ecran));
            }
            case 472 -> fermerEcran(client);
            case 500 -> commande(client, "scoreboard players set " + nom + " hcd_test 1");
            case 520 -> interagir(client, MAGISTRAT);
            case 550 -> {
                RpgDialogueScreen ecran = ecranEasy(client);
                boolean ok = ecran != null && ecran.reponsesVisibles().size() == 2
                        && !ecran.reponsesVisibles().get(0).verrouillee;
                noter(ok, "condition de score remplie : réponse disponible",
                        ecran == null ? "aucun écran" : decrire(ecran));
            }
            case 552 -> choisir(client, 0);
            case 582 -> mesurerFermeture(client, "action CLOSE_DIALOG côté serveur : fermeture propre", null);
            // --- 6. personnage disparu ---------------------------------------
            case 590 -> interagir(client, CAPITAINE);
            case 620 -> {
                noter(DialogueClientState.etat() == DialogueClientState.Etat.DIALOGUE_ACTIVE,
                        "dialogue rouvert avant disparition", "état " + DialogueClientState.etat());
                commande(client, "kill @e[type=easy_npc:humanoid,name=\"" + CAPITAINE + "\"]");
            }
            case 650 -> {
                boolean ok = DialogueClientState.etat() == DialogueClientState.Etat.IDLE
                        && !sessionServeur(client) && client.currentScreen == null;
                noter(ok, "personnage tué : session fermée",
                        "état " + DialogueClientState.etat() + ", raison " + DialogueClientState.derniereRaison());
            }
            // --- 7. capture d'un dialogue de chat (grammaire du datapack) ----
            case 660 -> {
                commande(client, "scoreboard objectives add hcd_choix trigger");
                commande(client, "summon easy_npc:humanoid 0.5 200 0.5 " + NBT_ROCH);
                commande(client, "summon easy_npc:humanoid -2.5 200 0.5 " + NBT_CRIEUR);
            }
            case 700 -> interagir(client, ROCH);
            case 735 -> mesurerOuverture(client, "capture : dialogue de chat ouvert (caméra, HUD, session)");
            case 736 -> mesurerCapture(client, "capture : réplique [Nom] et deux lignes [Choix] reçues", 1, 2, "voilà");
            case 738 -> choisir(client, 0);
            case 760 -> {
                int score = score(client, "hcd_choix");
                noter(score == 1, "capture : le choix a rejoué son /trigger (score attendu 1)", "score " + score);
                // Ce que ferait core/tick.mcfunction : la suite, en reponse au trigger.
                commande(client, "scoreboard players enable " + nom + " hcd_choix");
                commande(client, "tellraw " + nom + " [{\"text\":\"[Roch de test]\",\"color\":\"yellow\"},"
                        + "{\"text\":\" Bien. Voyez le contremaître sur les quais, dites-lui que je vous envoie.\",\"color\":\"white\"}]");
                commande(client, "tellraw " + nom + " [{\"text\":\"[Choix] \",\"color\":\"gray\"},"
                        + "{\"text\":\"[Au revoir.]\",\"color\":\"gray\",\"click_event\":{\"action\":\"run_command\",\"command\":\"/trigger hcd_choix set 2\"}}]");
            }
            case 790 -> {
                mesurerCapture(client, "capture : la salve suivante remplace la page", 1, 1, "contremaître");
                double bouge = cameraDialogue == null ? -1.0
                        : client.gameRenderer.getCamera().getCameraPos().distanceTo(cameraDialogue);
                noter(bouge >= 0.0 && bouge < 0.35 && DialogueClientState.etat() == DialogueClientState.Etat.DIALOGUE_ACTIVE,
                        "capture : la caméra ne bouge pas entre deux salves",
                        String.format(Locale.ROOT, "déplacée de %.2f, état %s", bouge, DialogueClientState.etat()));
            }
            case 792 -> choisir(client, 0);
            case 825 -> {
                mesurerFermeture(client, "capture : réponse de congé (« Au revoir. »)", CloseReason.ADIEU);
                int score = score(client, "hcd_choix");
                noter(score == 2, "capture : le /trigger du congé est parti (score attendu 2)", "score " + score);
            }
            // --- 8. silence : l'écran attend le joueur ---------------------------
            case 835 -> interagir(client, CRIEUR);
            case 870 -> mesurerCapture(client, "capture : réplique sans réponse", 1, 0, "Oyez");
            // Six secondes de silence, bien au-dela de l'ancien delai : le texte
            // est toujours la, l'ecran aussi. C'est Echap qui rend la main.
            case 970 -> mesurerCapture(client,
                    "capture : silence → l'écran reste ouvert, le texte reste lisible", 1, 0, "Oyez");
            // Le joueur qui veut l'ancien comportement l'a toujours.
            case 975 -> DialogueClientSettings.get().fermeture_automatique = true;
            case 990 -> {
                mesurerFermeture(client, "capture : fermeture automatique demandée → le silence vaut congé",
                        CloseReason.INACTIVITE);
                DialogueClientSettings.get().fermeture_automatique = false;
            }
            // --- 9. messages étrangers --------------------------------------
            case 1000 -> interagir(client, ROCH);
            case 1035 -> mesurerCapture(client, "capture : réouverture du même personnage", 1, 2, "voilà");
            case 1037 -> {
                commande(client, "tellraw " + nom + " {\"text\":\"[Quête] Nouvelle quête : Les quais\"}");
                commande(client, "tellraw " + nom + " \"Un message ordinaire du serveur\"");
            }
            case 1060 -> mesurerCapture(client, "capture : [Quête] et message ordinaire laissés au chat", 1, 2, "voilà");
            case 1062 -> fermerEcran(client);
            case 1095 -> mesurerFermeture(client, "capture : Échap", CloseReason.ESC);
            // --- 10. entité non éligible --------------------------------------
            case 1100 -> commande(client, "summon minecraft:pig -3.5 200 0.5 {NoAI:1b,CustomName:\"" + COCHON + "\"}");
            case 1120 -> interagirEntite(client, COCHON);
            case 1150 -> {
                boolean ok = DialogueClientState.etat() == DialogueClientState.Etat.IDLE
                        && !sessionServeur(client) && client.currentScreen == null;
                noter(ok, "entité sans tag ni action : aucune session",
                        "état " + DialogueClientState.etat() + ", écran " + nomEcran(client)
                                + ", session serveur " + sessionServeur(client));
            }
            // --- 11. véhicule : à l'arrêt, en route, à l'arrêt -----------------
            case 1160 -> {
                commande(client, "kill @e[type=easy_npc:humanoid]");
                commande(client, "summon easy_npc:humanoid 0.5 200 0.5 " + NBT_PASSEUR);
            }
            case 1200 -> interagir(client, PASSEUR);
            case 1235 -> {
                mesurerOuverture(client, "véhicule à l'arrêt : dialogue complet");
                noter(!DialogueClientState.simplifie(), "véhicule à l'arrêt : pas de drapeau simplifié",
                        "drapeaux " + SessionFlags.decrire(DialogueClientState.flags()));
            }
            case 1240 -> deplacer(0.1, 1280);
            case 1275 -> mesurerSimplifie(client, "véhicule en route : texte seul, caméra rendue au gameplay", true);
            case 1360 -> mesurerSimplifie(client, "véhicule à l'arrêt : cadrage repris en fondu", false);
            case 1362 -> fermerEcran(client);
            case 1395 -> mesurerFermeture(client, "véhicule : Échap", CloseReason.ESC);
            // --- 12. ouverture pendant le mouvement ----------------------------
            case 1400 -> {
                client.options.setPerspective(Perspective.FIRST_PERSON);
                deplacer(-0.1, 1480);
            }
            case 1410 -> interagir(client, PASSEUR);
            case 1450 -> {
                mesurerSimplifie(client, "ouverture en route : dialogue simplifié d'emblée", true);
                noter(client.options.getPerspective() == Perspective.FIRST_PERSON && !DialogueClientState.perspectiveForcee(),
                        "ouverture en route : perspective laissée en première personne",
                        "perspective " + client.options.getPerspective());
            }
            case 1452 -> fermerEcran(client);
            case 1485 -> {
                mesurerFermeture(client, "ouverture en route : Échap", CloseReason.ESC);
                client.options.setPerspective(Perspective.THIRD_PERSON_BACK);
            }
            // --- 13. politique « refuser », par la sonde d'un mod de transport --
            case 1490 -> {
                DialogueConfig.get().vehicule_en_mouvement = "refuser";
                sondeEnRoute = true;
                Dialogues.sondeMouvement(SONDE);
            }
            case 1500 -> interagir(client, PASSEUR);
            case 1530 -> {
                boolean ok = DialogueClientState.etat() == DialogueClientState.Etat.IDLE
                        && !sessionServeur(client) && client.currentScreen == null;
                noter(ok, "refuser : aucune conversation avec un véhicule en route",
                        "état " + DialogueClientState.etat() + ", écran " + nomEcran(client)
                                + ", session serveur " + sessionServeur(client));
                sondeEnRoute = false;
            }
            case 1540 -> interagir(client, PASSEUR);
            case 1575 -> {
                boolean ok = DialogueClientState.etat() == DialogueClientState.Etat.DIALOGUE_ACTIVE
                        && sessionServeur(client) && !DialogueClientState.simplifie() && ecran(client) != null;
                noter(ok, "refuser : conversation complète à quai",
                        "état " + DialogueClientState.etat() + ", drapeaux " + SessionFlags.decrire(DialogueClientState.flags()));
                sondeEnRoute = true;
            }
            case 1610 -> {
                mesurerFermeture(client, "refuser : le véhicule part → conversation fermée", CloseReason.MOUVEMENT);
                Dialogues.retirerSondeMouvement(SONDE);
                DialogueConfig.get().vehicule_en_mouvement = "simplifie";
            }
            case 1620 -> {
                commande(client, "kill @e[type=easy_npc:humanoid]");
                commande(client, "summon easy_npc:humanoid 0.5 200 0.5 " + NBT_CAPITAINE);
                commande(client, "summon easy_npc:humanoid 3.5 200 0.5 " + NBT_MARCHAND);
                commande(client, "summon easy_npc:humanoid -2.5 200 0.5 " + NBT_ROCH);
            }
            // --- 14. retour au dialogue après un commerce -----------------------
            case 1660 -> interagir(client, MARCHAND);
            case 1695 -> mesurerOuverture(client, "commerce : dialogue du marchand ouvert");
            case 1697 -> choisir(client, 0);
            case 1730 -> {
                boolean ok = client.currentScreen instanceof MerchantScreen
                        && DialogueClientState.etat() == DialogueClientState.Etat.DIALOGUE_ACTIVE
                        && sessionServeur(client) && CameraFocus.estDemande();
                noter(ok, "commerce : écran de commerce ouvert, conversation et caméra maintenues",
                        "écran " + nomEcran(client) + ", état " + DialogueClientState.etat()
                                + ", session serveur " + sessionServeur(client) + ", prise " + CameraFocus.estDemande());
            }
            case 1732 -> {
                if (client.currentScreen instanceof MerchantScreen) {
                    client.currentScreen.close();
                } else {
                    noter(false, "fermer le commerce sans écran de commerce", nomEcran(client));
                }
            }
            case 1765 -> {
                RpgDialogueScreen ecran = ecranEasy(client);
                boolean ok = ecran != null && "default".equals(ecran.libelleDialogue())
                        && DialogueClientState.etat() == DialogueClientState.Etat.DIALOGUE_ACTIVE
                        && sessionServeur(client) && CameraFocus.melange() > 0.99;
                noter(ok, "commerce : retour au dialogue, au même nœud, quand le commerce se ferme",
                        "écran " + nomEcran(client) + ", dialogue " + (ecran == null ? "-" : ecran.libelleDialogue())
                                + ", état " + DialogueClientState.etat() + ", session serveur " + sessionServeur(client));
            }
            case 1767 -> fermerEcran(client);
            case 1800 -> mesurerFermeture(client, "commerce : Échap", CloseReason.ESC);
            // --- 15. reprise après cinématique, dialogue à menu ------------------
            case 1805 -> DialogueConfig.get().reprise_apres_cinematique = true;
            case 1810 -> interagir(client, CAPITAINE);
            case 1845 -> mesurerOuverture(client, "reprise : dialogue ouvert");
            case 1847 -> choisir(client, 0);
            case 1875 -> {
                mesurerNoeud(client, "reprise : au nœud « suite » avant la cinématique", "suite");
                CameraOverrideManager.enterCinematicMode("autotest");
            }
            case 1890 -> {
                boolean ok = DialogueClientState.etat() == DialogueClientState.Etat.CINEMATIC_OVERRIDE
                        && !sessionServeur(client) && client.currentScreen == null;
                noter(ok, "reprise : cinématique, conversation suspendue",
                        "état " + DialogueClientState.etat() + ", session serveur " + sessionServeur(client));
                CameraOverrideManager.exitCinematicMode("autotest");
            }
            case 1930 -> {
                RpgDialogueScreen ecran = ecranEasy(client);
                boolean ok = ecran != null && "suite".equals(ecran.libelleDialogue())
                        && DialogueClientState.etat() == DialogueClientState.Etat.DIALOGUE_ACTIVE
                        && sessionServeur(client) && CameraFocus.melange() > 0.99;
                noter(ok, "reprise : dialogue rouvert au même nœud après la cinématique",
                        "écran " + nomEcran(client) + ", dialogue " + (ecran == null ? "-" : ecran.libelleDialogue())
                                + ", état " + DialogueClientState.etat() + ", session serveur " + sessionServeur(client)
                                + ", mélange " + String.format(Locale.ROOT, "%.2f", CameraFocus.melange()));
            }
            case 1932 -> fermerEcran(client);
            case 1965 -> mesurerFermeture(client, "reprise : Échap", CloseReason.ESC);
            // --- 16. reprise après cinématique, dialogue capturé -----------------
            case 1970 -> interagir(client, ROCH);
            case 2005 -> mesurerCapture(client, "reprise capture : ouverte", 1, 2, "voilà");
            case 2007 -> CameraOverrideManager.enterCinematicMode("autotest");
            case 2022 -> CameraOverrideManager.exitCinematicMode("autotest");
            case 2062 -> mesurerCapture(client, "reprise capture : conversation rejouée après la cinématique", 1, 2, "voilà");
            case 2064 -> fermerEcran(client);
            case 2095 -> {
                mesurerFermeture(client, "reprise capture : Échap", CloseReason.ESC);
                DialogueConfig.get().reprise_apres_cinematique = false;
            }
            // --- 17. styles par étiquette, historique ------------------------
            case 2100 -> {
                commande(client, "kill @e[type=easy_npc:humanoid]");
                commande(client, "summon easy_npc:humanoid 0.5 200 0.5 " + NBT_STYLISTE);
            }
            case 2140 -> interagir(client, STYLISTE);
            case 2175 -> {
                RpgDialogueScreen ecran = ecranEasy(client);
                List<RpgDialogueScreen.Reponse> r = ecran == null ? List.of() : ecran.reponsesVisibles();
                boolean ok = r.size() == 4
                        && r.get(0).type == DialoguePanel.Type.QUETE && "Accepter la tâche.".equals(r.get(0).libelle.getString())
                        && r.get(1).type == DialoguePanel.Type.COMMERCE && "Voir l'étal.".equals(r.get(1).libelle.getString())
                        && r.get(2).type == DialoguePanel.Type.DANGER
                        && r.get(3).type == DialoguePanel.Type.ADIEU;
                noter(ok, "styles : étiquettes [quête] [commerce] [danger] lues et retirées",
                        ecran == null ? "aucun écran" : decrire(ecran) + " types "
                                + r.stream().map(x -> x.type.name()).collect(Collectors.joining(",")));
                List<String> h = DialogueClientState.historique();
                noter(h.size() == 1 && h.get(0).contains("Que puis-je"), "historique : la réplique est notée", String.join(" | ", h));
            }
            case 2177 -> choisir(client, 0);
            case 2210 -> {
                List<String> h = DialogueClientState.historique();
                boolean ok = h.size() == 3 && h.get(1).equals("› Accepter la tâche.") && h.get(2).contains("Revenez");
                noter(ok, "historique : réponse puis réplique suivante notées", String.join(" | ", h));
            }
            case 2212 -> fermerEcran(client);
            case 2245 -> {
                mesurerFermeture(client, "styles : Échap", CloseReason.ESC);
                noter(DialogueClientState.historique().isEmpty(), "historique : vidé à la fin de la conversation", "");
            }
            // --- 18. panneau seul --------------------------------------------
            case 2250 -> DialogueConfig.get().panneau_seul = true;
            case 2260 -> interagir(client, STYLISTE);
            case 2295 -> mesurerSimplifie(client, "panneau seul : texte et réponses sans caméra", true);
            case 2297 -> fermerEcran(client);
            case 2330 -> {
                mesurerFermeture(client, "panneau seul : Échap", CloseReason.ESC);
                DialogueConfig.get().panneau_seul = false;
            }
            // --- 19. outillage admin : surcharge posée en regardant le PNJ ------
            case 2340 -> commande(client, "execute as " + nom + " at @s run dialogue pnj set exclusif on");
            case 2350 -> {
                Entity e = trouver(client, STYLISTE, true);
                DialogueConfig.Pnj p = e == null ? null : DialogueConfig.get().surchargeDe(e.getUuid());
                noter(p != null && Boolean.TRUE.equals(p.exclusif), "admin : /dialogue pnj set exclusif on posé sur le PNJ visé",
                        p == null ? "aucune surcharge" : DialogueConfig.decrire(p));
            }
            case 2352 -> commande(client, "execute as " + nom + " at @s run dialogue pnj oublier");
            case 2362 -> {
                Entity e = trouver(client, STYLISTE, true);
                noter(e != null && DialogueConfig.get().surchargeDe(e.getUuid()) == null,
                        "admin : /dialogue pnj oublier retire la surcharge", "");
            }
            // --- 20. cadrage d'épaule : collé, éloigné, contre un mur -----------
            case 2370 -> ancrage = "0.5 200 -0.5 0 0";
            case 2380 -> interagir(client, STYLISTE);
            case 2415 -> mesurerOuverture(client, "cadrage collé (1 bloc) : joueur à gauche, personnage à droite du centre");
            case 2417 -> fermerEcran(client);
            case 2440 -> ancrage = "0.5 200 -5.0 0 0";
            case 2450 -> interagir(client, STYLISTE);
            case 2485 -> mesurerOuverture(client, "cadrage éloigné (5,5 blocs) : le joueur reste dans le cadre");
            case 2487 -> fermerEcran(client);
            case 2510 -> {
                ancrage = "0.5 200 -2.5 0 0";
                // Un mur a droite du joueur (−x), la ou la camera d'epaule droite voudrait aller.
                commande(client, "fill -1 200 -5 -1 203 1 minecraft:stone");
            }
            case 2520 -> interagir(client, STYLISTE);
            case 2555 -> {
                mesurerOuverture(client, "cadrage contre un mur : caméra hors bloc, joueur toujours cadré");
                DialogueCameraController c = DialogueClientState.controleur();
                noter(c != null && (c.candidatRetenu() != 0 || c.dernierRepli()),
                        "cadrage contre un mur : l'épaule droite bloquée, autre candidat retenu",
                        c == null ? "aucun contrôleur" : "candidat " + c.candidatRetenu() + (c.dernierRepli() ? " (repli)" : ""));
            }
            case 2557 -> {
                fermerEcran(client);
                commande(client, "fill -1 200 -5 -1 203 1 minecraft:air");
            }
            case 2590 -> mesurerFermeture(client, "cadrage : Échap", CloseReason.ESC);
            case 2600 -> conclure(client);
            default -> {
            }
        }
    }

    // ------------------------------------------------------------------------
    // Mise en place
    // ------------------------------------------------------------------------

    private static void preparer(MinecraftClient client) {
        String nom = client.player.getNameForScoreboard();
        // La fenetre de test n'a pas le focus : sans cela, le menu pause s'ouvre
        // des que notre ecran se ferme et fausse chaque mesure « aucun ecran ».
        client.options.pauseOnLostFocus = false;
        // Et elle est reduite : tant qu'elle est visible et active, elle capture
        // la souris et le clavier de la personne qui utilise la machine — ses
        // clics rouvraient des dialogues et ses touches deplacaient le joueur.
        // Cachee plutot que reduite : une fenetre reduite reste dans la barre des
        // taches, et quelqu'un l'a rouverte pour cliquer dedans. Minecraft
        // continue de rendre et de simuler une fenetre cachee.
        GLFW.glfwHideWindow(client.getWindow().getHandle());
        client.options.setPerspective(Perspective.THIRD_PERSON_BACK);
        // Le scenario mesure le comportement livre, quoi qu'il y ait dans le
        // fichier de reglages de la machine qui fait tourner le banc.
        DialogueClientSettings.get().fermeture_automatique = false;
        commande(client, "gamemode creative " + nom);
        commande(client, "fill -8 199 -30 8 206 8 minecraft:air replace");
        commande(client, "fill -8 199 -30 8 199 8 minecraft:stone replace");
        commande(client, "kill @e[type=easy_npc:humanoid]");
        commande(client, "kill @e[type=minecraft:pig]");
        commande(client, "scoreboard objectives add hcd_test dummy");
        commande(client, "scoreboard players reset " + nom + " hcd_test");
        commande(client, "scoreboard objectives remove hcd_choix");
        commande(client, "tp " + nom + " 0.5 200 -2.5 0 0");
    }

    private static void poser(MinecraftClient client) {
        commande(client, "summon easy_npc:humanoid 0.5 200 0.5 " + NBT_CAPITAINE);
        commande(client, "summon easy_npc:humanoid 3.5 200 0.5 " + NBT_MAGISTRAT);
    }

    private static void commande(MinecraftClient client, String commande) {
        MinecraftServer serveur = client.getServer();
        if (serveur == null) {
            return;
        }
        serveur.execute(() -> {
            try {
                serveur.getCommandManager().parseAndExecute(serveur.getCommandSource(), commande);
            } catch (Exception e) {
                HauteCapitaleDialogue.LOGGER.warn("Autotest : commande en échec « {} » : {}", commande, e.toString());
            }
        });
    }

    // ------------------------------------------------------------------------
    // Actions
    // ------------------------------------------------------------------------

    /** Le vrai chemin d'interaction : paquet d'interaction, Easy NPC, notre menu ou nos commandes. */
    private static void interagir(MinecraftClient client, String nom) {
        interagir(client, nom, true);
    }

    /** Même chemin, pour une entité qui n'est pas un Easy NPC. */
    private static void interagirEntite(MinecraftClient client, String nom) {
        interagir(client, nom, false);
    }

    private static void interagir(MinecraftClient client, String nom, boolean easyNpc) {
        Entity cible = trouver(client, nom, easyNpc);
        if (cible == null) {
            noter(false, "entité « " + nom + " » introuvable côté client", "");
            return;
        }
        // Le joueur est remis a sa place juste avant : la tache de teleportation
        // est dans la file du serveur avant le paquet d'interaction, et la
        // distance d'ouverture est mesuree cote serveur.
        commande(client, "tp " + client.player.getNameForScoreboard() + " " + ancrage);
        client.interactionManager.interactEntity(client.player, cible, Hand.MAIN_HAND);
    }

    /** Où le joueur est remis avant chaque clic ; les scénarios de cadrage le déplacent. */
    private static String ancrage = "0.5 200 -2.5 0 0";

    private static Entity trouver(MinecraftClient client, String nom, boolean easyNpc) {
        for (Entity e : client.world.getEntities()) {
            if ((!easyNpc || e instanceof EasyNPC<?>) && e != client.player && e.isAlive()
                    && nom.equals(e.getName().getString())) {
                return e;
            }
        }
        return null;
    }

    private static EcranDialogue ecran(MinecraftClient client) {
        return client.currentScreen instanceof EcranDialogue e ? e : null;
    }

    private static RpgDialogueScreen ecranEasy(MinecraftClient client) {
        return client.currentScreen instanceof RpgDialogueScreen e ? e : null;
    }

    private static void choisir(MinecraftClient client, int index) {
        EcranDialogue e = ecran(client);
        if (e == null) {
            noter(false, "choisir la réponse " + index + " sans écran de dialogue", nomEcran(client));
            return;
        }
        e.choisir(index);
    }

    private static void fermerEcran(MinecraftClient client) {
        if (client.currentScreen == null || !(client.currentScreen instanceof EcranDialogue)) {
            noter(false, "fermer (Échap) sans écran de dialogue", nomEcran(client));
            return;
        }
        client.currentScreen.close();
    }

    // ------------------------------------------------------------------------
    // Mesures
    // ------------------------------------------------------------------------

    private static void mesurerOuverture(MinecraftClient client, String etape) {
        boolean actif = DialogueClientState.etat() == DialogueClientState.Etat.DIALOGUE_ACTIVE;
        EcranDialogue ecran = ecran(client);
        DialogueCameraController c = DialogueClientState.controleur();
        Camera camera = client.gameRenderer.getCamera();

        String detail;
        boolean ok = actif && ecran != null && c != null && sessionServeur(client) && HudMask.actif();
        if (c != null) {
            Vec3d cam = camera.getCameraPos();
            Vec3d cible = c.derniereCible();
            Vec3d yeux = client.player.getEyePos();
            Vector3fc avantF = camera.getHorizontalPlane();
            Vec3d avant = new Vec3d(avantF.x(), 0.0, avantF.z()).normalize();
            // Plan d'epaule : le personnage un peu d'un cote du centre, le joueur de
            // l'autre, tous deux dans l'image ; la camera a moins de 2,6 blocs des
            // yeux et hors de tout bloc. En repli (mur), la camera est aux yeux.
            double angleNpc = angleHorizontal(avant, cam, cible);
            double angleJoueur = angleHorizontal(avant, cam, yeux);
            double distanceYeux = cam.distanceTo(yeux);
            boolean dehors = client.world.getBlockState(BlockPos.ofFloored(cam)).isAir();
            boolean repli = c.dernierRepli();
            boolean cadrage = repli
                    ? distanceYeux < 0.05
                    : Math.abs(angleNpc) >= 2.0 && Math.abs(angleNpc) <= 18.0
                            && Math.abs(angleJoueur) >= 2.0 && Math.abs(angleJoueur) <= 45.0
                            && Math.signum(angleNpc) != Math.signum(angleJoueur)
                            && distanceYeux >= 1.0 && distanceYeux <= 2.6;
            ok &= cadrage && dehors;
            cameraDialogue = cam;
            detail = String.format(Locale.ROOT, "état %s, écran %s, personnage %+.1f°, joueur %+.1f°, caméra-yeux %.2f, %s%s, session serveur %s, HUD masqué %s",
                    DialogueClientState.etat(), nomEcran(client), angleNpc, angleJoueur, distanceYeux,
                    dehors ? "hors bloc" : "DANS UN BLOC", repli ? ", REPLI aux yeux" : "", sessionServeur(client), HudMask.actif());
        } else {
            detail = "état " + DialogueClientState.etat() + ", écran " + nomEcran(client) + ", aucun contrôleur de caméra";
        }
        noter(ok, etape, detail);
    }

    /** Angle horizontal signé d'un point sur l'image : positif à gauche du centre, négatif à droite. */
    private static double angleHorizontal(Vec3d avant, Vec3d camera, Vec3d point) {
        Vec3d vers = new Vec3d(point.x - camera.x, 0.0, point.z - camera.z);
        if (vers.lengthSquared() < 1.0e-6) {
            return 0.0;
        }
        vers = vers.normalize();
        double croix = avant.z * vers.x - avant.x * vers.z;
        double produit = avant.x * vers.x + avant.z * vers.z;
        return Math.toDegrees(Math.atan2(croix, produit));
    }

    private static void mesurerNoeud(MinecraftClient client, String etape, String label) {
        EcranDialogue ecran = ecran(client);
        boolean ok = ecran != null && label.equals(ecran.libelleDialogue())
                && DialogueClientState.etat() == DialogueClientState.Etat.DIALOGUE_ACTIVE;
        double bouge = cameraDialogue == null ? -1.0 : client.gameRenderer.getCamera().getCameraPos().distanceTo(cameraDialogue);
        ok &= bouge >= 0.0 && bouge < 0.35;
        noter(ok, etape, String.format(Locale.ROOT, "écran %s, dialogue %s, état %s, caméra déplacée de %.2f",
                nomEcran(client), ecran == null ? "-" : ecran.libelleDialogue(), DialogueClientState.etat(), bouge));
    }

    /** L'écran de capture montre-t-il ce que le datapack a dit ? */
    private static void mesurerCapture(MinecraftClient client, String etape, int repliquesAttendues,
                                       int choixAttendus, String contient) {
        CaptureDialogueScreen ecran = client.currentScreen instanceof CaptureDialogueScreen e ? e : null;
        List<Text> repliques = DialogueClientState.repliquesCapturees();
        String texte = repliques.stream().map(Text::getString).collect(Collectors.joining(" | "));
        String choix = DialogueClientState.choixCaptures().stream()
                .map(x -> x.libelle().getString() + "/" + x.type()).collect(Collectors.joining(", "));
        boolean ok = ecran != null
                && DialogueClientState.mode() == DialogueMode.CAPTURE
                && DialogueClientState.etat() == DialogueClientState.Etat.DIALOGUE_ACTIVE
                && sessionServeur(client)
                && repliques.size() == repliquesAttendues
                && DialogueClientState.choixCaptures().size() == choixAttendus
                && (contient == null || texte.contains(contient));
        noter(ok, etape, String.format(Locale.ROOT, "écran %s, mode %s, état %s, session serveur %s, %d réplique(s) « %s », %d choix [%s]",
                nomEcran(client), DialogueClientState.mode(), DialogueClientState.etat(), sessionServeur(client),
                repliques.size(), texte, DialogueClientState.choixCaptures().size(), choix));
    }

    /** Le personnage se déplace d'un pas par tick jusqu'au tick donné. */
    private static void deplacer(double pas, int jusqua) {
        deplacementPas = pas;
        deplacementJusqua = jusqua;
    }

    /** En route : texte seul, caméra de gameplay. À l'arrêt : cadrage repris. */
    private static void mesurerSimplifie(MinecraftClient client, String etape, boolean attendu) {
        boolean simplifie = DialogueClientState.simplifie();
        boolean base = DialogueClientState.etat() == DialogueClientState.Etat.DIALOGUE_ACTIVE
                && ecran(client) != null && sessionServeur(client) && simplifie == attendu;
        boolean camera = attendu
                ? !CameraFocus.estEngage() && RpgCameraManager.etat() == CameraState.GAMEPLAY_RPG
                        && DialogueClientState.controleur() == null
                : CameraFocus.estDemande() && RpgCameraManager.etat() == CameraState.NPC_DIALOGUE
                        && CameraFocus.melange() > 0.99;
        noter(base && camera, etape, String.format(Locale.ROOT,
                "état %s, écran %s, session serveur %s, drapeaux %s, prise %s, caméra %s, mélange %.2f",
                DialogueClientState.etat(), nomEcran(client), sessionServeur(client),
                SessionFlags.decrire(DialogueClientState.flags()), CameraFocus.estEngage(),
                RpgCameraManager.etat(), CameraFocus.melange()));
    }

    private static void mesurerFermeture(MinecraftClient client, String etape, CloseReason attendue) {
        boolean ok = DialogueClientState.etat() == DialogueClientState.Etat.IDLE
                && client.currentScreen == null
                && !sessionServeur(client)
                && !CameraFocus.estEngage()
                && RpgCameraManager.etat() == CameraState.GAMEPLAY_RPG
                && !HudMask.actif()
                && (attendue == null || DialogueClientState.derniereRaison() == attendue);
        noter(ok, etape, String.format(Locale.ROOT, "état %s, écran %s, session serveur %s, prise %s, caméra %s, raison %s",
                DialogueClientState.etat(), nomEcran(client), sessionServeur(client), CameraFocus.estEngage(),
                RpgCameraManager.etat(), DialogueClientState.derniereRaison()));
    }

    private static boolean sessionServeur(MinecraftClient client) {
        MinecraftServer serveur = client.getServer();
        if (serveur == null) {
            return false;
        }
        ServerPlayerEntity joueur = serveur.getPlayerManager().getPlayer(client.player.getUuid());
        return joueur != null && DialogueManager.session(joueur).isPresent();
    }

    /** Le score du joueur côté serveur, ou {@code Integer.MIN_VALUE} s'il n'y en a pas. */
    private static int score(MinecraftClient client, String objectif) {
        MinecraftServer serveur = client.getServer();
        if (serveur == null) {
            return Integer.MIN_VALUE;
        }
        ScoreboardObjective obj = serveur.getScoreboard().getNullableObjective(objectif);
        if (obj == null) {
            return Integer.MIN_VALUE;
        }
        ReadableScoreboardScore s = serveur.getScoreboard().getScore(
                ScoreHolder.fromName(client.player.getNameForScoreboard()), obj);
        return s == null ? Integer.MIN_VALUE : s.getScore();
    }

    private static String nomEcran(MinecraftClient client) {
        return client.currentScreen == null ? "aucun" : client.currentScreen.getClass().getSimpleName();
    }

    private static String decrire(RpgDialogueScreen ecran) {
        StringBuilder sb = new StringBuilder("dialogue ").append(ecran.libelleDialogue()).append(" :");
        for (RpgDialogueScreen.Reponse r : ecran.reponsesVisibles()) {
            sb.append(" [").append(r.libelle.getString()).append(r.verrouillee ? " 🔒" : "").append("]");
        }
        return sb.toString();
    }

    private static void noter(boolean ok, String etape, String detail) {
        String ligne = String.format(Locale.ROOT, "    %s  %-58s  %s", ok ? "ok   " : "ECHEC", etape, detail);
        resultats.add(ligne);
        HauteCapitaleDialogue.LOGGER.info(ligne);
    }

    private static void conclure(MinecraftClient client) {
        if (termine) {
            return;
        }
        termine = true;
        long echecs = resultats.stream().filter(l -> l.contains("ECHEC")).count();
        HauteCapitaleDialogue.LOGGER.info("");
        HauteCapitaleDialogue.LOGGER.info("  === AUTOTEST DIALOGUE RPG ===");
        resultats.forEach(HauteCapitaleDialogue.LOGGER::info);
        HauteCapitaleDialogue.LOGGER.info("  === {} mesures, {} échecs ===", resultats.size(), echecs);
        HauteCapitaleDialogue.LOGGER.info("");
        client.scheduleStop();
    }
}
