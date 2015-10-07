package com.indemnity83.irontank.init;

import static com.indemnity83.irontank.reference.TankType.COPPER;
import static com.indemnity83.irontank.reference.TankType.DIAMOND;
import static com.indemnity83.irontank.reference.TankType.GLASS;
import static com.indemnity83.irontank.reference.TankType.GOLD;
import static com.indemnity83.irontank.reference.TankType.IRON;
import static com.indemnity83.irontank.reference.TankType.OBSIDIAN;
import static com.indemnity83.irontank.reference.TankType.SILVER;

import com.indemnity83.irontank.item.ItemGeneric;
import com.indemnity83.irontank.item.ItemTankChanger;
import com.indemnity83.irontank.reference.TankChangerType;

import cpw.mods.fml.common.registry.GameRegistry;

public class ModItems {

	public static final ItemGeneric ironGoldUpgrade = new ItemTankChanger(TankChangerType.IRONGOLD);
	public static final ItemGeneric goldDiamondUpgrade = new ItemTankChanger(TankChangerType.GOLDDIAMOND);
	public static final ItemGeneric copperSilverUpgrade = new ItemTankChanger(TankChangerType.COPPERSILVER);
	public static final ItemGeneric silverGoldUpgrade = new ItemTankChanger(TankChangerType.SILVERGOLD);
	public static final ItemGeneric copperIronUpgrade = new ItemTankChanger(TankChangerType.COPPERIRON);
	public static final ItemGeneric glassIronUpgrade = new ItemTankChanger(TankChangerType.GLASSIRON);
	public static final ItemGeneric glassCopperUpgrade = new ItemTankChanger(TankChangerType.GLASSCOPPER);
	public static final ItemGeneric diamondObsidianUpgrade = new ItemTankChanger(TankChangerType.DIAMONDOBSIDIAN);
	public static final ItemGeneric silverDiamondUpgrade = new ItemTankChanger(TankChangerType.SILVERDIAMOND);

	public static void init() {
		GameRegistry.registerItem(ironGoldUpgrade, TankChangerType.IRONGOLD.name);
		GameRegistry.registerItem(goldDiamondUpgrade, TankChangerType.GOLDDIAMOND.name);
		GameRegistry.registerItem(copperSilverUpgrade, TankChangerType.COPPERSILVER.name);
		GameRegistry.registerItem(silverGoldUpgrade, TankChangerType.SILVERGOLD.name);
		GameRegistry.registerItem(copperIronUpgrade, TankChangerType.COPPERIRON.name);
		GameRegistry.registerItem(glassIronUpgrade, TankChangerType.GLASSIRON.name);
		GameRegistry.registerItem(glassCopperUpgrade, TankChangerType.GLASSCOPPER.name);
		GameRegistry.registerItem(diamondObsidianUpgrade, TankChangerType.DIAMONDOBSIDIAN.name);
		GameRegistry.registerItem(silverDiamondUpgrade, TankChangerType.SILVERDIAMOND.name);
	}
}
