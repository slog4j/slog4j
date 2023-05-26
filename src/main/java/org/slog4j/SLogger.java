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
 *     <li>The remaining text is a sequence of properties on the format <code>name=value</code> separated by a single space;</li>
 *     <li><code>key1</code> is always the event ID labeled by "<code>evtId</code>";</li>
 * </ul>
 */
public interface SLogger {

    void error(String eventId, Object... objs);
    void error(String eventId);
    void error(String eventId, Object obj);
    void error(String eventId, String name, Object value);

    void warn(String eventId, Object... objs);
    void warn(String eventId);
    void warn(String eventId, Object obj);
    void warn(String eventId, String name, Object value);

    // the untraced counterparts
    void info(String eventId, Object... objs);
    void info(String eventId);
    void info(String eventId, Object obj);
    void info(String eventId, String name, Object value);

    void debug(String eventId, Object... objs);
    void debug(String eventId);
    void debug(String eventId, Object obj);
    void debug(String eventId, String name, Object value);

    void trace(String eventId, Object... objs);
    void trace(String eventId);
    void trace(String eventId, Object obj);
    void trace(String eventId, String name, Object value);
}
