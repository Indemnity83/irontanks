package com.indemnity83.irontanks.neoforge.compat.logistics;

import com.indemnity83.irontanks.core.TankTier;
import com.indemnity83.irontanks.neoforge.content.TankBlockEntity;
import com.logistics.core.lib.fluids.IFluidKey;
import com.logistics.core.lib.fluids.SimpleFluidKey;
import com.logistics.core.lib.tank.TankCell;
import net.neoforged.neoforge.transfer.fluid.FluidResource;

/**
 * Adapts an Iron Tanks {@link TankBlockEntity} to the logistics {@link TankCell} contract, so a logistics
 * tank column can include iron tanks and settle the stack as one shared fluid body.
 *
 * <p>Iron Tanks cannot make {@code TankBlockEntity implements TankCell} directly: that interface is
 * absent when logistics is not installed, so the block entity class would fail to load. This adapter
 * (and everything in this package) is only ever class-loaded behind a mod-present check.
 *
 * <p><b>Units.</b> Iron Tanks stores droplets; logistics is millibucket-native on NeoForge. This adapter
 * converts at the boundary ({@link TankTier#DROPLETS_PER_MB}). Capacities are whole millibuckets, but a
 * cell amount that carries a sub-millibucket droplet remainder (only bottle/potion deposits do) loses
 * that remainder when settled through the shared engine — a known NeoForge-only limitation for
 * cross-mod columns; whole-millibucket fluids (water, lava, …) are exact.
 */
final class LogisticsTankCell implements TankCell {

    private final TankBlockEntity tank;

    LogisticsTankCell(TankBlockEntity tank) {
        this.tank = tank;
    }

    static IFluidKey toKey(FluidResource resource) {
        return resource.isEmpty()
                ? SimpleFluidKey.BLANK
                : new SimpleFluidKey(resource.getFluid(), resource.getComponentsPatch());
    }

    static FluidResource toResource(IFluidKey key) {
        return key.isBlank() ? FluidResource.EMPTY : FluidResource.of(key.getFluid(), key.getComponents());
    }

    /** Droplets &rarr; millibuckets (logistics' NeoForge native unit). */
    static long toMillibuckets(long droplets) {
        return droplets / TankTier.DROPLETS_PER_MB;
    }

    @Override
    public IFluidKey fluid() {
        return toKey(tank.fluidResource());
    }

    @Override
    public long amount() {
        return toMillibuckets(tank.amount());
    }

    @Override
    public long capacity() {
        return toMillibuckets(tank.capacity());
    }

    @Override
    public void setContents(IFluidKey fluid, long millibuckets) {
        // The logistics engine writes only the cells it actually changes during a rebalance, so syncing
        // here (rather than batching) touches just those cells.
        tank.setContentsRaw(toResource(fluid), millibuckets * TankTier.DROPLETS_PER_MB);
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
