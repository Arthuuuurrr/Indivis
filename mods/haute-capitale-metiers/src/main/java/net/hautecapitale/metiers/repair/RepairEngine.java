package net.hautecapitale.metiers.repair;

import net.hautecapitale.metiers.HauteCapitaleMetiers;
import net.hautecapitale.metiers.config.MetiersConfig;
import net.hautecapitale.metiers.craft.CraftEngine;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.tag.TagKey;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.util.Rarity;

import java.util.ArrayList;
import java.util.List;

/**
 * La réparation universelle : n'importe quel objet à durabilité, de n'importe
 * quel mod, remis à neuf contre des Martins. Ouverte à tous — aucun métier.
 *
 * <p>Le prix se lit : durabilité manquante × coefficient de rareté, multiplié
 * si l'objet est brisé, jamais moins que le minimum. Les coefficients vivent
 * dans la configuration. La rareté est celle du composant vanilla ; la famille
 * {@code #haute_capitale_metiers:legendaire} force le coefficient légendaire.
 *
 * <p>Comme pour la fabrication : tout est revérifié au clic — la durabilité
 * réelle, le solde —, puis on débite et on répare dans le même geste. « Tout
 * réparer » est tout ou rien : si le solde ne couvre pas le total, rien ne se
 * passe.
 */
public final class RepairEngine {

    /** Objets au coefficient légendaire, quel que soit leur composant de rareté. */
    public static final TagKey<Item> LEGENDARY =
            TagKey.of(RegistryKeys.ITEM, HauteCapitaleMetiers.id("legendaire"));

    public enum Refusal {
        RIEN_A_REPARER,
        MONNAIE_ABSENTE,
        MONNAIE_INSUFFISANTE
    }

    /** Ce qu'une réparation a fait. */
    public record Outcome(int repaired, int paid, Refusal refusal) {
        public boolean succeeded() {
            return refusal == null;
        }

        static Outcome refused(Refusal refusal) {
            return new Outcome(0, 0, refusal);
        }
    }

    private RepairEngine() {
    }

    // ------------------------------------------------------------------
    // Le prix

    /** Cet objet a-t-il quelque chose à réparer ? */
    public static boolean repairable(ItemStack stack) {
        return !stack.isEmpty() && stack.isDamageable() && (stack.isDamaged() || Breakage.isBroken(stack));
    }

    public static double coefficient(ItemStack stack) {
        MetiersConfig.Reparation scale = MetiersConfig.get().reparation;
        if (stack.isIn(LEGENDARY)) {
            return scale.legendaire;
        }
        Rarity rarity = stack.getRarity();
        if (rarity == Rarity.EPIC) {
            return scale.epique;
        }
        if (rarity == Rarity.RARE) {
            return scale.rare;
        }
        if (rarity == Rarity.UNCOMMON) {
            return scale.peu_commun;
        }
        return scale.commun;
    }

    /** Le prix en Martins, ou 0 si l'objet n'a rien à réparer. */
    public static int price(ItemStack stack) {
        if (!repairable(stack)) {
            return 0;
        }
        MetiersConfig.Reparation scale = MetiersConfig.get().reparation;
        int missing = Breakage.isBroken(stack) ? stack.getMaxDamage() : stack.getDamage();
        double cost = missing * coefficient(stack);
        if (Breakage.isBroken(stack)) {
            cost *= scale.brise;
        }
        return Math.max(scale.minimum, (int) Math.ceil(cost));
    }

    // ------------------------------------------------------------------
    // L'onglet

    /** Ce que le joueur voit : ses objets réparables, avec leur prix, et son solde. */
    public static RepairData describe(ServerPlayerEntity player) {
        return describe(player, false);
    }

    /** Idem, en disant au client s'il doit ouvrir l'écran sur cet onglet. */
    public static RepairData describe(ServerPlayerEntity player, boolean suggested) {
        PlayerInventory inventory = player.getInventory();
        List<RepairData.RepairEntry> items = new ArrayList<>();
        int total = 0;
        for (int slot = 0; slot < inventory.size(); slot++) {
            ItemStack stack = inventory.getStack(slot);
            if (!repairable(stack)) {
                continue;
            }
            int price = price(stack);
            boolean broken = Breakage.isBroken(stack);
            items.add(new RepairData.RepairEntry(slot, stack.getName(),
                    broken ? stack.getMaxDamage() : stack.getDamage(), stack.getMaxDamage(), broken, price));
            total += price;
        }
        Item currency = CraftEngine.currency();
        int balance = currency == null ? 0 : CraftEngine.count(inventory, currency);
        Text currencyName = currency == null ? Text.literal(MetiersConfig.get().monnaie) : currency.getName();
        return new RepairData(balance, currencyName, items, total, suggested);
    }

    // ------------------------------------------------------------------
    // Réparer

    /** Répare l'objet d'un emplacement, contre son prix. */
    public static Outcome repair(ServerPlayerEntity player, int slot) {
        PlayerInventory inventory = player.getInventory();
        if (slot < 0 || slot >= inventory.size()) {
            return Outcome.refused(Refusal.RIEN_A_REPARER);
        }
        ItemStack stack = inventory.getStack(slot);
        if (!repairable(stack)) {
            return Outcome.refused(Refusal.RIEN_A_REPARER);
        }
        int price = price(stack);
        Refusal refusal = pay(player, price);
        if (refusal != null) {
            return Outcome.refused(refusal);
        }
        mend(stack);
        return new Outcome(1, price, null);
    }

    /** Répare tout ce qui peut l'être, ou rien du tout si le solde ne couvre pas le total. */
    public static Outcome repairAll(ServerPlayerEntity player) {
        PlayerInventory inventory = player.getInventory();
        List<Integer> slots = new ArrayList<>();
        int total = 0;
        for (int slot = 0; slot < inventory.size(); slot++) {
            ItemStack stack = inventory.getStack(slot);
            if (repairable(stack)) {
                slots.add(slot);
                total += price(stack);
            }
        }
        if (slots.isEmpty()) {
            return Outcome.refused(Refusal.RIEN_A_REPARER);
        }
        Refusal refusal = pay(player, total);
        if (refusal != null) {
            return Outcome.refused(refusal);
        }
        for (int slot : slots) {
            mend(inventory.getStack(slot));
        }
        return new Outcome(slots.size(), total, null);
    }

    private static void mend(ItemStack stack) {
        Breakage.mend(stack);
        stack.setDamage(0);
    }

    /** Vérifie puis débite. {@code null} = payé. */
    private static Refusal pay(ServerPlayerEntity player, int price) {
        if (price <= 0) {
            return null;
        }
        Item currency = CraftEngine.currency();
        if (currency == null) {
            return Refusal.MONNAIE_ABSENTE;
        }
        if (CraftEngine.count(player.getInventory(), currency) < price) {
            return Refusal.MONNAIE_INSUFFISANTE;
        }
        CraftEngine.take(player.getInventory(), currency, price);
        return null;
    }
}
