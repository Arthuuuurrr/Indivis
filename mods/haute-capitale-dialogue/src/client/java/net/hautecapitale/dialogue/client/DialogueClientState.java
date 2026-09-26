package net.hautecapitale.dialogue.client;

import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.hautecapitale.dialogue.HauteCapitaleDialogue;
import net.hautecapitale.dialogue.client.capture.CaptureDialogueScreen;
import net.hautecapitale.dialogue.client.capture.MessageCapture;
import net.hautecapitale.dialogue.client.screen.EcranDialogue;
import net.hautecapitale.dialogue.network.DialogueNetwork;
import net.hautecapitale.dialogue.session.CameraProfile;
import net.hautecapitale.dialogue.session.CloseReason;
import net.hautecapitale.dialogue.session.DialogueMode;
import net.hautecapitale.dialogue.session.SessionFlags;
import net.hautecapitale.rpg.client.camera.CameraFocus;
import net.hautecapitale.rpg.client.camera.CameraOverrideManager;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.option.Perspective;
import net.minecraft.entity.Entity;
import net.minecraft.text.Text;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.function.BooleanSupplier;
import java.util.function.IntSupplier;

/**
 * La machine à états du dialogue, côté client.
 *
 * <p>Une seule conversation à la fois, six états et aucun booléen contradictoire :
 * {@code IDLE → ENTERING_DIALOGUE → DIALOGUE_ACTIVE ⇄ TRANSITIONING_NODE → EXITING_DIALOGUE → IDLE},
 * plus {@code CINEMATIC_OVERRIDE} quand une cinématique prend la caméra.
 *
 * <p>Le serveur ouvre et ferme ; le client demande. Tout ce qui est modifié ici
 * — caméra, perspective, HUD, orientation du joueur — est restauré à la sortie,
 * et jamais rien de ce qui appartient à Bosses'Rise.
 *
 * <p>En mode <i>capture</i>, l'état tient aussi la conversation elle-même :
 * les répliques et les réponses arrivées par le chat, et les deux horloges qui
 * disent quand une salve est finie et quand le silence vaut congé.
 */
public final class DialogueClientState {

    public enum Etat {
        IDLE, ENTERING_DIALOGUE, DIALOGUE_ACTIVE, TRANSITIONING_NODE, EXITING_DIALOGUE, CINEMATIC_OVERRIDE
    }

    /** Durée du fondu en mode « réduire les animations », en millisecondes. */
    private static final int FONDU_REDUIT_MS = 120;

    /** Ticks pendant lesquels le joueur est tourné vers le personnage. */
    private static final int ORIENTATION_TICKS = 20;

    /** Marge ajoutée à la fenêtre qui suit un choix : aller-retour réseau et tick de dispatch. */
    private static final int MARGE_REPONSE_MS = 250;

    private static Etat etat = Etat.IDLE;
    private static int sessionId = -1;
    private static int pnjEntityId = -1;
    private static UUID pnjUuid;
    private static String pnjNom = "";
    private static DialogueMode mode;
    private static CameraProfile profil = CameraProfile.DEFAUT;
    private static int flags;
    private static long changementMs;
    private static int transitionMs;
    private static DialogueCameraController controleur;

    private static Perspective perspectiveAvant;
    private static boolean perspectiveForcee;
    private static boolean restaurerPerspectiveApresCinematique;

    private static int orientationTicks;
    private static boolean ecranVu;
    private static Screen ecranCourant;
    private static boolean finEnvoyee;
    private static CloseReason derniereRaison;

    /** Un écran qui n'est pas le nôtre (commerce, atelier) a pris la place du dialogue. */
    private static boolean ecranEtrangerVu;
    /** Cet écran étranger s'est fermé : ticks qu'il reste au serveur pour rouvrir le dialogue. */
    private static int attenteRetourTicks;
    private static final int ATTENTE_RETOUR_TICKS = 40;
    private static final int ATTENTE_ECRAN_TICKS = 15;

    /** La session qu'une cinématique a interrompue, et s'il faut demander sa reprise. */
    private static int sessionSuspendue = -1;
    private static boolean repriseVoulue;

    // --- capture ------------------------------------------------------------

