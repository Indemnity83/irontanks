package com.indemnity83.irontanks.common.tiles;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.fluids.Fluid;

public class TankTile extends buildcraft.factory.tile.TileTank {
    public TankTile withCapacity(int capacity) {
        this.tank.setCapacity(capacity * Fluid.BUCKET_VOLUME);
        return this;
    }

    @Override
    public void readFromNBT(NBTTagCompound compound) {
        super.readFromNBT(compound);

        if (compound.hasKey("Capacity")) {
            tank.setCapacity(compound.getInteger("Capacity"));
        }
    }

    @Override
    public NBTTagCompound writeToNBT(NBTTagCompound compound) {
        super.writeToNBT(compound);
        compound.setInteger("Capacity", tank.getCapacity());

        return compound;
    }
}
