package com.indemnity83.irontanks.common;

import buildcraft.factory.BCFactoryBlocks;
import com.indemnity83.irontanks.IronTanks;
import com.indemnity83.irontanks.common.blocks.StackableTankBlock;
import com.indemnity83.irontanks.common.blocks.VoidTankBlock;
import com.indemnity83.irontanks.common.core.Blocks;
import com.indemnity83.irontanks.common.core.Items;
import com.indemnity83.irontanks.common.items.UpgradeItem;
import com.indemnity83.irontanks.common.tiles.TankTile;
import com.indemnity83.irontanks.common.tiles.VoidTankTile;
import net.minecraft.block.Block;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.registry.GameRegistry;

@Mod.EventBusSubscriber
public class CommonProxy {
    @SubscribeEvent
    public static void registerBlocks(RegistryEvent.Register<Block> event) {
        event.getRegistry().register(new StackableTankBlock("copper_tank", 27));
        event.getRegistry().register(new StackableTankBlock("iron_tank", 32));
        event.getRegistry().register(new StackableTankBlock("silver_tank", 43));
        event.getRegistry().register(new StackableTankBlock("gold_tank", 48));
        event.getRegistry().register(new StackableTankBlock("diamond_tank", 64));
        event.getRegistry().register(new StackableTankBlock("obsidian_tank", 64));
        event.getRegistry().register(new VoidTankBlock("void_tank", 8));

        GameRegistry.registerTileEntity(TankTile.class, IronTanks.MODID + ":tank");
        GameRegistry.registerTileEntity(VoidTankTile.class, IronTanks.MODID + ":void_tank");
    }

    @SubscribeEvent
    public static void registerItems(RegistryEvent.Register<Item> event) {
        event.getRegistry().register(new ItemBlock(Blocks.ironTank).setRegistryName(Blocks.ironTank.getRegistryName()));
        event.getRegistry().register(new ItemBlock(Blocks.goldTank).setRegistryName(Blocks.goldTank.getRegistryName()));
        event.getRegistry().register(new ItemBlock(Blocks.diamondTank).setRegistryName(Blocks.diamondTank.getRegistryName()));
        event.getRegistry().register(new ItemBlock(Blocks.obsidianTank).setRegistryName(Blocks.obsidianTank.getRegistryName()));
        event.getRegistry().register(new ItemBlock(Blocks.silverTank).setRegistryName(Blocks.silverTank.getRegistryName()));
        event.getRegistry().register(new ItemBlock(Blocks.copperTank).setRegistryName(Blocks.copperTank.getRegistryName()));
        event.getRegistry().register(new ItemBlock(Blocks.voidTank).setRegistryName(Blocks.voidTank.getRegistryName()));

        event.getRegistry().register(new UpgradeItem("copper_iron_upgrade", Blocks.copperTank, Blocks.ironTank));
        event.getRegistry().register(new UpgradeItem("copper_silver_upgrade", Blocks.copperTank, Blocks.silverTank));
        event.getRegistry().register(new UpgradeItem("diamond_obsidian_upgrade", Blocks.diamondTank, Blocks.obsidianTank));
        event.getRegistry().register(new UpgradeItem("glass_copper_upgrade", BCFactoryBlocks.tank, Blocks.copperTank));
        event.getRegistry().register(new UpgradeItem("glass_iron_upgrade", BCFactoryBlocks.tank, Blocks.ironTank));
        event.getRegistry().register(new UpgradeItem("gold_diamond_upgrade", Blocks.goldTank, Blocks.diamondTank));
        event.getRegistry().register(new UpgradeItem("iron_gold_upgrade", Blocks.ironTank, Blocks.goldTank));
        event.getRegistry().register(new UpgradeItem("silver_gold_upgrade", Blocks.silverTank, Blocks.goldTank));
    }

    /**
     * The preInit Handler is called at the very beginning of the startup routine. In this method we should read your
     * config file, create Blocks, Items, etc. and register them with the GameRegistry.
     *
     * @param event
     */
    public void preInit(FMLPreInitializationEvent event) {
    }

    /**
     * The init Handler is called after the preInit Handler. In this method we can build up data structures, add
     * Crafting Recipes and register new handler.
     *
     * @param event
     */
    public void init(FMLInitializationEvent event) {
        Blocks.init();
        Items.init();
    }

    /**
     * The postInit Handler is called at the very end. Its used to communicate with other mods and adjust your setup
     * based on this.
     *
     * @param event
     */
    public void postInit(FMLPostInitializationEvent event) {
    }
}