    private static final List<Text> REPLIQUES = new ArrayList<>();
    private static final List<MessageCapture.ChoixCapture> CHOIX = new ArrayList<>();
    /** Jusqu'à quand un message sans marque appartient encore à la salve en cours. */
    private static long fenetreJusquaMs;
    private static long dernierMessageMs;
    /** Le prochain message capturé ouvre une nouvelle page : la précédente est consommée. */
    private static boolean remplacerAuProchainMessage;
    /** Une réponse est partie ; on attend ce que le personnage en dit. */
    private static boolean choixEnvoye;
    /** Aucun message n'est encore arrivé : le clic n'a peut-être rien déclenché du tout. */
    private static boolean aucunMessageRecu = true;
    /** L'écran doit repartir de la première page à sa prochaine lecture. */
    private static boolean pageRemise;
    private static int captureVersion;

    private static BooleanSupplier typewriterDefaut = () -> true;
    private static IntSupplier typewriterVitesseDefaut = () -> 40;

    // --- historique ---------------------------------------------------------

    /** Les dernières répliques et réponses de la conversation, pour la touche H. */
    private static final List<String> HISTORIQUE = new ArrayList<>();

    /** Ce que les modules de compat font à l'ouverture (Better Combat : annuler l'élan). */
    private static final List<Runnable> A_L_OUVERTURE = new ArrayList<>();

    /** Journal detaille des transitions, en banc d'essai seulement. */
    private static final boolean TRACE = Boolean.getBoolean("hcd.selftest") || Boolean.getBoolean("hcd.devtools");

    private static void tracer(String message) {
        if (TRACE) {
            HauteCapitaleDialogue.LOGGER.info("[etat] {} (etat={}, session={}, mode={}, ecran={})", message, etat, sessionId,
                    mode, ecranCourant == null ? "aucun" : ecranCourant.getClass().getSimpleName());
        }
    }

    private DialogueClientState() {
    }

    public static void aLOuverture(Runnable action) {
        A_L_OUVERTURE.add(action);
    }

    /** Le pont Easy NPC fournit le réglage « machine à écrire » de son fichier de config. */
    public static void typewriterParDefaut(BooleanSupplier actif, IntSupplier vitesse) {
        typewriterDefaut = actif;
        typewriterVitesseDefaut = vitesse;
    }

    // ------------------------------------------------------------------------
    // Serveur → client
    // ------------------------------------------------------------------------

    static void ouvrir(DialogueNetwork.SessionOpen p) {
        MinecraftClient client = MinecraftClient.getInstance();
        if (client.player == null || client.world == null) {
            return;
        }
        DialogueClientSettings s = DialogueClientSettings.get();

        // Une session deja ouverte avec un autre personnage : on la solde sans
        // fondu, la nouvelle prend la place. Meme personnage : simple mise a jour.
        if (etat != Etat.IDLE && p.sessionId() != sessionId) {
            if (pnjEntityId == p.npcEntityId() && enConversation()) {
                sessionId = p.sessionId();
                profil = p.profile();
                mettreAJourDrapeaux(client, p.flags());
                changerMode(client, p.mode());
                return;
            }
            sortirLocalement(0);
        } else if (etat != Etat.IDLE) {
            // La meme session, renvoyee : ses drapeaux ont change (vehicule
            // parti ou arrete).
            mettreAJourDrapeaux(client, p.flags());
            return;
        }

        sessionId = p.sessionId();
        pnjEntityId = p.npcEntityId();
        pnjUuid = p.npcUuid();
        pnjNom = p.npcName();
        mode = p.mode();
        profil = p.profile();
        flags = p.flags();
        finEnvoyee = false;
        ecranVu = false;
        ecranCourant = null;
        derniereRaison = null;
        transitionMs = s.reduire_animations ? FONDU_REDUIT_MS : profil.transitionMs();
        orientationTicks = s.ne_pas_orienter_joueur ? 0 : ORIENTATION_TICKS;

        if (CameraOverrideManager.isCinematic()) {
            // Une cinematique tient la camera : on ne se bat pas. La session est
            // rendue au serveur tout de suite, et reprise a la fin si le
            // personnage le permet.
            sessionSuspendue = sessionId;
            repriseVoulue = SessionFlags.a(flags, SessionFlags.REPRISE_APRES_CINEMATIQUE);
            envoyerFin(CloseReason.CINEMATIQUE);
            etat = Etat.CINEMATIC_OVERRIDE;
            changementMs = System.currentTimeMillis();
            return;
        }

        if (simplifie()) {
            // Vehicule en route : texte seul. La camera reste celle du gameplay,
            // la perspective et l'orientation du joueur ne sont pas touchees.
            orientationTicks = 0;
        } else {
            forcerPerspective(client);
            controleur = new DialogueCameraController(pnjEntityId, profil);
            CameraFocus.request(controleur, transitionMs);
        }
        HudMask.activer(SessionFlags.a(flags, SessionFlags.MASQUER_HUD));
        for (Runnable action : A_L_OUVERTURE) {
            try {
                action.run();
            } catch (Throwable t) {
                HauteCapitaleDialogue.LOGGER.warn("Action d'ouverture client en échec : {}", t.toString());
            }
        }

        etat = Etat.ENTERING_DIALOGUE;
        changementMs = System.currentTimeMillis();
        if (mode == DialogueMode.CAPTURE) {
            demarrerCapture(client);
        }
        tracer("ouvrir #" + sessionId + " " + pnjNom);
        HauteCapitaleDialogue.LOGGER.debug("Dialogue : entrée, session #{} avec {} ({}).", sessionId, pnjNom, mode);
    }

