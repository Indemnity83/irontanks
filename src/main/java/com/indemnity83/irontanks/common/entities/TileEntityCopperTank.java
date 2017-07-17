package com.indemnity83.irontanks.common.entities;

import com.indemnity83.irontanks.common.blocks.IronTankType;
import net.minecraftforge.fluids.Fluid;

public class TileEntityCopperTank extends TileEntityIronTank {

    public TileEntityCopperTank() {
        super();

        int capacity = Fluid.BUCKET_VOLUME * IronTankType.COPPER.capacity;
        this.tank.setCapacity(capacity);
    }
}
