package net.hautecapitale.fusils.menu;

import net.hautecapitale.fusils.data.component.InstalledMods;
import net.hautecapitale.fusils.gun.AmmoType;
import net.hautecapitale.fusils.gun.FusilsData;
import net.hautecapitale.fusils.gun.GunModification;
import net.hautecapitale.fusils.gun.GunplayManager;
import net.hautecapitale.fusils.item.FusilItem;
import net.hautecapitale.fusils.item.ModificationItem;
import net.hautecapitale.fusils.item.MunitionItem;
import net.hautecapitale.fusils.registry.FusilsComponents;
import net.hautecapitale.fusils.registry.FusilsMenus;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.Container;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.SimpleMenuProvider;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;

/**
 * L'atelier de l'arme : trois emplacements typés (Canon, Mécanisme, Munition). Les pièces posées
 * dans Canon / Mécanisme sont installées sur l'arme en main ; une munition consommable posée dans
 * le troisième emplacement charge l'arme et est consommée. Aucun bloc requis : Adventure mode.
 */
public class AtelierMenu extends AbstractContainerMenu {
	public static final int SLOT_CANON = 0;
	public static final int SLOT_MECANISME = 1;
	public static final int SLOT_MUNITION = 2;
	public static final int[][] SLOT_POS = { {44, 35}, {80, 35}, {116, 35} };

	private final SimpleContainer parts = new SimpleContainer(3);
	private final Player player;
	private final ItemStack gun;
	private boolean loading = true;

	public AtelierMenu(int syncId, Inventory inventory) {
		super(FusilsMenus.ATELIER, syncId);
		this.player = inventory.player;
		this.gun = inventory.player.getMainHandItem();

		this.addSlot(new PartSlot(this.parts, SLOT_CANON, SLOT_POS[0][0], SLOT_POS[0][1], GunModification.Category.CANON));
		this.addSlot(new PartSlot(this.parts, SLOT_MECANISME, SLOT_POS[1][0], SLOT_POS[1][1], GunModification.Category.MECANISME));
		this.addSlot(new Slot(this.parts, SLOT_MUNITION, SLOT_POS[2][0], SLOT_POS[2][1]) {
			@Override
			public boolean mayPlace(ItemStack stack) {
				return stack.getItem() instanceof MunitionItem;
			}
		});
		for (int row = 0; row < 3; row++) {
			for (int col = 0; col < 9; col++) {
				this.addSlot(new Slot(inventory, col + row * 9 + 9, 8 + col * 18, 84 + row * 18));
			}
		}
		for (int col = 0; col < 9; col++) {
			this.addSlot(new Slot(inventory, col, 8 + col * 18, 142));
		}

		// Reflète les modifications déjà installées par des objets représentatifs.
		InstalledMods mods = this.gun.getOrDefault(FusilsComponents.MODS, InstalledMods.EMPTY);
		mods.get(GunModification.Category.CANON).ifPresent(id -> this.parts.setItem(SLOT_CANON, representative(id)));
		mods.get(GunModification.Category.MECANISME).ifPresent(id -> this.parts.setItem(SLOT_MECANISME, representative(id)));
		this.loading = false;
	}

	public static void open(ServerPlayer player) {
		player.openMenu(new SimpleMenuProvider((id, inv, p) -> new AtelierMenu(id, inv), Component.translatable("menu.haute_capitale_fusils.atelier")));
	}

	private static ItemStack representative(Identifier modId) {
		GunModification mod = FusilsData.modification(modId);
		Identifier itemId = mod != null && mod.item().isPresent() ? mod.item().get() : modId;
		return BuiltInRegistries.ITEM.get(itemId).map(h -> new ItemStack(h.value())).orElse(ItemStack.EMPTY);
	}

	public ItemStack gun() {
		return this.gun;
	}

	@Override
	public void slotsChanged(Container container) {
		super.slotsChanged(container);
		if (this.loading || this.player.level().isClientSide()) return;
		apply();
	}

	/** Serveur : écrit l'état des emplacements dans les composants de l'arme. */
	private void apply() {
		InstalledMods mods = InstalledMods.EMPTY;
		Identifier canon = ModificationItem.modificationOf(this.parts.getItem(SLOT_CANON));
		Identifier meca = ModificationItem.modificationOf(this.parts.getItem(SLOT_MECANISME));
		if (canon != null) mods = mods.with(GunModification.Category.CANON, canon);
		if (meca != null) mods = mods.with(GunModification.Category.MECANISME, meca);
		this.gun.set(FusilsComponents.MODS, mods);

		ItemStack ammoStack = this.parts.getItem(SLOT_MUNITION);
		if (ammoStack.getItem() instanceof MunitionItem mi) {
			AmmoType ammo = FusilsData.ammo(mi.ammoId());
			if (ammo != null && GunplayManager.charge(this.player, this.gun, mi.ammoId(), ammo.defaultShots())) {
				ammoStack.shrink(1);
				this.parts.setItem(SLOT_MUNITION, ammoStack.isEmpty() ? ItemStack.EMPTY : ammoStack);
			}
		}
	}

	@Override
	public ItemStack quickMoveStack(Player player, int index) {
		Slot slot = this.slots.get(index);
		if (!slot.hasItem()) return ItemStack.EMPTY;
		ItemStack stack = slot.getItem();
		ItemStack copy = stack.copy();
		if (index < 3) {
			if (!moveItemStackTo(stack, 3, this.slots.size(), true)) return ItemStack.EMPTY;
		} else {
			GunModification.Category cat = categoryOf(stack);
			boolean moved = false;
			if (cat == GunModification.Category.CANON) moved = moveItemStackTo(stack, SLOT_CANON, SLOT_CANON + 1, false);
			else if (cat == GunModification.Category.MECANISME) moved = moveItemStackTo(stack, SLOT_MECANISME, SLOT_MECANISME + 1, false);
			else if (stack.getItem() instanceof MunitionItem) moved = moveItemStackTo(stack, SLOT_MUNITION, SLOT_MUNITION + 1, false);
			if (!moved) return ItemStack.EMPTY;
		}
		if (stack.isEmpty()) slot.setByPlayer(ItemStack.EMPTY);
		else slot.setChanged();
		return copy;
	}

	private static GunModification.Category categoryOf(ItemStack stack) {
		Identifier id = ModificationItem.modificationOf(stack);
		if (id == null) return null;
		GunModification mod = FusilsData.modification(id);
		return mod == null ? null : mod.category();
	}

	@Override
	public boolean stillValid(Player player) {
		return !player.isSpectator() && player.getMainHandItem() == this.gun && FusilItem.isGun(this.gun);
	}

	@Override
	public void removed(Player player) {
		super.removed(player);
		if (!player.level().isClientSide()) {
			// Les pièces restent installées : elles ne retournent pas à l'inventaire, l'arme les porte.
			ItemStack ammo = this.parts.getItem(SLOT_MUNITION);
			if (!ammo.isEmpty()) {
				player.getInventory().placeItemBackInInventory(ammo);
			}
		}
	}

	private static class PartSlot extends Slot {
		private final GunModification.Category category;

		PartSlot(Container container, int index, int x, int y, GunModification.Category category) {
			super(container, index, x, y);
			this.category = category;
		}

		@Override
		public boolean mayPlace(ItemStack stack) {
			return categoryOf(stack) == this.category;
		}

		@Override
		public int getMaxStackSize() {
			return 1;
		}
	}
}
