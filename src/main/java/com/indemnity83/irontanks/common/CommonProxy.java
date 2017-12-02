package com.indemnity83.irontanks.common;

import buildcraft.api.BCBlocks;
import com.indemnity83.irontanks.common.blocks.IronTankType;
import com.indemnity83.irontanks.common.core.IronTankBlocks;
import com.indemnity83.irontanks.common.core.IronTankItems;
import com.indemnity83.irontanks.common.items.ItemTankChanger;
import com.indemnity83.irontanks.common.items.TankChangerType;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.registry.GameRegistry;
import net.minecraftforge.oredict.ShapedOreRecipe;

import static com.indemnity83.irontanks.common.blocks.IronTankType.translateOreName;
import static com.indemnity83.irontanks.common.core.IronTankBlocks.ironTankBlock;
import static com.indemnity83.irontanks.common.core.IronTankItems.ironTankItemBlock;

@Mod.EventBusSubscriber
public class CommonProxy {
    public static void registerBlocks() {
        GameRegistry.register(ironTankBlock);

        for (IronTankType type : IronTankType.VALUES) {
            if (type.tileEntity == null) continue;
            GameRegistry.registerTileEntity(type.tileEntity, type.tileEntityId);
        }
    }

    public static void registerItems() {
        GameRegistry.register(ironTankItemBlock);

        for (TankChangerType type : TankChangerType.VALUES) {
            type.item = new ItemTankChanger(type);
            GameRegistry.register(type.item);
        }
    }

    /**
     * The preInit Handler is called at the very beginning of the startup routine. In this method we should read your
     * config file, create Blocks, Items, etc. and register them with the GameRegistry.
     *
     * @param event
     */
    public void preInit(FMLPreInitializationEvent event) {
        IronTankBlocks.init();
        IronTankItems.init();
        this.registerBlocks();
        this.registerItems();
    }

    /**
     * The init Handler is called after the preInit Handler. In this method we can build up data structures, add
     * Crafting Recipes and register new handler.
     *
     * @param event
     */
    public void init(FMLInitializationEvent event) {
        this.addRecipes();
    }

    /**
     * The postInit Handler is called at the very end. Its used to communicate with other mods and adjust your setup
     * based on this.
     *
     * @param event
     */
    public void postInit(FMLPostInitializationEvent event) {
    }

    private void addRecipes() {
        // Items
        for (TankChangerType type : TankChangerType.VALUES) {
            for (String upgradeFromMaterial : type.upgradeFrom.materials) {
                Object sourceMaterial = translateOreName(upgradeFromMaterial);

                for (String upgradeToMaterial : type.upgradeTo.materials) {
                    Object targetMaterial = translateOreName(upgradeToMaterial);

                    String[] recipeSplit = new String[]{type.recipe.substring(0, 3), type.recipe.substring(3, 6), type.recipe.substring(6, 9)};
                    GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(type.item), recipeSplit, 's', sourceMaterial, 't', targetMaterial, 'g', "blockGlass"));
                }
            }
        }

        // Blocks
        for (IronTankType type : IronTankType.values()) {
            for (String recipe : type.recipes) {
                String[] recipeSplit = new String[]{recipe.substring(0, 3), recipe.substring(3, 6), recipe.substring(6, 9)};

                for (String material : type.materials) {
                    Object mainMaterial = translateOreName(material);

                    GameRegistry.addRecipe(new ShapedOreRecipe(
                            new ItemStack(ironTankBlock, 1, type.metaValue), recipeSplit,
                            'M', mainMaterial,
                            'G', "blockGlass",
                            'T', BCBlocks.FACTORY_TANK, // BuildCraft Tank
                            '0', new ItemStack(ironTankBlock, 1, IronTankType.COPPER.metaValue),
                            '1', new ItemStack(ironTankBlock, 1, IronTankType.IRON.metaValue),
                            '2', new ItemStack(ironTankBlock, 1, IronTankType.SILVER.metaValue),
                            '3', new ItemStack(ironTankBlock, 1, IronTankType.GOLD.metaValue),
                            '4', new ItemStack(ironTankBlock, 1, IronTankType.DIAMOND.metaValue)
                    ));
                }
            }
        }
    }
}
