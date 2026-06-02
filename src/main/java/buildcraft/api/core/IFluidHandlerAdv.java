/** Copyright (c) 2011-2015, SpaceToad and the BuildCraft Team http://www.mod-buildcraft.com
 * <p/>
 * BuildCraft is distributed under the terms of the Minecraft Mod Public License 1.0, or MMPL. Please check the contents
 * of the license located in http://www.mod-buildcraft.com/MMPL-1.0.txt */
package buildcraft.api.core;

import javax.annotation.Nullable;

import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.capability.IFluidHandler;

/** A version of {@link IFluidHandler} that can drain a fluid that a fluid filter accepts. */
public interface IFluidHandlerAdv extends IFluidHandler {
    /** Drains fluid out of internal tanks, distribution is left entirely to the IFluidHandler.
     *
     * @param filter A filter to filter the possible fluids that can be extracted.
     * @param maxDrain The maximum amount of fluid to drain
     * @param doDrain If false, drain will only be simulated.
     * @return FluidStack representing the Fluid and amount that was (or would have been, if simulated) drained. */
    @Nullable
    FluidStack drain(IFluidFilter filter, int maxDrain, boolean doDrain);
}
