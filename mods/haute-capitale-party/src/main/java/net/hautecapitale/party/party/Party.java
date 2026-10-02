package net.hautecapitale.party.party;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.util.Uuids;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Un groupe.
 *
 * <p>Modele mutable, volontairement : un groupe est modifie souvent (arrivee,
 * depart, changement de chef, de role) et le representer par un record immuable
 * obligerait a le reconstruire a chaque fois, ce qui multiplierait les occasions
 * de perdre une reference.
 *
 * <p><b>Un seul conteneur pour les membres.</b> Tout ce qui est propre a un membre
 * — son role, l'equipe de scoreboard qu'il avait avant d'entrer — vit dans
 * {@link MemberEntry}, dans une <em>liste ordonnee</em>. Cela donne trois choses
 * d'un coup : l'ordre d'anciennete necessaire au transfert deterministe de
 * leadership (regle §27), un codec qui ne contient aucune carte, et donc une
 * sauvegarde qui passe en NBT comme en JSON. Une {@code Map} a cles UUID aurait
 * casse silencieusement en NBT.
 */
public final class Party {

    /**
     * Ce qu'on sait d'un membre, en plus de son identite.
     *
     * <p>{@code lastKnownName} est rafraichi a chaque connexion. Il est stocke plutot
     * que relu a la demande parce que Minecraft 1.21.11 n'expose plus de cache de noms
     * exploitable : {@code ApiServices} ne donne acces qu'a un resolveur asynchrone.
     * Afficher un groupe dont trois membres sont hors ligne ne peut pas dependre d'une
     * requete reseau — et le HUD en aura besoin de toute facon.
     */
    public record MemberEntry(UUID uuid, Role role, Optional<String> previousTeam, String lastKnownName) {

        public static final Codec<MemberEntry> CODEC = RecordCodecBuilder.create(instance -> instance.group(
                Uuids.INT_STREAM_CODEC.fieldOf("uuid").forGetter(MemberEntry::uuid),
                Role.CODEC.optionalFieldOf("role", Role.UNSET).forGetter(MemberEntry::role),
                Codec.STRING.optionalFieldOf("previous_team").forGetter(MemberEntry::previousTeam),
                Codec.STRING.optionalFieldOf("last_known_name", "?").forGetter(MemberEntry::lastKnownName)
        ).apply(instance, MemberEntry::new));

        public static MemberEntry of(UUID uuid, String name) {
            return new MemberEntry(uuid, Role.UNSET, Optional.empty(), name);
        }

        public MemberEntry withRole(Role newRole) {
            return new MemberEntry(this.uuid, newRole, this.previousTeam, this.lastKnownName);
        }

        public MemberEntry withPreviousTeam(Optional<String> team) {
            return new MemberEntry(this.uuid, this.role, team, this.lastKnownName);
        }

        public MemberEntry withName(String name) {
            return new MemberEntry(this.uuid, this.role, this.previousTeam, name);
        }
    }

