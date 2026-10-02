package net.hautecapitale.metiers.gadget;

import net.fabricmc.fabric.api.entity.event.v1.ServerLivingEntityEvents;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.consume.UseAction;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.text.Text;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Formatting;
import net.minecraft.util.Hand;
import net.minecraft.util.math.GlobalPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

/**
 * Un objet qui téléporte après une canalisation : on maintient le clic, on
 * reste immobile, on ne prend pas de coup — et on part. Relâcher, bouger ou
 * être frappé annule tout. Un temps de recharge suit chaque départ.
 *
 * <p>La balise de rappel et la Pierre de foyer partagent cette mécanique ;
 * seules la destination et la portée changent.
 */
public abstract class ChanneledTeleportItem extends Item {

    /** Où chacun a commencé à canaliser — pour savoir s'il a bougé. */
    private static final Map<UUID, Vec3d> STARTS = new HashMap<>();

    /** Pourquoi on ne peut pas partir. */
    public enum Refusal {
        AUCUNE_DESTINATION,
        AUTRE_DIMENSION,
        TROP_LOIN
    }

    protected ChanneledTeleportItem(Settings settings) {
        super(settings);
    }

    public static void init() {
        // Un coup reçu interrompt la canalisation.
        ServerLivingEntityEvents.AFTER_DAMAGE.register((entity, source, base, taken, blocked) -> {
            if (entity instanceof ServerPlayerEntity player && taken > 0.0F && player.isUsingItem()
                    && player.getActiveItem().getItem() instanceof ChanneledTeleportItem item) {
                player.stopUsingItem();
                item.cancelled(player, Gadgets.text("hcm.gadget.teleport.annule_coup", "Canalisation interrompue : vous avez été touché"));
            }
        });
    }

    // ------------------------------------------------------------------
    // Ce que chaque objet décide

    /** La destination, ou {@code null} s'il n'y en a pas. */
    protected abstract GlobalPos destination(ServerPlayerEntity player, ItemStack stack);

    /** Secondes de canalisation. */
    protected abstract int channelSeconds();

    /** Secondes de recharge après le départ. */
    protected abstract int cooldownSeconds();

    /** Portée maximale en blocs ; 0 = illimitée. */
    protected abstract double range(ItemStack stack);

    /** Changer de dimension est-il permis ? */
    protected abstract boolean crossesDimensions();

    /** Le nom de l'objet dans les messages. */
    protected abstract String messageKey();

    // ------------------------------------------------------------------

    /** Peut-on partir d'ici ? {@code null} = oui. */
    public Refusal check(ServerPlayerEntity player, ItemStack stack) {
        GlobalPos destination = destination(player, stack);
        if (destination == null) {
            return Refusal.AUCUNE_DESTINATION;
        }
        if (!destination.dimension().equals(player.getEntityWorld().getRegistryKey())) {
            return crossesDimensions() ? null : Refusal.AUTRE_DIMENSION;
        }
        double range = range(stack);
        if (range > 0.0D && player.getEntityPos().squaredDistanceTo(Vec3d.ofBottomCenter(destination.pos())) > range * range) {
            return Refusal.TROP_LOIN;
        }
        return null;
    }

    @Override
    public ActionResult use(World world, PlayerEntity player, Hand hand) {
        ItemStack stack = player.getStackInHand(hand);
        if (!(player instanceof ServerPlayerEntity serverPlayer)) {
            // Côté client, on entre tout de suite en canalisation : sans cela le
            // client, ne se sachant pas en train d'utiliser l'objet, renvoie un
            // clic à chaque tick tant que le bouton est tenu — et chaque clic
            // relance la canalisation côté serveur, qui ne finit jamais.
            if (channelSeconds() > 0) {
                player.setCurrentHand(hand);
            }
            return ActionResult.CONSUME;
        }
        Refusal refusal = check(serverPlayer, stack);
        if (refusal != null) {
            Gadgets.overlay(player, refusalText(refusal));
            return ActionResult.FAIL;
        }
        if (channelSeconds() <= 0) {
            depart(serverPlayer, stack);
            return ActionResult.CONSUME;
        }
        STARTS.put(player.getUuid(), player.getEntityPos());
        player.setCurrentHand(hand);
        Gadgets.overlay(player, Gadgets.text("hcm.gadget.teleport.canalisation", "Téléportation dans %s s… gardez le clic droit enfoncé, sans bouger",
                Text.literal(String.valueOf(channelSeconds()))).formatted(Formatting.YELLOW));
        return ActionResult.CONSUME;
    }

