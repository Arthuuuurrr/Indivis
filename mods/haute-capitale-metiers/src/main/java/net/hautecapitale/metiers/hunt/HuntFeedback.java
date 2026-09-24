package net.hautecapitale.metiers.hunt;

import net.hautecapitale.metiers.creature.CreatureEnums.SpawnOrigin;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;

import java.util.Locale;

/**
 * Ce que le joueur lit au-dessus de sa barre d'inventaire quand une proie
 * tombe. Un Chasseur apprend pourquoi il n'a rien gagné ; un joueur qui n'a pas
 * le métier ne voit que ce qu'il ramasse.
 */
public final class HuntFeedback {

    private HuntFeedback() {
    }

    public static void overlay(ServerPlayerEntity player, LivingEntity prey, HuntEngine.Outcome outcome) {
        if (player.networkHandler == null) {
            return;
        }
        Text text = describe(prey.getDisplayName(), outcome);
        if (text != null) {
            player.sendMessage(text, true);
        }
    }

    /** Le message, ou {@code null} s'il n'y a rien à dire. */
    public static Text describe(Text preyName, HuntEngine.Outcome outcome) {
        MutableText text = switch (outcome.verdict()) {
            case XP -> Text.translatableWithFallback("hcm.chasse.abattu", "Proie abattue : %s", preyName)
                    .formatted(Formatting.GREEN)
                    .append(Text.literal(String.format(Locale.ROOT, "  +%.0f XP", outcome.xpGained()))
                            .formatted(Formatting.AQUA));
            case SANS_XP_TROP_FACILE -> Text.translatableWithFallback("hcm.chasse.abattu", "Proie abattue : %s", preyName)
                    .formatted(Formatting.GREEN)
                    .append(Text.translatableWithFallback("hcm.chasse.sans_xp", "  (0 XP : proie trop facile)")
                            .formatted(Formatting.DARK_GRAY));
            case NIVEAU_INSUFFISANT -> Text.translatableWithFallback("hcm.chasse.niveau",
                    "%s : niveau %s de Chasseur requis — aucune XP", preyName,
                    Text.literal(String.valueOf(outcome.profile().hunter().map(h -> h.level()).orElse(1))))
                    .formatted(Formatting.RED);
            case ORIGINE_NON_ELIGIBLE -> Text.translatableWithFallback("hcm.chasse.origine",
                    "%s : %s — aucune XP", preyName, origin(outcome.origin())).formatted(Formatting.GRAY);
            // Sans le métier, ou créature qui n'est pas une proie : seul le butin parle.
            case METIER_NON_APPRIS, PAS_UNE_PROIE -> null;
        };

        if (outcome.levelsGained() > 0 && text != null) {
            text.append(Text.translatableWithFallback("hcm.chasse.niveau_gagne", "  Niveau supérieur !")
                    .formatted(Formatting.GOLD));
        }

        if (!outcome.loot().isEmpty() || !outcome.meat().isEmpty()) {
            MutableText drops = Text.translatableWithFallback("hcm.chasse.butin", "Butin :").formatted(Formatting.WHITE);
            for (ItemStack stack : outcome.loot()) {
                drops.append(Text.literal(" ").append(stack.getName()).append(" ×" + stack.getCount()));
            }
            for (ItemStack stack : outcome.meat()) {
                drops.append(Text.literal(" ").append(stack.getName()).append(" ×" + stack.getCount()));
            }
            text = text == null ? drops : text.append(Text.literal("  ·  ").formatted(Formatting.DARK_GRAY)).append(drops);
        }
        return text;
    }

    /** « élevage », « générateur »… — le mot que le joueur comprend. */
    public static MutableText origin(SpawnOrigin origin) {
        String fallback = switch (origin) {
            case NATURELLE -> "apparition naturelle";
            case SPAWN_MMO -> "apparition du MMO";
            case ELEVAGE -> "élevage";
            case SPAWNER -> "générateur";
            case OEUF -> "œuf d'apparition";
            case COMMANDE -> "commande";
            case INVOCATION -> "invocation";
            case INCONNUE -> "origine inconnue";
        };
        return Text.translatableWithFallback("hcm.origine." + origin.asString(), fallback);
    }
}
