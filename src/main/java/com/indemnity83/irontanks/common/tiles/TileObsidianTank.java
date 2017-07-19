package com.indemnity83.irontanks.common.tiles;

import com.indemnity83.irontanks.common.blocks.IronTankType;

public class TileObsidianTank extends TileTank {
    int getCapacity() {
        return IronTankType.OBSIDIAN.capacity;
    }
}
