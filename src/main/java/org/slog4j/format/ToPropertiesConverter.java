package org.slog4j.format;

import java.util.Map;

public interface ToPropertiesConverter<T> {
    Iterable<Map.Entry<String, Object>> convert(T object);
}
