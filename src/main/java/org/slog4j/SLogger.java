package org.slog4j;

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
 *     <li>The remaining text is a sequence of properties on the format <code>key=value</code> separated by a single space;</li>
 *     <li><code>key1</code> is always the event ID labeled by "<code>evtId</code>";</li>
 *     <li><code>key2</code> can be the <code>spanId</code> to correlate the events.</li>
 * </ul>
 */
public interface SLogger {

    void error(long spanId, String eventId);
    void error(long spanId, String eventId, Object obj);
    void error(long spanId, String eventId, String key, Object value);
    void error(long spanId, String eventId, Object... objs);
    void error(String eventId);
    void error(String eventId, Object obj);
    void error(String eventId, String key, Object value);
    void error(String eventId, Object... objs);

    void warn(long spanId, String eventId);
    void warn(long spanId, String eventId, Object obj);
    void warn(long spanId, String eventId, String key, Object value);
    void warn(long spanId, String eventId, Object... objs);
    void warn(String eventId);
    void warn(String eventId, Object obj);
    void warn(String eventId, String key, Object value);
    void warn(String eventId, Object... objs);

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
     * Log a structured event at INFO level.
     *
     * @param spanId The SPAN identifier.
     * @param eventId The event identifier.
     * @param objs Other objects (key/value pairs and/or properties).
     */
    void info(long spanId, String eventId, Object... objs);

    // the untraced counterparts
    void info(String eventId);
    void info(String eventId, Object obj);
    void info(String eventId, String key, Object value);
    void info(String eventId, Object... objs);

    void debug(long spanId, String eventId);
    void debug(long spanId, String eventId, Object obj);
    void debug(long spanId, String eventId, Object... objs);
    void debug(String eventId);
    void debug(String eventId, Object obj);
    void debug(String eventId, String key, Object value);
    void debug(long spanId, String eventId, String key, Object value);
    void debug(String eventId, Object... objs);

    void trace(long spanId, String eventId);
    void trace(long spanId, String eventId, Object obj);
    void trace(long spanId, String eventId, String key, Object value);
    void trace(long spanId, String eventId, Object... objs);
    void trace(String eventId);
    void trace(String eventId, Object obj);
    void trace(String eventId, String key, Object value);
    void trace(String eventId, Object... objs);
}

