package net.hautecapitale.dialogue.client.compat.easynpc;

import de.markusbordihn.easynpc.condition.ClientConditionEvaluator;
import de.markusbordihn.easynpc.config.ClientDialogConfig;
import de.markusbordihn.easynpc.data.action.ActionDataEntry;
import de.markusbordihn.easynpc.data.action.ActionEventType;
import de.markusbordihn.easynpc.data.condition.ConditionDataEntry;
import de.markusbordihn.easynpc.data.dialog.DialogButtonConditionMode;
import de.markusbordihn.easynpc.data.dialog.DialogButtonEntry;
import de.markusbordihn.easynpc.data.dialog.DialogButtonType;
import de.markusbordihn.easynpc.data.dialog.DialogDataEntry;
import de.markusbordihn.easynpc.data.dialog.DialogDataSet;
import de.markusbordihn.easynpc.data.dialog.DialogMetaData;
import de.markusbordihn.easynpc.data.dialog.DialogOptionsData;
import de.markusbordihn.easynpc.data.dialog.DialogUtils;
import de.markusbordihn.easynpc.data.screen.AdditionalScreenData;
import de.markusbordihn.easynpc.data.screen.ScreenData;
import de.markusbordihn.easynpc.entity.easynpc.EasyNPC;
import de.markusbordihn.easynpc.menu.dialog.DialogMenu;
import de.markusbordihn.easynpc.network.NetworkMessageHandlerManager;
import de.markusbordihn.easynpc.network.components.TextComponent;
import net.hautecapitale.dialogue.HauteCapitaleDialogue;
import net.hautecapitale.dialogue.client.DialogueClientSettings;
import net.hautecapitale.dialogue.client.DialogueClientState;
import net.hautecapitale.dialogue.client.screen.DialoguePanel;
import net.hautecapitale.dialogue.client.screen.EcranDialogue;
import net.hautecapitale.dialogue.session.CloseReason;
import net.minecraft.client.gui.Click;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.ingame.HandledScreen;
import net.minecraft.client.input.KeyInput;
import net.minecraft.client.sound.PositionedSoundInstance;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.text.Text;
import org.lwjgl.glfw.GLFW;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.UUID;

/**
 * L'écran des dialogues Easy NPC : le bandeau partagé ({@link DialoguePanel})
 * posé sur le {@code DialogMenu} d'Easy NPC.
 *
 * <p>Les données — dialogue, boutons, verrous calculés par le serveur, scores —
 * sont celles qu'Easy NPC a déjà envoyées, et chaque clic repart par ses
 * paquets, qu'il revalide. Rien ici ne décide de ce qu'une réponse fait.
 */
public class RpgDialogueScreen extends HandledScreen<DialogMenu> implements EcranDialogue {

    /** Une réponse telle qu'elle est affichée. */
    public static final class Reponse implements DialoguePanel.Choix {
        public final DialogButtonEntry entree;
        public final Text libelle;
        public final DialoguePanel.Type type;
        public boolean verrouillee;
        public Text raison;

        Reponse(DialogButtonEntry entree, Text libelle, DialoguePanel.Type type) {
            this.entree = entree;
            this.libelle = libelle;
            this.type = type;
        }

        @Override
        public Text libelle() {
            return this.libelle;
        }

        @Override
        public DialoguePanel.Type type() {
            return this.type;
        }

        @Override
        public boolean verrouille() {
            return this.verrouillee;
        }

        @Override
        public Text raison() {
            return this.raison;
        }
    }

    private final DialogMenu menu;
    private DialoguePanel panneau;
    private AdditionalScreenData donnees;
    private DialogDataEntry dialogue;
    private UUID dialogId;
    private UUID pnjUuid;
    private LivingEntity pnjVivant;
    private Text texte = Text.empty();

    private final List<Reponse> reponses = new ArrayList<>();
    private final List<Reponse> visibles = new ArrayList<>();

