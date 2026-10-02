package net.hautecapitale.fusils.registry;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;

import net.hautecapitale.fusils.FusilsIds;
import net.hautecapitale.fusils.item.FusilItem;
import net.hautecapitale.fusils.item.ModificationItem;
import net.hautecapitale.fusils.item.MunitionItem;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;

/**
 * Objets du mod. Les armes sont fixes (un item = un profil), tout le reste est en données.
 * Aucune recette : distribution par commandes, PNJ, loot.
 */
public final class FusilsItems {
	public static final List<FusilItem> GUNS = new ArrayList<>();
	public static final List<Item> ALL = new ArrayList<>();

	/** Munition standard consommée à la recharge (ou tout objet du tag {@code #haute_capitale_fusils:balles}). */
	public static final TagKey<Item> BALLES_TAG = TagKey.create(Registries.ITEM, FusilsIds.id("balles"));
	public static final Item BALLE = reg("balle", p -> new Item(p.stacksTo(64)));

	public static final FusilItem PISTOLET_SILEX = gun("pistolet_silex");
	public static final FusilItem MOUSQUET = gun("mousquet");
	public static final FusilItem ARQUEBUSE = gun("arquebuse");
	public static final FusilItem TROMBLON = gun("tromblon");
	public static final FusilItem FUSIL_ROUAGES = gun("fusil_rouages");
	public static final FusilItem CANON_MAIN = gun("canon_main");

	// Modifications — Canon
	public static final Item CANON_RAYE = mod("canon_raye");
	public static final Item CANON_EVASE = mod("canon_evase");
	public static final Item CANON_LOURD = mod("canon_lourd");
	public static final Item CANON_LONG = mod("canon_long");
	public static final Item LONGUE_VUE = mod("longue_vue");
	// Modifications — Mécanisme
	public static final Item DETENTE_LEGERE = mod("detente_legere");
	public static final Item EVENT_VAPEUR = mod("event_vapeur");
	public static final Item HUILE_ARMURIER = mod("huile_armurier");
	public static final Item RESSORT_AMORTISSEUR = mod("ressort_amortisseur");
	public static final Item CHAMBRE_VENT = mod("chambre_vent");
	public static final Item REPETEUR_ROUAGES = mod("repeteur_rouages");
	public static final Item ACCELERATEUR_VOLANT = mod("accelerateur_volant");
	public static final Item DOUBLE_CHAMBRE = mod("double_chambre");
	public static final Item CULASSE_RUNIQUE = mod("culasse_runique");

	// Munitions consommables (les autres sont des charges de compétence)
	public static final Item BALLE_AETHERIUM = ammoItem("balle_aetherium", "aetherium");
	public static final Item BALLE_ARGENT = ammoItem("balle_argent", "argent");

	private FusilsItems() {}

	private static <T extends Item> T reg(String name, Function<Item.Properties, T> factory) {
		Identifier id = FusilsIds.id(name);
		T item = factory.apply(new Item.Properties().setId(ResourceKey.create(Registries.ITEM, id)));
		Registry.register(BuiltInRegistries.ITEM, id, item);
		ALL.add(item);
		return item;
	}

	private static FusilItem gun(String name) {
		FusilItem gun = reg(name, p -> new FusilItem(p, FusilsIds.id(name)));
		GUNS.add(gun);
		return gun;
	}

	private static Item mod(String name) {
		return reg(name, p -> new ModificationItem(p, FusilsIds.id(name)));
	}

	private static Item ammoItem(String name, String ammo) {
		return reg(name, p -> new MunitionItem(p.stacksTo(16), FusilsIds.id(ammo)));
	}

	public static void init() {}
}
