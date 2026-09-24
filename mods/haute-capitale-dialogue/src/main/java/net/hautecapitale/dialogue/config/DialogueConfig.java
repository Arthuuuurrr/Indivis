package net.hautecapitale.dialogue.config;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.Strictness;
import com.google.gson.stream.JsonReader;
import net.fabricmc.loader.api.FabricLoader;
import net.hautecapitale.dialogue.HauteCapitaleDialogue;
import net.hautecapitale.dialogue.session.CameraProfile;
import net.hautecapitale.dialogue.session.SessionFlags;
import net.minecraft.entity.Entity;

import java.io.IOException;
import java.io.Reader;
import java.io.Writer;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

/**
 * Réglages serveur du dialogue, et surcharges par personnage.
 *
 * <p>Le serveur décide de tout ce qui touche au jeu — qui est éligible, à quelle
 * distance, si le joueur est verrouillé — et envoie au client le profil de
 * caméra à appliquer. Les clients n'ont donc pas à être configurés un par un :
 * un admin règle un personnage ici, et tout le monde le voit cadré de la même
 * façon.
 *
 * <p>Fichier : {@code config/haute_capitale_dialogue.json}. Réécrit à chaque
 * chargement pour que les nouveaux réglages apparaissent.
 */
public final class DialogueConfig {

    // --- éligibilité --------------------------------------------------------

    /** Un personnage sans tag ni surcharge entre-t-il en dialogue immersif ? */
    public boolean actif_par_defaut = true;

    /** Tag de scoreboard qui force le mode immersif sur une entité. */
    public String tag_actif = "hc_dialogue";

    /** Tag de scoreboard qui l'interdit — prioritaire sur tout le reste. */
    public String tag_inactif = "hc_dialogue_off";

    // --- session ------------------------------------------------------------

    /** Distance maximale joueur–personnage à l'ouverture, en blocs. */
    public double distance_ouverture = 6.0;

    /** Distance au-delà de laquelle la session se ferme, en blocs. */
    public double distance_maintien = 10.0;

    /** Saut de position entre deux vérifications considéré comme une téléportation. */
    public double saut_teleportation = 8.0;

    /** Intervalle des vérifications de session, en ticks. */
    public int verification_ticks = 5;

    /**
     * Délai, en ticks, avant d'exiger que l'écran soit réellement ouvert.
     *
     * <p>Entre l'ouverture de la session et l'arrivée de l'écran, Easy NPC fait
     * un aller-retour réseau : la session ne doit pas se fermer pendant ce
     * délai sous prétexte qu'aucun écran n'est encore là.
     */
    public int grace_ecran_ticks = 60;

    /**
     * Ticks sans aucun écran avant de conclure que le dialogue est refermé.
     *
     * <p>Entre deux écrans d'Easy NPC — un nœud qui se ferme, le commerce qui
     * s'ouvre au tick suivant — le joueur n'a rien pendant un instant.
     */
    public int fermeture_ecran_ticks = 10;

    /** Refuser une seconde conversation sur un personnage déjà occupé. */
    public boolean exclusif_par_defaut = false;

    // --- comportement du joueur ---------------------------------------------

    /** Geler déplacement et saut par modificateur d'attribut (en plus de l'écran). */
    public boolean verrou_joueur = false;

    /** Le joueur peut quitter la conversation avec Échap. */
    public boolean autoriser_esc = true;

    /** Masquer barre d'action, vie, faim, réticule, chat pendant la conversation. */
    public boolean masquer_hud = true;

    /** Passer en troisième personne le temps de la conversation si le joueur est en première. */
    public boolean forcer_troisieme_personne = true;

    /** Rouvrir la conversation à la fin d'une cinématique qui l'a interrompue. */
    public boolean reprise_apres_cinematique = false;

    /** Délai, en ticks, au-delà duquel une conversation interrompue par une cinématique n'est plus reprise. */
    public int reprise_delai_max_ticks = 6000;

    /**
     * Un commerce ou un atelier ouvert depuis une réponse rend au dialogue quand
     * il se ferme, au nœud d'où il a été ouvert. Sinon la conversation finit là.
     */
    public boolean retour_apres_commerce = true;

    /** Le personnage tourne la tête vers le joueur (objectif temporaire Easy NPC). */
    public boolean regard_pnj = true;

