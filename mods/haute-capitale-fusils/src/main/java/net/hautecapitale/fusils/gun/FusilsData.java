package net.hautecapitale.fusils.gun;

import java.io.BufferedReader;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.function.BiConsumer;

import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import com.mojang.serialization.Codec;
import com.mojang.serialization.JsonOps;

import net.hautecapitale.fusils.FusilsIds;
import net.hautecapitale.fusils.HauteCapitaleFusils;
import net.minecraft.resources.Identifier;
import net.minecraft.server.packs.resources.Resource;
import net.minecraft.server.packs.resources.ResourceManager;

/**
 * Les quatre registres de données du mod (profils, variantes, munitions, modifications).
 * Chargés côté serveur depuis les datapacks, envoyés aux clients à la connexion et après
 * {@code /reload} sous forme de JSON, pour que le client prédise le tir avec les mêmes chiffres.
 */
public final class FusilsData {
	public static final String PROFILES_DIR = "fusil_profil";
	public static final String VARIANTS_DIR = "fusil_variante";
	public static final String AMMO_DIR = "fusil_munition";
	public static final String MODS_DIR = "fusil_modification";

	private static final Map<Identifier, GunProfile> PROFILES = new LinkedHashMap<>();
	private static final Map<Identifier, GunVariant> VARIANTS = new LinkedHashMap<>();
	private static final Map<Identifier, AmmoType> AMMO = new LinkedHashMap<>();
	private static final Map<Identifier, GunModification> MODS = new LinkedHashMap<>();
	/** Copie JSON brute de chaque définition, prête à être envoyée aux clients. */
	private static final Map<String, Map<Identifier, String>> RAW = new LinkedHashMap<>();

	private FusilsData() {}

	public static Map<Identifier, GunProfile> profiles() { return Collections.unmodifiableMap(PROFILES); }
	public static Map<Identifier, GunVariant> variants() { return Collections.unmodifiableMap(VARIANTS); }
	public static Map<Identifier, AmmoType> ammo() { return Collections.unmodifiableMap(AMMO); }
	public static Map<Identifier, GunModification> modifications() { return Collections.unmodifiableMap(MODS); }

	public static GunProfile profile(Identifier id) {
		GunProfile p = PROFILES.get(id);
		return p != null ? p : GunProfile.fallback(id.getPath());
	}

	public static boolean hasProfile(Identifier id) { return PROFILES.containsKey(id); }
	public static GunVariant variant(Identifier id) { return VARIANTS.get(id); }
	public static AmmoType ammo(Identifier id) { return AMMO.get(id); }
	public static GunModification modification(Identifier id) { return MODS.get(id); }

	public static Map<String, Map<Identifier, String>> raw() { return RAW; }

	/** Chargement serveur depuis le gestionnaire de ressources (jar du mod + datapacks). */
	public static void load(ResourceManager manager) {
		clear();
		read(manager, PROFILES_DIR, GunProfile.CODEC, PROFILES::put);
		read(manager, VARIANTS_DIR, GunVariant.CODEC, VARIANTS::put);
		read(manager, AMMO_DIR, AmmoType.CODEC, AMMO::put);
		read(manager, MODS_DIR, GunModification.CODEC, MODS::put);
		HauteCapitaleFusils.LOGGER.info("[fusils] {} profils, {} variantes, {} munitions, {} modifications charges",
				PROFILES.size(), VARIANTS.size(), AMMO.size(), MODS.size());
	}

	/** Application côté client de ce que le serveur a envoyé. */
	public static void applySync(Map<String, Map<Identifier, String>> raw) {
		clear();
		raw.forEach((dir, entries) -> entries.forEach((id, json) -> {
			try {
				JsonElement element = JsonParser.parseString(json);
				switch (dir) {
					case PROFILES_DIR -> decode(GunProfile.CODEC, element, id).ifPresent(v -> PROFILES.put(id, v));
					case VARIANTS_DIR -> decode(GunVariant.CODEC, element, id).ifPresent(v -> VARIANTS.put(id, v));
					case AMMO_DIR -> decode(AmmoType.CODEC, element, id).ifPresent(v -> AMMO.put(id, v));
					case MODS_DIR -> decode(GunModification.CODEC, element, id).ifPresent(v -> MODS.put(id, v));
					default -> {}
				}
				RAW.computeIfAbsent(dir, d -> new LinkedHashMap<>()).put(id, json);
			} catch (Exception e) {
				HauteCapitaleFusils.LOGGER.warn("[fusils] definition {} illisible : {}", id, e.toString());
			}
		}));
	}

	private static void clear() {
		PROFILES.clear();
		VARIANTS.clear();
		AMMO.clear();
		MODS.clear();
		RAW.clear();
	}

	private static <T> void read(ResourceManager manager, String dir, Codec<T> codec, BiConsumer<Identifier, T> sink) {
		Map<Identifier, String> rawDir = RAW.computeIfAbsent(dir, d -> new LinkedHashMap<>());
		for (Map.Entry<Identifier, Resource> entry : manager.listResources(dir, p -> p.getPath().endsWith(".json")).entrySet()) {
			Identifier file = entry.getKey();
			String path = file.getPath().substring(dir.length() + 1, file.getPath().length() - ".json".length());
			Identifier id = Identifier.fromNamespaceAndPath(file.getNamespace(), path);
			try (BufferedReader reader = entry.getValue().openAsReader()) {
				JsonElement element = JsonParser.parseReader(reader);
				decode(codec, element, id).ifPresent(v -> {
					sink.accept(id, v);
					rawDir.put(id, element.toString());
				});
			} catch (Exception e) {
				HauteCapitaleFusils.LOGGER.error("[fusils] impossible de lire {} : {}", file, e.toString());
			}
		}
	}

	private static <T> java.util.Optional<T> decode(Codec<T> codec, JsonElement element, Identifier id) {
		var result = codec.parse(JsonOps.INSTANCE, element);
		if (result.isError()) {
			HauteCapitaleFusils.LOGGER.error("[fusils] definition {} invalide : {}", id, result.error().map(Object::toString).orElse("?"));
			return java.util.Optional.empty();
		}
		return result.result();
	}

	public static Identifier idOf(String s) {
		return FusilsIds.parse(s);
	}
}
