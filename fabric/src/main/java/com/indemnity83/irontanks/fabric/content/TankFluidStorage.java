package com.indemnity83.irontanks.fabric.content;

import com.indemnity83.irontanks.core.TankColumn;
import com.indemnity83.irontanks.core.TankTier;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import net.fabricmc.fabric.api.transfer.v1.fluid.FluidVariant;
import net.fabricmc.fabric.api.transfer.v1.storage.Storage;
import net.fabricmc.fabric.api.transfer.v1.storage.StorageView;
import net.fabricmc.fabric.api.transfer.v1.transaction.TransactionContext;
import net.fabricmc.fabric.api.transfer.v1.transaction.base.SnapshotParticipant;

/**
 * Exposes a whole vertical tank column to Fabric's Transfer API as one {@link Storage}{@code
 * <FluidVariant>}. The fluid algorithm — mixed-fluid refusal, potion sealing, creative source/sink,
 * settling — lives in {@code core}'s {@link TankColumn}; this class is just the Fabric surface plus a
 * transaction snapshot.
 *
 * <p>Fabric measures fluid in droplets, exactly the unit {@code core} uses, so insert/extract pass a
 * quantum of {@code 1} (no flooring) — unlike the NeoForge adapter, this one needs no unit conversion.
 */
public final class TankFluidStorage extends SnapshotParticipant<TankFluidStorage.Snapshot>
        implements Storage<FluidVariant> {

    /** A tank and its contents at transaction start, restored on abort. */
    public record Slot(TankBlockEntity tank, FluidVariant fluid, long amount) {}

    public record Snapshot(List<Slot> slots) {}

    private final TankBlockEntity origin;

    public TankFluidStorage(TankBlockEntity origin) {
        this.origin = origin;
    }

    private TankColumn<FluidVariant> column() {
        return origin.asColumn();
    }

    @Override
    public long insert(FluidVariant resource, long maxAmount, TransactionContext transaction) {
        return column().insert(resource, maxAmount, 1, () -> updateSnapshots(transaction));
    }

    @Override
    public long extract(FluidVariant resource, long maxAmount, TransactionContext transaction) {
        return column().extract(resource, maxAmount, 1, () -> updateSnapshots(transaction));
    }

    /**
     * The column's actual fluid, including a stored potion. Unlike the sealed {@link ColumnView}, this is
     * not hidden, so the bottle interaction can see and draw a potion that the fluid API hides from pipes.
     */
    public FluidVariant currentFluid() {
        return column().shared();
    }

    /**
     * Deposits exactly one bottle (⅓ bucket, {@link TankTier#DROPLETS_PER_BOTTLE} droplets) of {@code
     * resource} into the column, all-or-nothing. Returns whether a full bottle fit.
     */
    public boolean depositBottle(FluidVariant resource, TransactionContext transaction) {
        return column().depositBottle(resource, () -> updateSnapshots(transaction));
    }

    /** Draws exactly one bottle of {@code resource} out of the column, all-or-nothing. */
    public boolean extractBottle(FluidVariant resource, TransactionContext transaction) {
        return column().extractBottle(resource, () -> updateSnapshots(transaction));
    }

    @Override
    public Iterator<StorageView<FluidVariant>> iterator() {
        return List.<StorageView<FluidVariant>>of(new ColumnView()).iterator();
    }

    /** A single aggregate view over the whole column, reported to Fabric in droplets. */
    private final class ColumnView implements StorageView<FluidVariant> {
        @Override
        public long extract(FluidVariant resource, long maxAmount, TransactionContext transaction) {
            return TankFluidStorage.this.extract(resource, maxAmount, transaction);
        }

        @Override
        public boolean isResourceBlank() {
            return getResource().isBlank();
        }

        @Override
        public FluidVariant getResource() {
            // Blank for a column no transfer can move (a stored potion is bottle-only; a mixed column
            // is refused outright), so pipes/pumps never see contents they can't take.
            return column().reportedFluid();
        }

        @Override
        public long getAmount() {
            return column().reportedTotal();
        }

        @Override
        public long getCapacity() {
            return column().reportedCapacity();
        }
    }

    @Override
    protected Snapshot createSnapshot() {
        List<Slot> slots = new ArrayList<>();
        for (TankBlockEntity tank : origin.columnTanks()) {
            slots.add(new Slot(tank, tank.fluidVariant(), tank.amount()));
        }
        return new Snapshot(slots);
    }

    @Override
    protected void readSnapshot(Snapshot snapshot) {
        for (Slot slot : snapshot.slots()) {
            slot.tank().setContentsRaw(slot.fluid(), slot.amount());
        }
    }

    @Override
    protected void onFinalCommit() {
        // Contents are already settled by the column; push every tank in it to clients.
        for (TankBlockEntity tank : origin.columnTanks()) {
            tank.sync();
        }
    }
}
