package net.hautecapitale.metiers.client;

import net.hautecapitale.metiers.HauteCapitaleMetiers;
import net.hautecapitale.metiers.npc.NpcRole;
import net.hautecapitale.metiers.npc.ProfessionEntry;
import net.hautecapitale.metiers.npc.ProfessionMenu;
import net.hautecapitale.metiers.npc.ProfessionNetwork;
import net.hautecapitale.metiers.npc.ProfessionScreenData;
import net.hautecapitale.metiers.repair.RepairData;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.minecraft.client.gui.Click;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.ingame.HandledScreen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.gui.widget.CheckboxWidget;
import net.minecraft.client.gui.widget.TextFieldWidget;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.text.OrderedText;
import net.minecraft.text.Text;
import net.minecraft.util.Colors;
import net.minecraft.util.Formatting;
import net.minecraft.util.Identifier;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Optional;

/**
 * L'écran de métier, identique pour les neuf rôles.
 *
 * <p>Dessiné à la main plutôt qu'avec une texture de fond : il n'y a aucun
 * emplacement d'inventaire à aligner, et un panneau tracé s'adapte à la
 * résolution du joueur au lieu d'imposer 176 × 166 pixels.
 *
 * <p>L'écran ne décide de rien. La liste, les libellés, l'accessibilité de
 * chaque ligne, la maîtrise et les multiplicateurs autorisés viennent tous du
 * serveur ; ici on trie, on filtre, on affiche, et un clic renvoie
 * l'identifiant de la recette — rien d'autre.
 *
 * <p>Les boutons des lignes ne sont pas des widgets : la liste défile sous un
 * ciseau, et des widgets ne suivraient pas. On dessine des rectangles et on
 * teste le clic soi-même.
 */
@Environment(EnvType.CLIENT)
public class ProfessionScreen extends HandledScreen<ProfessionMenu> {

    private static final int PANEL_WIDTH = 340;
    private static final int PANEL_HEIGHT = 230;
    /** Hauteur d'une ligne dont le descriptif tient sur une ligne de texte. */
    private static final int ROW_HEIGHT = 24;
    /** Chaque ligne de texte supplémentaire du descriptif allonge la ligne d'autant. */
    private static final int LINE_HEIGHT = 10;
    private static final int LIST_TOP = 58;
    private static final int LIST_LEFT = 8;
    /** Place réservée en bas : de quoi loger le bouton, ou juste la bordure. */
    private static final int MARGIN_WITH_BUTTON = 34;
    private static final int MARGIN_WITHOUT_BUTTON = 10;

    private static final int BUTTON_WIDTH = 46;
    private static final int SMALL_BUTTON_WIDTH = 22;
    private static final int BUTTON_HEIGHT = 16;

    private static final int COLOR_PANEL = 0xF01A1512;
    private static final int COLOR_BORDER = 0xFF6B5533;
    private static final int COLOR_ROW = 0x30FFFFFF;
    private static final int COLOR_ROW_LOCKED = 0x18FFFFFF;
    private static final int COLOR_TITLE = 0xFFE0B457;
    private static final int COLOR_DETAIL = 0xFFA0A0A0;
    private static final int COLOR_BUTTON = 0xFF3B6E3B;
    private static final int COLOR_BUTTON_HOVER = 0xFF4E8F4E;
    private static final int COLOR_BUTTON_OFF = 0xFF3A3A3A;
    private static final int COLOR_MASTERED = 0xFFD98CFF;
    private static final int COLOR_GAUGE = 0xFF8C6BB0;
    private static final int COLOR_GAUGE_BACK = 0x40FFFFFF;

    private ProfessionScreenData data;
    private final List<ProfessionEntry> visible = new ArrayList<>();
    // Les lignes n'ont pas toutes la même hauteur : un descriptif long — six
    // ingrédients, le prix, l'XP — se replie sur plusieurs lignes de texte
    // plutôt que d'être coupé. Haut et hauteur de chaque ligne, dans l'espace
    // de la liste, calculés à chaque reconstruction.
    private final List<Integer> rowTops = new ArrayList<>();
    private final List<Integer> rowHeights = new ArrayList<>();
    private int totalHeight;

