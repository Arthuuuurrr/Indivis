package net.hautecapitale.dialogue.dev;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.arguments.StringArgumentType;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.fabricmc.fabric.api.networking.v1.PayloadTypeRegistry;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.hautecapitale.dialogue.HauteCapitaleDialogue;
import net.minecraft.command.argument.EntityArgumentType;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.packet.CustomPayload;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;

/**
 * Outils de banc d'essai : le serveur ordonne à un client de cliquer, de
 * choisir, de fermer, ou de dire où il en est.
 *
 * <p>Sur cette machine, injecter des touches dans une fenêtre est interdit — le
 * joueur y a sa propre partie ouverte. Les clients de test reçoivent donc
 * leurs ordres par le réseau, depuis une commande RCON, et exécutent le
 * <b>vrai</b> chemin (paquet d'interaction, écran, réponse).
 *
 * <p>Inerte en production : rien n'est enregistré sans la propriété système
 * {@code hcd.devtools}, et le type de paquet n'existe alors ni côté serveur ni
 * côté client.
 */
public final class DevTools {

    public static final String PROPRIETE = "hcd.devtools";

    /** Serveur → client : « fais ceci ». */
    public record DevOrder(String op, int n, String texte) implements CustomPayload {
        public static final CustomPayload.Id<DevOrder> ID = new CustomPayload.Id<>(HauteCapitaleDialogue.id("dev_order"));
        public static final PacketCodec<RegistryByteBuf, DevOrder> CODEC = PacketCodec.ofStatic(
                (buf, p) -> {
                    buf.writeString(p.op);
                    buf.writeVarInt(p.n);
                    buf.writeString(p.texte);
                },
                buf -> new DevOrder(buf.readString(), buf.readVarInt(), buf.readString()));

        @Override
        public CustomPayload.Id<? extends CustomPayload> getId() {
            return ID;
        }
    }

    private DevTools() {
    }

    public static boolean actifs() {
        return Boolean.getBoolean(PROPRIETE);
    }

    public static void init() {
        if (!actifs()) {
            return;
        }
        PayloadTypeRegistry.playS2C().register(DevOrder.ID, DevOrder.CODEC);
        CommandRegistrationCallback.EVENT.register((dispatcher, registryAccess, environment) -> register(dispatcher));
        HauteCapitaleDialogue.LOGGER.warn("Outils de banc d'essai ACTIFS (-D{}) — jamais en production.", PROPRIETE);
    }

    private static void register(CommandDispatcher<ServerCommandSource> dispatcher) {
        dispatcher.register(CommandManager.literal("dialogue").then(CommandManager.literal("dev")
                .requires(CommandManager.requirePermissionLevel(CommandManager.GAMEMASTERS_CHECK))
                .then(CommandManager.literal("interagir")
                        .then(CommandManager.argument("joueur", EntityArgumentType.player())
                                .then(CommandManager.argument("pnj", StringArgumentType.greedyString())
                                        .executes(ctx -> ordre(ctx.getSource(), EntityArgumentType.getPlayer(ctx, "joueur"),
                                                "interagir", 0, StringArgumentType.getString(ctx, "pnj"))))))
                .then(CommandManager.literal("choisir")
                        .then(CommandManager.argument("joueur", EntityArgumentType.player())
                                .then(CommandManager.argument("n", IntegerArgumentType.integer(0, 8))
                                        .executes(ctx -> ordre(ctx.getSource(), EntityArgumentType.getPlayer(ctx, "joueur"),
                                                "choisir", IntegerArgumentType.getInteger(ctx, "n"), "")))))
                .then(CommandManager.literal("fermer")
                        .then(CommandManager.argument("joueur", EntityArgumentType.player())
                                .executes(ctx -> ordre(ctx.getSource(), EntityArgumentType.getPlayer(ctx, "joueur"), "fermer", 0, ""))))
                .then(CommandManager.literal("etat")
                        .then(CommandManager.argument("joueur", EntityArgumentType.player())
                                .executes(ctx -> ordre(ctx.getSource(), EntityArgumentType.getPlayer(ctx, "joueur"), "etat", 0, ""))))
                .then(CommandManager.literal("perspective")
                        .then(CommandManager.argument("joueur", EntityArgumentType.player())
                                .then(CommandManager.argument("mode", StringArgumentType.word())
                                        .executes(ctx -> ordre(ctx.getSource(), EntityArgumentType.getPlayer(ctx, "joueur"),
                                                "perspective", 0, StringArgumentType.getString(ctx, "mode"))))))
                .then(CommandManager.literal("cinematique")
                        .then(CommandManager.argument("joueur", EntityArgumentType.player())
                                .then(CommandManager.argument("mode", StringArgumentType.word())
                                        .executes(ctx -> ordre(ctx.getSource(), EntityArgumentType.getPlayer(ctx, "joueur"),
                                                "cinematique", 0, StringArgumentType.getString(ctx, "mode"))))))));
    }

    private static int ordre(ServerCommandSource source, ServerPlayerEntity joueur, String op, int n, String texte) {
        ServerPlayNetworking.send(joueur, new DevOrder(op, n, texte));
        source.sendFeedback(() -> Text.literal("ordre « " + op + " » envoyé à " + joueur.getName().getString()), false);
        return 1;
    }
}