    /**
     * Panneau seul : le texte et les réponses, sans déplacement de caméra ni
     * changement de perspective — pour un serveur qui ne veut que l'interface,
     * ou un personnage donné (surcharge {@code panneau_seul}).
     */
    public boolean panneau_seul = false;

    /**
     * Son joué au joueur à chaque nouvelle réplique d'un dialogue Easy NPC : le
     * son de ce type dans les sons du personnage ({@code AMBIENT} = son
     * d'ambiance, « hmm »), ou {@code none}. Les dialogues de datapack jouent
     * déjà les leurs.
     */
    public String son_replique = "AMBIENT";

    /** Journaliser l'ouverture et la fermeture de chaque session. */
    public boolean journaliser = true;

    // --- véhicules ------------------------------------------------------------

    /**
     * Un personnage porté par un véhicule en mouvement (charrette, ferry,
     * dirigeable) : {@code "simplifie"} = texte seul, la caméra reste au joueur ;
     * {@code "refuser"} = pas de conversation en route ; {@code "complet"} = comme
     * à l'arrêt (déconseillé : la caméra suit un point qui bouge).
     */
    public String vehicule_en_mouvement = "simplifie";

    /** Vitesse, en blocs par tick, au-delà de laquelle le véhicule est « en mouvement ». */
    public double vehicule_vitesse_mouvement = 0.03;

    /** Immobilité requise, en ticks, avant de considérer le véhicule à l'arrêt. */
    public int vehicule_ticks_arret = 40;

    // --- capture des dialogues de chat ---------------------------------------

    /**
     * Présenter dans l'écran de dialogue les conversations qui passent par le
     * chat ({@code tellraw} des fonctions de datapack, panneaux des transports).
     */
    public boolean capture_active = true;

    /**
     * Fermer d'autorité une conversation capturée qui dure.
     *
     * <p>Faux — le réglage livré : l'écran du joueur reste le sien, il le ferme
     * quand il a fini de lire. La session est de toute façon close dès qu'il
     * s'éloigne, meurt, change de monde ou se téléporte. Vrai : le serveur
     * referme au bout de {@link #capture_inactivite_max_ticks}, ce qui ramasse
     * une session laissée par un client sans le mod.
     */
    public boolean capture_fermeture_inactivite = false;

    /** Une conversation capturée sans activité se ferme après ce délai, en ticks — si le réglage ci-dessus l'autorise. */
    public int capture_inactivite_max_ticks = 6000;

    // --- caméra -------------------------------------------------------------

    public Camera camera = new Camera();

    /**
     * Profil de caméra par défaut : un plan d'épaule ancré sur le joueur.
     * Distances en blocs, angles en degrés. Les valeurs « proche » s'appliquent
     * quand le personnage est à moins de {@code distance_proche}, les valeurs
     * « loin » au-delà de {@code distance_loin}, et l'on glisse entre les deux.
     */
    public static final class Camera {
        /** Recul de la caméra derrière les yeux du joueur. */
        public double recul_proche = 1.5;
        public double recul_loin = 1.75;
        /** Décalage vers l'épaule. Plus il est grand, plus le joueur laisse voir le personnage. */
        public double decalage_proche = 1.35;
        public double decalage_loin = 0.8;
        /** Écart du personnage par rapport au centre de l'image, vers le côté opposé à l'épaule. */
        public double angle_visee_proche = 10.0;
        public double angle_visee_loin = 6.0;
        /** Bornes de distance joueur–personnage entre lesquelles les valeurs glissent. */
        public double distance_proche = 1.5;
        public double distance_loin = 5.0;
        /** Hauteur de la caméra au-dessus des yeux du joueur. */
        public double elevation = 0.15;
        /** Point visé, en fraction de la hauteur du personnage (0.85 = visage/torse). */
        public double hauteur_cible = 0.85;
        /** Durée du fondu d'entrée et de sortie, en millisecondes. */
        public int transition_ms = 350;
    }

    // --- surcharges par personnage -------------------------------------------

    /** Clé : UUID du personnage. Toute valeur absente hérite du défaut. */
    public Map<String, Pnj> pnj = new LinkedHashMap<>();

