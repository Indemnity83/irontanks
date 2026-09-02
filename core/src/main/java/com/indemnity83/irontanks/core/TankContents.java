package com.indemnity83.irontanks.core;

/**
 * The one rule a tank's stored contents must obey, owned in {@code core} so both loaders enforce it
 * identically: <b>a tank holds between zero and its capacity, and holds an amount only while it holds a
 * fluid.</b>
 *
 * <p>Each loader's {@code TankBlockEntity.setContentsRaw} is the single point every write funnels
 * through — the ticker, the transfer-API adapters, the upgrade item, and the optional cross-mod
 * logistics column engine, which settles a shared stack itself and so never passes through
 * {@link TankColumn}. Applying the rule there means no path can leave a tank in a state the rest of the
 * mod treats as impossible.
 */
public final class TankContents {

    private TankContents() {}

    /**
     * The amount, in droplets, a tank should actually store for a raw write of {@code amount} droplets
     * of {@code fluid} — {@code amount} saturated into {@code [0, capacity]}, or zero when there is no
     * fluid to store. {@code capacity} comes from a {@link TankTier} and is never negative.
     *
     * <p>Saturating rather than rejecting is deliberate: an over-capacity write is a state to repair,
     * not a programming error. A tank can be handed more than it can hold without anyone doing anything
     * wrong, because every tank block shares one {@code BlockEntityType} — {@code /setblock}, WorldEdit,
     * a structure block or an NBT edit swap a smaller tank in under the fluid and the block entity
     * survives with its old contents. Writes reach this from a block-entity ticker, so throwing here
     * would crash the server on every chunk load ({@link FluidColumn#settle} used to, which is what
     * issue #257 was).
     *
     * <p>The caller pairs this with the fluid to store: an amount of zero must store the empty fluid.
     */
    public static <F> long storedAmount(FluidKind<F> kind, F fluid, long amount, long capacity) {
        if (kind.isEmpty(fluid) || amount <= 0) {
            return 0;
        }
        return Math.min(amount, capacity);
    }
}