    /**
     * Le serveur renvoie la session avec d'autres drapeaux : le véhicule du
     * personnage est parti, ou s'est arrêté. La caméra est rendue en fondu, ou
     * reprise en fondu ; l'écran, lui, ne bouge pas.
     */
    private static void mettreAJourDrapeaux(MinecraftClient client, int nouveaux) {
        boolean avant = simplifie();
        flags = nouveaux;
        boolean apres = simplifie();
        if (avant == apres || !enConversation()) {
            return;
        }
        tracer(apres ? "véhicule en route : caméra rendue" : "véhicule à l'arrêt : caméra reprise");
        if (apres) {
            CameraFocus.release(transitionMs);
            controleur = null;
            restaurerPerspective(client);
            orientationTicks = 0;
        } else {
            forcerPerspective(client);
            controleur = new DialogueCameraController(pnjEntityId, profil);
            CameraFocus.request(controleur, transitionMs);
        }
    }

    /** Même personnage, nouvelle session : le mode peut avoir changé. */
    private static void changerMode(MinecraftClient client, DialogueMode nouveau) {
        DialogueMode ancien = mode;
        mode = nouveau;
        if (ancien == nouveau) {
            if (nouveau == DialogueMode.CAPTURE) {
                // Nouveau clic sur le meme personnage : nouvelle salve.
                remplacerAuProchainMessage = true;
                fenetreJusquaMs = System.currentTimeMillis() + DialogueClientSettings.get().capture_fenetre_ms;
                if (!(client.currentScreen instanceof CaptureDialogueScreen)) {
                    demarrerCapture(client);
                }
            }
            return;
        }
        tracer("changerMode " + ancien + " → " + nouveau);
        if (ancien == DialogueMode.CAPTURE && client.currentScreen instanceof CaptureDialogueScreen ecran) {
            ecran.fermerSansPrevenir();
        }
        ecranVu = false;
        if (nouveau == DialogueMode.CAPTURE) {
            demarrerCapture(client);
        }
    }

    private static void demarrerCapture(MinecraftClient client) {
        long maintenant = System.currentTimeMillis();
        REPLIQUES.clear();
        CHOIX.clear();
        fenetreJusquaMs = maintenant + DialogueClientSettings.get().capture_fenetre_ms;
        dernierMessageMs = maintenant;
        remplacerAuProchainMessage = false;
        choixEnvoye = false;
        aucunMessageRecu = true;
        pageRemise = true;
        captureVersion++;
        if (!(client.currentScreen instanceof CaptureDialogueScreen)) {
            client.setScreen(new CaptureDialogueScreen(pnjNom));
        }
    }

