package com.indemnity83.irontanks.common.tiles;

import net.minecraftforge.fluids.Fluid;

public class TankTile extends buildcraft.factory.tile.TileTank {
    public TankTile(int capacity) {
        super();

        this.tank.setCapacity(capacity * Fluid.BUCKET_VOLUME);
    }
}
