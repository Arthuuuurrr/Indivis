package net.hautecapitale.party.dialog;

import net.hautecapitale.party.party.PartyManager;
import net.minecraft.dialog.AfterAction;
import net.minecraft.dialog.DialogActionButtonData;
import net.minecraft.dialog.DialogButtonData;
import net.minecraft.dialog.DialogCommonData;
import net.minecraft.dialog.action.SimpleDialogAction;
import net.minecraft.dialog.body.DialogBody;
import net.minecraft.dialog.body.PlainMessageDialogBody;
import net.minecraft.dialog.type.ConfirmationDialog;
import net.minecraft.dialog.type.Dialog;
import net.minecraft.network.packet.s2c.common.ClearDialogS2CPacket;
import net.minecraft.network.packet.s2c.common.ShowDialogS2CPacket;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.ClickEvent;
import net.minecraft.text.Text;

import java.util.List;
import java.util.Optional;

/**
 * Les fenetres de confirmation, rendues par Minecraft lui-meme.
 *
 * <p>Minecraft 1.21.11 sait afficher une boite de dialogue envoyee par le serveur.
 * Tout ce qui suit s'appuie dessus, et cela change deux choses par rapport a une
 * interface maison :
 *
 * <ul>
 *   <li><b>Aucun mod client n'est necessaire.</b> Un joueur en vanilla voit la meme
 *       fenetre que les autres, avec ses deux boutons.</li>
 *   <li><b>Le serveur reste seul juge.</b> Un bouton n'execute qu'une commande, que
 *       le serveur revalide entierement — identifiant de demande compris. Le client
 *       ne transmet jamais un verdict, seulement une intention (regle §62).</li>
 * </ul>
 *
 * <p>Le dialogue est envoye en <em>entree directe</em> ({@link RegistryEntry#of}) :
 * il est construit a la volee pour une demande precise et n'a donc pas a exister
 * dans un datapack.
 */
public final class PartyDialogs {

    /** Largeur de fenetre confortable pour deux boutons cote a cote. */
    private static final int BODY_WIDTH = 320;
    private static final int BUTTON_WIDTH = 150;

    private PartyDialogs() {
    }

    /**
     * Pose une question a deux boutons.
     *
     * <p>Les deux commandes portent l'identifiant de la demande : une reponse
     * tardive designe donc explicitement la demande a laquelle elle repond, et le
     * serveur peut refuser celles qui ne sont plus d'actualite (regle §20).
     */
    public static void askYesNo(ServerPlayerEntity player,
                                Text title, Text body,
                                Text yesLabel, String yesCommand,
                                Text noLabel, String noCommand) {
        if (player == null || player.networkHandler == null) {
            return;
        }

        DialogCommonData common = new DialogCommonData(
                title,
                Optional.empty(),
                // Fermer la fenetre est une reponse valide : c'est une abstention, et
                // la regle traite l'abstention comme un refus. Enfermer le joueur dans
                // une fenetre modale serait pire que le probleme resolu.
                true,
                false,
                AfterAction.CLOSE,
                List.<DialogBody>of(new PlainMessageDialogBody(body, BODY_WIDTH)),
                List.of());

        ConfirmationDialog dialog = new ConfirmationDialog(
                common,
                button(yesLabel, yesCommand),
                button(noLabel, noCommand));

        player.networkHandler.sendPacket(new ShowDialogS2CPacket(RegistryEntry.of(dialog)));
    }

    private static DialogActionButtonData button(Text label, String command) {
        return new DialogActionButtonData(
                new DialogButtonData(label, Optional.empty(), BUTTON_WIDTH),
                Optional.of(new SimpleDialogAction(new ClickEvent.RunCommand(command))));
    }

    /**
     * Ferme la fenetre encore ouverte chez un joueur.
     *
     * <p>Appele quand la demande se conclut sans lui — expiration, annulation, ou
     * decision atteinte avant sa reponse. Sans cela, il resterait devant une
     * question a laquelle plus personne n'attend de reponse.
     */
    public static void clear(ServerPlayerEntity player) {
        if (player != null && player.networkHandler != null) {
            player.networkHandler.sendPacket(ClearDialogS2CPacket.INSTANCE);
        }
    }

    /** Ferme la fenetre chez plusieurs joueurs, designes par identifiant. */
    public static void clearAll(net.minecraft.server.MinecraftServer server, Iterable<java.util.UUID> players) {
        for (java.util.UUID uuid : players) {
            clear(server.getPlayerManager().getPlayer(uuid));
        }
    }

    /** Raccourci : envoie un message a un joueur s'il est connecte. */
    public static void tell(ServerPlayerEntity player, Text message) {
        PartyManager.tell(player, message);
    }
}
