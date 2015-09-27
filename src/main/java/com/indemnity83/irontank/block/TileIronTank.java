package com.indemnity83.irontank.block;

import com.indemnity83.irontank.utility.LogHelper;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.fluids.FluidContainerRegistry;
import net.minecraftforge.fluids.IFluidHandler;
import buildcraft.core.lib.fluids.Tank;
import buildcraft.factory.TileTank;

public class TileIronTank extends TileTank {
	
	private IronTankType type;
	
	public void setCapacity(int tankVolume) {
		this.tank.setCapacity(FluidContainerRegistry.BUCKET_VOLUME * tankVolume);
	}
	
	public int getCapacity() {
		return this.tank.getCapacity();
	}

	public void setType(IronTankType type) {
		this.type = type;
	}
	
	public IronTankType getType() {
		return this.type;
	}
	
	public void readFromNBT(NBTTagCompound data) {
		super.readFromNBT(data);
		this.type = IronTankType.values()[data.getInteger("type")];
	}

	public void writeToNBT(NBTTagCompound data) {
		super.writeToNBT(data);
		data.setInteger("type", type.ordinal());
	}

}
