package com.indemnity83.irontank.item;

import static com.indemnity83.irontank.block.IronTankType.COPPER;
import static com.indemnity83.irontank.block.IronTankType.DIAMOND;
import static com.indemnity83.irontank.block.IronTankType.GOLD;
import static com.indemnity83.irontank.block.IronTankType.IRON;
import static com.indemnity83.irontank.block.IronTankType.OBSIDIAN;
import static com.indemnity83.irontank.block.IronTankType.SILVER;
import static com.indemnity83.irontank.block.IronTankType.GLASS;
import net.minecraftforge.common.config.Configuration;

import com.indemnity83.irontank.block.IronTankType;

import cpw.mods.fml.common.registry.GameRegistry;

public enum TankChangerType {
	IRONGOLD(IRON, GOLD, "ironGoldUpgrade", "mmm", "msm", "mmm"),
	GOLDDIAMOND(GOLD, DIAMOND, "goldDiamondUpgrade", "mmm", "msm", "mmm"),
    COPPERSILVER(COPPER, SILVER, "copperSilverUpgrade", "mmm", "msm", "mmm"),
    SILVERGOLD(SILVER, GOLD, "silverGoldUpgrade", "mmm", "msm", "mmm"),
    COPPERIRON(COPPER, IRON, "copperIronUpgrade", "mmm", "msm", "mmm"),
    GLASSIRON(GLASS, IRON, "glassIronUpgrade", "mmm", "msm", "mmm"),
    GLASSCOPPER(GLASS, COPPER, "glassCopperUpgrade", "mmm", "msm", "mmm"),
    DIAMONDOBSIDIAN(DIAMOND, OBSIDIAN, "diamondObsidianUpgrade", "mmm", "msm", "mmm");
	
	private IronTankType source;
	private IronTankType target;
	private String itemName;
	private String[] recipe;
	private ItemTankChanger item;
	
	private TankChangerType(IronTankType source, IronTankType target, String itemName, String... recipe) 
	{
		this.source = source;
		this.target = target;
		this.itemName = itemName;
		this.recipe = recipe;
	}
	
	public boolean canUpgrade(IronTankType from)
    {
        return from == this.source;
    }

	public ItemTankChanger buildItem()
    {
        item = new ItemTankChanger(this);
        GameRegistry.registerItem(item, itemName);
        return item;
    }
	
	public static void buildItems()
    {
        for (TankChangerType type : values())
        {
            type.buildItem();
        }
    }
	
	public String getItemName() 
	{
		return this.itemName;
	}

}
