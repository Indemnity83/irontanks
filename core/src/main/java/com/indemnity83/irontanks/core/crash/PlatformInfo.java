package com.indemnity83.irontanks.core.crash;

/**
 * Loader/environment facts the crash reporter needs but {@code core} can't discover on its own (it
 * has no Minecraft on its classpath). Each loader builds this from its own APIs and hands it to
 * {@link CrashReporting#bootstrap}, keeping {@code core} free of any {@code net.minecraft.*} types.
 *
 * @param modVersion the running mod version, e.g. {@code 2.2.0+mc26.1.2.fabric} or {@code 0.0.0-dev}
 * @param loaderName {@code "fabric"} or {@code "neoforge"}
 * @param minecraftVersion the Minecraft version, e.g. {@code 26.1.2}
 * @param developmentEnvironment true in a dev/runClient session, false in a shipped build
 */
public record PlatformInfo(
        String modVersion, String loaderName, String minecraftVersion, boolean developmentEnvironment) {}
