package com.indemnity83.irontanks.fabric;

import com.indemnity83.irontanks.fabric.content.IronTanksContent;
import com.indemnity83.irontanks.fabric.content.TankFluidStorage;
import com.indemnity83.irontanks.fabric.crash.CrashReportingBootstrap;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.transfer.v1.fluid.FluidStorage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Fabric entry point for Iron Tanks. Thin glue: registers content (blocks, items, the tank block-entity
 * type, the creative tab) and the fluid storage, then defers all tank behavior to the loader-agnostic
 * {@code com.indemnity83.irontanks.core} module.
 */
public final class IronTanksFabric implements ModInitializer {

    public static final String MOD_ID = "irontanks";
    public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

    @Override
    public void onInitialize() {
        LOGGER.info("Registering Iron Tanks content");
        IronTanksContent.register();

        // Expose every tank's fluid storage so pipes/pumps can fill and drain it.
        FluidStorage.SIDED.registerForBlockEntity(
                (tank, direction) -> new TankFluidStorage(tank), IronTanksContent.TANK_BLOCK_ENTITY);

        // Opt-in (default off) sanitized crash reporting.
        CrashReportingBootstrap.init();
    }
}
