package net.hautecapitale.dialogue.client;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.command.v2.ClientCommandManager;
import net.fabricmc.fabric.api.client.command.v2.ClientCommandRegistrationCallback;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayConnectionEvents;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.client.rendering.v1.hud.HudElementRegistry;
import net.fabricmc.loader.api.FabricLoader;
import net.hautecapitale.dialogue.HauteCapitaleDialogue;
import net.hautecapitale.dialogue.client.capture.MessageCapture;
import net.hautecapitale.dialogue.client.compat.bettercombat.BetterCombatClientCompat;
import net.hautecapitale.dialogue.client.compat.easynpc.EasyNpcClientBridge;
import net.hautecapitale.dialogue.client.debug.DialogueDebugOverlay;
import net.hautecapitale.dialogue.client.selftest.DialogueSelfTest;
import net.hautecapitale.dialogue.network.DialogueNetwork;
import net.hautecapitale.dialogue.session.CloseReason;
import net.minecraft.text.Text;

/**
 * Point d'entrée client : caméra, écran, masque du HUD, réception des sessions.
 *
 * <p>Référencé uniquement par l'entrée {@code client} de {@code fabric.mod.json}
 * et compilé dans un source set séparé : un serveur dédié n'en charge rien.
 */
public class HauteCapitaleDialogueClient implements ClientModInitializer {

    @Override
    public void onInitializeClient() {
        DialogueClientSettings.load();
        HudMask.init();
        MessageCapture.init();

        ClientPlayNetworking.registerGlobalReceiver(DialogueNetwork.SessionOpen.ID,
                (payload, context) -> context.client().execute(() -> DialogueClientState.ouvrir(payload)));
        ClientPlayNetworking.registerGlobalReceiver(DialogueNetwork.SessionClose.ID,
                (payload, context) -> context.client().execute(() -> DialogueClientState.onServeurFerme(payload)));

        ClientTickEvents.END_CLIENT_TICK.register(DialogueClientState::tick);
        ClientPlayConnectionEvents.JOIN.register((handler, sender, client) -> DialogueClientState.reset());
        ClientPlayConnectionEvents.DISCONNECT.register((handler, client) -> DialogueClientState.reset());

        HudElementRegistry.addLast(HauteCapitaleDialogue.id("debug"), new DialogueDebugOverlay());

        ClientCommandRegistrationCallback.EVENT.register((dispatcher, registryAccess) ->
                dispatcher.register(ClientCommandManager.literal("dialoguec")
                        .then(ClientCommandManager.literal("debug").executes(ctx -> {
                            DialogueClientSettings s = DialogueClientSettings.get();
                            s.overlay_debug = !s.overlay_debug;
                            DialogueClientSettings.save();
                            ctx.getSource().sendFeedback(Text.literal("Overlay de dialogue : "
                                    + (s.overlay_debug ? "affiché" : "masqué")));
                            return 1;
                        }))
                        .then(ClientCommandManager.literal("fermeture").executes(ctx -> {
                            DialogueClientSettings s = DialogueClientSettings.get();
                            s.fermeture_automatique = !s.fermeture_automatique;
                            DialogueClientSettings.save();
                            ctx.getSource().sendFeedback(Text.literal("Fermeture automatique des dialogues : "
                                    + (s.fermeture_automatique
                                    ? "activée — le silence du personnage vaut congé."
                                    : "désactivée — l'écran reste ouvert jusqu'à Échap.")));
                            return 1;
                        }))
                        .then(ClientCommandManager.literal("fin").executes(ctx -> {
                            DialogueClientState.demanderFin(CloseReason.FERMEE);
                            ctx.getSource().sendFeedback(Text.literal("Dialogue interrompu localement."));
                            return 1;
                        }))));

        if (FabricLoader.getInstance().isModLoaded("easy_npc")) {
            EasyNpcClientBridge.init();
        }
        if (FabricLoader.getInstance().isModLoaded("bettercombat")) {
            BetterCombatClientCompat.init();
        }
        net.hautecapitale.dialogue.client.dev.DevToolsClient.init();
        if (DialogueSelfTest.demande()) {
            DialogueSelfTest.register();
        }

        HauteCapitaleDialogue.LOGGER.info("Dialogue RPG : client prêt.");
    }
}
