package com.indemnity83.irontanks.core;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.within;

import org.junit.jupiter.api.Test;

class FluidBodyTest {

    private static final long CAPACITY = 8 * TankTier.DROPLETS_PER_BUCKET;

    private static FluidBody liquid(long amount, boolean fluidAbove, boolean fluidBelow) {
        return FluidBody.of(amount, CAPACITY, false, fluidAbove, fluidBelow);
    }

    private static FluidBody gas(long amount, boolean fluidAbove, boolean fluidBelow) {
        return FluidBody.of(amount, CAPACITY, true, fluidAbove, fluidBelow);
    }

    @Test
    void anEmptyTankHasNothingToDraw() {
        assertThat(liquid(0, false, false)).isEqualTo(FluidBody.EMPTY);
        assertThat(gas(0, false, false)).isEqualTo(FluidBody.EMPTY);
        assertThat(FluidBody.of(CAPACITY, 0, false, false, false)).isEqualTo(FluidBody.EMPTY);
        assertThat(FluidBody.EMPTY.isEmpty()).isTrue();
    }

    @Test
    void aLiquidRestsOnTheFloorWithItsSurfaceOnTop() {
        FluidBody body = liquid(CAPACITY / 4, false, false);

        assertThat(body.bottom()).isEqualTo(FluidBody.FLOOR);
        assertThat(body.top()).isEqualTo(0.25F);
        assertThat(body.renderTop()).isTrue();
        assertThat(body.renderBottom()).isFalse();
    }

    @Test
    void aGasHangsFromTheCeilingWithItsSurfaceUnderneath() {
        FluidBody body = gas(CAPACITY / 4, false, false);

        assertThat(body.top()).isEqualTo(FluidBody.CEILING);
        assertThat(body.bottom()).isEqualTo(0.75F);
        assertThat(body.renderBottom()).isTrue();
        assertThat(body.renderTop()).isFalse();
    }

    @Test
    void aFullTankFillsTheWholeBlockEitherWay() {
        assertThat(liquid(CAPACITY, false, false)).isEqualTo(new FluidBody(0.0F, 1.0F, true, false));

        FluidBody full = gas(CAPACITY, false, false);
        assertThat(full.top()).isEqualTo(FluidBody.CEILING);
        assertThat(full.bottom()).isCloseTo(FluidBody.FLOOR, within(0.01F));
        assertThat(full.renderBottom()).isTrue();
        assertThat(full.renderTop()).isFalse();
    }

    @Test
    void anOverfullTankIsClampedToTheBlock() {
        assertThat(liquid(CAPACITY * 2, false, false).top()).isEqualTo(FluidBody.CEILING);
        assertThat(gas(CAPACITY * 2, false, false).bottom()).isCloseTo(FluidBody.FLOOR, within(0.01F));
    }

    @Test
    void aFullGasKeepsItsSurfaceOffTheFloorSoItCannotZFightTheTankBelow() {
        // A full gas hangs all the way down to the floor, which is exactly where a full liquid in the tank
        // below draws its own top surface (a mixed column, or a creative tank, which never merges). Two
        // coplanar translucent faces z-fight, so the gas's surface stops just short of the boundary.
        FluidBody gas = gas(CAPACITY, false, false);
        FluidBody liquidBelow = liquid(CAPACITY, false, false);

        assertThat(gas.renderBottom()).isTrue();
        assertThat(liquidBelow.renderTop()).isTrue();
        // liquidBelow's surface sits at the top of its own block, i.e. the floor of the gas's block.
        assertThat(liquidBelow.top()).isEqualTo(FluidBody.CEILING);
        assertThat(gas.bottom()).isGreaterThan(FluidBody.FLOOR);
    }

    @Test
    void aMergedGasStillMeetsItsNeighbourExactly() {
        // The gap is only for a free surface; a body that continues into the tank below must still reach
        // the block edge, or a full column would show a hairline seam at every boundary.
        assertThat(gas(CAPACITY, false, true).bottom()).isEqualTo(FluidBody.FLOOR);
        assertThat(gas(CAPACITY / 2, false, true).bottom()).isEqualTo(FluidBody.FLOOR);
    }

    @Test
    void aLiquidMergesUpwardIntoTheTankAbove() {
        FluidBody body = liquid(CAPACITY / 2, true, false);

        // Stretched to the block edge with no surface drawn, so the column reads as one body.
        assertThat(body.top()).isEqualTo(FluidBody.CEILING);
        assertThat(body.renderTop()).isFalse();
        assertThat(body.renderBottom()).isFalse();
    }

    @Test
    void aGasMergesDownwardIntoTheTankBelow() {
        FluidBody body = gas(CAPACITY / 2, false, true);

        assertThat(body.bottom()).isEqualTo(FluidBody.FLOOR);
        assertThat(body.renderBottom()).isFalse();
        assertThat(body.renderTop()).isFalse();
    }

    @Test
    void aGasIgnoresTheTankAboveAndALiquidTheTankBelow() {
        // The neighbour on the sealed end never moves the free surface.
        assertThat(gas(CAPACITY / 2, true, false)).isEqualTo(gas(CAPACITY / 2, false, false));
        assertThat(liquid(CAPACITY / 2, false, true)).isEqualTo(liquid(CAPACITY / 2, false, false));
    }

    @Test
    void aSettledGasColumnShowsItsSurfaceOnTheLowestFilledTank() {
        // Three tanks holding half the column: core settles a gas top-down, so the top tank is full, the
        // middle one is half full and the bottom one is empty — the free surface is in the middle tank.
        long[] capacities = {CAPACITY, CAPACITY, CAPACITY};
        long[] settled = FluidColumn.settle(capacities, capacities.length * CAPACITY / 2, true);

        // Bottom cell: empty. Middle cell: half full, and the free surface belongs to it.
        assertThat(FluidBody.of(settled[0], CAPACITY, true, true, false)).isEqualTo(FluidBody.EMPTY);
        FluidBody middle = FluidBody.of(settled[1], CAPACITY, true, true, false);
        assertThat(middle.bottom()).isEqualTo(0.5F);
        assertThat(middle.renderBottom()).isTrue();
        // Top cell: full, merged into the one below, no faces of its own.
        FluidBody top = FluidBody.of(settled[2], CAPACITY, true, false, true);
        assertThat(top).isEqualTo(new FluidBody(FluidBody.FLOOR, FluidBody.CEILING, false, false));
    }
}
