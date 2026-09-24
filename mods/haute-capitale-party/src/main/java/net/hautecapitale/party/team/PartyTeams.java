package net.hautecapitale.party.team;

import net.hautecapitale.party.HauteCapitaleParty;
import net.hautecapitale.party.config.PartyConfig;
import net.hautecapitale.party.party.Party;
import net.minecraft.scoreboard.AbstractTeam;
import net.minecraft.scoreboard.Scoreboard;
import net.minecraft.scoreboard.Team;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * L'equipe de scoreboard qui porte un groupe.
 *
 * <p><b>C'est ici que se joue le tir ami.</b> Minecraft refuse deja tout degat
 * entre deux joueurs d'une meme equipe dont le tir ami est desactive
 * ({@code ServerPlayerEntity.damage} consulte {@code canHarmPlayer}, qui consulte
 * {@code Team.isFriendlyFireAllowed}). Spell Engine et Better Combat lisent la
 * meme equipe : leurs deux « team matchers » vanilla en deduisent la relation
 * ALLY (Spell Engine) et FRIENDLY (Better Combat), ce qui bloque chez eux les
 * sorts nuisibles directs comme de zone, et laisse passer soins et bonus.
 *
 * <p>Autrement dit : une equipe correctement reglee couvre la melee, les fleches,
 * les sorts et les balayages, sans qu'aucun de ces mods soit modifie. Le reste du
 * travail — les degats d'invocations et les degats indirects — est fait par
 * {@code FriendlyFire}.
 *
 * <p><b>Regle de prudence.</b> Le scoreboard est une ressource partagee par tout le
 * serveur. Ce module ne cree, ne modifie et ne supprime que des equipes portant son
 * prefixe, et il rend a chaque joueur l'equipe qu'il avait avant d'entrer.
 */
public final class PartyTeams {

    /** Couleurs distinctes pour que deux groupes voisins ne se confondent pas. */
    private static final Formatting[] COLORS = {
            Formatting.AQUA, Formatting.YELLOW, Formatting.LIGHT_PURPLE, Formatting.GREEN,
            Formatting.GOLD, Formatting.BLUE, Formatting.RED, Formatting.DARK_AQUA
    };

    private PartyTeams() {
    }

    /** Nom de l'equipe d'un groupe. Court, stable, et reconnaissable a son prefixe. */
    public static String teamName(Party party) {
        return PartyConfig.get().teamNamePrefix + party.id().toString().replace("-", "").substring(0, 8);
    }

    public static boolean isOurs(String name) {
        return name != null && name.startsWith(PartyConfig.get().teamNamePrefix);
    }

    /** Cree l'equipe du groupe si besoin, et applique ses reglages. */
    public static Optional<Team> ensureTeam(MinecraftServer server, Party party) {
        PartyConfig config = PartyConfig.get();
        if (!config.manageScoreboardTeams) {
            return Optional.empty();
        }
        Scoreboard scoreboard = server.getScoreboard();
        String name = teamName(party);
        Team team = scoreboard.getTeam(name);
        if (team == null) {
            team = scoreboard.addTeam(name);
        }

        team.setDisplayName(Text.literal("Groupe " + party.type().label()));
        // Le reglage qui compte : c'est lui que lisent Minecraft, Spell Engine et Better Combat.
        team.setFriendlyFireAllowed(!config.friendlyFireProtection);
        team.setShowFriendlyInvisibles(config.seeFriendlyInvisibles);
        team.setCollisionRule(config.preventTeammateCollision
                ? AbstractTeam.CollisionRule.PUSH_OTHER_TEAMS
                : AbstractTeam.CollisionRule.ALWAYS);
        if (config.colorNameTags) {
            team.setColor(colorFor(party));
        }
        // Pas d'appel a updateScoreboardTeamAndPlayers ici : sur un ServerScoreboard,
        // addTeam envoie deja la creation aux clients et chaque setter envoie sa mise a
        // jour. Un appel de plus renvoie une *creation*, et le client le signale :
        // « Requested creation of existing team » — vu dans le journal d'un client de test.
        return Optional.of(team);
    }

    private static Formatting colorFor(Party party) {
        return COLORS[Math.floorMod(party.id().hashCode(), COLORS.length)];
    }

