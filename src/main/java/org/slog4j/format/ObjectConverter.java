package org.slog4j.format;

import java.util.Map;

public interface ObjectConverter<T> {
    Iterable<Map.Entry<String, Object>> convert(T object);
}
