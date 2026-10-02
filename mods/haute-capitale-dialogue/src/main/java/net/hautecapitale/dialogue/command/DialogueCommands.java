package net.hautecapitale.dialogue.command;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.StringArgumentType;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.hautecapitale.dialogue.config.DialogueConfig;
import net.hautecapitale.dialogue.session.CloseReason;
import net.hautecapitale.dialogue.session.DialogueManager;
import net.hautecapitale.dialogue.session.DialogueSession;
import net.hautecapitale.dialogue.session.SessionFlags;
import net.minecraft.command.argument.EntityArgumentType;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Vec3d;

import java.util.List;
import java.util.Locale;

/**
 * Les commandes d'administration.
 *
 * <ul>
 *   <li>{@code /dialogue debug} : ma session, le personnage, ce que son clic
 *       déclenche, le nœud courant, la caméra vue du serveur ;</li>
 *   <li>{@code /dialogue sessions}, {@code /dialogue fermer <joueur>} ;</li>
 *   <li>{@code /dialogue pnj on|off|effacer} : tags sur le personnage visé ;</li>
 *   <li>{@code /dialogue pnj voir|set <clé> <valeur>|oublier} : la surcharge de
 *       configuration du personnage visé, sans toucher au fichier ;</li>
 *   <li>{@code /dialogue recharger}.</li>
 * </ul>
 */
public final class DialogueCommands {

    private DialogueCommands() {
    }

    public static void init() {
        CommandRegistrationCallback.EVENT.register(
                (dispatcher, registryAccess, environment) -> register(dispatcher));
    }

    private static void register(CommandDispatcher<ServerCommandSource> dispatcher) {
        dispatcher.register(CommandManager.literal("dialogue")
                .then(CommandManager.literal("debug").executes(ctx -> debug(ctx.getSource())))
                .then(CommandManager.literal("sessions")
                        .requires(CommandManager.requirePermissionLevel(CommandManager.GAMEMASTERS_CHECK))
                        .executes(ctx -> sessions(ctx.getSource())))
                .then(CommandManager.literal("fermer")
                        .requires(CommandManager.requirePermissionLevel(CommandManager.GAMEMASTERS_CHECK))
                        .then(CommandManager.argument("joueur", EntityArgumentType.player())
                                .executes(ctx -> fermer(ctx.getSource(),
                                        EntityArgumentType.getPlayer(ctx, "joueur")))))
                .then(CommandManager.literal("pnj")
                        .requires(CommandManager.requirePermissionLevel(CommandManager.GAMEMASTERS_CHECK))
                        .then(CommandManager.literal("on").executes(ctx -> marquer(ctx.getSource(), true)))
                        .then(CommandManager.literal("off").executes(ctx -> marquer(ctx.getSource(), false)))
                        .then(CommandManager.literal("effacer").executes(ctx -> effacer(ctx.getSource())))
                        .then(CommandManager.literal("voir").executes(ctx -> voir(ctx.getSource())))
                        .then(CommandManager.literal("oublier").executes(ctx -> oublier(ctx.getSource())))
                        .then(CommandManager.literal("set")
                                .then(CommandManager.argument("cle", StringArgumentType.word())
                                        .then(CommandManager.argument("valeur", StringArgumentType.word())
                                                .executes(ctx -> poser(ctx.getSource(),
                                                        StringArgumentType.getString(ctx, "cle"),
                                                        StringArgumentType.getString(ctx, "valeur")))))))
                .then(CommandManager.literal("recharger")
                        .requires(CommandManager.requirePermissionLevel(CommandManager.GAMEMASTERS_CHECK))
                        .executes(ctx -> recharger(ctx.getSource()))));
    }

