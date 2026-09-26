package net.hautecapitale.metiers.npc;

import net.fabricmc.fabric.api.screenhandler.v1.ExtendedScreenHandlerType;
import net.hautecapitale.metiers.HauteCapitaleMetiers;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.screen.ScreenHandlerType;

/** Le seul type d'écran du mod. Les neuf rôles le partagent. */
public final class HcmScreens {

    public static final ExtendedScreenHandlerType<ProfessionMenu, ProfessionScreenData> PROFESSION =
            new ExtendedScreenHandlerType<>(ProfessionMenu::new, ProfessionScreenData.PACKET_CODEC);

    private HcmScreens() {
    }

    public static void init() {
        Registry.register(Registries.SCREEN_HANDLER, HauteCapitaleMetiers.id("profession"), PROFESSION);
    }
}
