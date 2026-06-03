package com.indemnity83.irontanks.fabric.crash;

import com.indemnity83.irontanks.core.crash.CrashReporting;
import com.indemnity83.irontanks.core.crash.PlatformInfo;
import java.nio.file.Path;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;
import net.fabricmc.fabric.api.networking.v1.ServerPlayConnectionEvents;
import net.fabricmc.loader.api.FabricLoader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/** Wires opt-in crash reporting into the Fabric lifecycle: bootstrap, command, join notice. */
public final class CrashReportingBootstrap {

    private CrashReportingBootstrap() {}

    private static final Logger LOGGER = LoggerFactory.getLogger("irontanks/crash");

    /** Best-effort init — a failure here logs a warning and never breaks mod load. */
    public static void init() {
        try {
            PlatformInfo platform = FabricPlatformInfo.create();
            Path configFile = FabricLoader.getInstance().getConfigDir().resolve("irontanks.json");
            CrashReporting.bootstrap(platform, configFile, CrashReporting.DEFAULT_DSN);

            CommandRegistrationCallback.EVENT.register((dispatcher, registryAccess, environment) ->
                    IronTanksDiagnosticsCommand.register(dispatcher, platform.developmentEnvironment()));
            ServerLifecycleEvents.SERVER_STARTING.register(server -> CrashReportNotifier.reset());
            ServerPlayConnectionEvents.JOIN.register(
                    (handler, sender, server) -> CrashReportNotifier.maybeNotify(handler.player));
        } catch (Throwable t) {
            LOGGER.warn("Could not initialize crash reporting", t);
        }
    }
}
