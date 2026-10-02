package net.hautecapitale.metiers.gadget;

import com.mojang.datafixers.util.Pair;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.component.type.TooltipDisplayComponent;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.tooltip.TooltipType;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.registry.tag.BlockTags;
import net.minecraft.registry.tag.TagKey;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.text.Text;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Formatting;
import net.minecraft.util.Hand;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.biome.BiomeKeys;
import net.minecraft.world.gen.structure.Structure;
import net.minecraft.world.World;

import java.util.List;
import java.util.function.Consumer;

/**
 * Les détecteurs : un clic droit, et la réponse.
 *
 * <ul>
 *   <li><b>Détecteur de minerai</b> : compte les minerais du rayon, par famille.</li>
 *   <li><b>Chercheur de géode</b> : direction et distance de l'améthyste
 *       bourgeonnante la plus proche.</li>
 *   <li><b>Sonde à écho</b> : direction du Deep Dark, ou d'une cité antique.</li>
 *   <li><b>Résonateur de cristal</b> : vibre d'autant plus aigu qu'un minerai
 *       précieux — diamant, émeraude — est proche.</li>
 * </ul>
 *
 * <p>Aucun ne touche à un bloc : ils regardent, ils comptent, ils indiquent.
 */
public class ScannerItem extends Item {

    public enum Kind {
        MINERAI,
        GEODE,
        ECHO,
        RESONATEUR
    }

    /** Les familles de minerais comptées, dans l'ordre d'affichage. */
    static final List<TagKey<net.minecraft.block.Block>> ORE_FAMILIES = List.of(
            BlockTags.COAL_ORES, BlockTags.COPPER_ORES, BlockTags.IRON_ORES, BlockTags.GOLD_ORES,
            BlockTags.LAPIS_ORES, BlockTags.REDSTONE_ORES, BlockTags.DIAMOND_ORES, BlockTags.EMERALD_ORES);

    static final TagKey<Structure> ANCIENT_CITY =
            TagKey.of(RegistryKeys.STRUCTURE, Identifier.of("minecraft", "ancient_city"));

    private final Kind kind;

    public ScannerItem(Kind kind, Settings settings) {
        super(settings);
        this.kind = kind;
    }

    public Kind kind() {
        return kind;
    }

    @Override
    public ActionResult use(World world, PlayerEntity player, Hand hand) {
        if (!(world instanceof ServerWorld serverWorld)) {
            return ActionResult.SUCCESS;
        }
        Text answer = scan(serverWorld, player);
        Gadgets.say(player, answer);
        player.getItemCooldownManager().set(player.getStackInHand(hand), 20);
        return ActionResult.CONSUME;
    }

    /** La réponse du détecteur, telle que le joueur la lit. */
    public Text scan(ServerWorld world, PlayerEntity player) {
        BlockPos center = player.getBlockPos();
        return switch (kind) {
            case MINERAI -> describeOres(world, center, Gadgets.config().scanner_rayon);
            case GEODE -> {
                BlockPos geode = nearest(world, center, Gadgets.config().geode_rayon, Blocks.BUDDING_AMETHYST.getDefaultState());
                yield geode == null
                        ? Gadgets.text("hcm.gadget.geode.rien", "Aucun signal de géode alentour").formatted(Formatting.GRAY)
                        : Gadgets.text("hcm.gadget.geode.trouve", "Signal de géode : %s",
                                Gadgets.direction(player.getEntityPos(), Vec3d.ofCenter(geode))).formatted(Formatting.LIGHT_PURPLE);
            }
            case ECHO -> {
                int radius = Gadgets.config().echo_rayon;
                Pair<BlockPos, RegistryEntry<Biome>> deepDark = world.locateBiome(
                        biome -> biome.matchesKey(BiomeKeys.DEEP_DARK), center, radius, 8, 16);
                BlockPos city = world.locateStructure(ANCIENT_CITY, center, Math.max(1, radius / 16), false);
                if (city != null && city.isWithinDistance(center, radius)) {
                    yield Gadgets.text("hcm.gadget.echo.cite", "Écho d'une cité antique : %s",
                            Gadgets.direction(player.getEntityPos(), Vec3d.ofCenter(city))).formatted(Formatting.DARK_AQUA);
                }
                if (deepDark != null) {
                    yield Gadgets.text("hcm.gadget.echo.deep_dark", "Écho des profondeurs sombres : %s",
                            Gadgets.direction(player.getEntityPos(), Vec3d.ofCenter(deepDark.getFirst()))).formatted(Formatting.DARK_AQUA);
                }
                yield Gadgets.text("hcm.gadget.echo.rien", "Aucun écho profond alentour").formatted(Formatting.GRAY);
            }
            case RESONATEUR -> {
                int radius = Gadgets.config().resonateur_rayon;
                BlockPos precious = nearestIn(world, center, radius, List.of(BlockTags.DIAMOND_ORES, BlockTags.EMERALD_ORES));
                if (precious == null) {
                    world.playSound(null, center, SoundEvents.BLOCK_AMETHYST_BLOCK_HIT, SoundCategory.PLAYERS, 0.6F, 0.5F);
                    yield Gadgets.text("hcm.gadget.resonateur.rien", "Le cristal reste muet").formatted(Formatting.GRAY);
                }
                double distance = Math.sqrt(precious.getSquaredDistance(center));
                float pitch = (float) (0.6D + 1.4D * (1.0D - Math.min(1.0D, distance / radius)));
                world.playSound(null, center, SoundEvents.BLOCK_AMETHYST_BLOCK_CHIME, SoundCategory.PLAYERS, 1.0F, pitch);
                yield Gadgets.text("hcm.gadget.resonateur.vibre", "Le cristal vibre — quelque chose de précieux à %s blocs",
                        Text.literal(String.valueOf((int) Math.round(distance)))).formatted(Formatting.AQUA);
            }
        };
    }