    private TextFieldWidget search;
    private CheckboxWidget onlyAvailable;
    private ButtonWidget learn;
    private double scroll;

    // L'onglet Réparation, quand le rôle le propose : mêmes lignes, autre source.
    private boolean repairTab;
    private ButtonWidget tabCraft;
    private ButtonWidget tabRepair;
    private ButtonWidget repairAll;
    private final List<ProfessionEntry> repairRows = new ArrayList<>();

    public ProfessionScreen(ProfessionMenu handler, PlayerInventory inventory, Text title) {
        super(handler, inventory, title);
        this.data = handler.data();
        this.backgroundWidth = PANEL_WIDTH;
        this.backgroundHeight = PANEL_HEIGHT;
    }

    /**
     * Appelé quand le serveur renvoie un écran à jour, sans le refermer. La
     * position de défilement est gardée : après une fabrication, on veut
     * retrouver la ligne qu'on vient de cliquer, pas le haut de la liste.
     */
    public void accept(ProfessionScreenData fresh) {
        double kept = scroll;
        this.data = fresh;
        if (fresh.repair().isEmpty()) {
            repairTab = false;
        }
        buildRepairRows();
        rebuild();
        scroll = Math.clamp(kept, 0.0D, maxScroll());
        updateWidgets();
    }

    @Override
    protected void init() {
        super.init();
        this.titleX = 10;
        this.titleY = 10;

        search = new TextFieldWidget(this.textRenderer, this.x + 10, this.y + 34,
                PANEL_WIDTH - 130, 16,
                Text.translatableWithFallback("hcm.ecran.recherche", "Rechercher"));
        search.setPlaceholder(Text.translatableWithFallback("hcm.ecran.recherche", "Rechercher")
                .formatted(Formatting.DARK_GRAY));
        search.setChangedListener(text -> rebuild());
        addDrawableChild(search);

        onlyAvailable = CheckboxWidget.builder(
                        Text.translatableWithFallback("hcm.ecran.accessibles", "Accessibles"),
                        this.textRenderer)
                .pos(this.x + PANEL_WIDTH - 112, this.y + 33)
                .callback((checkbox, checked) -> rebuild())
                .build();
        addDrawableChild(onlyAvailable);

        learn = ButtonWidget.builder(
                        Text.translatableWithFallback("hcm.ecran.apprendre", "Apprendre ce métier"),
                        button -> ClientPlayNetworking.send(
                                new ProfessionNetwork.LearnRequest(data.role())))
                .dimensions(this.x + 10, this.y + PANEL_HEIGHT - 26, PANEL_WIDTH - 20, 18)
                .build();
        addDrawableChild(learn);

        tabCraft = ButtonWidget.builder(
                        Text.translatableWithFallback("hcm.ecran.onglet_fabrication", "Fabrication"),
                        button -> switchTab(false))
                .dimensions(this.x + PANEL_WIDTH - 156, this.y + 6, 72, 14)
                .build();
        tabRepair = ButtonWidget.builder(
                        Text.translatableWithFallback("hcm.ecran.onglet_reparation", "Réparation"),
                        button -> switchTab(true))
                .dimensions(this.x + PANEL_WIDTH - 82, this.y + 6, 72, 14)
                .build();
        addDrawableChild(tabCraft);
        addDrawableChild(tabRepair);

        repairAll = ButtonWidget.builder(Text.empty(),
                        button -> ClientPlayNetworking.send(new ProfessionNetwork.RepairRequest(-1)))
                .dimensions(this.x + 10, this.y + PANEL_HEIGHT - 26, PANEL_WIDTH - 20, 18)
                .build();
        addDrawableChild(repairAll);

        repairTab = data.repair().map(RepairData::suggested).orElse(false);
        buildRepairRows();
        rebuild();
        updateWidgets();
    }

    private void switchTab(boolean repair) {
        if (repairTab != repair) {
            repairTab = repair;
            rebuild();
            updateWidgets();
        }
    }

