package org.slog4j;

import org.slog4j.format.Formatter;
import org.slog4j.time.TimeProvider;

/**
 * A structured log message always adheres to the following rules:
 *
 * <p>
 * <code>&lt;FULL_TIMESTAMP&gt; &lt;LEVEL&gt; &lt;key1=value1&gt; [&lt;key2=value2&gt; ...]</code>
 *
 * <ul>
 *     <li>Every message is represented by a single line of text;</li>
 *     <li>Always prefixed by zoned timestamp;</li>
 *     <li>The LEVEL is printed next;</li>
 *     <li>The remaining text is a sequence of fields according to the format <code>key=value</code> separated by a single space;</li>
 *     <li><code>key1</code> is always the event ID labeled by "<code>evtId</code>";</li>
 *     <li><code>key2</code> can be the <code>spanId</code> to correlate the events.</li>
 * </ul>
 */
public interface SLogger {
    SLogger withFormatter(Formatter formatter);

    SLogger withTimeProvider(TimeProvider timeProvider);

    void error(long spanId, String eventId);
    void error(long spanId, String eventId, Object obj);
    void error(long spanId, String eventId, String key, Object value);
    void error(long spanId, String eventId, Object... fields);
    void error(String eventId);
    void error(String eventId, Object obj);
    void error(String eventId, String key, Object value);
    void error(String eventId, Object... fields);

    void warn(long spanId, String eventId);
    void warn(long spanId, String eventId, Object obj);
    void warn(long spanId, String eventId, String key, Object value);
    void warn(long spanId, String eventId, Object... fields);
    void warn(String eventId);
    void warn(String eventId, Object obj);
    void warn(String eventId, String key, Object value);
    void warn(String eventId, Object... fields);

    /**
     * Log a structured event message at INFO level.
     *
     * @param spanId The SPAN identifier.
     * @param eventId The event identifier.
     */
    void info(long spanId, String eventId);
    void info(long spanId, String eventId, Object obj);
    void info(long spanId, String eventId, String key, Object value);

    /**
     * Log a structured message at INFO level according to the specified
     * @param spanId The SPAN identifier
     * @param eventId The event identifier
     * @param fields The optional fields
     */
    void info(long spanId, String eventId, Object... fields);

    // the untraced counterparts
    void info(String eventId);
    void info(String eventId, Object obj);
    void info(String eventId, String key, Object value);
    void info(String eventId, Object... fields);

    void debug(long spanId, String eventId);
    void debug(long spanId, String eventId, Object obj);
    void debug(long spanId, String eventId, Object... fields);
    void debug(String eventId);
    void debug(String eventId, Object obj);
    void debug(String eventId, String key, Object value);
    void debug(long spanId, String eventId, String key, Object value);
    void debug(String eventId, Object... fields);

    void trace(long spanId, String eventId);
    void trace(long spanId, String eventId, Object obj);
    void trace(long spanId, String eventId, String key, Object value);
    void trace(long spanId, String eventId, Object... fields);
    void trace(String eventId);
    void trace(String eventId, Object obj);
    void trace(String eventId, String key, Object value);
    void trace(String eventId, Object... fields);
}

