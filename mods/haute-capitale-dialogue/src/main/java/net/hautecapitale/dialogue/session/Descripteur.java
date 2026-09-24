package net.hautecapitale.dialogue.session;

import net.minecraft.entity.Entity;
import org.jetbrains.annotations.Nullable;

import java.util.UUID;

/**
 * Ce qu'un pont sait dire d'un personnage, pour {@code /dialogue debug} et
 * {@code /dialogue pnj voir} : ce que son clic déclenche, ses dialogues, le
 * nœud courant. En clair, pour un administrateur devant des centaines de PNJ.
 */
@FunctionalInterface
public interface Descripteur {

    /**
     * @param pnj      le personnage
     * @param dialogue le nœud courant de la session, s'il y en a une
     * @return une ou plusieurs lignes, ou {@code null} si le pont ne connaît pas ce personnage
     */
    @Nullable
    String decrire(Entity pnj, @Nullable UUID dialogue);
}
