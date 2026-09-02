package com.indemnity83.irontanks.core.crash;

import static org.assertj.core.api.Assertions.assertThat;

import io.sentry.ISentryClient;
import io.sentry.SentryEvent;
import io.sentry.SentryOptions;
import io.sentry.protocol.Message;
import java.lang.reflect.Proxy;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

class CrashReportingTest {

    private FakeClient fake;
    private Path configFile;

    @BeforeEach
    void setUp(@TempDir Path dir) {
        configFile = dir.resolve("irontanks.json");
        fake = new FakeClient();
        CrashReporting.resetForTesting(options -> fake.client);
    }

    @AfterEach
    void tearDown() {
        CrashReporting.resetForTesting(null);
    }

    private static PlatformInfo platform() {
        return new PlatformInfo("1.2.3+test", "fabric", "26.1.2", false);
    }

    private static PlatformInfo devPlatform() {
        return new PlatformInfo("9.9.9+dev", "neoforge", "26.1.2", true);
    }

    @Test
    void disabledByDefaultDropsCaptures() {
        CrashReporting.bootstrap(platform(), configFile, null);

        assertThat(CrashReporting.isActive()).isFalse();
        CrashReporting.capture(new RuntimeException("ignored"));
        assertThat(fake.captured).isEmpty();
    }

    @Test
    void enableActivatesPersistsAndForwards() {
        CrashReporting.bootstrap(platform(), configFile, null);

        assertThat(CrashReporting.enable()).isTrue();
        assertThat(CrashReporting.isActive()).isTrue();
        assertThat(IronTanksConfig.load(configFile).crashReporting().enabled()).isTrue();

        CrashReporting.capture(new RuntimeException("boom"));
        assertThat(fake.captured).hasSize(1);
    }

    @Test
    void disableDeactivatesPersistsAndCloses() {
        CrashReporting.bootstrap(platform(), configFile, null);
        CrashReporting.enable();

        CrashReporting.disable();

        assertThat(CrashReporting.isActive()).isFalse();
        assertThat(fake.closed).isTrue();
        assertThat(IronTanksConfig.load(configFile).crashReporting().enabled()).isFalse();

        CrashReporting.capture(new RuntimeException("dropped"));
        assertThat(fake.captured).isEmpty();
    }

    @Test
    void bootstrapComesUpActiveWhenConfigEnabled() {
        IronTanksConfig pre = IronTanksConfig.load(configFile);
        pre.crashReporting().setEnabled(true);
        pre.save(configFile);

        CrashReporting.bootstrap(platform(), configFile, null);

        assertThat(CrashReporting.isActive()).isTrue();
    }

    @Test
    void previewIsScrubbedAndNeverTouchesClient() {
        CrashReporting.bootstrap(platform(), configFile, null);
        CrashReporting.enable();

        String preview = CrashReporting.previewReport();

        assertThat(preview).contains("<uuid>").contains("<ip>").contains("<redacted>");
        assertThat(preview).doesNotContain("550e8400").doesNotContain("203.0.113.7");
        assertThat(fake.captured).isEmpty();
        assertThat(CrashReporting.isActive()).isTrue();
    }

    @Test
    void testReportRequiresActiveReporting() {
        CrashReporting.bootstrap(platform(), configFile, null);

        assertThat(CrashReporting.sendTestReport()).isFalse();

        CrashReporting.enable();
        assertThat(CrashReporting.sendTestReport()).isTrue();
        assertThat(fake.captured).hasSize(1);
    }

    // --- getters / persisted state ----------------------------------------------------------------

    @Test
    void exposesPersistedStateThroughGetters() {
        CrashReporting.bootstrap(platform(), configFile, null);

        assertThat(CrashReporting.isEnabled()).isFalse();
        assertThat(CrashReporting.notifyOperators()).isTrue();
        assertThat(CrashReporting.platform()).isEqualTo(platform());

        CrashReporting.enable();
        assertThat(CrashReporting.isEnabled()).isTrue();
    }

