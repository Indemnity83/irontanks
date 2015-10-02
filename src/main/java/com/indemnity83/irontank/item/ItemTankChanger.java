package com.indemnity83.irontank.item;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;
import net.minecraftforge.oredict.ShapedOreRecipe;

import buildcraft.factory.TileTank;

import com.indemnity83.irontank.block.IronTankType;
import com.indemnity83.irontank.creativetab.IronTankTabs;
import com.indemnity83.irontank.tile.TileIronTank;
import com.indemnity83.irontank.utility.MaterialHelper;

import cpw.mods.fml.common.registry.GameRegistry;

public class ItemTankChanger extends ItemIronTank {
	
	TankChangerType type;

	public ItemTankChanger(TankChangerType type)
	{
		super();
		
		this.type = type;
		
		this.setUnlocalizedName(type.name);
		this.setCreativeTab(IronTankTabs.MainTab);
		this.setMaxStackSize(1);
	}
	
	public TankChangerType getTankChangerType() 
	{
		return type;
	}

	@Override
	public void addRecipe() {
		for (String sourceMat : type.source.getMatList()) {
			for (String targetMat : type.target.getMatList()) {
				Object oSourceMat = MaterialHelper.translateOreName(sourceMat);
				Object oTargetMat = MaterialHelper.translateOreName(targetMat);
				GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(this), type.recipe, 's', oSourceMat, 't', oTargetMat, 'g', "blockGlass"));
			}
		}
	}

	@Override
	public boolean onItemUseFirst(ItemStack stack, EntityPlayer player, World world, int X, int Y, int Z, int side,
			float hitX, float hitY, float hitZ) {
		if (world.isRemote) {
			return false;
		}

		TileEntity worldTile = world.getTileEntity(X, Y, Z);
		TileTank curTankTile;
		if (worldTile != null && worldTile instanceof TileIronTank) {
			curTankTile = (TileTank) worldTile;
			if (!getType().canUpgrade(((TileIronTank) curTankTile).type)) {
				return false;
			}
		} else if (worldTile != null && worldTile instanceof TileTank) {
			curTankTile = (TileTank) worldTile;
			if (!getType().canUpgrade(IronTankType.GLASS)) {
				return false;
			}
		} else {
			return false;
		}

		TileIronTank newIronTankTile = new TileIronTank(getType().target);
		newIronTankTile.tank.setFluid(curTankTile.tank.getFluid());

		world.setBlock(X, Y, Z, getType().target.getBlock());
		world.setTileEntity(X, Y, Z, newIronTankTile);

		stack.stackSize = 0;

		return true;
	}

	public TankChangerType getType() {
		return this.type;
	}

}