    static void onServeurFerme(DialogueNetwork.SessionClose p) {
        tracer("onServeurFerme #" + p.sessionId() + " " + p.reason());
        if (p.sessionId() != sessionId) {
            return;
        }
        if (etat == Etat.IDLE || etat == Etat.EXITING_DIALOGUE) {
            derniereRaison = p.reason();
            return;
        }
        // La raison du serveur fait foi. On sort d'abord — l'etat passe a
        // EXITING — puis on referme l'ecran : sa fermeture repasse par
        // demanderFin, qui n'a alors plus rien a dire ni a envoyer.
        finEnvoyee = true;
        derniereRaison = p.reason();
        Screen ecran = ecranCourant;
        sortirLocalement(transitionMs);
        fermerEcran(ecran);
    }

    // ------------------------------------------------------------------------
    // Client → serveur
    // ------------------------------------------------------------------------

    /** Le joueur veut arrêter : Échap, réponse de congé, écran refermé, commande. */
    public static void demanderFin(CloseReason raison) {
        tracer("demanderFin " + raison);
        if (etat == Etat.IDLE || etat == Etat.EXITING_DIALOGUE) {
            return;
        }
        // Meme ordre que pour une fermeture venue du serveur : on sort d'abord,
        // puis on referme l'ecran, dont close() repasse ici pour rien.
        Screen ecran = ecranCourant;
        ecranCourant = null;
        envoyerFin(raison);
        sortirLocalement(transitionMs);
        fermerEcran(ecran);
    }

    private static void envoyerFin(CloseReason raison) {
        if (finEnvoyee || sessionId < 0) {
            return;
        }
        finEnvoyee = true;
        derniereRaison = raison;
        MinecraftClient client = MinecraftClient.getInstance();
        if (client.getNetworkHandler() != null) {
            ClientPlayNetworking.send(new DialogueNetwork.SessionEnd(sessionId, raison));
        }
    }

    // ------------------------------------------------------------------------
    // Historique
    // ------------------------------------------------------------------------

    /** Une réplique du personnage vient d'être montrée. */
    public static void noterReplique(String nom, String texte) {
        if (texte == null || texte.isBlank()) {
            return;
        }
        String prefixe = nom == null || nom.isBlank() ? "" : nom.toUpperCase(java.util.Locale.ROOT) + " — ";
        ajouterHistorique(prefixe + texte.strip());
    }

    /** Le joueur vient de répondre. */
    public static void noterReponse(String libelle) {
        if (libelle != null && !libelle.isBlank()) {
            ajouterHistorique("› " + libelle.strip());
        }
    }

    private static void ajouterHistorique(String entree) {
        if (!HISTORIQUE.isEmpty() && HISTORIQUE.get(HISTORIQUE.size() - 1).equals(entree)) {
            return;
        }
        HISTORIQUE.add(entree);
        int max = Math.max(2, DialogueClientSettings.get().historique_taille);
        while (HISTORIQUE.size() > max) {
            HISTORIQUE.remove(0);
        }
    }

    public static List<String> historique() {
        return List.copyOf(HISTORIQUE);
    }

    // ------------------------------------------------------------------------
    // Capture : ce qui arrive par le chat
    // ------------------------------------------------------------------------

    /** Sommes-nous en conversation capturée ? Les messages de jeu sont alors examinés. */
    public static boolean captureActive() {
        return mode == DialogueMode.CAPTURE && enConversation();
    }

    /** Dans la salve qui suit l'ouverture ou un choix : un message sans marque est une réplique. */
    public static boolean dansFenetreCapture() {
        return System.currentTimeMillis() <= fenetreJusquaMs;
    }

    /** Un message a été reconnu comme faisant partie de la conversation. */
    public static void onMessageCapture(MessageCapture.Classification c) {
        long maintenant = System.currentTimeMillis();
        if (remplacerAuProchainMessage) {
            REPLIQUES.clear();
            CHOIX.clear();
            remplacerAuProchainMessage = false;
            pageRemise = true;
        }
        if (c.replique() != null) {
            REPLIQUES.add(c.replique());
            noterReplique(pnjNom, c.replique().getString());
        }
        for (MessageCapture.ChoixCapture choix : c.choix()) {
            boolean doublon = false;
            for (MessageCapture.ChoixCapture existant : CHOIX) {
                if (existant.evenement().equals(choix.evenement())
                        && existant.libelle().getString().equals(choix.libelle().getString())) {
                    doublon = true;
                    break;
                }
            }
            if (!doublon) {
                CHOIX.add(choix);
            }
        }
        choixEnvoye = false;
        aucunMessageRecu = false;
        dernierMessageMs = maintenant;
        fenetreJusquaMs = Math.max(fenetreJusquaMs, maintenant + DialogueClientSettings.get().capture_fenetre_ms);
        captureVersion++;
        tracer("capture : " + (c.replique() == null ? "" : "réplique « " + c.replique().getString() + " » ")
                + (c.choix().isEmpty() ? "" : c.choix().size() + " choix") + " → " + REPLIQUES.size() + "/" + CHOIX.size());
    }

