package org.slog4j;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.UtilityClass;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@UtilityClass
public class SLoggerFactory {

    public static SLogger getLogger(Class<?> clazz) {
        return getLogger(clazz, TextFormatter.INSTANCE);
    }

    public static SLogger getLogger(Class<?> clazz, Formatter formatter) {
        return getLogger(LoggerFactory.getLogger(clazz), formatter);
    }

    public static SLogger getLogger(Logger log) {
        return getLogger(log, TextFormatter.INSTANCE);
    }

    public static SLogger getLogger(Logger log, Formatter formatter) {
        return new Slf4jSLogger(log, formatter);
    }

    @RequiredArgsConstructor(access = AccessLevel.PRIVATE)
    private static class Slf4jSLogger implements SLogger {
        private final Logger    log;
        private final Formatter formatter;

        @Override
        public void error(long spanId, String eventId) {
            if (log.isErrorEnabled()) {
                log.error(formatter.format(spanId, eventId));
            }
        }

        @Override
        public void error(long spanId, String eventId, Object obj) {
            if (log.isErrorEnabled()) {
                log.error(formatter.format(spanId, eventId, obj));
            }
        }

        @Override
        public void error(long spanId, String eventId, String key, Object value) {
            if (log.isErrorEnabled()) {
                log.error(formatter.format(spanId, eventId, key, value));
            }
        }

        @Override
        public void error(long spanId, String eventId, Object... fields) {
            if (log.isErrorEnabled()) {
                log.error(formatter.format(spanId, eventId, fields));
            }
        }

        @Override
        public void error(String eventId) {
            if (log.isErrorEnabled()) {
                log.error(formatter.format(eventId));
            }
        }

        @Override
        public void error(String eventId, Object obj) {
            if (log.isErrorEnabled()) {
                log.error(formatter.format(eventId, obj));
            }
        }

        @Override
        public void error(String eventId, String key, Object value) {
            if (log.isErrorEnabled()) {
                log.error(formatter.format(eventId, key, value));
            }
        }

        @Override
        public void error(String eventId, Object... fields) {
            if (log.isErrorEnabled()) {
                log.error(formatter.format(eventId, fields));
            }
        }

        @Override
        public void warn(long spanId, String eventId) {
            if (log.isWarnEnabled()) {
                log.warn(formatter.format(spanId, eventId));
            }
        }

        @Override
        public void warn(long spanId, String eventId, Object obj) {
            if (log.isWarnEnabled()) {
                log.warn(formatter.format(spanId, eventId, obj));
            }
        }

        @Override
        public void warn(long spanId, String eventId, String key, Object value) {
            if (log.isWarnEnabled()) {
                log.warn(formatter.format(spanId, eventId, key, value));
            }
        }

        @Override
        public void warn(long spanId, String eventId, Object... fields) {
            if (log.isWarnEnabled()) {
                log.warn(formatter.format(spanId, eventId, fields));
            }
        }

        @Override
        public void warn(String eventId) {
            if (log.isWarnEnabled()) {
                log.warn(formatter.format(eventId));
            }
        }

        @Override
        public void warn(String eventId, Object obj) {
            if (log.isWarnEnabled()) {
                log.warn(formatter.format(eventId, obj));
            }
        }

        @Override
        public void warn(String eventId, String key, Object value) {
            if (log.isWarnEnabled()) {
                log.warn(formatter.format(eventId, key, value));
            }
        }

        @Override
        public void warn(String eventId, Object... fields) {
            if (log.isWarnEnabled()) {
                log.warn(formatter.format(eventId, fields));
            }
        }

        @Override
        public void info(long spanId, String eventId) {
            if (log.isInfoEnabled()) {
                log.info(formatter.format(spanId, eventId));
            }
        }

        @Override
        public void info(long spanId, String eventId, Object obj) {
            if (log.isInfoEnabled()) {
                log.info(formatter.format(spanId, eventId, obj));
            }
        }

