package io.eliez.slog4j;

import io.eliez.slog4j.structmsg.Formatter;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.UtilityClass;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@UtilityClass
public class SLoggerFactory {

    public static SLogger getSLogger(Class<?> clazz) {
        return new Slf4jSLogger(LoggerFactory.getLogger(clazz));
    }

    @RequiredArgsConstructor(access = AccessLevel.PRIVATE)
    @SuppressWarnings("squid:S2629")    // "Preconditions" and logging arguments should not require evaluation
    private static class Slf4jSLogger implements SLogger {
        private final Logger log;

        @Override
        public void logTracedEvent(String eventId, long spanId, Object... fields) {
            log.info(Formatter.formatTracedEvent(eventId, spanId, fields));
        }

        @Override
        public void logEvent(String eventId, Object... fields) {
            log.info(Formatter.formatEvent(eventId, fields));
        }
    }
}

