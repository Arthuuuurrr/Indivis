package net.hautecapitale.dialogue.client.capture;

import net.fabricmc.fabric.api.client.message.v1.ClientReceiveMessageEvents;
import net.hautecapitale.dialogue.HauteCapitaleDialogue;
import net.hautecapitale.dialogue.client.DialogueClientSettings;
import net.hautecapitale.dialogue.client.DialogueClientState;
import net.hautecapitale.dialogue.client.screen.DialoguePanel;
import net.minecraft.text.ClickEvent;
import net.minecraft.text.Style;
import net.minecraft.text.Text;
import net.minecraft.text.TextColor;
import net.minecraft.text.TranslatableTextContent;
import org.jetbrains.annotations.Nullable;

import java.text.Normalizer;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Les dialogues qui arrivent par le chat — fonctions de datapack, panneaux des
 * transports — deviennent des répliques et des réponses.
 *
 * <p>Pendant une session en mode capture, chaque message de jeu reçu est
 * examiné : un message porteur de {@code click_event} est un jeu de réponses ;
 * un message qui commence par {@code [Nom du personnage]} ou {@code [Choix]} est
 * une réplique ; un message reçu dans la courte fenêtre qui suit le clic est
 * une réplique aussi (c'est la salve que produit la fonction) ; tout le reste
 * va au chat comme d'habitude. Rien n'est réécrit dans les datapacks.
 *
 * <p>Un clic sur une réponse rejoue le {@code click_event} exactement comme un
 * clic dans le chat, par le code vanilla. Un {@code /trigger} reste donc validé
 * par le serveur comme aujourd'hui.
 */
public final class MessageCapture {

    /** Un morceau de message et son style, tels que le texte les livre. */
    public record Segment(Style style, String texte) {
    }

    /** Une réponse capturée : son libellé, sa nuance, et ce qu'un clic doit faire. */
    public record ChoixCapture(Text libelle, DialoguePanel.Type type, ClickEvent evenement) implements DialoguePanel.Choix {
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
            return false;
        }

        @Override
        public Text raison() {
            return null;
        }
    }

    /** Ce qu'un message est devenu : une réplique, des réponses, ou rien. */
    public record Classification(@Nullable Text replique, List<ChoixCapture> choix, @Nullable String locuteur) {
        public boolean vide() {
            return this.replique == null && this.choix.isEmpty();
        }
    }

    private static final Pattern PREFIXE = Pattern.compile("^\\s*\\[([^\\]]{1,48})\\]\\s*(.*)$", Pattern.DOTALL);

    private MessageCapture() {
    }

    public static void init() {
        ClientReceiveMessageEvents.ALLOW_GAME.register(MessageCapture::onMessageDeJeu);
        HauteCapitaleDialogue.LOGGER.info("Capture des dialogues de chat prête.");
    }

    private static boolean onMessageDeJeu(Text message, boolean overlay) {
        if (overlay || !DialogueClientState.captureActive()) {
            return true;
        }
        DialogueClientSettings s = DialogueClientSettings.get();
        Classification c = classer(message, DialogueClientState.pnjNom(), DialogueClientState.dansFenetreCapture(),
                s.capture_prefixes_ignores);
        if (c == null || c.vide()) {
            return true;
        }
        DialogueClientState.onMessageCapture(c);
        return !s.capture_masquer_chat;
    }

    /**
     * Le tri, sans état : testable avec des textes construits à la main.
     *
     * @param message         le message de jeu reçu
     * @param nomPnj          le nom du personnage de la session
     * @param dansFenetre     sommes-nous dans la salve qui suit un clic ?
     * @param prefixesIgnores préfixes {@code [X]} qui doivent rester dans le chat
     * @return la classification, ou {@code null} si le message n'appartient pas à la conversation
     */
    @Nullable
    public static Classification classer(Text message, String nomPnj, boolean dansFenetre, List<String> prefixesIgnores) {
        if (estRetourDeCommande(message)) {
            // « Triggered [QuestChoix] (set value to 50) » : le retour du /trigger
            // que le choix vient de rejouer, pas une réplique du personnage.
            return null;
        }
        List<Segment> segments = segmenter(message);
        List<ChoixCapture> choix = extraireChoix(segments);
        String brut = message.getString();

        if (!choix.isEmpty()) {
            return new Classification(null, choix, null);
        }

        String plat = brut.strip();
        if (plat.isEmpty()) {
            return null;
        }
        Matcher m = PREFIXE.matcher(plat);
        String locuteur = null;
        String reste = plat;
        if (m.matches()) {
            locuteur = m.group(1).strip();
            reste = m.group(2).strip();
        }

        if (locuteur != null) {
            for (String ignore : prefixesIgnores) {
                if (ignore != null && normaliser(ignore).equals("[" + normaliser(locuteur) + "]")) {
                    return null;
                }
            }
            if (normaliser(locuteur).equals("choix")) {
                return reste.isEmpty() ? null : new Classification(sansPrefixe(message, m), List.of(), "choix");
            }
            if (nomPnj != null && !nomPnj.isBlank() && normaliser(locuteur).equals(normaliser(nomPnj))) {
                return new Classification(sansPrefixe(message, m), List.of(), locuteur);
            }
        }

        if (dansFenetre) {
            return new Classification(message, List.of(), locuteur);
        }
        return null;
    }

    /** Le retour d'une commande vanilla : une clé de traduction {@code commands.*}. */
    public static boolean estRetourDeCommande(Text message) {
        return message != null && message.getContent() instanceof TranslatableTextContent t
                && t.getKey() != null && t.getKey().startsWith("commands.");
    }

    /** Décompose un texte en segments stylés, les styles hérités compris. */
    public static List<Segment> segmenter(Text message) {
        List<Segment> segments = new ArrayList<>();
        message.visit((style, texte) -> {
            if (texte != null && !texte.isEmpty()) {
                segments.add(new Segment(style, texte));
            }
            return Optional.empty();
        }, Style.EMPTY);
        return segments;
    }

    /** Regroupe les segments consécutifs qui portent le même clic en une réponse. */
    static List<ChoixCapture> extraireChoix(List<Segment> segments) {
        List<ChoixCapture> choix = new ArrayList<>();
        ClickEvent courant = null;
        StringBuilder libelle = new StringBuilder();
        Style styleCourant = Style.EMPTY;
        for (Segment s : segments) {
            ClickEvent evenement = s.style().getClickEvent();
            if (evenement == null) {
                terminer(choix, courant, libelle, styleCourant);
                courant = null;
                continue;
            }
            if (courant == null || !courant.equals(evenement)) {
                terminer(choix, courant, libelle, styleCourant);
                courant = evenement;
                styleCourant = s.style();
            }
            libelle.append(s.texte());
        }
        terminer(choix, courant, libelle, styleCourant);
        return choix;
    }

    private static void terminer(List<ChoixCapture> choix, ClickEvent evenement, StringBuilder libelle, Style style) {
        if (evenement != null) {
            String texte = nettoyerLibelle(libelle.toString());
            if (!texte.isEmpty()) {
                choix.add(new ChoixCapture(Text.literal(texte), typeDe(style, texte), evenement));
            }
        }
        libelle.setLength(0);
    }

    /** « [Je vous suis.] » → « Je vous suis. » */
    static String nettoyerLibelle(String brut) {
        String t = brut.strip();
        if (t.startsWith("[") && t.endsWith("]") && t.length() >= 2) {
            t = t.substring(1, t.length() - 1).strip();
        }
        return t;
    }

    /** La nuance d'une réponse : la couleur choisie par l'auteur du datapack en dit assez. */
    static DialoguePanel.Type typeDe(Style style, String texte) {
        TextColor couleur = style.getColor();
        if (couleur != null) {
            int rgb = couleur.getRgb() & 0xFFFFFF;
            int r = (rgb >> 16) & 0xFF;
            int g = (rgb >> 8) & 0xFF;
            int b = rgb & 0xFF;
            if (g > 150 && r < 120 && b < 120) {
                return DialoguePanel.Type.QUETE;      // green, dark_green
            }
            if (r > 150 && g < 110 && b < 110) {
                return DialoguePanel.Type.DANGER;     // red, dark_red
            }
            if (g > 150 && b > 150 && r < 120) {
                return DialoguePanel.Type.TRANSPORT;  // aqua, dark_aqua
            }
            if (b > 150 && r < 120) {
                return DialoguePanel.Type.COMMERCE;   // blue, dark_blue
            }
            if (r > 180 && g > 120 && g < 220 && b < 100) {
                return DialoguePanel.Type.METIER;     // gold
            }
        }
        String bas = normaliser(texte);
        if (bas.startsWith("au revoir") || bas.startsWith("partir") || bas.startsWith("passer mon chemin")
                || bas.startsWith("adieu") || bas.startsWith("quitter")) {
            return DialoguePanel.Type.ADIEU;
        }
        return DialoguePanel.Type.NORMALE;
    }

    /** Le message sans son préfixe {@code [Nom]} : le premier segment est raccourci d'autant. */
    private static Text sansPrefixe(Text message, Matcher m) {
        String reste = m.group(2).strip();
        List<Segment> segments = segmenter(message);
        // On reconstruit a partir des segments pour garder les couleurs du datapack ;
        // le prefixe occupe le debut : on saute ce qu'il couvre.
        int aSauter = message.getString().length() - reste.length();
        var resultat = Text.empty();
        int position = 0;
        for (Segment s : segments) {
            int debut = position;
            position += s.texte().length();
            if (position <= aSauter) {
                continue;
            }
            String morceau = debut < aSauter ? s.texte().substring(aSauter - debut) : s.texte();
            resultat.append(Text.literal(morceau).setStyle(s.style()));
        }
        String verif = resultat.getString().strip();
        if (verif.isEmpty()) {
            return Text.literal(reste);
        }
        return resultat;
    }

    /** Minuscules, sans accents, espaces réduits : « Roch Vallet » et « roch vallet » sont égaux. */
    public static String normaliser(String s) {
        if (s == null) {
            return "";
        }
        String n = Normalizer.normalize(s, Normalizer.Form.NFD).replaceAll("\\p{M}+", "");
        return n.toLowerCase(Locale.ROOT).replaceAll("\\s+", " ").strip();
    }
}
