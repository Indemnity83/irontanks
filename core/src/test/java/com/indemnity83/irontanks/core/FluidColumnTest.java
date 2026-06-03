package com.indemnity83.irontanks.core;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import org.junit.jupiter.api.Test;

class FluidColumnTest {

    // A three-tank column, bottom-to-top, each holding 1000 mB capacity.
    private static final long[] COLUMN = {1000, 1000, 1000};

    @Test
    void totalCapacitySumsTanks() {
        assertThat(FluidColumn.totalCapacity(COLUMN)).isEqualTo(3000);
        assertThat(FluidColumn.totalCapacity(new long[0])).isZero();
    }

    @Test
    void liquidSettlesToTheBottom() {
        // 1500 mB of liquid fills the bottom tank, then half-fills the middle, leaving the top empty.
        assertThat(FluidColumn.settle(COLUMN, 1500, false)).containsExactly(1000, 500, 0);
    }

    @Test
    void gasRisesToTheTop() {
        // Same 1500 mB of gas fills the top tank first, then the middle.
        assertThat(FluidColumn.settle(COLUMN, 1500, true)).containsExactly(0, 500, 1000);
    }

    @Test
    void settleHandlesEmptyAndFull() {
        assertThat(FluidColumn.settle(COLUMN, 0, false)).containsExactly(0, 0, 0);
        assertThat(FluidColumn.settle(COLUMN, 3000, false)).containsExactly(1000, 1000, 1000);
        assertThat(FluidColumn.settle(COLUMN, 3000, true)).containsExactly(1000, 1000, 1000);
    }

    @Test
    void settleRespectsUnequalCapacities() {
        long[] mixed = {500, 2000, 1000}; // bottom-to-top
        assertThat(FluidColumn.settle(mixed, 2200, false)).containsExactly(500, 1700, 0);
        assertThat(FluidColumn.settle(mixed, 2200, true)).containsExactly(0, 1200, 1000);
    }

    @Test
    void settleRejectsNegativeOrOverfull() {
        assertThatThrownBy(() -> FluidColumn.settle(COLUMN, -1, false))
                .isInstanceOf(IllegalArgumentException.class);
        assertThatThrownBy(() -> FluidColumn.settle(COLUMN, 3001, false))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void fillableCapsAtRemainingRoom() {
        assertThat(FluidColumn.fillable(3000, 1000, 500)).isEqualTo(500);   // fits
        assertThat(FluidColumn.fillable(3000, 2800, 500)).isEqualTo(200);   // clipped to room
        assertThat(FluidColumn.fillable(3000, 3000, 500)).isZero();         // full
        assertThat(FluidColumn.fillable(3000, 1000, 0)).isZero();           // nothing requested
        assertThat(FluidColumn.fillable(3000, 1000, -5)).isZero();          // negative request
    }

    @Test
    void drainableCapsAtCurrentContents() {
        assertThat(FluidColumn.drainable(1500, 500)).isEqualTo(500);   // fits
        assertThat(FluidColumn.drainable(300, 500)).isEqualTo(300);    // clipped to contents
        assertThat(FluidColumn.drainable(0, 500)).isZero();            // empty
        assertThat(FluidColumn.drainable(1500, 0)).isZero();           // nothing requested
    }
}
