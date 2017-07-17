package com.indemnity83.irontanks.common;

import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;

public class CommonProxy {
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
