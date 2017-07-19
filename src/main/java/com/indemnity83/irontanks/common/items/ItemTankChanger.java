package com.indemnity83.irontanks.common.items;

import buildcraft.factory.block.BlockTank;
import buildcraft.factory.tile.TileTank;
import com.indemnity83.irontanks.common.Content;
import com.indemnity83.irontanks.common.blocks.BlockIronTank;
import com.indemnity83.irontanks.common.blocks.IronTankType;
import net.minecraft.block.state.IBlockState;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class ItemTankChanger extends Item {

    private final TankChangerType type;

    public ItemTankChanger(TankChangerType type) {
        this.type = type;
        this.setMaxStackSize(1);
        this.setUnlocalizedName("irontanks." + type.itemName);
        this.setCreativeTab(CreativeTabs.MISC);
    }

    /**
     * This is called when the item is used, before the block is activated.
     *
     * @param player The Player that used the item
     * @param world  The Current World
     * @param pos    Target position
     * @param side   The side of the target hit
     * @param hitX
     * @param hitY
     * @param hitZ
     * @param hand   Which hand the item is being held in.  @return Return PASS to allow vanilla handling, any other to skip normal code.
     */
    @Override
    public EnumActionResult onItemUseFirst(EntityPlayer player, World world, BlockPos pos, EnumFacing side, float hitX, float hitY, float hitZ, EnumHand hand) {
        ItemStack itemstack = player.getHeldItem(hand);

        // If the world is remote, exit early
        if (world.isRemote) {
            return EnumActionResult.PASS;
        }

        // If the item cannot upgrade a normal buildcraft tank, exit early
        IBlockState clickedBlockState = world.getBlockState(pos);
        if (this.type.canUpgrade(IronTankType.GLASS)) {
            if (!(clickedBlockState.getBlock() instanceof BlockTank))
            {
                return EnumActionResult.PASS;
            }

            // If the item cannot upgrade the clicked tank, exit early
        } else {
            if (clickedBlockState != Content.ironTankBlock.getStateFromMeta(IronTankType.valueOf(this.type.upgradeFrom.getName().toUpperCase()).ordinal())) {
                return EnumActionResult.PASS;
            }
        }

        // Swap the tile entity and block
        TileTank oldTank = (TileTank) world.getTileEntity(pos);
        TileTank newTank = (TileTank) this.type.upgradeTo.makeEntity();

        newTank.tank.setFluid(oldTank.tank.getFluid());

        IBlockState iblockstate = Content.ironTankBlock.getDefaultState().withProperty(BlockIronTank.VARIANT, this.type.upgradeTo);

        world.setTileEntity(pos, newTank);
        world.setBlockState(pos, iblockstate, 3);

        world.notifyBlockUpdate(pos, iblockstate, iblockstate, 3);

        if (!player.capabilities.isCreativeMode) {
            itemstack.shrink(1);
        }

        return EnumActionResult.SUCCESS;
    }
}
