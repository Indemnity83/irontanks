package com.indemnity83.irontanks.core;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import org.junit.jupiter.api.Test;

class FluidColumnTest {

    // A three-tank column, bottom-to-top, each of capacity 1000. The settle/fillable/drainable math is
    // pure arithmetic, so these cases use round numbers rather than real capacities — but note that in
    // production every amount crossing FluidColumn is in DROPLETS, never millibuckets (see TankTier).
    // The droplet-exactness of bucket/bottle mixing is covered by the last test in this class.
    private static final long[] COLUMN = {1000, 1000, 1000};

    @Test
    void totalCapacitySumsTanks() {
        assertThat(FluidColumn.totalCapacity(COLUMN)).isEqualTo(3000);
        assertThat(FluidColumn.totalCapacity(new long[0])).isZero();
    }

    @Test
    void liquidSettlesToTheBottom() {
        // 1500 of liquid fills the bottom tank, then half-fills the middle, leaving the top empty.
        assertThat(FluidColumn.settle(COLUMN, 1500, false)).containsExactly(1000, 500, 0);
    }

    @Test
    void gasRisesToTheTop() {
        // The same 1500 of gas fills the top tank first, then the middle.
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
        assertThatThrownBy(() -> FluidColumn.settle(COLUMN, -1, false)).isInstanceOf(IllegalArgumentException.class);
        assertThatThrownBy(() -> FluidColumn.settle(COLUMN, 3001, false)).isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void fillableCapsAtRemainingRoom() {
        assertThat(FluidColumn.fillable(3000, 1000, 500)).isEqualTo(500); // fits
        assertThat(FluidColumn.fillable(3000, 2800, 500)).isEqualTo(200); // clipped to room
        assertThat(FluidColumn.fillable(3000, 3000, 500)).isZero(); // full
        assertThat(FluidColumn.fillable(3000, 1000, 0)).isZero(); // nothing requested
        assertThat(FluidColumn.fillable(3000, 1000, -5)).isZero(); // negative request
    }

    @Test
    void drainableCapsAtCurrentContents() {
        assertThat(FluidColumn.drainable(1500, 500)).isEqualTo(500); // fits
        assertThat(FluidColumn.drainable(300, 500)).isEqualTo(300); // clipped to contents
        assertThat(FluidColumn.drainable(0, 500)).isZero(); // empty
        assertThat(FluidColumn.drainable(1500, 0)).isZero(); // nothing requested
    }

    @Test
    void bucketAndBottleTransfersAreExactAndReversibleInDroplets() {
        // Tracking fluid in droplets makes bottles (⅓ bucket) exact integers, so mixing buckets, bottles
        // and arbitrary pipe transfers never drifts. Walks the edge case: bucket + 3 bottles, a 200 mB
        // pipe pull, then two bottles out — deterministic to the droplet, and reversible.
        long bucket = TankTier.DROPLETS_PER_BUCKET; // 81_000
        long bottle = TankTier.DROPLETS_PER_BOTTLE; // 27_000
        long pipe200mb = 200L * TankTier.DROPLETS_PER_MB; // 16_200
        long cap = TankTier.GLASS.capacity();

        long total = 0;
        total += FluidColumn.fillable(cap, total, bucket); // +bucket
        for (int i = 0; i < 3; i++) total += FluidColumn.fillable(cap, total, bottle); // +3 bottles
        assertThat(total).isEqualTo(2000L * TankTier.DROPLETS_PER_MB); // exactly 2000 mB

        total -= FluidColumn.drainable(total, pipe200mb); // pipe pulls 200 mB
        total -= FluidColumn.drainable(total, bottle); // two bottles out
        total -= FluidColumn.drainable(total, bottle);
        assertThat(total).isEqualTo(91_800); // 1133⅓ mB, exact
        assertThat(total / TankTier.DROPLETS_PER_MB).isEqualTo(1133); // displayed mB
        assertThat(total % TankTier.DROPLETS_PER_MB).isEqualTo(27); // ⅓ mB carried as remainder

        // Reversible: the two bottles and the pipe amount put it back to exactly 2000 mB.
        total += FluidColumn.fillable(cap, total, bottle);
        total += FluidColumn.fillable(cap, total, bottle);
        total += FluidColumn.fillable(cap, total, pipe200mb);
        assertThat(total).isEqualTo(2000L * TankTier.DROPLETS_PER_MB);
    }
}
