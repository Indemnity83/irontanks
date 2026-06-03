package com.indemnity83.irontanks.core;

/**
 * Capacity tiers for Iron Tanks, expressed in buckets. Loader-agnostic — holds no Minecraft types.
 *
 * <p>A bucket is {@link #BUCKET_VOLUME} millibuckets, matching Minecraft's fluid granularity. Fluid
 * amounts everywhere else in {@code core} are tracked in millibuckets (see {@link #capacity()}).
 */
public enum TankTier {
    GLASS(16),
    COPPER(27),
    IRON(32),
    SILVER(43),
    GOLD(48),
    DIAMOND(64),
    /** Explosion-proof; same capacity as diamond. */
    OBSIDIAN(64),
    EMERALD(96),
    /** Destroys fluid a little each tick (see {@link VoidTank}); small buffer capacity. */
    VOID(8),
    /** Dispenses fluid endlessly; capacity is nominal since it never actually drains. */
    CREATIVE(1);

    /** Millibuckets per bucket (Minecraft's fluid unit). */
    public static final int BUCKET_VOLUME = 1000;

    private final int buckets;

    TankTier(int buckets) {
        this.buckets = buckets;
    }

    /** Capacity in whole buckets, as shown in the tank tooltip. */
    public int buckets() {
        return buckets;
    }

    /** Capacity in millibuckets, the unit fluid amounts are tracked in. */
    public long capacity() {
        return (long) buckets * BUCKET_VOLUME;
    }
}
