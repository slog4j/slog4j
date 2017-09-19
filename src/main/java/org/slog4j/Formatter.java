package org.slog4j;

import org.joda.convert.StringConverter;
import org.joda.convert.ToStringConverter;

public interface Formatter {
    Formatter setEventIdLabel(String eventIdLabel);

    Formatter setSpanIdLabel(String spanIdLabel);

    <T> Formatter registerValueConverter(Class<T> clazz, StringConverter<T> converter);

    <T> Formatter registerValueConverter(Class<T> clazz, ToStringConverter<T> valueConverter);

    <T> Formatter registerObjectConverter(Class<T> clazz, ObjectConverter<T> objectConverter);

    String format(String eventId);

    String format(String eventId, Object obj);

    String format(String eventId, String key, Object value);

    String format(String eventId, Object... otherFields);

    String format(long spanId, String eventId);

    String format(long spanId, String eventId, Object obj);

    String format(long spanId, String eventId, String key, Object value);

    String format(long spanId, String eventId, Object... otherFields);
}
