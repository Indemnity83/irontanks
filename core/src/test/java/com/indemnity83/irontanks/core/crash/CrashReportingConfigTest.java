package com.indemnity83.irontanks.core.crash;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;

class CrashReportingConfigTest {

    @Test
    void defaultsAreOptInWithBundledDsn() {
        CrashReportingConfig config = new CrashReportingConfig();

        assertThat(config.enabled()).isFalse();
        assertThat(config.notifyOperators()).isTrue();
        assertThat(config.dsnOverride()).isEmpty();
    }

    @Test
    void dsnOverrideNeverReturnsNull() {
        CrashReportingConfig config = new CrashReportingConfig();

        config.setDsnOverride(null);

        assertThat(config.dsnOverride()).isEmpty();
    }

    @Test
    void settersRoundTrip() {
        CrashReportingConfig config = new CrashReportingConfig();

        config.setEnabled(true);
        config.setNotifyOperators(false);
        config.setDsnOverride("https://key@example.test/1");

        assertThat(config.enabled()).isTrue();
        assertThat(config.notifyOperators()).isFalse();
        assertThat(config.dsnOverride()).isEqualTo("https://key@example.test/1");
    }
}
