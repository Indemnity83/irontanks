package com.indemnity83.irontanks.common.entities;

import com.indemnity83.irontanks.common.blocks.IronTankType;

public class TileEntitySilverTank extends TileEntityTank {
    int getCapacity() {
        return IronTankType.IRON.capacity;
    }
}
