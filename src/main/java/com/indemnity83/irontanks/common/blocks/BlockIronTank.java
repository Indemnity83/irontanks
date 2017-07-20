package com.indemnity83.irontanks.common.blocks;

import buildcraft.api.transport.pipe.ICustomPipeConnection;
import buildcraft.factory.block.BlockTank;
import buildcraft.factory.tile.TileTank;
import com.indemnity83.irontanks.IronTanks;
import net.minecraft.block.Block;
import net.minecraft.block.ITileEntityProvider;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.MapColor;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.PropertyBool;
import net.minecraft.block.properties.PropertyEnum;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.*;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;

import javax.annotation.Nullable;

public class BlockIronTank extends Block implements ICustomPipeConnection, ITileEntityProvider {

    public static final PropertyEnum<IronTankType> VARIANT = PropertyEnum.create("variant", IronTankType.class);
    public static final PropertyBool STACKED = PropertyBool.create("stacked");

    public BlockIronTank() {
        super(Material.IRON, MapColor.AIR);

        setRegistryName(new ResourceLocation(IronTanks.MODID, "iron_tank"));
        setUnlocalizedName("IronTank");

        setCreativeTab(CreativeTabs.MISC);

        setHardness(5.0F);
        setResistance(10.0F);
        setSoundType(SoundType.METAL);

        setDefaultState(this.blockState.getBaseState().withProperty(VARIANT, IronTankType.IRON).withProperty(STACKED, false));
    }

    @Override
    protected BlockStateContainer createBlockState() {
        return new BlockStateContainer(this, VARIANT, STACKED);
    }

    /**
     * returns a list of blocks with the same ID, but different meta (eg: wood returns 4 blocks)
     */
    public void getSubBlocks(Item itemIn, CreativeTabs tab, NonNullList<ItemStack> list) {
        for (IronTankType tankType : IronTankType.values()) {
            if(tankType.isValidForCreativeMode()) list.add(new ItemStack(itemIn, 1, tankType.ordinal()));
        }
    }

    /**
     * Convert the given metadata into a BlockState for this Block
     */
    public int getMetaFromState(IBlockState blockState) {
        return blockState.getValue(VARIANT).ordinal();
    }

    /**
     * Gets the metadata of the item this Block can drop. This method is called when the block gets destroyed. It
     * returns the metadata of the dropped item based on the old metadata of the block.
     */
    public int damageDropped(IBlockState blockState) {
        return blockState.getValue(VARIANT).ordinal();
    }

    @Override
    public BlockRenderLayer getBlockLayer() {
        return BlockRenderLayer.CUTOUT;
    }

    @Override
    public float getExtension(World world, BlockPos pos, EnumFacing face, IBlockState state) {
        if (faceIsSide(face)) {
            return 2 / 16f;
        }

        return 0;
    }

    private boolean faceIsSide(EnumFacing face) {
        return face.getAxis() != EnumFacing.Axis.Y;
    }

    /**
     * Get the actual Block state of this Block at the given position. This applies properties not visible in the
     * metadata, such as fence connections.
     */
    public IBlockState getActualState(IBlockState blockState, IBlockAccess world, BlockPos pos) {
        return blockState.withProperty(STACKED, isJoinedBelow(world, pos));
    }

    private boolean isJoinedBelow(IBlockAccess world, BlockPos pos) {
        return positionIsTank(world, pos.down());
    }

    @Override
    public boolean shouldSideBeRendered(IBlockState blockState, IBlockAccess world, BlockPos pos, EnumFacing face) {
        return faceIsSide(face) || faceIsNotAdjacentToAnotherTank(world, pos, face);
    }

    private boolean faceIsNotAdjacentToAnotherTank(IBlockAccess world, BlockPos pos, EnumFacing face) {
        return !positionIsTank(world, pos.offset(face));
    }

    private boolean positionIsTank(IBlockAccess world, BlockPos position) {
        Block block = world.getBlockState(position).getBlock();
        return block instanceof BlockIronTank || block instanceof BlockTank;
    }

    /**
     * Convert the BlockState into the correct metadata value
     */
    public IBlockState getStateFromMeta(int meta) {
        return this.getDefaultState().withProperty(VARIANT, IronTankType.VALUES[meta]);
    }

    @Override
    public boolean isFullCube(IBlockState state) {
        return false;
    }

    @Override
    public boolean isOpaqueCube(IBlockState state) {
        return false;
    }

    @Override
    public AxisAlignedBB getBoundingBox(IBlockState state, IBlockAccess world, BlockPos pos) {
        return new AxisAlignedBB(2 / 16D, 0 / 16D, 2 / 16D, 14 / 16D, 16 / 16D, 14 / 16D);
    }

    @Override
    public boolean onBlockActivated(World world, BlockPos pos, IBlockState state, EntityPlayer player, EnumHand hand, EnumFacing side, float hitX, float hitY, float hitZ) {
        TileEntity tile = world.getTileEntity(pos);
        if (tile instanceof TileTank) {
            TileTank tank = (TileTank) tile;
            return tank.onActivate(player, hand);
        }
        return false;
    }

    /**
     * Returns a new instance of a block's tile entity class. Called on placing the block.
     *
     * @param worldIn
     * @param meta
     */
    @Nullable
    @Override
    public TileEntity createNewTileEntity(World worldIn, int meta) {
        return getStateFromMeta(meta).getValue(VARIANT).makeEntity();
    }
}