    public static final class Pnj {
        public Boolean actif;
        public Boolean exclusif;
        public Boolean verrou_joueur;
        public Boolean autoriser_esc;
        public Boolean masquer_hud;
        public Boolean forcer_troisieme_personne;
        public Boolean reprise_apres_cinematique;
        public Boolean retour_apres_commerce;
        /** Capturer les dialogues de chat de ce personnage (défaut : comme {@code actif}). */
        public Boolean capture;
        /** Texte seul, sans caméra, pour ce personnage. */
        public Boolean panneau_seul;
        /** Recul fixe derrière le joueur, en blocs, quelle que soit la distance. */
        public Double recul;
        /** Décalage d'épaule fixe, en blocs. */
        public Double decalage;
        /** Écart fixe du personnage par rapport au centre, en degrés. */
        public Double angle_visee;
        public Double hauteur_cible;
        /** Décalage vertical du point visé, en blocs (personnage assis, modèle décalé). */
        public Double decalage_cible_y;
        public Double elevation;
        public Integer transition_ms;
    }

    // --- chargement ---------------------------------------------------------

    private static DialogueConfig instance = new DialogueConfig();

    public DialogueConfig() {
    }

    public static DialogueConfig get() {
        return instance;
    }

    private static Path file() {
        return FabricLoader.getInstance().getConfigDir().resolve(HauteCapitaleDialogue.MOD_ID + ".json");
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
            DialogueConfig loaded = gson.fromJson(json, DialogueConfig.class);
            if (loaded != null) {
                instance = loaded;
            }
        } catch (Exception e) {
            HauteCapitaleDialogue.LOGGER.error(
                    "Configuration illisible ({}) — valeurs par défaut conservées, fichier laissé intact.",
                    e.getMessage());
            return;
        }

