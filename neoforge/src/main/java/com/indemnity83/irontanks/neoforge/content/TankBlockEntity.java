package com.indemnity83.irontanks.neoforge.content;

import com.indemnity83.irontanks.core.TankCell;
import com.indemnity83.irontanks.core.TankColumn;
import com.indemnity83.irontanks.core.TankTier;
import com.indemnity83.irontanks.core.VoidTank;
import com.indemnity83.irontanks.neoforge.compat.LogisticsTanks;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Deque;
import java.util.List;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;
import net.neoforged.neoforge.transfer.fluid.FluidResource;
import org.jetbrains.annotations.Nullable;

/**
 * Stores a single fluid amount for one tank. Every connected tank in a vertical column holds the same
 * fluid, so a column's contents are a single total redistributed by {@link TankColumn}: liquids settle
 * to the bottom, gases rise. Void tanks destroy their own contents each tick; creative tanks never join
 * a column. All distribution math lives in {@code core}; this class is just the Minecraft wiring.
 */
public class TankBlockEntity extends BlockEntity implements TankCell<FluidResource> {

    private FluidResource fluid = FluidResource.EMPTY;
    private long amount;

    public TankBlockEntity(BlockPos pos, BlockState state) {
        super(IronTanksContent.TANK_BLOCK_ENTITY, pos, state);
    }

    public TankTier tier() {
        return getBlockState().getBlock() instanceof TankBlock tank ? tank.tier() : TankTier.GLASS;
    }

    public long capacity() {
        return tier().capacity();
    }

    public FluidResource fluidResource() {
        return fluid;
    }

    public long amount() {
        return amount;
    }

    /** Sets contents without side effects; used inside transactions where a revert is possible. */
    public void setContentsRaw(FluidResource fluid, long amount) {
        this.amount = Math.max(0, amount);
        this.fluid = this.amount == 0 ? FluidResource.EMPTY : fluid;
    }

    // ---- TankCell<FluidResource>: the core seam (tier/capacity/amount above already satisfy it) ----

    @Override
    public FluidResource fluid() {
        return fluid;
    }

    @Override
    public void setContents(FluidResource fluid, long amount) {
        setContentsRaw(fluid, amount);
    }

    /** This tank's column as the loader-agnostic {@link TankColumn} the fluid algorithm runs on. */
    public TankColumn<FluidResource> asColumn() {
        return new TankColumn<>(this, columnTanks(), FluidResourceKind.INSTANCE);
    }

    /** Persist + push this tile to clients (no rebalance). */
    public void sync() {
        setChanged();
        if (level != null && !level.isClientSide()) {
            level.sendBlockUpdated(worldPosition, getBlockState(), getBlockState(), Block.UPDATE_ALL);
        }
    }

    /** Persist + sync after an external content change, then rebalance the column. */
    public void onContentsChanged() {
        sync();
        if (LogisticsTanks.active()) {
            // Settle the whole (possibly cross-mod) column through the shared logistics engine.
            LogisticsTanks.get().rebalanceColumn(level, worldPosition);
        } else {
            balanceColumn();
        }
    }

    /** This tank's vertical column, ordered bottom-to-top (creative tanks stay isolated). */
    public List<TankBlockEntity> columnTanks() {
        return column();
    }

    /** Whether the connecting tank directly above currently holds fluid (used to merge fluid visually). */
    public boolean hasFluidAbove() {
        TankBlockEntity above = neighbour(this, Direction.UP);
        return above != null && connects(this, above) && above.amount() > 0;
    }

    // ==================== ticking ====================

    public static void serverTick(
            net.minecraft.world.level.Level level, BlockPos pos, BlockState state, TankBlockEntity be) {
        be.tick();
    }

