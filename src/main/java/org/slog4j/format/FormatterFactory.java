package org.slog4j.format;

import lombok.experimental.UtilityClass;
import lombok.val;
import org.joda.convert.ToStringConverter;
import org.slog4j.time.TimeProvider;
import org.slog4j.time.TimeProviders;
import org.yaml.snakeyaml.Yaml;

import java.io.InputStream;
import java.util.Map;

@UtilityClass
public class FormatterFactory {

    private static Formatter INSTANCE;

    public static Formatter getInstance() {
        return getInstance(TimeProviders.system());
    }

    public static Formatter getInstance(TimeProvider timeProvider) {
        if (INSTANCE == null) {
            val configInput = FormatterFactory.class.getResourceAsStream("/slog4j.yml");
            if (configInput != null) {
                try {
                    INSTANCE = loadFormatterFromStream(configInput, timeProvider);
                } catch (Exception e) {
                    throw new ConfigurationError("Error loading SLog4j configuration", e);
                }
            }
            if (INSTANCE == null) {
                INSTANCE = getDefaultInstance();
            }
        }
        return INSTANCE;
    }

    @SuppressWarnings("unchecked")
    private static Formatter loadFormatterFromStream(InputStream is, TimeProvider timeProvider)
        throws ClassNotFoundException, IllegalAccessException, InstantiationException {
        val cl = FormatterFactory.class.getClassLoader();
        val yaml = new Yaml().loadAs(is, Map.class);
        val formatterClassName = (String) yaml.get("formatter");
        final Formatter formatter;
        if (formatterClassName == null) {
            formatter = getDefaultInstance();
        } else {
            val formatterClass = cl.loadClass(formatterClassName);
            formatter = (Formatter) formatterClass.newInstance();
        }
        if (formatter instanceof ConfigurableFormatter) {
            val configurableFormatter = (ConfigurableFormatter) formatter;
            val convertersEntry = (Map<String, String>) yaml.get("converters");
            if (convertersEntry != null) {
                for (val entry : convertersEntry.entrySet()) {
                    val type = cl.loadClass(entry.getKey());
                    Object converter = cl.loadClass(entry.getValue()).newInstance();
                    if (converter instanceof ToPropertiesConverter) {
                        configurableFormatter.registerToPropertiesConverter(type, (ToPropertiesConverter) converter);
                    } else if (converter instanceof ToStringConverter) {
                        configurableFormatter.registerToStringConverter(type, (ToStringConverter) converter);
                    } else {
                        throw new ConfigurationError("Converter type does not implement a supported interface: " +
                            entry.getValue());
                    }
                }
            }
        }
        return formatter;
    }

    private static Formatter getDefaultInstance() {
        return new TextFormatter();
    }
}
