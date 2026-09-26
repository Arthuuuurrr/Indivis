package net.hautecapitale.spawns.text;

import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;

/** Petits constructeurs de texte, et envoi sur pour les joueurs sans connexion. */
public final class Msg {

    public static final String PREFIX = "[Spawns] ";

    private Msg() {
    }

    public static MutableText info(String text) {
        return Text.literal(PREFIX).formatted(Formatting.GOLD).append(Text.literal(text).formatted(Formatting.WHITE));
    }

    public static MutableText ok(String text) {
        return Text.literal(PREFIX).formatted(Formatting.GOLD).append(Text.literal(text).formatted(Formatting.GREEN));
    }

    public static MutableText warn(String text) {
        return Text.literal(PREFIX).formatted(Formatting.GOLD).append(Text.literal(text).formatted(Formatting.YELLOW));
    }

    public static MutableText error(String text) {
        return Text.literal(PREFIX).formatted(Formatting.GOLD).append(Text.literal(text).formatted(Formatting.RED));
    }

    public static MutableText line(String label, String value) {
        return Text.literal("  " + label + " : ").formatted(Formatting.GRAY).append(Text.literal(value).formatted(Formatting.WHITE));
    }

    public static MutableText plain(String text, Formatting formatting) {
        return Text.literal(text).formatted(formatting);
    }

    /** {@code player.sendMessage} leve un NPE sans connexion (joueur fabrique, deconnexion). */
    public static void tell(ServerPlayerEntity player, Text text) {
        if (player != null && player.networkHandler != null) {
            player.sendMessage(text, false);
        }
    }

    public static void actionBar(ServerPlayerEntity player, Text text) {
        if (player != null && player.networkHandler != null) {
            player.sendMessage(text, true);
        }
    }

    public static void feedback(ServerCommandSource source, Text text) {
        source.sendFeedback(() -> text, false);
    }

    public static String mmss(long seconds) {
        long s = Math.max(0, seconds);
        return String.format("%02d:%02d", s / 60, s % 60);
    }
}
