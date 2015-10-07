package com.indemnity83.irontank.init;

import com.indemnity83.irontank.block.BlockExtendedTank;
import com.indemnity83.irontank.item.ItemTankChanger;
import com.indemnity83.irontank.reference.TankChangerType;

import net.minecraft.item.ItemStack;
import net.minecraftforge.oredict.ShapedOreRecipe;
import cpw.mods.fml.common.registry.GameRegistry;

public class ModRecipies {

	public static void init() {
		// Item Recipes
		ModItems.ironGoldUpgrade.addRecipe();
		ModItems.goldDiamondUpgrade.addRecipe();
		ModItems.copperSilverUpgrade.addRecipe();
		ModItems.silverGoldUpgrade.addRecipe();
		ModItems.copperIronUpgrade.addRecipe();
		ModItems.glassIronUpgrade.addRecipe();
		ModItems.glassCopperUpgrade.addRecipe();
		ModItems.diamondObsidianUpgrade.addRecipe();
		ModItems.silverDiamondUpgrade.addRecipe();

		// Block Recipes
		ModBlocks.copperTank.addRecipe();
		ModBlocks.silverTank.addRecipe();
		ModBlocks.ironTank.addRecipe();
		ModBlocks.goldTank.addRecipe();
		ModBlocks.diamondTank.addRecipe();
		ModBlocks.obsidianTank.addRecipe();
	}

}
