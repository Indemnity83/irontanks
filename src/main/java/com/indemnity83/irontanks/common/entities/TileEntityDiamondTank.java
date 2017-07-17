package com.indemnity83.irontanks.common.entities;

import com.indemnity83.irontanks.common.blocks.IronTankType;
import net.minecraftforge.fluids.Fluid;

public class TileEntityDiamondTank extends TileEntityIronTank {
    public TileEntityDiamondTank() {
        super();

        int capacity = Fluid.BUCKET_VOLUME * IronTankType.DIAMOND.capacity;
        this.tank.setCapacity(capacity);
    }
}
