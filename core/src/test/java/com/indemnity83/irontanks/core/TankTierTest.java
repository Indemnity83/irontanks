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
        assertThat(TankTier.ALUMINIUM.buckets()).isEqualTo(96);
        assertThat(TankTier.STAINLESSSTEEL.buckets()).isEqualTo(128);
        assertThat(TankTier.TITANIUM.buckets()).isEqualTo(256);
        assertThat(TankTier.TUNGSTENSTEEL.buckets()).isEqualTo(512);
        assertThat(TankTier.VOID.buckets()).isEqualTo(8);
        assertThat(TankTier.CREATIVE.buckets()).isEqualTo(1);
    }

    @Test
    void capacityConvertsBucketsToDroplets() {
        assertThat(TankTier.GLASS.capacity()).isEqualTo(16L * 81_000); // 1_296_000
        assertThat(TankTier.EMERALD.capacity()).isEqualTo(96L * 81_000);
        assertThat(TankTier.DROPLETS_PER_BUCKET).isEqualTo(81_000);
        assertThat(TankTier.DROPLETS_PER_MB).isEqualTo(81);
    }

    @Test
    void bottleIsExactlyOneThirdOfABucket() {
        assertThat(TankTier.DROPLETS_PER_BOTTLE).isEqualTo(27_000);
        // Three bottles fill a bucket exactly — no rounding remainder in droplets.
        assertThat(3 * TankTier.DROPLETS_PER_BOTTLE).isEqualTo(TankTier.DROPLETS_PER_BUCKET);
    }
}
