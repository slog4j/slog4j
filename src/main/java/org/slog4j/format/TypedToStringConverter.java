package org.slog4j.format;

import org.joda.convert.TypedStringConverter;

public interface TypedToStringConverter<T> extends TypedStringConverter<T> {

    @Override
    default T convertFromString(Class<? extends T> cls, String str) {
        throw new UnsupportedOperationException("this is a one-way converter");
    }
}
