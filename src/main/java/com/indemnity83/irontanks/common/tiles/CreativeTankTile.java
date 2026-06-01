package com.indemnity83.irontanks.common.tiles;

import buildcraft.api.core.IFluidFilter;
import net.minecraftforge.fluids.FluidStack;

public class CreativeTankTile extends TankTile {
    // The Creative Tank dispenses fluid endlessly: every drain is resolved
    // against the stored fluid but never actually removed (doDrain is forced
    // to false).
    //
    // Note: BuildCraft 7.99.7 (MC 1.11.2) has no canConnectTo hook, so unlike
    // on 1.12.2 the Creative Tank cannot refuse to join a vertical tank stack.
    // Placed standalone it behaves as intended; stacked directly above/below
    // another tank it will share fluid with that stack.
    @Override
    public FluidStack drain(IFluidFilter filter, int maxDrain, boolean doDrain) {
        return super.drain(filter, maxDrain, false);
    }
}
