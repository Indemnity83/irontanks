package com.indemnity83.irontanks.core.crash;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;

class Log4j2BridgeTest {

    @Test
    void forwardsIronTanksErrorsWithThrowable() {
        assertThat(Log4j2Bridge.shouldForward("irontanks", true)).isTrue();
        assertThat(Log4j2Bridge.shouldForward("irontanks/crash", true)).isTrue();
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
    void ignoresEventsWithoutThrowable() {
        assertThat(Log4j2Bridge.shouldForward("irontanks", false)).isFalse();
    }

    @Test
    void ignoresNullLoggerName() {
        assertThat(Log4j2Bridge.shouldForward(null, true)).isFalse();
    }
}