    private static int debug(ServerCommandSource source) {
        ServerPlayerEntity joueur = source.getPlayer();
        if (joueur == null) {
            source.sendError(Text.literal("Un joueur est requis."));
            return 0;
        }
        DialogueSession s = DialogueManager.session(joueur).orElse(null);
        if (s == null) {
            source.sendFeedback(() -> Text.literal("Aucune session de dialogue.").formatted(Formatting.GRAY), false);
            Entity vise = personnageVise(source, false);
            if (vise != null) {
                source.sendFeedback(() -> Text.literal(fiche(vise, null)), false);
            }
            return 1;
        }
        Entity pnj = joueur.getEntityWorld().getEntity(s.pnj);
        double distance = pnj == null ? -1 : Math.sqrt(joueur.squaredDistanceTo(pnj));
        long age = joueur.getEntityWorld().getServer().getTicks() - s.ouvertureTick;
        String ecran = joueur.currentScreenHandler == joueur.playerScreenHandler
                ? "aucun" : joueur.currentScreenHandler.getClass().getSimpleName();
        String texte = String.format(Locale.ROOT,
                "Session #%d  mode %s\n  PNJ %s (%s, id %d)\n  distance %.2f  âge %d ticks  écran %s (sans écran depuis %d ticks)\n"
                        + "  drapeaux %s  regard %s  verrou %s\n  véhicule %s  nœud %s",
                s.id, s.mode, s.pnjNom, s.pnj, s.pnjEntityId, distance, age, ecran, s.ticksSansEcran,
                SessionFlags.decrire(s.flags), s.regardPose, s.verrouPose,
                s.mouvement.enMouvement() ? "en route" : "à l'arrêt",
                s.dernierDialogue == null ? "-" : s.dernierDialogue);
        source.sendFeedback(() -> Text.literal(texte), false);
        if (pnj != null) {
            source.sendFeedback(() -> Text.literal(fiche(pnj, s.dernierDialogue)), false);
        }
        return 1;
    }

    /** Tout ce qu'on sait d'un personnage : tags, surcharge, et ce que le pont en dit. */
    private static String fiche(Entity pnj, java.util.UUID dialogue) {
        DialogueConfig cfg = DialogueConfig.get();
        StringBuilder sb = new StringBuilder();
        sb.append(pnj.getDisplayName().getString()).append(" (").append(pnj.getUuid()).append(")\n  éligible ")
                .append(cfg.actif(pnj) ? "oui" : "non").append("  capture ").append(cfg.capture(pnj) ? "oui" : "non")
                .append("  tags ").append(pnj.getCommandTags())
                .append("\n  drapeaux ").append(SessionFlags.decrire(cfg.flags(pnj)))
                .append("\n  surcharge : ").append(DialogueConfig.decrire(cfg.surchargeDe(pnj.getUuid())));
        String pont = DialogueManager.decrire(pnj, dialogue);
        if (pont != null) {
            sb.append('\n').append(pont);
        }
        return sb.toString();
    }

    private static int sessions(ServerCommandSource source) {
        var toutes = DialogueManager.toutes();
        if (toutes.isEmpty()) {
            source.sendFeedback(() -> Text.literal("Aucune session ouverte.").formatted(Formatting.GRAY), false);
            return 0;
        }
        StringBuilder sb = new StringBuilder(toutes.size() + " session(s) :");
        for (DialogueSession s : toutes) {
            ServerPlayerEntity j = source.getServer().getPlayerManager().getPlayer(s.joueur);
            sb.append("\n  #").append(s.id).append("  ")
                    .append(j == null ? s.joueur.toString() : j.getName().getString())
                    .append(" ↔ ").append(s.pnjNom).append("  ").append(s.mode);
        }
        source.sendFeedback(() -> Text.literal(sb.toString()), false);
        return toutes.size();
    }

    private static int fermer(ServerCommandSource source, ServerPlayerEntity joueur) {
        if (!DialogueManager.enSession(joueur)) {
            source.sendError(Text.literal(joueur.getName().getString() + " n'est pas en dialogue."));
            return 0;
        }
        DialogueManager.fermer(joueur, CloseReason.ADMIN);
        source.sendFeedback(() -> Text.literal("Session de " + joueur.getName().getString() + " fermée."), true);
        return 1;
    }