    /** Une réponse vient de partir : les choix sont consommés, la réplique suivante remplacera la page. */
    public static void onChoixEnvoye(boolean adieu, String libelle) {
        long maintenant = System.currentTimeMillis();
        noterReponse(libelle);
        CHOIX.clear();
        choixEnvoye = !adieu;
        remplacerAuProchainMessage = true;
        fenetreJusquaMs = maintenant + DialogueClientSettings.get().capture_fenetre_reponse_ms + MARGE_REPONSE_MS;
        dernierMessageMs = maintenant;
        captureVersion++;
        tracer("choix envoyé" + (adieu ? " (congé)" : ""));
    }

    public static List<Text> repliquesCapturees() {
        return List.copyOf(REPLIQUES);
    }

    public static List<MessageCapture.ChoixCapture> choixCaptures() {
        return List.copyOf(CHOIX);
    }

    /** Change à chaque réplique, réponse ou choix : l'écran s'en sert pour savoir s'il a à relire. */
    public static int captureVersion() {
        return captureVersion;
    }

    /** Vrai une fois par nouvelle page : l'écran repart alors du début. */
    public static boolean consommerRemise() {
        boolean r = pageRemise;
        pageRemise = false;
        return r;
    }

    public static boolean typewriterActifCapture() {
        return switch (DialogueClientSettings.get().typewriter) {
            case "on" -> true;
            case "off" -> false;
            default -> typewriterDefaut.getAsBoolean();
        };
    }

    public static int typewriterVitesse() {
        int reglage = DialogueClientSettings.get().caracteres_par_seconde;
        return reglage > 0 ? reglage : Math.max(1, typewriterVitesseDefaut.getAsInt());
    }

    /**
     * Le silence ne vaut plus congé.
     *
     * <p>Par défaut, la conversation reste à l'écran tant que le joueur n'en
     * est pas sorti lui-même — Échap, ou une réponse d'adieu. Ce qui arrive
     * ensuite vient s'y afficher, et personne ne se fait refermer le bandeau au
     * nez pendant qu'il lit. Une seule exception, et elle ne fait disparaître
     * aucun texte : un clic qui n'a produit aucun message n'était pas une
     * conversation, on rend la main sans attendre plutôt que de laisser un
     * bandeau vide.
     *
     * <p>Les vraies sorties de secours restent au serveur : distance, mort,
     * changement de monde, téléportation, personnage disparu.
     *
     * <p>Avec {@code fermeture_automatique}, l'ancien comportement revient :
     * la salve finie et le texte lu, le personnage qui a fini de parler
     * referme lui-même. Il revient aussi, quoi qu'en dise le réglage, pour un
     * personnage qui interdit Échap : autrement le joueur n'aurait plus de
     * sortie du tout.
     */
    private static void surveillerCapture() {
        if (mode != DialogueMode.CAPTURE || !CHOIX.isEmpty()) {
            return;
        }
        long maintenant = System.currentTimeMillis();
        if (maintenant <= fenetreJusquaMs) {
            return;
        }
        if (aucunMessageRecu) {
            // Rien du tout apres l'ouverture : le clic n'etait pas une conversation.
            demanderFin(CloseReason.FERMEE);
            return;
        }
        // Un personnage qui interdit Échap garde sa fermeture automatique :
        // sans elle, le joueur n'aurait plus aucune sortie.
        if (!DialogueClientSettings.get().fermeture_automatique && escAutorise()) {
            return;
        }
        if (REPLIQUES.isEmpty() || choixEnvoye) {
            // Rien en reponse au choix : fini.
            demanderFin(REPLIQUES.isEmpty() ? CloseReason.FERMEE : CloseReason.ADIEU);
            return;
        }
        long delai = DialogueClientSettings.get().capture_delai_silence_ms + dureeLecture();
        if (maintenant - dernierMessageMs > delai) {
            demanderFin(CloseReason.INACTIVITE);
        }
    }

