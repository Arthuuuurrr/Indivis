package net.hautecapitale.fusils.registry;

import net.hautecapitale.fusils.FusilsIds;
import net.hautecapitale.fusils.menu.AtelierMenu;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.flag.FeatureFlags;
import net.minecraft.world.inventory.MenuType;

public final class FusilsMenus {
	public static final MenuType<AtelierMenu> ATELIER = Registry.register(BuiltInRegistries.MENU, FusilsIds.id("atelier"),
			new MenuType<>(AtelierMenu::new, FeatureFlags.VANILLA_SET));

	private FusilsMenus() {}

	public static void init() {}
}
