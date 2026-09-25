package fr.hautecapitale.armorbalance;

import net.fabricmc.api.ModInitializer;

public final class CapitaleArmorBalance implements ModInitializer {
    @Override
    public void onInitialize() {
        System.out.println("[capitale_armor_balance] ARMOR40 runtime x2 registered: effective ItemStack ARMOR ADD_VALUE on armor slots only; weapons/toughness/HP unchanged.");
    }
}
