package com.indemnity83.irontank.init;

import static com.indemnity83.irontank.block.IronTankType.COPPER;
import static com.indemnity83.irontank.block.IronTankType.DIAMOND;
import static com.indemnity83.irontank.block.IronTankType.GLASS;
import static com.indemnity83.irontank.block.IronTankType.GOLD;
import static com.indemnity83.irontank.block.IronTankType.IRON;
import static com.indemnity83.irontank.block.IronTankType.OBSIDIAN;
import static com.indemnity83.irontank.block.IronTankType.SILVER;

import com.indemnity83.irontank.item.ItemIronTank;
import com.indemnity83.irontank.item.ItemTankChanger;
import com.indemnity83.irontank.item.TankChangerType;

import cpw.mods.fml.common.registry.GameRegistry;

public class ModItems {
	
	public static final ItemIronTank ironGoldUpgrade = new ItemTankChanger(TankChangerType.IRONGOLD);
	public static final ItemIronTank goldDiamondUpgrade = new ItemTankChanger(TankChangerType.GOLDDIAMOND);
	public static final ItemIronTank copperSilverUpgrade = new ItemTankChanger(TankChangerType.COPPERSILVER);
	public static final ItemIronTank silverGoldUpgrade = new ItemTankChanger(TankChangerType.SILVERGOLD);
	public static final ItemIronTank copperIronUpgrade = new ItemTankChanger(TankChangerType.COPPERIRON);
	public static final ItemIronTank glassIronUpgrade = new ItemTankChanger(TankChangerType.GLASSIRON);
	public static final ItemIronTank glassCopperUpgrade = new ItemTankChanger(TankChangerType.GLASSCOPPER);
	public static final ItemIronTank diamondObsidianUpgrade = new ItemTankChanger(TankChangerType.DIAMONDOBSIDIAN);
	public static final ItemIronTank silverDiamondUpgrade = new ItemTankChanger(TankChangerType.SILVERDIAMOND);
	
	public static void init()
	{
		GameRegistry.registerItem(ironGoldUpgrade, ironGoldUpgrade.getInternalName());
		GameRegistry.registerItem(goldDiamondUpgrade, goldDiamondUpgrade.getInternalName());
		GameRegistry.registerItem(copperSilverUpgrade, copperSilverUpgrade.getInternalName());
		GameRegistry.registerItem(silverGoldUpgrade, silverGoldUpgrade.getInternalName());
		GameRegistry.registerItem(copperIronUpgrade, copperIronUpgrade.getInternalName());
		GameRegistry.registerItem(glassIronUpgrade, glassIronUpgrade.getInternalName());
		GameRegistry.registerItem(glassCopperUpgrade, glassCopperUpgrade.getInternalName());
		GameRegistry.registerItem(diamondObsidianUpgrade, diamondObsidianUpgrade.getInternalName());
		GameRegistry.registerItem(silverDiamondUpgrade, silverDiamondUpgrade.getInternalName());
	}
}
