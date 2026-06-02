package com.indemnity83.irontanks.common.tiles;

import buildcraft.api.core.IFluidFilter;
import buildcraft.api.core.IFluidHandlerAdv;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.play.server.SPacketUpdateTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.ITickable;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.Explosion;
import net.minecraft.world.World;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.FluidTank;
import net.minecraftforge.fluids.FluidUtil;
import net.minecraftforge.fluids.capability.CapabilityFluidHandler;
import net.minecraftforge.fluids.capability.FluidTankProperties;
import net.minecraftforge.fluids.capability.IFluidTankProperties;

import javax.annotation.Nullable;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Deque;
import java.util.List;

/**
 * A self-contained tank tile that exposes Forge's native fluid handler capability.
 * <p>
 * This deliberately does NOT extend BuildCraft's TileTank: a class that extends a type from an
 * optional mod cannot load when that mod is absent. BuildCraft fluid pipes connect purely through
 * the standard Forge {@link CapabilityFluidHandler#FLUID_HANDLER_CAPABILITY}, so exposing it here
 * keeps full BuildCraft-pipe compatibility with no hard dependency. The vertical "tank column"
 * merging that BuildCraft's TileTank provided is reimplemented here over our own tile type, gated
 * by {@link #canConnectTo} so special tanks (e.g. the Creative tank) can refuse to join a stack —
 * which makes that behavior identical on every Minecraft version.
 */
public class TankTile extends TileEntity implements ITickable, IFluidHandlerAdv {

    protected final FluidTank tank = new FluidTank(16 * Fluid.BUCKET_VOLUME);

    // Server-side snapshot used to detect content changes and push a render update to clients.
    private FluidStack lastSent;

    public TankTile withCapacity(int capacity) {
        this.tank.setCapacity(capacity * Fluid.BUCKET_VOLUME);
        return this;
    }

    // Accessors used by the renderer and the upgrade item.

    @Nullable
    public FluidStack getFluid() {
        return tank.getFluid();
    }

    public int getFluidAmount() {
        return tank.getFluidAmount();
    }

    public int getCapacity() {
        return tank.getCapacity();
    }

    public void setFluid(@Nullable FluidStack fluid) {
        tank.setFluid(fluid);
    }

    /** Drains and returns everything in just this tile's tank (no column traversal); used when upgrading a tank. */
    public FluidStack drainEntireTank() {
        return tank.drain(tank.getFluidAmount(), true);
    }

    // ITickable

    @Override
    public void update() {
        if (world == null || world.isRemote) {
            return;
        }
        FluidStack current = tank.getFluid();
        if (!fluidsEqual(current, lastSent)) {
            lastSent = current == null ? null : current.copy();
            sync();
        }
    }

    private static boolean fluidsEqual(@Nullable FluidStack a, @Nullable FluidStack b) {
        if (a == null && b == null) return true;
        if (a == null || b == null) return false;
        return a.isFluidEqual(b) && a.amount == b.amount;
    }

    /** Re-send this tile to nearby clients so the renderer reflects the new contents. */
    protected void sync() {
        markDirty();
        if (world != null && !world.isRemote) {
            IBlockState state = world.getBlockState(pos);
            world.notifyBlockUpdate(pos, state, state, 3);
        }
    }

    // TileEntity / NBT

    @Override
    public void readFromNBT(NBTTagCompound compound) {
        super.readFromNBT(compound);
        if (compound.hasKey("Capacity")) {
            tank.setCapacity(compound.getInteger("Capacity"));
        }
        if (compound.hasKey("tank")) {
            tank.readFromNBT(compound.getCompoundTag("tank"));
        }
    }

    @Override
    public NBTTagCompound writeToNBT(NBTTagCompound compound) {
        super.writeToNBT(compound);
        compound.setInteger("Capacity", tank.getCapacity());
        compound.setTag("tank", tank.writeToNBT(new NBTTagCompound()));
        return compound;
    }

    // Client sync via the vanilla tile-update packet.

