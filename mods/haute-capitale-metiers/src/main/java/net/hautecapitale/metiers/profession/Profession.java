package net.hautecapitale.metiers.profession;

import com.mojang.serialization.Codec;
import net.hautecapitale.metiers.util.Vocabulary;
import net.minecraft.util.StringIdentifiable;

import java.util.Arrays;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * Les onze métiers de Haute Capitale.
 *
 * <p>La pêche et l'enchantement sont volontairement absents : ils relèvent
 * d'autres systèmes du serveur.
 *
 * <p>L'ensemble est fixé par le design, d'où l'énumération. Ce qui doit rester
 * data-driven — créatures, recettes, filons — vivra dans des fichiers de
 * données à l'étape 2, et référencera ces métiers par leur {@link #id}.
 */
public enum Profession implements StringIdentifiable {
    // Récolte
    MINEUR("mineur", Kind.RECOLTE),
    HERBORISTE("herboriste", Kind.RECOLTE),
    CHASSEUR("chasseur", Kind.RECOLTE),
    DEPECEUR("depeceur", Kind.RECOLTE),
    // Artisanat
    FORGERON("forgeron", Kind.ARTISANAT),
    COUTURIER("couturier", Kind.ARTISANAT),
    TRAVAILLEUR_DU_CUIR("travailleur_du_cuir", Kind.ARTISANAT),
    JOAILLIER("joaillier", Kind.ARTISANAT),
    CUISINIER("cuisinier", Kind.ARTISANAT),
    INGENIEUR("ingenieur", Kind.ARTISANAT),
    /** Les potions, à partir des plantes de l'Herboriste — ajouté après les onze étapes. */
    ALCHIMISTE("alchimiste", Kind.ARTISANAT);

    /** Famille de métier. Sert au tri des interfaces, pas aux règles de jeu. */
    public enum Kind { RECOLTE, ARTISANAT }

    public static final Codec<Profession> CODEC = Vocabulary.of("metier", Profession::values);

    private static final Map<String, Profession> BY_ID =
            Arrays.stream(values()).collect(Collectors.toUnmodifiableMap(Profession::getId, Function.identity()));

    private final String id;
    private final Kind kind;
    private final String translationKey;

    Profession(String id, Kind kind) {
        this.id = id;
        this.kind = kind;
        this.translationKey = "profession.haute_capitale_metiers." + id;
    }

    public String getId() {
        return id;
    }

    public Kind getKind() {
        return kind;
    }

    public String getTranslationKey() {
        return translationKey;
    }

    /** Renvoie {@code null} si l'identifiant est inconnu — au caller de le signaler. */
    public static Profession byId(String id) {
        return BY_ID.get(id);
    }

    public static Iterable<String> ids() {
        return BY_ID.keySet();
    }

    @Override
    public String asString() {
        return id;
    }
}
