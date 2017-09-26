package org.slog4j;

import lombok.experimental.UtilityClass;

@UtilityClass
public class TimeProviders {

    public static final TimeProvider SYSTEM = new SystemTimeProvider();

    public static TimeProvider brokenClock(final long fixedMillis) {
        return new TimeProvider() {
            @Override
            public long currentTimeMillis() {
                return fixedMillis;
            }
        };
    }

    private static final class SystemTimeProvider implements TimeProvider {
        @Override
        public long currentTimeMillis() {
            return System.currentTimeMillis();
        }
    }
}
