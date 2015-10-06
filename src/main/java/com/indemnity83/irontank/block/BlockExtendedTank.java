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
import com.indemnity83.irontank.reference.Reference;
import com.indemnity83.irontank.reference.TankType;
import com.indemnity83.irontank.tile.TileIronTank;
import com.indemnity83.irontank.utility.MaterialHelper;

import cpw.mods.fml.common.registry.GameRegistry;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

import buildcraft.BuildCraftFactory;
import buildcraft.factory.BlockTank;

public class BlockExtendedTank extends BlockTank {

	private IIcon textureStackedSide;

	/**
	 * TankType of this block
	 */
	public final TankType type;

	public BlockExtendedTank(TankType type) {
		super();

		this.type = type;

		this.setBlockName(type.name);
		this.setCreativeTab(IronTankTabs.MainTab);
		this.setResistance(type.resistance);
	}

	public TileEntity createNewTileEntity(World world, int metadata) {
		TileIronTank tile = new TileIronTank(this.type);
		return tile;
	}

	@SideOnly(Side.CLIENT)
	public void registerBlockIcons(IIconRegister par1IconRegister) {
		super.registerBlockIcons(par1IconRegister);
		textureStackedSide = par1IconRegister.registerIcon("irontank:" + type.name + "/side_stacked");
	}
	
	@SuppressWarnings({"all"})
	@Override
	public IIcon getIconAbsolute(IBlockAccess iblockaccess, int i, int j, int k, int side, int metadata) {
		if (side >= 2 && iblockaccess.getBlock(i, j - 1, k) instanceof BlockTank) {
			return textureStackedSide;
		} else {
			return super.getIconAbsolute(side, metadata);
		}
	}

	public void addRecipe() {
		for (String recipe : type.recipes) {
			String[] recipeSplit = new String[] { recipe.substring(0, 3), recipe.substring(3, 6),
					recipe.substring(6, 9) };
			for (String material : type.materials) {
				Object targetMaterial = MaterialHelper.translateOreName(material);
				GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(this), recipeSplit, 't', targetMaterial, 'g',
						"blockGlass", '0', new ItemStack(BuildCraftFactory.tankBlock, 1), '1',
						new ItemStack(ModBlocks.ironTank, 1), '2', new ItemStack(ModBlocks.goldTank, 1), '3',
						new ItemStack(ModBlocks.diamondTank, 1), '4', new ItemStack(ModBlocks.copperTank, 1), '5',
						new ItemStack(ModBlocks.silverTank, 1), '6', new ItemStack(ModBlocks.obsidianTank, 1)));
			}
		}
	}

	@Override
	public String getUnlocalizedName() {
		return String.format("tile.%s%s", Reference.MODID.toLowerCase() + ":",
				getUnwrappedUnlocalizedName(super.getUnlocalizedName()));
	}

	protected String getUnwrappedUnlocalizedName(String unlocalizedName) {
		return unlocalizedName.substring(unlocalizedName.indexOf(".") + 1);
	}

}
