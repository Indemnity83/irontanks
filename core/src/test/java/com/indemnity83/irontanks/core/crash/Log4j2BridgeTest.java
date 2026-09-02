package com.indemnity83.irontanks.core.crash;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Supplier;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.core.LoggerContext;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;

class Log4j2BridgeTest {

    // The bridge's appender name; the bridge keeps it private, so we mirror it here only to scrub any
    // appender a failure-path test might leave attached to the shared root logger.
    private static final String APPENDER_NAME = "IronTanksSentryBridge";

    @AfterEach
    void removeLeakedAppender() {
        if (LogManager.getContext(false) instanceof LoggerContext ctx) {
            ctx.getConfiguration().getRootLogger().removeAppender(APPENDER_NAME);
            ctx.updateLoggers();
        }
    }

    @Test
    void forwardsIronTanksErrorsWithThrowable() {
        assertThat(Log4j2Bridge.shouldForward("irontanks", true)).isTrue();
        assertThat(Log4j2Bridge.shouldForward("irontanks/crash", true)).isTrue();
        assertThat(Log4j2Bridge.shouldForward("irontanks.core", true)).isTrue();
        assertThat(Log4j2Bridge.shouldForward("IronTanks/Crash", true)).isTrue();
        assertThat(Log4j2Bridge.shouldForward("com.indemnity83.irontanks", true))
                .isTrue();
        assertThat(Log4j2Bridge.shouldForward("com.indemnity83.irontanks.core.crash", true))
                .isTrue();
        assertThat(Log4j2Bridge.shouldForward("com.indemnity83.irontanks.core.FluidColumn", true))
                .isTrue();
    }

    @Test
    void ignoresOtherLoggers() {
        assertThat(Log4j2Bridge.shouldForward("net.minecraft.server.Main", true))
                .isFalse();
        assertThat(Log4j2Bridge.shouldForward("com.someothermod.Thing", true)).isFalse();
    }

    @Test
    void ignoresThirdPartyLoggersThatMerelyStartWithOurName() {
        assertThat(Log4j2Bridge.shouldForward("IronTanksExtras", true)).isFalse();
        assertThat(Log4j2Bridge.shouldForward("irontanks_addon", true)).isFalse();
        assertThat(Log4j2Bridge.shouldForward("irontanksplus.core", true)).isFalse();
        assertThat(Log4j2Bridge.shouldForward("irontanks-compat/net.example.Thing", true))
                .isFalse();
    }

    @Test
    void ignoresThirdPartyPackagesThatExtendOurPackageName() {
        assertThat(Log4j2Bridge.shouldForward("com.indemnity83.irontanksplus.core", true))
                .isFalse();
        assertThat(Log4j2Bridge.shouldForward("com.indemnity83.irontanks_addon.Thing", true))
                .isFalse();
        assertThat(Log4j2Bridge.shouldForward("net.example.com.indemnity83.irontanks.Thing", true))
                .isFalse();
    }

    @Test
    void ignoresEventsWithoutThrowable() {
        assertThat(Log4j2Bridge.shouldForward("irontanks", false)).isFalse();
    }

    @Test
    void ignoresNullLoggerName() {
        assertThat(Log4j2Bridge.shouldForward(null, true)).isFalse();
    }

    @Test
    void attachesToRealBackendAndForwardsOnlyOurErrors() {
        List<Throwable> sink = new ArrayList<>();
        Log4j2Bridge bridge = new Log4j2Bridge(sink::add);
        bridge.attach();
        try {
            RuntimeException boom = new RuntimeException("boom");
            LogManager.getLogger("irontanks.test").error("kaboom", boom); // forwarded
            LogManager.getLogger("net.other.Thing").error("nope", new RuntimeException()); // wrong logger
            LogManager.getLogger("irontanks.test").error("no throwable"); // no throwable

            assertThat(sink).containsExactly(boom);
        } finally {
            bridge.detach();
        }

        LogManager.getLogger("irontanks.test").error("after detach", new RuntimeException());
        assertThat(sink).hasSize(1); // nothing captured once detached
    }

    @Test
    void degradesQuietlyWhenBackendIsNotLog4j2() {
        List<Throwable> sink = new ArrayList<>();
        Log4j2Bridge bridge = new Log4j2Bridge(sink::add, Object::new); // not a LoggerContext

        bridge.attach(); // backend-unavailable branch; appender never created
        bridge.detach(); // appender == null short-circuits

        assertThat(sink).isEmpty();
    }

    @Test
    void attachFailureLeavesBridgeInert() {
        List<Throwable> sink = new ArrayList<>();
        Log4j2Bridge bridge = new Log4j2Bridge(sink::add, () -> {
            throw new RuntimeException("no context");
        });

        bridge.attach(); // caught; appender stays null
        bridge.detach(); // appender == null short-circuits

        assertThat(sink).isEmpty();
    }

    @Test
    void detachFailureIsContained() {
        List<Throwable> sink = new ArrayList<>();
        AtomicReference<Supplier<Object>> context = new AtomicReference<>(() -> LogManager.getContext(false));
        Log4j2Bridge bridge = new Log4j2Bridge(sink::add, () -> context.get().get());

        bridge.attach(); // succeeds with the real context
        context.set(() -> {
            throw new RuntimeException("boom on detach");
        });
        bridge.detach(); // throws inside detach -> caught, appender cleared in finally

        context.set(() -> LogManager.getContext(false));
        bridge.detach(); // now a safe no-op

        assertThat(sink).isEmpty();
    }

    @Test
    void detachReturnsWhenBackendVanishes() {
        List<Throwable> sink = new ArrayList<>();
        AtomicReference<Supplier<Object>> context = new AtomicReference<>(() -> LogManager.getContext(false));
        Log4j2Bridge bridge = new Log4j2Bridge(sink::add, () -> context.get().get());

        bridge.attach(); // appender set
        context.set(Object::new); // backend is no longer a LoggerContext
        bridge.detach(); // appender != null but context check fails -> returns

        assertThat(sink).isEmpty();
    }
}
