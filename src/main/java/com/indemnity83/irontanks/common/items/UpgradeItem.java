package com.indemnity83.irontanks.common.items;

import buildcraft.factory.tile.TileTank;
import com.indemnity83.irontanks.IronTanks;
import com.indemnity83.irontanks.common.tiles.TankTile;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class UpgradeItem extends Item {
    Block upgradeFrom;
    Block upgradeTo;

    public UpgradeItem(String upgradeName, Block upgradeFrom, Block upgradeTo) {
        setRegistryName(upgradeName);
        setUnlocalizedName(IronTanks.MODID + "." + upgradeName);
        setCreativeTab(CreativeTabs.MISC);
        this.upgradeFrom = upgradeFrom;
        this.upgradeTo = upgradeTo;
    }

    @SideOnly(Side.CLIENT)
    public void initModel() {
        ModelLoader.setCustomModelResourceLocation(this, 0, new ModelResourceLocation(getRegistryName(), "inventory"));
    }

    @Override
    public EnumActionResult onItemUse(EntityPlayer player, World worldIn, BlockPos pos, EnumHand hand, EnumFacing facing, float hitX, float hitY, float hitZ) {
        if (worldIn.isRemote) {
            return EnumActionResult.PASS;
        }

        if (!blockEquals(worldIn.getBlockState(pos), this.upgradeFrom)) {
            return EnumActionResult.PASS;
        }

        upgradeTankAtPosition(this.upgradeTo, worldIn, pos);

        if (!player.capabilities.isCreativeMode) {
            player.getHeldItem(hand).shrink(1);
        }

        return EnumActionResult.SUCCESS;
    }

    private void upgradeTankAtPosition(Block newTank, World worldIn, BlockPos pos) {
        FluidStack fluid = ((TileTank) worldIn.getTileEntity(pos)).tank.getFluid();

        // Replace tank
        IBlockState oldState = worldIn.getBlockState(pos);
        worldIn.setBlockState(pos, newTank.getDefaultState(), 3);
        IBlockState newState = worldIn.getBlockState(pos);

        // Store the flid in the new tank
        ((TankTile) worldIn.getTileEntity(pos)).tank.setFluid(fluid);

        worldIn.notifyBlockUpdate(pos, oldState, newState, 3);
    }

    private boolean blockEquals(IBlockState blockState, Block block) {
        return blockState.getBlock().getRegistryName().equals(block.getRegistryName());
    }
}
