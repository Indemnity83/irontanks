package com.indemnity83.irontanks.fabric.crash;

import com.indemnity83.irontanks.core.crash.PlatformInfo;
import net.fabricmc.loader.api.FabricLoader;

/** Builds the loader-agnostic {@link PlatformInfo} the crash reporter needs from Fabric's APIs. */
public final class FabricPlatformInfo {

    private FabricPlatformInfo() {}

    public static PlatformInfo create() {
        FabricLoader loader = FabricLoader.getInstance();
        String modVersion = loader.getModContainer("irontanks")
                .map(container -> container.getMetadata().getVersion().getFriendlyString())
                .orElse("0.0.0-dev");
        String minecraftVersion = loader.getModContainer("minecraft")
                .map(container -> container.getMetadata().getVersion().getFriendlyString())
                .orElse("unknown");
        return new PlatformInfo(modVersion, "fabric", minecraftVersion, loader.isDevelopmentEnvironment());
    }
}
