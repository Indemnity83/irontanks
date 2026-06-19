package com.indemnity83.irontanks.core;

/**
 * Capacity tiers for Iron Tanks, expressed in buckets. Loader-agnostic — holds no Minecraft types.
 *
 * <p>The canonical fluid unit in {@code core} is the <strong>droplet</strong> ({@link
 * #DROPLETS_PER_BUCKET} per bucket — Minecraft's finest fluid granularity, what Fabric uses natively).
 * Droplets are chosen because a bottle is exactly one third of a bucket ({@link #DROPLETS_PER_BOTTLE}),
 * which is not a whole number of millibuckets; in droplets every bucket/bottle operation is exact
 * integer arithmetic. NeoForge speaks millibuckets, so its adapter converts at the boundary using
 * {@link #DROPLETS_PER_MB}; Fabric is already in droplets and needs no conversion. Fluid amounts
 * everywhere else in {@code core} are tracked in droplets (see {@link #capacity()}).
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
    /** Optional high tiers, gated on conventional material tags (empty by default — light up in packs). */
    ALUMINIUM(96),
    STAINLESSSTEEL(128),
    TITANIUM(256),
    TUNGSTENSTEEL(512),
    /** Destroys fluid a little each tick (see {@link VoidTank}); small buffer capacity. */
    VOID(8),
    /** Dispenses fluid endlessly; capacity is nominal since it never actually drains. */
    CREATIVE(1);

    /** Droplets per bucket — the canonical fluid unit (matches Fabric's {@code FluidConstants.BUCKET}). */
    public static final int DROPLETS_PER_BUCKET = 81_000;

    /** Droplets per millibucket, for converting at the NeoForge (mB) boundary. */
    public static final int DROPLETS_PER_MB = DROPLETS_PER_BUCKET / 1000; // 81

    /**
     * Droplets a single bottle holds: exactly one third of a bucket, matching vanilla cauldrons (three
     * bottles fill a bucket). Exact in droplets — three bottles total exactly one bucket.
     */
    public static final int DROPLETS_PER_BOTTLE = DROPLETS_PER_BUCKET / 3; // 27_000

    private final int buckets;

    TankTier(int buckets) {
        this.buckets = buckets;
    }

    /** Capacity in whole buckets, as shown in the tank tooltip. */
    public int buckets() {
        return buckets;
    }

    /** Capacity in droplets, the unit fluid amounts are tracked in. */
    public long capacity() {
        return (long) buckets * DROPLETS_PER_BUCKET;
    }
}
