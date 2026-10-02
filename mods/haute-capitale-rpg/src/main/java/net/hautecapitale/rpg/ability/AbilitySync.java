package net.hautecapitale.rpg.ability;

import net.hautecapitale.rpg.HauteCapitaleRpg;
import net.minecraft.server.network.ServerPlayerEntity;
import net.spell_engine.api.spell.container.SpellContainer;
import net.spell_engine.internals.container.SpellContainerSource;

import java.util.List;

/**
 * Le seul point d'écriture vers Spell Engine.
 *
 * <p>Spell Engine met en commun tous les conteneurs de sorts d'un joueur — mains, armure,
 * sets d'équipement — puis laisse l'objet en main droite décider lesquels deviennent
 * actifs. Il expose pour cela une carte de conteneurs <i>alimentée par le serveur</i>,
 * prévue exactement pour un mod qui veut accorder des sorts sans passer par un objet.
 * On y écrit sous une seule clé, la nôtre, sans jamais toucher aux autres.
 *
 * <p><b>Accès interne assumé.</b> {@code SpellContainerSource.Owner} est une interface
 * publique, mais dans le paquet {@code internals} de Spell Engine. Aucune façade d'API ne
 * l'expose ; l'alternative serait un mixin sur {@code PlayerEntity}, plus fragile et plus
 * intrusif. Le risque de maintenance est réel mais borné : une seule interface, trois
 * méthodes, et une rupture se verrait à la compilation, pas en jeu.
 *
 * <p>Ce qu'on écrit est la liste <i>déjà filtrée par l'arme</i>, pas la liste apprise.
 * Ce choix ferme au passage un trou d'autorité : le verrou de possession de Spell Engine
 * interroge le tas commun, si bien qu'un client modifié pourrait lancer une capacité
 * apprise mais incompatible avec son arme. En ne mettant dans le tas que ce qui est
 * réellement utilisable, ce verrou devient aussi le verrou d'arme.
 *
 * <p>Contrepartie : c'est à nous de reconstruire quand l'arme change, là où le mécanisme
 * natif l'aurait fait seul. {@link AbilityFeature} s'en charge par une comparaison d'objet
 * en main, du même coût que celle que Spell Engine fait déjà chaque tick.
 */
public final class AbilitySync {

    /** Notre clé dans la carte du joueur. Nommée, pour ne jamais écraser celle d'un autre mod. */
    public static final String SOURCE = HauteCapitaleRpg.MOD_ID + ":abilities";

    private AbilitySync() {
    }

    /**
     * Reconstruit intégralement l'état d'un joueur.
     *
     * <p>Idempotente par construction : elle ne lit rien de ce qu'elle a écrit
     * précédemment, elle recalcule tout et remplace. L'appeler dix fois de suite donne le
     * même résultat qu'une fois, ce qui permet de la brancher sur n'importe quel
     * événement sans craindre les doublons.
     *
     * @return vrai si l'état a réellement changé — donc si un paquet a été programmé.
     */
    public static boolean rebuild(ServerPlayerEntity player) {
        List<String> spellIds = AbilityResolver.availableSpellIds(player);

        var owner = (SpellContainerSource.Owner) player;
        var containers = owner.serverSideSpellContainers();
        SpellContainer previous = containers.get(SOURCE);

        if (spellIds.isEmpty()) {
            if (previous == null) {
                return false;
            }
            containers.remove(SOURCE);
            SpellContainerSource.setDirtyServerSide(player);
            return true;
        }

        // `NONE` : ce conteneur donne des sorts, il n'en résout aucun. La résolution reste
        // le rôle de l'arme tenue — c'est ce qui fait qu'une capacité reste connue quand on
        // change d'arme, au lieu de disparaître.
        var container = new SpellContainer(
                SpellContainer.ContentType.NONE, "", "", "", 0, spellIds, 0);

        if (container.equals(previous)) {
            return false; // rien de neuf : pas de paquet, pas de recalcul
        }

        containers.put(SOURCE, container);
        SpellContainerSource.setDirtyServerSide(player);
        return true;
    }

    /** Retire toute trace du joueur côté Spell Engine. */
    public static void clear(ServerPlayerEntity player) {
        var containers = ((SpellContainerSource.Owner) player).serverSideSpellContainers();
        if (containers.remove(SOURCE) != null) {
            SpellContainerSource.setDirtyServerSide(player);
        }
    }
}
