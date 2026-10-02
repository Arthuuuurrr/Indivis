package net.hautecapitale.metiers.config;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonSyntaxException;
import net.fabricmc.loader.api.FabricLoader;
import net.hautecapitale.metiers.HauteCapitaleMetiers;
import net.hautecapitale.metiers.creature.CreatureEnums.SpawnOrigin;
import net.minecraft.util.Identifier;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

/**
 * Source de vérité unique pour les valeurs numériques de progression.
 *
 * <p>Aucune constante d'équilibrage ne doit vivre dans le code Java : tout ce
 * qui se règle passe par ce fichier, rechargeable sans recompiler.
 */
public final class MetiersConfig {

    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();
    private static final String FILE_NAME = HauteCapitaleMetiers.MOD_ID + ".json";

    private static MetiersConfig instance = new MetiersConfig();

    /** Niveau maximum atteignable, tous métiers confondus. */
    public int niveau_max = 50;

    /** Nombre de métiers qu'un joueur peut apprendre. 0 ou moins = illimité. */
    public int metiers_max_par_joueur = 0;

    /** XP requise pour passer du niveau N au N+1 : base × N^exposant. */
    public double xp_base = 75.0D;
    public double xp_exposant = 1.1D;

    /**
     * Décote d'XP selon l'écart {@code niveau du joueur − niveau de la recette}.
     * Chaque palier dit : « jusqu'à cet écart, ce pourcentage ». Au-delà du
     * dernier palier, 0 %.
     */
    public List<Palier> decote = List.of(
            new Palier(5, 100),
            new Palier(10, 60),
            new Palier(15, 25),
            new Palier(20, 5));

    /** Nombre de fabrications à partir duquel une recette est « en apprentissage ». */
    public int maitrise_apprentissage = 5;
    /** Nombre de fabrications à partir duquel une recette est maîtrisée. */
    public int maitrise_acquise = 15;

    /** L'objet qui sert de monnaie pour le coût des recettes. */
    public String monnaie = "capitale_currency:martin_dor";

    /**
     * Les joueurs en survie ne cassent aucun bloc. Les nodes, eux, ne se cassent
     * jamais, quel que soit ce réglage. Le mode créatif n'est pas concerné.
     */
    public boolean proteger_les_blocs = true;

    /** Toutes les combien de secondes on regarde si des nodes récoltés ont repoussé. */
    public int nodes_balayage_secondes = 5;

    /**
     * Origine attribuée à une créature adulte que le mod rencontre sans marque —
     * la faune d'avant son installation, ou un mob créé par un mod sans passer
     * par l'initialisation de Minecraft. « naturelle » pour ne pas priver les
     * chasseurs d'un monde déjà peuplé ; « inconnue » pour être strict.
     */
    public String origine_par_defaut = "naturelle";

    /** Distance maximale, en blocs, entre le joueur et la carcasse qu'il dépèce. */
    public double depecage_portee = 3.0D;

    /**
     * Famille d'outils exigée en main pour dépecer, quand la fiche n'en précise
     * pas. Vide = aucun outil requis.
     */
    public String depecage_outil = "haute_capitale_metiers:couteaux";

    /** Au-delà de ce nombre de carcasses dans un rayon de 16 blocs, la plus ancienne disparaît. */
    public int carcasses_max_par_zone = 8;


    /**
     * La réparation universelle : prix = durabilité manquante × coefficient de
     * rareté, × {@code brise} si l'objet est brisé, jamais moins que
     * {@code minimum}. Les objets de {@code exceptions} ne se brisent pas : leur
     * mod gère lui-même leur destruction.
     */
    public Reparation reparation = new Reparation();

    /** Le barème de réparation, en Martins par point de durabilité. */
    public static final class Reparation {
        public double commun = 0.05D;
        public double peu_commun = 0.08D;
        public double rare = 0.12D;
        public double epique = 0.20D;
        public double legendaire = 0.35D;
        public double brise = 2.5D;
        public int minimum = 1;
        public List<String> exceptions = List.of();

        void sanitize() {
            commun = coefficient(commun, 0.05D);
            peu_commun = coefficient(peu_commun, 0.08D);
            rare = coefficient(rare, 0.12D);
            epique = coefficient(epique, 0.20D);
            legendaire = coefficient(legendaire, 0.35D);
            if (!(brise >= 1.0D) || !Double.isFinite(brise)) {
                brise = 2.5D;
            }
            if (minimum < 0) {
                minimum = 0;
            }
            if (exceptions == null) {
                exceptions = List.of();
            }
            List<String> kept = new ArrayList<>();
            for (String id : exceptions) {
                if (id != null && Identifier.tryParse(id) != null) {
                    kept.add(id);
                }
            }
            exceptions = List.copyOf(kept);
        }

