package net.hautecapitale.rpg.client.camera;

/**
 * Façade du module caméra : ce que le point d'entrée client a le droit d'appeler.
 *
 * <p>Le reste du paquet garde une visibilité de paquet. C'est délibéré : la seule
 * surface publique destinée aux autres mods est {@link CameraOverrideManager},
 * et il ne faut pas qu'un appelant extérieur puisse pousser la caméra dans un état
 * incohérent en appelant une pièce interne.
 */
public final class CameraModule {

    private CameraModule() {
    }

    public static void init() {
        CameraSettings.load();
        CameraKeybinds.register();
        CinematicBridge.init();
        if (CameraSelfTest.demande()) {
            CameraSelfTest.register();
        }
    }

    /**
     * Tête de tick, avant que le jeu ne lise ses touches : c'est là, et seulement
     * là, que F5 peut être interceptée sans mixin.
     */
    public static void tickDebut(net.minecraft.client.MinecraftClient client) {
        ViewCycle.tickDebut(client);
    }

    public static void tick() {
        CameraKeybinds.tick();
        RpgCameraManager.tick();
    }

    public static void reset() {
        RpgCameraManager.reset();
    }
}
