package com.indemnity83.irontanks.neoforge.content;

import com.indemnity83.irontanks.core.TankTier;
import com.indemnity83.irontanks.core.TankUpgrade;
import net.minecraft.core.BlockPos;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.transfer.fluid.FluidResource;

/**
 * Promotes a placed tank to the next tier in place, preserving its contents. Right-clicking a tank of
 * the upgrade's source tier replaces it with the target tier (see {@link TankUpgrade}); anything else
 * passes through. Targets always have at least as much capacity, so no fluid is lost.
 */
public class UpgradeItem extends Item {

    private final TankUpgrade upgrade;

    public UpgradeItem(TankUpgrade upgrade, Properties properties) {
        super(properties);
        this.upgrade = upgrade;
    }

    @Override
    public InteractionResult useOn(UseOnContext context) {
        Level level = context.getLevel();
        BlockPos pos = context.getClickedPos();
        BlockState state = level.getBlockState(pos);
        if (!(state.getBlock() instanceof TankBlock tank) || tank.tier() != upgrade.from()) {
            return InteractionResult.PASS;
        }

        if (!level.isClientSide()) {
            Block target = IronTanksContent.TANK_BLOCKS.get(upgrade.to());

            FluidResource fluid = FluidResource.EMPTY;
            long amount = 0;
            if (level.getBlockEntity(pos) instanceof TankBlockEntity old) {
                fluid = old.fluidResource();
                amount = old.amount();
            }

            BlockState placed = target.defaultBlockState().setValue(TankBlock.JOINED_BELOW, joinsBelow(level, pos));
            level.setBlock(pos, placed, Block.UPDATE_ALL);

            if (level.getBlockEntity(pos) instanceof TankBlockEntity upgraded) {
                upgraded.setContentsRaw(fluid, Math.min(amount, upgraded.capacity()));
                upgraded.onContentsChanged();
            }

            if (context.getPlayer() == null || !context.getPlayer().getAbilities().instabuild) {
                context.getItemInHand().shrink(1);
            }
        }
        return InteractionResult.SUCCESS;
    }

    private static boolean joinsBelow(Level level, BlockPos pos) {
        return level.getBlockState(pos.below()).getBlock() instanceof TankBlock below
                && below.tier() != TankTier.CREATIVE;
    }
}