    private static int marquer(ServerCommandSource source, boolean actif) {
        Entity cible = personnageVise(source, true);
        if (cible == null) {
            return 0;
        }
        DialogueConfig cfg = DialogueConfig.get();
        cible.removeCommandTag(cfg.tag_actif);
        cible.removeCommandTag(cfg.tag_inactif);
        cible.addCommandTag(actif ? cfg.tag_actif : cfg.tag_inactif);
        source.sendFeedback(() -> Text.literal(cible.getDisplayName().getString()
                + " : dialogue immersif " + (actif ? "ACTIVÉ" : "DÉSACTIVÉ")
                + " (tag " + (actif ? cfg.tag_actif : cfg.tag_inactif) + ")"), true);
        return 1;
    }

    private static int effacer(ServerCommandSource source) {
        Entity cible = personnageVise(source, true);
        if (cible == null) {
            return 0;
        }
        DialogueConfig cfg = DialogueConfig.get();
        cible.removeCommandTag(cfg.tag_actif);
        cible.removeCommandTag(cfg.tag_inactif);
        source.sendFeedback(() -> Text.literal(cible.getDisplayName().getString()
                + " : tags de dialogue retirés, retour au défaut ("
                + (cfg.actif_par_defaut ? "actif" : "inactif") + ")"), true);
        return 1;
    }

    private static int voir(ServerCommandSource source) {
        Entity cible = personnageVise(source, true);
        if (cible == null) {
            return 0;
        }
        source.sendFeedback(() -> Text.literal(fiche(cible, null)), false);
        return 1;
    }

    private static int poser(ServerCommandSource source, String cle, String valeur) {
        Entity cible = personnageVise(source, true);
        if (cible == null) {
            return 0;
        }
        DialogueConfig cfg = DialogueConfig.get();
        DialogueConfig.Pnj p = cfg.surchargeOuCreer(cible.getUuid());
        String erreur = DialogueConfig.poser(p, cle, valeur);
        if (erreur != null) {
            source.sendError(Text.literal(erreur));
            return 0;
        }
        DialogueConfig.save();
        source.sendFeedback(() -> Text.literal(cible.getDisplayName().getString() + " : " + cle + " = " + valeur
                + "\n  surcharge : " + DialogueConfig.decrire(p)), true);
        return 1;
    }

    private static int oublier(ServerCommandSource source) {
        Entity cible = personnageVise(source, true);
        if (cible == null) {
            return 0;
        }
        boolean retiree = DialogueConfig.get().oublier(cible.getUuid());
        if (retiree) {
            DialogueConfig.save();
        }
        source.sendFeedback(() -> Text.literal(cible.getDisplayName().getString()
                + (retiree ? " : surcharge retirée, retour au défaut." : " : aucune surcharge.")), true);
        return 1;
    }

    private static int recharger(ServerCommandSource source) {
        DialogueConfig.load();
        source.sendFeedback(() -> Text.literal("Configuration du dialogue rechargée."), true);
        return 1;
    }

    /** Le personnage vivant le plus proche dans le cône de regard de la source, à 6 blocs. */
    private static Entity personnageVise(ServerCommandSource source, boolean sinonErreur) {
        Entity origine = source.getEntity();
        if (origine == null) {
            if (sinonErreur) {
                source.sendError(Text.literal("Cette commande se lance en regardant un personnage."));
            }
            return null;
        }
        Vec3d oeil = origine.getEyePos();
        Vec3d regard = origine.getRotationVec(1.0f);
        Box zone = origine.getBoundingBox().expand(6.0);
        List<Entity> candidats = origine.getEntityWorld().getOtherEntities(origine, zone,
                e -> e instanceof LivingEntity && e.isAlive() && !(e instanceof PlayerEntity));
        Entity meilleur = null;
        double meilleurScore = 0.0;
        for (Entity e : candidats) {
            Vec3d vers = e.getEyePos().subtract(oeil);
            double distance = vers.length();
            if (distance < 1.0e-3 || distance > 6.0) {
                continue;
            }
            double cos = vers.normalize().dotProduct(regard);
            if (cos < 0.85) {
                continue;
            }
            double score = cos / distance;
            if (score > meilleurScore) {
                meilleurScore = score;
                meilleur = e;
            }
        }
        if (meilleur == null && sinonErreur) {
            source.sendError(Text.literal("Aucun personnage dans votre regard (6 blocs)."));
        }
        return meilleur;
    }
}
