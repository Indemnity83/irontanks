package com.indemnity83.irontanks.common.blocks;

import com.indemnity83.irontanks.common.tiles.CreativeTankTile;
import net.minecraft.client.resources.I18n;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;

import javax.annotation.Nullable;
import java.util.List;

public class CreativeTankBlock extends TankBlock {
    public CreativeTankBlock(String tankName, int tankCapacity) {
        super(tankName, tankCapacity);
    }

    @Override
    public void addInformation(ItemStack stack, EntityPlayer player, List<String> tooltip, boolean advanced) {
        super.addInformation(stack, player, tooltip, advanced);
        tooltip.add(I18n.format("irontanks.tooltip.creative_tank"));
    }

    @Nullable
    @Override
    public TileEntity createNewTileEntity(World worldIn, int meta) {
        return new CreativeTankTile().withCapacity(this.getTankCapacity());
    }
}
