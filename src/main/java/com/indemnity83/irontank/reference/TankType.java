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
    IRON(32, "irontank", Arrays.asList("ingotIron", "ingotRefinedIron"), Arrays.asList("tgtg0gtgt", "gggt4tggg"), 3.0F, 5.0F),
    GOLD(48, "goldtank", Arrays.asList("ingotGold"), Arrays.asList("tgtg1gtgt", "gggt5tggg"), 4.0F, 7.0F),
    DIAMOND(64, "diamondtank", Arrays.asList("gemDiamond"), Arrays.asList("gggt2tggg", "gggg5gttt"), 6.0F, 8.0F),
    COPPER(27, "coppertank", Arrays.asList("ingotCopper"), Arrays.asList("tgtg0gtgt"), 2.0F, 4.0F),
    SILVER(43, "silvertank", Arrays.asList("ingotSilver"), Arrays.asList("tgtg4gtgt", "gggt1tggg"), 5.0F, 6.0F),
    OBSIDIAN(64, "obsidiantank", Arrays.asList("obsidian"), Arrays.asList("tgtg3gtgt"), 1200.0F, 50.0F),
    GLASS(0, "", Arrays.asList("blockGlass"), Arrays.asList(""), 0.3F, 0.3F),
    EMERALD(96, "emeraldtank", Arrays.asList("gemEmerald"), Arrays.asList("gggt3tggg"), 6.0F, 8.0F),
    ALUMINIUM(96, "aluminiumtank", Arrays.asList("ingotAluminium", "ingotAluminum"), Arrays.asList("gggt3tggg"), 4.0F, 5.0F),
    STAINLESSSTEEL(128, "stainlesssteeltank", Arrays.asList("ingotStainlessSteel"), Arrays.asList("gggt8tggg", "gggt7tggg"), 8.0F, 9.0F),
    TITANIUM(256, "titaniumtank", Arrays.asList("ingotTitanium"), Arrays.asList("gggt9tggg"), 10.0F, 10.0F),
    TUNGSTENSTEEL(512, "tungstensteeltank", Arrays.asList("ingotTungstenSteel"), Arrays.asList("gggtztggg"), 14.0F, 12.0F);

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

	/**
	 * Hardness (mining time) of the tank type
	 */
	public final float hardness;

	TankType(int capacity, String name, List<String> materials, List<String> recipes, float resistance, float hardness) {
		this.capacity = capacity;
		this.name = name;
		this.materials = new ArrayList<String>();
		this.recipes = new ArrayList<String>();
		this.resistance = resistance;
		this.hardness = hardness;

		this.materials.addAll(materials);
		this.recipes.addAll(recipes);
	}

}
