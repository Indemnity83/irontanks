package com.indemnity83.irontanks.common.entities;

import com.indemnity83.irontanks.common.blocks.IronTankType;

public class TileEntityGoldTank extends TileEntityTank {
    int getCapacity() {
        return IronTankType.GOLD.capacity;
    }
}