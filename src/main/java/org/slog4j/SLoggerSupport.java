package org.slog4j;

import lombok.experimental.UtilityClass;

@UtilityClass
public class SLoggerSupport {

    private static final ThreadLocal<Long> spanIdLocal = ThreadLocal.withInitial(() -> 0L);

    public static void setSpanId(long spanId) {
        spanIdLocal.set(spanId);
    }

    public static long getSpanId() {
        return spanIdLocal.get();
    }
}
