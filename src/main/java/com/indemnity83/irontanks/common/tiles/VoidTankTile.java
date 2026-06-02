package com.indemnity83.irontanks.common.tiles;

/**
 * Destroys the fluid it holds, a little each tick. The drain is limited to {@link #VOID_RATE} mB/tick
 * to match BuildCraft's void pipe so it can't instantly annihilate a connected network.
 */
public class VoidTankTile extends TankTile {

    // Matches BuildCraft's default fluid pipe transfer rate (PipeApi.fluidInfoDefault = 20 mB/tick),
    // which is what the void pipe uses. Kept as a constant so we don't depend on BuildCraft at runtime.
    private static final int VOID_RATE = 20;

    @Override
    public void update() {
        // Drain only *this* tank, never the whole column, so a Void tank placed at the top of a stack
        // acts as overflow protection rather than emptying the tanks below it.
        if (world != null && !world.isRemote && getFluidAmount() > 0) {
            drainSelf(Math.min(getFluidAmount(), VOID_RATE), true);
        }
        super.update();
    }
}
