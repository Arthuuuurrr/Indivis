package net.hautecapitale.party.client;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonParseException;
import net.fabricmc.loader.api.FabricLoader;
import net.hautecapitale.party.HauteCapitaleParty;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Locale;

/**
 * Reglages du HUD, propres a chaque client.
 *
 * <p>Fichier {@code config/haute_capitale_party_client.json}. Position et echelle
 * sont la des le premier jour : quatre systemes se disputent deja le haut de l'ecran
 * sur ce serveur, et un HUD qu'on ne peut pas deplacer finit par en recouvrir un.
 */
public final class HudSettings {

    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();
    private static final String FILE_NAME = "haute_capitale_party_client.json";
    private static HudSettings instance = new HudSettings();

    public enum Anchor {
        TOP_LEFT, TOP_RIGHT, BOTTOM_LEFT, BOTTOM_RIGHT;

        static Anchor parse(String raw) {
            if (raw == null) {
                return TOP_LEFT;
            }
            try {
                return valueOf(raw.trim().toUpperCase(Locale.ROOT));
            } catch (IllegalArgumentException ignored) {
                return TOP_LEFT;
            }
        }
    }

    public boolean enabled = true;

    /** Coin de l'ecran auquel le bloc est accroche. */
    public String anchor = "TOP_LEFT";

    /** Decalage depuis ce coin, en pixels d'interface, avant mise a l'echelle. */
    public int offsetX = 4;
    public int offsetY = 4;

    /** 0.5 a 2.0. */
    public float scale = 1.0f;

    /** Masque sa propre ligne : on a deja sa vie sous les yeux. */
    public boolean hideSelf = false;

    public boolean showDistance = true;
    public boolean showClassAndLevel = true;

    public static HudSettings get() {
        return instance;
    }

    public Anchor anchorValue() {
        return Anchor.parse(this.anchor);
    }

    public static void load() {
        Path path = FabricLoader.getInstance().getConfigDir().resolve(FILE_NAME);
        if (!Files.exists(path)) {
            instance = new HudSettings();
            save(path);
            return;
        }
        try {
            HudSettings loaded = GSON.fromJson(Files.readString(path, StandardCharsets.UTF_8), HudSettings.class);
            instance = loaded != null ? loaded : new HudSettings();
            instance.clamp();
        } catch (IOException | JsonParseException exception) {
            instance = new HudSettings();
            HauteCapitaleParty.LOGGER.error("Reglages du HUD illisibles ({}), valeurs par defaut : {}",
                    path, exception.getMessage());
        }
    }

    private void clamp() {
        this.scale = Math.clamp(this.scale, 0.5f, 2.0f);
        this.offsetX = Math.clamp(this.offsetX, 0, 4000);
        this.offsetY = Math.clamp(this.offsetY, 0, 4000);
        this.anchor = anchorValue().name();
    }

    private static void save(Path path) {
        try {
            Files.createDirectories(path.getParent());
            Files.writeString(path, GSON.toJson(instance), StandardCharsets.UTF_8);
        } catch (IOException exception) {
            HauteCapitaleParty.LOGGER.error("Impossible d'ecrire les reglages du HUD : {}", exception.getMessage());
        }
    }
}
