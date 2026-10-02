package net.hautecapitale.metiers.command;

import net.hautecapitale.metiers.HauteCapitaleMetiers;
import net.hautecapitale.metiers.api.Metiers;
import net.hautecapitale.metiers.craft.CraftEngine;
import net.hautecapitale.metiers.craft.CraftRecipe;
import net.hautecapitale.metiers.craft.Mastery;
import net.hautecapitale.metiers.craft.MasteryAttachment;
import net.hautecapitale.metiers.data.HcmData;
import net.hautecapitale.metiers.profession.Profession;
import net.minecraft.entity.ItemEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.registry.Registries;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.Box;
import net.minecraft.world.World;

import java.util.List;

/**
 * Diagnostic serveur de la fabrication.
 *
 * <p>Le point sensible du mod est la transaction : un retrait d'ingrédients sans
 * remise d'objet serait un bug grave, un objet obtenu sans retrait aussi. Ces
 * scénarios manipulent un vrai inventaire sur un joueur en mémoire et comptent,
 * après chaque opération, ce qui a été pris et ce qui a été donné.
 *
 * <p>La recette « assemblage de chutes » sert de banc d'essai : elle ne demande
 * que des cuirs de lapin vanilla et rend une peau FleshZ — fabricable sur
 * n'importe quelle installation qui a FleshZ, sans monnaie ni écorce.
 */
final class CraftDiagnostic {

    private static final Identifier SCRAPS = HauteCapitaleMetiers.id("travailleur_du_cuir/assemblage_de_chutes");
    private static final Identifier TANNING = HauteCapitaleMetiers.id("travailleur_du_cuir/tannage_simple");

    private CraftDiagnostic() {
    }

    static List<String> run(MinecraftServer server, List<String> report) {
        ServerWorld world = server.getWorld(World.OVERWORLD);
        if (world == null) {
            return report;
        }
        CraftRecipe scraps = HcmData.RECIPES.get(SCRAPS);
        Item hide = itemOrNull("fleshz:hide");
        if (scraps == null || hide == null) {
            report.add("--- Fabrication");
            report.add("  FAIL  recette d'essai ou FleshZ absents : " + SCRAPS);
            return report;
        }

        ServerPlayerEntity player = testPlayer(server, world);
        Metiers.learn(player, Profession.TRAVAILLEUR_DU_CUIR);

        scenarioFullXp(player, scraps, hide, report);
        scenarioFalloff(player, scraps, report);
        scenarioNothingConsumedOnRefusal(player, hide, report);
        scenarioSpam(player, hide, report);
        scenarioMastery(player, hide, report);
        scenarioBatch(player, hide, report);
        scenarioOverflow(player, world, hide, report);
        scenarioCurrency(player, report);

        MasteryAttachment.reset(player);
        return report;
    }

    // ------------------------------------------------------------------

    /** Au bon niveau, l'XP est entière et les comptes sont exacts. */
    private static void scenarioFullXp(ServerPlayerEntity player, CraftRecipe scraps, Item hide, List<String> report) {
        section(report, "Fabrication — au bon niveau, 100 % d'XP");
        Metiers.setLevel(player, Profession.TRAVAILLEUR_DU_CUIR, 1);
        clear(player);
        give(player, Items.RABBIT_HIDE, 7);

        double xpBefore = Metiers.getXp(player, Profession.TRAVAILLEUR_DU_CUIR);
        CraftEngine.Outcome outcome = CraftEngine.craft(player, SCRAPS, 1);

        check(report, "la fabrication réussit", outcome::succeeded);
        check(report, "trois cuirs de lapin retirés, quatre restent",
                () -> count(player, Items.RABBIT_HIDE) == 4);
        check(report, "une peau commune donnée", () -> count(player, hide) == 1);
        check(report, "l'XP accordée est celle de la recette",
                () -> Math.abs(outcome.xpGained() - scraps.xp()) < 0.001D);
        check(report, "et elle est bien créditée au métier",
                () -> Metiers.getXp(player, Profession.TRAVAILLEUR_DU_CUIR) > xpBefore
                        || Metiers.getLevel(player, Profession.TRAVAILLEUR_DU_CUIR) > 1);
        check(report, "la fabrication est comptée", () -> MasteryAttachment.crafts(player, SCRAPS) == 1);
    }

