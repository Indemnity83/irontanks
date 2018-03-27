package com.indemnity83.irontanks.common.blocks;

import buildcraft.api.transport.pipe.ICustomPipeConnection;
import buildcraft.factory.block.BlockTank;
import buildcraft.factory.tile.TileTank;
import com.indemnity83.irontanks.IronTanks;
import com.indemnity83.irontanks.common.tiles.TankTile;
import net.minecraft.block.Block;
import net.minecraft.block.ITileEntityProvider;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.MapColor;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.IProperty;
import net.minecraft.block.properties.PropertyBool;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.client.resources.I18n;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.BlockRenderLayer;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.Explosion;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import javax.annotation.Nullable;
import java.util.List;

public class TankBlock extends Block implements ITileEntityProvider, ICustomPipeConnection {

    private static final IProperty<Boolean> JOINED_BELOW = PropertyBool.create("joined_below");

    private final int tankCapacity;

    public TankBlock(String tankName, int tankCapacity) {
        super(Material.GLASS, MapColor.AIR);
        this.tankCapacity = tankCapacity;

        setRegistryName(tankName);
        setUnlocalizedName(IronTanks.MODID + "." + tankName);

        setCreativeTab(CreativeTabs.MISC);

        setHardness(5.0F);
        setResistance(10.0F);
        setSoundType(SoundType.METAL);

        setDefaultState(this.blockState.getBaseState().withProperty(JOINED_BELOW, false));
    }

    @Override
    public void addInformation(ItemStack stack, EntityPlayer player, List<String> tooltip, boolean advanced) {
        tooltip.add(I18n.format("irontanks.tooltip.capacity", this.tankCapacity));
    }

    @Nullable
    @Override
    public TileEntity createNewTileEntity(World worldIn, int meta) {
        return new TankTile().withCapacity(this.tankCapacity);
    }

    @Override
    public float getExtension(World world, BlockPos pos, EnumFacing face, IBlockState state) {
        return face.getAxis() == EnumFacing.Axis.Y ? 0 : 2 / 16f;
    }

    @Override
    public boolean onBlockActivated(World world, BlockPos pos, IBlockState state, EntityPlayer player, EnumHand hand, EnumFacing side, float hitX, float hitY, float hitZ) {
        TileEntity tile = world.getTileEntity(pos);
        return tile instanceof TileTank && ((TileTank) tile).onActivate(player, hand);
    }

    @Override
    public AxisAlignedBB getBoundingBox(IBlockState state, IBlockAccess world, BlockPos pos) {
        return new AxisAlignedBB(2 / 16D, 0 / 16D, 2 / 16D, 14 / 16D, 16 / 16D, 14 / 16D);
    }

    @SideOnly(Side.CLIENT)
    public BlockRenderLayer getBlockLayer() {
        return BlockRenderLayer.CUTOUT;
    }

    @Override
    public boolean isOpaqueCube(IBlockState state) {
        return false;
    }

    @Override
    public boolean isFullCube(IBlockState state) {
        return false;
    }

    @SideOnly(Side.CLIENT)
    public void initModel() {
        ModelLoader.setCustomModelResourceLocation(Item.getItemFromBlock(this), 0, new ModelResourceLocation(getRegistryName(), "inventory"));
    }

    /**
     * Get the actual Block state of this Block at the given position. This applies properties not visible in the
     * metadata, such as fence connections.
     */
    public IBlockState getActualState(IBlockState blockState, IBlockAccess world, BlockPos pos) {
        Block block = world.getBlockState(pos.down()).getBlock();
        boolean tankBelow = block instanceof TankBlock || block instanceof BlockTank;
        return blockState.withProperty(JOINED_BELOW, tankBelow);
    }

    @Override
    protected BlockStateContainer createBlockState() {
        return new BlockStateContainer(this, JOINED_BELOW);
    }

    /**
     * Convert the given metadata into a BlockState for this Block
     */
    public int getMetaFromState(IBlockState blockState) {
        return 0;
    }

    /**
     * Convert the BlockState into the correct metadata value
     */
    public IBlockState getStateFromMeta(int meta) {
        return this.getDefaultState();
    }

    @Override
    public boolean shouldSideBeRendered(IBlockState state, IBlockAccess world, BlockPos pos, EnumFacing side) {
        Block block = world.getBlockState(pos.offset(side)).getBlock();
        return side.getAxis() != EnumFacing.Axis.Y || !(block instanceof TankBlock || block instanceof BlockTank);
    }

    @Override
    public void onBlockExploded(World world, BlockPos pos, Explosion explosion) {
        TileEntity tile = world.getTileEntity(pos);
        if (tile instanceof TankTile) {
            TankTile tankTile = (TankTile) tile;
            tankTile.onExplode(explosion);
        }
        super.onBlockExploded(world, pos, explosion);
    }

    @Override
    public void breakBlock(World world, BlockPos pos, IBlockState state) {
        TileEntity tile = world.getTileEntity(pos);
        if (tile instanceof TankTile) {
            TankTile tankTile = (TankTile) tile;
            tankTile.onRemove();
        }
        super.breakBlock(world, pos, state);
    }

    public void onBlockPlacedBy(World world, BlockPos pos, IBlockState state, EntityLivingBase placer, ItemStack stack) {
        TileEntity tile = world.getTileEntity(pos);
        if (tile instanceof TileTank) {
            TileTank tileBC = (TileTank) tile;
            tileBC.onPlacedBy(placer, stack);
        }
        super.onBlockPlacedBy(world, pos, state, placer, stack);
    }
}
