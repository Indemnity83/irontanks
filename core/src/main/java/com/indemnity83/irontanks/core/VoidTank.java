package com.indemnity83.irontanks.core;

/**
 * Void-tank behavior: destroys the fluid it holds a little each tick. The per-tick limit matches
 * BuildCraft's default fluid-pipe transfer rate (20 mB/tick) so a void tank can't instantly
 * annihilate a connected network — kept as a constant here so there is no runtime BuildCraft tie.
 *
 * <p>This drains only the tank's own contents, never the whole column, so a void tank at the top of
 * a stack acts as overflow protection rather than emptying the tanks below it.
 */
public final class VoidTank {

    private VoidTank() {}

    /** Maximum millibuckets destroyed per tick. */
    public static final long RATE = 20;

    /** How much this tank destroys this tick, given its own current contents. Never negative. */
    public static long drainPerTick(long current) {
        return FluidColumn.drainable(current, RATE);
    }
}