    @Test
    void setNotifyOperatorsPersists() {
        CrashReporting.bootstrap(platform(), configFile, null);

        assertThat(CrashReporting.setNotifyOperators(false)).isFalse();

        assertThat(CrashReporting.notifyOperators()).isFalse();
        assertThat(IronTanksConfig.load(configFile).crashReporting().notifyOperators())
                .isFalse();
    }

    @Test
    void gettersAndMutatorsAreSafeBeforeBootstrap() {
        assertThat(CrashReporting.isEnabled()).isFalse();
        assertThat(CrashReporting.notifyOperators()).isFalse();
        assertThat(CrashReporting.platform()).isNull();
        assertThat(CrashReporting.setNotifyOperators(true)).isTrue(); // no config yet: returns value, no persist

        assertThat(CrashReporting.enable()).isFalse(); // enable before bootstrap is rejected
        CrashReporting.disable(); // disable before bootstrap is a no-op
        assertThat(CrashReporting.isActive()).isFalse();
    }

    // --- enable/start lifecycle edges -------------------------------------------------------------

    @Test
    void enableIsIdempotentWhileActive() {
        CrashReporting.bootstrap(platform(), configFile, null);

        assertThat(CrashReporting.enable()).isTrue();
        assertThat(CrashReporting.enable()).isTrue(); // already active: short-circuits
        assertThat(CrashReporting.isActive()).isTrue();
    }

    @Test
    void bootstrapWhileActiveLeavesReportingOn() {
        IronTanksConfig pre = IronTanksConfig.load(configFile);
        pre.crashReporting().setEnabled(true);
        pre.save(configFile);

        CrashReporting.bootstrap(platform(), configFile, null);
        assertThat(CrashReporting.isActive()).isTrue();

        CrashReporting.bootstrap(platform(), configFile, null); // start() called again -> early return
        assertThat(CrashReporting.isActive()).isTrue();
    }

    @Test
    void startFailureIsContainedAndLeavesReportingOff() {
        CrashReporting.resetForTesting(options -> {
            throw new RuntimeException("client construction failed");
        });
        CrashReporting.bootstrap(platform(), configFile, null);

        assertThat(CrashReporting.enable()).isFalse();
        assertThat(CrashReporting.isActive()).isFalse();
    }

    @Test
    void enabledWithoutADsnDoesNotStart() {
        CrashReporting.bootstrap(platform(), configFile, null);
        CrashReporting.setDsnForTesting(null); // no base DSN, and no override configured

        assertThat(CrashReporting.enable()).isFalse();
        assertThat(CrashReporting.isActive()).isFalse();
    }

    @Test
    void dsnOverrideTakesPrecedenceAndIsTrimmed() {
        IronTanksConfig pre = IronTanksConfig.load(configFile);
        pre.crashReporting().setEnabled(true);
        pre.crashReporting().setDsnOverride("  https://override@example.test/2  ");
        pre.save(configFile);

        CrashReporting.bootstrap(platform(), configFile, null);

        assertThat(CrashReporting.isActive()).isTrue();
    }

    @Test
    void privacyUrlResolvesThroughTheDefaultBranch() {
        // The in-game notice links operators here; a branch-pinned URL 404s as soon as that line is
        // archived, so the link has to resolve through the repository's default branch instead.
        assertThat(CrashReporting.PRIVACY_URL)
                .isEqualTo("https://github.com/Indemnity83/irontanks/blob/HEAD/CRASH_REPORTING.md");
    }

    @Test
    void effectiveDsnFallsBackToBaseDsnWithoutConfig() {
        // After reset, config is null: effectiveDsn() skips the override and returns the base DSN.
        assertThat(CrashReporting.effectiveDsn()).isEqualTo(CrashReporting.DEFAULT_DSN);
    }

    @Test
    void bootstrapHonorsBlankAndExplicitDsnArguments() {
        CrashReporting.bootstrap(platform(), configFile, "   "); // blank -> default DSN
        CrashReporting.bootstrap(platform(), configFile, "https://arg@example.test/3"); // explicit DSN

        assertThat(CrashReporting.enable()).isTrue();
        assertThat(CrashReporting.isActive()).isTrue();
    }