    /** Qui est visible, qui est actif — selon l'onglet et ce que le serveur a envoyé. */
    private void updateWidgets() {
        boolean repairs = data.repair().isPresent();
        if (tabCraft != null) {
            tabCraft.visible = repairs;
            tabRepair.visible = repairs;
            // L'onglet courant reste cliquable mais se marque : un bouton grisé se
            // lit « indisponible », pas « vous êtes ici ».
            Text craft = Text.translatableWithFallback("hcm.ecran.onglet_fabrication", "Fabrication");
            Text repair = Text.translatableWithFallback("hcm.ecran.onglet_reparation", "Réparation");
            tabCraft.setMessage(repairTab ? craft : Text.literal("▸ ").append(craft));
            tabRepair.setMessage(repairTab ? Text.literal("▸ ").append(repair) : repair);
        }
        if (onlyAvailable != null) {
            onlyAvailable.visible = !repairTab;
        }
        if (learn != null) {
            learn.visible = !repairTab && data.offersLearning();
        }
        if (repairAll != null) {
            RepairData repair = data.repair().orElse(null);
            repairAll.visible = repairTab && repair != null;
            if (repair != null) {
                repairAll.active = !repair.items().isEmpty() && repair.total() <= repair.balance();
                repairAll.setMessage(Text.translatableWithFallback("hcm.ecran.tout_reparer",
                        "Tout réparer — %s %s (vous avez %s)",
                        Text.literal(String.valueOf(repair.total())), repair.currencyName(),
                        Text.literal(String.valueOf(repair.balance()))));
            }
        }
    }

    /**
     * Les lignes de l'onglet Réparation, sous la même forme que les recettes :
     * une ligne d'information sur le solde, puis un objet par ligne, dans
     * l'ordre de l'inventaire, dont le bouton porte l'emplacement.
     */
    private void buildRepairRows() {
        repairRows.clear();
        RepairData repair = data.repair().orElse(null);
        if (repair == null) {
            return;
        }
        repairRows.add(new ProfessionEntry(
                Text.translatableWithFallback("hcm.ecran.solde", "Votre solde : %s %s",
                        Text.literal(String.valueOf(repair.balance())), repair.currencyName()),
                Text.translatableWithFallback("hcm.ecran.reparation_ouverte", "Réparation ouverte à tous, sans métier."),
                0, true));
        for (RepairData.RepairEntry item : repair.items()) {
            Text detail = item.broken()
                    ? Text.translatableWithFallback("hcm.ecran.brise_prix", "BRISÉ — %s %s",
                            Text.literal(String.valueOf(item.price())), repair.currencyName())
                    : Text.translatableWithFallback("hcm.ecran.durabilite_prix", "Durabilité %s %% — %s %s",
                            Text.literal(String.valueOf(item.percent())),
                            Text.literal(String.valueOf(item.price())), repair.currencyName());
            repairRows.add(new ProfessionEntry(item.label(), detail, 0, item.price() <= repair.balance(),
                    Optional.of(HauteCapitaleMetiers.id("reparation/" + item.slot())), 0, 0, 1));
        }
    }

    /**
     * Recalcule la liste affichée. Les lignes d'information restent en tête ;
     * puis ce que le joueur peut faire maintenant, les recettes maîtrisées et en
     * apprentissage d'abord ; puis le reste, par niveau requis. C'est l'ordre
     * dans lequel on cherche quand on veut agir.
     */
    private void rebuild() {
        String needle = search == null ? "" : search.getText().trim().toLowerCase(Locale.ROOT);
        boolean filtered = onlyAvailable != null && onlyAvailable.isChecked();

        visible.clear();
        List<ProfessionEntry> source = repairTab ? repairRows : data.entries();
        for (ProfessionEntry entry : source) {
            if (filtered && !repairTab && !entry.available() && entry.action().isPresent()) {
                continue;
            }
            if (!needle.isEmpty() && !entry.searchable().contains(needle)) {
                continue;
            }
            visible.add(entry);
        }
        if (repairTab) {
            // L'ordre de l'inventaire : c'est celui que le joueur a sous les yeux.
            layoutRows();
            scroll = 0.0D;
            return;
        }
        visible.sort((a, b) -> {
            if (a.action().isPresent() != b.action().isPresent()) {
                return a.action().isPresent() ? 1 : -1;
            }
            if (a.available() != b.available()) {
                return a.available() ? -1 : 1;
            }
            if (a.mastery() != b.mastery()) {
                return Integer.compare(b.mastery(), a.mastery());
            }
            if (a.requiredLevel() != b.requiredLevel()) {
                return Integer.compare(a.requiredLevel(), b.requiredLevel());
            }
            return a.label().getString().compareToIgnoreCase(b.label().getString());
        });
        layoutRows();
        scroll = 0.0D;
    }

