package net.hautecapitale.metiers.textile;

import com.mojang.serialization.MapCodec;
import net.hautecapitale.metiers.HauteCapitaleMetiers;
import net.minecraft.block.AbstractBlock;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.MapColor;
import net.minecraft.block.PlantBlock;
import net.minecraft.block.piston.PistonBehavior;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.sound.BlockSoundGroup;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.WorldView;

/**
 * Le cotonnier : le node de coton de l'Herboriste.
 *
 * <p>Deux blocs, comme tout node : plein (les capsules sont mûres) et vide
 * (la plante repousse). Une plante sans collision, qui ne tombe pas si son
 * sol change — un node ne doit jamais se détruire tout seul — et qui ne
 * lâche rien quand on la casse : c'est le moteur de nodes qui donne le coton,
 * et la protection des blocs empêche de la casser de toute façon.
 *
 * <p>Textures : les stades du cotonnier de Weaver's Paradise (MIT).
 */
public final class CottonBlocks {

    public static final class CottonBlock extends PlantBlock {
        public static final MapCodec<CottonBlock> CODEC = createCodec(CottonBlock::new);

        public CottonBlock(AbstractBlock.Settings settings) {
            super(settings);
        }

        @Override
        protected MapCodec<? extends PlantBlock> getCodec() {
            return CODEC;
        }

        /** Un node tient où l'administrateur l'a posé, quel que soit le sol. */
        @Override
        protected boolean canPlaceAt(BlockState state, WorldView world, BlockPos pos) {
            return true;
        }
    }

    public static final Block COTONNIER = registerBlock("cotonnier");
    public static final Block COTONNIER_JEUNE = registerBlock("cotonnier_jeune");

    /** Les objets-blocs, pour l'administrateur et la baguette de node. */
    public static final Item COTONNIER_ITEM = registerItem("cotonnier", COTONNIER);
    public static final Item COTONNIER_JEUNE_ITEM = registerItem("cotonnier_jeune", COTONNIER_JEUNE);

    private CottonBlocks() {
    }

    public static void init() {
        // Les constantes ci-dessus s'enregistrent en se chargeant.
    }

    private static Block registerBlock(String path) {
        Identifier id = HauteCapitaleMetiers.id(path);
        RegistryKey<Block> key = RegistryKey.of(RegistryKeys.BLOCK, id);
        return Registry.register(Registries.BLOCK, key, new CottonBlock(AbstractBlock.Settings.create()
                .registryKey(key)
                .mapColor(MapColor.DARK_GREEN)
                .noCollision()
                .breakInstantly()
                .sounds(BlockSoundGroup.CROP)
                .pistonBehavior(PistonBehavior.DESTROY)));
    }

    private static Item registerItem(String path, Block block) {
        Identifier id = HauteCapitaleMetiers.id(path);
        RegistryKey<Item> key = RegistryKey.of(RegistryKeys.ITEM, id);
        return Registry.register(Registries.ITEM, key, new BlockItem(block, new Item.Settings().registryKey(key)
                .useBlockPrefixedTranslationKey()));
    }
}
