package org.slog4j.format;

import org.slf4j.event.Level;

public interface Formatter {

    interface Result {
        String getString();

        Object getAttachment();

        void clear();
    }

    Result format(Level level, long spanId, String eventId);

    Result format(Level level, long spanId, String eventId, Object obj);

    Result format(Level level, long spanId, String eventId, String name, Object value);

    Result format(Level level, long spanId, String eventId, Object... objs);
}