    /**
     * Mesure chaque ligne : une ligne de texte pour le libellé, puis autant
     * qu'il en faut pour le descriptif replié à la largeur laissée par les
     * boutons. Rien n'est coupé ; la liste s'allonge et défile.
     */
    private void layoutRows() {
        rowTops.clear();
        rowHeights.clear();
        totalHeight = 0;
        for (ProfessionEntry entry : visible) {
            int lines = this.textRenderer == null ? 1
                    : Math.max(1, this.textRenderer.wrapLines(entry.detail(), textWidth(entry)).size());
            int height = ROW_HEIGHT + (lines - 1) * LINE_HEIGHT;
            rowTops.add(totalHeight);
            rowHeights.add(height);
            totalHeight += height;
        }
    }

    /** La largeur laissée au texte d'une ligne, à gauche de ses boutons. */
    private int textWidth(ProfessionEntry entry) {
        int textRight = entry.action().isPresent() ? buttonsLeft(entry) - 4 : PANEL_WIDTH - 13;
        return textRight - 13;
    }

    /** La liste occupe tout ce que le bouton ne prend pas. */
    private int listHeight() {
        int margin = repairTab || data.offersLearning() ? MARGIN_WITH_BUTTON : MARGIN_WITHOUT_BUTTON;
        return PANEL_HEIGHT - LIST_TOP - margin;
    }

    private int maxScroll() {
        return Math.max(0, totalHeight - listHeight());
    }

    /** Haut d'une ligne dans l'espace du panneau, défilement compris. */
    private int rowTop(int index) {
        return LIST_TOP + rowTops.get(index) - (int) scroll;
    }

    private int rowHeight(int index) {
        return rowHeights.get(index);
    }

    // ------------------------------------------------------------------
    // Rendu

    @Override
    protected void drawBackground(DrawContext context, float delta, int mouseX, int mouseY) {
        int left = this.x;
        int top = this.y;
        int right = left + PANEL_WIDTH;
        int bottom = top + PANEL_HEIGHT;

        context.fill(left, top, right, bottom, COLOR_PANEL);
        context.fill(left, top, right, top + 1, COLOR_BORDER);
        context.fill(left, bottom - 1, right, bottom, COLOR_BORDER);
        context.fill(left, top, left + 1, bottom, COLOR_BORDER);
        context.fill(right - 1, top, right, bottom, COLOR_BORDER);
    }

    @Override
    protected void drawForeground(DrawContext context, int mouseX, int mouseY) {
        context.drawText(this.textRenderer, this.title, this.titleX, this.titleY, COLOR_TITLE, false);

        data.greeting().ifPresent(greeting -> context.drawText(
                this.textRenderer, greeting, 10, 22, COLOR_DETAIL, false));

        if (visible.isEmpty()) {
            context.drawWrappedText(this.textRenderer, emptyMessage(),
                    14, LIST_TOP + 14, PANEL_WIDTH - 28, COLOR_DETAIL, false);
            return;
        }

        int top = LIST_TOP;
        int bottom = top + listHeight();
        // Coordonnées du panneau, pas de l'écran : Minecraft a déjà décalé la
        // matrice de (x, y), et enableScissor la respecte.
        context.enableScissor(LIST_LEFT, top, PANEL_WIDTH - LIST_LEFT, bottom);

        // La souris arrive en coordonnées d'écran ; le panneau en coordonnées
        // locales. On ramène la souris dans l'espace du panneau une fois.
        int localX = mouseX - this.x;
        int localY = mouseY - this.y;

        for (int index = 0; index < visible.size(); index++) {
            ProfessionEntry entry = visible.get(index);
            int rowY = rowTop(index);
            int rowHeight = rowHeight(index);
            if (rowY + rowHeight < top || rowY > bottom) {
                continue;
            }
            drawRow(context, entry, rowY, rowHeight, localX, localY, localY >= top && localY < bottom);
        }

        context.disableScissor();
    }

