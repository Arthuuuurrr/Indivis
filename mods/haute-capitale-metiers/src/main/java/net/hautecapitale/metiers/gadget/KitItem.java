package net.hautecapitale.metiers.gadget;

import net.minecraft.component.type.TooltipDisplayComponent;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.item.tooltip.TooltipType;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.text.Text;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Formatting;
import net.minecraft.util.Hand;
import net.minecraft.world.World;

import java.util.List;
import java.util.Map;
import java.util.function.Consumer;

/**
 * Les kits : un clic droit, un effet, une recharge.
 *
 * <ul>
 *   <li><b>Kit de secours</b> : soigne et purge les effets néfastes. Consommé.</li>
 *   <li><b>Kit de campement</b> : un moment de repos — régénération, saturation,
 *       résistance — sans poser un seul bloc. La version de MBK montait un camp ;
 *       ici le camp est dans le kit. Consommé.</li>
 *   <li><b>Kit de raffinage</b> : pendant une minute, le minerai brut de
 *       l'inventaire devient lingot, un par seconde. Réutilisable, avec recharge.</li>
 * </ul>
 */
public class KitItem extends Item {

    public enum Kind {
        SECOURS,
        CAMPEMENT,
        RAFFINAGE
    }

    private static final Map<Item, Item> REFINING = Map.of(
            Items.RAW_IRON, Items.IRON_INGOT,
            Items.RAW_COPPER, Items.COPPER_INGOT,
            Items.RAW_GOLD, Items.GOLD_INGOT);

    private final Kind kind;

    public KitItem(Kind kind, Settings settings) {
        super(settings);
        this.kind = kind;
    }

    public Kind kind() {
        return kind;
    }

    @Override
    public ActionResult use(World world, PlayerEntity player, Hand hand) {
        if (!(world instanceof ServerWorld)) {
            return ActionResult.SUCCESS;
        }
        ItemStack stack = player.getStackInHand(hand);
        switch (kind) {
            case SECOURS -> {
                recover(player);
                player.getItemCooldownManager().set(stack, Gadgets.config().secours_recharge_secondes * 20);
                if (!player.isCreative()) {
                    stack.decrement(1);
                }
                Gadgets.overlay(player, Gadgets.text("hcm.gadget.secours.fait", "Soigné et purgé").formatted(Formatting.GREEN));
            }
            case CAMPEMENT -> {
                camp(player);
                player.getItemCooldownManager().set(stack, Gadgets.config().campement_recharge_secondes * 20);
                if (!player.isCreative()) {
                    stack.decrement(1);
                }
                Gadgets.overlay(player, Gadgets.text("hcm.gadget.campement.fait", "Un moment de repos…").formatted(Formatting.GREEN));
            }
            case RAFFINAGE -> {
                long until = System.currentTimeMillis() + Gadgets.config().raffinage_secondes * 1000L;
                stack.set(GadgetComponents.ECHEANCE, until);
                player.getItemCooldownManager().set(stack, Gadgets.config().raffinage_secondes * 20);
                Gadgets.overlay(player, Gadgets.text("hcm.gadget.raffinage.actif", "Raffinage actif pendant %s s",
                        Text.literal(String.valueOf(Gadgets.config().raffinage_secondes))).formatted(Formatting.GOLD));
            }
        }
        return ActionResult.CONSUME;
    }

    /** Vie pleine, faim comblée, effets néfastes retirés. */
    public static void recover(PlayerEntity player) {
        player.setHealth(player.getMaxHealth());
        player.getHungerManager().add(8, 0.8F);
        for (StatusEffectInstance effect : List.copyOf(player.getStatusEffects())) {
            if (!effect.getEffectType().value().isBeneficial()) {
                player.removeStatusEffect(effect.getEffectType());
            }
        }
        player.extinguish();
    }

    public static void camp(PlayerEntity player) {
        int ticks = Gadgets.config().campement_secondes * 20;
        player.addStatusEffect(new StatusEffectInstance(StatusEffects.REGENERATION, ticks, 1));
        player.addStatusEffect(new StatusEffectInstance(StatusEffects.SATURATION, 40, 0));
        player.addStatusEffect(new StatusEffectInstance(StatusEffects.RESISTANCE, ticks, 0));
    }

    @Override
    public void inventoryTick(ItemStack stack, ServerWorld world, Entity entity, EquipmentSlot slot) {
        if (kind != Kind.RAFFINAGE || !(entity instanceof ServerPlayerEntity player) || world.getTime() % 20 != 0) {
            return;
        }
        Long until = stack.get(GadgetComponents.ECHEANCE);
        if (until == null) {
            return;
        }
        if (System.currentTimeMillis() >= until) {
            stack.remove(GadgetComponents.ECHEANCE);
            Gadgets.overlay(player, Gadgets.text("hcm.gadget.raffinage.fini", "Raffinage terminé").formatted(Formatting.GRAY));
            return;
        }
        refineOne(player.getInventory());
    }

    /** Un brut devient un lingot. {@code true} s'il y avait quelque chose à raffiner. */
    public static boolean refineOne(PlayerInventory inventory) {
        for (ItemStack stack : inventory.getMainStacks()) {
            Item ingot = REFINING.get(stack.getItem());
            if (ingot != null && !stack.isEmpty()) {
                stack.decrement(1);
                ItemStack result = new ItemStack(ingot);
                if (!inventory.insertStack(result) || !result.isEmpty()) {
                    inventory.player.dropItem(result, false);
                }
                return true;
            }
        }
        return false;
    }

    @Override
    public void appendTooltip(ItemStack stack, TooltipContext context, TooltipDisplayComponent display,
                              Consumer<Text> tooltip, TooltipType type) {
        String fallback = switch (kind) {
            case SECOURS -> "Clic droit : soigne et purge les effets néfastes";
            case CAMPEMENT -> "Clic droit : régénération, saturation, résistance";
            case RAFFINAGE -> "Clic droit : raffine le minerai brut du sac pendant une minute";
        };
        tooltip.accept(Gadgets.text("hcm.gadget.infobulle." + kind.name().toLowerCase(java.util.Locale.ROOT), fallback)
                .formatted(Formatting.GRAY));
        if (kind == Kind.RAFFINAGE && stack.contains(GadgetComponents.ECHEANCE)) {
            tooltip.accept(Gadgets.text("hcm.gadget.raffinage.en_cours", "Raffinage en cours").formatted(Formatting.GOLD));
        }
    }
}
