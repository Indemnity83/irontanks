package com.indemnity83.irontank.block;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import net.minecraft.init.Blocks;
import net.minecraft.init.Items;

import com.indemnity83.irontank.item.ItemTankChanger;
import com.indemnity83.irontank.item.TankChangerType;

import cpw.mods.fml.common.registry.GameRegistry;

public enum IronTankType{
	IRON(32, "ironTank", Arrays.asList("ingotIron", "ingotRefinedIron"), Arrays.asList("tgtg0gtgt", "gggt4tggg")),  //1
    GOLD(48, "goldTank", Arrays.asList("ingotGold"), Arrays.asList("tgtg1gtgt", "gggt5tggg")),						//2
    DIAMOND(64, "diamondTank", Arrays.asList("gemDiamond"), Arrays.asList("gggt2tggg", "gggg5gttt")),				//3
    COPPER(27, "copperTank", Arrays.asList("ingotCopper"), Arrays.asList("tgtg0gtgt")),								//4
    SILVER(43, "silverTank", Arrays.asList("ingotSilver"), Arrays.asList("tgtg4gtgt", "gggt1tggg")),				//5
    OBSIDIAN(64, "obsidianTank", Arrays.asList("obsidian"), Arrays.asList("tgtg3gtgt")),							//6
    GLASS(0, "", Arrays.asList("blockGlass"), Arrays.asList("")), ;													//0
	
	private int size;
	private String blockName;
	private BlockIronTank block;
	private ArrayList<String> matList;
	private ArrayList<String> recipeList;

	IronTankType(int size, String name, List<String> mats, List<String> recipes)
    {
        this.size = size;
        this.blockName = name;
        this.matList = new ArrayList<String>();
        this.recipeList = new ArrayList<String>();
        
        matList.addAll(mats);
        recipeList.addAll(recipes);
    }

	public String getBlockName() {
		return this.blockName;
	}
	
	public boolean isExplosionResistant()
    {
        return this == OBSIDIAN;
    }
	
	public List<String> getRecipes() {
		return recipeList;
	}

	public List<String> getMatList()
    {
        return matList;
    }

	public int getTankVolume() {
		return size;
	}

}
