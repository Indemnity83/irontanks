package com.indemnity83.irontanks.core;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.util.ArrayList;
import java.util.List;
import org.junit.jupiter.api.Test;

/**
 * Exercises the column algorithm with tiny in-memory fakes, no Minecraft. A fluid is just an id plus
 * gas/potion flags; a cell is a mutable capacity + contents. {@code null} is the empty fluid.
 */
class TankColumnTest {

    private static final long MB = TankTier.DROPLETS_PER_MB; // 81 droplets
    private static final long BOTTLE = TankTier.DROPLETS_PER_BOTTLE; // 27_000 droplets

    private static final FakeFluid WATER = new FakeFluid("water", false, false);
    private static final FakeFluid LAVA = new FakeFluid("lava", false, false);
    private static final FakeFluid GAS = new FakeFluid("gas", true, false);
    private static final FakeFluid POTION = new FakeFluid("potion", false, true);

    private record FakeFluid(String id, boolean gas, boolean potion) {}

    private static final class FakeKind implements FluidKind<FakeFluid> {
        @Override
        public FakeFluid empty() {
            return null;
        }

        @Override
        public boolean isEmpty(FakeFluid fluid) {
            return fluid == null;
        }

        @Override
        public boolean isGas(FakeFluid fluid) {
            return fluid != null && fluid.gas();
        }

        @Override
        public boolean isPotion(FakeFluid fluid) {
            return fluid != null && fluid.potion();
        }
    }

    private static final class FakeCell implements TankCell<FakeFluid> {
        private final TankTier tier;
        private final long capacity;
        private FakeFluid fluid;
        private long amount;

        FakeCell(TankTier tier, long capacity, FakeFluid fluid, long amount) {
            this.tier = tier;
            this.capacity = capacity;
            this.fluid = fluid;
            this.amount = amount;
        }

        static FakeCell of(long capacity) {
            return new FakeCell(TankTier.GLASS, capacity, null, 0);
        }

        static FakeCell of(long capacity, FakeFluid fluid, long amount) {
            return new FakeCell(TankTier.GLASS, capacity, fluid, amount);
        }

        @Override
        public TankTier tier() {
            return tier;
        }

        @Override
        public long capacity() {
            return capacity;
        }

        @Override
        public long amount() {
            return amount;
        }

        @Override
        public FakeFluid fluid() {
            return fluid;
        }

        @Override
        public void setContents(FakeFluid fluid, long amount) {
            this.amount = Math.max(0, amount);
            this.fluid = this.amount == 0 ? null : fluid;
        }
    }

    /** Counts onMutate invocations so a test can assert "snapshot taken only when actually changing". */
    private static final class MutateCounter implements Runnable {
        int count;

        @Override
        public void run() {
            count++;
        }
    }

    private static final MutateCounter IGNORE = new MutateCounter();

    private static TankColumn<FakeFluid> column(FakeCell... cells) {
        List<FakeCell> list = new ArrayList<>(List.of(cells));
        return new TankColumn<>(list.get(0), list, new FakeKind());
    }

    // ==================== aggregation ====================

    @Test
    void aggregatesTotalCapacityAndRoom() {
        TankColumn<FakeFluid> col = column(FakeCell.of(1000, WATER, 400), FakeCell.of(1000), FakeCell.of(500));
        assertThat(col.total()).isEqualTo(400);
        assertThat(col.capacity()).isEqualTo(2500);
        assertThat(col.room()).isEqualTo(2100);
        assertThat(col.shared()).isEqualTo(WATER);
        assertThat(col.mixed()).isFalse();
    }

    @Test
    void isPotionDelegatesToTheKind() {
        TankColumn<FakeFluid> col = column(FakeCell.of(1000));
        assertThat(col.isPotion(POTION)).isTrue();
        assertThat(col.isPotion(WATER)).isFalse();
    }

    @Test
    void detectsMixedColumns() {
        assertThat(column(FakeCell.of(1000, WATER, 400), FakeCell.of(1000, LAVA, 400))
                        .mixed())
                .isTrue();
        assertThat(column(FakeCell.of(1000, WATER, 400), FakeCell.of(1000, WATER, 400))
                        .mixed())
                .isFalse();
    }

