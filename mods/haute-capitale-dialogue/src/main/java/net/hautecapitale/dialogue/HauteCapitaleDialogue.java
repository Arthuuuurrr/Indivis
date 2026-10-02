package net.hautecapitale.dialogue;

import net.fabricmc.api.ModInitializer;
import net.fabricmc.loader.api.FabricLoader;
import net.hautecapitale.dialogue.capture.CaptureOpener;
import net.hautecapitale.dialogue.command.DialogueCommands;
import net.hautecapitale.dialogue.compat.easynpc.EasyNpcDialogueBridge;
import net.hautecapitale.dialogue.compat.spellengine.SpellCastGuard;
import net.hautecapitale.dialogue.config.DialogueConfig;
import net.hautecapitale.dialogue.guard.DialogueGuards;
import net.hautecapitale.dialogue.network.DialogueNetwork;
import net.hautecapitale.dialogue.session.DialogueManager;
import net.minecraft.util.Identifier;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Haute Capitale — Dialogue.
 *
 * <p>Une couche de <b>présentation</b> au-dessus d'Easy NPC, qui reste le cœur
 * des PNJ : identité, quêtes, dialogues, actions, commerce. Ce mod n'enregistre
 * aucun PNJ, aucune donnée de dialogue et aucun mixin. Il tient une
 * <i>session</i> par joueur — « il parle à ce personnage » —, la fait respecter
 * (distance, mort, téléportation, cinématique) et laisse le client cadrer la
 * caméra et dessiner le texte.
 *
 * <p>Côté serveur, tout est décidé ici ; côté client, tout n'est qu'affiché.
 * Les réponses d'un dialogue Easy NPC repartent par les paquets d'Easy NPC, qui
 * les revalide ; ce mod n'ouvre aucun canal par lequel un client pourrait
 * choisir une réponse qu'on lui a verrouillée.
 */
public class HauteCapitaleDialogue implements ModInitializer {

    public static final String MOD_ID = "haute_capitale_dialogue";
    public static final Logger LOGGER = LoggerFactory.getLogger("Haute Capitale — Dialogue");

    public static Identifier id(String path) {
        return Identifier.of(MOD_ID, path);
    }

    @Override
    public void onInitialize() {
        DialogueConfig.load();
        DialogueNetwork.init();
        DialogueManager.init();
        DialogueGuards.init();
        CaptureOpener.init();
        DialogueCommands.init();
        net.hautecapitale.dialogue.dev.DevTools.init();

        // Easy NPC est la raison d'être du mod, mais il reste facultatif : la
        // classe qui le référence n'est chargée que s'il est présent, sinon un
        // serveur sans Easy NPC ne démarrerait pas (NoClassDefFoundError).
        if (FabricLoader.getInstance().isModLoaded("easy_npc")) {
            EasyNpcDialogueBridge.init();
        } else {
            LOGGER.warn("Easy NPC absent : aucun dialogue de PNJ ne sera présenté par ce mod.");
        }

        if (FabricLoader.getInstance().isModLoaded("spell_engine")) {
            SpellCastGuard.init();
        }

        LOGGER.info("Dialogue RPG initialisé (sessions, réseau, gardes).");
    }
}
