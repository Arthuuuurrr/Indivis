package net.hautecapitale.fusils.api;

import java.util.Optional;

import net.hautecapitale.fusils.data.component.InstalledMods;
import net.hautecapitale.fusils.gun.ComposedShot;
import net.hautecapitale.fusils.gun.FusilsData;
import net.hautecapitale.fusils.gun.GunModification;
import net.hautecapitale.fusils.gun.GunVariant;
import net.hautecapitale.fusils.gun.GunplayManager;
import net.hautecapitale.fusils.gun.HunterMark;
import net.hautecapitale.fusils.gun.ShotComposer;
import net.hautecapitale.fusils.item.FusilItem;
import net.hautecapitale.fusils.registry.FusilsComponents;
import net.hautecapitale.fusils.registry.FusilsItems;
import net.hautecapitale.fusils.skills.HunterSkills;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.Identifier;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;

/**
 * Point d'entrée Java pour les autres mods du serveur (noyau RPG, capacités, PNJ).
 * Tout ce que font les commandes {@code /fusil} passe par ici.
 */
public final class FusilAPI {
	private FusilAPI() {}

	/** Fabrique un fusil : {@code id} est un profil ({@code arquebuse}) ou une variante ({@code arquebuse_chasse_epique}). */
	public static Optional<ItemStack> create(Identifier id) {
		GunVariant variant = FusilsData.variant(id);
		if (variant != null) {
			return gunItemFor(variant.profile()).map(item -> {
				ItemStack stack = new ItemStack(item);
				stack.set(FusilsComponents.VARIANT, id);
				return stack;
			});
		}
		return gunItemFor(id).map(ItemStack::new);
	}

	public static Optional<FusilItem> gunItemFor(Identifier profileId) {
		for (FusilItem gun : FusilsItems.GUNS) {
			if (gun.profileId().equals(profileId)) return Optional.of(gun);
		}
		if (BuiltInRegistries.ITEM.get(profileId).map(h -> h.value()).orElse(null) instanceof FusilItem gun) return Optional.of(gun);
		return Optional.empty();
	}

	/** Tire avec l'arme équipée, avec ses statistiques réelles. */
	public static boolean fireEquipped(LivingEntity shooter, GunplayManager.FireOptions options) {
		return GunplayManager.tryFire(shooter, shooter.getLookAngle(), options);
	}

	public static boolean chargeSpecialAmmo(LivingEntity holder, Identifier ammoId, int shots) {
		return GunplayManager.charge(holder, holder.getMainHandItem(), ammoId, shots);
	}

	public static Optional<String> useSkill(LivingEntity user, String skill, int param) {
		return HunterSkills.use(user, skill, param);
	}

	public static void mark(LivingEntity target, LivingEntity marker, int ticks) {
		HunterMark.apply(target, marker, ticks);
	}

	public static boolean isMarkedBy(LivingEntity target, LivingEntity marker) {
		return HunterMark.isMarkedBy(target, marker);
	}

	public static ComposedShot compose(LivingEntity holder, ItemStack gun) {
		return ShotComposer.compose(holder, gun);
	}

	public static boolean install(ItemStack gun, Identifier modificationId) {
		GunModification mod = FusilsData.modification(modificationId);
		if (mod == null || !FusilItem.isGun(gun)) return false;
		InstalledMods mods = gun.getOrDefault(FusilsComponents.MODS, InstalledMods.EMPTY);
		gun.set(FusilsComponents.MODS, mods.with(mod.category(), modificationId));
		return true;
	}

	public static void uninstall(ItemStack gun, GunModification.Category category) {
		InstalledMods mods = gun.getOrDefault(FusilsComponents.MODS, InstalledMods.EMPTY);
		gun.set(FusilsComponents.MODS, mods.with(category, null));
	}

	public static int instantReload(LivingEntity holder, int rounds) {
		return GunplayManager.instantReload(holder, holder.getMainHandItem(), rounds);
	}
}
