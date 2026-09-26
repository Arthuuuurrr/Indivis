package net.hautecapitale.party.network;

import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.codec.PacketCodecs;
import net.minecraft.network.packet.CustomPayload;
import net.minecraft.util.Identifier;

import java.util.ArrayList;
import java.util.List;

/**
 * L'instantane du groupe envoye au client pour son HUD.
 *
 * <p>Une liste vide signifie « vous n'etes dans aucun groupe » : le client efface
 * son affichage. C'est le seul paquet du mod, et il ne part que vers les clients qui
 * ont declare le connaitre — un client vanilla n'en recoit jamais et continue de
 * jouer avec tout le reste (regle §44).
 *
 * <p>Le paquet est construit <em>pour</em> son destinataire : la distance est
 * mesuree depuis lui, et le drapeau {@code SELF} designe sa propre ligne.
 *
 * <p>Codec ecrit a la main plutot que par {@code PacketCodec.tuple}, qui s'arrete
 * a quelques champs. Les quatre booleens tiennent dans un octet de drapeaux.
 */
public record HudPayload(List<Member> members) implements CustomPayload {

    public static final Id<HudPayload> ID = new Id<>(Identifier.of("haute_capitale_party", "hud"));

    /** Une ligne du HUD. */
    public record Member(
            String name,
            float health,
            float maxHealth,
            int distance,
            int level,
            byte role,
            String className,
            byte flags
    ) {
        public static final byte LEADER = 1;
        public static final byte ALIVE = 2;
        public static final byte ONLINE = 4;
        public static final byte SELF = 8;

        /** Distance quand elle n'a pas de sens : autre dimension, ou hors ligne. */
        public static final int NO_DISTANCE = -1;
        /** Niveau quand aucune source ne le fournit. */
        public static final int NO_LEVEL = -1;

        public boolean leader() {
            return (this.flags & LEADER) != 0;
        }

        public boolean alive() {
            return (this.flags & ALIVE) != 0;
        }

        public boolean online() {
            return (this.flags & ONLINE) != 0;
        }

        public boolean self() {
            return (this.flags & SELF) != 0;
        }

        public static byte flags(boolean leader, boolean alive, boolean online, boolean self) {
            int value = 0;
            if (leader) value |= LEADER;
            if (alive) value |= ALIVE;
            if (online) value |= ONLINE;
            if (self) value |= SELF;
            return (byte) value;
        }

        static final PacketCodec<RegistryByteBuf, Member> CODEC = PacketCodec.of(
                (member, buf) -> {
                    PacketCodecs.STRING.encode(buf, member.name);
                    buf.writeFloat(member.health);
                    buf.writeFloat(member.maxHealth);
                    buf.writeVarInt(member.distance);
                    buf.writeVarInt(member.level);
                    buf.writeByte(member.role);
                    PacketCodecs.STRING.encode(buf, member.className);
                    buf.writeByte(member.flags);
                },
                buf -> new Member(
                        PacketCodecs.STRING.decode(buf),
                        buf.readFloat(),
                        buf.readFloat(),
                        buf.readVarInt(),
                        buf.readVarInt(),
                        buf.readByte(),
                        PacketCodecs.STRING.decode(buf),
                        buf.readByte()));
    }

    /** Plafond de securite : aucun type de groupe n'approche ce nombre. */
    private static final int MAX_MEMBERS = 40;

    public static final PacketCodec<RegistryByteBuf, HudPayload> CODEC = PacketCodec.of(
            (payload, buf) -> {
                buf.writeVarInt(payload.members.size());
                for (Member member : payload.members) {
                    Member.CODEC.encode(buf, member);
                }
            },
            buf -> {
                int count = buf.readVarInt();
                if (count < 0 || count > MAX_MEMBERS) {
                    throw new IllegalStateException("HUD de groupe : " + count + " membres, refuse");
                }
                List<Member> members = new ArrayList<>(count);
                for (int i = 0; i < count; i++) {
                    members.add(Member.CODEC.decode(buf));
                }
                return new HudPayload(List.copyOf(members));
            });

    public static HudPayload empty() {
        return new HudPayload(List.of());
    }

    public boolean isEmpty() {
        return this.members.isEmpty();
    }

    @Override
    public Id<? extends CustomPayload> getId() {
        return ID;
    }
}
