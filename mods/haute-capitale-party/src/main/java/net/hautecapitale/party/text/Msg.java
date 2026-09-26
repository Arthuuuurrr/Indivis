package net.hautecapitale.party.text;

import net.minecraft.text.MutableText;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;

/**
 * Tous les textes affiches aux joueurs.
 *
 * <p>Ecrits en dur plutot que passes par {@code Text.translatable} : le mod est
 * concu pour que des clients vanilla, sans rien d'installe, jouent normalement
 * (regle §44). Une cle de traduction s'afficherait telle quelle chez eux.
 */
public final class Msg {

    private static final Formatting ACCENT = Formatting.AQUA;
    private static final Formatting SOFT = Formatting.GRAY;
    private static final Formatting BAD = Formatting.RED;
    private static final Formatting GOOD = Formatting.GREEN;

    private Msg() {
    }

    /** Prefixe commun, pour que le canal du groupe se reconnaisse d'un coup d'oeil. */
    private static MutableText prefix() {
        return Text.literal("[Groupe] ").formatted(ACCENT);
    }

    public static Text info(String message) {
        return prefix().append(Text.literal(message).formatted(SOFT));
    }

    public static Text good(String message) {
        return prefix().append(Text.literal(message).formatted(GOOD));
    }

    public static Text bad(String message) {
        return prefix().append(Text.literal(message).formatted(BAD));
    }

    /** Ligne de titre d'un panneau, sans prefixe : elle ouvre un bloc. */
    public static Text heading(String message) {
        return Text.literal(message).formatted(ACCENT, Formatting.BOLD);
    }

    public static Text plain(String message) {
        return Text.literal(message).formatted(SOFT);
    }

    /** Une ligne de membre dans {@code /party info}. */
    public static Text memberLine(String symbol, Formatting roleColor, String name,
                                  String roleLabel, boolean leader, boolean online) {
        MutableText line = Text.literal(leader ? " ♛ " : "   ")
                .formatted(leader ? Formatting.GOLD : SOFT);
        line.append(Text.literal(symbol + " ").formatted(roleColor));
        line.append(Text.literal(name).formatted(online ? Formatting.WHITE : Formatting.DARK_GRAY));
        line.append(Text.literal("  " + roleLabel).formatted(SOFT));
        if (!online) {
            line.append(Text.literal("  (hors ligne)").formatted(Formatting.DARK_GRAY));
        }
        return line;
    }

    /** Message du canal de groupe. */
    public static Text chat(String senderName, String message) {
        return Text.literal("[G] ").formatted(ACCENT)
                .append(Text.literal(senderName).formatted(Formatting.WHITE))
                .append(Text.literal(" : ").formatted(SOFT))
                .append(Text.literal(message).formatted(Formatting.WHITE));
    }

    /**
     * Invitation avec deux boutons cliquables.
     *
     * <p>Le clic execute une commande, donc la validation reste entierement du cote
     * du serveur : le client ne fait que demander (regle §62).
     */
    public static Text invitation(String leaderName, String partyLabel) {
        MutableText text = prefix()
                .append(Text.literal(leaderName).formatted(Formatting.WHITE))
                .append(Text.literal(" vous invite dans son groupe (" + partyLabel + ").")
                        .formatted(SOFT))
                .append(Text.literal("\n   "));
        text.append(button("[ACCEPTER]", Formatting.GREEN, "/party accept"));
        text.append(Text.literal("  "));
        text.append(button("[REFUSER]", Formatting.RED, "/party decline"));
        return text;
    }

    // --- consultations du groupe --------------------------------------------

    /** Titre de la fenetre de verification de preparation. */
    public static Text readyTitle() {
        return Text.literal("Etes-vous pret ?").formatted(ACCENT, Formatting.BOLD);
    }

    public static Text readyBody(String leaderName) {
        return Text.literal(leaderName + " verifie que le groupe est pret.")
                .formatted(Formatting.WHITE);
    }

    /** Une ligne du recapitulatif, un joueur par ligne. */
    public static Text readyLine(String name, boolean ready) {
        return Text.literal("   " + name + " : ").formatted(SOFT)
                .append(Text.literal(ready ? "PRET" : "PAS PRET")
                        .formatted(ready ? GOOD : BAD, Formatting.BOLD));
    }

    public static Text readyTally(int ready, int total) {
        Formatting color = ready == total ? GOOD : Formatting.YELLOW;
        return prefix().append(Text.literal(ready + "/" + total + " prets.").formatted(color));
    }

    /** Titre de la fenetre de vote d'exclusion. */
    public static Text kickVoteTitle() {
        return Text.literal("Vote d'exclusion").formatted(BAD, Formatting.BOLD);
    }

    public static Text kickVoteBody(String leaderName, String targetName, int needed, int voters) {
        return Text.literal(leaderName + " propose d'exclure " + targetName + " du groupe.\n"
                        + needed + " voix sur " + voters + " sont necessaires.\n"
                        + "Ne pas repondre revient a voter non.")
                .formatted(Formatting.WHITE);
    }

    public static Text kickVoteTally(int yes, int no, int needed) {
        return prefix().append(Text.literal("Vote : " + yes + " pour, " + no + " contre ("
                + needed + " requises).").formatted(SOFT));
    }

    /** Le chiffre affiche au centre de l'ecran pendant le compte a rebours. */
    public static Text countdownNumber(int seconds) {
        Formatting color = seconds <= 3 ? BAD : (seconds <= 5 ? Formatting.GOLD : ACCENT);
        return Text.literal(Integer.toString(seconds)).formatted(color, Formatting.BOLD);
    }

    public static Text countdownGo() {
        return Text.literal("PARTEZ !").formatted(GOOD, Formatting.BOLD);
    }

    private static MutableText button(String label, Formatting color, String command) {
        return Text.literal(label).styled(style -> style
                .withColor(color)
                .withBold(true)
                .withClickEvent(new net.minecraft.text.ClickEvent.RunCommand(command))
                .withHoverEvent(new net.minecraft.text.HoverEvent.ShowText(
                        Text.literal(command).formatted(Formatting.GRAY))));
    }
}