        @Override
        public void info(long spanId, String eventId, String key, Object value) {
            if (log.isInfoEnabled()) {
                log.info(formatter.format(spanId, eventId, key, value));
            }
        }

        @Override
        public void info(long spanId, String eventId, Object... fields) {
            if (log.isInfoEnabled()) {
                log.info(formatter.format(spanId, eventId, fields));
            }
        }

        @Override
        public void info(String eventId) {
            if (log.isInfoEnabled()) {
                log.info(formatter.format(eventId));
            }
        }

        @Override
        public void info(String eventId, Object obj) {
            if (log.isInfoEnabled()) {
                log.info(formatter.format(eventId, obj));
            }
        }

        @Override
        public void info(String eventId, String key, Object value) {
            if (log.isInfoEnabled()) {
                log.info(formatter.format(eventId, key, value));
            }
        }

        @Override
        public void info(String eventId, Object... fields) {
            if (log.isInfoEnabled()) {
                log.info(formatter.format(eventId, fields));
            }
        }

        @Override
        public void debug(long spanId, String eventId) {
            if (log.isDebugEnabled()) {
                log.debug(formatter.format(spanId, eventId));
            }
        }

        @Override
        public void debug(long spanId, String eventId, Object obj) {
            if (log.isDebugEnabled()) {
                log.debug(formatter.format(spanId, eventId, obj));
            }
        }

        @Override
        public void debug(long spanId, String eventId, String key, Object value) {
            if (log.isDebugEnabled()) {
                log.debug(formatter.format(spanId, eventId, key, value));
            }
        }

        @Override
        public void debug(long spanId, String eventId, Object... fields) {
            if (log.isDebugEnabled()) {
                log.debug(formatter.format(spanId, eventId, fields));
            }
        }

        @Override
        public void debug(String eventId) {
            if (log.isDebugEnabled()) {
                log.debug(formatter.format(eventId));
            }
        }

        @Override
        public void debug(String eventId, Object obj) {
            if (log.isDebugEnabled()) {
                log.debug(formatter.format(eventId, obj));
            }
        }

        @Override
        public void debug(String eventId, String key, Object value) {
            if (log.isDebugEnabled()) {
                log.debug(formatter.format(eventId, key, value));
            }
        }

        @Override
        public void debug(String eventId, Object... fields) {
            if (log.isDebugEnabled()) {
                log.debug(formatter.format(eventId, fields));
            }
        }

        @Override
        public void trace(long spanId, String eventId) {
            if (log.isTraceEnabled()) {
                log.trace(formatter.format(spanId, eventId));
            }
        }

        @Override
        public void trace(long spanId, String eventId, Object obj) {
            if (log.isTraceEnabled()) {
                log.trace(formatter.format(spanId, eventId, obj));
            }
        }

        @Override
        public void trace(long spanId, String eventId, String key, Object value) {
            if (log.isTraceEnabled()) {
                log.trace(formatter.format(spanId, eventId, key, value));
            }
        }

        @Override
        public void trace(long spanId, String eventId, Object... fields) {
            if (log.isTraceEnabled()) {
                log.trace(formatter.format(spanId, eventId, fields));
            }
        }

        @Override
        public void trace(String eventId) {
            if (log.isTraceEnabled()) {
                log.trace(formatter.format(eventId));
            }
        }

        @Override
        public void trace(String eventId, Object obj) {
            if (log.isTraceEnabled()) {
                log.trace(formatter.format(eventId, obj));
            }
        }

        @Override
        public void trace(String eventId, String key, Object value) {
            if (log.isTraceEnabled()) {
                log.trace(formatter.format(eventId, key, value));
            }
        }

        @Override
        public void trace(String eventId, Object... fields) {
            if (log.isTraceEnabled()) {
                log.trace(formatter.format(eventId, fields));
            }
        }
    }
}

