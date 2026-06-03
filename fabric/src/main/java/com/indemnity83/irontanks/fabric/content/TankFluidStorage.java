package com.indemnity83.irontanks.fabric.content;

import com.indemnity83.irontanks.core.FluidColumn;
import com.indemnity83.irontanks.core.TankTier;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import net.fabricmc.fabric.api.transfer.v1.fluid.FluidConstants;
import net.fabricmc.fabric.api.transfer.v1.fluid.FluidVariant;
import net.fabricmc.fabric.api.transfer.v1.fluid.FluidVariantAttributes;
import net.fabricmc.fabric.api.transfer.v1.storage.Storage;
import net.fabricmc.fabric.api.transfer.v1.storage.StorageView;
import net.fabricmc.fabric.api.transfer.v1.transaction.TransactionContext;
import net.fabricmc.fabric.api.transfer.v1.transaction.base.SnapshotParticipant;

/**
 * Exposes a whole vertical tank column to Fabric's Transfer API as one {@link Storage}{@code
 * <FluidVariant>}. Operations act on the column's combined contents and re-settle them via {@code
 * core}, so filling any tank in a stack fills the column. A creative tank never joins a column, so it
 * acts as a single endless source/sink.
 *
 * <p>Fabric measures fluid in droplets ({@link FluidConstants#BUCKET} per bucket) while the tank and
 * {@code core} work in millibuckets; this adapter converts at the boundary ({@link #DROPLETS_PER_MB}).
 */
public final class TankFluidStorage extends SnapshotParticipant<TankFluidStorage.Snapshot>
        implements Storage<FluidVariant> {

    private static final long DROPLETS_PER_MB = FluidConstants.BUCKET / TankTier.BUCKET_VOLUME;

    /** A tank and its contents at transaction start, restored on abort. */
    public record Slot(TankBlockEntity tank, FluidVariant fluid, long amount) {}

    public record Snapshot(List<Slot> slots) {}

    private final TankBlockEntity origin;

    public TankFluidStorage(TankBlockEntity origin) {
        this.origin = origin;
    }

    private boolean creative() {
        return origin.tier() == TankTier.CREATIVE;
    }

    private List<TankBlockEntity> column() {
        return origin.columnTanks();
    }

    @Override
    public long insert(FluidVariant resource, long maxAmount, TransactionContext transaction) {
        if (resource.isBlank() || maxAmount <= 0) {
            return 0;
        }
        long maxMillibuckets = maxAmount / DROPLETS_PER_MB;
        if (maxMillibuckets <= 0) {
            return 0;
        }
        List<TankBlockEntity> column = column();
        FluidVariant current = shared(column);
        if (!current.isBlank() && !current.equals(resource)) {
            return 0;
        }
        if (creative()) {
            updateSnapshots(transaction);
            origin.setContentsRaw(resource, origin.capacity());
            return maxMillibuckets * DROPLETS_PER_MB;
        }
        long capacity = 0;
        long total = 0;
        for (TankBlockEntity tank : column) {
            capacity += tank.capacity();
            total += tank.amount();
        }
        long room = FluidColumn.fillable(capacity, total, maxMillibuckets);
        if (room <= 0) {
            return 0;
        }
        updateSnapshots(transaction);
        distribute(column, resource, total + room);
        return room * DROPLETS_PER_MB;
    }

    @Override
    public long extract(FluidVariant resource, long maxAmount, TransactionContext transaction) {
        if (resource.isBlank() || maxAmount <= 0) {
            return 0;
        }
        long maxMillibuckets = maxAmount / DROPLETS_PER_MB;
        if (maxMillibuckets <= 0) {
            return 0;
        }
        List<TankBlockEntity> column = column();
        FluidVariant current = shared(column);
        if (current.isBlank() || !current.equals(resource)) {
            return 0;
        }
        if (creative()) {
            return maxMillibuckets * DROPLETS_PER_MB; // endless supply: never depletes
        }
        long total = 0;
        for (TankBlockEntity tank : column) {
            total += tank.amount();
        }
        long taken = FluidColumn.drainable(total, maxMillibuckets);
        if (taken <= 0) {
            return 0;
        }
        updateSnapshots(transaction);
        distribute(column, current, total - taken);
        return taken * DROPLETS_PER_MB;
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
            return shared(column());
        }

        @Override
        public long getAmount() {
            long total = 0;
            for (TankBlockEntity tank : column()) {
                total += tank.amount();
            }
            return total * DROPLETS_PER_MB;
        }

        @Override
        public long getCapacity() {
            long capacity = 0;
            for (TankBlockEntity tank : column()) {
                capacity += tank.capacity();
            }
            return capacity * DROPLETS_PER_MB;
        }
    }

    private static void distribute(List<TankBlockEntity> column, FluidVariant fluid, long totalMillibuckets) {
        long[] capacities = column.stream().mapToLong(TankBlockEntity::capacity).toArray();
        boolean gas = FluidVariantAttributes.isLighterThanAir(fluid);
        long[] settled = FluidColumn.settle(capacities, totalMillibuckets, gas);
        for (int i = 0; i < column.size(); i++) {
            long amount = settled[i];
            column.get(i).setContentsRaw(amount == 0 ? FluidVariant.blank() : fluid, amount);
        }
    }

    private static FluidVariant shared(List<TankBlockEntity> column) {
        for (TankBlockEntity tank : column) {
            if (!tank.fluidVariant().isBlank()) {
                return tank.fluidVariant();
            }
        }
        return FluidVariant.blank();
    }

    @Override
    protected Snapshot createSnapshot() {
        List<Slot> slots = new ArrayList<>();
        for (TankBlockEntity tank : column()) {
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
        // Contents are already settled by distribute(); push every tank in the column to clients.
        for (TankBlockEntity tank : column()) {
            tank.sync();
        }
    }
}
