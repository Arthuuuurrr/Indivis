package net.hautecapitale.spawns.config;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonParseException;
import net.hautecapitale.spawns.HauteCapitaleSpawns;
import net.hautecapitale.spawns.data.CreditMode;
import net.hautecapitale.spawns.data.Leash;
import net.hautecapitale.spawns.data.Respawn;
import net.hautecapitale.spawns.data.RespawnConditions;
import net.hautecapitale.spawns.data.SpawnSettings;
import net.minecraft.util.Identifier;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * Configuration globale : {@code config/haute_capitale_spawns/config.json}.
 *
 * <p>Ce sont les valeurs de dernier recours de la resolution (voir
 * {@code Resolved}) et les reglages du moteur. Tout ce qui concerne un camp
 * precis vit dans les fichiers de zones et de profils, pas ici.
 */
public final class SpawnsConfig {

    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().disableHtmlEscaping().create();
    private static SpawnsConfig current = new SpawnsConfig();

    // --- moteur ---------------------------------------------------------------------
    /** Periode du moteur, en ticks. 10 = deux passes par seconde. */
    public int tickInterval = 10;
    /** Rayon d'activation par defaut d'un point (blocs). */
    public int defaultActivationRadius = 96;
    /** Plafond des rayons d'activation, pour borner le balayage des chunks. */
    public int maxActivationRadius = 256;
    /** Temps sans retrouver un mob « vivant » dans un chunk actif avant de le declarer perdu. */
    public int lostEntityGraceSeconds = 8;
    /** Delai de reapparition applique apres une perte anormale (pas une mort). */
    public int lostEntityRespawnSeconds = 30;
    /** Supprimer une entite controlee dont le point n'existe plus (sinon on la libere). */
    public boolean discardOrphans = true;
    /** Supprimer le mob quand son point ou sa zone est desactive. */
    public boolean despawnOnDisable = true;

    // --- valeurs par defaut des points ------------------------------------------------
    public String defaultRespawnMode = "random";
    public int defaultRespawnMinSeconds = 240;
    public int defaultRespawnMaxSeconds = 300;
    public int defaultLeashRadius = 35;
    public int leashReturnRadius = 3;
    public boolean leashHealOnReturn = true;
    public boolean leashInvulnerableWhileReturning = true;
    public int leashTimeoutSeconds = 30;
    public double leashSpeed = 1.2D;
    public int defaultWanderRadius = 10;
    public int defaultMinPlayerDistance = 6;
    public String defaultRank = "normal";
    /** Rangs acceptes par {@code set rank}. Librement extensible. */
    public List<String> ranks = new ArrayList<>(List.of("normal", "elite", "mini_boss", "world_boss"));

    // --- niveaux ------------------------------------------------------------------------
    /** PV max = base x (1 + (niveau - 1) x facteur) : 1.0 => niveau 2 double, niveau 3 triple. */
    public double levelHealthFactor = 1.0D;
    /** Meme formule pour les degats d'attaque ; 0 = les degats ne changent pas avec le niveau. */
    public double levelAttackFactor = 0.0D;
    /** Niveau maximal accepte par {@code set level}. */
    public int maxLevel = 100;

    // --- credit de kill, quetes -------------------------------------------------------
    public String defaultCreditMode = "party_nearby";
    /** Fenetre pendant laquelle un coup compte comme participation (secondes). */
    public int participationWindowSeconds = 60;
    /** Distance maximale au kill pour qu'un participant ou un membre du groupe soit credite. */
    public int creditRadius = 48;
    /** Laisser le gestionnaire attribuer lui-meme les objets de quete (sinon, evenement seul). */
    public boolean builtInQuestDrops = true;
    /** Creer les objectifs de scoreboard manquants pour les compteurs. */
    public boolean createMissingObjectives = true;
    /** Etiquettes de commande ajoutees a chaque mob controle, en plus des etiquettes hcspawn.*. */
    public List<String> extraCommandTags = new ArrayList<>(List.of("hcm_origine_spawn_mmo"));

    // --- administration -------------------------------------------------------------
    /** Portee de l'affichage de debogage autour de l'admin (blocs). */
    public int debugRange = 48;
    /** Journaliser chaque apparition et chaque mort dans la console. */
    public boolean logEvents = false;

    public static SpawnsConfig get() {
        return current;
    }

    public static Path file(Path root) {
        return root.resolve("config.json");
    }