    @Test
    void aCellHoldingNoFluidContributesNothingEvenIfItsAmountSurvived() {
        // A tank whose stored fluid no longer resolves (its mod was removed) can come back with a
        // positive amount and no fluid. That blank amount must never be aggregated: the next insert
        // redistributes total() as the inserted fluid, so counting it would mint real fluid from
        // nothing — 16 free buckets in a half-full iron tank. See issue #254.
        FakeCell phantom = new FakeCell(TankTier.GLASS, 1000, null, 800);
        TankColumn<FakeFluid> col = column(phantom);
        assertThat(col.total()).isZero();
        assertThat(col.room()).isEqualTo(1000);

        assertThat(col.insert(WATER, 200, 1, IGNORE)).isEqualTo(200);
        assertThat(phantom.fluid()).isEqualTo(WATER);
        assertThat(phantom.amount()).isEqualTo(200);
    }

    // ==================== insert ====================

    @Test
    void insertFillsEmptyColumnInDroplets() {
        FakeCell a = FakeCell.of(1000);
        FakeCell b = FakeCell.of(1000);
        long moved = column(a, b).insert(WATER, 1500, 1, IGNORE);
        assertThat(moved).isEqualTo(1500);
        // liquid settles to the bottom (origin/first cell), then the next
        assertThat(a.amount()).isEqualTo(1000);
        assertThat(a.fluid()).isEqualTo(WATER);
        assertThat(b.amount()).isEqualTo(500);
    }

    @Test
    void insertCapsAtRoom() {
        FakeCell a = FakeCell.of(1000, WATER, 800);
        assertThat(column(a).insert(WATER, 5000, 1, IGNORE)).isEqualTo(200);
        assertThat(a.amount()).isEqualTo(1000);
    }

    @Test
    void insertFloorsToQuantumWhenRoomHasSubUnitRemainder() {
        // One bottle (333.33 mB) sits in a 1000 mB tank; room is 666.66 mB. A pipe inserting whole
        // millibuckets must take only 666 mB and leave the fractional remainder, never overfilling.
        FakeCell a = FakeCell.of(1000 * MB, WATER, BOTTLE);
        long moved = column(a).insert(WATER, 1000 * MB, MB, IGNORE);
        assertThat(moved).isEqualTo(666 * MB);
        assertThat(a.amount()).isEqualTo(BOTTLE + 666 * MB);
        assertThat(a.amount()).isLessThan(a.capacity()); // 54-droplet remainder still free
    }

    @Test
    void insertGasSettlesToTop() {
        FakeCell a = FakeCell.of(1000);
        FakeCell b = FakeCell.of(1000);
        column(a, b).insert(GAS, 1500, 1, IGNORE);
        assertThat(b.amount()).isEqualTo(1000); // top fills first for gas
        assertThat(a.amount()).isEqualTo(500);
    }

    @Test
    void insertRejectsMixedDifferentFluidAndPotions() {
        assertThat(column(FakeCell.of(1000, WATER, 400), FakeCell.of(1000, LAVA, 400))
                        .insert(WATER, 100, 1, IGNORE))
                .isZero();
        assertThat(column(FakeCell.of(1000, WATER, 400)).insert(LAVA, 100, 1, IGNORE))
                .isZero();
        assertThat(column(FakeCell.of(1000)).insert(POTION, 100, 1, IGNORE)).isZero();
        assertThat(column(FakeCell.of(1000, POTION, 400)).insert(WATER, 100, 1, IGNORE))
                .isZero();
    }

    @Test
    void insertIgnoresBlankAndNonPositive() {
        assertThat(column(FakeCell.of(1000)).insert(null, 100, 1, IGNORE)).isZero();
        assertThat(column(FakeCell.of(1000)).insert(WATER, 0, 1, IGNORE)).isZero();
    }

    @Test
    void insertAndExtractRejectNonPositiveQuantum() {
        assertThatThrownBy(() -> column(FakeCell.of(1000)).insert(WATER, 100, 0, IGNORE))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("quantum");
        assertThatThrownBy(() -> column(FakeCell.of(1000, WATER, 400)).extract(WATER, 100, -1, IGNORE))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("quantum");
    }

    @Test
    void insertIntoFullColumnMovesNothing() {
        FakeCell a = FakeCell.of(1000, WATER, 1000);
        assertThat(column(a).insert(WATER, 500, 1, IGNORE)).isZero();
        assertThat(a.amount()).isEqualTo(1000);
    }

