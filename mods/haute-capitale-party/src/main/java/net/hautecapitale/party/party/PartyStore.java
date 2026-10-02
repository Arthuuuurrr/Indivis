package net.hautecapitale.party.party;

import com.mojang.serialization.Codec;
import net.minecraft.datafixer.DataFixTypes;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.world.PersistentState;
import net.minecraft.world.PersistentStateType;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

/**
 * Les groupes, sauvegardes avec le monde.
 *
 * <p>Ce qui est ecrit sur le disque : les groupes eux-memes, leur chef, leurs
 * membres, leurs roles. Ce qui ne l'est pas, et volontairement : invitations,
 * verifications de preparation, votes et demandes de teleportation. Ces objets
 * sont attaches a des joueurs connectes et a des delais de quelques secondes ;
 * les faire survivre a un redemarrage produirait des demandes fantomes que
 * personne ne peut plus honorer (regle §39).
 *
 * <p>L'index joueur → groupe n'est pas serialise non plus : il se reconstruit a
 * la lecture. Une seule source de verite sur le disque, donc aucune divergence
 * possible entre les deux structures.
 */
public final class PartyStore extends PersistentState {

    private static final String ID = "haute_capitale_party";

    private static final Codec<PartyStore> CODEC = Party.CODEC.listOf()
            .xmap(PartyStore::new, store -> List.copyOf(store.parties.values()));

    private static final PersistentStateType<PartyStore> TYPE =
            new PersistentStateType<>(ID, () -> new PartyStore(List.of()), CODEC, DataFixTypes.LEVEL);

    private final Map<UUID, Party> parties = new HashMap<>();
    private final Map<UUID, UUID> playerToParty = new HashMap<>();

    private PartyStore(List<Party> loaded) {
        for (Party party : loaded) {
            // Un groupe vide n'a pas de sens : on le laisse tomber au chargement
            // plutot que de le trainer. Cas possible apres un arret brutal.
            if (party.isEmpty()) {
                continue;
            }
            this.parties.put(party.id(), party);
            for (UUID member : party.memberIds()) {
                this.playerToParty.put(member, party.id());
            }
        }
    }

    /**
     * Le magasin du serveur. Toujours celui de l'Overworld : les groupes
     * traversent les dimensions, ils ne peuvent donc appartenir a aucune.
     */
    public static PartyStore of(MinecraftServer server) {
        ServerWorld overworld = server.getOverworld();
        return overworld.getPersistentStateManager().getOrCreate(TYPE);
    }

    // --- lecture -------------------------------------------------------------

    /** Le groupe d'un joueur, en un seul acces. Aucun parcours (regle §65). */
    public Optional<Party> partyOf(UUID player) {
        UUID partyId = this.playerToParty.get(player);
        return partyId == null ? Optional.empty() : Optional.ofNullable(this.parties.get(partyId));
    }

    public Optional<Party> byId(UUID partyId) {
        return Optional.ofNullable(this.parties.get(partyId));
    }

    public Collection<Party> all() {
        return List.copyOf(this.parties.values());
    }

    public int count() {
        return this.parties.size();
    }

    public boolean hasParty(UUID player) {
        return this.playerToParty.containsKey(player);
    }

    // --- ecriture ------------------------------------------------------------

    /** Enregistre un groupe neuf et indexe ses membres. */
    public void register(Party party) {
        this.parties.put(party.id(), party);
        for (UUID member : party.memberIds()) {
            this.playerToParty.put(member, party.id());
        }
        markDirty();
    }

    public void indexMember(UUID player, Party party) {
        this.playerToParty.put(player, party.id());
        markDirty();
    }

    public void unindexMember(UUID player) {
        this.playerToParty.remove(player);
        markDirty();
    }

    /** Retire un groupe et desindexe tous ses membres. */
    public void unregister(Party party) {
        this.parties.remove(party.id());
        // On balaye l'index par valeur : un membre a pu etre retire du groupe sans
        // que l'index le soit, si un appelant s'est trompe. Ce nettoyage rend la
        // dissolution idempotente.
        this.playerToParty.entrySet().removeIf(entry -> entry.getValue().equals(party.id()));
        markDirty();
    }

    /**
     * Verifie que l'index et les groupes disent la meme chose, et corrige au besoin.
     *
     * <p>Appele une fois au demarrage du serveur. Rend compte de ce qu'il a corrige :
     * une divergence signalee dans le journal est un bug a chercher, pas un evenement
     * normal.
     */
    public List<String> reconcile() {
        List<String> problems = new ArrayList<>();

        List<Party> empty = new ArrayList<>();
        for (Party party : this.parties.values()) {
            if (party.isEmpty()) {
                empty.add(party);
                continue;
            }
            if (!party.contains(party.leader())) {
                party.nextLeaderAfter(party.leader()).ifPresent(party::transferTo);
                problems.add("groupe " + party.id() + " : le chef n'etait pas membre, leadership reattribue");
            }
        }
        for (Party party : empty) {
            unregister(party);
            problems.add("groupe " + party.id() + " : vide au chargement, supprime");
        }

        this.playerToParty.entrySet().removeIf(entry -> {
            Party party = this.parties.get(entry.getValue());
            boolean orphan = party == null || !party.contains(entry.getKey());
            if (orphan) {
                problems.add("joueur " + entry.getKey() + " : index vers un groupe absent, nettoye");
            }
            return orphan;
        });

        for (Party party : this.parties.values()) {
            for (UUID member : party.memberIds()) {
                if (!party.id().equals(this.playerToParty.get(member))) {
                    this.playerToParty.put(member, party.id());
                    problems.add("joueur " + member + " : absent de l'index, reindexe");
                }
            }
        }

        if (!problems.isEmpty()) {
            markDirty();
        }
        return problems;
    }
}
