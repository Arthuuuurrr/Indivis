package net.hautecapitale.metiers.util;

import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import net.minecraft.util.StringIdentifiable;

import java.util.Arrays;
import java.util.function.Supplier;
import java.util.stream.Collectors;

/**
 * Décodeurs de mots-clés qui disent ce qu'ils attendaient.
 *
 * <p>Le décodeur d'énumération standard répond {@code Unknown element
 * name:bovin} : en anglais, sans nommer le champ fautif ni les valeurs
 * possibles. Les fichiers de données de ce mod s'écrivent à la main par
 * centaines — le message d'erreur fait partie de l'outil, au même titre que le
 * format lui-même.
 */
public final class Vocabulary {

    private Vocabulary() {
    }

    /**
     * @param field  nom du champ tel qu'il apparaît dans le JSON, cité dans l'erreur
     * @param values les valeurs acceptées
     */
    public static <E extends Enum<E> & StringIdentifiable> Codec<E> of(
            String field, Supplier<E[]> values) {
        return Codec.STRING.comapFlatMap(
                name -> {
                    for (E value : values.get()) {
                        if (value.asString().equals(name)) {
                            return DataResult.success(value);
                        }
                    }
                    return DataResult.error(() -> "« " + field + " » : valeur inconnue « " + name
                            + " ». Valeurs acceptées : " + accepted(values.get()));
                },
                StringIdentifiable::asString);
    }

    /** Liste lisible des valeurs acceptées, pour les messages d'erreur et l'aide. */
    public static <E extends Enum<E> & StringIdentifiable> String accepted(E[] values) {
        return Arrays.stream(values)
                .map(StringIdentifiable::asString)
                .collect(Collectors.joining(", "));
    }
}
