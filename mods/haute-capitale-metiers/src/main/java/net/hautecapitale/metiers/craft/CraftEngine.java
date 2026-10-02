package net.hautecapitale.metiers.craft;

import net.hautecapitale.metiers.api.Metiers;
import net.hautecapitale.metiers.config.MetiersConfig;
import net.hautecapitale.metiers.data.HcmData;
import net.hautecapitale.metiers.meal.MealBuff;
import net.hautecapitale.metiers.meal.MealEngine;
import net.hautecapitale.metiers.quality.Quality;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.registry.Registries;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.Identifier;

import java.util.ArrayList;
import java.util.List;

/**
 * Le moteur de fabrication.
 *
 * <p>Tout se joue sur le fil du serveur, en une seule passe :
 * <ol>
 *   <li>on <em>vérifie tout</em> — recette, métier, niveau, ingrédients,
 *       monnaie, sans rien toucher ;</li>
 *   <li>seulement si tout passe, on <em>retire</em> ingrédients et monnaie,
 *       puis on <em>remet</em> le résultat.</li>
 * </ol>
 * Entre les deux, rien ne peut s'intercaler : un paquet client est traité
 * entier avant le suivant, et une déconnexion n'interrompt pas une méthode en
 * cours. C'est ce qui rend la transaction atomique sans verrou. Un résultat qui
 * ne tient pas dans l'inventaire est déposé aux pieds du joueur, jamais perdu.
 *
 * <p>Le moteur ne fait pas confiance à l'écran : chaque demande est revérifiée
 * ici, y compris celles que l'interface n'aurait jamais dû permettre.
 */
public final class CraftEngine {

    private CraftEngine() {
    }

    /** Pourquoi une recette n'est pas fabricable, ou {@code null} si elle l'est. */
    public enum Refusal {
        RECETTE_INCONNUE,
        METIER_NON_APPRIS,
        NIVEAU_INSUFFISANT,
        INGREDIENTS_MANQUANTS,
        MONNAIE_INSUFFISANTE,
        MONNAIE_ABSENTE,
        RESULTAT_INCONNU
    }

    /** Ce qu'une fabrication a produit. */
    public record Outcome(int crafted, double xpGained, int levelsGained, int totalCrafts,
                          Mastery masteryBefore, Mastery masteryAfter, Refusal refusal, int excellent) {

        public boolean succeeded() {
            return refusal == null;
        }

        static Outcome refused(Refusal refusal) {
            return new Outcome(0, 0.0D, 0, 0, Mastery.NONE, Mastery.NONE, refusal, 0);
        }
    }

    /**
     * Vérifie sans rien modifier. C'est ce que l'écran affiche comme
     * « accessible », et ce que la fabrication revérifie avant d'agir.
     *
     * @param times nombre de fabrications demandées, 1 au moins
     */
    public static Refusal check(ServerPlayerEntity player, Identifier recipeId, int times) {
        CraftRecipe recipe = HcmData.RECIPES.get(recipeId);
        if (recipe == null) {
            return Refusal.RECETTE_INCONNUE;
        }
        if (!Metiers.hasProfession(player, recipe.profession())) {
            return Refusal.METIER_NON_APPRIS;
        }
        if (Metiers.getLevel(player, recipe.profession()) < recipe.level()) {
            return Refusal.NIVEAU_INSUFFISANT;
        }
        if (!Registries.ITEM.containsId(recipe.result().item())) {
            return Refusal.RESULTAT_INCONNU;
        }

        int batch = Math.max(1, times);
        PlayerInventory inventory = player.getInventory();
        for (CraftRecipe.Ingredient ingredient : recipe.ingredients()) {
            if (count(inventory, ingredient) < ingredient.count() * batch) {
                return Refusal.INGREDIENTS_MANQUANTS;
            }
        }

        if (recipe.cost() > 0) {
            Item currency = currency();
            if (currency == null) {
                return Refusal.MONNAIE_ABSENTE;
            }
            if (count(inventory, currency) < recipe.cost() * batch) {
                return Refusal.MONNAIE_INSUFFISANTE;
            }
        }
        return null;
    }

