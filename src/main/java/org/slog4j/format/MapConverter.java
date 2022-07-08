package org.slog4j.format;

import java.util.Map;

public class MapConverter implements ToPropertiesConverter<Map<String, Object>> {

    @Override
    public Class<?> getEffectiveType() {
        return Map.class;
    }

    @Override
    public Iterable<Map.Entry<String, Object>> convert(Map<String, Object> map) {
        return map.entrySet();
    }
}
