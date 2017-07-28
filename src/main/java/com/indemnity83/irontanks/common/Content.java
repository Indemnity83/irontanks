package com.indemnity83.irontanks.common;

import com.indemnity83.irontanks.common.blocks.BlockIronTank;
import com.indemnity83.irontanks.common.blocks.IronTankType;
import com.indemnity83.irontanks.common.items.ItemIronTank;
import com.indemnity83.irontanks.common.items.TankChangerType;
import net.minecraftforge.fml.common.registry.GameRegistry;

public final class Content {

    public static BlockIronTank ironTankBlock = new BlockIronTank();
    public static ItemIronTank ironTankItemBlock = new ItemIronTank(ironTankBlock);

    public static void registerBlocks() {
        GameRegistry.register(ironTankBlock);
        GameRegistry.register(ironTankItemBlock);

        for (IronTankType type : IronTankType.VALUES) {
            if (type.tileEntity == null) continue;
            GameRegistry.registerTileEntity(type.tileEntity, type.tileEntityId);
        }
    }

    public static void registerItems() {
        TankChangerType.buildItems();
    }
}