        private static double coefficient(double value, double fallback) {
            return value >= 0.0D && Double.isFinite(value) ? value : fallback;
        }
    }

    /** Les réglages des gadgets de l'Ingénieur — rayons, durées, cadences. */
    public Gadgets gadgets = new Gadgets();

    /** La Pierre de foyer : canalisation, temps de recharge, portée. */
    public Foyer foyer = new Foyer();

    /** La qualité Excellent : la chance qu'un artisan très au-dessus du niveau la produise. */
    public Qualite qualite = new Qualite();

    /** Les buffs de repas du Cuisinier. */
    public Repas repas = new Repas();

    public static final class Gadgets {
        /** Aimant à butin : rayon d'attraction, en blocs. */
        public double aimant_rayon = 6.0D;
        /** Compresseur de poche et broyeur : toutes les combien de ticks. */
        public int compresseur_ticks = 40;
        /** Détecteur de minerai et lunettes de prospection : rayon de balayage. */
        public int scanner_rayon = 12;
        /** Chercheur de géode : rayon de recherche d'améthyste bourgeonnante. */
        public int geode_rayon = 48;
        /** Sonde à écho : rayon de recherche du Deep Dark et des cités antiques. */
        public int echo_rayon = 96;
        /** Résonateur de cristal : rayon des minerais précieux qui le font vibrer. */
        public int resonateur_rayon = 10;
        /** Lunettes de trésor : rayon des coffres signalés. */
        public int tresor_rayon = 12;
        /** Grappin : portée maximale, en blocs. */
        public double grappin_portee = 24.0D;
        /** Lumière en dessous de laquelle casque de mineur et lanterne s'allument. */
        public int obscurite = 5;
        /** Kit de raffinage : durée d'activité, en secondes. */
        public int raffinage_secondes = 60;
        /** Kit de campement : durée des soins, en secondes ; recharge, en secondes. */
        public int campement_secondes = 30;
        public int campement_recharge_secondes = 300;
        /** Kit de secours : recharge, en secondes. */
        public int secours_recharge_secondes = 120;
        /** Balise de rappel : canalisation et recharge, en secondes ; portée en blocs. */
        public int rappel_canalisation_secondes = 4;
        public int rappel_recharge_secondes = 180;
        public double rappel_portee = 1500.0D;
        /** Corde d'évasion : recharge, en secondes. */
        public int evasion_recharge_secondes = 60;
        /** Repoussant (sel, onguent, baume) : durée en secondes et rayon en blocs, par palier. */
        public int sel_secondes = 300;
        public int sel_rayon = 8;
        public int onguent_secondes = 900;
        public int onguent_rayon = 16;
        public int baume_secondes = 3600;
        public int baume_rayon = 24;
        /** Drone minier : distance au-delà de laquelle il perd le signal, rayon de ramassage. */
        public double drone_portee = 24.0D;
        public double drone_ramassage = 3.0D;
        /** Exosquelette : bonus de set. */
        public boolean exosquelette_bonus = true;

        void sanitize() {
            aimant_rayon = clamp(aimant_rayon, 1.0D, 32.0D, 6.0D);
            compresseur_ticks = Math.max(1, compresseur_ticks);
            scanner_rayon = (int) clamp(scanner_rayon, 2, 32, 12);
            geode_rayon = (int) clamp(geode_rayon, 8, 128, 48);
            echo_rayon = (int) clamp(echo_rayon, 16, 256, 96);
            resonateur_rayon = (int) clamp(resonateur_rayon, 2, 32, 10);
            tresor_rayon = (int) clamp(tresor_rayon, 2, 32, 12);
            grappin_portee = clamp(grappin_portee, 4.0D, 64.0D, 24.0D);
            obscurite = (int) clamp(obscurite, 0, 15, 5);
            raffinage_secondes = Math.max(1, raffinage_secondes);
            campement_secondes = Math.max(1, campement_secondes);
            campement_recharge_secondes = Math.max(0, campement_recharge_secondes);
            secours_recharge_secondes = Math.max(0, secours_recharge_secondes);
            rappel_canalisation_secondes = Math.max(0, rappel_canalisation_secondes);
            rappel_recharge_secondes = Math.max(0, rappel_recharge_secondes);
            rappel_portee = Math.max(1.0D, rappel_portee);
            evasion_recharge_secondes = Math.max(0, evasion_recharge_secondes);
            sel_secondes = Math.max(1, sel_secondes);
            onguent_secondes = Math.max(1, onguent_secondes);
            baume_secondes = Math.max(1, baume_secondes);
            sel_rayon = (int) clamp(sel_rayon, 1, 64, 8);
            onguent_rayon = (int) clamp(onguent_rayon, 1, 64, 16);
            baume_rayon = (int) clamp(baume_rayon, 1, 64, 24);
            drone_portee = clamp(drone_portee, 4.0D, 128.0D, 24.0D);
            drone_ramassage = clamp(drone_ramassage, 1.0D, 16.0D, 3.0D);
        }
    }

