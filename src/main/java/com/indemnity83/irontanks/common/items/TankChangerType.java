package com.indemnity83.irontanks.common.items;

import com.indemnity83.irontanks.common.blocks.IronTankType;
import net.minecraftforge.fml.common.registry.GameRegistry;

import static com.indemnity83.irontanks.common.blocks.IronTankType.*;

public enum TankChangerType {

    GLASS_COPPER(GLASS, COPPER, "glass_copper_tank_upgrade"),
    GLASS_IRON(GLASS, IRON, "glass_iron_tank_upgrade"),
    COPPER_IRON(COPPER, IRON, "copper_iron_tank_upgrade"),
    COPPER_SILVER(COPPER, SILVER, "copper_silver_tank_upgrade"),
    IRON_GOLD(IRON, GOLD, "iron_gold_tank_upgrade"),
    SILVER_GOLD(SILVER, GOLD, "silver_gold_tank_upgrade"),
    GOLD_DIAMOND(GOLD, DIAMOND, "gold_diamond_tank_upgrade"),
    DIAMOND_OBSIDIAN(DIAMOND, OBSIDIAN, "diamond_obsidian_tank_upgrade");

    public static final TankChangerType VALUES[] = values();

    public final IronTankType upgradeFrom;
    public final IronTankType upgradeTo;
    public final String itemName;
    public ItemTankChanger item;

    TankChangerType(IronTankType upgradeFrom, IronTankType upgradeTo, String itemName) {
        this.upgradeFrom = upgradeFrom;
        this.upgradeTo = upgradeTo;
        this.itemName = itemName.toLowerCase();
    }

    public static void buildItems() {
        for (TankChangerType type : VALUES) {
            type.buildItem();
            // TODO: Register recipe for tank changer items
        }
    }

    public ItemTankChanger buildItem() {
        this.item = new ItemTankChanger(this);

        this.item.setRegistryName(this.itemName);

        GameRegistry.register(this.item);

        return this.item;
    }

    public boolean canUpgrade(IronTankType tankType) {
        return tankType == upgradeFrom;
    }
}