    @Test
    void skipsPersistenceWhenNoConfigFileIsKnown() {
        CrashReporting.bootstrap(platform(), null, null); // config is loaded, but no file path to save to

        assertThat(CrashReporting.setNotifyOperators(false)).isFalse(); // persist() finds no file: no save

        assertThat(CrashReporting.notifyOperators()).isFalse(); // in-memory value still changes
    }

    // --- capture resilience -----------------------------------------------------------------------

    @Test
    void captureIgnoresNullAndSwallowsClientErrors() {
        fake.throwOnCapture = true;
        CrashReporting.bootstrap(platform(), configFile, null);
        CrashReporting.enable();

        CrashReporting.capture(null); // null throwable: ignored
        CrashReporting.capture(new RuntimeException("x")); // client throws: swallowed

        assertThat(CrashReporting.isActive()).isTrue();
    }

    @Test
    void activatesEvenWhenClientFactoryReturnsNull() {
        CrashReporting.resetForTesting(options -> null);
        CrashReporting.bootstrap(platform(), configFile, null);

        assertThat(CrashReporting.enable()).isTrue();
        assertThat(CrashReporting.isActive()).isTrue();

        CrashReporting.capture(new RuntimeException("x")); // no client: no-op
        assertThat(CrashReporting.sendTestReport()).isFalse(); // no client: nothing to send

        CrashReporting.disable(); // stop() with a null client must still tear down cleanly
        assertThat(CrashReporting.isActive()).isFalse();
    }

    @Test
    void disableToleratesFlushFailure() {
        fake.throwOnFlush = true;
        CrashReporting.bootstrap(platform(), configFile, null);
        CrashReporting.enable();

        CrashReporting.disable(); // flush throws inside stop(): swallowed

        assertThat(CrashReporting.isActive()).isFalse();
    }

    // --- shutdown-hook + flush-on-exit ------------------------------------------------------------

    @Test
    void registersFlushHookExactlyOnceAcrossRestarts() {
        AtomicInteger registrations = new AtomicInteger();
        CrashReporting.resetForTesting(options -> fake.client, thread -> registrations.incrementAndGet());
        CrashReporting.bootstrap(platform(), configFile, null);

        CrashReporting.enable();
        CrashReporting.disable();
        CrashReporting.enable(); // second start: the hook is already registered

        assertThat(registrations.get()).isEqualTo(1);
        assertThat(CrashReporting.isActive()).isTrue();
    }

    @Test
    void flushOnExitFlushesActiveClientAndToleratesNone() {
        CrashReporting.flushOnExit(); // no client yet: no-op

        CrashReporting.bootstrap(platform(), configFile, null);
        CrashReporting.enable();
        CrashReporting.flushOnExit();

        assertThat(fake.flushed).isTrue();
    }

    @Test
    void flushOnExitSwallowsFlushFailure() {
        fake.throwOnFlush = true;
        CrashReporting.bootstrap(platform(), configFile, null);
        CrashReporting.enable();

        CrashReporting.flushOnExit(); // flush throws: ignored on shutdown

        assertThat(CrashReporting.isActive()).isTrue();
    }

    // --- option building + scrubbing --------------------------------------------------------------

    @Test
    void buildsScrubbingDevelopmentOptions() {
        AtomicReference<SentryOptions> captured = new AtomicReference<>();
        CrashReporting.resetForTesting(options -> {
            captured.set(options);
            return fake.client;
        });
        CrashReporting.bootstrap(devPlatform(), configFile, null);
        CrashReporting.enable();

        SentryOptions options = captured.get();
        assertThat(options).isNotNull();
        assertThat(options.isDebug()).isTrue();
        assertThat(options.getEnvironment()).isEqualTo("development");
        assertThat(options.getRelease()).isEqualTo("irontanks@9.9.9+dev");

        SentryEvent dirty = new SentryEvent();
        Message message = new Message();
        message.setMessage("error from 203.0.113.7");
        message.setFormatted(message.getMessage());
        dirty.setMessage(message);

        SentryEvent scrubbed = options.getBeforeSend().execute(dirty, null);

        assertThat(scrubbed).isNotNull();
        assertThat(scrubbed.getMessage().getMessage()).isEqualTo("error from <ip>");
    }

