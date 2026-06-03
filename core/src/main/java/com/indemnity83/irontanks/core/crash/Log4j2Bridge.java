package com.indemnity83.irontanks.core.crash;

import java.util.Locale;
import java.util.function.Consumer;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.core.LogEvent;
import org.apache.logging.log4j.core.LoggerContext;
import org.apache.logging.log4j.core.appender.AbstractAppender;
import org.apache.logging.log4j.core.config.Configuration;
import org.apache.logging.log4j.core.config.Property;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Routes Iron Tanks' own {@code ERROR}-level log events that carry a throwable into the crash
 * reporter. It attaches a forwarding appender to the Log4j2 root logger (Minecraft's logging
 * backend) rather than a single named logger, then filters by logger name so only our errors are
 * captured — never another mod's or vanilla's.
 *
 * <p>Log4j2 is a {@code compileOnly} dependency provided by Minecraft at runtime. Attach/detach are
 * guarded so that if the backend isn't Log4j2 (or anything else goes wrong) the bridge degrades to a
 * no-op instead of breaking. The routing predicate {@link #shouldForward} is pure and unit-tested.
 */
public final class Log4j2Bridge {

    private static final Logger LOGGER = LoggerFactory.getLogger("irontanks/crash");
    private static final String APPENDER_NAME = "IronTanksSentryBridge";

    private final Consumer<Throwable> sink;
    private ForwardingAppender appender;

    public Log4j2Bridge(Consumer<Throwable> sink) {
        this.sink = sink;
    }

    /**
     * True when an event should be forwarded: it carries a throwable and originates from an Iron
     * Tanks logger. Parameterized over primitives so it's testable without constructing a Log4j2
     * {@link LogEvent}.
     */
    public static boolean shouldForward(String loggerName, boolean hasThrowable) {
        if (!hasThrowable || loggerName == null) {
            return false;
        }
        String lower = loggerName.toLowerCase(Locale.ROOT);
        return lower.startsWith("irontanks") || lower.contains("com.indemnity83.irontanks");
    }

    public void attach() {
        try {
            if (!(LogManager.getContext(false) instanceof LoggerContext ctx)) {
                LOGGER.warn("Log4j2 backend unavailable; crash log capture disabled");
                return;
            }
            Configuration config = ctx.getConfiguration();
            appender = new ForwardingAppender(APPENDER_NAME, sink);
            appender.start();
            config.getRootLogger().addAppender(appender, Level.ERROR, null);
            ctx.updateLoggers();
        } catch (Throwable t) {
            LOGGER.warn("Failed to attach crash log capture: {}", t.toString());
            appender = null;
        }
    }

    public void detach() {
        try {
            if (appender == null || !(LogManager.getContext(false) instanceof LoggerContext ctx)) {
                return;
            }
            ctx.getConfiguration().getRootLogger().removeAppender(APPENDER_NAME);
            appender.stop();
            ctx.updateLoggers();
        } catch (Throwable t) {
            LOGGER.warn("Failed to detach crash log capture: {}", t.toString());
        } finally {
            appender = null;
        }
    }

    private static final class ForwardingAppender extends AbstractAppender {

        private final Consumer<Throwable> sink;

        ForwardingAppender(String name, Consumer<Throwable> sink) {
            super(name, null, null, true, Property.EMPTY_ARRAY);
            this.sink = sink;
        }

        @Override
        public void append(LogEvent event) {
            if (shouldForward(event.getLoggerName(), event.getThrown() != null)) {
                sink.accept(event.getThrown());
            }
        }
    }
}
