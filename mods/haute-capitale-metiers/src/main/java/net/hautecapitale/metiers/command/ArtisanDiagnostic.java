package net.hautecapitale.metiers.command;

import net.fabricmc.fabric.api.entity.event.v1.ServerLivingEntityEvents;
import net.hautecapitale.metiers.HauteCapitaleMetiers;
import net.hautecapitale.metiers.api.Metiers;
import net.hautecapitale.metiers.config.MetiersConfig;
import net.hautecapitale.metiers.craft.CraftEngine;
import net.hautecapitale.metiers.craft.CraftFeedback;
import net.hautecapitale.metiers.craft.CraftRecipe;
import net.hautecapitale.metiers.data.HcmData;
import net.hautecapitale.metiers.meal.MealAttachment;
import net.hautecapitale.metiers.meal.MealBuff;
import net.hautecapitale.metiers.meal.MealConsumeEffect;
import net.hautecapitale.metiers.meal.MealEngine;
import net.hautecapitale.metiers.node.NodeEngine;
import net.hautecapitale.metiers.profession.Profession;
import net.hautecapitale.metiers.quality.Quality;
import net.hautecapitale.metiers.textile.CottonBlocks;
import net.hautecapitale.metiers.textile.TextileItems;
import net.minecraft.block.Blocks;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.component.type.ConsumableComponent;
import net.minecraft.entity.attribute.EntityAttributeInstance;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.recipe.Recipe;
import net.minecraft.registry.Registries;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import java.util.List;

/**
 * Diagnostic serveur de l'étape 11 : la qualité Excellent, les buffs de
 * repas, la chaîne textile et le cotonnier, et — quand Farmer's Delight est
 * là — un vrai plat cuisiné puis mangé.
 */
final class ArtisanDiagnostic {

    private ArtisanDiagnostic() {
    }

    static List<String> run(MinecraftServer server, List<String> report) {
        ServerWorld world = server.getWorld(World.OVERWORLD);
        if (world == null) {
            return report;
        }
        ServerPlayerEntity player = testPlayer(server, world, "ArtisanTest");
        MetiersConfig.Qualite quality = MetiersConfig.get().qualite;
        int previousGap = quality.ecart_minimum;
        double previousBase = quality.chance_base;
        double previousMax = quality.chance_max;
        try {
            scenarioCatalogue(server, report);
            scenarioQuality(player, report);
            scenarioMeal(world, player, report);
            scenarioCooking(world, player, report);
            scenarioCotton(world, player, report);
        } finally {
            quality.ecart_minimum = previousGap;
            quality.chance_base = previousBase;
            quality.chance_max = previousMax;
            MealEngine.setClock(null);
            MealEngine.clear(player);
            for (Profession profession : List.of(Profession.COUTURIER, Profession.CUISINIER, Profession.HERBORISTE)) {
                Metiers.forget(player, profession);
            }
            clear(player);
        }
        return report;
    }

    // ------------------------------------------------------------------

    private static void scenarioCatalogue(MinecraftServer server, List<String> report) {
        section(report, "Étape 11 — catalogue");
        check(report, "trente-neuf objets textiles enregistrés, et les deux cotonniers",
                () -> TextileItems.all().size() == 39
                        && Registries.BLOCK.containsId(HauteCapitaleMetiers.id("cotonnier"))
                        && Registries.BLOCK.containsId(HauteCapitaleMetiers.id("cotonnier_jeune")));
        check(report, "le composant de qualité, l'effet d'ingestion « repas » et l'effet de statut « repas » sont enregistrés",
                () -> Registries.DATA_COMPONENT_TYPE.containsId(HauteCapitaleMetiers.id("qualite"))
                        && Registries.CONSUME_EFFECT_TYPE.containsId(HauteCapitaleMetiers.id("repas"))
                        && Registries.STATUS_EFFECT.containsId(HauteCapitaleMetiers.id("repas")));
        check(report, "quinze buffs de repas chargés", () -> HcmData.BUFFS.size() == 15);
        long tailoring = HcmData.RECIPES.ids().stream().filter(id -> id.getPath().startsWith("couturier/")).count();
        long jewelry = HcmData.RECIPES.ids().stream().filter(id -> id.getPath().startsWith("joaillier/")).count();
        long cooking = HcmData.RECIPES.ids().stream().filter(id -> id.getPath().startsWith("cuisinier/")).count();
        check(report, "Couturier : au moins les trente-huit recettes de notre propre chaîne textile chargées (" + tailoring + ")",
                () -> tailoring >= 38);
        boolean fd = Registries.ITEM.containsId(Identifier.of("farmersdelight", "bone_broth"));
        boolean jw = Registries.ITEM.containsId(Identifier.of("jewelry", "ruby_ring"));
        report.add("      Farmer's Delight " + (fd ? "présent" : "absent") + " (" + cooking + " recettes de Cuisinier chargées), jewelry "
                + (jw ? "présent" : "absent") + " (" + jewelry + " recettes de Joaillier chargées)");
        if (fd) {
            check(report, "Cuisinier : les soixante recettes Farmer's Delight chargées au moins", () -> cooking >= 57);
            check(report, "la marmite ne prépare plus le ragoût de bœuf (niveau 22), mais toujours le bouillon d'os (niveau 6)",
                    () -> server.getRecipeManager().get(RegistryKey.of(RegistryKeys.RECIPE, Identifier.of("farmersdelight", "cooking/beef_stew"))).isEmpty()
                            && server.getRecipeManager().get(RegistryKey.of(RegistryKeys.RECIPE, Identifier.of("farmersdelight", "cooking/bone_broth"))).isPresent());
        }
        if (jw) {
            check(report, "Joaillier : les trente-huit recettes jewelry chargées au moins", () -> jewelry >= 38);
        }
    }

