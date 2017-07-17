package com.indemnity83.irontanks.common.entities;

import buildcraft.factory.tile.TileTank;
import com.indemnity83.irontanks.common.blocks.IronTankType;
import net.minecraftforge.fluids.Fluid;

public class TileEntityIronTank extends TileTank {

    public TileEntityIronTank() {
        super();

        int capacity = Fluid.BUCKET_VOLUME * IronTankType.IRON.capacity;
        this.tank.setCapacity(capacity);
    }
}
