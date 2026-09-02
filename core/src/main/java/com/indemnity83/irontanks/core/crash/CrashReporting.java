package com.indemnity83.irontanks.core.crash;

import io.sentry.ISentryClient;
import io.sentry.SentryClient;
import io.sentry.SentryEvent;
import io.sentry.SentryOptions;
import io.sentry.protocol.Message;
import io.sentry.protocol.SentryException;
import java.nio.file.Path;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.Consumer;
import java.util.function.Function;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * The crash-reporting orchestrator: opt-in (default OFF), Iron-Tanks-only, sanitized Sentry
 * reporting. Pure Java — it depends on the Sentry SDK, {@link LogScrubber}, {@link Log4j2Bridge} and
 * {@link IronTanksConfig}, but never on Minecraft. Each loader calls {@link #bootstrap} once at init
 * with a {@link PlatformInfo} and its config path; the {@code /irontanks diagnostics} commands drive
 * {@link #enable()} / {@link #disable()}, which keep the persisted config and the live Sentry client
 * in lock-step so they can never drift.
 *
 * <p>It builds a <em>dedicated</em> {@link SentryClient} and never calls {@code Sentry.init()}, so it
 * can't clobber a global Sentry SDK that another mod might bundle.
 */
public final class CrashReporting {

    private CrashReporting() {}

    private static final Logger LOGGER = LoggerFactory.getLogger("irontanks/crash");

    /**
     * Public Sentry ingest key (DSN). NOT a secret — DSNs are write-only and designed to be embedded
     * in shipped clients. Overridable per-install via {@code crashReporting.dsnOverride}.
     */
    public static final String DEFAULT_DSN =
            "https://ccf7650d1be5ce1b785b1ff51dd064f3@o148290.ingest.us.sentry.io/4511502294908928";

    /**
     * The operator-facing crash-reporting &amp; privacy document, linked from the in-game notice by
     * every loader. Pinned to {@code blob/HEAD} rather than to a branch so it always resolves to the
     * repository's default branch and can never rot when a per-Minecraft-version line is archived.
     */
    public static final String PRIVACY_URL = "https://github.com/Indemnity83/irontanks/blob/HEAD/CRASH_REPORTING.md";

    private static final long FLUSH_TIMEOUT_MS = 2_000L;

    private static final AtomicBoolean ACTIVE = new AtomicBoolean(false);
    private static final AtomicBoolean SHUTDOWN_HOOK_REGISTERED = new AtomicBoolean(false);

    private static volatile ISentryClient client;
    private static volatile Log4j2Bridge bridge;
    private static volatile PlatformInfo platform;
    private static volatile Path configFile;
    private static volatile IronTanksConfig config;
    private static volatile String dsn = DEFAULT_DSN;

    // Test seam: how the live client is built. Default uses the real async-HTTP Sentry client.
    private static Function<SentryOptions, ISentryClient> clientFactory = SentryClient::new;

    // Test seam: how the JVM flush-on-exit hook is registered. Default adds a real shutdown hook;
    // tests install a no-op/recording registrar so the registration path can run without leaking hooks.
    private static Consumer<Thread> shutdownHookRegistrar = Runtime.getRuntime()::addShutdownHook;

    /**
     * Load config and, if crash reporting was left enabled, start it. Call once per loader during
     * init. {@code dsn} is normally {@link #DEFAULT_DSN}; the config's {@code dsnOverride} wins when set.
     */
    public static synchronized void bootstrap(PlatformInfo platformInfo, Path file, String dsnValue) {
        platform = platformInfo;
        configFile = file;
        dsn = (dsnValue == null || dsnValue.isBlank()) ? DEFAULT_DSN : dsnValue;
        config = IronTanksConfig.load(file);
        if (config.crashReporting().enabled()) {
            start();
        }
    }

    /** Turn reporting on: persist the choice and start the client + log bridge. Idempotent. */
    public static synchronized boolean enable() {
        if (config == null) {
            LOGGER.warn("Crash reporting enable() called before bootstrap()");
            return false;
        }
        config.crashReporting().setEnabled(true);
        persist();
        return ACTIVE.get() || start();
    }

    /** Turn reporting off: persist the choice and flush + close the client. Idempotent. */
    public static synchronized void disable() {
        if (config != null) {
            config.crashReporting().setEnabled(false);
            persist();
        }
        stop();
    }

    public static boolean isActive() {
        return ACTIVE.get();
    }

    /** Whether reporting is configured on (persisted), independent of whether the client is live. */
    public static boolean isEnabled() {
        return config != null && config.crashReporting().enabled();
    }

    public static boolean notifyOperators() {
        return config != null && config.crashReporting().notifyOperators();
    }

    /** Persist the operator join-notice preference. Returns the new value for convenience. */
    public static synchronized boolean setNotifyOperators(boolean value) {
        if (config != null) {
            config.crashReporting().setNotifyOperators(value);
            persist();
        }
        return value;
    }

    public static PlatformInfo platform() {
        return platform;
    }

    /** Forward a throwable to Sentry if reporting is live. No-op (never throws) otherwise. */
    public static void capture(Throwable throwable) {
        if (throwable == null || !ACTIVE.get()) {
            return;
        }
        ISentryClient current = client;
        if (current != null) {
            try {
                current.captureException(throwable);
            } catch (Throwable t) {
                LOGGER.warn("Failed to capture crash report: {}", t.toString());
            }
        }
    }

    /** Send a synthetic report to verify the pipeline. Returns false if reporting isn't live. */
    public static synchronized boolean sendTestReport() {
        if (!ACTIVE.get()) {
            return false;
        }
        ISentryClient current = client;
        if (current == null) {
            return false;
        }
        current.captureException(new IllegalStateException("Iron Tanks crash-reporting test event"));
        return true;
    }

    /**
     * Render a sanitized example of what a report would look like — built and scrubbed, but never
     * sent and never touching the client. Backs {@code /irontanks diagnostics preview}.
     */
    public static synchronized String previewReport() {
        SentryEvent event = new SentryEvent();
        Message message = new Message();
        String user = System.getProperty("user.name");
        String home = System.getProperty("user.home");
        message.setMessage("Example captured error: failed saving tank for player "
                + "550e8400-e29b-41d4-a716-446655440000 at "
                + (home == null ? "/home/" + user : home)
                + "/saves from 203.0.113.7 (token=s3cr3t-abc123)");
        message.setFormatted(message.getMessage());
        event.setMessage(message);

        SentryException exception = new SentryException();
        exception.setType("java.lang.IllegalStateException");
        exception.setValue("Tank column desync at /home/" + (user == null ? "player" : user) + "/world");
        event.setExceptions(List.of(exception));

        LogScrubber.scrub(event);
        return renderPreview(event);
    }

    static String renderPreview(SentryEvent event) {
        StringBuilder sb = new StringBuilder("Iron Tanks crash-report preview (nothing is sent):\n");
        if (platform != null) {
            sb.append("  release: irontanks@").append(platform.modVersion()).append('\n');
            sb.append("  environment: ")
                    .append(platform.developmentEnvironment() ? "development" : "production")
                    .append('\n');
            sb.append("  tags: loader=")
                    .append(platform.loaderName())
                    .append(", minecraft_version=")
                    .append(platform.minecraftVersion())
                    .append('\n');
        }
        Message message = event.getMessage();
        if (message != null && message.getMessage() != null) {
            sb.append("  message: ").append(message.getMessage()).append('\n');
        }
        if (event.getExceptions() != null) {
            for (SentryException ex : event.getExceptions()) {
                sb.append("  exception: ")
                        .append(ex.getType())
                        .append(": ")
                        .append(ex.getValue())
                        .append('\n');
            }
        }
        return sb.toString();
    }

    private static boolean start() {
        if (ACTIVE.get()) {
            return true;
        }
        String effectiveDsn = effectiveDsn();
        if (effectiveDsn.isBlank()) {
            LOGGER.warn("Crash reporting enabled but no DSN is configured; not starting");
            return false;
        }
        try {
            client = clientFactory.apply(buildOptions(effectiveDsn));
            registerShutdownHookOnce();
            bridge = new Log4j2Bridge(CrashReporting::capture);
            bridge.attach();
            ACTIVE.set(true);
            LOGGER.info(
                    "Crash reporting enabled (release=irontanks@{}, loader={})",
                    platform != null ? platform.modVersion() : "unknown",
                    platform != null ? platform.loaderName() : "unknown");
            return true;
        } catch (Throwable t) {
            LOGGER.warn("Failed to start crash reporting: {}", t.toString());
            client = null;
            bridge = null;
            ACTIVE.set(false);
            return false;
        }
    }

    private static void stop() {
        if (!ACTIVE.getAndSet(false)) {
            return;
        }
        // start() assigns bridge before flipping ACTIVE on, and its failure path clears ACTIVE, so
        // reaching here (ACTIVE was true) always means there is a live bridge to detach.
        bridge.detach();
        bridge = null;
        ISentryClient current = client;
        client = null;
        if (current != null) {
            try {
                current.flush(FLUSH_TIMEOUT_MS);
                current.close();
            } catch (Throwable t) {
                LOGGER.warn("Error closing crash reporter: {}", t.toString());
            }
        }
        LOGGER.info("Crash reporting disabled");
    }

    private static SentryOptions buildOptions(String effectiveDsn) {
        boolean dev = platform != null && platform.developmentEnvironment();
        SentryOptions options = new SentryOptions();
        options.setDsn(effectiveDsn);
        options.setEnableUncaughtExceptionHandler(false);
        options.setSendDefaultPii(false);
        options.setAttachServerName(false);
        options.setEnableExternalConfiguration(false);
        if (platform != null) {
            options.setRelease("irontanks@" + platform.modVersion());
            options.setTag("loader", platform.loaderName());
            options.setTag("minecraft_version", platform.minecraftVersion());
            options.setTag("mod_version", platform.modVersion());
        }
        options.setEnvironment(dev ? "development" : "production");
        options.setBeforeSend((event, hint) -> LogScrubber.scrub(event));
        options.setDebug(dev);
        return options;
    }

    static String effectiveDsn() {
        if (config != null) {
            String override = config.crashReporting().dsnOverride();
            if (!override.isBlank()) {
                return override.trim();
            }
        }
        return dsn == null ? "" : dsn;
    }

    private static void persist() {
        // Every caller reaches persist() only with a live config, so configFile is the only guard needed.
        if (configFile != null) {
            config.save(configFile);
        }
    }

    private static void registerShutdownHookOnce() {
        if (SHUTDOWN_HOOK_REGISTERED.compareAndSet(false, true)) {
            shutdownHookRegistrar.accept(new Thread(CrashReporting::flushOnExit, "irontanks-sentry-flush"));
        }
    }

    static void flushOnExit() {
        ISentryClient current = client;
        if (current != null) {
            try {
                current.flush(FLUSH_TIMEOUT_MS);
            } catch (Throwable ignored) {
                // best-effort flush on JVM shutdown
            }
        }
    }

    // --- test-only hooks -------------------------------------------------------------------------

    /** Swap the client factory and clear all state so each unit test starts clean. */
    static synchronized void resetForTesting(Function<SentryOptions, ISentryClient> factory) {
        resetForTesting(factory, null);
    }

    /**
     * Swap the client factory and shutdown-hook registrar and clear all state so each unit test starts
     * clean. A null registrar defaults to a no-op, so the registration path runs without ever leaking a
     * real JVM hook between tests.
     */
    static synchronized void resetForTesting(
            Function<SentryOptions, ISentryClient> factory, Consumer<Thread> registrar) {
        stop();
        clientFactory = factory != null ? factory : SentryClient::new;
        shutdownHookRegistrar = registrar != null ? registrar : thread -> {};
        config = null;
        platform = null;
        configFile = null;
        dsn = DEFAULT_DSN;
        SHUTDOWN_HOOK_REGISTERED.set(false);
    }

    /** Override the base DSN so the "enabled but no DSN configured" start() path is reachable. */
    static void setDsnForTesting(String value) {
        dsn = value;
    }
}