    private boolean escAutorise = true;
    private boolean cacherVerrouillees;
    private boolean ouvertureNotifiee;
    private boolean fermetureNotifiee;
    private CloseReason raisonFermeture = CloseReason.ESC;
    private int ticks;
    private boolean invalide;

    public RpgDialogueScreen(DialogMenu menu, PlayerInventory inventory, Text title) {
        super(menu, inventory, title);
        this.menu = menu;
    }

    // ------------------------------------------------------------------------
    // Construction
    // ------------------------------------------------------------------------

    @Override
    protected void init() {
        super.init();
        this.x = 0;
        this.y = 0;
        this.backgroundWidth = this.width;
        this.backgroundHeight = this.height;

        if (this.panneau == null) {
            this.panneau = new DialoguePanel(this.textRenderer);
        }
        if (this.dialogue == null) {
            construire();
        }
        if (this.invalide) {
            return;
        }
        this.panneau.disposer(this.width, this.height);

        if (!this.ouvertureNotifiee) {
            this.ouvertureNotifiee = true;
            if (this.donnees.getActionEventSet().hasActionEvent(ActionEventType.ON_OPEN_DIALOG)) {
                NetworkMessageHandlerManager.getServerHandler()
                        .executeActionEvent(this.pnjUuid, ActionEventType.ON_OPEN_DIALOG);
            }
            DialogueClientState.onEcranOuvert(this);
        }
    }

    private void construire() {
        this.donnees = this.menu.getAdditionalScreenData();
        ScreenData screenData = this.menu.getScreenData();
        if (this.donnees == null || screenData == null || !this.donnees.hasDialogDataSet()) {
            HauteCapitaleDialogue.LOGGER.warn("Écran de dialogue sans données : fermeture.");
            this.invalide = true;
            this.close();
            return;
        }
        this.dialogId = screenData.dialogId();
        this.pnjUuid = screenData.uuid();
        EasyNPC<?> npc = this.menu.getEasyNPC();
        this.pnjVivant = npc == null ? null : npc.getLivingEntity();

        DialogDataSet ensemble = this.donnees.getDialogDataSet();
        this.dialogue = this.dialogId == null ? null : ensemble.getDialog(this.dialogId);
        if (this.dialogue == null) {
            List<DialogDataEntry> tous = ensemble.getDialogsByLabel();
            this.dialogue = tous.isEmpty() ? null : tous.get(0);
        }
        if (this.dialogue == null) {
            HauteCapitaleDialogue.LOGGER.warn("Écran de dialogue sans dialogue {} : fermeture.", this.dialogId);
            this.invalide = true;
            this.close();
            return;
        }
        if (this.dialogId == null) {
            this.dialogId = this.dialogue.getId();
        }

        DialogOptionsData options = this.dialogue.getDialogOptions();
        this.escAutorise = options.allowEscClose() && DialogueClientState.escAutorise();
        this.cacherVerrouillees = options.buttonConditionMode() == DialogButtonConditionMode.HIDE;

        // Le texte, avec les macros d'Easy NPC (@npc, @initiator, @score) et ses
        // codes de mise en forme, resolus exactement comme il le fait lui-meme.
        String brut = DialogUtils.parseDialogText(this.dialogue.getDialogText(),
                new DialogMetaData(this.pnjVivant, this.client == null ? null : this.client.player,
                        this.donnees.getScoreboardData()));
        this.texte = TextComponent.getText(brut == null ? "" : brut);

        this.reponses.clear();
        for (DialogButtonEntry bouton : this.dialogue.getDialogButtons()) {
            if (bouton == null || bouton.name() == null || bouton.name().isBlank()) {
                continue;
            }
            DialoguePanel.Type type = typeDe(bouton);
            Text libelle;
            if (bouton.isTranslationKey()) {
                libelle = TextComponent.getTextComponentRaw(bouton.name(), true);
            } else {
                // Une etiquette en tete du libelle — « [quete] Accepter. » — fixe la
                // nuance et disparait du texte : l'auteur du PNJ decide, sans code.
                DialoguePanel.Etiquette etiquette = DialoguePanel.etiquette(bouton.name());
                if (etiquette.type() != null) {
                    type = etiquette.type();
                }
                libelle = TextComponent.getTextComponentRaw(etiquette.reste(), false);
            }
            this.reponses.add(new Reponse(bouton, libelle, type));
        }
        rafraichirVerrous();
        DialogueClientState.noterReplique(this.title.getString(), this.texte.getString());

        DialogueClientSettings s = DialogueClientSettings.get();
        boolean typewriter = switch (s.typewriter) {
            case "on" -> true;
            case "off" -> false;
            default -> ClientDialogConfig.TYPEWRITER_ENABLED;
        };
        int cps = s.caracteres_par_seconde > 0 ? s.caracteres_par_seconde : ClientDialogConfig.TYPEWRITER_CHARS_PER_SECOND;

        this.panneau.nom(this.title.getString());
        this.panneau.escAutorise(this.escAutorise);
        this.panneau.typewriter(typewriter, cps);
        this.panneau.paragraphes(List.of(this.texte), true);
        this.panneau.choix(this.visibles);
        this.panneau.historique(DialogueClientState.historique());
    }

