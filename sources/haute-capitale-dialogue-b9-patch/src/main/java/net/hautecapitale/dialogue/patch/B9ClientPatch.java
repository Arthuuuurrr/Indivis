package net.hautecapitale.dialogue.patch;

import java.text.Normalizer;
import java.util.Locale;
import java.util.concurrent.atomic.AtomicBoolean;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.message.v1.ClientReceiveMessageEvents;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayConnectionEvents;
import net.hautecapitale.dialogue.client.DialogueClientSettings;
import net.hautecapitale.dialogue.client.DialogueClientState;
import net.hautecapitale.dialogue.client.capture.MessageCapture;
import net.minecraft.client.MinecraftClient;
import net.minecraft.text.ClickEvent;
import net.minecraft.text.Text;
import net.minecraft.text.Style;

public final class B9ClientPatch implements ClientModInitializer {
    private static final int PENDING_WINDOW_TICKS = 40;

    private static Text previousGameMessage;
    private static Text pendingSpeakerMessage;
    private static Text pendingChoiceMessage;
    private static int pendingTicks;
    private static boolean commandSent;

    @Override
    public void onInitializeClient() {
        ClientReceiveMessageEvents.ALLOW_GAME.register((message, overlay) -> {
            if (!overlay) {
                onGameMessage(message);
            }
            return true;
        });

        ClientTickEvents.END_CLIENT_TICK.register(B9ClientPatch::onClientTick);

        ClientPlayConnectionEvents.DISCONNECT.register((handler, client) -> clearPending());
    }

    private static void onGameMessage(Text message) {
        if (message == null) {
            return;
        }

        if (!DialogueClientState.captureActive()
                && isAccessChoicePrompt(message)
                && !commandSent) {
            pendingSpeakerMessage =
                    isGuardSpeakerMessage(previousGameMessage) ? previousGameMessage : null;
            pendingChoiceMessage = message;
            pendingTicks = PENDING_WINDOW_TICKS;
            commandSent = true;

            MinecraftClient client = MinecraftClient.getInstance();
            if (client.getNetworkHandler() != null) {
                client.getNetworkHandler().sendChatCommand("hcd_interpellation auto_guard");
            }
        }

        previousGameMessage = message;
    }

    private static void onClientTick(MinecraftClient client) {
        if (pendingTicks <= 0) {
            return;
        }

        if (DialogueClientState.captureActive()) {
            replayIntoCapture(pendingSpeakerMessage);
            replayIntoCapture(pendingChoiceMessage);
            clearPending();
            return;
        }

        pendingTicks--;
        if (pendingTicks <= 0) {
            clearPending();
        }
    }

    private static boolean isAccessChoicePrompt(Text message) {
        String text = normalize(message.getString());
        if (!text.contains("arrangement")
                || !(text.contains("suivre") && text.contains("garde"))) {
            return false;
        }

        AtomicBoolean hasCapChoice = new AtomicBoolean(false);
        message.visit(
                (style, segment) -> {
                    ClickEvent clickEvent = style.getClickEvent();
                    if (clickEvent instanceof ClickEvent.RunCommand runCommand) {
                        String command = runCommand.command();
                        if (command != null
                                && command.trim().startsWith("/trigger CapChoix set ")) {
                            hasCapChoice.set(true);
                        }
                    }
                    return java.util.Optional.empty();
                },
                Style.EMPTY);

        return hasCapChoice.get();
    }

    private static boolean isGuardSpeakerMessage(Text message) {
        if (message == null) {
            return false;
        }
        String text = normalize(message.getString()).stripLeading();
        return text.startsWith("[garde")
                || text.startsWith("garde ")
                || text.contains("[garde ");
    }

    private static void replayIntoCapture(Text message) {
        if (message == null || !DialogueClientState.captureActive()) {
            return;
        }

        DialogueClientSettings settings = DialogueClientSettings.get();
        var capture =
                MessageCapture.classer(
                        message,
                        DialogueClientState.pnjNom(),
                        true,
                        settings.capture_prefixes_ignores);
        if (capture != null && !capture.vide()) {
            DialogueClientState.onMessageCapture(capture);
        }
    }

    private static void clearPending() {
        pendingSpeakerMessage = null;
        pendingChoiceMessage = null;
        pendingTicks = 0;
        commandSent = false;
    }

    private static String normalize(String value) {
        if (value == null) {
            return "";
        }
        return Normalizer.normalize(value, Normalizer.Form.NFD)
                .replaceAll("\\p{M}+", "")
                .toLowerCase(Locale.ROOT)
                .replace('’', '\'')
                .trim();
    }
}
