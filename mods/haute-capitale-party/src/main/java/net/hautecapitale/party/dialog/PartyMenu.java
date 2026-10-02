package net.hautecapitale.party.dialog;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import net.fabricmc.loader.api.FabricLoader;
import net.hautecapitale.party.party.Party;
import net.hautecapitale.party.party.PartyManager;
import net.hautecapitale.party.party.Role;
import net.minecraft.dialog.AfterAction;
import net.minecraft.dialog.DialogActionButtonData;
import net.minecraft.dialog.DialogButtonData;
import net.minecraft.dialog.DialogCommonData;
import net.minecraft.dialog.action.SimpleDialogAction;
import net.minecraft.dialog.body.DialogBody;
import net.minecraft.dialog.body.PlainMessageDialogBody;
import net.minecraft.dialog.type.Dialog;
import net.minecraft.dialog.type.MultiActionDialog;
import net.minecraft.network.packet.s2c.common.ShowDialogS2CPacket;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.ClickEvent;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;

/**
 * Le <b>menu de groupe</b> (touche « P » côté client) — tout se fait au clic, aucune commande à
 * taper. Rendu par les <b>dialogues serveur vanilla</b> (comme le ready-check / le vote) : il
 * s'affiche donc sur un client vanilla, et chaque bouton ne fait qu'exécuter une commande que le
 * serveur revalide (le client ne décide de rien).
 *
 * <p>Un {@link MultiActionDialog} = un titre + un corps + une liste de boutons. Les boutons qui
 * ouvrent un sous-menu relancent {@code /party menu <section>} ; ceux qui agissent lancent l'action
 * correspondante ({@code /party invite <nom>}, etc.).
 */
public final class PartyMenu {

    private static final int BODY_WIDTH = 320;
    private static final int BUTTON_WIDTH = 200;

    private PartyMenu() {
    }

    /** Menu principal : selon qu'on est dans un groupe ou non, et chef ou non. */
    public static void open(MinecraftServer server, ServerPlayerEntity player) {
        send(player, buildMain(server, player));
    }

    public static void openInvite(MinecraftServer server, ServerPlayerEntity player) {
        send(player, buildInvite(server, player));
    }

    public static void openKick(MinecraftServer server, ServerPlayerEntity player) {
        send(player, buildKick(server, player));
    }

    public static void openTransfer(MinecraftServer server, ServerPlayerEntity player) {
        send(player, buildTransfer(server, player));
    }

    public static void openRole(MinecraftServer server, ServerPlayerEntity player) {
        send(player, buildRole());
    }

    // --- construction (séparée de l'envoi : testable sans connexion réseau) ------

    public static MultiActionDialog buildMain(MinecraftServer server, ServerPlayerEntity player) {
        Optional<Party> maybe = PartyManager.partyOf(server, player.getUuid());
        List<DialogActionButtonData> buttons = new ArrayList<>();
        Text body;
        if (maybe.isEmpty()) {
            body = Text.literal("Vous n'êtes dans aucun groupe.");
            buttons.add(button(Text.literal("Créer un groupe"), "party create"));
        } else {
            Party party = maybe.get();
            boolean leader = party.isLeader(player.getUuid());
            int max = PartyManager.maxSizeOf(party.type());
            StringBuilder sb = new StringBuilder("Groupe " + party.type().label() + " — " + party.size() + "/" + max);
            for (Party.MemberEntry e : party.entries()) {
                boolean online = server.getPlayerManager().getPlayer(e.uuid()) != null;
                sb.append("\n").append(party.isLeader(e.uuid()) ? "★ " : "• ").append(nameOf(server, e));
                if (e.role() != Role.UNSET) {
                    sb.append(" (").append(e.role().label()).append(")");
                }
                if (!online) {
                    sb.append(" (hors ligne)");
                }
            }
            body = Text.literal(sb.toString());
            if (leader) {
                buttons.add(button(Text.literal("Inviter un joueur"), "party menu invite"));
                buttons.add(button(Text.literal("Exclure un membre"), "party menu kick"));
                buttons.add(button(Text.literal("Nommer un chef"), "party menu transfer"));
            }
            buttons.add(button(Text.literal("Mon rôle"), "party menu role"));
            // Entrée en donjon : réservée au chef, et seulement si le mod de donjon est présent
            // (dépendance douce — on ne fait qu'exécuter sa commande, sans lien de compilation).
            if (leader && FabricLoader.getInstance().isModLoaded("dungeonz")) {
                buttons.add(button(Text.literal("Entrer en donjon").formatted(Formatting.AQUA), "dungeon menu"));
            }
            if (leader) {
                buttons.add(button(Text.literal("Dissoudre le groupe").formatted(Formatting.RED), "party disband"));
            } else {
                buttons.add(button(Text.literal("Quitter le groupe").formatted(Formatting.RED), "party leave"));
            }
        }
        return dialog("Groupe", body, buttons);
    }

