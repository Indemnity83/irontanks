package com.indemnity83.irontanks.core.crash;

import io.sentry.Breadcrumb;
import io.sentry.SentryEvent;
import io.sentry.protocol.Message;
import io.sentry.protocol.SentryException;
import java.util.regex.Pattern;

/**
 * Strips identifying substrings from an event before it leaves the process. Best-effort and
 * intentionally biased toward over-redaction: when in doubt, it strips. Pure string work, so the
 * whole thing is unit-tested in {@code core} without a running game.
 *
 * <p>Stack frames carry only source file names (not absolute paths), so only free-text fields —
 * the message, exception values, and breadcrumb messages — need scrubbing.
 */
public final class LogScrubber {

    private LogScrubber() {}

    private static final Pattern IPV4 = Pattern.compile("\\b\\d{1,3}(?:\\.\\d{1,3}){3}\\b");
    private static final Pattern UUID =
            Pattern.compile("\\b[0-9a-fA-F]{8}-[0-9a-fA-F]{4}-[0-9a-fA-F]{4}-[0-9a-fA-F]{4}-[0-9a-fA-F]{12}\\b");
    private static final Pattern UNIX_HOME = Pattern.compile("(/(?:Users|home))/[^/\\s]+");
    private static final Pattern WINDOWS_HOME = Pattern.compile("([A-Za-z]:\\\\Users\\\\)[^\\\\\\s]+");
    private static final Pattern SECRET_KV =
            Pattern.compile("(?i)(password|passwd|token|secret|api[_-]?key|access[_-]?key|dsn)(\\s*[=:]\\s*)\\S+");

    /** Scrub an event in place (message, exception values, breadcrumb messages) and drop the server name. */
    public static SentryEvent scrub(SentryEvent event) {
        if (event == null) {
            return null;
        }
        event.setServerName(null);
        Message message = event.getMessage();
        if (message != null) {
            message.setFormatted(redact(message.getFormatted()));
            message.setMessage(redact(message.getMessage()));
        }
        if (event.getExceptions() != null) {
            for (SentryException ex : event.getExceptions()) {
                ex.setValue(redact(ex.getValue()));
            }
        }
        if (event.getBreadcrumbs() != null) {
            for (Breadcrumb breadcrumb : event.getBreadcrumbs()) {
                breadcrumb.setMessage(redact(breadcrumb.getMessage()));
            }
        }
        return event;
    }

    /**
     * Redact identifying substrings from a single string: this machine's home dir / username, generic
     * {@code /Users|/home/<name>} and {@code C:\Users\<name>} paths, UUIDs, IPv4 addresses, and
     * secret-like {@code key=value} pairs. Idempotent — running it twice is stable.
     */
    public static String redact(String value) {
        if (value == null || value.isEmpty()) {
            return value;
        }
        String home = System.getProperty("user.home");
        String user = System.getProperty("user.name");
        if (home != null && !home.isBlank()) {
            value = value.replace(home, "~");
        }
        // Whole-word match only, and skip very short logins, so a common username like "root" can't
        // mangle unrelated text such as "rootCause". (Home-dir paths are handled separately above.)
        if (user != null && user.length() >= 3) {
            value = value.replaceAll("\\b" + Pattern.quote(user) + "\\b", "<user>");
        }
        value = UNIX_HOME.matcher(value).replaceAll("$1/<user>");
        value = WINDOWS_HOME.matcher(value).replaceAll("$1<user>");
        value = UUID.matcher(value).replaceAll("<uuid>");
        value = IPV4.matcher(value).replaceAll("<ip>");
        value = SECRET_KV.matcher(value).replaceAll("$1$2<redacted>");
        return value;
    }
}
