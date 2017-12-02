package com.indemnity83.irontanks.common.core;

import com.indemnity83.irontanks.common.blocks.BlockIronTank;

public class IronTankBlocks {
    public static BlockIronTank ironTankBlock;

    public static void init() {
        ironTankBlock = new BlockIronTank();
    }
}
