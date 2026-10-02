package net.hautecapitale.metiers.craft;

import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.hautecapitale.metiers.profession.Profession;
import net.minecraft.item.ItemStack;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.tag.TagKey;
import com.mojang.serialization.Dynamic;
import net.hautecapitale.metiers.HauteCapitaleMetiers;
import net.minecraft.component.ComponentChanges;
import net.minecraft.registry.RegistryOps;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.util.Identifier;

import java.util.List;
import java.util.Optional;

/**
 * Une recette de métier — ce qu'un artisan fabrique chez son PNJ.
 *
 * <p>Rien à voir avec une recette d'établi : pas de grille, pas de forme, pas
 * de livre de recettes. Une liste d'ingrédients par famille, un prix, un
 * résultat, et le niveau à partir duquel le métier la propose. Le fichier
 * {@code data/<ns>/hcm/recipes/travailleur_du_cuir/tannage_simple.json} décrit
 * {@code <ns>:travailleur_du_cuir/tannage_simple} — le chemin est la clé.
 *
 * <p>Les ingrédients se demandent par <em>tag</em> autant que possible : ainsi
 * la fourrure de loup d'un autre mod vaut la nôtre, et personne ne perd ce qu'il
 * possède déjà. Un identifiant d'objet précis reste possible quand c'est
 * réellement cet objet-là qu'il faut.
 *
 * @param profession le métier qui fabrique
 * @param level      niveau requis
 * @param xp         XP accordée à niveau égal — la décote s'applique ensuite
 * @param ingredients ce qu'il faut fournir
 * @param cost       prix en monnaie du serveur, 0 si gratuit
 * @param result     ce qu'on obtient
 * @param title      libellé affiché, sinon le nom du résultat
 * @param quality    la qualité Excellent est-elle possible ? Non pour les
 *                   matières et composants, qui ne la transmettent pas
 * @param meal       pour un plat : le buff de repas qu'il porte
 */
