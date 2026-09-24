package net.hautecapitale.metiers.command;

import net.hautecapitale.metiers.HauteCapitaleMetiers;
import net.hautecapitale.metiers.config.MetiersConfig;
import net.hautecapitale.metiers.craft.CraftEngine;
import net.hautecapitale.metiers.data.HcmData;
import net.hautecapitale.metiers.npc.NpcRole;
import net.hautecapitale.metiers.npc.ProfessionScreenData;
import net.hautecapitale.metiers.npc.RoleGate;
import net.hautecapitale.metiers.repair.Breakage;
import net.hautecapitale.metiers.repair.BrokenComponent;
import net.hautecapitale.metiers.repair.RepairData;
import net.hautecapitale.metiers.repair.RepairEngine;
import net.hautecapitale.metiers.repair.RepairEngine.Refusal;
import net.hautecapitale.metiers.repair.RepairFeedback;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.component.type.AttributeModifiersComponent;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.registry.Registries;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.Identifier;
import net.minecraft.util.Rarity;
import net.minecraft.world.World;

import java.util.ArrayList;
import java.util.List;

/**
 * Diagnostic serveur de la réparation et des objets brisés.
 *
 * <p>Un joueur en mémoire, un vrai inventaire, de vrais objets abîmés — de
 * Minecraft et de tous les mods présents qui en ont —, la monnaie du serveur,
 * et le mixin sur {@code ItemStack} exercé par de vrais dégâts d'usure.
 */
final class RepairDiagnostic {

    private RepairDiagnostic() {
    }

    static List<String> run(MinecraftServer server, List<String> report) {
        ServerWorld world = server.getWorld(World.OVERWORLD);
        if (world == null) {
            return report;
        }
        Item currency = CraftEngine.currency();
        if (currency == null) {
            report.add("--- Réparation");
            report.add("  FAIL  la monnaie du serveur est absente : rien à tester");
            return report;
        }
        ServerPlayerEntity player = testPlayer(server, world, "RepairTest");
        List<String> previousExceptions = MetiersConfig.get().reparation.exceptions;
        try {
            scenarioPrice(report);
            scenarioBreak(world, player, report);
            scenarioRepairBroken(player, currency, report);
            scenarioRefusals(player, currency, report);
            scenarioRepairAll(player, currency, report);
            scenarioMods(player, currency, report);
            scenarioScreen(player, currency, report);
            scenarioExceptions(world, player, report);
            scenarioSmithing(player, currency, report);
        } finally {
            MetiersConfig.get().reparation.exceptions = previousExceptions;
            clear(player);
        }
        return report;
    }

    // ------------------------------------------------------------------

    /** Le barème : durabilité manquante × coefficient, brisé × 2,5, jamais gratuit. */
    private static void scenarioPrice(List<String> report) {
        section(report, "Réparation — barème");
        MetiersConfig.Reparation scale = MetiersConfig.get().reparation;
        ItemStack pickaxe = new ItemStack(Items.IRON_PICKAXE);
        check(report, "un objet neuf n'a rien à réparer, prix 0",
                () -> !RepairEngine.repairable(pickaxe) && RepairEngine.price(pickaxe) == 0);
        pickaxe.setDamage(100);
        int expected = (int) Math.ceil(100 * scale.commun);
        check(report, "pioche de fer, 100 points manquants : " + expected + " (commun × 100)",
                () -> RepairEngine.price(pickaxe) == Math.max(scale.minimum, expected));
        pickaxe.setDamage(1);
        check(report, "un point manquant : jamais moins que le minimum (" + scale.minimum + ")",
                () -> RepairEngine.price(pickaxe) == Math.max(scale.minimum, 1));

        ItemStack rare = new ItemStack(Items.IRON_PICKAXE);
        rare.set(DataComponentTypes.RARITY, Rarity.RARE);
        rare.setDamage(100);
        ItemStack epic = new ItemStack(Items.IRON_PICKAXE);
        epic.set(DataComponentTypes.RARITY, Rarity.EPIC);
        epic.setDamage(100);
        check(report, "la rareté renchérit : commun < rare < épique",
                () -> RepairEngine.price(pickaxe.copyWithCount(1)) <= RepairEngine.price(rare)
                        && RepairEngine.price(rare) < RepairEngine.price(epic)
                        && RepairEngine.price(rare) == Math.max(scale.minimum, (int) Math.ceil(100 * scale.rare)));

        ItemStack broken = new ItemStack(Items.IRON_PICKAXE);
        Breakage.markBroken(broken);
        broken.setDamage(broken.getMaxDamage() - 1);
        int brokenExpected = (int) Math.ceil(broken.getMaxDamage() * scale.commun * scale.brise);
        check(report, "brisé : toute la durabilité × " + scale.brise + " = " + brokenExpected,
                () -> RepairEngine.price(broken) == Math.max(scale.minimum, brokenExpected));
        check(report, "la famille « légendaire » est déclarée", () -> RepairEngine.LEGENDARY.id()
                .equals(HauteCapitaleMetiers.id("legendaire")));
    }

