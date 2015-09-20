package com.indemnity83.irontank.block;

import java.util.ArrayList;
import java.util.Arrays;

import com.indemnity83.irontank.item.ItemTankChanger;
import com.indemnity83.irontank.item.TankChangerType;

import cpw.mods.fml.common.registry.GameRegistry;

public enum IronTankType{
	IRON(32, true, "ironTank"),
    GOLD(48, true, "goldTank"),
    DIAMOND(64, true, "diamondTank"),
    COPPER(27, false, "copperTank"),
    SILVER(43, false, "silverTank"),
    OBSIDIAN(64, false, "obsidianTank"),
    GLASS(0, false, "");
	
	public int size;
	public String blockName;
	private boolean tieredTank;
	private BlockIronTank block;

	IronTankType(int size, boolean tieredTank, String name)
    {
        this.size = size;
        this.tieredTank = tieredTank;
        this.blockName = name;
    }

	public String getBlockName() {
		return this.blockName;
	}
	
	public boolean isExplosionResistant()
    {
        return this == OBSIDIAN;
    }

}
