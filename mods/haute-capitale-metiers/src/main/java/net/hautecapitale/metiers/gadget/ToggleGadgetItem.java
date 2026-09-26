package net.hautecapitale.metiers.gadget;

import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.ItemEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.item.tooltip.TooltipType;
import net.minecraft.component.type.TooltipDisplayComponent;
import net.minecraft.registry.Registries;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.text.Text;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Formatting;
import net.minecraft.util.Hand;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

import java.util.List;
import java.util.Map;
import java.util.function.Consumer;

/**
 * Les gadgets à interrupteur : un clic droit les allume ou les éteint, et
 * tant qu'ils sont allumés dans l'inventaire, ils travaillent.
 *
 * <ul>
 *   <li><b>Aimant à butin</b> : attire les objets au sol vers le joueur.</li>
 *   <li><b>Compresseur de poche</b> : neuf lingots, gemmes ou brut deviennent
 *       un bloc, tout seuls.</li>
 *   <li><b>Broyeur automatique</b> : détruit l'objet choisi dès qu'il entre
 *       dans l'inventaire — accroupi + clic droit avec l'objet en main gauche
 *       pour le choisir.</li>
 * </ul>
 */
public class ToggleGadgetItem extends Item {

    public enum Kind {
        AIMANT("aimant"),
        COMPRESSEUR("compresseur"),
        BROYEUR("broyeur");

        final String key;

        Kind(String key) {
            this.key = key;
        }
    }

    /** Ce que le compresseur sait compacter : neuf d'un côté, un bloc de l'autre. */
    private static final Map<Item, Block> COMPRESSIONS = Map.ofEntries(
            Map.entry(Items.IRON_INGOT, Blocks.IRON_BLOCK),
            Map.entry(Items.GOLD_INGOT, Blocks.GOLD_BLOCK),
            Map.entry(Items.COPPER_INGOT, Blocks.COPPER_BLOCK),
            Map.entry(Items.DIAMOND, Blocks.DIAMOND_BLOCK),
            Map.entry(Items.EMERALD, Blocks.EMERALD_BLOCK),
            Map.entry(Items.LAPIS_LAZULI, Blocks.LAPIS_BLOCK),
            Map.entry(Items.REDSTONE, Blocks.REDSTONE_BLOCK),
            Map.entry(Items.COAL, Blocks.COAL_BLOCK),
            Map.entry(Items.RAW_IRON, Blocks.RAW_IRON_BLOCK),
            Map.entry(Items.RAW_GOLD, Blocks.RAW_GOLD_BLOCK),
            Map.entry(Items.RAW_COPPER, Blocks.RAW_COPPER_BLOCK),
            Map.entry(Items.NETHERITE_INGOT, Blocks.NETHERITE_BLOCK));

    private final Kind kind;

    public ToggleGadgetItem(Kind kind, Settings settings) {
        super(settings.component(GadgetComponents.ACTIF, false));
        this.kind = kind;
    }

    public Kind kind() {
        return kind;
    }

    @Override
    public ActionResult use(World world, PlayerEntity player, Hand hand) {
        ItemStack stack = player.getStackInHand(hand);
        if (world.isClient()) {
            return ActionResult.SUCCESS;
        }
        if (kind == Kind.BROYEUR && player.isSneaking()) {
            ItemStack other = player.getStackInHand(hand == Hand.MAIN_HAND ? Hand.OFF_HAND : Hand.MAIN_HAND);
            if (other.isEmpty()) {
                stack.remove(GadgetComponents.FILTRE);
                Gadgets.overlay(player, Gadgets.text("hcm.gadget.broyeur.filtre_vide", "Broyeur : aucun filtre."));
            } else {
                Identifier id = Registries.ITEM.getId(other.getItem());
                stack.set(GadgetComponents.FILTRE, id);
                Gadgets.overlay(player, Gadgets.text("hcm.gadget.broyeur.filtre", "Broyeur : détruira %s.", other.getName()));
            }
            return ActionResult.CONSUME;
        }
        boolean on = !Gadgets.isOn(stack);
        stack.set(GadgetComponents.ACTIF, on);
        Gadgets.overlay(player, on
                ? Gadgets.text("hcm.gadget." + kind.key + ".on", "Allumé").formatted(Formatting.GREEN)
                : Gadgets.text("hcm.gadget." + kind.key + ".off", "Éteint").formatted(Formatting.GRAY));
        return ActionResult.CONSUME;
    }

