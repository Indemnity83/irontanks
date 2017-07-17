package com.indemnity83.irontanks.common.entities;

import com.indemnity83.irontanks.common.blocks.IronTankType;
import net.minecraftforge.fluids.Fluid;

public class TileEntityGoldTank extends TileEntityIronTank {
    public TileEntityGoldTank() {
        super();

        int capacity = Fluid.BUCKET_VOLUME * IronTankType.GOLD.capacity;
        this.tank.setCapacity(capacity);
    }
}
