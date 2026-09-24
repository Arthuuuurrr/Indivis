package net.hautecapitale.fusils.item;

import java.util.Map;
import java.util.function.Consumer;

import net.hautecapitale.fusils.gun.FusilsData;
import net.hautecapitale.fusils.gun.GunModification;
import net.minecraft.ChatFormatting;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.component.TooltipDisplay;

/** Une pièce de modification (canon, mécanisme) : l'objet qu'on pose dans l'atelier. */
public class ModificationItem extends Item {
	private final Identifier modId;

	public ModificationItem(Properties properties, Identifier modId) {
		super(properties.stacksTo(1));
		this.modId = modId;
	}

	public Identifier modId() {
		return this.modId;
	}

	/** La modification représentée par cet objet : par son item déclaré, ou par un objet du mod portant son id. */
	public static Identifier modificationOf(ItemStack stack) {
		if (stack.isEmpty()) return null;
		Identifier itemId = BuiltInRegistries.ITEM.getKey(stack.getItem());
		for (Map.Entry<Identifier, GunModification> e : FusilsData.modifications().entrySet()) {
			if (e.getValue().item().isPresent() && e.getValue().item().get().equals(itemId)) return e.getKey();
		}
		if (stack.getItem() instanceof ModificationItem mi && FusilsData.modification(mi.modId) != null) return mi.modId;
		return null;
	}

	@Override
	public void appendHoverText(ItemStack stack, TooltipContext context, TooltipDisplay display, Consumer<Component> lines, TooltipFlag flag) {
		GunModification mod = FusilsData.modification(this.modId);
		if (mod == null) {
			lines.accept(Component.translatable("tooltip.haute_capitale_fusils.modification_inconnue").withStyle(ChatFormatting.RED));
			return;
		}
		lines.accept(Component.translatable(mod.category().translationKey()).withStyle(ChatFormatting.GOLD));
		mod.description().forEach(d -> lines.accept(d.copy().withStyle(ChatFormatting.GRAY)));
		mod.stats().forEach((stat, m) -> lines.accept(Component.literal(" ").append(net.hautecapitale.fusils.gun.ShotComposer.describe(stat, m))));
		if (mod.forceAuto()) lines.accept(Component.literal(" ").append(Component.translatable("tooltip.haute_capitale_fusils.tir_automatique").withStyle(ChatFormatting.AQUA)));
		if (mod.scope()) lines.accept(Component.literal(" ").append(Component.translatable("tooltip.haute_capitale_fusils.lunette").withStyle(ChatFormatting.AQUA)));
	}
}