    /** Le mixin : au dernier point, l'objet se brise au lieu de disparaître. */
    private static void scenarioBreak(ServerWorld world, ServerPlayerEntity player, List<String> report) {
        section(report, "Réparation — brisé plutôt que détruit");
        clear(player);
        ItemStack pickaxe = new ItemStack(Items.IRON_PICKAXE);
        AttributeModifiersComponent originalAttributes = pickaxe.get(DataComponentTypes.ATTRIBUTE_MODIFIERS);
        check(report, "la pioche de fer se brise au lieu de disparaître (famille reparable_si_brise)",
                () -> Breakage.protects(pickaxe));
        check(report, "neuve : un outil, des attributs",
                () -> pickaxe.get(DataComponentTypes.TOOL) != null && originalAttributes != null
                        && !originalAttributes.modifiers().isEmpty());
        player.getInventory().setStack(0, pickaxe);
        pickaxe.setDamage(pickaxe.getMaxDamage() - 1);
        // L'usure vanilla : le dernier point, sans joueur (un joueur créatif n'use rien).
        pickaxe.damage(1, world, null, item -> { });
        ItemStack after = player.getInventory().getStack(0);
        check(report, "après le dernier point d'usure : toujours dans l'inventaire", () -> !after.isEmpty()
                && after.isOf(Items.IRON_PICKAXE) && after.getCount() == 1);
        check(report, "marquée brisée", () -> Breakage.isBroken(after));
        check(report, "à son dernier point de durabilité", () -> after.getDamage() == after.getMaxDamage() - 1);
        check(report, "plus d'outil : elle ne creuse plus", () -> after.get(DataComponentTypes.TOOL) == null);
        check(report, "plus d'attributs : elle ne frappe plus",
                () -> after.get(DataComponentTypes.ATTRIBUTE_MODIFIERS).modifiers().isEmpty());
        check(report, "ce qu'elle faisait est gardé dans la marque", () -> {
            BrokenComponent saved = after.get(BrokenComponent.TYPE);
            return saved != null && saved.tool().isPresent() && saved.attributes().isPresent()
                    && saved.attributes().get().equals(originalAttributes);
        });
        after.damage(5, world, null, item -> { });
        check(report, "user encore un objet brisé ne le détruit pas non plus",
                () -> !player.getInventory().getStack(0).isEmpty() && after.getDamage() == after.getMaxDamage() - 1);

        ItemStack sword = new ItemStack(Items.IRON_SWORD);
        sword.setDamage(sword.getMaxDamage() - 1);
        player.getInventory().setStack(1, sword);
        sword.damage(1, world, null, item -> { });
        check(report, "une épée brisée n'a plus d'arme ni d'attributs", () -> Breakage.isBroken(sword)
                && sword.get(DataComponentTypes.WEAPON) == null
                && sword.get(DataComponentTypes.ATTRIBUTE_MODIFIERS).modifiers().isEmpty());
        ItemStack shield = new ItemStack(Items.SHIELD);
        shield.setDamage(shield.getMaxDamage() - 1);
        player.getInventory().setStack(2, shield);
        shield.damage(1, world, null, item -> { });
        check(report, "un bouclier brisé ne pare plus", () -> Breakage.isBroken(shield)
                && shield.get(DataComponentTypes.BLOCKS_ATTACKS) == null);
    }

