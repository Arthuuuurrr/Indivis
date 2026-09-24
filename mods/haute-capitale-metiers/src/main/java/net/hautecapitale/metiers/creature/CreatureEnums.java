package net.hautecapitale.metiers.creature;

import com.mojang.serialization.Codec;
import net.hautecapitale.metiers.util.Vocabulary;
import net.minecraft.util.StringIdentifiable;

import java.util.Locale;

/** Vocabulaire des fiches de créature. */
public final class CreatureEnums {

    private CreatureEnums() {
    }

    /**
     * Famille d'une créature. Sert au tri, aux règles générales et aux
     * futurs filtres d'administration — jamais à décider seule si une créature
     * est chassable ou dépeçable : cela reste écrit explicitement dans sa fiche.
     */
    public enum Category implements StringIdentifiable {
        ANIMAL("animal"),
        BETE("bete"),
        BETE_FANTASY("bete_fantasy"),
        REPTILE("reptile"),
        INSECTE("insecte"),
        HUMANOIDE("humanoide"),
        HUMANOIDE_MONSTRUEUX("humanoide_monstrueux"),
        ORC("orc"),
        MORT_VIVANT("mort_vivant"),
        ELEMENTAIRE("elementaire"),
        MINIBOSS("miniboss"),
        BOSS("boss"),
        AUTRE("autre");

        public static final Codec<Category> CODEC = Vocabulary.of("categorie", Category::values);

        private final String id;

        Category(String id) {
            this.id = id;
        }

        @Override
        public String asString() {
            return id;
        }
    }

    /** Rareté d'une créature, qui guide la valeur de son butin. */
    public enum Rarity implements StringIdentifiable {
        COMMUNE("commune"),
        PEU_COMMUNE("peu_commune"),
        RARE("rare"),
        EXOTIQUE("exotique"),
        TRES_RARE("tres_rare"),
        LEGENDAIRE("legendaire");

        public static final Codec<Rarity> CODEC = Vocabulary.of("rarete", Rarity::values);

        private final String id;

        Rarity(String id) {
            this.id = id;
        }

        @Override
        public String asString() {
            return id;
        }
    }

    /**
     * D'où vient une créature. C'est la clé de l'anti-farm : une fiche déclare
     * quelles origines rapportent de l'XP, et l'élevage en masse ne rapporte
     * rien tant qu'il n'est pas explicitement autorisé.
     *
     * <p>Le marquage effectif des entités arrive à l'étape 7 ; l'étape 2 ne fait
     * qu'établir le vocabulaire.
     */
    public enum SpawnOrigin implements StringIdentifiable {
        /** Apparition naturelle du monde. */
        NATURELLE("naturelle"),
        /** Placée par le système MMO — événement, donjon, proie rare. */
        SPAWN_MMO("spawn_mmo"),
        /** Née d'une reproduction entre deux animaux. */
        ELEVAGE("elevage"),
        /** Issue d'un générateur de monstres. */
        SPAWNER("spawner"),
        /** Issue d'un œuf d'apparition. */
        OEUF("oeuf"),
        /** Invoquée par une commande ou un administrateur. */
        COMMANDE("commande"),
        /** Invoquée par un sort, un objet ou un autre mod. */
        INVOCATION("invocation"),
        /** Origine indéterminée — typiquement une entité d'avant l'installation du mod. */
        INCONNUE("inconnue");

        public static final Codec<SpawnOrigin> CODEC = Vocabulary.of("origines_xp", SpawnOrigin::values);

        /** Ce qu'on autorise par défaut si une fiche ne dit rien. */
        public static final java.util.List<SpawnOrigin> DEFAUT =
                java.util.List.of(NATURELLE, SPAWN_MMO);

        private final String id;

        SpawnOrigin(String id) {
            this.id = id;
        }

        @Override
        public String asString() {
            return id;
        }
    }

    /** « bete_fantasy » → « Bete fantasy », pour l'affichage des commandes. */
    public static String pretty(String id) {
        String text = id.replace('_', ' ');
        return Character.toUpperCase(text.charAt(0)) + text.substring(1).toLowerCase(Locale.ROOT);
    }
}
