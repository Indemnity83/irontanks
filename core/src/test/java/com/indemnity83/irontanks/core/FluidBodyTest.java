package com.indemnity83.irontanks.core;

import static org.assertj.core.api.Assertions.assertThat;

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
        assertThat(gas(CAPACITY, false, false)).isEqualTo(new FluidBody(0.0F, 1.0F, false, true));
    }

    @Test
    void anOverfullTankIsClampedToTheBlock() {
        assertThat(liquid(CAPACITY * 2, false, false).top()).isEqualTo(FluidBody.CEILING);
        assertThat(gas(CAPACITY * 2, false, false).bottom()).isEqualTo(FluidBody.FLOOR);
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
