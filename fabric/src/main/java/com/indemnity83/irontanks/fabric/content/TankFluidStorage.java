package com.indemnity83.irontanks.fabric.content;

import com.indemnity83.irontanks.core.FluidColumn;
import com.indemnity83.irontanks.core.TankTier;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import net.fabricmc.fabric.api.transfer.v1.fluid.FluidVariant;
import net.fabricmc.fabric.api.transfer.v1.fluid.FluidVariantAttributes;
import net.fabricmc.fabric.api.transfer.v1.storage.Storage;
import net.fabricmc.fabric.api.transfer.v1.storage.StorageView;
import net.fabricmc.fabric.api.transfer.v1.transaction.TransactionContext;
import net.fabricmc.fabric.api.transfer.v1.transaction.base.SnapshotParticipant;
import net.minecraft.core.component.DataComponents;

/**
 * Exposes a whole vertical tank column to Fabric's Transfer API as one {@link Storage}{@code
 * <FluidVariant>}. Operations act on the column's combined contents and re-settle them via {@code
 * core}, so filling any tank in a stack fills the column. A creative tank never joins a column, so it
 * acts as a single endless source/sink.
 *
 * <p>Fabric measures fluid in droplets, which is exactly the unit {@code core} and the tank store (see
 * {@link TankTier}). So unlike the NeoForge adapter, this one needs no unit conversion — it is native
 * droplets throughout.
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
        List<TankBlockEntity> column = column();
        if (mixed(column)) {
            return 0; // distinct fluids joined into one column: leave them alone, never aggregate
        }
        FluidVariant current = shared(column);
        if (isPotion(current) || isPotion(resource)) {
            return 0; // sealed: potions are deposited only via depositBottle
        }
        if (!current.isBlank() && !current.equals(resource)) {
            return 0;
        }
        if (creative()) {
            updateSnapshots(transaction);
            origin.setContentsRaw(resource, origin.capacity());
            return maxAmount;
        }
        long room = FluidColumn.fillable(capacityDroplets(column), totalDroplets(column), maxAmount);
        if (room <= 0) {
            return 0;
        }
        updateSnapshots(transaction);
        distribute(column, resource, totalDroplets(column) + room);
        return room;
    }

    @Override
    public long extract(FluidVariant resource, long maxAmount, TransactionContext transaction) {
        if (resource.isBlank() || maxAmount <= 0) {
            return 0;
        }
        List<TankBlockEntity> column = column();
        if (mixed(column)) {
            return 0; // distinct fluids joined into one column: leave them alone, never aggregate
        }
        FluidVariant current = shared(column);
        if (isPotion(current)) {
            return 0; // sealed: potions are drawn only via extractBottle
        }
        if (current.isBlank() || !current.equals(resource)) {
            return 0;
        }
        if (creative()) {
            return maxAmount; // endless supply: never depletes
        }
        long taken = FluidColumn.drainable(totalDroplets(column), maxAmount);
        if (taken <= 0) {
            return 0;
        }
        updateSnapshots(transaction);
        distribute(column, current, totalDroplets(column) - taken);
        return taken;
    }

    /**
     * The column's actual fluid, including a stored potion. Unlike the sealed {@link ColumnView}, this is
     * not hidden, so the bottle interaction can see and draw a potion that the fluid API hides from pipes.
     */
    public FluidVariant currentFluid() {
        return shared(column());
    }

    /**
     * Deposits exactly one bottle (⅓ bucket, {@link TankTier#DROPLETS_PER_BOTTLE} droplets) of {@code
     * resource} into the column, all-or-nothing. Returns whether a full bottle fit.
     */
    public boolean depositBottle(FluidVariant resource, TransactionContext transaction) {
        if (resource.isBlank()) {
            return false;
        }
        List<TankBlockEntity> column = column();
        if (mixed(column)) {
            return false; // distinct fluids joined into one column: leave them alone
        }
        FluidVariant current = shared(column);
        if (!current.isBlank() && !current.equals(resource)) {
            return false;
        }
        if (creative()) {
            updateSnapshots(transaction);
            origin.setContentsRaw(resource, origin.capacity());
            return true;
        }
        if (capacityDroplets(column) - totalDroplets(column) < TankTier.DROPLETS_PER_BOTTLE) {
            return false;
        }
        updateSnapshots(transaction);
        distribute(column, resource, totalDroplets(column) + TankTier.DROPLETS_PER_BOTTLE);
        return true;
    }

    /** Draws exactly one bottle of {@code resource} out of the column, all-or-nothing. */
    public boolean extractBottle(FluidVariant resource, TransactionContext transaction) {
        if (resource.isBlank()) {
            return false;
        }
        List<TankBlockEntity> column = column();
        if (mixed(column)) {
            return false; // distinct fluids joined into one column: leave them alone
        }
        FluidVariant current = shared(column);
        if (current.isBlank() || !current.equals(resource)) {
            return false;
        }
        if (creative()) {
            return true; // endless supply: never depletes
        }
        if (totalDroplets(column) < TankTier.DROPLETS_PER_BOTTLE) {
            return false;
        }
        updateSnapshots(transaction);
        distribute(column, current, totalDroplets(column) - TankTier.DROPLETS_PER_BOTTLE);
        return true;
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
            // A stored potion is bottle-only: present the tank as blank so pipes/pumps can't drain it.
            FluidVariant current = shared(column());
            return isPotion(current) ? FluidVariant.blank() : current;
        }

        @Override
        public long getAmount() {
            return isPotion(shared(column())) ? 0 : totalDroplets(column());
        }

        @Override
        public long getCapacity() {
            return isPotion(shared(column())) ? 0 : capacityDroplets(column());
        }
    }

    private static long totalDroplets(List<TankBlockEntity> column) {
        long total = 0;
        for (TankBlockEntity tank : column) {
            total += tank.amount();
        }
        return total;
    }

    private static long capacityDroplets(List<TankBlockEntity> column) {
        long capacity = 0;
        for (TankBlockEntity tank : column) {
            capacity += tank.capacity();
        }
        return capacity;
    }

    private static void distribute(List<TankBlockEntity> column, FluidVariant fluid, long totalDroplets) {
        long[] capacities = column.stream().mapToLong(TankBlockEntity::capacity).toArray();
        boolean gas = FluidVariantAttributes.isLighterThanAir(fluid);
        long[] settled = FluidColumn.settle(capacities, totalDroplets, gas);
        for (int i = 0; i < column.size(); i++) {
            long amount = settled[i];
            column.get(i).setContentsRaw(amount == 0 ? FluidVariant.blank() : fluid, amount);
        }
    }

    /**
     * Whether a fluid is a stored potion — water carrying a {@code potion_contents} component. Potions
     * are bottle-only: this adapter hides them from pipes/pumps so a potion can never be drained out (and
     * silently degraded to water) or topped up through the fluid API. Bottle I/O bypasses these methods.
     */
    private static boolean isPotion(FluidVariant fluid) {
        return !fluid.isBlank() && fluid.get(DataComponents.POTION_CONTENTS) != null;
    }

    private static FluidVariant shared(List<TankBlockEntity> column) {
        for (TankBlockEntity tank : column) {
            if (!tank.fluidVariant().isBlank()) {
                return tank.fluidVariant();
            }
        }
        return FluidVariant.blank();
    }

    /**
     * Whether the column holds two or more distinct fluids — e.g. two pre-filled tanks joined by a third.
     * {@link #shared(List)} only reports the first one, so the fluid API would otherwise sum the whole
     * column and redistribute it as that single fluid, converting the others. Operations refuse to act on
     * such a column, mirroring {@link TankBlockEntity#balanceColumn()}, which leaves it unsettled.
     */
    private static boolean mixed(List<TankBlockEntity> column) {
        FluidVariant seen = FluidVariant.blank();
        for (TankBlockEntity tank : column) {
            FluidVariant fluid = tank.fluidVariant();
            if (fluid.isBlank()) {
                continue;
            }
            if (seen.isBlank()) {
                seen = fluid;
            } else if (!seen.equals(fluid)) {
                return true;
            }
        }
        return false;
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
