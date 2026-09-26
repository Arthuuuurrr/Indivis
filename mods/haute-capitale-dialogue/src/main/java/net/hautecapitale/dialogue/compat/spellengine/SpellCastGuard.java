package net.hautecapitale.dialogue.compat.spellengine;

import net.hautecapitale.dialogue.HauteCapitaleDialogue;
import net.hautecapitale.dialogue.session.DialogueManager;
import net.spell_engine.api.spell.event.SpellEvents;
import net.spell_engine.internals.casting.SpellCast;

/**
 * Pas de sort pendant une conversation, quoi qu'envoie le client.
 *
 * <p>Le client de Spell Engine annule lui-même tout lancement dès qu'un écran
 * est ouvert ; ceci est la version serveur de la même règle, par le point
 * d'extension que Spell Engine documente comme « la porte devant chaque
 * lancement ». {@code Attempt.none()} annule sans présenter l'échec comme un
 * problème de coût ou de recharge.
 *
 * <p>Classe chargée uniquement si Spell Engine est présent.
 */
public final class SpellCastGuard {

    private SpellCastGuard() {
    }

    public static void init() {
        SpellEvents.CASTING_ATTEMPT.PRE.register(args -> {
            if (args.caster() == null || args.caster().getEntityWorld().isClient()) {
                return null;
            }
            return DialogueManager.enSession(args.caster()) ? SpellCast.Attempt.none() : null;
        });
        HauteCapitaleDialogue.LOGGER.info("Garde de sorts branchée sur Spell Engine.");
    }
}
