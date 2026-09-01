package com.indemnity83.irontanks.core;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * The whole tank-column fluid algorithm, owned once in {@code core} instead of copied into each
 * loader's transfer-API adapter. A column is an {@code origin} cell plus the connected cells ordered
 * bottom-to-top, all sharing one {@link FluidKind}. Every operation works in droplets and re-settles
 * the column via {@link FluidColumn} (liquids to the bottom, gases to the top).
 *
 * <p>The rules live here so they are unit-testable without a game:
 *
 * <ul>
 *   <li><b>Mixed columns</b> (two distinct fluids joined together) are never aggregated — every
 *       operation refuses, mirroring {@link #rebalance()} leaving them unsettled.
 *   <li><b>Potions</b> are sealed from the fluid path: {@link #insert}/{@link #extract} reject them, so
 *       they move only through {@link #depositBottle}/{@link #extractBottle}.
 *   <li><b>Creative</b> tanks are an endless source/sink and never join a column (single-cell column).
 * </ul>
 *
 * <p>Loaders that report fluid in coarser units than droplets pass a {@code quantum} to insert/extract:
 * the amount moved is floored to a whole multiple of it, so a sub-quantum remainder (e.g. a stored
 * bottle fraction) is never partially filled. Fabric passes {@code quantum = 1} (native droplets);
 * NeoForge passes {@link TankTier#DROPLETS_PER_MB} and converts the droplet result back to millibuckets.
 *
 * <p>Mutating methods take an {@code onMutate} hook run exactly once immediately before the first write,
 * so a loader can capture its transaction snapshot only when the column is actually about to change.
 */
public final class TankColumn<F> {

    private final TankCell<F> origin;
    private final List<? extends TankCell<F>> cells;
    private final FluidKind<F> kind;

    /** @param cells the column ordered bottom-to-top; {@code origin} is the cell the operation entered through */
    public TankColumn(TankCell<F> origin, List<? extends TankCell<F>> cells, FluidKind<F> kind) {
        this.origin = origin;
        this.cells = cells;
        this.kind = kind;
    }

    private boolean creative() {
        return origin.tier() == TankTier.CREATIVE;
    }

    // ==================== aggregation ====================

    /**
     * Total contents of the column, in droplets. A cell holding no fluid contributes nothing whatever
     * its amount says: a tank whose stored fluid no longer resolves (its mod was removed) can load with
     * a leftover amount, and since every operation redistributes {@code total()} as the column's single
     * fluid, counting that blank amount would turn it into real fluid.
     */
    public long total() {
        long total = 0;
        for (TankCell<F> cell : cells) {
            if (!kind.isEmpty(cell.fluid())) {
                total += cell.amount();
            }
        }
        return total;
    }

    /** Total capacity of the column, in droplets. */
    public long capacity() {
        long capacity = 0;
        for (TankCell<F> cell : cells) {
            capacity += cell.capacity();
        }
        return capacity;
    }

    /** Free space left in the column, in droplets. */
    public long room() {
        return capacity() - total();
    }

    /** The single fluid the column holds, or the kind's empty value if it is empty. */
    public F shared() {
        for (TankCell<F> cell : cells) {
            if (!kind.isEmpty(cell.fluid())) {
                return cell.fluid();
            }
        }
        return kind.empty();
    }

    /**
     * Whether the column holds two or more distinct fluids — e.g. two pre-filled tanks joined by a
     * third. {@link #shared()} reports only the first, so an operation that trusted it would sum the
     * whole column and redistribute it as that one fluid, silently converting the others. Operations
     * refuse to act on such a column.
     */
    public boolean mixed() {
        F seen = kind.empty();
        for (TankCell<F> cell : cells) {
            F fluid = cell.fluid();
            if (kind.isEmpty(fluid)) {
                continue;
            }
            if (kind.isEmpty(seen)) {
                seen = fluid;
            } else if (!Objects.equals(seen, fluid)) {
                return true;
            }
        }
        return false;
    }

    /** Whether {@code fluid} is a sealed potion (delegates to the kind). */
    public boolean isPotion(F fluid) {
        return kind.isPotion(fluid);
    }

    // ==================== fluid-API operations ====================

    /**
     * Adds up to {@code maxDroplets} of {@code resource} to the column, floored to a whole multiple of
     * {@code quantum}. Returns the droplets actually added (0 if the column is mixed, sealed by a
     * potion, holds a different fluid, or is full). A creative column becomes the source and stays full.
     */
    public long insert(F resource, long maxDroplets, long quantum, Runnable onMutate) {
        requirePositiveQuantum(quantum);
        if (kind.isEmpty(resource) || maxDroplets <= 0) {
            return 0;
        }
        if (mixed()) {
            return 0;
        }
        F current = shared();
        if (kind.isPotion(current) || kind.isPotion(resource)) {
            return 0; // sealed: potions are deposited only via depositBottle
        }
        if (!kind.isEmpty(current) && !Objects.equals(current, resource)) {
            return 0;
        }
        if (creative()) {
            onMutate.run();
            origin.setContents(resource, origin.capacity()); // define the source, stay full
            return floorTo(maxDroplets, quantum);
        }
        long take = Math.min(floorTo(maxDroplets, quantum), floorTo(room(), quantum));
        if (take <= 0) {
            return 0;
        }
        onMutate.run();
        distribute(resource, total() + take);
        return take;
    }

    /**
     * Removes up to {@code maxDroplets} of {@code resource} from the column, floored to a whole multiple
     * of {@code quantum}. Returns the droplets actually removed (0 if mixed, sealed, empty, or holding a
     * different fluid). A creative column is endless and never depletes.
     */
    public long extract(F resource, long maxDroplets, long quantum, Runnable onMutate) {
        requirePositiveQuantum(quantum);
        if (kind.isEmpty(resource) || maxDroplets <= 0) {
            return 0;
        }
        if (mixed()) {
            return 0;
        }
        F current = shared();
        if (kind.isPotion(current)) {
            return 0; // sealed: potions are drawn only via extractBottle
        }
        if (kind.isEmpty(current) || !Objects.equals(current, resource)) {
            return 0;
        }
        if (creative()) {
            return floorTo(maxDroplets, quantum); // endless supply: never depletes
        }
        long take = Math.min(floorTo(maxDroplets, quantum), floorTo(total(), quantum));
        if (take <= 0) {
            return 0;
        }
        onMutate.run();
        distribute(current, total() - take);
        return take;
    }

    /**
     * Deposits exactly one bottle ({@link TankTier#DROPLETS_PER_BOTTLE} droplets) of {@code resource},
     * all-or-nothing and droplet-exact (no quantum). Returns whether a full bottle fit.
     */
    public boolean depositBottle(F resource, Runnable onMutate) {
        if (kind.isEmpty(resource)) {
            return false;
        }
        if (mixed()) {
            return false;
        }
        F current = shared();
        if (!kind.isEmpty(current) && !Objects.equals(current, resource)) {
            return false;
        }
        if (creative()) {
            onMutate.run();
            origin.setContents(resource, origin.capacity());
            return true;
        }
        if (room() < TankTier.DROPLETS_PER_BOTTLE) {
            return false;
        }
        onMutate.run();
        distribute(resource, total() + TankTier.DROPLETS_PER_BOTTLE);
        return true;
    }

    /** Draws exactly one bottle of {@code resource} out of the column, all-or-nothing. */
    public boolean extractBottle(F resource, Runnable onMutate) {
        if (kind.isEmpty(resource)) {
            return false;
        }
        if (mixed()) {
            return false;
        }
        F current = shared();
        if (kind.isEmpty(current) || !Objects.equals(current, resource)) {
            return false;
        }
        if (creative()) {
            return true; // endless supply: never depletes
        }
        if (total() < TankTier.DROPLETS_PER_BOTTLE) {
            return false;
        }
        onMutate.run();
        distribute(current, total() - TankTier.DROPLETS_PER_BOTTLE);
        return true;
    }

    // ==================== settling ====================

    /**
     * Re-settles the column's existing contents (liquids down, gases up) and writes each cell's share.
     * Returns the cells whose contents changed (empty if nothing moved, the column is empty, or it holds
     * mixed fluids and is therefore left alone). The caller syncs the returned cells.
     */
    public List<TankCell<F>> rebalance() {
        F shared = shared();
        if (kind.isEmpty(shared) || mixed()) {
            return List.of();
        }
        long[] settled = settle(shared, total());
        List<TankCell<F>> changed = new ArrayList<>();
        for (int i = 0; i < cells.size(); i++) {
            TankCell<F> cell = cells.get(i);
            long target = settled[i];
            F targetFluid = target == 0 ? kind.empty() : shared;
            if (cell.amount() != target || !Objects.equals(cell.fluid(), targetFluid)) {
                cell.setContents(targetFluid, target);
                changed.add(cell);
            }
        }
        return changed;
    }

    private void distribute(F fluid, long totalDroplets) {
        long[] settled = settle(fluid, totalDroplets);
        for (int i = 0; i < cells.size(); i++) {
            long amount = settled[i];
            cells.get(i).setContents(amount == 0 ? kind.empty() : fluid, amount);
        }
    }

    private long[] settle(F fluid, long totalDroplets) {
        long[] capacities = new long[cells.size()];
        for (int i = 0; i < cells.size(); i++) {
            capacities[i] = cells.get(i).capacity();
        }
        return FluidColumn.settle(capacities, totalDroplets, kind.isGas(fluid));
    }

    /** Floors a non-negative {@code value} to a whole multiple of {@code quantum}. */
    private static long floorTo(long value, long quantum) {
        return value - (value % quantum);
    }

    private static void requirePositiveQuantum(long quantum) {
        if (quantum <= 0) {
            throw new IllegalArgumentException("quantum must be > 0: " + quantum);
        }
    }
}
