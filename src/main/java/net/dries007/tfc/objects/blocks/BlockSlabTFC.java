/*
 * Work under Copyright. Licensed under the EUPL.
 * See the project README.md and LICENSE.txt for more information.
 */

package net.dries007.tfc.objects.blocks;

import java.util.EnumMap;
import java.util.Random;

import net.minecraft.block.Block;
import net.minecraft.block.BlockSlab;
import net.minecraft.block.SoundType;
import net.minecraft.block.properties.IProperty;
import net.minecraft.block.properties.PropertyEnum;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.init.Blocks;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.IStringSerializable;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import net.dries007.tfc.objects.Rock;
import net.dries007.tfc.objects.Wood;
import net.dries007.tfc.objects.blocks.stone.BlockRockVariant;
import net.dries007.tfc.objects.blocks.wood.BlockPlanksTFC;
import net.dries007.tfc.util.InsertOnlyEnumTable;
import net.dries007.tfc.util.OreDictionaryHelper;

public abstract class BlockSlabTFC extends BlockSlab
{
    public static final PropertyEnum<Variant> VARIANT = PropertyEnum.create("variant", Variant.class);
    public final Block modelBlock;
    protected Half halfSlab;

    private BlockSlabTFC(Rock rock, Rock.Type type)
    {
        this(BlockRockVariant.get(rock, type));
        Block c = BlockRockVariant.get(rock, type);
        //noinspection ConstantConditions
        setHarvestLevel(c.getHarvestTool(c.getDefaultState()), c.getHarvestLevel(c.getDefaultState()));
    }

    private BlockSlabTFC(Wood wood)
    {
        this(BlockPlanksTFC.get(wood));
        Block c = BlockPlanksTFC.get(wood);
        //noinspection ConstantConditions
        setHarvestLevel(c.getHarvestTool(c.getDefaultState()), c.getHarvestLevel(c.getDefaultState()));
        Blocks.FIRE.setFireInfo(this, 5, 20);
    }

    private BlockSlabTFC(Block block)
    {
        super(block.getDefaultState().getMaterial());
        IBlockState state = blockState.getBaseState();
        if (!isDouble()) state = state.withProperty(HALF, BlockSlab.EnumBlockHalf.BOTTOM);
        setDefaultState(state.withProperty(VARIANT, Variant.DEFAULT));
        this.modelBlock = block;
        setLightOpacity(255);
    }

    @Override
    public String getUnlocalizedName(int meta)
    {
        return getUnlocalizedName();
    }

    @SuppressWarnings("deprecation")
    @Override
    public IBlockState getStateFromMeta(int meta)
    {
        IBlockState iblockstate = this.getDefaultState().withProperty(VARIANT, Variant.DEFAULT);

        if (!this.isDouble())
        {
            iblockstate = iblockstate.withProperty(HALF, (meta & 8) == 0 ? BlockSlab.EnumBlockHalf.BOTTOM : BlockSlab.EnumBlockHalf.TOP);
        }

        return iblockstate;
    }

    @Override
    public int getMetaFromState(IBlockState state)
    {
        int i = 0;

        if (!this.isDouble() && state.getValue(HALF) == BlockSlab.EnumBlockHalf.TOP)
        {
            i |= 8;
        }

        return i;
    }

    @SuppressWarnings("deprecation")
    @Override
    public float getBlockHardness(IBlockState blockState, World worldIn, BlockPos pos)
    {
        return modelBlock.getBlockHardness(blockState, worldIn, pos);
    }

    @Override
    public Item getItemDropped(IBlockState state, Random rand, int fortune)
    {
        return Item.getItemFromBlock(halfSlab);
    }

    @SuppressWarnings("deprecation")
    @Override
    public float getExplosionResistance(Entity exploder)
    {
        return modelBlock.getExplosionResistance(exploder);
    }

    @SuppressWarnings("deprecation")
    @Override
    public ItemStack getItem(World worldIn, BlockPos pos, IBlockState state)
    {
        return new ItemStack(halfSlab);
    }

    @Override
    protected BlockStateContainer createBlockState()
    {
        return this.isDouble() ? new BlockStateContainer(this, VARIANT) : new BlockStateContainer(this, HALF, VARIANT);
    }

    @SuppressWarnings("deprecation")
    @Override
    public SoundType getSoundType()
    {
        return modelBlock.getSoundType();
    }

    @Override
    public IProperty<?> getVariantProperty()
    {
        return VARIANT; // why is this not null-tolerable ...
    }

    @Override
    public Comparable<?> getTypeForItem(ItemStack stack)
    {
        return Variant.DEFAULT;
    }

    public enum Variant implements IStringSerializable
    {
        DEFAULT;

        public String getName()
        {
            return "default";
        }
    }

    public static class Double extends BlockSlabTFC
    {
        private static final InsertOnlyEnumTable<Rock, Rock.Type, Double> ROCK_TABLE = new InsertOnlyEnumTable<>(Rock.class, Rock.Type.class);
        private static final EnumMap<Wood, Double> WOOD_MAP = new EnumMap<>(Wood.class);

        public static Double get(Rock rock, Rock.Type type)
        {
            return ROCK_TABLE.get(rock, type);
        }

        public static Double get(Wood wood)
        {
            return WOOD_MAP.get(wood);
        }

        public Double(Rock rock, Rock.Type type)
        {
            super(rock, type);
            ROCK_TABLE.put(rock, type, this);
            // No oredict, because no item.
        }

        public Double(Wood wood)
        {
            super(wood);
            if (WOOD_MAP.put(wood, this) != null) throw new IllegalStateException("There can only be one.");
            // No oredict, because no item.
        }

        public boolean isDouble()
        {
            return true;
        }
    }

    public static class Half extends BlockSlabTFC
    {
        private static final InsertOnlyEnumTable<Rock, Rock.Type, Half> ROCK_TABLE = new InsertOnlyEnumTable<>(Rock.class, Rock.Type.class);
        private static final EnumMap<Wood, Half> WOOD_MAP = new EnumMap<>(Wood.class);

        public static Half get(Rock rock, Rock.Type type)
        {
            return ROCK_TABLE.get(rock, type);
        }

        public static Half get(Wood wood)
        {
            return WOOD_MAP.get(wood);
        }

        public final Double doubleSlab;

        public Half(Rock rock, Rock.Type type)
        {
            super(rock, type);
            ROCK_TABLE.put(rock, type, this);
            doubleSlab = Double.get(rock, type);
            doubleSlab.halfSlab = this;
            halfSlab = this;
            OreDictionaryHelper.register(this, "slab");
            OreDictionaryHelper.registerRockType(this, type, rock, "slab");
        }

        public Half(Wood wood)
        {
            super(wood);
            if (WOOD_MAP.put(wood, this) != null) throw new IllegalStateException("There can only be one.");
            doubleSlab = Double.get(wood);
            doubleSlab.halfSlab = this;
            halfSlab = this;
            OreDictionaryHelper.register(this, "slab");
            OreDictionaryHelper.register(this, "slab", "wood");
            OreDictionaryHelper.register(this, "slab", "wood", wood);
        }

        public boolean isDouble()
        {
            return false;
        }
    }


}
