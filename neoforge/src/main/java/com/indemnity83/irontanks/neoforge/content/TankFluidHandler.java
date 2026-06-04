package com.indemnity83.irontanks.neoforge.content;

import com.indemnity83.irontanks.core.FluidColumn;
import com.indemnity83.irontanks.core.TankTier;
import java.util.ArrayList;
import java.util.List;
import net.minecraft.core.component.DataComponents;
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
        FluidResource current = shared(column());
        // A stored potion is bottle-only: hide it from the fluid API so pipes/buckets can't drain it.
        return isPotion(current) ? FluidResource.EMPTY : current;
    }

    @Override
    public long getAmountAsLong(int index) {
        checkIndex(index);
        if (isPotion(shared(column()))) {
            return 0; // potion is bottle-only: report empty to the fluid API
        }
        // The column stores droplets; the NeoForge transfer API speaks millibuckets.
        return totalDroplets(column()) / TankTier.DROPLETS_PER_MB;
    }

    @Override
    public long getCapacityAsLong(int index, FluidResource resource) {
        checkIndex(index);
        if (isPotion(shared(column()))) {
            return 0; // sealed while holding a potion
        }
        return capacityDroplets(column()) / TankTier.DROPLETS_PER_MB;
    }

    @Override
    public boolean isValid(int index, FluidResource resource) {
        checkIndex(index);
        FluidResource current = shared(column());
        if (isPotion(current) || isPotion(resource)) {
            return false; // potions never move through the fluid API
        }
        return current.isEmpty() || current.equals(resource);
    }

    @Override
    public int insert(int index, FluidResource resource, int amountMb, TransactionContext transaction) {
        checkIndex(index);
        if (resource.isEmpty() || amountMb <= 0) {
            return 0;
        }
        List<TankBlockEntity> column = column();
        if (mixed(column)) {
            return 0; // distinct fluids joined into one column: leave them alone, never aggregate
        }
        FluidResource current = shared(column);
        if (isPotion(current) || isPotion(resource)) {
            return 0; // sealed: potions are deposited only via depositBottle
        }
        if (!current.isEmpty() && !current.equals(resource)) {
            return 0;
        }
        if (creative()) {
            updateSnapshots(transaction);
            origin.setContentsRaw(resource, origin.capacity()); // define the source, stay full
            return amountMb;
        }
        // Only whole millibuckets cross this boundary: floor the room so an existing sub-mB bottle
        // fraction is never partially filled (which would create fluid against the pipe's mB accounting).
        long roomMb = roomDroplets(column) / TankTier.DROPLETS_PER_MB;
        long take = Math.min(amountMb, roomMb);
        if (take <= 0) {
            return 0;
        }
        updateSnapshots(transaction);
        distribute(column, resource, totalDroplets(column) + take * TankTier.DROPLETS_PER_MB);
        return (int) take;
    }

    @Override
    public int extract(int index, FluidResource resource, int amountMb, TransactionContext transaction) {
        checkIndex(index);
        if (resource.isEmpty() || amountMb <= 0) {
            return 0;
        }
        List<TankBlockEntity> column = column();
        if (mixed(column)) {
            return 0; // distinct fluids joined into one column: leave them alone, never aggregate
        }
        FluidResource current = shared(column);
        if (isPotion(current)) {
            return 0; // sealed: potions are drawn only via extractBottle
        }
        if (current.isEmpty() || !current.equals(resource)) {
            return 0;
        }
        if (creative()) {
            return amountMb; // endless supply: never depletes
        }
        long availMb = totalDroplets(column) / TankTier.DROPLETS_PER_MB;
        long take = Math.min(amountMb, availMb);
        if (take <= 0) {
            return 0;
        }
        updateSnapshots(transaction);
        distribute(column, current, totalDroplets(column) - take * TankTier.DROPLETS_PER_MB);
        return (int) take;
    }

    /**
     * The column's actual fluid, including a stored potion. Unlike {@link #getResource} this is not
     * sealed, so the bottle interaction can see and draw a potion that the fluid API hides from pipes.
     */
    public FluidResource currentFluid() {
        return shared(column());
    }

    /**
     * Deposits exactly one bottle (⅓ bucket, {@link TankTier#DROPLETS_PER_BOTTLE} droplets) of {@code
     * resource} into the column, all-or-nothing. Works in droplets so the bottle is exact, bypassing the
     * whole-mB quantization of {@link #insert}. Returns whether a full bottle fit.
     */
    public boolean depositBottle(FluidResource resource, TransactionContext transaction) {
        if (resource.isEmpty()) {
            return false;
        }
        List<TankBlockEntity> column = column();
        if (mixed(column)) {
            return false; // distinct fluids joined into one column: leave them alone
        }
        FluidResource current = shared(column);
        if (!current.isEmpty() && !current.equals(resource)) {
            return false;
        }
        if (creative()) {
            updateSnapshots(transaction);
            origin.setContentsRaw(resource, origin.capacity());
            return true;
        }
        if (roomDroplets(column) < TankTier.DROPLETS_PER_BOTTLE) {
            return false;
        }
        updateSnapshots(transaction);
        distribute(column, resource, totalDroplets(column) + TankTier.DROPLETS_PER_BOTTLE);
        return true;
    }

    /**
     * Draws exactly one bottle of {@code resource} out of the column, all-or-nothing. Returns whether a
     * full bottle was present.
     */
    public boolean extractBottle(FluidResource resource, TransactionContext transaction) {
        if (resource.isEmpty()) {
            return false;
        }
        List<TankBlockEntity> column = column();
        if (mixed(column)) {
            return false; // distinct fluids joined into one column: leave them alone
        }
        FluidResource current = shared(column);
        if (current.isEmpty() || !current.equals(resource)) {
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

    private static long roomDroplets(List<TankBlockEntity> column) {
        return capacityDroplets(column) - totalDroplets(column);
    }

    /** Settles {@code total} droplets across the column (via {@code core}) and writes each tank's share. */
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

    /**
     * Whether the column holds two or more distinct fluids — e.g. two pre-filled tanks joined by a third.
     * {@link #shared(List)} only reports the first one, so the fluid API would otherwise sum the whole
     * column and redistribute it as that single fluid, converting the others. Operations refuse to act on
     * such a column, mirroring {@link TankBlockEntity#balanceColumn()}, which leaves it unsettled.
     */
    private static boolean mixed(List<TankBlockEntity> column) {
        FluidResource seen = FluidResource.EMPTY;
        for (TankBlockEntity tank : column) {
            FluidResource fluid = tank.fluidResource();
            if (fluid.isEmpty()) {
                continue;
            }
            if (seen.isEmpty()) {
                seen = fluid;
            } else if (!seen.equals(fluid)) {
                return true;
            }
        }
        return false;
    }

    /**
     * Whether a fluid is a stored potion — water carrying a {@code potion_contents} component. Potions
     * are bottle-only: this adapter hides them from pipes/pumps so a potion can never be drained out (and
     * silently degraded to water) or topped up through the fluid API. Bottle I/O bypasses these methods.
     */
    private static boolean isPotion(FluidResource fluid) {
        return !fluid.isEmpty() && fluid.get(DataComponents.POTION_CONTENTS) != null;
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