    /** Le temps que la machine à écrire mette à tout révéler, en millisecondes. */
    private static long dureeLecture() {
        if (!typewriterActifCapture()) {
            return 0L;
        }
        int caracteres = 0;
        for (Text t : REPLIQUES) {
            caracteres += t.getString().length();
        }
        return caracteres * 1000L / typewriterVitesse();
    }

    // ------------------------------------------------------------------------
    // Écrans
    // ------------------------------------------------------------------------

    /** Un écran de dialogue à nous vient de s'ouvrir. */
    public static void onEcranOuvert(Screen ecran) {
        tracer("onEcranOuvert " + ecran.getClass().getSimpleName());
        ecranCourant = ecran;
        ecranVu = true;
        ecranEtrangerVu = false;
        attenteRetourTicks = 0;
        if (etat == Etat.DIALOGUE_ACTIVE) {
            // Nouveau noeud du meme dialogue : la camera ne bouge pas, l'etat le dit.
            etat = Etat.TRANSITIONING_NODE;
            changementMs = System.currentTimeMillis();
        }
    }

    /**
     * Le même écran a été réinstallé (le code vanilla le remet en place après
     * un {@code click_event}) : il reste le nôtre.
     */
    public static void reprendreEcran(Screen ecran) {
        ecranCourant = ecran;
        ecranVu = true;
    }

    /** Un écran de dialogue à nous vient d'être retiré — remplacé ou fermé. */
    public static void onEcranFerme(Screen ecran) {
        tracer("onEcranFerme " + ecran.getClass().getSimpleName());
        if (ecranCourant == ecran) {
            ecranCourant = null;
        }
    }

    private static void fermerEcran(Screen ecran) {
        MinecraftClient client = MinecraftClient.getInstance();
        if (ecran != null && client.currentScreen == ecran) {
            // close() renvoie ici via demanderFin : l'etat EXITING l'ignore.
            ecran.close();
        }
    }

    // ------------------------------------------------------------------------
    // Tick
    // ------------------------------------------------------------------------

    static void tick(MinecraftClient client) {
        if (etat == Etat.IDLE) {
            if (restaurerPerspectiveApresCinematique && !CameraOverrideManager.isCinematic()) {
                restaurerPerspective(client);
                restaurerPerspectiveApresCinematique = false;
            }
            return;
        }
        long maintenant = System.currentTimeMillis();

        // Une cinematique prend tout. On se retire sans fondu, on previent le
        // serveur, et on rendra la perspective une fois la cinematique finie.
        if (CameraOverrideManager.isCinematic()) {
            if (etat != Etat.CINEMATIC_OVERRIDE) {
                sessionSuspendue = sessionId;
                repriseVoulue = SessionFlags.a(flags, SessionFlags.REPRISE_APRES_CINEMATIQUE);
                envoyerFin(CloseReason.CINEMATIQUE);
                Screen ecran = ecranCourant;
                etat = Etat.CINEMATIC_OVERRIDE;
                fermerEcran(ecran);
                ecranCourant = null;
                CameraFocus.clear();
                controleur = null;
                HudMask.activer(false);
                restaurerPerspectiveApresCinematique = perspectiveForcee;
                perspectiveForcee = false;
                etat = Etat.CINEMATIC_OVERRIDE;
                changementMs = maintenant;
            }
            return;
        }
        if (etat == Etat.CINEMATIC_OVERRIDE) {
            // Fin de cinematique : retour au gameplay, puis, si le personnage le
            // permet, on demande au serveur de reprendre la conversation la ou
            // elle en etait. C'est lui qui decide, et qui rouvre.
            if (restaurerPerspectiveApresCinematique) {
                restaurerPerspective(client);
                restaurerPerspectiveApresCinematique = false;
            }
            if (repriseVoulue && sessionSuspendue >= 0 && client.getNetworkHandler() != null) {
                tracer("fin de cinématique : demande de reprise de la session #" + sessionSuspendue);
                ClientPlayNetworking.send(new DialogueNetwork.Reprise(sessionSuspendue));
            }
            repriseVoulue = false;
            sessionSuspendue = -1;
            auRepos();
            return;
        }

        switch (etat) {
            case ENTERING_DIALOGUE -> {
                orienterJoueur(client);
                if (maintenant - changementMs >= transitionMs) {
                    etat = Etat.DIALOGUE_ACTIVE;
                }
            }
            case TRANSITIONING_NODE -> {
                etat = Etat.DIALOGUE_ACTIVE;
                surveillerEcran(client);
            }
            case DIALOGUE_ACTIVE -> {
                orienterJoueur(client);
                surveillerEcran(client);
                if (etat == Etat.DIALOGUE_ACTIVE) {
                    surveillerCapture();
                }
            }
            case EXITING_DIALOGUE -> {
                if (maintenant - changementMs >= transitionMs) {
                    auRepos();
                }
            }
            default -> {
            }
        }
    }

