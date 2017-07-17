package com.indemnity83.irontanks.common.items;

import com.indemnity83.irontanks.IronTanks;
import com.indemnity83.irontanks.common.blocks.IronTankType;
import net.minecraft.block.Block;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;

public class ItemIronTank extends ItemBlock {
    public ItemIronTank(Block block) {
        super(block);

        this.setRegistryName(new ResourceLocation(IronTanks.MODID, "iron_tank"));
        this.setMaxDamage(0);
        this.setHasSubtypes(true);
    }

    @Override
    public int getMetadata(int meta) {
        return meta;
    }

    @Override
    public String getUnlocalizedName(ItemStack itemstack) {
        return "tile.irontanks." + IronTankType.VALUES[itemstack.getMetadata()].getName() + "_tank";
    }
}
