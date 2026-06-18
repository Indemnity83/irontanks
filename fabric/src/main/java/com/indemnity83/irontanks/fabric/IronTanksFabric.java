package com.indemnity83.irontanks.fabric;

import com.indemnity83.irontanks.fabric.content.IronTanksContent;
import com.indemnity83.irontanks.fabric.content.TankFluidStorage;
import com.indemnity83.irontanks.fabric.crash.CrashReportingBootstrap;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.transfer.v1.fluid.FluidStorage;
import net.fabricmc.loader.api.FabricLoader;
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

        // Optional: when the logistics mod is present AND ships the tank-column API, wire iron tanks into
        // its shared tank columns so the two mods' tanks stack and share fluid, and iron upgrades work on
        // its glass tank. The bridge class is referenced only inside this guard, so its logistics imports
        // never link when logistics is absent. The apiPresent() probe keeps an older logistics (same mod
        // id, no API) from crashing init.
        if (FabricLoader.getInstance().isModLoaded("logistics")
                && com.indemnity83.irontanks.fabric.compat.LogisticsTanks.apiPresent()) {
            com.indemnity83.irontanks.fabric.compat.logistics.LogisticsTanksBridge.init();
        }

        // Opt-in (default off) sanitized crash reporting.
        CrashReportingBootstrap.init();
    }
}
