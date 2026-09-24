package net.hautecapitale.dialogue.client.screen;

import net.hautecapitale.dialogue.client.DialogueClientSettings;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.text.OrderedText;
import net.minecraft.text.Text;
import org.jetbrains.annotations.Nullable;

import java.text.Normalizer;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Le bandeau de dialogue lui-même — nom, réplique, réponses — indépendant de
 * l'origine du texte.
 *
 * <p>Partagé par l'écran des dialogues Easy NPC et par celui des dialogues
 * capturés : un seul rendu, une seule machine à écrire, une seule pagination,
 * une seule façon de cliquer. Le panneau ne sait pas ce qu'une réponse fait ;
 * il dit seulement laquelle a été choisie.
 *
 * <p>Toutes les coordonnées internes sont en « unités locales » : le contenu
 * est dessiné dans une matrice translatée en haut de la colonne et mise à
 * l'échelle du réglage {@code echelle_texte}. Les tests de survol ramènent la
 * souris dans ce repère.
 */
public final class DialoguePanel {

    /** Ce qu'une réponse fait, pour la nuance de couleur. Déduit, ou annoté par une étiquette. */
    public enum Type {
        NORMALE(0xFFE8E1CF),
        QUETE(0xFF9CD9A8),
        COMMERCE(0xFF9EC1E6),
        METIER(0xFFE3C46B),
        TRANSPORT(0xFF9ED2CD),
        DANGER(0xFFE08A7A),
        ADIEU(0xFFCFC6B2);

        public final int couleur;

        Type(int couleur) {
            this.couleur = couleur;
        }

        /** La couleur réglée par le joueur pour ce type, ou celle par défaut. */
        public int couleurReglee() {
            DialogueClientSettings.Style s = DialogueClientSettings.get().styles_reponses.get(name());
            Integer c = s == null ? null : couleurDepuis(s.couleur);
            return c == null ? this.couleur : c;
        }

        /** Le préfixe réglé pour ce type ({@code "[Quête] "}), ou rien. */
        public String prefixe() {
            DialogueClientSettings.Style s = DialogueClientSettings.get().styles_reponses.get(name());
            return s == null || s.prefixe == null ? "" : s.prefixe;
        }
    }

    /** Une réponse, vue du panneau. */
    public interface Choix {
        Text libelle();

        Type type();

        boolean verrouille();

        @Nullable
        Text raison();
    }

    /**
     * Une étiquette de style en tête d'un libellé : {@code [quête] Accepter} →
     * type QUÊTE, libellé « Accepter ». {@code type} vaut {@code null} si le
     * libellé n'en porte pas ; le reste est alors le libellé intact.
     */
    public record Etiquette(@Nullable Type type, String reste) {
    }

    private static final Pattern ETIQUETTE = Pattern.compile("^\\s*[\\[(]\\s*([^\\])]{1,16})\\s*[\\])]\\s*(.*)$", Pattern.DOTALL);

    /** Reconnaît une étiquette de style en tête d'un libellé de réponse. */
    public static Etiquette etiquette(String brut) {
        if (brut == null) {
            return new Etiquette(null, "");
        }
        Matcher m = ETIQUETTE.matcher(brut);
        if (!m.matches()) {
            return new Etiquette(null, brut);
        }
        String tag = Normalizer.normalize(m.group(1), Normalizer.Form.NFD).replaceAll("\\p{M}+", "")
                .toLowerCase(Locale.ROOT).strip();
        Type type = switch (tag) {
            case "normal", "normale" -> Type.NORMALE;
            case "quete", "quest" -> Type.QUETE;
            case "commerce", "boutique", "trade" -> Type.COMMERCE;
            case "metier", "job" -> Type.METIER;
            case "transport", "voyage" -> Type.TRANSPORT;
            case "danger", "menace" -> Type.DANGER;
            case "adieu", "conge", "bye" -> Type.ADIEU;
            default -> null;
        };
        return type == null ? new Etiquette(null, brut) : new Etiquette(type, m.group(2).strip());
    }

    /** {@code "#RRGGBB"} ou {@code "RRGGBB"} → couleur opaque, ou {@code null}. */
    @Nullable
    public static Integer couleurDepuis(String texte) {
        if (texte == null) {
            return null;
        }
        String t = texte.strip();
        if (t.startsWith("#")) {
            t = t.substring(1);
        }
        if (t.length() != 6) {
            return null;
        }
        try {
            return 0xFF000000 | Integer.parseInt(t, 16);
        } catch (NumberFormatException e) {
            return null;
        }
    }

