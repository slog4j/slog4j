package org.slog4j.format;

import lombok.experimental.UtilityClass;

@UtilityClass
public class StrBuilderResultFactory {

    // Use a more suitable size for typical log messages
    private static final int INITIAL_CAPACITY = 128;

    private static final ThreadLocal<StrBuilderResult> RESULT_POOL = new ThreadLocal<StrBuilderResult>() {

        @Override
        protected StrBuilderResult initialValue() {
            return new StrBuilderResult(INITIAL_CAPACITY);
        }

        @Override
        public StrBuilderResult get() {
            StrBuilderResult result = super.get();
            result.clear();
            return result;
        }
    };

    public static StrBuilderResult get() {
        return RESULT_POOL.get();
    }
}
