package com.indemnity83.irontank.block;

import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.entity.Entity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.IIcon;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;

import com.indemnity83.irontank.creativetab.IronTankTabs;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

import buildcraft.factory.BlockTank;

public class BlockIronTank extends BlockTank {

	private int tankVolume;
	private String blockName;
	private IIcon textureStackedSide;
	
	public BlockIronTank(String blockName, int tankVolume) {
		super();
		
		this.tankVolume = tankVolume;
		this.blockName = blockName;
		
		this.setBlockName(this.blockName);
		this.setCreativeTab(IronTankTabs.MainTab);
	}

	public TileEntity createNewTileEntity(World world, int metadata) {
		return new TileIronTank(this.tankVolume);
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

}