    /** Recalcule l'état verrouillé de chaque réponse. Toutes les 20 ticks, comme Easy NPC. */
    private void rafraichirVerrous() {
        for (Reponse r : this.reponses) {
            DialogButtonEntry b = r.entree;
            boolean disponible = true;
            if (b.hasConditions()) {
                boolean client = ClientConditionEvaluator.evaluateAll(b.conditions(),
                        this.client == null ? null : this.client.player, this.pnjVivant);
                disponible = client
                        && !this.donnees.isExecutionLimitReached(b.id())
                        && !this.donnees.isDialogButtonLocked(b.id());
            }
            r.verrouillee = !disponible;
            r.raison = disponible ? null : raisonDe(b);
        }
        this.visibles.clear();
        for (Reponse r : this.reponses) {
            if (!(r.verrouillee && this.cacherVerrouillees)) {
                this.visibles.add(r);
            }
        }
    }

    /** Le style d'une réponse se lit dans ses actions, pas dans une annotation. */
    static DialoguePanel.Type typeDe(DialogButtonEntry bouton) {
        if (bouton.type() == DialogButtonType.CLOSE || !bouton.hasActionData()) {
            return DialoguePanel.Type.ADIEU;
        }
        String nom = bouton.name() == null ? "" : bouton.name().toLowerCase(Locale.ROOT);
        if (nom.startsWith("[danger]") || nom.startsWith("(menace)")) {
            return DialoguePanel.Type.DANGER;
        }
        DialoguePanel.Type type = DialoguePanel.Type.NORMALE;
        for (ActionDataEntry action : bouton.actionDataSet().getEntries()) {
            String cmd = action.command() == null ? "" : action.command().toLowerCase(Locale.ROOT);
            switch (action.actionDataType()) {
                case OPEN_TRADING_SCREEN -> {
                    return DialoguePanel.Type.COMMERCE;
                }
                case CUSTOM -> {
                    if (cmd.startsWith("haute_capitale_metiers:")) {
                        return DialoguePanel.Type.METIER;
                    }
                    if (cmd.startsWith("mmo_ferries:") || cmd.startsWith("mmo_airships:")
                            || cmd.startsWith("mmo_caravans:")) {
                        return DialoguePanel.Type.TRANSPORT;
                    }
                    if (cmd.startsWith("haute_capitale_party:")) {
                        type = DialoguePanel.Type.QUETE;
                    }
                }
                case COMMAND -> {
                    if (cmd.contains("capitale:quest") || cmd.contains("quest")) {
                        type = DialoguePanel.Type.QUETE;
                    } else if (cmd.contains("ferry") || cmd.contains("airship") || cmd.contains("caravan")) {
                        type = DialoguePanel.Type.TRANSPORT;
                    }
                }
                default -> {
                }
            }
        }
        return type;
    }

