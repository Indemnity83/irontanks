package com.indemnity83.irontanks;

import com.indemnity83.irontanks.common.CommonProxy;
import com.indemnity83.irontanks.common.Content;
import com.indemnity83.irontanks.common.blocks.IronTankType;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventHandler;
import net.minecraftforge.fml.common.Mod.Instance;
import net.minecraftforge.fml.common.SidedProxy;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;

@Mod(modid = IronTanks.MODID, name = IronTanks.MODNAME)
public class IronTanks {

    public static final String MODID = "irontanks";
    public static final String MODNAME = "Iron Tanks";

    @Instance
    public static IronTanks instance = new IronTanks();

    @SidedProxy(clientSide = "com.indemnity83.irontanks.client.ClientProxy", serverSide = "com.indemnity83.irontanks.common.CommonProxy")
    public static CommonProxy proxy;

    /**
     * The preInit Handler is called at the very beginning of the startup routine. In this method we should read your
     * config file, create Blocks, Items, etc. and register them with the GameRegistry.
     *
     * @param event
     */
    @EventHandler
    public void preInit(FMLPreInitializationEvent event) {
        proxy.preInit(event);

        Content.registerBlocks();
        Content.registerItems();
        Content.registerRenders();
    }

    /**
     * The init Handler is called after the preInit Handler. In this method we can build up data structures, add
     * Crafting Recipes and register new handler.
     *
     * @param event
     */
    @EventHandler
    public void init(FMLInitializationEvent event) {
        proxy.init(event);

        IronTankType.registerRecipes(Content.ironTankBlock);
    }

    /**
     * The postInit Handler is called at the very end. Its used to communicate with other mods and adjust your setup
     * based on this.
     *
     * @param event
     */
    @EventHandler
    public void postInit(FMLPostInitializationEvent event) {
        proxy.postInit(event);
    }
}
