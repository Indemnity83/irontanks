package com.indemnity83.irontanks.neoforge.compat;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.transfer.fluid.FluidResource;
import org.jetbrains.annotations.Nullable;

/**
 * Neutral seam for the optional logistics integration. Holds no references to {@code com.logistics.*}
 * types, so the content classes that call it (the tank block entity, the upgrade item) link cleanly
 * whether or not logistics is installed.
 *
 * <p>When the logistics mod IS present, {@code com.indemnity83.irontanks.neoforge.compat.logistics}
 * installs a {@link Bridge} that drives a shared, cross-mod fluid column through the logistics API. That
 * package — and only that package — references logistics types, and is class-loaded behind a
 * mod-present check, so logistics' absence can never cause a {@code NoClassDefFoundError}.
 */
public final class LogisticsTanks {

    /** A foreign (logistics) tank cell's contents, read for an in-place upgrade. Amount is in droplets. */
    public record Contents(FluidResource fluid, long droplets) {}

    /** Implemented in the logistics-coupled compat package; installed only when logistics is present. */
    public interface Bridge {
        /** Whether {@code pos} is the single cell that should settle its (possibly cross-mod) column. */
        boolean isColumnBottom(Level level, BlockPos pos);

        /** Settle the whole column containing {@code pos}; changed cells (foreign included) sync themselves. */
        void rebalanceColumn(Level level, BlockPos pos);

        /** Whether {@code state} is a logistics tank that this mod's upgrade items can consume. */
        boolean isForeignTank(BlockState state);

        /** The logistics tank cell's contents at {@code pos} (droplets), or {@code null} if absent/empty. */
        @Nullable
        Contents readForeignTank(Level level, BlockPos pos);
    }

    @Nullable
    private static volatile Bridge bridge;

    private LogisticsTanks() {}

    /** A tank-column API class whose presence signals a logistics build new enough to integrate with. */
    private static final String API_PROBE_CLASS = "com.logistics.core.lib.tank.TankCellLookup";

    /**
     * Whether the installed logistics build actually ships the tank-column API this integration needs.
     * The mod id alone is not enough — an older logistics could declare the same id without these classes —
     * so the entry point must confirm the API is present before wiring the bridge, or {@code init()} would
     * throw {@link NoClassDefFoundError}. Referenced only by name, so this class still links when logistics
     * is absent.
     */
    public static boolean apiPresent() {
        try {
            Class.forName(API_PROBE_CLASS, false, LogisticsTanks.class.getClassLoader());
            return true;
        } catch (Throwable ignored) {
            return false;
        }
    }

    /** Install the logistics-backed bridge. Called once, only when the logistics mod is present. */
    public static void install(Bridge installed) {
        bridge = installed;
    }

    /** Whether the logistics integration is active (the mod is installed and wired). */
    public static boolean active() {
        return bridge != null;
    }

    /** The active bridge, or {@code null} when logistics is absent. Guard with {@link #active()}. */
    @Nullable
    public static Bridge get() {
        return bridge;
    }
}
