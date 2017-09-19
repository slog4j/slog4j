package io.eliez.slog4j;

@FunctionalInterface
public interface ValueConverter<T> {
    String convert(T value);
}
