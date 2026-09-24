package net.hautecapitale.metiers.npc;

import net.fabricmc.loader.api.FabricLoader;
import net.hautecapitale.metiers.HauteCapitaleMetiers;
import net.minecraft.util.Identifier;

/**
 * Le pont vers Easy NPC.
 *
 * <p>Easy NPC reste un mod tiers : il n'est ni modifié, ni recopié, ni requis.
 * On se contente d'enregistrer une action personnalisée dans son registre
 * public, ce qui ne demande aucun mixin.
 *
 * <p>Cette classe ne touche aucune classe d'Easy NPC : elle vérifie d'abord que
 * le mod est là, et seulement ensuite charge {@link EasyNpcProfessionAction},
 * qui, lui, en dépend. Sans cette séparation, un serveur sans Easy NPC ne
 * démarrerait pas — la machine virtuelle refuserait de charger une classe dont
 * les types n'existent pas.
 *
 * <p>Côté joueur : dans l'éditeur d'actions d'Easy NPC, événement
 * {@code ON_INTERACTION}, type {@code CUSTOM}, commande
 * {@code haute_capitale_metiers:profession forgeron}.
 */
public final class EasyNpcBridge {

    /** Le mod dont on dépend, s'il est présent. */
    public static final String EASY_NPC = "easy_npc";

    /** L'action à écrire dans l'éditeur d'Easy NPC. */
    public static final Identifier ACTION = HauteCapitaleMetiers.id("profession");

    private static boolean active;

    private EasyNpcBridge() {
    }

    public static void init() {
        if (!FabricLoader.getInstance().isModLoaded(EASY_NPC)) {
            HauteCapitaleMetiers.LOGGER.info(
                    "Easy NPC absent : les interfaces de métier restent accessibles par « /metiers atelier ».");
            return;
        }

        try {
            EasyNpcProfessionAction.register();
            active = true;
            HauteCapitaleMetiers.LOGGER.info(
                    "Action Easy NPC « {} <role> » enregistrée.", ACTION);
        } catch (Throwable error) {
            // Une évolution d'Easy NPC ne doit pas empêcher le serveur de
            // démarrer : le repli par commande reste opérationnel.
            HauteCapitaleMetiers.LOGGER.error(
                    "Easy NPC est présent mais son API d'actions n'a pas pu être utilisée. "
                            + "Les PNJ n'ouvriront pas d'interface ; « /metiers atelier » fonctionne toujours.",
                    error);
        }
    }

    /** L'action est-elle réellement branchée sur Easy NPC ? */
    public static boolean isActive() {
        return active;
    }
}
