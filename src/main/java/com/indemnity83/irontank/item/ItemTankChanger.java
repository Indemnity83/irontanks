package com.indemnity83.irontank.item;

import com.indemnity83.irontank.creativetab.IronTankTabs;

public class ItemTankChanger extends ItemIronTank {

	public ItemTankChanger(TankChangerType type)
	{
		super();
		this.setUnlocalizedName(type.getItemName());
		this.setCreativeTab(IronTankTabs.MainTab);
	}

}
