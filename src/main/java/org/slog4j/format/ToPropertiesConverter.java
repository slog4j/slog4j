package org.slog4j.format;

import java.util.Map;

public interface ToPropertiesConverter {
    /**
     * Get the effective type supported by this converter.
     *
     * @return the effective type
     */
    Class<?> getEffectiveType();

    Iterable<Map.Entry<String, Object>> convert(Object object);
}
