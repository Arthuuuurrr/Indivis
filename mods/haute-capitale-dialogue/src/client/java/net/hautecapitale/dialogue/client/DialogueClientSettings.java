package net.hautecapitale.dialogue.client;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.Strictness;
import com.google.gson.annotations.SerializedName;
import com.google.gson.stream.JsonReader;
import net.fabricmc.loader.api.FabricLoader;
import net.hautecapitale.dialogue.HauteCapitaleDialogue;

import java.io.IOException;
import java.io.Reader;
import java.io.Writer;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Préférences d'affichage du joueur. Rien ici ne remonte au serveur.
 *
 * <p>Fichier : {@code config/haute_capitale_dialogue_client.json}.
 */
public final class DialogueClientSettings {

    // --- texte --------------------------------------------------------------

    /**
     * Effet machine à écrire : {@code "easy_npc"} suit le réglage d'Easy NPC
     * ({@code dialog_client.cfg}), {@code "on"} ou {@code "off"} l'imposent.
     */
    public String typewriter = "easy_npc";

    /** Caractères par seconde ; 0 = la valeur d'Easy NPC. */
    public int caracteres_par_seconde = 0;

    /** Échelle du texte et des réponses, de 0.8 à 1.6. */
    public double echelle_texte = 1.0;

    /** Opacité du bandeau, de 0.3 à 1. */
    public double opacite_fond = 0.86;

    /** Jouer un son léger quand une réponse est choisie. */
    public boolean son_reponse = true;

    // --- confort ------------------------------------------------------------

    /**
     * La conversation se referme d'elle-même quand le personnage se tait.
     *
     * <p>Faux — le réglage livré : l'écran reste ouvert tant que le joueur n'a
     * pas pris congé, par Échap ou par une réponse d'adieu. Les répliques qui
     * arrivent ensuite viennent s'y afficher, et rien ne referme le bandeau au
     * milieu d'une lecture. La conversation se ferme encore d'elle-même si le
     * joueur s'éloigne du personnage, meurt, change de monde ou se
     * téléporte : ces sorties-là appartiennent au serveur.
     *
     * <p>Vrai : l'ancien comportement, où le silence vaut congé au bout de
     * {@link #capture_delai_silence_ms} plus le temps de lecture.
     *
     * <p>Le nom {@code fermetureAutomatique} est accepté comme synonyme.
     */
    @SerializedName(value = "fermeture_automatique", alternate = {"fermetureAutomatique"})
    public boolean fermeture_automatique = false;

    /** Aucun mouvement de caméra : prise et retour instantanés, avec un bref fondu. */
    public boolean reduire_animations = false;

    /** Ne jamais changer la perspective, même si le serveur le demande. */
    public boolean jamais_changer_perspective = false;

    /** Ne pas tourner le joueur vers le personnage. */
    public boolean ne_pas_orienter_joueur = false;

    /** Éléments vanilla masqués pendant une conversation (si le serveur masque le HUD). */
    public List<String> hud_masque = new ArrayList<>(List.of(
            "hotbar", "health_bar", "food_bar", "armor_bar", "air_bar", "experience_level",
            "crosshair", "status_effects", "chat", "held_item_tooltip", "mount_health", "info_bar"));

    // --- capture des dialogues de chat ---------------------------------------

    /** Les messages capturés n'apparaissent pas dans le chat (ils sont dans le dialogue). */
    public boolean capture_masquer_chat = true;

    /**
     * Après l'ouverture, durée pendant laquelle tout message de jeu fait partie
     * de la conversation, en millisecondes. Chaque message capturé la prolonge.
     */
    public int capture_fenetre_ms = 1500;

    /** Après une réponse, délai accordé au personnage pour répliquer, en millisecondes. */
    public int capture_fenetre_reponse_ms = 2500;

    /**
     * Sans réponse à choisir, la conversation se referme après ce silence (plus
     * le temps de lecture) — seulement si {@link #fermeture_automatique} est vrai.
     */
    public int capture_delai_silence_ms = 4000;

    /** Préfixes {@code [X]} qui restent dans le chat même pendant une conversation. */
    public List<String> capture_prefixes_ignores = new ArrayList<>(List.of(
            "[Quête]", "[Quete]", "[Système]", "[Systeme]", "[Serveur]", "[Info]"));

    // --- styles des réponses ------------------------------------------------

    /** La nuance d'un type de réponse : couleur {@code #RRGGBB} et préfixe optionnel ({@code "[Quête] "}). */
    public static final class Style {
        public String couleur;
        public String prefixe;

