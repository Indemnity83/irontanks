package com.indemnity83.irontanks.neoforge;

import com.indemnity83.irontanks.neoforge.client.IronTanksClient;
import com.indemnity83.irontanks.neoforge.content.IronTanksContent;
import com.indemnity83.irontanks.neoforge.crash.CrashReportingBootstrap;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.ModList;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.event.lifecycle.FMLCommonSetupEvent;
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

        // Optional: when the logistics mod is present AND ships the tank-column API, wire iron tanks into
        // its shared tank columns so the two mods' tanks stack and share fluid, and iron upgrades work on
        // its glass tank. Done at common setup (ModList is populated by then); the bridge class is referenced
        // only inside the guard, so its logistics imports never link when logistics is absent. The
        // apiPresent() probe keeps an older logistics (same mod id, no API) from crashing init. Iron Tanks
        // works fully standalone.
        modBus.addListener((FMLCommonSetupEvent event) -> {
            if (ModList.get().isLoaded("logistics")
                    && com.indemnity83.irontanks.neoforge.compat.LogisticsTanks.apiPresent()) {
                com.indemnity83.irontanks.neoforge.compat.logistics.LogisticsTanksBridge.init();
            }
        });

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
