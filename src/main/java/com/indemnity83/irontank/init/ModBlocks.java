package com.indemnity83.irontank.init;

import com.indemnity83.irontank.block.BlockExtendedTank;
import com.indemnity83.irontank.reference.TankType;

import cpw.mods.fml.common.registry.GameRegistry;

public class ModBlocks {

	public static final BlockExtendedTank copperTank = new BlockExtendedTank(TankType.COPPER);
	public static final BlockExtendedTank silverTank = new BlockExtendedTank(TankType.SILVER);
	public static final BlockExtendedTank ironTank = new BlockExtendedTank(TankType.IRON);
	public static final BlockExtendedTank goldTank = new BlockExtendedTank(TankType.GOLD);
	public static final BlockExtendedTank diamondTank = new BlockExtendedTank(TankType.DIAMOND);
	public static final BlockExtendedTank obsidianTank = new BlockExtendedTank(TankType.OBSIDIAN);

	public static void init() {
		GameRegistry.registerBlock(ironTank, ironTank.type.name);
		GameRegistry.registerBlock(goldTank, goldTank.type.name);
		GameRegistry.registerBlock(diamondTank, diamondTank.type.name);
		GameRegistry.registerBlock(obsidianTank, obsidianTank.type.name);
		GameRegistry.registerBlock(copperTank, copperTank.type.name);
		GameRegistry.registerBlock(silverTank, silverTank.type.name);
	}

}
