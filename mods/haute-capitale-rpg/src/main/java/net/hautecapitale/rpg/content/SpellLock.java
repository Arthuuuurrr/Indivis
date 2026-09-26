package net.hautecapitale.rpg.content;

import net.hautecapitale.rpg.api.Classes;
import net.hautecapitale.rpg.config.RpgConfig;
import net.hautecapitale.rpg.rpgclass.RpgClass;
import net.minecraft.item.ItemStack;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.spell_engine.api.spell.event.SpellEvents;
import net.spell_engine.internals.casting.SpellCast;

import java.util.Set;

/**
 * Verrou de lancement de sorts.
 *
 * <p>Ferme la porte que le verrou d'équipement laissait ouverte : une arme d'une
 * autre classe ne pouvait déjà plus frapper, mais elle pouvait encore lancer ses
 * sorts.
 *
 * <p>Le refus passe par {@code SpellEvents.CASTING_ATTEMPT.PRE}, le point
 * d'extension que Spell Engine expose exactement pour cela — sa propre
 * documentation le décrit comme « la porte devant chaque lancement, où les mods
 * peuvent injecter un verdict ». Renvoyer un {@link SpellCast.Attempt} non nul
 * court-circuite la tentative ; {@code Attempt.none()} l'annule sans la
 * présenter comme un échec de coût ou de recharge.
 *
 * <p>Deux vérifications, pas une :
 * <ul>
 *   <li>l'<b>objet</b> qui porte le sort — un bâton de Sorcier ;</li>
 *   <li>le <b>sort lui-même</b> — un sort {@code wizards:} reste un sort de
 *       Sorcier, même lié à une arme libre d'Arsenal.</li>
 * </ul>
 *
 * <p>Cette classe référence Spell Engine et n'est chargée que si le mod est
 * présent : le noyau reste utilisable sans lui.
 */
public final class SpellLock {

    private SpellLock() {
    }

    public static void init() {
        SpellEvents.CASTING_ATTEMPT.PRE.register(args -> {
            if (!RpgConfig.get().verrou_equipement) {
                return null; // null : on laisse Spell Engine poursuivre ses propres contrôles
            }

            RpgClass rpgClass = Classes.of(args.caster()).orElse(null);

            // L'objet qui lance.
            ItemStack stack = args.itemStack();
            if (!ClassContent.allowed(stack, rpgClass)) {
                refuser(args.caster(), ClassContent.owners(stack));
                return SpellCast.Attempt.none();
            }

            // Le sort lui-même : un sort de classe reste réservé, quel que soit
            // le support qui le porte.
            var key = args.spell().getKey().orElse(null);
            if (key != null) {
                Set<RpgClass> owners = ClassContent.owners(key.getValue().getNamespace());
                if (!owners.isEmpty() && (rpgClass == null || !owners.contains(rpgClass))) {
                    refuser(args.caster(), owners);
                    return SpellCast.Attempt.none();
                }
            }

            return null;
        });
    }

    private static void refuser(net.minecraft.entity.player.PlayerEntity player,
                                Set<RpgClass> owners) {
        if (player.getEntityWorld().isClient()) {
            return; // un seul message, envoyé par le serveur
        }
        String classes = owners.stream()
                .map(RpgClass::getId)
                .sorted()
                .reduce((a, b) -> a + " ou " + b)
                .orElse("une autre classe");

        player.sendMessage(Text.literal("Sort réservé à ")
                .append(Text.literal(classes).formatted(Formatting.GOLD))
                .formatted(Formatting.GRAY), true);
    }
}
