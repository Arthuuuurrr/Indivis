package net.hautecapitale.dialogue.client.capture;

import net.hautecapitale.dialogue.HauteCapitaleDialogue;
import net.hautecapitale.dialogue.client.DialogueClientSettings;
import net.hautecapitale.dialogue.client.DialogueClientState;
import net.hautecapitale.dialogue.client.screen.DialoguePanel;
import net.hautecapitale.dialogue.client.screen.EcranDialogue;
import net.hautecapitale.dialogue.session.CloseReason;
import net.minecraft.client.gui.Click;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.input.KeyInput;
import net.minecraft.client.sound.PositionedSoundInstance;
import net.minecraft.sound.SoundEvents;
import net.minecraft.text.Text;
import org.lwjgl.glfw.GLFW;

import java.util.List;

/**
 * L'écran des dialogues capturés : le même bandeau, alimenté par les messages
 * de jeu au fil de leur arrivée.
 *
 * <p>Pas de {@code ScreenHandler} derrière : la conversation vit dans l'état
 * client, cet écran ne fait que la montrer. Choisir une réponse rejoue son
 * {@code click_event} par le code vanilla — un {@code /trigger} part comme
 * s'il avait été cliqué dans le chat, et le serveur le valide comme aujourd'hui.
 */
public class CaptureDialogueScreen extends Screen implements EcranDialogue {

    private DialoguePanel panneau;
    private boolean ouvertureNotifiee;
    private boolean fermetureNotifiee;
    private CloseReason raisonFermeture = CloseReason.ESC;
    private int ticks;
    private int versionVue = -1;

    public CaptureDialogueScreen(String nom) {
        super(Text.literal(nom));
    }

    @Override
    protected void init() {
        super.init();
        if (this.panneau == null) {
            this.panneau = new DialoguePanel(this.textRenderer);
            this.panneau.typewriter(DialogueClientState.typewriterActifCapture(), DialogueClientState.typewriterVitesse());
            this.panneau.nom(this.title.getString());
            this.panneau.escAutorise(DialogueClientState.escAutorise());
        }
        this.panneau.disposer(this.width, this.height);
        rafraichir();
        if (!this.ouvertureNotifiee) {
            this.ouvertureNotifiee = true;
            DialogueClientState.onEcranOuvert(this);
        } else {
            // Le code vanilla nous reinstalle apres un click_event : on reste l'ecran courant.
            DialogueClientState.reprendreEcran(this);
        }
    }

    /** Relit l'état si quelque chose y a changé : réplique, réponses, choix envoyé. */
    private void rafraichir() {
        int version = DialogueClientState.captureVersion();
        if (this.panneau == null || version == this.versionVue) {
            return;
        }
        this.versionVue = version;
        List<Text> repliques = DialogueClientState.repliquesCapturees();
        boolean remise = DialogueClientState.consommerRemise();
        this.panneau.attente(repliques.isEmpty());
        this.panneau.paragraphes(repliques, remise);
        this.panneau.choix(DialogueClientState.choixCaptures());
        this.panneau.historique(DialogueClientState.historique());
    }

    // ------------------------------------------------------------------------
    // Rendu
    // ------------------------------------------------------------------------

    @Override
    public void renderBackground(DrawContext context, int mouseX, int mouseY, float deltaTicks) {
        // Le personnage doit rester visible : ni flou ni assombrissement.
    }

    @Override
    public void render(DrawContext context, int mouseX, int mouseY, float deltaTicks) {
        if (this.panneau != null) {
            rafraichir();
            this.panneau.render(context, mouseX, mouseY, this.ticks);
        }
    }

    @Override
    public void tick() {
        super.tick();
        this.ticks++;
        rafraichir();
    }

    // ------------------------------------------------------------------------
    // Entrées
    // ------------------------------------------------------------------------

    @Override
    public boolean mouseClicked(Click click, boolean doubled) {
        if (click.button() == 0 || click.button() == 1) {
            if (this.panneau.avancer()) {
                return true;
            }
            int index = this.panneau.choixSous(click.x(), click.y());
            if (index >= 0) {
                choisir(index);
            }
        }
        return true;
    }

    @Override
    public boolean mouseScrolled(double mouseX, double mouseY, double horizontalAmount, double verticalAmount) {
        if (verticalAmount < 0) {
            this.panneau.avancer();
        } else if (verticalAmount > 0) {
            this.panneau.pagePrecedente();
        }
        return true;
    }

