package com.indemnity83.irontanks.common.entities;

import com.indemnity83.irontanks.common.blocks.IronTankType;
import net.minecraftforge.fluids.Fluid;

public class TileEntityObsidianTank extends TileEntityIronTank {
    public TileEntityObsidianTank() {
        super();

        int capacity = Fluid.BUCKET_VOLUME * IronTankType.OBSIDIAN.capacity;
        this.tank.setCapacity(capacity);
    }
}