    /** La qualité : forcée à 100 %, puis à 0 %, sur un vêtement et sur une matière. */
    private static void scenarioQuality(ServerPlayerEntity player, List<String> report) {
        section(report, "Qualité — Excellent");
        MetiersConfig.Qualite quality = MetiersConfig.get().qualite;
        Identifier tank = HauteCapitaleMetiers.id("couturier/debardeur");
        Identifier spool = HauteCapitaleMetiers.id("couturier/bobine_de_coton");
        CraftRecipe tankRecipe = HcmData.RECIPES.get(tank);
        if (tankRecipe == null || HcmData.RECIPES.get(spool) == null) {
            check(report, "les recettes du débardeur et de la bobine sont chargées", () -> false);
            return;
        }
        clear(player);
        Metiers.learn(player, Profession.COUTURIER);
        Metiers.setLevel(player, Profession.COUTURIER, 50);
        quality.ecart_minimum = 0;
        quality.chance_base = 1.0D;
        quality.chance_max = 1.0D;

        give(player, TextileItems.TISSU_DE_COTON, 2);
        give(player, TextileItems.AIGUILLE_ENFILEE, 1);
        CraftEngine.Outcome sure = CraftEngine.craft(player, tank, 1);
        ItemStack excellent = find(player, TextileItems.DEBARDEUR);
        check(report, "chance 100 % : le débardeur fabriqué est Excellent, composant posé, nom doré, un Excellent annoncé",
                () -> sure.succeeded() && sure.excellent() == 1 && excellent != null && Quality.isExcellent(excellent)
                        && excellent.contains(DataComponentTypes.ITEM_NAME)
                        && CraftFeedback.describe(tankRecipe, sure).getString().contains("Excellent"));
        check(report, "l'infobulle a sa ligne", () -> Quality.label().getString().contains("Excellent"));

        give(player, TextileItems.FIBRE_DE_COTON, 4);
        give(player, TextileItems.BOBINE_VIDE, 1);
        CraftEngine.Outcome material = CraftEngine.craft(player, spool, 1);
        check(report, "la bobine (« qualite »: false) reste Normal même à 100 %",
                () -> material.succeeded() && material.excellent() == 0 && !Quality.isExcellent(find(player, TextileItems.BOBINE_DE_COTON)));

        quality.chance_base = 0.0D;
        quality.chance_max = 0.0D;
        give(player, TextileItems.TISSU_DE_COTON, 2);
        give(player, TextileItems.AIGUILLE_ENFILEE, 1);
        CraftEngine.Outcome none = CraftEngine.craft(player, tank, 1);
        check(report, "chance 0 % : un second débardeur, Normal, dans une pile à part", () -> none.succeeded() && none.excellent() == 0
                && count(player, TextileItems.DEBARDEUR) == 2 && !ItemStack.areItemsAndComponentsEqual(excellent, findNormal(player, TextileItems.DEBARDEUR)));

        quality.ecart_minimum = 3;
        quality.chance_base = 0.05D;
        quality.chance_max = 0.35D;
        Metiers.setLevel(player, Profession.COUTURIER, 9);
        check(report, "au niveau de la recette : aucune chance (écart 0 < 3)", () -> quality.chance(9, 9) == 0.0D);
        clear(player);
    }

