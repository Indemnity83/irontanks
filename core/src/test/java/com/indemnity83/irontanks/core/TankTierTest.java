package com.indemnity83.irontanks.core;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;

class TankTierTest {

    @Test
    void capacitiesMatchTheTierTable() {
        // Locks in the bucket capacities ported from the 1.12 registration.
        assertThat(TankTier.GLASS.buckets()).isEqualTo(16);
        assertThat(TankTier.COPPER.buckets()).isEqualTo(27);
        assertThat(TankTier.IRON.buckets()).isEqualTo(32);
        assertThat(TankTier.SILVER.buckets()).isEqualTo(43);
        assertThat(TankTier.GOLD.buckets()).isEqualTo(48);
        assertThat(TankTier.DIAMOND.buckets()).isEqualTo(64);
        assertThat(TankTier.OBSIDIAN.buckets()).isEqualTo(64);
        assertThat(TankTier.EMERALD.buckets()).isEqualTo(96);
        assertThat(TankTier.VOID.buckets()).isEqualTo(8);
        assertThat(TankTier.CREATIVE.buckets()).isEqualTo(1);
    }

    @Test
    void capacityConvertsBucketsToMillibuckets() {
        assertThat(TankTier.GLASS.capacity()).isEqualTo(16_000L);
        assertThat(TankTier.EMERALD.capacity()).isEqualTo(96_000L);
        assertThat(TankTier.BUCKET_VOLUME).isEqualTo(1000);
    }
}