    public static final Codec<Party> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            Uuids.INT_STREAM_CODEC.fieldOf("id").forGetter(Party::id),
            Uuids.INT_STREAM_CODEC.fieldOf("leader").forGetter(Party::leader),
            MemberEntry.CODEC.listOf().fieldOf("members").forGetter(p -> List.copyOf(p.members)),
            PartyType.CODEC.optionalFieldOf("type", PartyType.DUNGEON_5).forGetter(Party::type),
            Codec.LONG.optionalFieldOf("created_at", 0L).forGetter(Party::createdAt),
            Codec.INT.optionalFieldOf("epoch", 0).forGetter(Party::epoch)
    ).apply(instance, Party::new));

    private final UUID id;
    private UUID leader;
    private final List<MemberEntry> members;
    private final PartyType type;
    private final long createdAt;
    private int epoch;

    private Party(UUID id, UUID leader, List<MemberEntry> members, PartyType type, long createdAt, int epoch) {
        this.id = id;
        this.leader = leader;
        this.members = new ArrayList<>(members);
        this.type = type;
        this.createdAt = createdAt;
        this.epoch = epoch;
    }

    /** Cree un groupe neuf autour de son fondateur, qui en est le premier chef. */
    public static Party found(UUID founder, String founderName, PartyType type, long now) {
        return new Party(UUID.randomUUID(), founder, List.of(MemberEntry.of(founder, founderName)), type, now, 0);
    }

    // --- identite ------------------------------------------------------------

    public UUID id() {
        return this.id;
    }

    public PartyType type() {
        return this.type;
    }

    public long createdAt() {
        return this.createdAt;
    }

    /**
     * Compteur de composition.
     *
     * <p>Toute operation collective — verification de preparation, vote, et surtout
     * la teleportation de groupe — memorise cette valeur au depart et refuse de se
     * conclure si elle a change entre temps. C'est le verrou qui empeche qu'une
     * reponse valide un groupe qui n'est plus le meme (regles §19 et §20).
     */
    public int epoch() {
        return this.epoch;
    }

    /** A appeler apres toute modification de la composition ou du chef. */
    private void bumpEpoch() {
        this.epoch++;
    }

    // --- chef ----------------------------------------------------------------

    public UUID leader() {
        return this.leader;
    }

    public boolean isLeader(UUID player) {
        return this.leader.equals(player);
    }

    /** Change de chef. Sans effet si la cible n'est pas membre. */
    public boolean transferTo(UUID newLeader) {
        if (!contains(newLeader) || this.leader.equals(newLeader)) {
            return false;
        }
        this.leader = newLeader;
        bumpEpoch();
        return true;
    }

    /**
     * Designe le successeur quand le chef s'en va : le membre restant le plus ancien.
     *
     * <p>Regle deterministe, et c'est le point : elle donne le meme resultat avant et
     * apres un redemarrage du serveur, donc elle est verifiable en test (regle §27).
     */
    public Optional<UUID> nextLeaderAfter(UUID leaving) {
        for (MemberEntry entry : this.members) {
            if (!entry.uuid().equals(leaving)) {
                return Optional.of(entry.uuid());
            }
        }
        return Optional.empty();
    }

    // --- membres -------------------------------------------------------------

    public int size() {
        return this.members.size();
    }

    public boolean contains(UUID player) {
        return indexOf(player) >= 0;
    }

    private int indexOf(UUID player) {
        for (int i = 0; i < this.members.size(); i++) {
            if (this.members.get(i).uuid().equals(player)) {
                return i;
            }
        }
        return -1;
    }

    /** Les identifiants, dans l'ordre d'anciennete. */
    public List<UUID> memberIds() {
        List<UUID> ids = new ArrayList<>(this.members.size());
        for (MemberEntry entry : this.members) {
            ids.add(entry.uuid());
        }
        return ids;
    }

    /** Les entrees completes, dans l'ordre d'anciennete. Copie defensive. */
    public List<MemberEntry> entries() {
        return List.copyOf(this.members);
    }

    public Optional<MemberEntry> entryOf(UUID player) {
        int index = indexOf(player);
        return index < 0 ? Optional.empty() : Optional.of(this.members.get(index));
    }

    public Role roleOf(UUID player) {
        return entryOf(player).map(MemberEntry::role).orElse(Role.UNSET);
    }

    public boolean setRole(UUID player, Role role) {
        int index = indexOf(player);
        if (index < 0) {
            return false;
        }
        this.members.set(index, this.members.get(index).withRole(role));
        return true; // le role ne change pas la composition : pas de bump d'epoch
    }

    /** Memorise l'equipe de scoreboard qu'avait le joueur avant d'entrer, pour la lui rendre. */
    public boolean setPreviousTeam(UUID player, Optional<String> team) {
        int index = indexOf(player);
        if (index < 0) {
            return false;
        }
        this.members.set(index, this.members.get(index).withPreviousTeam(team));
        return true;
    }

    public Optional<String> previousTeamOf(UUID player) {
        return entryOf(player).flatMap(MemberEntry::previousTeam);
    }

    /** Ajoute un membre en queue. Sans effet s'il est deja la. */
    public boolean add(UUID player, String name) {
        if (contains(player)) {
            return false;
        }
        this.members.add(MemberEntry.of(player, name));
        bumpEpoch();
        return true;
    }

    /** Rafraichit le nom affichable d'un membre. Ne change pas la composition. */
    public boolean setName(UUID player, String name) {
        int index = indexOf(player);
        if (index < 0) {
            return false;
        }
        this.members.set(index, this.members.get(index).withName(name));
        return true;
    }

    public String nameOf(UUID player) {
        return entryOf(player).map(MemberEntry::lastKnownName).orElse("?");
    }

    /**
     * Retire un membre.
     *
     * <p>Ne touche pas au chef : c'est au gestionnaire de decider s'il faut
     * transferer le leadership ou dissoudre, parce que lui seul sait ce qu'il faut
     * annoncer au groupe.
     */
    public boolean remove(UUID player) {
        int index = indexOf(player);
        if (index < 0) {
            return false;
        }
        this.members.remove(index);
        bumpEpoch();
        return true;
    }

    public boolean isEmpty() {
        return this.members.isEmpty();
    }

    @Override
    public String toString() {
        return "Party[" + this.id + " type=" + this.type.asString()
                + " chef=" + this.leader + " membres=" + this.members.size()
                + " epoch=" + this.epoch + "]";
    }
}
