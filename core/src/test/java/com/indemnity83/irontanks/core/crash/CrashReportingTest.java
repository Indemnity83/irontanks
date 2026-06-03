package com.indemnity83.irontanks.core.crash;

import static org.assertj.core.api.Assertions.assertThat;

import io.sentry.ISentryClient;
import java.lang.reflect.Proxy;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
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

    /**
     * A recording stand-in for {@link ISentryClient}. The interface has many methods (most defaulted),
     * so we implement it with a dynamic proxy and only react to the few calls the orchestrator makes —
     * {@code captureException}, {@code flush}, {@code close}, {@code isEnabled}.
     */
    private static final class FakeClient {
        private final List<Throwable> captured = new ArrayList<>();
        private boolean closed;
        private final ISentryClient client = (ISentryClient) Proxy.newProxyInstance(
                ISentryClient.class.getClassLoader(), new Class<?>[] {ISentryClient.class}, (proxy, method, args) -> {
                    String name = method.getName();
                    if (name.equals("captureException") && args != null) {
                        for (Object arg : args) {
                            if (arg instanceof Throwable t) {
                                captured.add(t);
                                break;
                            }
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
