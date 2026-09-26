package net.hautecapitale.party.config;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonParseException;
import net.fabricmc.loader.api.FabricLoader;
import net.hautecapitale.party.HauteCapitaleParty;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;

/**
 * Reglages du systeme de groupe.
 *
 * <p>Ecrit dans {@code config/haute_capitale_party.json} au premier demarrage.
 * Un fichier illisible n'empeche jamais le serveur de demarrer : on repart des
 * valeurs par defaut et on le dit dans le journal.
 */
public final class PartyConfig {

    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();
    private static final String FILE_NAME = "haute_capitale_party.json";

    private static PartyConfig instance = new PartyConfig();

    // --- protection entre membres -------------------------------------------

    /**
     * Empeche les membres d'un meme groupe de se blesser (regle §4).
     *
     * <p>L'essentiel du travail est fait par l'equipe de scoreboard : Minecraft,
     * Spell Engine et Better Combat en deduisent tous les trois que les membres
     * sont allies. Mettre ceci a {@code false} rend le groupe purement social.
     */
    public boolean friendlyFireProtection = true;

    /**
     * Etend la protection aux degats qui ne portent pas la signature d'un joueur :
     * ceux d'une invocation, d'un familier ou d'un projectile appartenant a un
     * membre. C'est le filet decrit dans l'audit ; l'equipe de scoreboard seule
     * ne les couvre pas.
     */
    public boolean guardOwnedEntities = true;

    /**
     * Empeche une invocation ou un familier d'un membre de <b>cibler</b> les autres membres du
     * groupe (un golem d'invocateur qui, sa cible morte, fonçait sur ses allies). Complete la
     * protection de degats : sans ca, meme si le degat est annule, la creature poursuit l'allie.
     */
    public boolean guardSummonTargeting = true;

    // --- equipes de scoreboard ----------------------------------------------

    /** Cree et entretient une equipe vanilla par groupe. Sans elle, pas de protection. */
    public boolean manageScoreboardTeams = true;

    /** Prefixe des equipes creees. Ne touche jamais a une equipe qui ne le porte pas. */
    public String teamNamePrefix = "hcp_";

    /**
     * Rend au joueur l'equipe qu'il avait avant d'entrer dans un groupe.
     *
     * <p>A laisser vrai si un autre mod du pack se sert des equipes : sans cela,
     * rejoindre un groupe effacerait definitivement son appartenance d'origine.
     */
    public boolean restorePreviousTeam = true;

    /** Colore la plaque de nom des equipiers : c'est le reperage demande par la regle §46. */
    public boolean colorNameTags = true;

    /** Les equipiers se voient meme invisibles. */
    public boolean seeFriendlyInvisibles = true;

    /** Les equipiers ne se poussent pas entre eux. */
    public boolean preventTeammateCollision = true;

    // --- social --------------------------------------------------------------

    /** Duree de validite d'une invitation, en secondes. */
    public int inviteTimeoutSeconds = 60;

    /** Delai minimal entre deux invitations envoyees par le meme joueur (regle §49). */
    public int inviteCooldownSeconds = 5;

    /**
     * Taille maximale d'un groupe de donjon.
     *
     * <p>Le controle « exactement cinq » se fait a l'entree du donjon, pas ici :
     * un groupe doit pouvoir exister a trois pendant qu'il cherche du monde.
     */
    public int dungeonPartyMaxSize = 5;

    /** En dessous de ce nombre de membres, le chef exclut directement, sans vote (regle §15). */
    public int kickVoteMinMembers = 4;

    // --- consultations du groupe (phase 2) -----------------------------------

    /** Duree d'une verification de preparation. Passe ce delai, le silence vaut « pas pret ». */
    public int readyCheckSeconds = 30;

    /** Delai entre deux verifications de preparation lancees par le meme groupe (regle §49). */
    public int readyCheckCooldownSeconds = 30;

    /** Duree d'un vote d'exclusion. Le silence y vaut un refus : le statu quo l'emporte. */
    public int kickVoteSeconds = 45;

    /** Delai entre deux votes d'exclusion dans le meme groupe, quelle que soit la cible. */
    public int kickVoteCooldownSeconds = 30;

    /** Delai avant de pouvoir relancer un vote contre la meme personne. */
    public int kickVoteTargetCooldownSeconds = 60;

