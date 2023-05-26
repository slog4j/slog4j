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
    public void error(String eventId, Object... objs) {
        if (log.isErrorEnabled()) {
            logError(formatter.format(Level.ERROR, eventId, objs));
        }
    }

    @Override
    public void error(String eventId) {
        if (log.isErrorEnabled()) {
            logError(formatter.format(Level.ERROR, eventId));
        }
    }

    @Override
    public void error(String eventId, Object obj) {
        if (log.isErrorEnabled()) {
            logError(formatter.format(Level.ERROR, eventId, obj));
        }
    }

    @Override
    public void error(String eventId, String name, Object value) {
        if (log.isErrorEnabled()) {
            logError(formatter.format(Level.ERROR, eventId, name, value));
        }
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
    public void warn(String eventId, Object... objs) {
        if (log.isWarnEnabled()) {
            logWarn(formatter.format(Level.WARN, eventId, objs));
        }
    }

    @Override
    public void warn(String eventId) {
        if (log.isWarnEnabled()) {
            logWarn(formatter.format(Level.WARN, eventId));
        }
    }

    @Override
    public void warn(String eventId, Object obj) {
        if (log.isWarnEnabled()) {
            logWarn(formatter.format(Level.WARN, eventId, obj));
        }
    }

    @Override
    public void warn(String eventId, String name, Object value) {
        if (log.isWarnEnabled()) {
            logWarn(formatter.format(Level.WARN, eventId, name, value));
        }
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
    public void info(String eventId, Object... objs) {
        if (log.isInfoEnabled()) {
            logInfo(formatter.format(Level.INFO, eventId, objs));
        }
    }

    @Override
    public void info(String eventId) {
        if (log.isInfoEnabled()) {
            logInfo(formatter.format(Level.INFO, eventId));
        }
    }

    @Override
    public void info(String eventId, Object obj) {
        if (log.isInfoEnabled()) {
            logInfo(formatter.format(Level.INFO, eventId, obj));
        }
    }

    @Override
    public void info(String eventId, String name, Object value) {
        if (log.isInfoEnabled()) {
            logInfo(formatter.format(Level.INFO, eventId, name, value));
        }
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
    public void debug(String eventId, Object... objs) {
        if (log.isDebugEnabled()) {
            logDebug(formatter.format(Level.DEBUG, eventId, objs));
        }
    }

    @Override
    public void debug(String eventId) {
        if (log.isDebugEnabled()) {
            logDebug(formatter.format(Level.DEBUG, eventId));
        }
    }

    @Override
    public void debug(String eventId, Object obj) {
        if (log.isDebugEnabled()) {
            logDebug(formatter.format(Level.DEBUG, eventId, obj));
        }
    }

    @Override
    public void debug(String eventId, String name, Object value) {
        if (log.isDebugEnabled()) {
            logDebug(formatter.format(Level.DEBUG, eventId, name, value));
        }
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
    public void trace(String eventId, Object... objs) {
        if (log.isTraceEnabled()) {
            logTrace(formatter.format(Level.TRACE, eventId, objs));
        }
    }

    @Override
    public void trace(String eventId) {
        if (log.isTraceEnabled()) {
            logTrace(formatter.format(Level.TRACE, eventId));
        }
    }

    @Override
    public void trace(String eventId, Object obj) {
        if (log.isTraceEnabled()) {
            logTrace(formatter.format(Level.TRACE, eventId, obj));
        }
    }

    @Override
    public void trace(String eventId, String name, Object value) {
        if (log.isTraceEnabled()) {
            logTrace(formatter.format(Level.TRACE, eventId, name, value));
        }
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
