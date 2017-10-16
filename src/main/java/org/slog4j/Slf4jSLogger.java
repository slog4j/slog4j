package org.slog4j;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.event.Level;
import org.slog4j.format.Formatter;

@RequiredArgsConstructor(access = AccessLevel.PACKAGE)
class Slf4jSLogger implements SLogger {
    private final Logger       log;
    private final Formatter    formatter;

    @Override
    public void error(long spanId, String eventId, Object... objs) {
        if (log.isErrorEnabled()) {
            logError(formatter.format(Level.ERROR, spanId, eventId, objs));
        }
    }

    @Override
    public void error(long spanId, String eventId) {
        if (log.isErrorEnabled()) {
            logError(formatter.format(Level.ERROR, spanId, eventId));
        }
    }

    @Override
    public void error(long spanId, String eventId, Object obj) {
        if (log.isErrorEnabled()) {
            logError(formatter.format(Level.ERROR, spanId, eventId, obj));
        }
    }

    @Override
    public void error(long spanId, String eventId, String name, Object value) {
        if (log.isErrorEnabled()) {
            logError(formatter.format(Level.ERROR, spanId, eventId, name, value));
        }
    }

    @Override
    public void error(String eventId, Object... objs) {
        error(NO_SPAN_ID, eventId, objs);
    }

    @Override
    public void error(String eventId) {
        error(NO_SPAN_ID, eventId);
    }

    @Override
    public void error(String eventId, Object obj) {
        error(NO_SPAN_ID, eventId, obj);
    }

    @Override
    public void error(String eventId, String name, Object value) {
        error(NO_SPAN_ID, eventId, name, value);
    }

    private void logError(Formatter.Result result) {
        try {
            if (result.getAttachment() instanceof Throwable) {
                log.error(result.getString(), (Throwable) result.getAttachment());
            } else {
                log.error(result.getString());
            }
        } finally {
            result.clear();
        }
    }

    @Override
    public void warn(long spanId, String eventId, Object... objs) {
        if (log.isWarnEnabled()) {
            logWarn(formatter.format(Level.WARN, spanId, eventId, objs));
        }
    }

    @Override
    public void warn(long spanId, String eventId) {
        if (log.isWarnEnabled()) {
            logWarn(formatter.format(Level.WARN, spanId, eventId));
        }
    }

    @Override
    public void warn(long spanId, String eventId, Object obj) {
        if (log.isWarnEnabled()) {
            logWarn(formatter.format(Level.WARN, spanId, eventId, obj));
        }
    }

    @Override
    public void warn(long spanId, String eventId, String name, Object value) {
        if (log.isWarnEnabled()) {
            logWarn(formatter.format(Level.WARN, spanId, eventId, name, value));
        }
    }

    @Override
    public void warn(String eventId, Object... objs) {
        warn(NO_SPAN_ID, eventId, objs);
    }

    @Override
    public void warn(String eventId) {
        warn(NO_SPAN_ID, eventId);
    }

    @Override
    public void warn(String eventId, Object obj) {
        warn(NO_SPAN_ID, eventId, obj);
    }

    @Override
    public void warn(String eventId, String name, Object value) {
        warn(NO_SPAN_ID, eventId, name, value);
    }

    private void logWarn(Formatter.Result result) {
        try {
            if (result.getAttachment() instanceof Throwable) {
                log.warn(result.getString(), (Throwable) result.getAttachment());
            } else {
                log.warn(result.getString());
            }
        } finally {
            result.clear();
        }
    }

    @Override
    public void info(long spanId, String eventId, Object... objs) {
        if (log.isInfoEnabled()) {
            logInfo(formatter.format(Level.INFO, spanId, eventId, objs));
        }
    }

    @Override
    public void info(long spanId, String eventId) {
        if (log.isInfoEnabled()) {
            logInfo(formatter.format(Level.INFO, spanId, eventId));
        }
    }

    @Override
    public void info(long spanId, String eventId, Object obj) {
        if (log.isInfoEnabled()) {
            logInfo(formatter.format(Level.INFO, spanId, eventId, obj));
        }
    }