    /** Les buffs de repas, sans plat : le moteur, par une fiche livrée. */
    private static void scenarioMeal(ServerWorld world, ServerPlayerEntity player, List<String> report) {
        section(report, "Repas — un seul buff, remplacé, retiré");
        Identifier vigueur = HauteCapitaleMetiers.id("vigueur");
        Identifier celerite = HauteCapitaleMetiers.id("celerite");
        MealBuff vigueurBuff = HcmData.BUFFS.get(vigueur);
        if (vigueurBuff == null || HcmData.BUFFS.get(celerite) == null) {
            check(report, "les buffs vigueur et célérité sont chargés", () -> false);
            return;
        }
        MealEngine.clear(player);
        long[] offset = {0L};
        MealEngine.setClock(() -> System.currentTimeMillis() + offset[0]);
        EntityAttributeInstance health = player.getAttributeInstance(EntityAttributes.MAX_HEALTH);
        EntityAttributeInstance speed = player.getAttributeInstance(EntityAttributes.MOVEMENT_SPEED);
        double baseHealth = health.getValue();

        check(report, "vigueur 5 % : le modificateur est posé, la vie maximale monte de 5 %, l'effet « Repas » est visible", () -> {
            MealEngine.apply(player, vigueur, 0.05D, 900);
            return health.hasModifier(MealEngine.MODIFIER_ID) && Math.abs(health.getValue() - baseHealth * 1.05D) < 1e-6
                    && player.hasStatusEffect(MealEngine.REPAS) && MealAttachment.of(player) != null;
        });
        check(report, "un second plat (célérité) remplace le premier : la vie retombe, la vitesse monte", () -> {
            MealEngine.apply(player, celerite, 0.05D, 900);
            return !health.hasModifier(MealEngine.MODIFIER_ID) && speed.hasModifier(MealEngine.MODIFIER_ID)
                    && MealAttachment.of(player).buff().equals(celerite);
        });
        check(report, "mourir retire le repas (crochet AFTER_DEATH)", () -> {
            ServerLivingEntityEvents.AFTER_DEATH.invoker().afterDeath(player, world.getDamageSources().generic());
            return !speed.hasModifier(MealEngine.MODIFIER_ID) && MealAttachment.of(player) == null && !player.hasStatusEffect(MealEngine.REPAS);
        });
        check(report, "l'échéance passée, le tick retire le buff", () -> {
            MealEngine.apply(player, vigueur, 0.05D, 60);
            offset[0] = 61_000L;
            MealEngine.tick(player);
            boolean gone = !health.hasModifier(MealEngine.MODIFIER_ID) && MealAttachment.of(player) == null;
            offset[0] = 0L;
            return gone;
        });
        check(report, "l'effet visible retiré (lait, commande) : le tick retire le buff avec lui", () -> {
            MealEngine.apply(player, vigueur, 0.05D, 900);
            player.removeStatusEffect(MealEngine.REPAS);
            MealEngine.tick(player);
            return !health.hasModifier(MealEngine.MODIFIER_ID) && MealAttachment.of(player) == null;
        });
        check(report, "à la reconnexion, le modificateur est reposé depuis l'attachement", () -> {
            MealEngine.apply(player, vigueur, 0.05D, 900);
            health.removeModifier(MealEngine.MODIFIER_ID);
            MealEngine.restore(player);
            return health.hasModifier(MealEngine.MODIFIER_ID);
        });
        MealEngine.clear(player);

        ItemStack bread = new ItemStack(Items.BREAD);
        MealEngine.attach(bread, vigueur, vigueurBuff, 0.06D);
        MealConsumeEffect effect = MealEngine.effectOf(bread);
        check(report, "un pain reçoit l'effet « repas » à côté de son composant consommable", () -> effect != null
                && effect.buff().equals(vigueur) && effect.value() == 0.06D && effect.seconds() == 900
                && bread.get(DataComponentTypes.CONSUMABLE) != null);
        check(report, "le manger (effet d'ingestion) pose le buff à 6 %", () -> {
            boolean applied = effect.onConsume(world, bread, player);
            return applied && Math.abs(health.getValue() - baseHealth * 1.06D) < 1e-6;
        });
        ItemStack excellent = bread.copy();
        Quality.markExcellent(excellent);
        check(report, "le même pain Excellent : 6 % × 4/3 = 8 %", () -> {
            MealEngine.effectOf(excellent).onConsume(world, excellent, player);
            return Math.abs(health.getValue() - baseHealth * 1.08D) < 1e-6;
        });
        ItemStack apple = new ItemStack(Items.GOLDEN_APPLE);
        int vanillaEffects = apple.get(DataComponentTypes.CONSUMABLE).onConsumeEffects().size();
        MealEngine.attach(apple, vigueur, vigueurBuff, 0.05D);
        check(report, "les effets vanilla d'un plat (pomme dorée) sont conservés à côté du nôtre", () -> {
            ConsumableComponent consumable = apple.get(DataComponentTypes.CONSUMABLE);
            return consumable.onConsumeEffects().size() == vanillaEffects + 1;
        });
        check(report, "attacher deux fois ne double pas l'effet", () -> {
            MealEngine.attach(apple, celerite, HcmData.BUFFS.get(celerite), 0.05D);
            return apple.get(DataComponentTypes.CONSUMABLE).onConsumeEffects().size() == vanillaEffects + 1
                    && MealEngine.effectOf(apple).buff().equals(celerite);
        });
        check(report, "l'infobulle décrit le buff : titre, valeur, attribut, durée",
                () -> MealEngine.describe(effect, 0.06D).getString().contains("6 %"));
        MealEngine.clear(player);
    }