    /** +12 niveaux : 25 %. +25 : rien, mais l'objet se fabrique. */
    private static void scenarioFalloff(ServerPlayerEntity player, CraftRecipe scraps, List<String> report) {
        section(report, "Fabrication — décote d'XP");
        clear(player);
        give(player, Items.RABBIT_HIDE, 6);

        Metiers.setLevel(player, Profession.TRAVAILLEUR_DU_CUIR, scraps.level() + 12);
        CraftEngine.Outcome plus12 = CraftEngine.craft(player, SCRAPS, 1);
        check(report, "à +12 niveaux, l'XP tombe à 25 %",
                () -> plus12.succeeded() && Math.abs(plus12.xpGained() - scraps.xp() * 0.25D) < 0.001D);

        Metiers.setLevel(player, Profession.TRAVAILLEUR_DU_CUIR, scraps.level() + 25);
        double xpBefore = Metiers.getXp(player, Profession.TRAVAILLEUR_DU_CUIR);
        CraftEngine.Outcome plus25 = CraftEngine.craft(player, SCRAPS, 1);
        check(report, "à +25 niveaux, l'XP est nulle", () -> plus25.succeeded() && plus25.xpGained() == 0.0D);
        check(report, "mais l'objet se fabrique quand même",
                () -> plus25.crafted() == 1 && count(player, Items.RABBIT_HIDE) == 0);
        check(report, "et le métier n'a pas bougé",
                () -> Metiers.getXp(player, Profession.TRAVAILLEUR_DU_CUIR) == xpBefore);
    }

    /** Un refus ne touche à rien : ni ingrédient pris, ni objet donné. */
    private static void scenarioNothingConsumedOnRefusal(ServerPlayerEntity player, Item hide, List<String> report) {
        section(report, "Fabrication — refus sans rien consommer");
        Metiers.setLevel(player, Profession.TRAVAILLEUR_DU_CUIR, 1);
        clear(player);
        give(player, Items.RABBIT_HIDE, 2);

        CraftEngine.Outcome outcome = CraftEngine.craft(player, SCRAPS, 1);
        check(report, "deux cuirs sur trois : refusée pour ingrédients manquants",
                () -> outcome.refusal() == CraftEngine.Refusal.INGREDIENTS_MANQUANTS);
        check(report, "les deux cuirs sont toujours là", () -> count(player, Items.RABBIT_HIDE) == 2);
        check(report, "aucune peau n'est apparue", () -> count(player, hide) == 0);
        check(report, "rien n'est compté", () -> MasteryAttachment.crafts(player, SCRAPS) == 3);

        Metiers.setLevel(player, Profession.TRAVAILLEUR_DU_CUIR, 1);
        CraftRecipe tanning = HcmData.RECIPES.get(TANNING);
        if (tanning != null) {
            give(player, Items.RABBIT_HIDE, 1);
            CraftEngine.Outcome tooLow = CraftEngine.craft(player, TANNING, 1);
            check(report, "une recette au-dessus du niveau est refusée",
                    () -> tooLow.refusal() == CraftEngine.Refusal.NIVEAU_INSUFFISANT);
        }

        CraftEngine.Outcome unknown = CraftEngine.craft(player, HauteCapitaleMetiers.id("nulle_part/rien"), 1);
        check(report, "une recette inconnue est refusée", () -> unknown.refusal() == CraftEngine.Refusal.RECETTE_INCONNUE);
        Metiers.forget(player, Profession.TRAVAILLEUR_DU_CUIR);
        CraftEngine.Outcome noJob = CraftEngine.craft(player, SCRAPS, 1);
        check(report, "sans le métier, refusée", () -> noJob.refusal() == CraftEngine.Refusal.METIER_NON_APPRIS);
        Metiers.learn(player, Profession.TRAVAILLEUR_DU_CUIR);
    }

    /**
     * Dix demandes d'affilée avec de quoi en faire trois : exactement trois
     * réussissent. C'est le cas du client qui clique plus vite que le serveur ne
     * répond, ou qui se déconnecte au milieu — chaque demande est traitée entière
     * et revérifiée, rien ne se duplique.
     */
    private static void scenarioSpam(ServerPlayerEntity player, Item hide, List<String> report) {
        section(report, "Fabrication — dix demandes, trois possibles");
        Metiers.setLevel(player, Profession.TRAVAILLEUR_DU_CUIR, 1);
        clear(player);
        give(player, Items.RABBIT_HIDE, 9);

        int succeeded = 0;
        for (int i = 0; i < 10; i++) {
            if (CraftEngine.craft(player, SCRAPS, 1).succeeded()) {
                succeeded++;
            }
        }
        int made = succeeded;
        check(report, "exactement trois fabrications", () -> made == 3);
        check(report, "neuf cuirs consommés, zéro restant", () -> count(player, Items.RABBIT_HIDE) == 0);
        check(report, "trois peaux, pas une de plus", () -> count(player, hide) == 3);
    }

