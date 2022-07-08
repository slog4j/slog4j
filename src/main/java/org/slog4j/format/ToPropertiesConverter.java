package org.slog4j.format;

import java.util.Map;

public interface ToPropertiesConverter<T> {
    /**
     * Gets the effective type that the converter works on.
     *
     * @return the effective type
     */
    Class<?> getEffectiveType();

    Iterable<Map.Entry<String, Object>> convert(T object);
}