        public Style() {
        }

        public Style(String couleur, String prefixe) {
            this.couleur = couleur;
            this.prefixe = prefixe;
        }
    }

    /**
     * Par type : {@code NORMALE, QUETE, COMMERCE, METIER, TRANSPORT, DANGER, ADIEU}.
     * Sobre par défaut : la couleur seule ; un préfixe se pose ici si le serveur en veut un.
     */
    public Map<String, Style> styles_reponses = stylesParDefaut();

    /** Répliques et réponses gardées pour la touche H (« précédemment »). */
    public int historique_taille = 8;

    public static Map<String, Style> stylesParDefaut() {
        Map<String, Style> m = new LinkedHashMap<>();
        m.put("NORMALE", new Style("#E8E1CF", ""));
        m.put("QUETE", new Style("#9CD9A8", ""));
        m.put("COMMERCE", new Style("#9EC1E6", ""));
        m.put("METIER", new Style("#E3C46B", ""));
        m.put("TRANSPORT", new Style("#9ED2CD", ""));
        m.put("DANGER", new Style("#E08A7A", ""));
        m.put("ADIEU", new Style("#CFC6B2", ""));
        return m;
    }

    // --- raisons de verrou --------------------------------------------------

    /** Libellé d'un objectif de scoreboard cité dans une condition : {@code "CAP_RANG": "Rang social"}. */
    public Map<String, String> libelles_conditions = new LinkedHashMap<>();

    // --- debug --------------------------------------------------------------

    public boolean overlay_debug = false;

    // --- chargement ---------------------------------------------------------

    private static DialogueClientSettings instance = new DialogueClientSettings();

    private DialogueClientSettings() {
    }

    public static DialogueClientSettings get() {
        return instance;
    }

    private static Path file() {
        return FabricLoader.getInstance().getConfigDir().resolve(HauteCapitaleDialogue.MOD_ID + "_client.json");
    }

    public static void load() {
        Path path = file();
        Gson gson = new GsonBuilder().setPrettyPrinting().create();
        if (Files.notExists(path)) {
            write(gson, path, instance);
            return;
        }
        try (Reader reader = Files.newBufferedReader(path);
             JsonReader json = new JsonReader(reader)) {
            json.setStrictness(Strictness.LENIENT);
            DialogueClientSettings loaded = gson.fromJson(json, DialogueClientSettings.class);
            if (loaded != null) {
                instance = loaded;
            }
        } catch (Exception e) {
            HauteCapitaleDialogue.LOGGER.error("Réglages client illisibles ({}) — valeurs par défaut.", e.getMessage());
            return;
        }
        instance.borner();
        write(gson, path, instance);
    }

    public static void save() {
        instance.borner();
        write(new GsonBuilder().setPrettyPrinting().create(), file(), instance);
    }

    private static void write(Gson gson, Path path, DialogueClientSettings s) {
        try {
            Files.createDirectories(path.getParent());
            try (Writer writer = Files.newBufferedWriter(path)) {
                gson.toJson(s, writer);
            }
        } catch (IOException e) {
            HauteCapitaleDialogue.LOGGER.error("Impossible d'écrire les réglages client : {}", e.getMessage());
        }
    }

    public void borner() {
        if (this.typewriter == null) {
            this.typewriter = "easy_npc";
        }
        this.caracteres_par_seconde = (int) clamp(this.caracteres_par_seconde, 0, 400);
        this.echelle_texte = clamp(this.echelle_texte, 0.8, 1.6);
        this.opacite_fond = clamp(this.opacite_fond, 0.3, 1.0);
        if (this.hud_masque == null) {
            this.hud_masque = new ArrayList<>();
        }
        if (this.libelles_conditions == null) {
            this.libelles_conditions = new LinkedHashMap<>();
        }
        this.capture_fenetre_ms = (int) clamp(this.capture_fenetre_ms, 0, 10000);
        this.capture_fenetre_reponse_ms = (int) clamp(this.capture_fenetre_reponse_ms, 200, 20000);
        this.capture_delai_silence_ms = (int) clamp(this.capture_delai_silence_ms, 500, 60000);
        if (this.capture_prefixes_ignores == null) {
            this.capture_prefixes_ignores = new ArrayList<>();
        }
        if (this.styles_reponses == null) {
            this.styles_reponses = stylesParDefaut();
        }
        this.historique_taille = (int) clamp(this.historique_taille, 2, 40);
    }

    private static double clamp(double v, double min, double max) {
        return v < min ? min : Math.min(v, max);
    }
}