    /** Réparer l'objet brisé le remet en service, contre le tarif majoré. */
    private static void scenarioRepairBroken(ServerPlayerEntity player, Item currency, List<String> report) {
        section(report, "Réparation — remettre en service");
        ItemStack pickaxe = player.getInventory().getStack(0);
        int price = RepairEngine.price(pickaxe);
        give(player, currency, price + 10);
        RepairEngine.Outcome outcome = RepairEngine.repair(player, 0);
        check(report, "réparée, pour " + price + " Martins", () -> outcome.succeeded() && outcome.paid() == price);
        check(report, "les Martins sont débités au centime", () -> CraftEngine.count(player.getInventory(), currency) == 10);
        ItemStack repaired = player.getInventory().getStack(0);
        check(report, "durabilité pleine", () -> repaired.getDamage() == 0 && !repaired.isDamaged());
        check(report, "plus de marque", () -> !Breakage.isBroken(repaired));
        check(report, "l'outil est revenu", () -> repaired.get(DataComponentTypes.TOOL) != null);
        check(report, "les attributs sont revenus, à l'identique",
                () -> repaired.get(DataComponentTypes.ATTRIBUTE_MODIFIERS)
                        .equals(new ItemStack(Items.IRON_PICKAXE).get(DataComponentTypes.ATTRIBUTE_MODIFIERS)));
        check(report, "l'épée réparée frappe de nouveau", () -> {
            give(player, currency, RepairEngine.price(player.getInventory().getStack(1)));
            RepairEngine.Outcome sword = RepairEngine.repair(player, 1);
            ItemStack stack = player.getInventory().getStack(1);
            return sword.succeeded() && stack.get(DataComponentTypes.WEAPON) != null
                    && !stack.get(DataComponentTypes.ATTRIBUTE_MODIFIERS).modifiers().isEmpty();
        });
        check(report, "le bouclier réparé pare de nouveau", () -> {
            give(player, currency, RepairEngine.price(player.getInventory().getStack(2)));
            RepairEngine.Outcome shield = RepairEngine.repair(player, 2);
            return shield.succeeded() && player.getInventory().getStack(2).get(DataComponentTypes.BLOCKS_ATTACKS) != null;
        });
        check(report, "un objet neuf : « rien à réparer », rien débité", () -> {
            int before = CraftEngine.count(player.getInventory(), currency);
            RepairEngine.Outcome again = RepairEngine.repair(player, 0);
            return again.refusal() == Refusal.RIEN_A_REPARER && CraftEngine.count(player.getInventory(), currency) == before;
        });
        clear(player);
    }

    /** Solde insuffisant : rien ne se passe. */
    private static void scenarioRefusals(ServerPlayerEntity player, Item currency, List<String> report) {
        section(report, "Réparation — solde insuffisant");
        clear(player);
        ItemStack chestplate = new ItemStack(Items.DIAMOND_CHESTPLATE);
        chestplate.setDamage(300);
        player.getInventory().setStack(0, chestplate);
        int price = RepairEngine.price(chestplate);
        give(player, currency, price - 1);
        RepairEngine.Outcome outcome = RepairEngine.repair(player, 0);
        check(report, "il manque un Martin : refus", () -> outcome.refusal() == Refusal.MONNAIE_INSUFFISANTE);
        check(report, "rien n'a bougé : dégâts intacts, solde intact",
                () -> player.getInventory().getStack(0).getDamage() == 300
                        && CraftEngine.count(player.getInventory(), currency) == price - 1);
        give(player, currency, 1);
        RepairEngine.Outcome ok = RepairEngine.repair(player, 0);
        check(report, "avec le dernier Martin : réparée, solde à zéro", () -> ok.succeeded()
                && player.getInventory().getStack(0).getDamage() == 0
                && CraftEngine.count(player.getInventory(), currency) == 0);
        check(report, "emplacement vide : rien à réparer", () -> RepairEngine.repair(player, 20).refusal() == Refusal.RIEN_A_REPARER);
        check(report, "le message d'un refus existe", () -> !RepairFeedback.describe(outcome).getString().isEmpty());
        clear(player);
    }

