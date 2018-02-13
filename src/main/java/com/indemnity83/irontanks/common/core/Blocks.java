package com.indemnity83.irontanks.common.core;

import com.indemnity83.irontanks.IronTanks;
import com.indemnity83.irontanks.common.blocks.StackableTankBlock;
import com.indemnity83.irontanks.common.blocks.VoidTankBlock;
import net.minecraftforge.fml.common.registry.GameRegistry;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class Blocks {
    @GameRegistry.ObjectHolder(IronTanks.MODID + ":iron_tank")
    public static StackableTankBlock ironTank;

    @GameRegistry.ObjectHolder(IronTanks.MODID + ":gold_tank")
    public static StackableTankBlock goldTank;

    @GameRegistry.ObjectHolder(IronTanks.MODID + ":diamond_tank")
    public static StackableTankBlock diamondTank;

    @GameRegistry.ObjectHolder(IronTanks.MODID + ":obsidian_tank")
    public static StackableTankBlock obsidianTank;

    @GameRegistry.ObjectHolder(IronTanks.MODID + ":silver_tank")
    public static StackableTankBlock silverTank;

    @GameRegistry.ObjectHolder(IronTanks.MODID + ":copper_tank")
    public static StackableTankBlock copperTank;

    @GameRegistry.ObjectHolder(IronTanks.MODID + ":void_tank")
    public static VoidTankBlock voidTank;

    public static void init() {
        obsidianTank.setResistance(6000);
    }

    @SideOnly(Side.CLIENT)
    public static void initModels() {
        ironTank.initModel();
        goldTank.initModel();
        diamondTank.initModel();
        obsidianTank.initModel();
        copperTank.initModel();
        silverTank.initModel();
        voidTank.initModel();
    }
}
