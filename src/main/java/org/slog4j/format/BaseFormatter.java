package org.slog4j.format;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.experimental.Accessors;
import org.apache.commons.lang3.time.FastDateFormat;
import org.joda.convert.StringConvert;
import org.joda.convert.StringConverter;
import org.joda.convert.ToStringConverter;
import org.slog4j.types.LongId;

import java.net.InetSocketAddress;
import java.util.HashMap;
import java.util.Map;

public abstract class BaseFormatter implements Formatter {

    private static final   String DEFAULT_EVENT_ID_LABEL = "evt";
    private static final   String DEFAULT_SPAN_ID_LABEL  = "spanId";
    protected static final String TIME_LABEL             = "time";
    protected static final String LEVEL_LABEL            = "level";

    static final String NULL_PLACEHOLDER          = "_NULL_";
    static final String NO_CONVERTER_PLACEHOLDER  = "_NO_CONVERTER_";
    static final String MISSING_VALUE_PLACEHOLDER = "_MISSING_";

    // TextFormatter assumes the format don't have spaces and no special characters that requires quoting
    static final FastDateFormat FORMAT_ISO8601_MILLIS = FastDateFormat.getInstance("yyyy-MM-dd'T'HH:mm:ss.SSSZ");

    private final StringConvert               stringConvert    = new StringConvert(true);
    private final Map<Class, ObjectConverter> objectConverters = new ClassMap<ObjectConverter>();

    @Getter(AccessLevel.PROTECTED)
    @Accessors(fluent = true)
    private String eventIdLabel = DEFAULT_EVENT_ID_LABEL;

    @Getter(AccessLevel.PROTECTED)
    @Accessors(fluent = true)
    private String spanIdLabel = DEFAULT_SPAN_ID_LABEL;

    private final boolean immutable;

    public BaseFormatter() {
        this(false);
    }

    public BaseFormatter(boolean immutable) {
        registerAdditionalConverters();
        this.immutable = immutable;
    }

    @Override
    public Formatter eventIdLabel(String eventIdLabel) {
        checkIfMutable();
        this.eventIdLabel = eventIdLabel;
        return this;
    }

    @Override
    public Formatter spanIdLabel(String spanIdLabel) {
        checkIfMutable();
        this.spanIdLabel = spanIdLabel;
        return this;
    }

    private void registerAdditionalConverters() {
        registerValueConverter(LongId.class, new LongIdConverter());
        registerValueConverter(InetSocketAddress.class, new InetSocketAddressConverter());
        registerObjectConverter(Map.class, new MapConverter());
    }

    @Override
    public <T> Formatter registerValueConverter(Class<T> clazz, StringConverter<T> converter) {
        checkIfMutable();
        stringConvert.register(clazz, converter);
        return this;
    }

    @Override
    public <T> Formatter registerValueConverter(Class<T> clazz, final ToStringConverter<T> valueConverter) {
        checkIfMutable();
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
        return this;
    }

    @Override
    public <T> Formatter registerObjectConverter(Class<T> clazz, ObjectConverter<T> objectConverter) {
        checkIfMutable();
        objectConverters.put(clazz, objectConverter);
        return this;
    }

    protected String convertToString(Object obj) {
        return stringConvert.convertToString(obj);
    }

    protected ObjectConverter getObjectConverter(Class<?> clazz) {
        return objectConverters.get(clazz);
    }

    private void checkIfMutable() {
        if (immutable) {
            throw new IllegalStateException("Immutable formatter cannot be extended");
        }
    }

    private static final class ClassMap<T> extends HashMap<Class, T> {
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

    protected static final class LongIdConverter implements ToStringConverter<LongId> {

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

    private static final class MapConverter implements ObjectConverter<Map> {

        @Override
        @SuppressWarnings("unchecked")
        public Iterable<Map.Entry<String, Object>> convert(Map map) {
            return map.entrySet();
        }
    }
}