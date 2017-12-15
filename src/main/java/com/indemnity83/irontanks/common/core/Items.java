package com.indemnity83.irontanks.common.core;

import com.indemnity83.irontanks.IronTanks;
import com.indemnity83.irontanks.common.items.UpgradeItem;
import net.minecraftforge.fml.common.registry.GameRegistry;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class Items {
    @GameRegistry.ObjectHolder(IronTanks.MODID + ":copper_iron_upgrade")
    public static UpgradeItem copperIronUpgrade;

    @GameRegistry.ObjectHolder(IronTanks.MODID + ":copper_silver_upgrade")
    public static UpgradeItem copperSilverUpgrade;

    @GameRegistry.ObjectHolder(IronTanks.MODID + ":diamond_obsidian_upgrade")
    public static UpgradeItem diamondObsidianUpgrade;

    @GameRegistry.ObjectHolder(IronTanks.MODID + ":glass_copper_upgrade")
    public static UpgradeItem glassCopperUpgrade;

    @GameRegistry.ObjectHolder(IronTanks.MODID + ":glass_iron_upgrade")
    public static UpgradeItem glassIronUpgrade;

    @GameRegistry.ObjectHolder(IronTanks.MODID + ":gold_diamond_upgrade")
    public static UpgradeItem goldDiamondUpgrade;

    @GameRegistry.ObjectHolder(IronTanks.MODID + ":iron_gold_upgrade")
    public static UpgradeItem ironGoldUpgrade;

    @GameRegistry.ObjectHolder(IronTanks.MODID + ":silver_diamond_upgrade")
    public static UpgradeItem silverDiamondUpgrade;

    @GameRegistry.ObjectHolder(IronTanks.MODID + ":silver_gold_upgrade")
    public static UpgradeItem silverGoldUpgrade;





    public static void init() {
    }

    @SideOnly(Side.CLIENT)
    public static void initModels() {
        copperIronUpgrade.initModel();
        copperSilverUpgrade.initModel();
        diamondObsidianUpgrade.initModel();
        glassCopperUpgrade.initModel();
        glassIronUpgrade.initModel();
        goldDiamondUpgrade.initModel();
        ironGoldUpgrade.initModel();
        silverDiamondUpgrade.initModel();
        silverGoldUpgrade.initModel();
    }
}
