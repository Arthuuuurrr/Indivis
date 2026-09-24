package net.hautecapitale.metiers.gadget;

import net.minecraft.component.type.TooltipDisplayComponent;
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
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.Heightmap;
import net.minecraft.world.World;

import java.util.function.Consumer;

/**
 * La corde d'évasion : hors des grottes et des bâtiments, vers le point de
 * surface le plus proche. Une durabilité, une recharge, et jamais sous terre :
 * si le joueur est déjà à l'air libre, la corde ne sert à rien.
 *
 * <p>Idée reprise de GAG (MIT, MaxNeedsSnacks) ; réécrite pour Fabric.
 */
public class EscapeRopeItem extends Item {

    public EscapeRopeItem(Settings settings) {
        super(settings);
    }

    @Override
    public ActionResult use(World world, PlayerEntity player, Hand hand) {
        if (!(world instanceof ServerWorld serverWorld) || !(player instanceof ServerPlayerEntity serverPlayer)) {
            return ActionResult.SUCCESS;
        }
        BlockPos surface = surfaceAbove(serverWorld, player.getBlockPos());
        if (surface == null) {
            Gadgets.overlay(player, Gadgets.text("hcm.gadget.evasion.rien", "Déjà à l'air libre : la corde n'a nulle part où mener")
                    .formatted(Formatting.GRAY));
            return ActionResult.FAIL;
        }
        ItemStack stack = player.getStackInHand(hand);
        serverWorld.playSound(null, player.getBlockPos(), SoundEvents.ENTITY_ENDERMAN_TELEPORT, SoundCategory.PLAYERS, 0.8F, 1.2F);
        serverPlayer.teleport(serverWorld, surface.getX() + 0.5D, surface.getY(), surface.getZ() + 0.5D,
                java.util.Set.of(), player.getYaw(), player.getPitch(), true);
        serverWorld.playSound(null, surface, SoundEvents.ENTITY_ENDERMAN_TELEPORT, SoundCategory.PLAYERS, 0.8F, 1.2F);
        stack.damage(1, serverPlayer, hand == Hand.MAIN_HAND ? net.minecraft.entity.EquipmentSlot.MAINHAND
                : net.minecraft.entity.EquipmentSlot.OFFHAND);
        player.getItemCooldownManager().set(stack, Gadgets.config().evasion_recharge_secondes * 20);
        Gadgets.overlay(player, Gadgets.text("hcm.gadget.evasion.fait", "À l'air libre").formatted(Formatting.GREEN));
        return ActionResult.CONSUME;
    }

    /**
     * Le premier bloc à ciel ouvert au-dessus du joueur, ou {@code null} s'il
     * y est déjà. On monte droit : la corde ne cherche pas, elle remonte.
     */
    public static BlockPos surfaceAbove(ServerWorld world, BlockPos from) {
        BlockPos top = world.getTopPosition(Heightmap.Type.MOTION_BLOCKING_NO_LEAVES, from);
        if (top.getY() <= from.getY()) {
            return null;
        }
        return top;
    }

    @Override
    public void appendTooltip(ItemStack stack, TooltipContext context, TooltipDisplayComponent display,
                              Consumer<Text> tooltip, TooltipType type) {
        tooltip.accept(Gadgets.text("hcm.gadget.infobulle.evasion", "Clic droit : remonte à la surface, droit au-dessus")
                .formatted(Formatting.GRAY));
    }
}
