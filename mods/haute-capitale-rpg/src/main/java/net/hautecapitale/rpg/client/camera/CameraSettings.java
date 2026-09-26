package net.hautecapitale.rpg.client.camera;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.Strictness;
import com.google.gson.stream.JsonReader;
import net.fabricmc.loader.api.FabricLoader;
import net.hautecapitale.rpg.HauteCapitaleRpg;

import java.io.IOException;
import java.io.Reader;
import java.io.Writer;
import java.nio.file.Files;
import java.nio.file.Path;

/**
 * Réglages de la caméra RPG, purement client.
 *
 * <p>Rien de ce fichier ne remonte au serveur : distance, offsets, épaule et
 * sensibilité sont des préférences d'affichage. Le serveur n'a aucune raison de
 * les connaître, et ne pas les synchroniser évite d'ajouter du trafic pour du
 * confort visuel.
 */
public final class CameraSettings {

    // --- activation ---------------------------------------------------------

    /** Module actif. À {@code false}, la caméra redevient strictement vanilla. */
    public boolean actif = true;

    // --- cycle F5 -----------------------------------------------------------

    /**
     * Intégrer les styles RPG au cycle de la touche F5.
     *
     * <p>Avec cette option, F5 parcourt : première personne vanilla → troisième
     * personne vanilla → vue de face vanilla → chaque style RPG dans l'ordre de
     * {@link #styles} → retour à la première personne. Les trois vues vanilla
     * restent <b>strictement</b> vanilla : la caméra RPG n'y touche pas.
     *
     * <p>À {@code false}, F5 redevient le cycle vanilla à trois positions et la
     * caméra RPG s'applique dès que la vue est en troisième personne.
     */
    public boolean f5_cycle = true;

    // --- cadrage ------------------------------------------------------------

    /**
     * Un style de vue : un cadrage nommé, sélectionnable au F5.
     *
     * <p>Les valeurs sont en blocs. Le décalage horizontal est pris vers l'épaule
     * choisie ({@link #epaule_droite}) ; 0 donne une caméra centrée derrière le
     * joueur, façon MMO classique.
     */
    public static final class Style {
        public String nom = "epaule";
        /** Distance caméra-joueur, hors collision. */
        public double distance = 5.5;
        /** Décalage latéral. Positif = vers l'épaule choisie. */
        public double offset_horizontal = 0.55;
        /** Décalage vertical, au-dessus de la ligne des yeux. */
        public double offset_vertical = 0.25;

        public Style() {
        }

        public Style(String nom, double distance, double offsetHorizontal, double offsetVertical) {
            this.nom = nom;
            this.distance = distance;
            this.offset_horizontal = offsetHorizontal;
            this.offset_vertical = offsetVertical;
        }
    }

    /**
     * Les styles RPG, dans l'ordre où F5 les parcourt après les vues vanilla.
     *
     * <p>Deux par défaut : « épaule », la vue par-dessus l'épaule, et « mmo »,
     * une vue centrée, plus haute et plus lointaine. Ajouter une entrée au fichier
     * ajoute une position au cycle.
     */
    public java.util.List<Style> styles = stylesParDefaut();

    /** Borne basse du zoom, commune à tous les styles. */
    public double distance_min = 1.5;

    /** Borne haute du zoom, commune à tous les styles. */
    public double distance_max = 9.0;

    /** Épaule droite (true) ou gauche (false). Bascule à la touche dédiée. */
    public boolean epaule_droite = true;

    /** Pas de zoom appliqué par pression de touche, en blocs. */
    public double pas_de_zoom = 0.5;

    // --- douceur ------------------------------------------------------------

    /**
     * Constante de temps du lissage, en millisecondes.
     *
     * <p>Le lissage est exponentiel et calculé sur le temps réel écoulé, pas sur
     * le nombre d'images : la caméra bouge à la même vitesse à 30 et à 144 ips.
     * 0 désactive le lissage.
     */
    public int lissage_ms = 130;

    /** Durée du retour en place après une cinématique, en millisecondes. */
    public int retour_cinematique_ms = 320;

    /** Lissage vertical de l'ancre, qui absorbe le à-coup de l'accroupissement. */
    public int lissage_hauteur_ms = 90;

    // --- collision ----------------------------------------------------------

    /** Empêcher la caméra de traverser les blocs. */
    public boolean collision_active = true;

    /** Marge conservée entre la caméra et la surface touchée, en blocs. */
    public double collision_marge = 0.20;

    /**
     * Durée de retour à la distance normale en sortie d'espace étroit.
     *
     * <p>Le rapprochement est immédiat — sinon la caméra passe dans le mur le
     * temps du lissage — mais l'éloignement est progressif.
     */
    public int collision_retour_ms = 260;

    // --- espaces très étroits -----------------------------------------------

    /**
     * Basculer en première personne quand la collision écrase la distance.
     *
     * <p><b>Désactivé par défaut, et ce n'est pas un oubli.</b> Cette option est
     * la seule du module qui écrit dans les options du jeu
     * ({@code setPerspective}), or Bosses'Rise s'est déjà réservé cette variable :
     * il la force à chaque tick pendant une cinématique et restaure en fin de plan
     * un instantané pris à son début. Deux écrivains sur la même variable, c'est
     * exactement le conflit à éviter. Activable en connaissance de cause ; la
     * bascule est de toute façon interdite pendant une cinématique.
     */
    public boolean passage_premiere_personne = false;

    /** Distance sous laquelle la première personne prend le relais, en blocs. */
    public double seuil_premiere_personne = 0.9;

    // --- visée --------------------------------------------------------------

