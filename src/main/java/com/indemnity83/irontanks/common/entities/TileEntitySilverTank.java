package com.indemnity83.irontanks.common.entities;

import com.indemnity83.irontanks.common.blocks.IronTankType;
import net.minecraftforge.fluids.Fluid;

public class TileEntitySilverTank extends TileEntityIronTank {
    public TileEntitySilverTank() {
        super();

        int capacity = Fluid.BUCKET_VOLUME * IronTankType.SILVER.capacity;
        this.tank.setCapacity(capacity);
    }
}
