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
    void hardnessAndBlastResistanceFollowTheTierTable() {
        // Tiers get tougher up the ladder; obsidian stays explosion-proof. Locks the whole table
        // so a regression in any single entry is caught.
        assertThat(TankTier.GLASS.hardness()).isEqualTo(0.3F);
        assertThat(TankTier.GLASS.blastResistance()).isEqualTo(0.3F);
        assertThat(TankTier.COPPER.hardness()).isEqualTo(4.0F);
        assertThat(TankTier.COPPER.blastResistance()).isEqualTo(2.0F);
        assertThat(TankTier.IRON.hardness()).isEqualTo(5.0F);
        assertThat(TankTier.IRON.blastResistance()).isEqualTo(3.0F);
        assertThat(TankTier.SILVER.hardness()).isEqualTo(6.0F);
        assertThat(TankTier.SILVER.blastResistance()).isEqualTo(5.0F);
        assertThat(TankTier.GOLD.hardness()).isEqualTo(7.0F);
        assertThat(TankTier.GOLD.blastResistance()).isEqualTo(4.0F);
        assertThat(TankTier.DIAMOND.hardness()).isEqualTo(8.0F);
        assertThat(TankTier.DIAMOND.blastResistance()).isEqualTo(6.0F);
        assertThat(TankTier.OBSIDIAN.hardness()).isEqualTo(50.0F);
        assertThat(TankTier.OBSIDIAN.blastResistance()).isEqualTo(1200.0F);
        assertThat(TankTier.EMERALD.hardness()).isEqualTo(8.0F);
        assertThat(TankTier.EMERALD.blastResistance()).isEqualTo(6.0F);
        assertThat(TankTier.ALUMINIUM.hardness()).isEqualTo(5.0F);
        assertThat(TankTier.ALUMINIUM.blastResistance()).isEqualTo(4.0F);
        assertThat(TankTier.STAINLESSSTEEL.hardness()).isEqualTo(9.0F);
        assertThat(TankTier.STAINLESSSTEEL.blastResistance()).isEqualTo(8.0F);
        assertThat(TankTier.TITANIUM.hardness()).isEqualTo(10.0F);
        assertThat(TankTier.TITANIUM.blastResistance()).isEqualTo(10.0F);
        assertThat(TankTier.TUNGSTENSTEEL.hardness()).isEqualTo(12.0F);
        assertThat(TankTier.TUNGSTENSTEEL.blastResistance()).isEqualTo(14.0F);
        assertThat(TankTier.VOID.hardness()).isEqualTo(5.0F);
        assertThat(TankTier.VOID.blastResistance()).isEqualTo(6.0F);
        assertThat(TankTier.CREATIVE.hardness()).isEqualTo(5.0F);
        assertThat(TankTier.CREATIVE.blastResistance()).isEqualTo(6.0F);
    }

    @Test
    void bottleIsExactlyOneThirdOfABucket() {
        assertThat(TankTier.DROPLETS_PER_BOTTLE).isEqualTo(27_000);
        // Three bottles fill a bucket exactly — no rounding remainder in droplets.
        assertThat(3 * TankTier.DROPLETS_PER_BOTTLE).isEqualTo(TankTier.DROPLETS_PER_BUCKET);
    }

    @Test
    void onlyOrdinaryTiersJoinTheSharedColumn() {
        // Void and creative tanks stay isolated single-cell columns. A void tank that joined the column
        // would be refilled from its neighbours every tick and annihilate the whole stack (#255);
        // a creative tank would feed an endless source into a shared body.
        assertThat(TankTier.VOID.joinsColumn()).isFalse();
        assertThat(TankTier.CREATIVE.joinsColumn()).isFalse();
        for (TankTier tier : TankTier.values()) {
            if (tier != TankTier.VOID && tier != TankTier.CREATIVE) {
                assertThat(tier.joinsColumn()).as("%s joins the column", tier).isTrue();
            }
        }
    }
}