    // --- géométrie, en unités locales -------------------------------------------

    private static final int LARGEUR_MAX = 520;
    private static final int MARGE_MIN = 24;
    private static final int LIGNE = 11;
    private static final int REPONSE_H = 13;
    private static final int NOM_Y = 10;
    private static final int TEXTE_Y = 26;

    // --- couleurs : parchemin sombre et laiton ----------------------------------

    private static final int FOND = 0x14100C;
    private static final int LAITON = 0xFFB08D3C;
    private static final int LAITON_CLAIR = 0xFFE0B457;
    private static final int TEXTE = 0xFFF0EAD8;
    private static final int TEXTE_SOMBRE = 0xFFB9B1A0;
    private static final int GRIS = 0xFF8A8477;
    private static final int BLANC = 0xFFFFFFFF;

    private final TextRenderer textRenderer;

    private String nom = "";
    private final List<Text> paragraphes = new ArrayList<>();
    private final List<Choix> choix = new ArrayList<>();
    private boolean escAutorise = true;
    private boolean attente;

    private final List<OrderedText> lignes = new ArrayList<>();
    private final List<Integer> longueurs = new ArrayList<>();

    private boolean typewriter;
    private int caracteresParSeconde = 40;
    private long debutPageMs = System.currentTimeMillis();
    private boolean revele;
    private int page;
    private int lignesParPage = 4;
    private int survol = -1;

    private final List<String> historique = new ArrayList<>();
    private boolean historiqueVisible;

    private float echelle = 1.0f;
    private int largeur;
    private int hauteur;
    private int panneauHaut;
    private int colonneGauche;
    private int colonneLargeur;
    private int largeurLocale;
    private int hauteurLocale;
    private int reponsesHautLocal;

    public DialoguePanel(TextRenderer textRenderer) {
        this.textRenderer = textRenderer;
    }

    // ------------------------------------------------------------------------
    // Contenu
    // ------------------------------------------------------------------------

    public void nom(String nom) {
        this.nom = nom == null ? "" : nom.toUpperCase(Locale.ROOT);
    }

    /**
     * Remplace le texte. Chaque paragraphe est une réplique ; ils s'enchaînent avec un blanc.
     *
     * @param remise repartir de la première page, machine à écrire au début ;
     *               sinon la lecture en cours continue et ce qui s'ajoute
     *               apparaît à la suite
     */
    public void paragraphes(List<Text> nouveaux, boolean remise) {
        this.paragraphes.clear();
        this.paragraphes.addAll(nouveaux);
        if (remise || this.lignes.isEmpty()) {
            this.page = 0;
            this.revele = false;
            this.debutPageMs = System.currentTimeMillis();
        }
        envelopper();
    }

    public void choix(List<? extends Choix> nouveaux) {
        this.choix.clear();
        this.choix.addAll(nouveaux);
        disposerContenu();
    }

    public void escAutorise(boolean valeur) {
        this.escAutorise = valeur;
    }

    /** Aucune réplique encore : le panneau montre trois points. */
    public void attente(boolean valeur) {
        this.attente = valeur;
    }

    public void typewriter(boolean actif, int caracteresParSeconde) {
        this.typewriter = actif;
        this.caracteresParSeconde = Math.max(1, caracteresParSeconde);
    }

    /** Les dernières répliques et réponses, pour la touche H. */
    public void historique(List<String> entrees) {
        this.historique.clear();
        this.historique.addAll(entrees);
    }

    public void basculerHistorique() {
        this.historiqueVisible = !this.historiqueVisible;
    }

    public boolean historiqueVisible() {
        return this.historiqueVisible;
    }

    public List<Choix> choix() {
        return List.copyOf(this.choix);
    }

    public int page() {
        return this.page;
    }

    public int pages() {
        return Math.max(1, (this.lignes.size() + this.lignesParPage - 1) / this.lignesParPage);
    }

    public boolean dernierePage() {
        return this.page >= pages() - 1;
    }

    public int survol() {
        return this.survol;
    }

    public boolean vide() {
        return this.paragraphes.isEmpty();
    }

    // ------------------------------------------------------------------------
    // Disposition
    // ------------------------------------------------------------------------

