package org.slog4j.format;

import lombok.experimental.UtilityClass;
import lombok.extern.slf4j.Slf4j;
import lombok.val;
import org.joda.convert.ToStringConverter;
import org.slog4j.time.TimeProvider;
import org.slog4j.time.TimeProviders;
import org.yaml.snakeyaml.Yaml;

import java.io.InputStream;
import java.util.Map;

@UtilityClass
@Slf4j
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
                    log.error("Error loading SLog4j configuration", e);
                }
            }
            if (INSTANCE == null) {
                log.info("Using SLog4j default configuration");
                INSTANCE = new TextFormatter();
            }
        }
        return INSTANCE;
    }

    @SuppressWarnings("unchecked")
    // @Nullable
    private static Formatter loadFormatterFromStream(InputStream is, TimeProvider timeProvider)
        throws ClassNotFoundException, IllegalAccessException, InstantiationException {
        val yaml = new Yaml().loadAs(is, Map.class);
        val formatterEntry = (Map<String, ?>) yaml.get("formatter");
        if (formatterEntry != null) {
            val className = (String) formatterEntry.get("class");
            if (className != null) {
                val cl = FormatterFactory.class.getClassLoader();
                val formatterClass = cl.loadClass(className);
                val formatter = (Formatter) formatterClass.newInstance();
                if (formatter instanceof ConfigurableFormatter) {
                    val configurableInstance = (ConfigurableFormatter) formatter;
                    val convertersEntry = (Map<String, ?>) formatterEntry.get("converters");
                    if (convertersEntry != null) {
                        val toStringConverters = (Map<String, String>) convertersEntry.get("to_string");
                        if (toStringConverters != null) {
                            for (val entry : toStringConverters.entrySet()) {
                                val type = cl.loadClass(entry.getKey());
                                val converter = (ToStringConverter) cl.loadClass(entry.getValue()).newInstance();
                                configurableInstance.registerToStringConverter(type, converter);
                            }
                        }
                        val toPropertiesConverters = (Map<String, String>) convertersEntry.get("to_properties");
                        if (toPropertiesConverters != null) {
                            for (val entry : toPropertiesConverters.entrySet()) {
                                val type = cl.loadClass(entry.getKey());
                                val converter = (ToPropertiesConverter) cl.loadClass(entry.getValue()).newInstance();
                                configurableInstance.registerToPropertiesConverter(type, converter);
                            }
                        }
                    }
                }
                return formatter;
            }
        }
        return null;
    }
}
