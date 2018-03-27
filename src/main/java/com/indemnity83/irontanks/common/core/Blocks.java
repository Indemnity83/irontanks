package com.indemnity83.irontanks.common.core;

import com.indemnity83.irontanks.IronTanks;
import com.indemnity83.irontanks.common.blocks.TankBlock;
import net.minecraftforge.fml.common.registry.GameRegistry;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class Blocks {
    @GameRegistry.ObjectHolder(IronTanks.MODID + ":iron_tank")
    public static TankBlock ironTank;

    @GameRegistry.ObjectHolder(IronTanks.MODID + ":gold_tank")
    public static TankBlock goldTank;

    @GameRegistry.ObjectHolder(IronTanks.MODID + ":diamond_tank")
    public static TankBlock diamondTank;

    @GameRegistry.ObjectHolder(IronTanks.MODID + ":obsidian_tank")
    public static TankBlock obsidianTank;

    @GameRegistry.ObjectHolder(IronTanks.MODID + ":silver_tank")
    public static TankBlock silverTank;

    @GameRegistry.ObjectHolder(IronTanks.MODID + ":copper_tank")
    public static TankBlock copperTank;

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
    }
}
