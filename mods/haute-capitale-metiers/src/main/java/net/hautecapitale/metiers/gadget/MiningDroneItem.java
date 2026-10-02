package net.hautecapitale.metiers.gadget;

import net.hautecapitale.metiers.entity.HcmEntities;
import net.minecraft.component.type.TooltipDisplayComponent;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.tooltip.TooltipType;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.text.Text;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Formatting;
import net.minecraft.util.Hand;
import net.minecraft.world.World;

import java.util.UUID;
import java.util.function.Consumer;

/**
 * Le gadget du drone : clic droit pour le déployer, clic droit pour le
 * rappeler. Le gadget se souvient de son drone ; s'il disparaît — signal
 * perdu, redémarrage —, le gadget le sait au clic suivant.
 */
public class MiningDroneItem extends Item {

    public MiningDroneItem(Settings settings) {
        super(settings);
    }

    @Override
    public ActionResult use(World world, PlayerEntity player, Hand hand) {
        if (!(world instanceof ServerWorld serverWorld) || !(player instanceof ServerPlayerEntity serverPlayer)) {
            return ActionResult.SUCCESS;
        }
        ItemStack stack = player.getStackInHand(hand);
        MiningDroneEntity current = drone(serverWorld, stack);
        if (current != null) {
            current.discard();
            stack.remove(GadgetComponents.DRONE);
            Gadgets.overlay(player, Gadgets.text("hcm.gadget.drone.rappele", "Drone rappelé").formatted(Formatting.GRAY));
            return ActionResult.CONSUME;
        }
        MiningDroneEntity drone = deploy(serverWorld, serverPlayer);
        stack.set(GadgetComponents.DRONE, drone.getUuid());
        Gadgets.overlay(player, Gadgets.text("hcm.gadget.drone.deploye", "Drone déployé").formatted(Formatting.GREEN));
        return ActionResult.CONSUME;
    }

    public static MiningDroneEntity deploy(ServerWorld world, ServerPlayerEntity player) {
        MiningDroneEntity drone = new MiningDroneEntity(HcmEntities.DRONE, world);
        drone.setOwner(player.getUuid());
        drone.refreshPositionAndAngles(player.getX(), player.getY() + 1.9D, player.getZ(), player.getYaw(), 0.0F);
        world.spawnEntity(drone);
        world.playSound(null, player.getBlockPos(), SoundEvents.BLOCK_BEACON_ACTIVATE, SoundCategory.PLAYERS, 0.6F, 1.6F);
        return drone;
    }

    /** Le drone de ce gadget, s'il vole encore. */
    public static MiningDroneEntity drone(ServerWorld world, ItemStack stack) {
        UUID uuid = stack.get(GadgetComponents.DRONE);
        if (uuid == null) {
            return null;
        }
        Entity entity = world.getEntity(uuid);
        return entity instanceof MiningDroneEntity drone && drone.isAlive() ? drone : null;
    }

    /** Le maître porte-t-il encore le gadget de ce drone ? */
    public static boolean isBound(ServerPlayerEntity master, MiningDroneEntity drone) {
        for (ItemStack stack : master.getInventory().getMainStacks()) {
            if (stack.getItem() instanceof MiningDroneItem && drone.getUuid().equals(stack.get(GadgetComponents.DRONE))) {
                return true;
            }
        }
        ItemStack offhand = master.getOffHandStack();
        return offhand.getItem() instanceof MiningDroneItem && drone.getUuid().equals(offhand.get(GadgetComponents.DRONE));
    }

    /** Le drone s'en va : le gadget qui le portait l'oublie. */
    static void forget(ServerPlayerEntity master, MiningDroneEntity drone) {
        for (ItemStack stack : master.getInventory().getMainStacks()) {
            if (stack.getItem() instanceof MiningDroneItem && drone.getUuid().equals(stack.get(GadgetComponents.DRONE))) {
                stack.remove(GadgetComponents.DRONE);
            }
        }
    }

    @Override
    public void appendTooltip(ItemStack stack, TooltipContext context, TooltipDisplayComponent display,
                              Consumer<Text> tooltip, TooltipType type) {
        tooltip.accept(Gadgets.text("hcm.gadget.infobulle.drone", "Clic droit : déploie un drone qui vous suit et ramasse")
                .formatted(Formatting.GRAY));
        tooltip.accept(stack.contains(GadgetComponents.DRONE)
                ? Gadgets.text("hcm.gadget.drone.etat_on", "Drone en vol").formatted(Formatting.GREEN)
                : Gadgets.text("hcm.gadget.drone.etat_off", "Drone au repos").formatted(Formatting.DARK_GRAY));
    }
}
