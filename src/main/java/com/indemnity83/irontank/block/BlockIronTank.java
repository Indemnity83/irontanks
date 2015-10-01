package com.indemnity83.irontank.block;

import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.entity.Entity;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.IIcon;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.oredict.ShapedOreRecipe;

import com.indemnity83.irontank.creativetab.IronTankTabs;
import com.indemnity83.irontank.init.ModBlocks;
import com.indemnity83.irontank.utility.MaterialHelper;

import cpw.mods.fml.common.registry.GameRegistry;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

import buildcraft.BuildCraftFactory;
import buildcraft.factory.BlockTank;

public class BlockIronTank extends BlockTank {

	private int tankVolume;
	private String blockName;
	private IIcon textureStackedSide;
	private IronTankType type;
	
	public BlockIronTank(IronTankType type) {
		super();
		
		this.type = type;
		this.tankVolume = type.getTankVolume();
		this.blockName = type.getBlockName();
		
		this.setBlockName(this.blockName);
		this.setCreativeTab(IronTankTabs.MainTab);
	}

	public TileEntity createNewTileEntity(World world, int metadata) {
		TileIronTank tile = new TileIronTank(this.type);
		return tile;
	}
	
	@SideOnly(Side.CLIENT)
	public void registerBlockIcons(IIconRegister par1IconRegister) {
		super.registerBlockIcons(par1IconRegister);
		textureStackedSide = par1IconRegister.registerIcon("irontank:" + this.blockName + "/side_stacked");
	}
	
	@SuppressWarnings({"all"})
	public IIcon getIconAbsolute(IBlockAccess iblockaccess, int i, int j, int k, int side, int metadata) {
		if (side >= 2 && iblockaccess.getBlock(i, j - 1, k) == this) {
			return textureStackedSide;
		} else {
			return super.getIconAbsolute(side, metadata);
		}
	}
	
	public String getInternalName()
    {
		return this.blockName;
    }
	
	public void addRecipe() 
	{
		for (String recipe : type.getRecipes())
        {
			String[] recipeSplit = new String[] { recipe.substring(0, 3), recipe.substring(3, 6), recipe.substring(6, 9) };
			for (String material : type.getMatList()) {
				Object targetMaterial = MaterialHelper.translateOreName(material);
				GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(this), recipeSplit, 
						't', targetMaterial, 
						'g', "blockGlass", 
						'0', new ItemStack(BuildCraftFactory.tankBlock, 1),
						'1', new ItemStack(ModBlocks.ironTank, 1),
						'2', new ItemStack(ModBlocks.goldTank, 1),
						'3', new ItemStack(ModBlocks.diamondTank, 1),
						'4', new ItemStack(ModBlocks.copperTank, 1),
						'5', new ItemStack(ModBlocks.silverTank, 1),
						'6', new ItemStack(ModBlocks.obsidianTank, 1)
				));
			}
        }
	}
	
	@Override
    public float getExplosionResistance(Entity par1Entity, World world, int x, int y, int z, double explosionX, double explosionY, double explosionZ)
    {
       TileEntity worldTile = world.getTileEntity(x, y, z);
       if (worldTile instanceof TileIronTank)
       {
    	   TileIronTank tile = (TileIronTank) worldTile;
    	   if (tile.type.isExplosionResistant())
           {
               return 10000f;
           }
       }
       return super.getExplosionResistance(par1Entity, world, x, y, z, explosionX, explosionY, explosionZ);
    }

}
