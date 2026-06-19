package com.indemnity83.irontanks.common.core;

import com.indemnity83.irontanks.IronTanks;
import com.indemnity83.irontanks.common.blocks.CreativeTankBlock;
import com.indemnity83.irontanks.common.blocks.StackableTankBlock;
import com.indemnity83.irontanks.common.blocks.VoidTankBlock;
import net.minecraft.block.Block;
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

    @GameRegistry.ObjectHolder(IronTanks.MODID + ":emerald_tank")
    public static StackableTankBlock emeraldTank;

    @GameRegistry.ObjectHolder(IronTanks.MODID + ":aluminium_tank")
    public static StackableTankBlock aluminiumTank;

    @GameRegistry.ObjectHolder(IronTanks.MODID + ":stainlesssteel_tank")
    public static StackableTankBlock stainlessSteelTank;

    @GameRegistry.ObjectHolder(IronTanks.MODID + ":titanium_tank")
    public static StackableTankBlock titaniumTank;

    @GameRegistry.ObjectHolder(IronTanks.MODID + ":tungstensteel_tank")
    public static StackableTankBlock tungstenSteelTank;

    @GameRegistry.ObjectHolder(IronTanks.MODID + ":void_tank")
    public static VoidTankBlock voidTank;

    @GameRegistry.ObjectHolder(IronTanks.MODID + ":creative_tank")
    public static CreativeTankBlock creativeTank;

    public static void init() {
        // Per-tier hardness (mining time) and resistance (blast resistance); obsidian stays blast-proof.
        setStrength(copperTank, 4.0F, 2.0F);
        setStrength(ironTank, 5.0F, 3.0F);
        setStrength(silverTank, 6.0F, 5.0F);
        setStrength(goldTank, 7.0F, 4.0F);
        setStrength(diamondTank, 8.0F, 6.0F);
        setStrength(emeraldTank, 8.0F, 6.0F);
        setStrength(aluminiumTank, 5.0F, 4.0F);
        setStrength(stainlessSteelTank, 9.0F, 8.0F);
        setStrength(titaniumTank, 10.0F, 10.0F);
        setStrength(tungstenSteelTank, 12.0F, 14.0F);
        setStrength(obsidianTank, 50.0F, 1200.0F);
        setStrength(voidTank, 5.0F, 6.0F);
        setStrength(creativeTank, 5.0F, 6.0F);

        if (!IronTanksConfig.creativeTankBreakable) {
            creativeTank.setBlockUnbreakable();
        }
    }

    private static void setStrength(Block tank, float hardness, float resistance) {
        tank.setHardness(hardness);
        tank.setResistance(resistance);
    }

    @SideOnly(Side.CLIENT)
    public static void initModels() {
        ironTank.initModel();
        goldTank.initModel();
        diamondTank.initModel();
        obsidianTank.initModel();
        copperTank.initModel();
        emeraldTank.initModel();
        aluminiumTank.initModel();
        stainlessSteelTank.initModel();
        titaniumTank.initModel();
        tungstenSteelTank.initModel();
        silverTank.initModel();
        voidTank.initModel();
        creativeTank.initModel();
    }
}
