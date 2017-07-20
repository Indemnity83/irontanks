package com.indemnity83.irontanks.common.blocks;

import buildcraft.api.BCBlocks;
import com.indemnity83.irontanks.common.entities.*;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.IStringSerializable;
import net.minecraftforge.fml.common.registry.GameRegistry;
import net.minecraftforge.oredict.ShapedOreRecipe;

import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;

public enum IronTankType implements IStringSerializable {

    GLASS(0, 16, null, Collections.EMPTY_LIST, Collections.EMPTY_LIST),
    COPPER(1, 27, TileEntityCopperTank.class, Collections.singleton("ingotCopper"), Collections.singleton("GGGMTMGGG")),
    IRON(2, 32, TileEntityIronTank.class, Arrays.asList("ingotIron", "ingotRefinedIron"), Arrays.asList("GMGMTMGMG", "GGGM0MGGG")),
    SILVER(3, 43, TileEntitySilverTank.class, Collections.singleton("ingotSilver"), Arrays.asList("GMGM0MGMG", "GGGM1MGGG")),
    GOLD(4, 48, TileEntityGoldTank.class, Collections.singleton("ingotGold"), Arrays.asList("GMGM1MGMG", "GGGM2MGGG")),
    DIAMOND(5, 64, TileEntityDiamondTank.class, Collections.singleton("gemDiamond"), Arrays.asList("GGGG2GMMM", "GGGM3MGGG")),
    OBSIDIAN(6, 64, TileEntityObsidianTank.class, Collections.singleton("obsidian"), Collections.singleton("MMMM4MMMM"));

    public static final IronTankType VALUES[] = values();
    public final String name;
    public final int metaValue;
    public final int capacity;
    private final Collection<String> materials;
    private final Collection<String> recipes;
    private final Class<? extends TileEntityIronTank> tileEntity;

    IronTankType(int metaValue, int capacity, Class<? extends TileEntityIronTank> tileEntity, Collection<String> materials, Collection<String> recipes) {
        this.metaValue = metaValue;
        this.capacity = capacity;
        this.tileEntity = tileEntity;
        this.name = this.name().toLowerCase();
        this.materials = materials;
        this.recipes = recipes;
    }

    public static void registerRecipes(BlockIronTank blockIronTank) {
        for (IronTankType type : values()) {
            for (String recipe : type.recipes) {
                String[] recipeSplit = new String[]{recipe.substring(0, 3), recipe.substring(3, 6), recipe.substring(6, 9)};

                for (String material : type.materials) {
                    Object mainMaterial = translateOreName(material);

                    GameRegistry.addRecipe(new ShapedOreRecipe(
                            new ItemStack(blockIronTank, 1, type.metaValue), recipeSplit,
                            'M', mainMaterial,
                            'G', "blockGlass",
                            'T', BCBlocks.FACTORY_TANK, // BuildCraft Tank
                            '0', new ItemStack(blockIronTank, 1, COPPER.metaValue),
                            '1', new ItemStack(blockIronTank, 1, IRON.metaValue),
                            '2', new ItemStack(blockIronTank, 1, SILVER.metaValue),
                            '3', new ItemStack(blockIronTank, 1, GOLD.metaValue),
                            '4', new ItemStack(blockIronTank, 1, DIAMOND.metaValue)
                    ));
                }
            }
        }
    }

    public static Object translateOreName(String materialString) {
        if (materialString.equals("obsidian")) {
            return Blocks.OBSIDIAN;
        }

        return materialString;
    }

    @Override
    public String getName() {
        return this.name;
    }

    // TODO: We have the class in the enum data, switch shouldn't be necessary
    public TileEntity makeEntity() {
        switch (this) {
            case IRON:
                return new TileEntityIronTank();
            case GOLD:
                return new TileEntityGoldTank();
            case DIAMOND:
                return new TileEntityDiamondTank();
            case COPPER:
                return new TileEntityCopperTank();
            case SILVER:
                return new TileEntitySilverTank();
            case OBSIDIAN:
                return new TileEntityObsidianTank();
            default:
                return null;
        }
    }

    public boolean isValidForCreativeMode() {
        return this != GLASS;
    }
}
