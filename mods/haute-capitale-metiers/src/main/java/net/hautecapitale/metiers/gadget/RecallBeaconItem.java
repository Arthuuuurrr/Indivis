package net.hautecapitale.metiers.gadget;

import net.minecraft.component.type.TooltipDisplayComponent;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.tooltip.TooltipType;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Formatting;
import net.minecraft.util.Hand;
import net.minecraft.util.math.GlobalPos;
import net.minecraft.world.World;

import java.util.function.Consumer;

/**
 * La balise de rappel : accroupi + clic droit, elle retient l'endroit ;
 * clic droit, elle y ramène — après canalisation, dans le même monde, à
 * portée. L'endroit voyage avec l'objet, dans ses données.
 */
public class RecallBeaconItem extends ChanneledTeleportItem {

    public RecallBeaconItem(Settings settings) {
        super(settings);
    }

    @Override
    public ActionResult use(World world, PlayerEntity player, Hand hand) {
        if (player.isSneaking()) {
            if (player instanceof ServerPlayerEntity serverPlayer) {
                ItemStack stack = player.getStackInHand(hand);
                stack.set(GadgetComponents.POSITION, GlobalPos.create(world.getRegistryKey(), player.getBlockPos()));
                Gadgets.overlay(player, Gadgets.text("hcm.gadget.rappel.enregistre", "Position enregistrée : %s",
                        Text.literal(player.getBlockPos().toShortString())).formatted(Formatting.GREEN));
            }
            return ActionResult.CONSUME;
        }
        return super.use(world, player, hand);
    }

    @Override
    protected GlobalPos destination(ServerPlayerEntity player, ItemStack stack) {
        return stack.get(GadgetComponents.POSITION);
    }

    @Override
    protected int channelSeconds() {
        return Gadgets.config().rappel_canalisation_secondes;
    }

    @Override
    protected int cooldownSeconds() {
        return Gadgets.config().rappel_recharge_secondes;
    }

    @Override
    protected double range(ItemStack stack) {
        return Gadgets.config().rappel_portee;
    }

    @Override
    protected boolean crossesDimensions() {
        return false;
    }

    @Override
    protected String messageKey() {
        return "hcm.gadget.rappel";
    }

    @Override
    public void appendTooltip(ItemStack stack, TooltipContext context, TooltipDisplayComponent display,
                              Consumer<Text> tooltip, TooltipType type) {
        GlobalPos position = stack.get(GadgetComponents.POSITION);
        tooltip.accept(position == null
                ? Gadgets.text("hcm.gadget.rappel.vide", "Aucune position — accroupi + clic droit pour retenir l'endroit").formatted(Formatting.GRAY)
                : Gadgets.text("hcm.gadget.rappel.position", "Position retenue : %s — maintenez le clic droit 4 s pour y retourner",
                        Text.literal(position.pos().toShortString()))
                        .formatted(Formatting.AQUA));
    }
}
