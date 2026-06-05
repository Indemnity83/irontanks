package com.indemnity83.irontanks.core.crash;

import static org.assertj.core.api.Assertions.assertThat;

import io.sentry.Breadcrumb;
import io.sentry.SentryEvent;
import io.sentry.protocol.Message;
import io.sentry.protocol.SentryException;
import java.util.List;
import org.junit.jupiter.api.Test;

class LogScrubberTest {

    @Test
    void redactsGenericHomeDirectories() {
        assertThat(LogScrubber.redact("saving /Users/alice/world")).isEqualTo("saving /Users/<user>/world");
        assertThat(LogScrubber.redact("saving /home/bob/world")).isEqualTo("saving /home/<user>/world");
        assertThat(LogScrubber.redact("at C:\\Users\\carol\\saves")).isEqualTo("at C:\\Users\\<user>\\saves");
    }

    @Test
    void redactsUuidsAndIpAddresses() {
        assertThat(LogScrubber.redact("player 550e8400-e29b-41d4-a716-446655440000 joined"))
                .isEqualTo("player <uuid> joined");
        assertThat(LogScrubber.redact("from 203.0.113.7 now")).isEqualTo("from <ip> now");
    }

    @Test
    void redactsSecretLikeKeyValues() {
        assertThat(LogScrubber.redact("token=s3cr3t-abc")).isEqualTo("token=<redacted>");
        assertThat(LogScrubber.redact("password: hunter2")).isEqualTo("password: <redacted>");
        assertThat(LogScrubber.redact("api_key = ABC123")).isEqualTo("api_key = <redacted>");
        assertThat(LogScrubber.redact("dsn=https://abc@example.com/1")).isEqualTo("dsn=<redacted>");
    }

    @Test
    void leavesOrdinaryTextUntouched() {
        String safe = "Tank column at 12, 64, -8 settled 16000 mB of water";
        assertThat(LogScrubber.redact(safe)).isEqualTo(safe);
    }

    @Test
    void isIdempotent() {
        String once = LogScrubber.redact("u 550e8400-e29b-41d4-a716-446655440000 ip 203.0.113.7 token=abc");
        assertThat(LogScrubber.redact(once)).isEqualTo(once);
    }

    @Test
    void redactsWholeWordUsernameButNotEmbeddedOrShortMatches() {
        String original = System.getProperty("user.name");
        try {
            System.setProperty("user.name", "root");
            assertThat(LogScrubber.redact("logged in as root")).isEqualTo("logged in as <user>");
            assertThat(LogScrubber.redact("rootCause: boom")).isEqualTo("rootCause: boom");

            // Names shorter than 3 chars are too collision-prone to replace at all.
            System.setProperty("user.name", "mc");
            assertThat(LogScrubber.redact("running as mc on mcServer")).isEqualTo("running as mc on mcServer");
        } finally {
            if (original != null) {
                System.setProperty("user.name", original);
            } else {
                System.clearProperty("user.name");
            }
        }
    }

    @Test
    void handlesNullAndEmpty() {
        assertThat(LogScrubber.redact(null)).isNull();
        assertThat(LogScrubber.redact("")).isEmpty();
    }

    @Test
    void scrubsMessageExceptionAndDropsServerName() {
        SentryEvent event = new SentryEvent();
        event.setServerName("my-secret-host");
        Message message = new Message();
        message.setMessage("crash for 550e8400-e29b-41d4-a716-446655440000 from 203.0.113.7");
        message.setFormatted(message.getMessage());
        event.setMessage(message);
        SentryException exception = new SentryException();
        exception.setValue("failed at /home/dave/world with token=abc123");
        event.setExceptions(List.of(exception));

        LogScrubber.scrub(event);

        assertThat(event.getServerName()).isNull();
        assertThat(event.getMessage().getMessage()).isEqualTo("crash for <uuid> from <ip>");
        assertThat(event.getMessage().getFormatted()).isEqualTo("crash for <uuid> from <ip>");
        assertThat(event.getExceptions().get(0).getValue())
                .isEqualTo("failed at /home/<user>/world with token=<redacted>");
    }

    @Test
    void scrubNullEventReturnsNull() {
        assertThat(LogScrubber.scrub(null)).isNull();
    }

    @Test
    void scrubsBreadcrumbsAndToleratesMissingMessageAndExceptions() {
        SentryEvent event = new SentryEvent();
        Breadcrumb crumb = new Breadcrumb();
        crumb.setMessage("saving /home/erin/world from 203.0.113.7");
        event.addBreadcrumb(crumb);

        LogScrubber.scrub(event);

        assertThat(event.getMessage()).isNull();
        assertThat(event.getExceptions()).isNull();
        assertThat(event.getBreadcrumbs().get(0).getMessage()).isEqualTo("saving /home/<user>/world from <ip>");
    }

    @Test
    void toleratesMissingHomeAndUserProperties() {
        String home = System.getProperty("user.home");
        String user = System.getProperty("user.name");
        try {
            System.clearProperty("user.home");
            System.clearProperty("user.name");
            // Generic path/IP redaction still works without the local identity properties present.
            assertThat(LogScrubber.redact("at /Users/frank/x from 203.0.113.7"))
                    .isEqualTo("at /Users/<user>/x from <ip>");

            System.setProperty("user.home", ""); // present but blank
            assertThat(LogScrubber.redact("blank home from 203.0.113.7")).isEqualTo("blank home from <ip>");
        } finally {
            restore("user.home", home);
            restore("user.name", user);
        }
    }

    private static void restore(String key, String value) {
        if (value != null) {
            System.setProperty(key, value);
        } else {
            System.clearProperty(key);
        }
    }
}
