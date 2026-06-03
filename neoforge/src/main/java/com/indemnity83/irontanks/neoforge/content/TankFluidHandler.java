package com.indemnity83.irontanks.neoforge.content;

import com.indemnity83.irontanks.core.FluidColumn;
import com.indemnity83.irontanks.core.TankTier;
import java.util.ArrayList;
import java.util.List;
import net.neoforged.neoforge.transfer.ResourceHandler;
import net.neoforged.neoforge.transfer.fluid.FluidResource;
import net.neoforged.neoforge.transfer.transaction.SnapshotJournal;
import net.neoforged.neoforge.transfer.transaction.TransactionContext;

/**
 * Exposes a whole vertical tank column to NeoForge's transfer API as one single-slot {@link
 * ResourceHandler}. Inserting or extracting through any tank in a stack affects the column's combined
 * contents and re-settles them (liquids to the bottom, gases to the top) via {@code core}. This is why
 * filling the bottom of a full-bottom/empty-top stack still works — the room is in the column, not the
 * clicked tank. A creative tank never joins a column, so it acts as a single endless source/sink.
 */
public final class TankFluidHandler extends SnapshotJournal<TankFluidHandler.Snapshot>
        implements ResourceHandler<FluidResource> {

    /** A tank and its contents at transaction start, restored on abort. */
    public record Slot(TankBlockEntity tank, FluidResource fluid, long amount) {}

    public record Snapshot(List<Slot> slots) {}

    private final TankBlockEntity origin;

    public TankFluidHandler(TankBlockEntity origin) {
        this.origin = origin;
    }

    private boolean creative() {
        return origin.tier() == TankTier.CREATIVE;
    }

    private List<TankBlockEntity> column() {
        return origin.columnTanks();
    }

    @Override
    public int size() {
        return 1;
    }

    @Override
    public FluidResource getResource(int index) {
        checkIndex(index);
        return shared(column());
    }

    @Override
    public long getAmountAsLong(int index) {
        checkIndex(index);
        long total = 0;
        for (TankBlockEntity tank : column()) {
            total += tank.amount();
        }
        return total;
    }

    @Override
    public long getCapacityAsLong(int index, FluidResource resource) {
        checkIndex(index);
        long capacity = 0;
        for (TankBlockEntity tank : column()) {
            capacity += tank.capacity();
        }
        return capacity;
    }

    @Override
    public boolean isValid(int index, FluidResource resource) {
        checkIndex(index);
        FluidResource current = shared(column());
        return current.isEmpty() || current.equals(resource);
    }

    @Override
    public int insert(int index, FluidResource resource, int amount, TransactionContext transaction) {
        checkIndex(index);
        if (resource.isEmpty() || amount <= 0) {
            return 0;
        }
        List<TankBlockEntity> column = column();
        FluidResource current = shared(column);
        if (!current.isEmpty() && !current.equals(resource)) {
            return 0;
        }
        if (creative()) {
            updateSnapshots(transaction);
            origin.setContentsRaw(resource, origin.capacity()); // define the source, stay full
            return amount;
        }
        long capacity = 0;
        long total = 0;
        for (TankBlockEntity tank : column) {
            capacity += tank.capacity();
            total += tank.amount();
        }
        long room = FluidColumn.fillable(capacity, total, amount);
        if (room <= 0) {
            return 0;
        }
        updateSnapshots(transaction);
        distribute(column, resource, total + room);
        return clampToInt(room);
    }

    @Override
    public int extract(int index, FluidResource resource, int amount, TransactionContext transaction) {
        checkIndex(index);
        if (resource.isEmpty() || amount <= 0) {
            return 0;
        }
        List<TankBlockEntity> column = column();
        FluidResource current = shared(column);
        if (current.isEmpty() || !current.equals(resource)) {
            return 0;
        }
        if (creative()) {
            return amount; // endless supply: never depletes
        }
        long total = 0;
        for (TankBlockEntity tank : column) {
            total += tank.amount();
        }
        long taken = FluidColumn.drainable(total, amount);
        if (taken <= 0) {
            return 0;
        }
        updateSnapshots(transaction);
        distribute(column, current, total - taken);
        return clampToInt(taken);
    }

    /**
     * Narrows a non-negative mB {@code amount} to {@code int}, clamping at {@link Integer#MAX_VALUE} so a
     * tall column of high-tier tanks can't wrap a {@code long} total to a negative or truncated value.
     */
    private static int clampToInt(long amount) {
        return (int) Math.min(amount, Integer.MAX_VALUE);
    }

    /** Settles {@code total} mB across the column (via {@code core}) and writes each tank's share. */
    private static void distribute(List<TankBlockEntity> column, FluidResource fluid, long total) {
        long[] capacities = column.stream().mapToLong(TankBlockEntity::capacity).toArray();
        boolean gas = fluid.value().getFluidType().isLighterThanAir();
        long[] settled = FluidColumn.settle(capacities, total, gas);
        for (int i = 0; i < column.size(); i++) {
            long amount = settled[i];
            column.get(i).setContentsRaw(amount == 0 ? FluidResource.EMPTY : fluid, amount);
        }
    }

    /** The single fluid the column holds, or empty if the column is empty. */
    private static FluidResource shared(List<TankBlockEntity> column) {
        for (TankBlockEntity tank : column) {
            if (!tank.fluidResource().isEmpty()) {
                return tank.fluidResource();
            }
        }
        return FluidResource.EMPTY;
    }

    @Override
    protected Snapshot createSnapshot() {
        List<Slot> slots = new ArrayList<>();
        for (TankBlockEntity tank : column()) {
            slots.add(new Slot(tank, tank.fluidResource(), tank.amount()));
        }
        return new Snapshot(slots);
    }

    @Override
    protected void revertToSnapshot(Snapshot snapshot) {
        for (Slot slot : snapshot.slots()) {
            slot.tank().setContentsRaw(slot.fluid(), slot.amount());
        }
    }

    @Override
    protected void onRootCommit(Snapshot originalState) {
        // Sync every tank touched this transaction (contents are already settled by distribute()).
        for (Slot slot : originalState.slots()) {
            slot.tank().sync();
        }
    }

    private static void checkIndex(int index) {
        if (index != 0) {
            throw new IndexOutOfBoundsException(index);
        }
    }
}
