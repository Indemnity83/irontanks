package com.indemnity83.irontanks.core;

/**
 * Where one tank's fluid sits inside its own block, and which of its horizontal faces is the visible
 * free surface. Heights are block-relative: {@code 0} is the block floor, {@code 1} the block ceiling.
 *
 * <p>The shape follows how {@link FluidColumn#settle} distributes a column. Liquids settle downward, so
 * a liquid rests on the floor and its free surface is the <em>top</em> face, rising with the fill.
 * Gases rise, so a gas hangs from the ceiling and its free surface is the <em>bottom</em> face,
 * descending as the tank fills. In a settled column that means the partially filled cell is the topmost
 * one for a liquid and the bottommost one for a gas — draw the free surface on the wrong face and a
 * half-full gas column reads as brim-full.
 *
 * <p>Where the same fluid continues into the neighbouring tank, the body is stretched to that block
 * edge and the face there is dropped, so the two tanks read as one continuous body. The sealed end
 * (a liquid's floor, a gas's ceiling) is never drawn: it is flush against the block boundary and the
 * fluid's own walls already close it off visually.
 *
 * <p>Pure geometry — no Minecraft types, so it is unit-testable without a game.
 */
public record FluidBody(float bottom, float top, boolean renderTop, boolean renderBottom) {

    /** Bottom of the block, in block-relative height. */
    public static final float FLOOR = 0.0F;

    /** Top of the block, in block-relative height. */
    public static final float CEILING = 1.0F;

    /** Nothing to draw. */
    public static final FluidBody EMPTY = new FluidBody(FLOOR, FLOOR, false, false);

    /**
     * How far a free surface is held off the block boundary it would otherwise land on. Only a gas needs
     * it: a completely full gas's underside sits at the floor, exactly where the top of a full liquid in
     * the tank below is drawn, and two coplanar translucent faces z-fight. A thousandth of a block is far
     * too small to see, and it is never applied where the body merges into its neighbour, so a continuous
     * column still meets edge to edge.
     */
    private static final float SURFACE_GAP = 1.0F / 1024.0F;

    /**
     * The body to draw for a tank holding {@code amount} of {@code capacity} droplets.
     *
     * @param gas whether the fluid is lighter than air (settles toward the top of a column)
     * @param fluidAbove whether the connecting tank above holds the same fluid
     * @param fluidBelow whether the connecting tank below holds the same fluid
     */
    public static FluidBody of(long amount, long capacity, boolean gas, boolean fluidAbove, boolean fluidBelow) {
        if (amount <= 0 || capacity <= 0) {
            return EMPTY;
        }
        // An overfull tank still only has one block to fill.
        float fill = Math.min(1.0F, (float) amount / capacity);
        if (gas) {
            // Hangs from the ceiling; the free surface is underneath it, held just clear of the floor so a
            // full gas never draws that face into the block boundary (see SURFACE_GAP).
            float bottom = fluidBelow ? FLOOR : Math.max(SURFACE_GAP, CEILING - fill);
            return new FluidBody(bottom, CEILING, false, !fluidBelow);
        }
        // Rests on the floor; the free surface is on top.
        return new FluidBody(FLOOR, fluidAbove ? CEILING : fill, !fluidAbove, false);
    }

    /** Height of the body; zero when there is nothing to draw. */
    public float height() {
        return top - bottom;
    }

    /** Whether this body has no height, so the renderer can skip it entirely. */
    public boolean isEmpty() {
        return height() <= 0.0F;
    }
}
