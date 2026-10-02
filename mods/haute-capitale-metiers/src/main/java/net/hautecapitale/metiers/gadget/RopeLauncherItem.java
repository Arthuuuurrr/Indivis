package net.hautecapitale.metiers.gadget;

import net.minecraft.component.type.TooltipDisplayComponent;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.tooltip.TooltipType;
import net.minecraft.network.packet.s2c.play.EntityVelocityUpdateS2CPacket;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.text.Text;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Formatting;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

import java.util.function.Consumer;

/**
 * Le grappin : on vise un bloc, on part vers lui. Une impulsion, pas un vol —
 * la gravité reprend ensuite ses droits. Aucun bloc touché.
 */
public class RopeLauncherItem extends Item {

    public RopeLauncherItem(Settings settings) {
        super(settings);
    }

    @Override
    public ActionResult use(World world, PlayerEntity player, Hand hand) {
        if (!(world instanceof ServerWorld serverWorld) || !(player instanceof ServerPlayerEntity serverPlayer)) {
            return ActionResult.SUCCESS;
        }
        Vec3d target = aim(player);
        if (target == null) {
            Gadgets.overlay(player, Gadgets.text("hcm.gadget.grappin.rien", "Rien à accrocher à portée").formatted(Formatting.GRAY));
            return ActionResult.FAIL;
        }
        launch(serverPlayer, target);
        serverWorld.playSound(null, player.getBlockPos(), SoundEvents.ENTITY_FISHING_BOBBER_THROW, SoundCategory.PLAYERS, 1.0F, 0.7F);
        ItemStack stack = player.getStackInHand(hand);
        stack.damage(1, serverPlayer, hand == Hand.MAIN_HAND ? EquipmentSlot.MAINHAND : EquipmentSlot.OFFHAND);
        player.getItemCooldownManager().set(stack, 20);
        return ActionResult.CONSUME;
    }

    /** Le point visé, à portée du grappin, ou {@code null}. */
    public static Vec3d aim(PlayerEntity player) {
        HitResult hit = player.raycast(Gadgets.config().grappin_portee, 0.0F, false);
        if (hit.getType() != HitResult.Type.BLOCK) {
            return null;
        }
        return ((BlockHitResult) hit).getPos();
    }

    /** L'impulsion : vers le point, assez fort pour y arriver, avec un peu de hauteur. */
    public static void launch(ServerPlayerEntity player, Vec3d target) {
        Vec3d from = player.getEntityPos();
        Vec3d delta = target.subtract(from);
        double distance = delta.length();
        Vec3d direction = delta.normalize();
        double strength = Math.min(2.4D, 0.45D + distance * 0.09D);
        Vec3d velocity = direction.multiply(strength).add(0.0D, Math.min(0.9D, 0.25D + distance * 0.02D), 0.0D);
        player.setVelocity(velocity);
        player.velocityDirty = true;
        player.fallDistance = 0.0F;
        // La vélocité d'un joueur ne lui parvient pas par le suivi d'entités —
        // celui-ci ne parle qu'aux autres joueurs. Il faut la lui envoyer.
        if (player.networkHandler != null) {
            player.networkHandler.sendPacket(new EntityVelocityUpdateS2CPacket(player));
        }
    }

    @Override
    public void appendTooltip(ItemStack stack, TooltipContext context, TooltipDisplayComponent display,
                              Consumer<Text> tooltip, TooltipType type) {
        tooltip.accept(Gadgets.text("hcm.gadget.infobulle.grappin", "Clic droit : s'élance vers le bloc visé (%s blocs)",
                Text.literal(String.valueOf((int) Gadgets.config().grappin_portee))).formatted(Formatting.GRAY));
    }
}