    @Override
    public boolean keyPressed(KeyInput input) {
        int code = input.getKeycode();
        if (input.isEscape()) {
            return super.keyPressed(input);
        }
        if (code == GLFW.GLFW_KEY_H) {
            this.panneau.basculerHistorique();
            return true;
        }
        if (input.isEnterOrSpace()) {
            if (!this.panneau.avancer() && this.panneau.choix().size() == 1) {
                choisir(0);
            }
            return true;
        }
        int numero = -1;
        if (code >= GLFW.GLFW_KEY_1 && code <= GLFW.GLFW_KEY_9) {
            numero = code - GLFW.GLFW_KEY_1;
        } else if (code >= GLFW.GLFW_KEY_KP_1 && code <= GLFW.GLFW_KEY_KP_9) {
            numero = code - GLFW.GLFW_KEY_KP_1;
        }
        if (numero >= 0) {
            if (this.panneau.typewriterActif() || !this.panneau.dernierePage()) {
                this.panneau.avancer();
            } else {
                choisir(numero);
            }
            return true;
        }
        if (this.client != null && this.client.options.inventoryKey.matchesKey(input)) {
            // La touche d'inventaire ne ferme pas une conversation.
            return true;
        }
        return super.keyPressed(input);
    }

    @Override
    public boolean shouldCloseOnEsc() {
        return DialogueClientState.escAutorise();
    }

    @Override
    public boolean shouldPause() {
        return false;
    }

    // ------------------------------------------------------------------------
    // Réponses
    // ------------------------------------------------------------------------

    @Override
    public void choisir(int index) {
        List<DialoguePanel.Choix> choix = this.panneau == null ? List.of() : this.panneau.choix();
        if (index < 0 || index >= choix.size() || !(choix.get(index) instanceof MessageCapture.ChoixCapture c)) {
            if (Boolean.getBoolean("hcd.selftest")) {
                HauteCapitaleDialogue.LOGGER.info("[capture] choisir {} : hors limites ({} choix)", index, choix.size());
            }
            return;
        }
        if (Boolean.getBoolean("hcd.selftest")) {
            HauteCapitaleDialogue.LOGGER.info("[capture] choisir {} : {} → {}", index, c.libelle().getString(), c.evenement());
        }
        if (this.client != null && DialogueClientSettings.get().son_reponse) {
            this.client.getSoundManager().play(PositionedSoundInstance.ui(SoundEvents.UI_BUTTON_CLICK, 0.8f));
        }
        boolean adieu = c.type() == DialoguePanel.Type.ADIEU;
        DialogueClientState.onChoixEnvoye(adieu, c.libelle().getString());
        // Le code vanilla, exactement celui d'un clic dans le chat : un /trigger
        // repart comme aujourd'hui, valide par le serveur comme aujourd'hui. Il
        // nous reinstalle ensuite comme ecran courant (voir init()).
        Screen.handleClickEvent(c.evenement(), this.client, this);
        if (adieu) {
            this.raisonFermeture = CloseReason.ADIEU;
            this.close();
        }
    }

    // ------------------------------------------------------------------------
    // Cycle de vie
    // ------------------------------------------------------------------------

    @Override
    public void close() {
        if (Boolean.getBoolean("hcd.selftest")) {
            HauteCapitaleDialogue.LOGGER.info("[capture] close (notifiee={}, raison={})", this.fermetureNotifiee, this.raisonFermeture);
        }
        if (!this.fermetureNotifiee) {
            this.fermetureNotifiee = true;
            DialogueClientState.demanderFin(this.raisonFermeture);
        }
        super.close();
    }

    /** Retire l'écran sans clore la conversation (elle continue sous un autre écran). */
    public void fermerSansPrevenir() {
        this.fermetureNotifiee = true;
        super.close();
    }

    @Override
    public void removed() {
        super.removed();
        DialogueClientState.onEcranFerme(this);
    }

    @Override
    public String libelleDialogue() {
        return "capture";
    }

    @Override
    public List<String> reponsesDebug() {
        List<String> lignes = new java.util.ArrayList<>();
        List<DialoguePanel.Choix> choix = this.panneau == null ? List.of() : this.panneau.choix();
        for (int i = 0; i < choix.size(); i++) {
            lignes.add((i + 1) + ". " + choix.get(i).libelle().getString() + " [" + choix.get(i).type() + "]");
        }
        return lignes;
    }

    public int nombreRepliques() {
        return DialogueClientState.repliquesCapturees().size();
    }

    public int nombreChoix() {
        return this.panneau == null ? 0 : this.panneau.choix().size();
    }
}