    @Override
    public int getMaxUseTime(ItemStack stack, LivingEntity user) {
        return channelSeconds() * 20;
    }

    @Override
    public UseAction getUseAction(ItemStack stack) {
        return UseAction.BRUSH;
    }

    @Override
    public void usageTick(World world, LivingEntity user, ItemStack stack, int remainingTicks) {
        if (!(user instanceof ServerPlayerEntity player)) {
            return;
        }
        Vec3d start = STARTS.get(player.getUuid());
        if (start != null && player.getEntityPos().squaredDistanceTo(start) > 0.15D * 0.15D) {
            player.stopUsingItem();
            cancelled(player, Gadgets.text("hcm.gadget.teleport.annule_bouge", "Canalisation interrompue : vous avez bougé"));
            return;
        }
        if (remainingTicks % 20 == 0 && remainingTicks > 0) {
            Gadgets.overlay(player, Gadgets.text("hcm.gadget.teleport.compte", "Téléportation dans %s s…",
                    Text.literal(String.valueOf(remainingTicks / 20))).formatted(Formatting.YELLOW));
        }
    }

    @Override
    public boolean onStoppedUsing(ItemStack stack, World world, LivingEntity user, int remainingTicks) {
        if (user instanceof ServerPlayerEntity player && remainingTicks > 0 && STARTS.containsKey(player.getUuid())) {
            cancelled(player, Gadgets.text("hcm.gadget.teleport.annule_relache", "Canalisation interrompue"));
        }
        return false;
    }

    @Override
    public ItemStack finishUsing(ItemStack stack, World world, LivingEntity user) {
        if (user instanceof ServerPlayerEntity player) {
            STARTS.remove(player.getUuid());
            Refusal refusal = check(player, stack);
            if (refusal != null) {
                Gadgets.overlay(player, refusalText(refusal));
            } else {
                depart(player, stack);
            }
        }
        return stack;
    }

    /** Le départ : le son ici, le joueur là-bas, le son là-bas, la recharge. */
    public boolean depart(ServerPlayerEntity player, ItemStack stack) {
        GlobalPos destination = destination(player, stack);
        if (destination == null) {
            return false;
        }
        ServerWorld target = player.getEntityWorld().getServer().getWorld(destination.dimension());
        if (target == null) {
            Gadgets.overlay(player, refusalText(Refusal.AUTRE_DIMENSION));
            return false;
        }
        ServerWorld from = (ServerWorld) player.getEntityWorld();
        from.playSound(null, player.getBlockPos(), SoundEvents.ENTITY_ENDERMAN_TELEPORT, SoundCategory.PLAYERS, 1.0F, 1.0F);
        Vec3d to = Vec3d.ofBottomCenter(destination.pos());
        player.teleport(target, to.x, to.y, to.z, Set.of(), player.getYaw(), player.getPitch(), true);
        target.playSound(null, destination.pos(), SoundEvents.ENTITY_ENDERMAN_TELEPORT, SoundCategory.PLAYERS, 1.0F, 1.0F);
        player.getItemCooldownManager().set(stack, cooldownSeconds() * 20);
        Gadgets.overlay(player, Gadgets.text(messageKey() + ".arrive", "Vous voilà arrivé").formatted(Formatting.GREEN));
        return true;
    }

    void cancelled(ServerPlayerEntity player, Text why) {
        STARTS.remove(player.getUuid());
        Gadgets.overlay(player, why.copy().formatted(Formatting.RED));
        // Une seconde de pause : sans elle, un joueur qui garde le clic enfoncé
        // repart aussitôt et n'a pas le temps de lire pourquoi il a été interrompu.
        player.getItemCooldownManager().set(new ItemStack(this), 20);
    }

    public Text refusalText(Refusal refusal) {
        return switch (refusal) {
            case AUCUNE_DESTINATION -> Gadgets.text(messageKey() + ".aucune", "Aucune destination").formatted(Formatting.RED);
            case AUTRE_DIMENSION -> Gadgets.text(messageKey() + ".dimension", "La destination est dans un autre monde").formatted(Formatting.RED);
            case TROP_LOIN -> Gadgets.text(messageKey() + ".trop_loin",
                    "Trop faible pour vous ramener d'aussi loin").formatted(Formatting.RED);
        };
    }

    /** Pour le diagnostic : le joueur canalise-t-il ? */
    public static boolean isChanneling(ServerPlayerEntity player) {
        return STARTS.containsKey(player.getUuid());
    }
}
