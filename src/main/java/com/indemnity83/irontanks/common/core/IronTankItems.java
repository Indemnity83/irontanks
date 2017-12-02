package com.indemnity83.irontanks.common.core;

import com.indemnity83.irontanks.common.items.ItemIronTank;
import com.indemnity83.irontanks.common.items.ItemTankChanger;

public class IronTankItems {
    public static ItemIronTank ironTankItemBlock;
    public static ItemTankChanger glassToCopperTankUpgrade;
    public static ItemTankChanger glassToIronTankUpgrade;
    public static ItemTankChanger copperToIronTankUpgrade;
    public static ItemTankChanger copperToSilverTankUpgrade;
    public static ItemTankChanger ironToGoldTankUpgrade;
    public static ItemTankChanger silverToGoldTankUpgrade;
    public static ItemTankChanger goldToDiamondTankUpgrade;
    public static ItemTankChanger diamondToObsidianTankUpgrade;

    public static void init() {
        ironTankItemBlock = new ItemIronTank(IronTankBlocks.ironTankBlock);
    }
}