    private void tick() {
        if (level == null || level.isClientSide()) {
            return;
        }
        boolean changed = false;
        TankTier tier = tier();
        if (tier == TankTier.VOID && amount > 0) {
            long destroyed = VoidTank.drainPerTick(amount);
            if (destroyed > 0) {
                setContentsRaw(fluid, amount - destroyed);
                changed = true;
            }
        } else if (tier == TankTier.CREATIVE && !fluid.isEmpty() && amount < capacity()) {
            setContentsRaw(fluid, capacity());
            changed = true;
        }
        if (LogisticsTanks.active()) {
            // Logistics elects one driver per (possibly cross-mod) column — its bottom cell — and settles
            // the whole stack; changed cells (foreign included) sync themselves. Every other cell no-ops,
            // so the two mods' tickers never fight over a shared column.
            if (LogisticsTanks.get().isColumnBottom(level, worldPosition)) {
                LogisticsTanks.get().rebalanceColumn(level, worldPosition);
            }
        } else if (balanceColumn()) {
            changed = true;
        }
        if (changed) {
            setChanged();
            if (!level.isClientSide()) {
                level.sendBlockUpdated(worldPosition, getBlockState(), getBlockState(), Block.UPDATE_ALL);
            }
        }
    }

    // ==================== column balancing (core math) ====================

    /** Redistributes the whole column so it settles (liquids down, gases up). Returns whether anything moved. */
    public boolean balanceColumn() {
        if (level == null || level.isClientSide()) {
            return false;
        }
        List<TankCell<FluidResource>> changed = asColumn().rebalance();
        for (TankCell<FluidResource> cell : changed) {
            ((TankBlockEntity) cell).sync();
        }
        return !changed.isEmpty();
    }

    /** This column, ordered bottom-to-top. Creative tanks never connect, so they stay isolated. */
    private List<TankBlockEntity> column() {
        Deque<TankBlockEntity> tanks = new ArrayDeque<>();
        tanks.add(this);
        TankBlockEntity current = this;
        while (true) {
            TankBlockEntity up = neighbour(current, Direction.UP);
            if (up == null || !connects(current, up)) {
                break;
            }
            tanks.addLast(up);
            current = up;
        }
        current = this;
        while (true) {
            TankBlockEntity down = neighbour(current, Direction.DOWN);
            if (down == null || !connects(current, down)) {
                break;
            }
            tanks.addFirst(down);
            current = down;
        }
        return new ArrayList<>(tanks);
    }

    private static boolean connects(TankBlockEntity a, TankBlockEntity b) {
        return a.tier() != TankTier.CREATIVE && b.tier() != TankTier.CREATIVE;
    }

    @Nullable
    private TankBlockEntity neighbour(TankBlockEntity from, Direction direction) {
        if (level == null) {
            return null;
        }
        BlockEntity neighbour = level.getBlockEntity(from.worldPosition.relative(direction));
        return neighbour instanceof TankBlockEntity tank ? tank : null;
    }

    // ==================== persistence + client sync ====================

    @Override
    protected void saveAdditional(ValueOutput output) {
        super.saveAdditional(output);
        // Store millibuckets (the historical key, unchanged) plus the sub-mB droplet remainder, so old
        // v3.0.0 worlds (mB only) still load correctly and this stays a non-breaking, additive change.
        output.putLong("Amount", amount / TankTier.DROPLETS_PER_MB);
        long remainder = amount % TankTier.DROPLETS_PER_MB;
        if (remainder != 0) {
            output.putInt("Rem", (int) remainder);
        }
        if (!fluid.isEmpty()) {
            output.store("Fluid", FluidResource.CODEC, fluid);
        }
    }

    @Override
    protected void loadAdditional(ValueInput input) {
        super.loadAdditional(input);
        amount = input.getLongOr("Amount", 0L) * TankTier.DROPLETS_PER_MB + input.getIntOr("Rem", 0);
        fluid = input.read("Fluid", FluidResource.CODEC).orElse(FluidResource.EMPTY);
        if (amount <= 0) {
            fluid = FluidResource.EMPTY;
            amount = 0;
        }
    }

    @Override
    public CompoundTag getUpdateTag(HolderLookup.Provider registries) {
        return saveWithoutMetadata(registries);
    }

    @Nullable
    @Override
    public Packet<ClientGamePacketListener> getUpdatePacket() {
        return ClientboundBlockEntityDataPacket.create(this);
    }
}
