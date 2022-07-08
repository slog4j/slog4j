package org.slog4j.format;

import org.slog4j.types.LongId;

public class LongIdConverter implements TypedToStringConverter<LongId> {

    @Override
    public Class<?> getEffectiveType() {
        return LongId.class;
    }

    @Override
    public String convertToString(LongId longId) {
        return convertToString(longId.getValue());
    }

    static String convertToString(long value) {
        return String.format("%016x", value);
    }
}
