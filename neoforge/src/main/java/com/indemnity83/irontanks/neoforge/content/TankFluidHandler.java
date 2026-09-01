package com.indemnity83.irontanks.neoforge.content;

import com.indemnity83.irontanks.core.TankColumn;
import com.indemnity83.irontanks.core.TankTier;
import java.util.ArrayList;
import java.util.List;
import net.neoforged.neoforge.transfer.ResourceHandler;
import net.neoforged.neoforge.transfer.fluid.FluidResource;
import net.neoforged.neoforge.transfer.transaction.SnapshotJournal;
import net.neoforged.neoforge.transfer.transaction.TransactionContext;

/**
 * Exposes a whole vertical tank column to NeoForge's transfer API as one single-slot {@link
 * ResourceHandler}. The fluid algorithm — mixed-fluid refusal, potion sealing, creative source/sink,
 * settling — lives in {@code core}'s {@link TankColumn}; this class is just the NeoForge surface plus a
 * transaction snapshot. The transfer API speaks millibuckets while {@code core} works in droplets, so
 * insert/extract pass {@link TankTier#DROPLETS_PER_MB} as the column's quantum (flooring to whole mB)
 * and convert the droplet result back; bottles stay droplet-exact.
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

    private TankColumn<FluidResource> column() {
        return origin.asColumn();
    }

    @Override
    public int size() {
        return 1;
    }

    @Override
    public FluidResource getResource(int index) {
        checkIndex(index);
        // Empty for a column no transfer can move (a stored potion is bottle-only; a mixed column is
        // refused outright), so pipes/buckets never see contents they can't take.
        return column().reportedFluid();
    }

    @Override
    public long getAmountAsLong(int index) {
        checkIndex(index);
        // The column stores droplets; the NeoForge transfer API speaks millibuckets.
        return column().reportedTotal() / TankTier.DROPLETS_PER_MB;
    }

    @Override
    public long getCapacityAsLong(int index, FluidResource resource) {
        checkIndex(index);
        // Per the ResourceHandler contract, 0 for any resource isValid rejects — otherwise a router
        // computing free room as capacity - amount would keep picking a tank that refuses the fluid.
        return column().capacityFor(resource) / TankTier.DROPLETS_PER_MB;
    }

    @Override
    public boolean isValid(int index, FluidResource resource) {
        checkIndex(index);
        return column().accepts(resource);
    }

    @Override
    public int insert(int index, FluidResource resource, int amountMb, TransactionContext transaction) {
        checkIndex(index);
        long moved = column().insert(
                        resource,
                        (long) amountMb * TankTier.DROPLETS_PER_MB,
                        TankTier.DROPLETS_PER_MB,
                        () -> updateSnapshots(transaction));
        return (int) (moved / TankTier.DROPLETS_PER_MB);
    }

    @Override
    public int extract(int index, FluidResource resource, int amountMb, TransactionContext transaction) {
        checkIndex(index);
        long moved = column().extract(
                        resource,
                        (long) amountMb * TankTier.DROPLETS_PER_MB,
                        TankTier.DROPLETS_PER_MB,
                        () -> updateSnapshots(transaction));
        return (int) (moved / TankTier.DROPLETS_PER_MB);
    }

    /**
     * The column's actual fluid, including a stored potion. Unlike {@link #getResource} this is not
     * sealed, so the bottle interaction can see and draw a potion that the fluid API hides from pipes.
     */
    public FluidResource currentFluid() {
        return column().shared();
    }

    /**
     * Deposits exactly one bottle (⅓ bucket, {@link TankTier#DROPLETS_PER_BOTTLE} droplets) of {@code
     * resource} into the column, all-or-nothing. Works in droplets so the bottle is exact, bypassing the
     * whole-mB quantization of {@link #insert}. Returns whether a full bottle fit.
     */
    public boolean depositBottle(FluidResource resource, TransactionContext transaction) {
        return column().depositBottle(resource, () -> updateSnapshots(transaction));
    }

    /**
     * Draws exactly one bottle of {@code resource} out of the column, all-or-nothing. Returns whether a
     * full bottle was present.
     */
    public boolean extractBottle(FluidResource resource, TransactionContext transaction) {
        return column().extractBottle(resource, () -> updateSnapshots(transaction));
    }

    @Override
    protected Snapshot createSnapshot() {
        List<Slot> slots = new ArrayList<>();
        for (TankBlockEntity tank : origin.columnTanks()) {
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
        // Sync every tank touched this transaction (contents are already settled by the column).
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
