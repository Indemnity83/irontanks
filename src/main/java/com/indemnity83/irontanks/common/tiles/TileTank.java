package com.indemnity83.irontanks.common.tiles;

import net.minecraftforge.fluids.Fluid;

public abstract class TileTank extends buildcraft.factory.tile.TileTank {

    /**
     * Get the capacity of the tank in buckets.
     *
     * @return int capacity in buckets
     */
    abstract int getCapacity();

    TileTank() {
        super();

        this.tank.setCapacity(getCapacity() * Fluid.BUCKET_VOLUME);
    }

    // This method is actually implemented in TileTank
    // but a quirk of making an abstract class that extends
    // a concrete class, which implements interfaces is I can't
    // just call super.update(). Maybe my Java skills just aren't
    // good enough yet...
    public void update() {
        smoothedTank.tick(getWorld());
    }
}
