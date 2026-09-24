package net.hautecapitale.metiers.node;

import net.minecraft.item.ItemStack;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;

import java.util.Locale;

/**
 * Ce que le joueur lit au-dessus de sa barre d'inventaire quand il frappe un
 * node. Court : c'est un message d'action, pas une notice.
 */
public final class NodeFeedback {

    private NodeFeedback() {
    }

    public static void overlay(ServerPlayerEntity player, NodeEngine.HitOutcome outcome) {
        if (player.networkHandler == null) {
            return;
        }
        Text text = describe(outcome);
        if (text != null) {
            player.sendMessage(text, true);
        }
    }

    /** Au clic droit sur un node : on rappelle que ça se frappe. */
    public static void hint(ServerPlayerEntity player) {
        if (player.networkHandler != null) {
            player.sendMessage(Text.translatableWithFallback("hcm.node.frapper",
                    "Frappez ce node pour le récolter.").formatted(Formatting.GRAY), true);
        }
    }

    public static Text describe(NodeEngine.HitOutcome outcome) {
        return switch (outcome.hit()) {
            case PAS_UN_NODE -> null;
            case NODE_VIDE -> Text.translatableWithFallback("hcm.node.vide",
                    "Rien à récolter ici — repousse dans %s s",
                    Text.literal(String.valueOf(outcome.secondsUntilRespawn()))).formatted(Formatting.GRAY);
            case METIER_NON_APPRIS -> Text.translatableWithFallback("hcm.node.metier",
                    "Vous n'exercez pas le métier qui récolte ceci.").formatted(Formatting.RED);
            case NIVEAU_INSUFFISANT -> Text.translatableWithFallback("hcm.node.niveau",
                    "Votre niveau est insuffisant pour récolter ceci.").formatted(Formatting.RED);
            case MAUVAIS_OUTIL -> Text.translatableWithFallback("hcm.node.outil",
                    "Il vous faut le bon outil en main.").formatted(Formatting.RED);
            case EN_COURS -> Text.literal(String.format(Locale.ROOT, "%d / %d",
                    outcome.hitsDone(), outcome.hitsNeeded())).formatted(Formatting.YELLOW);
            case RECOLTE -> harvested(outcome);
        };
    }

    private static Text harvested(NodeEngine.HitOutcome outcome) {
        MutableText text = Text.translatableWithFallback("hcm.node.recolte", "Récolté :")
                .formatted(Formatting.GREEN);
        for (ItemStack stack : outcome.loot()) {
            text.append(Text.literal(" ").append(stack.getName()).append(" ×" + stack.getCount())
                    .formatted(Formatting.WHITE));
        }
        if (outcome.loot().isEmpty()) {
            text.append(Text.translatableWithFallback("hcm.node.rien", " rien cette fois").formatted(Formatting.GRAY));
        }
        if (outcome.xpGained() > 0.0D) {
            text.append(Text.literal(String.format(Locale.ROOT, "  +%.0f XP", outcome.xpGained()))
                    .formatted(Formatting.AQUA));
        } else {
            text.append(Text.translatableWithFallback("hcm.node.sans_xp", "  (0 XP : trop facile)")
                    .formatted(Formatting.DARK_GRAY));
        }
        return text;
    }
}