    /** À la quinzième, la recette est maîtrisée ; trop basse, elle rapporte toujours 0. */
    private static void scenarioMastery(ServerPlayerEntity player, Item hide, List<String> report) {
        section(report, "Fabrication — maîtrise");
        MasteryAttachment.reset(player);
        Metiers.setLevel(player, Profession.TRAVAILLEUR_DU_CUIR, 1);
        clear(player);
        give(player, Items.RABBIT_HIDE, 45);

        Mastery at14 = null;
        Mastery at15 = null;
        for (int i = 1; i <= 15; i++) {
            CraftEngine.Outcome outcome = CraftEngine.craft(player, SCRAPS, 1);
            if (i == 14) {
                at14 = outcome.masteryAfter();
            }
            if (i == 15) {
                at15 = outcome.masteryAfter();
            }
        }
        Mastery before = at14;
        Mastery after = at15;
        check(report, "à 14 fabrications : en apprentissage", () -> before == Mastery.LEARNING);
        check(report, "à la 15e fabrication : maîtrisée ✦", () -> after == Mastery.MASTERED);
        check(report, "le compteur dit 15", () -> MasteryAttachment.crafts(player, SCRAPS) == 15);

        Metiers.setLevel(player, Profession.TRAVAILLEUR_DU_CUIR, 30);
        give(player, Items.RABBIT_HIDE, 3);
        CraftEngine.Outcome mastered = CraftEngine.craft(player, SCRAPS, 1);
        check(report, "maîtrisée mais trop basse : toujours 0 XP",
                () -> mastered.succeeded() && mastered.xpGained() == 0.0D);
    }

    /** ×5 n'existe que pour une recette maîtrisée ; sinon la demande vaut ×1. */
    private static void scenarioBatch(ServerPlayerEntity player, Item hide, List<String> report) {
        section(report, "Fabrication — multiple");
        Metiers.setLevel(player, Profession.TRAVAILLEUR_DU_CUIR, 1);
        clear(player);
        give(player, Items.RABBIT_HIDE, 15);

        CraftEngine.Outcome five = CraftEngine.craft(player, SCRAPS, 5);
        check(report, "recette maîtrisée : ×5 fabrique cinq", () -> five.succeeded() && five.crafted() == 5);
        check(report, "quinze cuirs consommés", () -> count(player, Items.RABBIT_HIDE) == 0);
        check(report, "cinq peaux données", () -> count(player, hide) == 5);

        give(player, Items.RABBIT_HIDE, 12);
        CraftEngine.Outcome tooMany = CraftEngine.craft(player, SCRAPS, 5);
        check(report, "×5 avec douze cuirs : refusé, rien pris",
                () -> tooMany.refusal() == CraftEngine.Refusal.INGREDIENTS_MANQUANTS
                        && count(player, Items.RABBIT_HIDE) == 12);

        MasteryAttachment.reset(player);
        CraftEngine.Outcome notMastered = CraftEngine.craft(player, SCRAPS, 5);
        check(report, "recette non maîtrisée : ×5 ramené à ×1",
                () -> notMastered.succeeded() && notMastered.crafted() == 1
                        && count(player, Items.RABBIT_HIDE) == 9);
    }

