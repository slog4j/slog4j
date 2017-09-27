package org.slog4j.format;

import org.joda.convert.StringConverter;
import org.joda.convert.ToStringConverter;
import org.slf4j.event.Level;
import org.slog4j.time.TimeProvider;

public interface Formatter {
    Formatter eventIdLabel(String eventIdLabel);

    Formatter spanIdLabel(String spanIdLabel);

    <T> Formatter registerToStringConverter(Class<T> clazz, StringConverter<T> converter);

    <T> Formatter registerToStringConverter(Class<T> clazz, ToStringConverter<T> converter);

    <T> Formatter registerToPropertiesConverter(Class<T> clazz, ToPropertiesConverter<T> converter);

    String format(TimeProvider timeProvider, Level level, String eventId);

    String format(TimeProvider timeProvider, Level level, String eventId, Object obj);

    String format(TimeProvider timeProvider, Level level, String eventId, String key, Object value);

    String format(TimeProvider timeProvider, Level level, String eventId, Object... objs);

    String format(TimeProvider timeProvider, Level level, long spanId, String eventId);

    String format(TimeProvider timeProvider, Level level, long spanId, String eventId, Object obj);

    String format(TimeProvider timeProvider, Level level, long spanId, String eventId, String key, Object value);

    String format(TimeProvider timeProvider, Level level, long spanId, String eventId, Object... objs);
}
