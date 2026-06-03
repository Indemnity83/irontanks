package com.indemnity83.irontanks.core;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;

class TankUpgradeTest {

    @Test
    void everyUpgradeChangesTier() {
        for (TankUpgrade upgrade : TankUpgrade.values()) {
            assertThat(upgrade.from()).isNotEqualTo(upgrade.to());
        }
    }

    @Test
    void glassUpgradesToCopperAndIron() {
        assertThat(TankUpgrade.from(TankTier.GLASS))
                .extracting(TankUpgrade::to)
                .containsExactlyInAnyOrder(TankTier.COPPER, TankTier.IRON);
    }

    @Test
    void diamondBranchesToObsidianAndEmerald() {
        assertThat(TankUpgrade.from(TankTier.DIAMOND))
                .extracting(TankUpgrade::to)
                .containsExactlyInAnyOrder(TankTier.OBSIDIAN, TankTier.EMERALD);
    }

    @Test
    void nothingUpgradesIntoGlassAndTerminalTiersHaveNoUpgrades() {
        for (TankUpgrade upgrade : TankUpgrade.values()) {
            assertThat(upgrade.to()).isNotEqualTo(TankTier.GLASS);
        }
        // Obsidian and emerald are end states.
        assertThat(TankUpgrade.from(TankTier.OBSIDIAN)).isEmpty();
        assertThat(TankUpgrade.from(TankTier.EMERALD)).isEmpty();
    }
}
