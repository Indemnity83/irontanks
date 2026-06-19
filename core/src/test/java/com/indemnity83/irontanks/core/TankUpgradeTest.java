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
    void diamondBranchesToObsidianEmeraldAndAluminium() {
        assertThat(TankUpgrade.from(TankTier.DIAMOND))
                .extracting(TankUpgrade::to)
                .containsExactlyInAnyOrder(TankTier.OBSIDIAN, TankTier.EMERALD, TankTier.ALUMINIUM);
    }

    @Test
    void emeraldAndAluminiumBothClimbToStainlessSteel() {
        assertThat(TankUpgrade.from(TankTier.EMERALD))
                .extracting(TankUpgrade::to)
                .containsExactly(TankTier.STAINLESSSTEEL);
        assertThat(TankUpgrade.from(TankTier.ALUMINIUM))
                .extracting(TankUpgrade::to)
                .containsExactly(TankTier.STAINLESSSTEEL);
    }

    @Test
    void highTiersChainStainlessToTitaniumToTungsten() {
        assertThat(TankUpgrade.from(TankTier.STAINLESSSTEEL))
                .extracting(TankUpgrade::to)
                .containsExactly(TankTier.TITANIUM);
        assertThat(TankUpgrade.from(TankTier.TITANIUM))
                .extracting(TankUpgrade::to)
                .containsExactly(TankTier.TUNGSTENSTEEL);
    }

    @Test
    void nothingUpgradesIntoGlassAndTerminalTiersHaveNoUpgrades() {
        for (TankUpgrade upgrade : TankUpgrade.values()) {
            assertThat(upgrade.to()).isNotEqualTo(TankTier.GLASS);
        }
        // Obsidian and tungsten steel are the end states.
        assertThat(TankUpgrade.from(TankTier.OBSIDIAN)).isEmpty();
        assertThat(TankUpgrade.from(TankTier.TUNGSTENSTEEL)).isEmpty();
    }
}