    @Override
    public NBTTagCompound getUpdateTag() {
        return writeToNBT(new NBTTagCompound());
    }

    @Nullable
    @Override
    public SPacketUpdateTileEntity getUpdatePacket() {
        return new SPacketUpdateTileEntity(pos, 0, getUpdateTag());
    }

    @Override
    public void onDataPacket(NetworkManager net, SPacketUpdateTileEntity pkt) {
        readFromNBT(pkt.getNbtCompound());
        if (world != null && world.isRemote) {
            world.markBlockRangeForRenderUpdate(pos, pos);
        }
    }

    @Override
    public boolean shouldRefresh(World world, BlockPos pos, IBlockState oldState, IBlockState newState) {
        return oldState.getBlock() != newState.getBlock();
    }

    // Forge fluid-handler capability — this is the entire pipe-interop story.

    @Override
    public boolean hasCapability(Capability<?> capability, @Nullable EnumFacing facing) {
        return capability == CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY || super.hasCapability(capability, facing);
    }

    @Override
    @SuppressWarnings("unchecked")
    public <T> T getCapability(Capability<T> capability, @Nullable EnumFacing facing) {
        if (capability == CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY) {
            return (T) this;
        }
        return super.getCapability(capability, facing);
    }

    // Block hooks (invoked from TankBlock).

    public boolean onActivated(EntityPlayer player, EnumHand hand, EnumFacing facing, float hitX, float hitY, float hitZ) {
        return FluidUtil.interactWithFluidHandler(player, hand, this);
    }

    public void onPlacedBy(EntityLivingBase placer, ItemStack stack) {
        if (world != null && !world.isRemote) {
            balanceTankFluids();
        }
    }

    /** Breaking or exploding a tank simply loses its fluid (tanks store no item inventory). */
    public void onExplode(Explosion explosion) {
    }

    public void onRemove() {
    }

    // Vertical tank-column handling (ported from BuildCraft's TileTank, keyed to our own type).

    /**
     * Whether this tank will join a column with {@code other} across {@code direction}. Overridden by special tanks
     * (e.g. Creative) to stay isolated. BuildCraft only ever queries UP/DOWN.
     */
    public boolean canConnectTo(TankTile other, EnumFacing direction) {
        return true;
    }

    private static boolean canTanksConnect(TankTile from, TankTile to, EnumFacing direction) {
        return from.canConnectTo(to, direction) && to.canConnectTo(from, direction.getOpposite());
    }

    @Nullable
    private TankTile getNeighbourTank(EnumFacing offset) {
        if (world == null) {
            return null;
        }
        TileEntity tile = world.getTileEntity(pos.offset(offset));
        return tile instanceof TankTile ? (TankTile) tile : null;
    }

    /** All connected tanks in this column, ordered bottom-to-top. */
    private List<TankTile> getTanks() {
        Deque<TankTile> tanks = new ArrayDeque<>();
        tanks.add(this);
        TankTile prev = this;
        while (true) {
            TankTile up = prev.getNeighbourTank(EnumFacing.UP);
            if (up == null || !canTanksConnect(prev, up, EnumFacing.UP)) {
                break;
            }
            tanks.addLast(up);
            prev = up;
        }
        prev = this;
        while (true) {
            TankTile down = prev.getNeighbourTank(EnumFacing.DOWN);
            if (down == null || !canTanksConnect(prev, down, EnumFacing.DOWN)) {
                break;
            }
            tanks.addFirst(down);
            prev = down;
        }
        return new ArrayList<>(tanks);
    }

