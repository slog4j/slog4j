package org.slog4j.format;

import lombok.experimental.UtilityClass;
import lombok.val;
import org.apache.commons.lang3.ClassUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.text.StrBuilder;
import org.slf4j.event.Level;
import org.slog4j.time.TimeProvider;

import java.util.Map;

public class TextFormatter extends BaseFormatter {
    private static final char PROPERTY_SEP   = ' ';
    private static final char KV_SEP         = '=';
    private static final char OPEN_SUBFIELD  = '[';
    private static final char CLOSE_SUBFIELD = ']';

    public static final TextFormatter INSTANCE = new TextFormatter(true);

    private PropertyFormat timeFormat  = PropertyFormat.PROPERTY;
    private PropertyFormat levelFormat = PropertyFormat.PROPERTY;

    public enum PropertyFormat {
        OMIT,
        VALUE_ONLY,
        PROPERTY
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

    public TextFormatter commonPropertiesFormat(PropertyFormat format) {
        return timeFormat(format)
            .levelFormat(format);
    }

    @Override
    public String format(TimeProvider timeProvider, Level level, String eventId) {
        val sb = StrBuilderFactory.get();
        appendCommonProperties(sb, level, timeProvider);
        return appendText(sb.appendSeparator(PROPERTY_SEP).append(eventIdLabel()).append(KV_SEP), eventId).toString();
    }

    @Override
    public String format(TimeProvider timeProvider, Level level, String eventId, Object obj) {
        val sb = StrBuilderFactory.get();
        appendCommonProperties(sb, level, timeProvider);
        appendText(sb.appendSeparator(PROPERTY_SEP).append(eventIdLabel()).append(KV_SEP), eventId);
        return appendComplexObject(sb.append(PROPERTY_SEP), obj).toString();
    }

    @Override
    public String format(TimeProvider timeProvider, Level level, String eventId, String key, Object value) {
        val sb = StrBuilderFactory.get();
        appendCommonProperties(sb, level, timeProvider);
        appendText(sb.appendSeparator(PROPERTY_SEP).append(eventIdLabel()).append(KV_SEP), eventId);
        return appendValue(sb.append(PROPERTY_SEP).append(normalizeKey(key)).append(KV_SEP), value).toString();
    }

    @Override
    public String format(TimeProvider timeProvider, Level level, String eventId, Object... objs) {
        val sb = StrBuilderFactory.get();
        appendCommonProperties(sb, level, timeProvider);
        appendText(sb.appendSeparator(PROPERTY_SEP).append(eventIdLabel()).append(KV_SEP), eventId);
        return appendObjects(sb, objs).toString();
    }

    @Override
    public String format(TimeProvider timeProvider, Level level, long spanId, String eventId) {
        val sb = StrBuilderFactory.get();
        appendCommonProperties(sb, level, timeProvider);
        return appendText(sb.appendSeparator(PROPERTY_SEP).append(eventIdLabel()).append(KV_SEP), eventId)
            .append(PROPERTY_SEP)
            .append(spanIdLabel()).append(KV_SEP)
            .append(LongIdConverter.convertToString(spanId)).toString();
    }

    @Override
    public String format(TimeProvider timeProvider, Level level, long spanId, String eventId, Object obj) {
        val sb = StrBuilderFactory.get();
        appendCommonProperties(sb, level, timeProvider);
        appendText(sb.appendSeparator(PROPERTY_SEP).append(eventIdLabel()).append(KV_SEP), eventId)
            .append(PROPERTY_SEP)
            .append(spanIdLabel()).append(KV_SEP)
            .append(LongIdConverter.convertToString(spanId));
        return appendComplexObject(sb.append(PROPERTY_SEP), obj).toString();
    }

    @Override
    public String format(TimeProvider timeProvider, Level level, long spanId, String eventId, String key, Object value) {
        val sb = StrBuilderFactory.get();
        appendCommonProperties(sb, level, timeProvider);
        appendText(sb.appendSeparator(PROPERTY_SEP).append(eventIdLabel()).append(KV_SEP), eventId)
            .append(PROPERTY_SEP)
            .append(spanIdLabel()).append(KV_SEP)
            .append(LongIdConverter.convertToString(spanId));
        return appendValue(sb.appendSeparator(PROPERTY_SEP).append(normalizeKey(key)).append(KV_SEP), value).toString();
    }

    @Override
    public String format(TimeProvider timeProvider, Level level, long spanId, String eventId, Object... objs) {
        val sb = StrBuilderFactory.get();
        appendCommonProperties(sb, level, timeProvider);
        appendText(sb.appendSeparator(PROPERTY_SEP).append(eventIdLabel()).append(KV_SEP), eventId)
            .append(PROPERTY_SEP)
            .append(spanIdLabel()).append(KV_SEP)
            .append(LongIdConverter.convertToString(spanId));
        return appendObjects(sb, objs).toString();
    }

    private void appendCommonProperties(StrBuilder sb, Level level, TimeProvider timeProvider) {
        switch (timeFormat) {
            case OMIT:
                break;

            case VALUE_ONLY:
                sb.append(FORMAT_ISO8601_MILLIS.format(timeProvider.currentTimeMillis()));
                break;

            case PROPERTY:
                sb.append(TIME_LABEL).append(KV_SEP)
                    .append(FORMAT_ISO8601_MILLIS.format(timeProvider.currentTimeMillis()));
                break;
        }
        switch (levelFormat) {
            case OMIT:
                break;

            case VALUE_ONLY:
                sb.appendSeparator(PROPERTY_SEP).append(level.toString());
                break;

            case PROPERTY:
                sb.appendSeparator(PROPERTY_SEP).append(LEVEL_LABEL).append(KV_SEP).append(level.toString());
                break;
        }
    }

    private static StrBuilder appendText(StrBuilder sb, String str) {
        boolean mustQuote = str.indexOf(PROPERTY_SEP) >= 0;
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
    private StrBuilder appendObjects(StrBuilder sb, Object[] objs) {
        for (int i = 0; i < objs.length; i++) {
            Object obj = objs[i];
            if (obj == null) {
                // silently ignore null keys
                continue;
            }
            if (obj instanceof String) {
                String key = (String) obj;
                sb.append(PROPERTY_SEP).append(normalizeKey(key)).append(KV_SEP);
                if ((i + 1) != objs.length) {
                    Object value = objs[++i];
                    appendValue(sb, value);
                } else {
                    sb.append(MISSING_VALUE_PLACEHOLDER);
                }
            } else {
                appendComplexObject(sb.append(PROPERTY_SEP), obj);
            }
        }
        return sb;
    }

    private static String normalizeKey(String key) {
        if (key.indexOf(PROPERTY_SEP) >= 0) {
            return StringUtils.replaceChars(key, PROPERTY_SEP, '_');
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
        val converter = propertiesConverter(obj.getClass());
        if (converter != null) {
            return appendProperties(sb, converter.convert(obj));
        }
        return sb.append(obj.getClass().getSimpleName())
            .append('#')
            .append(NO_CONVERTER_PLACEHOLDER);
    }

    private StrBuilder appendProperties(StrBuilder sb, Iterable<Map.Entry<?, Object>> props) {
        int loopIndex = 0;
        for (Map.Entry<?, Object> prop : props) {
            sb.appendSeparator(PROPERTY_SEP, loopIndex++)
                .append(normalizeKey(prop.getKey().toString()))
                .append(KV_SEP);
            appendValue(sb, prop.getValue());
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
