package org.slog4j.format;

import java.util.Map;

public class MapConverter implements ToPropertiesConverter {

    @Override
    public Class<?> getEffectiveType() {
        return Map.class;
    }

    @Override
    @SuppressWarnings("unchecked")
    public Iterable<Map.Entry<String, Object>> convert(Object map) {
        return ((Map<String, Object>) map).entrySet();
    }
}
