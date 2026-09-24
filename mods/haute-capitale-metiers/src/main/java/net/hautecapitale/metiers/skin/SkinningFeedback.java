package net.hautecapitale.metiers.skin;

import net.hautecapitale.metiers.entity.CarcassEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;

import java.util.Locale;

/**
 * Ce que le Dépeceur lit au-dessus de sa barre d'inventaire : pourquoi il ne
 * peut pas, où il en est, pourquoi ça s'est arrêté, ce qu'il a obtenu.
 */
public final class SkinningFeedback {

    private SkinningFeedback() {
    }

    public static void refused(ServerPlayerEntity player, SkinningEngine.Refusal refusal, CarcassEntity carcass) {
        say(player, refusal(refusal, carcass));
    }

    public static Text refusal(SkinningEngine.Refusal refusal, CarcassEntity carcass) {
        int level = carcass != null && carcass.profile() != null
                ? carcass.profile().skinning().map(entry -> entry.level()).orElse(1) : 1;
        return switch (refusal) {
            case PAS_UNE_CARCASSE -> Text.translatableWithFallback("hcm.depecage.refus.carcasse",
                    "Il n'y a plus rien à dépecer ici.").formatted(Formatting.GRAY);
            case FICHE_INCONNUE -> Text.translatableWithFallback("hcm.depecage.refus.fiche",
                    "Cette carcasse ne se dépèce pas.").formatted(Formatting.GRAY);
            case METIER_NON_APPRIS -> Text.translatableWithFallback("hcm.depecage.refus.metier",
                    "Vous n'exercez pas le métier de Dépeceur.").formatted(Formatting.RED);
            case NIVEAU_INSUFFISANT -> Text.translatableWithFallback("hcm.depecage.refus.niveau",
                    "Niveau %s de Dépeceur requis.", Text.literal(String.valueOf(level))).formatted(Formatting.RED);
            case MAUVAIS_OUTIL -> Text.translatableWithFallback("hcm.depecage.refus.outil",
                    "Il vous faut un couteau en main.").formatted(Formatting.RED);
            case TROP_LOIN -> Text.translatableWithFallback("hcm.depecage.refus.loin",
                    "Approchez-vous de la carcasse.").formatted(Formatting.RED);
            case DEJA_EN_COURS -> Text.translatableWithFallback("hcm.depecage.refus.en_cours",
                    "Quelqu'un dépèce déjà cette carcasse.").formatted(Formatting.YELLOW);
            case DEJA_PRISE -> Text.translatableWithFallback("hcm.depecage.refus.prise",
                    "Cette carcasse a déjà été dépecée.").formatted(Formatting.GRAY);
        };
    }

    public static void started(ServerPlayerEntity player, SkinningEngine.Channel channel) {
        say(player, Text.translatableWithFallback("hcm.depecage.debut", "Dépeçage… restez en place (%s s)",
                Text.literal(String.valueOf(channel.entry.channelSeconds()))).formatted(Formatting.YELLOW));
    }

    /** Une jauge, mise à jour quand le dixième change — pas à chaque tick. */
    public static void progress(SkinningEngine.Channel channel, long now) {
        int tenth = (int) (channel.progress(now) * 10.0D);
        if (tenth == channel.lastReportedSecond) {
            return;
        }
        channel.lastReportedSecond = tenth;
        StringBuilder bar = new StringBuilder();
        for (int i = 0; i < 10; i++) {
            bar.append(i < tenth ? '▮' : '▯');
        }
        double remaining = Math.max(0.0D, (channel.endsAt - now) / 1000.0D);
        say(channel.player, Text.translatableWithFallback("hcm.depecage.progression", "Dépeçage %s %s s",
                Text.literal(bar.toString()).formatted(Formatting.GOLD),
                Text.literal(String.format(Locale.ROOT, "%.1f", remaining))).formatted(Formatting.YELLOW));
    }

    public static void cancelled(ServerPlayerEntity player, SkinningEngine.Cancel reason) {
        say(player, cancel(reason));
    }

    public static Text cancel(SkinningEngine.Cancel reason) {
        String key = "hcm.depecage.annule." + reason.name().toLowerCase(Locale.ROOT);
        String fallback = switch (reason) {
            case TROP_LOIN -> "Dépeçage interrompu : trop loin.";
            case A_BOUGE -> "Dépeçage interrompu : vous avez bougé.";
            case OUTIL_LACHE -> "Dépeçage interrompu : gardez votre outil en main.";
            case CARCASSE_DISPARUE -> "Dépeçage interrompu : la carcasse a disparu.";
            case PRISE_PAR_UN_AUTRE -> "Quelqu'un d'autre a dépecé cette carcasse.";
            case JOUEUR_PARTI -> "Dépeçage interrompu.";
        };
        return Text.translatableWithFallback(key, fallback).formatted(Formatting.RED);
    }

    public static void completed(ServerPlayerEntity player, SkinningEngine.Outcome outcome) {
        say(player, describe(outcome));
    }

    public static Text describe(SkinningEngine.Outcome outcome) {
        MutableText text = Text.translatableWithFallback("hcm.depecage.fini", "Dépecé :").formatted(Formatting.GREEN);
        for (ItemStack stack : outcome.materials()) {
            text.append(Text.literal(" ").append(stack.getName()).append(" ×" + stack.getCount()).formatted(Formatting.WHITE));
        }
        if (outcome.materials().isEmpty()) {
            text.append(Text.translatableWithFallback("hcm.depecage.rien", " rien d'utilisable").formatted(Formatting.GRAY));
        }
        if (outcome.xpGained() > 0.0D) {
            text.append(Text.literal(String.format(Locale.ROOT, "  +%.0f XP", outcome.xpGained())).formatted(Formatting.AQUA));
        } else {
            text.append(Text.translatableWithFallback("hcm.depecage.sans_xp", "  (0 XP : trop facile)").formatted(Formatting.DARK_GRAY));
        }
        if (outcome.levelsGained() > 0) {
            text.append(Text.translatableWithFallback("hcm.depecage.niveau_gagne", "  Niveau supérieur !").formatted(Formatting.GOLD));
        }
        return text;
    }

    private static void say(ServerPlayerEntity player, Text text) {
        if (player.networkHandler != null) {
            player.sendMessage(text, true);
        }
    }
}
