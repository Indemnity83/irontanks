package com.indemnity83.irontanks.common.entities;

import buildcraft.factory.tile.TileTank;

public abstract class TileEntityTank extends TileTank {

    abstract int getCapacity();

    TileEntityTank() {
        super();

        this.tank.setCapacity(getCapacity());
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
