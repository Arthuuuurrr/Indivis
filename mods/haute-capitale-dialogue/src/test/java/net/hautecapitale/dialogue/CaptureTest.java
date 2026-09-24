package net.hautecapitale.dialogue;

import net.hautecapitale.dialogue.client.capture.MessageCapture;
import net.hautecapitale.dialogue.client.capture.MessageCapture.Classification;
import net.hautecapitale.dialogue.client.screen.DialoguePanel;
import net.minecraft.text.ClickEvent;
import net.minecraft.text.Style;
import net.minecraft.text.Text;
import net.minecraft.text.TextColor;
import net.minecraft.util.Formatting;

import java.util.List;

/**
 * Harnais du tri des messages de chat — un {@code main()} classique.
 *
 * <p>Les messages sont construits exactement comme les {@code tellraw} du
 * datapack les produisent : un préfixe {@code [Nom]} coloré, un texte, ou une
 * ligne {@code [Choix]} portant un {@code click_event}. Aucun monde, aucun
 * réseau : la fonction de tri est pure.
 */
public final class CaptureTest {

    private static int reussis = 0;
    private static int echecs = 0;

    private static final String PNJ = "Roch Vallet";
    private static final List<String> IGNORES = List.of("[Quête]", "[Système]");
    private static final ClickEvent SUIVRE = new ClickEvent.RunCommand("/trigger QuestChoix set 50");
    private static final ClickEvent REFUSER = new ClickEvent.RunCommand("/trigger QuestChoix set 51");
    private static final ClickEvent BILLET = new ClickEvent.RunCommand("/ferryticket buy");

    public static void main(String[] args) {
        reponses();
        repliques();
        etrangers();
        nuances();
        etiquettes();
        outils();

        System.out.println();
        System.out.printf("  %d vérifications réussies, %d échecs%n", reussis, echecs);
        if (echecs > 0) {
            System.exit(1);
        }
    }

    // MARK: réponses

    private static void reponses() {
        section("Réponses ([Choix] + click_event)");
        Classification c = MessageCapture.classer(choix("[Je vous suis.]", Formatting.GREEN, SUIVRE), PNJ, false, IGNORES);
        check("une ligne [Choix] cliquable est une réponse, pas une réplique",
                c != null && c.replique() == null && c.choix().size() == 1);
        check("le libellé perd ses crochets",
                c != null && "Je vous suis.".equals(c.choix().get(0).libelle().getString()));
        check("le click_event est conservé tel quel",
                c != null && SUIVRE.equals(c.choix().get(0).evenement()));
        check("vert → nuance quête",
                c != null && c.choix().get(0).type() == DialoguePanel.Type.QUETE);

        Text deux = Text.empty()
                .append(Text.literal("[Choix] ").setStyle(Style.EMPTY.withColor(Formatting.GRAY)))
                .append(Text.literal("[Oui]").setStyle(Style.EMPTY.withColor(Formatting.GREEN).withClickEvent(SUIVRE)))
                .append(Text.literal("  "))
                .append(Text.literal("[Non]").setStyle(Style.EMPTY.withColor(Formatting.RED).withClickEvent(REFUSER)));
        Classification c2 = MessageCapture.classer(deux, PNJ, false, IGNORES);
        check("deux clics différents sur une ligne → deux réponses",
                c2 != null && c2.choix().size() == 2
                        && "Oui".equals(c2.choix().get(0).libelle().getString())
                        && "Non".equals(c2.choix().get(1).libelle().getString()));

        Text coupe = Text.empty()
                .append(Text.literal("[Je vous ").setStyle(Style.EMPTY.withColor(Formatting.GREEN).withClickEvent(SUIVRE)))
                .append(Text.literal("suis.]").setStyle(Style.EMPTY.withColor(Formatting.GREEN).withClickEvent(SUIVRE)));
        Classification c3 = MessageCapture.classer(coupe, PNJ, false, IGNORES);
        check("un libellé coupé en segments portant le même clic → une seule réponse",
                c3 != null && c3.choix().size() == 1 && "Je vous suis.".equals(c3.choix().get(0).libelle().getString()));

        Text billet = Text.empty()
                .append(Text.literal("Traversée vers Port-Nord — 12 écus. ").setStyle(Style.EMPTY.withColor(Formatting.WHITE)))
                .append(Text.literal("[Acheter un billet]").setStyle(Style.EMPTY.withColor(Formatting.AQUA).withClickEvent(BILLET)));
        Classification c4 = MessageCapture.classer(billet, PNJ, false, IGNORES);
        check("panneau de transport : le clic devient une réponse, nuance transport",
                c4 != null && c4.choix().size() == 1 && c4.choix().get(0).type() == DialoguePanel.Type.TRANSPORT
                        && "Acheter un billet".equals(c4.choix().get(0).libelle().getString()));
    }

