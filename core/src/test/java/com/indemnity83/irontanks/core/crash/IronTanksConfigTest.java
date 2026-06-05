package com.indemnity83.irontanks.core.crash;

import static org.assertj.core.api.Assertions.assertThat;

import com.google.gson.Gson;
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

    @Test
    void nullPathYieldsInMemoryDefaults() {
        IronTanksConfig config = IronTanksConfig.load(null);

        assertThat(config.crashReporting().enabled()).isFalse();
    }

    @Test
    void nullJsonContentFallsBackToDefaults(@TempDir Path dir) throws Exception {
        Path file = dir.resolve("irontanks.json");
        Files.writeString(file, "null"); // Gson parses this to a null object

        IronTanksConfig config = IronTanksConfig.load(file);

        assertThat(config.crashReporting().enabled()).isFalse();
    }

    @Test
    void missingCrashReportingSectionIsRecreatedOnLoad(@TempDir Path dir) throws Exception {
        Path file = dir.resolve("irontanks.json");
        Files.writeString(file, "{\"crashReporting\":null}");

        IronTanksConfig config = IronTanksConfig.load(file);

        assertThat(config.crashReporting()).isNotNull();
        assertThat(config.crashReporting().enabled()).isFalse();
    }

    @Test
    void crashReportingGetterRecreatesNullSection() {
        IronTanksConfig config = new Gson().fromJson("{\"crashReporting\":null}", IronTanksConfig.class);

        assertThat(config.crashReporting()).isNotNull();
    }

    @Test
    void saveToNullPathIsNoOp() {
        new IronTanksConfig().save(null); // must not throw
    }

    @Test
    void savesToPathWithoutParentDirectory() throws Exception {
        Path bare = Path.of("irontanks-coverage-tmp.json"); // relative -> no parent

        try {
            new IronTanksConfig().save(bare);
            assertThat(Files.exists(bare)).isTrue();
        } finally {
            Files.deleteIfExists(bare);
        }
    }

    @Test
    void saveFailureIsSwallowed(@TempDir Path dir) throws Exception {
        Path blocker = dir.resolve("blocker");
        Files.writeString(blocker, "x"); // a regular file where a directory would be needed
        Path target = blocker.resolve("child.json");

        new IronTanksConfig().save(target); // createDirectories fails, but save must not throw

        assertThat(Files.isRegularFile(blocker)).isTrue();
    }
}
