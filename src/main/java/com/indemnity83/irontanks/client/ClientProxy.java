package com.indemnity83.irontanks.client;

import com.indemnity83.irontanks.IronTanks;
import com.indemnity83.irontanks.common.CommonProxy;
import com.indemnity83.irontanks.common.Content;
import com.indemnity83.irontanks.common.blocks.IronTankType;
import com.indemnity83.irontanks.common.items.TankChangerType;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.item.Item;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;

public class ClientProxy extends CommonProxy {
    @Override
    public void preInit(FMLPreInitializationEvent event) {
        super.preInit(event);

        Item tankItem = Item.getItemFromBlock(Content.ironTankBlock);

        for (IronTankType type : IronTankType.VALUES) {
            ModelLoader.setCustomModelResourceLocation(tankItem, type.metaValue, new ModelResourceLocation(Content.ironTankItemBlock.getRegistryName(), "variant=" + type.name));
        }

        for (TankChangerType type : TankChangerType.VALUES) {
            ModelLoader.setCustomModelResourceLocation(type.item, 0, new ModelResourceLocation(new ResourceLocation(IronTanks.MODID, "iron_tank_upgrades"), "variant=" + type.itemName));
        }
    }
}