    /** Avec Farmer's Delight : un vrai bouillon d'os cuisiné par le PNJ, mangé, comparé au bouillon de marmite. */
    private static void scenarioCooking(ServerWorld world, ServerPlayerEntity player, List<String> report) {
        section(report, "Cuisinier — un plat cuisiné");
        Identifier broth = HauteCapitaleMetiers.id("cuisinier/bouillon_d_os");
        CraftRecipe recipe = HcmData.RECIPES.get(broth);
        if (recipe == null) {
            report.add("      Farmer's Delight absent : le bouillon d'os n'est pas fabricable ici");
            return;
        }
        clear(player);
        MealEngine.clear(player);
        Metiers.learn(player, Profession.CUISINIER);
        Metiers.setLevel(player, Profession.CUISINIER, 6);
        for (CraftRecipe.Ingredient ingredient : recipe.ingredients()) {
            Item item = ingredient.item().map(Registries.ITEM::get).orElseGet(() -> firstOfTag(ingredient));
            if (item != null) {
                give(player, item, ingredient.count());
            }
        }
        CraftEngine.Outcome outcome = CraftEngine.craft(player, broth, 1);
        Item bowl = Registries.ITEM.get(recipe.result().item());
        ItemStack dish = find(player, bowl);
        check(report, "Cuisinier niveau 6 : un bouillon d'os fabriqué, +30 XP, porteur de l'effet « repas » (convalescence)",
                () -> outcome.succeeded() && outcome.xpGained() == 30.0D && dish != null && MealEngine.effectOf(dish) != null
                        && MealEngine.effectOf(dish).buff().equals(HauteCapitaleMetiers.id("regeneration")));
        check(report, "le bouillon garde ses effets Farmer's Delight (confort) à côté", () -> {
            ConsumableComponent vanilla = new ItemStack(bowl).get(DataComponentTypes.CONSUMABLE);
            ConsumableComponent ours = dish.get(DataComponentTypes.CONSUMABLE);
            return vanilla != null && ours != null && ours.onConsumeEffects().size() == vanilla.onConsumeEffects().size() + 1;
        });
        check(report, "un bouillon de marmite (pile neuve) ne porte rien", () -> MealEngine.effectOf(new ItemStack(bowl)) == null);
        check(report, "les deux bouillons ne s'empilent pas ensemble", () -> !ItemStack.areItemsAndComponentsEqual(dish, new ItemStack(bowl)));
        check(report, "le manger pose le buff de convalescence — si Puffish Attributes est là, le modificateur ; sinon l'attachement seul", () -> {
            MealEngine.effectOf(dish).onConsume(world, dish, player);
            MealAttachment current = MealAttachment.of(player);
            return current != null && current.buff().equals(HauteCapitaleMetiers.id("regeneration")) && player.hasStatusEffect(MealEngine.REPAS);
        });
        MealEngine.clear(player);
        clear(player);
    }