    /** À appeler à l'ouverture et à chaque redimensionnement. */
    public void disposer(int largeur, int hauteur) {
        this.largeur = largeur;
        this.hauteur = hauteur;
        this.echelle = (float) DialogueClientSettings.get().echelle_texte;
        int hauteurPanneau = Math.max(120, Math.min(230, (int) (hauteur * 0.40)));
        this.panneauHaut = hauteur - hauteurPanneau;
        this.colonneLargeur = Math.min(largeur - 2 * MARGE_MIN, LARGEUR_MAX);
        this.colonneGauche = (largeur - this.colonneLargeur) / 2;
        this.largeurLocale = (int) (this.colonneLargeur / this.echelle);
        this.hauteurLocale = (int) (hauteurPanneau / this.echelle);
        envelopper();
    }

    private void envelopper() {
        this.lignes.clear();
        this.longueurs.clear();
        if (this.largeurLocale <= 0) {
            return;
        }
        boolean premier = true;
        for (Text paragraphe : this.paragraphes) {
            if (!premier) {
                this.lignes.add(OrderedText.EMPTY);
                this.longueurs.add(0);
            }
            premier = false;
            for (OrderedText ligne : this.textRenderer.wrapLines(paragraphe, this.largeurLocale - 8)) {
                this.lignes.add(ligne);
                this.longueurs.add(longueur(ligne));
            }
        }
        disposerContenu();
    }

    private void disposerContenu() {
        int hauteurReponses = this.choix.isEmpty() ? 0 : this.choix.size() * REPONSE_H + 2;
        this.reponsesHautLocal = this.hauteurLocale - 16 - hauteurReponses;
        int dispo = this.reponsesHautLocal - 4 - TEXTE_Y;
        this.lignesParPage = Math.max(2, dispo / LIGNE);
        this.page = Math.min(this.page, Math.max(0, pages() - 1));
    }

    /** Nombre de caractères d'une ligne, en points de code. */
    public static int longueur(OrderedText ligne) {
        int[] compte = {0};
        ligne.accept((index, style, codePoint) -> {
            compte[0]++;
            return true;
        });
        return compte[0];
    }

    /** Les {@code max} premiers caractères d'une ligne, styles compris. */
    public static OrderedText limiter(OrderedText ligne, int max) {
        return max <= 0 ? OrderedText.EMPTY : sink -> {
            int[] compte = {0};
            return ligne.accept((index, style, codePoint) -> compte[0]++ < max && sink.accept(index, style, codePoint));
        };
    }

    // ------------------------------------------------------------------------
    // Rendu
    // ------------------------------------------------------------------------

    public void render(DrawContext context, int mouseX, int mouseY, int ticks) {
        int alpha = (int) (DialogueClientSettings.get().opacite_fond * 255.0) & 0xFF;
        int fond = (alpha << 24) | FOND;
        context.fillGradient(0, this.panneauHaut - 28, this.largeur, this.panneauHaut, FOND, fond);
        context.fill(0, this.panneauHaut, this.largeur, this.hauteur, fond);

        context.fill(this.colonneGauche, this.panneauHaut + 5, this.colonneGauche + this.colonneLargeur,
                this.panneauHaut + 6, LAITON);
        context.fill(this.colonneGauche - 3, this.panneauHaut + 4, this.colonneGauche, this.panneauHaut + 7, LAITON);
        context.fill(this.colonneGauche + this.colonneLargeur, this.panneauHaut + 4,
                this.colonneGauche + this.colonneLargeur + 3, this.panneauHaut + 7, LAITON);

        this.survol = choixSous(mouseX, mouseY);

        if (this.historiqueVisible) {
            dessinerHistorique(context, fond);
        }

        context.getMatrices().pushMatrix();
        context.getMatrices().translate((float) this.colonneGauche, (float) this.panneauHaut);
        context.getMatrices().scale(this.echelle, this.echelle);
        dessinerNom(context);
        if (this.attente && this.lignes.isEmpty()) {
            boolean clignote = (ticks / 8) % 2 == 0;
            context.drawText(this.textRenderer, clignote ? "…" : " ", 4, TEXTE_Y, TEXTE_SOMBRE, true);
        } else {
            dessinerTexte(context);
        }
        if (dernierePage()) {
            dessinerReponses(context);
        } else {
            dessinerContinuer(context, ticks);
        }
        dessinerIndications(context);
        context.getMatrices().popMatrix();

        if (this.survol >= 0 && this.survol < this.choix.size()) {
            Choix c = this.choix.get(this.survol);
            if (c.verrouille() && c.raison() != null) {
                context.drawTooltip(c.raison(), mouseX, mouseY);
            }
        }
    }

