package com.indemnity83.irontank.init;

import com.indemnity83.irontank.block.BlockIronTank;
import com.indemnity83.irontank.block.TankType;

import cpw.mods.fml.common.registry.GameRegistry;

public class ModBlocks {
	
	public static final BlockIronTank copperTank = new BlockIronTank(TankType.COPPER);
	public static final BlockIronTank silverTank = new BlockIronTank(TankType.SILVER);
	public static final BlockIronTank ironTank = new BlockIronTank(TankType.IRON);
	public static final BlockIronTank goldTank = new BlockIronTank(TankType.GOLD);
	public static final BlockIronTank diamondTank = new BlockIronTank(TankType.DIAMOND);
	public static final BlockIronTank obsidianTank = new BlockIronTank(TankType.OBSIDIAN);
	
	public static void init()
	{
		GameRegistry.registerBlock(ironTank, ironTank.getInternalName());
		GameRegistry.registerBlock(goldTank, goldTank.getInternalName());
		GameRegistry.registerBlock(diamondTank, diamondTank.getInternalName());
		GameRegistry.registerBlock(obsidianTank, obsidianTank.getInternalName());
		GameRegistry.registerBlock(copperTank, copperTank.getInternalName());
		GameRegistry.registerBlock(silverTank, silverTank.getInternalName());
	}

}
