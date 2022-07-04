package org.slog4j.format;

import lombok.RequiredArgsConstructor;
import org.slf4j.event.Level;

import java.time.Clock;

@RequiredArgsConstructor
public class PureTextFormatter extends TextFormatter {
    private final Clock clock;

    public PureTextFormatter() {
        this(Clock.systemDefaultZone());
    }

    @Override
    protected StrBuilderResult beforeAddContentsHook(StrBuilderResult sbr, Level level) {
        return appendTimeAndLevel(sbr, level);
    }

    private StrBuilderResult appendTimeAndLevel(StrBuilderResult sbr, Level level) {
        sbr.append(timeLabel()).append(NAME_VALUE_SEP)
            .append(FORMAT_ISO8601_MILLIS.format(clock.millis()));
        return appendNameValue(sbr, levelLabel(), level.toString());
    }
}
