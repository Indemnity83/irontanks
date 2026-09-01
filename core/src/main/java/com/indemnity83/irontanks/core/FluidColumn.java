package com.indemnity83.irontanks.core;

/**
 * Pure fluid-distribution math for a vertical column of tanks that all hold the same fluid.
 *
 * <p>A column is described by per-tank capacities ordered <em>bottom-to-top</em> plus a single
 * aggregate amount — every tank in a connected column holds one fluid, so its contents are fully
 * described by a total. Liquids settle toward the bottom; gases rise toward the top. All amounts are
 * in <strong>droplets</strong>, the canonical unit in {@code core} (see {@link TankTier}) — never
 * millibuckets. This class holds no Minecraft types, so it is unit-testable without a game.
 *
 * <p>The loader glue gathers the column (capacities + current total) from the world, calls
 * {@link #settle} / {@link #fillable} / {@link #drainable} here, and writes the result back to each
 * tile. This is the reimplementation of the 1.12 {@code TankTile} column logic with no MC dependency.
 */
public final class FluidColumn {

    private FluidColumn() {}

    /** Total capacity of the column (sum of per-tank capacities). */
    public static long totalCapacity(long[] capacities) {
        long sum = 0;
        for (long c : capacities) {
            sum += c;
        }
        return sum;
    }

    /**
     * Distributes {@code total} droplets across {@code capacities}, filling each tank to capacity
     * in settle order — bottom-up for liquids, top-down for gases — and returns the per-tank amounts
     * (always indexed bottom-to-top, matching {@code capacities}).
     *
     * @throws IllegalArgumentException if {@code total} is negative or exceeds the column capacity
     */
    public static long[] settle(long[] capacities, long total, boolean gas) {
        if (total < 0) {
            throw new IllegalArgumentException("total must be >= 0: " + total);
        }
        long capacity = totalCapacity(capacities);
        if (total > capacity) {
            throw new IllegalArgumentException("total " + total + " exceeds column capacity " + capacity);
        }
        int n = capacities.length;
        long[] out = new long[n];
        long remaining = total;
        for (int k = 0; k < n; k++) {
            int i = gas ? (n - 1 - k) : k;
            long put = Math.min(capacities[i], remaining);
            out[i] = put;
            remaining -= put;
        }
        return out;
    }

    /**
     * How much can be added to a column that currently holds {@code current} of {@code capacity}.
     * Never negative; capped so the column never overfills.
     */
    public static long fillable(long capacity, long current, long amount) {
        if (amount <= 0) {
            return 0;
        }
        return Math.min(amount, Math.max(0, capacity - current));
    }

    /** How much can be removed from a column that currently holds {@code current}. Never negative. */
    public static long drainable(long current, long amount) {
        if (amount <= 0) {
            return 0;
        }
        return Math.min(amount, Math.max(0, current));
    }
}