    @Override
    public void inventoryTick(ItemStack stack, ServerWorld world, Entity entity, EquipmentSlot slot) {
        if (!(entity instanceof ServerPlayerEntity player) || !Gadgets.isOn(stack)) {
            return;
        }
        switch (kind) {
            case AIMANT -> attract(world, player);
            case COMPRESSEUR -> {
                if (world.getTime() % Gadgets.config().compresseur_ticks == 0) {
                    compress(player.getInventory());
                }
            }
            case BROYEUR -> {
                if (world.getTime() % Gadgets.config().compresseur_ticks == 0) {
                    trash(player.getInventory(), stack.get(GadgetComponents.FILTRE));
                }
            }
        }
    }

    /** L'aimant : chaque objet au sol dans le rayon glisse vers le joueur. */
    public static void attract(ServerWorld world, ServerPlayerEntity player) {
        double radius = Gadgets.config().aimant_rayon;
        Vec3d to = player.getEntityPos().add(0.0D, 0.8D, 0.0D);
        for (ItemEntity item : world.getEntitiesByClass(ItemEntity.class, player.getBoundingBox().expand(radius),
                item -> item.isAlive() && !item.cannotPickup())) {
            Vec3d pull = to.subtract(item.getEntityPos());
            double distance = pull.length();
            if (distance > 0.5D) {
                item.setVelocity(item.getVelocity().multiply(0.5D).add(pull.normalize().multiply(0.35D)));
                item.velocityDirty = true;
            }
        }
    }

    /** Le compresseur : neuf du même deviennent un bloc, une famille par passage. */
    public static boolean compress(PlayerInventory inventory) {
        for (Map.Entry<Item, Block> entry : COMPRESSIONS.entrySet()) {
            int total = 0;
            for (ItemStack stack : inventory.getMainStacks()) {
                if (stack.isOf(entry.getKey())) {
                    total += stack.getCount();
                }
            }
            if (total < 9) {
                continue;
            }
            int remaining = 9;
            for (ItemStack stack : inventory.getMainStacks()) {
                if (stack.isOf(entry.getKey()) && remaining > 0) {
                    int taken = Math.min(remaining, stack.getCount());
                    stack.decrement(taken);
                    remaining -= taken;
                }
            }
            ItemStack block = new ItemStack(entry.getValue().asItem());
            if (!inventory.insertStack(block) || !block.isEmpty()) {
                inventory.player.dropItem(block, false);
            }
            return true;
        }
        return false;
    }

    /** Le broyeur : ce qui correspond au filtre disparaît. */
    public static int trash(PlayerInventory inventory, Identifier filter) {
        if (filter == null || !Registries.ITEM.containsId(filter)) {
            return 0;
        }
        Item item = Registries.ITEM.get(filter);
        if (item == Items.AIR) {
            return 0;
        }
        int destroyed = 0;
        List<ItemStack> stacks = inventory.getMainStacks();
        for (int slot = 0; slot < stacks.size(); slot++) {
            ItemStack stack = stacks.get(slot);
            if (stack.isOf(item) && !(stack.getItem() instanceof ToggleGadgetItem)) {
                destroyed += stack.getCount();
                stacks.set(slot, ItemStack.EMPTY);
            }
        }
        return destroyed;
    }

    @Override
    public void appendTooltip(ItemStack stack, TooltipContext context, TooltipDisplayComponent display,
                              Consumer<Text> tooltip, TooltipType type) {
        tooltip.accept(Gadgets.isOn(stack)
                ? Gadgets.text("hcm.gadget.etat.on", "Allumé — clic droit pour éteindre").formatted(Formatting.GREEN)
                : Gadgets.text("hcm.gadget.etat.off", "Éteint — clic droit pour allumer").formatted(Formatting.GRAY));
        if (kind == Kind.BROYEUR) {
            Identifier filter = stack.get(GadgetComponents.FILTRE);
            tooltip.accept(filter == null
                    ? Gadgets.text("hcm.gadget.broyeur.astuce", "Accroupi + clic droit, l'objet à détruire dans l'autre main")
                            .formatted(Formatting.DARK_GRAY)
                    : Gadgets.text("hcm.gadget.broyeur.filtre_infobulle", "Détruit : %s", Text.literal(filter.toString()))
                            .formatted(Formatting.RED));
        }
    }
}
