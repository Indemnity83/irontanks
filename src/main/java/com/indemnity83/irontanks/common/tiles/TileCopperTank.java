package com.indemnity83.irontanks.common.tiles;

import com.indemnity83.irontanks.common.blocks.IronTankType;

public class TileCopperTank extends TileTank {
    int getCapacity() {
        return IronTankType.COPPER.capacity;
    }
}