    /**
     * Place un joueur connecte dans l'equipe de son groupe, en memorisant celle
     * qu'il quitte pour pouvoir la lui rendre.
     */
    public static void join(MinecraftServer server, Party party, ServerPlayerEntity player) {
        if (!PartyConfig.get().manageScoreboardTeams) {
            return;
        }
        Optional<Team> team = ensureTeam(server, party);
        if (team.isEmpty()) {
            return;
        }
        Scoreboard scoreboard = server.getScoreboard();
        String holder = player.getNameForScoreboard();

        Team current = scoreboard.getScoreHolderTeam(holder);
        if (current != null && current != team.get()) {
            // Appartenance venue d'ailleurs : on la note avant de l'ecraser, pour
            // ne pas faire disparaitre silencieusement le travail d'un autre mod.
            if (!isOurs(current.getName())) {
                party.setPreviousTeam(player.getUuid(), Optional.of(current.getName()));
                HauteCapitaleParty.LOGGER.info(
                        "{} appartenait a l'equipe '{}' : elle lui sera rendue en quittant le groupe.",
                        holder, current.getName());
            }
        }
        scoreboard.addScoreHolderToTeam(holder, team.get());
    }

    /** Sort un joueur connecte de l'equipe du groupe et lui rend la precedente. */
    public static void leave(MinecraftServer server, Party party, ServerPlayerEntity player) {
        if (!PartyConfig.get().manageScoreboardTeams) {
            return;
        }
        Scoreboard scoreboard = server.getScoreboard();
        String holder = player.getNameForScoreboard();
        Team current = scoreboard.getScoreHolderTeam(holder);
        if (current != null && isOurs(current.getName())) {
            scoreboard.removeScoreHolderFromTeam(holder, current);
        }
        restorePrevious(server, party, player);
    }

    private static void restorePrevious(MinecraftServer server, Party party, ServerPlayerEntity player) {
        if (!PartyConfig.get().restorePreviousTeam) {
            return;
        }
        Optional<String> previous = party.previousTeamOf(player.getUuid());
        if (previous.isEmpty()) {
            return;
        }
        Team team = server.getScoreboard().getTeam(previous.get());
        if (team != null) {
            server.getScoreboard().addScoreHolderToTeam(player.getNameForScoreboard(), team);
        } else {
            HauteCapitaleParty.LOGGER.info(
                    "L'equipe d'origine '{}' de {} n'existe plus : elle ne peut pas lui etre rendue.",
                    previous.get(), player.getNameForScoreboard());
        }
        party.setPreviousTeam(player.getUuid(), Optional.empty());
    }

    /**
     * Supprime l'equipe d'un groupe dissous.
     *
     * <p>Les membres encore connectes recuperent d'abord leur equipe d'origine ;
     * {@code removeTeam} sort ensuite tout le monde, y compris les joueurs hors
     * ligne, ce qu'aucun appel par UUID ne saurait faire (le scoreboard indexe par
     * nom, pas par identifiant).
     */
    public static void dissolve(MinecraftServer server, Party party) {
        if (!PartyConfig.get().manageScoreboardTeams) {
            return;
        }
        for (UUID member : party.memberIds()) {
            ServerPlayerEntity player = server.getPlayerManager().getPlayer(member);
            if (player != null) {
                restorePrevious(server, party, player);
            }
        }
        Team team = server.getScoreboard().getTeam(teamName(party));
        if (team != null) {
            server.getScoreboard().removeTeam(team);
        }
    }

    /**
     * Remet un joueur qui vient de se connecter dans l'etat qu'il devrait avoir.
     *
     * <p>Necessaire parce que le scoreboard travaille par nom de joueur et ne peut
     * donc rien faire d'un membre hors ligne : les mutations subies pendant son
     * absence — exclusion, dissolution — sont rattrapees ici.
     */
    public static void syncOnJoin(MinecraftServer server, Optional<Party> party, ServerPlayerEntity player) {
        if (!PartyConfig.get().manageScoreboardTeams) {
            return;
        }
        if (party.isPresent()) {
            join(server, party.get(), player);
            return;
        }
        Scoreboard scoreboard = server.getScoreboard();
        String holder = player.getNameForScoreboard();
        Team current = scoreboard.getScoreHolderTeam(holder);
        if (current != null && isOurs(current.getName())) {
            scoreboard.removeScoreHolderFromTeam(holder, current);
        }
    }

    /**
     * Supprime les equipes de groupe qui ne correspondent plus a aucun groupe.
     *
     * <p>Appele une fois au demarrage. Sans ce nettoyage, un arret brutal laisserait
     * des equipes orphelines qui continueraient a proteger d'anciens coequipiers.
     */
    public static List<String> pruneOrphans(MinecraftServer server, List<String> liveTeamNames) {
        List<String> removed = new ArrayList<>();
        if (!PartyConfig.get().manageScoreboardTeams) {
            return removed;
        }
        Scoreboard scoreboard = server.getScoreboard();
        for (String name : List.copyOf(scoreboard.getTeamNames())) {
            if (isOurs(name) && !liveTeamNames.contains(name)) {
                Team team = scoreboard.getTeam(name);
                if (team != null) {
                    scoreboard.removeTeam(team);
                    removed.add(name);
                }
            }
        }
        return removed;
    }
}