    private void drawRow(DrawContext context, ProfessionEntry entry, int rowY, int rowHeight,
                         int localX, int localY, boolean mouseInList) {
        context.fill(LIST_LEFT, rowY, PANEL_WIDTH - LIST_LEFT, rowY + rowHeight - 2,
                entry.available() ? COLOR_ROW : COLOR_ROW_LOCKED);

        int textWidth = textWidth(entry);
        int textRight = 13 + textWidth;

        int labelColor = entry.available() ? Colors.WHITE : COLOR_DETAIL;
        Text label = entry.isMastered()
                ? Text.literal("✦ ").formatted(Formatting.LIGHT_PURPLE).append(entry.label())
                : entry.label();
        context.drawText(this.textRenderer, this.textRenderer.trimToWidth(label.getString(), textWidth),
                13, rowY + 4, entry.isMastered() ? COLOR_MASTERED : labelColor, false);
        // Le descriptif entier, replié : la hauteur de la ligne a été calculée pour lui.
        int lineY = rowY + 14;
        for (OrderedText line : this.textRenderer.wrapLines(entry.detail(), textWidth)) {
            context.drawText(this.textRenderer, line, 13, lineY, COLOR_DETAIL, false);
            lineY += LINE_HEIGHT;
        }

        if (entry.action().isEmpty()) {
            if (entry.requiredLevel() > 0) {
                drawLevelTag(context, entry, rowY, PANEL_WIDTH - 13);
            }
            return;
        }

        // Jauge d'apprentissage : discrète, sous le libellé, à droite.
        if (entry.isLearning() && data.masteryTarget() > 0) {
            int gaugeLeft = textRight - 40;
            int filled = (int) (40.0D * Math.min(entry.masteryCount(), data.masteryTarget()) / data.masteryTarget());
            context.fill(gaugeLeft, rowY + 3, gaugeLeft + 40, rowY + 6, COLOR_GAUGE_BACK);
            context.fill(gaugeLeft, rowY + 3, gaugeLeft + filled, rowY + 6, COLOR_GAUGE);
        }

        // Boutons : Fabriquer, puis ×5 et ×10 si la maîtrise les ouvre.
        int bx = buttonsLeft(entry);
        int by = rowY + 3;
        boolean inn = entry.action().map(action -> action.getPath().startsWith("foyer/")).orElse(false);
        drawButton(context, bx, by, BUTTON_WIDTH,
                repairTab ? Text.translatableWithFallback("hcm.ecran.reparer", "Réparer")
                        : inn ? Text.translatableWithFallback("hcm.ecran.choisir", "Choisir")
                        : Text.translatableWithFallback("hcm.ecran.fabriquer", "Fabriquer"),
                entry.available(), mouseInList && hovering(localX, localY, bx, by, BUTTON_WIDTH));
        if (entry.batches() >= 5) {
            int b5 = bx + BUTTON_WIDTH + 2;
            drawButton(context, b5, by, SMALL_BUTTON_WIDTH, Text.literal("×5"),
                    entry.available(), mouseInList && hovering(localX, localY, b5, by, SMALL_BUTTON_WIDTH));
        }
        if (entry.batches() >= 10) {
            int b10 = bx + BUTTON_WIDTH + 2 + SMALL_BUTTON_WIDTH + 2;
            drawButton(context, b10, by, SMALL_BUTTON_WIDTH, Text.literal("×10"),
                    entry.available(), mouseInList && hovering(localX, localY, b10, by, SMALL_BUTTON_WIDTH));
        }
    }

    private void drawLevelTag(DrawContext context, ProfessionEntry entry, int rowY, int right) {
        Text level = Text.literal("niv. " + entry.requiredLevel());
        int width = this.textRenderer.getWidth(level);
        context.drawText(this.textRenderer, level, right - width, rowY + 9, COLOR_DETAIL, false);
    }

    private void drawButton(DrawContext context, int bx, int by, int width, Text label,
                            boolean enabled, boolean hovered) {
        int color = !enabled ? COLOR_BUTTON_OFF : hovered ? COLOR_BUTTON_HOVER : COLOR_BUTTON;
        context.fill(bx, by, bx + width, by + BUTTON_HEIGHT, color);
        int textWidth = this.textRenderer.getWidth(label);
        context.drawText(this.textRenderer, label, bx + (width - textWidth) / 2, by + 4,
                enabled ? Colors.WHITE : COLOR_DETAIL, false);
    }

