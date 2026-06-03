package com.indemnity83.irontanks.neoforge.crash;

import com.indemnity83.irontanks.core.crash.CrashReporting;
import com.indemnity83.irontanks.core.crash.PlatformInfo;
import java.nio.file.Path;
import net.minecraft.server.level.ServerPlayer;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.loading.FMLPaths;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.event.RegisterCommandsEvent;
import net.neoforged.neoforge.event.entity.player.PlayerEvent;
import net.neoforged.neoforge.event.server.ServerStartingEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/** Wires opt-in crash reporting into the NeoForge lifecycle: bootstrap, command, join notice. */
public final class CrashReportingBootstrap {

    private CrashReportingBootstrap() {}

    private static final Logger LOGGER = LoggerFactory.getLogger("irontanks/crash");

    /** Best-effort init — a failure here logs a warning and never breaks mod load. */
    public static void init() {
        try {
            PlatformInfo platform = NeoForgePlatformInfo.create();
            Path configFile = FMLPaths.CONFIGDIR.get().resolve("irontanks.json");
            CrashReporting.bootstrap(platform, configFile, CrashReporting.DEFAULT_DSN);

            IEventBus gameBus = NeoForge.EVENT_BUS;
            gameBus.addListener((RegisterCommandsEvent event) ->
                    IronTanksDiagnosticsCommand.register(event.getDispatcher(), platform.developmentEnvironment()));
            gameBus.addListener((ServerStartingEvent event) -> CrashReportNotifier.reset());
            gameBus.addListener((PlayerEvent.PlayerLoggedInEvent event) -> {
                if (event.getEntity() instanceof ServerPlayer player) {
                    CrashReportNotifier.maybeNotify(player);
                }
            });
        } catch (Throwable t) {
            LOGGER.warn("Could not initialize crash reporting", t);
        }
    }
}
