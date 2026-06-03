package com.indemnity83.irontanks.core;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;

class VoidTankTest {

    @Test
    void destroysAtMostTheRatePerTick() {
        assertThat(VoidTank.RATE).isEqualTo(20);
        assertThat(VoidTank.drainPerTick(1000)).isEqualTo(20); // plenty held -> full rate
        assertThat(VoidTank.drainPerTick(5)).isEqualTo(5); // less than rate -> only what's there
        assertThat(VoidTank.drainPerTick(0)).isZero(); // empty -> nothing
    }
}