    /**
     * Fabrique, ou refuse sans rien toucher.
     *
     * <p>Les fabrications multiples sont réservées aux recettes maîtrisées ; une
     * demande de ×5 sur une recette non maîtrisée est ramenée à ×1, elle n'est
     * pas refusée — l'écran d'un client modifié ne doit pas pouvoir contourner
     * la règle, mais un joueur honnête ne doit pas non plus être puni.
     */
    public static Outcome craft(ServerPlayerEntity player, Identifier recipeId, int times) {
        int before = MasteryAttachment.crafts(player, recipeId);
        Mastery masteryBefore = Mastery.of(before);
        int batch = masteryBefore == Mastery.MASTERED ? Math.clamp(times, 1, 10) : 1;

        Refusal refusal = check(player, recipeId, batch);
        if (refusal != null) {
            return Outcome.refused(refusal);
        }

        CraftRecipe recipe = HcmData.RECIPES.get(recipeId);
        PlayerInventory inventory = player.getInventory();

        // --- À partir d'ici on modifie. Tout a été vérifié : chaque retrait
        // trouvera ce qu'il cherche, et rien ne peut s'intercaler.
        for (CraftRecipe.Ingredient ingredient : recipe.ingredients()) {
            take(inventory, ingredient, ingredient.count() * batch);
        }
        if (recipe.cost() > 0) {
            take(inventory, currency(), recipe.cost() * batch);
        }

        Item resultItem = Registries.ITEM.get(recipe.result().item());
        int playerLevel = Metiers.getLevel(player, recipe.profession());
        int made = recipe.result().count() * batch;

        // --- La qualité : chaque objet fabriqué a sa chance d'être Excellent,
        // d'autant plus grande que l'artisan domine la recette. Les matières et
        // composants (« qualite »: false) restent toujours Normal.
        int excellent = 0;
        if (recipe.quality()) {
            double chance = MetiersConfig.get().qualite.chance(playerLevel, recipe.level());
            for (int i = 0; i < made; i++) {
                if (chance > 0.0D && player.getRandom().nextDouble() < chance) {
                    excellent++;
                }
            }
        }
        give(player, resultItem, made - excellent, recipe, false);
        give(player, resultItem, excellent, recipe, true);
        inventory.markDirty();

        // --- Progression.
        double xp = XpFalloff.apply(recipe.xp(), playerLevel, recipe.level()) * batch;
        int levels = xp > 0.0D ? Metiers.addXp(player, recipe.profession(), xp) : 0;

        int total = MasteryAttachment.record(player, recipeId, batch);
        return new Outcome(batch, xp, levels, total, masteryBefore, Mastery.of(total), null, excellent);
    }

    /** Le résultat tel que la recette le produit : composants de qualité et de repas posés. */
    public static ItemStack produce(CraftRecipe recipe, int count, boolean excellent) {
        return produce(recipe, count, excellent, null);
    }

    /**
     * @param registries les registres du serveur, pour décoder les composants du
     *                   résultat (le contenu d'une potion) ; {@code null} les ignore
     */
    public static ItemStack produce(CraftRecipe recipe, int count, boolean excellent, RegistryWrapper.WrapperLookup registries) {
        ItemStack stack = new ItemStack(Registries.ITEM.get(recipe.result().item()), count);
        // Les composants de la recette d'abord — une potion reçoit son contenu —,
        // la qualité et le repas par-dessus.
        if (registries != null) {
            recipe.result().decodeComponents(registries).ifPresent(stack::applyChanges);
        }
        if (excellent) {
            Quality.markExcellent(stack);
        }
        recipe.meal().ifPresent(meal -> {
            MealBuff buff = HcmData.BUFFS.get(meal.buff());
            double value = meal.value().orElse(buff != null ? buff.value() : 0.0D);
            if (buff != null) {
                MealEngine.attach(stack, meal.buff(), buff, value);
            }
        });
        return stack;
    }

    // ------------------------------------------------------------------

    /** L'objet-monnaie, ou {@code null} si le mod qui le fournit est absent. */
    public static Item currency() {
        Identifier id = MetiersConfig.get().currency();
        return Registries.ITEM.containsId(id) ? Registries.ITEM.get(id) : null;
    }

    /** Combien d'exemplaires d'un ingrédient le joueur possède, tous emplacements confondus. */
    public static int count(PlayerInventory inventory, CraftRecipe.Ingredient ingredient) {
        int total = 0;
        for (ItemStack stack : inventory.getMainStacks()) {
            if (ingredient.matches(stack)) {
                total += stack.getCount();
            }
        }
        return total;
    }

    public static int count(PlayerInventory inventory, Item item) {
        int total = 0;
        for (ItemStack stack : inventory.getMainStacks()) {
            if (stack.isOf(item)) {
                total += stack.getCount();
            }
        }
        return total;
    }

    /** Retire {@code amount} exemplaires, pile par pile, dans l'ordre de l'inventaire. */
    private static void take(PlayerInventory inventory, CraftRecipe.Ingredient ingredient, int amount) {
        take(inventory, ingredient::matches, amount);
    }

    /** Retire {@code amount} exemplaires d'un objet — la monnaie, pour la réparation. */
    public static void take(PlayerInventory inventory, Item item, int amount) {
        take(inventory, stack -> stack.isOf(item), amount);
    }

    private static void take(PlayerInventory inventory, java.util.function.Predicate<ItemStack> matches, int amount) {
        int remaining = amount;
        List<ItemStack> stacks = inventory.getMainStacks();
        for (int slot = 0; slot < stacks.size() && remaining > 0; slot++) {
            ItemStack stack = stacks.get(slot);
            if (matches.test(stack)) {
                int taken = Math.min(remaining, stack.getCount());
                stack.decrement(taken);
                remaining -= taken;
            }
        }
        if (remaining > 0) {
            // Impossible si check() a été appelé juste avant sur le même fil.
            throw new IllegalStateException("retrait incomplet : il manque " + remaining);
        }
    }

    /** Donne le résultat ; ce qui ne rentre pas tombe aux pieds du joueur. */
    private static void give(ServerPlayerEntity player, Item item, int amount, CraftRecipe recipe, boolean excellent) {
        List<ItemStack> stacks = new ArrayList<>();
        int remaining = amount;
        int max = item.getMaxCount();
        while (remaining > 0) {
            int size = Math.min(remaining, max);
            stacks.add(produce(recipe, size, excellent, player.getRegistryManager()));
            remaining -= size;
        }
        for (ItemStack stack : stacks) {
            if (!player.getInventory().insertStack(stack) || !stack.isEmpty()) {
                player.dropItem(stack, false);
            }
        }
    }
}
