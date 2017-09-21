package io.eliez.slog4j;

public interface ValueConverter<T> {
    String convert(T value);
}
