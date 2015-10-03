package com.indemnity83.irontank.item;

import static com.indemnity83.irontank.block.IronTankType.COPPER;
import static com.indemnity83.irontank.block.IronTankType.DIAMOND;
import static com.indemnity83.irontank.block.IronTankType.GOLD;
import static com.indemnity83.irontank.block.IronTankType.IRON;
import static com.indemnity83.irontank.block.IronTankType.OBSIDIAN;
import static com.indemnity83.irontank.block.IronTankType.SILVER;
import static com.indemnity83.irontank.block.IronTankType.GLASS;

import java.util.List;

import net.minecraft.item.ItemStack;
import net.minecraftforge.common.config.Configuration;

import com.indemnity83.irontank.block.IronTankType;

import cpw.mods.fml.common.registry.GameRegistry;

public enum TankChangerType {
	IRONGOLD(IRON, GOLD, "ironGoldUpgrade", "tgt", "gsg", "tgt"),
	GOLDDIAMOND(GOLD, DIAMOND, "goldDiamondUpgrade", "ggg", "tst", "ggg"),
	SILVERDIAMOND(SILVER, DIAMOND, "silverDiamondUpgrade", "ggg", "gsg", "ttt"),
    COPPERSILVER(COPPER, SILVER, "copperSilverUpgrade", "tgt", "gsg", "tgt"),
    SILVERGOLD(SILVER, GOLD, "silverGoldUpgrade", "ggg", "tst", "ggg"),
    COPPERIRON(COPPER, IRON, "copperIronUpgrade", "ggg", "tst", "ggg"),
    GLASSIRON(GLASS, IRON, "glassIronUpgrade", "tgt", "gsg", "tgt"),
    GLASSCOPPER(GLASS, COPPER, "glassCopperUpgrade", "ggg", "tst", "ggg"),
    DIAMONDOBSIDIAN(DIAMOND, OBSIDIAN, "diamondObsidianUpgrade", "tgt", "gsg", "tgt");
	
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
	
	public String getItemName() 
	{
		return this.itemName;
	}

	public Object[] getRecipe() {
		return recipe;
	}

	public List<String> getSourceMats() {
		return source.getMatList();
	}

	public List<String> getTargetMats() {
		return target.getMatList();
	}

	public IronTankType getTarget() {
		return this.target;
	}

}
