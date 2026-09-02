package com.indemnity83.irontanks.core;

/**
 * Void-tank behavior: destroys the fluid it holds a little each tick. The per-tick limit matches
 * BuildCraft's default fluid-pipe transfer rate (20 mB/tick) so a void tank can't instantly
 * annihilate a connected network — kept as a constant here so there is no runtime BuildCraft tie.
 *
 * <p>This drains only the tank's own contents. A void tank never joins the shared fluid column
 * ({@link TankTier#joinsColumn()}): settling would top its cell back up from its neighbours every tick,
 * so a void tank anywhere in a stack would drain the whole stack at 20 mB/tick rather than only itself.
 *
 * <p>Because it is isolated, a stacked void tank is <b>not</b> passive overflow protection — a full
 * column above or below it does not spill into it. Fluid has to be pumped or poured into the void tank
 * directly for it to be destroyed, which is what the tank's tooltip has always promised.
 */
public final class VoidTank {

    private VoidTank() {}

    /** Maximum fluid destroyed per tick, in droplets (20 mB/tick — BuildCraft's default pipe rate). */
    public static final long RATE = 20L * TankTier.DROPLETS_PER_MB; // 1620

    /** How much this tank destroys this tick, given its own current contents. Never negative. */
    public static long drainPerTick(long current) {
        return FluidColumn.drainable(current, RATE);
    }
}
