package com.indemnity83.irontank.item;

import com.indemnity83.irontank.creativetab.IronTankTabs;
import com.indemnity83.irontank.reference.Reference;
import com.indemnity83.irontank.reference.TankType;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;

public abstract class ItemGeneric extends Item {

	public ItemGeneric() {
		super();

		this.setCreativeTab(IronTankTabs.MainTab);
	}

	@Override
	public String getUnlocalizedName() {
		return String.format("item.%s%s", Reference.MODID.toLowerCase() + ":",
				getUnwrappedUnlocalizedName(super.getUnlocalizedName()));
	}

	@Override
	public String getUnlocalizedName(ItemStack itemStack) {
		return getUnlocalizedName();
	}

	@Override
	@SideOnly(Side.CLIENT)
	public void registerIcons(IIconRegister iconRegister) {
		itemIcon = iconRegister
				.registerIcon(this.getUnlocalizedName().substring(this.getUnlocalizedName().indexOf(".") + 1));
	}

	protected String getUnwrappedUnlocalizedName(String unlocalizedName) {
		return unlocalizedName.substring(unlocalizedName.indexOf(".") + 1);
	}

	public abstract void addRecipe();

}
