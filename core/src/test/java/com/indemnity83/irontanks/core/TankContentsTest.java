package com.indemnity83.irontanks.core;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;

/**
 * The rule every raw write into a tank funnels through. A fluid is just an id here; {@code null} is the
 * empty fluid, matching the fake kind used by {@link TankColumnTest}.
 */
class TankContentsTest {

    private record FakeFluid(String id) {}

    private static final FakeFluid WATER = new FakeFluid("water");

    private static final FluidKind<FakeFluid> KIND = new FluidKind<>() {
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
            return false;
        }

        @Override
        public boolean isPotion(FakeFluid fluid) {
            return false;
        }
    };

    @Test
    void saturatesAWriteBiggerThanTheTank() {
        // A tank can be handed more than it can hold: every tank block shares one BlockEntityType, so
        // /setblock, WorldEdit, a structure block or an NBT edit swap a smaller tank in under the fluid
        // and the block entity survives with its old contents. It must come to rest at capacity rather
        // than carry an impossible amount into the settling maths (which rejects it) or onto a client.
        assertThat(TankContents.storedAmount(KIND, WATER, 5000, 1000)).isEqualTo(1000);
    }

    @Test
    void saturatesAWriteFromAnotherModsColumnEngine() {
        // The logistics integration settles a shared cross-mod column and writes each cell's share
        // straight into the block entity, so it never reaches the clamp inside TankColumn. A foreign
        // engine handing a 16-bucket glass tank the 512 buckets of the tungstensteel tank it replaced
        // has to be saturated here or the over-full state simply persists.
        long glassTank = 16 * TankTier.DROPLETS_PER_BUCKET;
        long tungstensteelLoad = 512 * TankTier.DROPLETS_PER_BUCKET;
        assertThat(TankContents.storedAmount(KIND, WATER, tungstensteelLoad, glassTank))
                .isEqualTo(glassTank);
    }

    @Test
    void keepsAWriteThatFits() {
        assertThat(TankContents.storedAmount(KIND, WATER, 400, 1000)).isEqualTo(400);
        assertThat(TankContents.storedAmount(KIND, WATER, 1000, 1000)).isEqualTo(1000);
    }

    @Test
    void storesNothingForANegativeAmount() {
        assertThat(TankContents.storedAmount(KIND, WATER, -5, 1000)).isZero();
    }

    @Test
    void storesNothingWhenThereIsNoFluidToStore() {
        // The other half of the "amount > 0 iff a fluid is held" invariant: a write of a positive amount
        // with no fluid would leave a phantom volume the next insert could turn into real fluid.
        assertThat(TankContents.storedAmount(KIND, null, 800, 1000)).isZero();
    }

    @Test
    void storesNothingInATankWithNoRoom() {
        assertThat(TankContents.storedAmount(KIND, WATER, 800, 0)).isZero();
    }
}
