package com.indemnity83.irontank.block;

import com.indemnity83.irontank.utility.LogHelper;

import net.minecraftforge.fluids.FluidContainerRegistry;
import net.minecraftforge.fluids.IFluidHandler;
import buildcraft.core.lib.fluids.Tank;
import buildcraft.factory.TileTank;

public class TileIronTank extends TileTank {
	
	public void setCapacity(int tankVolume) {
		this.tank.setCapacity(FluidContainerRegistry.BUCKET_VOLUME * tankVolume);
	}
	
	public int getCapacity() {
		return this.tank.getCapacity();
	}

}