    /**
     * Dessiner un réticule à l'endroit réellement visé par le joueur.
     *
     * <p>La caméra étant décalée sur l'épaule, le centre de l'écran ne désigne
     * plus la direction de tir. Le module ne corrige pas la visée — il corrigerait
     * alors le joueur lui-même, ce qui casserait Better Combat et Spell Engine —
     * il déplace seulement le réticule. Purement visuel.
     */
    public boolean reticule_projete = true;

    /** Portée du rayon de visée servant à placer le réticule, en blocs. */
    public double reticule_portee = 32.0;

    // --- debug --------------------------------------------------------------

    /** Overlay d'état, en haut à gauche. */
    public boolean overlay_debug = false;

    /**
     * Délai au-delà duquel une cinématique est considérée comme bloquée, en
     * secondes.
     *
     * <p>Bosses'Rise documente lui-même le cas : un boss qui cesse d'être dessiné
     * cesse d'être animé, et une cinématique qui cesse d'être animée n'atteint
     * jamais son keyframe de fin. Sans ce garde-fou, la caméra RPG resterait
     * suspendue jusqu'à la déconnexion. 0 désactive le garde-fou.
     */
    public int watchdog_cinematique_s = 25;

    // --- chargement ---------------------------------------------------------

    private static CameraSettings instance = new CameraSettings();

    private CameraSettings() {
    }

    public static CameraSettings get() {
        return instance;
    }

    private static Path file() {
        return FabricLoader.getInstance().getConfigDir()
                .resolve(HauteCapitaleRpg.MOD_ID + "_camera.json");
    }

    /** Charge les réglages, ou en écrit un fichier par défaut s'il manque. */
    public static void load() {
        Path path = file();
        Gson gson = new GsonBuilder().setPrettyPrinting().create();

        if (Files.notExists(path)) {
            write(gson, path, instance);
            return;
        }

        try (Reader reader = Files.newBufferedReader(path);
             JsonReader json = new JsonReader(reader)) {
            json.setStrictness(Strictness.STRICT);
            CameraSettings loaded = gson.fromJson(json, CameraSettings.class);
            if (loaded != null) {
                instance = loaded;
            }
        } catch (Exception e) {
            HauteCapitaleRpg.LOGGER.error(
                    "Reglages camera illisibles ({}) — valeurs par defaut conservees, fichier laisse intact.",
                    e.getMessage());
            return;
        }

        instance.borner();
        // Reecriture systematique : un fichier ecrit par une version anterieure ne
        // contient pas les nouveaux reglages, et personne ne peut activer une
        // option qu'il ne voit pas.
        write(gson, path, instance);
    }

    /** Réécrit le fichier après une modification en jeu (touche de zoom, épaule…). */
    public static void save() {
        instance.borner();
        write(new GsonBuilder().setPrettyPrinting().create(), file(), instance);
    }

    private static void write(Gson gson, Path path, CameraSettings settings) {
        try {
            Files.createDirectories(path.getParent());
            try (Writer writer = Files.newBufferedWriter(path)) {
                gson.toJson(settings, writer);
            }
        } catch (IOException e) {
            HauteCapitaleRpg.LOGGER.error("Impossible d'ecrire les reglages camera : {}", e.getMessage());
        }
    }

    /**
     * Ramène les valeurs dans des bornes tenables.
     *
     * <p>Un fichier édité à la main peut demander une distance de 400 blocs ou une
     * marge de collision négative ; le module doit rester utilisable plutôt que
     * d'afficher n'importe quoi.
     */
    public void borner() {
        this.distance_min = clamp(this.distance_min, 0.0, 20.0);
        this.distance_max = clamp(this.distance_max, this.distance_min, 20.0);

        // Un fichier sans style, ou avec une liste vide, ne doit pas laisser le
        // cycle F5 sans position RPG : on remet les deux styles par defaut.
        if (this.styles == null || this.styles.isEmpty()) {
            this.styles = stylesParDefaut();
        }
        this.styles.removeIf(java.util.Objects::isNull);
        if (this.styles.isEmpty()) {
            this.styles = stylesParDefaut();
        }
        int sansNom = 0;
        for (Style style : this.styles) {
            if (style.nom == null || style.nom.isBlank()) {
                style.nom = "style" + (++sansNom);
            }
            style.distance = clamp(style.distance, this.distance_min, this.distance_max);
            style.offset_horizontal = clamp(style.offset_horizontal, 0.0, 3.0);
            style.offset_vertical = clamp(style.offset_vertical, -2.0, 3.0);
        }

        this.pas_de_zoom = clamp(this.pas_de_zoom, 0.05, 4.0);
        this.lissage_ms = (int) clamp(this.lissage_ms, 0, 2000);
        this.retour_cinematique_ms = (int) clamp(this.retour_cinematique_ms, 0, 5000);
        this.lissage_hauteur_ms = (int) clamp(this.lissage_hauteur_ms, 0, 2000);
        this.collision_marge = clamp(this.collision_marge, 0.0, 1.0);
        this.collision_retour_ms = (int) clamp(this.collision_retour_ms, 0, 5000);
        this.seuil_premiere_personne = clamp(this.seuil_premiere_personne, 0.0, 4.0);
        this.reticule_portee = clamp(this.reticule_portee, 4.0, 128.0);
        this.watchdog_cinematique_s = (int) clamp(this.watchdog_cinematique_s, 0, 600);
    }

    private static double clamp(double valeur, double min, double max) {
        return valeur < min ? min : Math.min(valeur, max);
    }

    /** Les deux styles livrés : par-dessus l'épaule, et centré façon MMO. */
    static java.util.List<Style> stylesParDefaut() {
        java.util.List<Style> liste = new java.util.ArrayList<>();
        liste.add(new Style("epaule", 5.5, 0.55, 0.25));
        liste.add(new Style("mmo", 7.0, 0.0, 0.9));
        return liste;
    }
}