    private void dessinerNom(DrawContext context) {
        int x = 4;
        for (int i = 0; i < this.nom.length(); i++) {
            String c = String.valueOf(this.nom.charAt(i));
            context.drawText(this.textRenderer, c, x, NOM_Y, LAITON_CLAIR, true);
            x += this.textRenderer.getWidth(c) + 1;
        }
    }

    private void dessinerTexte(DrawContext context) {
        if (this.lignes.isEmpty()) {
            return;
        }
        int debut = this.page * this.lignesParPage;
        int fin = Math.min(this.lignes.size(), debut + this.lignesParPage);
        int reveles = caracteresReveles();
        int consommes = 0;
        for (int i = debut; i < fin; i++) {
            int longueur = this.longueurs.get(i);
            if (longueur == 0) {
                continue;
            }
            int montrer = Math.min(longueur, Math.max(0, reveles - consommes));
            OrderedText ligne = montrer >= longueur ? this.lignes.get(i) : limiter(this.lignes.get(i), montrer);
            context.drawText(this.textRenderer, ligne, 4, TEXTE_Y + (i - debut) * LIGNE, TEXTE, true);
            consommes += longueur;
        }
    }

    private void dessinerReponses(DrawContext context) {
        for (int i = 0; i < this.choix.size(); i++) {
            Choix c = this.choix.get(i);
            int y = this.reponsesHautLocal + i * REPONSE_H;
            boolean survole = i == this.survol && !c.verrouille();

            if (survole) {
                context.fill(0, y - 1, this.largeurLocale, y + REPONSE_H - 2, 0x30E0B457);
                context.drawText(this.textRenderer, "▸", 2, y + 1, LAITON_CLAIR, false);
            }
            context.drawText(this.textRenderer, String.valueOf(i + 1), 12, y + 1, c.verrouille() ? GRIS : LAITON_CLAIR, true);

            int couleur = c.verrouille() ? GRIS : (survole ? BLANC : c.type().couleurReglee());
            int x = 24;
            String prefixe = c.type().prefixe();
            if (!prefixe.isEmpty()) {
                context.drawText(this.textRenderer, prefixe, x, y + 1, c.verrouille() ? GRIS : c.type().couleurReglee(), true);
                x += this.textRenderer.getWidth(prefixe);
            }
            context.drawText(this.textRenderer, c.libelle(), x, y + 1, couleur, true);

            if (c.verrouille()) {
                int droite = this.largeurLocale - 4;
                dessinerCadenas(context, droite - 8, y + 1);
                if (c.raison() != null) {
                    String raison = "[" + c.raison().getString() + "]";
                    int largeurRaison = this.textRenderer.getWidth(raison);
                    context.drawText(this.textRenderer, raison, droite - 12 - largeurRaison, y + 1, GRIS, true);
                }
            }
        }
    }

    /** Un cadenas de 7 × 8 pixels, au pixel. */
    private static void dessinerCadenas(DrawContext context, int x, int y) {
        context.fill(x + 2, y, x + 5, y + 1, GRIS);
        context.fill(x + 1, y + 1, x + 2, y + 3, GRIS);
        context.fill(x + 5, y + 1, x + 6, y + 3, GRIS);
        context.fill(x, y + 3, x + 7, y + 8, GRIS);
    }

    private void dessinerContinuer(DrawContext context, int ticks) {
        Text t = Text.translatableWithFallback("hcd.ecran.continuer", "Continuer ▸");
        int largeurTexte = this.textRenderer.getWidth(t);
        boolean clignote = (ticks / 10) % 2 == 0;
        context.drawText(this.textRenderer, t, this.largeurLocale - 4 - largeurTexte, this.reponsesHautLocal + 2,
                clignote ? LAITON_CLAIR : TEXTE_SOMBRE, true);
    }