        instance.borner();
        write(gson, path, instance);
    }

    public static void save() {
        instance.borner();
        write(new GsonBuilder().setPrettyPrinting().create(), file(), instance);
    }

    private static void write(Gson gson, Path path, DialogueConfig config) {
        try {
            Files.createDirectories(path.getParent());
            try (Writer writer = Files.newBufferedWriter(path)) {
                gson.toJson(config, writer);
            }
        } catch (IOException e) {
            HauteCapitaleDialogue.LOGGER.error("Impossible d'écrire la configuration : {}", e.getMessage());
        }
    }

    /** Ramène les valeurs dans des bornes tenables, quoi qu'on ait tapé dans le fichier. */
    public void borner() {
        if (this.camera == null) {
            this.camera = new Camera();
        }
        if (this.pnj == null) {
            this.pnj = new LinkedHashMap<>();
        }
        if (this.tag_actif == null || this.tag_actif.isBlank()) {
            this.tag_actif = "hc_dialogue";
        }
        if (this.tag_inactif == null || this.tag_inactif.isBlank()) {
            this.tag_inactif = "hc_dialogue_off";
        }
        this.distance_ouverture = clamp(this.distance_ouverture, 1.0, 32.0);
        this.distance_maintien = clamp(this.distance_maintien, this.distance_ouverture, 64.0);
        this.saut_teleportation = clamp(this.saut_teleportation, 2.0, 64.0);
        this.verification_ticks = (int) clamp(this.verification_ticks, 1, 40);
        this.grace_ecran_ticks = (int) clamp(this.grace_ecran_ticks, 20, 400);
        this.fermeture_ecran_ticks = (int) clamp(this.fermeture_ecran_ticks, 0, 200);
        this.capture_inactivite_max_ticks = (int) clamp(this.capture_inactivite_max_ticks, 200, 72000);
        if (this.vehicule_en_mouvement == null || politiqueVehicule(this.vehicule_en_mouvement) == null) {
            this.vehicule_en_mouvement = "simplifie";
        }
        this.vehicule_vitesse_mouvement = clamp(this.vehicule_vitesse_mouvement, 0.001, 5.0);
        this.vehicule_ticks_arret = (int) clamp(this.vehicule_ticks_arret, 0, 1200);
        this.reprise_delai_max_ticks = (int) clamp(this.reprise_delai_max_ticks, 20, 72000);
        if (this.son_replique == null || this.son_replique.isBlank()) {
            this.son_replique = "none";
        }
        this.camera.recul_proche = clamp(this.camera.recul_proche, 0.5, 6.0);
        this.camera.recul_loin = clamp(this.camera.recul_loin, 0.5, 6.0);
        this.camera.decalage_proche = clamp(this.camera.decalage_proche, 0.0, 4.0);
        this.camera.decalage_loin = clamp(this.camera.decalage_loin, 0.0, 4.0);
        this.camera.angle_visee_proche = clamp(this.camera.angle_visee_proche, -45.0, 45.0);
        this.camera.angle_visee_loin = clamp(this.camera.angle_visee_loin, -45.0, 45.0);
        this.camera.distance_proche = clamp(this.camera.distance_proche, 0.5, 16.0);
        this.camera.distance_loin = clamp(this.camera.distance_loin, this.camera.distance_proche, 16.0);
        this.camera.hauteur_cible = clamp(this.camera.hauteur_cible, 0.0, 1.5);
        this.camera.elevation = clamp(this.camera.elevation, -2.0, 3.0);
        this.camera.transition_ms = (int) clamp(this.camera.transition_ms, 0, 3000);
    }

    // --- véhicules : la politique ----------------------------------------------

    public enum PolitiqueVehicule { SIMPLIFIE, REFUSER, COMPLET }

    public PolitiqueVehicule politiqueVehicule() {
        PolitiqueVehicule p = politiqueVehicule(this.vehicule_en_mouvement);
        return p == null ? PolitiqueVehicule.SIMPLIFIE : p;
    }

    /** La règle pure : {@code null} si le texte n'est pas une politique connue. */
    public static PolitiqueVehicule politiqueVehicule(String texte) {
        if (texte == null) {
            return null;
        }
        return switch (texte.trim().toLowerCase(java.util.Locale.ROOT)) {
            case "simplifie", "simplifié" -> PolitiqueVehicule.SIMPLIFIE;
            case "refuser" -> PolitiqueVehicule.REFUSER;
            case "complet" -> PolitiqueVehicule.COMPLET;
            default -> null;
        };
    }

    // --- lecture par personnage ------------------------------------------------

    private Pnj surcharge(UUID uuid) {
        return uuid == null ? null : this.pnj.get(uuid.toString());
    }

    /** La surcharge d'un personnage, créée si besoin — pour {@code /dialogue pnj set}. */
    public Pnj surchargeOuCreer(UUID uuid) {
        return this.pnj.computeIfAbsent(uuid.toString(), k -> new Pnj());
    }

    public Pnj surchargeDe(UUID uuid) {
        return surcharge(uuid);
    }

    public boolean oublier(UUID uuid) {
        return uuid != null && this.pnj.remove(uuid.toString()) != null;
    }

    /**
     * Pose une clé de surcharge depuis un texte : {@code /dialogue pnj set exclusif true}.
     *
     * @return un message d'erreur, ou {@code null} si la clé a été posée
     */
    public static String poser(Pnj p, String cle, String valeur) {
        String v = valeur == null ? "" : valeur.trim();
        boolean effacer = v.isEmpty() || v.equalsIgnoreCase("defaut") || v.equalsIgnoreCase("défaut") || v.equals("-");
        try {
            switch (cle.toLowerCase(java.util.Locale.ROOT)) {
                case "actif" -> p.actif = effacer ? null : booleen(v);
                case "exclusif" -> p.exclusif = effacer ? null : booleen(v);
                case "verrou_joueur" -> p.verrou_joueur = effacer ? null : booleen(v);
                case "autoriser_esc" -> p.autoriser_esc = effacer ? null : booleen(v);
                case "masquer_hud" -> p.masquer_hud = effacer ? null : booleen(v);
                case "forcer_troisieme_personne" -> p.forcer_troisieme_personne = effacer ? null : booleen(v);
                case "reprise_apres_cinematique" -> p.reprise_apres_cinematique = effacer ? null : booleen(v);
                case "retour_apres_commerce" -> p.retour_apres_commerce = effacer ? null : booleen(v);
                case "capture" -> p.capture = effacer ? null : booleen(v);
                case "panneau_seul" -> p.panneau_seul = effacer ? null : booleen(v);
                case "recul" -> p.recul = effacer ? null : clamp(Double.parseDouble(v), 0.5, 6.0);
                case "decalage" -> p.decalage = effacer ? null : clamp(Double.parseDouble(v), 0.0, 4.0);
                case "angle_visee" -> p.angle_visee = effacer ? null : clamp(Double.parseDouble(v), -45.0, 45.0);
                case "hauteur_cible" -> p.hauteur_cible = effacer ? null : clamp(Double.parseDouble(v), 0.0, 1.5);
                case "decalage_cible_y" -> p.decalage_cible_y = effacer ? null : clamp(Double.parseDouble(v), -3.0, 3.0);
                case "elevation" -> p.elevation = effacer ? null : clamp(Double.parseDouble(v), -2.0, 3.0);
                case "transition_ms" -> p.transition_ms = effacer ? null : (int) clamp(Integer.parseInt(v), 0, 3000);
                default -> {
                    return "clé inconnue « " + cle + "» ; clés : " + CLES;
                }
            }
        } catch (NumberFormatException e) {
            return "valeur numérique attendue pour « " + cle + " » : " + v;
        } catch (IllegalArgumentException e) {
            return e.getMessage();
        }
        return null;
    }

    public static final String CLES = "actif, exclusif, verrou_joueur, autoriser_esc, masquer_hud, forcer_troisieme_personne, "
            + "reprise_apres_cinematique, retour_apres_commerce, capture, panneau_seul, recul, decalage, angle_visee, "
            + "hauteur_cible, decalage_cible_y, elevation, transition_ms";

    private static Boolean booleen(String v) {
        return switch (v.toLowerCase(java.util.Locale.ROOT)) {
            case "true", "on", "oui", "vrai", "1" -> Boolean.TRUE;
            case "false", "off", "non", "faux", "0" -> Boolean.FALSE;
            default -> throw new IllegalArgumentException("booléen attendu (on/off) : " + v);
        };
    }

    /** La surcharge en clair, pour {@code /dialogue pnj voir}. */
    public static String decrire(Pnj p) {
        if (p == null) {
            return "aucune surcharge";
        }
        StringBuilder sb = new StringBuilder();
        ajouter(sb, "actif", p.actif);
        ajouter(sb, "exclusif", p.exclusif);
        ajouter(sb, "verrou_joueur", p.verrou_joueur);
        ajouter(sb, "autoriser_esc", p.autoriser_esc);
        ajouter(sb, "masquer_hud", p.masquer_hud);
        ajouter(sb, "forcer_troisieme_personne", p.forcer_troisieme_personne);
        ajouter(sb, "reprise_apres_cinematique", p.reprise_apres_cinematique);
        ajouter(sb, "retour_apres_commerce", p.retour_apres_commerce);
        ajouter(sb, "capture", p.capture);
        ajouter(sb, "panneau_seul", p.panneau_seul);
        ajouter(sb, "recul", p.recul);
        ajouter(sb, "decalage", p.decalage);
        ajouter(sb, "angle_visee", p.angle_visee);
        ajouter(sb, "hauteur_cible", p.hauteur_cible);
        ajouter(sb, "decalage_cible_y", p.decalage_cible_y);
        ajouter(sb, "elevation", p.elevation);
        ajouter(sb, "transition_ms", p.transition_ms);
        return sb.length() == 0 ? "surcharge vide" : sb.toString();
    }

    private static void ajouter(StringBuilder sb, String cle, Object valeur) {
        if (valeur != null) {
            if (sb.length() > 0) {
                sb.append(", ");
            }
            sb.append(cle).append('=').append(valeur);
        }
    }

    /**
     * Le personnage est-il présenté en dialogue immersif ?
     *
     * <p>Ordre de décision : tag d'interdiction, tag d'activation, surcharge par
     * UUID, défaut global. Les tags passent en premier parce qu'un admin les
     * pose en jeu, sans redémarrage, et doit voir l'effet tout de suite.
     */
    public boolean actif(Entity entity) {
        Pnj p = surcharge(entity.getUuid());
        return decider(entity.getCommandTags(), p == null ? null : p.actif, this.actif_par_defaut,
                this.tag_actif, this.tag_inactif);
    }

    /**
     * Les dialogues de chat de ce personnage sont-ils capturés ?
     *
     * <p>Pour un personnage dont le clic lance des commandes : oui dès qu'il est
     * éligible au dialogue immersif, sauf surcharge {@code capture}.
     */
    public boolean capture(Entity entity) {
        if (!this.capture_active) {
            return false;
        }
        Pnj p = surcharge(entity.getUuid());
        return decider(entity.getCommandTags(), p == null ? null : p.capture, actif(entity),
                this.tag_actif, this.tag_inactif);
    }

    /**
     * Capture sur demande explicite seulement : tag d'activation ou surcharge
     * {@code capture}, jamais par défaut. Pour les personnages sans action
     * connue et les entités qui ne sont pas des Easy NPC.
     */
    public boolean captureExplicite(Entity entity) {
        if (!this.capture_active) {
            return false;
        }
        Pnj p = surcharge(entity.getUuid());
        return decider(entity.getCommandTags(), p == null ? null : p.capture, false,
                this.tag_actif, this.tag_inactif);
    }

        /** La règle pure, testable sans entité. */
    public static boolean decider(Set<String> tags, Boolean surcharge, boolean defaut,
                                  String tagActif, String tagInactif) {
        if (tags != null) {
            if (tags.contains(tagInactif)) {
                return false;
            }
            if (tags.contains(tagActif)) {
                return true;
            }
        }
        if (surcharge != null) {
            return surcharge;
        }
        return defaut;
    }

    public CameraProfile profil(Entity entity) {
        Pnj p = surcharge(entity == null ? null : entity.getUuid());
        Camera c = this.camera;
        // Une valeur fixe par personnage s'applique aux deux bornes : plus de glissement.
        double reculProche = p != null && p.recul != null ? p.recul : c.recul_proche;
        double reculLoin = p != null && p.recul != null ? p.recul : c.recul_loin;
        double decalageProche = p != null && p.decalage != null ? p.decalage : c.decalage_proche;
        double decalageLoin = p != null && p.decalage != null ? p.decalage : c.decalage_loin;
        double viseeProche = p != null && p.angle_visee != null ? p.angle_visee : c.angle_visee_proche;
        double viseeLoin = p != null && p.angle_visee != null ? p.angle_visee : c.angle_visee_loin;
        return new CameraProfile(
                (float) reculProche, (float) reculLoin,
                (float) decalageProche, (float) decalageLoin,
                (float) viseeProche, (float) viseeLoin,
                (float) c.distance_proche, (float) c.distance_loin,
                (float) (p != null && p.elevation != null ? p.elevation : c.elevation),
                (float) (p != null && p.hauteur_cible != null ? p.hauteur_cible : c.hauteur_cible),
                (float) (p != null && p.decalage_cible_y != null ? p.decalage_cible_y : 0.0),
                p != null && p.transition_ms != null ? p.transition_ms : c.transition_ms);
    }

    public int flags(Entity entity) {
        Pnj p = surcharge(entity == null ? null : entity.getUuid());
        int flags = 0;
        if (valeur(p == null ? null : p.verrou_joueur, this.verrou_joueur)) {
            flags |= SessionFlags.VERROU_JOUEUR;
        }
        if (valeur(p == null ? null : p.autoriser_esc, this.autoriser_esc)) {
            flags |= SessionFlags.AUTORISER_ESC;
        }
        if (valeur(p == null ? null : p.masquer_hud, this.masquer_hud)) {
            flags |= SessionFlags.MASQUER_HUD;
        }
        if (valeur(p == null ? null : p.forcer_troisieme_personne, this.forcer_troisieme_personne)) {
            flags |= SessionFlags.FORCER_TROISIEME_PERSONNE;
        }
        if (valeur(p == null ? null : p.reprise_apres_cinematique, this.reprise_apres_cinematique)) {
            flags |= SessionFlags.REPRISE_APRES_CINEMATIQUE;
        }
        if (valeur(p == null ? null : p.retour_apres_commerce, this.retour_apres_commerce)) {
            flags |= SessionFlags.RETOUR_APRES_COMMERCE;
        }
        if (valeur(p == null ? null : p.panneau_seul, this.panneau_seul)) {
            flags |= SessionFlags.SIMPLIFIE;
        }
        if (valeur(p == null ? null : p.exclusif, this.exclusif_par_defaut)) {
            flags |= SessionFlags.EXCLUSIF;
        }
        return flags;
    }

    private static boolean valeur(Boolean surcharge, boolean defaut) {
        return surcharge != null ? surcharge : defaut;
    }

    private static double clamp(double valeur, double min, double max) {
        return valeur < min ? min : Math.min(valeur, max);
    }
}