    /** Tout réparer : le bon total, tout ou rien. */
    private static void scenarioRepairAll(ServerPlayerEntity player, Item currency, List<String> report) {
        section(report, "Réparation — tout réparer");
        clear(player);
        ItemStack a = new ItemStack(Items.IRON_PICKAXE);
        a.setDamage(50);
        ItemStack b = new ItemStack(Items.BOW);
        b.setDamage(200);
        ItemStack c = new ItemStack(Items.IRON_HELMET);
        c.setDamage(80);
        player.getInventory().setStack(3, a);
        player.getInventory().setStack(7, b);
        player.getInventory().setStack(9, c);
        int total = RepairEngine.price(a) + RepairEngine.price(b) + RepairEngine.price(c);
        RepairData data = RepairEngine.describe(player);
        check(report, "trois objets listés, total = somme des prix",
                () -> data.items().size() == 3 && data.total() == total && total > 0);
        check(report, "chaque ligne porte son emplacement et son pourcentage",
                () -> data.items().get(0).slot() == 3 && data.items().get(1).slot() == 7 && data.items().get(2).slot() == 9
                        && data.items().get(1).percent() == (int) Math.round(100.0D * (b.getMaxDamage() - 200) / b.getMaxDamage()));
        give(player, currency, total - 1);
        RepairEngine.Outcome refused = RepairEngine.repairAll(player);
        check(report, "il manque un Martin sur le total : rien du tout n'est réparé",
                () -> refused.refusal() == Refusal.MONNAIE_INSUFFISANTE && a.getDamage() == 50 && b.getDamage() == 200
                        && c.getDamage() == 80 && CraftEngine.count(player.getInventory(), currency) == total - 1);
        give(player, currency, 1);
        RepairEngine.Outcome done = RepairEngine.repairAll(player);
        check(report, "avec le total : les trois réparés, solde à zéro", () -> done.succeeded() && done.repaired() == 3
                && done.paid() == total && a.getDamage() == 0 && b.getDamage() == 0 && c.getDamage() == 0
                && CraftEngine.count(player.getInventory(), currency) == 0);
        check(report, "plus rien à réparer ensuite", () -> RepairEngine.repairAll(player).refusal() == Refusal.RIEN_A_REPARER
                && RepairEngine.describe(player).items().isEmpty());
        clear(player);
    }

    /** N'importe quel mod : tout ce qui a une durabilité ici se répare. */
    private static void scenarioMods(ServerPlayerEntity player, Item currency, List<String> report) {
        section(report, "Réparation — objets de tous les mods");
        List<String> candidates = List.of("magistuarmory:iron_sword", "magistuarmory:knight_chestplate",
                "wizards:wizard_robe", "archers:longbow", "landsoficaria:sideros_dagger", "hazennstuff:*",
                "rogues:rogue_hood", "paladins:paladin_helmet", "minecraft:iron_pickaxe", "minecraft:elytra");
        int tested = 0;
        for (String id : candidates) {
            Item item = id.endsWith("*") ? firstDamageable(id.substring(0, id.length() - 2)) : itemOrNull(id);
            if (item == null) {
                continue;
            }
            ItemStack stack = new ItemStack(item);
            if (!stack.isDamageable()) {
                continue;
            }
            clear(player);
            stack.setDamage(Math.max(1, stack.getMaxDamage() / 2));
            player.getInventory().setStack(0, stack);
            int price = RepairEngine.price(stack);
            give(player, currency, price);
            RepairEngine.Outcome outcome = RepairEngine.repair(player, 0);
            String label = Registries.ITEM.getId(item).toString();
            check(report, label + " : abîmé à moitié, prix " + price + ", réparé",
                    () -> outcome.succeeded() && stack.getDamage() == 0 && CraftEngine.count(player.getInventory(), currency) == 0);
            tested++;
        }
        report.add("      " + tested + " objet(s) de mods testé(s) sur cette installation");
        clear(player);
    }

    /** L'onglet : le rôle forgeron l'envoie, les autres non. */
    private static void scenarioScreen(ServerPlayerEntity player, Item currency, List<String> report) {
        section(report, "Réparation — l'onglet du forgeron");
        Identifier forgeronId = HauteCapitaleMetiers.id("forgeron");
        NpcRole forgeron = HcmData.ROLES.get(forgeronId);
        NpcRole couturier = HcmData.ROLES.get(HauteCapitaleMetiers.id("couturier"));
        check(report, "le rôle forgeron propose la réparation", () -> forgeron != null && forgeron.repairs());
        check(report, "le rôle couturier, non", () -> couturier != null && !couturier.repairs());
        clear(player);
        ItemStack axe = new ItemStack(Items.IRON_AXE);
        axe.setDamage(40);
        player.getInventory().setStack(4, axe);
        give(player, currency, 7);
        ProfessionScreenData screen = RoleGate.build(player, forgeronId, forgeron);
        check(report, "l'écran du forgeron porte l'onglet, sans métier Forgeron",
                () -> screen.repair().isPresent() && screen.repair().get().items().size() == 1
                        && screen.repair().get().balance() == 7 && screen.repair().get().items().get(0).slot() == 4);
        ProfessionScreenData other = RoleGate.build(player, HauteCapitaleMetiers.id("couturier"), couturier);
        check(report, "celui du couturier, non", () -> other.repair().isEmpty());
        clear(player);
    }

