package com.indemnity83.irontanks.neoforge.content;

import com.indemnity83.irontanks.core.FluidColumn;
import com.indemnity83.irontanks.core.TankTier;
import net.neoforged.neoforge.transfer.ResourceHandler;
import net.neoforged.neoforge.transfer.fluid.FluidResource;
import net.neoforged.neoforge.transfer.transaction.SnapshotJournal;
import net.neoforged.neoforge.transfer.transaction.TransactionContext;

/**
 * Exposes one tank to NeoForge's transfer API as a single-slot {@link ResourceHandler}. Operations
 * act on this tank's own contents; the column rebalances afterward (on commit and on tick), so fluid
 * pumped into any tank in a stack settles across the whole column. Capacity/clamping math is in
 * {@code core}. A creative tank dispenses endlessly: it accepts a fluid to define its source, stays
 * full, and never depletes on extract.
 */
public final class TankFluidHandler extends SnapshotJournal<TankFluidHandler.Snapshot>
        implements ResourceHandler<FluidResource> {

    /** Snapshot of this tank's contents, restored if a transaction is aborted. */
    public record Snapshot(FluidResource fluid, long amount) {
    }

    private final TankBlockEntity tank;

    public TankFluidHandler(TankBlockEntity tank) {
        this.tank = tank;
    }

    private boolean creative() {
        return tank.tier() == TankTier.CREATIVE;
    }

    @Override
    public int size() {
        return 1;
    }

    @Override
    public FluidResource getResource(int index) {
        checkIndex(index);
        return tank.fluidResource();
    }

    @Override
    public long getAmountAsLong(int index) {
        checkIndex(index);
        return tank.amount();
    }

    @Override
    public long getCapacityAsLong(int index, FluidResource resource) {
        checkIndex(index);
        return tank.capacity();
    }

    @Override
    public boolean isValid(int index, FluidResource resource) {
        checkIndex(index);
        FluidResource current = tank.fluidResource();
        return current.isEmpty() || current.equals(resource);
    }

    @Override
    public int insert(int index, FluidResource resource, int amount, TransactionContext transaction) {
        checkIndex(index);
        if (resource.isEmpty() || amount <= 0) {
            return 0;
        }
        FluidResource current = tank.fluidResource();
        if (!current.isEmpty() && !current.equals(resource)) {
            return 0;
        }
        if (creative()) {
            updateSnapshots(transaction);
            tank.setContentsRaw(resource, tank.capacity());
            return amount;
        }
        long room = FluidColumn.fillable(tank.capacity(), tank.amount(), amount);
        if (room <= 0) {
            return 0;
        }
        updateSnapshots(transaction);
        tank.setContentsRaw(resource, tank.amount() + room);
        return (int) room;
    }

    @Override
    public int extract(int index, FluidResource resource, int amount, TransactionContext transaction) {
        checkIndex(index);
        if (resource.isEmpty() || amount <= 0) {
            return 0;
        }
        FluidResource current = tank.fluidResource();
        if (current.isEmpty() || !current.equals(resource)) {
            return 0;
        }
        if (creative()) {
            return amount; // endless supply: never reduces, so no snapshot needed
        }
        long taken = FluidColumn.drainable(tank.amount(), amount);
        if (taken <= 0) {
            return 0;
        }
        updateSnapshots(transaction);
        tank.setContentsRaw(resource, tank.amount() - taken);
        return (int) taken;
    }

    @Override
    protected Snapshot createSnapshot() {
        return new Snapshot(tank.fluidResource(), tank.amount());
    }

    @Override
    protected void revertToSnapshot(Snapshot snapshot) {
        tank.setContentsRaw(snapshot.fluid(), snapshot.amount());
    }

    @Override
    protected void onRootCommit(Snapshot originalState) {
        tank.onContentsChanged();
    }

    private static void checkIndex(int index) {
        if (index != 0) {
            throw new IndexOutOfBoundsException(index);
        }
    }
}
