package com.indemnity83.irontank.reference;

import static com.indemnity83.irontank.reference.TankType.*;

import java.util.List;

import net.minecraft.item.ItemStack;
import net.minecraftforge.common.config.Configuration;


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
	IRONGOLD(IRON, GOLD, "iron_gold_upgrade", "tgt", "gsg", "tgt"),
	GOLDDIAMOND(GOLD, DIAMOND, "gold_diamond_upgrade", "ggg", "tst", "ggg"),
	SILVERDIAMOND(SILVER, DIAMOND, "silver_diamond_upgrade", "ggg", "gsg", "ttt"),
	COPPERSILVER(COPPER, SILVER, "copper_silver_upgrade", "tgt", "gsg", "tgt"),
	SILVERGOLD(SILVER, GOLD, "silver_gold_upgrade", "ggg", "tst", "ggg"),
	COPPERIRON(COPPER, IRON, "copper_iron_upgrade", "ggg", "tst", "ggg"),
	GLASSIRON(GLASS, IRON, "glass_iron_upgrade", "tgt", "gsg", "tgt"),
	GLASSCOPPER(GLASS, COPPER, "glass_copper_upgrade", "ggg", "tst", "ggg"),
	DIAMONDOBSIDIAN(DIAMOND, OBSIDIAN, "diamond_obsidian_upgrade", "tgt", "gsg", "tgt"),
	DIAMONDEMERALD(DIAMOND, EMERALD, "diamond_emerald_upgrade", "ggg", "tst", "ggg");

	/**
	 * IronTankType that this changer will work on
	 */
	public final TankType source;

	/**
	 * IronTankType that tank should be changed to on activation
	 */
	public final TankType target;

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
	public boolean canUpgrade(TankType from) {
		return from == this.source;
	}

	private TankChangerType(TankType source, TankType target, String itemName, String... recipe) {
		this.source = source;
		this.target = target;
		this.name = itemName;
		this.recipe = recipe;
	}

}
