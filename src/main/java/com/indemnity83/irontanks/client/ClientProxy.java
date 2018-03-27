package com.indemnity83.irontanks.client;

import com.indemnity83.irontanks.common.CommonProxy;
import com.indemnity83.irontanks.common.core.Blocks;
import com.indemnity83.irontanks.common.core.Items;
import net.minecraftforge.client.event.ModelRegistryEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.relauncher.Side;

@Mod.EventBusSubscriber(Side.CLIENT)
public class ClientProxy extends CommonProxy {

    @SubscribeEvent
    public static void registerModels(ModelRegistryEvent event) {
        Items.initModels();
        Blocks.initModels();
    }
}
