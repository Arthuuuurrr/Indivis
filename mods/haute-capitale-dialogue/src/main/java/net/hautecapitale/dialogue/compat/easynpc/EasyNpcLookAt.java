package net.hautecapitale.dialogue.compat.easynpc;

import de.markusbordihn.easynpc.data.objective.ObjectiveDataEntry;
import de.markusbordihn.easynpc.data.objective.ObjectiveType;
import de.markusbordihn.easynpc.entity.easynpc.EasyNPC;
import de.markusbordihn.easynpc.entity.easynpc.data.ObjectiveDataCapable;
import net.hautecapitale.dialogue.HauteCapitaleDialogue;
import net.hautecapitale.dialogue.session.DialogueSession;
import net.minecraft.entity.Entity;
import net.minecraft.server.network.ServerPlayerEntity;

/**
 * Le personnage regarde son interlocuteur — avec le look-at d'Easy NPC.
 *
 * <p>Easy NPC a déjà tout : un objectif « regarde cette entité », un contrôle
 * de tête qui respecte les poses verrouillées (assis, sur véhicule, figé dans
 * une animation) et une remise à zéro. On pose l'objectif à l'ouverture, on le
 * retire à la fermeture, et on ne touche à rien si l'admin en avait déjà
 * configuré un du même type : ce serait le sien qu'on retirerait.
 */
final class EasyNpcLookAt {

    private static final int PRIORITE = 1;

    private EasyNpcLookAt() {
    }

    static void poser(ServerPlayerEntity joueur, DialogueSession session) {
        ObjectiveDataCapable<?> objectifs = objectifs(joueur, session);
        if (objectifs == null || objectifs.hasObjective(ObjectiveType.LOOK_AT_ENTITY_BY_UUID)) {
            return;
        }
        ObjectiveDataEntry entree = new ObjectiveDataEntry(ObjectiveType.LOOK_AT_ENTITY_BY_UUID, PRIORITE);
        entree.setTargetEntityUUID(joueur.getUuid());
        if (objectifs.addOrUpdateCustomObjective(entree)) {
            session.regardPose = true;
        }
    }

    static void retirer(ServerPlayerEntity joueur, DialogueSession session) {
        if (!session.regardPose) {
            return;
        }
        session.regardPose = false;
        ObjectiveDataCapable<?> objectifs = objectifs(joueur, session);
        if (objectifs != null) {
            try {
                objectifs.removeCustomObjective(ObjectiveType.LOOK_AT_ENTITY_BY_UUID);
            } catch (Exception e) {
                HauteCapitaleDialogue.LOGGER.debug("Retrait du regard impossible : {}", e.toString());
            }
        }
    }

    private static ObjectiveDataCapable<?> objectifs(ServerPlayerEntity joueur, DialogueSession session) {
        Entity entite = joueur.getEntityWorld().getEntity(session.pnj);
        if (!(entite instanceof EasyNPC<?> npc)) {
            return null;
        }
        return npc.getEasyNPCObjectiveData();
    }
}
