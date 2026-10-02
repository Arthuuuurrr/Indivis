package net.hautecapitale.rpg.rpgclass;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

import java.util.Optional;

/**
 * L'état RPG d'un joueur : sa classe, et rien de plus.
 *
 * <p><b>Aucune progression n'est stockée ici.</b> L'arbre Pufferfish est la seule
 * source de progression du serveur ; maintenir un niveau et une expérience en
 * parallèle créerait deux vérités concurrentes sur ce que le personnage a appris.
 * Une version antérieure de ce noyau le faisait — les champs {@code niveau} et
 * {@code xp} écrits par elle sont simplement ignorés à la relecture, sans erreur.
 *
 * <p>Ce qui reste ici est de l'<i>identité</i>, pas de la progression : la classe
 * choisie. Ce que le personnage sait faire vient de Pufferfish.
 *
 * <p>Immuable — chaque changement produit une nouvelle instance, ce qui évite
 * qu'un attachement synchronisé soit muté sous les pieds du réseau.
 */
public record PlayerClassState(Optional<RpgClass> rpgClass) {

    public static final PlayerClassState NONE = new PlayerClassState(Optional.empty());

    public static final Codec<PlayerClassState> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            RpgClass.CODEC.optionalFieldOf("classe").forGetter(PlayerClassState::rpgClass)
    ).apply(instance, PlayerClassState::new));

    public static PlayerClassState empty() {
        return NONE;
    }

    public boolean hasClass() {
        return rpgClass.isPresent();
    }

    public static PlayerClassState of(RpgClass rpgClass) {
        return new PlayerClassState(Optional.of(rpgClass));
    }
}
