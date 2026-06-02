package com.indemnity83.irontanks.common.tiles;

import buildcraft.api.core.IFluidFilter;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.fluids.FluidStack;

/**
 * Dispenses fluid endlessly: every drain is resolved against the stored fluid but never actually
 * removed (doDrain is forced to false). Because we own the tank-column logic, the Creative tank can
 * refuse to join a vertical stack via {@link #canConnectTo} on every Minecraft version — so a
 * stacked Creative tank stays isolated instead of sharing fluid with its neighbours.
 */
public class CreativeTankTile extends TankTile {

    @Override
    public FluidStack drain(IFluidFilter filter, int maxDrain, boolean doDrain) {
        return super.drain(filter, maxDrain, false);
    }

    @Override
    public boolean canConnectTo(TankTile other, EnumFacing direction) {
        return false;
    }
}
