package net.hautecapitale.metiers.profession;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

import java.util.Collections;
import java.util.EnumMap;
import java.util.Map;

/**
 * L'ensemble des métiers d'un joueur.
 *
 * <p>Immuable : chaque modification produit un nouvel état. C'est ce qui permet
 * à l'API d'attachement de détecter le changement et de resynchroniser le
 * client sans qu'on ait à le demander.
 *
 * <p>Un métier absent de la carte signifie « non appris ». On ne stocke donc
 * jamais dix entrées par joueur, seulement celles qui existent vraiment.
 *
 * <p>Les recettes apprises et la maîtrise viendront dans leurs propres
 * attachements, pas ici : chacun a son cycle de vie et sa règle de
 * synchronisation.
 */
public record ProfessionsState(Map<Profession, ProfessionProgress> entries) {

    public static final ProfessionsState EMPTY = new ProfessionsState(Collections.emptyMap());

    public static final Codec<ProfessionsState> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            Codec.unboundedMap(Profession.CODEC, ProfessionProgress.CODEC)
                    .optionalFieldOf("metiers", Collections.emptyMap())
                    .forGetter(ProfessionsState::entries)
    ).apply(instance, ProfessionsState::new));

    public ProfessionsState {
        entries = entries.isEmpty() ? Collections.emptyMap() : Collections.unmodifiableMap(new EnumMap<>(entries));
    }

    public static ProfessionsState empty() {
        return EMPTY;
    }

    public boolean has(Profession profession) {
        return entries.containsKey(profession);
    }

    /** Progression du métier, ou {@code null} s'il n'est pas appris. */
    public ProfessionProgress get(Profession profession) {
        return entries.get(profession);
    }

    /** Progression du métier, ou la progression initiale s'il n'est pas appris. */
    public ProfessionProgress getOrInitial(Profession profession) {
        return entries.getOrDefault(profession, ProfessionProgress.INITIAL);
    }

    public int count() {
        return entries.size();
    }

    public ProfessionsState with(Profession profession, ProfessionProgress progress) {
        EnumMap<Profession, ProfessionProgress> copy = new EnumMap<>(Profession.class);
        copy.putAll(entries);
        copy.put(profession, progress);
        return new ProfessionsState(copy);
    }

    public ProfessionsState without(Profession profession) {
        if (!entries.containsKey(profession)) {
            return this;
        }
        EnumMap<Profession, ProfessionProgress> copy = new EnumMap<>(Profession.class);
        copy.putAll(entries);
        copy.remove(profession);
        return new ProfessionsState(copy);
    }
}
