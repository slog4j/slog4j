package io.eliez.slog4j;

import java.util.Collection;
import java.util.Map;

@FunctionalInterface
public interface ObjectConverter<T> {
    Collection<Map.Entry> convert(T object);
}
