package com.indemnity83.irontanks.core;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;

class VoidTankTest {

    @Test
    void destroysAtMostTheRatePerTick() {
        assertThat(VoidTank.RATE).isEqualTo(1620); // 20 mB/tick in droplets
        assertThat(VoidTank.drainPerTick(81_000)).isEqualTo(1620); // plenty held -> full rate
        assertThat(VoidTank.drainPerTick(500)).isEqualTo(500); // less than rate -> only what's there
        assertThat(VoidTank.drainPerTick(0)).isZero(); // empty -> nothing
    }
}
