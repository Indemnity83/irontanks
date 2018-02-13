package com.indemnity83.irontanks.common.tiles;

import buildcraft.api.transport.pipe.PipeApi;
import buildcraft.transport.BCTransportPipes;

public class VoidTankTile extends TankTile {

    // Limit rate of fluid destruction to match void pipe
    private final int transferPerTick = PipeApi.getFluidTransferInfo(BCTransportPipes.voidFluid).transferPerTick;

    @Override
    public void update() {
        super.update();

        // We specifically only want to drain fluid from *this*
        // tank; not the whole tank stack. This lets a user put the
        // void tank at the top of a stack as overflow prevention
        this.tank.drain(transferPerTick, true);
    }
}