    /** Moves fluid to its preferred position in the column (liquids settle down, gases rise). */
    public void balanceTankFluids() {
        List<TankTile> tanks = getTanks();
        FluidStack fluid = null;
        for (TankTile tile : tanks) {
            FluidStack held = tile.tank.getFluid();
            if (held == null) {
                continue;
            }
            if (fluid == null) {
                fluid = held;
            } else if (!fluid.isFluidEqual(held)) {
                return;
            }
        }
        if (fluid == null) {
            return;
        }
        if (fluid.getFluid().isGaseous(fluid)) {
            Collections.reverse(tanks);
        }
        TankTile prev = null;
        for (TankTile tile : tanks) {
            if (prev != null) {
                int room = prev.tank.getCapacity() - prev.tank.getFluidAmount();
                if (room > 0) {
                    FluidStack moved = tile.tank.drain(room, true);
                    if (moved != null) {
                        prev.tank.fill(moved, true);
                    }
                }
            }
            prev = tile;
        }
    }

    // IFluidHandler

    @Override
    public IFluidTankProperties[] getTankProperties() {
        List<TankTile> tanks = getTanks();
        FluidStack total = tanks.get(0).tank.getFluid();
        int capacity = 0;
        if (total == null) {
            for (TankTile t : tanks) {
                capacity += t.tank.getCapacity();
            }
        } else {
            total = total.copy();
            total.amount = 0;
            for (TankTile t : tanks) {
                FluidStack other = t.tank.getFluid();
                if (other != null) {
                    total.amount += other.amount;
                }
                capacity += t.tank.getCapacity();
            }
        }
        return new IFluidTankProperties[] { new FluidTankProperties(total, capacity) };
    }

    @Override
    public int fill(FluidStack resource, boolean doFill) {
        if (resource == null || resource.amount <= 0) {
            return 0;
        }
        List<TankTile> tanks = getTanks();
        for (TankTile t : tanks) {
            FluidStack current = t.tank.getFluid();
            if (current != null && !current.isFluidEqual(resource)) {
                return 0;
            }
        }
        if (resource.getFluid().isGaseous(resource)) {
            Collections.reverse(tanks);
        }
        resource = resource.copy();
        int filled = 0;
        for (TankTile t : tanks) {
            int tankFilled = t.tank.fill(resource, doFill);
            if (tankFilled > 0) {
                resource.amount -= tankFilled;
                filled += tankFilled;
                if (resource.amount == 0) {
                    break;
                }
            }
        }
        return filled;
    }

    @Override
    public FluidStack drain(int maxDrain, boolean doDrain) {
        return drain((fluid) -> true, maxDrain, doDrain);
    }

    @Override
    public FluidStack drain(FluidStack resource, boolean doDrain) {
        if (resource == null) {
            return null;
        }
        return drain(resource::isFluidEqual, resource.amount, doDrain);
    }

    // IFluidHandlerAdv

    @Override
    public FluidStack drain(IFluidFilter filter, int maxDrain, boolean doDrain) {
        if (maxDrain <= 0) {
            return null;
        }
        List<TankTile> tanks = getTanks();
        boolean gas = false;
        for (TankTile tile : tanks) {
            FluidStack fluid = tile.tank.getFluid();
            if (fluid != null) {
                gas = fluid.getFluid().isGaseous(fluid);
                break;
            }
        }
        if (!gas) {
            Collections.reverse(tanks);
        }
        FluidStack total = null;
        for (TankTile t : tanks) {
            int realMax = maxDrain - (total == null ? 0 : total.amount);
            if (realMax <= 0) {
                break;
            }
            FluidStack drained = drainFiltered(t.tank, filter, realMax, doDrain);
            if (drained == null) {
                continue;
            }
            if (total == null) {
                total = drained.copy();
                total.amount = 0;
            }
            total.amount += drained.amount;
        }
        return total;
    }

    /** Drains only this tile's tank (no column traversal); used by the Void tank. */
    protected FluidStack drainSelf(int maxDrain, boolean doDrain) {
        return tank.drain(maxDrain, doDrain);
    }

    private static FluidStack drainFiltered(FluidTank tank, IFluidFilter filter, int maxDrain, boolean doDrain) {
        FluidStack available = tank.getFluid();
        if (available == null || available.amount <= 0 || !filter.matches(available)) {
            return null;
        }
        return tank.drain(maxDrain, doDrain);
    }
}