    /** Inventaire plein : le résultat tombe au sol, il n'est jamais perdu. */
    private static void scenarioOverflow(ServerPlayerEntity player, ServerWorld world, Item hide, List<String> report) {
        section(report, "Fabrication — inventaire plein");
        Metiers.setLevel(player, Profession.TRAVAILLEUR_DU_CUIR, 1);
        clear(player);
        // Trois cuirs dans un emplacement, de la pierre partout ailleurs.
        give(player, Items.RABBIT_HIDE, 3);
        for (int slot = 0; slot < player.getInventory().getMainStacks().size(); slot++) {
            if (player.getInventory().getStack(slot).isEmpty()) {
                player.getInventory().setStack(slot, new ItemStack(Items.STONE, 64));
            }
        }
        player.setPosition(0.5D, 200.0D, 0.5D);

        CraftEngine.Outcome outcome = CraftEngine.craft(player, SCRAPS, 1);
        check(report, "la fabrication réussit malgré l'inventaire plein", outcome::succeeded);

        // Les trois cuirs partis libèrent un emplacement : la peau y entre.
        boolean inInventory = count(player, hide) == 1;
        List<ItemEntity> dropped = world.getEntitiesByClass(ItemEntity.class,
                new Box(-3.0D, 195.0D, -3.0D, 4.0D, 205.0D, 4.0D),
                entity -> entity.getStack().isOf(hide));
        check(report, "la peau est dans l'inventaire ou au sol, jamais perdue",
                () -> inInventory || !dropped.isEmpty());
        dropped.forEach(ItemEntity::discard);
        clear(player);
    }

    /** Le prix en monnaie, si la monnaie du serveur est là. */
    private static void scenarioCurrency(ServerPlayerEntity player, List<String> report) {
        section(report, "Fabrication — monnaie");
        Item currency = CraftEngine.currency();
        CraftRecipe tanning = HcmData.RECIPES.get(TANNING);
        Item bark = itemOrNull("farmersdelight:tree_bark");
        Item hide = itemOrNull("fleshz:hide");
        Item leather = Items.LEATHER;

        if (tanning == null || hide == null) {
            report.add("  PASS  (recette de tannage ou FleshZ absents : scénario sans objet)");
            return;
        }
        if (currency == null) {
            Metiers.setLevel(player, Profession.TRAVAILLEUR_DU_CUIR, tanning.level());
            clear(player);
            give(player, hide, 3);
            if (bark != null) {
                give(player, bark, 1);
            }
            CraftEngine.Outcome outcome = CraftEngine.craft(player, TANNING, 1);
            check(report, "monnaie absente de cette installation : recette payante refusée proprement",
                    () -> outcome.refusal() == CraftEngine.Refusal.MONNAIE_ABSENTE
                            || outcome.refusal() == CraftEngine.Refusal.INGREDIENTS_MANQUANTS);
            check(report, "et les peaux n'ont pas bougé", () -> count(player, hide) == 3);
            return;
        }
        if (bark == null) {
            report.add("  PASS  (écorce absente : Farmer's Delight n'est pas installé, tannage non testable ici)");
            return;
        }

        Metiers.setLevel(player, Profession.TRAVAILLEUR_DU_CUIR, tanning.level());
        clear(player);
        give(player, hide, 3);
        give(player, bark, 1);
        CraftEngine.Outcome broke = CraftEngine.craft(player, TANNING, 1);
        check(report, "sans monnaie : refusée", () -> broke.refusal() == CraftEngine.Refusal.MONNAIE_INSUFFISANTE);
        check(report, "peaux et écorce intactes", () -> count(player, hide) == 3 && count(player, bark) == 1);

        give(player, currency, tanning.cost() + 2);
        CraftEngine.Outcome paid = CraftEngine.craft(player, TANNING, 1);
        check(report, "avec monnaie : fabriquée", paid::succeeded);
        check(report, "le prix est débité, le reste conservé", () -> count(player, currency) == 2);
        check(report, "peaux et écorce consommées, cuir obtenu",
                () -> count(player, hide) == 0 && count(player, bark) == 0 && count(player, leather) == 1);
        clear(player);
    }

    // ------------------------------------------------------------------

    private static ServerPlayerEntity testPlayer(MinecraftServer server, ServerWorld world) {
        return DiagnosticPlayer.create(world, "CraftTest");
    }

    private static Item itemOrNull(String id) {
        Identifier identifier = Identifier.tryParse(id);
        return identifier != null && Registries.ITEM.containsId(identifier) ? Registries.ITEM.get(identifier) : null;
    }

    private static void clear(ServerPlayerEntity player) {
        player.getInventory().getMainStacks().replaceAll(stack -> ItemStack.EMPTY);
    }

    private static void give(ServerPlayerEntity player, Item item, int amount) {
        int remaining = amount;
        while (remaining > 0) {
            int size = Math.min(remaining, item.getMaxCount());
            player.getInventory().insertStack(new ItemStack(item, size));
            remaining -= size;
        }
    }

    private static int count(ServerPlayerEntity player, Item item) {
        return CraftEngine.count(player.getInventory(), item);
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
