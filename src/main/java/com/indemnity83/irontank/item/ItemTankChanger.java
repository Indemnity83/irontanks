package com.indemnity83.irontank.item;

import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraftforge.oredict.ShapedOreRecipe;

import com.indemnity83.irontank.creativetab.IronTankTabs;
import com.indemnity83.irontank.utility.MaterialHelper;

import cpw.mods.fml.common.registry.GameRegistry;

public class ItemTankChanger extends ItemIronTank {
	
	TankChangerType type;

	public ItemTankChanger(TankChangerType type)
	{
		super();
		
		this.type = type;
		
		this.setUnlocalizedName(type.getItemName());
		this.setCreativeTab(IronTankTabs.MainTab);
	}
	
	public TankChangerType getTankChangerType() 
	{
		return type;
	}

	@Override
	public void addRecipe() {
		for (String sourceMat : type.getSourceMats()) {
			for (String targetMat : type.getTargetMats()) {
				Object oSourceMat = MaterialHelper.translateOreName(sourceMat);
				Object oTargetMat = MaterialHelper.translateOreName(targetMat);
				GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(this), type.getRecipe(), 's', oSourceMat, 't', oTargetMat, 'g', "blockGlass"));
			}
		}
	}

}
