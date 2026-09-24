package net.hautecapitale.dialogue.compat.easynpc;

import de.markusbordihn.easynpc.data.sound.SoundDataEntry;
import de.markusbordihn.easynpc.data.sound.SoundType;
import de.markusbordihn.easynpc.entity.easynpc.EasyNPC;
import de.markusbordihn.easynpc.entity.easynpc.data.SoundDataCapable;
import net.hautecapitale.dialogue.HauteCapitaleDialogue;
import net.hautecapitale.dialogue.config.DialogueConfig;
import net.minecraft.network.packet.s2c.play.PlaySoundS2CPacket;
import net.minecraft.registry.Registries;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvent;

import java.util.Locale;

/**
 * Le son du personnage quand une nouvelle réplique apparaît.
 *
 * <p>Rien de nouveau n'est enregistré : c'est l'un des sons déjà configurés
 * sur le personnage dans Easy NPC (« hmm » d'ambiance, par défaut), joué au
 * seul joueur en conversation, à chaque nœud de dialogue.
 */
final class EasyNpcSons {

    private EasyNpcSons() {
    }

    static void jouerReplique(ServerPlayerEntity joueur, EasyNPC<?> npc) {
        String reglage = DialogueConfig.get().son_replique;
        if (reglage == null || reglage.equalsIgnoreCase("none") || npc == null) {
            return;
        }
        SoundType type;
        try {
            type = SoundType.valueOf(reglage.trim().toUpperCase(Locale.ROOT));
        } catch (IllegalArgumentException e) {
            HauteCapitaleDialogue.LOGGER.warn("son_replique « {} » inconnu d'Easy NPC ; aucun son.", reglage);
            return;
        }
        try {
            SoundDataCapable<?> sons = npc.getEasyNPCSoundData();
            if (sons == null || !sons.hasDefaultSound(type)) {
                return;
            }
            SoundDataEntry entree = sons.getDefaultSound(type);
            SoundEvent evenement = entree == null ? null : entree.getSoundEvent();
            if (evenement == null) {
                return;
            }
            // Au seul joueur en conversation : un paquet, pas un son de monde que
            // tout le quai entendrait a chaque noeud.
            if (joueur.networkHandler != null) {
                joueur.networkHandler.sendPacket(new PlaySoundS2CPacket(
                        Registries.SOUND_EVENT.getEntry(evenement), SoundCategory.NEUTRAL,
                        npc.getEntity().getX(), npc.getEntity().getY(), npc.getEntity().getZ(),
                        entree.getVolume(), entree.getPitch(), joueur.getRandom().nextLong()));
            }
        } catch (Exception e) {
            HauteCapitaleDialogue.LOGGER.debug("Son de réplique impossible pour {} : {}", npc, e.toString());
        }
    }
}
