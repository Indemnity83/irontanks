package com.indemnity83.irontanks.common.core;

import com.indemnity83.irontanks.IronTanks;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.ItemStack;

/** Dedicated creative tab for all Iron Tanks blocks and upgrade items, iconed with the glass tank. */
public class IronTanksTab extends CreativeTabs {
    public static final IronTanksTab INSTANCE = new IronTanksTab();

    private IronTanksTab() {
        super(IronTanks.MODID);
    }

    @Override
    public ItemStack getTabIconItem() {
        return new ItemStack(Blocks.glassTank);
    }
}
