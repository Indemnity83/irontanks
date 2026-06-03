package com.indemnity83.irontanks.core.crash;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import java.nio.file.Files;
import java.nio.file.Path;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Iron Tanks' general configuration file (a single {@code config/irontanks.json}). Today it holds
 * only the {@link CrashReportingConfig} section, but it's shaped as a general config root so future
 * settings are just new fields here. Pure Gson file I/O — the config-dir {@link Path} is injected by
 * each loader, so {@code core} stays free of Minecraft.
 *
 * <p>Reads and writes are best-effort: a missing or corrupt file yields defaults (and rewrites a
 * clean file) rather than throwing into callers, so a bad config can never break mod load.
 */
public final class IronTanksConfig {

    private static final Logger LOGGER = LoggerFactory.getLogger("irontanks/config");
    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();

    private CrashReportingConfig crashReporting = new CrashReportingConfig();

    public CrashReportingConfig crashReporting() {
        if (crashReporting == null) {
            crashReporting = new CrashReportingConfig();
        }
        return crashReporting;
    }

    /**
     * Load the config from {@code file}. A missing file is created with defaults; a corrupt file
     * falls back to defaults (and is left as-is on disk only if it can't be rewritten). Never throws.
     */
    public static IronTanksConfig load(Path file) {
        if (file == null) {
            return new IronTanksConfig();
        }
        try {
            if (Files.exists(file)) {
                IronTanksConfig parsed = GSON.fromJson(Files.readString(file), IronTanksConfig.class);
                if (parsed != null) {
                    parsed.ensureSections();
                    return parsed;
                }
            }
        } catch (Exception e) {
            LOGGER.warn("Could not read {}; using defaults: {}", file, e.toString());
        }
        IronTanksConfig defaults = new IronTanksConfig();
        defaults.save(file);
        return defaults;
    }

    /** Persist the current values to {@code file}, creating parent directories as needed. Never throws. */
    public void save(Path file) {
        if (file == null) {
            return;
        }
        try {
            if (file.getParent() != null) {
                Files.createDirectories(file.getParent());
            }
            Files.writeString(file, GSON.toJson(this));
        } catch (Exception e) {
            LOGGER.warn("Could not write {}: {}", file, e.toString());
        }
    }

    private void ensureSections() {
        if (crashReporting == null) {
            crashReporting = new CrashReportingConfig();
        }
    }
}
