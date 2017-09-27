package com.indemnity83.irontanks.common.items;

import com.indemnity83.irontanks.common.blocks.IronTankType;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.common.registry.GameRegistry;
import net.minecraftforge.oredict.ShapedOreRecipe;

import static com.indemnity83.irontanks.common.blocks.IronTankType.*;

public enum TankChangerType {

    GLASS_COPPER(GLASS, COPPER, "glass_copper_tank_upgrade", "gggtstggg"),
    GLASS_IRON(GLASS, IRON, "glass_iron_tank_upgrade", "tgtgsgtgt"),
    COPPER_IRON(COPPER, IRON, "copper_iron_tank_upgrade", "gggtstggg"),
    COPPER_SILVER(COPPER, SILVER, "copper_silver_tank_upgrade", "tgtgsgtgt"),
    IRON_GOLD(IRON, GOLD, "iron_gold_tank_upgrade", "tgtgsgtgt"),
    SILVER_GOLD(SILVER, GOLD, "silver_gold_tank_upgrade", "gggtstggg"),
    GOLD_DIAMOND(GOLD, DIAMOND, "gold_diamond_tank_upgrade", "gggtstggg"),
    DIAMOND_OBSIDIAN(DIAMOND, OBSIDIAN, "diamond_obsidian_tank_upgrade", "tgtgsgtgt");

    public static final TankChangerType VALUES[] = values();

    public final IronTankType upgradeFrom;
    public final IronTankType upgradeTo;
    public final String itemName;
    public final String recipe;
    public ItemTankChanger item;

    TankChangerType(IronTankType upgradeFrom, IronTankType upgradeTo, String itemName, String recipe) {
        this.upgradeFrom = upgradeFrom;
        this.upgradeTo = upgradeTo;
        this.itemName = itemName.toLowerCase();
        this.recipe = recipe;
    }

    public static void buildItems() {
        for (TankChangerType type : VALUES) {
            type.buildItem();
        }
    }

    public ItemTankChanger buildItem() {
        this.item = new ItemTankChanger(this);

        this.item.setRegistryName(this.itemName);
        GameRegistry.register(this.item);

        for (String upgradeFromMaterial : this.upgradeFrom.materials) {
            Object sourceMaterial = translateOreName(upgradeFromMaterial);

            for (String upgradeToMaterial : this.upgradeTo.materials) {
                Object targetMaterial = translateOreName(upgradeToMaterial);

                String[] recipeSplit = new String[]{recipe.substring(0, 3), recipe.substring(3, 6), recipe.substring(6, 9)};
                GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(this.item), recipeSplit, 's', sourceMaterial, 't', targetMaterial, 'g', "blockGlass"));
            }
        }

        return this.item;
    }

    public boolean canUpgrade(IronTankType tankType) {
        return tankType == upgradeFrom;
    }
}
