package com.indemnity83.irontanks.client;

import com.indemnity83.irontanks.client.render.TankTESR;
import com.indemnity83.irontanks.common.CommonProxy;
import com.indemnity83.irontanks.common.core.Blocks;
import com.indemnity83.irontanks.common.core.Items;
import com.indemnity83.irontanks.common.tiles.CreativeTankTile;
import com.indemnity83.irontanks.common.tiles.TankTile;
import com.indemnity83.irontanks.common.tiles.VoidTankTile;
import net.minecraftforge.client.event.ModelRegistryEvent;
import net.minecraftforge.fml.client.registry.ClientRegistry;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.relauncher.Side;

@Mod.EventBusSubscriber(Side.CLIENT)
public class ClientProxy extends CommonProxy {

    @Override
    public void preInit(FMLPreInitializationEvent event) {
        super.preInit(event);

        // The tank fluid is drawn by our own renderer (bound per concrete tile class).
        ClientRegistry.bindTileEntitySpecialRenderer(TankTile.class, new TankTESR());
        ClientRegistry.bindTileEntitySpecialRenderer(CreativeTankTile.class, new TankTESR());
        ClientRegistry.bindTileEntitySpecialRenderer(VoidTankTile.class, new TankTESR());
    }

    @SubscribeEvent
    public static void registerModels(ModelRegistryEvent event) {
        Items.initModels();
        Blocks.initModels();
    }
}