public record CraftRecipe(
        Profession profession,
        int level,
        double xp,
        List<Ingredient> ingredients,
        int cost,
        Result result,
        Optional<String> title,
        Optional<String> comment,
        boolean quality,
        Optional<Meal> meal
) {

    /**
     * Le buff qu'un plat du Cuisinier donne — une fiche de { hcm/buffs},
     * et une valeur qui remplace celle de la fiche si le plat est plus ou moins
     * nourrissant que le buff par défaut.
     */
    public record Meal(Identifier buff, Optional<Double> value) {
        public static final Codec<Meal> CODEC = RecordCodecBuilder.create(instance -> instance.group(
                Identifier.CODEC.fieldOf("buff").forGetter(Meal::buff),
                Codec.DOUBLE.optionalFieldOf("valeur").forGetter(Meal::value)
        ).apply(instance, Meal::new));

        public Meal {
            value = value.filter(Double::isFinite);
        }
    }

    /**
     * Un ingrédient : une famille ({@code tag}) ou un objet précis ({@code id}),
     * jamais les deux, et une quantité.
     */
    public record Ingredient(Optional<TagKey<net.minecraft.item.Item>> tag, Optional<Identifier> item,
                             int count, Optional<String> name) {

        private static final Codec<TagKey<net.minecraft.item.Item>> TAG_CODEC =
                Identifier.CODEC.xmap(id -> TagKey.of(RegistryKeys.ITEM, id), TagKey::id);

        public static final Codec<Ingredient> CODEC = RecordCodecBuilder.<Ingredient>create(instance -> instance.group(
                TAG_CODEC.optionalFieldOf("tag").forGetter(Ingredient::tag),
                Identifier.CODEC.optionalFieldOf("id").forGetter(Ingredient::item),
                Codec.INT.optionalFieldOf("quantite", 1).forGetter(Ingredient::count),
                Codec.STRING.optionalFieldOf("nom").forGetter(Ingredient::name)
        ).apply(instance, Ingredient::new)).validate(Ingredient::validate);

        public Ingredient {
            if (count < 1) {
                count = 1;
            }
        }

        private DataResult<Ingredient> validate() {
            if (tag.isPresent() == item.isPresent()) {
                return DataResult.error(() -> "un ingrédient indique soit « tag », soit « id » — exactement l'un des deux");
            }
            return DataResult.success(this);
        }

        /** Cet objet peut-il servir d'ingrédient ici ? */
        public boolean matches(ItemStack stack) {
            if (stack.isEmpty()) {
                return false;
            }
            if (tag.isPresent()) {
                return stack.isIn(tag.get());
            }
            return item.map(id -> net.minecraft.registry.Registries.ITEM.getId(stack.getItem()).equals(id))
                    .orElse(false);
        }

        /** Ce que l'ingrédient désigne, pour les messages et l'inspection. */
        public String describeSource() {
            return tag.map(t -> "#" + t.id()).orElseGet(() -> item.map(Identifier::toString).orElse("?"));
        }
    }

    /**
     * Ce que la recette produit.
     *
     * @param components des composants posés sur la pile produite — c'est ainsi
     *                   qu'une recette d'Alchimiste fabrique une potion vanilla :
     *                   {@code "composants": {"minecraft:potion_contents": {"potion": "minecraft:healing"}}}
     */
    public record Result(Identifier item, int count, Optional<Dynamic<?>> components) {
        // Les composants restent du JSON brut jusqu'à la fabrication : leur codec
        // (contenu de potion, entrées de registre) exige les registres du serveur,
        // que le chargement des fichiers n'a pas sous la main.
        public static final Codec<Result> CODEC = RecordCodecBuilder.create(instance -> instance.group(
                Identifier.CODEC.fieldOf("id").forGetter(Result::item),
                Codec.INT.optionalFieldOf("quantite", 1).forGetter(Result::count),
                Codec.PASSTHROUGH.optionalFieldOf("composants").forGetter(Result::components)
        ).apply(instance, Result::new));

        public Result(Identifier item, int count) {
            this(item, count, Optional.empty());
        }

        public Result {
            if (count < 1) {
                count = 1;
            }
        }

        /** Les composants à poser sur la pile, décodés avec les registres du serveur ; vide si absents ou illisibles. */
        public Optional<ComponentChanges> decodeComponents(RegistryWrapper.WrapperLookup registries) {
            return components.flatMap(dynamic -> decode(dynamic, registries).resultOrPartial(
                    error -> HauteCapitaleMetiers.LOGGER.warn("Composants illisibles pour {} : {}", item, error)));
        }

        private static <T> DataResult<ComponentChanges> decode(Dynamic<T> dynamic, RegistryWrapper.WrapperLookup registries) {
            return ComponentChanges.CODEC.parse(RegistryOps.of(dynamic.getOps(), registries), dynamic.getValue());
        }
    }

    public static final Codec<CraftRecipe> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            Profession.CODEC.fieldOf("metier").forGetter(CraftRecipe::profession),
            Codec.INT.optionalFieldOf("niveau", 1).forGetter(CraftRecipe::level),
            Codec.DOUBLE.optionalFieldOf("xp", 0.0D).forGetter(CraftRecipe::xp),
            Ingredient.CODEC.listOf().fieldOf("ingredients").forGetter(CraftRecipe::ingredients),
            Codec.INT.optionalFieldOf("cout", 0).forGetter(CraftRecipe::cost),
            Result.CODEC.fieldOf("resultat").forGetter(CraftRecipe::result),
            Codec.STRING.optionalFieldOf("titre").forGetter(CraftRecipe::title),
            Codec.STRING.optionalFieldOf("commentaire").forGetter(CraftRecipe::comment),
            Codec.BOOL.optionalFieldOf("qualite", true).forGetter(CraftRecipe::quality),
            Meal.CODEC.optionalFieldOf("repas").forGetter(CraftRecipe::meal)
    ).apply(instance, CraftRecipe::new));

    public CraftRecipe {
        if (level < 1) {
            level = 1;
        }
        if (xp < 0.0D || !Double.isFinite(xp)) {
            xp = 0.0D;
        }
        if (cost < 0) {
            cost = 0;
        }
        ingredients = List.copyOf(ingredients);
    }
}
