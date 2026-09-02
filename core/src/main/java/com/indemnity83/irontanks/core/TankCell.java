package com.indemnity83.irontanks.core;

/**
 * One tank in a vertical column, abstracted over the loader's block entity so {@link TankColumn} can
 * read and rewrite its contents without any Minecraft type. Each loader's {@code TankBlockEntity}
 * implements this directly (it already exposes {@link #tier()}, {@link #capacity()}, {@link #amount()}).
 * All amounts are in droplets — the canonical unit (see {@link TankTier}).
 */
public interface TankCell<F> {

    /** This tank's tier — lets {@code core} recognise a {@link TankTier#CREATIVE} cell itself. */
    TankTier tier();

    /** Capacity in droplets. */
    long capacity();

    /** Current contents in droplets. */
    long amount();

    /** The fluid currently held, or the kind's empty value. */
    F fluid();

    /**
     * Raw write of {@code fluid} + {@code amount} (droplets), no client sync — the caller handles that.
     *
     * <p>Implementations keep the invariant <b>{@code amount == 0} if and only if there is no fluid</b>,
     * in both directions: a non-positive amount clears the fluid, and an empty fluid clears the amount.
     * Anything that reads the raw amount (tooltips, the neighbour check, the renderer) would otherwise
     * show a fluid level for a cell holding nothing.
     */
    void setContents(F fluid, long amount);
}
