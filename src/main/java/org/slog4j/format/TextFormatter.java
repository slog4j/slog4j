package org.slog4j.format;

import lombok.val;
import org.apache.commons.lang3.ClassUtils;
import org.slf4j.event.Level;
import org.slog4j.SLogger;

import java.util.Map;

public class TextFormatter extends BaseFormatter {
    protected static final char PROPERTY_SEP   = ' ';
    protected static final char NAME_VALUE_SEP = '=';

    private static final char OPEN_SUBFIELD  = '[';
    private static final char CLOSE_SUBFIELD = ']';

    private static final TextFormatter INSTANCE = new TextFormatter();

    public static TextFormatter getInstance() {
        return INSTANCE;
    }

    @Override
    public Result format(Level level, long spanId, String eventId) {
        val sbr = StrBuilderResultFactory.get();
        beforeAddContentsHook(sbr, level);
        appendNameValue(sbr, eventIdLabel(), eventId);
        return appendSpanId(sbr, spanId);
    }

    @Override
    public Result format(Level level, long spanId, String eventId, Object obj) {
        val sbr = StrBuilderResultFactory.get();
        beforeAddContentsHook(sbr, level);
        appendNameValue(sbr, eventIdLabel(), eventId);
        appendSpanId(sbr, spanId);
        return appendComplexObject(sbr, obj, true);
    }

    @Override
    public Result format(Level level, long spanId, String eventId, String name, Object value) {
        val sbr = StrBuilderResultFactory.get();
        beforeAddContentsHook(sbr, level);
        appendNameValue(sbr, eventIdLabel(), eventId);
        appendSpanId(sbr, spanId);
        return appendValue(sbr.appendWithSeparator(PROPERTY_SEP, name).append(NAME_VALUE_SEP), value);
    }

    @Override
    public Result format(Level level, long spanId, String eventId, Object... objs) {
        val sbr = StrBuilderResultFactory.get();
        beforeAddContentsHook(sbr, level);
        appendNameValue(sbr, eventIdLabel(), eventId);
        appendSpanId(sbr, spanId);
        return appendObjects(sbr, objs);
    }

    protected StrBuilderResult appendNameValue(StrBuilderResult sbr, String name, String value) {
        return appendText(sbr.appendWithSeparator(PROPERTY_SEP, name).append(NAME_VALUE_SEP), value);
    }

    private StrBuilderResult appendSpanId(StrBuilderResult sbr, long spanId) {
        if (spanId != SLogger.NO_SPAN_ID) {
            sbr.appendSeparator(PROPERTY_SEP)
                .append(spanIdLabel()).append(NAME_VALUE_SEP)
                .append(LongIdConverter.convertToString(spanId));
        }
        return sbr;
    }

    private static StrBuilderResult appendText(StrBuilderResult sbr, String str) {
        boolean mustQuote = str.indexOf(PROPERTY_SEP) >= 0;
        if (mustQuote) {
            sbr.append('\'');
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
                sbr.append(chars, startIndex, index - startIndex)
                    // TODO: check if LogStash kv filter supports escaped chars!
                    .append('\\')
                    .append((char) charToScape);
                startIndex = index + 1;
            }
        }
        if (startIndex < chars.length) {
            sbr.append(chars, startIndex, str.length() - startIndex);
        }
        if (mustQuote) {
            sbr.append('\'');
        }
        return sbr;
    }

    @SuppressWarnings({
        "unchecked",
        "squid:ForLoopCounterChangedCheck"  // "for" loop stop conditions should be invariant
    })
    private StrBuilderResult appendObjects(StrBuilderResult sbr, Object[] objs) {
        for (int i = 0; i < objs.length; i++) {
            Object obj = objs[i];
            if (obj == null) {
                // silently ignore null keys
                continue;
            }
            if (obj instanceof String) {
                String name = (String) obj;
                sbr.appendWithSeparator(PROPERTY_SEP, name).append(NAME_VALUE_SEP);
                if ((i + 1) != objs.length) {
                    Object value = objs[++i];
                    appendValue(sbr, value);
                } else {
                    sbr.append(MISSING_VALUE_PLACEHOLDER);
                }
            } else {
                appendComplexObject(sbr, obj, true);
            }
        }
        return sbr;
    }

    @SuppressWarnings("squid:S2259")    // Null pointers should not be dereferenced
    private StrBuilderResult appendValue(StrBuilderResult sbr, Object obj) {
        try {
            return appendSimpleObject(sbr, obj);
        } catch (RuntimeException ignored) {
            return appendComplexObject(sbr.append(OPEN_SUBFIELD), obj, false)
                .append(CLOSE_SUBFIELD);
        }
    }

    private StrBuilderResult appendSimpleObject(StrBuilderResult sbr, Object obj) {
        if (obj == null) {
            return sbr.append(NULL_PLACEHOLDER);
        }
        if (obj instanceof String) {
            return appendText(sbr, (String) obj);
        }
        if (ClassUtils.isPrimitiveWrapper(obj.getClass()) || obj.getClass().isEnum()) {
            // don't need to handle special characters here
            return sbr.append(obj.toString());
        }
        String str = convertToString(obj);
        return appendText(sbr, str);
    }

    @SuppressWarnings("unchecked")
    private StrBuilderResult appendComplexObject(StrBuilderResult sbr, Object obj, boolean topLevel) {
        val converter = propertiesConverter(obj.getClass());
        int loopIndex = topLevel ? 1 : 0;
        if (converter != null) {
            return appendProperties(sbr.appendSeparator(PROPERTY_SEP, loopIndex), converter.convert(obj));
        }
        if ((obj instanceof Throwable) && (topLevel || (sbr.getAttachment() == null))) {
            return sbr.setAttachment(obj);
        }
        return sbr.appendSeparator(PROPERTY_SEP, loopIndex)
            .append(obj.getClass().getName())
            .append('#')
            .append(NO_CONVERTER_PLACEHOLDER);
    }

    private StrBuilderResult appendProperties(StrBuilderResult sbr, Iterable<Map.Entry<?, Object>> props) {
        int loopIndex = 0;
        for (Map.Entry<?, Object> prop : props) {
            sbr.appendSeparator(PROPERTY_SEP, loopIndex++)
                .append(prop.getKey().toString())
                .append(NAME_VALUE_SEP);
            appendValue(sbr, prop.getValue());
        }
        return sbr;
    }
}
