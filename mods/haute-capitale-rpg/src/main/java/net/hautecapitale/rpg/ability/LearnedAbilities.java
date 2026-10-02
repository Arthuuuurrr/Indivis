package net.hautecapitale.rpg.ability;

import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.Identifier;

import java.util.Collection;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Ce que chaque joueur a appris — <b>un cache, pas une sauvegarde</b>.
 *
 * <p>La distinction est essentielle. Pufferfish reste la seule source de vérité de la
 * progression ; ce qui est gardé ici est reconstruit à partir de lui et jamais écrit sur
 * disque. Rien ne peut donc diverger : au pire, ce cache est vide, et il se remplit à la
 * première application des récompenses.
 *
 * <p>Il est rempli par {@link AbilityReward}, que Pufferfish rejoue sur tout changement —
 * déblocage, montée de rang, respec, reconnexion. C'est ce rejeu systématique qui rend la
 * reconstruction idempotente sans qu'on ait à l'orchestrer.
 *
 * <p>Les capacités intrinsèques — celles dont la définition porte
 * {@code requires_skill: false} — ne passent pas par ici : elles sont ajoutées à la
 * résolution, pour qu'une arme conserve ses gestes de base sans coûter un point d'arbre.
 */
public final class LearnedAbilities {

    private static final Map<UUID, Set<Identifier>> GRANTED = new ConcurrentHashMap<>();

    private LearnedAbilities() {
    }

    public static Set<Identifier> of(ServerPlayerEntity player) {
        return Set.copyOf(GRANTED.getOrDefault(player.getUuid(), Set.of()));
    }

    public static boolean has(ServerPlayerEntity player, Identifier abilityId) {
        var set = GRANTED.get(player.getUuid());
        return set != null && set.contains(abilityId);
    }

    public static void grant(ServerPlayerEntity player, Collection<Identifier> abilities) {
        GRANTED.computeIfAbsent(player.getUuid(), uuid -> new LinkedHashSet<>()).addAll(abilities);
    }

    public static void revoke(ServerPlayerEntity player, Collection<Identifier> abilities) {
        var set = GRANTED.get(player.getUuid());
        if (set != null) {
            set.removeAll(abilities);
        }
    }

    /**
     * Oublie tout d'un joueur.
     *
     * <p>Appelé à la déconnexion pour ne pas garder de mémoire d'un joueur absent, et avant
     * une reconstruction complète pour qu'une capacité retirée de l'arbre entre deux
     * sessions ne survive pas dans le cache.
     */
    public static void forget(ServerPlayerEntity player) {
        GRANTED.remove(player.getUuid());
    }
}
