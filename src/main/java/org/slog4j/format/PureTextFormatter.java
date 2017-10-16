package org.slog4j.format;

import lombok.RequiredArgsConstructor;
import org.slf4j.event.Level;
import org.slog4j.time.TimeProvider;
import org.slog4j.time.TimeProviders;

@RequiredArgsConstructor
public class PureTextFormatter extends TextFormatter {
    private final TimeProvider timeProvider;

    public PureTextFormatter() {
        this(TimeProviders.system());
    }

    protected StrBuilderResult beforeAddContentsHook(StrBuilderResult sbr, Level level) {
        return appendTimeAndLevel(sbr, level);
    }

    private StrBuilderResult appendTimeAndLevel(StrBuilderResult sbr, Level level) {
        sbr.append(TIME_LABEL).append(NAME_VALUE_SEP)
            .append(FORMAT_ISO8601_MILLIS.format(timeProvider.currentTimeMillis()));
        return appendNameValue(sbr, LEVEL_LABEL, level.toString());
    }
}
