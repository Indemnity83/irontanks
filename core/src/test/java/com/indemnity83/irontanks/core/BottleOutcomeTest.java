package com.indemnity83.irontanks.core;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;

/**
 * Guards the rule behind issue #258: a bottle interaction the tank <em>refused</em> must be
 * distinguishable from one the tank never claimed, so the loaders can block the item's own use
 * behavior (drinking) instead of falling through to it.
 */
class BottleOutcomeTest {

    @Test
    void depositedPotionConsumesTheInteraction() {
        assertThat(BottleOutcome.deposit(true, true)).isEqualTo(BottleOutcome.TRANSFERRED);
    }

    @Test
    void refusedPotionIsNotAFallThrough() {
        // A full or wrong-fluid tank turning a real potion away must not let the player drink it.
        assertThat(BottleOutcome.deposit(true, false)).isEqualTo(BottleOutcome.REFUSED);
    }

    @Test
    void potionWithoutContentsIsLeftToTheGame() {
        assertThat(BottleOutcome.deposit(false, false)).isEqualTo(BottleOutcome.NOT_HANDLED);
        assertThat(BottleOutcome.deposit(false, true)).isEqualTo(BottleOutcome.NOT_HANDLED);
    }

    @Test
    void drawnBottleConsumesTheInteraction() {
        assertThat(BottleOutcome.draw(true)).isEqualTo(BottleOutcome.TRANSFERRED);
    }

    @Test
    void emptyBottleAgainstANonWaterTankIsRefusedNotHandedOff() {
        assertThat(BottleOutcome.draw(false)).isEqualTo(BottleOutcome.REFUSED);
    }
}
