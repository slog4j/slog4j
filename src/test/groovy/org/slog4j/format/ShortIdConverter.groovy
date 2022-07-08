package org.slog4j.format

class ShortIdConverter implements TypedToStringConverter<TextFormatterSpec.ShortId> {
    @Override
    Class<?> getEffectiveType() {
        TextFormatterSpec.ShortId
    }

    @Override
    String convertToString(TextFormatterSpec.ShortId sid) {
        String.format('0x%04x', sid.id)
    }
}
