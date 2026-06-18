package com.indemnity83.irontanks.fabric.compat.logistics;

import com.indemnity83.irontanks.core.TankTier;
import com.indemnity83.irontanks.fabric.content.TankBlockEntity;
import com.logistics.core.lib.fluids.IFluidKey;
import com.logistics.core.lib.fluids.SimpleFluidKey;
import com.logistics.core.lib.tank.TankCell;
import net.fabricmc.fabric.api.transfer.v1.fluid.FluidVariant;

/**
 * Adapts an Iron Tanks {@link TankBlockEntity} to the logistics {@link TankCell} contract, so a logistics
 * tank column can include iron tanks and settle the stack as one shared fluid body.
 *
 * <p>Iron Tanks cannot make {@code TankBlockEntity implements TankCell} directly: that interface is
 * absent when logistics is not installed, so the block entity class would fail to load. This adapter
 * (and everything in this package) is only ever class-loaded behind a mod-present check.
 *
 * <p>Fabric is droplet-native, which matches the logistics Fabric unit, so amounts pass through with no
 * conversion. (The NeoForge adapter must convert droplets&harr;mB.)
 */
final class LogisticsTankCell implements TankCell {

    private final TankBlockEntity tank;

    LogisticsTankCell(TankBlockEntity tank) {
        this.tank = tank;
    }

    static IFluidKey toKey(FluidVariant variant) {
        return variant.isBlank()
                ? SimpleFluidKey.BLANK
                : new SimpleFluidKey(variant.getFluid(), variant.getComponentsPatch());
    }

    static FluidVariant toVariant(IFluidKey key) {
        return key.isBlank() ? FluidVariant.blank() : FluidVariant.of(key.getFluid(), key.getComponents());
    }

    @Override
    public IFluidKey fluid() {
        return toKey(tank.fluidVariant());
    }

    @Override
    public long amount() {
        return tank.amount();
    }

    @Override
    public long capacity() {
        return tank.capacity();
    }

    @Override
    public void setContents(IFluidKey fluid, long amount) {
        // The logistics engine writes only the cells it actually changes during a rebalance, so syncing
        // here (rather than batching) touches just those cells.
        tank.setContentsRaw(toVariant(fluid), amount);
        tank.sync();
    }

    @Override
    public boolean joinsColumn() {
        // Creative and void tanks stay isolated single-cell columns — creative would feed an endless
        // source into a shared body, and void would silently destroy a neighbour's fluid.
        TankTier tier = tank.tier();
        return tier != TankTier.CREATIVE && tier != TankTier.VOID;
    }
}
