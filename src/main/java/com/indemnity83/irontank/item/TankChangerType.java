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

/**
 * Defines a type of upgrade item.
 * 
 * An upgrade item can change a tank of a fixed type (source) into another fixed
 * type (target)
 * 
 * @author Kyle Klaus
 *
 */
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

	/**
	 * IronTankType that this changer will work on
	 */
	public final IronTankType source;

	/**
	 * IronTankType that tank should be changed to on activation
	 */
	public final IronTankType target;

	/**
	 * The internal name of the changer type
	 */
	public final String name;

	/**
	 * A string array of the recipe for this changer type
	 */
	public final String[] recipe;

	/**
	 * Checks if this upgrade type can perform an upgrade on the given
	 * IronTankType
	 * 
	 * @param from type to be checked
	 * @return true if upgrade is allowed
	 */
	public boolean canUpgrade(IronTankType from) {
		return from == this.source;
	}
	
	private TankChangerType(IronTankType source, IronTankType target, String itemName, String... recipe) {
		this.source = source;
		this.target = target;
		this.name = itemName;
		this.recipe = recipe;
	}

}
