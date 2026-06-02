package com.indemnity83.irontanks.common.items;

import com.indemnity83.irontanks.IronTanks;
import com.indemnity83.irontanks.common.core.IronTanksTab;
import com.indemnity83.irontanks.common.tiles.TankTile;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.capability.CapabilityFluidHandler;
import net.minecraftforge.fluids.capability.IFluidHandler;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import javax.annotation.Nullable;

public class UpgradeItem extends Item {
    private final Block upgradeFrom;
    private final Block upgradeTo;
    // Optional extra source matched by registry name (e.g. BuildCraft's tank) without a hard dependency.
    private ResourceLocation alsoUpgradeFrom;

    public UpgradeItem(String upgradeName, Block upgradeFrom, Block upgradeTo) {
        setRegistryName(upgradeName);
        setUnlocalizedName(IronTanks.MODID + "." + upgradeName);
        setCreativeTab(IronTanksTab.INSTANCE);
        this.upgradeFrom = upgradeFrom;
        this.upgradeTo = upgradeTo;
    }

    /** Additionally accept the block with this registry name as an upgrade source (e.g. "buildcraftfactory:tank"). */
    public UpgradeItem alsoUpgradeFrom(String registryName) {
        this.alsoUpgradeFrom = new ResourceLocation(registryName);
        return this;
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

        if (!isUpgradeSource(worldIn.getBlockState(pos))) {
            return EnumActionResult.PASS;
        }

        upgradeTankAtPosition(this.upgradeTo, worldIn, pos, player);

        if (!player.capabilities.isCreativeMode) {
            player.getHeldItem(hand).shrink(1);
        }

        return EnumActionResult.SUCCESS;
    }

    private void upgradeTankAtPosition(Block tankBlock, World worldIn, BlockPos pos, EntityPlayer player) {
        // Drain the fluid from the old tank. Our tanks drain only this tile; a BuildCraft tank (when present)
        // is reached through the standard Forge fluid capability so no hard dependency is needed.
        FluidStack fluid = drainOldTank(worldIn.getTileEntity(pos));

        // Replace tank
        IBlockState oldState = worldIn.getBlockState(pos);
        worldIn.setBlockState(pos, tankBlock.getDefaultState(), 3);
        IBlockState newState = worldIn.getBlockState(pos);

        // Transfer fluid to new tank
        TileEntity newTile = worldIn.getTileEntity(pos);
        if (newTile instanceof TankTile && fluid != null) {
            ((TankTile) newTile).setFluid(fluid);
        }

        // Notify the world, and let the new tank settle into any stack it belongs to
        worldIn.notifyBlockUpdate(pos, oldState, newState, 3);
        if (newTile instanceof TankTile) {
            ((TankTile) newTile).onPlacedBy(player, null);
        }
    }

    @Nullable
    private static FluidStack drainOldTank(@Nullable TileEntity tile) {
        if (tile instanceof TankTile) {
            return ((TankTile) tile).drainEntireTank();
        }
        if (tile != null && tile.hasCapability(CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY, null)) {
            IFluidHandler handler = tile.getCapability(CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY, null);
            if (handler != null) {
                return handler.drain(Integer.MAX_VALUE, true);
            }
        }
        return null;
    }

    private boolean isUpgradeSource(IBlockState blockState) {
        ResourceLocation placed = blockState.getBlock().getRegistryName();
        if (placed == null) {
            return false;
        }
        if (upgradeFrom != null && placed.equals(upgradeFrom.getRegistryName())) {
            return true;
        }
        return alsoUpgradeFrom != null && placed.equals(alsoUpgradeFrom);
    }
}
