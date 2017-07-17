package com.indemnity83.irontanks.common.items;

import net.minecraftforge.fml.common.registry.GameRegistry;

public enum TankChangerType {

    GLASS_COPPER("glass_copper_tank_upgrade"),
    GLASS_IRON("glass_iron_tank_upgrade"),
    COPPER_IRON("copper_iron_tank_upgrade"),
    COPPER_SILVER("copper_silver_tank_upgrade"),
    IRON_GOLD("iron_gold_tank_upgrade"),
    SILVER_GOLD("silver_gold_tank_upgrade"),
    GOLD_DIAMOND("gold_diamond_tank_upgrade"),
    DIAMOND_OBSIDIAN("diamond_obsidian_tank_upgrade");

    public static final TankChangerType VALUES[] = values();

    public final String itemName;
    public ItemTankChanger item;

    TankChangerType(String itemName) {
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
}