    @Test
    void creativeInsertDefinesSourceAndStaysFull() {
        FakeCell creative = new FakeCell(TankTier.CREATIVE, 1000, null, 0);
        long moved = column(creative).insert(WATER, 50 * MB, MB, IGNORE);
        assertThat(moved).isEqualTo(50 * MB); // reports the full request honored
        assertThat(creative.amount()).isEqualTo(1000); // and the tank is full
        assertThat(creative.fluid()).isEqualTo(WATER);
    }

    // ==================== extract ====================

    @Test
    void extractDrainsAndCapsAtContents() {
        FakeCell a = FakeCell.of(1000, WATER, 600);
        assertThat(column(a).extract(WATER, 5000, 1, IGNORE)).isEqualTo(600);
        assertThat(a.amount()).isZero();
        assertThat(a.fluid()).isNull();
    }

    @Test
    void extractFloorsToQuantum() {
        FakeCell a = FakeCell.of(1000 * MB, WATER, BOTTLE); // 333.33 mB available
        long moved = column(a).extract(WATER, 1000 * MB, MB, IGNORE);
        assertThat(moved).isEqualTo(333 * MB);
        assertThat(a.amount()).isEqualTo(BOTTLE - 333 * MB);
    }

    @Test
    void extractRejectsMismatchPotionAndEmpty() {
        assertThat(column(FakeCell.of(1000, WATER, 400)).extract(LAVA, 100, 1, IGNORE))
                .isZero();
        assertThat(column(FakeCell.of(1000, POTION, 400)).extract(POTION, 100, 1, IGNORE))
                .isZero();
        assertThat(column(FakeCell.of(1000)).extract(WATER, 100, 1, IGNORE)).isZero();
    }

    @Test
    void extractIgnoresBlankAndNonPositive() {
        assertThat(column(FakeCell.of(1000, WATER, 400)).extract(null, 100, 1, IGNORE))
                .isZero();
        assertThat(column(FakeCell.of(1000, WATER, 400)).extract(WATER, 0, 1, IGNORE))
                .isZero();
    }

    @Test
    void extractRejectsMixedColumn() {
        assertThat(column(FakeCell.of(1000, WATER, 400), FakeCell.of(1000, LAVA, 400))
                        .extract(WATER, 100, 1, IGNORE))
                .isZero();
    }

    @Test
    void extractMovesNothingBelowOneQuantum() {
        // Less than a whole millibucket is present; a pipe draining in mB can take nothing.
        FakeCell a = FakeCell.of(1000 * MB, WATER, 40);
        assertThat(column(a).extract(WATER, 1000 * MB, MB, IGNORE)).isZero();
        assertThat(a.amount()).isEqualTo(40);
    }

    @Test
    void creativeExtractIsEndless() {
        FakeCell creative = new FakeCell(TankTier.CREATIVE, 1000, WATER, 1000);
        assertThat(column(creative).extract(WATER, 50 * MB, MB, IGNORE)).isEqualTo(50 * MB);
        assertThat(creative.amount()).isEqualTo(1000); // never depletes
    }

    // ==================== bottles (droplet-exact) ====================

    @Test
    void depositBottleAddsExactlyOneBottle() {
        FakeCell a = FakeCell.of(1000 * MB);
        assertThat(column(a).depositBottle(WATER, IGNORE)).isTrue();
        assertThat(a.amount()).isEqualTo(BOTTLE);
        assertThat(a.fluid()).isEqualTo(WATER);
    }

    @Test
    void depositBottleTopsUpTheSameFluid() {
        FakeCell a = FakeCell.of(1000 * MB, WATER, BOTTLE);
        assertThat(column(a).depositBottle(WATER, IGNORE)).isTrue();
        assertThat(a.amount()).isEqualTo(2 * BOTTLE);
        assertThat(a.fluid()).isEqualTo(WATER);
    }

    @Test
    void depositBottleFailsWithoutRoom() {
        FakeCell a = FakeCell.of(BOTTLE - 1);
        assertThat(column(a).depositBottle(WATER, IGNORE)).isFalse();
        assertThat(a.amount()).isZero();
    }

    @Test
    void depositBottleRejectsMixedAndDifferentFluid() {
        assertThat(column(FakeCell.of(1000, WATER, 400)).depositBottle(LAVA, IGNORE))
                .isFalse();
        assertThat(column(FakeCell.of(1000, WATER, 400), FakeCell.of(1000, LAVA, 400))
                        .depositBottle(WATER, IGNORE))
                .isFalse();
    }