    /** Les exceptions de la configuration : ces objets disparaissent comme avant. */
    private static void scenarioExceptions(ServerWorld world, ServerPlayerEntity player, List<String> report) {
        section(report, "Réparation — exceptions");
        clear(player);
        MetiersConfig.get().reparation.exceptions = List.of("minecraft:shears");
        ItemStack shears = new ItemStack(Items.SHEARS);
        check(report, "les cisailles en exception ne se brisent pas", () -> !Breakage.protects(shears));
        player.getInventory().setStack(0, shears);
        shears.setDamage(shears.getMaxDamage() - 1);
        List<Item> broken = new ArrayList<>();
        shears.damage(1, world, null, broken::add);
        check(report, "au dernier point : détruites, comme en vanilla",
                () -> player.getInventory().getStack(0).isEmpty() && broken.size() == 1);
        MetiersConfig.get().reparation.exceptions = List.of();
        check(report, "hors exception, les cisailles se brisent", () -> Breakage.protects(new ItemStack(Items.SHEARS)));
        clear(player);
    }

    /** Le volet fabrication : une dague de chert, la première recette du Forgeron. */
    private static void scenarioSmithing(ServerPlayerEntity player, Item currency, List<String> report) {
        section(report, "Forgeron — fabrication");
        Identifier recipeId = HauteCapitaleMetiers.id("forgeron/dague_de_chert");
        net.hautecapitale.metiers.craft.CraftRecipe recipe = HcmData.RECIPES.get(recipeId);
        Item chert = itemOrNull("landsoficaria:chert");
        if (recipe == null || chert == null) {
            report.add("      Lands of Icaria absent : la dague de chert n'est pas fabricable ici");
            return;
        }
        clear(player);
        net.hautecapitale.metiers.api.Metiers.forget(player, net.hautecapitale.metiers.profession.Profession.FORGERON);
        give(player, chert, 2);
        give(player, Items.STICK, 1);
        check(report, "sans le métier Forgeron : refus", () -> !CraftEngine.craft(player, recipeId, 1).succeeded());
        net.hautecapitale.metiers.api.Metiers.learn(player, net.hautecapitale.metiers.profession.Profession.FORGERON);
        net.hautecapitale.metiers.craft.CraftEngine.Outcome outcome = CraftEngine.craft(player, recipeId, 1);
        Item dagger = itemOrNull("landsoficaria:chert_dagger");
        check(report, "Forgeron niveau 1 : une dague de chert contre 2 chert + 1 bâton, +15 XP",
                () -> outcome.succeeded() && dagger != null && CraftEngine.count(player.getInventory(), dagger) == 1
                        && CraftEngine.count(player.getInventory(), chert) == 0 && outcome.xpGained() == 15.0D);
        check(report, "la dague est un couteau de Dépeceur (famille couteaux)", () -> dagger != null
                && new ItemStack(dagger).isIn(net.minecraft.registry.tag.TagKey.of(net.minecraft.registry.RegistryKeys.ITEM,
                        HauteCapitaleMetiers.id("couteaux"))));
        net.hautecapitale.metiers.api.Metiers.forget(player, net.hautecapitale.metiers.profession.Profession.FORGERON);
        clear(player);
    }

    // ------------------------------------------------------------------

    private static Item itemOrNull(String id) {
        Identifier identifier = Identifier.tryParse(id);
        return identifier != null && Registries.ITEM.containsId(identifier) ? Registries.ITEM.get(identifier) : null;
    }

    private static Item firstDamageable(String namespace) {
        for (Item item : Registries.ITEM) {
            if (Registries.ITEM.getId(item).getNamespace().equals(namespace) && new ItemStack(item).isDamageable()) {
                return item;
            }
        }
        return null;
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

    private static ServerPlayerEntity testPlayer(MinecraftServer server, ServerWorld world, String name) {
        // Un FakePlayer : d'autres mods du pack envoient des paquets au joueur qui frappe, mange ou
        // réapparaît (zones musicales, attributs, accessoires) — un joueur en mémoire ordinaire ne peut pas les recevoir.
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
            report.add("  FAIL  " + label + "  (" + e + ")");
            return;
        }
        report.add((ok ? "  PASS  " : "  FAIL  ") + label);
    }

    @FunctionalInterface
    private interface Check {
        boolean test() throws Exception;
    }
}
