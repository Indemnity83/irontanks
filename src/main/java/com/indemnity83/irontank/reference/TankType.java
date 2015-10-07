package com.indemnity83.irontank.reference;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import net.minecraft.init.Blocks;
import net.minecraft.init.Items;

import com.indemnity83.irontank.block.BlockExtendedTank;
import com.indemnity83.irontank.init.ModBlocks;
import com.indemnity83.irontank.item.ItemTankChanger;

import cpw.mods.fml.common.registry.GameRegistry;

/**
 * Defines a type of tank.
 * 
 * Tank definitions include all variables required to create a tank block. A
 * given tank type can have multiple crafting recipes and multiple primary
 * crafting materials
 * 
 * @author Kyle Klaus
 *
 */
public enum TankType{
    IRON(32, "ironTank", Arrays.asList("ingotIron", "ingotRefinedIron"), Arrays.asList("tgtg0gtgt", "gggt4tggg"), 25/3F),
    GOLD(48, "goldTank", Arrays.asList("ingotGold"), Arrays.asList("tgtg1gtgt", "gggt5tggg"), 25/3F),
    DIAMOND(64, "diamondTank", Arrays.asList("gemDiamond"), Arrays.asList("gggt2tggg", "gggg5gttt"), 25/3F),
    COPPER(27, "copperTank", Arrays.asList("ingotCopper"), Arrays.asList("tgtg0gtgt"), 25/3F),
    SILVER(43, "silverTank", Arrays.asList("ingotSilver"), Arrays.asList("tgtg4gtgt", "gggt1tggg"), 25/3F),
    OBSIDIAN(64, "obsidianTank", Arrays.asList("obsidian"), Arrays.asList("tgtg3gtgt"), 6000000.0F),
    GLASS(0, "", Arrays.asList("blockGlass"), Arrays.asList(""), 0f);

	/**
	 * fluid capacity of the tank type
	 */
	public final int capacity;

	/**
	 * The internal name of the tank type
	 */
	public final String name;

	/**
	 * List of primary crafting materials that may be used in crafting recipe.
	 * These materials should substitute for 't' in the recipes list.
	 */
	public final ArrayList<String> materials;

	/**
	 * List of crafting recipes for tank type
	 */
	public final ArrayList<String> recipes;

	/**
	 * Blast resistance of the tank type
	 */
	public final float resistance;

	TankType(int capacity, String name, List<String> materials, List<String> recipes, float resistance) {
		this.capacity = capacity;
		this.name = name;
		this.materials = new ArrayList<String>();
		this.recipes = new ArrayList<String>();
		this.resistance = resistance;

		this.materials.addAll(materials);
		this.recipes.addAll(recipes);
	}

}