    /** Pourquoi une réponse est grisée, en clair. Easy NPC n'a qu'un cadenas ; on a les conditions. */
    private Text raisonDe(DialogButtonEntry bouton) {
        if (this.donnees.isExecutionLimitReached(bouton.id())) {
            return Text.translatableWithFallback("hcd.raison.limite", "Déjà fait");
        }
        for (ConditionDataEntry c : bouton.conditions()) {
            if (c == null || c.conditionType() == null) {
                continue;
            }
            String symbole = c.operationType() == null ? "" : c.operationType().getSymbol();
            switch (c.conditionType()) {
                case SCOREBOARD -> {
                    String libelle = DialogueClientSettings.get().libelles_conditions.getOrDefault(c.name(), c.name());
                    return Text.translatableWithFallback("hcd.raison.score", "%s %s %s requis",
                            libelle, symbole, String.valueOf(c.value()));
                }
                case HAS_ITEM_IN_INVENTORY, HAS_ITEM_IN_HAND -> {
                    return Text.translatableWithFallback("hcd.raison.objet", "Objet requis : %s", c.name());
                }
                case ADVANCEMENT -> {
                    return Text.translatableWithFallback("hcd.raison.progres", "Progrès requis");
                }
                case EXPERIENCE_LEVEL -> {
                    return Text.translatableWithFallback("hcd.raison.niveau", "Niveau %s %s requis",
                            symbole, String.valueOf(c.value()));
                }
                case PLAYER_TAG, GAMEMODE, RELATIONSHIP, NPC_STATE -> {
                    return Text.translatableWithFallback("hcd.raison.statut", "Statut requis");
                }
                case TEAM -> {
                    return Text.translatableWithFallback("hcd.raison.equipe", "Équipe requise");
                }
                case PLAYER_HEALTH -> {
                    return Text.translatableWithFallback("hcd.raison.sante", "Santé insuffisante");
                }
                case EXECUTION_LIMIT -> {
                    return Text.translatableWithFallback("hcd.raison.limite", "Déjà fait");
                }
                case CUSTOM -> {
                    String id = c.customConditionId() == null ? "" : c.customConditionId().getPath().replace('_', ' ');
                    return Text.literal(id.isEmpty() ? "Condition requise" : id + " requis");
                }
                default -> {
                }
            }
        }
        return Text.translatableWithFallback("hcd.raison.autre", "Condition requise");
    }

    // ------------------------------------------------------------------------
    // Rendu
    // ------------------------------------------------------------------------

    @Override
    public void renderBackground(DrawContext context, int mouseX, int mouseY, float deltaTicks) {
        // Pas de flou ni d'assombrissement : le personnage doit rester visible.
    }

    @Override
    protected void drawBackground(DrawContext context, float deltaTicks, int mouseX, int mouseY) {
    }

    @Override
    public void render(DrawContext context, int mouseX, int mouseY, float deltaTicks) {
        if (this.invalide || this.dialogue == null || this.panneau == null) {
            return;
        }
        this.panneau.render(context, mouseX, mouseY, this.ticks);
    }

    // ------------------------------------------------------------------------
    // Entrées
    // ------------------------------------------------------------------------