    public static final class Foyer {
        /** Secondes à rester immobile avant le départ. */
        public int canalisation_secondes = 5;
        /** Recharge après usage, en secondes — pierre ordinaire, puis améliorée. */
        public int recharge_secondes = 900;
        public int amelioree_recharge_secondes = 300;
        /** Au-delà de cette distance, la pierre est « trop faible ». 0 = illimitée. */
        public double portee = 4000.0D;
        /** Une pierre améliorée porte plus loin, d'autant. */
        public double amelioree_portee_facteur = 2.0D;
        /** Changer de dimension est-il permis ? */
        public boolean autre_dimension = false;
        /** Combien d'auberges un joueur peut retenir. 0 = sans limite ; au-delà, la plus ancienne est oubliée. */
        public int auberges_max = 0;
        /** L'aubergiste donne-t-il une pierre de foyer au joueur qui n'en a pas ? */
        public boolean pierre_offerte = true;

        void sanitize() {
            canalisation_secondes = Math.max(0, canalisation_secondes);
            recharge_secondes = Math.max(0, recharge_secondes);
            amelioree_recharge_secondes = Math.max(0, amelioree_recharge_secondes);
            portee = Math.max(0.0D, Double.isFinite(portee) ? portee : 4000.0D);
            amelioree_portee_facteur = clamp(amelioree_portee_facteur, 1.0D, 100.0D, 2.0D);
        }
    }

    public static final class Qualite {
        /** Écart de niveau (joueur − recette) à partir duquel l'Excellent devient possible. */
        public int ecart_minimum = 3;
        /** Chance à l'écart minimum, puis par niveau d'écart supplémentaire, puis plafond. */
        public double chance_base = 0.05D;
        public double chance_par_niveau = 0.015D;
        public double chance_max = 0.35D;

        void sanitize() {
            ecart_minimum = Math.max(0, ecart_minimum);
            chance_base = clamp(chance_base, 0.0D, 1.0D, 0.05D);
            chance_par_niveau = clamp(chance_par_niveau, 0.0D, 1.0D, 0.015D);
            chance_max = clamp(chance_max, 0.0D, 1.0D, 0.35D);
        }

        /** La chance d'un objet Excellent pour cet écart de niveau, entre 0 et 1. */
        public double chance(int playerLevel, int recipeLevel) {
            int gap = playerLevel - recipeLevel;
            if (gap < ecart_minimum) {
                return 0.0D;
            }
            return Math.min(chance_max, chance_base + (gap - ecart_minimum) * chance_par_niveau);
        }
    }

    public static final class Repas {
        /** Un repas Excellent renforce son buff d'autant : 4/3 = un tiers de plus. */
        public double excellent_facteur = 4.0D / 3.0D;
        /** Durée par défaut d'un buff, en secondes, quand la fiche n'en donne pas. */
        public int duree_secondes = 900;

        void sanitize() {
            excellent_facteur = clamp(excellent_facteur, 1.0D, 10.0D, 4.0D / 3.0D);
            duree_secondes = Math.max(1, duree_secondes);
        }
    }

    private static double clamp(double value, double min, double max, double fallback) {
        if (!Double.isFinite(value)) {
            return fallback;
        }
        return Math.clamp(value, min, max);
    }
    /** Un palier de décote. */
    public static final class Palier {
        public int ecart;
        public int pourcent;

        public Palier() {
        }

        public Palier(int ecart, int pourcent) {
            this.ecart = ecart;
            this.pourcent = pourcent;
        }
    }

    private MetiersConfig() {
    }

    public static MetiersConfig get() {
        return instance;
    }

    private static Path path() {
        return FabricLoader.getInstance().getConfigDir().resolve(FILE_NAME);
    }

    /**
     * Charge la configuration, ou écrit le fichier par défaut s'il est absent.
     * Une configuration illisible ne doit jamais empêcher le serveur de démarrer :
     * on journalise et on repart sur les valeurs par défaut.
     */
    public static void load() {
        Path file = path();
        if (!Files.exists(file)) {
            instance = new MetiersConfig();
            save();
            HauteCapitaleMetiers.LOGGER.info("Configuration créée : {}", file);
            return;
        }
        try {
            String json = Files.readString(file, StandardCharsets.UTF_8);
            MetiersConfig loaded = GSON.fromJson(json, MetiersConfig.class);
            if (loaded == null) {
                throw new JsonSyntaxException("fichier vide");
            }
            loaded.sanitize();
            instance = loaded;
            // Réécrit le fichier : les réglages apparus depuis sa création y
            // figurent ainsi avec leur valeur par défaut, visibles et modifiables.
            save();
            HauteCapitaleMetiers.LOGGER.info("Configuration chargée : {}", file);
        } catch (IOException | JsonSyntaxException e) {
            HauteCapitaleMetiers.LOGGER.error(
                    "Configuration illisible ({}), valeurs par défaut appliquées : {}", file, e.getMessage());
            instance = new MetiersConfig();
        }
    }

