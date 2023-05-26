package org.slog4j.format;

import lombok.experimental.UtilityClass;
import lombok.val;
import org.yaml.snakeyaml.Yaml;

import java.io.InputStream;
import java.util.Map;

@UtilityClass
public class FormatterFactory {

    private static Formatter INSTANCE;

    public static Formatter getInstance() {
        if (INSTANCE == null) {
            val configInput = FormatterFactory.class.getResourceAsStream("/slog4j.yml");
            if (configInput != null) {
                try {
                    INSTANCE = loadFormatterFromStream(configInput);
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
    private static Formatter loadFormatterFromStream(InputStream is)
        throws ReflectiveOperationException {
        val cl = FormatterFactory.class.getClassLoader();
        val yaml = new Yaml().loadAs(is, Map.class);
        val formatterClassName = (String) yaml.get("formatter");
        final Formatter formatter;
        if (formatterClassName == null) {
            formatter = getDefaultInstance();
        } else {
            val formatterClass = cl.loadClass(formatterClassName);
            formatter = (Formatter) formatterClass.getDeclaredConstructor().newInstance();
        }
        if (formatter instanceof ConfigurableFormatter) {
            val configurableFormatter = (ConfigurableFormatter) formatter;
            val labelsEntry = (Map<String, String>) yaml.get("labels");
            if (labelsEntry != null) {
                for (val entry : labelsEntry.entrySet()) {
                    String name = entry.getKey();
                    String value = entry.getValue();
                    if ("time".equals(name)) {
                        configurableFormatter.timeLabel(value);
                    } else if ("level".equals(name)) {
                        configurableFormatter.levelLabel(value);
                    } else if ("eventId".equals(name)) {
                        configurableFormatter.eventIdLabel(value);
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
