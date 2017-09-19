package org.slog4j;

import java.util.Map;

public interface ObjectConverter<T> {
    Iterable<Map.Entry<String, Object>> convert(T object);
}
