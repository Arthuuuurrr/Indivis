package net.hautecapitale.party.client;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayConnectionEvents;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.client.rendering.v1.hud.HudElementRegistry;
import net.hautecapitale.party.HauteCapitaleParty;
import net.hautecapitale.party.network.HudPayload;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.util.InputUtil;
import net.minecraft.util.Identifier;
import org.lwjgl.glfw.GLFW;

/**
 * Le seul code client du mod : recevoir l'instantane, le dessiner.
 *
 * <p>Tout le reste — commandes, invitations, dialogues, tir ami, plaques colorees —
 * marche sans lui. Un joueur qui n'installe pas le mod perd le HUD et rien d'autre.
 */
public final class PartyClient implements ClientModInitializer {

    public static final Identifier HUD_ID = Identifier.of(HauteCapitaleParty.MOD_ID, "party_hud");

    private static KeyBinding openMenuKey;

    @Override
    public void onInitializeClient() {
        HudSettings.load();

        // Touche « P » (rebindable) : ouvre le menu de groupe. Le menu lui-meme est un dialogue
        // vanilla construit par le serveur (aucun ecran maison a dessiner) -> on demande juste au
        // serveur de l'ouvrir via une commande, que le joueur n'a donc jamais a taper.
        openMenuKey = KeyBindingHelper.registerKeyBinding(new KeyBinding(
                "key.haute_capitale_party.menu",
                InputUtil.Type.KEYSYM,
                // Touche « ² » (grave, sous Echap) par defaut : P est deja la touche vanilla des
                // « Interactions sociales » (conflit). Rebindable dans Options > Commandes.
                GLFW.GLFW_KEY_GRAVE_ACCENT,
                KeyBinding.Category.MULTIPLAYER));
        ClientTickEvents.END_CLIENT_TICK.register(client -> {
            while (openMenuKey.wasPressed()) {
                if (client.player != null && client.currentScreen == null && client.getNetworkHandler() != null) {
                    client.getNetworkHandler().sendChatCommand("party menu");
                }
            }
        });

        // Fabric livre le paquet sur le fil du client : pas de synchronisation a faire
        // au-dela du volatile de HudModel.
        ClientPlayNetworking.registerGlobalReceiver(HudPayload.ID,
                (payload, context) -> HudModel.accept(payload));

        // Efface a la deconnexion : un instantane d'une autre partie n'a plus de sens.
        ClientPlayConnectionEvents.DISCONNECT.register((handler, client) -> HudModel.clear());

        HudElementRegistry.addLast(HUD_ID, new HudRenderer());

        HauteCapitaleParty.LOGGER.info("HUD de groupe pret (echelle {}, ancre {}).",
                HudSettings.get().scale, HudSettings.get().anchorValue());
    }
}