    @Override
    public boolean mouseClicked(Click click, boolean doubled) {
        if (this.invalide) {
            return true;
        }
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
        if (this.invalide) {
            return true;
        }
        int code = input.getKeycode();
        if (input.isEscape()) {
            return super.keyPressed(input);
        }
        if (code == GLFW.GLFW_KEY_H) {
            this.panneau.basculerHistorique();
            return true;
        }
        if (input.isEnterOrSpace()) {
            if (!this.panneau.avancer() && this.visibles.size() == 1 && !this.visibles.get(0).verrouillee) {
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
            } else if (numero < this.visibles.size()) {
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
        return this.escAutorise;
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
        if (Boolean.getBoolean("hcd.selftest")) {
            HauteCapitaleDialogue.LOGGER.info("[ecran] choisir {} parmi {} (verrouillee={})", index, this.visibles.size(),
                    index >= 0 && index < this.visibles.size() ? this.visibles.get(index).verrouillee : "-");
        }
        if (this.invalide || index < 0 || index >= this.visibles.size()) {
            return;
        }
        Reponse r = this.visibles.get(index);
        if (r.verrouillee) {
            return;
        }
        son();
        DialogueClientState.noterReponse(r.libelle.getString());

        if (this.donnees.getActionEventSet().hasActionEvent(ActionEventType.ON_BUTTON_CLICK)) {
            NetworkMessageHandlerManager.getServerHandler()
                    .executeActionEvent(this.pnjUuid, ActionEventType.ON_BUTTON_CLICK);
        }
        if (r.entree.hasActionData()) {
            NetworkMessageHandlerManager.getServerHandler()
                    .executeDialogButtonAction(this.pnjUuid, this.dialogId, r.entree.id());
        } else {
            this.raisonFermeture = CloseReason.ADIEU;
            this.close();
        }
    }

    private void son() {
        if (this.client != null && DialogueClientSettings.get().son_reponse) {
            this.client.getSoundManager().play(PositionedSoundInstance.ui(SoundEvents.UI_BUTTON_CLICK, 0.8f));
        }
    }

    // ------------------------------------------------------------------------
    // Cycle de vie
    // ------------------------------------------------------------------------

    @Override
    protected void handledScreenTick() {
        super.handledScreenTick();
        this.ticks++;
        if (this.ticks % 20 == 0 && !this.invalide) {
            int avant = this.visibles.size();
            rafraichirVerrous();
            if (this.visibles.size() != avant) {
                this.panneau.choix(this.visibles);
            }
        }
    }

    @Override
    public void close() {
        if (Boolean.getBoolean("hcd.selftest")) {
            HauteCapitaleDialogue.LOGGER.info("[ecran] close (notifiee={}, raison={})", this.fermetureNotifiee, this.raisonFermeture);
        }
        if (!this.fermetureNotifiee && !this.invalide) {
            this.fermetureNotifiee = true;
            if (this.donnees != null && this.donnees.getActionEventSet().hasActionEvent(ActionEventType.ON_CLOSE_DIALOG)) {
                NetworkMessageHandlerManager.getServerHandler()
                        .executeActionEvent(this.pnjUuid, ActionEventType.ON_CLOSE_DIALOG);
            }
            DialogueClientState.demanderFin(this.raisonFermeture);
        }
        super.close();
    }

    @Override
    public void removed() {
        super.removed();
        DialogueClientState.onEcranFerme(this);
    }

    // ------------------------------------------------------------------------
    // Lecture, pour les tests et l'overlay
    // ------------------------------------------------------------------------

    @Override
    public String libelleDialogue() {
        return this.dialogue == null ? "" : this.dialogue.getLabel();
    }

    public List<Reponse> reponsesVisibles() {
        return List.copyOf(this.visibles);
    }

    @Override
    public List<String> reponsesDebug() {
        List<String> lignes = new ArrayList<>();
        for (int i = 0; i < this.visibles.size(); i++) {
            Reponse r = this.visibles.get(i);
            lignes.add((i + 1) + ". " + r.libelle.getString() + " [" + r.type + "]"
                    + (r.verrouillee ? " verrouillée" + (r.raison == null ? "" : " : " + r.raison.getString()) : ""));
        }
        return lignes;
    }

    public UUID pnjUuid() {
        return this.pnjUuid;
    }

    public boolean estInvalide() {
        return this.invalide;
    }

    public int page() {
        return this.panneau == null ? 0 : this.panneau.page();
    }

    public int pagesTotal() {
        return this.panneau == null ? 1 : this.panneau.pages();
    }
}
