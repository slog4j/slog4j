package org.slog4j;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.UtilityClass;
import lombok.experimental.Wither;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.event.Level;

@UtilityClass
public class SLoggerFactory {

    public static SLogger getLogger(Class<?> clazz) {
        return getLogger(LoggerFactory.getLogger(clazz));
    }

    public static SLogger getLogger(Logger log) {
        return new Slf4jSLogger(log, TextFormatter.INSTANCE, TimeProviders.SYSTEM);
    }

    @RequiredArgsConstructor(access = AccessLevel.PRIVATE)
    private static class Slf4jSLogger implements SLogger {
        private final Logger       log;
        @Wither
        private final Formatter    formatter;
        @Wither
        private final TimeProvider timeProvider;

        @Override
        public void error(long spanId, String eventId) {
            if (log.isErrorEnabled()) {
                log.error(formatter.format(timeProvider, Level.ERROR, spanId, eventId));
            }
        }

        @Override
        public void error(long spanId, String eventId, Object obj) {
            if (log.isErrorEnabled()) {
                log.error(formatter.format(timeProvider, Level.ERROR, spanId, eventId, obj));
            }
        }

        @Override
        public void error(long spanId, String eventId, String key, Object value) {
            if (log.isErrorEnabled()) {
                log.error(formatter.format(timeProvider, Level.ERROR, spanId, eventId, key, value));
            }
        }

        @Override
        public void error(long spanId, String eventId, Object... fields) {
            if (log.isErrorEnabled()) {
                log.error(formatter.format(timeProvider, Level.ERROR, spanId, eventId, fields));
            }
        }

        @Override
        public void error(String eventId) {
            if (log.isErrorEnabled()) {
                log.error(formatter.format(timeProvider, Level.ERROR, eventId));
            }
        }

        @Override
        public void error(String eventId, Object obj) {
            if (log.isErrorEnabled()) {
                log.error(formatter.format(timeProvider, Level.ERROR, eventId, obj));
            }
        }

        @Override
        public void error(String eventId, String key, Object value) {
            if (log.isErrorEnabled()) {
                log.error(formatter.format(timeProvider, Level.ERROR, eventId, key, value));
            }
        }

        @Override
        public void error(String eventId, Object... fields) {
            if (log.isErrorEnabled()) {
                log.error(formatter.format(timeProvider, Level.ERROR, eventId, fields));
            }
        }

        @Override
        public void warn(long spanId, String eventId) {
            if (log.isWarnEnabled()) {
                log.warn(formatter.format(timeProvider, Level.WARN, spanId, eventId));
            }
        }

        @Override
        public void warn(long spanId, String eventId, Object obj) {
            if (log.isWarnEnabled()) {
                log.warn(formatter.format(timeProvider, Level.WARN, spanId, eventId, obj));
            }
        }

        @Override
        public void warn(long spanId, String eventId, String key, Object value) {
            if (log.isWarnEnabled()) {
                log.warn(formatter.format(timeProvider, Level.WARN, spanId, eventId, key, value));
            }
        }

        @Override
        public void warn(long spanId, String eventId, Object... fields) {
            if (log.isWarnEnabled()) {
                log.warn(formatter.format(timeProvider, Level.WARN, spanId, eventId, fields));
            }
        }

        @Override
        public void warn(String eventId) {
            if (log.isWarnEnabled()) {
                log.warn(formatter.format(timeProvider, Level.WARN, eventId));
            }
        }

        @Override
        public void warn(String eventId, Object obj) {
            if (log.isWarnEnabled()) {
                log.warn(formatter.format(timeProvider, Level.WARN, eventId, obj));
            }
        }

        @Override
        public void warn(String eventId, String key, Object value) {
            if (log.isWarnEnabled()) {
                log.warn(formatter.format(timeProvider, Level.WARN, eventId, key, value));
            }
        }

        @Override
        public void warn(String eventId, Object... fields) {
            if (log.isWarnEnabled()) {
                log.warn(formatter.format(timeProvider, Level.WARN, eventId, fields));
            }
        }

        @Override
        public void info(long spanId, String eventId) {
            if (log.isInfoEnabled()) {
                log.info(formatter.format(timeProvider, Level.INFO, spanId, eventId));
            }
        }

        @Override
        public void info(long spanId, String eventId, Object obj) {
            if (log.isInfoEnabled()) {
                log.info(formatter.format(timeProvider, Level.INFO, spanId, eventId, obj));
            }
        }

