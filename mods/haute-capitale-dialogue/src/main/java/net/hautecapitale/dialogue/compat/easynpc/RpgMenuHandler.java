package net.hautecapitale.dialogue.compat.easynpc;

import de.markusbordihn.easynpc.data.screen.ScreenData;
import de.markusbordihn.easynpc.entity.easynpc.EasyNPC;
import de.markusbordihn.easynpc.menu.MenuHandlerInterface;
import de.markusbordihn.easynpc.menu.MenuManager;
import de.markusbordihn.easynpc.menu.dialog.DialogMenu;
import de.markusbordihn.easynpc.menu.dialog.DialogMenuHandler;
import net.hautecapitale.dialogue.HauteCapitaleDialogue;
import net.hautecapitale.dialogue.config.DialogueConfig;
import net.hautecapitale.dialogue.session.DialogueManager;
import net.hautecapitale.dialogue.session.DialogueMode;
import net.hautecapitale.dialogue.session.DialogueSession;
import net.minecraft.entity.Entity;
import net.minecraft.screen.NamedScreenHandlerFactory;
import net.minecraft.screen.ScreenHandlerType;
import net.minecraft.server.network.ServerPlayerEntity;

import java.util.UUID;

/**
 * Le gestionnaire de menu qui décide, personnage par personnage.
 *
 * <p>Easy NPC appelle {@code openDialogMenu} pour chaque nœud de dialogue. Pour
 * un personnage éligible, on ouvre une session — ou on la rafraîchit — puis on
 * fait exactement ce que fait Easy NPC, avec <b>notre</b> type de menu. Pour les
 * autres, on délègue au gestionnaire d'origine : comportement identique à un
 * serveur sans ce mod.
 *
 * <p>Le paquet de session part avant le menu : sur la connexion, il arrive donc
 * avant l'écran, et le client sait qu'il est en conversation quand celui-ci
 * s'ouvre.
 */
final class RpgMenuHandler implements MenuHandlerInterface {

    /** Trace de pile a chaque ouverture, en banc d'essai seulement. */
    private static final boolean TRACE = Boolean.getBoolean("hcd.selftest") || Boolean.getBoolean("hcd.devtools");

    private final MenuHandlerInterface origine;

    RpgMenuHandler(MenuHandlerInterface origine) {
        this.origine = origine;
    }

    @Override
    public void openDialogMenu(ServerPlayerEntity joueur, EasyNPC<?> npc, UUID dialogId, int pageIndex) {
        if (TRACE) {
            HauteCapitaleDialogue.LOGGER.info("openDialogMenu pour {} (dialogue {}) — pile :", joueur.getName().getString(), dialogId, new Throwable("trace"));
        }
        Entity entite = npc == null ? null : npc.getEntity();
        if (entite == null || !DialogueConfig.get().actif(entite)) {
            this.origine.openDialogMenu(joueur, npc, dialogId, pageIndex);
            return;
        }

        // Conversation interdite (vehicule en route, politique « refuser ») : ni
        // notre ecran ni celui d'Easy NPC. Le joueur est prevenu.
        var refus = DialogueManager.refus(joueur, entite);
        if (refus.isPresent()) {
            DialogueManager.dire(joueur, refus.get());
            return;
        }

        DialogueSession session = DialogueManager.ouvrir(joueur, entite, DialogueMode.EASYNPC_MENU);
        if (session == null) {
            // Refus (distance) : le dialogue garde l'ecran d'Easy NPC.
            this.origine.openDialogMenu(joueur, npc, dialogId, pageIndex);
            return;
        }
        // Le noeud courant : c'est la qu'on revient apres un commerce ou une cinematique.
        session.dernierDialogue = dialogId;
        session.ecranEtrangerVu = false;

        try {
            ScreenData screenData = DialogMenuHandler.getScreenData(npc, dialogId, pageIndex, joueur);
            NamedScreenHandlerFactory provider =
                    DialogMenuHandler.getMenuProvider(npc, EasyNpcDialogueBridge.MENU_TYPE, screenData);
            MenuManager.openMenu(npc.getEntityUUID(), provider, joueur, screenData.encode());
            EasyNpcSons.jouerReplique(joueur, npc);
        } catch (Exception e) {
            // Une evolution d'Easy NPC ne doit pas rendre ses PNJ muets : on
            // retombe sur son ecran, et on le dit une fois par session.
            HauteCapitaleDialogue.LOGGER.error("Ouverture du menu de dialogue en échec, repli sur Easy NPC.", e);
            this.origine.openDialogMenu(joueur, npc, dialogId, pageIndex);
        }
    }

    @Override
    public ScreenHandlerType<? extends DialogMenu> getDialogMenuType() {
        // Ce qu'un appelant exterieur obtiendrait d'Easy NPC : on ne force pas
        // notre ecran a ceux qui ne passent pas par openDialogMenu.
        return this.origine.getDialogMenuType();
    }
}