    /** Le nombre de blocs de minerai — toutes familles — dans un cube de rayon donné. */
    public static int countOres(ServerWorld world, BlockPos center, int radius) {
        int total = 0;
        for (BlockPos pos : BlockPos.iterate(center.add(-radius, -radius, -radius), center.add(radius, radius, radius))) {
            BlockState state = world.getBlockState(pos);
            for (TagKey<net.minecraft.block.Block> family : ORE_FAMILIES) {
                if (state.isIn(family)) {
                    total++;
                    break;
                }
            }
        }
        return total;
    }

    static Text describeOres(ServerWorld world, BlockPos center, int radius) {
        int[] counts = new int[ORE_FAMILIES.size()];
        for (BlockPos pos : BlockPos.iterate(center.add(-radius, -radius, -radius), center.add(radius, radius, radius))) {
            BlockState state = world.getBlockState(pos);
            for (int i = 0; i < ORE_FAMILIES.size(); i++) {
                if (state.isIn(ORE_FAMILIES.get(i))) {
                    counts[i]++;
                    break;
                }
            }
        }
        String[] names = {"charbon", "cuivre", "fer", "or", "lapis", "redstone", "diamant", "émeraude"};
        StringBuilder text = new StringBuilder();
        int total = 0;
        for (int i = 0; i < counts.length; i++) {
            if (counts[i] > 0) {
                text.append(text.isEmpty() ? "" : ", ").append(names[i]).append(' ').append(counts[i]);
                total += counts[i];
            }
        }
        return total == 0
                ? Gadgets.text("hcm.gadget.minerai.rien", "Aucun minerai à %s blocs", Text.literal(String.valueOf(radius))).formatted(Formatting.GRAY)
                : Gadgets.text("hcm.gadget.minerai.trouve", "Minerais à %s blocs : %s", Text.literal(String.valueOf(radius)),
                        Text.literal(text.toString())).formatted(Formatting.AQUA);
    }

    static BlockPos nearest(ServerWorld world, BlockPos center, int radius, BlockState wanted) {
        BlockPos best = null;
        double bestDistance = Double.MAX_VALUE;
        for (BlockPos pos : BlockPos.iterate(center.add(-radius, -radius, -radius), center.add(radius, radius, radius))) {
            if (world.getBlockState(pos).isOf(wanted.getBlock())) {
                double distance = pos.getSquaredDistance(center);
                if (distance < bestDistance) {
                    bestDistance = distance;
                    best = pos.toImmutable();
                }
            }
        }
        return best;
    }

    static BlockPos nearestIn(ServerWorld world, BlockPos center, int radius, List<TagKey<net.minecraft.block.Block>> families) {
        BlockPos best = null;
        double bestDistance = Double.MAX_VALUE;
        for (BlockPos pos : BlockPos.iterate(center.add(-radius, -radius, -radius), center.add(radius, radius, radius))) {
            BlockState state = world.getBlockState(pos);
            for (TagKey<net.minecraft.block.Block> family : families) {
                if (state.isIn(family)) {
                    double distance = pos.getSquaredDistance(center);
                    if (distance < bestDistance) {
                        bestDistance = distance;
                        best = pos.toImmutable();
                    }
                    break;
                }
            }
        }
        return best;
    }

    @Override
    public void appendTooltip(ItemStack stack, TooltipContext context, TooltipDisplayComponent display,
                              Consumer<Text> tooltip, TooltipType type) {
        String fallback = switch (kind) {
            case MINERAI -> "Clic droit : compte les minerais alentour";
            case GEODE -> "Clic droit : direction de la géode la plus proche";
            case ECHO -> "Clic droit : écoute les profondeurs";
            case RESONATEUR -> "Clic droit : vibre près des minerais précieux";
        };
        tooltip.accept(Gadgets.text("hcm.gadget.infobulle." + kind.name().toLowerCase(java.util.Locale.ROOT), fallback)
                .formatted(Formatting.GRAY));
    }
}
