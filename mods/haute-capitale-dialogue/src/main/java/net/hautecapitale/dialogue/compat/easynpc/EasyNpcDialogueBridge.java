package net.hautecapitale.dialogue.compat.easynpc;

import de.markusbordihn.easynpc.api.event.EasyNPCEventRegistry;
import de.markusbordihn.easynpc.data.action.ActionDataEntry;
import de.markusbordihn.easynpc.data.action.ActionDataSet;
import de.markusbordihn.easynpc.data.action.ActionEventType;
import de.markusbordihn.easynpc.data.dialog.DialogDataEntry;
import de.markusbordihn.easynpc.entity.easynpc.EasyNPC;
import de.markusbordihn.easynpc.entity.easynpc.data.ActionEventDataCapable;
import de.markusbordihn.easynpc.entity.easynpc.data.DialogDataCapable;
import de.markusbordihn.easynpc.menu.MenuHandlerInterface;
import de.markusbordihn.easynpc.menu.MenuManager;
import de.markusbordihn.easynpc.menu.dialog.DialogMenu;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;
import net.hautecapitale.dialogue.HauteCapitaleDialogue;
import net.hautecapitale.dialogue.capture.InteractionKind;
import net.hautecapitale.dialogue.config.DialogueConfig;
import net.hautecapitale.dialogue.session.DialogueManager;
import net.hautecapitale.dialogue.session.Rouvreur;
import net.minecraft.entity.Entity;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.resource.featuretoggle.FeatureSet;
import net.minecraft.screen.ScreenHandlerType;
import net.minecraft.server.network.ServerPlayerEntity;

import java.util.UUID;

/**
 * Le pont vers Easy NPC, côté serveur.
 *
 * <p>Seule famille de classes du mod qui référence Easy NPC ; chargée
 * uniquement si le mod est présent (voir {@code HauteCapitaleDialogue}).
 *
 * <p>Deux prises, toutes deux publiques chez Easy NPC :
 * <ul>
 *   <li>un <b>type de menu</b> à nous, dont l'écran client est le nôtre ; côté
 *       serveur c'est un {@code DialogMenu} ordinaire, ce qui laisse à Easy NPC
 *       toute sa validation (session, bouton, conditions) ;</li>
 *   <li>un <b>gestionnaire de menu</b> enveloppant celui d'Easy NPC, qui choisit
 *       personnage par personnage d'ouvrir notre type ou le sien.</li>
 * </ul>
 *
 * <p>Et une lecture : les actions {@code ON_INTERACTION} d'un personnage, pour
 * savoir si son clic ouvre un menu, un commerce, ou lance des commandes.
 */
public final class EasyNpcDialogueBridge {

    /**
     * Notre type de menu. Un {@code DialogMenu} d'Easy NPC, mais un type distinct :
     * {@code HandledScreens} refuse deux écrans pour un même type, et Easy NPC a
     * déjà le sien.
     */
    public static final ScreenHandlerType<DialogMenu> MENU_TYPE = new ScreenHandlerType<>(
            (syncId, inventory) -> new DialogMenu(EasyNpcDialogueBridge.MENU_TYPE, syncId, inventory),
            FeatureSet.empty());

    private static boolean installe;

    private EasyNpcDialogueBridge() {
    }

