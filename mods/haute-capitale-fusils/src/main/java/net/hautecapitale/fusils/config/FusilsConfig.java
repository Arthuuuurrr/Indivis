package net.hautecapitale.fusils.config;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import net.fabricmc.loader.api.FabricLoader;
import net.hautecapitale.fusils.HauteCapitaleFusils;

/** Réglages du serveur et du client, dans {@code config/haute_capitale_fusils.json}. */
public final class FusilsConfig {
	private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();
	private static final Path FILE = FabricLoader.getInstance().getConfigDir().resolve("haute_capitale_fusils.json");
	public static FusilsConfig INSTANCE = new FusilsConfig();

	// --- Serveur -------------------------------------------------------------
	/** "item" : la recharge consomme des balles de l'inventaire ; "free" : munitions standard illimitées. */
	public String ammo_mode = "item";
	/** Tolérance (degrés) entre la direction envoyée par le client et la vue connue du serveur. */
	public double aim_tolerance_degrees = 8.0;
	/** Les balles d'un joueur touchent-elles les autres joueurs ? (les équipes alliées restent protégées) */
	public boolean player_vs_player = true;
	/** Multiplicateur global des dégâts des balles (équilibrage rapide). */
	public double global_damage_multiplier = 1.0;
	/** Durée par défaut de la Marque du Chasseur (ticks). */
	public int mark_duration_ticks = 200;
	/** Rafale : nombre de coups et intervalle en ticks. */
	public int burst_shots = 3;
	public int burst_interval_ticks = 3;
	/** Repli tactique : force de l'impulsion arrière et chambres rechargées instantanément. */
	public double retreat_strength = 1.2;
	public int retreat_reload_rounds = 1;
	/** La rafale consomme-t-elle des munitions ? */
	public boolean burst_consumes_ammo = true;

	// --- Client --------------------------------------------------------------
	public boolean hud_enabled = true;
	public String hud_anchor = "bottom_right"; // bottom_right, bottom_left, top_right, top_left, hotbar
	public int hud_offset_x = 24;
	public int hud_offset_y = 24;
	public double hud_scale = 2.0;
	public boolean hud_show_reserve = true;
	public boolean hud_flash_on_empty = true;
	public boolean crosshair_enabled = true;
	public double camera_recoil_scale = 1.0;
	public boolean hitmarker_enabled = true;

	public static void load() {
		try {
			if (Files.exists(FILE)) {
				INSTANCE = GSON.fromJson(Files.readString(FILE, StandardCharsets.UTF_8), FusilsConfig.class);
				if (INSTANCE == null) INSTANCE = new FusilsConfig();
			}
			save();
		} catch (Exception e) {
			HauteCapitaleFusils.LOGGER.error("[fusils] config illisible, valeurs par defaut : {}", e.toString());
			INSTANCE = new FusilsConfig();
		}
	}

	public static void save() {
		try {
			Files.createDirectories(FILE.getParent());
			Files.writeString(FILE, GSON.toJson(INSTANCE), StandardCharsets.UTF_8);
		} catch (IOException e) {
			HauteCapitaleFusils.LOGGER.error("[fusils] impossible d'ecrire la config : {}", e.toString());
		}
	}

	public boolean freeAmmo() {
		return "free".equalsIgnoreCase(this.ammo_mode);
	}
}
