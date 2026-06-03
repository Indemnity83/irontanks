package com.indemnity83.irontanks.neoforge.crash;

import com.indemnity83.irontanks.core.crash.PlatformInfo;
import net.neoforged.fml.ModList;
import net.neoforged.fml.loading.FMLEnvironment;

/** Builds the loader-agnostic {@link PlatformInfo} the crash reporter needs from NeoForge's APIs. */
public final class NeoForgePlatformInfo {

    private NeoForgePlatformInfo() {}

    public static PlatformInfo create() {
        String modVersion = ModList.get()
                .getModContainerById("irontanks")
                .map(container -> container.getModInfo().getVersion().toString())
                .orElse("0.0.0-dev");
        String minecraftVersion = ModList.get()
                .getModContainerById("minecraft")
                .map(container -> container.getModInfo().getVersion().toString())
                .orElse("unknown");
        return new PlatformInfo(modVersion, "neoforge", minecraftVersion, !FMLEnvironment.isProduction());
    }
}
