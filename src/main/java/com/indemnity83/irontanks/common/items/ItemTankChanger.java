package com.indemnity83.irontanks.common.items;

import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.Item;

public class ItemTankChanger extends Item {

    private final TankChangerType type;

    public ItemTankChanger(TankChangerType type) {
        this.type = type;
        this.setMaxStackSize(1);
        this.setUnlocalizedName("irontanks." + type.itemName);
        this.setCreativeTab(CreativeTabs.MISC);
    }
}