    /**
     * L'écran de dialogue a disparu sans qu'on nous le dise ?
     *
     * <p>Un écran étranger (commerce, atelier, billetterie) qui a pris la place
     * du nôtre garde la conversation ouverte. Quand il se ferme : si le
     * personnage permet le retour, on laisse au serveur quelques ticks pour
     * rouvrir le dialogue (mode menu), ou on le remontre nous-mêmes (capture) ;
     * sinon, plus aucun écran veut dire que la conversation est finie.
     */
    private static void surveillerEcran(MinecraftClient client) {
        Screen courant = client.currentScreen;
        if (courant != null) {
            if (ecranVu && !(courant instanceof EcranDialogue) && !ecranEtrangerVu) {
                ecranEtrangerVu = true;
                tracer("écran étranger : " + courant.getClass().getSimpleName());
            }
            attenteRetourTicks = 0;
            return;
        }
        if (!ecranVu) {
            return;
        }
        boolean retour = ecranEtrangerVu && SessionFlags.a(flags, SessionFlags.RETOUR_APRES_COMMERCE);
        if (retour && mode == DialogueMode.CAPTURE) {
            // La conversation capturee vit ici : on la remontre telle qu'elle etait.
            ecranEtrangerVu = false;
            tracer("écran étranger fermé : retour au dialogue capturé");
            client.setScreen(new CaptureDialogueScreen(pnjNom));
            return;
        }
        // Entre deux ecrans d'Easy NPC (un noeud qui se ferme, le commerce qui
        // s'ouvre au tick d'apres) il n'y a rien pendant un instant : on laisse
        // ce temps-la avant de conclure — et davantage si un retour est attendu.
        if (attenteRetourTicks == 0) {
            attenteRetourTicks = retour ? ATTENTE_RETOUR_TICKS : ATTENTE_ECRAN_TICKS;
            tracer(retour ? "écran étranger fermé : on attend le retour du dialogue" : "plus d'écran : on attend un instant");
            return;
        }
        if (--attenteRetourTicks == 0) {
            demanderFin(CloseReason.ECRAN_FERME);
        }
    }

    /** Tourne doucement le joueur vers le personnage, sans à-coup. */
    private static void orienterJoueur(MinecraftClient client) {
        if (orientationTicks <= 0 || client.player == null || client.world == null) {
            return;
        }
        Entity pnj = client.world.getEntityById(pnjEntityId);
        if (pnj == null) {
            return;
        }
        orientationTicks--;
        Vec3d oeil = client.player.getEyePos();
        Vec3d cible = pnj.getEyePos();
        Vec3d d = cible.subtract(oeil);
        double horizontal = Math.sqrt(d.x * d.x + d.z * d.z);
        if (horizontal < 1.0e-3) {
            return;
        }
        float yawCible = (float) (Math.toDegrees(Math.atan2(d.z, d.x)) - 90.0);
        float pitchCible = (float) -Math.toDegrees(Math.atan2(d.y, horizontal));
        float alpha = 0.25f;
        client.player.setYaw(MathHelper.lerpAngleDegrees(alpha, client.player.getYaw(), yawCible));
        client.player.setPitch(MathHelper.lerp(alpha, client.player.getPitch(), pitchCible));
    }

