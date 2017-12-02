package com.indemnity83.irontanks.common.items;

import com.indemnity83.irontanks.common.blocks.IronTankType;
import com.indemnity83.irontanks.common.core.IronTankItems;

import static com.indemnity83.irontanks.common.blocks.IronTankType.*;

public enum TankChangerType {

    GLASS_COPPER(GLASS, COPPER, "glass_copper_tank_upgrade", "gggtstggg", IronTankItems.glassToCopperTankUpgrade),
    GLASS_IRON(GLASS, IRON, "glass_iron_tank_upgrade", "tgtgsgtgt", IronTankItems.glassToIronTankUpgrade),
    COPPER_IRON(COPPER, IRON, "copper_iron_tank_upgrade", "gggtstggg", IronTankItems.copperToIronTankUpgrade),
    COPPER_SILVER(COPPER, SILVER, "copper_silver_tank_upgrade", "tgtgsgtgt", IronTankItems.copperToSilverTankUpgrade),
    IRON_GOLD(IRON, GOLD, "iron_gold_tank_upgrade", "tgtgsgtgt", IronTankItems.ironToGoldTankUpgrade),
    SILVER_GOLD(SILVER, GOLD, "silver_gold_tank_upgrade", "gggtstggg", IronTankItems.silverToGoldTankUpgrade),
    GOLD_DIAMOND(GOLD, DIAMOND, "gold_diamond_tank_upgrade", "gggtstggg", IronTankItems.goldToDiamondTankUpgrade),
    DIAMOND_OBSIDIAN(DIAMOND, OBSIDIAN, "diamond_obsidian_tank_upgrade", "tgtgsgtgt", IronTankItems.diamondToObsidianTankUpgrade);

    public static final TankChangerType VALUES[] = values();

    public final IronTankType upgradeFrom;
    public final IronTankType upgradeTo;
    public final String itemName;
    public final String recipe;
    public ItemTankChanger item;

    TankChangerType(IronTankType upgradeFrom, IronTankType upgradeTo, String itemName, String recipe, ItemTankChanger item) {
        this.upgradeFrom = upgradeFrom;
        this.upgradeTo = upgradeTo;
        this.itemName = itemName.toLowerCase();
        this.recipe = recipe;
        this.item = item;
    }

    public boolean canUpgrade(IronTankType tankType) {
        return tankType == upgradeFrom;
    }

}
