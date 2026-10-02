package net.hautecapitale.dialogue.capture;

import net.fabricmc.fabric.api.event.Event;
import net.fabricmc.fabric.api.event.player.UseEntityCallback;
import net.fabricmc.loader.api.FabricLoader;
import net.hautecapitale.dialogue.HauteCapitaleDialogue;
import net.hautecapitale.dialogue.compat.easynpc.EasyNpcDialogueBridge;
import net.hautecapitale.dialogue.config.DialogueConfig;
import net.hautecapitale.dialogue.session.DialogueManager;
import net.hautecapitale.dialogue.session.DialogueMode;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.registry.Registries;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.Identifier;
import net.minecraft.util.hit.EntityHitResult;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

/**
 * Ouvre une session de <i>capture</i> au moment du clic, avant tout le monde.
 *
 * <p>Les dialogues de datapack ne passent par aucun menu : le clic lance des
 * commandes, les commandes écrivent dans le chat. Pour que le client sache que
 * ce qui va arriver dans son chat est une conversation, la session doit être
 * ouverte — et son paquet parti — avant que ces commandes ne s'exécutent. D'où
 * une phase Fabric propre, ordonnée avant la phase par défaut où Easy NPC,
 * les ferries et les dirigeables écoutent le même clic.
 *
 * <p>On ne consomme jamais le clic : on note, on laisse passer.
 */
public final class CaptureOpener {

    public static final Identifier PHASE_AVANT = HauteCapitaleDialogue.id("avant");

    private static final boolean TRACE = Boolean.getBoolean("hcd.selftest") || Boolean.getBoolean("hcd.devtools");

    private CaptureOpener() {
    }

    public static void init() {
        UseEntityCallback.EVENT.addPhaseOrdering(PHASE_AVANT, Event.DEFAULT_PHASE);
        UseEntityCallback.EVENT.register(PHASE_AVANT, CaptureOpener::onUse);
        HauteCapitaleDialogue.LOGGER.info("Capture des dialogues de chat : ouverture sur clic armée (phase {}).", PHASE_AVANT);
    }

    private static ActionResult onUse(PlayerEntity joueur, World monde, Hand main, Entity cible,
                                      @Nullable EntityHitResult hit) {
        if (monde.isClient() || main != Hand.MAIN_HAND || !(joueur instanceof ServerPlayerEntity serveur)) {
            return ActionResult.PASS;
        }
        DialogueConfig cfg = DialogueConfig.get();
        if (!cfg.capture_active || joueur.isSpectator() || !(cible instanceof LivingEntity) || !cible.isAlive()) {
            return ActionResult.PASS;
        }
        // Les outils d'administration d'Easy NPC (baguette, deplacement, presets)
        // et le clic accroupi restent ce qu'ils sont : pas de conversation.
        if (joueur.isSneaking()
                || "easy_npc".equals(Registries.ITEM.getId(joueur.getMainHandStack().getItem()).getNamespace())) {
            return ActionResult.PASS;
        }

        InteractionKind genre = classer(cible);
        boolean ouvrir = switch (genre) {
            case MENU, TRADE -> false;
            case COMMAND -> cfg.capture(cible);
            case CUSTOM, NONE, NOT_EASYNPC -> cfg.captureExplicite(cible);
        };
        if (TRACE) {
            HauteCapitaleDialogue.LOGGER.info("[capture] clic de {} sur {} : {} → {}", joueur.getName().getString(),
                    cible.getName().getString(), genre, ouvrir ? "capture" : "laisser");
        }
        if (ouvrir) {
            // Conversation interdite (vehicule en route, politique « refuser ») :
            // le clic est consomme, les commandes du personnage ne partent pas.
            var refus = DialogueManager.refus(serveur, cible);
            if (refus.isPresent()) {
                DialogueManager.dire(serveur, refus.get());
                return ActionResult.FAIL;
            }
            DialogueManager.ouvrir(serveur, cible, DialogueMode.CAPTURE);
        }
        return ActionResult.PASS;
    }

    /** Sans Easy NPC, tout est « autre » ; avec, le pont regarde les actions du personnage. */
    static InteractionKind classer(Entity cible) {
        if (!FabricLoader.getInstance().isModLoaded("easy_npc")) {
            return InteractionKind.NOT_EASYNPC;
        }
        try {
            return EasyNpcDialogueBridge.classerInteraction(cible);
        } catch (Throwable t) {
            HauteCapitaleDialogue.LOGGER.warn("Classification de l'interaction impossible pour {} : {}",
                    cible.getName().getString(), t.toString());
            return InteractionKind.NOT_EASYNPC;
        }
    }
}