    @Test
    void extractBottleDrawsExactlyOneBottle() {
        FakeCell a = FakeCell.of(1000 * MB, WATER, BOTTLE);
        assertThat(column(a).extractBottle(WATER, IGNORE)).isTrue();
        assertThat(a.amount()).isZero();
    }

    @Test
    void extractBottleFailsBelowABottle() {
        FakeCell a = FakeCell.of(1000 * MB, WATER, BOTTLE - 1);
        assertThat(column(a).extractBottle(WATER, IGNORE)).isFalse();
        assertThat(a.amount()).isEqualTo(BOTTLE - 1);
    }

    @Test
    void depositBottleRejectsBlank() {
        assertThat(column(FakeCell.of(1000 * MB)).depositBottle(null, IGNORE)).isFalse();
    }

    @Test
    void extractBottleRejectsBlankMixedAndAbsent() {
        assertThat(column(FakeCell.of(1000 * MB, WATER, BOTTLE)).extractBottle(null, IGNORE))
                .isFalse();
        assertThat(column(FakeCell.of(1000, WATER, 400), FakeCell.of(1000, LAVA, 400))
                        .extractBottle(WATER, IGNORE))
                .isFalse();
        assertThat(column(FakeCell.of(1000 * MB)).extractBottle(WATER, IGNORE)).isFalse();
        assertThat(column(FakeCell.of(1000 * MB, WATER, BOTTLE)).extractBottle(LAVA, IGNORE))
                .isFalse();
    }

    @Test
    void creativeBottlesAreEndlessAndDefining() {
        FakeCell creative = new FakeCell(TankTier.CREATIVE, 1000, null, 0);
        assertThat(column(creative).depositBottle(WATER, IGNORE)).isTrue();
        assertThat(creative.amount()).isEqualTo(1000);
        assertThat(column(creative).extractBottle(WATER, IGNORE)).isTrue();
        assertThat(creative.amount()).isEqualTo(1000); // never depletes
    }

    // ==================== rebalance (settling path) ====================

    @Test
    void rebalanceSettlesLiquidDownAndReportsChangedCells() {
        // Fluid sitting in the top tank should fall to the bottom.
        FakeCell bottom = FakeCell.of(1000);
        FakeCell top = FakeCell.of(1000, WATER, 600);
        List<TankCell<FakeFluid>> changed = column(bottom, top).rebalance();
        assertThat(bottom.amount()).isEqualTo(600);
        assertThat(top.amount()).isZero();
        assertThat(changed).containsExactlyInAnyOrder(bottom, top);
    }

    @Test
    void rebalanceLeavesSettledColumnUnchanged() {
        FakeCell bottom = FakeCell.of(1000, WATER, 1000);
        FakeCell top = FakeCell.of(1000, WATER, 200);
        assertThat(column(bottom, top).rebalance()).isEmpty();
    }

    @Test
    void rebalanceClearsAGhostFluidWhoseAmountIsAlreadyZero() {
        // A cell that still names a fluid but holds nothing (amount 0) is inconsistent; rebalance
        // normalises it to empty even though its amount already matches the settled target.
        FakeCell ghost = new FakeCell(TankTier.GLASS, 1000, WATER, 0);
        List<TankCell<FakeFluid>> changed = column(ghost).rebalance();
        assertThat(ghost.amount()).isZero();
        assertThat(ghost.fluid()).isNull();
        assertThat(changed).containsExactly(ghost);
    }

    @Test
    void rebalanceLeavesMixedAndEmptyColumnsAlone() {
        assertThat(column(FakeCell.of(1000, WATER, 400), FakeCell.of(1000, LAVA, 400))
                        .rebalance())
                .isEmpty();
        assertThat(column(FakeCell.of(1000), FakeCell.of(1000)).rebalance()).isEmpty();
    }

    // ==================== onMutate hook ====================

    @Test
    void onMutateRunsOnlyWhenColumnActuallyChanges() {
        MutateCounter counter = new MutateCounter();
        // Rejected insert: no snapshot.
        column(FakeCell.of(1000, WATER, 400)).insert(LAVA, 100, 1, counter);
        assertThat(counter.count).isZero();
        // Accepted insert: snapshot taken once.
        column(FakeCell.of(1000)).insert(WATER, 100, 1, counter);
        assertThat(counter.count).isEqualTo(1);
    }
}
