package io.eliez.slog4j;

/**
 * A structured log always adheres to the following rules:
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
    /**
     * Log a structured message at INFO level according to the specified
     * @param eventId The event identifier
     * @param spanId The SPAN identifier
     * @param fields The optional fields
     */
    void logTracedEvent(String eventId, long spanId, Object... fields);

    void logEvent(String eventId, Object... fields);
}