    // ------------------------------------------------------------------------
    // Sortie
    // ------------------------------------------------------------------------

    private static void sortirLocalement(int fonduMs) {
        tracer("sortirLocalement " + fonduMs + " ms, raison " + derniereRaison);
        MinecraftClient client = MinecraftClient.getInstance();
        CameraFocus.release(fonduMs);
        HudMask.activer(false);
        restaurerPerspective(client);
        orientationTicks = 0;
        etat = Etat.EXITING_DIALOGUE;
        changementMs = System.currentTimeMillis();
        transitionMs = fonduMs;
        HauteCapitaleDialogue.LOGGER.debug("Dialogue : sortie, session #{} ({}).", sessionId, derniereRaison);
        if (fonduMs <= 0) {
            auRepos();
        }
    }

    private static void auRepos() {
        etat = Etat.IDLE;
        controleur = null;
        ecranCourant = null;
        ecranVu = false;
        ecranEtrangerVu = false;
        attenteRetourTicks = 0;
        sessionId = -1;
        pnjEntityId = -1;
        pnjUuid = null;
        mode = null;
        REPLIQUES.clear();
        CHOIX.clear();
        HISTORIQUE.clear();
        choixEnvoye = false;
        aucunMessageRecu = true;
        remplacerAuProchainMessage = false;
        captureVersion++;
    }

    /** Changement de monde, déconnexion : ardoise propre, sans paquet. */
    static void reset() {
        MinecraftClient client = MinecraftClient.getInstance();
        CameraFocus.clear();
        HudMask.activer(false);
        if (perspectiveForcee) {
            restaurerPerspective(client);
        }
        restaurerPerspectiveApresCinematique = false;
        auRepos();
    }

    // ------------------------------------------------------------------------
    // Perspective
    // ------------------------------------------------------------------------

    private static void forcerPerspective(MinecraftClient client) {
        if (!SessionFlags.a(flags, SessionFlags.FORCER_TROISIEME_PERSONNE)
                || DialogueClientSettings.get().jamais_changer_perspective
                || client.options == null) {
            return;
        }
        Perspective courante = client.options.getPerspective();
        if (courante.isFirstPerson()) {
            perspectiveAvant = courante;
            client.options.setPerspective(Perspective.THIRD_PERSON_BACK);
            perspectiveForcee = true;
        }
    }

    private static void restaurerPerspective(MinecraftClient client) {
        if (!perspectiveForcee) {
            return;
        }
        perspectiveForcee = false;
        if (client.options != null && perspectiveAvant != null
                && client.options.getPerspective() == Perspective.THIRD_PERSON_BACK) {
            client.options.setPerspective(perspectiveAvant);
        }
        perspectiveAvant = null;
    }

    // ------------------------------------------------------------------------
    // Lecture
    // ------------------------------------------------------------------------

    public static Etat etat() {
        return etat;
    }

    public static int sessionId() {
        return sessionId;
    }

    public static int pnjEntityId() {
        return pnjEntityId;
    }

    public static UUID pnjUuid() {
        return pnjUuid;
    }

    public static String pnjNom() {
        return pnjNom;
    }

    public static DialogueMode mode() {
        return mode;
    }

    public static int flags() {
        return flags;
    }

    public static CameraProfile profil() {
        return profil;
    }

    public static DialogueCameraController controleur() {
        return controleur;
    }

    public static CloseReason derniereRaison() {
        return derniereRaison;
    }

    public static boolean perspectiveForcee() {
        return perspectiveForcee;
    }

    public static Screen ecranCourant() {
        return ecranCourant;
    }

    /** Texte seul, caméra de gameplay : le personnage est porté par un véhicule en route. */
    public static boolean simplifie() {
        return SessionFlags.a(flags, SessionFlags.SIMPLIFIE);
    }

    /** Échap est-il autorisé pour la conversation en cours ? */
    public static boolean escAutorise() {
        return etat == Etat.IDLE || SessionFlags.a(flags, SessionFlags.AUTORISER_ESC);
    }

    public static boolean enConversation() {
        return etat == Etat.ENTERING_DIALOGUE || etat == Etat.DIALOGUE_ACTIVE || etat == Etat.TRANSITIONING_NODE;
    }
}
