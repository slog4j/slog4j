package io.eliez.slog4j.structmsg;

import io.eliez.slog4j.LongId;
import io.eliez.slog4j.ObjectConverter;
import lombok.experimental.UtilityClass;
import org.apache.commons.lang3.ClassUtils;
import org.apache.commons.text.StrBuilder;
import org.joda.convert.StringConvert;
import org.joda.convert.StringConverter;
import org.joda.convert.ToStringConverter;

import java.net.InetSocketAddress;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

@UtilityClass
public class Formatter {
    private static final char   FIELD_SEP                 = ' ';
    private static final char   KV_SEP                    = '=';
    private static final char   OPEN_SUBFIELD             = '[';
    private static final char   CLOSE_SUBFIELD            = ']';
    private static final String NULL_PLACEHOLDER          = "_null_";
    private static final String NO_CONVERTER_PLACEHOLDER  = "_no_converter_";
    private static final String MISSING_VALUE_PLACEHOLDER = "_missing_";

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

    private static final StringConvert               STRING_CONVERT    = new StringConvert(true);
    private static final Map<Class, ObjectConverter> OBJECT_CONVERTERS = new ClassMap<ObjectConverter>();

    public static <T> void registerValueConverter(Class<T> clazz, StringConverter<T> converter) {
        STRING_CONVERT.register(clazz, converter);
    }

    public static <T> void registerValueConverter(Class<T> clazz, final ToStringConverter<T> valueConverter) {
        registerValueConverter(clazz, new StringConverter<T>() {
            @Override
            public T convertFromString(Class<? extends T> cls, String str) {
                throw new UnsupportedOperationException();
            }

            @Override
            public String convertToString(T object) {
                return valueConverter.convertToString(object);
            }
        });
    }

    public static <T> void registerObjectConverter(Class<T> clazz, ObjectConverter<T> objectConverter) {
        OBJECT_CONVERTERS.put(clazz, objectConverter);
    }

    static {
        registerValueConverter(LongId.class, new LongIdConverter());
        registerValueConverter(InetSocketAddress.class, new InetSocketAddressConverter());
        registerObjectConverter(Map.class, new MapConverter());
    }

    public static String format(Object... otherFields) {
        StrBuilder sb = SB_POOL.get();
        return appendFields(sb, otherFields).toString();
    }

    public static String formatEvent(String eventId, Object... otherFields) {
        StrBuilder sb = SB_POOL.get();
        appendBareText(sb.append("evt").append(KV_SEP), eventId);
        return appendFields(sb, otherFields).toString();
    }

    public static String formatTracedEvent(String eventId, long spanId, Object... otherFields) {
        StrBuilder sb = SB_POOL.get();
        appendBareText(sb.append("evt").append(KV_SEP), eventId)
                .append(FIELD_SEP)
                .append("spanId").append(KV_SEP)
                .append(LongIdConverter.convertToString(spanId));
        return appendFields(sb, otherFields).toString();
    }

    private static StrBuilder appendBareText(StrBuilder sb, String str) {
        boolean mustQuote = str.indexOf(FIELD_SEP) >= 0;
        if (mustQuote) {
            sb.append('\'');
        }
        char[] chars = str.toCharArray();
        int startIndex = 0;
        int index;
        for (index = 0; index < chars.length; index++) {
            char c = chars[index];
            if ((c == '\'') || (c == OPEN_SUBFIELD) || (c == CLOSE_SUBFIELD)) {
                sb.append(chars, startIndex, index - startIndex)
                        .append('\\')   // TODO: garantir que filtro kv do LogStash considera escaped special chars!
                        .append(c);
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
    private static StrBuilder appendFields(StrBuilder sb, Object[] fields) {
        for (int i = 0; i < fields.length; i++) {
            Object field = fields[i];
            if (field == null) {
                // silently ignore null keys
                continue;
            }
            if (field instanceof String) {
                String key = (String) field;
                // TODO: verificar key [alphanum]+
                sb.appendSeparator(FIELD_SEP).append(key).append(KV_SEP);
                if ((i + 1) != fields.length) {
                    Object obj = fields[++i];
                    appendValue(sb, obj);
                } else {
                    sb.append(MISSING_VALUE_PLACEHOLDER);
                }
            } else {
                ObjectConverter objectConverter = OBJECT_CONVERTERS.get(field.getClass());
                if (objectConverter != null) {
                    appendFields(sb.appendSeparator(FIELD_SEP), objectConverter.convert(field));
                } else {
                    sb.appendSeparator(FIELD_SEP)
                            .append(field.getClass().getName())
                            .append(KV_SEP)
                            .append(field.hashCode());
                }
            }
        }
        return sb;
    }

    @SuppressWarnings({"unchecked", "UnusedReturnValue"})
    private static StrBuilder appendValue(StrBuilder sb, Object obj) {
        if (obj == null) {
            return sb.append(NULL_PLACEHOLDER);
        }
        if (obj instanceof String) {
            return appendBareText(sb, (String) obj);
        }
        if (ClassUtils.isPrimitiveWrapper(obj.getClass()) || obj.getClass().isEnum()) {
            // don't need to handle special characters here
            return sb.append(obj.toString());
        }
        try {
            String str = STRING_CONVERT.convertToString(obj);
            return appendBareText(sb, str);
        } catch (IllegalStateException ignored) {
            // no converter was found for object's class
            ObjectConverter objectConverter = OBJECT_CONVERTERS.get(obj.getClass());
            if (objectConverter != null) {
                return appendFields(sb.append(OPEN_SUBFIELD), objectConverter.convert(obj))
                        .append(CLOSE_SUBFIELD);
            }
            return sb.append(obj.getClass().getSimpleName())
                    .append('#')
                    .append(NO_CONVERTER_PLACEHOLDER);
        }
    }

    private static StrBuilder appendFields(StrBuilder sb, Collection<Map.Entry<?, Object>> fields) {
        int loopIndex = 0;
        for (Map.Entry<?, Object> entry : fields) {
            // TODO: verificar key [alphanum]+
            sb.appendSeparator(FIELD_SEP, loopIndex++).append(entry.getKey().toString()).append(KV_SEP);
            appendValue(sb, entry.getValue());
        }
        return sb;
    }

    private static final class LongIdConverter implements ToStringConverter<LongId> {

        @Override
        public String convertToString(LongId longId) {
            return convertToString(longId.getValue());
        }

        static String convertToString(long value) {
            return String.format("%016x", value);
        }
    }

    private static final class InetSocketAddressConverter implements ToStringConverter<InetSocketAddress> {

        @Override
        public String convertToString(InetSocketAddress value) {
            String text = value.toString();
            int sep;
            if ((sep = text.indexOf('/')) >= 0) {
                return text.substring(sep + 1);
            }
            return text;
        }
    }

    public static final class ClassMap<T> extends HashMap<Class, T> {
        @Override
        public T get(Object key) {
            assert key != null;
            T value = super.get(key);
            if (value == null) {
                value = getCompatible(key);
                if (value != null) {
                    put((Class) key, value);
                }
            }
            return value;
        }

        @SuppressWarnings("unchecked")
        private T getCompatible(Object key) {
            for (Entry<Class, T> entry : entrySet()) {
                if (entry.getKey().isAssignableFrom((Class<?>) key)) {
                    return entry.getValue();
                }
            }
            return null;
        }
    }

    public static final class MapConverter implements ObjectConverter<Map> {

        @Override
        @SuppressWarnings("unchecked")
        public Collection<Map.Entry<String, Object>> convert(Map map) {
            return map.entrySet();
        }
    }
}
