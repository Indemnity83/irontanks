package com.indemnity83.irontank.init;

import com.indemnity83.irontank.block.BlockIronTank;

import cpw.mods.fml.common.registry.GameRegistry;

public class ModBlocks {
	
	public static final BlockIronTank copperTank = new BlockIronTank("copperTank", 27);
	public static final BlockIronTank silverTank = new BlockIronTank("silverTank", 32);
	public static final BlockIronTank ironTank = new BlockIronTank("ironTank", 43);
	public static final BlockIronTank goldTank = new BlockIronTank("goldTank", 48);
	public static final BlockIronTank diamondTank = new BlockIronTank("diamondTank", 64);
	public static final BlockIronTank obsidianTank = new BlockIronTank("obsidianTank", 64);
	
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
