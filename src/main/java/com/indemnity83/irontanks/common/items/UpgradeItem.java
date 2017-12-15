package com.indemnity83.irontanks.common.items;

import com.indemnity83.irontanks.IronTanks;
import com.indemnity83.irontanks.common.blocks.TankBlock;
import com.indemnity83.irontanks.common.core.Blocks;
import com.indemnity83.irontanks.common.tiles.TankTile;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class UpgradeItem extends Item {
    public UpgradeItem(String upgradeName) {
        setRegistryName(upgradeName);
        setUnlocalizedName(IronTanks.MODID + "." + upgradeName);
        setCreativeTab(CreativeTabs.MISC);
    }

    @SideOnly(Side.CLIENT)
    public void initModel() {
        ModelLoader.setCustomModelResourceLocation(this, 0, new ModelResourceLocation(getRegistryName(), "inventory"));
    }

    @Override
    public EnumActionResult onItemUse(EntityPlayer player, World worldIn, BlockPos pos, EnumHand hand, EnumFacing facing, float hitX, float hitY, float hitZ) {
        // If the world is remote, exit early
        if (worldIn.isRemote) {
            return EnumActionResult.PASS;
        }

        IBlockState iblockstate = Blocks.ironTank.getDefaultState();

        // Swap the tile entity and block
        TankTile oldTank = (TankTile) worldIn.getTileEntity(pos);
        TankTile newTank = (TankTile) Blocks.ironTank.createTileEntity(worldIn, iblockstate);
        newTank.tank.setFluid(oldTank.tank.getFluid());

        worldIn.setTileEntity(pos, newTank);
        worldIn.setBlockState(pos, iblockstate, 3);
        worldIn.notifyBlockUpdate(pos, iblockstate, iblockstate, 3);

        if (!player.capabilities.isCreativeMode) {
            player.getHeldItem(hand).shrink(1);
        }

        return EnumActionResult.SUCCESS;
    }

//    @Override
//    public EnumActionResult onItemUseFirst(EntityPlayer player, World world, BlockPos pos, EnumFacing side, float hitX, float hitY, float hitZ, EnumHand hand) {
//        ItemStack itemstack = player.getHeldItem(hand);
//
//        // If the world is remote, exit early
//        if (world.isRemote) {
//            return EnumActionResult.PASS;
//        }
//
//        // If the item cannot upgrade a normal buildcraft tank, exit early
//        IBlockState clickedBlockState = world.getBlockState(pos);
//        if (this.type.canUpgrade(IronTankType.GLASS)) {
//            if (!(clickedBlockState.getBlock() instanceof BlockTank)) {
//                return EnumActionResult.PASS;
//            }
//
//            // If the item cannot upgrade the clicked tank, exit early
//        } else {
//            if (clickedBlockState != IronTankBlocks.ironTankBlock.getStateFromMeta(IronTankType.valueOf(this.type.upgradeFrom.getName().toUpperCase()).metaValue)) {
//                return EnumActionResult.PASS;
//            }
//        }
//
//        // Swap the tile entity and block
//        TileTank oldTank = (TileTank) world.getTileEntity(pos);
//        TileTank newTank = (TileTank) this.type.upgradeTo.makeEntity();
//
//        newTank.tank.setFluid(oldTank.tank.getFluid());
//
//        IBlockState iblockstate = IronTankBlocks.ironTankBlock.getDefaultState().withProperty(BlockIronTank.VARIANT, this.type.upgradeTo);
//
//        world.setTileEntity(pos, newTank);
//        world.setBlockState(pos, iblockstate, 3);
//
//        world.notifyBlockUpdate(pos, iblockstate, iblockstate, 3);
//
//        if (!player.capabilities.isCreativeMode) {
//            itemstack.shrink(1);
//        }
//
//        return EnumActionResult.SUCCESS;
//    }

}
