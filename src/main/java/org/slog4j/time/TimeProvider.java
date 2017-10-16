package org.slog4j.time;

/**
 * A simple abstraction to enable using mocking time providers on test.
 */
public interface TimeProvider {
    long currentTimeMillis();
}