    public static SpawnsConfig load(Path root) {
        Path file = file(root);
        SpawnsConfig loaded = null;
        if (Files.exists(file)) {
            try {
                loaded = GSON.fromJson(Files.readString(file, StandardCharsets.UTF_8), SpawnsConfig.class);
            } catch (IOException | JsonParseException e) {
                HauteCapitaleSpawns.LOGGER.warn("Configuration illisible ({}), valeurs par defaut conservees : {}", file, e.getMessage());
            }
        }
        if (loaded == null) {
            loaded = new SpawnsConfig();
        }
        loaded.sanitize();
        current = loaded;
        save(root, loaded);
        return loaded;
    }

    public static void save(Path root, SpawnsConfig config) {
        try {
            Files.createDirectories(root);
            Files.writeString(file(root), GSON.toJson(config), StandardCharsets.UTF_8);
        } catch (IOException e) {
            HauteCapitaleSpawns.LOGGER.warn("Impossible d'ecrire la configuration : {}", e.getMessage());
        }
    }

    /** Pour les tests : remplace la configuration courante. */
    public static void set(SpawnsConfig config) {
        config.sanitize();
        current = config;
    }

    void sanitize() {
        this.tickInterval = Math.max(1, this.tickInterval);
        this.defaultActivationRadius = Math.max(16, this.defaultActivationRadius);
        this.maxActivationRadius = Math.max(this.defaultActivationRadius, this.maxActivationRadius);
        this.lostEntityGraceSeconds = Math.max(1, this.lostEntityGraceSeconds);
        this.lostEntityRespawnSeconds = Math.max(0, this.lostEntityRespawnSeconds);
        this.defaultRespawnMinSeconds = Math.max(0, this.defaultRespawnMinSeconds);
        this.defaultRespawnMaxSeconds = Math.max(this.defaultRespawnMinSeconds, this.defaultRespawnMaxSeconds);
        this.defaultLeashRadius = Math.max(0, this.defaultLeashRadius);
        this.leashReturnRadius = Math.max(1, this.leashReturnRadius);
        this.leashTimeoutSeconds = Math.max(1, this.leashTimeoutSeconds);
        this.leashSpeed = this.leashSpeed <= 0 ? 1.0D : this.leashSpeed;
        this.defaultWanderRadius = Math.max(0, this.defaultWanderRadius);
        this.defaultMinPlayerDistance = Math.max(0, this.defaultMinPlayerDistance);
        this.participationWindowSeconds = Math.max(1, this.participationWindowSeconds);
        this.creditRadius = Math.max(1, this.creditRadius);
        this.debugRange = Math.max(8, this.debugRange);
        if (this.ranks == null || this.ranks.isEmpty()) {
            this.ranks = new ArrayList<>(List.of("normal", "elite", "mini_boss", "world_boss"));
        }
        if (this.defaultRank == null || !this.ranks.contains(this.defaultRank)) {
            this.defaultRank = this.ranks.get(0);
        }
        if (this.extraCommandTags == null) {
            this.extraCommandTags = new ArrayList<>();
        }
        if (CreditMode.byId(this.defaultCreditMode) == null) {
            this.defaultCreditMode = "party_nearby";
        }
        this.levelHealthFactor = Math.max(0.0D, this.levelHealthFactor);
        this.levelAttackFactor = Math.max(0.0D, this.levelAttackFactor);
        this.maxLevel = Math.max(1, this.maxLevel);
    }

    /** La couche de base de la resolution : complete, sans entite. */
    public SpawnSettings baseSettings() {
        Respawn respawn = "fixed".equalsIgnoreCase(this.defaultRespawnMode)
                ? Respawn.fixed(this.defaultRespawnMinSeconds)
                : Respawn.random(this.defaultRespawnMinSeconds, this.defaultRespawnMaxSeconds);
        Leash leash = new Leash(this.defaultLeashRadius, this.leashReturnRadius, this.leashHealOnReturn,
                this.leashInvulnerableWhileReturning, this.leashTimeoutSeconds, this.leashSpeed);
        return new SpawnSettings(
                Optional.<Identifier>empty(),
                Optional.of(respawn),
                Optional.of(leash),
                Optional.of(this.defaultActivationRadius),
                Optional.of(this.defaultWanderRadius),
                Optional.of(this.defaultRank),
                Map.of(),
                Optional.of(List.of()),
                Optional.of(List.of()),
                Optional.of(new RespawnConditions(true, this.defaultMinPlayerDistance)),
                Optional.empty(),
                Optional.empty(),
                Optional.of(Boolean.TRUE),
                Optional.of(CreditMode.byId(this.defaultCreditMode)),
                Optional.of(1),
                Map.of());
    }

    public int lostGraceChecks() {
        return Math.max(1, (this.lostEntityGraceSeconds * 20) / this.tickInterval);
    }
}