    public static void init() {
        Registry.register(Registries.SCREEN_HANDLER, HauteCapitaleDialogue.id("dialogue_easy_npc"), MENU_TYPE);

        // Easy NPC pose son gestionnaire pendant son initialisation ; l'ordre des
        // points d'entree n'est pas garanti, on attend donc que tout le monde ait
        // fini. Vaut aussi pour le serveur integre d'une partie solo.
        ServerLifecycleEvents.SERVER_STARTING.register(server -> installer());

        EasyNPCEventRegistry.registerDialogEventListener((npc, joueur, dialogue) -> {
            if (DialogueConfig.get().journaliser && dialogue != null) {
                HauteCapitaleDialogue.LOGGER.debug("Dialogue Easy NPC ouvert : {} → {} pour {}",
                        npc.getEntityUUID(), dialogue.getLabel(), joueur.getName().getString());
            }
        });

        if (DialogueConfig.get().regard_pnj) {
            DialogueManager.onOuverture(EasyNpcLookAt::poser);
            DialogueManager.onFermeture(EasyNpcLookAt::retirer);
        }

        // Ce que le gestionnaire de sessions nous demande : reconnaitre un menu de
        // dialogue parmi les ecrans, et rouvrir une conversation (retour apres un
        // commerce, reprise apres une cinematique).
        DialogueManager.menuDeDialogue(handler -> handler instanceof DialogMenu);
        DialogueManager.descripteur((pnj, dialogue) -> {
            if (!(pnj instanceof EasyNPC<?> npc)) {
                return null;
            }
            StringBuilder sb = new StringBuilder("  Easy NPC : clic → ").append(classerInteraction(pnj));
            DialogDataCapable<?> donnees = npc.getEasyNPCDialogData();
            if (donnees != null && donnees.hasDialog()) {
                sb.append("\n  dialogues :");
                for (DialogDataEntry entree : donnees.getDialogDataSet().getDialogsByLabel()) {
                    boolean courant = dialogue != null && dialogue.equals(entree.getId());
                    sb.append(courant ? " [" : " ").append(entree.getLabel())
                            .append('(').append(entree.getDialogButtons().size()).append(courant ? ")]" : ")");
                }
            } else {
                sb.append("\n  aucun dialogue à menu");
            }
            return sb.toString();
        });
        DialogueManager.rouvreur(new Rouvreur() {
            @Override
            public boolean ouvrirDialogue(ServerPlayerEntity joueur, Entity pnj, UUID dialogue) {
                if (!(pnj instanceof EasyNPC<?> npc)) {
                    return false;
                }
                DialogDataCapable<?> donnees = npc.getEasyNPCDialogData();
                if (donnees == null || !donnees.hasDialog()) {
                    return false;
                }
                UUID id = dialogue;
                if (id == null || !donnees.hasDialog(id)) {
                    DialogDataEntry entree = donnees.getDialogDataSet().getNextAvailableDialog(joueur, npc.getLivingEntity());
                    id = entree == null ? null : entree.getId();
                }
                MenuHandlerInterface gestionnaire = MenuManager.getMenuHandler();
                if (id == null || gestionnaire == null) {
                    return false;
                }
                // Par le gestionnaire, pas par openDialog() : ni compteur d'execution
                // ni evenement « dialogue ouvert » — ce n'est pas une nouvelle ouverture.
                gestionnaire.openDialogMenu(joueur, npc, id, 0);
                return true;
            }

            @Override
            public boolean rejouerInteraction(ServerPlayerEntity joueur, Entity pnj) {
                if (!(pnj instanceof EasyNPC<?> npc)) {
                    return false;
                }
                ActionEventDataCapable<?> actions = npc.getEasyNPCActionEventData();
                if (actions == null || !actions.hasActionEvent(ActionEventType.ON_INTERACTION)) {
                    return false;
                }
                actions.handleActionEvent(ActionEventType.ON_INTERACTION, joueur);
                return true;
            }
        });

        HauteCapitaleDialogue.LOGGER.info("Pont Easy NPC prêt : type de menu {}.", HauteCapitaleDialogue.id("dialogue_easy_npc"));
    }

    private static void installer() {
        MenuHandlerInterface courant = MenuManager.getMenuHandler();
        if (courant instanceof RpgMenuHandler) {
            return;
        }
        if (courant == null) {
            HauteCapitaleDialogue.LOGGER.warn(
                    "Easy NPC n'a enregistré aucun gestionnaire de menu : ses dialogues garderont leur écran.");
            return;
        }
        MenuManager.registerMenuHandler(new RpgMenuHandler(courant));
        installe = true;
        HauteCapitaleDialogue.LOGGER.info("Gestionnaire de menu Easy NPC enveloppé ({}).",
                courant.getClass().getSimpleName());
    }

    public static boolean installe() {
        return installe;
    }

    public static boolean estEasyNpc(Entity entity) {
        return entity instanceof EasyNPC<?>;
    }

    /**
     * Ce que le clic sur ce personnage déclenche, d'après ses actions {@code ON_INTERACTION}.
     *
     * <p>Priorité : un dialogue à menu l'emporte (le gestionnaire de menu s'en
     * occupe), puis le commerce, puis les commandes, puis les actions d'autres mods.
     */
    public static InteractionKind classerInteraction(Entity entity) {
        if (!(entity instanceof EasyNPC<?> npc)) {
            return InteractionKind.NOT_EASYNPC;
        }
        ActionEventDataCapable<?> donnees = npc.getEasyNPCActionEventData();
        ActionDataSet actions = donnees == null ? null : donnees.getActionDataSet(ActionEventType.ON_INTERACTION);
        if (actions == null || actions.isEmpty()) {
            return InteractionKind.NONE;
        }
        InteractionKind genre = InteractionKind.NONE;
        for (ActionDataEntry action : actions.getEntries()) {
            if (action == null || action.actionDataType() == null) {
                continue;
            }
            switch (action.actionDataType()) {
                case OPEN_DEFAULT_DIALOG, OPEN_NAMED_DIALOG, OPEN_NAMED_DIALOG_CONDITIONAL -> {
                    return InteractionKind.MENU;
                }
                case OPEN_TRADING_SCREEN -> genre = InteractionKind.TRADE;
                case COMMAND -> {
                    if (genre != InteractionKind.TRADE) {
                        genre = InteractionKind.COMMAND;
                    }
                }
                case CUSTOM -> {
                    if (genre == InteractionKind.NONE) {
                        genre = InteractionKind.CUSTOM;
                    }
                }
                default -> {
                }
            }
        }
        return genre;
    }
}
