package net.hautecapitale.party.network;

import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.fabricmc.fabric.api.networking.v1.PayloadTypeRegistry;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.hautecapitale.party.bridge.RpgClassBridge;
import net.hautecapitale.party.bridge.SkillsBridge;
import net.hautecapitale.party.config.PartyConfig;
import net.hautecapitale.party.party.Party;
import net.hautecapitale.party.party.PartyManager;
import net.hautecapitale.party.party.PartyStore;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerPlayerEntity;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

/**
 * Construit et envoie les instantanes de HUD, cote serveur.
 *
 * <p>Trois regles tiennent la charge sous controle (regle §65) :
 * <ul>
 *   <li>on regarde toutes les {@code hudUpdateTicks} ticks, pas a chaque tick ;</li>
 *   <li>on parcourt les <em>groupes</em>, jamais la liste des joueurs : un serveur
 *       plein de joueurs sans groupe ne coute rien ;</li>
 *   <li>un instantane ne part que s'il differe du dernier envoye a ce joueur —
 *       cinq joueurs immobiles a pleine vie n'echangent plus rien.</li>
 * </ul>
 *
 * <p>Et une regle de compatibilite : {@link ServerPlayNetworking#canSend} filtre les
 * clients qui n'ont pas le mod. Un client vanilla ne recoit jamais ce paquet et
 * garde tout le reste — invitations, dialogues, tir ami, plaques colorees.
 */
public final class HudSync {

    private static final Map<UUID, HudPayload> LAST_SENT = new HashMap<>();
    private static int ticks = 0;

    private HudSync() {
    }

    public static void register() {
        PayloadTypeRegistry.playS2C().register(HudPayload.ID, HudPayload.CODEC);
        ServerTickEvents.END_SERVER_TICK.register(HudSync::tick);
    }

    /** A appeler quand un joueur se deconnecte : son dernier instantane n'a plus de sens. */
    public static void forget(UUID player) {
        LAST_SENT.remove(player);
    }

    public static void resetTransient() {
        LAST_SENT.clear();
        ticks = 0;
    }

    private static void tick(MinecraftServer server) {
        if (++ticks < PartyConfig.get().hudUpdateTicks) {
            return;
        }
        ticks = 0;
        pushAll(server);
    }

    /** Une passe complete. Exposee pour le diagnostic, qui ne veut pas attendre le tick. */
    public static void pushAll(MinecraftServer server) {
        Set<UUID> served = new HashSet<>();

        for (Party party : PartyStore.of(server).all()) {
            List<ServerPlayerEntity> online = PartyManager.onlineMembers(server, party);
            if (online.isEmpty()) {
                continue;
            }
            for (ServerPlayerEntity viewer : online) {
                if (!ServerPlayNetworking.canSend(viewer, HudPayload.ID)) {
                    continue;
                }
                HudPayload snapshot = build(server, party, viewer);
                served.add(viewer.getUuid());
                if (!snapshot.equals(LAST_SENT.get(viewer.getUuid()))) {
                    ServerPlayNetworking.send(viewer, snapshot);
                    LAST_SENT.put(viewer.getUuid(), snapshot);
                }
            }
        }

        // Ceux qui avaient un HUD et n'appartiennent plus a aucun groupe : un dernier
        // envoi vide, pour qu'ils l'effacent, puis on les oublie.
        Iterator<Map.Entry<UUID, HudPayload>> iterator = LAST_SENT.entrySet().iterator();
        while (iterator.hasNext()) {
            Map.Entry<UUID, HudPayload> entry = iterator.next();
            if (served.contains(entry.getKey())) {
                continue;
            }
            ServerPlayerEntity player = server.getPlayerManager().getPlayer(entry.getKey());
            if (player != null && ServerPlayNetworking.canSend(player, HudPayload.ID)) {
                ServerPlayNetworking.send(player, HudPayload.empty());
            }
            iterator.remove();
        }
    }

    /**
     * L'instantane d'un groupe tel que le voit un membre donne.
     *
     * <p>Relatif au destinataire pour deux champs : la distance, mesuree depuis lui,
     * et le drapeau {@code SELF} sur sa propre ligne.
     */
    public static HudPayload build(MinecraftServer server, Party party, ServerPlayerEntity viewer) {
        List<HudPayload.Member> members = new ArrayList<>(party.size());
        for (Party.MemberEntry entry : party.entries()) {
            UUID uuid = entry.uuid();
            ServerPlayerEntity player = server.getPlayerManager().getPlayer(uuid);
            boolean online = player != null;
            boolean self = uuid.equals(viewer.getUuid());

            String name = online ? player.getName().getString() : entry.lastKnownName();
            float health = online ? quantizeHealth(player.getHealth()) : 0f;
            float maxHealth = online ? quantizeHealth(player.getMaxHealth()) : 0f;
            // Un joueur hors ligne n'est pas « mort » : on ne sait pas, et afficher
            // une croix serait un mensonge.
            boolean alive = !online || player.isAlive();
            int distance = distanceBetween(viewer, player);
            int level = online ? SkillsBridge.level(player) : HudPayload.Member.NO_LEVEL;
            String className = online ? RpgClassBridge.classLabel(player) : "";

            members.add(new HudPayload.Member(
                    name, health, maxHealth, distance, level,
                    (byte) entry.role().ordinal(), className,
                    HudPayload.Member.flags(party.isLeader(uuid), alive, online, self)));
        }
        return new HudPayload(List.copyOf(members));
    }

    /** Au demi-coeur pres : la regeneration naturelle ne declenche plus un envoi par tick. */
    private static float quantizeHealth(float value) {
        return Math.round(value * 2f) / 2f;
    }

    /**
     * Distance en blocs, arrondie de plus en plus grossierement avec l'eloignement.
     *
     * <p>A dix blocs, un bloc de difference se voit ; a deux cents, non. L'arrondi
     * evite qu'un groupe en marche renvoie cinq instantanes par seconde.
     */
    private static int distanceBetween(ServerPlayerEntity viewer, ServerPlayerEntity other) {
        if (other == null || other == viewer) {
            return other == viewer ? 0 : HudPayload.Member.NO_DISTANCE;
        }
        if (viewer.getEntityWorld() != other.getEntityWorld()) {
            return HudPayload.Member.NO_DISTANCE;
        }
        int exact = Math.round(viewer.distanceTo(other));
        if (exact < 16) {
            return exact;
        }
        if (exact < 64) {
            return exact / 2 * 2;
        }
        return exact / 5 * 5;
    }
}