    private void dessinerIndications(DrawContext context) {
        int y = this.hauteurLocale - 11;
        int x = this.largeurLocale - 4;
        // L'ecran ne se referme plus tout seul : quand plus rien n'est attendu
        // du joueur, la sortie s'allume pour qu'il sache par ou rendre la main.
        if (this.escAutorise) {
            String quitter = Text.translatableWithFallback("hcd.ecran.quitter", "Échap · quitter").getString();
            x -= this.textRenderer.getWidth(quitter);
            boolean sortie = dernierePage() && this.choix.isEmpty();
            context.drawText(this.textRenderer, quitter, x, y, sortie ? LAITON_CLAIR : TEXTE_SOMBRE, false);
            x -= 12;
        }
        if (!this.historique.isEmpty()) {
            String histo = Text.translatableWithFallback("hcd.ecran.historique", "H · précédemment").getString();
            x -= this.textRenderer.getWidth(histo);
            context.drawText(this.textRenderer, histo, x, y, TEXTE_SOMBRE, false);
        }
        if (dernierePage() && !this.choix.isEmpty()) {
            String gauche = Text.translatableWithFallback("hcd.ecran.choisir", "1–9 · choisir").getString();
            context.drawText(this.textRenderer, gauche, 4, y, TEXTE_SOMBRE, false);
        }
    }

    /** « Précédemment » : les dernières répliques et réponses, au-dessus du bandeau, en retrait. */
    private void dessinerHistorique(DrawContext context, int fond) {
        List<OrderedText> lignesHisto = new ArrayList<>();
        for (String entree : this.historique) {
            lignesHisto.addAll(this.textRenderer.wrapLines(Text.literal(entree), this.largeurLocale - 8));
        }
        int max = 10;
        if (lignesHisto.size() > max) {
            lignesHisto = lignesHisto.subList(lignesHisto.size() - max, lignesHisto.size());
        }
        int hauteurHisto = (int) ((lignesHisto.size() * LIGNE + 22) * this.echelle);
        int haut = this.panneauHaut - 28 - hauteurHisto;
        context.fill(this.colonneGauche - 6, haut, this.colonneGauche + this.colonneLargeur + 6, this.panneauHaut - 28, fond);
        context.fill(this.colonneGauche, haut + 4, this.colonneGauche + this.colonneLargeur, haut + 5, LAITON);

        context.getMatrices().pushMatrix();
        context.getMatrices().translate((float) this.colonneGauche, (float) haut);
        context.getMatrices().scale(this.echelle, this.echelle);
        String titre = Text.translatableWithFallback("hcd.ecran.precedemment", "PRÉCÉDEMMENT").getString();
        context.drawText(this.textRenderer, titre, 4, 9, LAITON_CLAIR, true);
        int y = 20;
        for (OrderedText ligne : lignesHisto) {
            context.drawText(this.textRenderer, ligne, 4, y, TEXTE_SOMBRE, true);
            y += LIGNE;
        }
        context.getMatrices().popMatrix();
    }

    // ------------------------------------------------------------------------
    // Machine à écrire et pagination
    // ------------------------------------------------------------------------

    private int caracteresReveles() {
        if (!this.typewriter || this.revele) {
            return Integer.MAX_VALUE;
        }
        long ecoule = System.currentTimeMillis() - this.debutPageMs;
        return (int) (ecoule * this.caracteresParSeconde / 1000L);
    }

    private int caracteresPage() {
        int debut = this.page * this.lignesParPage;
        int fin = Math.min(this.lignes.size(), debut + this.lignesParPage);
        int total = 0;
        for (int i = debut; i < fin; i++) {
            total += this.longueurs.get(i);
        }
        return total;
    }

    public boolean typewriterActif() {
        return this.typewriter && !this.revele && caracteresReveles() < caracteresPage();
    }

    /** Clic ou touche : tout révéler d'abord, puis page suivante. Vrai si quelque chose a été consommé. */
    public boolean avancer() {
        if (typewriterActif()) {
            this.revele = true;
            return true;
        }
        if (!dernierePage()) {
            this.page++;
            this.revele = false;
            this.debutPageMs = System.currentTimeMillis();
            return true;
        }
        return false;
    }

    public void pagePrecedente() {
        if (this.page > 0) {
            this.page--;
            this.revele = true;
        }
    }

    // ------------------------------------------------------------------------
    // Souris
    // ------------------------------------------------------------------------

    /** Index de la réponse sous la souris, ou -1. */
    public int choixSous(double mouseX, double mouseY) {
        if (!dernierePage() || this.choix.isEmpty()) {
            return -1;
        }
        double lx = (mouseX - this.colonneGauche) / this.echelle;
        double ly = (mouseY - this.panneauHaut) / this.echelle;
        if (lx < 0 || lx > this.largeurLocale || ly < this.reponsesHautLocal - 1) {
            return -1;
        }
        int index = (int) ((ly - this.reponsesHautLocal + 1) / REPONSE_H);
        return index >= 0 && index < this.choix.size() ? index : -1;
    }
}