        @Override
        public void info(long spanId, String eventId, String key, Object value) {
            if (log.isInfoEnabled()) {
                log.info(formatter.format(timeProvider, Level.INFO, spanId, eventId, key, value));
            }
        }

        @Override
        public void info(long spanId, String eventId, Object... fields) {
            if (log.isInfoEnabled()) {
                log.info(formatter.format(timeProvider, Level.INFO, spanId, eventId, fields));
            }
        }

        @Override
        public void info(String eventId) {
            if (log.isInfoEnabled()) {
                log.info(formatter.format(timeProvider, Level.INFO, eventId));
            }
        }

        @Override
        public void info(String eventId, Object obj) {
            if (log.isInfoEnabled()) {
                log.info(formatter.format(timeProvider, Level.INFO, eventId, obj));
            }
        }

        @Override
        public void info(String eventId, String key, Object value) {
            if (log.isInfoEnabled()) {
                log.info(formatter.format(timeProvider, Level.INFO, eventId, key, value));
            }
        }

        @Override
        public void info(String eventId, Object... fields) {
            if (log.isInfoEnabled()) {
                log.info(formatter.format(timeProvider, Level.INFO, eventId, fields));
            }
        }

        @Override
        public void debug(long spanId, String eventId) {
            if (log.isDebugEnabled()) {
                log.debug(formatter.format(timeProvider, Level.DEBUG, spanId, eventId));
            }
        }

        @Override
        public void debug(long spanId, String eventId, Object obj) {
            if (log.isDebugEnabled()) {
                log.debug(formatter.format(timeProvider, Level.DEBUG, spanId, eventId, obj));
            }
        }

        @Override
        public void debug(long spanId, String eventId, String key, Object value) {
            if (log.isDebugEnabled()) {
                log.debug(formatter.format(timeProvider, Level.DEBUG, spanId, eventId, key, value));
            }
        }

        @Override
        public void debug(long spanId, String eventId, Object... fields) {
            if (log.isDebugEnabled()) {
                log.debug(formatter.format(timeProvider, Level.DEBUG, spanId, eventId, fields));
            }
        }

        @Override
        public void debug(String eventId) {
            if (log.isDebugEnabled()) {
                log.debug(formatter.format(timeProvider, Level.DEBUG, eventId));
            }
        }

        @Override
        public void debug(String eventId, Object obj) {
            if (log.isDebugEnabled()) {
                log.debug(formatter.format(timeProvider, Level.DEBUG, eventId, obj));
            }
        }

        @Override
        public void debug(String eventId, String key, Object value) {
            if (log.isDebugEnabled()) {
                log.debug(formatter.format(timeProvider, Level.DEBUG, eventId, key, value));
            }
        }

        @Override
        public void debug(String eventId, Object... fields) {
            if (log.isDebugEnabled()) {
                log.debug(formatter.format(timeProvider, Level.DEBUG, eventId, fields));
            }
        }

        @Override
        public void trace(long spanId, String eventId) {
            if (log.isTraceEnabled()) {
                log.trace(formatter.format(timeProvider, Level.TRACE, spanId, eventId));
            }
        }

        @Override
        public void trace(long spanId, String eventId, Object obj) {
            if (log.isTraceEnabled()) {
                log.trace(formatter.format(timeProvider, Level.TRACE, spanId, eventId, obj));
            }
        }

        @Override
        public void trace(long spanId, String eventId, String key, Object value) {
            if (log.isTraceEnabled()) {
                log.trace(formatter.format(timeProvider, Level.TRACE, spanId, eventId, key, value));
            }
        }

        @Override
        public void trace(long spanId, String eventId, Object... fields) {
            if (log.isTraceEnabled()) {
                log.trace(formatter.format(timeProvider, Level.TRACE, spanId, eventId, fields));
            }
        }

        @Override
        public void trace(String eventId) {
            if (log.isTraceEnabled()) {
                log.trace(formatter.format(timeProvider, Level.TRACE, eventId));
            }
        }

        @Override
        public void trace(String eventId, Object obj) {
            if (log.isTraceEnabled()) {
                log.trace(formatter.format(timeProvider, Level.TRACE, eventId, obj));
            }
        }

        @Override
        public void trace(String eventId, String key, Object value) {
            if (log.isTraceEnabled()) {
                log.trace(formatter.format(timeProvider, Level.TRACE, eventId, key, value));
            }
        }

        @Override
        public void trace(String eventId, Object... fields) {
            if (log.isTraceEnabled()) {
                log.trace(formatter.format(timeProvider, Level.TRACE, eventId, fields));
            }
        }
    }
}

