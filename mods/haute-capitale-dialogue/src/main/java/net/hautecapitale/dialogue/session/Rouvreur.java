package net.hautecapitale.dialogue.session;

import net.minecraft.entity.Entity;
import net.minecraft.server.network.ServerPlayerEntity;
import org.jetbrains.annotations.Nullable;

import java.util.UUID;

/**
 * Ce qu'un pont sait faire pour rouvrir une conversation : après un commerce
 * qui se ferme, après une cinématique qui se termine.
 *
 * <p>Le gestionnaire de sessions ne connaît pas Easy NPC ; il demande, et le
 * pont fait. Sans pont, rien ne se rouvre et la conversation finit là.
 */
public interface Rouvreur {

    /**
     * Rouvre le dialogue à menu du personnage, au nœud donné ou au nœud
     * d'entrée si {@code dialogue} est inconnu.
     *
     * @return vrai si un écran de dialogue a été ouvert
     */
    boolean ouvrirDialogue(ServerPlayerEntity joueur, Entity pnj, @Nullable UUID dialogue);

    /**
     * Rejoue l'interaction du personnage — ses commandes, donc sa conversation de
     * chat — comme si le joueur venait de cliquer.
     *
     * @return vrai si le personnage avait quelque chose à rejouer
     */
    boolean rejouerInteraction(ServerPlayerEntity joueur, Entity pnj);
}
