package net.hautecapitale.dialogue.network;

import net.fabricmc.fabric.api.networking.v1.PayloadTypeRegistry;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.hautecapitale.dialogue.HauteCapitaleDialogue;
import net.hautecapitale.dialogue.session.CameraProfile;
import net.hautecapitale.dialogue.session.CloseReason;
import net.hautecapitale.dialogue.session.DialogueManager;
import net.hautecapitale.dialogue.session.DialogueMode;
import net.hautecapitale.dialogue.session.DialogueSession;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.packet.CustomPayload;
import net.minecraft.server.network.ServerPlayerEntity;

import java.util.UUID;

/**
 * Les quatre paquets du mod. Pas un de plus.
 *
 * <p>Aucune réponse de dialogue ne transite par ici : celles d'Easy NPC partent
 * par ses propres paquets, que son serveur revalide ; celles des datapacks par
 * la commande vanilla d'un clic dans le chat. Ce mod n'ouvre donc aucun canal
 * par lequel un client pourrait choisir une réponse verrouillée.
 */
public final class DialogueNetwork {

    /** Serveur → client : « tu entres en conversation avec ce personnage, voici comment le cadrer. » */
    public record SessionOpen(int sessionId, int npcEntityId, UUID npcUuid, String npcName,
                              DialogueMode mode, CameraProfile profile, int flags) implements CustomPayload {
        public static final CustomPayload.Id<SessionOpen> ID =
                new CustomPayload.Id<>(HauteCapitaleDialogue.id("session_open"));
        public static final PacketCodec<RegistryByteBuf, SessionOpen> CODEC = PacketCodec.ofStatic(
                (buf, p) -> {
                    buf.writeVarInt(p.sessionId);
                    buf.writeVarInt(p.npcEntityId);
                    buf.writeUuid(p.npcUuid);
                    buf.writeString(p.npcName);
                    buf.writeEnumConstant(p.mode);
                    p.profile.write(buf);
                    buf.writeVarInt(p.flags);
                },
                buf -> new SessionOpen(buf.readVarInt(), buf.readVarInt(), buf.readUuid(),
                        buf.readString(), buf.readEnumConstant(DialogueMode.class),
                        CameraProfile.read(buf), buf.readVarInt()));

        @Override
        public CustomPayload.Id<? extends CustomPayload> getId() {
            return ID;
        }
    }

    /** Serveur → client : « la conversation est finie, et voici pourquoi. » */
    public record SessionClose(int sessionId, CloseReason reason) implements CustomPayload {
        public static final CustomPayload.Id<SessionClose> ID =
                new CustomPayload.Id<>(HauteCapitaleDialogue.id("session_close"));
        public static final PacketCodec<RegistryByteBuf, SessionClose> CODEC = PacketCodec.ofStatic(
                (buf, p) -> {
                    buf.writeVarInt(p.sessionId);
                    buf.writeEnumConstant(p.reason);
                },
                buf -> new SessionClose(buf.readVarInt(), buf.readEnumConstant(CloseReason.class)));

        @Override
        public CustomPayload.Id<? extends CustomPayload> getId() {
            return ID;
        }
    }

    /** Client → serveur : « je voudrais arrêter. » Le serveur reste libre de refuser. */
    public record SessionEnd(int sessionId, CloseReason reason) implements CustomPayload {
        public static final CustomPayload.Id<SessionEnd> ID =
                new CustomPayload.Id<>(HauteCapitaleDialogue.id("session_end"));
        public static final PacketCodec<RegistryByteBuf, SessionEnd> CODEC = PacketCodec.ofStatic(
                (buf, p) -> {
                    buf.writeVarInt(p.sessionId);
                    buf.writeEnumConstant(p.reason);
                },
                buf -> new SessionEnd(buf.readVarInt(), buf.readEnumConstant(CloseReason.class)));

        @Override
        public CustomPayload.Id<? extends CustomPayload> getId() {
            return ID;
        }
    }

    /**
     * Client → serveur : « la cinématique qui a interrompu cette conversation est
     * finie, reprenez-la. » Le serveur ne reprend que ce qu'il a lui-même mis en
     * attente, et seulement si le personnage est encore là.
     */
    public record Reprise(int sessionId) implements CustomPayload {
        public static final CustomPayload.Id<Reprise> ID =
                new CustomPayload.Id<>(HauteCapitaleDialogue.id("reprise"));
        public static final PacketCodec<RegistryByteBuf, Reprise> CODEC = PacketCodec.ofStatic(
                (buf, p) -> buf.writeVarInt(p.sessionId),
                buf -> new Reprise(buf.readVarInt()));

        @Override
        public CustomPayload.Id<? extends CustomPayload> getId() {
            return ID;
        }
    }

    private DialogueNetwork() {
    }

    public static void init() {
        PayloadTypeRegistry.playS2C().register(SessionOpen.ID, SessionOpen.CODEC);
        PayloadTypeRegistry.playS2C().register(SessionClose.ID, SessionClose.CODEC);
        PayloadTypeRegistry.playC2S().register(SessionEnd.ID, SessionEnd.CODEC);
        PayloadTypeRegistry.playC2S().register(Reprise.ID, Reprise.CODEC);

        ServerPlayNetworking.registerGlobalReceiver(SessionEnd.ID,
                (payload, context) -> context.server().execute(
                        () -> DialogueManager.onFinClient(context.player(), payload.sessionId(), payload.reason())));
        ServerPlayNetworking.registerGlobalReceiver(Reprise.ID,
                (payload, context) -> context.server().execute(
                        () -> DialogueManager.onRepriseClient(context.player(), payload.sessionId())));
    }

    public static void envoyerOuverture(ServerPlayerEntity joueur, DialogueSession s) {
        if (joueur.networkHandler == null) {
            return;
        }
        ServerPlayNetworking.send(joueur, new SessionOpen(
                s.id, s.pnjEntityId, s.pnj, s.pnjNom, s.mode, s.profil, s.flags));
    }

    public static void envoyerFermeture(ServerPlayerEntity joueur, DialogueSession s, CloseReason raison) {
        if (joueur.networkHandler == null) {
            return;
        }
        ServerPlayNetworking.send(joueur, new SessionClose(s.id, raison));
    }
}
