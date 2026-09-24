package net.hautecapitale.metiers.gadget;

import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.LootableContainerBlockEntity;
import net.minecraft.component.type.TooltipDisplayComponent;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.tooltip.TooltipType;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.registry.tag.BlockTags;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.world.chunk.Chunk;

import java.util.function.Consumer;

/**
 * Les gadgets qui agissent tant qu'on les porte — sur la tête, aux pieds,
 * ou simplement dans le sac.
 *
 * <ul>
 *   <li><b>Lunettes nocturnes</b> (tête) : vision nocturne.</li>
 *   <li><b>Casque de mineur</b> (tête) : vision nocturne, mais seulement dans
 *       l'obscurité.</li>
 *   <li><b>Lanterne de sac</b> (dans le sac) : idem — la version de MBK posait
 *       des blocs de lumière ; ici elle éclaire le porteur, pas la carte.</li>
 *   <li><b>Lunettes de trésor</b> (tête) : les coffres proches se signalent par
 *       une lueur.</li>
 *   <li><b>Lunettes de prospection</b> (tête) : une impulsion périodique compte
 *       les minerais alentour.</li>
 *   <li><b>Bottes de lave</b> (pieds) : résistance au feu — la version de MBK
 *       posait du magma sur la lave ; ici on marche à côté, pas dessus.</li>
 *   <li><b>Exosquelette de mineur</b> (quatre pièces) : le set complet donne
 *       la célérité, la résistance au recul, et la vision nocturne sous terre.</li>
 * </ul>
 */
public class WornGadgetItem extends Item {

    public enum Kind {
        LUNETTES_NOCTURNES(EquipmentSlot.HEAD),
        CASQUE_DE_MINEUR(EquipmentSlot.HEAD),
        LANTERNE_DE_SAC(null),
        LUNETTES_DE_TRESOR(EquipmentSlot.HEAD),
        LUNETTES_DE_PROSPECTION(EquipmentSlot.HEAD),
        BOTTES_DE_LAVE(EquipmentSlot.FEET),
        EXOSQUELETTE(null);

        final EquipmentSlot slot;

        Kind(EquipmentSlot slot) {
            this.slot = slot;
        }
    }

    private final Kind kind;

    public WornGadgetItem(Kind kind, Settings settings) {
        super(settings);
        this.kind = kind;
    }

    public Kind kind() {
        return kind;
    }

    @Override
    public void inventoryTick(ItemStack stack, ServerWorld world, Entity entity, EquipmentSlot slot) {
        if (!(entity instanceof ServerPlayerEntity player)) {
            return;
        }
        boolean worn = kind.slot == null || slot == kind.slot;
        if (!worn) {
            return;
        }
        switch (kind) {
            case LUNETTES_NOCTURNES -> Gadgets.keepEffect(player, StatusEffects.NIGHT_VISION, 0);
            case CASQUE_DE_MINEUR, LANTERNE_DE_SAC -> {
                if (Gadgets.isDark(world, player)) {
                    Gadgets.keepEffect(player, StatusEffects.NIGHT_VISION, 0);
                }
            }
            case BOTTES_DE_LAVE -> Gadgets.keepEffect(player, StatusEffects.FIRE_RESISTANCE, 0);
            case LUNETTES_DE_TRESOR -> {
                if (world.getTime() % 20 == 0) {
                    revealChests(world, player);
                }
            }
            case LUNETTES_DE_PROSPECTION -> {
                if (world.getTime() % 200 == 0) {
                    int ores = ScannerItem.countOres(world, player.getBlockPos(), Gadgets.config().scanner_rayon);
                    Gadgets.overlay(player, ores == 0
                            ? Gadgets.text("hcm.gadget.prospection.rien", "Impulsion : aucun minerai alentour").formatted(Formatting.GRAY)
                            : Gadgets.text("hcm.gadget.prospection.trouve", "Impulsion : %s bloc(s) de minerai alentour",
                                    Text.literal(String.valueOf(ores))).formatted(Formatting.AQUA));
                }
            }
            case EXOSQUELETTE -> {
                if (Gadgets.config().exosquelette_bonus && slot != null && slot.isArmorSlot() && fullExosuit(player)) {
                    Gadgets.keepEffect(player, StatusEffects.HASTE, 0);
                    Gadgets.keepEffect(player, StatusEffects.RESISTANCE, 0);
                    if (player.getBlockPos().getY() < world.getSeaLevel() && Gadgets.isDark(world, player)) {
                        Gadgets.keepEffect(player, StatusEffects.NIGHT_VISION, 0);
                    }
                }
            }
        }
    }