    /** Le cotonnier : posé par le moteur de nodes, récolté par un Herboriste, il donne du coton et repousse. */
    private static void scenarioCotton(ServerWorld world, ServerPlayerEntity player, List<String> report) {
        section(report, "Nodes — le cotonnier");
        Identifier cotton = HauteCapitaleMetiers.id("herboriste/coton");
        if (HcmData.NODES.get(cotton) == null) {
            check(report, "le type de node coton est chargé", () -> false);
            return;
        }
        BlockPos spawn = world.getSpawnPoint().getPos();
        BlockPos pos = new BlockPos(spawn.getX(), Math.min(world.getTopYInclusive() - 12, 240), spawn.getZ()).north(14).west(14);
        if (!world.isPosLoaded(pos) || !world.isAir(pos)) {
            report.add("      terrain d'essai indisponible en " + pos.toShortString() + " : cotonnier non testé");
            return;
        }
        clear(player);
        Metiers.learn(player, Profession.HERBORISTE);
        Metiers.setLevel(player, Profession.HERBORISTE, 3);
        player.setPosition(pos.getX() + 0.5D, pos.getY(), pos.getZ() + 0.5D);
        try {
            check(report, "poser le cotonnier : le bloc du mod apparaît", () -> NodeEngine.place(world, pos, cotton) == null
                    && world.getBlockState(pos).isOf(CottonBlocks.COTONNIER));
            check(report, "deux coups à la main : récolté, du coton (2 à 4) dans le sac, jeune cotonnier à la place", () -> {
                NodeEngine.hit(player, world, pos);
                NodeEngine.HitOutcome second = NodeEngine.hit(player, world, pos);
                int got = count(player, TextileItems.COTON);
                return second.harvested() && got >= 2 && got <= 4 && world.getBlockState(pos).isOf(CottonBlocks.COTONNIER_JEUNE);
            });
            check(report, "le jeune cotonnier tient sans sol (un node ne tombe jamais tout seul)",
                    () -> world.getBlockState(pos).canPlaceAt(world, pos));
        } finally {
            NodeEngine.remove(world, pos);
            world.setBlockState(pos, Blocks.AIR.getDefaultState());
            Metiers.forget(player, Profession.HERBORISTE);
        }
    }

    // ------------------------------------------------------------------

    private static Item firstOfTag(CraftRecipe.Ingredient ingredient) {
        return ingredient.tag().flatMap(tag -> Registries.ITEM.getOptional(tag))
                .map(entries -> entries.stream().findFirst().map(net.minecraft.registry.entry.RegistryEntry::value).orElse(null))
                .orElse(null);
    }

    private static ItemStack find(ServerPlayerEntity player, Item item) {
        for (ItemStack stack : player.getInventory().getMainStacks()) {
            if (stack.isOf(item)) {
                return stack;
            }
        }
        return null;
    }

    private static ItemStack findNormal(ServerPlayerEntity player, Item item) {
        for (ItemStack stack : player.getInventory().getMainStacks()) {
            if (stack.isOf(item) && !Quality.isExcellent(stack)) {
                return stack;
            }
        }
        return ItemStack.EMPTY;
    }

    private static int count(ServerPlayerEntity player, Item item) {
        return CraftEngine.count(player.getInventory(), item);
    }

    private static void give(ServerPlayerEntity player, Item item, int amount) {
        int remaining = amount;
        while (remaining > 0) {
            int size = Math.min(remaining, item.getMaxCount());
            player.getInventory().insertStack(new ItemStack(item, size));
            remaining -= size;
        }
    }

    private static void clear(ServerPlayerEntity player) {
        for (int slot = 0; slot < player.getInventory().size(); slot++) {
            player.getInventory().setStack(slot, ItemStack.EMPTY);
        }
    }

    /** Un FakePlayer de Fabric : les effets de statut envoient des paquets, qu'il sait avaler. */
    private static ServerPlayerEntity testPlayer(MinecraftServer server, ServerWorld world, String name) {
        return DiagnosticPlayer.create(world, name);
    }

    private static void section(List<String> report, String title) {
        report.add("--- " + title);
    }

    private static void check(List<String> report, String label, Check condition) {
        boolean ok;
        try {
            ok = condition.test();
        } catch (Exception e) {
            StackTraceElement[] trace = e.getStackTrace();
            report.add("  FAIL  " + label + "  (" + e + (trace.length > 0 ? " — " + trace[0] : "") + ")");
            return;
        }
        report.add((ok ? "  PASS  " : "  FAIL  ") + label);
    }

    @FunctionalInterface
    private interface Check {
        boolean test() throws Exception;
    }
}