    @Test
    void startsWithoutPlatformInfo() {
        IronTanksConfig pre = IronTanksConfig.load(configFile);
        pre.crashReporting().setEnabled(true);
        pre.save(configFile);

        CrashReporting.bootstrap(null, configFile, null); // no platform, but enabled -> start

        assertThat(CrashReporting.isActive()).isTrue();
        assertThat(CrashReporting.platform()).isNull();
    }

    // --- preview rendering edges ------------------------------------------------------------------

    @Test
    void previewShowsDevelopmentEnvironment() {
        CrashReporting.bootstrap(devPlatform(), configFile, null);

        String preview = CrashReporting.previewReport();

        assertThat(preview).contains("environment: development");
    }

    @Test
    void previewToleratesMissingSystemProperties() {
        String home = System.getProperty("user.home");
        String user = System.getProperty("user.name");
        try {
            System.clearProperty("user.home");
            System.clearProperty("user.name");
            CrashReporting.bootstrap(platform(), configFile, null);

            String preview = CrashReporting.previewReport();

            assertThat(preview).contains("nothing is sent");
        } finally {
            restore("user.home", home);
            restore("user.name", user);
        }
    }

    @Test
    void renderPreviewToleratesMissingPlatformAndFields() {
        // After reset, platform is null; a bare event has no message and no exceptions.
        String preview = CrashReporting.renderPreview(new SentryEvent());

        assertThat(preview).contains("nothing is sent");
        assertThat(preview).doesNotContain("release:");
        assertThat(preview).doesNotContain("message:");
        assertThat(preview).doesNotContain("exception:");
    }

    @Test
    void renderPreviewToleratesMessageWithoutText() {
        SentryEvent event = new SentryEvent();
        event.setMessage(new Message()); // a Message whose text is null
        event.setExceptions(List.of()); // present but empty

        String preview = CrashReporting.renderPreview(event);

        assertThat(preview).doesNotContain("message:");
    }

    private static void restore(String key, String value) {
        if (value != null) {
            System.setProperty(key, value);
        } else {
            System.clearProperty(key);
        }
    }

    /**
     * A recording stand-in for {@link ISentryClient}. The interface has many methods (most defaulted),
     * so we implement it with a dynamic proxy and only react to the few calls the orchestrator makes —
     * {@code captureException}, {@code flush}, {@code close}, {@code isEnabled} — with optional failure
     * injection for the resilience tests.
     */
    private static final class FakeClient {
        private final List<Throwable> captured = new ArrayList<>();
        private boolean closed;
        private boolean flushed;
        private boolean throwOnCapture;
        private boolean throwOnFlush;
        private final ISentryClient client = (ISentryClient) Proxy.newProxyInstance(
                ISentryClient.class.getClassLoader(), new Class<?>[] {ISentryClient.class}, (proxy, method, args) -> {
                    String name = method.getName();
                    if (name.equals("captureException") && args != null) {
                        if (throwOnCapture) {
                            throw new RuntimeException("capture failed");
                        }
                        for (Object arg : args) {
                            if (arg instanceof Throwable t) {
                                captured.add(t);
                                break;
                            }
                        }
                        return null;
                    }
                    if (name.equals("flush")) {
                        flushed = true;
                        if (throwOnFlush) {
                            throw new RuntimeException("flush failed");
                        }
                        return null;
                    }
                    if (name.equals("close")) {
                        closed = true;
                        return null;
                    }
                    if (name.equals("isEnabled")) {
                        return Boolean.TRUE;
                    }
                    if (name.equals("hashCode")) {
                        return System.identityHashCode(proxy);
                    }
                    if (name.equals("equals")) {
                        return proxy == (args == null ? null : args[0]);
                    }
                    if (name.equals("toString")) {
                        return "FakeClient";
                    }
                    Class<?> rt = method.getReturnType();
                    if (rt == boolean.class) {
                        return false;
                    }
                    if (rt == int.class) {
                        return 0;
                    }
                    if (rt == long.class) {
                        return 0L;
                    }
                    return null;
                });
    }
}