    @Override
    public void info(long spanId, String eventId, String name, Object value) {
        if (log.isInfoEnabled()) {
            logInfo(formatter.format(Level.INFO, spanId, eventId, name, value));
        }
    }

    @Override
    public void info(String eventId, Object... objs) {
        info(NO_SPAN_ID, eventId, objs);
    }

    @Override
    public void info(String eventId) {
        info(NO_SPAN_ID, eventId);
    }

    @Override
    public void info(String eventId, Object obj) {
        info(NO_SPAN_ID, eventId, obj);
    }

    @Override
    public void info(String eventId, String name, Object value) {
        info(NO_SPAN_ID, eventId, name, value);
    }

    private void logInfo(Formatter.Result result) {
        try {
            if (result.getAttachment() instanceof Throwable) {
                log.info(result.getString(), (Throwable) result.getAttachment());
            } else {
                log.info(result.getString());
            }
        } finally {
            result.clear();
        }
    }

    @Override
    public void debug(long spanId, String eventId, Object... objs) {
        if (log.isDebugEnabled()) {
            logDebug(formatter.format(Level.DEBUG, spanId, eventId, objs));
        }
    }

    @Override
    public void debug(long spanId, String eventId) {
        if (log.isDebugEnabled()) {
            logDebug(formatter.format(Level.DEBUG, spanId, eventId));
        }
    }

    @Override
    public void debug(long spanId, String eventId, Object obj) {
        if (log.isDebugEnabled()) {
            logDebug(formatter.format(Level.DEBUG, spanId, eventId, obj));
        }
    }

    @Override
    public void debug(long spanId, String eventId, String name, Object value) {
        if (log.isDebugEnabled()) {
            logDebug(formatter.format(Level.DEBUG, spanId, eventId, name, value));
        }
    }

    @Override
    public void debug(String eventId, Object... objs) {
        debug(NO_SPAN_ID, eventId, objs);
    }

    @Override
    public void debug(String eventId) {
        debug(NO_SPAN_ID, eventId);
    }

    @Override
    public void debug(String eventId, Object obj) {
        debug(NO_SPAN_ID, eventId, obj);
    }

    @Override
    public void debug(String eventId, String name, Object value) {
        debug(NO_SPAN_ID, eventId, name, value);
    }

    private void logDebug(Formatter.Result result) {
        try {
            if (result.getAttachment() instanceof Throwable) {
                log.debug(result.getString(), (Throwable) result.getAttachment());
            } else {
                log.debug(result.getString());
            }
        } finally {
            result.clear();
        }
    }

    @Override
    public void trace(long spanId, String eventId, Object... objs) {
        if (log.isTraceEnabled()) {
            logTrace(formatter.format(Level.TRACE, spanId, eventId, objs));
        }
    }

    @Override
    public void trace(long spanId, String eventId) {
        if (log.isTraceEnabled()) {
            logTrace(formatter.format(Level.TRACE, spanId, eventId));
        }
    }

    @Override
    public void trace(long spanId, String eventId, Object obj) {
        if (log.isTraceEnabled()) {
            logTrace(formatter.format(Level.TRACE, spanId, eventId, obj));
        }
    }

    @Override
    public void trace(long spanId, String eventId, String name, Object value) {
        if (log.isTraceEnabled()) {
            logTrace(formatter.format(Level.TRACE, spanId, eventId, name, value));
        }
    }

    @Override
    public void trace(String eventId, Object... objs) {
        trace(NO_SPAN_ID, eventId, objs);
    }

    @Override
    public void trace(String eventId) {
        trace(NO_SPAN_ID, eventId);
    }

    @Override
    public void trace(String eventId, Object obj) {
        trace(NO_SPAN_ID, eventId, obj);
    }

    @Override
    public void trace(String eventId, String name, Object value) {
        trace(NO_SPAN_ID, eventId, name, value);
    }

    private void logTrace(Formatter.Result result) {
        try {
            if (result.getAttachment() instanceof Throwable) {
                log.trace(result.getString(), (Throwable) result.getAttachment());
            } else {
                log.trace(result.getString());
            }
        } finally {
            result.clear();
        }
    }
}
