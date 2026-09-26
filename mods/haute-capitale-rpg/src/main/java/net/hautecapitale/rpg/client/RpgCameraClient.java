package net.hautecapitale.rpg.client;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayConnectionEvents;
import net.fabricmc.fabric.api.client.rendering.v1.hud.HudElementRegistry;
import net.fabricmc.fabric.api.client.rendering.v1.hud.VanillaHudElements;
import net.hautecapitale.rpg.HauteCapitaleRpg;
import net.hautecapitale.rpg.client.camera.AimReticle;
import net.hautecapitale.rpg.client.camera.CameraDebugOverlay;
import net.hautecapitale.rpg.client.camera.CameraModule;

/**
 * Point d'entrée client du mod.
 *
 * <p>Le noyau RPG reste un mod serveur : classes, progression, verrous et
 * commandes ne dépendent d'aucune de ces classes. Ce point d'entrée n'est
 * référencé que par {@code fabric.mod.json}, sous l'entrée {@code client} — un
 * serveur dédié ne charge donc jamais rien de ce paquet.
 */
public class RpgCameraClient implements ClientModInitializer {

    @Override
    public void onInitializeClient() {
        CameraModule.init();

        HudElementRegistry.attachElementAfter(
                VanillaHudElements.CROSSHAIR, HauteCapitaleRpg.id("aim_reticle"), new AimReticle());
        HudElementRegistry.addLast(
                HauteCapitaleRpg.id("camera_debug"), new CameraDebugOverlay());

        // START : en tete de MinecraftClient#tick, avant handleInputEvents — la
        // seule place ou consommer F5 avant que le jeu ne la voie.
        ClientTickEvents.START_CLIENT_TICK.register(CameraModule::tickDebut);
        ClientTickEvents.END_CLIENT_TICK.register(client -> CameraModule.tick());

        // Changement de monde ou de serveur : aucune suspension ne doit survivre a
        // la deconnexion. Une cinematique interrompue par un /kick laisserait sinon
        // la camera RPG suspendue pour toute la session suivante.
        ClientPlayConnectionEvents.JOIN.register((handler, sender, client) -> CameraModule.reset());
        ClientPlayConnectionEvents.DISCONNECT.register((handler, client) -> CameraModule.reset());

        HauteCapitaleRpg.LOGGER.info("Camera RPG chargee.");
    }
}
