package com.indemnity83.irontanks.fabric.compat.logistics;

import com.indemnity83.irontanks.fabric.compat.LogisticsTanks;
import com.indemnity83.irontanks.fabric.content.TankBlockEntity;
import com.logistics.core.lib.tank.TankCell;
import com.logistics.core.lib.tank.TankCellLookup;
import com.logistics.core.lib.tank.TankColumns;
import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.Identifier;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.Nullable;

/**
 * Logistics-coupled implementation of {@link LogisticsTanks.Bridge}. Loaded only when the logistics mod
 * is installed (see {@link #init()}), so its {@code com.logistics.*} references are never linked
 * otherwise.
 *
 * <p>{@link #init()} registers Iron Tanks' block entities with the logistics {@link TankCellLookup} so a
 * vertical column can span both mods, and installs this bridge so Iron Tanks routes its column rebalance
 * through the shared logistics engine — driven by whichever mod owns the column's bottom block.
 */
public final class LogisticsTanksBridge implements LogisticsTanks.Bridge {

    private static final Identifier GLASS_TANK_ID = Identifier.fromNamespaceAndPath("logistics", "glass_tank");

    private LogisticsTanksBridge() {}

    /** Wire Iron Tanks into the logistics tank-column system. Call only when logistics is present. */
    public static void init() {
        TankCellLookup.register((level, pos) ->
                level.getBlockEntity(pos) instanceof TankBlockEntity tank ? new LogisticsTankCell(tank) : null);
        LogisticsTanks.install(new LogisticsTanksBridge());
    }

    @Override
    public boolean isColumnBottom(Level level, BlockPos pos) {
        return TankColumns.isColumnBottom(level, pos);
    }

    @Override
    public void rebalanceColumn(Level level, BlockPos pos) {
        TankColumns.columnAt(level, pos).rebalance();
    }

    @Override
    public boolean isForeignTank(BlockState state) {
        // Identify logistics' Glass Tank by registry id — its block class is not on the API jar.
        return GLASS_TANK_ID.equals(BuiltInRegistries.BLOCK.getKey(state.getBlock()));
    }

    @Override
    @Nullable
    public LogisticsTanks.Contents readForeignTank(Level level, BlockPos pos) {
        TankCell cell = TankCellLookup.find(level, pos);
        if (cell == null || cell.fluid().isBlank() || cell.amount() <= 0) {
            return null;
        }
        return new LogisticsTanks.Contents(LogisticsTankCell.toVariant(cell.fluid()), cell.amount());
    }
}
