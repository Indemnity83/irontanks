package com.indemnity83.irontank.item;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;
import net.minecraftforge.oredict.ShapedOreRecipe;

import buildcraft.factory.TileTank;

import com.indemnity83.irontank.block.IronTankType;
import com.indemnity83.irontank.block.TileIronTank;
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
		this.setMaxStackSize(1);
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
	
    @Override
    public boolean onItemUseFirst(ItemStack stack, EntityPlayer player, World world, int X, int Y, int Z, int side, float hitX, float hitY, float hitZ)
    {
        if (world.isRemote) return false;
        TileEntity worldTile = world.getTileEntity(X, Y, Z);
        TileIronTank newIronTankTile;
        if (worldTile != null && worldTile instanceof TileIronTank)
        {
        	TileIronTank ironTankTile = (TileIronTank) worldTile;
        	newIronTankTile = ironTankTile.applyUpgradeItem(this);
            if (newIronTankTile == null)
            {
                return false;
            }
            
        }
        else if (worldTile != null && worldTile instanceof TileTank)
        {
        	TileTank curTankTile = (TileTank) worldTile;
        	if (!getType().canUpgrade(IronTankType.GLASS))
            {
                return false;
            }
    		
        	newIronTankTile = new TileIronTank();
        	newIronTankTile.setCapacity(getType().getTarget().getTankVolume());
        	newIronTankTile.setType(getType().getTarget());
        	newIronTankTile.tank.setFluid(curTankTile.tank.getFluid());

        }
        else
        {
            return false;
        }

        world.setBlock(X, Y, Z, getType().getTarget().getBlock());
        world.setTileEntity(X, Y, Z, newIronTankTile);
        
        stack.stackSize = 0;
        
        return true;
    }

	public TankChangerType getType() {
		return this.type;
	}

}
