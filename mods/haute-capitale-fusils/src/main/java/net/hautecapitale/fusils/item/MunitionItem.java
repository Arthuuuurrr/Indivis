package net.hautecapitale.fusils.item;

import java.util.function.Consumer;

import net.hautecapitale.fusils.gun.AmmoType;
import net.hautecapitale.fusils.gun.FusilsData;
import net.hautecapitale.fusils.gun.GunplayManager;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.component.TooltipDisplay;
import net.minecraft.world.level.Level;

/**
 * Une munition spéciale consommable (balle d'Aetherium…) : clic droit avec un fusil dans la main
 * principale pour la charger. Les autres munitions viennent des compétences, sans objet.
 */
public class MunitionItem extends Item {
	private final Identifier ammoId;

	public MunitionItem(Properties properties, Identifier ammoId) {
		super(properties);
		this.ammoId = ammoId;
	}

	public Identifier ammoId() {
		return this.ammoId;
	}

	@Override
	public InteractionResult use(Level level, Player player, InteractionHand hand) {
		if (hand != InteractionHand.OFF_HAND) return InteractionResult.PASS;
		ItemStack gun = player.getMainHandItem();
		if (!FusilItem.isGun(gun)) return InteractionResult.PASS;
		if (!level.isClientSide()) {
			AmmoType ammo = FusilsData.ammo(this.ammoId);
			if (ammo == null) return InteractionResult.FAIL;
			if (GunplayManager.charge(player, gun, this.ammoId, ammo.defaultShots())) {
				player.getItemInHand(hand).consume(1, player);
			}
		}
		return InteractionResult.SUCCESS;
	}

	@Override
	public void appendHoverText(ItemStack stack, TooltipContext context, TooltipDisplay display, Consumer<Component> lines, TooltipFlag flag) {
		AmmoType ammo = FusilsData.ammo(this.ammoId);
		if (ammo != null) {
			lines.accept(Component.translatable("tooltip.haute_capitale_fusils.munition_tirs", ammo.defaultShots()).withStyle(ChatFormatting.GRAY));
		}
		lines.accept(Component.translatable("tooltip.haute_capitale_fusils.munition_usage").withStyle(ChatFormatting.DARK_GRAY, ChatFormatting.ITALIC));
	}
}
