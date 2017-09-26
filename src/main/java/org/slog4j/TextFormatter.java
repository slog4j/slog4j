package org.slog4j;

import lombok.experimental.UtilityClass;
import lombok.val;
import org.apache.commons.lang3.ClassUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.text.StrBuilder;

import java.util.Map;

public class TextFormatter extends BaseFormatter {
    private static final char FIELD_SEP      = ' ';
    private static final char KV_SEP         = '=';
    private static final char OPEN_SUBFIELD  = '[';
    private static final char CLOSE_SUBFIELD = ']';

    public static final TextFormatter INSTANCE = new TextFormatter(true);

    public TextFormatter() {
    }

    public TextFormatter(boolean immutable) {
        super(immutable);
    }

    @Override
    public String format(String eventId) {
        val sb = StrBuilderFactory.get();
        return appendText(sb.append(getEventIdLabel()).append(KV_SEP), eventId).toString();
    }

    @Override
    public String format(String eventId, Object obj) {
        val sb = StrBuilderFactory.get();
        appendText(sb.append(getEventIdLabel()).append(KV_SEP), eventId);
        return appendComplexObject(sb.append(FIELD_SEP), obj).toString();
    }

    @Override
    public String format(String eventId, String key, Object value) {
        val sb = StrBuilderFactory.get();
        appendText(sb.append(getEventIdLabel()).append(KV_SEP), eventId);
        return appendValue(sb.append(FIELD_SEP).append(normalizeKey(key)).append(KV_SEP), value).toString();
    }

    @Override
    public String format(String eventId, Object... otherFields) {
        val sb = StrBuilderFactory.get();
        appendText(sb.append(getEventIdLabel()).append(KV_SEP), eventId);
        return appendFields(sb, otherFields).toString();
    }

    @Override
    public String format(long spanId, String eventId) {
        val sb = StrBuilderFactory.get();
        return appendText(sb.append(getEventIdLabel()).append(KV_SEP), eventId)
            .append(FIELD_SEP)
            .append(getSpanIdLabel()).append(KV_SEP)
            .append(LongIdConverter.convertToString(spanId)).toString();
    }

    @Override
    public String format(long spanId, String eventId, Object obj) {
        val sb = StrBuilderFactory.get();
        appendText(sb.append(getEventIdLabel()).append(KV_SEP), eventId)
            .append(FIELD_SEP)
            .append(getSpanIdLabel()).append(KV_SEP)
            .append(LongIdConverter.convertToString(spanId));
        return appendComplexObject(sb.append(FIELD_SEP), obj).toString();
    }

    @Override
    public String format(long spanId, String eventId, String key, Object value) {
        val sb = StrBuilderFactory.get();
        appendText(sb.append(getEventIdLabel()).append(KV_SEP), eventId)
            .append(FIELD_SEP)
            .append(getSpanIdLabel()).append(KV_SEP)
            .append(LongIdConverter.convertToString(spanId));
        return appendValue(sb.appendSeparator(FIELD_SEP).append(normalizeKey(key)).append(KV_SEP), value).toString();
    }

    @Override
    public String format(long spanId, String eventId, Object... otherFields) {
        val sb = StrBuilderFactory.get();
        appendText(sb.append(getEventIdLabel()).append(KV_SEP), eventId)
            .append(FIELD_SEP)
            .append(getSpanIdLabel()).append(KV_SEP)
            .append(LongIdConverter.convertToString(spanId));
        return appendFields(sb, otherFields).toString();
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
