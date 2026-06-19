package com.indemnity83.irontank.init;

import com.indemnity83.irontank.block.BlockExtendedTank;
import com.indemnity83.irontank.item.ItemBlockExtendedTank;
import com.indemnity83.irontank.reference.TankType;

import cpw.mods.fml.common.registry.GameRegistry;

public class ModBlocks {

	public static final BlockExtendedTank copperTank = new BlockExtendedTank(TankType.COPPER);
	public static final BlockExtendedTank silverTank = new BlockExtendedTank(TankType.SILVER);
	public static final BlockExtendedTank ironTank = new BlockExtendedTank(TankType.IRON);
	public static final BlockExtendedTank goldTank = new BlockExtendedTank(TankType.GOLD);
	public static final BlockExtendedTank diamondTank = new BlockExtendedTank(TankType.DIAMOND);
	public static final BlockExtendedTank obsidianTank = new BlockExtendedTank(TankType.OBSIDIAN);
	public static final BlockExtendedTank emeraldTank = new BlockExtendedTank(TankType.EMERALD);
	public static final BlockExtendedTank aluminiumTank = new BlockExtendedTank(TankType.ALUMINIUM);
	public static final BlockExtendedTank stainlessSteelTank = new BlockExtendedTank(TankType.STAINLESSSTEEL);
	public static final BlockExtendedTank titaniumTank = new BlockExtendedTank(TankType.TITANIUM);
	public static final BlockExtendedTank tungstenSteelTank = new BlockExtendedTank(TankType.TUNGSTENSTEEL);

	public static void init() {
		GameRegistry.registerBlock(ironTank, ItemBlockExtendedTank.class, ironTank.type.name);
		GameRegistry.registerBlock(goldTank, ItemBlockExtendedTank.class, goldTank.type.name);
		GameRegistry.registerBlock(diamondTank, ItemBlockExtendedTank.class, diamondTank.type.name);
		GameRegistry.registerBlock(obsidianTank, ItemBlockExtendedTank.class, obsidianTank.type.name);
		GameRegistry.registerBlock(emeraldTank, ItemBlockExtendedTank.class, emeraldTank.type.name);
		GameRegistry.registerBlock(copperTank, ItemBlockExtendedTank.class, copperTank.type.name);
		GameRegistry.registerBlock(silverTank, ItemBlockExtendedTank.class, silverTank.type.name);
		GameRegistry.registerBlock(aluminiumTank, ItemBlockExtendedTank.class, aluminiumTank.type.name);
		GameRegistry.registerBlock(stainlessSteelTank, ItemBlockExtendedTank.class, stainlessSteelTank.type.name);
		GameRegistry.registerBlock(titaniumTank, ItemBlockExtendedTank.class, titaniumTank.type.name);
		GameRegistry.registerBlock(tungstenSteelTank, ItemBlockExtendedTank.class, tungstenSteelTank.type.name);
	}

}