    /** Sous-menu : la liste des joueurs en ligne, hors groupe, qu'on peut inviter. */
    public static MultiActionDialog buildInvite(MinecraftServer server, ServerPlayerEntity player) {
        List<DialogActionButtonData> buttons = new ArrayList<>();
        for (ServerPlayerEntity other : server.getPlayerManager().getPlayerList()) {
            if (other.getUuid().equals(player.getUuid())) {
                continue;
            }
            if (PartyManager.partyOf(server, other.getUuid()).isPresent()) {
                continue;
            }
            String name = other.getName().getString();
            buttons.add(button(Text.literal(name), "party invite " + name));
        }
        Text body = buttons.isEmpty()
                ? Text.literal("Aucun joueur disponible (tous en ligne sont déjà en groupe).")
                : Text.literal("Choisissez un joueur à inviter :");
        buttons.add(back());
        return dialog("Inviter", body, buttons);
    }

    /** Sous-menu : exclure un membre (chef). */
    public static MultiActionDialog buildKick(MinecraftServer server, ServerPlayerEntity player) {
        return dialog("Exclure", Text.literal("Choisissez le membre à exclure :"),
                memberButtons(server, player, "party kick "));
    }

    /** Sous-menu : nommer un nouveau chef (chef). */
    public static MultiActionDialog buildTransfer(MinecraftServer server, ServerPlayerEntity player) {
        return dialog("Nommer un chef", Text.literal("Choisissez le nouveau chef :"),
                memberButtons(server, player, "party transfer "));
    }

    /** Sous-menu : choisir son rôle. */
    public static MultiActionDialog buildRole() {
        List<DialogActionButtonData> buttons = new ArrayList<>();
        buttons.add(button(Text.literal("Tank"), "party role tank"));
        buttons.add(button(Text.literal("Soigneur"), "party role heal"));
        buttons.add(button(Text.literal("DPS"), "party role dps"));
        buttons.add(button(Text.literal("Aucun"), "party role aucun"));
        buttons.add(back());
        return dialog("Mon rôle", Text.literal("Choisissez votre rôle :"), buttons);
    }

    // --- interne ---------------------------------------------------------------

    /** Boutons = membres du groupe sauf soi-même, chacun lançant {@code <commandPrefix><nom>}. */
    private static List<DialogActionButtonData> memberButtons(MinecraftServer server, ServerPlayerEntity player, String commandPrefix) {
        List<DialogActionButtonData> buttons = new ArrayList<>();
        Optional<Party> maybe = PartyManager.partyOf(server, player.getUuid());
        if (maybe.isPresent()) {
            for (Party.MemberEntry e : maybe.get().entries()) {
                if (e.uuid().equals(player.getUuid())) {
                    continue;
                }
                String name = nameOf(server, e);
                buttons.add(button(Text.literal(name), commandPrefix + name));
            }
        }
        buttons.add(back());
        return buttons;
    }

    private static DialogActionButtonData back() {
        return button(Text.literal("← Retour").formatted(Formatting.GRAY), "party menu");
    }

    /** Construit le dialogue (sans l'envoyer) — séparé pour être testable hors connexion réseau. */
    private static MultiActionDialog dialog(String title, Text body, List<DialogActionButtonData> buttons) {
        DialogCommonData common = new DialogCommonData(
                Text.literal(title),
                Optional.empty(),
                true,   // fermable avec Échap
                false,  // ne met pas le jeu en pause
                AfterAction.CLOSE,
                List.<DialogBody>of(new PlainMessageDialogBody(body, BODY_WIDTH)),
                List.of());
        return new MultiActionDialog(common, buttons, Optional.empty(), 1);
    }

    private static void send(ServerPlayerEntity player, Dialog dialog) {
        if (player.networkHandler != null) {
            player.networkHandler.sendPacket(new ShowDialogS2CPacket(RegistryEntry.of(dialog)));
        }
    }

    private static DialogActionButtonData button(Text label, String command) {
        return new DialogActionButtonData(
                new DialogButtonData(label, Optional.empty(), BUTTON_WIDTH),
                Optional.of(new SimpleDialogAction(new ClickEvent.RunCommand(command))));
    }

    private static String nameOf(MinecraftServer server, Party.MemberEntry e) {
        ServerPlayerEntity member = server.getPlayerManager().getPlayer(e.uuid());
        return member != null ? member.getName().getString() : e.lastKnownName();
    }
}