    /**
     * Protection accordee a quelqu'un dont l'exclusion vient d'echouer.
     *
     * <p>Sans elle, un chef pourrait relancer le vote en boucle jusqu'a ce que
     * l'inattention des autres finisse par le faire passer.
     */
    public int kickVoteFailProtectionSeconds = 300;

    /** Valeur maximale acceptee par la commande de compte a rebours. */
    public int countdownMaxSeconds = 30;

    // --- HUD (phase 3) -------------------------------------------------------

    /**
     * Cadence d'envoi du HUD, en ticks. 5 = quatre fois par seconde.
     *
     * <p>Seuls les instantanes qui ont change partent reellement ; ce reglage borne
     * simplement la frequence a laquelle on regarde s'ils ont change.
     */
    public int hudUpdateTicks = 5;

    /**
     * Categorie Pufferfish qui fait foi pour le niveau affiche, par exemple
     * {@code "puffish_skills:combat"}. Vide : le plus haut niveau parmi toutes les
     * categories du joueur.
     */
    public String levelCategory = "";

    /** Delai entre deux comptes a rebours lances par le meme groupe. */
    public int countdownCooldownSeconds = 15;

    // --- acces ---------------------------------------------------------------

    public static PartyConfig get() {
        return instance;
    }

    public static void load() {
        Path path = FabricLoader.getInstance().getConfigDir().resolve(FILE_NAME);
        if (!Files.exists(path)) {
            instance = new PartyConfig();
            save(path);
            HauteCapitaleParty.LOGGER.info("Configuration creee : {}", path);
            return;
        }
        try {
            String json = Files.readString(path, StandardCharsets.UTF_8);
            PartyConfig loaded = GSON.fromJson(json, PartyConfig.class);
            instance = loaded != null ? loaded : new PartyConfig();
            instance.clamp();
        } catch (IOException | JsonParseException exception) {
            instance = new PartyConfig();
            HauteCapitaleParty.LOGGER.error(
                    "Configuration illisible ({}), valeurs par defaut utilisees : {}",
                    path, deepestMessage(exception));
        }
    }

    /** Borne les valeurs qu'un humain peut avoir mal saisies. */
    private void clamp() {
        this.inviteTimeoutSeconds = Math.clamp(this.inviteTimeoutSeconds, 5, 600);
        this.inviteCooldownSeconds = Math.clamp(this.inviteCooldownSeconds, 0, 60);
        this.dungeonPartyMaxSize = Math.clamp(this.dungeonPartyMaxSize, 1, 5);
        this.kickVoteMinMembers = Math.clamp(this.kickVoteMinMembers, 2, 20);
        this.readyCheckSeconds = Math.clamp(this.readyCheckSeconds, 5, 300);
        this.readyCheckCooldownSeconds = Math.clamp(this.readyCheckCooldownSeconds, 0, 600);
        this.kickVoteSeconds = Math.clamp(this.kickVoteSeconds, 10, 300);
        this.kickVoteCooldownSeconds = Math.clamp(this.kickVoteCooldownSeconds, 0, 600);
        this.kickVoteTargetCooldownSeconds = Math.clamp(this.kickVoteTargetCooldownSeconds, 0, 3600);
        this.kickVoteFailProtectionSeconds = Math.clamp(this.kickVoteFailProtectionSeconds, 0, 3600);
        this.countdownMaxSeconds = Math.clamp(this.countdownMaxSeconds, 1, 300);
        this.countdownCooldownSeconds = Math.clamp(this.countdownCooldownSeconds, 0, 600);
        this.hudUpdateTicks = Math.clamp(this.hudUpdateTicks, 1, 100);
        if (this.levelCategory == null) {
            this.levelCategory = "";
        }
        if (this.teamNamePrefix == null || this.teamNamePrefix.isBlank()) {
            this.teamNamePrefix = "hcp_";
        }
    }

    private static void save(Path path) {
        try {
            Files.createDirectories(path.getParent());
            Files.writeString(path, GSON.toJson(instance), StandardCharsets.UTF_8);
        } catch (IOException exception) {
            HauteCapitaleParty.LOGGER.error("Impossible d'ecrire la configuration : {}", exception.getMessage());
        }
    }

    /** Gson enfouit la vraie cause ; sans cela le message ne dit ni la ligne ni la colonne. */
    private static String deepestMessage(Throwable throwable) {
        Throwable current = throwable;
        while (current.getCause() != null && current.getCause() != current) {
            current = current.getCause();
        }
        return current.getMessage() != null ? current.getMessage() : current.toString();
    }
}
