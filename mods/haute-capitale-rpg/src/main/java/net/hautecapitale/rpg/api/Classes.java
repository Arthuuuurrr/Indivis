package net.hautecapitale.rpg.api;

import net.hautecapitale.rpg.rpgclass.ClassAttachments;
import net.hautecapitale.rpg.rpgclass.PlayerClassState;
import net.hautecapitale.rpg.rpgclass.RpgClass;
import net.minecraft.entity.player.PlayerEntity;

import java.util.Optional;

/**
 * Façade publique du noyau.
 *
 * <p>Point d'entrée unique et stable : les modules de classe à venir et les
 * autres systèmes du serveur passent par ici, jamais par les attachements
 * directement. Cela laisse la liberté de changer le stockage sans casser
 * les appelants.
 */
public final class Classes {

    private Classes() {
    }

    public static PlayerClassState stateOf(PlayerEntity player) {
        return player.getAttachedOrCreate(ClassAttachments.CLASSE);
    }

    public static Optional<RpgClass> of(PlayerEntity player) {
        return stateOf(player).rpgClass();
    }

    public static boolean is(PlayerEntity player, RpgClass rpgClass) {
        return of(player).filter(c -> c == rpgClass).isPresent();
    }

    /** Attribue une classe. Écrase la précédente, s'il y en avait une. */
    public static void set(PlayerEntity player, RpgClass rpgClass) {
        player.setAttached(ClassAttachments.CLASSE, PlayerClassState.of(rpgClass));
    }

    /** Retire la classe et remet le niveau à zéro. */
    public static void clear(PlayerEntity player) {
        player.setAttached(ClassAttachments.CLASSE, PlayerClassState.empty());
    }

}
