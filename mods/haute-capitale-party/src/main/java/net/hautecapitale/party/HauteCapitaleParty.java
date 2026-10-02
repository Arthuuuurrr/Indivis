package net.hautecapitale.party;

import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.entity.event.v1.ServerPlayerEvents;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.hautecapitale.party.combat.FriendlyFire;
import net.hautecapitale.party.command.PartyCommands;
import net.hautecapitale.party.config.PartyConfig;
import net.hautecapitale.party.network.HudSync;
import net.hautecapitale.party.party.Party;
import net.hautecapitale.party.party.PartyManager;
import net.hautecapitale.party.party.PartyStore;
import net.hautecapitale.party.request.RequestManager;
import net.hautecapitale.party.team.PartyTeams;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;

/**
 * Haute Capitale — Party.
 *
 * <p>Systeme de groupe du serveur MMO. Entierement cote serveur : un client vanilla,
 * sans rien d'installe, joue normalement et voit tout — les messages, les boutons
 * d'invitation, la couleur des plaques de nom des equipiers.
 *
 * <p>Ce mod ne declare aucun objet, aucun bloc, aucune entite, et ne pose aucun
 * mixin. Il n'ecrit dans le monde que ses propres groupes et les equipes de
 * scoreboard qu'il a lui-meme creees ; le retirer rend le serveur a son etat
 * anterieur.
 */
public class HauteCapitaleParty implements ModInitializer {

    public static final String MOD_ID = "haute_capitale_party";
    public static final Logger LOGGER = LoggerFactory.getLogger("Haute Capitale — Party");

    @Override
    public void onInitialize() {
        PartyConfig.load();

        PartyCommands.register();
        FriendlyFire.register();
        HudSync.register();

        ServerLifecycleEvents.SERVER_STARTED.register(HauteCapitaleParty::onServerStarted);
        ServerPlayerEvents.JOIN.register(player -> {
            if (player.getEntityWorld().getServer() != null) {
                PartyManager.onPlayerJoin(player.getEntityWorld().getServer(), player);
            }
        });
        ServerPlayerEvents.LEAVE.register(player -> {
            if (player.getEntityWorld().getServer() != null) {
                RequestManager.onPlayerLeave(player.getEntityWorld().getServer(), player.getUuid());
                HudSync.forget(player.getUuid());
                PartyManager.onPlayerLeave(player.getEntityWorld().getServer(), player);
            }
        });

        // Le seul battement du mod. Il sert aux expirations de consultation et au
        // compte a rebours, et sort immediatement quand il n'y a rien en cours :
        // aucun groupe n'est parcouru, jamais (regle §65).
        ServerTickEvents.END_SERVER_TICK.register(RequestManager::tick);

        LOGGER.info("Systeme de groupe initialise. Protection entre membres : {}.",
                PartyConfig.get().friendlyFireProtection ? "active" : "desactivee");
    }

    /**
     * Remet le monde d'aplomb au demarrage.
     *
     * <p>Trois choses, dans cet ordre : oublier les objets volatils, verifier que
     * les groupes charges sont coherents, puis supprimer les equipes de scoreboard
     * qui ne correspondent plus a aucun groupe. Sans ce dernier menage, un arret
     * brutal laisserait des equipes orphelines qui continueraient a proteger
     * d'anciens coequipiers.
     */
    private static void onServerStarted(net.minecraft.server.MinecraftServer server) {
        PartyManager.resetTransient();
        RequestManager.resetTransient();
        HudSync.resetTransient();

        PartyStore store = PartyStore.of(server);
        List<String> problems = store.reconcile();
        for (String problem : problems) {
            LOGGER.warn("Coherence des groupes — {}", problem);
        }

        List<String> liveTeams = new ArrayList<>();
        for (Party party : store.all()) {
            liveTeams.add(PartyTeams.teamName(party));
            PartyTeams.ensureTeam(server, party);
        }
        List<String> removed = PartyTeams.pruneOrphans(server, liveTeams);
        if (!removed.isEmpty()) {
            LOGGER.info("Equipes de groupe orphelines supprimees : {}", String.join(", ", removed));
        }

        LOGGER.info("{} groupe(s) charge(s).", store.count());
    }
}
