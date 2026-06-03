package com.indemnity83.irontanks.core.crash;

/**
 * The {@code crashReporting} section of {@link IronTanksConfig}. Opt-in: everything stays off until
 * an operator runs {@code /irontanks diagnostics enable}. Gson (de)serializes the private fields by
 * name, so the on-disk shape is {@code {"enabled": false, "notifyOperators": true, "dsnOverride": ""}}.
 */
public final class CrashReportingConfig {

    /** Whether sanitized crash reports are sent. Opt-in: off until an operator enables it. */
    private boolean enabled = false;

    /** Whether operators are invited to opt in on join (until they silence the notice). */
    private boolean notifyOperators = true;

    /** Optional DSN override for self-hosting/testing; empty uses the bundled default. */
    private String dsnOverride = "";

    public boolean enabled() {
        return enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    public boolean notifyOperators() {
        return notifyOperators;
    }

    public void setNotifyOperators(boolean notifyOperators) {
        this.notifyOperators = notifyOperators;
    }

    public String dsnOverride() {
        return dsnOverride == null ? "" : dsnOverride;
    }

    public void setDsnOverride(String dsnOverride) {
        this.dsnOverride = dsnOverride;
    }
}
