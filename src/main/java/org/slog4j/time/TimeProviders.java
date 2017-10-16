package org.slog4j.time;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.experimental.UtilityClass;

/**
 * Supplies some basic time providers.
 */
@UtilityClass
public class TimeProviders {

    /**
     * The standard system time provider.
     *
     * @return The system time provider.
     */
    public static TimeProvider system() {
        return SystemTimeProvider.INSTANCE;
    }

    /**
     * A time provider that always returns the same instant.
     *
     * @param fixedMillis The instant when the clock broke.
     * @return The broken-clock provider.
     */
    public static TimeProvider brokenClock(final long fixedMillis) {
        return new TimeProvider() {
            @Override
            public long currentTimeMillis() {
                return fixedMillis;
            }
        };
    }

    @NoArgsConstructor(access = AccessLevel.PRIVATE)
    private static final class SystemTimeProvider implements TimeProvider {
        static final TimeProvider INSTANCE = new SystemTimeProvider();

        @Override
        public long currentTimeMillis() {
            return System.currentTimeMillis();
        }
    }
}