    // MARK: répliques

    private static void repliques() {
        section("Répliques ([Nom] texte)");
        Classification c = MessageCapture.classer(replique(PNJ, "Vous voilà enfin."), PNJ, false, IGNORES);
        check("[Nom du personnage] hors fenêtre → réplique",
                c != null && c.replique() != null && c.choix().isEmpty());
        check("le préfixe [Nom] est retiré du texte",
                c != null && c.replique() != null && "Vous voilà enfin.".equals(c.replique().getString()));
        check("le locuteur est retenu",
                c != null && PNJ.equals(c.locuteur()));
        check("la couleur du datapack est conservée sur le texte",
                c != null && c.replique() != null && !MessageCapture.segmenter(c.replique()).isEmpty()
                        && TextColor.fromFormatting(Formatting.WHITE).equals(
                        MessageCapture.segmenter(c.replique()).get(0).style().getColor()));

        Classification maj = MessageCapture.classer(replique("ROCH  VALLET", "Halte."), PNJ, false, IGNORES);
        check("casse et espaces indifférents dans le nom",
                maj != null && maj.replique() != null && "Halte.".equals(maj.replique().getString()));

        Classification accents = MessageCapture.classer(replique("Eleonore", "Bonjour."), "Éléonore", false, IGNORES);
        check("accents indifférents dans le nom",
                accents != null && accents.replique() != null);

        Classification question = MessageCapture.classer(
                Text.literal("[Choix] Que voulez-vous ?"), PNJ, false, IGNORES);
        check("[Choix] sans clic → réplique (la question qui précède les réponses)",
                question != null && question.replique() != null
                        && "Que voulez-vous ?".equals(question.replique().getString()) && question.choix().isEmpty());

        Classification fenetre = MessageCapture.classer(Text.literal("Il vous regarde longuement."), PNJ, true, IGNORES);
        check("message sans marque dans la fenêtre → réplique",
                fenetre != null && fenetre.replique() != null && "Il vous regarde longuement.".equals(fenetre.replique().getString()));

        Classification autre = MessageCapture.classer(replique("Garde", "Halte !"), PNJ, true, IGNORES);
        check("autre locuteur dans la fenêtre → réplique, préfixe conservé",
                autre != null && autre.replique() != null && "[Garde] Halte !".equals(autre.replique().getString())
                        && "Garde".equals(autre.locuteur()));
    }

    // MARK: étrangers

    private static void etrangers() {
        section("Messages étrangers (restent dans le chat)");
        check("message sans marque hors fenêtre → rien",
                MessageCapture.classer(Text.literal("Un message ordinaire"), PNJ, false, IGNORES) == null);
        check("autre locuteur hors fenêtre → rien",
                MessageCapture.classer(replique("Garde", "Halte !"), PNJ, false, IGNORES) == null);
        check("préfixe ignoré dans la fenêtre → rien",
                MessageCapture.classer(Text.literal("[Quête] Nouvelle quête : Les quais"), PNJ, true, IGNORES) == null);
        check("préfixe ignoré, casse et accents indifférents",
                MessageCapture.classer(Text.literal("[QUETE] Objectif atteint"), PNJ, true, IGNORES) == null);
        check("message vide → rien",
                MessageCapture.classer(Text.literal("   "), PNJ, true, IGNORES) == null);
        check("retour du /trigger (commands.trigger.*) dans la fenêtre → rien",
                MessageCapture.classer(Text.translatable("commands.trigger.set.success", "hcd_choix", 1), PNJ, true, IGNORES) == null);
        check("nom de personnage vide : seul la fenêtre capture",
                MessageCapture.classer(replique(PNJ, "Salut."), "", false, IGNORES) == null
                        && MessageCapture.classer(replique(PNJ, "Salut."), "", true, IGNORES) != null);
    }

    // MARK: nuances

    private static void nuances() {
        section("Nuances des réponses (couleur du datapack)");
        check("rouge → danger", type("[Menacer]", Formatting.RED) == DialoguePanel.Type.DANGER);
        check("vert sombre → quête", type("[Accepter]", Formatting.DARK_GREEN) == DialoguePanel.Type.QUETE);
        check("bleu → commerce", type("[Acheter]", Formatting.BLUE) == DialoguePanel.Type.COMMERCE);
        check("cyan → transport", type("[Embarquer]", Formatting.AQUA) == DialoguePanel.Type.TRANSPORT);
        check("or → métier", type("[Forger]", Formatting.GOLD) == DialoguePanel.Type.METIER);
        check("gris « Au revoir. » → congé", type("[Au revoir.]", Formatting.GRAY) == DialoguePanel.Type.ADIEU);
        check("blanc « Partir. » → congé", type("[Partir.]", Formatting.WHITE) == DialoguePanel.Type.ADIEU);
        check("blanc ordinaire → normale", type("[Continuer]", Formatting.WHITE) == DialoguePanel.Type.NORMALE);
        check("jaune → normale (pas un métier)", type("[Continuer]", Formatting.YELLOW) == DialoguePanel.Type.NORMALE);
        check("sans couleur → normale", type("[Continuer]", null) == DialoguePanel.Type.NORMALE);
    }

