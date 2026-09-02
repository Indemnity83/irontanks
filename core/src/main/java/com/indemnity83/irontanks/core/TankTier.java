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
    // buckets, hardness (mining time), blastResistance (explosion resistance)
    GLASS(16, 0.3F, 0.3F),
    COPPER(27, 4.0F, 2.0F),
    IRON(32, 5.0F, 3.0F),
    SILVER(43, 6.0F, 5.0F),
    GOLD(48, 7.0F, 4.0F),
    DIAMOND(64, 8.0F, 6.0F),
    /** Explosion-proof; same capacity as diamond. */
    OBSIDIAN(64, 50.0F, 1200.0F),
    EMERALD(96, 8.0F, 6.0F),
    /** Optional high tiers, gated on conventional material tags (empty by default — light up in packs). */
    ALUMINIUM(96, 5.0F, 4.0F),
    STAINLESSSTEEL(128, 9.0F, 8.0F),
    TITANIUM(256, 10.0F, 10.0F),
    TUNGSTENSTEEL(512, 12.0F, 14.0F),
    /** Destroys fluid a little each tick (see {@link VoidTank}); small buffer capacity. */
    VOID(8, 5.0F, 6.0F),
    /** Dispenses fluid endlessly; capacity is nominal since it never actually drains. */
    CREATIVE(1, 5.0F, 6.0F);

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
    private final float hardness;
    private final float blastResistance;

    TankTier(int buckets, float hardness, float blastResistance) {
        this.buckets = buckets;
        this.hardness = hardness;
        this.blastResistance = blastResistance;
    }

    /** Capacity in whole buckets, as shown in the tank tooltip. */
    public int buckets() {
        return buckets;
    }

    /** Capacity in droplets, the unit fluid amounts are tracked in. */
    public long capacity() {
        return (long) buckets * DROPLETS_PER_BUCKET;
    }

    /** Block hardness (mining time) for this tier's tank. */
    public float hardness() {
        return hardness;
    }

    /**
     * Whether tanks of this tier join the shared vertical fluid column. Creative and void tanks stay
     * isolated single-cell columns: a creative tank would feed an endless source into a shared body, and
     * a void tank would silently destroy its neighbours' fluid — settling keeps its cell topped up, so
     * the per-tick drain in {@link VoidTank} would eat the whole stack instead of only its own contents.
     *
     * <p>This is the single membership rule; every consumer (column traversal, the block's joined/seamless
     * rendering, the in-place upgrade, and the Logistics bridge) asks here so they cannot diverge.
     */
    public boolean joinsColumn() {
        return this != CREATIVE && this != VOID;
    }

    /**
     * Whether a tank of this tier renders joined to the tank directly below it — the same rule the fluid
     * column uses, so the seamless side texture never claims a connection the fluid does not have.
     * {@code below} is {@code null} when the block underneath is not a tank at all.
     */
    public boolean joinsWith(TankTier below) {
        return below != null && joinsColumn() && below.joinsColumn();
    }

    /** Explosion resistance for this tier's tank; obsidian is high enough to be blast-proof. */
    public float blastResistance() {
        return blastResistance;
    }
}
