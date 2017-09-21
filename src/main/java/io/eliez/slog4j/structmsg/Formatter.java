package io.eliez.slog4j.structmsg;

import io.eliez.slog4j.LongId;
import io.eliez.slog4j.ObjectConverter;
import io.eliez.slog4j.ValueConverter;
import org.apache.commons.lang3.ClassUtils;
import org.apache.commons.text.StrBuilder;

import java.net.SocketAddress;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

public class Formatter {
    private static final char   SEP                       = ' ';
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

    private static final Map<Class, ValueConverter>  VALUE_CONVERTERS  = new ClassMap<ValueConverter>();
    private static final Map<Class, ObjectConverter> OBJECT_CONVERTERS = new ClassMap<ObjectConverter>();

    public static <T> void registerValueConverter(Class<T> clazz, ValueConverter<T> valueConverter) {
        VALUE_CONVERTERS.put(clazz, valueConverter);
    }

    public static <T> void registerObjectConverter(Class<T> clazz, ObjectConverter<T> objectConverter) {
        OBJECT_CONVERTERS.put(clazz, objectConverter);
    }

    static {
        registerValueConverter(LongId.class, LongIdConverter.SINGLETON);
        registerValueConverter(SocketAddress.class, SocketAddressConverter.SINGLETON);
        registerObjectConverter(Map.class, MapConverter.SINGLETON);
    }

    public static String format(Object... otherFields) {
        StrBuilder sb = SB_POOL.get();
        return appendFields(sb, otherFields).toString();
    }

    public static String formatEvent(String eventId, Object... otherFields) {
        StrBuilder sb = SB_POOL.get();
        appendBareText(sb.append("evt="), eventId);
        return appendFields(sb, otherFields).toString();
    }

    public static String formatTracedEvent(String eventId, long spanId, Object... otherFields) {
        StrBuilder sb = SB_POOL.get();
        appendBareText(sb.append("evt="), eventId)
                .append(SEP)
                .append("spanId=")
                .append(LongIdConverter.convert(spanId));
        return appendFields(sb, otherFields).toString();
    }

    private static StrBuilder appendBareText(StrBuilder sb, String str) {
        boolean mustQuote = str.indexOf(SEP) >= 0;
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

    @SuppressWarnings("unchecked")
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
                sb.appendSeparator(SEP).append(key).append('=');
                if ((i + 1) != fields.length) {
                    Object obj = fields[++i];
                    appendValue(sb, obj);
                } else {
                    sb.append(MISSING_VALUE_PLACEHOLDER);
                }
            } else {
                ObjectConverter objectConverter = OBJECT_CONVERTERS.get(field.getClass());
                if (objectConverter != null) {
                    appendFields(sb.appendSeparator(SEP), objectConverter.convert(field));
                } else {
                    sb.appendSeparator(SEP)
                            .append(field.getClass().getName())
                            .append('=')
                            .append(field.hashCode());
                }
            }
        }
        return sb;
    }

    @SuppressWarnings("unchecked")
    private static StrBuilder appendValue(StrBuilder sb, Object obj) {
        if (obj == null) {
            return sb.append(NULL_PLACEHOLDER);
        }
        if (obj instanceof String) {
            return appendBareText(sb, (String) obj);
        }
        if (ClassUtils.isPrimitiveWrapper(obj.getClass()) || obj.getClass().isEnum()) {
            // no special characters to handle
            return sb.append(obj.toString());
        }
        ValueConverter valueConverter = VALUE_CONVERTERS.get(obj.getClass());
        if (valueConverter != null) {
            return appendBareText(sb, valueConverter.convert(obj));
        }
        ObjectConverter objectConverter = OBJECT_CONVERTERS.get(obj.getClass());
        if (objectConverter != null) {
            return appendFields(sb.append(OPEN_SUBFIELD), objectConverter.convert(obj))
                    .append(CLOSE_SUBFIELD);
        }
        return sb.append(obj.getClass().getSimpleName())
                .append('#')
                .append(NO_CONVERTER_PLACEHOLDER);
    }

    private static StrBuilder appendFields(StrBuilder sb, Collection<Map.Entry<?, Object>> fields) {
        int loopIndex = 0;
        for (Map.Entry<?, Object> entry : fields) {
            // TODO: verificar key [alphanum]+
            sb.appendSeparator(SEP, loopIndex++).append(entry.getKey().toString()).append('=');
            appendValue(sb, entry.getValue());
        }
        return sb;
    }

    private static final class LongIdConverter implements ValueConverter<LongId> {
        static final LongIdConverter SINGLETON = new LongIdConverter();

        private LongIdConverter() {
        }

        @Override
        public String convert(LongId longId) {
            return convert(longId.getValue());
        }

        static String convert(long value) {
            return String.format("%016x", value);
        }
    }

    private static final class SocketAddressConverter implements ValueConverter<SocketAddress> {
        static final SocketAddressConverter SINGLETON = new SocketAddressConverter();

        private SocketAddressConverter() {
        }

        @Override
        public String convert(SocketAddress value) {
            String text = value.toString();
            int sep;
            if ((sep = text.indexOf('/')) >= 0) {
                return text.substring(sep + 1);
            }
            return text;
        }
    }

    public static final class ClassMap<T> extends HashMap<Class, T> {
        @SuppressWarnings("unchecked")
        @Override
        public T get(Object key) {
            assert key != null;
            T value = super.get(key);
            if (value == null) {
                for (Entry<Class, T> entry : entrySet()) {
                    if (entry.getKey().isAssignableFrom((Class<?>) key)) {
                        value = entry.getValue();
                        break;
                    }
                }
                if (value != null) {
                    put((Class) key, value);
                }
            }
            return value;
        }
    }

    public static final class MapConverter implements ObjectConverter<Map> {
        static final MapConverter SINGLETON = new MapConverter();

        private MapConverter() {
        }

        @Override
        @SuppressWarnings("unchecked")
        public Collection<Map.Entry> convert(Map map) {
            return map.entrySet();
        }
    }
}