    /** Où commencent les boutons d'une ligne : ils s'alignent sur le bord droit. */
    private int buttonsLeft(ProfessionEntry entry) {
        int total = BUTTON_WIDTH;
        if (entry.batches() >= 5) {
            total += 2 + SMALL_BUTTON_WIDTH;
        }
        if (entry.batches() >= 10) {
            total += 2 + SMALL_BUTTON_WIDTH;
        }
        return PANEL_WIDTH - LIST_LEFT - 4 - total;
    }

    private static boolean hovering(int localX, int localY, int bx, int by, int width) {
        return localX >= bx && localX < bx + width && localY >= by && localY < by + BUTTON_HEIGHT;
    }

    /** Un écran vide doit dire pourquoi il est vide. */
    private Text emptyMessage() {
        if (repairTab) {
            return Text.translatableWithFallback("hcm.ecran.rien_a_reparer",
                    "Rien à réparer : tout votre équipement est en état.");
        }
        if (!data.entries().isEmpty()) {
            return Text.translatableWithFallback("hcm.ecran.aucun_resultat",
                    "Aucune ligne ne correspond à votre recherche.");
        }
        if (data.screen() == NpcRole.Interface.ATELIER) {
            return Text.translatableWithFallback("hcm.ecran.atelier_vide",
                    "Cet artisan n'a pas encore de recette à proposer.");
        }
        return Text.translatableWithFallback("hcm.ecran.rien", "Rien à afficher pour l'instant.");
    }

    // ------------------------------------------------------------------
    // Entrées

    // 1.21.11 : le clic arrive dans un objet Click (position + bouton + modificateurs).
    @Override
    public boolean mouseClicked(Click click, boolean doubled) {
        if (click.button() == 0 && clickRow(click.x() - this.x, click.y() - this.y)) {
            return true;
        }
        return super.mouseClicked(click, doubled);
    }

    /** Un clic sur un bouton de ligne envoie la recette et le multiplicateur ; rien d'autre. */
    private boolean clickRow(double localX, double localY) {
        int top = LIST_TOP;
        int bottom = top + listHeight();
        if (localY < top || localY >= bottom) {
            return false;
        }

        for (int index = 0; index < visible.size(); index++) {
            ProfessionEntry entry = visible.get(index);
            if (entry.action().isEmpty()) {
                continue;
            }
            int rowY = rowTop(index);
            int by = rowY + 3;
            if (localY < by || localY >= by + BUTTON_HEIGHT) {
                continue;
            }

            int bx = buttonsLeft(entry);
            int times = 0;
            if (localX >= bx && localX < bx + BUTTON_WIDTH) {
                times = 1;
            } else if (entry.batches() >= 5
                    && localX >= bx + BUTTON_WIDTH + 2 && localX < bx + BUTTON_WIDTH + 2 + SMALL_BUTTON_WIDTH) {
                times = 5;
            } else if (entry.batches() >= 10
                    && localX >= bx + BUTTON_WIDTH + 4 + SMALL_BUTTON_WIDTH
                    && localX < bx + BUTTON_WIDTH + 4 + 2 * SMALL_BUTTON_WIDTH) {
                times = 10;
            }

            if (times > 0) {
                if (entry.available()) {
                    Identifier action = entry.action().get();
                    if (repairTab && action.getPath().startsWith("reparation/")) {
                        int slot = Integer.parseInt(action.getPath().substring("reparation/".length()));
                        ClientPlayNetworking.send(new ProfessionNetwork.RepairRequest(slot));
                    } else {
                        ClientPlayNetworking.send(new ProfessionNetwork.CraftRequest(action, times));
                    }
                }
                return true;
            }
        }
        return false;
    }

    @Override
    public boolean mouseScrolled(double mouseX, double mouseY, double horizontal, double vertical) {
        if (maxScroll() > 0) {
            scroll = Math.clamp(scroll - vertical * ROW_HEIGHT, 0.0D, maxScroll());
            return true;
        }
        return super.mouseScrolled(mouseX, mouseY, horizontal, vertical);
    }

    @Override
    public boolean shouldPause() {
        return false;
    }
}