    // MARK: étiquettes de style

    private static void etiquettes() {
        section("Étiquettes de style en tête d'un libellé");
        DialoguePanel.Etiquette e = DialoguePanel.etiquette("[quête] Accepter la tâche.");
        check("[quête] → nuance quête, libellé sans l'étiquette",
                e.type() == DialoguePanel.Type.QUETE && "Accepter la tâche.".equals(e.reste()));
        check("casse et accents indifférents : [Métier]",
                DialoguePanel.etiquette("[Métier] Forger une lame").type() == DialoguePanel.Type.METIER);
        check("parenthèses acceptées : (commerce)",
                DialoguePanel.etiquette("(commerce) Voir l'étal").type() == DialoguePanel.Type.COMMERCE);
        check("[danger] et [menace] → danger",
                DialoguePanel.etiquette("[danger] Vous allez le regretter.").type() == DialoguePanel.Type.DANGER
                        && DialoguePanel.etiquette("[menace] Reculez.").type() == DialoguePanel.Type.DANGER);
        DialoguePanel.Etiquette inconnue = DialoguePanel.etiquette("[Je vous suis.]");
        check("crochets sans étiquette connue → intact",
                inconnue.type() == null && "[Je vous suis.]".equals(inconnue.reste()));
        DialoguePanel.Etiquette sans = DialoguePanel.etiquette("Au revoir.");
        check("libellé sans étiquette → intact", sans.type() == null && "Au revoir.".equals(sans.reste()));
        check("libellé nul → vide", DialoguePanel.etiquette(null).reste().isEmpty());
        check("couleur « #9CD9A8 » lue, opaque",
                Integer.valueOf(0xFF9CD9A8).equals(DialoguePanel.couleurDepuis("#9CD9A8"))
                        && Integer.valueOf(0xFF9CD9A8).equals(DialoguePanel.couleurDepuis("9cd9a8")));
        check("couleur invalide → nulle",
                DialoguePanel.couleurDepuis("rouge") == null && DialoguePanel.couleurDepuis("#12") == null);
    }

    // MARK: outils

    private static void outils() {
        section("Outils");
        check("normaliser : minuscules, accents, espaces",
                "roch vallet".equals(MessageCapture.normaliser("  Roch   VALLET ")) && "eleonore".equals(MessageCapture.normaliser("Éléonore")));
        check("normaliser : nul → vide", "".equals(MessageCapture.normaliser(null)));
        check("segmenter : un segment par style",
                MessageCapture.segmenter(replique(PNJ, "Bonjour.")).size() == 2);
    }

    // MARK: fabrique

    private static Text choix(String libelle, Formatting couleur, ClickEvent evenement) {
        return Text.empty()
                .append(Text.literal("[Choix] ").setStyle(Style.EMPTY.withColor(Formatting.GRAY)))
                .append(Text.literal(libelle).setStyle(Style.EMPTY.withColor(couleur).withClickEvent(evenement)));
    }

    private static Text replique(String nom, String texte) {
        return Text.empty()
                .append(Text.literal("[" + nom + "]").setStyle(Style.EMPTY.withColor(Formatting.YELLOW)))
                .append(Text.literal(" " + texte).setStyle(Style.EMPTY.withColor(Formatting.WHITE)));
    }

    private static DialoguePanel.Type type(String libelle, Formatting couleur) {
        Style style = couleur == null ? Style.EMPTY.withClickEvent(SUIVRE) : Style.EMPTY.withColor(couleur).withClickEvent(SUIVRE);
        Text message = Text.empty()
                .append(Text.literal("[Choix] ").setStyle(Style.EMPTY.withColor(Formatting.GRAY)))
                .append(Text.literal(libelle).setStyle(style));
        Classification c = MessageCapture.classer(message, PNJ, false, IGNORES);
        return c == null || c.choix().isEmpty() ? null : c.choix().get(0).type();
    }

    // MARK: plomberie

    private static void section(String titre) {
        System.out.println();
        System.out.println("  " + titre);
    }

    private static void check(String libelle, boolean ok) {
        if (ok) {
            reussis++;
            System.out.println("    ok    " + libelle);
        } else {
            echecs++;
            System.out.println("    ECHEC " + libelle);
        }
    }
}
