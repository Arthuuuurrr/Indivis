package net.hautecapitale.metiers.node;

import net.hautecapitale.metiers.data.HcmData;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.math.BlockPos;

/** Textes partagés par la baguette et les commandes — une seule façon de décrire un node. */
public final class NodeCommandsSupport {

    private NodeCommandsSupport() {
    }

    public static Text describe(BlockPos pos, NodeStore.Node node) {
        NodeType type = HcmData.NODES.get(node.type);
        MutableText text = Text.literal(pos.toShortString() + " — ").formatted(Formatting.GRAY)
                .append(Text.literal(node.type.toString()).formatted(Formatting.GOLD));
        if (type == null) {
            return text.append(Text.literal("  (type inconnu dans les données)").formatted(Formatting.RED));
        }
        text.append(Text.literal("  " + type.profession().getId() + " niv. " + type.level()).formatted(Formatting.WHITE));
        if (node.isFull()) {
            text.append(Text.literal("  plein").formatted(Formatting.GREEN));
        } else {
            long remaining = Math.max(0L, (node.respawnAt - NodeEngine.clock.getAsLong() + 999L) / 1000L);
            text.append(Text.literal("  vide, repousse dans " + remaining + " s").formatted(Formatting.YELLOW));
        }
        return text;
    }

    public static String explain(NodeEngine.PlaceRefusal refusal) {
        return switch (refusal) {
            case TYPE_INCONNU -> "type de node inconnu";
            case BLOC_INCONNU -> "le bloc plein de ce type n'existe pas dans cette installation";
            case DEJA_UN_NODE -> "il y a déjà un node à cet endroit";
        };
    }
}
