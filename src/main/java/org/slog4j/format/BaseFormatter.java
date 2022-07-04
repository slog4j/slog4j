package org.slog4j.format;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.experimental.Accessors;
import org.apache.commons.lang3.time.FastDateFormat;
import org.joda.convert.StringConvert;
import org.joda.convert.StringConverter;
import org.joda.convert.ToStringConverter;
import org.slf4j.event.Level;
import org.slog4j.types.LongId;

import java.net.InetSocketAddress;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public abstract class BaseFormatter implements ConfigurableFormatter {

    private static final String DEFAULT_TIME_LABEL     = "time";
    private static final String DEFAULT_LEVEL_LABEL    = "level";
    private static final String DEFAULT_EVENT_ID_LABEL = "evt";
    private static final String DEFAULT_SPAN_ID_LABEL  = "spanId";

    static final String NULL_PLACEHOLDER          = "_NULL_";
    static final String NO_CONVERTER_PLACEHOLDER  = "_NO_CONVERTER_";
    static final String MISSING_VALUE_PLACEHOLDER = "_MISSING_";

    /**
     * The standard ISO8601/RFC3339 format for date/time but with milliseconds precision.
     */
    static final String DATE_TIME_FORMAT = "yyyy-MM-dd'T'HH:mm:ss.SSSZ";

    /**
     * Thread-safe alternative to {@link java.text.SimpleDateFormat}.
     */
    static final FastDateFormat FORMAT_ISO8601_MILLIS = FastDateFormat.getInstance(DATE_TIME_FORMAT);

    private final StringConvert                     toStringConverters     = new StringConvert(true);
    private final Map<Class, ToPropertiesConverter> toPropertiesConverters = new ClassMap<>();

    @Getter(AccessLevel.PROTECTED)
    @Accessors(fluent = true)
    private String timeLabel = DEFAULT_TIME_LABEL;

    @Getter(AccessLevel.PROTECTED)
    @Accessors(fluent = true)
    private String levelLabel = DEFAULT_LEVEL_LABEL;

    @Getter(AccessLevel.PROTECTED)
    @Accessors(fluent = true)
    private String eventIdLabel = DEFAULT_EVENT_ID_LABEL;

    @Getter(AccessLevel.PROTECTED)
    @Accessors(fluent = true)
    private String spanIdLabel = DEFAULT_SPAN_ID_LABEL;

    public BaseFormatter() {
        registerAdditionalConverters();
    }

    @Override
    public Formatter timeLabel(String timeLabel) {
        this.timeLabel = timeLabel;
        return this;
    }

    @Override
    public Formatter levelLabel(String levelLabel) {
        this.levelLabel = levelLabel;
        return this;
    }

    @Override
    public Formatter eventIdLabel(String eventIdLabel) {
        this.eventIdLabel = eventIdLabel;
        return this;
    }

    @Override
    public Formatter spanIdLabel(String spanIdLabel) {
        this.spanIdLabel = spanIdLabel;
        return this;
    }

    private void registerAdditionalConverters() {
        registerToStringConverter(LongId.class, LongIdConverter.INSTANCE);
        registerToStringConverter(InetSocketAddress.class, InetSocketAddressConverter.INSTANCE);
        registerToPropertiesConverter(Map.class, MapConverter.INSTANCE);
    }

    @Override
    public <T> Formatter registerToStringConverter(Class<T> clazz, final ToStringConverter<T> converter) {
        toStringConverters.register(clazz, new StringConverter<T>() {
            @Override
            public T convertFromString(Class<? extends T> cls, String str) {
                throw new UnsupportedOperationException();
            }

            @Override
            public String convertToString(T object) {
                return converter.convertToString(object);
            }
        });
        return this;
    }

    @Override
    public <T> Formatter registerToPropertiesConverter(Class<T> clazz, ToPropertiesConverter<T> converter) {
        toPropertiesConverters.put(clazz, converter);
        return this;
    }

    protected String convertToString(Object obj) {
        return toStringConverters.convertToString(obj);
    }

    protected ToPropertiesConverter propertiesConverter(Class<?> clazz) {
        return toPropertiesConverters.get(clazz);
    }

    protected StrBuilderResult beforeAddContentsHook(StrBuilderResult sbr, Level level) {
        return sbr;
    }

    private static final class ClassMap<T> extends ConcurrentHashMap<Class, T> {
        @Override
        public T get(Object key) {
            assert key != null;
            T value = super.get(key);
            if (value == null) {
                value = getCompatible(key);
                if (value != null) {
                    // to speed up next look up
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
        private static final LongIdConverter INSTANCE = new LongIdConverter();

        private LongIdConverter() {}

        @Override
        public String convertToString(LongId longId) {
            return convertToString(longId.getValue());
        }

        static String convertToString(long value) {
            return String.format("%016x", value);
        }
    }

    private static final class InetSocketAddressConverter implements ToStringConverter<InetSocketAddress> {
        private static final InetSocketAddressConverter INSTANCE = new InetSocketAddressConverter();

        private InetSocketAddressConverter() { }

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

    private static final class MapConverter implements ToPropertiesConverter<Map> {
        private static final MapConverter INSTANCE = new MapConverter();

        private MapConverter() { }

        @Override
        @SuppressWarnings("unchecked")
        public Iterable<Map.Entry<String, Object>> convert(Map map) {
            return map.entrySet();
        }
    }
}
