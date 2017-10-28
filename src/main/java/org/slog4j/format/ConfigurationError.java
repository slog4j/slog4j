package org.slog4j.format;

public class ConfigurationError extends RuntimeException {
    private static final long serialVersionUID = 3632880833001342304L;

    public ConfigurationError(String message) {
        super(message);
    }

    public ConfigurationError(String message, Throwable cause) {
        super(message, cause);
    }
}
