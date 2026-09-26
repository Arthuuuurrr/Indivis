package net.hautecapitale.rpg.ability;

import net.minecraft.item.ItemStack;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.Identifier;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;

/**
 * Le pipeline de résolution, en une seule descente et sans état.
 *
 * <p>Chaque étape a une réponse distincte, et elles ne sont jamais fusionnées :
 * <i>définie</i> (le sort existe), <i>apprise</i> (l'arbre l'a accordée, ou elle est
 * intrinsèque), <i>compatible</i> (l'arme tenue convient). Une capacité disponible est
 * l'intersection des trois.
 *
 * <p>La séparation n'est pas une élégance gratuite : c'est elle qui permet de répondre
 * « tu la connais, mais pas avec cette arme » plutôt que de la faire disparaître sans
 * explication.
 */
public final class AbilityResolver {

    private AbilityResolver() {
    }

    /** L'état complet, pour la commande de diagnostic. */
    public static List<AbilityStatus> statuses(ServerPlayerEntity player) {
        ItemStack mainHand = player.getMainHandStack();
        var learned = LearnedAbilities.of(player);
        var result = new ArrayList<AbilityStatus>();

        for (var entry : AbilityRegistry.all().entrySet()) {
            Identifier id = entry.getKey();
            AbilityDefinition definition = entry.getValue();
            // Aligne sur le jar « 0.1.1-capskills-spell-id-fix » qui tourne sur le serveur
            // de production depuis le 10/09 : la verification se fait sur l'identifiant du
            // *sort*, pas sur celui de la capacite. Comme l'audit range les capacites
            // manquantes par identifiant de capacite, ce test ne bloque en pratique plus
            // rien — c'est le comportement en service, on ne le change pas a l'aveugle.
            // A revoir : pourquoi l'audit signalait-il des sorts CapSkills manquants ?
            boolean defined = AbilityRegistry.spellExists(definition.spell());
            boolean known = !definition.requires_skill() || learned.contains(id);
            boolean compatible = definition.weapon().matches(mainHand);
            result.add(new AbilityStatus(id, definition, defined, known, compatible));
        }
        result.sort((a, b) -> a.id().compareTo(b.id()));
        return result;
    }

    /** Les capacités que le joueur connaît, indépendamment de ce qu'il tient. */
    public static List<AbilityStatus> known(ServerPlayerEntity player) {
        return statuses(player).stream().filter(s -> s.defined() && s.learned()).toList();
    }

    /**
     * Les identifiants de sorts réellement utilisables maintenant.
     *
     * <p>C'est exactement ce que {@link AbilitySync} écrit dans le conteneur côté serveur.
     * Deux capacités peuvent désigner le même sort — un doublon serait accordé deux fois —
     * d'où l'ensemble ordonné plutôt qu'une liste.
     */
    public static List<String> availableSpellIds(ServerPlayerEntity player) {
        var ids = new LinkedHashSet<String>();
        for (var status : statuses(player)) {
            if (status.available()) {
                ids.add(status.definition().spell().toString());
            }
        }
        return List.copyOf(ids);
    }
}
