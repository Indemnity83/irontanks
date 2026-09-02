package com.indemnity83.irontanks.fabric.compat.logistics;

import com.indemnity83.irontanks.fabric.IronTanksFabric;
import com.indemnity83.irontanks.fabric.compat.LogisticsTanks;
import com.indemnity83.irontanks.fabric.content.TankBlockEntity;
import com.logistics.core.lib.tank.TankCell;
import com.logistics.core.lib.tank.TankCellLookup;
import com.logistics.core.lib.tank.TankColumns;
import java.util.Set;
import java.util.concurrent.atomic.AtomicBoolean;
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

    /**
     * Every registry id logistics has shipped its Glass Tank under: 0.7.x registered it as
     * {@code fluid/glass_tank}, 0.8+ moved it to {@code pipe/glass_tank}. Both are listed so the upgrade
     * path works against either build. This id is the one part of the logistics surface that is a string
     * rather than a symbol, so the {@code compileOnly} dependency cannot catch a rename — hence
     * {@link #checkGlassTankResolves()}.
     */
    private static final Set<Identifier> GLASS_TANK_IDS = Set.of(
            Identifier.fromNamespaceAndPath("logistics", "pipe/glass_tank"),
            Identifier.fromNamespaceAndPath("logistics", "fluid/glass_tank"));

    private static final AtomicBoolean GLASS_TANK_CHECKED = new AtomicBoolean();

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
        checkGlassTankResolves();
        Identifier id = BuiltInRegistries.BLOCK.getKey(state.getBlock());
        return id != null && GLASS_TANK_IDS.contains(id);
    }

    /**
     * Warn once if none of {@link #GLASS_TANK_IDS} is a real block in the installed logistics build —
     * i.e. it renamed the tank again and this integration has silently gone dead. Checked on first use
     * rather than at {@link #init()} because mod-initializer order does not guarantee logistics has
     * registered its blocks by the time the bridge is installed; by the first right-click it has.
     */
    private static void checkGlassTankResolves() {
        if (!GLASS_TANK_CHECKED.compareAndSet(false, true)) {
            return;
        }
        if (GLASS_TANK_IDS.stream().noneMatch(BuiltInRegistries.BLOCK::containsKey)) {
            IronTanksFabric.LOGGER.warn(
                    "Logistics is installed but none of {} is a registered block — its tank was renamed, so"
                            + " Iron Tanks upgrade items will not work on it. Please report this to Iron Tanks.",
                    GLASS_TANK_IDS);
        }
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
