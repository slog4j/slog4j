package org.slog4j;

import lombok.experimental.UtilityClass;
import lombok.val;
import org.apache.commons.lang3.ClassUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.text.StrBuilder;
import org.slf4j.event.Level;

import java.util.Map;

public class TextFormatter extends BaseFormatter {
    private static final char FIELD_SEP      = ' ';
    private static final char KV_SEP         = '=';
    private static final char OPEN_SUBFIELD  = '[';
    private static final char CLOSE_SUBFIELD = ']';

    public static final TextFormatter INSTANCE = new TextFormatter(true);

    private PropertyFormat timeFormat  = PropertyFormat.KEY_VALUE;
    private PropertyFormat levelFormat = PropertyFormat.KEY_VALUE;

    public enum PropertyFormat {
        OMIT,
        VALUE_ONLY,
        KEY_VALUE
    }

    public TextFormatter() {
    }

    private TextFormatter(boolean immutable) {
        super(immutable);
    }

    public TextFormatter timeFormat(PropertyFormat format) {
        this.timeFormat = format;
        return this;
    }

    public TextFormatter levelFormat(PropertyFormat format) {
        this.levelFormat = format;
        return this;
    }

    public TextFormatter omitCommonProperties() {
        return timeFormat(PropertyFormat.OMIT)
            .levelFormat(PropertyFormat.OMIT);
    }

    public TextFormatter valueOnlyCommonProperties() {
        return timeFormat(PropertyFormat.VALUE_ONLY)
            .levelFormat(PropertyFormat.VALUE_ONLY);
    }

    @Override
    public String format(TimeProvider timeProvider, Level level, String eventId) {
        val sb = StrBuilderFactory.get();
        appendCommonFields(sb, level, timeProvider);
        return appendText(sb.appendSeparator(FIELD_SEP).append(eventIdLabel()).append(KV_SEP), eventId).toString();
    }

    @Override
    public String format(TimeProvider timeProvider, Level level, String eventId, Object obj) {
        val sb = StrBuilderFactory.get();
        appendCommonFields(sb, level, timeProvider);
        appendText(sb.appendSeparator(FIELD_SEP).append(eventIdLabel()).append(KV_SEP), eventId);
        return appendComplexObject(sb.append(FIELD_SEP), obj).toString();
    }

    @Override
    public String format(TimeProvider timeProvider, Level level, String eventId, String key, Object value) {
        val sb = StrBuilderFactory.get();
        appendCommonFields(sb, level, timeProvider);
        appendText(sb.appendSeparator(FIELD_SEP).append(eventIdLabel()).append(KV_SEP), eventId);
        return appendValue(sb.append(FIELD_SEP).append(normalizeKey(key)).append(KV_SEP), value).toString();
    }

    @Override
    public String format(TimeProvider timeProvider, Level level, String eventId, Object... otherFields) {
        val sb = StrBuilderFactory.get();
        appendCommonFields(sb, level, timeProvider);
        appendText(sb.appendSeparator(FIELD_SEP).append(eventIdLabel()).append(KV_SEP), eventId);
        return appendFields(sb, otherFields).toString();
    }

    @Override
    public String format(TimeProvider timeProvider, Level level, long spanId, String eventId) {
        val sb = StrBuilderFactory.get();
        appendCommonFields(sb, level, timeProvider);
        return appendText(sb.appendSeparator(FIELD_SEP).append(eventIdLabel()).append(KV_SEP), eventId)
            .append(FIELD_SEP)
            .append(spanIdLabel()).append(KV_SEP)
            .append(LongIdConverter.convertToString(spanId)).toString();
    }

    @Override
    public String format(TimeProvider timeProvider, Level level, long spanId, String eventId, Object obj) {
        val sb = StrBuilderFactory.get();
        appendCommonFields(sb, level, timeProvider);
        appendText(sb.appendSeparator(FIELD_SEP).append(eventIdLabel()).append(KV_SEP), eventId)
            .append(FIELD_SEP)
            .append(spanIdLabel()).append(KV_SEP)
            .append(LongIdConverter.convertToString(spanId));
        return appendComplexObject(sb.append(FIELD_SEP), obj).toString();
    }

    @Override
    public String format(TimeProvider timeProvider, Level level, long spanId, String eventId, String key, Object value) {
        val sb = StrBuilderFactory.get();
        appendCommonFields(sb, level, timeProvider);
        appendText(sb.appendSeparator(FIELD_SEP).append(eventIdLabel()).append(KV_SEP), eventId)
            .append(FIELD_SEP)
            .append(spanIdLabel()).append(KV_SEP)
            .append(LongIdConverter.convertToString(spanId));
        return appendValue(sb.appendSeparator(FIELD_SEP).append(normalizeKey(key)).append(KV_SEP), value).toString();
    }

    @Override
    public String format(TimeProvider timeProvider, Level level, long spanId, String eventId, Object... otherFields) {
        val sb = StrBuilderFactory.get();
        appendCommonFields(sb, level, timeProvider);
        appendText(sb.appendSeparator(FIELD_SEP).append(eventIdLabel()).append(KV_SEP), eventId)
            .append(FIELD_SEP)
            .append(spanIdLabel()).append(KV_SEP)
            .append(LongIdConverter.convertToString(spanId));
        return appendFields(sb, otherFields).toString();
    }