    /**
     * Lit une configuration depuis un texte JSON et la borne, sans la charger.
     * Pour les tests : {@code null} si le texte est illisible.
     */
    public static MetiersConfig fromJson(String json) {
        try {
            MetiersConfig parsed = GSON.fromJson(json, MetiersConfig.class);
            if (parsed != null) {
                parsed.sanitize();
            }
            return parsed;
        } catch (JsonSyntaxException e) {
            return null;
        }
    }

    public static void save() {
        Path file = path();
        try {
            Files.createDirectories(file.getParent());
            Files.writeString(file, GSON.toJson(instance), StandardCharsets.UTF_8);
        } catch (IOException e) {
            HauteCapitaleMetiers.LOGGER.error("Impossible d'écrire la configuration {} : {}", file, e.getMessage());
        }
    }

    /** Ramène les valeurs aberrantes dans un domaine utilisable. */
    private void sanitize() {
        if (niveau_max < 1) {
            niveau_max = 1;
        }
        if (niveau_max > 1000) {
            niveau_max = 1000;
        }
        if (xp_base <= 0.0D || !Double.isFinite(xp_base)) {
            xp_base = 75.0D;
        }
        if (xp_exposant <= 0.0D || !Double.isFinite(xp_exposant)) {
            xp_exposant = 1.1D;
        }
        if (metiers_max_par_joueur < 0) {
            metiers_max_par_joueur = 0;
        }

        // Une décote absente ou vide serait une absence de décote : on ne veut
        // pas ça par accident. Paliers triés par écart croissant, sans doublon.
        if (decote == null || decote.isEmpty()) {
            decote = new MetiersConfig().decote;
        }
        List<Palier> sorted = new ArrayList<>();
        for (Palier palier : decote) {
            if (palier != null && palier.ecart >= 0) {
                sorted.add(new Palier(palier.ecart, Math.clamp(palier.pourcent, 0, 100)));
            }
        }
        sorted.sort(Comparator.comparingInt(palier -> palier.ecart));
        decote = List.copyOf(sorted);

        if (maitrise_apprentissage < 1) {
            maitrise_apprentissage = 1;
        }
        if (maitrise_acquise < maitrise_apprentissage) {
            maitrise_acquise = maitrise_apprentissage;
        }
        if (monnaie == null || Identifier.tryParse(monnaie) == null) {
            monnaie = "capitale_currency:martin_dor";
        }
        if (nodes_balayage_secondes < 1) {
            nodes_balayage_secondes = 1;
        }
        if (origine_par_defaut == null || defaultOriginOrNull() == null) {
            origine_par_defaut = "naturelle";
        }
        if (!(depecage_portee >= 1.0D) || depecage_portee > 16.0D) {
            depecage_portee = 3.0D;
        }
        if (depecage_outil == null || (!depecage_outil.isEmpty() && Identifier.tryParse(depecage_outil) == null)) {
            depecage_outil = "haute_capitale_metiers:couteaux";
        }
        if (carcasses_max_par_zone < 1) {
            carcasses_max_par_zone = 1;
        }
        if (reparation == null) {
            reparation = new Reparation();
        }
        reparation.sanitize();
        if (gadgets == null) {
            gadgets = new Gadgets();
        }
        gadgets.sanitize();
        if (foyer == null) {
            foyer = new Foyer();
        }
        foyer.sanitize();
        if (qualite == null) {
            qualite = new Qualite();
        }
        qualite.sanitize();
        if (repas == null) {
            repas = new Repas();
        }
        repas.sanitize();
    }

    /** L'origine par défaut, toujours valide après {@link #sanitize()}. */
    public SpawnOrigin defaultOrigin() {
        SpawnOrigin origin = defaultOriginOrNull();
        return origin == null ? SpawnOrigin.NATURELLE : origin;
    }

    private SpawnOrigin defaultOriginOrNull() {
        for (SpawnOrigin origin : SpawnOrigin.values()) {
            if (origin.asString().equals(origine_par_defaut)) {
                return origin;
            }
        }
        return null;
    }

    /** La famille d'outils de dépeçage par défaut, ou {@code null} si aucun outil n'est requis. */
    public Identifier skinningTool() {
        return depecage_outil.isEmpty() ? null : Identifier.of(depecage_outil);
    }

    /** L'identifiant de la monnaie, toujours valide après {@link #sanitize()}. */
    public Identifier currency() {
        return Identifier.of(monnaie);
    }
}
