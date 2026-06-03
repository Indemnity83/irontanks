package com.indemnity83.irontanks.neoforge;

import com.indemnity83.irontanks.neoforge.client.IronTanksClient;
import com.indemnity83.irontanks.neoforge.content.IronTanksContent;
import com.indemnity83.irontanks.neoforge.crash.CrashReportingBootstrap;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.loading.FMLEnvironment;
import net.neoforged.neoforge.registries.RegisterEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * NeoForge entry point for Iron Tanks. Thin glue: it registers content (blocks, items, the tank
 * block-entity type and the creative tab) and the fluid-handler capability, then defers all actual
 * tank behavior to the loader-agnostic {@code com.indemnity83.irontanks.core} module.
 */
@Mod(IronTanksNeoForge.MOD_ID)
public final class IronTanksNeoForge {

    public static final String MOD_ID = "irontanks";
    public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

    private boolean contentRegistered;

    public IronTanksNeoForge(IEventBus modBus) {
        modBus.addListener(this::onRegister);
        IronTanksCapabilities.register(modBus);
        if (FMLEnvironment.getDist().isClient()) {
            IronTanksClient.register(modBus);
        }

        // Opt-in (default off) sanitized crash reporting.
        CrashReportingBootstrap.init();
    }

    private synchronized void onRegister(RegisterEvent event) {
        if (contentRegistered) {
            return;
        }
        // NeoForge unfreezes all BuiltInRegistries during RegisterEvent emission, so direct
        // Registry.register() calls are safe here regardless of which registry fired first.
        LOGGER.info("Registering Iron Tanks content");
        IronTanksContent.register();
        contentRegistered = true;
    }
}