    private void appendCommonFields(StrBuilder sb, Level level, TimeProvider timeProvider) {
        switch (timeFormat) {
            case OMIT:
                break;

            case VALUE_ONLY:
                sb.append(FORMAT_ISO8601_MILLIS.format(timeProvider.currentTimeMillis()));
                break;

            case KEY_VALUE:
                sb.append(TIME_LABEL).append(KV_SEP)
                    .append(FORMAT_ISO8601_MILLIS.format(timeProvider.currentTimeMillis()));
                break;
        }
        switch (levelFormat) {
            case OMIT:
                break;

            case VALUE_ONLY:
                sb.appendSeparator(FIELD_SEP).append(level.toString());
                break;

            case KEY_VALUE:
                sb.appendSeparator(FIELD_SEP).append(LEVEL_LABEL).append(KV_SEP).append(level.toString());
                break;
        }
    }

    private static StrBuilder appendText(StrBuilder sb, String str) {
        boolean mustQuote = str.indexOf(FIELD_SEP) >= 0;
        if (mustQuote) {
            sb.append('\'');
        }
        char[] chars = str.toCharArray();
        int startIndex = 0;
        int index;
        for (index = 0; index < chars.length; index++) {
            char c = chars[index];
            int charToScape = Integer.MAX_VALUE;
            if ((c == '\'') || (c == OPEN_SUBFIELD) || (c == CLOSE_SUBFIELD)) {
                charToScape = c;
            } else if (c == '\r') {
                charToScape = 'r';
            } else if (c == '\n') {
                charToScape = 'n';
            }
            if (charToScape != Integer.MAX_VALUE) {
                sb.append(chars, startIndex, index - startIndex)
                    // TODO: garantir que filtro kv do LogStash considera escaped special chars!
                    .append('\\')
                    .append((char) charToScape);
                startIndex = index + 1;
            }
        }
        if (startIndex < chars.length) {
            sb.append(chars, startIndex, str.length() - startIndex);
        }
        if (mustQuote) {
            sb.append('\'');
        }
        return sb;
    }

    @SuppressWarnings({
        "unchecked",
        "squid:ForLoopCounterChangedCheck"  // "for" loop stop conditions should be invariant
    })
    private StrBuilder appendFields(StrBuilder sb, Object[] fields) {
        for (int i = 0; i < fields.length; i++) {
            Object field = fields[i];
            if (field == null) {
                // silently ignore null keys
                continue;
            }
            if (field instanceof String) {
                String key = (String) field;
                sb.append(FIELD_SEP).append(normalizeKey(key)).append(KV_SEP);
                if ((i + 1) != fields.length) {
                    Object obj = fields[++i];
                    appendValue(sb, obj);
                } else {
                    sb.append(MISSING_VALUE_PLACEHOLDER);
                }
            } else {
                appendComplexObject(sb.append(FIELD_SEP), field);
            }
        }
        return sb;
    }

    private static String normalizeKey(String key) {
        if (key.indexOf(FIELD_SEP) >= 0) {
            return StringUtils.replaceChars(key, FIELD_SEP, '_');
        }
        return key;
    }

    @SuppressWarnings("squid:S2259")    // Null pointers should not be dereferenced
    private StrBuilder appendValue(StrBuilder sb, Object obj) {
        try {
            return appendSimpleObject(sb, obj);
        } catch (RuntimeException ignored) {
            return appendComplexObject(sb.append(OPEN_SUBFIELD), obj)
                .append(CLOSE_SUBFIELD);
        }
    }

    private StrBuilder appendSimpleObject(StrBuilder sb, Object obj) {
        if (obj == null) {
            return sb.append(NULL_PLACEHOLDER);
        }
        if (obj instanceof String) {
            return appendText(sb, (String) obj);
        }
        if (ClassUtils.isPrimitiveWrapper(obj.getClass()) || obj.getClass().isEnum()) {
            // don't need to handle special characters here
            return sb.append(obj.toString());
        }
        String str = convertToString(obj);
        return appendText(sb, str);
    }

    @SuppressWarnings("unchecked")
    private StrBuilder appendComplexObject(StrBuilder sb, Object obj) {
        val objectConverter = getObjectConverter(obj.getClass());
        if (objectConverter != null) {
            return appendFields(sb, objectConverter.convert(obj));
        }
        return sb.append(obj.getClass().getSimpleName())
            .append('#')
            .append(NO_CONVERTER_PLACEHOLDER);
    }

    private StrBuilder appendFields(StrBuilder sb, Iterable<Map.Entry<?, Object>> fields) {
        int loopIndex = 0;
        for (Map.Entry<?, Object> entry : fields) {
            sb.appendSeparator(FIELD_SEP, loopIndex++)
                .append(normalizeKey(entry.getKey().toString()))
                .append(KV_SEP);
            appendValue(sb, entry.getValue());
        }
        return sb;
    }

    @UtilityClass
    private static class StrBuilderFactory {

        private static final ThreadLocal<StrBuilder> SB_POOL = new ThreadLocal<StrBuilder>() {
            @Override
            protected StrBuilder initialValue() {
                return new StrBuilder();
            }

            @Override
            public StrBuilder get() {
                StrBuilder sb = super.get();
                sb.clear();
                return sb;
            }
        };

        static StrBuilder get() {
            return SB_POOL.get();
        }
    }
}
