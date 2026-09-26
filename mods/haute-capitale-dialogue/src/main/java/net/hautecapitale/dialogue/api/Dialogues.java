package net.hautecapitale.dialogue.api;

import net.hautecapitale.dialogue.session.CloseReason;
import net.hautecapitale.dialogue.session.DialogueManager;
import net.hautecapitale.dialogue.session.DialogueSession;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.server.network.ServerPlayerEntity;

import java.util.Optional;
import java.util.UUID;
import java.util.function.Function;

/**
 * Ce que les autres mods de Haute Capitale ont le droit de demander.
 *
 * <p>Côté serveur uniquement. Métiers, Party ou un futur système de donjon
 * peuvent savoir si un joueur est en conversation, avec qui, et fermer la
 * conversation avant une téléportation collective.
 */
public final class Dialogues {

    private Dialogues() {
    }

    /** Le joueur est-il en conversation ? */
    public static boolean enConversation(PlayerEntity joueur) {
        return DialogueManager.enSession(joueur);
    }

    /** L'UUID du personnage avec qui le joueur parle. */
    public static Optional<UUID> interlocuteur(PlayerEntity joueur) {
        return DialogueManager.session(joueur).map(s -> s.pnj);
    }

    /** L'identifiant de la session en cours, pour un journal ou un test. */
    public static Optional<Integer> session(PlayerEntity joueur) {
        return DialogueManager.session(joueur).map(s -> s.id);
    }

    /** Ferme la conversation du joueur, s'il en a une. */
    public static void fermer(ServerPlayerEntity joueur) {
        DialogueManager.fermer(joueur, CloseReason.FERMEE);
    }

    /**
     * Un mod de transport dit si le véhicule d'un personnage bouge : {@code true}
     * en route, {@code false} à quai, vide s'il ne connaît pas ce personnage.
     * Consulté avant la mesure physique, à l'ouverture et pendant la conversation.
     */
    public static void sondeMouvement(Function<Entity, Optional<Boolean>> sonde) {
        DialogueManager.sondeMouvement(sonde);
    }

    public static void retirerSondeMouvement(Function<Entity, Optional<Boolean>> sonde) {
        DialogueManager.retirerSondeMouvement(sonde);
    }

    static DialogueSession brute(ServerPlayerEntity joueur) {
        return DialogueManager.session(joueur).orElse(null);
    }
}
