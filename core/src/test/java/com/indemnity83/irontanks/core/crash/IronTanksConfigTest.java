package com.indemnity83.irontanks.core.crash;

import static org.assertj.core.api.Assertions.assertThat;

import java.nio.file.Files;
import java.nio.file.Path;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

class IronTanksConfigTest {

    @Test
    void missingFileYieldsDefaultsAndWritesIt(@TempDir Path dir) throws Exception {
        Path file = dir.resolve("irontanks.json");

        IronTanksConfig config = IronTanksConfig.load(file);

        assertThat(config.crashReporting().enabled()).isFalse();
        assertThat(config.crashReporting().notifyOperators()).isTrue();
        assertThat(config.crashReporting().dsnOverride()).isEmpty();
        assertThat(Files.exists(file)).isTrue();
    }

    @Test
    void roundTripsChangedValues(@TempDir Path dir) {
        Path file = dir.resolve("irontanks.json");
        IronTanksConfig config = IronTanksConfig.load(file);
        config.crashReporting().setEnabled(true);
        config.crashReporting().setNotifyOperators(false);
        config.crashReporting().setDsnOverride("https://override@example.com/9");
        config.save(file);

        IronTanksConfig reloaded = IronTanksConfig.load(file);

        assertThat(reloaded.crashReporting().enabled()).isTrue();
        assertThat(reloaded.crashReporting().notifyOperators()).isFalse();
        assertThat(reloaded.crashReporting().dsnOverride()).isEqualTo("https://override@example.com/9");
    }

    @Test
    void corruptFileFallsBackToDefaults(@TempDir Path dir) throws Exception {
        Path file = dir.resolve("irontanks.json");
        Files.writeString(file, "{ this is not valid json ");

        IronTanksConfig config = IronTanksConfig.load(file);

        assertThat(config.crashReporting().enabled()).isFalse();
        assertThat(config.crashReporting().notifyOperators()).isTrue();
    }
}
