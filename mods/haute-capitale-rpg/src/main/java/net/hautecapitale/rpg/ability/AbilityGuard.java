package net.hautecapitale.rpg.ability;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.Identifier;
import net.spell_engine.api.spell.event.SpellEvents;
import net.spell_engine.internals.casting.SpellCast;

/**
 * La revérification serveur, juste avant l'incantation.
 *
 * <p>Spell Engine possède déjà un verrou de possession : il refuse un sort que le joueur
 * n'a pas. Mais ce verrou interroge le tas commun de conteneurs, pas la liste résolue par
 * l'arme tenue — un client modifié pourrait donc lancer, à l'arc, une capacité réservée à
 * l'épée.
 *
 * <p>{@link AbilitySync} referme déjà ce trou en n'écrivant que les capacités compatibles.
 * Ce garde-fou couvre ce que la reconstruction ne peut pas couvrir : la fenêtre d'un tic
 * entre le changement d'arme et la synchronisation, et le cas d'une capacité accordée par
 * une autre source que la nôtre. Il donne surtout un refus <i>explicable</i>, là où le
 * verrou natif se contente de ne rien faire.
 */
public final class AbilityGuard {

    private AbilityGuard() {
    }

    public static void init() {
        SpellEvents.CASTING_ATTEMPT.PRE.register(args -> {
            PlayerEntity caster = args.caster();
            if (!(caster instanceof ServerPlayerEntity player)) {
                return null; // le client ne tranche pas ; il attendra le verdict du serveur
            }
            Identifier spellId = args.spell().getKey().map(key -> key.getValue()).orElse(null);
            if (spellId == null) {
                return null;
            }

            AbilityStatus blocking = null;
            for (var status : AbilityResolver.statuses(player)) {
                if (!status.definition().spell().equals(spellId)) {
                    continue;
                }
                if (status.available()) {
                    return null; // au moins une définition l'autorise : on laisse passer
                }
                blocking = status;
            }

            if (blocking == null) {
                return null; // sort inconnu de notre catalogue : ce n'est pas notre affaire
            }

            player.sendMessage(Text.literal(blocking.reason()).formatted(Formatting.GRAY), true);
            return SpellCast.Attempt.none();
        });
    }
}
