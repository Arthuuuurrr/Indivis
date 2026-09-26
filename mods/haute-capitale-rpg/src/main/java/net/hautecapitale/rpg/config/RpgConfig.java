package net.hautecapitale.rpg.config;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.stream.JsonReader;
import net.fabricmc.loader.api.FabricLoader;
import net.hautecapitale.rpg.HauteCapitaleRpg;

import java.io.IOException;
import java.io.Reader;
import java.io.Writer;
import java.nio.file.Files;
import java.nio.file.Path;

/**
 * Configuration du noyau, en JSON, rechargeable sans recompilation.
 *
 * <p>Rien de ce qui s'équilibre ne doit vivre en dur dans du Java : niveau
 * maximum, liberté de choisir sa classe, possibilité d'en changer. Les valeurs
 * de gameplay des classes elles-mêmes appartiennent à leurs mods d'origine et ne
 * sont pas reprises ici.
 */
public final class RpgConfig {

    /** Le joueur peut-il choisir sa classe lui-même, ou seul un MJ l'attribue ? */
    public boolean choix_libre = true;

    /** Le joueur peut-il changer de classe une fois choisie ? */
    public boolean changement_autorise = false;

    /** Journaliser au démarrage les mods de classe détectés. */
    public boolean journaliser_detection = true;

    /**
     * Empêcher un joueur de porter l'équipement d'une autre classe.
     *
     * <p>Désactivé par défaut : installer le noyau sur un serveur en cours ne
     * doit rien changer tant que l'administrateur ne l'a pas décidé.
     */
    public boolean verrou_equipement = false;

    /** Intervalle de vérification du verrou, en ticks. 20 = une fois par seconde. */
    public int intervalle_verrou_ticks = 20;

    private static RpgConfig instance = new RpgConfig();

    private RpgConfig() {
    }

    public static RpgConfig get() {
        return instance;
    }

    private static Path file() {
        return FabricLoader.getInstance().getConfigDir().resolve(HauteCapitaleRpg.MOD_ID + ".json");
    }

    /**
     * Charge la configuration, ou en écrit une par défaut si elle manque.
     *
     * <p>Gson est permissif par défaut et avale des virgules finales que
     * Minecraft refuserait ; la lecture est donc faite en mode strict pour qu'un
     * fichier mal formé se signale tout de suite au lieu d'être à moitié lu.
     */
    public static void load() {
        Path path = file();
        Gson gson = new GsonBuilder().setPrettyPrinting().create();

        if (Files.notExists(path)) {
            save(gson, path, new RpgConfig());
            return;
        }

        try (Reader reader = Files.newBufferedReader(path);
             JsonReader json = new JsonReader(reader)) {
            json.setStrictness(com.google.gson.Strictness.STRICT);
            RpgConfig loaded = gson.fromJson(json, RpgConfig.class);
            if (loaded != null) {
                instance = loaded;
            }
        } catch (Exception e) {
            HauteCapitaleRpg.LOGGER.error(
                    "Configuration illisible ({}) — valeurs par defaut conservees, fichier laisse intact.",
                    e.getMessage());
            return;
        }

        // Reecriture systematique : un fichier ecrit par une version anterieure ne
        // contient pas les nouveaux reglages, et un administrateur ne peut pas
        // activer une option qu il ne voit pas.
        try {
            save(gson, path, instance);
        } catch (Exception e) {
            HauteCapitaleRpg.LOGGER.warn("Configuration non reecrite : {}", e.getMessage());
        }
    }

    private static void save(Gson gson, Path path, RpgConfig config) {
        try {
            Files.createDirectories(path.getParent());
            try (Writer writer = Files.newBufferedWriter(path)) {
                gson.toJson(config, writer);
            }
        } catch (IOException e) {
            HauteCapitaleRpg.LOGGER.error("Impossible d'écrire la configuration : {}", e.getMessage());
        }
    }
}