    /** Les quatre pièces sont-elles portées ? */
    public static boolean fullExosuit(ServerPlayerEntity player) {
        for (EquipmentSlot slot : new EquipmentSlot[]{EquipmentSlot.HEAD, EquipmentSlot.CHEST, EquipmentSlot.LEGS, EquipmentSlot.FEET}) {
            if (!(player.getEquippedStack(slot).getItem() instanceof WornGadgetItem worn) || worn.kind != Kind.EXOSQUELETTE) {
                return false;
            }
        }
        return true;
    }

    /** Une lueur sur chaque coffre du rayon — visible par tous, mais c'est le porteur qui regarde. */
    public static int revealChests(ServerWorld world, ServerPlayerEntity player) {
        int radius = Gadgets.config().tresor_rayon;
        BlockPos center = player.getBlockPos();
        int found = 0;
        int minChunkX = (center.getX() - radius) >> 4;
        int maxChunkX = (center.getX() + radius) >> 4;
        int minChunkZ = (center.getZ() - radius) >> 4;
        int maxChunkZ = (center.getZ() + radius) >> 4;
        for (int cx = minChunkX; cx <= maxChunkX; cx++) {
            for (int cz = minChunkZ; cz <= maxChunkZ; cz++) {
                if (!world.getChunkManager().isChunkLoaded(cx, cz)) {
                    continue;
                }
                Chunk chunk = world.getChunk(cx, cz);
                for (BlockPos pos : chunk.getBlockEntityPositions()) {
                    BlockEntity blockEntity = chunk.getBlockEntity(pos);
                    if (blockEntity instanceof LootableContainerBlockEntity && pos.isWithinDistance(center, radius)) {
                        world.spawnParticles(player, ParticleTypes.END_ROD, true, false,
                                pos.getX() + 0.5D, pos.getY() + 1.1D, pos.getZ() + 0.5D, 4, 0.2D, 0.2D, 0.2D, 0.01D);
                        found++;
                    }
                }
            }
        }
        if (found > 0 && world.getTime() % 100 == 0) {
            Gadgets.overlay(player, Gadgets.text("hcm.gadget.tresor.detecte", "Trésor détecté à proximité")
                    .formatted(Formatting.GOLD));
        }
        return found;
    }

    @Override
    public void appendTooltip(ItemStack stack, TooltipContext context, TooltipDisplayComponent display,
                              Consumer<Text> tooltip, TooltipType type) {
        String key = "hcm.gadget.infobulle." + kind.name().toLowerCase(java.util.Locale.ROOT);
        String fallback = switch (kind) {
            case LUNETTES_NOCTURNES -> "Vision nocturne tant qu'on les porte";
            case CASQUE_DE_MINEUR -> "S'allume dans l'obscurité";
            case LANTERNE_DE_SAC -> "Éclaire le porteur dans l'obscurité, depuis le sac";
            case LUNETTES_DE_TRESOR -> "Signale les coffres proches";
            case LUNETTES_DE_PROSPECTION -> "Compte les minerais alentour, régulièrement";
            case BOTTES_DE_LAVE -> "Résistance au feu tant qu'on les porte";
            case EXOSQUELETTE -> "Set complet : célérité, résistance, vision nocturne sous terre";
        };
        tooltip.accept(Gadgets.text(key, fallback).formatted(Formatting.GRAY));
    }
}
