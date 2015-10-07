package com.indemnity83.irontank.tile;

import com.indemnity83.irontank.item.ItemTankChanger;
import com.indemnity83.irontank.reference.TankType;
import com.indemnity83.irontank.utility.LogHelper;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.fluids.FluidContainerRegistry;
import net.minecraftforge.fluids.IFluidHandler;
import buildcraft.core.lib.fluids.Tank;
import buildcraft.factory.TileTank;

public class TileIronTank extends TileTank {

	public TankType type;

	public TileIronTank() {
		this(TankType.IRON);
	}

	public TileIronTank(TankType type) {
		this.type = type;

		int capacity = FluidContainerRegistry.BUCKET_VOLUME * type.capacity;
		this.tank.setCapacity(capacity);
	}

	public void readFromNBT(NBTTagCompound data) {
		super.readFromNBT(data);
		this.type = TankType.values()[data.getInteger("type")];
	}

	public void writeToNBT(NBTTagCompound data) {
		super.writeToNBT(data);
		data.setInteger("type", type.ordinal());
	}

}
